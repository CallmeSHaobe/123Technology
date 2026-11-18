package com.newmaa.othtech.recipe;

import static gregtech.api.enums.Mods.GregTech;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;

public class RecipesOTHOre implements IRecipePool {

    @Override
    public void loadRecipes() {
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(Materials.HydrochloricAcid.getDust(1))
            .fluidOutputs(Materials.HydrochloricAcid.getFluid(250))
            .eut(120)
            .duration(20)
            .addTo(RecipeMaps.fluidExtractionRecipes);
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(GTModHandler.getModItem(GregTech.ID, "gt.metaitem.03", 1, 5023))
            .fluidOutputs(Materials.Chlorine.getGas(1000))
            .eut(480)
            .duration(100)
            .addTo(RecipeMaps.fluidExtractionRecipes);
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(GTModHandler.getModItem(GregTech.ID, "gt.blockores2", 1, 23))
            .itemOutputs(GTModHandler.getModItem(GregTech.ID, "gt.metaitem.03", 8, 5023))
            .eut(120)
            .duration(80)
            .addTo(RecipeMaps.maceratorRecipes);
    }
}
