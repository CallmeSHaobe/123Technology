package com.newmaa.othtech.recipe;

public class RecipeLoader {

    public static void loadRecipes() {
        IRecipePool[] recipePools = new IRecipePool[] { new Comass(), new EBF(), new Freezer(), new Main(), new MAXs(),
            new Mega_ISA_ForgePool(), new Mixer(), new QFT(), new RecipesFromTST(), new Mega_QFT_Pool(),
            new Cyclotron(), new Circuit(), new EVACannon_Pool(), new SINOPEC_Pool(), new SF_Pool(), new MegaEEC_Pool(),
            new SunFactory_Pool() };
        for (IRecipePool recipePool : recipePools) {
            recipePool.loadRecipes();
        }

    }

}
