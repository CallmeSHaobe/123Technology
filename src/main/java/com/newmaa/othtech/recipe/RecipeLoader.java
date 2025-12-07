package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.Config.is_EggMachine_Recipes_For_NHU;

import net.minecraftforge.fluids.FluidRegistry;

import com.newmaa.othtech.machine.OTEMiniSteamNineInOne;
import com.newmaa.othtech.utils.modsEnum;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;

public class RecipeLoader {

    public static void loadRecipes() {
        IRecipePool[] recipePools = new IRecipePool[] { new RecipesComponentAssemblyLineRecipes(),
            new RecipesBlastFurnaceRecipes(), new RecipesFreezerRecipes(), new RecipesMain(), new RecipesMAXs(),
            new RecipesMegaISAForge(), new RecipesMixerRecipes(), new RecipesQFTRecipes(), new RecipesMegaQFT(),
            new RecipesCyclotronRecipes(), new RecipesCircuit(), new RecipesSINOPEC(),
            new RecipesTangshanSteelFactory(), new RecipesSunFactoryEnqing(), new RecipesEpicCokeOvenFake(),
            new RecipesWoodenFusionReactor(), new RecipesMISA(), new RecipesNaquadah(), new RecipesEIO(),
            new RecipesEXH(), new RecipesNewWetware(), new RecipesAntimonia(), new RecipesNASA() };
        OTEMiniSteamNineInOne.loadRecipes();
        if (!modsEnum.TwistSpaceTechnology.isModLoaded()) {
            new RecipesCopiedFromTST().loadRecipes();
        }
        if (modsEnum.NHUtilities.isModLoaded() && is_EggMachine_Recipes_For_NHU) {
            new RecipesDE().loadRecipes();
        }
        if (!modsEnum.GTNN.isModLoaded()) {
            // 78water
            GTValues.RA.stdBuilder()
                .itemOutputs(GTModHandler.getModItem("miscutils", "item.BasicGenericChemItem", 1, 30))
                .fluidInputs(FluidRegistry.getFluidStack("molten.shirabon", 92160))
                .itemInputs(
                    GTUtility.getIntegratedCircuit(10),
                    GTModHandler.getModItem("miscutils", "item.BasicAgrichemItem", 1, 13),
                    GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedlens", 64, 25),
                    GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 4054),
                    GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 32722))
                .duration(60 * 20)
                .eut(TierEU.UMV)
                .addTo(RecipeMaps.assemblerRecipes);

        }
        for (IRecipePool recipePool : recipePools) {
            recipePool.loadRecipes();
        }

    }

}
