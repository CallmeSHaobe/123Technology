package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.utils.Utils.setStackSize;
import static gregtech.api.enums.TierEU.RECIPE_HV;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.utils.RecipeBuilder;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

public class RecipesSINOPEC implements IRecipePool {

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
        GTValues.RA.stdBuilder()
            .fluidOutputs(
                new FluidStack(gas, 144000),
                new FluidStack(nap, 360000),
                new FluidStack(naphthenicacid, 6000),
                new FluidStack(lig, 120000),
                new FluidStack(hea, 24000))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("liquid_medium_oil"), 240000))
            .itemInputs(GTUtility.getIntegratedCircuit(24))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 52))
            .noOptimize()
            .duration(3600)
            .eut(TierEU.EV)
            .addTo(SINOPEC);
        GTValues.RA.stdBuilder()
            .fluidOutputs(
                new FluidStack(gas, 288000),
                new FluidStack(nap, 96000),
                new FluidStack(naphthenicacid, 12000),
                new FluidStack(lig, 240000),
                new FluidStack(hea, 72000))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("oil"), 240000))
            .itemInputs(GTUtility.getIntegratedCircuit(24))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 55))
            .noOptimize()
            .duration(3600)
            .eut(TierEU.EV)
            .addTo(SINOPEC);
        GTValues.RA.stdBuilder()
            .fluidOutputs(
                new FluidStack(gas, 576000),
                new FluidStack(nap, 72000),
                new FluidStack(naphthenicacid, 6000),
                new FluidStack(lig, 48000),
                new FluidStack(hea, 24000))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("liquid_light_oil"), 360000))
            .itemInputs(GTUtility.getIntegratedCircuit(24))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 49))
            .noOptimize()
            .duration(3600)
            .eut(TierEU.EV)
            .addTo(SINOPEC);
        GTValues.RA.stdBuilder()
            .fluidOutputs(
                new FluidStack(gas, 144000),
                new FluidStack(nap, 36000),
                new FluidStack(naphthenicacid, 12000),
                new FluidStack(lig, 108000),
                new FluidStack(hea, 600000))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("liquid_heavy_oil"), 24000))
            .itemInputs(GTUtility.getIntegratedCircuit(24))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 96))
            .noOptimize()
            .duration(3600)
            .eut(TierEU.EV)
            .addTo(SINOPEC);
        GTValues.RA.stdBuilder()
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
            .itemInputs(GTUtility.getIntegratedCircuit(24))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1))
            .noOptimize()
            .duration(240)
            .eut(TierEU.EV)
            .addTo(SINOPEC);
        GTValues.RA.stdBuilder()
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
            .itemInputs(GTUtility.getIntegratedCircuit(24))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1))
            .noOptimize()
            .duration(240)
            .eut(TierEU.EV)
            .addTo(SINOPEC);
        GTValues.RA.stdBuilder()
            .fluidInputs(new FluidStack(steam, 50), new FluidStack(gas, 5000))
            .fluidOutputs(
                new FluidStack(C3H6, 100),
                new FluidStack(C2H6, 500),
                new FluidStack(C2H4, 2000),
                new FluidStack(CH4, 6000),
                new FluidStack(He, 700))
            .itemInputs(GTUtility.getIntegratedCircuit(24))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1))
            .noOptimize()
            .duration(240)
            .eut(TierEU.EV)
            .addTo(SINOPEC);
        GTValues.RA.stdBuilder()
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
            .itemInputs(GTUtility.getIntegratedCircuit(24))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1))
            .noOptimize()
            .duration(240)
            .eut(TierEU.EV)
            .addTo(SINOPEC);
        // radoX
        GTValues.RA.stdBuilder()
            .fluidInputs(FluidRegistry.getFluidStack("rawradox", 1000000), Materials.Silver.getPlasma(2560))
            .fluidOutputs(
                FluidRegistry.getFluidStack("aceticacid", 250),
                FluidRegistry.getFluidStack("radoxgas", 14795),
                FluidRegistry.getFluidStack("lightradox", 2690),
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
                FluidRegistry.getFluidStack("xenoxene", 2500))
            .itemOutputs(GTModHandler.getModItem("IC2", "itemFertilizer", 10))
            .itemInputs(GTUtility.getIntegratedCircuit(24))
            .noOptimize()
            .duration(3600 * 20)
            .eut(TierEU.UHV)
            .addTo(SINOPEC);
        GTValues.RA.stdBuilder()
            .fluidInputs(FluidRegistry.getFluidStack("rawradox", 1000000), Materials.Silver.getPlasma(2560))
            .fluidOutputs(
                FluidRegistry.getFluidStack("aceticacid", 250),
                FluidRegistry.getFluidStack("radoxgas", 14465),
                FluidRegistry.getFluidStack("lightradox", 2630),
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
                FluidRegistry.getFluidStack("xenoxene", 2500))
            .itemOutputs(GTModHandler.getModItem("IC2", "itemFertilizer", 10))
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .noOptimize()
            .duration(3600 * 20)
            .eut(TierEU.UHV)
            .addTo(SINOPEC);
        // naquadah fuels
        GTValues.RA.stdBuilder()
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
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadah, 8))
            .itemInputs(GTUtility.getIntegratedCircuit(24))
            .noOptimize()
            .duration(1800 * 20)
            .eut(TierEU.UHV)
            .addTo(SINOPEC);
        // Bios
        GTValues.RA.stdBuilder()
            .fluidInputs(FluidRegistry.getFluidStack("water", 123750))
            .fluidOutputs(
                FluidRegistry.getFluidStack("ammonia", 19800),
                FluidRegistry.getFluidStack("aceticacid", 4950),
                FluidRegistry.getFluidStack("methanol", 29700),
                FluidRegistry.getFluidStack("methane", 118800),
                FluidRegistry.getFluidStack("bioethanol", 29700),
                FluidRegistry.getFluidStack("carbondioxide", 79200))
            .itemOutputs(setStackSize(GTModHandler.getModItem("IC2", "itemFertilizer", 1), 198))
            .itemInputs(
                GTUtility.getIntegratedCircuit(24),
                setStackSize(GTModHandler.getModItem("IC2", "itemFuelPlantBall", 1), 96))
            .noOptimize()
            .duration(180 * 20)
            .eut(TierEU.HV)
            .addTo(SINOPEC);

        RecipeBuilder.builder()
            .fluidInputs(FluidRegistry.getFluidStack("sulfuricacid", 24000))
            .fluidOutputs(
                FluidRegistry.getFluidStack("aceticacid", 12800),
                new FluidStack(C2H4, 9600),
                FluidRegistry.getFluidStack("carbondioxide", 31200),
                new FluidStack(C6H6, 32000),
                FluidRegistry.getFluidStack("carbonmonoxide", 19200),
                FluidRegistry.getFluidStack("fluid.ethylbenzene", 8000),
                FluidRegistry.getFluidStack("hydrogen", 9600),
                new FluidStack(C7H8, 8000),
                FluidRegistry.getFluidStack("methanol", 38400),
                new FluidStack(steam, 64000),
                new FluidStack(nap, 6000),
                new FluidStack(CH4, 10400),
                FluidRegistry.getFluidStack("fluid.kerosene", 24000),
                FluidRegistry.getFluidStack("phenol", 8000),
                FluidRegistry.getFluidStack("methylacetate", 1280),
                FluidRegistry.getFluidStack("creosote", 20000),
                FluidRegistry.getFluidStack("fluid.naphthalene", 48000),
                FluidRegistry.getFluidStack("dimethylbenzene", 44000),
                FluidRegistry.getFluidStack("bioethanol", 29700),
                FluidRegistry.getFluidStack("fluid.anthracene", 2000),
                FluidRegistry.getFluidStack("acetone", 6400))
            .itemInputs(
                GTUtility.getIntegratedCircuit(24),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.log, Materials.Wood, 1), 1280))
            .noOptimize()
            .duration(360 * 20)
            .eut(TierEU.HV)
            .addTo(SINOPEC);

        RecipeBuilder.builder()
            .fluidInputs(FluidRegistry.getFluidStack("charcoal_byproducts", 640000))
            .fluidOutputs(
                FluidRegistry.getFluidStack("aceticacid", 25600),
                new FluidStack(C2H4, 19200),
                FluidRegistry.getFluidStack("carbondioxide", 62400),
                new FluidStack(C6H6, 64000),
                FluidRegistry.getFluidStack("carbonmonoxide", 38400),
                FluidRegistry.getFluidStack("hydrogen", 192000),
                new FluidStack(C7H8, 16000),
                FluidRegistry.getFluidStack("methanol", 76800),
                new FluidStack(steam, 128000),
                new FluidStack(CH4, 20800),
                FluidRegistry.getFluidStack("phenol", 16000),
                FluidRegistry.getFluidStack("methylacetate", 1280),
                FluidRegistry.getFluidStack("creosote", 40000),
                FluidRegistry.getFluidStack("dimethylbenzene", 88000),
                FluidRegistry.getFluidStack("bioethanol", 2560),
                FluidRegistry.getFluidStack("acetone", 12800))
            .noOptimize()
            .duration(720 * 20)
            .eut(TierEU.HV)
            .addTo(SINOPEC);

        // Octane
        RecipeBuilder.builder()
            .fluidOutputs(
                new FluidStack(CH4, 64 * 125),
                FluidRegistry.getFluidStack("butane", 64 * 150),
                new FluidStack(C3H8, 64 * 200),
                new FluidStack(C2H6, 64 * 125),
                FluidRegistry.getFluidStack("octane", 64 * 100),
                new FluidStack(nap, 64 * 800))
            .fluidInputs(new FluidStack(lig, 64 * 1000), Materials.Hydrogen.getGas(64 * 1600))
            .noOptimize()
            .eut(RECIPE_HV)
            .duration(200)
            .addTo(SINOPEC);
    }

}
