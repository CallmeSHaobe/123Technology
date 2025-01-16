package com.newmaa.othtech.recipe;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.TierEU;
import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;

public class recipesBlastFurnaceRecipes implements IRecipePool {

    @Override
    public void loadRecipes() {
        final IRecipeMap EBF = RecipeMaps.blastFurnaceRecipes;
        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(1), GTModHandler.getModItem("123Technology", "MetaItem", 1, 0))
            .itemOutputs(GTModHandler.getModItem("123Technology", "ingotHotDog", 1))
            .specialValue(-123123)
            .noOptimize()
            .duration(114514)
            .eut(TierEU.UXV)
            .addTo(EBF);

    }
}
