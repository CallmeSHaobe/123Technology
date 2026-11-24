package com.newmaa.othtech.client.radio;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.ByteOrder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.newmaa.othtech.machine.single.SpeakingStonePreset;

import paulscode.sound.ICodec;
import paulscode.sound.SoundBuffer;
import paulscode.sound.SoundSystemConfig;

public final class RadioDecoderCheck {

    private RadioDecoderCheck() {}

    public static void main(String[] args) throws Exception {
        URI base = URI.create("https://radio.example/hls/list?token=playlist");
        BasicHlsRadioDecoder.Playlist playlist = BasicHlsRadioDecoder
            .parse(base, "#EXTM3U\r\n#EXT-X-MEDIA-SEQUENCE:42\r\n#EXTINF:4,\r\nradio.ts?sig=a%2Fb\r\n#EXT-X-ENDLIST");
        require(playlist.sequence == 42 && playlist.end && playlist.segments.size() == 1, "playlist metadata");
        require(
            playlist.segments.get(0)
                .toASCIIString()
                .equals("https://radio.example/hls/radio.ts?sig=a%2Fb"),
            "relative segment and signed query");
        for (String invalid : new String[] { "\n", "<html>", "#EXTM3U\nfile:///audio.ts",
            "#EXTM3U\n#EXT-X-STREAM-INF:BANDWIDTH=64000\nsub.m3u8",
            "#EXTM3U\n#EXT-X-KEY:METHOD=AES-128,URI=key\nradio.ts", "#EXTM3U\n#EXT-X-MAP:URI=init.mp4\npart.m4s" }) {
            try {
                BasicHlsRadioDecoder.parse(base, invalid);
                throw new AssertionError("Accepted unsupported playlist");
            } catch (IOException expected) {
                // Unsupported input should fail before downloading a segment.
            }
        }
        byte[] ts = new byte[3 * 188];
        Arrays.fill(ts, (byte) 0xff);
        packet(ts, 0, 0, new int[] { 0, 0, 0xb0, 13, 0, 1, 0xc1, 0, 0, 0, 1, 0xe1, 0, 0, 0, 0, 0 });
        packet(
            ts,
            1,
            256,
            new int[] { 0, 2, 0xb0, 18, 0, 1, 0xc1, 0, 0, 0xe1, 1, 0xf0, 0, 0x0f, 0xe1, 1, 0xf0, 0, 0, 0, 0, 0 });
        packet(ts, 2, 257, new int[] { 0, 0, 1, 0xc0, 0, 7, 0x80, 0, 0, 0xff, 0xf1, 0x4c, 0x80 });
        require(
            Arrays.equals(MpegTsAac.extract(ts), new byte[] { (byte) 0xff, (byte) 0xf1, 0x4c, (byte) 0x80 }),
            "extract AAC PID and strip PES header and padding");
        System.out.println("PASS: HLS playlist validation and MPEG-TS AAC extraction");
        checkGamePcm();
        checkSchedule();
        checkPresets();
        for (String source : args) {
            if ("--presets".equals(source)) {
                for (SpeakingStonePreset station : SpeakingStonePreset.values()) {
                    System.out.println("Testing " + station.name());
                    smoke(station.getUrl());
                }
            } else smoke(source);
        }
    }

    private static void checkPresets() throws IOException {
        Set<String> urls = new HashSet<>();
        Set<String> labels = new HashSet<>();
        for (SpeakingStonePreset station : SpeakingStonePreset.values()) {
            String url = station.getUrl();
            URI uri = RadioStreamResolver.httpUri(url);
            require(
                "https".equals(uri.getScheme()) && uri.getRawFragment() == null && url.length() <= 512,
                "preset is a valid HTTPS radio URL");
            require(urls.add(url) && labels.add(station.getLabelKey()), "unique preset URL and label");
            require(SpeakingStonePreset.find(url) == station, "preset exact match");
            require(SpeakingStonePreset.find(url + "#other") == null, "modified URL is not a preset");
        }
        System.out.println("PASS: exact-match built-in radio preset allowlist");
    }

    private static void checkSchedule() throws IOException {
        LocalDate date = LocalDate.of(2026, 11, 24);
        String guide = "{\"broadcastName\":\"中国之声\",\"con\":[{\"datetime\":\"2026-11-24\",\"progamlist\":["
            + "{\"channelId\":639,\"programDate\":\"2026-11-24\",\"name\":\"Evening news\",\"startTime\":\"20:00\",\"endTime\":\"21:00\"},"
            + "{\"channelId\":\"639\",\"programDate\":\"2026-11-24\",\"name\":\"Late edition\",\"startTime\":\"21:00\",\"endTime\":\"00:00\"}]}]}";
        List<CnrRadioSchedule.Programme> entries = CnrRadioSchedule.parse(guide, "中国之声", "639", date);
        require("Evening news".equals(CnrRadioSchedule.select(entries, LocalTime.of(20, 59))), "active show");
        require("Late edition".equals(CnrRadioSchedule.select(entries, LocalTime.of(21, 0))), "programme boundary");
        require("Late edition".equals(CnrRadioSchedule.select(entries, LocalTime.of(23, 59))), "midnight boundary");
        require(
            CnrRadioSchedule.select(entries, LocalTime.of(0, 0))
                .isEmpty(),
            "no title outside schedule");
        require(
            CnrRadioSchedule.parse(guide, "中国之声", "640", date)
                .isEmpty(),
            "wrong station rejected");
        require(
            CnrRadioSchedule.parse(guide, "中国之声", "639", date.plusDays(1))
                .isEmpty(),
            "wrong date rejected");
        require(
            "radio.example".equals(
                RadioStreamResolver.resolveInfo("https://radio.example/live?secret=value")
                    .getDisplayName()),
            "direct URL label excludes credentials and tokens");
        System.out.println("PASS: CNR programme boundaries, station/date matching and safe direct-stream label");
    }

