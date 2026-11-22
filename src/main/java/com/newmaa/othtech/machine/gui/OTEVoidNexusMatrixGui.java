package com.newmaa.othtech.machine.gui;

import java.util.Locale;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.drawable.Rectangle;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.LongSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widget.ScrollWidget;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.newmaa.othtech.common.compat.thaumcraft.VoidNexusUpgrade;
import com.newmaa.othtech.machine.OTEVoidNexusMatrix;
import com.newmaa.othtech.machine.machineclass.voidnexus.VoidNexusCalculator;

import gregtech.api.util.GTUtility;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;

public class OTEVoidNexusMatrixGui extends MTEMultiBlockBaseGui<OTEVoidNexusMatrix> {

    private static final String TEXTURE_ROOT = "gui/void_nexus/";
    private static final UITexture MAIN_BACKGROUND = UITexture
        .fullImage("123technology", TEXTURE_ROOT + "main_background");
    private static final UITexture UPGRADE_BACKGROUND = UITexture
        .fullImage("123technology", TEXTURE_ROOT + "upgrade_background");
    private static final UITexture UPGRADE_ICON = transparentImage(TEXTURE_ROOT + "upgrade_icon");
    private static final UITexture WIRELESS_ON = transparentImage(TEXTURE_ROOT + "wireless_on");
    private static final UITexture WIRELESS_OFF = transparentImage(TEXTURE_ROOT + "wireless_off");
    private static final UITexture POWER_ON = transparentImage(TEXTURE_ROOT + "control_power_on");
    private static final UITexture POWER_OFF = transparentImage(TEXTURE_ROOT + "control_power_off");
    private static final UITexture REFRESH = transparentImage(TEXTURE_ROOT + "control_refresh");
    private static final UITexture SOUND_ON = transparentImage(TEXTURE_ROOT + "control_sound_on");
    private static final UITexture SOUND_OFF = transparentImage(TEXTURE_ROOT + "control_sound_off");
    private static final UITexture POWER_PANEL = transparentImage(TEXTURE_ROOT + "control_energy");
    private static final UITexture ITEM_INPUT = transparentImage(TEXTURE_ROOT + "control_item");
    private static final UITexture ESSENTIA_INPUT = transparentImage(TEXTURE_ROOT + "control_essentia");
    private static final UITexture NODE_MAIN_ON = transparentImage(TEXTURE_ROOT + "node_main_on");
    private static final UITexture NODE_BRANCH_ON = transparentImage(TEXTURE_ROOT + "node_branch_on");
    private static final UITexture NODE_OFF = transparentImage(TEXTURE_ROOT + "node_off");
    private static final UITexture[] CORE_FRAMES = { transparentImage(TEXTURE_ROOT + "core_0"),
        transparentImage(TEXTURE_ROOT + "core_1"), transparentImage(TEXTURE_ROOT + "core_2"),
        transparentImage(TEXTURE_ROOT + "core_3"), transparentImage(TEXTURE_ROOT + "core_4"),
        transparentImage(TEXTURE_ROOT + "core_5"), transparentImage(TEXTURE_ROOT + "core_6"),
        transparentImage(TEXTURE_ROOT + "core_7") };

    private static final int NODE_SIZE = 22;
    private static final int MAIN_X = 117;
    private static final int PANEL_WIDTH = 288;
    private static final int PANEL_HEIGHT = 330;
    private static final int TREE_WIDTH = 268;
    private static final int TREE_HEIGHT = 252;
    private static final int SCROLL_SIZE = 990;
    private static final int FAR_LEFT_X = 38;
    private static final int INNER_LEFT_X = 78;
    private static final int INNER_RIGHT_X = 156;
    private static final int FAR_RIGHT_X = 196;
    private static final int[] NODE_X = { MAIN_X, FAR_LEFT_X, FAR_RIGHT_X, INNER_LEFT_X, MAIN_X, MAIN_X, INNER_LEFT_X,
        INNER_RIGHT_X, FAR_LEFT_X, FAR_RIGHT_X, MAIN_X, INNER_LEFT_X, INNER_RIGHT_X, FAR_LEFT_X, FAR_RIGHT_X, MAIN_X,
        INNER_LEFT_X, INNER_RIGHT_X, FAR_LEFT_X, FAR_RIGHT_X, INNER_LEFT_X, INNER_RIGHT_X, FAR_LEFT_X, FAR_RIGHT_X,
        MAIN_X, INNER_LEFT_X, INNER_RIGHT_X, MAIN_X };
    private static final int[] NODE_Y = { 42, 88, 88, 134, 188, 244, 292, 292, 340, 340, 404, 452, 452, 500, 500, 564,
        612, 612, 660, 660, 708, 708, 756, 756, 820, 868, 868, 936 };

    public OTEVoidNexusMatrixGui(OTEVoidNexusMatrix multiblock) {
        super(multiblock);
    }

