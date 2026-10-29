package com.newmaa.othtech.machine.gui;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.CycleButtonWidget;
import com.newmaa.othtech.machine.OTESunFactory;

import gregtech.api.util.GTUtility;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;

/**
 * MUI2 (ModularUI2) GUI for the {@link OTESunFactory}, same pattern as GT5U's own {@code MTEIndustrialPackagerGui}
 * hook and this mod's {@code OTEBBPlasmaForgeGui}.
 *
 * <p>
 * The only difference to the stock multiblock GUI is the mode switch button: it renders its overlay and its tooltip
 * from the synced {@code machineMode} value instead of the machine field. The client does not know the sfg channel,
 * so relying on the field made the icon cycle while the tooltip kept showing the previously synced mode name.
 */
public class OTESunFactoryGui extends MTEMultiBlockBaseGui<OTESunFactory> {

    /** Mode icons, index = machine mode = recipe map (0 PCB, 1 SMD, 2 wafer, 3 components). */
    public static final UITexture[] MODE_ICONS = {
        UITexture.fullImage("123technology", "gui/overlay_button/sunfactory_mode_pcb"),
        UITexture.fullImage("123technology", "gui/overlay_button/sunfactory_mode_smd"),
        UITexture.fullImage("123technology", "gui/overlay_button/sunfactory_mode_wafer"),
        UITexture.fullImage("123technology", "gui/overlay_button/sunfactory_mode_component") };

    public OTESunFactoryGui(OTESunFactory multiblock) {
        super(multiblock);
        withMachineModeIcons(MODE_ICONS);
    }

    @Override
    protected IWidget createModeSwitchButton(PanelSyncManager syncManager) {
        IntSyncValue modeSyncer = syncManager.findSyncHandler("machineMode", IntSyncValue.class);
        return new CycleButtonWidget().value(modeSyncer)
            .length(MODE_ICONS.length)
            .overlay(new DynamicDrawable(() -> getMachineModeIcon(modeSyncer.getIntValue())))
            .tooltipBuilder(
                t -> t.addLine(IKey.lang("GT5U.gui.button.mode_switch"))
                    .addLine(
                        IKey.dynamic(
                            () -> GTUtility
                                .getColoredSecondaryTooltip(multiblock.getMachineModeName(modeSyncer.getIntValue())))));
    }
}
