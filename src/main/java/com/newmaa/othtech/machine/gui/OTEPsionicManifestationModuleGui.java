package com.newmaa.othtech.machine.gui;

import java.util.Locale;

import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.LongSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.newmaa.othtech.machine.OTEPsionicManifestationModule;

import gregtech.api.util.GTUtility;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;

public class OTEPsionicManifestationModuleGui extends MTEMultiBlockBaseGui<OTEPsionicManifestationModule> {

    private static final UITexture BACKGROUND = UITexture.fullImage("123technology", "gui/void_nexus/main_background");

    public OTEPsionicManifestationModuleGui(OTEPsionicManifestationModule module) {
        super(module);
    }

    @Override
    protected ModularPanel getBasePanel(PosGuiData guiData, PanelSyncManager syncManager, UISettings uiSettings) {
        return super.getBasePanel(guiData, syncManager, uiSettings).background(BACKGROUND)
            .disableHoverBackground();
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        syncManager.syncValue("psionicConnected", new BooleanSyncValue(multiblock::isConnected));
        syncManager.syncValue("psionicBuffer", new LongSyncValue(multiblock::getModuleStoredEU));
        syncManager.syncValue("psionicBufferMax", new LongSyncValue(multiblock::getModuleMaxEU));
        syncManager.syncValue("psionicRecipePower", new LongSyncValue(multiblock::getCurrentRecipePower));
        syncManager.syncValue("psionicHostPower", new LongSyncValue(multiblock::getHostInputStoredEU));
        syncManager.syncValue("psionicAvailableAmperage", new LongSyncValue(multiblock::getAvailableModuleAmperage));
        syncManager.syncValue("psionicNaniteTier", new IntSyncValue(multiblock::getNaniteTierLevel));
        syncManager.syncValue(
            "psionicNanitePct",
            new IntSyncValue(() -> (int) Math.round(multiblock.getNaniteSpeedPercent())));
        syncManager.syncValue(
            "psionicPotentialPct",
            new IntSyncValue(() -> (int) Math.round(multiblock.getPotentialSpeedPercent())));
        syncManager.syncValue(
            "psionicPermanentPct",
            new IntSyncValue(() -> (int) Math.round(multiblock.getPermanentSpeedPercent())));
        syncManager.syncValue("psionicOverclockSteps", new IntSyncValue(multiblock::getLastOverclockSteps));
        syncManager.syncValue("psionicOverclockDivisor", new IntSyncValue(multiblock::getLastOverclockDivisor));
        syncManager.syncValue(
            "psionicSpeedDuration",
            new StringSyncValue(() -> formatDurationMultiplier(multiblock.getDurationMultiplier()), value -> {}));
        syncManager.syncValue(
            "psionicFinalDuration",
            new StringSyncValue(() -> formatDurationMultiplier(multiblock.getFinalDurationMultiplier()), value -> {}));
        syncManager.syncValue("psionicParallelLimit", new IntSyncValue(multiblock::getParallelLimit));
        syncManager.syncValue("psionicHostTier", new IntSyncValue(multiblock::getHostStructureTier));
        syncManager
            .syncValue("psionicHostStage", new StringSyncValue(multiblock::getHostStageName, value -> {}).allowC2S());
        syncManager.syncValue("psionicProgress", new IntSyncValue(multiblock::getProgresstime));
        syncManager.syncValue("psionicProgressMax", new IntSyncValue(multiblock::maxProgresstime));
    }