    @Override
    protected ModularPanel getBasePanel(PosGuiData guiData, PanelSyncManager syncManager, UISettings uiSettings) {
        return super.getBasePanel(guiData, syncManager, uiSettings).background(MAIN_BACKGROUND)
            .disableHoverBackground();
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        syncManager.syncValue("voidPower", new LongSyncValue(multiblock::getActivePower));
        syncManager.syncValue("voidParallel", new LongSyncValue(multiblock::getMaxVoidParallel));
        syncManager.syncValue("voidConsumed", new LongSyncValue(multiblock::getLastConsumedEssentia));
        LongSyncValue potential = new LongSyncValue(multiblock::getVirtualPotential);
        syncManager.syncValue("voidPotential", potential);
        LongSyncValue upgrades = new LongSyncValue(multiblock::getUpgradeMask);
        StringSyncValue purchaseStatus = new StringSyncValue(multiblock::getUpgradeStatusKey, value -> {});
        StringSyncValue potentialStatus = new StringSyncValue(multiblock::getPotentialTransferStatusKey, value -> {});
        syncManager.syncValue("voidUpgradeMask", upgrades);
        syncManager.syncValue("voidPurchaseStatus", purchaseStatus);
        syncManager.syncValue("voidPotentialStatus", potentialStatus);
        syncManager.syncValue("voidStructureTier", new IntSyncValue(multiblock::getStructureTier));
        syncManager.syncValue("voidModules", new IntSyncValue(multiblock::getConnectedPsionicModuleCount));
        syncManager.syncValue("voidWandVis", new IntSyncValue(multiblock::getHostWandVisPoints));
        syncManager.syncValue("voidU26", new IntSyncValue(multiblock::getU26BonusPercent));
        syncManager.syncValue("voidStarArray", new BooleanSyncValue(multiblock::isStarArrayActive));
        syncManager.syncValue("voidStargateCore", new BooleanSyncValue(multiblock::isStargateCoreDetected));
        syncManager.syncValue("voidStage", new StringSyncValue(multiblock::getStageName, value -> {}).allowC2S());
        syncManager.syncValue(
            "voidDeviation",
            new StringSyncValue(() -> String.format(Locale.ROOT, "%.4f", multiblock.getLastDeviation()), value -> {})
                .allowC2S());
        syncManager.syncValue(
            "voidHeat",
            new StringSyncValue(() -> String.format(Locale.ROOT, "%.1f", multiblock.getLastAverageHeat()), value -> {})
                .allowC2S());
        syncManager.syncValue(
            "voidWireless",
            new BooleanSyncValue(multiblock::isWirelessModeEnabled, multiblock::setWirelessModeEnabled).allowC2S());
        syncManager.syncValue(
            "voidMaterialInput",
            new BooleanSyncValue(multiblock::isMaterialInputModeEnabled, multiblock::setMaterialInputModeEnabled)
                .allowC2S());
        syncManager.syncValue(
            "voidAnimation",
            new BooleanSyncValue(multiblock::isVoidAnimationEnabled, multiblock::setVoidAnimationEnabled).allowC2S());
        syncManager.syncValue(
            "voidAutoPotential",
            new BooleanSyncValue(
                multiblock::isAutomaticPotentialOutputEnabled,
                multiblock::setAutomaticPotentialOutputEnabled).allowC2S());
        syncManager.syncValue("voidPurchase", new LongSyncValue(() -> 0L, upgradeId -> {
            if (multiblock.getBaseMetaTileEntity() == null || !multiblock.getBaseMetaTileEntity()
                .isServerSide()) return;
            if (multiblock.tryPurchaseUpgrade((int) upgradeId)) {
                upgrades.notifyUpdate();
            }
            purchaseStatus.notifyUpdate();
        }).allowC2S());
        syncManager.syncValue("voidPotentialTransfer", new LongSyncValue(() -> 0L, operation -> {
            if (multiblock.getBaseMetaTileEntity() == null || !multiblock.getBaseMetaTileEntity()
                .isServerSide()) return;
            if (operation == 1L) {
                multiblock.depositPotentialFromInputBus();
            } else if (operation == 2L) {
                multiblock.withdrawPotentialToOutputBus();
            } else {
                return;
            }
            potential.notifyUpdate();
            potentialStatus.notifyUpdate();
        }).allowC2S());
    }

    @Override
    protected int getTextBoxToInventoryGap() {
        return 62;
    }

    @Override
    protected Flow createLeftPanelGapRow(ModularPanel parent, PanelSyncManager syncManager) {
        BooleanSyncValue animation = syncManager.findSyncHandler("voidAnimation", BooleanSyncValue.class);
        BooleanSyncValue wireless = syncManager.findSyncHandler("voidWireless", BooleanSyncValue.class);
        BooleanSyncValue material = syncManager.findSyncHandler("voidMaterialInput", BooleanSyncValue.class);
        LongSyncValue upgrades = syncManager.findSyncHandler("voidUpgradeMask", LongSyncValue.class);
        IntSyncValue tier = syncManager.findSyncHandler("voidStructureTier", IntSyncValue.class);
        StringSyncValue stage = syncManager.findSyncHandler("voidStage", StringSyncValue.class);
        return super.createLeftPanelGapRow(parent, syncManager).child(createUpgradeMapButton(syncManager))
            .child(createPotentialPanelButton(syncManager))
            .child(
                createModeToggleButton(
                    animation,
                    new DynamicDrawable(
                        () -> animation.getBoolValue()
                            ? CORE_FRAMES[(int) ((System.currentTimeMillis() / 160L) % CORE_FRAMES.length)]
                            : NODE_OFF),
                    "ote.vnm.gui.mode.animation",
                    null,
                    upgrades,
                    tier,
                    stage))
            .child(
                createModeToggleButton(
                    wireless,
                    new DynamicDrawable(() -> wireless.getBoolValue() ? WIRELESS_ON : WIRELESS_OFF),
                    "ote.vnm.gui.mode.wireless",
                    VoidNexusUpgrade.ENTER_THE_VOID,
                    upgrades,
                    tier,
                    stage))
            .child(
                createModeToggleButton(
                    material,
                    new DynamicDrawable(() -> material.getBoolValue() ? ITEM_INPUT : ESSENTIA_INPUT),
                    "ote.vnm.gui.mode.material",
                    VoidNexusUpgrade.EARTH_SHAPER,
                    upgrades,
                    tier,
                    stage));
    }

