package com.newmaa.othtech.recipe;

import static com.dreammaster.item.NHItemList.StargateShieldingFoil;
import static com.newmaa.othtech.recipe.RecipesCircuit.getAstralArray;
import static com.newmaa.othtech.utils.Utils.setStackSize;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.materials.BWLiquids;
import com.newmaa.othtech.utils.RecipeBuilder;

import goodgenerator.api.recipe.GoodGeneratorRecipeMaps;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.material.MaterialsElements;
import tectech.recipe.TTRecipeAdder;
import tectech.thing.CustomItemList;

public class RecipesComponentAssemblyLineRecipes implements IRecipePool {

    public static ItemStack getNanites(int amount, Object material) {
        return GTOreDictUnificator.get(OrePrefixes.nanite, material, amount);
    }

    @Override
    public void loadRecipes() {
        final RecipeMap<?> comass = GoodGeneratorRecipeMaps.componentAssemblyLineRecipes;
        ItemStack coil = GTModHandler.getModItem("gregtech", "gt.blockcasings5", 1, 13);

        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem("eternalsingularity", "combined_singularity", 48, 15),
                setStackSize(coil, 6 * 64),
                getNanites(48, MaterialsUEVplus.Eternity))
            .itemOutputs(ItemList.Electric_Motor_MAX.get(64))
            .fluidInputs(
                Materials.SuperconductorUMVBase.getMolten(276480),
                Materials.SuperconductorUIVBase.getMolten(276480),
                BWLiquids.Void.getFluidOrGas(16000 * 48),
                BWLiquids.Stars.getFluidOrGas(16 * 48 * 144),
                MaterialsUEVplus.MagMatter.getMolten(442368),
                MaterialsUEVplus.Universium.getMolten(118368))
            .duration(5904 * 20)
            .specialValue(14)
            .eut(512000000)
            .addTo(comass);
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Electric_Motor_MAX.get(48),
                setStackSize(CustomItemList.SpacetimeCompressionFieldGeneratorTier8.get(1), 64 + 32),
                setStackSize(coil, 6 * 64),
                getNanites(48, MaterialsUEVplus.Eternity),
                GTUtility.getIntegratedCircuit(3))
            .itemOutputs(ItemList.Electric_Pump_MAX.get(64))
            .fluidInputs(
                Materials.SuperconductorUMVBase.getMolten(276480),
                Materials.SuperconductorUIVBase.getMolten(276480),
                BWLiquids.Void.getFluidOrGas(16000 * 48),
                BWLiquids.Stars.getFluidOrGas(16 * 48 * 144),
                MaterialsUEVplus.MagMatter.getMolten(331776),
                MaterialsUEVplus.Universium.getMolten(290304),
                MaterialsUEVplus.Eternity.getMolten(290304),
                MaterialsUEVplus.BlackDwarfMatter.getMolten(995328))
            .duration(5904 * 20)
            .specialValue(14)
            .eut(512000000)
            .addTo(comass);
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Electric_Motor_MAX.get(48),
                setStackSize(coil, 6 * 64),
                getNanites(48, MaterialsUEVplus.Eternity),
                GTUtility.getIntegratedCircuit(2))
            .itemOutputs(ItemList.Electric_Piston_MAX.get(64))
            .fluidInputs(
                Materials.SuperconductorUMVBase.getMolten(276480),
                Materials.SuperconductorUIVBase.getMolten(276480),
                BWLiquids.Void.getFluidOrGas(16000 * 48),
                BWLiquids.Stars.getFluidOrGas(16 * 48 * 144),
                MaterialsUEVplus.MagMatter.getMolten(297216),
                MaterialsUEVplus.Universium.getMolten(165888),
                MaterialsUEVplus.Eternity.getMolten(165888),
                MaterialsUEVplus.BlackDwarfMatter.getMolten(995328))

            .duration(5904 * 20)
            .specialValue(14)
            .eut(512000000)
            .addTo(comass);
        GTValues.RA.stdBuilder()
            .itemInputs(
                setStackSize(ItemList.Electric_Motor_MAX.get(1), 96),
                setStackSize(coil, 6 * 64),
                setStackSize(CustomItemList.SpacetimeCompressionFieldGeneratorTier8.get(1), 64 + 32),
                getNanites(48, MaterialsUEVplus.Eternity),
                GTUtility.getIntegratedCircuit(5))
            .itemOutputs(ItemList.Conveyor_Module_MAX.get(64))
            .fluidInputs(
                Materials.SuperconductorUMVBase.getMolten(276480),
                Materials.SuperconductorUIVBase.getMolten(276480),
                BWLiquids.Void.getFluidOrGas(16000 * 48),
                BWLiquids.Stars.getFluidOrGas(16 * 48 * 144),
                MaterialsUEVplus.MagMatter.getMolten(96768),
                MaterialsUEVplus.Universium.getMolten(884736),
                MaterialsUEVplus.BlackDwarfMatter.getMolten(884736),
                MaterialsUEVplus.WhiteDwarfMatter.getMolten(884736))
            .duration(5904 * 20)
            .specialValue(14)
            .eut(512000000)
            .addTo(comass);
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Electric_Motor_MAX.get(48),
                GTModHandler.getModItem("GoodGenerator", "circuitWrap", 12, 14),
                setStackSize(coil, 6 * 64),
                setStackSize(CustomItemList.StabilisationFieldGeneratorTier8.get(1), 96),
                getNanites(48, MaterialsUEVplus.Eternity),
                setStackSize(OTHItemList.glassSingularityM.get(1), 192),
                GTUtility.getIntegratedCircuit(6))
            .itemOutputs(ItemList.Emitter_MAX.get(64))
            .fluidInputs(
                Materials.SuperconductorUMVBase.getMolten(276480),
                new FluidStack(FluidRegistry.getFluidID("temporalfluid"), 3072000),
                BWLiquids.Void.getFluidOrGas(16000 * 48),
                BWLiquids.Stars.getFluidOrGas(16 * 48 * 144),
                MaterialsUEVplus.MagMatter.getMolten(276480),
                MaterialsUEVplus.Universium.getMolten(110592),
                MaterialsElements.STANDALONE.CHRONOMATIC_GLASS.getFluidStack(552960))
            .duration(5904 * 20)
            .specialValue(14)
            .eut(512000000)
            .addTo(comass);
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Electric_Motor_MAX.get(48),
                GTModHandler.getModItem("GoodGenerator", "circuitWrap", 12, 14),
                GTOreDictUnificator.get(OrePrefixes.frameGt, MaterialsUEVplus.Eternity, 48),
                setStackSize(coil, 6 * 64),
                setStackSize(CustomItemList.StabilisationFieldGeneratorTier8.get(1), 96),
                getNanites(64, MaterialsUEVplus.Eternity),
                setStackSize(OTHItemList.glassSingularityM.get(1), 192),
                GTUtility.getIntegratedCircuit(7))
            .itemOutputs(ItemList.Sensor_MAX.get(64))
            .fluidInputs(
                Materials.SuperconductorUMVBase.getMolten(276480),
                new FluidStack(FluidRegistry.getFluidID("spatialfluid"), 3072000),
                BWLiquids.Void.getFluidOrGas(16000 * 48),
                BWLiquids.Stars.getFluidOrGas(16 * 48 * 144),
                MaterialsUEVplus.MagMatter.getMolten(276480),
                MaterialsUEVplus.Universium.getMolten(110592),
                MaterialsElements.STANDALONE.CHRONOMATIC_GLASS.getFluidStack(552960))
            .duration(5904 * 20)
            .specialValue(14)
            .eut(512000000)
            .addTo(comass);
        GTValues.RA.stdBuilder()
            .itemInputs(
                setStackSize(ItemList.Electric_Motor_MAX.get(1), 96),
                setStackSize(ItemList.Electric_Piston_MAX.get(1), 48),
                GTModHandler.getModItem("GoodGenerator", "circuitWrap", 6, 14),
                GTModHandler.getModItem("GoodGenerator", "circuitWrap", 12, 13),
                GTModHandler.getModItem("GoodGenerator", "circuitWrap", 24, 12),
                getAstralArray(3),
                GTUtility.getIntegratedCircuit(4))
            .itemOutputs(ItemList.Robot_Arm_MAX.get(64))
            .fluidInputs(
                Materials.SuperconductorUMVBase.getMolten(276480),
                new FluidStack(FluidRegistry.getFluidID("spatialfluid"), 3072000),
                BWLiquids.Void.getFluidOrGas(16000 * 48),
                BWLiquids.Stars.getFluidOrGas(16 * 48 * 144),
                MaterialsUEVplus.MagMatter.getMolten(193536))
            .duration(5904 * 20)
            .specialValue(14)
            .eut(512000000)
            .addTo(comass);
        GTValues.RA.stdBuilder()
            .itemInputs(
                setStackSize(ItemList.Emitter_MAX.get(1), 64 * 3),
                getAstralArray(50),
                GTOreDictUnificator.get(OrePrefixes.frameGt, MaterialsUEVplus.Eternity, 48),
                GTModHandler.getModItem("GoodGenerator", "circuitWrap", 24, 14),
                getNanites(64, MaterialsUEVplus.Eternity),
                setStackSize(OTHItemList.glassSingularityM.get(1), 192),
                setStackSize(OTHItemList.machineSingularityM.get(1), 192),
                GTUtility.getIntegratedCircuit(7))
            .itemOutputs(ItemList.Field_Generator_MAX.get(64))
            .fluidInputs(
                Materials.SuperconductorUMVBase.getMolten(276480),
                MaterialsUEVplus.Eternity.getMolten(3072000),
                BWLiquids.Void.getFluidOrGas(16000 * 48),
                BWLiquids.Stars.getFluidOrGas(16 * 48 * 144),
                MaterialsUEVplus.MagMatter.getMolten(304128),
                MaterialsUEVplus.Universium.getMolten(13834))
            .duration(5904 * 20)
            .specialValue(14)
            .eut(512000000)
            .addTo(comass);
        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTModHandler.getModItem("GoodGenerator", "componentAssemblylineCasing", 1, 12),
            (int) TierEU.UMV,
            4096,
            (int) TierEU.UMV,
            123,
            new Object[] {
                GTOreDictUnificator
                    .get(OrePrefixes.frameGt, MaterialsUEVplus.MagnetohydrodynamicallyConstrainedStarMatter, 1),
                StargateShieldingFoil.getIS(2),
                GTOreDictUnificator.get(OrePrefixes.plateDense, MaterialsUEVplus.MagMatter, 12),
                GTOreDictUnificator.get(OrePrefixes.plateDense, MaterialsUEVplus.Universium, 12),
                ItemList.Robot_Arm_MAX.get(8), ItemList.Electric_Piston_MAX.get(10),
                ItemList.Electric_Motor_MAX.get(16),
                GTOreDictUnificator.get(OrePrefixes.gearGt, MaterialsUEVplus.Universium, 4),
                GTOreDictUnificator.get(OrePrefixes.gearGtSmall, MaterialsUEVplus.Universium, 16),
                GTOreDictUnificator.get(OrePrefixes.gearGt, MaterialsUEVplus.Universium, 4),
                GTOreDictUnificator.get(OrePrefixes.gearGtSmall, MaterialsUEVplus.Universium, 16), getAstralArray(2),
                setStackSize(coil, 64), OTHItemList.machineSingularityM.get(12),
                getNanites(64, MaterialsUEVplus.Universium), getNanites(64, MaterialsUEVplus.Eternity) },
            new FluidStack[] { MaterialsUEVplus.Universium.getMolten(114000),
                MaterialsUEVplus.WhiteDwarfMatter.getMolten(514000),
                MaterialsUEVplus.BlackDwarfMatter.getMolten(191000), MaterialsUEVplus.RawStarMatter.getMolten(9810) },
            GTModHandler.getModItem("GoodGenerator", "componentAssemblylineCasing", 1, 13),
            2460,
            2000000000);

    }
}
