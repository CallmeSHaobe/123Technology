package com.newmaa.othtech.machine.gui;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.regex.Pattern;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.newmaa.othtech.machine.OTEDroneLaunchPlatform;

import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;

public class OTEDroneLaunchPlatformGui extends MTEMultiBlockBaseGui<OTEDroneLaunchPlatform> {

    private static final UITexture INFO_BACKGROUND = UITexture.fullImage("123technology", "gui/blackhand");
    private static final UITexture LOGO = UITexture.fullImage("123technology", "items/MetaItem/0");
    private static final IDrawable[] BRAND_GLYPHS = { UITexture.fullImage("123technology", "gui/drone/glyph_1"),
        UITexture.fullImage("123technology", "gui/drone/glyph_2"),
        UITexture.fullImage("123technology", "gui/drone/glyph_3"), IDrawable.EMPTY,
        UITexture.fullImage("123technology", "gui/drone/glyph_t"),
        UITexture.fullImage("123technology", "gui/drone/glyph_e"),
        UITexture.fullImage("123technology", "gui/drone/glyph_c") };

    private static final Pattern COORDINATE = Pattern.compile("-?[0-9]{0,8}");

    public OTEDroneLaunchPlatformGui(OTEDroneLaunchPlatform multiblock) {
        super(multiblock);
    }

    @Override
    protected Widget<? extends Widget<?>> makeLogoWidget(PanelSyncManager sync, ModularPanel parent) {
        return new IDrawable.DrawableWidget(LOGO).size(18)
            .marginTop(4);
    }

    @Override
    protected ParentWidget<?> createTerminalParentWidget(ModularPanel panel, PanelSyncManager sync) {
        ParentWidget<?> terminal = super.createTerminalParentWidget(panel, sync).background(INFO_BACKGROUND);
        Flow brand = Flow.column()
            .size(10, 70)
            .left(4)
            .bottom(6);
        for (IDrawable glyph : BRAND_GLYPHS) {
            brand.child(new IDrawable.DrawableWidget(glyph).size(10));
        }
        terminal.child(brand);
        return terminal;
    }

    @Override
    protected ListWidget<IWidget, ?> createTerminalTextWidget(PanelSyncManager sync, ModularPanel panel) {
        return super.createTerminalTextWidget(sync, panel).paddingLeft(14)
            .paddingRight(24);
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        registerCoordinate(syncManager, "droneX", multiblock::getTargetX, multiblock::setTargetX);
        registerCoordinate(syncManager, "droneY", multiblock::getTargetY, multiblock::setTargetY);
        registerCoordinate(syncManager, "droneZ", multiblock::getTargetZ, multiblock::setTargetZ);
        registerCoordinate(syncManager, "droneSpeed", multiblock::getLaunchSpeedKmh, multiblock::setLaunchSpeedKmh);
        syncManager.syncValue(
            "droneAutoDownlink",
            new BooleanSyncValue(multiblock::isAutoDownlinkTarget, multiblock::setAutoDownlinkTarget).allowC2S());
        syncManager.syncValue(
            "droneLaunch",
            new BooleanSyncValue(multiblock::isLaunchEnabled, multiblock::setLaunchEnabled).allowC2S());
    }

    private static void registerCoordinate(PanelSyncManager manager, String name, IntSupplier getter,
        IntConsumer setter) {
        manager.syncValue(name, new StringSyncValue(() -> Integer.toString(getter.getAsInt()), value -> {
            try {
                setter.accept(Integer.parseInt(value));
            } catch (NumberFormatException ignored) {}
        }).allowC2S());
    }

    @Override
    protected int getTextBoxToInventoryGap() {
        return 102;
    }

    @Override
    protected Flow createPanelGap(ModularPanel parent, PanelSyncManager syncManager) {
        BooleanSyncValue launch = syncManager.findSyncHandler("droneLaunch", BooleanSyncValue.class);
        BooleanSyncValue auto = syncManager.findSyncHandler("droneAutoDownlink", BooleanSyncValue.class);
        return Flow.row()
            .fullWidth()
            .height(getTextBoxToInventoryGap())
            .paddingLeft(8)
            .child(
                Flow.column()
                    .width(152)
                    .height(100)
                    .child(
                        new ButtonWidget<>().size(152, 20)
                            .overlay(
                                IKey.dynamic(
                                    () -> net.minecraft.util.StatCollector.translateToLocal(
                                        auto.getBoolValue() ? "ote.drone.gui.mode.downlink"
                                            : "ote.drone.gui.mode.coordinates")))
                            .onMousePressed(button -> {
                                auto.setBoolValue(!auto.getBoolValue());
                                return true;
                            })
                            .tooltip(t -> t.addLine(IKey.lang("ote.drone.gui.mode.help"))))
                    .child(
                        new TextWidget<>(IKey.lang("ote.drone.gui.target")).width(152)
                            .height(12))
                    .child(
                        Flow.row()
                            .width(152)
                            .height(24)
                            .child(coordinateRow("X", "droneX", syncManager))
                            .child(coordinateRow("Y", "droneY", syncManager))
                            .child(coordinateRow("Z", "droneZ", syncManager)))
                    .child(
                        Flow.row()
                            .width(152)
                            .height(22)
                            .child(
                                new TextWidget<>(IKey.lang("ote.drone.gui.speed")).width(100)
                                    .height(18))
                            .child(
                                new TextFieldWidget()
                                    .value(syncManager.findSyncHandler("droneSpeed", StringSyncValue.class))
                                    .numbersInt(50, 300)
                                    .setMaxLength(3)
                                    .width(50)
                                    .height(18)
                                    .tooltip(t -> t.addLine(IKey.lang("ote.drone.gui.speed.help")))))
                    .child(
                        new ButtonWidget<>().size(152, 18)
                            .overlay(
                                IKey.dynamic(
                                    () -> net.minecraft.util.StatCollector.translateToLocal(
                                        launch.getBoolValue() ? "ote.drone.gui.enabled" : "ote.drone.gui.disabled")))
                            .onMousePressed(button -> {
                                launch.setBoolValue(!launch.getBoolValue());
                                return true;
                            })
                            .tooltip(t -> t.addLine(IKey.lang("ote.drone.gui.help")))));
    }

    private IWidget coordinateRow(String axis, String name, PanelSyncManager manager) {
        return Flow.row()
            .width(50)
            .height(22)
            .child(
                new TextWidget<>(IKey.str(axis)).width(9)
                    .height(18))
            .child(
                new TextFieldWidget().value(manager.findSyncHandler(name, StringSyncValue.class))
                    .setPattern(COORDINATE)
                    .setMaxLength(9)
                    .width(40)
                    .height(18));
    }
}
