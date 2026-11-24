package com.newmaa.othtech.machine.gui;

import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.ListWidget;

import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;

public class OTHMultiMachineGui<T extends MTEMultiBlockBase> extends MTEMultiBlockBaseGui<T> {

    public OTHMultiMachineGui(T multiblock) {
        super(multiblock);
    }

    @Override
    protected Widget<? extends Widget<?>> makeLogoWidget(PanelSyncManager sync, ModularPanel panel) {
        return OTHGuiBranding.logo()
            .marginTop(4);
    }

    @Override
    protected ParentWidget<?> createTerminalParentWidget(ModularPanel panel, PanelSyncManager sync) {
        return super.createTerminalParentWidget(panel, sync).child(
            OTHGuiBranding.glyphColumn(10)
                .left(4)
                .bottom(6));
    }

    @Override
    protected ListWidget<IWidget, ?> createTerminalTextWidget(PanelSyncManager sync, ModularPanel panel) {
        return super.createTerminalTextWidget(sync, panel).paddingLeft(14)
            .paddingRight(24);
    }
}
