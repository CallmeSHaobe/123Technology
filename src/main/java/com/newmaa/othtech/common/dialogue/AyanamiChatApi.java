package com.newmaa.othtech.common.dialogue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.newmaa.othtech.common.dialogue.AyanamiReasoning.Effort;
import com.newmaa.othtech.common.dialogue.AyanamiReasoning.Mode;
import com.newmaa.othtech.common.dialogue.AyanamiReasoning.Protocol;

/** Synchronous Chat Completions transport; invoke it from a bounded worker, never a game tick thread. */
public final class AyanamiChatApi {

    private static final int MAX_MESSAGES = 32;
    private static final int MAX_MESSAGE_CHARS = 8192;
    private static final int MAX_CONVERSATION_CHARS = 32768;
    private static final int MAX_REQUEST_BYTES = 128 * 1024;
    private static final int MAX_RESPONSE_BYTES = 256 * 1024;
    private static final int MAX_REPLY_CHARS = 8192;
    public static final int MAX_KEY_CHARS = 512;

    private AyanamiChatApi() {}

    public static final class Settings {

        private final URL endpoint;
        private final String model;
        private final String apiKey;
        private final int connectTimeoutMillis;
        private final int readTimeoutMillis;
        private final int maxOutputTokens;
        private final boolean useMaxCompletionTokens;
        private final Protocol reasoningProtocol;
        private final Mode thinkingMode;
        private final Effort reasoningEffort;

        /** Supply the full /chat/completions URL. Only opted-in literal loopback permits plain HTTP. */
        public Settings(String endpoint, String model, boolean allowLoopbackHttp, int connectTimeoutMillis,
            int readTimeoutMillis, int maxOutputTokens) {
            this(endpoint, model, allowLoopbackHttp, connectTimeoutMillis, readTimeoutMillis, maxOutputTokens, false);
        }

        public Settings(String endpoint, String model, boolean allowLoopbackHttp, int connectTimeoutMillis,
            int readTimeoutMillis, int maxOutputTokens, boolean useMaxCompletionTokens) {
            this(
                endpoint,
                model,
                allowLoopbackHttp,
                connectTimeoutMillis,
                readTimeoutMillis,
                maxOutputTokens,
                useMaxCompletionTokens,
                Protocol.AUTO,
                Mode.DEFAULT,
                Effort.DEFAULT);
        }

        Settings(String endpoint, String model, boolean allowLoopbackHttp, int connectTimeoutMillis,
            int readTimeoutMillis, int maxOutputTokens, boolean useMaxCompletionTokens, Protocol protocol, Mode mode,
            Effort effort) {
            this.endpoint = validateEndpoint(endpoint, allowLoopbackHttp);
            if (model == null || model.trim()
                .isEmpty() || model.length() > 256 || hasControl(model)) {
                throw new IllegalArgumentException("Invalid chat API model name.");
            }
            if (connectTimeoutMillis < 1 || connectTimeoutMillis > 120000
                || readTimeoutMillis < 1
                || readTimeoutMillis > 120000) {
                throw new IllegalArgumentException("Chat API timeouts must be between 1 and 120000 milliseconds.");
            }
            if (maxOutputTokens < 1 || maxOutputTokens > 4096) {
                throw new IllegalArgumentException("Chat API output limit must be between 1 and 4096 tokens.");
            }
            this.model = model.trim();
            this.apiKey = null;
            this.connectTimeoutMillis = connectTimeoutMillis;
            this.readTimeoutMillis = readTimeoutMillis;
            this.maxOutputTokens = maxOutputTokens;
            this.reasoningProtocol = AyanamiReasoning.resolve(protocol, this.endpoint.toExternalForm(), this.model);
            if (!AyanamiReasoning.valid(reasoningProtocol, this.model, mode, effort)) {
                throw new IllegalArgumentException("Unsupported reasoning settings for this chat model.");
            }
            this.thinkingMode = mode;
            this.reasoningEffort = effort;
            Protocol tokenProtocol = AyanamiReasoning
                .tokenProtocol(reasoningProtocol, this.endpoint.toExternalForm(), this.model);
            this.useMaxCompletionTokens = tokenProtocol != Protocol.DEEPSEEK
                && (useMaxCompletionTokens || AyanamiReasoning.needsCompletionTokens(tokenProtocol, this.model));
        }

