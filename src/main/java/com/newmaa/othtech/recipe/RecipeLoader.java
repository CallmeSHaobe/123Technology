package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.Config.is_EggMachine_Recipes_For_NHU;

import com.newmaa.othtech.utils.modsEnum;

public class RecipeLoader {

    public static void loadRecipes() {
        IRecipePool[] recipePools = new IRecipePool[] { new RecipesComponentAssemblyLineRecipes(),
            new RecipesBlastFurnaceRecipes(), new RecipesFreezerRecipes(), new RecipesMain(), new RecipesMAXs(),
            new RecipesMegaISAForge(), new RecipesMixerRecipes(), new RecipesQFTRecipes(), new RecipesMegaQFT(),
            new RecipesCyclotronRecipes(), new RecipesCircuit(), new RecipesSINOPEC(),
            new RecipesTangshanSteelFactory(), new RecipesSunFactoryEnqing(), new RecipesEpicCokeOvenFake(),
            new RecipesWoodenFusionReactor(), new RecipesMISA(), new RecipesNaquadah(), new RecipesEIO(),
            new RecipesEXH(), new RecipesNewWetware(), new RecipesAntimonia() };
        if (!modsEnum.TwistSpaceTechnology.isModLoaded()) {
            new RecipesCopiedFromTST().loadRecipes();
        }
        if (modsEnum.NHUtilities.isModLoaded() && is_EggMachine_Recipes_For_NHU) {
            new RecipesDE().loadRecipes();
        }
        for (IRecipePool recipePool : recipePools) {
            recipePool.loadRecipes();
        }

    }

}