    @Override
    protected ListWidget<IWidget, ?> createTerminalTextWidget(PanelSyncManager syncManager, ModularPanel parent) {
        LongSyncValue power = syncManager.findSyncHandler("voidPower", LongSyncValue.class);
        LongSyncValue parallel = syncManager.findSyncHandler("voidParallel", LongSyncValue.class);
        LongSyncValue consumed = syncManager.findSyncHandler("voidConsumed", LongSyncValue.class);
        LongSyncValue potential = syncManager.findSyncHandler("voidPotential", LongSyncValue.class);
        LongSyncValue upgrades = syncManager.findSyncHandler("voidUpgradeMask", LongSyncValue.class);
        IntSyncValue tier = syncManager.findSyncHandler("voidStructureTier", IntSyncValue.class);
        IntSyncValue modules = syncManager.findSyncHandler("voidModules", IntSyncValue.class);
        IntSyncValue wandVis = syncManager.findSyncHandler("voidWandVis", IntSyncValue.class);
        IntSyncValue u26 = syncManager.findSyncHandler("voidU26", IntSyncValue.class);
        BooleanSyncValue starArray = syncManager.findSyncHandler("voidStarArray", BooleanSyncValue.class);
        BooleanSyncValue stargateCore = syncManager.findSyncHandler("voidStargateCore", BooleanSyncValue.class);
        StringSyncValue stage = syncManager.findSyncHandler("voidStage", StringSyncValue.class);
        StringSyncValue deviation = syncManager.findSyncHandler("voidDeviation", StringSyncValue.class);
        StringSyncValue heat = syncManager.findSyncHandler("voidHeat", StringSyncValue.class);
        BooleanSyncValue wireless = syncManager.findSyncHandler("voidWireless", BooleanSyncValue.class);
        BooleanSyncValue material = syncManager.findSyncHandler("voidMaterialInput", BooleanSyncValue.class);

        return super.createTerminalTextWidget(syncManager, parent).child(statusLine("ote.vnm.gui.header"))
            .child(statusLine("ote.vnm.gui.stage", () -> "T" + tier.getIntValue() + " // " + stage.getValue()))
            .child(
                statusLine(
                    "ote.vnm.gui.modules",
                    () -> String.valueOf(modules.getIntValue()),
                    () -> isPurchased(upgrades, VoidNexusUpgrade.PSIONIC_HARMONY)))
            .child(
                statusLine("ote.vnm.gui.output", () -> GTUtility.formatShortenedLong(power.getLongValue()) + " EU/t"))
            .child(
                statusLine(
                    "ote.vnm.gui.phase",
                    () -> "Δ " + deviation.getValue()
                        + "  §8|  "
                        + StatCollector.translateToLocal("ote.vnm.gui.heat_short")
                        + heat.getValue()
                        + " EU"))
            .child(
                statusLine(
                    "ote.vnm.gui.grid",
                    () -> GTUtility.formatShortenedLong(consumed.getLongValue()) + " / "
                        + GTUtility.formatShortenedLong(parallel.getLongValue())))
            .child(
                statusLine(
                    "ote.vnm.gui.veil",
                    () -> potential.getLongValue() + " / "
                        + OTEVoidNexusMatrix.MAX_VIRTUAL_POTENTIAL
                        + "  §8|  "
                        + StatCollector.translateToLocal("ote.vnm.gui.wand_short")
                        + wandVis.getValue()
                        + " vis"))
            .child(
                statusLine(
                    "ote.vnm.gui.star_array",
                    () -> StatCollector.translateToLocal(
                        starArray.getBoolValue() ? "ote.vnm.gui.star_array.on" : "ote.vnm.gui.star_array.off"),
                    () -> isPurchased(upgrades, VoidNexusUpgrade.END_OF_REINCARNATION)))
            .child(
                statusLine(
                    "ote.vnm.gui.core_crystal",
                    () -> StatCollector.translateToLocal(
                        stargateCore.getBoolValue() ? "ote.vnm.gui.core_crystal.on" : "ote.vnm.gui.core_crystal.off"),
                    () -> isPurchased(upgrades, VoidNexusUpgrade.WHISPER_OF_VOID)))
            .child(
                statusLine(
                    "ote.vnm.gui.stargate",
                    () -> imagePercent(u26.getValue()),
                    () -> isPurchased(upgrades, VoidNexusUpgrade.WHISPER_OF_VOID)))
            .child(statusLine("ote.vnm.gui.separator"))
            .child(
                statusLine(
                    "ote.vnm.gui.link",
                    () -> StatCollector.translateToLocal(
                        wireless.getBoolValue() ? "ote.vnm.gui.wireless_on" : "ote.vnm.gui.wireless_off") + " §8| "
                        + StatCollector.translateToLocal(
                            material.getBoolValue() ? "ote.vnm.gui.material_on" : "ote.vnm.gui.material_off")));
    }

    private static IWidget statusLine(String translationKey, java.util.function.Supplier<String> value) {
        return IKey.dynamic(() -> StatCollector.translateToLocal(translationKey) + value.get())
            .asWidget()
            .fullWidth()
            .marginBottom(1);
    }

    private static IWidget statusLine(String translationKey, java.util.function.Supplier<String> value,
        java.util.function.Supplier<Boolean> visible) {
        return IKey.dynamic(() -> StatCollector.translateToLocal(translationKey) + value.get())
            .asWidget()
            .fullWidth()
            .marginBottom(1)
            .setEnabledIf(widget -> visible.get());
    }

    private static IWidget statusLine(String translationKey) {
        return IKey.lang(translationKey)
            .asWidget()
            .fullWidth()
            .marginBottom(1);
    }

