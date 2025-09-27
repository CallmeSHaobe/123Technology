package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.utils.Utils.setStackSize;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.materials.BWLiquids;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.utils.RecipeBuilder;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.material.MaterialsElements;
import gtnhlanth.common.register.WerkstoffMaterialPool;
import tectech.thing.CustomItemList;
import tectech.thing.item.ItemAstralArrayFabricator;

public class RecipesCircuit implements IRecipePool {

    public static ItemStack getAstralArray(int amount) {
        return new ItemStack(ItemAstralArrayFabricator.INSTANCE, amount);
    }

    @Override
    public void loadRecipes() {
        Fluid i140 = FluidRegistry.getFluid("molten.indalloy140");
        Fluid bio = FluidRegistry.getFluid("molten.mutatedlivingsolder");

        final RecipeMap<?> ISA = Recipemaps.MegaIsaForge;
        final RecipeMap<?> MQFT = Recipemaps.QFTMega;
        final RecipeMap<?> Assem = RecipeMaps.assemblerRecipes;
        final int eut = 1000000000;

        // 超时空电路基板
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.boardTransM.get(2))
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plateDense, MaterialsUEVplus.SpaceTime, 16),
                GTOreDictUnificator.get(OrePrefixes.nanite, MaterialsUEVplus.TranscendentMetal, 16),
                CustomItemList.SpacetimeCompressionFieldGeneratorTier0.get(2))
            .fluidInputs(MaterialsUEVplus.ExcitedDTSC.getFluid(2000))
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
            .fluidInputs(MaterialsUEVplus.ExcitedDTSC.getFluid(2000))
            .duration(40 * 20)
            .eut(eut)
            .addTo(MQFT);
        // 超距遥感器
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.transTransM.get(4))
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plateDense, MaterialsUEVplus.Universium, 4),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Cosmic, 2),
                GTOreDictUnificator.get(OrePrefixes.nanite, MaterialsUEVplus.TranscendentMetal, 64),
                CustomItemList.SpacetimeCompressionFieldGeneratorTier6.get(4),
                ItemList.Field_Generator_UXV.get(1),
                ItemList.Sensor_UXV.get(1))
            .fluidInputs(MaterialsUEVplus.ExcitedDTSC.getFluid(4000))
            .duration(80 * 20)
            .eut(eut)
            .addTo(MQFT);
        // 超维卡西米尔发生器
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.induTransM.get(4))
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plateDense, MaterialsUEVplus.MagMatter, 4),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Cosmic, 2),
                OTHItemList.beeISAM.get(64),
                OTHItemList.leCasimirM.get(16),
                getAstralArray(1))
            .fluidInputs(MaterialsUEVplus.ExcitedDTSC.getFluid(4000))
            .duration(80 * 20)
            .eut(eut)
            .addTo(MQFT);
        // 时空升变蜂巢
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.resTransM.get(4))
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plateDense, MaterialsUEVplus.SpaceTime, 16),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Exotic, 4),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Glowstone, 64),
                GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorUMV, 64),
                GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorUMV, 64),
                ItemList.Field_Generator_UMV.get(1))
            .fluidInputs(MaterialsUEVplus.ExcitedDTSC.getFluid(4000))
            .duration(80 * 20)
            .eut(eut)
            .addTo(MQFT);
        // 创世纪信息注入器
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.dioTransM.get(4))
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Cosmic, 2),
                OTHItemList.beeISAM.get(16),
                CustomItemList.TimeAccelerationFieldGeneratorTier5.get(4),
                ItemList.Field_Generator_UXV.get(1),
                ItemList.Emitter_UXV.get(2))
            .fluidInputs(MaterialsUEVplus.ExcitedDTSC.getFluid(4000))
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
                GTOreDictUnificator.get(OrePrefixes.nanite, MaterialsUEVplus.Eternity, 16))
            .fluidInputs(MaterialsUEVplus.ExcitedDTSC.getFluid(4000), BWLiquids.Stars.getFluidOrGas(16 * 144))
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
                GTOreDictUnificator
                    .get(OrePrefixes.foil, MaterialsUEVplus.MagnetohydrodynamicallyConstrainedStarMatter, 32),
                GTOreDictUnificator.get(OrePrefixes.foil, MaterialsUEVplus.TranscendentMetal, 48),
                GTOreDictUnificator.get(OrePrefixes.foil, MaterialsUEVplus.TranscendentMetal, 48),
                GTOreDictUnificator.get(OrePrefixes.foil, MaterialsUEVplus.MagMatter, 32),
                GTOreDictUnificator.get(OrePrefixes.stick, MaterialsUEVplus.MagMatter, 4),
                CustomItemList.StabilisationFieldGeneratorTier5.get(2))
            .fluidInputs(MaterialsUEVplus.ExcitedDTSC.getFluid(256 * 144), BWLiquids.Stars.getFluidOrGas(12 * 144))
            .duration(256 * 20)
            .eut(eut)
            .addTo(ISA);
        // 卡西米尔基板
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.boardCasimirM.get(16))
            .itemInputs(
                GTOreDictUnificator
                    .get(OrePrefixes.itemCasing, MaterialsUEVplus.MagnetohydrodynamicallyConstrainedStarMatter, 64),
                GTOreDictUnificator
                    .get(OrePrefixes.wireFine, MaterialsUEVplus.MagnetohydrodynamicallyConstrainedStarMatter, 64))
            .fluidInputs(
                MaterialsUEVplus.ExcitedDTSC.getFluid(64 * 144),
                BWLiquids.Stars.getFluidOrGas(8 * 144),
                MaterialsUEVplus.MagMatter.getMolten(64 * 144),
                MaterialsUEVplus.MagnetohydrodynamicallyConstrainedStarMatter.getMolten(64 * 144),
                MaterialsUEVplus.Universium.getMolten(64 * 144),
                MaterialsUEVplus.SpaceTime.getMolten(64 * 144))
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
            .fluidInputs(MaterialsUEVplus.ExcitedDTSC.getFluid(16 * 144), BWLiquids.Stars.getFluidOrGas(4 * 144))
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
                GTOreDictUnificator
                    .get(OrePrefixes.frameGt, MaterialsUEVplus.MagnetohydrodynamicallyConstrainedStarMatter, 2),
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
                GTOreDictUnificator
                    .get(OrePrefixes.wireFine, MaterialsUEVplus.MagnetohydrodynamicallyConstrainedStarMatter, 64))
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
        // ULV - ZPM general soc recipes
        // ULV
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                ItemList.Circuit_Board_Plastic.get(4),
                OTHItemList.socNorM.get(1))
            .itemOutputs(setStackSize(ItemList.NandChip.get(1), 4096))
            .fluidInputs(new FluidStack(i140, 72))
            .duration(20)
            .eut(30)
            .addTo(ISA);
        // LV
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                ItemList.Circuit_Board_Plastic_Advanced.get(8),
                OTHItemList.socNorM.get(1))
            .itemOutputs(setStackSize(ItemList.Circuit_Microprocessor.get(1), 2048))
            .fluidInputs(new FluidStack(i140, 144))
            .duration(80)
            .eut(120)
            .addTo(ISA);
        // MV
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(3),
                ItemList.Circuit_Board_Plastic_Advanced.get(16),
                OTHItemList.socNorM.get(1))
            .itemOutputs(setStackSize(ItemList.Circuit_Processor.get(1), 1024))
            .fluidInputs(new FluidStack(i140, 576))
            .duration(320)
            .eut(480)
            .addTo(ISA);
        // HV
        RecipeBuilder.builder()
            .itemInputs(ItemList.Circuit_Board_Plastic_Advanced.get(16), OTHItemList.socNorM.get(1))
            .itemOutputs(setStackSize(ItemList.Circuit_Nanoprocessor.get(1), 512))
            .fluidInputs(new FluidStack(i140, 2304))
            .duration(1280)
            .eut(1920)
            .addTo(ISA);
        // EV
        RecipeBuilder.builder()
            .itemInputs(ItemList.Circuit_Board_Advanced.get(32), OTHItemList.socNorM.get(2))
            .itemOutputs(setStackSize(ItemList.Circuit_Quantumprocessor.get(1), 256))
            .fluidInputs(new FluidStack(i140, 2304 * 4))
            .duration(5120)
            .eut(7680)
            .addTo(ISA);
        // IV
        RecipeBuilder.builder()
            .itemInputs(ItemList.Circuit_Board_Multifiberglass_Elite.get(32), OTHItemList.socNorM.get(2))
            .itemOutputs(setStackSize(ItemList.Circuit_Crystalprocessor.get(1), 128))
            .fluidInputs(new FluidStack(bio, 576))
            .duration(5120 * 4)
            .eut(7680 * 4)
            .addTo(ISA);
        // LuV
        RecipeBuilder.builder()
            .itemInputs(ItemList.Circuit_Board_Wetware_Extreme.get(16), OTHItemList.socNorM.get(4))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 32092))
            .fluidInputs(new FluidStack(bio, 576))
            .duration(5120 * 16)
            .eut(7680 * 16)
            .addTo(ISA);
        // ZPM
        RecipeBuilder.builder()
            .itemInputs(ItemList.Circuit_Board_Bio_Ultra.get(8), OTHItemList.socNorM.get(16))
            .itemOutputs(setStackSize(ItemList.Circuit_Bioprocessor.get(1), 32))
            .fluidInputs(
                new FluidStack(bio, 2304),
                new FluidStack(FluidRegistry.getFluidID("molten.chromaticglass"), 16 * 72))
            .duration(5120 * 64)
            .eut(7680 * 64)
            .addTo(ISA);
    }
}
