package com.newmaa.othtech.common.dialogue;

import static net.minecraft.util.StatCollector.translateToLocal;
import static net.minecraft.util.StatCollector.translateToLocalFormatted;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import com.cleanroommc.modularui.ModularUI;
import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.Rectangle;
import com.cleanroommc.modularui.factory.EntityGuiData;
import com.cleanroommc.modularui.screen.ModularContainer;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widget.ScrollWidget;
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.newmaa.othtech.common.dialogue.AyanamiDialogueText.HistoryEntry;
import com.newmaa.othtech.common.dialogue.AyanamiReasoning.Effort;
import com.newmaa.othtech.common.dialogue.AyanamiReasoning.Mode;
import com.newmaa.othtech.common.dialogue.AyanamiReasoning.Protocol;
import com.newmaa.othtech.common.entity.EntityAyanami;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.eigenraven.lwjgl3ify.api.InputEvents;

/** MUI2 chat input and a private, read-only view of the server's recent completed dialogue. */
public final class AyanamiChatGui {

    static final String PREFIX = "oth.ayanami.dialogue.";
    static final int INK = 0xFFF5F3FA;
    static final int MUTED = 0xFFC1C6D4;
    static final int ERROR = 0xFFFFA5B2;
    static final int FIELD = 0xC90D1420;
    static final int BUTTON = 0xD5425069;
    static final int BUTTON_HOVER = 0xEC526483;
    static final Pattern SINGLE_LINE = Pattern.compile("[^\\p{Cc}\\p{Zl}\\p{Zp}§]*");
    static final int MAX_ENDPOINT = 2048;
    static final int MAX_MODEL = 256;
    static final int MAX_API_KEY = AyanamiChatApi.MAX_KEY_CHARS;

    private static final String SEND = "send";
    private static final String READ_SETTINGS = "read_settings";
    private static final String SAVE_SETTINGS = "save_settings";
    private static final int WIDTH = 320;
    private static final int HEIGHT = 126;
    private static final int HISTORY_WIDTH = WIDTH - 14;
    private static final int HISTORY_HEIGHT = 62;
    private static final int INPUT_TOP = 83;
    private static final int MAX_USAGE_CHARS = 59; // Three 19-digit longs plus two separators.
    private static final int SETTINGS_OVERHEAD_BYTES = 12;

    private AyanamiChatGui() {}

