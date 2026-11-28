package com.newmaa.othtech.recipe;

import com.newmaa.othtech.common.recipemap.Recipemaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.util.GTModHandler;

public class RecipesWoodenFusionReactor implements IRecipePool {

    public void loadRecipes() {
        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.Steam.getGas(1000))
            .fluidOutputs(Materials.Hydrogen.getPlasma(1))
            .eut(0)
            .duration(20 * 20)
            .addTo(Recipemaps.WFR);
        GTValues.RA.stdBuilder()
            .fluidInputs(GTModHandler.getSuperHeatedSteam(1000))
            .fluidOutputs(Materials.Hydrogen.getPlasma(10))
            .eut(0)
            .duration(20 * 20)
            .addTo(Recipemaps.WFR);
        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.DenseSupercriticalSteam.getGas(1000))
            .fluidOutputs(Materials.Hydrogen.getPlasma(100))
            .eut(0)
            .duration(20 * 20)
            .addTo(Recipemaps.WFR);
    }
}
