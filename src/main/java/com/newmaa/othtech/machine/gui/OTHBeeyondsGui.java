package com.newmaa.othtech.machine.gui;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.utils.ICopy;
import com.cleanroommc.modularui.utils.serialization.ByteBufAdapters;
import com.cleanroommc.modularui.value.sync.DynamicSyncHandler;
import com.cleanroommc.modularui.value.sync.GenericSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widget.EmptyWidget;
import com.cleanroommc.modularui.widgets.CycleButtonWidget;
import com.cleanroommc.modularui.widgets.DynamicSyncedWidget;
import com.cleanroommc.modularui.widgets.ItemDisplayWidget;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.newmaa.othtech.machine.OTEBeeyonds;
import com.newmaa.othtech.machine.OTHBeeyondsMode;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.common.gui.modularui.multiblock.base.TTMultiblockBaseGui;

/**
 * MUI2 GUI for the {@link OTEBeeyonds} controller - same pattern as {@code OTEBBPlasmaForgeGui}.
 * <p>
 * Adds a mode-cycle button (Production &lt;-&gt; Breed) to the right button column, above the
 * standard TecTech row (power pass / edit parameters / power switch) - same single-button addition
 * spot {@code OTEBBPlasmaForgeGui} uses for its wireless toggle. That column sits in a hardcoded
 * 76px-tall row ({@code MTEMultiBlockBaseGui#createInventoryRow}), so only one extra icon is added
 * here; the eject action stays in the settings panel (wrench icon) instead of also living here, to
 * avoid overflowing that fixed-height row.
 * <p>
 * Bound directly to the same {@link tectech.thing.metaTileEntity.multi.base.parameter.Parameter}
 * sync handler the generic settings panel already uses for mode, so there's a single source of
 * truth and no risk of the two UIs disagreeing.
 * <p>
 * Everything this mod adds (status line, queen grid / parent slots) lives INSIDE the stock
 * "Running perfectly." box, as an extra row appended to GT5's own terminal {@code ListWidget} -
 * same pattern kubatech's {@code MTEMegaIndustrialApiaryGui} uses for its own bee grid: never grow
 * the root panel or the terminal box, just fit the content inside the existing fixed budget. The
 * content here (one status line, plus either a 4x8 queen grid or 2 parent slots + a species picker)
 * comfortably fits the 174px the stock terminal row already reserves, and GT5's own
 * {@code ListWidget} scrolls internally if it ever doesn't.
 */
public class OTHBeeyondsGui extends TTMultiblockBaseGui<OTEBeeyonds> {

    public OTHBeeyondsGui(OTEBeeyonds multiblock) {
        super(multiblock);
    }

    @Override
    protected Flow createButtonColumn(ModularPanel panel, PanelSyncManager syncManager) {
        return super.createButtonColumn(panel, syncManager).child(createModeButton());
    }

    /**
     * Appends our content as one more child of GT5's own terminal {@code ListWidget} (it auto-stacks
     * its children and scrolls if they don't fit) instead of adding a whole separate panel that
     * would need the root window itself to grow.
     */
    @Override
    protected ListWidget<IWidget, ?> createTerminalTextWidget(PanelSyncManager syncManager, ModularPanel panel) {
        return super.createTerminalTextWidget(syncManager, panel).child(createBeeyondsContent(syncManager));
    }

    /**
     * Content differs by mode: Production shows the queen grid, Breed shows the two held parent
     * bees + the target species picker. No border/theme/padding of its own - it's just extra rows
     * inside the box GT5 already draws and themes for us.
     */
    private IWidget createBeeyondsContent(PanelSyncManager syncManager) {
        return Flow.column()
            .coverChildren()
            .marginTop(4)
            .child(createStatusWidget(syncManager))
            .child(createModeContentWidget(syncManager))
            .child(
                // Kept registered but hidden (zero-sized) rather than removed: dropping these two
                // sync-registered widgets from the tree entirely destabilizes PanelSyncManager for
                // this GUI, so they stay registered and just take no visible space. Neither is wired
                // to anything useful yet, so there's nothing worth showing the player right now.
                Flow.row()
                    .size(0, 0)
                    .child(createEffectPicker(syncManager))
                    .child(createFlowerPicker(syncManager)));
    }