    @Override
    protected Widget<? extends Widget<?>> makeLogoWidget(PanelSyncManager syncManager, ModularPanel parent) {
        return new IDrawable.DrawableWidget(
            new DynamicDrawable(() -> CORE_FRAMES[(int) ((System.currentTimeMillis() / 160L) % CORE_FRAMES.length)]))
                .size(18)
                .tooltipBuilder(
                    tooltip -> tooltip.addLine(
                        IKey.dynamic(
                            () -> StatCollector.translateToLocal(
                                baseMetaTileEntity.isActive() ? "ote.vnm.gui.active" : "ote.vnm.gui.idle"))));
    }

    @Override
    protected ToggleButton createPowerSwitchButton() {
        return voidButton(super.createPowerSwitchButton()).overlay(true, POWER_ON)
            .overlay(false, POWER_OFF);
    }

    @Override
    protected IWidget createStructureUpdateButton(PanelSyncManager syncManager) {
        return voidButton(new ToggleButton().syncHandler("structureUpdateButton")).overlay(REFRESH)
            .tooltipBuilder(t -> t.addLine(IKey.lang("GT5U.gui.button.structure_update")));
    }

    @Override
    protected ToggleButton createMuffleButton() {
        return voidButton(super.createMuffleButton()).overlay(true, SOUND_OFF)
            .overlay(false, SOUND_ON);
    }

    @Override
    protected ButtonWidget<?> createPowerPanelButton(PanelSyncManager sync, ModularPanel panel) {
        return voidButton(super.createPowerPanelButton(sync, panel)).overlay(POWER_PANEL);
    }

    private ButtonWidget<?> createModeToggleButton(BooleanSyncValue value, IDrawable icon, String labelKey,
        VoidNexusUpgrade requiredUpgrade, LongSyncValue upgrades, IntSyncValue tier, StringSyncValue stage) {
        return voidButton(new ButtonWidget<>()).size(16)
            .overlay(icon)
            .setEnabledIf(button -> requiredUpgrade == null || isUpgradeActive(upgrades, tier, stage, requiredUpgrade))
            .onMousePressed(button -> {
                if (button != 0
                    || (requiredUpgrade != null && !isUpgradeActive(upgrades, tier, stage, requiredUpgrade)))
                    return false;
                value.setBoolValue(!value.getBoolValue());
                return true;
            })
            .tooltipAutoUpdate(true)
            .tooltipBuilder(
                tooltip -> tooltip.addLine(IKey.lang(labelKey))
                    .addLine(
                        IKey.dynamic(
                            () -> StatCollector.translateToLocal(
                                requiredUpgrade == VoidNexusUpgrade.EARTH_SHAPER
                                    ? (value.getBoolValue() ? "ote.vnm.gui.mode.items" : "ote.vnm.gui.mode.essentia")
                                    : (value.getBoolValue() ? "ote.vnm.gui.mode.on" : "ote.vnm.gui.mode.off"))))
                    .addLine(
                        IKey.lang(
                            requiredUpgrade == VoidNexusUpgrade.ENTER_THE_VOID ? "ote.vnm.gui.u16_required"
                                : requiredUpgrade == VoidNexusUpgrade.EARTH_SHAPER ? "ote.vnm.gui.u17_required"
                                    : "ote.vnm.gui.mode.free")));
    }

    private static String potentialStatusText(StringSyncValue status) {
        return status.getValue()
            .isEmpty() ? "" : EnumChatFormatting.GRAY + StatCollector.translateToLocal(status.getValue());
    }

    private IWidget createPotentialPanelButton(PanelSyncManager syncManager) {
        IPanelHandler[] panelHolder = new IPanelHandler[1];
        IPanelHandler potentialPanel = syncManager.syncedPanel(
            "voidNexusPotentialPanel",
            true,
            (panelSyncManager, syncHandler) -> createPotentialPanel(syncManager, panelHolder));
        panelHolder[0] = potentialPanel;
        LongSyncValue potential = syncManager.findSyncHandler("voidPotential", LongSyncValue.class);
        return voidButton(new ButtonWidget<>()).size(16)
            .overlay(glyph("§d◆"))
            .onMousePressed(button -> {
                if (potentialPanel.isPanelOpen()) potentialPanel.closePanel();
                else potentialPanel.openPanel();
                return true;
            })
            .tooltipBuilder(
                tooltip -> tooltip.addLine(IKey.lang("ote.vnm.gui.potential.title"))
                    .addLine(
                        IKey.dynamic(
                            () -> StatCollector.translateToLocal("ote.vnm.gui.potential.value")
                                + potential.getLongValue()
                                + " / "
                                + OTEVoidNexusMatrix.MAX_VIRTUAL_POTENTIAL))
                    .addLine(IKey.lang("ote.vnm.gui.potential.open_hint")));
    }

