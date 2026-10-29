package com.newmaa.othtech.recipe;

import static gregtech.api.enums.Mods.GregTech;

import com.newmaa.othtech.utils.RecipeBuilder;

import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;

public class RecipesOTHOre implements IRecipePool {

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(Materials.HydrochloricAcid.getDust(1))
            .fluidOutputs(Materials.HydrochloricAcid.getFluid(250))
            .eut(120)
            .duration(20)
            .addTo(RecipeMaps.fluidExtractionRecipes);
        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem(GregTech.ID, "gt.metaitem.03", 1, 5023))
            .fluidOutputs(Materials.Chlorine.getGas(1000))
            .eut(480)
            .duration(100)
            .addTo(RecipeMaps.fluidExtractionRecipes);
        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem(GregTech.ID, "gt.blockores2", 1, 23))
            .itemOutputs(GTModHandler.getModItem(GregTech.ID, "gt.metaitem.03", 8, 5023))
            .eut(120)
            .duration(80)
            .addTo(RecipeMaps.maceratorRecipes);
    }
}
