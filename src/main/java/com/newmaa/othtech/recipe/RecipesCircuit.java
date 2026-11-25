package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.utils.Utils.setStackSize;
import static gregtech.api.enums.TierEU.*;
import static gregtech.api.recipe.RecipeMaps.nanochipAssemblyMatrixRecipes;
import static gregtech.api.util.GTRecipeBuilder.HOURS;
import static gregtech.api.util.GTRecipeConstants.*;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.item.OTHCircuitComponent;
import com.newmaa.othtech.common.materials.BWLiquids;
import com.newmaa.othtech.common.recipemap.Recipemaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.metadata.NanochipAssemblyMatrixTierKey;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeConstants;
import gregtech.api.util.GTUtility;
import gregtech.api.util.recipe.Scanning;
import gregtech.common.tileentities.machines.multi.nanochip.util.CircuitCalibration;
import gregtech.common.tileentities.machines.multi.nanochip.util.CircuitComponent;
import gtPlusPlus.core.material.MaterialsElements;
import gtnhlanth.common.register.WerkstoffMaterialPool;
import li.cil.oc.OpenComputers;
import tectech.thing.CustomItemList;
import tectech.thing.item.ItemAstralArrayFabricator;

public class RecipesCircuit implements IRecipePool {

    public static ItemStack getAstralArray(int amount) {
        return new ItemStack(ItemAstralArrayFabricator.INSTANCE, amount);
    }