    private ModularPanel createPotentialPanel(PanelSyncManager syncManager, IPanelHandler[] panelHolder) {
        ModularPanel panel = new ModularPanel("voidNexusPotentialPanel").size(178, 142)
            .background(potentialPanelBackground())
            .disableHoverBackground();
        panel.child(box(5, 5, 168, 24, panelHeaderBackground()));
        panel.child(box(5, 5, 168, 1, solid(0xFFBA91FF)));
        panel.child(box(5, 28, 168, 1, solid(0x6639D7FF)));
        panel.child(
            IKey.lang("ote.vnm.gui.potential.title")
                .asWidget()
                .left(11)
                .top(11));
        panel.child(
            voidButton(new ButtonWidget<>()).right(8)
                .top(9)
                .size(14)
                .overlay(glyph("§f×"))
                .onMousePressed(button -> {
                    if (panelHolder[0] != null) panelHolder[0].closePanel();
                    return true;
                }));

        LongSyncValue potential = syncManager.findSyncHandler("voidPotential", LongSyncValue.class);
        LongSyncValue potentialTransfer = syncManager.findSyncHandler("voidPotentialTransfer", LongSyncValue.class);
        StringSyncValue potentialStatus = syncManager.findSyncHandler("voidPotentialStatus", StringSyncValue.class);
        BooleanSyncValue autoPotential = syncManager.findSyncHandler("voidAutoPotential", BooleanSyncValue.class);
        LongSyncValue upgrades = syncManager.findSyncHandler("voidUpgradeMask", LongSyncValue.class);
        IntSyncValue tier = syncManager.findSyncHandler("voidStructureTier", IntSyncValue.class);
        StringSyncValue stage = syncManager.findSyncHandler("voidStage", StringSyncValue.class);

        panel.child(
            IKey.dynamic(
                () -> StatCollector.translateToLocal("ote.vnm.gui.potential.value") + potential.getLongValue()
                    + " / "
                    + OTEVoidNexusMatrix.MAX_VIRTUAL_POTENTIAL)
                .asWidget()
                .left(11)
                .top(35));
        panel.child(
            createPotentialActionButton(
                "ote.vnm.gui.potential.deposit_short",
                true,
                1L,
                potentialTransfer,
                upgrades,
                tier,
                stage).left(9)
                    .top(50));
        panel.child(
            createPotentialActionButton(
                "ote.vnm.gui.potential.withdraw_short",
                false,
                2L,
                potentialTransfer,
                upgrades,
                tier,
                stage).right(9)
                    .top(50));
        panel.child(
            createAutomaticOutputButton(autoPotential, upgrades, tier, stage).left(9)
                .top(81));
        panel.child(
            IKey.dynamic(
                () -> isUpgradeActive(upgrades, tier, stage, VoidNexusUpgrade.FIND_ONES_OWN_PATH)
                    ? potentialStatusText(potentialStatus)
                    : StatCollector.translateToLocal("ote.vnm.gui.potential.u28_required"))
                .asWidget()
                .left(11)
                .right(11)
                .bottom(8)
                .scale(0.85F));
        return panel;
    }

    private ButtonWidget<?> createAutomaticOutputButton(BooleanSyncValue autoPotential, LongSyncValue upgrades,
        IntSyncValue tier, StringSyncValue stage) {
        return new ButtonWidget<>().size(160, 23)
            .disableThemeBackground(true)
            .disableHoverThemeBackground(true)
            .background(
                new DynamicDrawable(
                    () -> automaticOutputButtonBackground(
                        autoPotential.getBoolValue(),
                        false,
                        isUpgradeActive(upgrades, tier, stage, VoidNexusUpgrade.FIND_ONES_OWN_PATH))))
            .hoverBackground(
                new DynamicDrawable(
                    () -> automaticOutputButtonBackground(
                        autoPotential.getBoolValue(),
                        true,
                        isUpgradeActive(upgrades, tier, stage, VoidNexusUpgrade.FIND_ONES_OWN_PATH))))
            .overlay(
                IKey.dynamic(
                    () -> (isUpgradeActive(upgrades, tier, stage, VoidNexusUpgrade.FIND_ONES_OWN_PATH) ? "" : "§8")
                        + StatCollector.translateToLocal(
                            autoPotential.getBoolValue() ? "ote.vnm.gui.potential.auto_on"
                                : "ote.vnm.gui.potential.auto_off"))
                    .scale(0.82F)
                    .alignment(Alignment.Center))
            .onMousePressed(button -> {
                if (button != 0 || !isUpgradeActive(upgrades, tier, stage, VoidNexusUpgrade.FIND_ONES_OWN_PATH))
                    return false;
                autoPotential.setBoolValue(!autoPotential.getBoolValue());
                return true;
            })
            .tooltipAutoUpdate(true)
            .tooltipBuilder(
                tooltip -> tooltip.addLine(IKey.lang("ote.vnm.gui.potential.auto_tooltip"))
                    .addLine(IKey.lang("ote.vnm.gui.potential.u28_required")));
    }

    private ButtonWidget<?> createPotentialActionButton(String labelKey, boolean deposit, long operation,
        LongSyncValue transfer, LongSyncValue upgrades, IntSyncValue tier, StringSyncValue stage) {
        return new ButtonWidget<>().size(75, 25)
            .disableThemeBackground(true)
            .disableHoverThemeBackground(true)
            .background(
                new DynamicDrawable(
                    () -> potentialButtonBackground(
                        deposit,
                        false,
                        isUpgradeActive(upgrades, tier, stage, VoidNexusUpgrade.FIND_ONES_OWN_PATH))))
            .hoverBackground(
                new DynamicDrawable(
                    () -> potentialButtonBackground(
                        deposit,
                        true,
                        isUpgradeActive(upgrades, tier, stage, VoidNexusUpgrade.FIND_ONES_OWN_PATH))))
            .overlay(
                IKey.dynamic(
                    () -> (isUpgradeActive(upgrades, tier, stage, VoidNexusUpgrade.FIND_ONES_OWN_PATH) ? "" : "§8")
                        + StatCollector.translateToLocal(labelKey))
                    .scale(0.82F)
                    .alignment(Alignment.Center))
            .onMousePressed(button -> {
                if (button != 0 || !isUpgradeActive(upgrades, tier, stage, VoidNexusUpgrade.FIND_ONES_OWN_PATH))
                    return false;
                transfer.setLongValue(operation, true, true);
                return true;
            })
            .tooltipAutoUpdate(true)
            .tooltipBuilder(
                tooltip -> tooltip
                    .addLine(IKey.lang(deposit ? "ote.vnm.gui.potential.deposit" : "ote.vnm.gui.potential.withdraw"))
                    .addLine(IKey.lang("ote.vnm.gui.potential.u28_required")));
    }

