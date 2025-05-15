package com.newmaa.othtech.recipe;

import com.newmaa.othtech.common.OTHItemList;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.TierEU;
import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTUtility;

public class RecipesBlastFurnaceRecipes implements IRecipePool {

    @Override
    public void loadRecipes() {
        final IRecipeMap EBF = RecipeMaps.blastFurnaceRecipes;
        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(1), OTHItemList.dustIrOsSmM.get(1))
            .itemOutputs(OTHItemList.ingotHotDog.get(1))
            .specialValue(-3600000)
            .noOptimize()
            .duration(640 * 20)
            .eut(TierEU.UMV)
            .addTo(EBF);

    }
}
