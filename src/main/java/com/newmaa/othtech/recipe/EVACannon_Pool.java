package com.newmaa.othtech.recipe;

import com.newmaa.othtech.common.recipemap.Recipemaps;

import gregtech.api.recipe.RecipeMap;

public class EVACannon_Pool implements IRecipePool {

    @Override
    public void loadRecipes() {
        RecipeMap<?> EVA = Recipemaps.Cannon;
    }
}
