package com.newmaa.othtech.recipe;

public class RecipeLoader {

    public static void loadRecipes() {
        IRecipePool[] recipepools = new IRecipePool[] {};
        for (IRecipePool recipePool : recipepools) {
            recipePool.loadRecipes();
        }
        new Mega_ISA_ForgePool().loadRecipes();

    }
}