    @Override
    protected ListWidget<IWidget, ?> createTerminalTextWidget(PanelSyncManager syncManager, ModularPanel parent) {
        BooleanSyncValue connected = syncManager.findSyncHandler("psionicConnected", BooleanSyncValue.class);
        LongSyncValue buffer = syncManager.findSyncHandler("psionicBuffer", LongSyncValue.class);
        LongSyncValue bufferMax = syncManager.findSyncHandler("psionicBufferMax", LongSyncValue.class);
        LongSyncValue recipePower = syncManager.findSyncHandler("psionicRecipePower", LongSyncValue.class);
        LongSyncValue hostPower = syncManager.findSyncHandler("psionicHostPower", LongSyncValue.class);
        LongSyncValue availableAmperage = syncManager.findSyncHandler("psionicAvailableAmperage", LongSyncValue.class);
        IntSyncValue naniteTier = syncManager.findSyncHandler("psionicNaniteTier", IntSyncValue.class);
        IntSyncValue nanitePct = syncManager.findSyncHandler("psionicNanitePct", IntSyncValue.class);
        IntSyncValue potentialPct = syncManager.findSyncHandler("psionicPotentialPct", IntSyncValue.class);
        IntSyncValue permanentPct = syncManager.findSyncHandler("psionicPermanentPct", IntSyncValue.class);
        IntSyncValue overclockSteps = syncManager.findSyncHandler("psionicOverclockSteps", IntSyncValue.class);
        IntSyncValue overclockDivisor = syncManager.findSyncHandler("psionicOverclockDivisor", IntSyncValue.class);
        StringSyncValue speedDuration = syncManager.findSyncHandler("psionicSpeedDuration", StringSyncValue.class);
        StringSyncValue finalDuration = syncManager.findSyncHandler("psionicFinalDuration", StringSyncValue.class);
        IntSyncValue parallelLimit = syncManager.findSyncHandler("psionicParallelLimit", IntSyncValue.class);
        IntSyncValue hostTier = syncManager.findSyncHandler("psionicHostTier", IntSyncValue.class);
        StringSyncValue hostStage = syncManager.findSyncHandler("psionicHostStage", StringSyncValue.class);
        IntSyncValue progress = syncManager.findSyncHandler("psionicProgress", IntSyncValue.class);
        IntSyncValue progressMax = syncManager.findSyncHandler("psionicProgressMax", IntSyncValue.class);

        return super.createTerminalTextWidget(syncManager, parent).child(statusLine("ote.pmm.gui.header"))
            .child(
                statusLine(
                    "ote.pmm.gui.link",
                    () -> StatCollector
                        .translateToLocal(connected.getBoolValue() ? "ote.pmm.gui.linked" : "ote.pmm.gui.unlinked")))
            .child(statusLine("ote.pmm.gui.host", () -> "T" + hostTier.getIntValue() + " // " + hostStage.getValue()))
            .child(
                statusLine(
                    "ote.pmm.gui.parallel_limit",
                    () -> GTUtility.formatShortenedLong(parallelLimit.getIntValue())))
            .child(
                statusLine(
                    "ote.pmm.gui.rated",
                    () -> "1A UXV (" + GTUtility.formatShortenedLong(536_870_912L) + " EU/t)"))
            .child(
                statusLine(
                    "ote.pmm.gui.oc_limit",
                    () -> GTUtility.formatShortenedLong(availableAmperage.getLongValue()) + "A UXV"))
            .child(
                statusLine(
                    "ote.pmm.gui.host_buffer",
                    () -> GTUtility.formatShortenedLong(hostPower.getLongValue()) + " EU"))
            .child(
                statusLine(
                    "ote.pmm.gui.buffer",
                    () -> GTUtility.formatShortenedLong(buffer.getLongValue()) + " / "
                        + GTUtility.formatShortenedLong(bufferMax.getLongValue())
                        + " EU"))
            .child(
                statusLine(
                    "ote.pmm.gui.usage",
                    () -> GTUtility.formatShortenedLong(recipePower.getLongValue()) + " EU/t"))
            .child(statusLine("ote.pmm.gui.separator"))
            .child(
                statusLine(
                    "ote.pmm.gui.nanite",
                    () -> "T" + naniteTier.getIntValue() + "  +" + nanitePct.getIntValue() + "%"))
            .child(statusLine("ote.pmm.gui.potential", () -> "+" + potentialPct.getIntValue() + "%"))
            .child(statusLine("ote.pmm.gui.permanent", () -> "+" + permanentPct.getIntValue() + "%"))
            .child(
                statusLine(
                    "ote.pmm.gui.total_speed",
                    () -> String.format(
                        Locale.ROOT,
                        "+%d%%  |  %s",
                        nanitePct.getIntValue() + potentialPct.getIntValue() + permanentPct.getIntValue(),
                        speedDuration.getValue())))
            .child(
                statusLine(
                    "ote.pmm.gui.overclock",
                    () -> overclockSteps.getIntValue() + " rounds  |  /" + overclockDivisor.getIntValue()))
            .child(statusLine("ote.pmm.gui.final_duration", finalDuration::getValue))
            .child(
                statusLine(
                    "ote.pmm.gui.progress",
                    () -> progress.getIntValue() + " / " + progressMax.getIntValue() + " ticks"));
    }

    private static String formatDurationMultiplier(double multiplier) {
        return String.format(Locale.ROOT, "x%.6g", multiplier);
    }

    private static IWidget statusLine(String key) {
        return IKey.lang(key)
            .asWidget()
            .fullWidth()
            .marginBottom(1);
    }

    private static IWidget statusLine(String key, java.util.function.Supplier<String> value) {
        return IKey.dynamic(() -> StatCollector.translateToLocal(key) + value.get())
            .asWidget()
            .fullWidth()
            .marginBottom(1);
    }
}