    // region effect/flower pickers - kept registered but hidden, see the comment above.
    //
    // Both the row AND each button need their own .size(0, 0): a button still renders its overlay
    // text at its own explicit size regardless of what box its parent claims, since Flow doesn't
    // clip children larger than their parent.
    protected IWidget createEffectPicker(PanelSyncManager syncManager) {
        IntSyncValue effectSync = new IntSyncValue(
            multiblock::getEffectSelectionIndex,
            multiblock::setEffectSelectionIndex).allowC2S();
        syncManager.syncValue("beeyondsEffect", effectSync);

        int optionCount = multiblock.getEffectOptionCount();
        CycleButtonWidget effectButton = new CycleButtonWidget().value(effectSync)
            .stateCount(optionCount)
            .size(0, 0);
        for (int i = 0; i < optionCount; i++) {
            String name = multiblock.getEffectOptionName(i);
            effectButton.stateOverlay(i, IKey.str(name));
            effectButton.addTooltip(i, name);
        }
        return effectButton;
    }

    protected IWidget createFlowerPicker(PanelSyncManager syncManager) {
        IntSyncValue flowerSync = new IntSyncValue(
            multiblock::getFlowerSelectionIndex,
            multiblock::setFlowerSelectionIndex).allowC2S();
        syncManager.syncValue("beeyondsFlower", flowerSync);

        int optionCount = multiblock.getFlowerOptionCount();
        CycleButtonWidget flowerButton = new CycleButtonWidget().value(flowerSync)
            .stateCount(optionCount)
            .size(0, 0);
        for (int i = 0; i < optionCount; i++) {
            String name = multiblock.getFlowerOptionName(i);
            flowerButton.stateOverlay(i, IKey.str(name));
            flowerButton.addTooltip(i, name);
        }
        return flowerButton;
    }
    // endregion

    /**
     * The mode content (queen grid vs. parent slots) is rebuilt reactively through a
     * {@link DynamicSyncHandler} instead of being decided once from a plain getter at GUI
     * construction time - same {@code widgetProvider}/{@code notifyUpdate} idiom GT5 itself uses for
     * {@code createStructureErrorWidget}/{@code createRecipeInfoWidget}. A raw Java getter read
     * during construction has no guarantee the client's copy has caught up with the server, so
     * anything that decides which widget to show has to go through the sync framework, not a direct
     * field read.
     * <p>
     * {@code activeModeSync} mirrors {@link OTEBeeyonds#getActiveMode()} for the packet payload (the
     * content builder needs the raw mode ordinal), but its supplier is
     * {@link OTEBeeyonds#getModeContentTrigger()}, which folds the Breed-mode parent species pairing
     * into the same int the automatic per-tick change detector already watches - so swapping a
     * parent bee still triggers a content rebuild without needing a second top-level sync
     * registration.
     */
    private IWidget createModeContentWidget(PanelSyncManager syncManager) {
        IntSyncValue activeModeSync = new IntSyncValue(multiblock::getModeContentTrigger);
        syncManager.syncValue("beeyondsActiveMode", activeModeSync);

        DynamicSyncHandler modeContentHandler = new DynamicSyncHandler().widgetProvider(
            (sm, packet) -> packet == null ? new EmptyWidget()
                : createModeContent(sm, OTHBeeyondsMode.values()[packet.readInt()]));
        syncManager.syncValue("beeyondsModeContent", modeContentHandler);

        if (!syncManager.isClient()) {
            Runnable pushContent = () -> {
                // NOT activeModeSync.getValue() - that carries the combined mode+species trigger
                // (see the doc above), not a valid OTHBeeyondsMode ordinal by itself.
                int ordinal = multiblock.getActiveMode()
                    .ordinal();
                modeContentHandler.notifyUpdate(packet -> packet.writeInt(ordinal));
            };
            pushContent.run();
            activeModeSync.setChangeListener(pushContent);
        }

        return new DynamicSyncedWidget<>().syncHandler(modeContentHandler)
            .coverChildren();
    }

