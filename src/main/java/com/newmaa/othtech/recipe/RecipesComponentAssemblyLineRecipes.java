package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.recipe.RecipesCircuit.getAstralArray;
import static com.newmaa.othtech.utils.Utils.getEternalSingularity;
import static com.newmaa.othtech.utils.Utils.setStackSize;
import static gregtech.api.enums.TierEU.RECIPE_UXV;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.materials.BWLiquids;
import com.newmaa.othtech.common.materials.OTHMaterials;
import com.newmaa.othtech.utils.RecipeBuilder;

import goodgenerator.api.recipe.GoodGeneratorRecipeMaps;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.material.MaterialsElements;

public class RecipesComponentAssemblyLineRecipes implements IRecipePool {

    public static ItemStack getNanites(int amount, Object material) {
        return GTOreDictUnificator.get(OrePrefixes.nanite, material, amount);
    }

    @Override
    public void loadRecipes() {
        final RecipeMap<?> comass = GoodGeneratorRecipeMaps.componentAssemblyLineRecipes;
        ItemStack coil = GTModHandler.getModItem("gregtech", "gt.blockcasings5", 1, 13);

        RecipeBuilder.builder()
            .itemInputs(getEternalSingularity(48), setStackSize(coil, 2 * 48), getNanites(48, Materials.Eternity))
            .itemOutputs(ItemList.Electric_Motor_MAX.get(64))
            .fluidInputs(
                OTHMaterials.Gunmu.getMolten(297216),
                Materials.Universium.getMolten(110592),
                Materials.Eternity.getMolten(110592),
                Materials.MagMatter.getMolten(110592),
                Materials.SuperconductorUMVBase.getMolten(276480),
                Materials.SuperconductorUIVBase.getMolten(276480),
                BWLiquids.Void.getFluidOrGas(16000 * 48),
                BWLiquids.Stars.getFluidOrGas(16 * 48 * 144))
            .duration(25 * 48 * 20)
            .specialValue(14)
            .eut(RECIPE_UXV)
            .addTo(comass);
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Electric_Motor_MAX.get(48),
                setStackSize(coil, 2 * 48),
                getNanites(28, Materials.Eternity),
                GTUtility.getIntegratedCircuit(3))
            .itemOutputs(ItemList.Electric_Pump_MAX.get(64))
            .fluidInputs(
                OTHMaterials.Gunmu.getMolten(400896),
                Materials.Universium.getMolten(359424),
                Materials.Eternity.getMolten(359424),
                Materials.MHDCSM.getMolten(359424),
                Materials.SuperconductorUMVBase.getMolten(276480),
                Materials.SuperconductorUIVBase.getMolten(276480),
                BWLiquids.Void.getFluidOrGas(16000 * 48),
                BWLiquids.Stars.getFluidOrGas(16 * 48 * 144))
            .duration(25 * 48 * 20)
            .specialValue(14)
            .eut(RECIPE_UXV)
            .addTo(comass);
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Electric_Motor_MAX.get(48),
                setStackSize(coil, 4 * 48),
                getNanites(28, Materials.Eternity),
                GTUtility.getIntegratedCircuit(2))
            .itemOutputs(ItemList.Electric_Piston_MAX.get(64))
            .fluidInputs(
                OTHMaterials.Gunmu.getMolten(331776),
                Materials.Universium.getMolten(165888),
                Materials.Eternity.getMolten(165888),
                Materials.RadoxPolymer.getMolten(3538944),
                Materials.SuperconductorUMVBase.getMolten(276480),
                Materials.SuperconductorUIVBase.getMolten(276480),
                BWLiquids.Void.getFluidOrGas(16000 * 48),
                BWLiquids.Stars.getFluidOrGas(16 * 48 * 144))
            .duration(25 * 48 * 20)
            .specialValue(14)
            .eut(RECIPE_UXV)
            .addTo(comass);
        GTValues.RA.stdBuilder()
            .itemInputs(
                setStackSize(ItemList.Electric_Motor_MAX.get(1), 96),
                GTModHandler.getModItem("GoodGenerator", "circuitWrap", 6, 14),
                setStackSize(coil, 8 * 48),
                getNanites(28, Materials.Eternity),
                GTUtility.getIntegratedCircuit(5))
            .itemOutputs(ItemList.Conveyor_Module_MAX.get(64))
            .fluidInputs(
                OTHMaterials.Gunmu.getMolten(165888),
                Materials.Universium.getMolten(27648),
                Materials.BlackDwarfMatter.getMolten(27648),
                Materials.WhiteDwarfMatter.getMolten(27648),
                Materials.Kevlar.getMolten(7077888),
                Materials.RadoxPolymer.getMolten(42467328),
                Materials.SuperconductorUMVBase.getMolten(276480),
                Materials.SuperconductorUIVBase.getMolten(276480),
                BWLiquids.Void.getFluidOrGas(16000 * 48),
                BWLiquids.Stars.getFluidOrGas(16 * 48 * 144))
            .duration(25 * 48 * 20)
            .specialValue(14)
            .eut(RECIPE_UXV)
            .addTo(comass);
        GTValues.RA.stdBuilder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.frameGt, OTHMaterials.Gunmu, 1), 192),
                ItemList.Electric_Motor_MAX.get(48),
                GTModHandler.getModItem("GoodGenerator", "circuitWrap", 12, 14),
                setStackSize(coil, 8 * 48),
                setStackSize(getNanites(1, Materials.Eternity), 32),
                setStackSize(OTHItemList.glassSingularityM.get(1), 192),
                GTUtility.getIntegratedCircuit(6))
            .itemOutputs(ItemList.Emitter_MAX.get(64))
            .fluidInputs(
                OTHMaterials.Gunmu.getMolten(165888),
                Materials.Universium.getMolten(110592),
                Materials.Eternity.getMolten(110592),
                Materials.MHDCSM.getMolten(110592),
                MaterialsElements.STANDALONE.CHRONOMATIC_GLASS.getFluidStack(552960),
                Materials.SuperconductorUMVBase.getMolten(276480),
                new FluidStack(FluidRegistry.getFluidID("temporalfluid"), 3072000),
                BWLiquids.Void.getFluidOrGas(16000 * 48),
                BWLiquids.Stars.getFluidOrGas(16 * 48 * 144))
            .duration(25 * 48 * 20)
            .specialValue(14)
            .eut(RECIPE_UXV)
            .addTo(comass);
        GTValues.RA.stdBuilder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.frameGt, OTHMaterials.Gunmu, 1), 192),
                ItemList.Electric_Motor_MAX.get(48),
                GTModHandler.getModItem("GoodGenerator", "circuitWrap", 12, 14),
                setStackSize(coil, 8 * 48),
                setStackSize(getNanites(1, Materials.Eternity), 32),
                setStackSize(OTHItemList.glassSingularityM.get(1), 192),
                GTUtility.getIntegratedCircuit(7))
            .itemOutputs(ItemList.Sensor_MAX.get(64))
            .fluidInputs(
                OTHMaterials.Gunmu.getMolten(221184),
                Materials.Universium.getMolten(110592),
                Materials.Eternity.getMolten(110592),
                Materials.MHDCSM.getMolten(110592),
                MaterialsElements.STANDALONE.CHRONOMATIC_GLASS.getFluidStack(552960),
                Materials.SuperconductorUMVBase.getMolten(276480),
                new FluidStack(FluidRegistry.getFluidID("spatialfluid"), 3072000),
                BWLiquids.Void.getFluidOrGas(16000 * 48),
                BWLiquids.Stars.getFluidOrGas(16 * 48 * 144))
            .duration(25 * 48 * 20)
            .specialValue(14)
            .eut(RECIPE_UXV)
            .addTo(comass);
        GTValues.RA.stdBuilder()
            .itemInputs(
                setStackSize(ItemList.Electric_Motor_MAX.get(1), 96),
                setStackSize(ItemList.Electric_Piston_MAX.get(1), 48),
                GTModHandler.getModItem("GoodGenerator", "circuitWrap", 6, 14),
                GTModHandler.getModItem("GoodGenerator", "circuitWrap", 12, 13),
                GTModHandler.getModItem("GoodGenerator", "circuitWrap", 24, 12),
                setStackSize(coil, 4 * 48),
                setStackSize(getNanites(1, Materials.Eternity), 36),
                GTUtility.getIntegratedCircuit(4))
            .itemOutputs(ItemList.Robot_Arm_MAX.get(64))
            .fluidInputs(
                OTHMaterials.Gunmu.getMolten(387072),
                Materials.SuperconductorUMVBase.getMolten(276480),
                Materials.SuperconductorUIVBase.getMolten(276480),
                BWLiquids.Void.getFluidOrGas(16000 * 48),
                BWLiquids.Stars.getFluidOrGas(16 * 48 * 144))
            .duration(25 * 48 * 20)
            .specialValue(14)
            .eut(RECIPE_UXV)
            .addTo(comass);
        GTValues.RA.stdBuilder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.frameGt, OTHMaterials.Gunmu, 1), 192),
                getAstralArray(48),
                setStackSize(ItemList.Emitter_MAX.get(1), 4 * 48),
                GTModHandler.getModItem("GoodGenerator", "circuitWrap", 12, 14),
                setStackSize(coil, 16 * 48),
                setStackSize(getNanites(1, Materials.Eternity), 48),
                setStackSize(OTHItemList.glassSingularityM.get(1), 192),
                setStackSize(OTHItemList.machineSingularityM.get(1), 192),
                GTUtility.getIntegratedCircuit(7))
            .itemOutputs(ItemList.Field_Generator_MAX.get(64))
            .fluidInputs(
                OTHMaterials.Gunmu.getMolten(221184),
                Materials.MagMatter.getMolten(82944),
                Materials.SuperconductorUMVBase.getMolten(276480),
                Materials.Eternity.getMolten(3072000),
                BWLiquids.Void.getFluidOrGas(16000 * 48),
                BWLiquids.Stars.getFluidOrGas(16 * 48 * 144))
            .duration(25 * 48 * 20)
            .specialValue(14)
            .eut(RECIPE_UXV)
            .addTo(comass);

    }
}
