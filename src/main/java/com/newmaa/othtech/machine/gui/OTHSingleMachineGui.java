package com.newmaa.othtech.machine.gui;

import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.Widget;
import com.newmaa.othtech.machine.machineclass.OTHSingleMachineBase;

import gregtech.api.recipe.BasicUIProperties;
import gregtech.common.gui.modularui.singleblock.base.MTEBasicMachineBaseGui;

public final class OTHSingleMachineGui extends MTEBasicMachineBaseGui<OTHSingleMachineBase> {

    public OTHSingleMachineGui(OTHSingleMachineBase machine, BasicUIProperties properties) {
        super(machine, properties);
    }

    @Override
    protected int getBasePanelWidth() {
        // The recipe area is centered; reserve a gutter rather than drawing over its leftmost slot.
        return super.getBasePanelWidth() + 24;
    }

    @Override
    protected ParentWidget<?> createContentSection(ModularPanel panel, PanelSyncManager sync) {
        return super.createContentSection(panel, sync).child(
            OTHGuiBranding.glyphColumn(7)
                .left(1)
                .verticalCenter());
    }

    @Override
    protected Widget<?> makeLogoWidget() {
        return OTHGuiBranding.logo();
    }
}
