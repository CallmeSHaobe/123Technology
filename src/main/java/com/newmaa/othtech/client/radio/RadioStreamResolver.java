package com.newmaa.othtech.client.radio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class RadioStreamResolver {

    private static final Pattern CHANNEL_ID = Pattern.compile("(?:^|&)channelId=([0-9]{1,12})(?:&|$)");

    private RadioStreamResolver() {}

    public static URI resolve(String source) throws IOException {
        return resolveInfo(source).getUri();
    }

    /** Resolves the stream and initial station label. Optional programme metadata does not delay playback. */
    public static StreamInfo resolveInfo(String source) throws IOException {
        URI uri = httpUri(source);
        if (!"apicnrapp.cnr.cn".equalsIgnoreCase(uri.getHost()) || !"/html/play.html".equals(uri.getPath())) {
            return new StreamInfo(uri, displayHost(uri), null);
        }
        Matcher channel = CHANNEL_ID.matcher(uri.getRawQuery() == null ? "" : uri.getRawQuery());
        if (!channel.find()) throw new IOException("CNR share URL requires a numeric channelId");
        HttpURLConnection connection = (HttpURLConnection) URI
            .create("https://pacc.cnr.cn/ygw/getlivechannel?channelId=" + channel.group(1) + "&111")
            .toURL()
            .openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestProperty("Accept", "application/json");
        try {
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException("CNR station lookup returned a non-200 status");
            }
            try (InputStream input = connection.getInputStream()) {
                return parseCnrInfo(readResponse(input, 262144), channel.group(1));
            }
        } finally {
            connection.disconnect();
        }
    }

    static URI parseCnrResponse(String body) throws IOException {
        return parseCnrInfo(body, null).getUri();
    }

    static StreamInfo parseCnrInfo(String body, String channelId) throws IOException {
        try {
            JsonObject station = new JsonParser().parse(body)
                .getAsJsonObject()
                .getAsJsonObject("data")
                .getAsJsonArray("categories")
                .get(0)
                .getAsJsonObject()
                .getAsJsonArray("detail")
                .get(0)
                .getAsJsonObject();
            String url = station.getAsJsonArray("other_info11")
                .get(0)
                .getAsJsonObject()
                .getAsJsonPrimitive("url")
                .getAsString();
            URI uri = httpUri(url);
            String name = sanitizeLabel(text(station, "name"));
            CnrRadioSchedule schedule = CnrRadioSchedule.forStation(channelId, name);
            return new StreamInfo(uri, name.isEmpty() ? displayHost(uri) : name, schedule);
        } catch (RuntimeException ex) {
            throw new IOException("CNR station response did not contain a stream URL");
        }
    }

    /** User-facing host only: never includes the port, credentials, path, query or fragment. */
    public static String displayHost(URI uri) {
        return uri == null || uri.getHost() == null ? "" : sanitizeLabel(uri.getHost());
    }

    static String text(JsonObject object, String key) {
        JsonElement value = object.get(key);
        return value != null && value.isJsonPrimitive()
            && value.getAsJsonPrimitive()
                .isString() ? value.getAsString() : "";
    }

    static String sanitizeLabel(String value) {
        // Metadata is untrusted. Reject URL/query-looking values; strip Minecraft styles and invisible controls.
        if (value.length() > 4096 || value.contains("://") || value.indexOf('=') >= 0) return "";
        StringBuilder result = new StringBuilder();
        int chars = 0;
        boolean space = false;
        for (int i = 0; i < value.length() && chars < 80;) {
            int point = value.codePointAt(i);
            i += Character.charCount(point);
            if (point == '\u00a7') {
                if (i < value.length() && "0123456789abcdefklmnorABCDEFKLMNOR".indexOf(value.charAt(i)) >= 0) i++;
                continue;
            }
            if (Character.isWhitespace(point) || Character.isSpaceChar(point)) {
                space = result.length() > 0;
                continue;
            }
            int type = Character.getType(point);
            if (Character.isISOControl(point) || type == Character.FORMAT || type == Character.SURROGATE) continue;
            if (space) {
                if (chars == 79) break;
                result.append(' ');
                chars++;
                space = false;
            }
            result.appendCodePoint(point);
            chars++;
        }
        return result.toString();
    }

    static String readResponse(InputStream input, int limit) throws IOException {
        long start = System.nanoTime();
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        byte[] chunk = new byte[4096];
        int count;
        while ((count = input.read(chunk)) != -1) {
            if (body.size() + count > limit || System.nanoTime() - start > TimeUnit.SECONDS.toNanos(8)) {
                throw new IOException("Radio lookup response exceeds size or time limit");
            }
            body.write(chunk, 0, count);
        }
        return new String(body.toByteArray(), StandardCharsets.UTF_8);
    }

    public static final class StreamInfo {

        private final URI uri;
        private final String stationName;
        private final CnrRadioSchedule schedule;
        private volatile String displayName;

        private StreamInfo(URI uri, String stationName, CnrRadioSchedule schedule) {
            this.uri = uri;
            this.stationName = stationName;
            this.schedule = schedule;
            displayName = stationName;
        }

        public URI getUri() {
            return uri;
        }

        /** Current cached programme, station name, or host. Never performs network I/O. */
        public String getDisplayName() {
            return displayName;
        }

        /** Whether this station has an independently verified programme feed. */
        public boolean hasProgrammeMetadata() {
            return schedule != null;
        }

        /** Refreshes a programme label, rate-limited per stream. Call only from a background worker. */
        public String refreshDisplayName() {
            if (schedule != null) {
                String programme = schedule.currentProgramme();
                displayName = programme.isEmpty() ? stationName : programme;
            }
            return displayName;
        }
    }

    static URI httpUri(String source) throws IOException {
        try {
            URI uri = URI.create(source.trim());
            String scheme = uri.getScheme();
            if (uri.getHost() == null || uri.getRawUserInfo() != null
                || !("https".equalsIgnoreCase(scheme) || "http".equalsIgnoreCase(scheme))) {
                throw new IOException("Expected an HTTP(S) stream URL without embedded credentials");
            }
            return uri;
        } catch (IllegalArgumentException ex) {
            throw new IOException("Invalid stream URL");
        }
    }
}
