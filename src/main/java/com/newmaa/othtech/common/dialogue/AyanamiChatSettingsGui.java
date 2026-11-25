package com.newmaa.othtech.common.dialogue;

import static com.newmaa.othtech.common.dialogue.AyanamiChatGui.*;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;

import org.lwjgl.input.Keyboard;

import com.cleanroommc.modularui.ModularUI;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.Rectangle;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.ObjectValue;
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widgets.menu.DropdownWidget;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.newmaa.othtech.common.dialogue.AyanamiReasoning.Effort;
import com.newmaa.othtech.common.dialogue.AyanamiReasoning.Mode;
import com.newmaa.othtech.common.dialogue.AyanamiReasoning.Protocol;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.eigenraven.lwjgl3ify.api.InputEvents;

/** Client-local secondary panel; only the bound parent container exchanges validated settings. */
final class AyanamiChatSettingsGui {

    private static final int WIDTH = 352;
    private static final int HEIGHT = 240;
    private static final Pattern DIGITS = Pattern.compile("[0-9]*");
    private static final int INNER = WIDTH - 18;
    private static final String CUSTOM_MODEL = "";
    private static final String[] MODEL_PRESETS = { CUSTOM_MODEL, "deepseek-flash", "deepseek-v4-pro", "gpt-6.2-sol",
        "gpt-6.2-astra", "gpt-6-luna", "gpt-4.1-mini" };

    private final PanelSyncManager sync;
    private final StringSyncValue snapshot;
    private final StringSyncValue saveStatus;
    private final String saveAction;
    private final StringValue endpoint = new StringValue("");
    private final StringValue model = new StringValue("");
    private final StringValue replyLength = new StringValue("");
    private TextFieldWidget endpointField;
    private TextFieldWidget modelField;
    private TextFieldWidget replyLengthField;
    private MaskedKeyField keyField;
    private String previousSnapshot = "";
    private String previousSave = "";
    private String statusKey = PREFIX + "settings_loading";
    private boolean canEdit;
    private boolean keyConfigured;
    private boolean canWriteKey;
    private boolean saving;
    private boolean enabled;
    private boolean completionTokens;
    private Protocol reasoningProtocol = Protocol.AUTO;
    private Mode thinkingMode = Mode.DEFAULT;
    private Effort reasoningEffort = Effort.DEFAULT;

    AyanamiChatSettingsGui(PanelSyncManager sync, StringSyncValue snapshot, StringSyncValue saveStatus,
        String saveAction) {
        this.sync = sync;
        this.snapshot = snapshot;
        this.saveStatus = saveStatus;
        this.saveAction = saveAction;
    }

    static String encodeView(AyanamiDialogue.SettingsView view) {
        if (view == null) return "";
        return line(view.endpoint, MAX_ENDPOINT) + '\n'
            + line(view.model, MAX_MODEL)
            + '\n'
            + (view.enabled ? "1" : "0")
            + (view.useMaxCompletionTokens ? '1' : '0')
            + (view.keyConfigured ? '1' : '0')
            + (view.canWriteKey ? '1' : '0')
            + (char) ('0' + view.reasoningProtocol.ordinal())
            + (char) ('0' + view.thinkingMode.ordinal())
            + (char) ('0' + view.reasoningEffort.ordinal())
            + '\n'
            + view.maxReplyChars;
    }

    private static String line(String value, int limit) {
        return value != null && value.length() <= limit
            && SINGLE_LINE.matcher(value)
                .matches() ? value : "";
    }

    void request() {
        previousSnapshot = snapshot.getStringValue();
        previousSave = saveStatus.getStringValue();
        statusKey = PREFIX + "settings_loading";
        canEdit = false;
        saving = false;
        keyConfigured = false;
        canWriteKey = false;
        clearKey();
        enabled = false;
        completionTokens = false;
        reasoningProtocol = Protocol.AUTO;
        thinkingMode = Mode.DEFAULT;
        reasoningEffort = Effort.DEFAULT;
        applyText("", "", "");
    }

