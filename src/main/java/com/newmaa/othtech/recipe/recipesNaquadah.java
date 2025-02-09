package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.Utils.Utils.setStackSize;
import static com.newmaa.othtech.common.recipemap.Recipemaps.NQF;
import static gregtech.api.util.GTRecipeConstants.NFR_COIL_TIER;

import com.newmaa.othtech.Utils.RecipeBuilder;
import net.minecraftforge.fluids.FluidRegistry;

import bartworks.system.material.WerkstoffLoader;
import goodgenerator.api.recipe.GoodGeneratorRecipeMaps;
import goodgenerator.items.GGMaterial;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.material.MaterialsElements;

public class recipesNaquadah implements IRecipePool {

    public void loadRecipes() {
        RecipeBuilder.builder()
            .fluidOutputs(FluidRegistry.getFluidStack("naquadah based liquid fuel mkv", 4000))
            .fluidInputs(
                FluidRegistry.getFluidStack("heavy naquadah fuel", 512000),
                FluidRegistry.getFluidStack("light naquadah fuel", 640000),
                FluidRegistry.getFluidStack("heavyradox", 1000),
                Materials.Praseodymium.getFluid(294912))
            .itemInputs(
                setStackSize(GGMaterial.plutoniumOxideUraniumMixture.get(OrePrefixes.dust, 1), 12800),
                setStackSize(GGMaterial.graphiteUraniumMixture.get(OrePrefixes.dust, 1), 102400),
                setStackSize(GGMaterial.extremelyUnstableNaquadah.get(OrePrefixes.dust, 1), 3424),
                setStackSize(GGMaterial.atomicSeparationCatalyst.get(OrePrefixes.dust, 1), 128),
                setStackSize(WerkstoffLoader.Tiberium.get(OrePrefixes.dust, 1), 17280),
                setStackSize(GGMaterial.orundum.get(OrePrefixes.dust, 1), 1024),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.DraconiumAwakened, 1), 2048),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Infinity, 1), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.NetherStar, 1), 2048))
            .eut(TierEU.RECIPE_UMV)
            .duration(150 * 20)
            .noOptimize()
            .addTo(NQF);
        RecipeBuilder.builder()
            .fluidOutputs(FluidRegistry.getFluidStack("naquadah based liquid fuel mkv", 6000))
            .fluidInputs(
                FluidRegistry.getFluidStack("heavy naquadah fuel", 128000),
                FluidRegistry.getFluidStack("light naquadah fuel", 160000),
                FluidRegistry.getFluidStack("molten.mellion", 1152),
                FluidRegistry.getFluidStack("molten.creon", 1152),
                FluidRegistry.getFluidStack("molten.hypogen", 3840))
            .itemInputs(
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 10009), 12800),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 10024), 1712),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 10022), 192),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 10023), 1024),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.DraconiumAwakened, 1), 1024),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, MaterialsUEVplus.TranscendentMetal, 1), 64),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bedrockium, 1), 1024),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 10001), 25600),
                setStackSize(GTModHandler.getModItem("miscutils", "itemDustChromaticGlass", 1), 1440))
            .eut(TierEU.RECIPE_UMV)
            .duration(100 * 20)
            .noOptimize()
            .addTo(NQF);
        RecipeBuilder.builder()
            .fluidOutputs(FluidRegistry.getFluidStack("naquadah based liquid fuel mkvi", 4500))
            .fluidInputs(
                FluidRegistry.getFluidStack("heavy naquadah fuel", 128000),
                FluidRegistry.getFluidStack("light naquadah fuel", 160000),
                FluidRegistry.getFluidStack("molten.mellion", 1152),
                FluidRegistry.getFluidStack("molten.creon", 1152),
                GGMaterial.shirabon.getMolten(1440))
            .itemInputs(
                setStackSize(GGMaterial.plutoniumOxideUraniumMixture.get(OrePrefixes.dust, 1), 12800),
                setStackSize(GGMaterial.graphiteUraniumMixture.get(OrePrefixes.dust, 1), 25600),
                setStackSize(GGMaterial.extremelyUnstableNaquadah.get(OrePrefixes.dust, 1), 1712),
                setStackSize(GGMaterial.atomicSeparationCatalyst.get(OrePrefixes.dust, 1), 192),
                setStackSize(GGMaterial.orundum.get(OrePrefixes.dust, 1), 1024),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.DraconiumAwakened, 1), 1024),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, MaterialsUEVplus.TranscendentMetal, 1), 64),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bedrockium, 1), 1024),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tritanium, 1), 32 * 3),
                setStackSize(MaterialsElements.STANDALONE.ASTRAL_TITANIUM.getDust(1), 192),
                setStackSize(MaterialsElements.STANDALONE.CHRONOMATIC_GLASS.getDust(1), 1440))
            .eut(TierEU.RECIPE_UXV)
            .duration(150 * 20)
            .noOptimize()
            .addTo(NQF);
        RecipeBuilder.builder()
            .fluidOutputs(FluidRegistry.getFluidStack("naquadah based liquid fuel mkvi", 7500))
            .fluidInputs(
                FluidRegistry.getFluidStack("heavy naquadah fuel", 128000),
                FluidRegistry.getFluidStack("light naquadah fuel", 160000),
                FluidRegistry.getFluidStack("rawstarmatter", 400),
                GGMaterial.shirabon.getMolten(1440))
            .itemInputs(
                setStackSize(GGMaterial.plutoniumOxideUraniumMixture.get(OrePrefixes.dust, 1), 12800),
                setStackSize(GGMaterial.graphiteUraniumMixture.get(OrePrefixes.dust, 1), 25600),
                setStackSize(GGMaterial.extremelyUnstableNaquadah.get(OrePrefixes.dust, 1), 1712),
                setStackSize(GGMaterial.atomicSeparationCatalyst.get(OrePrefixes.dust, 1), 192),
                setStackSize(GGMaterial.orundum.get(OrePrefixes.dust, 1), 1024),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.DraconiumAwakened, 1), 1024),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, MaterialsUEVplus.TranscendentMetal, 1), 64),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bedrockium, 1), 1024),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tritanium, 1), 48 * 3),
                setStackSize(MaterialsElements.STANDALONE.CELESTIAL_TUNGSTEN.getDust(1), 192),
                setStackSize(MaterialsElements.STANDALONE.CHRONOMATIC_GLASS.getDust(1), 1440))
            .eut(TierEU.RECIPE_UXV)
            .duration(100 * 20)
            .noOptimize()
            .addTo(NQF);
    }
}
