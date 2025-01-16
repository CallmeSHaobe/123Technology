package com.newmaa.othtech.recipe;

public class RecipeLoader {

    public static void loadRecipes() {
        IRecipePool[] recipePools = new IRecipePool[] { new recipesComponentAssemblyLineRecipes(),
            new recipesBlastFurnaceRecipes(), new recipesVacuumFreezerRecipes(), new recipesMain(), new recipesMAXs(),
            new recipesMegaISAForge(), new recipesMixerRecipes(), new recipesQFTRecipes(), new recipesCopiedFromTST(),
            new recipesMegaQFT(), new recipesCyclotronRecipes(), new recipesCircuit(), new recipesSINOPEC(),
            new recipesTangshanSteelFactory(), new recipesMegaEEC(), new recipesSunFactoryEnqing(),
            new recipesEpicCokeOvenFake(), new recipesWoodenFusionReactor() };
        for (IRecipePool recipePool : recipePools) {
            recipePool.loadRecipes();
        }

    }

}