    ModularPanel build() {
        ModularPanel panel = new ModularPanel("ayanami_dialogue_settings") {

            @Override
            public boolean disablePanelsBelow() {
                return true;
            }

            @Override
            public boolean isDraggable() {
                return false;
            }
        }.size(WIDTH, HEIGHT)
            .background(new Rectangle().color(0xF4252937));
        endpointField = field(endpoint, MAX_ENDPOINT, 31);
        modelField = field(model, MAX_MODEL, 62).width(194);
        replyLengthField = field(replyLength, 4, 181).setPattern(DIGITS)
            .left(WIDTH - 69)
            .width(60);
        SelectMenu<String> presets = menu(
            "presets",
            String.class,
            this::modelPreset,
            this::selectModel,
            () -> MODEL_PRESETS,
            value -> value.equals(CUSTOM_MODEL) ? translateToLocal(PREFIX + "model_custom") : value,
            210,
            62,
            133).directionDown();
        SelectMenu<Protocol> protocol = menu("protocol", Protocol.class, () -> reasoningProtocol, value -> {
            reasoningProtocol = value;
            normalizeReasoning();
        }, Protocol::values, value -> choiceLabel("reasoning_protocol", value), 9, 128, 112);
        SelectMenu<Mode> mode = menu("thinking", Mode.class, () -> thinkingMode, value -> {
            thinkingMode = value;
            normalizeReasoning();
        }, this::allowedModes, value -> choiceLabel("thinking_mode", value), 130, 128, 100);
        SelectMenu<Effort> effort = menu(
            "effort",
            Effort.class,
            () -> reasoningEffort,
            value -> reasoningEffort = value,
            this::allowedEfforts,
            value -> choiceLabel("reasoning_effort", value),
            239,
            128,
            104);
        keyField = new MaskedKeyField();
        keyField.setTextColor(INK)
            .hintColor(MUTED)
            .hintText(translateToLocal(PREFIX + "api_key_hint"))
            .left(9)
            .top(93)
            .size(INNER, 17)
            .background(new Rectangle().color(FIELD))
            .setEnabledIf(w -> canEdit && canWriteKey && !saving);
        return panel.onCloseAction(this::clearKey)
            .onUpdateListener(ignored -> update(), true)
            .child(label(IKey.lang(PREFIX + "settings"), 9, 6, 150, INK))
            .child(button(IKey.lang(PREFIX + "close"), WIDTH - 59, 5, 50, panel::closeIfOpen))
            .child(label(IKey.lang(PREFIX + "endpoint"), 9, 21, INNER, MUTED).setEnabledIf(w -> canEdit))
            .child(endpointField)
            .child(label(IKey.lang(PREFIX + "model"), 9, 52, 194, MUTED).setEnabledIf(w -> canEdit))
            .child(label(IKey.lang(PREFIX + "model_presets"), 210, 52, 133, MUTED).setEnabledIf(w -> canEdit))
            .child(modelField)
            .child(presets)
            .child(label(IKey.lang(PREFIX + "api_key"), 9, 83, 55, MUTED).setEnabledIf(w -> canEdit))
            .child(
                label(IKey.dynamic(this::keyLabel), 67, 83, INNER - 58, MUTED).scale(0.74F)
                    .setEnabledIf(w -> canEdit))
            .child(keyField)
            .child(label(IKey.lang(PREFIX + "reasoning_protocol"), 9, 117, 112, MUTED).setEnabledIf(w -> canEdit))
            .child(label(IKey.lang(PREFIX + "thinking_mode"), 130, 117, 100, MUTED).setEnabledIf(w -> canEdit))
            .child(label(IKey.lang(PREFIX + "reasoning_effort"), 239, 117, 104, MUTED).setEnabledIf(w -> canEdit))
            .child(protocol)
            .child(mode)
            .child(effort)
            .child(
                label(IKey.lang(PREFIX + "enabled"), 9, 162, 78, INK).scale(0.9F)
                    .setEnabledIf(w -> canEdit))
            .child(
                button(
                    IKey.dynamic(() -> onOff(enabled)),
                    86,
                    157,
                    40,
                    () -> { if (canEdit && !saving) enabled = !enabled; }).setEnabledIf(w -> canEdit && !saving))
            .child(
                label(IKey.lang(PREFIX + "completion_tokens"), 141, 162, 147, INK).scale(0.78F)
                    .setEnabledIf(w -> canEdit))
            .child(
                button(
                    IKey.dynamic(() -> onOff(usesCompletionTokens())),
                    WIDTH - 49,
                    157,
                    40,
                    () -> { if (canEdit && !saving && !fixedCompletionTokens()) completionTokens = !completionTokens; })
                        .setEnabledIf(w -> canEdit && !saving))
            .child(label(IKey.lang(PREFIX + "max_reply_chars"), 9, 186, 258, INK).setEnabledIf(w -> canEdit))
            .child(replyLengthField)
            .child(
                button(IKey.lang(PREFIX + "save"), WIDTH - 59, 213, 50, this::save)
                    .setEnabledIf(w -> canEdit && !saving))
            .child(
                label(IKey.dynamic(() -> statusKey.isEmpty() ? "" : translateToLocal(statusKey)), 9, 213, 278, MUTED)
                    .scale(0.85F)
                    .color(() -> statusKey.startsWith(PREFIX + "error.") ? ERROR : MUTED));
    }