    private static IDrawable potentialPanelBackground() {
        return new Rectangle().verticalGradient(0xFF160D2A, 0xFF07050F)
            .cornerRadius(5);
    }

    private static IDrawable panelHeaderBackground() {
        return new Rectangle().horizontalGradient(0xCC331B58, 0xAA101F3B)
            .cornerRadius(3);
    }

    private static <T extends Widget<?>> T voidButton(T button) {
        button.disableThemeBackground(true)
            .disableHoverThemeBackground(true)
            .background(
                new Rectangle().verticalGradient(0xFF493264, 0xFF191124)
                    .cornerRadius(3))
            .hoverBackground(
                new Rectangle().verticalGradient(0xFF7651A3, 0xFF301D44)
                    .cornerRadius(3));
        return button;
    }

    private static IDrawable glyph(String text) {
        return IKey.str(text)
            .alignment(Alignment.Center);
    }

    private static IDrawable potentialButtonBackground(boolean deposit, boolean hover, boolean active) {
        if (!active) return new Rectangle().verticalGradient(hover ? 0xFF241F2A : 0xFF17151D, 0xFF09080C)
            .cornerRadius(4);
        if (deposit) {
            return new Rectangle().verticalGradient(hover ? 0xFF1C8A63 : 0xFF116046, 0xFF073326)
                .cornerRadius(4);
        }
        return new Rectangle().verticalGradient(hover ? 0xFF9B55D6 : 0xFF6B2D9B, 0xFF2D123F)
            .cornerRadius(4);
    }

    private static IDrawable automaticOutputButtonBackground(boolean enabled, boolean hover, boolean active) {
        if (!active) return new Rectangle().verticalGradient(hover ? 0xFF241F2A : 0xFF17151D, 0xFF09080C)
            .cornerRadius(4);
        if (enabled) {
            return new Rectangle().verticalGradient(hover ? 0xFF247FA8 : 0xFF175C7D, 0xFF0A2637)
                .cornerRadius(4);
        }
        return new Rectangle().verticalGradient(hover ? 0xFF3D3151 : 0xFF291F38, 0xFF100B17)
            .cornerRadius(4);
    }

    private IWidget createUpgradeMapButton(PanelSyncManager syncManager) {
        IPanelHandler[] panelHolder = new IPanelHandler[1];
        IPanelHandler upgradePanel = syncManager.syncedPanel(
            "voidNexusUpgradeMap",
            true,
            (panelSyncManager, syncHandler) -> createUpgradePanel(syncManager, panelHolder));
        panelHolder[0] = upgradePanel;
        return voidButton(new ButtonWidget<>()).size(16)
            .overlay(UPGRADE_ICON)
            .onMousePressed(button -> {
                if (upgradePanel.isPanelOpen()) upgradePanel.closePanel();
                else upgradePanel.openPanel();
                return true;
            })
            .tooltipBuilder(
                tooltip -> tooltip.addLine(IKey.lang("ote.vnm.gui.upgrade_map"))
                    .addLine(IKey.lang("ote.vnm.gui.upgrade_hint")));
    }

    private ModularPanel createUpgradePanel(PanelSyncManager syncManager, IPanelHandler[] panelHolder) {
        ModularPanel panel = new ModularPanel("voidNexusUpgradeMap").size(PANEL_WIDTH, PANEL_HEIGHT)
            .background(UPGRADE_BACKGROUND)
            .disableHoverBackground();

        panel.child(box(6, 4, PANEL_WIDTH - 12, 30, solid(0x99140B2E)));
        panel.child(box(6, 4, PANEL_WIDTH - 12, 1, solid(0xFF7D5CFF)));
        panel.child(box(6, 33, PANEL_WIDTH - 12, 1, solid(0x5520D8FF)));
        panel.child(
            IKey.lang("ote.vnm.gui.grid_title")
                .asWidget()
                .left(10)
                .top(7));
        panel.child(
            voidButton(new ButtonWidget<>()).right(7)
                .top(7)
                .size(12)
                .overlay(glyph("§f×"))
                .onMousePressed(button -> {
                    if (panelHolder[0] != null) panelHolder[0].closePanel();
                    return true;
                }));

        LongSyncValue upgrades = syncManager.findSyncHandler("voidUpgradeMask", LongSyncValue.class);
        LongSyncValue parallel = syncManager.findSyncHandler("voidParallel", LongSyncValue.class);
        LongSyncValue potential = syncManager.findSyncHandler("voidPotential", LongSyncValue.class);
        IntSyncValue tier = syncManager.findSyncHandler("voidStructureTier", IntSyncValue.class);
        StringSyncValue stage = syncManager.findSyncHandler("voidStage", StringSyncValue.class);
        StringSyncValue status = syncManager.findSyncHandler("voidPurchaseStatus", StringSyncValue.class);
        LongSyncValue purchase = syncManager.findSyncHandler("voidPurchase", LongSyncValue.class);

        panel.child(
            IKey.dynamic(
                () -> StatCollector.translateToLocalFormatted(
                    "ote.vnm.gui.grid_summary",
                    activeNodeCount(upgrades.getLongValue()),
                    GTUtility.formatShortenedLong(parallel.getLongValue()),
                    GTUtility.formatShortenedLong(potential.getLongValue())))
                .asWidget()
                .left(10)
                .top(19));
        panel.child(
            IKey.dynamic(
                () -> status.getValue()
                    .isEmpty() ? ""
                        : EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal(status.getValue()))
                .asWidget()
                .left(10)
                .bottom(8));

        VerticalScrollData scrollData = new VerticalScrollData();
        scrollData.setScrollSize(SCROLL_SIZE);
        ScrollWidget<?> tree = new ScrollWidget<>(scrollData).pos(10, 40)
            .size(TREE_WIDTH, TREE_HEIGHT);

        addLinearConnectors(tree);
        for (VoidNexusUpgrade upgrade : VoidNexusUpgrade.values()) {
            tree.child(createUpgradeNode(upgrade, upgrades, tier, stage, status, purchase));
        }
        panel.child(tree);
        return panel;
    }