    private static void checkGamePcm() throws Exception {
        RadioPcmCodec.Stream stream = new RadioPcmCodec.Stream();
        URL url = stream.url("radio-test");
        SoundSystemConfig.setCodec(RadioPcmCodec.EXTENSION, RadioPcmCodec.class);
        ICodec codec = SoundSystemConfig.getCodec(url.getPath());
        require(codec != null && !codec.initialize(new URL("file:/wrong.othradiopcm")), "private codec URL");
        byte[] pcm = new byte[2048 * 4];
        Arrays.fill(pcm, (byte) 0x12);
        pcm[0] = 0x34;
        for (int i = 0; i < 3; i++) stream.write(pcm, pcm.length, 48000, 2);
        require(stream.ready() && codec.initialize(url), "playable PCM prebuffer");
        require(
            codec.getAudioFormat()
                .getChannels() == 2
                && codec.getAudioFormat()
                    .getSampleRate() == 48000,
            "game codec audio format");
        byte[] expected = pcm.clone();
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            expected[0] = 0x12;
            expected[1] = 0x34;
        }
        for (int i = 0; i < 3; i++) {
            SoundBuffer buffer = codec.read();
            require(Arrays.equals(expected, buffer.audioData), "native PCM data in order");
        }
        require(
            Arrays.equals(new byte[pcm.length], codec.read().audioData) && !codec.endOfStream(),
            "temporary underrun is not end of stream");
        for (int i = 0; i < 16; i++) stream.write(pcm, pcm.length, 48000, 2);
        FutureTask<Void> blockedWrite = new FutureTask<>(() -> {
            stream.write(pcm, pcm.length, 48000, 2);
            return null;
        });
        Thread writer = new Thread(blockedWrite, "radio backpressure test");
        writer.setDaemon(true);
        writer.start();
        try {
            blockedWrite.get(150, TimeUnit.MILLISECONDS);
            throw new AssertionError("PCM buffer must remain bounded");
        } catch (TimeoutException expectedTimeout) {
            // A full buffer waits on the decoder thread; closing must release it.
        } finally {
            stream.close();
        }
        blockedWrite.get(1, TimeUnit.SECONDS);
        require(codec.read() == null && codec.endOfStream(), "closed stream stops producing audio");
        codec.cleanup();
        System.out.println("PASS: game PCM codec format, ordering, underrun, bounded queue and close");
    }

    private static void smoke(String source) throws Exception {
        RadioStreamResolver.StreamInfo station = RadioStreamResolver.resolveInfo(source);
        System.out.println("Live station or programme: " + station.refreshDisplayName());
        long[] stats = new long[3];
        int[] format = new int[2];
        long deadline = System.nanoTime() + 30_000_000_000L;
        new BasicHlsRadioDecoder().play(
            station.getUri(),
            () -> (format[0] > 0 && stats[0] >= 5L * format[0]) || System.nanoTime() - deadline >= 0,
            (pcm, size, rate, channels) -> {
                require(
                    rate > 0 && (channels == 1 || channels == 2) && size % (2 * channels) == 0,
                    "expected aligned mono or stereo PCM");
                if (format[0] == 0) {
                    format[0] = rate;
                    format[1] = channels;
                }
                require(rate == format[0] && channels == format[1], "stable live stream format");
                stats[0] += size / (2 * channels);
                for (int i = 0; i < size; i += 2) {
                    int sample = (short) ((pcm[i] & 255) | pcm[i + 1] << 8);
                    stats[1] = Math.max(stats[1], Math.abs(sample));
                    stats[2] += (long) sample * sample;
                }
            });
        require(format[0] > 0 && stats[0] >= 5L * format[0] && stats[1] != 0, "live audio is missing or silent");
        System.out.println(
            "PASS: built-in live PCM frames=" + stats[0]
                + ", rate="
                + format[0]
                + ", channels="
                + format[1]
                + ", peak="
                + stats[1]
                + ", rms="
                + Math.round(Math.sqrt((double) stats[2] / (format[1] * stats[0]))));
    }

    private static void packet(byte[] ts, int index, int pid, int[] body) {
        int pos = index * 188;
        ts[pos] = 0x47;
        ts[pos + 1] = (byte) (0x40 | pid >> 8);
        ts[pos + 2] = (byte) pid;
        ts[pos + 3] = 0x10;
        for (int i = 0; i < body.length; i++) ts[pos + 4 + i] = (byte) body[i];
    }

    private static void require(boolean condition, String reason) {
        if (!condition) throw new AssertionError(reason);
    }
}
