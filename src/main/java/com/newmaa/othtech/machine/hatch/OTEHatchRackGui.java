package com.newmaa.othtech.machine.hatch;

import static net.minecraft.util.StatCollector.translateToLocal;
import static net.minecraft.util.StatCollector.translateToLocalFormatted;

import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTGuis;
import gregtech.common.modularui2.widget.builder.ItemSlotGridBuilder;

/**
 * MUI2 GUI for {@link OTEHatchRack}. Modelled after {@code MTEHatchRackGui} from upstream.
 */
public class OTEHatchRackGui {

    private final OTEHatchRack machine;

    public OTEHatchRackGui(OTEHatchRack machine) {
        this.machine = machine;
    }

    public ModularPanel build(PosGuiData guiData, PanelSyncManager syncManager, UISettings uiSettings) {
        BooleanSyncValue isActiveSyncer = new BooleanSyncValue(
            () -> machine.getBaseMetaTileEntity()
                .isActive());
        syncManager.syncValue("isActive", isActiveSyncer);

        ModularPanel panel = GTGuis.mteTemplatePanelBuilder(machine, guiData, syncManager, uiSettings)
            .doesBindPlayerInventory(false)
            .build();

        // Central decoration: heat sink picture
        panel.child(
            GTGuiTextures.TT_PICTURE_HEAT_SINK.asWidget()
                .size(84, 60)
                .center());

        // Status indicators column (top-right)
        Flow statusColumn = Flow.col()
            .coverChildren()
            .childPadding(2)
            .topRel(0)
            .rightRel(0);

        statusColumn.child(
            new DynamicDrawable(
                () -> isActiveSyncer.getBoolValue() ? GTGuiTextures.TT_OVERLAY_BUTTON_POWER_SWITCH_ON
                    : GTGuiTextures.TT_OVERLAY_BUTTON_POWER_SWITCH_DISABLED).asWidget()
                        .background(GTGuiTextures.BUTTON_STANDARD_LIGHT_16x16)
                        .size(16)
                        .tooltipAutoUpdate(true)
                        .tooltipDynamic(
                            t -> t.addLine(
                                translateToLocalFormatted(
                                    "tt.gui.text.hatch.status",
                                    translateToLocal(
                                        isActiveSyncer.getBoolValue() ? "tt.gui.text.hatch.status.active"
                                            : "tt.gui.text.hatch.status.inactive")))));

        panel.child(statusColumn);

        // Input slots (2x2 grid)
        panel.child(
            new ItemSlotGridBuilder(machine.inventoryHandler, syncManager).size(2)
                .itemSlotSupplier(() -> new ItemSlot().backgroundOverlay(GTGuiTextures.TT_OVERLAY_SLOT_RACK))
                .filter(stack -> !isActiveSyncer.getBoolValue())
                .build()
                .center()
                .minElementMargin(2));

        return panel;
    }
}