    private String modelPreset() {
        String current = modelField.getText()
            .trim();
        for (String candidate : MODEL_PRESETS) if (candidate.equals(current)) return candidate;
        return CUSTOM_MODEL;
    }

    private void selectModel(String preset) {
        if (CUSTOM_MODEL.equals(preset)) {
            if (modelField.isValid()) modelField.getContext()
                .focus(modelField);
            return;
        }
        // A preset changes the model ID only; endpoint and masked key draft are intentionally untouched.
        model.setStringValue(preset);
        modelField.setText(preset);
        normalizeReasoning();
    }

    private Protocol resolvedProtocol() {
        return AyanamiReasoning.resolve(
            reasoningProtocol,
            endpointField.getText()
                .trim(),
            modelField.getText()
                .trim());
    }

    private Mode[] allowedModes() {
        return AyanamiReasoning.modes(
            resolvedProtocol(),
            modelField.getText()
                .trim());
    }

    private Effort[] allowedEfforts() {
        return AyanamiReasoning.efforts(
            resolvedProtocol(),
            modelField.getText()
                .trim(),
            thinkingMode);
    }

    private void normalizeReasoning() {
        if (!Arrays.asList(allowedModes())
            .contains(thinkingMode)) thinkingMode = Mode.DEFAULT;
        if (!Arrays.asList(allowedEfforts())
            .contains(reasoningEffort)) reasoningEffort = Effort.DEFAULT;
    }

    private Protocol tokenProtocol() {
        return AyanamiReasoning.tokenProtocol(
            resolvedProtocol(),
            endpointField.getText(),
            modelField.getText()
                .trim());
    }

    private boolean fixedCompletionTokens() {
        Protocol resolved = tokenProtocol();
        return resolved == Protocol.DEEPSEEK || AyanamiReasoning.needsCompletionTokens(
            resolved,
            modelField.getText()
                .trim());
    }

    private boolean usesCompletionTokens() {
        Protocol resolved = tokenProtocol();
        return resolved != Protocol.DEEPSEEK && (completionTokens || AyanamiReasoning.needsCompletionTokens(
            resolved,
            modelField.getText()
                .trim()));
    }

    private static String choiceLabel(String key, Enum<?> value) {
        return translateToLocal(
            PREFIX + key
                + '_'
                + value.name()
                    .toLowerCase(Locale.ROOT));
    }

    private <T> SelectMenu<T> menu(String name, Class<T> type, Supplier<T> getter, Consumer<T> setter,
        Supplier<T[]> choices, Function<T, String> title, int x, int y, int width) {
        return new SelectMenu<>("ayanami_settings_" + name, type, choices)
            .value(new ObjectValue.Dynamic<>(type, getter, setter))
            .optionToWidget(
                (item,
                    selected) -> label(
                        IKey.dynamic(() -> selected ? title.apply(getter.get()) + " ▾" : title.apply(item)),
                        4,
                        2,
                        width - 8,
                        INK).height(12))
            .maxVerticalMenuSize(104)
            .directionUp()
            .left(x)
            .top(y)
            .size(width, 17)
            .background(new Rectangle().color(BUTTON))
            .hoverBackground(new Rectangle().color(BUTTON_HOVER))
            .setEnabledIf(w -> canEdit && !saving);
    }

    private static final class SelectMenu<T> extends DropdownWidget<T, SelectMenu<T>> {

        private final Supplier<T[]> choices;
        private T[] previous;

        SelectMenu(String panelName, Class<T> valueType, Supplier<T[]> choices) {
            super(panelName, valueType);
            this.choices = choices;
        }

        @Override
        public void openMenu(boolean soft) {
            if (!isOpen()) {
                T[] current = choices.get();
                if (current.length < 2) return;
                if (!Arrays.equals(previous, current)) {
                    clearOptions().options(current);
                    deleteMenu();
                    previous = current;
                }
            }
            super.openMenu(soft);
        }
    }

