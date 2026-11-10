package com.newmaa.othtech.common.recipemap.recipeMapFrontends;

import static com.newmaa.othtech.Config.NEIFrontend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizons.modularui.api.ModularUITextures;
import com.gtnewhorizons.modularui.api.drawable.UITexture;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.math.Size;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.ProgressBar;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.newmaa.othtech.recipe.AbstractSanctumRecipes;

import codechicken.nei.PositionedStack;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.api.recipe.RecipeMapFrontend;
import gregtech.common.gui.modularui.UIHelper;
import gregtech.nei.GTNEIDefaultHandler;

public class AbstractSanctumFrontend extends RecipeMapFrontend {

    private static final int INPUT_X = 6;
    private static final int RIGHT_COLUMN_X = 150;
    private static final int TOP_Y = 8;
    private static final int INPUT_COLUMNS = 8;
    private static final int MAX_INPUT_ROWS = 4;
    private static final int MAX_ITEM_INPUTS = INPUT_COLUMNS * MAX_INPUT_ROWS;
    private static final int MAX_FLUID_INPUTS = 2;
    private static final int SALT_X = INPUT_X;
    private static final int SALT_Y = TOP_Y + MAX_INPUT_ROWS * 18 + 4;
    private static final int FLUID_X = SALT_X + 2 * 18;
    private static final int FLUID_Y = SALT_Y;
    private static final int DUPLICATE_INPUT_COLUMNS = 4;
    private static final int DUPLICATE_OUTPUT_X = 108;
    private static final int DUPLICATE_PROGRESS_X = 78;
    private static final int DUPLICATE_RESOURCE_Y = TOP_Y + 22;
    private static final int DUPLICATE_SALT_X = INPUT_X;
    private static final int DUPLICATE_FLUID_X = DUPLICATE_SALT_X + 18;
    private static final int CRAFT_BACKGROUND_HEIGHT = 178;
    private static final int DUPLICATE_BACKGROUND_WIDTH = 138;
    private static final int DUPLICATE_BACKGROUND_HEIGHT = 54;
    public static final UITexture OTHUIT = NEIFrontend ? UITexture.fullImage("123technology", "items/MetaItem/0")
        : UITexture.fullImage("123technology", "items/MetaItem/bb");

