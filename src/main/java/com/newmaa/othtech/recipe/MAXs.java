package com.newmaa.othtech.recipe;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.github.technus.tectech.recipe.TT_recipeAdder;
import com.newmaa.othtech.common.materials.liquids;

import gregtech.api.enums.GT_Values;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.util.GT_Utility;

public class MAXs implements IRecipePool {

    @Override
    public void loadRecipes() {

        Fluid UIVsc = FluidRegistry.getFluid("molten.superconductoruivbase");
        Fluid UMVsc = FluidRegistry.getFluid("molten.superconductorumvbase");
        final RecipeMap<?> Assembler = RecipeMaps.assemblerRecipes;
        final RecipeMap<?> Tower = RecipeMaps.distillationTowerRecipes;

        TT_recipeAdder.addResearchableAssemblylineRecipe(
            ItemList.Electric_Motor_UXV.get(1),
            (int) TierEU.UHV,
            2048,
            (int) TierEU.UMV,
            123,
            new ItemStack[] { GT_ModHandler.getModItem("eternalsingularity", "combined_singularity", 1, 15),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 16, 22139),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 8, 28139),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 32, 25139),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19143),
                GT_ModHandler.getModItem("gregtech", "gt.blockcasings5", 8, 13),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4139),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4585),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4586), },
            new FluidStack[] { liquids.Void.getFluidOrGas(16000), liquids.Stars.getFluidOrGas(144 * 16),
                new FluidStack(UIVsc, 5760), new FluidStack(UMVsc, 5760) },
            ItemList.Electric_Motor_MAX.get(1),
            2460,
            512000000);
        GT_Values.RA.stdBuilder()
            .itemInputs(
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 4, 17583),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 4, 17143),
                GT_Utility.getIntegratedCircuit(8))
            .itemOutputs(GT_ModHandler.getModItem("dreamcraft", "gt.blockcasingsNH", 1, 14))
            .noOptimize()
            .duration(50)
            .eut(TierEU.LV)
            .addTo(Assembler);
        GT_Values.RA.stdBuilder()
            .itemOutputs(GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 11234))
            .itemInputs(
                GT_ModHandler.getModItem("dreamcraft", "gt.blockcasingsNH", 1, 14),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 2, 2611))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("molten.radoxpoly"), 576))
            .duration(50)
            .eut(TierEU.UXV)
            .addTo(Assembler);
        TT_recipeAdder.addResearchableAssemblylineRecipe(
            GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 269),
            512000,
            2048,
            (int) TierEU.UXV,
            64,
            new ItemStack[] { GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 11234),
                GT_ModHandler.getModItem("GoodGenerator", "compactFusionCoil", 16, 2),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 15300),
                GT_ModHandler.getModItem("tectech", "gt.blockcasingsTT", 16),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19139),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 4, 22583),
                GT_ModHandler.getModItem("dreamcraft", "item.QuantumCircuit", 64),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 32417),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4141),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 32, 32165),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 32149),
                GT_ModHandler.getModItem("gregtech", "gt.1080k_Space_Coolantcell", 1),
                GT_ModHandler.getModItem("gregtech", "gt.1080k_Space_Coolantcell", 1),
                GT_ModHandler.getModItem("gregtech", "gt.1080k_Space_Coolantcell", 1),
                GT_ModHandler.getModItem("gregtech", "gt.1080k_Space_Coolantcell", 1),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 32028) },
            new FluidStack[] { new FluidStack(FluidRegistry.getFluidID("cryotheum"), 64000),
                new FluidStack(FluidRegistry.getFluidID("molten.mutatedlivingsolder"), 23040),
                new FluidStack(FluidRegistry.getFluidID("ic2uumatter"), 32000),
                new FluidStack(FluidRegistry.getFluidID("exciteddtsc"), 1000) },
            GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 286),
            1000,
            2000000000);
        TT_recipeAdder.addResearchableAssemblylineRecipe(
            GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 286),
            512000000,
            131072,
            (int) TierEU.MAX,
            262144,
            new ItemStack[] { GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 16, 11234),
                GT_ModHandler.getModItem("GoodGenerator", "compactFusionCoil", 64, 4),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 16, 15300),
                GT_ModHandler.getModItem("tectech", "gt.blockcasingsTT", 64),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 22583),
                GT_OreDictUnificator.get(OrePrefixes.circuit, Materials.Transcendent, 64),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 32417),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 22126),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 22126),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 22126),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 22126),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 22126),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 22126),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 22126),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 22126) },
            new FluidStack[] { new FluidStack(FluidRegistry.getFluidID("cryotheum"), 64000000),
                new FluidStack(FluidRegistry.getFluidID("molten.mutatedlivingsolder"), 64000000),
                new FluidStack(FluidRegistry.getFluidID("ic2uumatter"), 64000000),
                new FluidStack(FluidRegistry.getFluidID("exciteddtsc"), 64000000) },
            GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 22124),
            20000,
            2000000000);
        TT_recipeAdder.addResearchableAssemblylineRecipe(
            GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 15199),
            (int) TierEU.UXV,
            32767,
            (int) TierEU.MAX,
            123,
            new ItemStack[] { GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 15199),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 13106),
                GT_ModHandler.getModItem("kekztech", "kekztech_lapotronicenergyunit_block", 64),
                GT_ModHandler.getModItem("kekztech", "kekztech_lapotronicenergyunit_block", 64, 10),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 15497),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 15465),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 2611),
                GT_ModHandler.getModItem("miscutils", "gtplusplus.blockcasings.6", 16, 1),
                GT_ModHandler.getModItem("gregtech", "gt.blockcasings", 1, 15),
                GT_ModHandler.getModItem("tectech", "gt.blockcasingsTT", 64),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 15300),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 32165),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 32417), },
            new FluidStack[] { new FluidStack(UMVsc, 576000000),
                new FluidStack(FluidRegistry.getFluidID("exciteddtsc"), 114514000),
                new FluidStack(FluidRegistry.getFluidID("molten.mutatedlivingsolder"), 114514000) },
            GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 22125),
            1024 * 20,
            (int) TierEU.UXV);
        GT_Values.RA.stdBuilder()
            .itemOutputs(GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 22126))
            .itemInputs(
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 4, 286),
                ItemList.Electric_Pump_MAX.get(64),
                ItemList.Sensor_MAX.get(64),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 24500),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 16, 2611),
                GT_Utility.getIntegratedCircuit(7))
            .noOptimize()
            .duration(3200 * 20)
            .eut(TierEU.MAX)
            .addTo(Assembler);
        GT_Values.RA.stdBuilder()
            .fluidInputs(
                new FluidStack(FluidRegistry.getFluidID("molten.universium"), 128 * 144),
                Materials.Hydrogen.getPlasma(16000))
            .fluidOutputs(
                liquids.Galaxy.getFluidOrGas(48 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.quantum"), 16 * 144))
            .noOptimize()
            .duration(123 * 20)
            .requiresLowGravity()
            .eut(512000000)
            .addTo(Tower);
        TT_recipeAdder.addResearchableAssemblylineRecipe(
            ItemList.Electric_Pump_UXV.get(1),
            (int) TierEU.UHV,
            2048,
            (int) TierEU.UMV,
            123,
            new ItemStack[] { ItemList.Electric_Motor_MAX.get(1),
                GT_ModHandler.getModItem("tectech", "gt.spacetime_compression_field_generator", 2, 8),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 4, 17143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 27143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 28143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 28139),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 28583),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 8, 21143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 8, 21139),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 8, 21583),
                GT_ModHandler.getModItem("gregtech", "gt.blockcasings5", 8, 13),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4139),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4585),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4586),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 22586) },
            new FluidStack[] { liquids.Void.getFluidOrGas(16000), liquids.Stars.getFluidOrGas(144 * 16),
                new FluidStack(UIVsc, 5760), new FluidStack(UMVsc, 5760) },
            ItemList.Electric_Pump_MAX.get(1),
            2460,
            512000000);
        TT_recipeAdder.addResearchableAssemblylineRecipe(
            ItemList.Electric_Piston_UXV.get(1),
            (int) TierEU.UHV,
            2048,
            (int) TierEU.UMV,
            123,
            new ItemStack[] { ItemList.Electric_Motor_MAX.get(1),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 6, 17143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 8, 28143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 25143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 8, 23143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 4, 31143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 4, 31139),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 4, 31141),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 8, 20143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 8, 20139),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 8, 20141),
                GT_ModHandler.getModItem("gregtech", "gt.blockcasings5", 16, 13),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4139),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4585),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4586),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 22586) },
            new FluidStack[] { liquids.Void.getFluidOrGas(16000), liquids.Stars.getFluidOrGas(144 * 16),
                new FluidStack(UIVsc, 5760), new FluidStack(UMVsc, 5760) },
            ItemList.Electric_Piston_MAX.get(1),
            2460,
            512000000);
        TT_recipeAdder.addResearchableAssemblylineRecipe(
            ItemList.Conveyor_Module_UXV.get(1),
            (int) TierEU.UHV,
            2048,
            (int) TierEU.UMV,
            123,
            new ItemStack[] { ItemList.Electric_Motor_MAX.get(2),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 4, 17143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 28143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 28143),
                GT_ModHandler.getModItem("tectech", "gt.time_acceleration_field_generator", 2, 8),
                GT_ModHandler.getModItem("gregtech", "gt.blockcasings5", 8, 13),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 17139),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 17139),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 17586),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 17586),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 17585),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 17585),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4139),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4585),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4586), },
            new FluidStack[] { liquids.Void.getFluidOrGas(16000), liquids.Stars.getFluidOrGas(144 * 16),
                new FluidStack(UIVsc, 5760), new FluidStack(UMVsc, 5760) },
            ItemList.Conveyor_Module_MAX.get(1),
            2460,
            512000000);
        TT_recipeAdder.addResearchableAssemblylineRecipe(
            ItemList.Robot_Arm_UXV.get(1),
            (int) TierEU.UHV,
            2048,
            (int) TierEU.UMV,
            123,
            new ItemStack[] { GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 8, 23143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 8, 31143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 16, 20143),
                GT_ModHandler.getModItem("tectech", "gt.time_acceleration_field_generator", 2, 8),
                GT_ModHandler.getModItem("tectech", "gt.stabilisation_field_generator", 2, 8),
                ItemList.Electric_Motor_MAX.get(2), ItemList.Electric_Piston_MAX.get(1),
                GT_OreDictUnificator.get(OrePrefixes.circuit, Materials.Transcendent, 2),
                GT_OreDictUnificator.get(OrePrefixes.circuit, Materials.Cosmic, 4),
                GT_OreDictUnificator.get(OrePrefixes.circuit, Materials.Exotic, 8),
                GT_ModHandler.getModItem("gregtech", "gt.blockcasings5", 8, 13),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4139),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4585),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4586), },
            new FluidStack[] { liquids.Void.getFluidOrGas(16000), liquids.Stars.getFluidOrGas(144 * 16),
                new FluidStack(UIVsc, 5760), new FluidStack(UMVsc, 5760) },
            ItemList.Robot_Arm_MAX.get(1),
            2460,
            512000000);
        TT_recipeAdder.addResearchableAssemblylineRecipe(
            ItemList.Sensor_UXV.get(1),
            (int) TierEU.UHV,
            2048,
            (int) TierEU.UMV,
            123,
            new ItemStack[] { GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 4239),
                ItemList.Electric_Motor_MAX.get(1), GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 17143),
                GT_ModHandler.getModItem("tectech", "gt.stabilisation_field_generator", 2, 8),
                GT_ModHandler.getModItem("123Technology", "singularity1", 4),
                GT_ModHandler.getModItem("dreamcraft", "item.ChromaticLens", 64),
                GT_OreDictUnificator.get(OrePrefixes.circuit, Materials.Transcendent, 4),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 29143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 29143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 29139),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 29139),
                GT_ModHandler.getModItem("gregtech", "gt.blockcasings5", 16, 13),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 8, 4139),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 8, 4585),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 8, 4586), },
            new FluidStack[] { liquids.Void.getFluidOrGas(16000), liquids.Stars.getFluidOrGas(144 * 16),
                new FluidStack(FluidRegistry.getFluidID("spatialfluid"), 64000), new FluidStack(UMVsc, 5760) },
            ItemList.Sensor_MAX.get(1),
            2460,
            512000000);
        TT_recipeAdder.addResearchableAssemblylineRecipe(
            ItemList.Emitter_UXV.get(1),
            (int) TierEU.UHV,
            2048,
            (int) TierEU.UMV,
            123,
            new ItemStack[] { GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 4239),
                ItemList.Electric_Motor_MAX.get(1), GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 23143),
                GT_ModHandler.getModItem("tectech", "gt.stabilisation_field_generator", 2, 8),
                GT_ModHandler.getModItem("123Technology", "singularity1", 4),
                GT_ModHandler.getModItem("dreamcraft", "item.ChromaticLens", 64),
                GT_OreDictUnificator.get(OrePrefixes.circuit, Materials.Transcendent, 4),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 29143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 29143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 29139),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 29139),
                GT_ModHandler.getModItem("gregtech", "gt.blockcasings5", 16, 13),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 8, 4139),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 8, 4585),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 8, 4586), },
            new FluidStack[] { liquids.Void.getFluidOrGas(16000), liquids.Stars.getFluidOrGas(144 * 16),
                new FluidStack(FluidRegistry.getFluidID("temporalfluid"), 64000), new FluidStack(UMVsc, 5760) },
            ItemList.Emitter_MAX.get(1),
            2460,
            512000000);
        TT_recipeAdder.addResearchableAssemblylineRecipe(
            ItemList.Field_Generator_UXV.get(1),
            (int) TierEU.UHV,
            2048,
            (int) TierEU.UMV,
            123,
            new ItemStack[] { GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 4239),
                GT_ModHandler.getModItem("tectech", "item.tm.itemAstralArrayFabricator", 1),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 12, 17143),
                GT_ModHandler.getModItem("123Technology", "singularity2", 4),
                GT_ModHandler.getModItem("123Technology", "singularity1", 4),
                GT_ModHandler.getModItem("eternalsingularity", "eternal_singularity", 4), ItemList.Emitter_MAX.get(4),
                GT_OreDictUnificator.get(OrePrefixes.circuit, Materials.Transcendent, 8),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19143),
                GT_ModHandler.getModItem("gregtech", "gt.blockcasings5", 32, 13),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 24, 4139),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 24, 4585),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 24, 4586), },
            new FluidStack[] { liquids.Void.getFluidOrGas(16000), liquids.Stars.getFluidOrGas(144 * 16),
                new FluidStack(FluidRegistry.getFluidID("molten.eternity"), 64000), new FluidStack(UMVsc, 5760) },
            ItemList.Field_Generator_MAX.get(1),
            2460,
            512000000);
    }
}
