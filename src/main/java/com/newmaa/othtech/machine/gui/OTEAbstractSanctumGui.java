package com.newmaa.othtech.machine.gui;

import java.util.Locale;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.regex.Pattern;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.newmaa.othtech.common.materials.AbstractSanctumContent;
import com.newmaa.othtech.machine.OTEAbstractSanctum;
import com.newmaa.othtech.recipe.AbstractSanctumRecipes;

import codechicken.nei.item.ItemFluidDisplay;
import codechicken.nei.recipe.GuiUsageRecipe;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;

public class OTEAbstractSanctumGui extends MTEMultiBlockBaseGui<OTEAbstractSanctum> {

    private static final Pattern MULTIPLIER_PATTERN = Pattern.compile("[0-9]{1,2}(\\.[0-9])?");
    private static final String TEXTURE_ROOT = "gui/abstract_sanctum/";
    private static final UITexture MAIN_BACKGROUND = UITexture
        .fullImage("123technology", TEXTURE_ROOT + "main_background");
    private static final UITexture CONFIG_BACKGROUND = UITexture
        .fullImage("123technology", TEXTURE_ROOT + "config_background");
    private static final UITexture CONFIG_ICON = UITexture.fullImage("123technology", TEXTURE_ROOT + "config_icon");
    private static final UITexture MODE_CRAFT_ICON = UITexture.fullImage("123technology", TEXTURE_ROOT + "mode_craft");
    private static final UITexture MODE_DUPLICATE_ICON = UITexture
        .fullImage("123technology", TEXTURE_ROOT + "mode_duplicate");
    private static final UITexture[] CORE_FRAMES = { UITexture.fullImage("123technology", TEXTURE_ROOT + "core_0"),
        UITexture.fullImage("123technology", TEXTURE_ROOT + "core_1"),
        UITexture.fullImage("123technology", TEXTURE_ROOT + "core_2"),
        UITexture.fullImage("123technology", TEXTURE_ROOT + "core_3") };
    private static final UITexture[] UPGRADE_SLOT_FRAMES = {
        UITexture.fullImage("123technology", TEXTURE_ROOT + "slot_mana"),
        UITexture.fullImage("123technology", TEXTURE_ROOT + "slot_salt"),
        UITexture.fullImage("123technology", TEXTURE_ROOT + "slot_blood") };

    private enum UsageResource {
        MANA,
        SALT,
        BLOOD
    }

    public OTEAbstractSanctumGui(OTEAbstractSanctum multiblock) {
        super(multiblock);
        withMachineModeIcons(MODE_CRAFT_ICON, MODE_DUPLICATE_ICON);
    }

    @Override
    protected ModularPanel getBasePanel(PosGuiData guiData, PanelSyncManager syncManager, UISettings uiSettings) {
        return super.getBasePanel(guiData, syncManager, uiSettings).background(MAIN_BACKGROUND)
            .disableHoverBackground();
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        registerMultiplierSync(
            syncManager,
            "abstractManaMultiplier",
            multiblock::getManaMultiplier,
            multiblock::setManaMultiplier);
        registerMultiplierSync(
            syncManager,
            "abstractSaltMultiplier",
            multiblock::getSaltMultiplier,
            multiblock::setSaltMultiplier);
        registerMultiplierSync(
            syncManager,
            "abstractBloodMultiplier",
            multiblock::getBloodMultiplier,
            multiblock::setBloodMultiplier);
        syncManager.syncValue("abstractCurrentParallel", new IntSyncValue(multiblock::getCurrentParallelCount));
    }

    private static void registerMultiplierSync(PanelSyncManager syncManager, String name, DoubleSupplier getter,
        DoubleConsumer setter) {
        syncManager.syncValue(name, new StringSyncValue(() -> formatMultiplier(getter.getAsDouble()), value -> {
            try {
                setter.accept(Double.parseDouble(value));
            } catch (NumberFormatException ignored) {}
        }).allowC2S());
    }

    @Override
    protected Flow createRightPanelGapRow(ModularPanel parent, PanelSyncManager syncManager) {
        return super.createRightPanelGapRow(parent, syncManager).child(createUpgradeSlotColumn())
            .child(createConfigurationButton(syncManager, parent));
    }

    @Override
    protected int getTextBoxToInventoryGap() {
        return 62;
    }

    @Override
    protected ListWidget<IWidget, ?> createTerminalTextWidget(PanelSyncManager syncManager, ModularPanel parent) {
        IntSyncValue currentParallel = syncManager.findSyncHandler("abstractCurrentParallel", IntSyncValue.class);
        return super.createTerminalTextWidget(syncManager, parent).child(
            IKey.dynamic(
                () -> StatCollector
                    .translateToLocalFormatted("ote.abstract_sanctum.gui.parallel", currentParallel.getValue()))
                .asWidget()
                .fullWidth()
                .marginBottom(2)
                .setEnabledIf(widget -> baseMetaTileEntity.isActive()));
    }

