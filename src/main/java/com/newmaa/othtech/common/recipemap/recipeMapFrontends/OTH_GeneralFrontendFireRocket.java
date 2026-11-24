package com.newmaa.othtech.common.recipemap.recipeMapFrontends;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.gtnewhorizons.modularui.api.ModularUITextures;
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.drawable.UITexture;
import com.gtnewhorizons.modularui.api.forge.IItemHandlerModifiable;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.math.Size;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;

import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.api.recipe.RecipeMapFrontend;
import gregtech.api.util.GTRecipe;
import gregtech.common.gui.modularui.UIHelper;
import gregtech.nei.GTNEIDefaultHandler;

public class OTH_GeneralFrontendFireRocket extends RecipeMapFrontend {

    private static final int INPUT_COLUMNS = 4;
    private static final int INPUT_ROWS = 3;
    public static final int MAX_ITEM_INPUTS = INPUT_COLUMNS * INPUT_ROWS;
    public static final int MAX_FLUID_INPUTS = 3;
    private static final int SLOT_SIZE = 18;
    private static final int INPUT_X = 8;
    private static final int INPUT_Y = 8;
    private static final int RESOURCE_Y = INPUT_Y + INPUT_ROWS * SLOT_SIZE + 3;
    private static final Pos2d OUTPUT_POS = new Pos2d(133, INPUT_Y + SLOT_SIZE);
    private static final Pos2d FLUID_POS = new Pos2d(INPUT_X, RESOURCE_Y);
    private static final Pos2d SCHEMATIC_POS = new Pos2d(79, RESOURCE_Y);
    private static final int BANNER_Y = RESOURCE_Y + SLOT_SIZE + 3;
    private static final int BANNER_HEIGHT = 24;
    private static final Size BACKGROUND_SIZE = new Size(170, BANNER_Y + BANNER_HEIGHT + 4);
    private static final UITexture FIRE_BANNER = UITexture.fullImage("123technology", "gui/fire");
    private static final String SCHEMATIC_HINT = "ote.nasa.nei.schematic_hint";

    public OTH_GeneralFrontendFireRocket(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        super(uiPropertiesBuilder, neiPropertiesBuilder.recipeBackgroundSize(BACKGROUND_SIZE));
    }

    @Override
    public ModularWindow.Builder createNEITemplate(GTNEIDefaultHandler.NEITemplateContext ctx) {
        ModularWindow.Builder builder = ModularWindow.builder(BACKGROUND_SIZE)
            .setBackground(GTUITextures.BACKGROUND_NEI_SINGLE_RECIPE);
        List<Pos2d> inputPositions = getItemInputPositions(MAX_ITEM_INPUTS);
        for (int i = 0; i < inputPositions.size(); i++) {
            int slot = i;
            addSlot(
                builder,
                ctx,
                ctx.itemInputsInventory,
                slot,
                inputPositions.get(slot),
                ModularUITextures.ITEM_SLOT,
                recipe -> slot < recipe.mInputs.length && recipe.mInputs[slot] != null);
        }
        addSlot(
            builder,
            ctx,
            ctx.itemOutputsInventory,
            0,
            OUTPUT_POS,
            ModularUITextures.ITEM_SLOT,
            recipe -> recipe.mOutputs.length > 0 && recipe.mOutputs[0] != null);
        List<Pos2d> fluidPositions = getFluidInputPositions(MAX_FLUID_INPUTS);
        for (int i = 0; i < fluidPositions.size(); i++) {
            int slot = i;
            addSlot(
                builder,
                ctx,
                ctx.fluidInputsInventory,
                slot,
                fluidPositions.get(slot),
                ModularUITextures.FLUID_SLOT,
                recipe -> slot < recipe.mFluidInputs.length && recipe.mFluidInputs[slot] != null);
        }
        addSlot(
            builder,
            ctx,
            ctx.specialSlotInventory,
            0,
            SCHEMATIC_POS,
            ModularUITextures.ITEM_SLOT,
            recipe -> recipe.mSpecialItems != null);
        builder.widget(
            new DrawableWidget().setDrawable(FIRE_BANNER)
                .setPos(new Pos2d(INPUT_X, BANNER_Y).add(ctx.windowOffset))
                .setSize(BACKGROUND_SIZE.width - 2 * INPUT_X, BANNER_HEIGHT));
        return builder;
    }

    private static void addSlot(ModularWindow.Builder builder, GTNEIDefaultHandler.NEITemplateContext ctx,
        IItemHandlerModifiable inventory, int index, Pos2d pos, IDrawable texture, Predicate<GTRecipe> present) {
        // Keep the phantom widgets for GT's cached stack positions; only hide unused slot backgrounds.
        IDrawable background = (x, y, width, height, partialTicks) -> {
            if (hasRecipe(ctx, present)) texture.draw(x, y, width, height, partialTicks);
        };
        builder.widget(
            SlotWidget.phantom(inventory, index)
                .setBackground(background)
                .setPos(pos.add(ctx.windowOffset))
                .setSize(SLOT_SIZE, SLOT_SIZE));
    }

    private static boolean hasRecipe(GTNEIDefaultHandler.NEITemplateContext ctx, Predicate<GTRecipe> test) {
        GTNEIDefaultHandler.CachedDefaultRecipe current = ctx.recipeSupplier.get();
        return current != null && test.test(current.mRecipe);
    }

    @Override
    public List<Pos2d> getItemInputPositions(int itemInputCount) {
        return UIHelper.getGridPositions(itemInputCount, INPUT_X, INPUT_Y, INPUT_COLUMNS);
    }

    @Override
    public List<Pos2d> getItemOutputPositions(int itemOutputCount) {
        return itemOutputCount == 0 ? Collections.emptyList() : Collections.singletonList(OUTPUT_POS);
    }

    @Override
    public List<Pos2d> getFluidInputPositions(int fluidInputCount) {
        return UIHelper.getGridPositions(fluidInputCount, FLUID_POS.x, FLUID_POS.y, MAX_FLUID_INPUTS);
    }

    @Override
    public List<Pos2d> getFluidOutputPositions(int fluidOutputCount) {
        return Collections.emptyList();
    }

    @Override
    public Pos2d getSpecialItemPosition() {
        return SCHEMATIC_POS;
    }

    @Override
    protected List<String> handleNEIItemInputTooltip(List<String> currentTip,
        GTNEIDefaultHandler.FixedPositionedStack stack) {
        Object schematic = stack.recipe.mRecipe.mSpecialItems;
        if (schematic instanceof ItemStack && stack.item != null && stack.item.isItemEqual((ItemStack) schematic)) {
            currentTip.add(StatCollector.translateToLocal(SCHEMATIC_HINT));
        }
        return super.handleNEIItemInputTooltip(currentTip, stack);
    }
}
