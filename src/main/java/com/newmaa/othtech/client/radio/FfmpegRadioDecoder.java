package com.newmaa.othtech.client.radio;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class FfmpegRadioDecoder {

    public static final int SAMPLE_RATE = 48000;
    public static final int CHANNELS = 2;
    public static final int FRAME_BYTES = CHANNELS * 2;

    private FfmpegRadioDecoder() {}

    public static Process start(String executable, URI stream) throws IOException {
        Path path = Paths.get(executable);
        if (!path.isAbsolute() || !Files.isRegularFile(path)) {
            throw new IOException("Configure the absolute path to the FFmpeg executable");
        }
        // FFmpeg handles container sniffing, HLS playlists and audio decoding; stdout is only PCM.
        Process process = new ProcessBuilder(
            path.toString(),
            "-hide_banner",
            "-nostdin",
            "-loglevel",
            "error",
            "-rw_timeout",
            "8000000",
            "-protocol_whitelist",
            "http,https,tcp,tls,crypto",
            "-i",
            stream.toASCIIString(),
            "-map",
            "0:a:0",
            "-ac",
            Integer.toString(CHANNELS),
            "-ar",
            Integer.toString(SAMPLE_RATE),
            "-c:a",
            "pcm_s16le",
            "-f",
            "s16le",
            "pipe:1").start();
        Thread stderr = new Thread(() -> {
            // Drain but do not log stderr: FFmpeg errors can include signed stream URLs.
            try (InputStream input = process.getErrorStream()) {
                byte[] buffer = new byte[4096];
                while (input.read(buffer) != -1) {
                    // Discard.
                }
            } catch (IOException ignored) {
                // The pipe closes when playback stops.
            }
        }, "OTH Radio FFmpeg stderr");
        stderr.setDaemon(true);
        stderr.start();
        return process;
    }
}