    private static void addLinearConnectors(ScrollWidget<?> tree) {
        int mainCenter = MAIN_X + NODE_SIZE / 2;
        int mainTop = NODE_Y[0] + NODE_SIZE / 2;
        int mainBottom = NODE_Y[27] + NODE_SIZE / 2;
        tree.child(box(mainCenter - 2, mainTop, 4, mainBottom - mainTop, solid(0xD04AD7FF)));
        for (VoidNexusUpgrade upgrade : VoidNexusUpgrade.values()) {
            if (isMainline(upgrade)) continue;
            addBranchConnector(tree, upgrade, mainCenter);
        }
    }

    private static void addBranchConnector(ScrollWidget<?> tree, VoidNexusUpgrade branch, int mainCenter) {
        int branchIndex = branch.id - 1;
        int parentId = branchParentId(branch);
        int parentIndex = parentId - 1;
        int branchX = NODE_X[branchIndex];
        int branchCenterY = NODE_Y[branchIndex] + NODE_SIZE / 2;
        int parentCenterY = NODE_Y[parentIndex] + NODE_SIZE / 2;
        boolean left = branchX < MAIN_X;
        int lane = left ? mainCenter - NODE_SIZE / 2 - 8 : mainCenter + NODE_SIZE / 2 + 8;
        int branchEdge = left ? branchX + NODE_SIZE : branchX;

        int horizontalStart = Math.min(branchEdge, lane);
        int horizontalEnd = Math.max(branchEdge, lane);
        tree.child(box(horizontalStart, branchCenterY - 1, horizontalEnd - horizontalStart, 3, solid(0xE09A74FF)));

        int verticalTop = Math.min(branchCenterY, parentCenterY);
        int verticalLength = Math.abs(branchCenterY - parentCenterY);
        tree.child(box(lane - 1, verticalTop, 3, Math.max(2, verticalLength), solid(0xE09A74FF)));

        int parentEdge = left ? MAIN_X : MAIN_X + NODE_SIZE;
        int parentStart = Math.min(lane, parentEdge);
        int parentEnd = Math.max(lane, parentEdge);
        tree.child(box(parentStart, parentCenterY - 1, parentEnd - parentStart, 3, solid(0xE09A74FF)));
        tree.child(box(lane - 2, parentCenterY - 2, 5, 5, solid(0xFFD7C6FF)));
    }

    private static int branchParentId(VoidNexusUpgrade upgrade) {
        int id = upgrade.id;
        if (id >= 2 && id <= 4) return 1;
        if (id >= 7 && id <= 10) return 6;
        if (id >= 12 && id <= 15) return 11;
        if (id >= 17 && id <= 24) return 16;
        if (id == 26 || id == 27) return 25;
        return 1;
    }

    private IWidget createUpgradeNode(VoidNexusUpgrade upgrade, LongSyncValue upgrades, IntSyncValue tier,
        StringSyncValue stage, StringSyncValue status, LongSyncValue purchase) {
        int index = upgrade.id - 1;
        boolean mainline = isMainline(upgrade);
        return voidButton(new ButtonWidget<>()).left(NODE_X[index])
            .top(NODE_Y[index])
            .size(NODE_SIZE)
            .overlay(
                new DynamicDrawable(
                    () -> isUpgradeActive(upgrades, tier, stage, upgrade) ? (mainline ? NODE_MAIN_ON : NODE_BRANCH_ON)
                        : NODE_OFF),
                IKey.str("§f" + upgrade.id)
                    .scale(0.68F)
                    .alignment(Alignment.Center))
            .onMousePressed(button -> {
                if (button != 0 || isPurchased(upgrades, upgrade) || !arePrerequisitesPurchased(upgrades, upgrade))
                    return false;
                purchase.setLongValue(upgrade.id, true, true);
                return true;
            })
            .tooltipAutoUpdate(true)
            .tooltipBuilder(tooltip -> addUpgradeTooltip(tooltip, upgrade, upgrades, tier, stage, status));
    }