    private final boolean duplicateMode;
    private Pos2d windowOffset = new Pos2d(0, 0);

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder, Pos2d windowOffset) {
        builder.widget(
            new DrawableWidget().setDrawable(OTHUIT)
                .setSize(18, 18)
                .setPos(uiProperties.logoPos.add(windowOffset)));
    }

    public AbstractSanctumFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder, boolean duplicateMode) {
        super(
            uiPropertiesBuilder
                .logoPos(duplicateMode ? new Pos2d(DUPLICATE_OUTPUT_X + 8, 28) : new Pos2d(RIGHT_COLUMN_X, 52)),
            neiPropertiesBuilder.recipeBackgroundSize(
                duplicateMode ? new Size(DUPLICATE_BACKGROUND_WIDTH, DUPLICATE_BACKGROUND_HEIGHT)
                    : new Size(170, CRAFT_BACKGROUND_HEIGHT)));
        this.duplicateMode = duplicateMode;
    }

    @Override
    public void prepareRecipe(GTNEIDefaultHandler.CachedDefaultRecipe recipe) {
        List<PositionedStack> regularInputs = new ArrayList<>();
        List<PositionedStack> fluidInputs = new ArrayList<>();
        List<GTNEIDefaultHandler.FixedPositionedStack> worldSaltInputs = new ArrayList<>();

        for (PositionedStack stack : recipe.mInputs) {
            if (!(stack instanceof GTNEIDefaultHandler.FixedPositionedStack fixed)) {
                regularInputs.add(stack);
                continue;
            }
            if (fixed.isFluid()) {
                fluidInputs.add(fixed);
            } else if (fixed.item != null && AbstractSanctumRecipes.isWorldSalt(fixed.item)) {
                worldSaltInputs.add(fixed);
            } else {
                regularInputs.add(fixed);
            }
        }

        GTNEIDefaultHandler.FixedPositionedStack resourceSalt = null;
        if (!worldSaltInputs.isEmpty()) {
            // The last world-salt stack is the machine's separate resource cost. Everything
            // before it belongs to the recipe itself and can be combined for display only.
            resourceSalt = worldSaltInputs.remove(worldSaltInputs.size() - 1);
            if (!worldSaltInputs.isEmpty()) {
                regularInputs.add(mergeWorldSaltInputs(recipe, worldSaltInputs));
            }
        }

        ensureCircuitVisible(recipe, regularInputs);
        recipe.mInputs.clear();
        recipe.mInputs.addAll(regularInputs);
        recipe.mInputs.addAll(fluidInputs);
        if (resourceSalt != null) {
            resourceSalt.relx = (duplicateMode ? DUPLICATE_SALT_X : SALT_X) + windowOffset.x + 1;
            resourceSalt.rely = (duplicateMode ? DUPLICATE_RESOURCE_Y : SALT_Y) + windowOffset.y + 1;
            recipe.mInputs.add(resourceSalt);
        }
    }

    private static GTNEIDefaultHandler.FixedPositionedStack mergeWorldSaltInputs(
        GTNEIDefaultHandler.CachedDefaultRecipe recipe, List<GTNEIDefaultHandler.FixedPositionedStack> saltInputs) {
        GTNEIDefaultHandler.FixedPositionedStack first = saltInputs.get(0);
        long total = 0;
        for (GTNEIDefaultHandler.FixedPositionedStack salt : saltInputs) {
            total += salt.realStackSize;
        }
        ItemStack merged = first.item.copy();
        merged.stackSize = (int) Math.min(Integer.MAX_VALUE, Math.max(1L, total));
        return new GTNEIDefaultHandler.FixedPositionedStack(
            recipe,
            merged,
            true,
            first.relx,
            first.rely,
            PositionedStack.CHANCE_FULL,
            false,
            true);
    }

    private static void ensureCircuitVisible(GTNEIDefaultHandler.CachedDefaultRecipe recipe,
        List<PositionedStack> regularInputs) {
        if (recipe.mRecipe.mInputs == null) return;
        for (PositionedStack stack : regularInputs) {
            if (stack instanceof GTNEIDefaultHandler.FixedPositionedStack fixed && fixed.item != null
                && gregtech.api.util.GTUtility.isAnyIntegratedCircuit(fixed.item)) {
                return;
            }
        }
        for (ItemStack item : recipe.mRecipe.mInputs) {
            if (item != null && gregtech.api.util.GTUtility.isAnyIntegratedCircuit(item)) {
                regularInputs.add(
                    new GTNEIDefaultHandler.FixedPositionedStack(
                        recipe,
                        item.copy(),
                        false,
                        INPUT_X + 1,
                        TOP_Y + 1,
                        false));
                return;
            }
        }
    }

    @Override
    public ModularWindow.Builder createNEITemplate(GTNEIDefaultHandler.NEITemplateContext ctx) {
        windowOffset = ctx.windowOffset;
        ModularWindow.Builder builder = ModularWindow
            .builder(
                duplicateMode ? new Size(DUPLICATE_BACKGROUND_WIDTH, DUPLICATE_BACKGROUND_HEIGHT)
                    : new Size(170, CRAFT_BACKGROUND_HEIGHT))
            .setBackground(GTUITextures.BACKGROUND_NEI_SINGLE_RECIPE);
        List<Pos2d> itemPositions = duplicateMode
            ? UIHelper.getGridPositions(DUPLICATE_INPUT_COLUMNS, INPUT_X, TOP_Y, DUPLICATE_INPUT_COLUMNS)
            : getItemInputPositions(MAX_ITEM_INPUTS);
        for (int i = 0; i < itemPositions.size(); i++) {
            addItemInputSlot(builder, ctx, i, itemPositions.get(i));
        }
        List<Pos2d> fluidPositions = getFluidInputPositions(MAX_FLUID_INPUTS);
        for (int i = 0; i < fluidPositions.size(); i++) {
            addFluidInputSlot(builder, ctx, i, fluidPositions.get(i));
        }
        addResourceSlotBackground(
            builder,
            ctx,
            new Pos2d(duplicateMode ? DUPLICATE_SALT_X : SALT_X, duplicateMode ? DUPLICATE_RESOURCE_Y : SALT_Y));
        builder.widget(
            SlotWidget.phantom(ctx.itemOutputsInventory, 0)
                .setBackground(ModularUITextures.ITEM_SLOT)
                .setPos(new Pos2d(duplicateMode ? DUPLICATE_OUTPUT_X : RIGHT_COLUMN_X, TOP_Y).add(ctx.windowOffset))
                .setSize(18, 18));
        addProgressBar(builder, ctx);
        addGregTechLogo(builder, ctx.windowOffset);
        return builder;
    }

    private static void addItemInputSlot(ModularWindow.Builder builder, GTNEIDefaultHandler.NEITemplateContext ctx,
        int inventoryIndex, Pos2d pos) {
        builder.widget(
            SlotWidget.phantom(ctx.itemInputsInventory, inventoryIndex)
                .setBackground(ModularUITextures.ITEM_SLOT)
                .setPos(pos.add(ctx.windowOffset))
                .setSize(18, 18));
    }

    private static void addFluidInputSlot(ModularWindow.Builder builder, GTNEIDefaultHandler.NEITemplateContext ctx,
        int inventoryIndex, Pos2d pos) {
        builder.widget(
            SlotWidget.phantom(ctx.fluidInputsInventory, inventoryIndex)
                .setBackground(ModularUITextures.FLUID_SLOT)
                .setPos(pos.add(ctx.windowOffset))
                .setSize(18, 18));
    }

    private static void addResourceSlotBackground(ModularWindow.Builder builder,
        GTNEIDefaultHandler.NEITemplateContext ctx, Pos2d pos) {
        builder.widget(
            new DrawableWidget().setDrawable(ModularUITextures.ITEM_SLOT)
                .setPos(pos.add(ctx.windowOffset))
                .setSize(18, 18));
    }

    @Override
    public List<Pos2d> getItemInputPositions(int itemInputCount) {
        return UIHelper
            .getGridPositions(itemInputCount, INPUT_X, TOP_Y, duplicateMode ? DUPLICATE_INPUT_COLUMNS : INPUT_COLUMNS);
    }

    @Override
    public List<Pos2d> getItemOutputPositions(int itemOutputCount) {
        if (itemOutputCount <= 0) return Collections.emptyList();
        return UIHelper
            .getGridPositions(itemOutputCount, duplicateMode ? DUPLICATE_OUTPUT_X : RIGHT_COLUMN_X, TOP_Y, 1);
    }

    @Override
    public List<Pos2d> getFluidInputPositions(int fluidInputCount) {
        return duplicateMode ? UIHelper.getGridPositions(fluidInputCount, DUPLICATE_FLUID_X, DUPLICATE_RESOURCE_Y, 2)
            : UIHelper.getGridPositions(fluidInputCount, FLUID_X, FLUID_Y, 3);
    }

    @Override
    public List<Pos2d> getFluidOutputPositions(int fluidOutputCount) {
        return Collections.emptyList();
    }

    @Override
    public void addProgressBar(ModularWindow.Builder builder, GTNEIDefaultHandler.NEITemplateContext ctx) {
        assert uiProperties.progressBarTexture != null;
        int progressX = duplicateMode ? DUPLICATE_PROGRESS_X : RIGHT_COLUMN_X;
        int progressY = duplicateMode ? TOP_Y : 30;
        builder.widget(
            new ProgressBar().setTexture(uiProperties.progressBarTexture.get(), 20)
                .setDirection(ProgressBar.Direction.RIGHT)
                .setProgress(ctx.progressSupplier)
                .setSynced(false, false)
                .setPos(new Pos2d(progressX, progressY).add(ctx.windowOffset))
                .setSize(20, 18));
    }

    @Override
    protected void drawNEIOverlayForInput(GTNEIDefaultHandler.FixedPositionedStack stack) {
        if (stack.item == null || !AbstractSanctumRecipes.isWorldSalt(stack.item)) {
            return;
        }

        int x = stack.relx - 2;
        int y = stack.rely - 2;
        int width = 20;
        int height = 20;
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_LINE_BIT);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(0.20F, 0.20F, 0.20F, 0.92F);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2i(x + 1, y + 1);
        GL11.glVertex2i(x + width - 1, y + 1);
        GL11.glVertex2i(x + width - 1, y + height - 1);
        GL11.glVertex2i(x + 1, y + height - 1);
        GL11.glEnd();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        stack.draw(0, 0);
        GL11.glColor4f(0.95F, 0.72F, 0.18F, 0.95F);
        GL11.glLineWidth(1.0F);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex2i(x, y);
        GL11.glVertex2i(x + width, y);
        GL11.glVertex2i(x + width, y + height);
        GL11.glVertex2i(x, y + height);
        GL11.glEnd();
        GL11.glPopAttrib();
    }
}
