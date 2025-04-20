package com.newmaa.othtech.recipe;

import net.minecraftforge.fluids.FluidRegistry;

import com.newmaa.othtech.OTHTechnology;
import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.materials.liquids;
import com.newmaa.othtech.utils.RecipeBuilder;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeBuilder;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;

public class recipesAntimonia implements IRecipePool {

    public void loadRecipes() {
        GTRecipeBuilder.builder()
            .itemInputs(OTHItemList.AntimoniaPlanetDust.get(48))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Antimony, 27),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Niobium, 18),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 6),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Boron, 4),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bismuth, 4))
            .fluidOutputs(
                FluidRegistry.getFluidStack("aquaregia", 400),
                liquids.HCl10.getFluidOrGas(10000),
                liquids.HCl100.getFluidOrGas(1000),
                liquids.HCl1000.getFluidOrGas(100),
                liquids.HCl10000.getFluidOrGas(10),
                liquids.HCl100000.getFluidOrGas(1))
            .outputChances(9980, 8800, 4400, 3200, 4200, 5000)
            .eut(250250)
            .duration(123 * 20)
            .addTo(RecipeMaps.centrifugeRecipes);
        GTRecipeBuilder.builder()
            .itemInputs(OTHItemList.AntimoniaPlanetDust.get(48))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Antimony, 27),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Niobium, 18),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 6),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Boron, 4),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bismuth, 4))
            .fluidOutputs(
                FluidRegistry.getFluidStack("aquaregia", 400),
                liquids.HCl10.getFluidOrGas(10000),
                liquids.HCl100.getFluidOrGas(1000),
                liquids.HCl1000.getFluidOrGas(100),
                liquids.HCl10000.getFluidOrGas(10),
                liquids.HCl100000.getFluidOrGas(1))
            .outputChances(9980, 8800, 4400, 3200, 4200, 5000)
            .eut(250250)
            .duration(123 * 20)
            .addTo(GTPPRecipeMaps.centrifugeNonCellRecipes);
        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem(OTHTechnology.MODID, "antimoniaDirtBlock", 1))
            .itemOutputs(
                OTHItemList.AntimoniaPlanetDust.get(1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Antimony, 3))
            .outputChances(10000, 3333)
            .eut(250)
            .duration(100)
            .addTo(RecipeMaps.maceratorRecipes);
        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem(OTHTechnology.MODID, "antimoniaGrassBlock", 1))
            .itemOutputs(
                OTHItemList.AntimoniaPlanetDust.get(1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Antimony, 3))
            .outputChances(10000, 3333)
            .eut(250)
            .duration(100)
            .addTo(RecipeMaps.maceratorRecipes);
        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem(OTHTechnology.MODID, "antimoniaStoneBlock", 1))
            .itemOutputs(
                OTHItemList.AntimoniaPlanetDust.get(1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Antimony, 3))
            .outputChances(10000, 3333)
            .eut(250)
            .duration(100)
            .addTo(RecipeMaps.maceratorRecipes);
    }
}