    private static void addUpgradeTooltip(RichTooltip tooltip, VoidNexusUpgrade upgrade, LongSyncValue upgrades,
        IntSyncValue tier, StringSyncValue stage, StringSyncValue status) {
        boolean purchased = isPurchased(upgrades, upgrade);
        boolean stageMet = isStageMet(upgrade, stage.getValue());
        boolean tierMet = tier.getIntValue() >= upgrade.minimumStructureTier;
        boolean prerequisitesMet = arePrerequisitesPurchased(upgrades, upgrade);
        boolean active = purchased && stageMet && tierMet && prerequisitesMet;
        tooltip.addLine(IKey.str("§d✦ U" + upgrade.id + "  §f" + researchName(upgrade)))
            .addLine(
                IKey.str(
                    "§8" + StatCollector.translateToLocal("ote.vnm.gui.required_stage")
                        + ": "
                        + (stageMet ? "§a" : "§c")
                        + upgrade.minimumStage
                        + " §8/ "
                        + StatCollector.translateToLocal("ote.vnm.gui.current_stage")
                        + " "
                        + stage.getValue()))
            .addLine(
                IKey.str(
                    (tierMet ? "§a" : "§c") + StatCollector.translateToLocalFormatted(
                        "ote.vnm.gui.required_structure_tier",
                        upgrade.minimumStructureTier,
                        tier.getIntValue())));
        if (upgrade.getPrerequisiteIds().length > 0) {
            tooltip.addLine(
                IKey.str(
                    (prerequisitesMet ? "§a" : "§c") + StatCollector
                        .translateToLocalFormatted("ote.vnm.gui.prerequisites", prerequisiteLabel(upgrade))));
        }
        tooltip
            .addLine(
                IKey.str(
                    purchased
                        ? "§b" + StatCollector
                            .translateToLocal(active ? "ote.vnm.gui.purchased" : "ote.vnm.gui.purchased_inactive")
                        : ""))
            .addLine(IKey.str(""))
            .addLine(IKey.str("§d◆ " + StatCollector.translateToLocal("ote.vnm.gui.lore")))
            .addLine(IKey.str(""));
        addLocalizedResearchText(tooltip, upgrade.getDescriptionKey(), "§7");
        tooltip.addLine(IKey.str(""))
            .addLine(IKey.str("§b◆ " + StatCollector.translateToLocal("ote.vnm.gui.effect")))
            .addLine(IKey.str(""));
        addLocalizedResearchText(tooltip, upgrade.getEffectKey(), "§b");
        tooltip.addLine(IKey.str(""));
        if (active) {
            tooltip.addLine(IKey.str("§b" + StatCollector.translateToLocal("ote.vnm.gui.synchronized")));
        } else if (purchased) {
            tooltip.addLine(IKey.str("§e" + StatCollector.translateToLocal("ote.vnm.gui.purchase.requirements")));
        } else if (!stageMet || !tierMet || !prerequisitesMet) {
            tooltip.addLine(IKey.str("§c" + StatCollector.translateToLocal("ote.vnm.gui.purchase.requirements")));
        } else {
            tooltip.addLine(IKey.str("§e" + StatCollector.translateToLocal("ote.vnm.gui.purchase.click")));
        }
        if (!status.getValue()
            .isEmpty()) {
            tooltip.addLine(IKey.str("§8" + StatCollector.translateToLocal(status.getValue())));
        }
    }

    private static void addLocalizedResearchText(RichTooltip tooltip, String key, String color) {
        String text = StatCollector.translateToLocal(key);
        for (String line : text.split("<BR>")) {
            if (!line.isEmpty()) tooltip.addLine(IKey.str(color + line));
        }
    }

    private static String researchName(VoidNexusUpgrade upgrade) {
        return StatCollector.translateToLocal("tc.research_name." + upgrade.getResearchKey());
    }

    private static boolean isStageMet(VoidNexusUpgrade upgrade, String stageName) {
        try {
            return VoidNexusCalculator.Stage.valueOf(stageName).index >= upgrade.minimumStage.index;
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    private static boolean isMainline(VoidNexusUpgrade upgrade) {
        return upgrade == VoidNexusUpgrade.PSIONIC_THEORY || upgrade == VoidNexusUpgrade.BEYOND_DIMENSIONS
            || upgrade == VoidNexusUpgrade.VOID_LINK_THEORY
            || upgrade == VoidNexusUpgrade.PSIONIC_HARMONY
            || upgrade == VoidNexusUpgrade.ENTER_THE_VOID
            || upgrade == VoidNexusUpgrade.REFORGE_THE_VOID
            || upgrade == VoidNexusUpgrade.FIND_ONES_OWN_PATH;
    }

    private static boolean isPurchased(LongSyncValue upgrades, VoidNexusUpgrade upgrade) {
        return (upgrades.getLongValue() & (1L << (upgrade.id - 1))) != 0L;
    }

    private static boolean isUpgradeActive(LongSyncValue upgrades, IntSyncValue tier, StringSyncValue stage,
        VoidNexusUpgrade upgrade) {
        return isPurchased(upgrades, upgrade) && isStageMet(upgrade, stage.getValue())
            && tier.getIntValue() >= upgrade.minimumStructureTier
            && arePrerequisitesPurchased(upgrades, upgrade);
    }

    private static boolean arePrerequisitesPurchased(LongSyncValue upgrades, VoidNexusUpgrade upgrade) {
        for (int prerequisiteId : upgrade.getPrerequisiteIds()) {
            if ((upgrades.getLongValue() & (1L << (prerequisiteId - 1))) == 0L) return false;
        }
        return true;
    }

    private static String prerequisiteLabel(VoidNexusUpgrade upgrade) {
        StringBuilder result = new StringBuilder();
        for (int prerequisiteId : upgrade.getPrerequisiteIds()) {
            if (result.length() > 0) result.append(", ");
            result.append('U')
                .append(prerequisiteId);
        }
        return result.toString();
    }

    private static int activeNodeCount(long mask) {
        return Long.bitCount(mask);
    }

    private static String imagePercent(int percent) {
        return percent + "%";
    }

    private static UITexture transparentImage(String path) {
        return UITexture.builder()
            .location(new ResourceLocation("123technology", path))
            .fullImage()
            .nonOpaque()
            .build();
    }

    private static Widget<?> box(int x, int y, int width, int height, IDrawable background) {
        return new ButtonWidget<>().left(x)
            .top(y)
            .size(width, height)
            .background(background)
            .disableHoverBackground();
    }

    private static Rectangle solid(int argb) {
        return new Rectangle().color(argb);
    }
}
