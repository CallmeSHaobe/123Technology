package com.newmaa.othtech.recipe;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.newmaa.othtech.common.materials.liquids;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import tectech.recipe.TTRecipeAdder;

public class recipesMAXs implements IRecipePool {

    @Override
    public void loadRecipes() {

        Fluid UIVsc = FluidRegistry.getFluid("molten.superconductoruivbase");
        Fluid UMVsc = FluidRegistry.getFluid("molten.superconductorumvbase");
        final RecipeMap<?> Assembler = RecipeMaps.assemblerRecipes;
        final RecipeMap<?> Tower = RecipeMaps.distillationTowerRecipes;

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemList.Electric_Motor_UXV.get(1),
            (int) TierEU.UHV,
            2048,
            (int) TierEU.UMV,
            123,
            new ItemStack[] { GTModHandler.getModItem("eternalsingularity", "combined_singularity", 1, 15),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 16, 22139),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 8, 28139),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 32, 25139),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19143),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19143),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19143),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19143),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19143),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19143),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19143),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19143),
                GTModHandler.getModItem("gregtech", "gt.blockcasings5", 8, 13),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4139),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4585),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4586), },
            new FluidStack[] { liquids.Void.getFluidOrGas(16000), liquids.Stars.getFluidOrGas(144 * 16),
                new FluidStack(UIVsc, 5760), new FluidStack(UMVsc, 5760) },
            ItemList.Electric_Motor_MAX.get(1),
            2460,
            512000000);
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 17143),
                GTUtility.getIntegratedCircuit(8))
            .itemOutputs(GTModHandler.getModItem("dreamcraft", "gt.blockcasingsNH", 1, 14))
            .noOptimize()
            .duration(50)
            .eut(TierEU.LV)
            .addTo(Assembler);
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 11234))
            .itemInputs(
                GTModHandler.getModItem("dreamcraft", "gt.blockcasingsNH", 1, 14),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 2, 2611))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("molten.radoxpoly"), 576))
            .duration(50)
            .eut(TierEU.UXV)
            .addTo(Assembler);
        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 269),
            512000,
            2048,
            (int) TierEU.UXV,
            64,
            new ItemStack[] { GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 11234),
                GTModHandler.getModItem("GoodGenerator", "compactFusionCoil", 16, 2),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 15300),
                GTModHandler.getModItem("tectech", "gt.blockcasingsTT", 16),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19139),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 4, 22583),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Cosmic, 64),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 32417),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4141),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 32, 32165),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 32149),
                GTModHandler.getModItem("gregtech", "gt.1080k_Space_Coolantcell", 1),
                GTModHandler.getModItem("gregtech", "gt.1080k_Space_Coolantcell", 1),
                GTModHandler.getModItem("gregtech", "gt.1080k_Space_Coolantcell", 1),
                GTModHandler.getModItem("gregtech", "gt.1080k_Space_Coolantcell", 1),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 32028) },
            new FluidStack[] { new FluidStack(FluidRegistry.getFluidID("cryotheum"), 64000),
                new FluidStack(FluidRegistry.getFluidID("molten.mutatedlivingsolder"), 23040),
                new FluidStack(FluidRegistry.getFluidID("ic2uumatter"), 32000),
                new FluidStack(FluidRegistry.getFluidID("exciteddtsc"), 1000) },
            GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 286),
            1000,
            2000000000);
        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 286),
            512000000,
            131072,
            (int) TierEU.MAX,
            262144,
            new ItemStack[] { GTModHandler.getModItem("gregtech", "gt.blockmachines", 16, 11234),
                GTModHandler.getModItem("GoodGenerator", "compactFusionCoil", 64, 4),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 16, 15300),
                GTModHandler.getModItem("tectech", "gt.blockcasingsTT", 64),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19143),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 22583),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Transcendent, 64),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 32417),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 23524),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 23524),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 23524),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 23524),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 23524),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 23524),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 23524),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 23524) },
            new FluidStack[] { new FluidStack(FluidRegistry.getFluidID("cryotheum"), 64000000),
                new FluidStack(FluidRegistry.getFluidID("molten.mutatedlivingsolder"), 64000000),
                new FluidStack(FluidRegistry.getFluidID("ic2uumatter"), 64000000),
                new FluidStack(FluidRegistry.getFluidID("exciteddtsc"), 64000000) },
            GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 23522),
            20000,
            2000000000);
        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 15199),
            (int) TierEU.UXV,
            32767,
            (int) TierEU.MAX,
            123,
            new ItemStack[] { GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 15199),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 13106),
                GTModHandler.getModItem("kekztech", "kekztech_lapotronicenergyunit_block", 64),
                GTModHandler.getModItem("kekztech", "kekztech_lapotronicenergyunit_block", 64, 10),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 15497),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 15465),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 2611),
                GTModHandler.getModItem("miscutils", "gtplusplus.blockcasings.6", 16, 1),
                GTModHandler.getModItem("gregtech", "gt.blockcasings", 1, 15),
                GTModHandler.getModItem("tectech", "gt.blockcasingsTT", 64),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 15300),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 32165),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 32417), },
            new FluidStack[] { new FluidStack(UMVsc, 576000000),
                new FluidStack(FluidRegistry.getFluidID("exciteddtsc"), 114514000),
                new FluidStack(FluidRegistry.getFluidID("molten.mutatedlivingsolder"), 114514000) },
            GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 23523),
            1024 * 20,
            (int) TierEU.UXV);
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 23524))
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 4, 286),
                ItemList.Electric_Pump_MAX.get(64),
                ItemList.Sensor_MAX.get(64),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 24500),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 16, 2611),
                GTUtility.getIntegratedCircuit(7))
            .noOptimize()
            .duration(3200 * 20)
            .eut(TierEU.MAX)
            .addTo(Assembler);
        GTValues.RA.stdBuilder()
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
        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemList.Electric_Pump_UXV.get(1),
            (int) TierEU.UHV,
            2048,
            (int) TierEU.UMV,
            123,
            new ItemStack[] { ItemList.Electric_Motor_MAX.get(1),
                GTModHandler.getModItem("tectech", "gt.spacetime_compression_field_generator", 2, 8),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 4, 17143),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 27143),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 28143),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 28139),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 28583),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 8, 21143),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 8, 21139),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 8, 21583),
                GTModHandler.getModItem("gregtech", "gt.blockcasings5", 8, 13),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4139),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4585),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4586),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 22586) },
            new FluidStack[] { liquids.Void.getFluidOrGas(16000), liquids.Stars.getFluidOrGas(144 * 16),
                new FluidStack(UIVsc, 5760), new FluidStack(UMVsc, 5760) },
            ItemList.Electric_Pump_MAX.get(1),
            2460,
            512000000);
        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemList.Electric_Piston_UXV.get(1),
            (int) TierEU.UHV,
            2048,
            (int) TierEU.UMV,
            123,
            new ItemStack[] { ItemList.Electric_Motor_MAX.get(1),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 6, 17143),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 8, 28143),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 25143),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 8, 23143),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 4, 31143),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 4, 31139),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 4, 31141),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 8, 20143),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 8, 20139),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 8, 20141),
                GTModHandler.getModItem("gregtech", "gt.blockcasings5", 16, 13),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4139),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4585),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4586),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 22586) },
            new FluidStack[] { liquids.Void.getFluidOrGas(16000), liquids.Stars.getFluidOrGas(144 * 16),
                new FluidStack(UIVsc, 5760), new FluidStack(UMVsc, 5760) },
            ItemList.Electric_Piston_MAX.get(1),
            2460,
            512000000);
        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemList.Conveyor_Module_UXV.get(1),
            (int) TierEU.UHV,
            2048,
            (int) TierEU.UMV,
            123,
            new ItemStack[] { ItemList.Electric_Motor_MAX.get(2),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 4, 17143),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 28143),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 28143),
                GTModHandler.getModItem("tectech", "gt.time_acceleration_field_generator", 2, 8),
                GTModHandler.getModItem("gregtech", "gt.blockcasings5", 8, 13),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 17139),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 17139),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 17586),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 17586),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 17585),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 17585),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4139),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4585),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4586), },
            new FluidStack[] { liquids.Void.getFluidOrGas(16000), liquids.Stars.getFluidOrGas(144 * 16),
                new FluidStack(UIVsc, 5760), new FluidStack(UMVsc, 5760) },
            ItemList.Conveyor_Module_MAX.get(1),
            2460,
            512000000);
        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemList.Robot_Arm_UXV.get(1),
            (int) TierEU.UHV,
            2048,
            (int) TierEU.UMV,
            123,
            new ItemStack[] { GTModHandler.getModItem("gregtech", "gt.metaitem.01", 8, 23143),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 8, 31143),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 16, 20143),
                GTModHandler.getModItem("tectech", "gt.time_acceleration_field_generator", 2, 8),
                GTModHandler.getModItem("tectech", "gt.stabilisation_field_generator", 2, 8),
                ItemList.Electric_Motor_MAX.get(2), ItemList.Electric_Piston_MAX.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Transcendent, 2),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Cosmic, 4),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Exotic, 8),
                GTModHandler.getModItem("gregtech", "gt.blockcasings5", 8, 13),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4139),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4585),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4586), },
            new FluidStack[] { liquids.Void.getFluidOrGas(16000), liquids.Stars.getFluidOrGas(144 * 16),
                new FluidStack(UIVsc, 5760), new FluidStack(UMVsc, 5760) },
            ItemList.Robot_Arm_MAX.get(1),
            2460,
            512000000);
        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemList.Sensor_UXV.get(1),
            (int) TierEU.UHV,
            2048,
            (int) TierEU.UMV,
            123,
            new ItemStack[] { GTModHandler.getModItem("gregtech", "gt.blockframes", 1, 141),
                ItemList.Electric_Motor_MAX.get(1), GTModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 17143),
                GTModHandler.getModItem("tectech", "gt.stabilisation_field_generator", 2, 8),
                GTModHandler.getModItem("123Technology", "MetaItemOTH", 4, 18),
                GTModHandler.getModItem("dreamcraft", "item.ChromaticLens", 64),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Transcendent, 4),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 29143),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 29143),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 29139),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 29139),
                GTModHandler.getModItem("gregtech", "gt.blockcasings5", 16, 13),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 8, 4139),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 8, 4585),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 8, 4586), },
            new FluidStack[] { liquids.Void.getFluidOrGas(16000), liquids.Stars.getFluidOrGas(144 * 16),
                new FluidStack(FluidRegistry.getFluidID("spatialfluid"), 64000), new FluidStack(UMVsc, 5760) },
            ItemList.Sensor_MAX.get(1),
            2460,
            512000000);
        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemList.Emitter_UXV.get(1),
            (int) TierEU.UHV,
            2048,
            (int) TierEU.UMV,
            123,
            new ItemStack[] { GTModHandler.getModItem("gregtech", "gt.blockframes", 1, 141),
                ItemList.Electric_Motor_MAX.get(1), GTModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 23143),
                GTModHandler.getModItem("tectech", "gt.stabilisation_field_generator", 2, 8),
                GTModHandler.getModItem("123Technology", "MetaItemOTH", 4, 18),
                GTModHandler.getModItem("dreamcraft", "item.ChromaticLens", 64),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Transcendent, 4),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 29143),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 29143),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 29139),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 29139),
                GTModHandler.getModItem("gregtech", "gt.blockcasings5", 16, 13),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 8, 4139),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 8, 4585),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 8, 4586), },
            new FluidStack[] { liquids.Void.getFluidOrGas(16000), liquids.Stars.getFluidOrGas(144 * 16),
                new FluidStack(FluidRegistry.getFluidID("temporalfluid"), 64000), new FluidStack(UMVsc, 5760) },
            ItemList.Emitter_MAX.get(1),
            2460,
            512000000);
        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemList.Field_Generator_UXV.get(1),
            (int) TierEU.UHV,
            2048,
            (int) TierEU.UMV,
            123,
            new ItemStack[] { GTModHandler.getModItem("gregtech", "gt.blockframes", 1, 141),
                GTModHandler.getModItem("tectech", "item.tm.itemAstralArrayFabricator", 1),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 12, 17143),
                GTModHandler.getModItem("123Technology", "MetaItemOTH", 4, 19),
                GTModHandler.getModItem("123Technology", "MetaItemOTH", 4, 18),
                GTModHandler.getModItem("eternalsingularity", "eternal_singularity", 4), ItemList.Emitter_MAX.get(4),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Transcendent, 8),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19143),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19143),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19143),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19143),
                GTModHandler.getModItem("gregtech", "gt.blockcasings5", 32, 13),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 24, 4139),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 24, 4585),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 24, 4586), },
            new FluidStack[] { liquids.Void.getFluidOrGas(16000), liquids.Stars.getFluidOrGas(144 * 16),
                new FluidStack(FluidRegistry.getFluidID("molten.eternity"), 64000), new FluidStack(UMVsc, 5760) },
            ItemList.Field_Generator_MAX.get(1),
            2460,
            512000000);

    }
}
