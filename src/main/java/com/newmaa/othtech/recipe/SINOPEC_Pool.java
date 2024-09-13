package com.newmaa.othtech.recipe;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.newmaa.othtech.common.recipemap.Recipemaps;

import gregtech.api.enums.GT_Values;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.util.GT_Utility;

public class SINOPEC_Pool implements IRecipePool {

    @Override
    public void loadRecipes() {
        final RecipeMap<?> SINOPEC = Recipemaps.SINOPEC;
        Fluid gas = FluidRegistry.getFluid("gas_gas");
        Fluid nap = FluidRegistry.getFluid("liquid_naphtha");
        Fluid lig = FluidRegistry.getFluid("liquid_light_fuel");
        Fluid hea = FluidRegistry.getFluid("liquid_heavy_fuel");
        Fluid naphthenicacid = FluidRegistry.getFluid("naphthenicacid");
        Fluid C7H8 = FluidRegistry.getFluid("liquid_toluene");
        Fluid C6H6 = FluidRegistry.getFluid("benzene");
        Fluid C4H8 = FluidRegistry.getFluid("butene");
        Fluid C4H6 = FluidRegistry.getFluid("butadiene");
        Fluid C3H8 = FluidRegistry.getFluid("propane");
        Fluid C3H6 = FluidRegistry.getFluid("propene");
        Fluid C2H6 = FluidRegistry.getFluid("ethane");
        Fluid C2H4 = FluidRegistry.getFluid("ethylene");
        Fluid CH4 = FluidRegistry.getFluid("methane");
        Fluid steam = FluidRegistry.getFluid("water");
        Fluid He = FluidRegistry.getFluid("helium");
        // oils
        GT_Values.RA.stdBuilder()
            .fluidOutputs(
                new FluidStack(gas, 144000),
                new FluidStack(nap, 360000),
                new FluidStack(naphthenicacid, 6000),
                new FluidStack(lig, 120000),
                new FluidStack(hea, 24000))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("liquid_medium_oil"), 240000))
            .itemInputs(GT_Utility.getIntegratedCircuit(24))
            .itemOutputs(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 52))
            .noOptimize()
            .duration(3600)
            .eut(TierEU.EV)
            .addTo(SINOPEC);
        GT_Values.RA.stdBuilder()
            .fluidOutputs(
                new FluidStack(gas, 288000),
                new FluidStack(nap, 96000),
                new FluidStack(naphthenicacid, 12000),
                new FluidStack(lig, 240000),
                new FluidStack(hea, 72000))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("oil"), 240000))
            .itemInputs(GT_Utility.getIntegratedCircuit(24))
            .itemOutputs(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 55))
            .noOptimize()
            .duration(3600)
            .eut(TierEU.EV)
            .addTo(SINOPEC);
        GT_Values.RA.stdBuilder()
            .fluidOutputs(
                new FluidStack(gas, 576000),
                new FluidStack(nap, 72000),
                new FluidStack(naphthenicacid, 6000),
                new FluidStack(lig, 48000),
                new FluidStack(hea, 24000))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("liquid_light_oil"), 360000))
            .itemInputs(GT_Utility.getIntegratedCircuit(24))
            .itemOutputs(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 49))
            .noOptimize()
            .duration(3600)
            .eut(TierEU.EV)
            .addTo(SINOPEC);
        GT_Values.RA.stdBuilder()
            .fluidOutputs(
                new FluidStack(gas, 144000),
                new FluidStack(nap, 36000),
                new FluidStack(naphthenicacid, 12000),
                new FluidStack(lig, 108000),
                new FluidStack(hea, 600000))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("liquid_heavy_oil"), 24000))
            .itemInputs(GT_Utility.getIntegratedCircuit(24))
            .itemOutputs(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 96))
            .noOptimize()
            .duration(3600)
            .eut(TierEU.EV)
            .addTo(SINOPEC);
        GT_Values.RA.stdBuilder()
            .fluidInputs(new FluidStack(steam, 50), new FluidStack(hea, 5000))
            .fluidOutputs(
                new FluidStack(lig, 2000),
                new FluidStack(nap, 2000),
                new FluidStack(C7H8, 400),
                new FluidStack(C6H6, 2000),
                new FluidStack(C4H8, 400),
                new FluidStack(C4H6, 250),
                new FluidStack(C3H8, 50),
                new FluidStack(C3H6, 500),
                new FluidStack(C2H6, 70),
                new FluidStack(C2H4, 750),
                new FluidStack(CH4, 750))
            .itemInputs(GT_Utility.getIntegratedCircuit(24))
            .itemOutputs(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1))
            .noOptimize()
            .duration(240)
            .eut(TierEU.EV)
            .addTo(SINOPEC);
        GT_Values.RA.stdBuilder()
            .fluidInputs(new FluidStack(steam, 50), new FluidStack(lig, 5000))
            .fluidOutputs(
                new FluidStack(hea, 1000),
                new FluidStack(nap, 2500),
                new FluidStack(C7H8, 500),
                new FluidStack(C6H6, 3000),
                new FluidStack(C4H8, 900),
                new FluidStack(C4H6, 750),
                new FluidStack(C3H8, 350),
                new FluidStack(C3H6, 2000),
                new FluidStack(C2H6, 300),
                new FluidStack(C2H4, 1500),
                new FluidStack(CH4, 1500))
            .itemInputs(GT_Utility.getIntegratedCircuit(24))
            .itemOutputs(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1))
            .noOptimize()
            .duration(240)
            .eut(TierEU.EV)
            .addTo(SINOPEC);
        GT_Values.RA.stdBuilder()
            .fluidInputs(new FluidStack(steam, 50), new FluidStack(gas, 5000))
            .fluidOutputs(
                new FluidStack(C3H6, 100),
                new FluidStack(C2H6, 500),
                new FluidStack(C2H4, 2000),
                new FluidStack(CH4, 6000),
                new FluidStack(He, 700))
            .itemInputs(GT_Utility.getIntegratedCircuit(24))
            .itemOutputs(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1))
            .noOptimize()
            .duration(240)
            .eut(TierEU.EV)
            .addTo(SINOPEC);
        GT_Values.RA.stdBuilder()
            .fluidInputs(new FluidStack(steam, 50), new FluidStack(nap, 5000))
            .fluidOutputs(
                new FluidStack(hea, 500),
                new FluidStack(lig, 1000),
                new FluidStack(C7H8, 300),
                new FluidStack(C6H6, 1250),
                new FluidStack(C4H8, 650),
                new FluidStack(C4H6, 1000),
                new FluidStack(C3H8, 300),
                new FluidStack(C3H6, 4000),
                new FluidStack(C2H6, 500),
                new FluidStack(C2H4, 3500),
                new FluidStack(CH4, 3500))
            .itemInputs(GT_Utility.getIntegratedCircuit(24))
            .itemOutputs(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1))
            .noOptimize()
            .duration(240)
            .eut(TierEU.EV)
            .addTo(SINOPEC);
        // radoX
        GT_Values.RA.stdBuilder()
            .fluidInputs(FluidRegistry.getFluidStack("rawradox", 1000000), Materials.Silver.getPlasma(2299))
            .fluidOutputs(
                FluidRegistry.getFluidStack("aceticacid", 250),
                FluidRegistry.getFluidStack("crackedradox", 134500),
                FluidRegistry.getFluidStack("carbondioxide", 4000),
                FluidRegistry.getFluidStack("oil", 60000),
                FluidRegistry.getFluidStack("mutagen", 1000),
                FluidRegistry.getFluidStack("creosote", 200000),
                FluidRegistry.getFluidStack("superheavyradox", 20000),
                FluidRegistry.getFluidStack("ammonia", 1000),
                FluidRegistry.getFluidStack("methanol", 1500),
                new FluidStack(steam, 283750),
                new FluidStack(CH4, 6000),
                FluidRegistry.getFluidStack("bioethanol", 1500),
                FluidRegistry.getFluidStack("liquid_heavy_oil", 120000),
                FluidRegistry.getFluidStack("xeonxene", 2500))
            .itemOutputs(GT_ModHandler.getModItem("IC2", "itemFertilizer", 10))
            .itemInputs(GT_Utility.getIntegratedCircuit(24))
            .noOptimize()
            .duration(3600 * 20)
            .eut(TierEU.UHV)
            .addTo(SINOPEC);
        GT_Values.RA.stdBuilder()
            .fluidInputs(FluidRegistry.getFluidStack("rawradox", 1000000), Materials.Silver.getPlasma(1945))
            .fluidOutputs(
                FluidRegistry.getFluidStack("aceticacid", 250),
                FluidRegistry.getFluidStack("crackedradox", 131500),
                FluidRegistry.getFluidStack("heavyradox", 30000),
                FluidRegistry.getFluidStack("carbondioxide", 4000),
                FluidRegistry.getFluidStack("oil", 60000),
                FluidRegistry.getFluidStack("mutagen", 1000),
                FluidRegistry.getFluidStack("creosote", 200000),
                FluidRegistry.getFluidStack("superheavyradox", 20000),
                FluidRegistry.getFluidStack("ammonia", 1000),
                FluidRegistry.getFluidStack("methanol", 1500),
                new FluidStack(steam, 283750),
                new FluidStack(CH4, 6000),
                FluidRegistry.getFluidStack("bioethanol", 1500),
                FluidRegistry.getFluidStack("liquid_heavy_oil", 120000),
                FluidRegistry.getFluidStack("xeonxene", 2500))
            .itemOutputs(GT_ModHandler.getModItem("IC2", "itemFertilizer", 10))
            .itemInputs(GT_Utility.getIntegratedCircuit(1))
            .noOptimize()
            .duration(3600 * 20)
            .eut(TierEU.UHV)
            .addTo(SINOPEC);
        // naquadah fuels
        GT_Values.RA.stdBuilder()
            .fluidInputs(
                FluidRegistry.getFluidStack("naquadah solution", 20000),
                FluidRegistry.getFluidStack("molten.atomic separation catalyst", 264)

            )
            .fluidOutputs(
                FluidRegistry.getFluidStack("heavy naquadah fuel", 5490),
                FluidRegistry.getFluidStack("light naquadah fuel", 11680),
                new FluidStack(steam, 10000),
                FluidRegistry.getFluidStack("neon", 3720),
                FluidRegistry.getFluidStack("helium", 25200),
                FluidRegistry.getFluidStack("argon", 5760),
                FluidRegistry.getFluidStack("xenon", 1800),
                FluidRegistry.getFluidStack("krypton", 960),
                FluidRegistry.getFluidStack("radon", 588000),
                FluidRegistry.getFluidStack("molten.plutonium", 1266),
                FluidRegistry.getFluidStack("molten.thulium", 324),
                FluidRegistry.getFluidStack("molten.thorium", 2072),
                FluidRegistry.getFluidStack("molten.uranium", 2880))
            .itemOutputs(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Naquadah, 8))
            .itemInputs(GT_Utility.getIntegratedCircuit(24))
            .noOptimize()
            .duration(240 * 20)
            .eut(TierEU.UHV)
            .addTo(SINOPEC);
    }

}