    private IWidget createModeContent(PanelSyncManager syncManager, OTHBeeyondsMode mode) {
        return mode == OTHBeeyondsMode.BREED ? createBreedContent(syncManager) : createQueenGrid(syncManager);
    }

    private IWidget createBreedContent(PanelSyncManager syncManager) {
        return Flow.column()
            .coverChildren()
            .child(createParentSlotsRow(syncManager))
            .child(createTargetSpeciesWidget(syncManager));
    }

    /**
     * Cycle button listing every result species this exact parent pairing can actually produce -
     * the shared species itself (purebred pairing, see {@link OTEBeeyonds#tickBreed()}) or, for two
     * different species, whichever different-species mutations Forestry has registered for that
     * pair. Only ever rebuilt (not just value-updated) as part of {@link #createBreedContent}, since
     * a {@link CycleButtonWidget}'s state count can't change live once built. Registered with
     * {@code getOrCreateSyncHandler(...)}, not {@code syncValue(...)}: this runs inside a
     * {@link DynamicSyncHandler}'s {@code widgetProvider}, which only allows the former.
     */
    @SuppressWarnings("unchecked")
    private IWidget createTargetSpeciesWidget(PanelSyncManager syncManager) {
        int optionCount = multiblock.getTargetSpeciesOptionCount();
        IntSyncValue speciesIndexSync = syncManager.getOrCreateSyncHandler(
            "beeyondsTargetSpeciesIndex",
            0,
            IntSyncValue.class,
            () -> new IntSyncValue(
                multiblock::getTargetSpeciesSelectionIndex,
                multiblock::setTargetSpeciesSelectionIndex).allowC2S());

        CycleButtonWidget speciesButton = new CycleButtonWidget().marginTop(4)
            .size(getTerminalRowWidth() - 8, 18)
            .value(speciesIndexSync)
            .stateCount(optionCount);
        // Deliberately no .addTooltip(...) here: with no parents held (optionCount falls back to 1,
        // the button's default state count), the tooltip array never grows past its initial empty
        // state and the first addTooltip(0, ...) throws. The button's own text (stateChild below)
        // already shows the full species name, so a tooltip isn't needed anyway.
        for (int i = 0; i < optionCount; i++) {
            int index = i;
            speciesButton
                .stateChild(i, new TextWidget<>(IKey.dynamic(() -> multiblock.getTargetSpeciesOptionName(index))));
        }
        return speciesButton;
    }

    // region parent slots (Breed mode)
    private IWidget createParentSlotsRow(PanelSyncManager syncManager) {
        return Flow.row()
            .coverChildren()
            .child(createParentSlotWidget(syncManager, 0))
            .child(createParentSlotWidget(syncManager, 1));
    }

    // getOrCreateSyncHandler(...), NOT syncValue(...): this widget is only ever built inside
    // createModeContent(), which only ever runs inside the DynamicSyncHandler's widgetProvider (see
    // createModeContentWidget()) - registering a sync handler any other way there throws.
    @SuppressWarnings("unchecked")
    private IWidget createParentSlotWidget(PanelSyncManager syncManager, int index) {
        GenericSyncValue<ItemStack, ?> parentSync = syncManager.getOrCreateSyncHandler(
            "beeyondsParentSlot" + index,
            0,
            GenericSyncValue.class,
            () -> new GenericSyncValue<>(
                ItemStack.class,
                () -> multiblock.getParentDisplayStack(index),
                stack -> {},
                ByteBufAdapters.ITEM_STACK,
                ICopy.immutable()));
        return new ItemDisplayWidget().item(parentSync);
    }
    // endregion

