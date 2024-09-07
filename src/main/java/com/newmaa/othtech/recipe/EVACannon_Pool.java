package com.newmaa.othtech.recipe;

import com.newmaa.othtech.common.recipemap.Recipemaps;

import gregtech.api.enums.GT_Values;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.util.GT_Utility;

public class EVACannon_Pool implements IRecipePool {

    @Override
    public void loadRecipes() {
        RecipeMap<?> EVA = Recipemaps.Cannon;
        GT_Values.RA.stdBuilder()
            .itemInputs(GT_Utility.getIntegratedCircuit(1))
            .itemOutputs(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 64))
            .noOptimize()
            .eut(2147483647)
            .duration(1)
            .addTo(EVA);
        GT_Values.RA.stdBuilder()
            .itemInputs(GT_Utility.getIntegratedCircuit(2))
            .itemOutputs(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 64))
            .noOptimize()
            .eut(2147483647)
            .duration(65536)
            .addTo(EVA);
    }
}
