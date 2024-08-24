package com.newmaa.othtech.recipe;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.GT_Values;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.util.GT_Utility;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;

public class Mixer implements IRecipePool {

    @Override
    public void loadRecipes() {
        Fluid Ne = FluidRegistry.getFluid("neon");
        Fluid I = FluidRegistry.getFluid("iodine");
        Fluid Shit = FluidRegistry.getFluid("molten.arcanite");

        final IRecipeMap LMixer = GTPPRecipeMaps.mixerNonCellRecipes;
        GT_Values.RA.stdBuilder()
            .fluidInputs(new FluidStack(Ne, 1000), new FluidStack(I, 1000))
            .fluidOutputs(new FluidStack(Shit, 144))
            .itemOutputs(GT_ModHandler.getModItem("123Technology", "LookNEIdust", 1))
            .noOptimize()
            .duration(200)
            .eut(6144)
            .addTo(LMixer);
        // GT_Values.RA.stdBuilder()
        // .itemInputs(
        // GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Tin, 1),
        // GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Arsenic, 1),
        // GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Fireclay, 4))
        // .itemOutputs(GT_ModHandler.getModItem("123Technology", "dustSnAs", 1))
        // .noOptimize()
        // .duration(200)
        // .eut(30)
        // .addTo(LMixer);
        GT_Values.RA.stdBuilder()
            .itemOutputs(GT_ModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 5, 10101))
            .itemInputs(
                GT_Utility.getIntegratedCircuit(17),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Tin, 20),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 20),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Glowstone, 30),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 2),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Silver, 8),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Phosphorus, 40),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 10),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 5))
            .noOptimize()
            .duration(400)
            .eut(123123)
            .addTo(LMixer);
    }
}