    protected IWidget createStatusWidget(PanelSyncManager syncManager) {
        StringSyncValue statusSync = new StringSyncValue(multiblock::getStatusText);
        syncManager.syncValue("beeyondsStatus", statusSync);

        // One aggregated "Producing X x N in Ts" tooltip for the whole machine - like GTNH's Steam
        // Space Elevator shows a single countdown, not a separate tooltip per held queen.
        StringSyncValue productionTooltipSync = new StringSyncValue(multiblock::getProductionPreviewTooltip);
        syncManager.syncValue("beeyondsProductionTooltip", productionTooltipSync);

        TextWidget<?> statusWidget = new TextWidget<>(IKey.dynamic(statusSync::getStringValue));
        statusWidget.tooltipDynamic((RichTooltip tooltip) -> {
            String text = productionTooltipSync.getStringValue();
            if (text == null || text.isEmpty()) return;
            for (String line : text.split("\n")) {
                tooltip.addLine(line);
            }
        });
        return statusWidget;
    }

    // region queen grid
    // Read-only queen slot grid, like GTNH's own Industrial Apiary / Mega Industrial Apiary controllers
    // show every queen they're working instead of hiding them behind a settings sub-panel. Capped at
    // QUEEN_GRID_ROWS x QUEEN_GRID_COLUMNS icons - past that (higher tiers double the slot count every
    // named GT voltage tier, see OTEBeeyonds#queenSlotCount()) the extra queens still work fully, they
    // just aren't individually pictured in this fixed-size grid.
    private static final int QUEEN_GRID_COLUMNS = 8;
    private static final int QUEEN_GRID_ROWS = 4;

    protected IWidget createQueenGrid(PanelSyncManager syncManager) {
        return Flow.column()
            .coverChildren()
            .children(QUEEN_GRID_ROWS, row -> createQueenGridRow(syncManager, row));
    }

    private IWidget createQueenGridRow(PanelSyncManager syncManager, int row) {
        return Flow.row()
            .coverChildren()
            .children(QUEEN_GRID_COLUMNS, col -> createQueenSlotWidget(syncManager, row * QUEEN_GRID_COLUMNS + col));
    }

    // getOrCreateSyncHandler(...), NOT syncValue(...) - see the comment on createParentSlotWidget().
    @SuppressWarnings("unchecked")
    private IWidget createQueenSlotWidget(PanelSyncManager syncManager, int index) {
        // GenericSyncValue.forItem(...) throws unless the getter is guaranteed non-null - most queen
        // cells are empty most of the time (null stack), so the type must be passed explicitly, which
        // is the overload that actually tolerates a null getter value.
        GenericSyncValue<ItemStack, ?> queenSync = syncManager.getOrCreateSyncHandler(
            "beeyondsQueenSlot" + index,
            0,
            GenericSyncValue.class,
            () -> new GenericSyncValue<>(
                ItemStack.class,
                () -> multiblock.getQueenDisplayStack(index),
                stack -> {},
                ByteBufAdapters.ITEM_STACK,
                ICopy.immutable()));
        // Per-queen tooltip deliberately not shown here - production preview is a single aggregated
        // tooltip on the main status line instead, see createStatusWidget().
        return new ItemDisplayWidget().item(queenSync);
    }
    // endregion

    protected IWidget createModeButton() {
        OTHBeeyondsMode[] modes = OTHBeeyondsMode.values();
        CycleButtonWidget modeButton = new CycleButtonWidget().marginBottom(2)
            .value(
                multiblock.getModeParameter()
                    .getSyncHandler())
            .stateCount(modes.length);
        for (int i = 0; i < modes.length; i++) {
            modeButton.stateOverlay(i, GTGuiTextures.OVERLAY_BUTTON_MODE[i]);
            modeButton.addTooltip(
                i,
                IKey.lang(
                    modes[i] == OTHBeeyondsMode.PRODUCTION ? "otht.bee.gui.mode.production"
                        : "otht.bee.gui.mode.breed"));
        }
        return modeButton;
    }
}