    // TODO
    @Override
    public void loadRecipes() {
        Fluid i140 = FluidRegistry.getFluid("molten.indalloy140");
        Fluid bio = FluidRegistry.getFluid("molten.mutatedlivingsolder");

        final RecipeMap<?> ISA = Recipemaps.MegaIsaForge;
        final RecipeMap<?> MQFT = Recipemaps.QFTMega;
        final RecipeMap<?> Assem = RecipeMaps.assemblerRecipes;
        final int eut = 1000000000;
        // 690
        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, GTModHandler.getModItem(OpenComputers.ID(), "item", 1, 103))
            .metadata(SCANNING, new Scanning(HOURS * 2, RECIPE_MV))
            .itemOutputs(OTHItemList.NukeThrowable.get(1))
            .itemInputs(
                GTModHandler.getModItem(OpenComputers.ID(), "item", 1, 33),
                OTHItemList.AF.get(1),
                setStackSize(GTModHandler.getModItem(OpenComputers.ID(), "item", 1, 27), 3072),
                setStackSize(GTModHandler.getModItem(OpenComputers.ID(), "item", 1, 28), 256 + 64 + 16),
                GTModHandler.getModItem(OpenComputers.ID(), "item", 2, 90),
                setStackSize(ItemList.Circuit_Chip_Ram.get(1), 4096),
                setStackSize(ItemList.Circuit_Parts_ResistorASMD.get(1), 512),
                setStackSize(ItemList.Circuit_Parts_DiodeASMD.get(1), 512),
                setStackSize(ItemList.Circuit_Parts_TransistorASMD.get(1), 512),
                setStackSize(ItemList.Circuit_Parts_CapacitorASMD.get(1), 512),
                setStackSize(ItemList.Circuit_Parts_InductorASMD.get(1), 512),
                setStackSize(GTModHandler.getModItem("IC2", "reactorVentDiamond", 1, 1), 48),
                GTOreDictUnificator.get(OrePrefixes.pipeTiny, Materials.Copper, 16),
                GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorLuV, 64))
            .fluidInputs(
                Materials.Copper.getMolten(9216),
                Materials.SolderingAlloy.getMolten(6144),
                Materials.SuperCoolant.getFluid(2000),
                Materials.Gold.getMolten(1440))
            .duration(150 * 20)
            .eut(RECIPE_UV)
            .addTo(AssemblyLine);

        GTValues.RA.stdBuilder()
            .metadata(NanochipAssemblyMatrixTierKey.INSTANCE, VoltageIndex.ZPM)
            .metadata(GTRecipeConstants.CIRCUIT_CALIBRATION_TYPE, CircuitCalibration.SPECIAL)
            .itemOutputs(OTHItemList.NukeThrowable.get(1))
            .itemInputs(
                CircuitComponent.ProcessedBasicMainframeCasing.getFakeStack(1),
                CircuitComponent.ProcessedWireElectrum.getFakeStack(160),
                OTHCircuitComponent.AF.getFakeStack(1),
                CircuitComponent.ProcessedBoardMultifiberglassElite.getFakeStack(4),
                setStackSize(CircuitComponent.Microprocessor.getFakeStack(1), 256),
                setStackSize(CircuitComponent.ProcessedChipNanoCPU.getFakeStack(1), 3072),
                setStackSize(CircuitComponent.ProcessedChipRAM.getFakeStack(1), 4096),
                setStackSize(CircuitComponent.ProcessedOpticalSMDResistor.getFakeStack(1), 240),
                setStackSize(CircuitComponent.ProcessedOpticalSMDDiode.getFakeStack(1), 240),
                setStackSize(CircuitComponent.ProcessedOpticalSMDTransistor.getFakeStack(1), 240),
                setStackSize(CircuitComponent.ProcessedOpticalSMDCapacitor.getFakeStack(1), 240),
                setStackSize(CircuitComponent.ProcessedOpticalSMDInductor.getFakeStack(1), 240),
                CircuitComponent.ProcessedSuperconductorZPM.getFakeStack(32))
            .fluidInputs(
                Materials.Iridium.getMolten(1440),
                Materials.SolderingAlloy.getMolten(6144),
                Materials.SuperCoolant.getFluid(2000),
                Materials.Gold.getMolten(1440))
            .duration(150 * 20)
            .eut(RECIPE_UEV)
            .addTo(nanochipAssemblyMatrixRecipes);

        // 超时空电路基板
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.boardTransM.get(2))
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.SpaceTime, 16),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.TranscendentMetal, 16),
                CustomItemList.SpacetimeCompressionFieldGeneratorTier0.get(2))
            .fluidInputs(Materials.ExcitedDTSC.getFluid(2000))
            .duration(40 * 20)
            .eut(eut)
            .addTo(MQFT);
        // 悖论消置器
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.capTransM.get(4))
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Infinity, 16),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UMV, 4),
                CustomItemList.StabilisationFieldGeneratorTier0.get(2))
            .fluidInputs(Materials.ExcitedDTSC.getFluid(2000))
            .duration(40 * 20)
            .eut(eut)
            .addTo(MQFT);
        // 超距遥感器
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.transTransM.get(4))
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Universium, 4),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.MAX, 2),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.TranscendentMetal, 64),
                CustomItemList.SpacetimeCompressionFieldGeneratorTier6.get(4),
                ItemList.Field_Generator_UXV.get(1),
                ItemList.Sensor_UXV.get(1))
            .fluidInputs(Materials.ExcitedDTSC.getFluid(4000))
            .duration(80 * 20)
            .eut(eut)
            .addTo(MQFT);
        // 超维卡西米尔发生器
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.induTransM.get(4))
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.MagMatter, 4),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.MAX, 2),
                OTHItemList.beeISAM.get(64),
                OTHItemList.leCasimirM.get(16),
                getAstralArray(1))
            .fluidInputs(Materials.ExcitedDTSC.getFluid(4000))
            .duration(80 * 20)
            .eut(eut)
            .addTo(MQFT);
        // 时空升变蜂巢
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.resTransM.get(4))
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.SpaceTime, 16),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UXV, 4),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Glowstone, 64),
                GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorUMV, 64),
                GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorUMV, 64),
                ItemList.Field_Generator_UMV.get(1))
            .fluidInputs(Materials.ExcitedDTSC.getFluid(4000))
            .duration(80 * 20)
            .eut(eut)
            .addTo(MQFT);
        // 创世纪信息注入器
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.dioTransM.get(4))
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.MAX, 2),
                OTHItemList.beeISAM.get(16),
                CustomItemList.TimeAccelerationFieldGeneratorTier5.get(4),
                ItemList.Field_Generator_UXV.get(1),
                ItemList.Emitter_UXV.get(2))
            .fluidInputs(Materials.ExcitedDTSC.getFluid(4000))
            .duration(80 * 20)
            .eut(eut)
            .addTo(MQFT);
        // 微型卡西米尔发生器
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.leCasimirM.get(16))
            .itemInputs(
                OTHItemList.finCasimirM.get(16),
                OTHItemList.beeISAM.get(16),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UIV, 32),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Eternity, 16))
            .fluidInputs(Materials.ExcitedDTSC.getFluid(4000), BWLiquids.Stars.getFluidOrGas(16 * 144))
            .duration(256 * 20)
            .eut(eut)
            .addTo(ISA);
        // 卡西米尔鳍片
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.finCasimirM.get(16))
            .itemInputs(
                OTHItemList.boardCasimirM.get(8),
                OTHItemList.beeISAM.get(4),
                OTHItemList.beeMagM.get(4),
                OTHItemList.boardCasimirM.get(8),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.WhiteDwarfMatter, 32),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.TranscendentMetal, 48),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.TranscendentMetal, 48),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.MagMatter, 32),
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.MagMatter, 4),
                CustomItemList.StabilisationFieldGeneratorTier5.get(2))
            .fluidInputs(Materials.ExcitedDTSC.getFluid(256 * 144), BWLiquids.Stars.getFluidOrGas(12 * 144))
            .duration(256 * 20)
            .eut(eut)
            .addTo(ISA);
        // 卡西米尔基板
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.boardCasimirM.get(16))
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.itemCasing, Materials.WhiteDwarfMatter, 64),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.WhiteDwarfMatter, 64))
            .fluidInputs(
                Materials.ExcitedDTSC.getFluid(64 * 144),
                BWLiquids.Stars.getFluidOrGas(8 * 144),
                Materials.MagMatter.getMolten(64 * 144),
                Materials.WhiteDwarfMatter.getMolten(64 * 144),
                Materials.Universium.getMolten(64 * 144),
                Materials.SpaceTime.getMolten(64 * 144))
            .duration(128 * 20)
            .eut(eut)
            .addTo(ISA);
        // 时空能源接收器
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.energyHatchSpacetimeM.get(4))
            .itemInputs(
                ItemList.ZPM5.get(4),
                ItemList.Wireless_Hatch_Energy_UMV.get(16),
                ItemList.Wireless_Hatch_Energy_UMV.get(16),
                ItemList.Emitter_UMV.get(16))
            .fluidInputs(Materials.ExcitedDTSC.getFluid(16 * 144), BWLiquids.Stars.getFluidOrGas(4 * 144))
            .duration(512 * 20)
            .eut(eut)
            .addTo(ISA);
        // UIV
        GTValues.RA.stdBuilder()
            .itemOutputs(ItemList.Circuit_TranscendentProcessor.get(1))
            .itemInputs(
                OTHItemList.boardTransM.get(2),
                OTHItemList.energyHatchSpacetimeM.get(1),
                OTHItemList.capTransM.get(16),
                OTHItemList.resTransM.get(16),
                GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorUMV, 16))
            .fluidInputs(BWLiquids.Stars.getFluidOrGas(4 * 144))
            .duration(60 * 20)
            .eut(TierEU.MAX)
            .addTo(ISA);
        // UMV
        GTValues.RA.stdBuilder()
            .itemOutputs(ItemList.Circuit_TranscendentAssembly.get(1))
            .itemInputs(
                OTHItemList.boardTransM.get(4),
                OTHItemList.energyHatchSpacetimeM.get(4),
                OTHItemList.transTransM.get(4),
                OTHItemList.capTransM.get(32),
                OTHItemList.resTransM.get(32),
                GTOreDictUnificator.get(OrePrefixes.wireGt02, Materials.SuperconductorUMV, 8))
            .fluidInputs(BWLiquids.Stars.getFluidOrGas(8 * 144))
            .duration(120 * 20)
            .eut(TierEU.MAX)
            .addTo(ISA);
        // UXV
        GTValues.RA.stdBuilder()
            .itemOutputs(ItemList.Circuit_TranscendentComputer.get(1))
            .itemInputs(
                OTHItemList.boardTransM.get(8),
                OTHItemList.leCasimirM.get(4),
                OTHItemList.transTransM.get(32),
                OTHItemList.capTransM.get(64),
                OTHItemList.resTransM.get(64),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.SuperconductorUMV, 4))
            .fluidInputs(BWLiquids.Stars.getFluidOrGas(16 * 144))
            .duration(240 * 20)
            .eut(TierEU.MAX)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemOutputs(ItemList.Circuit_TranscendentComputer.get(1))
            .itemInputs(
                OTHItemList.boardTransM.get(8),
                OTHItemList.socInfM.get(4),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.SuperconductorUMV, 1))
            .fluidInputs(BWLiquids.Stars.getFluidOrGas(8 * 144))
            .duration(120 * 20)
            .eut(TierEU.MAX)
            .addTo(ISA);
        // MAX
        GTValues.RA.stdBuilder()
            .itemOutputs(ItemList.Circuit_TranscendentMainframe.get(1))
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.WhiteDwarfMatter, 2),
                ItemList.Circuit_TranscendentComputer.get(2),
                OTHItemList.induTransM.get(16),
                OTHItemList.dioTransM.get(16),
                OTHItemList.transTransM.get(64),
                OTHItemList.capTransM.get(64),
                OTHItemList.resTransM.get(64),
                GTOreDictUnificator.get(OrePrefixes.wireGt08, Materials.SuperconductorUMV, 2))
            .fluidInputs(BWLiquids.Stars.getFluidOrGas(64 * 144))
            .duration(1200 * 20)
            .eut(TierEU.MAX)
            .addTo(ISA);
        // TransSoC
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.socInfM.get(1))
            .itemInputs(
                ItemList.Field_Generator_MAX.get(0),
                GTUtility.getIntegratedCircuit(17),
                OTHItemList.capTransM.get(16),
                OTHItemList.energyHatchSpacetimeM.get(4),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.WhiteDwarfMatter, 64))
            .fluidInputs(BWLiquids.Stars.getFluidOrGas(10 * 144))
            .duration(64 * 20)
            .eut(TierEU.MAX)
            .addTo(ISA);
        // general soc
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.socNorM.get(16))
            .itemInputs(
                ItemList.Field_Generator_UMV.get(0),
                GTUtility.getIntegratedCircuit(1),
                ItemList.Circuit_Silicon_Wafer7.get(1),
                WerkstoffMaterialPool.CeriumDopedLutetiumAluminiumGarnet.get(OrePrefixes.gemExquisite, 4))
            .fluidInputs(
                Materials.UUMatter.getFluid(1600),
                MaterialsElements.STANDALONE.HYPOGEN.getFluidStack(2000),
                Materials.BioMediumSterilized.getFluid(1600))
            .duration(200 * 20)
            .requiresCleanRoom()
            .requiresLowGravity()
            .eut(TierEU.UMV)
            .addTo(MQFT);
        // ULV–ZPM circuits from Universal SoC in the Nanochip Assembly Matrix.
        addUniversalSocRecipe(
            CircuitComponent.NandChip,
            4096,
            1,
            20,
            30,
            OTHCircuitComponent.PLASTIC_BOARD.getFakeStack(4),
            Materials.Lead.getMolten(72));
        addUniversalSocRecipe(
            CircuitComponent.Microprocessor,
            2048,
            1,
            80,
            120,
            OTHCircuitComponent.ADVANCED_PLASTIC_BOARD.getFakeStack(8),
            Materials.Tin.getMolten(144));
        addUniversalSocRecipe(
            CircuitComponent.IntegratedProcessor,
            1024,
            1,
            320,
            480,
            OTHCircuitComponent.ADVANCED_PLASTIC_BOARD.getFakeStack(16),
            Materials.SolderingAlloy.getMolten(576));
        addUniversalSocRecipe(
            CircuitComponent.NanoProcessor,
            512,
            1,
            1280,
            1920,
            OTHCircuitComponent.ADVANCED_PLASTIC_BOARD.getFakeStack(16),
            new FluidStack(i140, 2304));
        addUniversalSocRecipe(
            CircuitComponent.QuantumProcessor,
            256,
            2,
            5120,
            7680,
            OTHCircuitComponent.ADVANCED_BOARD.getFakeStack(32),
            new FluidStack(i140, 2304 * 4));
        addUniversalSocRecipe(
            CircuitComponent.CrystalProcessor,
            128,
            2,
            5120 * 4,
            7680 * 4,
            CircuitComponent.ProcessedBoardMultifiberglassElite.getFakeStack(32),
            new FluidStack(bio, 576));
        addUniversalSocRecipe(
            CircuitComponent.WetwareProcessor,
            64,
            4,
            5120 * 16,
            7680 * 16,
            CircuitComponent.ProcessedBoardWetwareLifesupport.getFakeStack(16),
            new FluidStack(bio, 576));
        addUniversalSocRecipe(
            CircuitComponent.BiowareProcessor,
            32,
            16,
            5120 * 64,
            7680 * 64,
            CircuitComponent.ProcessedBoardBioMutated.getFakeStack(8),
            new FluidStack(bio, 2304),
            new FluidStack(FluidRegistry.getFluidID("molten.chromaticglass"), 16 * 72));
    }

    private static void addUniversalSocRecipe(CircuitComponent output, int outputCount, int socCount, int duration,
        long eut, ItemStack board, FluidStack... fluids) {
        GTValues.RA.stdBuilder()
            .metadata(NanochipAssemblyMatrixTierKey.INSTANCE, VoltageIndex.ZPM)
            .metadata(GTRecipeConstants.CIRCUIT_CALIBRATION_TYPE, output.circuitType)
            .itemInputs(board, OTHCircuitComponent.UNIVERSAL_SOC.getFakeStack(socCount))
            .itemOutputs(output.getFakeStack(outputCount))
            .fluidInputs(fluids)
            .duration(duration)
            .eut(eut)
            .addTo(nanochipAssemblyMatrixRecipes);
    }
}
