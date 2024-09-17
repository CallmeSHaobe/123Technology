package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.Utils.Utils.setStackSize;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.github.technus.tectech.recipe.TT_recipeAdder;
import com.newmaa.othtech.Utils.recipes.RecipeBuilder;
import com.newmaa.othtech.common.materials.liquids;

import goodgenerator.api.recipe.GoodGeneratorRecipeMaps;
import gregtech.api.enums.GT_Values;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_Utility;

public class Comass implements IRecipePool {

    @Override
    public void loadRecipes() {
        final RecipeMap<?> comass = GoodGeneratorRecipeMaps.componentAssemblyLineRecipes;
        Fluid UIVsc = FluidRegistry.getFluid("molten.superconductoruivbase");
        Fluid UMVsc = FluidRegistry.getFluid("molten.superconductorumvbase");
        Fluid Um = FluidRegistry.getFluid("molten.universium");
        Fluid Et = FluidRegistry.getFluid("molten.eternity");
        Fluid Mm = FluidRegistry.getFluid("molten.magnetohydrodynamicallyconstrainedstarmatter");
        Fluid Ma = FluidRegistry.getFluid("molten.magmatter");
        Fluid Cg = FluidRegistry.getFluid("molten.chromaticglass");
        ItemStack coil = GT_ModHandler.getModItem("gregtech", "gt.blockcasings5", 1, 13);

        RecipeBuilder.builder()
            .itemInputs(
                GT_ModHandler.getModItem("eternalsingularity", "combined_singularity", 48, 15),
                setStackSize(coil, 6 * 64),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 48, 4141))
            .itemOutputs(ItemList.Electric_Motor_MAX.get(64))
            .fluidInputs(
                new FluidStack(UMVsc, 276480),
                new FluidStack(UIVsc, 276480),
                liquids.Void.getFluidOrGas(16000 * 48),
                liquids.Stars.getFluidOrGas(16 * 48 * 144),
                new FluidStack(Ma, 442368),
                new FluidStack(Um, 118368))
            .noOptimize()
            .duration(5904 * 20)
            .specialValue(14)
            .eut(512000000)
            .addTo(comass);
        GT_Values.RA.stdBuilder()
            .itemInputs(
                ItemList.Electric_Motor_MAX.get(48),
                setStackSize(
                    GT_ModHandler.getModItem("tectech", "gt.spacetime_compression_field_generator", 1, 8),
                    64 + 32),
                setStackSize(coil, 6 * 64),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 48, 4141),
                GT_Utility.getIntegratedCircuit(3))
            .itemOutputs(ItemList.Electric_Pump_MAX.get(64))
            .fluidInputs(
                new FluidStack(UMVsc, 276480),
                new FluidStack(UIVsc, 276480),
                liquids.Void.getFluidOrGas(16000 * 48),
                liquids.Stars.getFluidOrGas(16 * 48 * 144),
                new FluidStack(Ma, 331776),
                new FluidStack(Um, 290304),
                new FluidStack(Mm, 290304),
                new FluidStack(FluidRegistry.getFluid("molten.blackdwarfmatter"), 995328))
            .noOptimize()
            .duration(5904 * 20)
            .specialValue(14)
            .eut(512000000)
            .addTo(comass);
        GT_Values.RA.stdBuilder()
            .itemInputs(
                ItemList.Electric_Motor_MAX.get(48),
                setStackSize(coil, 6 * 64),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 48, 4141),
                GT_Utility.getIntegratedCircuit(2))
            .itemOutputs(ItemList.Electric_Piston_MAX.get(64))
            .fluidInputs(
                new FluidStack(UMVsc, 276480),
                new FluidStack(UIVsc, 276480),
                liquids.Void.getFluidOrGas(16000 * 48),
                liquids.Stars.getFluidOrGas(16 * 48 * 144),
                new FluidStack(Ma, 297216),
                new FluidStack(Um, 165888),
                new FluidStack(Et, 165888),
                new FluidStack(FluidRegistry.getFluid("molten.blackdwarfmatter"), 995328))
            .noOptimize()
            .duration(5904 * 20)
            .specialValue(14)
            .eut(512000000)
            .addTo(comass);
        GT_Values.RA.stdBuilder()
            .itemInputs(
                setStackSize(ItemList.Electric_Motor_MAX.get(1), 96),
                setStackSize(coil, 6 * 64),
                setStackSize(
                    GT_ModHandler.getModItem("tectech", "gt.time_acceleration_field_generator", 1, 8),
                    64 + 32),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 48, 4141),
                GT_Utility.getIntegratedCircuit(5))
            .itemOutputs(ItemList.Conveyor_Module_MAX.get(64))
            .fluidInputs(
                new FluidStack(UMVsc, 276480),
                new FluidStack(UIVsc, 276480),
                liquids.Void.getFluidOrGas(16000 * 48),
                liquids.Stars.getFluidOrGas(16 * 48 * 144),
                new FluidStack(Ma, 96768),
                new FluidStack(Um, 884736),
                new FluidStack(FluidRegistry.getFluid("molten.whitedwarfmatter"), 884736),
                new FluidStack(FluidRegistry.getFluid("molten.blackdwarfmatter"), 884736))
            .noOptimize()
            .duration(5904 * 20)
            .specialValue(14)
            .eut(512000000)
            .addTo(comass);
        GT_Values.RA.stdBuilder()
            .itemInputs(
                ItemList.Electric_Motor_MAX.get(48),
                GT_ModHandler.getModItem("GoodGenerator", "circuitWrap", 12, 14),
                setStackSize(coil, 6 * 64),
                setStackSize(GT_ModHandler.getModItem("tectech", "gt.stabilisation_field_generator", 1, 8), 96),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 4141),
                setStackSize(GT_ModHandler.getModItem("123Technology", "singularity1", 1), 192),
                GT_Utility.getIntegratedCircuit(6))
            .itemOutputs(ItemList.Emitter_MAX.get(64))
            .fluidInputs(
                new FluidStack(UMVsc, 276480),
                new FluidStack(FluidRegistry.getFluidID("temporalfluid"), 3072000),
                liquids.Void.getFluidOrGas(16000 * 48),
                liquids.Stars.getFluidOrGas(16 * 48 * 144),
                new FluidStack(Ma, 276480),
                new FluidStack(Um, 110592),
                new FluidStack(Cg, 552960))
            .noOptimize()
            .duration(5904 * 20)
            .specialValue(14)
            .eut(512000000)
            .addTo(comass);
        GT_Values.RA.stdBuilder()
            .itemInputs(
                ItemList.Electric_Motor_MAX.get(48),
                GT_ModHandler.getModItem("GoodGenerator", "circuitWrap", 12, 14),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 48, 4239),
                setStackSize(coil, 6 * 64),
                setStackSize(GT_ModHandler.getModItem("tectech", "gt.stabilisation_field_generator", 1, 8), 96),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 4141),
                setStackSize(GT_ModHandler.getModItem("123Technology", "singularity1", 1), 192),
                GT_Utility.getIntegratedCircuit(7))
            .itemOutputs(ItemList.Sensor_MAX.get(64))
            .fluidInputs(
                new FluidStack(UMVsc, 276480),
                new FluidStack(FluidRegistry.getFluidID("spatialfluid"), 3072000),
                liquids.Void.getFluidOrGas(16000 * 48),
                liquids.Stars.getFluidOrGas(16 * 48 * 144),
                new FluidStack(Ma, 276480),
                new FluidStack(Um, 110592),
                new FluidStack(Cg, 552960))
            .noOptimize()
            .duration(5904 * 20)
            .specialValue(14)
            .eut(512000000)
            .addTo(comass);
        GT_Values.RA.stdBuilder()
            .itemInputs(
                setStackSize(ItemList.Electric_Motor_MAX.get(1), 96),
                setStackSize(ItemList.Electric_Piston_MAX.get(1), 48),
                GT_ModHandler.getModItem("GoodGenerator", "circuitWrap", 6, 14),
                GT_ModHandler.getModItem("GoodGenerator", "circuitWrap", 12, 13),
                GT_ModHandler.getModItem("GoodGenerator", "circuitWrap", 24, 12),
                GT_ModHandler.getModItem("tectech", "item.tm.itemAstralArrayFabricator", 1),
                GT_Utility.getIntegratedCircuit(4))
            .itemOutputs(ItemList.Robot_Arm_MAX.get(64))
            .fluidInputs(
                new FluidStack(UMVsc, 276480),
                new FluidStack(FluidRegistry.getFluidID("spatialfluid"), 3072000),
                liquids.Void.getFluidOrGas(16000 * 48),
                liquids.Stars.getFluidOrGas(16 * 48 * 144),
                new FluidStack(Ma, 193536))
            .noOptimize()
            .duration(5904 * 20)
            .specialValue(14)
            .eut(512000000)
            .addTo(comass);
        GT_Values.RA.stdBuilder()
            .itemInputs(
                setStackSize(ItemList.Emitter_MAX.get(1), 64 * 3),
                GT_ModHandler.getModItem("tectech", "item.tm.itemAstralArrayFabricator", 50),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 48, 4239),
                GT_ModHandler.getModItem("GoodGenerator", "circuitWrap", 24, 14),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 4141),
                setStackSize(GT_ModHandler.getModItem("123Technology", "singularity1", 1), 192),
                setStackSize(GT_ModHandler.getModItem("123Technology", "singularity2", 1), 192),
                GT_Utility.getIntegratedCircuit(7))
            .itemOutputs(ItemList.Field_Generator_MAX.get(64))
            .fluidInputs(
                new FluidStack(UMVsc, 276480),
                new FluidStack(Et, 3072000),
                liquids.Void.getFluidOrGas(16000 * 48),
                liquids.Stars.getFluidOrGas(16 * 48 * 144),
                new FluidStack(Ma, 304128),
                new FluidStack(Um, 13824))
            .noOptimize()
            .duration(5904 * 20)
            .specialValue(14)
            .eut(512000000)
            .addTo(comass);
        TT_recipeAdder.addResearchableAssemblylineRecipe(
            GT_ModHandler.getModItem("GoodGenerator", "componentAssemblylineCasing", 1, 12),
            (int) TierEU.UMV,
            4096,
            (int) TierEU.UMV,
            123,
            new ItemStack[] { GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 4239),
                GT_ModHandler.getModItem("dreamcraft", "item.StargateShieldingFoil", 2),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 12, 22143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 12, 22139), ItemList.Robot_Arm_MAX.get(8),
                ItemList.Electric_Piston_MAX.get(10), ItemList.Electric_Motor_MAX.get(16),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 4, 31143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 16, 20143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 4, 31139),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 16, 20139),
                GT_ModHandler.getModItem("tectech", "item.tm.itemAstralArrayFabricator", 2),
                GT_ModHandler.getModItem("gregtech", "gt.blockcasings5", 64, 13),
                GT_ModHandler.getModItem("123Technology", "singularity2", 12),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 16, 4139),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 16, 4141) },
            new FluidStack[] { new FluidStack(Um, 114000),
                new FluidStack(FluidRegistry.getFluid("molten.whitedwarfmatter"), 514000),
                new FluidStack(FluidRegistry.getFluid("molten.blackdwarfmatter"), 191000),
                new FluidStack(FluidRegistry.getFluidID("rawstarmatter"), 9810) },
            GT_ModHandler.getModItem("GoodGenerator", "componentAssemblylineCasing", 1, 13),
            2460,
            2000000000);

    }
}
