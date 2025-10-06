package com.newmaa.othtech.common.recipemap.recipeMapFrontends;

import java.util.List;
import java.util.function.Supplier;

import com.gtnewhorizons.modularui.api.drawable.UITexture;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.math.Size;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.ProgressBar;

import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.api.recipe.RecipeMapFrontend;
import gregtech.common.gui.modularui.UIHelper;

public class OTH_GeneralFrontendFireRocket extends RecipeMapFrontend {

    private static final int xDirMaxCount = 7;
    private static final int yOrigin = 8;
    private final int itemRowCount;
    public static final UITexture OTHUIT = UITexture.fullImage("123technology", "gui/fire");

    public OTH_GeneralFrontendFireRocket(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        super(uiPropertiesBuilder.logoPos(new Pos2d(79, 7)), neiPropertiesBuilder);
        this.itemRowCount = getItemRowCount();
        neiProperties.recipeBackgroundSize = new Size(170, 40 + (itemRowCount + getFluidRowCount()) * 18);
    }

    public void addProgressBar(ModularWindow.Builder builder, Supplier<Float> progressSupplier, Pos2d windowOffset) {
        assert uiProperties.progressBarTexture != null;
        builder.widget(
            new ProgressBar().setTexture(uiProperties.progressBarTexture.get(), 100)
                .setDirection(ProgressBar.Direction.DOWN)
                .setProgress(progressSupplier)
                .setSynced(true, false)
                .setPos(new Pos2d(6 + 54, 190))
                .setSize(uiProperties.progressBarSize));
    }

    public Pos2d getSpecialItemPosition() {
        return new Pos2d(6 + 45, 220);
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder, Pos2d windowOffset) {
        builder.widget(
            new DrawableWidget().setDrawable(OTHUIT)
                .setSize(126, 24)
                .setPos(new Pos2d(5, 180)));
    }

    private int getItemRowCount() {
        return (Math.max(uiProperties.maxItemInputs, uiProperties.maxItemOutputs) - 1) / xDirMaxCount + 1;
    }

    private int getFluidRowCount() {
        return (Math.max(uiProperties.maxFluidInputs, uiProperties.maxFluidOutputs) - 1) / xDirMaxCount + 1;
    }

    @Override
    public List<Pos2d> getItemInputPositions(int itemInputCount) {
        return UIHelper.getGridPositions(itemInputCount, 6, yOrigin, xDirMaxCount);
    }

    @Override
    public List<Pos2d> getItemOutputPositions(int itemOutputCount) {
        return UIHelper.getGridPositions(itemOutputCount, 6 + 63, 220, xDirMaxCount);
    }

    @Override
    public List<Pos2d> getFluidInputPositions(int fluidInputCount) {
        return UIHelper.getGridPositions(fluidInputCount, 6, yOrigin + itemRowCount * 18, xDirMaxCount);
    }

    @Override
    public List<Pos2d> getFluidOutputPositions(int fluidOutputCount) {
        return UIHelper.getGridPositions(fluidOutputCount, 6 + 36, 220 - 18, xDirMaxCount);
    }

}
