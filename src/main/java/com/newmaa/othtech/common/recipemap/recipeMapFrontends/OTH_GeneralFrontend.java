package com.newmaa.othtech.common.recipemap.recipeMapFrontends;

import static com.newmaa.othtech.Config.NEIFrontend;

import java.util.List;

import com.gtnewhorizons.modularui.api.drawable.UITexture;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.math.Size;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;

import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.api.recipe.RecipeMapFrontend;
import gregtech.common.gui.modularui.UIHelper;

public class OTH_GeneralFrontend extends RecipeMapFrontend {

    private static final int xDirMaxCount = 4;
    private static final int yOrigin = 8;
    private final int itemRowCount;
    public static final UITexture OTHUIT = NEIFrontend ? UITexture.fullImage("123technology", "items/MetaItem/0")
        : UITexture.fullImage("123technology", "items/MetaItem/bb");

    public OTH_GeneralFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        super(uiPropertiesBuilder.logoPos(new Pos2d(79, 7)), neiPropertiesBuilder);
        this.itemRowCount = getItemRowCount();
        neiProperties.recipeBackgroundSize = new Size(170, 10 + (itemRowCount + getFluidRowCount()) * 18);
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder, Pos2d windowOffset) {
        builder.widget(
            new DrawableWidget().setDrawable(OTHUIT)
                .setSize(18, 18)
                .setPos(uiProperties.logoPos.add(windowOffset)));
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
        return UIHelper.getGridPositions(itemOutputCount, 98, yOrigin, xDirMaxCount);
    }

    @Override
    public List<Pos2d> getFluidInputPositions(int fluidInputCount) {
        return UIHelper.getGridPositions(fluidInputCount, 6, yOrigin + itemRowCount * 18, xDirMaxCount);
    }

    @Override
    public List<Pos2d> getFluidOutputPositions(int fluidOutputCount) {
        return UIHelper.getGridPositions(fluidOutputCount, 98, yOrigin + itemRowCount * 18, xDirMaxCount);
    }

}
