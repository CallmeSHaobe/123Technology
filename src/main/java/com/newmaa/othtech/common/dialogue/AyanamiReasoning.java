package com.newmaa.othtech.common.dialogue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import com.google.gson.JsonObject;

/** Capability and wire policy for Chat Completions reasoning controls. */
final class AyanamiReasoning {

    enum Protocol {
        AUTO,
        NONE,
        DEEPSEEK,
        OPENAI
    }

    enum Mode {
        DEFAULT,
        OFF,
        ON
    }

    enum Effort {
        DEFAULT,
        MINIMAL,
        LOW,
        MEDIUM,
        HIGH,
        XHIGH,
        MAX
    }

    private AyanamiReasoning() {}

    static Protocol resolve(Protocol protocol, String endpoint, String model) {
        if (protocol != Protocol.AUTO) return protocol;
        if (endpoint == null) return Protocol.NONE;
        try {
            String host = new URI(endpoint.trim()).getHost();
            if ("api.deepseek.com".equalsIgnoreCase(host)
                && (family(model, "deepseek-flash") || family(model, "deepseek-v4-flash")
                    || family(model, "deepseek-v4-pro")))
                return Protocol.DEEPSEEK;
            if ("api.openai.com".equalsIgnoreCase(host) && knownOpenAiReasoner(model)) return Protocol.OPENAI;
        } catch (URISyntaxException ignored) {
            // An incomplete model/endpoint draft must not enable provider-specific fields.
        }
        return Protocol.NONE;
    }

    static Mode[] modes(Protocol resolved, String model) {
        if (nonReasoning(resolved, model)) return new Mode[] { Mode.DEFAULT };
        if (resolved == Protocol.OPENAI && (gpt62(model) || family(model, "gpt-5") || legacyOpenAiReasoner(model))) {
            return new Mode[] { Mode.DEFAULT, Mode.ON };
        }
        return new Mode[] { Mode.DEFAULT, Mode.OFF, Mode.ON };
    }

    static Effort[] efforts(Protocol resolved, String model, Mode mode) {
        if (nonReasoning(resolved, model) || mode == Mode.OFF) return new Effort[] { Effort.DEFAULT };
        if (resolved == Protocol.DEEPSEEK) return new Effort[] { Effort.DEFAULT, Effort.LOW, Effort.HIGH, Effort.MAX };
        if (gpt62(model) || family(model, "gpt-6-luna")) {
            return new Effort[] { Effort.DEFAULT, Effort.LOW, Effort.MEDIUM, Effort.HIGH, Effort.XHIGH, Effort.MAX };
        }
        if (family(model, "gpt-5"))
            return new Effort[] { Effort.DEFAULT, Effort.MINIMAL, Effort.LOW, Effort.MEDIUM, Effort.HIGH };
        if (legacyOpenAiReasoner(model)) return new Effort[] { Effort.DEFAULT, Effort.LOW, Effort.MEDIUM, Effort.HIGH };
        if (family(model, "gpt-5.4") || family(model, "gpt-5.5")) {
            return new Effort[] { Effort.DEFAULT, Effort.LOW, Effort.MEDIUM, Effort.HIGH, Effort.XHIGH };
        }
        // Explicit OpenAI-compatible profile for custom model IDs. Their service determines compatibility.
        return Effort.values();
    }

    static boolean valid(Protocol resolved, String model, Mode mode, Effort effort) {
        if (resolved == null || mode == null || effort == null) return false;
        return contains(modes(resolved, model), mode) && contains(efforts(resolved, model, mode), effort);
    }

    static boolean mayReason(Protocol resolved, String model, Mode mode) {
        return !nonReasoning(resolved, model) && mode != Mode.OFF;
    }

    static boolean needsCompletionTokens(Protocol resolved, String model) {
        return resolved == Protocol.OPENAI && !nonReasoning(resolved, model);
    }

    static Protocol tokenProtocol(Protocol resolved, String endpoint, String model) {
        // Omitting reasoning fields does not change an official model's required token field or default behavior.
        return resolved == Protocol.NONE ? resolve(Protocol.AUTO, endpoint, model) : resolved;
    }

    static void write(JsonObject request, Protocol resolved, String model, Mode mode, Effort effort) {
        if (nonReasoning(resolved, model)) return;
        if (resolved == Protocol.DEEPSEEK) {
            if (mode != Mode.DEFAULT) {
                JsonObject thinking = new JsonObject();
                thinking.addProperty("type", mode == Mode.ON ? "enabled" : "disabled");
                request.add("thinking", thinking);
            }
            if (mode != Mode.OFF && effort != Effort.DEFAULT) request.addProperty("reasoning_effort", wireName(effort));
        } else if (resolved == Protocol.OPENAI) {
            if (mode == Mode.OFF) request.addProperty("reasoning_effort", "none");
            else if (effort != Effort.DEFAULT) request.addProperty("reasoning_effort", wireName(effort));
            else if (mode == Mode.ON) request.addProperty("reasoning_effort", "medium");
        }
    }

    static String wireName(Enum<?> option) {
        return option.name()
            .toLowerCase(Locale.ROOT);
    }

    static <T extends Enum<T>> T read(Class<T> type, String value) {
        return Enum.valueOf(
            type,
            value.trim()
                .toUpperCase(Locale.ROOT));
    }

    private static boolean nonReasoning(Protocol resolved, String model) {
        return resolved == Protocol.NONE || resolved == Protocol.AUTO
            || (resolved == Protocol.OPENAI && (family(model, "gpt-4.1") || family(model, "gpt-4.1-mini")
                || family(model, "gpt-4.1-nano")
                || family(model, "gpt-4o")
                || family(model, "gpt-4o-mini")));
    }

    private static boolean knownOpenAiReasoner(String model) {
        return gpt62(model) || family(model, "gpt-6-luna")
            || family(model, "gpt-5")
            || family(model, "gpt-5.4")
            || family(model, "gpt-5.5")
            || legacyOpenAiReasoner(model);
    }

    private static boolean legacyOpenAiReasoner(String model) {
        return family(model, "o1") || family(model, "o3");
    }

    private static boolean gpt62(String model) {
        return family(model, "gpt-6.2-sol") || family(model, "gpt-6.2-astra");
    }

    private static boolean family(String model, String id) {
        return model != null && (model.trim()
            .equalsIgnoreCase(id)
            || model.trim()
                .toLowerCase(Locale.ROOT)
                .startsWith(id + "-20"));
    }

    private static <T> boolean contains(T[] choices, T value) {
        for (T choice : choices) if (choice == value) return true;
        return false;
    }
}
