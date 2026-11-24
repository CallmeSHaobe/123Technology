package com.newmaa.othtech.client.radio;

import static com.newmaa.othtech.client.radio.MpegTsAac.u;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

import com.newmaa.othtech.internal.radio.aac.Decoder;
import com.newmaa.othtech.internal.radio.aac.SampleBuffer;

/** Small decoder for unencrypted HLS media playlists with MPEG-TS and AAC-LC/ADTS. */
public final class BasicHlsRadioDecoder {

    private static final int MAX_PLAYLIST = 256 * 1024;
    private static final int MAX_SEGMENT = 4 * 1024 * 1024;

    private Decoder decoder;
    private final SampleBuffer samples = new SampleBuffer();
    private int config = -1;

    public interface PcmSink {

        void write(byte[] pcm, int length, int sampleRate, int channels) throws IOException;
    }

    public void play(URI uri, BooleanSupplier cancelled, PcmSink sink) throws IOException, InterruptedException {
        long next = -1;
        while (!cancelled.getAsBoolean()) {
            Reply response = fetch(uri, MAX_PLAYLIST, cancelled);
            Playlist playlist = parse(response.uri, new String(response.body, StandardCharsets.UTF_8));
            if (next < 0) {
                // Start near the live edge without waiting for a new segment. Start VOD at the beginning.
                next = playlist.sequence + (playlist.end ? 0 : Math.max(0, playlist.segments.size() - 2));
            }
            int first = (int) Math.min(playlist.segments.size(), Math.max(0, next - playlist.sequence));
            for (int i = first; i < playlist.segments.size() && !cancelled.getAsBoolean(); i++) {
                Reply segment = fetch(playlist.segments.get(i), MAX_SEGMENT, cancelled);
                decodeSegment(segment.body, cancelled, sink);
                next = playlist.sequence + i + 1;
            }
            if (playlist.end) return;
            if (!cancelled.getAsBoolean()) Thread.sleep(750);
        }
    }

    void decodeSegment(byte[] ts, BooleanSupplier cancelled, PcmSink sink) throws IOException {
        byte[] adts = MpegTsAac.extract(ts);
        boolean decoded = false;
        for (int pos = 0; pos + 7 <= adts.length && !cancelled.getAsBoolean();) {
            if (u(adts, pos) != 255 || (u(adts, pos + 1) & 0xf6) != 0xf0) {
                pos++;
                continue;
            }
            int header = (u(adts, pos + 1) & 1) != 0 ? 7 : 9;
            int length = (u(adts, pos + 3) & 3) << 11 | u(adts, pos + 4) << 3 | u(adts, pos + 5) >> 5;
            if (length <= header) {
                pos++;
                continue;
            }
            if (pos + length > adts.length) break;
            int objectType = (u(adts, pos + 2) >> 6) + 1;
            int rateIndex = (u(adts, pos + 2) >> 2) & 15;
            int channels = (u(adts, pos + 2) & 1) << 2 | u(adts, pos + 3) >> 6;
            if (objectType != 2 || rateIndex >= 13 || (channels != 1 && channels != 2) || (u(adts, pos + 6) & 3) != 0) {
                throw new IOException("Built-in radio supports mono/stereo AAC-LC ADTS only");
            }
            int asc = objectType << 11 | rateIndex << 7 | channels << 3;
            if (asc != config) {
                decoder = new Decoder(new byte[] { (byte) (asc >> 8), (byte) asc });
                config = asc;
            }
            samples.setBigEndian(false);
            byte[] frame = new byte[length - header];
            System.arraycopy(adts, pos + header, frame, 0, frame.length);
            decoder.decodeFrame(frame, samples);
            byte[] pcm = samples.getData();
            if (pcm.length != 0 && samples.getBitsPerSample() == 16) {
                sink.write(pcm, pcm.length, samples.getSampleRate(), samples.getChannels());
                decoded = true;
            }
            pos += length;
        }
        if (!decoded && !cancelled.getAsBoolean()) throw new IOException("No complete AAC-LC audio in segment");
    }