        private Settings(Settings source, String apiKey) {
            this.endpoint = source.endpoint;
            this.model = source.model;
            this.connectTimeoutMillis = source.connectTimeoutMillis;
            this.readTimeoutMillis = source.readTimeoutMillis;
            this.maxOutputTokens = source.maxOutputTokens;
            this.useMaxCompletionTokens = source.useMaxCompletionTokens;
            this.reasoningProtocol = source.reasoningProtocol;
            this.thinkingMode = source.thinkingMode;
            this.reasoningEffort = source.reasoningEffort;
            this.apiKey = apiKey;
        }

        Settings withApiKey(String apiKey) {
            if (apiKey != null && !validKey(apiKey)) throw new IllegalArgumentException("Invalid chat API key.");
            return new Settings(this, apiKey);
        }

        boolean hasUsableKey() {
            return apiKey != null;
        }

        String credentialBinding() {
            return endpoint.toExternalForm();
        }
    }

    public static final class Message {

        private final String role;
        private final String content;

        String role() {
            return role;
        }

        String content() {
            return content;
        }

        public Message(String role, String content) {
            if (!("system".equals(role) || "developer".equals(role)
                || "user".equals(role)
                || "assistant".equals(role))) {
                throw new IllegalArgumentException("Unsupported chat message role.");
            }
            if (content == null || content.length() > MAX_MESSAGE_CHARS) {
                throw new IllegalArgumentException("Invalid chat message content length.");
            }
            this.role = role;
            this.content = content;
        }
    }

    public static String complete(Settings settings, List<Message> messages) throws IOException {
        return completeWithUsage(settings, messages).text;
    }