    public static ModularPanel build(EntityAyanami npc, EntityGuiData data, PanelSyncManager sync,
        UISettings settings) {
        final ServerTurn turn;
        if (!sync.isClient() && data.getPlayer() instanceof EntityPlayerMP player) {
            turn = new ServerTurn(npc, player, sync);
            settings.customContainer(() -> new ModularContainer() {

                @Override
                public void onContainerClosed(EntityPlayer closingPlayer) {
                    turn.close();
                    super.onContainerClosed(closingPlayer);
                }
            });
            sync.addCloseListener(ignored -> turn.close());
            sync.onServerTick(turn::tick);
        } else {
            turn = null;
        }
        // Packet callbacks only decode bounded input into a mailbox. World, permissions, persistence and
        // provider calls are reached exclusively through the server-side panel tick with a live container.
        sync.registerSyncedAction(SEND, false, true, packet -> { if (turn != null) turn.receiveMessage(packet); });
        sync.registerSyncedAction(
            READ_SETTINGS,
            false,
            true,
            packet -> { if (turn != null && packet.readableBytes() == 0) turn.requestSettings(); });
        sync.registerSyncedAction(
            SAVE_SETTINGS,
            false,
            true,
            packet -> { if (turn != null) turn.receiveSettings(packet); });

        StringSyncValue error = outboundString(() -> turn == null ? "" : turn.error);
        BooleanSyncValue pending = outboundBoolean(() -> turn != null && turn.pending);
        StringSyncValue snapshot = outboundString(() -> turn == null ? "" : turn.snapshot);
        StringSyncValue saveStatus = outboundString(() -> turn == null ? "" : turn.saveStatus);
        StringSyncValue history = outboundString(() -> turn == null ? "" : turn.history);
        StringSyncValue usage = outboundString(() -> turn == null ? "" : turn.usage);
        sync.syncValue("error", error);
        sync.syncValue("pending", pending);
        sync.syncValue("settings_snapshot", snapshot);
        sync.syncValue("settings_save_status", saveStatus);
        sync.syncValue("history", history);
        sync.syncValue("usage", usage);

        ModularPanel panel = ModularPanel.defaultPanel("ayanami_dialogue", WIDTH, HEIGHT);
        // The panel constructor sets a center/top anchor; remove it before assigning a bottom edge.
        panel.resizer()
            .resetPosition();
        panel.horizontalCenter()
            .bottom(16)
            .background(new Rectangle().color(0xB91B2230));
        if (sync.isClient()) addHistory(panel, history);
        ChatEntry input = new ChatEntry();
        StringValue draft = new StringValue("");
        input.value(draft)
            .setMaxLength(AyanamiDialogueText.MAX_INPUT)
            .setPattern(SINGLE_LINE)
            .setFocusOnGuiOpen(true)
            .setTextColor(INK)
            .hintColor(MUTED)
            .left(7)
            .top(INPUT_TOP)
            .size(196, 17)
            .background(new Rectangle().color(FIELD))
            .tooltip(t -> t.addLine(IKey.lang(PREFIX + "entry")));
        if (sync.isClient()) input.hintText(translateToLocal(PREFIX + "entry"));
        input.send = () -> {
            if (!sync.isClient() || pending.getBoolValue()) return;
            String text = input.getText()
                .trim();
            if (text.isEmpty() || text.length() > AyanamiDialogueText.MAX_INPUT) return;
            byte[] body = text.getBytes(StandardCharsets.UTF_8);
            sync.callSyncedAction(SEND, packet -> packet.writeBytes(body));
            draft.setStringValue("");
            input.setText("");
        };
        AyanamiChatSettingsGui form = new AyanamiChatSettingsGui(sync, snapshot, saveStatus, SAVE_SETTINGS);
        IPanelHandler popup = sync.isClient() ? IPanelHandler.simple(panel, (parent, player) -> form.build(), true)
            : null;

        return panel.child(label(IKey.lang(PREFIX + "title"), 7, 4, 100, INK))
            .child(label(IKey.lang(PREFIX + "privacy"), 108, 4, 205, MUTED).scale(0.78F))
            .child(input)
            .child(button(IKey.lang(PREFIX + "settings"), 207, INPUT_TOP, 57, () -> {
                if (popup == null || popup.isPanelOpen()) return;
                form.request();
                popup.openPanel();
                sync.callSyncedAction(READ_SETTINGS, packet -> {});
            }))
            .child(button(IKey.lang(PREFIX + SEND), 268, INPUT_TOP, 45, input.send))
            .child(
                label(IKey.dynamic(() -> status(pending, error)), 7, INPUT_TOP + 20, WIDTH - 14, MUTED).scale(0.82F)
                    .color(
                        () -> error.getStringValue()
                            .isEmpty() ? MUTED : ERROR))
            .child(
                label(IKey.dynamic(() -> usageLabel(usage.getStringValue())), 7, INPUT_TOP + 30, WIDTH - 14, MUTED)
                    .scale(0.82F));
    }

    @SideOnly(Side.CLIENT)
    private static void addHistory(ModularPanel panel, StringSyncValue history) {
        panel.child(
            new HistoryView(history).left(7)
                .top(17)
                .size(HISTORY_WIDTH, HISTORY_HEIGHT)
                .background(new Rectangle().color(0x8F0D1420)));
    }

    // A getter-only MUI value still decodes forged client writes into its cache unless this is overridden.
    private static StringSyncValue outboundString(Supplier<String> source) {
        return new StringSyncValue(source) {

            @Override
            public void readOnServer(int id, PacketBuffer packet) {}
        };
    }

    private static BooleanSyncValue outboundBoolean(BooleanSupplier source) {
        return new BooleanSyncValue(source) {

            @Override
            public void readOnServer(int id, PacketBuffer packet) {}
        };
    }

    static TextWidget<?> label(IKey text, int x, int y, int width, int color) {
        return new TextWidget<>(text).color(color)
            .shadow(false)
            .alignment(Alignment.CenterLeft)
            .left(x)
            .top(y)
            .width(width);
    }

    static ButtonWidget<?> button(IKey text, int x, int y, int width, Runnable action) {
        return new ButtonWidget<>().left(x)
            .top(y)
            .size(width, 17)
            .background(new Rectangle().color(BUTTON))
            .hoverBackground(new Rectangle().color(BUTTON_HOVER))
            .overlay(text)
            .onMousePressed(button -> {
                if (button != 0) return false;
                action.run();
                return true;
            });
    }