    private Flow createUpgradeSlotColumn() {
        return Flow.column()
            .width(18)
            .height(56)
            .childPadding(1)
            .child(createUpgradeSlot(0, "ote.abstract_sanctum.upgrade.mana"))
            .child(createUpgradeSlot(1, "ote.abstract_sanctum.upgrade.salt"))
            .child(createUpgradeSlot(2, "ote.abstract_sanctum.upgrade.blood"));
    }

    private IWidget createUpgradeSlot(int slot, String tooltipKey) {
        return new ItemSlot().size(18)
            .backgroundOverlay(UPGRADE_SLOT_FRAMES[slot])
            .slot(new ModularSlot(multiblock.getUpgradeInventory(), slot).singletonSlotGroup())
            .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang(tooltipKey)));
    }

    @Override
    protected Widget<? extends Widget<?>> makeLogoWidget(PanelSyncManager syncManager, ModularPanel parent) {
        return new IDrawable.DrawableWidget(
            new DynamicDrawable(() -> CORE_FRAMES[(int) ((System.currentTimeMillis() / 180L) % CORE_FRAMES.length)]))
                .size(18)
                .tooltipBuilder(
                    tooltip -> tooltip.addLine(
                        IKey.dynamic(
                            () -> StatCollector.translateToLocal(
                                baseMetaTileEntity.isActive() ? "ote.abstract_sanctum.gui.core.active"
                                    : "ote.abstract_sanctum.gui.core.idle"))));
    }

    private IWidget createConfigurationButton(PanelSyncManager syncManager, ModularPanel parent) {
        PanelSyncManager parentSyncManager = syncManager;
        IPanelHandler configPanel = syncManager.syncedPanel(
            "abstractSanctumConfig",
            true,
            (panelSyncManager, syncHandler) -> createConfigurationPanel(parent, parentSyncManager));

        return new ButtonWidget<>().size(18, 18)
            .overlay(CONFIG_ICON)
            .onMousePressed(button -> {
                if (configPanel.isPanelOpen()) configPanel.closePanel();
                else configPanel.openPanel();
                return true;
            })
            .tooltipBuilder(t -> t.addLine(IKey.lang("ote.abstract_sanctum.gui.config")));
    }

    private ModularPanel createConfigurationPanel(ModularPanel parent, PanelSyncManager parentSyncManager) {
        return new ModularPanel("abstractSanctumConfig").relative(parent)
            .left(4)
            .top(4)
            .size(150, 128)
            .background(CONFIG_BACKGROUND)
            .disableHoverBackground()
            .child(
                Flow.column()
                    .full()
                    .padding(5)
                    .child(
                        new TextWidget<>(IKey.lang("ote.abstract_sanctum.gui.config")).widthRel(1)
                            .height(12)
                            .marginBottom(2))
                    .child(
                        createInputRow(
                            "ote.abstract_sanctum.gui.mana",
                            "abstractManaMultiplier",
                            parentSyncManager,
                            UsageResource.MANA))
                    .child(
                        createInputRow(
                            "ote.abstract_sanctum.gui.salt",
                            "abstractSaltMultiplier",
                            parentSyncManager,
                            UsageResource.SALT))
                    .child(
                        createInputRow(
                            "ote.abstract_sanctum.gui.blood",
                            "abstractBloodMultiplier",
                            parentSyncManager,
                            UsageResource.BLOOD))
                    .child(
                        new TextWidget<>(IKey.lang("ote.abstract_sanctum.gui.range")).widthRel(1)
                            .height(12)
                            .marginTop(2)));
    }

    private IWidget createInputRow(String labelKey, String syncName, PanelSyncManager syncManager,
        UsageResource usageResource) {
        StringSyncValue value = syncManager.findSyncHandler(syncName, StringSyncValue.class);
        return Flow.row()
            .widthRel(1)
            .height(20)
            .child(
                new ButtonWidget<>().size(68, 16)
                    .marginTop(2)
                    .overlay(IKey.lang(labelKey))
                    .onMousePressed(button -> openUsageRecipe(usageResource))
                    .tooltipBuilder(t -> t.addLine(IKey.lang("ote.abstract_sanctum.gui.open_usage"))))
            .child(
                new TextFieldWidget().value(value)
                    .numbersDouble(1.0D, 10.0D)
                    .setPattern(MULTIPLIER_PATTERN)
                    .setMaxLength(4)
                    .width(52)
                    .height(16));
    }

    private static boolean openUsageRecipe(UsageResource resource) {
        ItemStack usageStack = switch (resource) {
            case MANA -> ItemFluidDisplay.createStack(AbstractSanctumContent.getManaStack(1));
            case SALT -> AbstractSanctumRecipes.getWorldSaltStack(1);
            case BLOOD -> ItemFluidDisplay.createStack(AbstractSanctumContent.getBloodStack(1));
        };
        return usageStack != null && GuiUsageRecipe.openRecipeGui("item", usageStack);
    }

    private static String formatMultiplier(double value) {
        return String.format(Locale.ROOT, "%.1f", value);
    }
}