    private TextFieldWidget field(StringValue value, int maxLength, int y) {
        return new TextFieldWidget().value(value)
            .setMaxLength(maxLength)
            .setPattern(SINGLE_LINE)
            .setTextColor(INK)
            .left(9)
            .top(y)
            .size(INNER, 17)
            .background(new Rectangle().color(FIELD))
            .setEnabledIf(w -> canEdit && !saving);
    }

    private static String onOff(boolean state) {
        return translateToLocal(PREFIX + (state ? "option_on" : "option_off"));
    }

    private String keyLabel() {
        if (!canWriteKey) return translateToLocal(PREFIX + "api_key_read_only");
        return translateToLocal(PREFIX + (keyConfigured ? "api_key_configured" : "api_key_missing"));
    }

    private void clearKey() {
        if (keyField != null) keyField.clearSecret();
    }

    private void update() {
        String value = snapshot.getStringValue();
        if (!previousSnapshot.equals(value)) {
            previousSnapshot = value;
            String[] fields = value.split("\n", -1);
            canEdit = fields.length == 5 && fields[3].length() == 7 && parseReplyLength(fields[4]) != -1;
            statusKey = canEdit ? "" : PREFIX + "settings_denied";
            canWriteKey = canEdit && fields[3].charAt(3) == '1' && canTransportKey();
            if (!canWriteKey) clearKey();
            if (canEdit) {
                String flags = fields[3];
                applyText(fields[1], fields[2], fields[4]);
                enabled = flags.charAt(0) == '1';
                completionTokens = flags.charAt(1) == '1';
                keyConfigured = flags.charAt(2) == '1';
                reasoningProtocol = readChoice(flags.charAt(4), Protocol.values(), Protocol.AUTO);
                thinkingMode = readChoice(flags.charAt(5), Mode.values(), Mode.DEFAULT);
                reasoningEffort = readChoice(flags.charAt(6), Effort.values(), Effort.DEFAULT);
            }
        }
        if (canEdit) normalizeReasoning();
        String outcome = saveStatus.getStringValue();
        if (!previousSave.equals(outcome)) {
            previousSave = outcome;
            if (saving) {
                saving = false;
                statusKey = outcome.substring(outcome.indexOf('\n') + 1);
            }
        }
    }

    private static <T> T readChoice(char digit, T[] choices, T fallback) {
        int ordinal = digit - '0';
        return ordinal >= 0 && ordinal < choices.length ? choices[ordinal] : fallback;
    }

    private static int parseReplyLength(String value) {
        if (value.isEmpty() || value.length() > 4
            || !DIGITS.matcher(value)
                .matches())
            return -1;
        int length = Integer.parseInt(value);
        return AyanamiDialogueText.validReplyLimit(length) ? length : -1;
    }

    private void save() {
        if (!canEdit || saving) return;
        normalizeReasoning();
        String address = endpointField.getText()
            .trim();
        String id = modelField.getText()
            .trim();
        if (address.isEmpty() || id.isEmpty() || address.length() > MAX_ENDPOINT || id.length() > MAX_MODEL) {
            statusKey = PREFIX + "error.configuration";
            return;
        }
        int maxReplyChars = parseReplyLength(replyLengthField.getText());
        if (maxReplyChars < 0) {
            statusKey = PREFIX + "error.reply_length_invalid";
            return;
        }
        byte[] addressBytes = address.getBytes(StandardCharsets.UTF_8);
        byte[] idBytes = id.getBytes(StandardCharsets.UTF_8);
        if (keyField.hasSecret() && (!canWriteKey || !canTransportKey())) {
            clearKey();
            statusKey = PREFIX + "error.key_channel";
            return;
        }
        int options = (enabled ? 1 : 0) | (completionTokens ? 2 : 0);
        byte[] keyBytes = keyField.copySecret();
        clearKey();
        saving = true;
        statusKey = PREFIX + "settings_saving";
        try {
            sync.callSyncedAction(saveAction, packet -> {
                packet.writeByte(options);
                packet.writeByte(reasoningProtocol.ordinal());
                packet.writeByte(thinkingMode.ordinal());
                packet.writeByte(reasoningEffort.ordinal());
                packet.writeShort(maxReplyChars);
                packet.writeShort(addressBytes.length);
                packet.writeBytes(addressBytes);
                packet.writeShort(idBytes.length);
                packet.writeBytes(idBytes);
                packet.writeShort(keyBytes.length);
                packet.writeBytes(keyBytes);
            });
        } finally {
            Arrays.fill(keyBytes, (byte) 0);
        }
    }

