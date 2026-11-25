package com.newmaa.othtech.common.dialogue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.config.Configuration;

import com.cleanroommc.modularui.factory.EntityGuiFactory;
import com.newmaa.othtech.OTHTechnology;
import com.newmaa.othtech.common.dialogue.AyanamiChatApi.Completion;
import com.newmaa.othtech.common.dialogue.AyanamiChatApi.Message;
import com.newmaa.othtech.common.dialogue.AyanamiReasoning.Effort;
import com.newmaa.othtech.common.dialogue.AyanamiReasoning.Mode;
import com.newmaa.othtech.common.dialogue.AyanamiReasoning.Protocol;
import com.newmaa.othtech.common.entity.EntityAyanami;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

/** Server-owned private conversation history; workers never touch game state. */
public final class AyanamiDialogue {

    private static final String CATEGORY = "ayanami_dialogue";
    private static final String ERROR_PREFIX = "oth.ayanami.dialogue.error.";
    private static final int MAX_CONVERSATIONS = 512;
    private static final long COOLDOWN_NANOS = TimeUnit.SECONDS.toNanos(2);
    private static final long EVENT_LINE_LIFETIME = TimeUnit.SECONDS.toNanos(5);
    private static final long EVENT_LINE_BACKOFF = TimeUnit.SECONDS.toNanos(30);
    private static final int EVENT_WORKERS = 2;
    private static final int EVENT_QUEUE_LIMIT = 2;
    private static final int EVENT_FAILURE_LIMIT = 3;
    private static Configuration config;
    private static volatile boolean enabled;
    private static boolean operatorsOnly;
    private static boolean allowInGameApiEditing;
    private static boolean allowLoopback;
    private static boolean useMaxCompletionTokens;
    private static boolean sharePlayerInventory = true;
    private static Set<String> nativeItemClickItems = Collections.emptySet();
    private static Protocol reasoningProtocol = Protocol.AUTO;
    private static Mode thinkingMode = Mode.DEFAULT;
    private static Effort reasoningEffort = Effort.DEFAULT;
    private static String endpoint;
    private static String model;
    private static Path vaultDirectory;
    private static int gestureChance;
    private static int maxReplyChars = AyanamiDialogueText.DEFAULT_REPLY;
    private static volatile AyanamiChatApi.Settings configuredSettings;
    private static AyanamiChatApi.Settings configuredEventSettings;
    private static volatile Engine engine;

    private AyanamiDialogue() {}

    public static void configure(Configuration cfg, Path configurationPath) {
        config = cfg;
        vaultDirectory = configurationPath.toAbsolutePath()
            .getParent()
            .resolve("ayanami-credentials");
        enabled = cfg.getBoolean(
            "enabled",
            CATEGORY,
            true,
            "Server only. AI requests require an API key. Includes dialogue and brief scripted event lines.");
        operatorsOnly = cfg.getBoolean(
            "operatorsOnly",
            CATEGORY,
            true,
            "Allow only the single-player host or server operators to use API dialogue. Disabling may incur provider costs.");
        allowInGameApiEditing = cfg.getBoolean(
            "allowInGameApiEditing",
            CATEGORY,
            false,
            "Dedicated servers only: trust game operators to change the server API host, which receives the configured key.");
        endpoint = cfg
            .getString(
                "endpoint",
                CATEGORY,
                "https://api.deepseek.com/chat/completions",
                "Trusted HTTPS /v1 base URL or full Chat Completions endpoint, set by the server operator.")
            .trim();
        model = cfg.getString("model", CATEGORY, "deepseek-flash", "Model identifier for the configured provider.")
            .trim();
        if (cfg.getCategory(CATEGORY)
            .containsKey("apiKeyEnv"))
            cfg.getCategory(CATEGORY)
                .remove("apiKeyEnv");
        allowLoopback = cfg.getBoolean(
            "allowLoopbackHttp",
            CATEGORY,
            false,
            "Permit unencrypted HTTP only at a literal loopback address, for a trusted local compatible server.");
        useMaxCompletionTokens = cfg.getBoolean(
            "useMaxCompletionTokens",
            CATEGORY,
            false,
            "Send max_completion_tokens instead of max_tokens; enable for OpenAI models that require it.");
        sharePlayerInventory = cfg.getBoolean(
            "sharePlayerInventory",
            CATEGORY,
            true,
            "Send the speaking player's main inventory item IDs and counts with explicit Ayanami chat requests.");
        Set<String> trusted = new HashSet<>();
        for (String value : cfg.getStringList(
            "nativeItemClickItems",
            CATEGORY,
            new String[0],
            "Exact registry IDs allowed to run native air-click/swing item hooks as an unprivileged NPC. "
                + "Only list trusted items: a mod's hook may have world effects. Empty disables native hooks.")) {
            String id = value.trim();
            if (id.length() <= 160 && id.matches("[A-Za-z0-9_.-]+:[A-Za-z0-9_./-]+")) trusted.add(id);
        }
        nativeItemClickItems = Collections.unmodifiableSet(trusted);
        String profile = cfg.getString("reasoningProtocol", CATEGORY, "auto", "auto, none, deepseek, or openai.");
        String thinking = cfg.getString("thinkingMode", CATEGORY, "default", "default, off, or on (model-specific).");
        String effort = cfg.getString(
            "reasoningEffort",
            CATEGORY,
            "default",
            "default, minimal, low, medium, high, xhigh, or max, depending on the model.");
        gestureChance = cfg.getInt(
            "gestureChancePercent",
            CATEGORY,
            40,
            0,
            100,
            "Chance to play an allowed preset suggested by the model. Set to 0 to disable dialogue gestures.");
        maxReplyChars = cfg.getInt(
            "maxReplyChars",
            CATEGORY,
            AyanamiDialogueText.DEFAULT_REPLY,
            AyanamiDialogueText.MIN_REPLY,
            AyanamiDialogueText.MAX_REPLY,
            "Maximum displayed reply characters.");
        configuredSettings = null;
        configuredEventSettings = null;
        try {
            reasoningProtocol = AyanamiReasoning.read(Protocol.class, profile);
            thinkingMode = AyanamiReasoning.read(Mode.class, thinking);
            reasoningEffort = AyanamiReasoning.read(Effort.class, effort);
            configuredSettings = createSettings(
                endpoint,
                model,
                useMaxCompletionTokens,
                reasoningProtocol,
                thinkingMode,
                reasoningEffort);
        } catch (IllegalArgumentException e) {
            OTHTechnology.LOG.warn("Invalid Ayanami dialogue configuration; external requests remain disabled.");
        }
    }