    static Completion completeWithUsage(Settings settings, List<Message> messages) throws IOException {
        if (settings == null) throw failure("Chat API settings are missing.");
        String apiKey = settings.apiKey;
        if (apiKey == null) throw failure("Chat API key is missing.");
        byte[] payload = requestBody(settings, messages);
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) settings.endpoint.openConnection(Proxy.NO_PROXY);
            connection.setInstanceFollowRedirects(false);
            connection.setConnectTimeout(settings.connectTimeoutMillis);
            connection.setReadTimeout(settings.readTimeoutMillis);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Accept-Encoding", "identity");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setDoOutput(true);
            connection.setFixedLengthStreamingMode(payload.length);
            try (OutputStream output = connection.getOutputStream()) {
                output.write(payload);
            }
            long responseDeadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(settings.readTimeoutMillis);
            int status = connection.getResponseCode();
            if (status < 200 || status >= 300) {
                if (status >= 300 && status < 400) throw failure("Chat API redirect was refused.");
                if (status == 401 || status == 403) throw failure("Chat API authentication was rejected.");
                if (status == 429) throw failure("Chat API rate limit was reached.");
                if (status == 400 || status == 404 || status == 422) throw new RequestRejectedException(status);
                throw failure(
                    status >= 100 && status <= 599 ? "Chat API returned HTTP " + status + "."
                        : "Chat API returned an invalid HTTP status.");
            }
            if (connection.getContentLengthLong() > MAX_RESPONSE_BYTES) {
                throw failure("Chat API response exceeds the size limit.");
            }
            try (InputStream input = connection.getInputStream()) {
                Completion reply = parseReply(readBody(input, responseDeadline));
                return new Completion(reply.text.replace(apiKey, "[redacted]"), reply.usage);
            }
        } catch (ApiFailure | OutputBudgetException | RequestRejectedException e) {
            throw e;
        } catch (SocketTimeoutException e) {
            throw failure("Chat API request timed out.");
        } catch (IOException | RuntimeException e) {
            // Transport exceptions may include the URL, response text or authentication details.
            throw failure("Chat API request failed.");
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    private static URL validateEndpoint(String endpoint, boolean allowLoopbackHttp) {
        String error = "Chat API endpoint requires HTTPS or explicitly enabled loopback HTTP.";
        if (endpoint == null || endpoint.length() > 2048 || hasControl(endpoint)) {
            throw new IllegalArgumentException(error);
        }
        try {
            URI uri = new URI(endpoint.trim());
            String host = uri.getHost();
            String path = uri.getRawPath();
            if (path == null) throw new IllegalArgumentException(error);
            while (path.endsWith("/")) path = path.substring(0, path.length() - 1);
            if (path.endsWith("/v1")) path += "/chat/completions";
            if (!path.endsWith("/chat/completions")) {
                throw new IllegalArgumentException("Chat API endpoint must end in /v1 or /chat/completions.");
            }
            if (host == null || uri.getRawUserInfo() != null
                || uri.getRawFragment() != null
                || uri.getPort() < -1
                || uri.getPort() == 0
                || uri.getPort() > 65535) {
                throw new IllegalArgumentException(error);
            }
            String scheme = uri.getScheme();
            if ("http".equalsIgnoreCase(scheme) && allowLoopbackHttp) {
                // Keep localhost convenient without trusting DNS or the machine's hosts file.
                if ("localhost".equalsIgnoreCase(host)) host = "127.0.0.1";
                if (!"127.0.0.1".equals(host) && !"[::1]".equals(host) && !"[0:0:0:0:0:0:0:1]".equals(host)) {
                    throw new IllegalArgumentException(error);
                }
                scheme = "http";
            } else if ("https".equalsIgnoreCase(scheme)) {
                scheme = "https";
            } else {
                throw new IllegalArgumentException(error);
            }
            return new URL(
                scheme + "://"
                    + host
                    + (uri.getPort() == -1 ? "" : ":" + uri.getPort())
                    + path
                    + (uri.getRawQuery() == null ? "" : "?" + uri.getRawQuery()));
        } catch (URISyntaxException | MalformedURLException e) {
            throw new IllegalArgumentException(error);
        }
    }

    private static boolean hasControl(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (Character.isISOControl(value.charAt(i))) return true;
        }
        return false;
    }

    static boolean validKey(String value) {
        if (value == null || value.isEmpty() || value.length() > MAX_KEY_CHARS) return false;
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (ch < '!' || ch > '~') return false;
        }
        return true;
    }

    private static byte[] requestBody(Settings settings, List<Message> messages) throws IOException {
        if (messages == null || messages.isEmpty() || messages.size() > MAX_MESSAGES) {
            throw failure("Invalid chat API message count.");
        }
        JsonArray items = new JsonArray();
        int total = 0;
        int count = 0;
        for (Message message : messages) {
            if (message == null) throw failure("Chat API messages cannot be null.");
            if (++count > MAX_MESSAGES || (total += message.content.length()) > MAX_CONVERSATION_CHARS) {
                throw failure("Chat API conversation exceeds the size limit.");
            }
            JsonObject item = new JsonObject();
            item.addProperty("role", message.role);
            item.addProperty("content", message.content);
            items.add(item);
        }
        JsonObject request = new JsonObject();
        request.addProperty("model", settings.model);
        request.add("messages", items);
        request.addProperty("stream", false);
        request.addProperty(
            settings.useMaxCompletionTokens ? "max_completion_tokens" : "max_tokens",
            settings.maxOutputTokens);
        AyanamiReasoning.write(
            request,
            settings.reasoningProtocol,
            settings.model,
            settings.thinkingMode,
            settings.reasoningEffort);
        byte[] body = request.toString()
            .getBytes(StandardCharsets.UTF_8);
        if (body.length > MAX_REQUEST_BYTES) throw failure("Chat API request exceeds the size limit.");
        return body;
    }

    private static String readBody(InputStream input, long deadline) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] block = new byte[4096];
        while (true) {
            if (System.nanoTime() - deadline >= 0) throw new SocketTimeoutException();
            int length = input.read(block, 0, Math.min(block.length, MAX_RESPONSE_BYTES - output.size() + 1));
            if (length < 0) break;
            if (output.size() + length > MAX_RESPONSE_BYTES) throw failure("Chat API response exceeds the size limit.");
            output.write(block, 0, length);
        }
        if (System.nanoTime() - deadline >= 0) throw new SocketTimeoutException();
        try {
            return StandardCharsets.UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT)
                .decode(ByteBuffer.wrap(output.toByteArray()))
                .toString();
        } catch (IOException e) {
            throw failure("Chat API returned invalid UTF-8.");
        }
    }

    private static Completion parseReply(String body) throws IOException {
        Choice choice = null;
        Usage usage = Usage.UNKNOWN;
        boolean sawChoices = false;
        boolean sawUsage = false;
        try (JsonReader json = new JsonReader(new StringReader(body))) {
            json.beginObject();
            while (json.hasNext()) {
                String name = json.nextName();
                if ("choices".equals(name) && !sawChoices && json.peek() == JsonToken.BEGIN_ARRAY) {
                    sawChoices = true;
                    json.beginArray();
                    if (json.hasNext()) choice = readChoice(json);
                    while (json.hasNext()) json.skipValue();
                    json.endArray();
                } else if ("usage".equals(name) && !sawUsage && json.peek() == JsonToken.BEGIN_OBJECT) {
                    sawUsage = true;
                    usage = readUsage(json);
                } else {
                    json.skipValue();
                }
            }
            json.endObject();
            if (json.peek() != JsonToken.END_DOCUMENT) throw failure("Chat API returned invalid JSON.");
        } catch (IOException | RuntimeException e) {
            throw failure("Chat API returned invalid JSON.");
        }
        String content = choice == null || choice.content == null ? "" : choice.content.trim();
        if (content.isEmpty()) {
            if (choice != null && choice.hitLimit) throw new OutputBudgetException();
            throw failure("Chat API returned no assistant text.");
        }
        if (content.length() > MAX_REPLY_CHARS) throw failure("Chat API reply exceeds the text limit.");
        return new Completion(content, usage);
    }

    private static Usage readUsage(JsonReader json) throws IOException {
        long input = -1;
        long output = -1;
        long total = -1;
        json.beginObject();
        while (json.hasNext()) {
            switch (json.nextName()) {
                case "prompt_tokens":
                    input = readTokenCount(json);
                    break;
                case "completion_tokens":
                    output = readTokenCount(json);
                    break;
                case "total_tokens":
                    total = readTokenCount(json);
                    break;
                default:
                    json.skipValue();
            }
        }
        json.endObject();
        return new Usage(input, output, total);
    }

    private static long readTokenCount(JsonReader json) throws IOException {
        if (json.peek() != JsonToken.NUMBER) {
            json.skipValue();
            return -1;
        }
        try {
            long count = Long.parseLong(json.nextString());
            return count >= 0 ? count : -1;
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }

    static final class Completion {

        final String text;
        final Usage usage;

        private Completion(String text, Usage usage) {
            this.text = text;
            this.usage = usage;
        }
    }

    static final class Usage {

        static final Usage UNKNOWN = new Usage(-1, -1, -1);
        final long input;
        final long output;
        final long total;

        private Usage(long input, long output, long total) {
            this.input = input;
            this.output = output;
            this.total = total;
        }

        String snapshot() {
            return input < 0 && output < 0 && total < 0 ? "" : input + ":" + output + ":" + total;
        }
    }

    private static final class Choice {

        private String content;
        private boolean hitLimit;
    }

    private static Choice readChoice(JsonReader json) throws IOException {
        Choice choice = new Choice();
        if (json.peek() != JsonToken.BEGIN_OBJECT) {
            json.skipValue();
            return null;
        }
        json.beginObject();
        while (json.hasNext()) {
            String field = json.nextName();
            if ("finish_reason".equals(field) && json.peek() == JsonToken.STRING) {
                choice.hitLimit = "length".equals(json.nextString());
                continue;
            }
            if (!"message".equals(field) || json.peek() != JsonToken.BEGIN_OBJECT) {
                json.skipValue();
                continue;
            }
            json.beginObject();
            while (json.hasNext()) {
                if ("content".equals(json.nextName()) && json.peek() == JsonToken.STRING) {
                    choice.content = json.nextString();
                } else {
                    json.skipValue();
                }
            }
            json.endObject();
        }
        json.endObject();
        return choice;
    }

    static final class OutputBudgetException extends IOException {

        private static final long serialVersionUID = 1L;

        private OutputBudgetException() {
            super("Chat API used its output budget before returning assistant text.");
        }
    }

    static final class RequestRejectedException extends IOException {

        private static final long serialVersionUID = 1L;

        private RequestRejectedException(int status) {
            super("Chat API rejected the request (HTTP " + status + ").");
        }
    }

    private static ApiFailure failure(String message) {
        return new ApiFailure(message);
    }

    private static final class ApiFailure extends IOException {

        private static final long serialVersionUID = 1L;

        private ApiFailure(String message) {
            super(message);
        }
    }
}
