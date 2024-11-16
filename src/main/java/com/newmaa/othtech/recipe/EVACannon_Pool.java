package com.newmaa.othtech.recipe;

import com.newmaa.othtech.common.recipemap.Recipemaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

public class EVACannon_Pool implements IRecipePool {

    @Override
    public void loadRecipes() {
        RecipeMap<?> EVA = Recipemaps.Cannon;
        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 64))
            .noOptimize()
            .eut(2147483647)
            .duration(1)
            .addTo(EVA);
        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 64))
            .noOptimize()
            .eut(2147483647)
            .duration(65536)
            .addTo(EVA);
    }
}
