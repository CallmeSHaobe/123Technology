package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.Config.is_EggMachine_Recipes_For_NHU;

import net.minecraftforge.fluids.FluidRegistry;

import com.newmaa.othtech.OTHTechnology;
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
            new RecipesEXH(), new RecipesNewWetware(), new RecipesAntimonia(), new RecipesNASA(), new RecipesOTHOre(),
            new RecipesDeepSeiko() };
        OTHTechnology.LOG.info("RecipeLoader: Loading OTEMiniSteamNineInOne recipes...");
        OTEMiniSteamNineInOne.loadRecipes();
        OTHTechnology.LOG.info("RecipeLoader: OTEMiniSteamNineInOne done.");
        if (!modsEnum.TwistSpaceTechnology.isModLoaded()) {
            OTHTechnology.LOG.info("RecipeLoader: Loading RecipesCopiedFromTST...");
            new RecipesCopiedFromTST().loadRecipes();
            OTHTechnology.LOG.info("RecipeLoader: RecipesCopiedFromTST done.");
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
        int idx = 0;
        for (IRecipePool recipePool : recipePools) {
            String poolName = recipePool.getClass()
                .getSimpleName();
            OTHTechnology.LOG.info("RecipeLoader: [{}/{}] Loading {}...", ++idx, recipePools.length, poolName);
            // 单个配方池抛异常绝不能让整个 completeInit 挂掉：
            // 2026-10-26 14:26 迪普西科的一条 NEI 展示配方就抛了 IllegalArgumentException，
            // 结果 FML 记为致命错误 ⇒ 所有存档进不去 / 新建世界失败。这里兜底，出错只跳过该池。
            try {
                recipePool.loadRecipes();
            } catch (Throwable t) {
                OTHTechnology.LOG
                    .error("RecipeLoader: [{}/{}] {} FAILED（已跳过该配方池，继续加载其它）", idx, recipePools.length, poolName, t);
            }
            OTHTechnology.LOG.info("RecipeLoader: [{}/{}] {} done.", idx, recipePools.length, poolName);
        }

    }

}
