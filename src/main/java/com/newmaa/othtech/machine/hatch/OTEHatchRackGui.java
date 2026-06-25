package com.newmaa.othtech.machine.hatch;

import static net.minecraft.util.StatCollector.translateToLocal;
import static net.minecraft.util.StatCollector.translateToLocalFormatted;

import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.common.gui.modularui.hatch.base.MTEHatchBaseGui;
import gregtech.common.modularui2.widget.builder.ItemSlotGridBuilder;

/**
 * MUI2 GUI for {@link OTEHatchRack}. Modelled after {@code MTEHatchRackGui} from upstream.
 */
public class OTEHatchRackGui extends MTEHatchBaseGui<OTEHatchRack> {

    public OTEHatchRackGui(OTEHatchRack machine) {
        super(machine);
    }

    @Override
    protected ParentWidget<?> createContentSection(ModularPanel panel, PanelSyncManager syncManager) {
        BooleanSyncValue isActiveSyncer = syncManager.findSyncHandler("isActive", BooleanSyncValue.class);

        ParentWidget<?> parent = super.createContentSection(panel, syncManager);

        // Central decoration: heat sink picture
        parent.child(
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

        parent.child(statusColumn);

        // Input slots (2x2 grid) — blocked when machine is active
        parent.child(
            new ItemSlotGridBuilder(machine.inventoryHandler, syncManager).size(2)
                .itemSlotSupplier(() -> new ItemSlot().backgroundOverlay(GTGuiTextures.TT_OVERLAY_SLOT_RACK))
                .filter(stack -> !isActiveSyncer.getBoolValue())
                .build()
                .center()
                .minElementMargin(2));

        return parent;
    }

    @Override
    public void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        syncManager.syncValue("isActive", new BooleanSyncValue(baseMetaTileEntity::isActive));
    }

    @Override
    protected boolean supportsBottomRowOverlap() {
        return true;
    }
}
