package com.newmaa.othtech.client.radio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/** Extracts ADTS AAC from self-contained 188-byte HLS transport stream segments. */
final class MpegTsAac {

    private static final int PACKET_SIZE = 188;

    private MpegTsAac() {}

    static byte[] extract(byte[] ts) throws IOException {
        if (ts.length == 0 || ts.length % PACKET_SIZE != 0) throw new IOException("Not an MPEG-TS segment");
        int pmt = -1;
        int audio = -1;
        int remaining = 0;
        ByteArrayOutputStream adts = new ByteArrayOutputStream();
        for (int packet = 0; packet < ts.length; packet += PACKET_SIZE) {
            int end = packet + PACKET_SIZE;
            if (u(ts, packet) != 0x47) throw new IOException("Invalid MPEG-TS sync byte");
            if ((u(ts, packet + 1) & 0x80) != 0 || (u(ts, packet + 3) & 0xd0) != 0x10) continue;
            int pid = (u(ts, packet + 1) & 0x1f) << 8 | u(ts, packet + 2);
            boolean start = (u(ts, packet + 1) & 0x40) != 0;
            int pos = packet + 4;
            if ((u(ts, packet + 3) & 0x20) != 0) pos += 1 + u(ts, pos);
            if (pos >= end) continue;
            if ((pid == 0 || pid == pmt) && start) {
                // Typical single-program radio streams put each PSI table in one packet.
                pos += 1 + u(ts, pos);
                if (pos + 3 > end) continue;
                int limit = pos + 3 + ((u(ts, pos + 1) & 15) << 8 | u(ts, pos + 2));
                if (limit > end || limit - pos < 12) continue;
                limit -= 4; // PSI CRC
                if (pid == 0 && u(ts, pos) == 0 && pmt < 0) {
                    for (int entry = pos + 8; entry + 4 <= limit; entry += 4) {
                        if (u(ts, entry) != 0 || u(ts, entry + 1) != 0) {
                            pmt = (u(ts, entry + 2) & 31) << 8 | u(ts, entry + 3);
                            break;
                        }
                    }
                } else if (pid == pmt && u(ts, pos) == 2 && audio < 0 && pos + 12 <= limit) {
                    int entry = pos + 12 + ((u(ts, pos + 10) & 15) << 8 | u(ts, pos + 11));
                    while (entry + 5 <= limit) {
                        int next = entry + 5 + ((u(ts, entry + 3) & 15) << 8 | u(ts, entry + 4));
                        if (next > limit) break;
                        if (u(ts, entry) == 0x0f) {
                            audio = (u(ts, entry + 1) & 31) << 8 | u(ts, entry + 2);
                            break;
                        }
                        entry = next;
                    }
                }
            } else if (pid == audio) {
                if (start) {
                    remaining = 0;
                    if (pos + 9 > end || u(ts, pos) != 0 || u(ts, pos + 1) != 0 || u(ts, pos + 2) != 1) continue;
                    int pesLength = u(ts, pos + 4) << 8 | u(ts, pos + 5);
                    int headerLength = 3 + u(ts, pos + 8);
                    pos += 6 + headerLength;
                    if (pos > end) continue;
                    remaining = pesLength == 0 ? Integer.MAX_VALUE : Math.max(0, pesLength - headerLength);
                }
                int size = Math.min(end - pos, remaining);
                adts.write(ts, pos, size);
                remaining -= size;
            }
        }
        if (adts.size() == 0) throw new IOException("No ADTS AAC audio in MPEG-TS segment");
        return adts.toByteArray();
    }

    static int u(byte[] data, int index) {
        return data[index] & 255;
    }
}
