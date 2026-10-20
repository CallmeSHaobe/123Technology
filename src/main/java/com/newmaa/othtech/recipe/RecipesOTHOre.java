package com.newmaa.othtech.recipe;

import com.newmaa.othtech.utils.RecipeBuilder;

import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMaps;

public class RecipesOTHOre implements IRecipePool {

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(Materials.HydrochloricAcid.getDust(1))
            .fluidOutputs(Materials.HydrochloricAcid.getFluid(250))
            .eut(123)
            .duration(20)
            .addTo(RecipeMaps.fluidExtractionRecipes);
        RecipeBuilder.builder()
            .itemInputs(Materials.Chlorine.getDust(1))
            .fluidOutputs(Materials.Chlorine.getGas(250))
            .eut(123)
            .duration(20)
            .addTo(RecipeMaps.fluidExtractionRecipes);
    }
}
