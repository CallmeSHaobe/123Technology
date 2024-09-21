package com.newmaa.othtech.recipe;

import com.newmaa.othtech.common.recipemap.Recipemaps;

import gregtech.api.enums.GT_Values;
import gregtech.api.enums.ItemList;
import gregtech.api.recipe.RecipeMap;

public class MegaEEC_Pool implements IRecipePool {

    @Override
    public void loadRecipes() {
        final RecipeMap<?> EEC = Recipemaps.MEEC;
        GT_Values.RA.stdBuilder()
            .itemInputs(ItemList.Sensor_MAX.get(1))
            .itemOutputs(ItemList.Sensor_MAX.get(1))
            .noOptimize()
            .eut(1)
            .duration(1)
            .addTo(EEC);
    }
}
