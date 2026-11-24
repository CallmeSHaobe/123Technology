package com.newmaa.othtech.machine.single;

import static net.minecraft.util.StatCollector.translateToLocal;
import static net.minecraft.util.StatCollector.translateToLocalFormatted;

import java.util.regex.Pattern;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;

public final class SpeakingStoneUI {

    private static final String PREFIX = "ote.speaking_stone.gui.";
    private static final int URL_LIMIT = OTESpeakingStone.MAX_URL_LENGTH;
    private static final int WIDTH = 244;
    private static final int CONTENT_WIDTH = WIDTH - 20;
    private static final Pattern URL_LINE = Pattern.compile("[^\\p{Cc}\\p{Zl}\\p{Zp}]*");
    private static final Pattern SHOW_FORMATTING = Pattern.compile("(?i)§[0-9A-FK-OR]|[\\p{Cc}\\p{Zl}\\p{Zp}]+");

    private SpeakingStoneUI() {}

    public static ModularPanel build(OTESpeakingStone stone, PanelSyncManager syncManager) {
        StringSyncValue url = new StringSyncValue(stone::getStreamUrl, value -> {
            if (value != null && value.length() <= URL_LIMIT
                && URL_LINE.matcher(value)
                    .matches()) {
                stone.setStreamUrl(value);
            }
        }).allowC2S();
        BooleanSyncValue enabled = new BooleanSyncValue(stone::isBroadcastEnabled, stone::setBroadcastEnabled)
            .allowC2S();
        BooleanSyncValue broadcasting = new BooleanSyncValue(stone::isBroadcasting);
        StringSyncValue radius = new StringSyncValue(
            () -> Integer.toString(stone.getBroadcastRadius()),
            value -> {
                if (value != null && value.matches("[0-9]{1,4}")) stone.setBroadcastRadius(Integer.parseInt(value));
            }).allowC2S();
        syncManager.syncValue("speakingStoneUrl", url);
        syncManager.syncValue("speakingStoneEnabled", enabled);
        syncManager.syncValue("speakingStoneBroadcasting", broadcasting);
        syncManager.syncValue("speakingStoneRadius", radius);

        ModularPanel main = ModularPanel.defaultPanel("speaking_stone", WIDTH, 139);
        // The picker has no independent server state. Its selection uses the main panel's existing URL sync.
        IPanelHandler presets = IPanelHandler.simple(main, (parent, player) -> buildPresets(url), true);
        return main.child(text(IKey.lang("ote.tn.speaking_stone"), 10, 6, CONTENT_WIDTH).alignment(Alignment.Center))
            .child(text(IKey.lang(PREFIX + "stream_url"), 10, 22, CONTENT_WIDTH))
            .child(
                new TextFieldWidget().value(url)
                    .setMaxLength(URL_LIMIT)
                    .setPattern(URL_LINE)
                    .autoUpdateOnChange(false)
                    .left(10)
                    .top(36)
                    .size(CONTENT_WIDTH, 18)
                    .tooltip(t -> t.addLine(IKey.lang(PREFIX + "stream_url.help"))))
            .child(
                new ButtonWidget<>().left(10)
                    .top(60)
                    .size(126, 18)
                    .overlay(IKey.lang(() -> PREFIX + (enabled.getBoolValue() ? "broadcast_on" : "broadcast_off")))
                    .onMousePressed(button -> {
                        if (button != 0) return false;
                        enabled.setBoolValue(!enabled.getBoolValue());
                        return true;
                    }))
            .child(button(IKey.lang(PREFIX + "presets"), 142, 60, 92, presets::openPanel))
            .child(text(IKey.lang(PREFIX + "radius"), 10, 83, 98))
            .child(
                new TextFieldWidget().value(radius)
                    .numbersInt(OTESpeakingStone.MIN_RADIUS, OTESpeakingStone.MAX_RADIUS)
                    .setMaxLength(4)
                    .autoUpdateOnChange(false)
                    .left(108)
                    .top(81)
                    .size(42, 16))
            .child(
                text(
                    IKey.dynamic(() -> translateToLocalFormatted(PREFIX + "cost", stone.getBroadcastRadius())),
                    158,
                    83,
                    76))
            .child(
                text(
                    IKey.lang(() -> PREFIX + (broadcasting.getBoolValue() ? "status_active" : "status_idle")),
                    10,
                    101,
                    CONTENT_WIDTH))
            .child(
                new TextWidget<>(IKey.dynamic(() -> currentShow(stone, broadcasting.getBoolValue())))
                    .alignment(Alignment.TopLeft)
                    .maxWidth(CONTENT_WIDTH)
                    .left(10)
                    .top(113)
                    .size(CONTENT_WIDTH, 22));
    }

    private static ModularPanel buildPresets(StringSyncValue url) {
        SpeakingStonePreset[] stations = SpeakingStonePreset.values();
        int row = 19;
        int backY = 50 + row * stations.length;
        ModularPanel panel = new ModularPanel("speaking_stone_presets") {

            @Override
            public boolean disablePanelsBelow() {
                return true;
            }
        }.size(WIDTH, backY + 26);
        panel.child(text(IKey.lang(PREFIX + "presets"), 10, 7, CONTENT_WIDTH).alignment(Alignment.Center));
        int y = 23;
        for (boolean international : new boolean[] { false, true }) {
            String group = international ? "presets.international" : "presets.domestic";
            panel.child(text(IKey.lang(PREFIX + group), 10, y, CONTENT_WIDTH));
            y += 13;
            for (SpeakingStonePreset station : stations) {
                if (station.isInternational() != international) continue;
                IKey label = IKey.dynamic(() -> {
                    String name = translateToLocal(station.getLabelKey());
                    return station.getUrl()
                        .equals(url.getStringValue()) ? "> " + name : name;
                });
                panel.child(button(label, 10, y, CONTENT_WIDTH, () -> {
                    url.setStringValue(station.getUrl());
                    panel.closeIfOpen();
                }));
                y += row;
            }
        }
        return panel.child(button(IKey.lang(PREFIX + "presets.back"), 10, backY, CONTENT_WIDTH, panel::closeIfOpen));
    }

    private static ButtonWidget<?> button(IKey label, int x, int y, int width, Runnable action) {
        return new ButtonWidget<>().left(x)
            .top(y)
            .size(width, 17)
            .overlay(label)
            .onMousePressed(button -> {
                if (button != 0) return false;
                action.run();
                return true;
            });
    }

    private static TextWidget<?> text(IKey key, int x, int y, int width) {
        return new TextWidget<>(key).alignment(Alignment.CenterLeft)
            .left(x)
            .top(y)
            .size(width, 12);
    }

    private static String currentShow(OTESpeakingStone stone, boolean broadcasting) {
        // This supplier is evaluated by the client widget; the programme title is not a server sync value.
        String show = broadcasting ? stone.getNowPlaying() : null;
        if (show != null) show = SHOW_FORMATTING.matcher(show)
            .replaceAll(" ")
            .trim();
        if (show == null || show.isEmpty()) return translateToLocal(PREFIX + "now_playing.none");
        int maxChars = 36;
        if (show.codePointCount(0, show.length()) > maxChars) {
            show = show.substring(0, show.offsetByCodePoints(0, maxChars)) + "…";
        }
        return translateToLocalFormatted(PREFIX + "now_playing", show);
    }
}