    static Playlist parse(URI uri, String contents) throws IOException {
        String[] lines = contents.split("\\r?\\n");
        if (lines.length == 0 || !"#EXTM3U".equals(lines[0].trim())) {
            throw new IOException("Expected an HLS media playlist");
        }
        long sequence = 0;
        boolean end = false;
        List<URI> segments = new ArrayList<>();
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("#EXT-X-MEDIA-SEQUENCE:")) {
                try {
                    sequence = Long.parseLong(
                        line.substring("#EXT-X-MEDIA-SEQUENCE:".length())
                            .trim());
                } catch (NumberFormatException ex) {
                    throw new IOException("Invalid HLS sequence");
                }
            } else if (line.startsWith("#EXT-X-STREAM-INF:") || line.startsWith("#EXT-X-MAP:")
                || line.startsWith("#EXT-X-BYTERANGE:")
                || (line.startsWith("#EXT-X-KEY:") && !line.equals("#EXT-X-KEY:METHOD=NONE"))) {
                    throw new IOException("Built-in radio requires an unencrypted MPEG-TS media playlist");
                } else if (line.equals("#EXT-X-ENDLIST")) {
                    end = true;
                } else if (!line.isEmpty() && !line.startsWith("#")) {
                    segments.add(resolve(uri, line));
                }
        }
        if (segments.isEmpty() || sequence < 0 || sequence > Long.MAX_VALUE - segments.size()) {
            throw new IOException("Invalid or empty HLS media playlist");
        }
        return new Playlist(sequence, segments, end);
    }

    private static URI resolve(URI base, String ref) throws IOException {
        try {
            return RadioStreamResolver.httpUri(
                base.resolve(ref)
                    .toASCIIString());
        } catch (IllegalArgumentException ex) {
            throw new IOException("Invalid HLS URL");
        }
    }

    private static Reply fetch(URI uri, int limit, BooleanSupplier cancelled) throws IOException {
        long started = System.nanoTime();
        for (int redirects = 0; redirects <= 4 && !cancelled.getAsBoolean(); redirects++) {
            HttpURLConnection connection = (HttpURLConnection) uri.toURL()
                .openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            try {
                int status = connection.getResponseCode();
                if (status == 301 || status == 302 || status == 303 || status == 307 || status == 308) {
                    String location = connection.getHeaderField("Location");
                    if (location == null) throw new IOException("HLS redirect is missing its destination");
                    uri = resolve(uri, location);
                    continue;
                }
                if (status != 200) throw new IOException("HLS download failed (HTTP " + status + ")");
                try (InputStream input = connection.getInputStream()) {
                    ByteArrayOutputStream body = new ByteArrayOutputStream();
                    byte[] chunk = new byte[8192];
                    int count;
                    while (!cancelled.getAsBoolean() && (count = input.read(chunk)) != -1) {
                        if (body.size() + count > limit || System.nanoTime() - started > TimeUnit.SECONDS.toNanos(12)) {
                            throw new IOException("HLS download exceeded size or time limit");
                        }
                        body.write(chunk, 0, count);
                    }
                    if (cancelled.getAsBoolean()) throw new IOException("Playback stopped");
                    return new Reply(uri, body.toByteArray());
                }
            } finally {
                connection.disconnect();
            }
        }
        throw new IOException("Playback stopped or exceeded HLS redirect limit");
    }

    static final class Playlist {

        final long sequence;
        final List<URI> segments;
        final boolean end;

        Playlist(long sequence, List<URI> segments, boolean end) {
            this.sequence = sequence;
            this.segments = segments;
            this.end = end;
        }
    }

    private static final class Reply {

        final URI uri;
        final byte[] body;

        Reply(URI uri, byte[] body) {
            this.uri = uri;
            this.body = body;
        }
    }
}
