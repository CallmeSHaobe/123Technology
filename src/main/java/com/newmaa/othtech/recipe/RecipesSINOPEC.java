package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.utils.Utils.setStackSize;
import static gregtech.api.enums.TierEU.RECIPE_HV;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.utils.RecipeFluids;

import bartworks.system.material.WerkstoffLoader;
import goodgenerator.items.GGMaterial;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
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
        Fluid gas = FluidRegistry.getFluid(RecipeFluids.registryName(Materials.Gas.mGas));
        Fluid nap = FluidRegistry.getFluid(RecipeFluids.registryName(Materials.Naphtha.mFluid));
        Fluid lig = FluidRegistry.getFluid(RecipeFluids.registryName(Materials.LightFuel.mFluid));
        Fluid hea = FluidRegistry.getFluid(RecipeFluids.registryName(Materials.HeavyFuel.mFluid));
        Fluid naphthenicacid = FluidRegistry.getFluid(RecipeFluids.registryName(Materials.NaphthenicAcid.mFluid));
        Fluid C7H8 = FluidRegistry.getFluid(RecipeFluids.registryName(Materials.Toluene.mFluid));
        Fluid C6H6 = FluidRegistry.getFluid(RecipeFluids.registryName(Materials.Benzene.mFluid));
        Fluid C4H8 = FluidRegistry.getFluid(RecipeFluids.registryName(Materials.Butene.mGas));
        Fluid C4H6 = FluidRegistry.getFluid(RecipeFluids.registryName(Materials.Butadiene.mGas));
        Fluid C3H8 = FluidRegistry.getFluid(RecipeFluids.registryName(Materials.Propane.mGas));
        Fluid C3H6 = FluidRegistry.getFluid(RecipeFluids.registryName(Materials.Propene.mGas));
        Fluid C2H6 = FluidRegistry.getFluid(RecipeFluids.registryName(Materials.Ethane.mGas));
        Fluid C2H4 = FluidRegistry.getFluid(RecipeFluids.registryName(Materials.Ethylene.mGas));
        Fluid CH4 = FluidRegistry.getFluid(RecipeFluids.registryName(Materials.Methane.mGas));
        Fluid steam = FluidRegistry.getFluid(FluidRegistry.WATER.getName());
        Fluid He = FluidRegistry.getFluid(RecipeFluids.registryName(Materials.Helium.mGas));
        // oils
        GTValues.RA.stdBuilder()
            .fluidOutputs(
                new FluidStack(gas, 144000),
                new FluidStack(nap, 360000),
                new FluidStack(naphthenicacid, 6000),
                new FluidStack(lig, 120000),
                new FluidStack(hea, 24000))
            .fluidInputs(
                new FluidStack(FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.OilMedium.mFluid)), 240000))
            .itemInputs(GTUtility.getIntegratedCircuit(24))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 52))

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
            .fluidInputs(
                new FluidStack(FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.Oil.mFluid)), 240000))
            .itemInputs(GTUtility.getIntegratedCircuit(24))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 55))

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
            .itemInputs(
                GTUtility.getIntegratedCircuit(24),
                setStackSize(GTModHandler.getModItem("cropsnh", "materialLeaf", 1, 10), 2400))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 55))

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
            .fluidInputs(
                new FluidStack(FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.OilLight.mFluid)), 360000))
            .itemInputs(GTUtility.getIntegratedCircuit(24))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 49))

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
            .fluidInputs(
                new FluidStack(FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.OilHeavy.mFluid)), 240000))
            .itemInputs(GTUtility.getIntegratedCircuit(24))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 96))

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

            .duration(240)
            .eut(TierEU.EV)
            .addTo(SINOPEC);
        // radoX
        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.RadoxRaw.getFluid(1000000), Materials.Silver.getPlasma(2560))
            .fluidOutputs(
                Materials.AceticAcid.getFluid(250),
                Materials.RadoxGas.getGas(14795),
                Materials.RadoxLight.getGas(2690),
                Materials.CarbonDioxide.getGas(4000),
                FluidRegistry.getFluidStack(RecipeFluids.registryName(Materials.Oil.mFluid), 60000),
                FluidRegistry.getFluidStack("mutagen", 1000),
                Materials.Creosote.getFluid(200000),
                Materials.RadoxSuperHeavy.getFluid(20000),
                Materials.Ammonia.getGas(1000),
                Materials.Methanol.getFluid(1500),
                new FluidStack(steam, 283750),
                new FluidStack(CH4, 6000),
                FluidRegistry.getFluidStack(RecipeFluids.registryName(Materials.Ethanol.mFluid), 1500),
                Materials.OilHeavy.getFluid(120000),
                Materials.Xenoxene.getFluid(2500))
            .itemOutputs(GTModHandler.getModItem("IC2", "itemFertilizer", 10))
            .itemInputs(GTUtility.getIntegratedCircuit(24))

            .duration(3600 * 20)
            .eut(TierEU.UHV)
            .addTo(SINOPEC);
        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.RadoxRaw.getFluid(1000000), Materials.Silver.getPlasma(2560))
            .fluidOutputs(
                Materials.AceticAcid.getFluid(250),
                Materials.RadoxGas.getGas(14465),
                Materials.RadoxLight.getGas(2630),
                Materials.RadoxHeavy.getFluid(30000),
                Materials.CarbonDioxide.getGas(4000),
                FluidRegistry.getFluidStack(RecipeFluids.registryName(Materials.Oil.mFluid), 60000),
                FluidRegistry.getFluidStack("mutagen", 1000),
                Materials.Creosote.getFluid(200000),
                Materials.RadoxSuperHeavy.getFluid(20000),
                Materials.Ammonia.getGas(1000),
                Materials.Methanol.getFluid(1500),
                new FluidStack(steam, 283750),
                new FluidStack(CH4, 6000),
                FluidRegistry.getFluidStack(RecipeFluids.registryName(Materials.Ethanol.mFluid), 1500),
                Materials.OilHeavy.getFluid(120000),
                Materials.Xenoxene.getFluid(2500))
            .itemOutputs(GTModHandler.getModItem("IC2", "itemFertilizer", 10))
            .itemInputs(GTUtility.getIntegratedCircuit(1))

            .duration(3600 * 20)
            .eut(TierEU.UHV)
            .addTo(SINOPEC);
        // naquadah fuels
        GTValues.RA.stdBuilder()
            .fluidInputs(
                FluidRegistry.getFluidStack(
                    Mods.GoodGenerator.isModLoaded() ? RecipeFluids.werkstoffName(GGMaterial.naquadahSolution)
                        : "naquadah solution",
                    20000),
                FluidRegistry.getFluidStack(
                    Mods.GoodGenerator.isModLoaded()
                        ? RecipeFluids.werkstoffMoltenName(GGMaterial.atomicSeparationCatalyst)
                        : "molten.atomic separation catalyst",
                    264)

            )
            .fluidOutputs(
                FluidRegistry.getFluidStack(
                    Mods.GoodGenerator.isModLoaded() ? RecipeFluids.werkstoffName(GGMaterial.heavyNaquadahFuel)
                        : "heavy naquadah fuel",
                    5490),
                FluidRegistry.getFluidStack(
                    Mods.GoodGenerator.isModLoaded() ? RecipeFluids.werkstoffName(GGMaterial.lightNaquadahFuel)
                        : "light naquadah fuel",
                    11680),
                new FluidStack(steam, 10000),
                FluidRegistry.getFluidStack(RecipeFluids.werkstoffName(WerkstoffLoader.Neon), 3720),
                Materials.Helium.getGas(25200),
                Materials.Argon.getGas(5760),
                FluidRegistry.getFluidStack(RecipeFluids.werkstoffName(WerkstoffLoader.Xenon), 1800),
                FluidRegistry.getFluidStack(RecipeFluids.werkstoffName(WerkstoffLoader.Krypton), 960),
                Materials.Radon.getGas(588000),
                Materials.Plutonium.getMolten(1266),
                Materials.Thulium.getMolten(324),
                Materials.Thorium.getMolten(2072),
                Materials.Uranium.getMolten(2880))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadah, 8))
            .itemInputs(GTUtility.getIntegratedCircuit(24))

            .duration(1800 * 20)
            .eut(TierEU.UHV)
            .addTo(SINOPEC);
        // Bios
        GTValues.RA.stdBuilder()
            .fluidInputs(FluidRegistry.getFluidStack(FluidRegistry.WATER.getName(), 123750))
            .fluidOutputs(
                Materials.Ammonia.getGas(19800),
                Materials.AceticAcid.getFluid(4950),
                Materials.Methanol.getFluid(29700),
                Materials.Methane.getGas(118800),
                FluidRegistry.getFluidStack(RecipeFluids.registryName(Materials.Ethanol.mFluid), 29700),
                Materials.CarbonDioxide.getGas(79200))
            .itemOutputs(setStackSize(GTModHandler.getModItem("IC2", "itemFertilizer", 1), 198))
            .itemInputs(
                GTUtility.getIntegratedCircuit(24),
                setStackSize(GTModHandler.getModItem("IC2", "itemFuelPlantBall", 1), 96))

            .duration(180 * 20)
            .eut(TierEU.HV)
            .addTo(SINOPEC);

        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.SulfuricAcid.getFluid(24000))
            .fluidOutputs(
                Materials.AceticAcid.getFluid(12800),
                new FluidStack(C2H4, 9600),
                Materials.CarbonDioxide.getGas(31200),
                new FluidStack(C6H6, 32000),
                Materials.CarbonMonoxide.getGas(19200),
                FluidRegistry.getFluidStack("fluid.ethylbenzene", 8000),
                Materials.Hydrogen.getGas(9600),
                new FluidStack(C7H8, 8000),
                Materials.Methanol.getFluid(38400),
                new FluidStack(steam, 64000),
                new FluidStack(nap, 6000),
                new FluidStack(CH4, 10400),
                FluidRegistry.getFluidStack("fluid.kerosene", 24000),
                Materials.Phenol.getFluid(8000),
                Materials.MethylAcetate.getFluid(1280),
                Materials.Creosote.getFluid(20000),
                FluidRegistry.getFluidStack("fluid.naphthalene", 48000),
                Materials.Dimethylbenzene.getFluid(44000),
                FluidRegistry.getFluidStack(RecipeFluids.registryName(Materials.Ethanol.mFluid), 29700),
                FluidRegistry.getFluidStack("fluid.anthracene", 2000),
                Materials.Acetone.getFluid(6400))
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(24),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.log, Materials.Wood, 1), 1280))

            .duration(360 * 20)
            .eut(TierEU.HV)
            .addTo(SINOPEC);

        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.CharcoalByproducts.getGas(640000))
            .fluidOutputs(
                Materials.AceticAcid.getFluid(25600),
                new FluidStack(C2H4, 19200),
                Materials.CarbonDioxide.getGas(62400),
                new FluidStack(C6H6, 64000),
                Materials.CarbonMonoxide.getGas(38400),
                Materials.Hydrogen.getGas(192000),
                new FluidStack(C7H8, 16000),
                Materials.Methanol.getFluid(76800),
                new FluidStack(steam, 128000),
                new FluidStack(CH4, 20800),
                Materials.Phenol.getFluid(16000),
                Materials.MethylAcetate.getFluid(1280),
                Materials.Creosote.getFluid(40000),
                Materials.Dimethylbenzene.getFluid(88000),
                FluidRegistry.getFluidStack(RecipeFluids.registryName(Materials.Ethanol.mFluid), 2560),
                Materials.Acetone.getFluid(12800))

            .duration(720 * 20)
            .eut(TierEU.HV)
            .addTo(SINOPEC);

        // Octane
        GTValues.RA.stdBuilder()
            .fluidOutputs(
                new FluidStack(CH4, 64 * 125),
                Materials.Butane.getGas(64 * 150),
                new FluidStack(C3H8, 64 * 200),
                new FluidStack(C2H6, 64 * 125),
                Materials.Octane.getFluid(64 * 100),
                new FluidStack(nap, 64 * 800))
            .fluidInputs(new FluidStack(lig, 64 * 1000), Materials.Hydrogen.getGas(64 * 1600))

            .eut(RECIPE_HV)
            .duration(200)
            .addTo(SINOPEC);
    }

}
