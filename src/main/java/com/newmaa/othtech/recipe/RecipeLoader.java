package com.newmaa.othtech.recipe;

import com.newmaa.othtech.utils.modsEnum;

public class RecipeLoader {

    public static void loadRecipes() {
        IRecipePool[] recipePools = new IRecipePool[] { new recipesComponentAssemblyLineRecipes(),
            new recipesBlastFurnaceRecipes(), new recipesFreezerRecipes(), new recipesMain(), new recipesMAXs(),
            new recipesMegaISAForge(), new recipesMixerRecipes(), new recipesQFTRecipes(), new recipesMegaQFT(),
            new recipesCyclotronRecipes(), new recipesCircuit(), new recipesSINOPEC(),
            new recipesTangshanSteelFactory(), new recipesMegaEEC(), new recipesSunFactoryEnqing(),
            new recipesEpicCokeOvenFake(), new recipesWoodenFusionReactor(), new recipesMISA(), new recipesNaquadah(),
            new recipesEIO(), new recipesEXH(), new recipesNewWetware() };
        if (!modsEnum.TwistSpaceTechnology.isModLoaded()) {
            new recipesCopiedFromTST().loadRecipes();
        }
        if (modsEnum.NHUtilities.isModLoaded()) {
            new recipesDE().loadRecipes();
        }
        for (IRecipePool recipePool : recipePools) {
            recipePool.loadRecipes();
        }

    }

}