    @SideOnly(Side.CLIENT)
    private static boolean canTransportKey() {
        NetHandlerPlayClient handler = Minecraft.getMinecraft()
            .getNetHandler();
        return handler != null && AyanamiDialogue.canTransportKey(handler.getNetworkManager());
    }

    private void applyText(String address, String id, String replyChars) {
        endpoint.setStringValue(address);
        model.setStringValue(id);
        if (endpointField != null) endpointField.setText(address);
        if (modelField != null) modelField.setText(id);
        replyLength.setStringValue(replyChars);
        if (replyLengthField != null) replyLengthField.setText(replyChars);
    }

    /** A write-only draft. MUI's text storage, selection, undo, renderer and tooltip see at most mask characters. */
    @SideOnly(Side.CLIENT)
    private static final class MaskedKeyField extends TextFieldWidget {

        private final char[] secret = new char[MAX_API_KEY];
        private int length;

        private boolean hasSecret() {
            return length != 0;
        }

        private byte[] copySecret() {
            byte[] bytes = new byte[length];
            for (int i = 0; i < bytes.length; i++) bytes[i] = (byte) secret[i];
            return bytes;
        }

        private void clearSecret() {
            clearDraft();
            if (isValid() && isFocused()) getContext().removeFocus();
        }

        private void clearDraft() {
            Arrays.fill(secret, '\0');
            length = 0;
            updateMask();
        }

        private void updateMask() {
            // Cap the visible length; it neither scrolls the credential out of view nor reveals its full length.
            char[] mask = new char[Math.min(length, 32)];
            Arrays.fill(mask, '*');
            String displayed = new String(mask);
            setText(displayed);
            if (getStringValue() != null) getStringValue().setStringValue(displayed);
        }

        private void append(char input) {
            if (input >= '!' && input <= '~' && length < secret.length) {
                secret[length++] = input;
                updateMask();
            }
        }

        private void append(CharSequence input) {
            if (input == null || input.length() == 0 || input.length() > secret.length - length) return;
            for (int i = 0; i < input.length(); i++) {
                char c = input.charAt(i);
                if (c < '!' || c > '~') return;
            }
            for (int i = 0; i < input.length(); i++) secret[length++] = input.charAt(i);
            updateMask();
        }

        @Override
        public void dispose() {
            clearDraft();
            super.dispose();
        }

        @Override
        public Result onMousePressed(int button) {
            if (!isHovering()) return Result.IGNORE;
            if (button == 1) clearDraft();
            return Result.SUCCESS;
        }

        @Override
        public void onMouseDrag(int button, long timeSinceClick) {}

        private void handleKey(int key) {
            if (key == Keyboard.KEY_ESCAPE) {
                clearSecret();
                getPanel().closeIfOpen();
            } else if (key == Keyboard.KEY_RETURN || key == Keyboard.KEY_NUMPADENTER) {
                getContext().removeFocus();
            } else if (key == Keyboard.KEY_BACK || key == Keyboard.KEY_DELETE) {
                if (Interactable.hasControlDown()) clearDraft();
                else if (length > 0) {
                    secret[--length] = '\0';
                    updateMask();
                }
            } else if (Interactable.isKeyComboCtrlV(key)) {
                if (length == 0) append(GuiScreen.getClipboardString());
            }
            // Never invoke MUI Ctrl+C/Ctrl+X handling; there is deliberately no copy or cut.
        }

        @Override
        public Result onKeyPressed(char character, int key) {
            if (!isFocused()) return Result.IGNORE;
            if (!ModularUI.Mods.LWJGL3IFY.isLoaded()) {
                handleKey(key);
                if (!Interactable.hasControlDown() && !Interactable.hasAltDown()) append(character);
            }
            return Result.SUCCESS;
        }

        @Override
        @Optional.Method(modid = ModularUI.ModIds.LWJGL3IFY)
        public Result onKeyEvent(InputEvents.KeyEvent event) {
            if (!isFocused()) return Result.IGNORE;
            if (event.action == InputEvents.KeyAction.PRESSED) handleKey(event.lwjgl2KeyCode);
            return Result.SUCCESS;
        }

        @Override
        @Optional.Method(modid = ModularUI.ModIds.LWJGL3IFY)
        public boolean onTextInput(InputEvents.TextEvent event) {
            if (!isFocused()) return false;
            if (!Interactable.hasControlDown() && !Interactable.hasAltDown()) append(event.text);
            return true;
        }
    }
}
