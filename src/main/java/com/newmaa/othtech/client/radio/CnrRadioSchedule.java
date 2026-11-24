package com.newmaa.othtech.client.radio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/** The official 云听 programme schedule is separate from the CNR share-page stream endpoint. */
final class CnrRadioSchedule {

    private static final URI ENDPOINT = URI.create("https://ytapi.radio.cn/ytsrv/srv/interactive/program/list");
    private static final ZoneId STATION_ZONE = ZoneId.of("Asia/Shanghai");
    private static final long RELOAD_INTERVAL = TimeUnit.MINUTES.toNanos(10);
    private static final long RETRY_INTERVAL = TimeUnit.MINUTES.toNanos(1);

    private final String broadcastId;
    private final String stationName;
    private LocalDate scheduleDate;
    private LocalDate attemptedDate;
    private List<Programme> schedule = Collections.emptyList();
    private long refreshedAt;

    private CnrRadioSchedule(String broadcastId, String stationName) {
        this.broadcastId = broadcastId;
        this.stationName = stationName;
    }

    static CnrRadioSchedule forStation(String channelId, String name) {
        // CNR and 云听 use different IDs. Only join independently verified channel/name pairs.
        if ("1".equals(channelId) && "中国之声".equals(name)) return new CnrRadioSchedule("639", name);
        if ("2".equals(channelId) && "经济之声".equals(name)) return new CnrRadioSchedule("640", name);
        if ("4".equals(channelId) && "音乐之声".equals(name)) return new CnrRadioSchedule("641", name);
        return null;
    }

    synchronized String currentProgramme() {
        if (Thread.currentThread()
            .isInterrupted()) return "";
        ZonedDateTime now = ZonedDateTime.now(STATION_ZONE);
        LocalDate date = now.toLocalDate();
        long clock = System.nanoTime();
        boolean haveToday = date.equals(scheduleDate);
        if (!date.equals(attemptedDate) || clock - refreshedAt >= (haveToday ? RELOAD_INTERVAL : RETRY_INTERVAL)) {
            attemptedDate = date;
            try {
                List<Programme> programmes = fetch(date);
                scheduleDate = date;
                schedule = programmes;
            } catch (IOException | RuntimeException ignored) {
                // Optional metadata must not stop playback or log URLs, and a stale title is misleading.
                scheduleDate = null;
                schedule = Collections.emptyList();
            } finally {
                refreshedAt = System.nanoTime();
            }
        }
        now = ZonedDateTime.now(STATION_ZONE);
        return now.toLocalDate()
            .equals(scheduleDate) ? select(schedule, now.toLocalTime()) : "";
    }

    private List<Programme> fetch(LocalDate date) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) ENDPOINT.toURL()
            .openConnection();
        connection.setConnectTimeout(3000);
        connection.setReadTimeout(3000);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        // This endpoint returns JSON, but rejects a strict application/json Accept header with HTTP 406.
        connection.setRequestProperty("Accept", "*/*");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("equipmentsource", "WEB");
        byte[] form = ("startdate=" + date + "&enddate=" + date + "&broadCastId=" + broadcastId)
            .getBytes(StandardCharsets.US_ASCII);
        connection.setFixedLengthStreamingMode(form.length);
        try {
            try (OutputStream output = connection.getOutputStream()) {
                output.write(form);
            }
            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                throw new IOException("CNR programme lookup returned HTTP " + status);
            }
            try (InputStream input = connection.getInputStream()) {
                return parse(RadioStreamResolver.readResponse(input, 512 * 1024), stationName, broadcastId, date);
            }
        } finally {
            connection.disconnect();
        }
    }

    static List<Programme> parse(String response, String name, String id, LocalDate date) {
        JsonObject root = new JsonParser().parse(response)
            .getAsJsonObject();
        List<Programme> results = new ArrayList<>();
        JsonArray dates = root.getAsJsonArray("con");
        if (!name.equals(RadioStreamResolver.text(root, "broadcastName")) || dates == null) return results;
        String requested = date.toString();
        for (JsonElement dayElement : dates) {
            if (!dayElement.isJsonObject()) continue;
            JsonObject day = dayElement.getAsJsonObject();
            if (!requested.equals(RadioStreamResolver.text(day, "datetime"))) continue;
            // This misspelling is part of the official response schema.
            JsonArray entries = day.getAsJsonArray("progamlist");
            if (entries == null) continue;
            for (JsonElement entryElement : entries) {
                if (!entryElement.isJsonObject()) continue;
                JsonObject entry = entryElement.getAsJsonObject();
                JsonElement channel = entry.get("channelId");
                if (channel == null || !channel.isJsonPrimitive()
                    || !id.equals(channel.getAsString())
                    || !requested.equals(RadioStreamResolver.text(entry, "programDate"))) continue;
                String label = RadioStreamResolver.sanitizeLabel(RadioStreamResolver.text(entry, "name"));
                int start = minute(RadioStreamResolver.text(entry, "startTime"));
                int end = minute(RadioStreamResolver.text(entry, "endTime"));
                if (end == 0 && start > 0) end = 24 * 60;
                if (!label.isEmpty() && start >= 0 && end > start) results.add(new Programme(start, end, label));
            }
        }
        return results;
    }

    private static int minute(String value) {
        if ("24:00".equals(value)) return 24 * 60;
        if (value.length() != 5 || value.charAt(2) != ':') return -1;
        for (int i = 0; i < 5; i++) {
            if (i != 2 && (value.charAt(i) < '0' || value.charAt(i) > '9')) return -1;
        }
        int hours = Integer.parseInt(value.substring(0, 2));
        int minutes = Integer.parseInt(value.substring(3));
        return hours < 24 && minutes < 60 ? hours * 60 + minutes : -1;
    }

    static String select(List<Programme> programmes, LocalTime time) {
        int minute = time.getHour() * 60 + time.getMinute();
        Programme latest = null;
        for (Programme p : programmes) {
            if (minute >= p.start && minute < p.end && (latest == null || p.start > latest.start)) latest = p;
        }
        return latest == null ? "" : latest.title;
    }

    static final class Programme {

        private final int start;
        private final int end;
        private final String title;

        private Programme(int start, int end, String title) {
            this.start = start;
            this.end = end;
            this.title = title;
        }
    }
}
