package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.Utils.Utils.setStackSize;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.newmaa.othtech.Utils.RecipeBuilder;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;

public class Mixer implements IRecipePool {

    @Override
    public void loadRecipes() {
        Fluid Ne = FluidRegistry.getFluid("neon");
        Fluid I = FluidRegistry.getFluid("iodine");
        Fluid Shit = FluidRegistry.getFluid("molten.arcanite");

        final RecipeMap<?> LMixer = GTPPRecipeMaps.mixerNonCellRecipes;
        GTValues.RA.stdBuilder()
            .fluidInputs(new FluidStack(Ne, 1000), new FluidStack(I, 1000))
            .fluidOutputs(new FluidStack(Shit, 144))
            .itemOutputs(GTModHandler.getModItem("123Technology", "LookNEIdust", 1))
            .noOptimize()
            .duration(200)
            .eut(6144)
            .addTo(LMixer);
        // GTValues.RA.stdBuilder()
        // .itemInputs(
        // GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tin, 1),
        // GTOreDictUnificator.get(OrePrefixes.dust, Materials.Arsenic, 1),
        // GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Fireclay, 4))
        // .itemOutputs(GTModHandler.getModItem("123Technology", "dustSnAs", 1))
        // .noOptimize()
        // .duration(200)
        // .eut(30)
        // .addTo(LMixer);
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 5, 10101))
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tin, 20),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 20),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Glowstone, 30),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silver, 8),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Phosphorus, 40),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 10),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 5))
            .noOptimize()
            .duration(400)
            .eut(123123)
            .addTo(LMixer);
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 10096), 18),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cerium, 1))
            .itemOutputs(setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 10097), 19))
            .fluidInputs(FluidRegistry.getFluidStack("molten.lithium chloride", 144))
            .noOptimize()
            .duration(30 * 20)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(LMixer);
    }
}
