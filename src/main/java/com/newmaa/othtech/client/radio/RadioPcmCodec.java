package com.newmaa.othtech.client.radio;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.ByteOrder;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFormat;

import paulscode.sound.ICodec;
import paulscode.sound.SoundBuffer;

/** An in-memory Paulscode codec. The sound engine never opens the synthetic URL or waits for the network. */
public final class RadioPcmCodec implements ICodec {

    static final String EXTENSION = "othradiopcm";
    private static final String PROTOCOL = "oth-radio";
    private static final boolean BIG_ENDIAN = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;
    private Stream stream;

    @Override
    public boolean initialize(URL url) {
        stream = null;
        if (url != null && PROTOCOL.equals(url.getProtocol())) {
            try {
                URLConnection connection = url.openConnection();
                if (connection instanceof PcmConnection) stream = ((PcmConnection) connection).stream;
            } catch (IOException ignored) {
                return false;
            }
        }
        return initialized();
    }

    @Override
    public boolean initialized() {
        return stream != null && !stream.closed;
    }

    @Override
    public SoundBuffer read() {
        if (!initialized()) return null;
        byte[] pcm = stream.queue.poll();
        // A live stream can underrun briefly; keep the channel alive without blocking other game sounds.
        if (pcm == null) pcm = new byte[stream.bufferBytes];
        return new SoundBuffer(pcm, stream.format);
    }

    @Override
    public SoundBuffer readAll() {
        return null; // Live streams must never be loaded as a single sound.
    }

    @Override
    public boolean endOfStream() {
        return !initialized();
    }

    @Override
    public AudioFormat getAudioFormat() {
        return stream == null ? null : stream.format;
    }

    @Override
    public void cleanup() {
        stream = null;
    }

    @Override
    public void reverseByteOrder(boolean reverse) {
        // The producer always supplies the platform's native byte order (required by OpenAL).
    }

    private static final class PcmConnection extends URLConnection {

        private final Stream stream;

        PcmConnection(URL url, Stream stream) {
            super(url);
            this.stream = stream;
        }

        @Override
        public void connect() {
            connected = true;
        }
    }

    static final class Stream {

        private static final int FRAMES = 2048;
        private final ArrayBlockingQueue<byte[]> queue = new ArrayBlockingQueue<>(16);
        private volatile AudioFormat format;
        private volatile boolean closed;
        private int bufferBytes;
        private int used;
        private byte[] pending;

        boolean ready() {
            return format != null && queue.size() >= 3;
        }

        URL url(String name) {
            try {
                return new URL(null, PROTOCOL + ":/" + name + "." + EXTENSION, new URLStreamHandler() {

                    @Override
                    protected URLConnection openConnection(URL url) {
                        return new PcmConnection(url, Stream.this);
                    }
                });
            } catch (MalformedURLException ex) {
                throw new IllegalStateException("Invalid local PCM URL", ex);
            }
        }

        void close() {
            closed = true;
            queue.clear();
        }

        void write(byte[] pcm, int count, int rate, int channels) throws IOException {
            if (rate <= 0 || (channels != 1 && channels != 2)
                || count < 0
                || count > pcm.length
                || count % (channels * 2) != 0) throw new IOException("Invalid radio PCM format");
            if (closed || count == 0) return;
            if (format == null) {
                bufferBytes = FRAMES * channels * 2;
                pending = new byte[bufferBytes];
                format = new AudioFormat(rate, 16, channels, true, BIG_ENDIAN);
            } else if (format.getSampleRate() != rate || format.getChannels() != channels) {
                throw new IOException("Radio PCM format changed during playback");
            }
            for (int pos = 0; pos < count && !closed;) {
                int size = Math.min(count - pos, bufferBytes - used);
                if (BIG_ENDIAN) {
                    for (int i = 0; i < size; i += 2) {
                        pending[used + i] = pcm[pos + i + 1];
                        pending[used + i + 1] = pcm[pos + i];
                    }
                } else {
                    System.arraycopy(pcm, pos, pending, used, size);
                }
                pos += size;
                used += size;
                if (used == bufferBytes) {
                    try {
                        while (!closed && !queue.offer(pending, 250, TimeUnit.MILLISECONDS)) {
                            // Bounded backpressure affects only the decoder worker, not the sound engine.
                        }
                    } catch (InterruptedException ex) {
                        Thread.currentThread()
                            .interrupt();
                        throw new IOException("Radio playback stopped", ex);
                    }
                    pending = new byte[bufferBytes];
                    used = 0;
                }
            }
        }
    }
}