    private static String status(BooleanSyncValue pending, StringSyncValue error) {
        if (pending.getBoolValue()) return translateToLocal(PREFIX + "waiting");
        String problem = error.getStringValue();
        return problem.isEmpty() ? "" : translateToLocal(problem);
    }

    private static String usageLabel(String snapshot) {
        if (snapshot != null && !snapshot.isEmpty() && snapshot.length() <= MAX_USAGE_CHARS) {
            String[] fields = snapshot.split(":", 4);
            if (fields.length == 3) {
                long input = readTokenCount(fields[0]);
                long output = readTokenCount(fields[1]);
                long total = readTokenCount(fields[2]);
                if (input >= 0 || output >= 0 || total >= 0) {
                    return translateToLocalFormatted(
                        PREFIX + "usage_last",
                        tokenCountLabel(input),
                        tokenCountLabel(output),
                        tokenCountLabel(total));
                }
            }
        }
        return translateToLocal(PREFIX + "usage_unavailable");
    }

    private static long readTokenCount(String value) {
        if (value.isEmpty() || value.length() > 19) return -1;
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) < '0' || value.charAt(i) > '9') return -1;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static String tokenCountLabel(long count) {
        return count < 0 ? "—" : Long.toString(count);
    }

    @SideOnly(Side.CLIENT)
    private static final class HistoryView extends ScrollWidget<HistoryView> {

        private static final int LABEL_X = 4;
        private static final int TEXT_X = 42;
        private static final int SCROLLBAR_WIDTH = 4;
        private static final int PLAYER_COLOR = 0xFFABE0DD;
        private static final int REI_COLOR = 0xFFB5C9FF;
        private final StringSyncValue history;
        private final List<HistoryLine> lines = new ArrayList<>();
        private String cachedSnapshot;
        private int cachedWidth;
        private int lineHeight;

        private HistoryView(StringSyncValue history) {
            super(new VerticalScrollData(false, SCROLLBAR_WIDTH));
            this.history = history;
        }

        @Override
        public void preDraw(ModularGuiContext context, boolean transformed) {
            FontRenderer font = Minecraft.getMinecraft().fontRenderer;
            if (!transformed) {
                updateLines(font);
                super.preDraw(context, false);
                return;
            }
            // MUI2 has applied the scroll transform and stencil. Its solid rectangles can leave texturing off,
            // which turns every glyph quad (ASCII or Unicode) into a filled box when drawn via FontRenderer.
            int activeTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
            GL11.glPushAttrib(
                GL11.GL_ENABLE_BIT | GL11.GL_TEXTURE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_CURRENT_BIT);
            try {
                OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
                GL11.glEnable(GL11.GL_BLEND);
                OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                drawLines(font);
            } finally {
                GL11.glPopAttrib();
                OpenGlHelper.setActiveTexture(activeTexture);
            }
        }

        private void drawLines(FontRenderer font) {
            if (lines.isEmpty()) {
                font.drawString(translateToLocal(PREFIX + "history_empty"), LABEL_X, 3, MUTED);
                return;
            }
            int scrollY = getScrollY();
            int lastY = scrollY + getArea().height;
            int firstLine = Math.max(0, (scrollY - 1) / lineHeight);
            for (int i = firstLine; i < lines.size(); i++) {
                int y = 1 + i * lineHeight;
                if (y >= lastY) break;
                HistoryLine line = lines.get(i);
                if (line.roleKey != null)
                    font.drawString(translateToLocal(PREFIX + line.roleKey), LABEL_X, y, line.color);
                font.drawString(line.text, TEXT_X, y, INK);
            }
        }

        private void updateLines(FontRenderer font) {
            String snapshot = history.getStringValue();
            int width = Math.max(1, getArea().width - TEXT_X - SCROLLBAR_WIDTH - 4);
            int height = font.FONT_HEIGHT + 1;
            if (snapshot.equals(cachedSnapshot) && width == cachedWidth && height == lineHeight) return;
            cachedSnapshot = snapshot;
            cachedWidth = width;
            lineHeight = height;
            lines.clear();
            for (HistoryEntry message : AyanamiDialogueText.decodeHistory(snapshot)) {
                String roleKey = message.assistant ? "history_rei" : "history_you";
                int color = message.assistant ? REI_COLOR : PLAYER_COLOR;
                for (String line : font.listFormattedStringToWidth(message.text, width)) {
                    lines.add(new HistoryLine(roleKey, color, line));
                    roleKey = null;
                }
            }
            VerticalScrollData scroll = getScrollArea().getScrollY();
            int contentHeight = 2 + lines.size() * lineHeight;
            scroll.setScrollSize(contentHeight);
            scroll.scrollTo(getScrollArea(), Math.max(0, contentHeight - getArea().height));
        }

        private static final class HistoryLine {

            private final String roleKey;
            private final int color;
            private final String text;

            private HistoryLine(String roleKey, int color, String text) {
                this.roleKey = roleKey;
                this.color = color;
                this.text = text;
            }
        }
    }

    private static final class ChatEntry extends TextFieldWidget {

        private Runnable send;

        private static boolean isEnter(int key) {
            return key == Keyboard.KEY_RETURN || key == Keyboard.KEY_NUMPADENTER;
        }

        @Override
        public void setText(String text) {
            // MUI2's setText replaces the buffer without updating either cursor. Reset before a shorter value lands.
            handler.setOffsetCursor(0, 0);
            handler.getMainCursor()
                .setLocation(0, 0);
            super.setText(text);
            getScrollData().scrollTo(getScrollArea(), 0);
        }

        @Override
        public Result onKeyPressed(char character, int key) {
            if (!isFocused()) return Result.IGNORE;
            if (isEnter(key)) {
                send.run();
                return Result.SUCCESS;
            }
            if (key == Keyboard.KEY_ESCAPE) {
                getPanel().closeIfOpen();
                return Result.SUCCESS;
            }
            String text = getText();
            int cursor = AyanamiInputEdit.boundary(text, handler.getMainCursor().x);
            int anchor = AyanamiInputEdit.boundary(text, handler.getOffsetCursor().x);
            setSelection(cursor, anchor);
            boolean control = Interactable.hasControlDown();
            boolean shift = Interactable.hasShiftDown();
            if (key == Keyboard.KEY_BACK || key == Keyboard.KEY_DELETE) {
                boolean backward = key == Keyboard.KEY_BACK;
                // Keep Shift+Delete's existing single-line clear shortcut when no selection is active.
                if (!backward && shift && cursor == anchor && cursor < text.length()) {
                    apply(AyanamiInputEdit.delete(text, 0, text.length(), false, false), text);
                } else {
                    apply(AyanamiInputEdit.delete(text, cursor, anchor, backward, control), text);
                }
                return Result.SUCCESS;
            }
            if (key == Keyboard.KEY_LEFT || key == Keyboard.KEY_RIGHT) {
                int next = AyanamiInputEdit.move(text, cursor, key == Keyboard.KEY_LEFT, control);
                setSelection(next, shift ? anchor : next);
                return Result.SUCCESS;
            }
            if (Interactable.isKeyComboCtrlV(key)) {
                insert(GuiScreen.getClipboardString());
                return Result.SUCCESS;
            }
            if (Interactable.isKeyComboCtrlA(key)) {
                setSelection(text.length(), 0);
                return Result.SUCCESS;
            }
            boolean cut = Interactable.isKeyComboCtrlX(key);
            if (Interactable.isKeyComboCtrlC(key) || cut) {
                if (!cut || cursor != anchor) {
                    GuiScreen.setClipboardString(text.substring(Math.min(cursor, anchor), Math.max(cursor, anchor)));
                    if (cut) apply(AyanamiInputEdit.delete(text, cursor, anchor, true, false), text);
                }
                return Result.SUCCESS;
            }
            if (key == Keyboard.KEY_HOME || key == Keyboard.KEY_END
                || key == Keyboard.KEY_UP
                || key == Keyboard.KEY_DOWN) {
                return super.onKeyPressed(character, key); // Normalized, single-line navigation never mutates the
                                                           // buffer.
            }
            if (character == 0 || Character.isISOControl(character) || character == '\u00a7') return Result.STOP;
            if (!ModularUI.Mods.LWJGL3IFY.isLoaded()) insert(String.valueOf(character));
            return Result.SUCCESS;
        }

        @Override
        public boolean onTextInput(InputEvents.TextEvent event) {
            if (!isFocused()) return false;
            insert(event.text);
            return true;
        }

        private void insert(String text) {
            String before = getText();
            apply(
                AyanamiInputEdit.insert(
                    before,
                    handler.getMainCursor().x,
                    handler.getOffsetCursor().x,
                    text,
                    AyanamiDialogueText.MAX_INPUT),
                before);
        }

        private void apply(AyanamiInputEdit.Edit edit, String previous) {
            if (edit == null || !handler.test(edit.text)) return;
            boolean changed = !edit.text.equals(previous);
            // Both endpoints must be valid even while the handler measures the text for horizontal scrolling.
            handler.setOffsetCursor(0, edit.cursor);
            if (changed) super.setText(edit.text);
            handler.setMainCursor(0, edit.cursor, false);
            if (edit.text.isEmpty()) getScrollData().scrollTo(getScrollArea(), 0);
            if (changed) handler.onChanged();
        }

        private void setSelection(int cursor, int anchor) {
            handler.setOffsetCursor(0, anchor);
            handler.setMainCursor(0, cursor, false);
        }

        @Override
        public Result onKeyEvent(InputEvents.KeyEvent event) {
            if (isFocused() && (isEnter(event.lwjgl2KeyCode) || event.lwjgl2KeyCode == Keyboard.KEY_ESCAPE)) {
                if (event.action == InputEvents.KeyAction.PRESSED) {
                    if (isEnter(event.lwjgl2KeyCode)) send.run();
                    else getPanel().closeIfOpen();
                }
                return Result.SUCCESS;
            }
            return super.onKeyEvent(event);
        }
    }

    private static final class SettingsUpdate {

        final String endpoint;
        final String model;
        final boolean enabled;
        final boolean completionTokens;
        final byte[] key;
        final Protocol reasoningProtocol;
        final Mode thinkingMode;
        final Effort reasoningEffort;
        final int maxReplyChars;

        SettingsUpdate(String endpoint, String model, boolean enabled, boolean completionTokens, byte[] key,
            Protocol reasoningProtocol, Mode thinkingMode, Effort reasoningEffort, int maxReplyChars) {
            this.endpoint = endpoint;
            this.model = model;
            this.enabled = enabled;
            this.completionTokens = completionTokens;
            this.key = key;
            this.reasoningProtocol = reasoningProtocol;
            this.thinkingMode = thinkingMode;
            this.reasoningEffort = reasoningEffort;
            this.maxReplyChars = maxReplyChars;
        }

        void eraseKey() {
            Arrays.fill(key, (byte) 0);
        }
    }

    private static final class ServerTurn {

        private final EntityPlayerMP player;
        private final PanelSyncManager sync;
        private final AyanamiDialogue.Session session;
        private final AtomicReference<String> incoming = new AtomicReference<>();
        private final AtomicReference<SettingsUpdate> settingsUpdate = new AtomicReference<>();
        private final AtomicBoolean settingsRead = new AtomicBoolean();
        private final AtomicBoolean closed = new AtomicBoolean();
        private String error = "";
        private String snapshot = "";
        private String saveStatus = "";
        private String history = "";
        private String usage = "";
        private int settingsRevision;
        private int saveRevision;
        private boolean initialized;
        private boolean pending;

        private ServerTurn(EntityAyanami npc, EntityPlayerMP player, PanelSyncManager sync) {
            this.player = player;
            this.sync = sync;
            this.session = new AyanamiDialogue.Session(
                npc,
                player,
                () -> !closed.get() && player.openContainer == sync.getContainer());
        }

        private void receiveMessage(PacketBuffer packet) {
            if (closed.get() || incoming.get() != null) return;
            int length = packet.readableBytes();
            if (length <= 0 || length > AyanamiDialogueText.MAX_INPUT * 3) return;
            try {
                String text = decode(packet, length, AyanamiDialogueText.MAX_INPUT);
                if (text != null) incoming.compareAndSet(null, text);
            } catch (Exception ignored) {
                // Malformed client data never reaches the provider.
            }
        }

        private void requestSettings() {
            if (!closed.get()) settingsRead.set(true);
        }

        private void receiveSettings(PacketBuffer packet) {
            if (closed.get() || settingsUpdate.get() != null
                || packet.readableBytes() < SETTINGS_OVERHEAD_BYTES
                || packet.readableBytes() > (MAX_ENDPOINT + MAX_MODEL) * 3 + MAX_API_KEY + SETTINGS_OVERHEAD_BYTES)
                return;
            byte[] key = null;
            try {
                int options = packet.readUnsignedByte();
                int protocol = packet.readUnsignedByte();
                int thinking = packet.readUnsignedByte();
                int effort = packet.readUnsignedByte();
                int maxReplyChars = packet.readUnsignedShort();
                Protocol[] protocols = Protocol.values();
                Mode[] modes = Mode.values();
                Effort[] efforts = Effort.values();
                if ((options & ~3) != 0 || protocol >= protocols.length
                    || thinking >= modes.length
                    || effort >= efforts.length
                    || !AyanamiDialogueText.validReplyLimit(maxReplyChars)) return;
                String endpoint = decode(packet, packet.readUnsignedShort(), MAX_ENDPOINT);
                String model = decode(packet, packet.readUnsignedShort(), MAX_MODEL);
                if (endpoint == null || model == null) return;
                int keyLength = packet.readUnsignedShort();
                if (keyLength > MAX_API_KEY || keyLength != packet.readableBytes()) return;
                key = new byte[keyLength];
                packet.readBytes(key);
                for (byte value : key) if (value < '!' || value > '~') return;
                SettingsUpdate update = new SettingsUpdate(
                    endpoint,
                    model,
                    (options & 1) != 0,
                    (options & 2) != 0,
                    key,
                    protocols[protocol],
                    modes[thinking],
                    efforts[effort],
                    maxReplyChars);
                if (settingsUpdate.compareAndSet(null, update)) {
                    key = null; // Ownership transferred to the single-entry mailbox.
                    if (closed.get() && settingsUpdate.compareAndSet(update, null)) update.eraseKey();
                }
            } catch (Exception ignored) {
                // Invalid lengths, malformed UTF-8 or trailing data are ignored.
            } finally {
                if (key != null) Arrays.fill(key, (byte) 0);
            }
        }

        private static String decode(PacketBuffer packet, int length, int maxChars)
            throws java.nio.charset.CharacterCodingException {
            if (length < 0 || length > maxChars * 3 || length > packet.readableBytes()) return null;
            byte[] bytes = new byte[length];
            packet.readBytes(bytes);
            String text = StandardCharsets.UTF_8.newDecoder()
                .decode(ByteBuffer.wrap(bytes))
                .toString();
            return text.length() <= maxChars && SINGLE_LINE.matcher(text)
                .matches() ? text : null;
        }

        private void publishSettings() {
            AyanamiDialogue.SettingsView view = session.readSettings();
            snapshot = ++settingsRevision + "\n" + AyanamiChatSettingsGui.encodeView(view);
        }

        private void tick() {
            if (!isCurrent()) {
                if (!closed.get()) {
                    error = PREFIX + "error.unavailable";
                    pending = false;
                    close();
                }
                return;
            }
            session.attendConversation();
            if (!initialized) {
                error = session.configurationError();
                initialized = true;
            }
            if (settingsRead.getAndSet(false)) publishSettings();
            SettingsUpdate update = settingsUpdate.getAndSet(null);
            if (update != null) {
                String outcome;
                try {
                    outcome = session.updateSettings(
                        update.endpoint,
                        update.model,
                        update.enabled,
                        update.completionTokens,
                        new String(update.key, StandardCharsets.US_ASCII),
                        update.reasoningProtocol,
                        update.thinkingMode,
                        update.reasoningEffort,
                        update.maxReplyChars);
                } finally {
                    update.eraseKey();
                }
                if (outcome.isEmpty()) {
                    publishSettings();
                    if (!pending) error = session.configurationError();
                }
                saveStatus = ++saveRevision + "\n" + (outcome.isEmpty() ? PREFIX + "settings_saved" : outcome);
            }
            history = session.historySnapshot();
            usage = session.usageSnapshot();
            String text = incoming.getAndSet(null);
            if (pending) return;
            if (text == null) return;
            error = "";
            pending = true;
            session.submit(text, result -> {
                if (!isCurrent()) return;
                pending = false;
                if (result.error) error = result.text;
            });
        }

        private boolean isCurrent() {
            return !closed.get() && player.openContainer == sync.getContainer() && session.isAvailable();
        }

        private void close() {
            if (!closed.compareAndSet(false, true)) return;
            incoming.set(null);
            SettingsUpdate discarded = settingsUpdate.getAndSet(null);
            if (discarded != null) discarded.eraseKey();
            settingsRead.set(false);
            history = "";
            usage = "";
            session.close();
        }
    }
}