    private static AyanamiChatApi.Settings createSettings(String url, String name, boolean completionTokens,
        Protocol protocol, Mode mode, Effort effort) {
        return createSettings(url, name, completionTokens, protocol, mode, effort, false);
    }

    private static AyanamiChatApi.Settings createSettings(String url, String name, boolean completionTokens,
        Protocol protocol, Mode mode, Effort effort, boolean eventLine) {
        Protocol resolved = AyanamiReasoning.resolve(protocol, url, name);
        boolean mayReason = AyanamiReasoning.mayReason(AyanamiReasoning.tokenProtocol(resolved, url, name), name, mode);
        boolean heavy = effort == Effort.HIGH || effort == Effort.XHIGH || effort == Effort.MAX;
        int budget = !mayReason ? 256 : heavy || effort == Effort.DEFAULT ? 4096 : 2048;
        int timeout = !mayReason ? 20000 : heavy || effort == Effort.DEFAULT ? 120000 : 75000;
        // Short network deadlines keep stalled event requests from monopolizing even their separate worker pool.
        return new AyanamiChatApi.Settings(
            url,
            name,
            allowLoopback,
            eventLine ? 2000 : 5000,
            eventLine ? 3000 : timeout,
            eventLine && !mayReason ? 128 : budget,
            completionTokens,
            protocol,
            mode,
            effort);
    }

    static void queueOpen(EntityPlayerMP player, int entityId) {
        Engine active = engine;
        if (active != null && active.running
            && (active.openRequests.size() < 256 || active.openRequests.containsKey(player)))
            active.openRequests.put(player, entityId);
    }

    /** Server-thread check so event callers can honor the existing operator policy per recipient. */
    public static boolean mayGenerateEventLine(EntityPlayerMP player) {
        Engine active = engine;
        AyanamiChatApi.Settings settings = configuredSettings;
        return player != null && player.isEntityAlive()
            && active != null
            && active.running
            && enabled
            && settings != null
            && settings.hasUsableKey()
            && configuredEventSettings != null
            && (!operatorsOnly || active.canUse(player));
    }

