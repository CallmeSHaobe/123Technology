package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.Config.is_EggMachine_Recipes_For_NHU;

import net.minecraftforge.fluids.FluidRegistry;

import com.newmaa.othtech.OTHTechnology;
import com.newmaa.othtech.machine.OTEMiniSteamNineInOne;
import com.newmaa.othtech.utils.ModsEnum;
import com.newmaa.othtech.utils.RecipeFluids;
import com.newmaa.othtech.utils.RecipeStacks;

import bartworks.system.material.WerkstoffLoader;
import goodgenerator.items.GGMaterial;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;
import gregtech.common.items.MetaGeneratedItem03;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;

public class RecipeLoader {

    public static void loadRecipes() {
        IRecipePool[] recipePools = new IRecipePool[] { new RecipesComponentAssemblyLineRecipes(),
            new RecipesBlastFurnaceRecipes(), new RecipesFreezerRecipes(), new RecipesMain(), new RecipesMAXs(),
            new RecipesMegaISAForge(), new RecipesMixerRecipes(), new RecipesQFTRecipes(), new RecipesMegaQFT(),
            new RecipesCyclotronRecipes(), new RecipesCircuit(), new RecipesSINOPEC(),
            new RecipesTangshanSteelFactory(), new RecipesSunFactoryEnqing(), new RecipesEpicCokeOvenFake(),
            new RecipesWoodenFusionReactor(), new RecipesMISA(), new RecipesNaquadah(), new RecipesEIO(),
            new RecipesEXH(), new RecipesNewWetware(), new RecipesAntimonia(), new RecipesNASA(), new RecipesOTHOre(),
            new RecipesAbstractSanctum(), new RecipesDeepSeiko(), new RecipesTSTPatch(), new RecipesVoidNexus() };
        OTHTechnology.LOG.info("RecipeLoader: Loading OTEMiniSteamNineInOne recipes...");
        OTEMiniSteamNineInOne.loadRecipes();
        OTHTechnology.LOG.info("RecipeLoader: OTEMiniSteamNineInOne done.");
        if (ModsEnum.NHUtilities.isModLoaded() && is_EggMachine_Recipes_For_NHU) {
            new RecipesDE().loadRecipes();
        }
        if (!ModsEnum.GTNN.isModLoaded()) {
            // 78water
            GTValues.RA.stdBuilder()
                .itemOutputs(GTModHandler.getModItem("miscutils", "item.BasicGenericChemItem", 1, 30))
                .fluidInputs(
                    FluidRegistry.getFluidStack(
                        Mods.GoodGenerator.isModLoaded() ? RecipeFluids.werkstoffMoltenName(GGMaterial.shirabon)
                            : "molten.shirabon",
                        92160))
                .itemInputs(
                    GTUtility.getIntegratedCircuit(10),
                    (GregTechAPI.sPreloadStarted && Mods.GTPlusPlus.isModLoaded()
                        && GregtechItemList.EmptyCatalystCarrier.hasBeenSet()
                            ? RecipeStacks.exact(1, GregtechItemList.EmptyCatalystCarrier.getItem(), 13)
                            : null),
                    RecipeStacks.werkstoff(64, OrePrefixes.lens, WerkstoffLoader.Hedenbergit),
                    RecipeStacks.generated(64, MetaGeneratedItem03.INSTANCE, OrePrefixes.nanite, Materials.Silver),
                    RecipeStacks.exact(64, ItemList.Circuit_Silicon_Wafer6))
                .duration(60 * 20)
                .eut(TierEU.UMV)
                .addTo(RecipeMaps.assemblerRecipes);

        }
        int idx = 0;
        for (IRecipePool recipePool : recipePools) {
            String poolName = recipePool.getClass()
                .getSimpleName();
            OTHTechnology.LOG.info("RecipeLoader: [{}/{}] Loading {}...", ++idx, recipePools.length, poolName);
            boolean loaded = true;
            try {
                recipePool.loadRecipes();
            } catch (Throwable t) {
                loaded = false;
                OTHTechnology.LOG.error(
                    "RecipeLoader: [{}/{}] {} FAILED; skipping this pool and continuing",
                    idx,
                    recipePools.length,
                    poolName,
                    t);
            }
            if (loaded) {
                OTHTechnology.LOG.info("RecipeLoader: [{}/{}] {} done.", idx, recipePools.length, poolName);
            }
        }

    }

}