    /**
     * Schedules a short event line from the server gameplay thread. Cue must be a static, trusted,
     * non-personal scene description. False means no callback. While the server keeps ticking, true
     * means one callback on a later server tick, with an empty string on failure, expiration or settings change.
     * Callers own audience checks, local fallback, and presentation; the request contains no game state.
     */
    public static boolean requestEventLine(String locale, String cue, Consumer<String> completion) {
        Engine active = engine;
        AyanamiChatApi.Settings settings = configuredSettings;
        AyanamiChatApi.Settings transport = configuredEventSettings;
        if (active == null || !active.running
            || !enabled
            || settings == null
            || !settings.hasUsableKey()
            || transport == null
            || !transport.hasUsableKey()
            || completion == null) return false;
        return active.requestEventLine(settings, transport, locale, cue, completion);
    }

    public static boolean allowsNativeItemClick(String registryId) {
        return registryId != null && nativeItemClickItems.contains(registryId);
    }

    private static boolean displayableEndpoint(String url) {
        try {
            URI uri = new URI(url);
            return uri.getRawUserInfo() == null && uri.getRawQuery() == null && uri.getRawFragment() == null;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    /** Credentials may never cross an unencrypted multiplayer connection. */
    public static boolean canTransportKey(NetworkManager connection) {
        if (connection == null || !connection.isChannelOpen()) return false;
        if (connection.isLocalChannel()) return true;
        Field encrypted = EncryptionFlag.FIELD;
        if (encrypted == null) return false;
        try {
            return encrypted.getBoolean(connection);
        } catch (IllegalAccessException e) {
            return false;
        }
    }

    private static final class EncryptionFlag {

        private static final Field FIELD = find();

        private static Field find() {
            // Vanilla 1.7.10 sets this flag only after both cipher handlers are installed.
            for (String name : new String[] { "field_152463_r", "isEncrypted" }) {
                try {
                    Field field = NetworkManager.class.getDeclaredField(name);
                    if (field.getType() != boolean.class) continue;
                    field.setAccessible(true);
                    return field;
                } catch (ReflectiveOperationException | RuntimeException ignored) {}
            }
            return null;
        }
    }

    static final class SettingsView {

        public final String endpoint;
        public final String model;
        public final boolean keyConfigured;
        public final boolean canWriteKey;
        public final boolean enabled;
        public final boolean useMaxCompletionTokens;
        public final Protocol reasoningProtocol;
        public final Mode thinkingMode;
        public final Effort reasoningEffort;
        public final int maxReplyChars;

        private SettingsView(boolean canWriteKey) {
            endpoint = displayableEndpoint(AyanamiDialogue.endpoint) ? AyanamiDialogue.endpoint : "";
            model = AyanamiDialogue.model;
            keyConfigured = configuredSettings != null && configuredSettings.hasUsableKey();
            this.canWriteKey = canWriteKey;
            enabled = AyanamiDialogue.enabled;
            useMaxCompletionTokens = AyanamiDialogue.useMaxCompletionTokens;
            reasoningProtocol = AyanamiDialogue.reasoningProtocol;
            thinkingMode = AyanamiDialogue.thinkingMode;
            reasoningEffort = AyanamiDialogue.reasoningEffort;
            maxReplyChars = AyanamiDialogue.maxReplyChars;
        }
    }

    public static void start(MinecraftServer server) {
        stop();
        engine = new Engine(server);
        if (configuredSettings != null) {
            try {
                String key = engine.readKey(configuredSettings);
                configuredEventSettings = createSettings(
                    endpoint,
                    model,
                    useMaxCompletionTokens,
                    reasoningProtocol,
                    thinkingMode,
                    reasoningEffort,
                    true).withApiKey(key);
                configuredSettings = configuredSettings.withApiKey(key);
            } catch (IOException e) {
                OTHTechnology.LOG.warn("Ayanami credential store could not be read; API requests remain disabled.");
            }
        }
        FMLCommonHandler.instance()
            .bus()
            .register(engine);
    }

    public static void stop() {
        if (engine == null) return;
        FMLCommonHandler.instance()
            .bus()
            .unregister(engine);
        engine.running = false;
        engine.executor.shutdownNow();
        engine.eventExecutor.shutdownNow();
        for (EventLine pending : engine.eventLines) pending.work.cancel(true);
        engine.eventLines.clear();
        engine.finished.clear();
        engine.openRequests.clear();
        engine.clients.clear();
        engine.histories.clear();
        if (configuredSettings != null) configuredSettings = configuredSettings.withApiKey(null);
        configuredEventSettings = null;
        engine = null;
    }

    public static final class Result {

        public final String text;
        public final boolean error;

        private Result(String text, boolean error) {
            this.text = text;
            this.error = error;
        }

        private static Result error(String id) {
            return new Result(ERROR_PREFIX + id, true);
        }
    }

    public static final class Session {

        private final EntityAyanami npc;
        private final EntityPlayerMP player;
        private final BooleanSupplier activePanel;
        private final Conversation conversation;
        private boolean closed;

        public Session(EntityAyanami npc, EntityPlayerMP player, BooleanSupplier activePanel) {
            this.npc = npc;
            this.player = player;
            this.activePanel = activePanel;
            this.conversation = new Conversation(player.getUniqueID(), npc.dimension, npc.getUniqueID());
        }

        public synchronized boolean isAvailable() {
            return !closed && activePanel.getAsBoolean() && speakerStillNearby();
        }

        private boolean speakerStillNearby() {
            return player.worldObj != null && !player.worldObj.isRemote
                && player.worldObj == npc.worldObj
                && player.isEntityAlive()
                && npc.isEntityAlive()
                && player.getDistanceSqToEntity(npc) <= 64
                && player.worldObj.getEntityByID(npc.getEntityId()) == npc
                && player.worldObj.playerEntities.contains(player);
        }

        String configurationError() {
            if (!enabled) return ERROR_PREFIX + "disabled";
            Engine active = engine;
            if (active == null || configuredSettings == null) return ERROR_PREFIX + "configuration";
            if (operatorsOnly && !active.canUse(player)) return ERROR_PREFIX + "permission";
            if (!configuredSettings.hasUsableKey()) return ERROR_PREFIX + "key_missing";
            return "";
        }

        synchronized SettingsView readSettings() {
            Engine active = engine;
            return active != null && isAvailable() && active.canEdit(player)
                ? new SettingsView(active.canWriteKey(player))
                : null;
        }

        synchronized String updateSettings(String url, String name, boolean activate, boolean completionTokens,
            String newApiKey, Protocol protocol, Mode mode, Effort effort, int replyLimit) {
            Engine active = engine;
            if (active == null || config == null || !isAvailable() || !active.canEdit(player)) {
                return ERROR_PREFIX + "settings_denied";
            }
            if (url == null || name == null
                || url.length() > 2048
                || name.length() > 256
                || !displayableEndpoint(url)) {
                return ERROR_PREFIX + "settings_invalid";
            }
            if (!AyanamiDialogueText.validReplyLimit(replyLimit)) return ERROR_PREFIX + "reply_length_invalid";
            boolean replacesKey = newApiKey != null && !newApiKey.isEmpty();
            if (replacesKey && !active.canWriteKey(player)) return ERROR_PREFIX + "key_channel";
            if (replacesKey && !AyanamiChatApi.validKey(newApiKey)) return ERROR_PREFIX + "key_invalid";
            url = url.trim();
            name = name.trim();
            if (!AyanamiReasoning.valid(AyanamiReasoning.resolve(protocol, url, name), name, mode, effort)) {
                return ERROR_PREFIX + "reasoning_unsupported";
            }
            boolean optionsChanged = enabled != activate || useMaxCompletionTokens != completionTokens
                || !endpoint.equals(url)
                || !model.equals(name)
                || reasoningProtocol != protocol
                || thinkingMode != mode
                || reasoningEffort != effort
                || configuredSettings == null;
            boolean limitChanged = replyLimit != maxReplyChars;
            if (!optionsChanged && !replacesKey && !limitChanged) return "";
            AyanamiChatApi.Settings next = configuredSettings;
            try {
                if (optionsChanged || replacesKey)
                    next = createSettings(url, name, completionTokens, protocol, mode, effort);
            } catch (IllegalArgumentException e) {
                return ERROR_PREFIX + "settings_invalid";
            }
            long now = System.nanoTime();
            if (active.savedSettings && now - active.lastSettingsSave < COOLDOWN_NANOS)
                return ERROR_PREFIX + "cooldown";
            active.savedSettings = true;
            active.lastSettingsSave = now;
            AyanamiChatApi.Settings nextEvent = configuredEventSettings;
            try {
                if (optionsChanged || replacesKey) {
                    if (replacesKey) active.vault.save(next.credentialBinding(), newApiKey);
                    String key = replacesKey ? newApiKey : active.readKey(next);
                    nextEvent = createSettings(url, name, completionTokens, protocol, mode, effort, true)
                        .withApiKey(key);
                    next = next.withApiKey(key);
                }
            } catch (IOException e) {
                return ERROR_PREFIX + "key_storage";
            }
            if (optionsChanged) {
                config.getCategory(CATEGORY)
                    .get("endpoint")
                    .set(url);
                config.getCategory(CATEGORY)
                    .get("model")
                    .set(name);
                config.getCategory(CATEGORY)
                    .get("enabled")
                    .set(activate);
                config.getCategory(CATEGORY)
                    .get("useMaxCompletionTokens")
                    .set(completionTokens);
                config.getCategory(CATEGORY)
                    .get("reasoningProtocol")
                    .set(AyanamiReasoning.wireName(protocol));
                config.getCategory(CATEGORY)
                    .get("thinkingMode")
                    .set(AyanamiReasoning.wireName(mode));
                config.getCategory(CATEGORY)
                    .get("reasoningEffort")
                    .set(AyanamiReasoning.wireName(effort));
            }
            if (limitChanged) config.getCategory(CATEGORY)
                .get("maxReplyChars")
                .set(replyLimit);
            if (optionsChanged || limitChanged) config.save();
            endpoint = url;
            model = name;
            enabled = activate;
            useMaxCompletionTokens = completionTokens;
            reasoningProtocol = protocol;
            thinkingMode = mode;
            reasoningEffort = effort;
            maxReplyChars = replyLimit;
            configuredEventSettings = nextEvent;
            configuredSettings = next;
            if (optionsChanged || replacesKey) active.histories.clear();
            return "";
        }

        synchronized boolean attendConversation() {
            Engine active = engine;
            return active != null && (!operatorsOnly || active.canUse(player))
                && isAvailable()
                && npc.holdForConversation(this, player);
        }

        public synchronized void close() {
            closed = true;
        }

        private History visibleHistory() {
            Engine active = engine;
            if (active == null || !isAvailable() || (operatorsOnly && !active.canUse(player))) return null;
            return active.histories.get(conversation);
        }

        synchronized String historySnapshot() {
            History history = visibleHistory();
            return history == null ? "" : history.snapshot;
        }

        synchronized String usageSnapshot() {
            History history = visibleHistory();
            return history == null ? "" : history.usage;
        }

        public synchronized void submit(String input, Consumer<Result> callback) {
            if (closed) return;
            if (!isAvailable()) {
                callback.accept(Result.error("unavailable"));
                return;
            }
            String problem = configurationError();
            if (!problem.isEmpty()) {
                callback.accept(new Result(problem, true));
                return;
            }
            if (!attendConversation()) {
                callback.accept(Result.error("npc_busy"));
                return;
            }
            Engine active = engine;
            AyanamiChatApi.Settings settings = configuredSettings;
            if (input == null || input.length() > AyanamiDialogueText.MAX_INPUT) {
                callback.accept(Result.error("length"));
                return;
            }
            String text = AyanamiDialogueText.clean(input, AyanamiDialogueText.MAX_INPUT);
            if (text.isEmpty()) {
                callback.accept(Result.error("empty"));
                return;
            }
            UUID id = player.getUniqueID();
            long now = System.nanoTime();
            Budget budget = active.clients.computeIfAbsent(id, ignored -> new Budget());
            if (budget.pending || (budget.hasSubmitted && now - budget.lastSubmit < COOLDOWN_NANOS)) {
                callback.accept(Result.error("cooldown"));
                return;
            }
            History history = active.histories.get(conversation);
            List<Message> request = new ArrayList<>(AyanamiDialogueText.MAX_HISTORY_MESSAGES + 2);
            int replyLimit = maxReplyChars;
            // submit is dispatched by the server-side panel tick. Never query game data from the HTTP worker.
            AyanamiWorldContext observations = captureObservations();
            String system = AyanamiDialogueText.systemPrompt(replyLimit);
            // Preserve the Message 8,192-character limit even with nine maximum-size personal item entries.
            String observationSuffix = observations.systemSuffix(8192 - system.length());
            request.add(new Message("system", system + observationSuffix));
            if (history != null) request.addAll(history.messages);
            request.add(new Message("user", text));
            budget.lastSubmit = now;
            budget.hasSubmitted = true;
            budget.pending = true;
            try {
                active.executor.execute(() -> {
                    AyanamiDialogueText.Reply answer = null;
                    String error = "provider";
                    String usage = "";
                    try {
                        Completion completion = AyanamiChatApi.completeWithUsage(settings, request);
                        answer = AyanamiDialogueText.parse(completion.text, replyLimit);
                        usage = completion.usage.snapshot();
                    } catch (AyanamiChatApi.OutputBudgetException e) {
                        error = "output_budget";
                    } catch (AyanamiChatApi.RequestRejectedException e) {
                        error = "provider_options";
                    } catch (IOException | RuntimeException ignored) {
                        // Never forward or log request bodies, upstream response bodies, or credentials.
                    }
                    final AyanamiDialogueText.Reply result = answer;
                    final String failure = error;
                    final String tokens = usage;
                    if (active.running) active.finished.add(
                        () -> finish(
                            active,
                            settings,
                            text,
                            result,
                            tokens,
                            failure,
                            budget,
                            callback,
                            observationSuffix.isEmpty() ? null : observations,
                            replyLimit));
                });
            } catch (RejectedExecutionException e) {
                budget.pending = false;
                callback.accept(Result.error("busy"));
            }
        }

        private AyanamiWorldContext captureObservations() {
            try {
                return AyanamiWorldContext.capture(npc, player, sharePlayerInventory);
            } catch (RuntimeException ignored) {
                // A modded world or item may reject a getter. Keep chat working without leaking details.
                return AyanamiWorldContext.originOnly(npc);
            }
        }

        private synchronized void finish(Engine active, AyanamiChatApi.Settings settings, String input,
            AyanamiDialogueText.Reply reply, String usage, String error, Budget budget, Consumer<Result> callback,
            AyanamiWorldContext observations, int replyLimit) {
            budget.pending = false;
            if (active != engine || !active.running) return;
            boolean viewOpen = isAvailable();
            if (settings != configuredSettings || !enabled
                || (reply != null && reply.itemAction != null && replyLimit != maxReplyChars)) {
                if (viewOpen) callback.accept(Result.error("settings_changed"));
                return;
            }
            if (reply == null || reply.text.isEmpty()) {
                if (viewOpen) callback.accept(Result.error(error));
                return;
            }
            boolean authorized = !operatorsOnly || active.canUse(player);
            boolean mayPresent = viewOpen && authorized;
            if (reply.itemAction != null) {
                // Do not retake focus for a stale reply or let arbitrary AI text reach an item handler.
                if (!mayPresent) {
                    if (viewOpen) callback.accept(Result.error("permission"));
                    return;
                }
                BooleanSupplier stillValid = () -> active == engine && active.running
                    && configuredSettings == settings
                    && enabled
                    && replyLimit == maxReplyChars
                    && isAvailable()
                    && (!operatorsOnly || active.canUse(player))
                    && npc.hasDialogueItemLease(this, player);
                Consumer<String> itemFinished = result -> {
                    if (!isAvailable()) return;
                    if (!result.isEmpty()) callback.accept(Result.error(result));
                    else if (stillValid.getAsBoolean())
                        publish(active, input, reply, usage, callback, observations, true);
                    else callback.accept(Result.error("item_interrupted"));
                };
                String immediate = applyItem(observations, reply.itemAction, stillValid, itemFinished);
                if (immediate != null) itemFinished.accept(immediate);
                return;
            }
            publish(active, input, reply, usage, callback, observations, viewOpen);
        }

        private void publish(Engine active, String input, AyanamiDialogueText.Reply reply, String usage,
            Consumer<Result> callback, AyanamiWorldContext observations, boolean viewOpen) {
            History history = active.histories.computeIfAbsent(conversation, ignored -> new History());
            AyanamiDialogueText.appendExchange(history.messages, input, reply.text);
            history.snapshot = AyanamiDialogueText.encodeHistory(history.messages);
            history.usage = usage;
            if ((operatorsOnly && !active.canUse(player)) || !speakerStillNearby()) return;
            if (viewOpen && reply.itemAction == null
                && gestureChance > 0
                && (reply.lookTarget >= 0 || reply.action != null)
                && npc.getRNG()
                    .nextInt(100) < gestureChance) {
                if (reply.lookTarget >= 0) {
                    if (observations != null) observations.lookAt(npc, reply.lookTarget);
                } else npc.playDialogueGesture(reply.action);
            }
            AyanamiDialogueNetwork.sendReply(npc, reply.text);
            callback.accept(new Result("", false));
        }

        private String applyItem(AyanamiWorldContext observations, AyanamiDialogueText.ItemAction action,
            BooleanSupplier stillValid, Consumer<String> completion) {
            if (!stillValid.getAsBoolean()) return "item_session";
            if (observations == null) return "item_missing";
            try {
                return observations.applyItem(npc, action, stillValid, completion);
            } catch (RuntimeException | LinkageError ignored) {
                return "item_hook_failed";
            }
        }
    }

    private static final class Conversation {

        private final UUID player;
        private final int dimension;
        private final UUID npc;

        private Conversation(UUID player, int dimension, UUID npc) {
            this.player = player;
            this.dimension = dimension;
            this.npc = npc;
        }

        @Override
        public int hashCode() {
            return Objects.hash(player, dimension, npc);
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof Conversation conversation && dimension == conversation.dimension
                && player.equals(conversation.player)
                && npc.equals(conversation.npc);
        }
    }

    private static final class History {

        private final Deque<Message> messages = new ArrayDeque<>();
        private String snapshot = "";
        private String usage = "";
    }

    private static final class Budget {

        boolean pending;
        boolean hasSubmitted;
        long lastSubmit;
    }

    private static final class EventLine {

        final AyanamiChatApi.Settings settings;
        final long submitted;
        final FutureTask<String> work;
        final Consumer<String> completion;

        EventLine(AyanamiChatApi.Settings settings, long submitted, FutureTask<String> work,
            Consumer<String> completion) {
            this.settings = settings;
            this.submitted = submitted;
            this.work = work;
            this.completion = completion;
        }
    }

    public static final class Engine {

        private final MinecraftServer server;
        private final AyanamiKeyVault vault;
        private final Map<UUID, Budget> clients = new HashMap<>();
        private final Map<Conversation, History> histories = new LinkedHashMap<Conversation, History>(32, 0.75F, true) {

            @Override
            protected boolean removeEldestEntry(Map.Entry<Conversation, History> eldest) {
                return size() > MAX_CONVERSATIONS;
            }
        };
        private final ConcurrentHashMap<EntityPlayerMP, Integer> openRequests = new ConcurrentHashMap<>();
        private final ConcurrentLinkedQueue<Runnable> finished = new ConcurrentLinkedQueue<>();
        private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            2,
            2,
            15,
            TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            task -> {
                Thread worker = new Thread(task, "Ayanami dialogue worker");
                worker.setDaemon(true);
                return worker;
            });
        // Event work and its callbacks cannot take the explicit chat worker slots or build an unbounded backlog.
        private final ThreadPoolExecutor eventExecutor = new ThreadPoolExecutor(
            EVENT_WORKERS,
            EVENT_WORKERS,
            15,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(EVENT_QUEUE_LIMIT),
            task -> {
                Thread worker = new Thread(task, "Ayanami event-line worker");
                worker.setDaemon(true);
                return worker;
            });
        private final List<EventLine> eventLines = new ArrayList<>(EVENT_WORKERS + EVENT_QUEUE_LIMIT);
        private AyanamiChatApi.Settings eventBreakerSettings;
        private int eventFailures;
        private long eventBackoffUntil;
        private volatile boolean running = true;
        private int cleanup;
        private boolean savedSettings;
        private long lastSettingsSave;

        private Engine(MinecraftServer server) {
            this.server = server;
            this.vault = new AyanamiKeyVault(vaultDirectory);
            executor.allowCoreThreadTimeOut(true);
            eventExecutor.allowCoreThreadTimeOut(true);
        }

        private void refreshEventBreaker(AyanamiChatApi.Settings settings) {
            if (eventBreakerSettings == settings) return;
            eventBreakerSettings = settings;
            eventFailures = 0;
            eventBackoffUntil = 0;
        }

        private boolean requestEventLine(AyanamiChatApi.Settings settings, AyanamiChatApi.Settings transport,
            String locale, String cue, Consumer<String> completion) {
            refreshEventBreaker(settings);
            long now = System.nanoTime();
            if ((eventFailures >= EVENT_FAILURE_LIMIT && now - eventBackoffUntil < 0)
                || eventLines.size() >= EVENT_WORKERS + EVENT_QUEUE_LIMIT) return false;
            List<Message> request = AyanamiEventLinePrompt.request(locale, cue);
            if (request == null) return false;
            FutureTask<String> work = new FutureTask<>(() -> {
                if (!running || !enabled
                    || configuredSettings != settings
                    || System.nanoTime() - now >= EVENT_LINE_LIFETIME) return "";
                try {
                    return AyanamiEventLinePrompt.plainLine(AyanamiChatApi.completeWithUsage(transport, request).text);
                } catch (IOException | RuntimeException ignored) {
                    // These requests never log provider bodies, credential details, or locale cues.
                    return "";
                }
            });
            try {
                eventExecutor.execute(work);
            } catch (RejectedExecutionException e) {
                return false;
            }
            eventLines.add(new EventLine(settings, now, work, completion));
            return true;
        }

        private void finishEventLines() {
            if (eventLines.isEmpty()) return;
            AyanamiChatApi.Settings settings = configuredSettings;
            refreshEventBreaker(settings);
            long now = System.nanoTime();
            List<Runnable> ready = new ArrayList<>(eventLines.size());
            for (Iterator<EventLine> it = eventLines.iterator(); it.hasNext();) {
                EventLine event = it.next();
                boolean current = enabled && event.settings == settings;
                boolean fresh = now - event.submitted < EVENT_LINE_LIFETIME;
                if (current && fresh && !event.work.isDone()) continue;
                it.remove();
                String line = "";
                if (current && fresh) {
                    try {
                        line = event.work.get(); // Nonblocking: isDone() was true above.
                    } catch (ExecutionException ignored) {
                        // FutureTask also contains unexpected worker failures; use the local fallback.
                    } catch (InterruptedException e) {
                        Thread.currentThread()
                            .interrupt();
                    }
                } else {
                    event.work.cancel(true);
                    eventExecutor.remove(event.work);
                }
                if (current) {
                    if (line.isEmpty()) {
                        if (eventFailures < EVENT_FAILURE_LIMIT) eventFailures++;
                        if (eventFailures == EVENT_FAILURE_LIMIT) eventBackoffUntil = now + EVENT_LINE_BACKOFF;
                    } else {
                        eventFailures = 0;
                    }
                }
                String result = line;
                ready.add(() -> event.completion.accept(enabled && configuredSettings == event.settings ? result : ""));
            }
            // No callback is invoked while iterating; callers may submit the next event from their callback.
            for (Runnable callback : ready) {
                try {
                    callback.run();
                } catch (RuntimeException ignored) {
                    OTHTechnology.LOG.warn("An Ayanami event-line callback failed.");
                }
            }
        }

        private boolean isHost(EntityPlayerMP player) {
            return server.isSinglePlayer() && player.playerNetServerHandler != null
                && player.playerNetServerHandler.netManager.isLocalChannel()
                && player.getCommandSenderName()
                    .equalsIgnoreCase(server.getServerOwner());
        }

        private boolean isOperator(EntityPlayerMP player) {
            return server.getConfigurationManager()
                .func_152603_m()
                .func_152683_b(player.getGameProfile()) != null;
        }

        private boolean canUse(EntityPlayerMP player) {
            return isHost(player) || isOperator(player);
        }

        private boolean canEdit(EntityPlayerMP player) {
            return isHost(player) || (server.isDedicatedServer() && allowInGameApiEditing && isOperator(player));
        }

        private boolean canWriteKey(EntityPlayerMP player) {
            if (!canEdit(player) || player.playerNetServerHandler == null) return false;
            NetworkManager connection = player.playerNetServerHandler.netManager;
            return canTransportKey(connection) && (connection.isLocalChannel() ? isHost(player)
                : server.isDedicatedServer() && server.isServerInOnlineMode());
        }

        private String readKey(AyanamiChatApi.Settings settings) throws IOException {
            String key = vault.read(settings.credentialBinding());
            if (key != null && !AyanamiChatApi.validKey(key)) throw new IOException("Invalid stored API credential.");
            return key;
        }

        private void openNearby(EntityPlayerMP player, int id) {
            if (player.worldObj == null || !player.isEntityAlive()
                || !player.worldObj.playerEntities.contains(player)
                || player.openContainer != player.inventoryContainer) return;
            Entity target = player.worldObj.getEntityByID(id);
            if (!(target instanceof EntityAyanami npc) || !npc.isEntityAlive()
                || npc.isInvisible()
                || player.getDistanceSqToEntity(npc) > 64
                || !player.canEntityBeSeen(npc)) return;
            // The client supplies only the crosshair entity ID. The server tolerates slight aim/latency differences.
            Vec3 eye = Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ);
            Vec3 look = player.getLookVec();
            Vec3 end = eye.addVector(look.xCoord * 8, look.yCoord * 8, look.zCoord * 8);
            if (npc.boundingBox.expand(0.35, 0.35, 0.35)
                .calculateIntercept(eye, end) == null && player.getDistanceSqToEntity(npc) > 1) return;
            EntityGuiFactory.INSTANCE.open(player, npc);
        }

        @SubscribeEvent
        public void onServerTick(TickEvent.ServerTickEvent event) {
            if (event.phase != TickEvent.Phase.END || !running) return;
            openRequests.forEach((player, id) -> { if (openRequests.remove(player, id)) openNearby(player, id); });
            for (Runnable callback; (callback = finished.poll()) != null;) callback.run();
            finishEventLines();
            if (++cleanup >= 100) {
                cleanup = 0;
                long now = System.nanoTime();
                clients.values()
                    .removeIf(budget -> !budget.pending && now - budget.lastSubmit >= COOLDOWN_NANOS);
            }
        }
    }
}
