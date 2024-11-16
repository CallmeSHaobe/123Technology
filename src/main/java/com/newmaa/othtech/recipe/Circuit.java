package com.newmaa.othtech.recipe;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.newmaa.othtech.common.materials.liquids;
import com.newmaa.othtech.common.recipemap.Recipemaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;

public class Circuit implements IRecipePool {

    @Override
    public void loadRecipes() {
        Fluid dtsc = FluidRegistry.getFluid("exciteddtsc");
        Fluid i140 = FluidRegistry.getFluid("molten.indalloy140");
        Fluid bio = FluidRegistry.getFluid("molten.mutatedlivingsolder");

        final RecipeMap<?> ISA = Recipemaps.MegaIsaForge;
        final RecipeMap<?> MQFT = Recipemaps.QFTMega;
        final RecipeMap<?> Assem = RecipeMaps.assemblerRecipes;
        final int eut = 1000000000;
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("123Technology", "boardTrans", 2))
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 22588),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 4581),
                GTModHandler.getModItem("tectech", "gt.spacetime_compression_field_generator", 8))
            .fluidInputs(new FluidStack(dtsc, 2000))
            .noOptimize()
            .duration(40 * 20)
            .eut(eut)
            .addTo(MQFT);
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("123Technology", "capTrans", 2))
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 22397),
                GTModHandler.getModItem("dreamcraft", "item.PikoCircuit", 8),
                GTModHandler.getModItem("tectech", "gt.stabilisation_field_generator", 8))
            .fluidInputs(new FluidStack(dtsc, 2000))
            .noOptimize()
            .duration(40 * 20)
            .eut(eut)
            .addTo(MQFT);
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("123Technology", "transTrans", 4))
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 22139),
                GTModHandler.getModItem("dreamcraft", "item.QuantumCircuit", 8),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 4581),
                GTModHandler.getModItem("tectech", "gt.spacetime_compression_field_generator", 8, 8),
                ItemList.Field_Generator_UXV.get(16),
                ItemList.Sensor_UXV.get(16))
            .fluidInputs(new FluidStack(dtsc, 4000))
            .noOptimize()
            .duration(80 * 20)
            .eut(eut)
            .addTo(MQFT);
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("123Technology", "induTrans", 4))
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 22143),
                GTModHandler.getModItem("dreamcraft", "item.QuantumCircuit", 16),
                GTModHandler.getModItem("123Technology", "IsaBee", 64),
                GTModHandler.getModItem("123Technology", "leCasimir", 16),
                GTModHandler.getModItem("tectech", "item.tm.itemAstralArrayFabricator", 1))
            .fluidInputs(new FluidStack(dtsc, 4000))
            .noOptimize()
            .duration(80 * 20)
            .eut(eut)
            .addTo(MQFT);
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("123Technology", "resTrans", 4))
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 22588),
                GTModHandler.getModItem("dreamcraft", "item.PikoCircuit", 8),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 4581),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 2089),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 2089),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 2089),
                ItemList.Field_Generator_UMV.get(8))
            .fluidInputs(new FluidStack(dtsc, 4000))
            .noOptimize()
            .duration(80 * 20)
            .eut(eut)
            .addTo(MQFT);
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("123Technology", "dioTrans", 4))
            .itemInputs(
                GTModHandler.getModItem("dreamcraft", "item.QuantumCircuit", 16),
                GTModHandler.getModItem("123Technology", "IsaBee", 16),
                GTModHandler.getModItem("tectech", "gt.time_acceleration_field_generator", 8, 8),
                ItemList.Field_Generator_UXV.get(16),
                ItemList.Emitter_UXV.get(16))
            .fluidInputs(new FluidStack(dtsc, 4000))
            .noOptimize()
            .duration(80 * 20)
            .eut(eut)
            .addTo(MQFT);
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("123Technology", "leCasimir", 16))
            .itemInputs(
                GTModHandler.getModItem("123Technology", "finCasimir", 16),
                GTModHandler.getModItem("123Technology", "IsaBee", 16),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 4141),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 32174))
            .fluidInputs(new FluidStack(dtsc, 256 * 144), liquids.Stars.getFluidOrGas(16 * 144))
            .noOptimize()
            .duration(256 * 20)
            .eut(eut)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("123Technology", "finCasimir", 16))
            .itemInputs(
                GTModHandler.getModItem("123Technology", "boardCasimir", 16),
                GTModHandler.getModItem("123Technology", "IsaBee", 4),
                GTModHandler.getModItem("123Technology", "MagBee", 4),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 29583),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 29581),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 29143),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 23143),
                GTModHandler.getModItem("tectech", "gt.stabilisation_field_generator", 2, 8))
            .fluidInputs(new FluidStack(dtsc, 256 * 144), liquids.Stars.getFluidOrGas(12 * 144))
            .noOptimize()
            .duration(256 * 20)
            .eut(eut)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("123Technology", "boardCasimir", 16))
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 18583),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19583))
            .fluidInputs(
                new FluidStack(dtsc, 64 * 144),
                liquids.Stars.getFluidOrGas(8 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.magmatter"), 64 * 144),
                new FluidStack(
                    FluidRegistry.getFluidID("molten.magnetohydrodynamicallyconstrainedstarmatter"),
                    64 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.universium"), 64 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.spacetime"), 64 * 144))
            .noOptimize()
            .duration(128 * 20)
            .eut(eut)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("123Technology", "energyHatchSpacetime", 4))
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 4, 32145),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 15091),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 346),
                ItemList.Emitter_UMV.get(16))
            .fluidInputs(new FluidStack(dtsc, 16 * 144), liquids.Stars.getFluidOrGas(4 * 144))
            .noOptimize()
            .duration(512 * 20)
            .eut(eut)
            .addTo(ISA);
        // UIV
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32174))
            .itemInputs(
                GTModHandler.getModItem("123Technology", "boardTrans", 2),
                GTModHandler.getModItem("123Technology", "energyHatchSpacetime", 1),
                GTModHandler.getModItem("123Technology", "capTrans", 16),
                GTModHandler.getModItem("123Technology", "resTrans", 16),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 2089))
            .fluidInputs(liquids.Stars.getFluidOrGas(4 * 144))
            .noOptimize()
            .duration(60 * 20)
            .eut(TierEU.MAX)
            .addTo(ISA);
        // UMV
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32175))
            .itemInputs(
                GTModHandler.getModItem("123Technology", "boardTrans", 4),
                GTModHandler.getModItem("123Technology", "energyHatchSpacetime", 4),
                GTModHandler.getModItem("123Technology", "transTrans", 4),
                GTModHandler.getModItem("123Technology", "capTrans", 32),
                GTModHandler.getModItem("123Technology", "resTrans", 32),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 2090))
            .fluidInputs(liquids.Stars.getFluidOrGas(8 * 144))
            .noOptimize()
            .duration(120 * 20)
            .eut(TierEU.MAX)
            .addTo(ISA);
        // UXV
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32176))
            .itemInputs(
                GTModHandler.getModItem("123Technology", "boardTrans", 8),
                GTModHandler.getModItem("123Technology", "leCasimir", 4),
                GTModHandler.getModItem("123Technology", "transTrans", 32),
                GTModHandler.getModItem("123Technology", "capTrans", 64),
                GTModHandler.getModItem("123Technology", "resTrans", 64),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 2091))
            .fluidInputs(liquids.Stars.getFluidOrGas(16 * 144))
            .noOptimize()
            .duration(240 * 20)
            .eut(TierEU.MAX)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32176))
            .itemInputs(
                GTModHandler.getModItem("123Technology", "boardTrans", 8),
                GTModHandler.getModItem("123Technology", "socInf", 4),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 16, 2091))
            .fluidInputs(liquids.Stars.getFluidOrGas(24 * 144))
            .noOptimize()
            .duration(120 * 20)
            .eut(TierEU.MAX)
            .addTo(ISA);
        // MAX
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32177))
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 4, 4679),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 2, 32176),
                GTModHandler.getModItem("123Technology", "induTrans", 16),
                GTModHandler.getModItem("123Technology", "dioTrans", 16),
                GTModHandler.getModItem("123Technology", "transTrans", 64),
                GTModHandler.getModItem("123Technology", "capTrans", 64),
                GTModHandler.getModItem("123Technology", "resTrans", 64),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 2092))
            .fluidInputs(liquids.Stars.getFluidOrGas(64 * 144))
            .noOptimize()
            .duration(1200 * 20)
            .eut(TierEU.MAX)
            .addTo(ISA);
        // TransSoC
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("123Technology", "socInf", 1))
            .itemInputs(
                ItemList.Field_Generator_MAX.get(0),
                GTUtility.getIntegratedCircuit(17),
                GTModHandler.getModItem("123Technology", "capTrans", 16),
                GTModHandler.getModItem("123Technology", "energyHatchSpacetime", 4),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19583))
            .fluidInputs(liquids.Stars.getFluidOrGas(10 * 144))
            .noOptimize()
            .duration(64 * 20)
            .eut(TierEU.MAX)
            .addTo(ISA);
        // WrapMAX
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 16, 32177),
                GTUtility.getIntegratedCircuit(16))
            .itemOutputs(GTModHandler.getModItem("GoodGenerator", "circuitWrap", 1, 14))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("molten.plastic"), 72))
            .noOptimize()
            .duration(30 * 20)
            .eut(30)
            .addTo(Assem);
        // Piko $ Quantum
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32176),
                GTUtility.getIntegratedCircuit(16))
            .itemOutputs(GTModHandler.getModItem("dreamcraft", "item.QuantumCircuit", 1))
            .noOptimize()
            .duration(20)
            .eut(114514)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32175),
                GTUtility.getIntegratedCircuit(16))
            .itemOutputs(GTModHandler.getModItem("dreamcraft", "item.PikoCircuit", 1))
            .noOptimize()
            .duration(20)
            .eut(114514)
            .addTo(ISA);
        // general soc
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("123Technology", "socNor", 16))
            .itemInputs(
                ItemList.Field_Generator_UMV.get(0),
                GTUtility.getIntegratedCircuit(1),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32722),
                GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedgemExquisite", 4, 11499))
            .fluidInputs(
                new FluidStack(FluidRegistry.getFluidID("ic2uumatter"), 1600),
                new FluidStack(FluidRegistry.getFluidID("molten.hypogen"), 36),
                new FluidStack(FluidRegistry.getFluidID("biohmediumsterilized"), 3200))
            .noOptimize()
            .duration(200 * 20)
            .requiresCleanRoom()
            .requiresLowGravity()
            .eut(TierEU.UMV)
            .addTo(MQFT);
        // ULV - ZPM general soc recipes
        // ULV
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 32106),
                GTModHandler.getModItem("123Technology", "socNor", 1))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 32081))
            .fluidInputs(new FluidStack(i140, 72))
            .noOptimize()
            .duration(20)
            .eut(30)
            .addTo(ISA);
        // LV
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 8, 32106),
                GTModHandler.getModItem("123Technology", "socNor", 1))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 32078))
            .fluidInputs(new FluidStack(i140, 144))
            .noOptimize()
            .duration(80)
            .eut(120)
            .addTo(ISA);
        // MV
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 16, 32106),
                GTModHandler.getModItem("123Technology", "socNor", 1))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 32080))
            .fluidInputs(new FluidStack(i140, 576))
            .noOptimize()
            .duration(320)
            .eut(480)
            .addTo(ISA);
        // HV
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 16, 32102),
                GTModHandler.getModItem("123Technology", "socNor", 1))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 32082))
            .fluidInputs(new FluidStack(i140, 2304))
            .noOptimize()
            .duration(1280)
            .eut(1920)
            .addTo(ISA);
        // EV
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 32, 32103),
                GTModHandler.getModItem("123Technology", "socNor", 2))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 32085))
            .fluidInputs(new FluidStack(i140, 2304 * 4))
            .noOptimize()
            .duration(5120)
            .eut(7680)
            .addTo(ISA);
        // IV
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 32, 32104),
                GTModHandler.getModItem("123Technology", "socNor", 2))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 32089))
            .fluidInputs(new FluidStack(bio, 576))
            .noOptimize()
            .duration(5120 * 4)
            .eut(7680 * 4)
            .addTo(ISA);
        // LuV
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 16, 32105),
                GTModHandler.getModItem("123Technology", "socNor", 4))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 32092))
            .fluidInputs(new FluidStack(bio, 576))
            .noOptimize()
            .duration(5120 * 16)
            .eut(7680 * 16)
            .addTo(ISA);
        // ZPM
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 8, 32107),
                GTModHandler.getModItem("123Technology", "socNor", 16))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 16, 32097))
            .fluidInputs(
                new FluidStack(bio, 2304),
                new FluidStack(FluidRegistry.getFluidID("molten.chromaticglass"), 16 * 72))
            .noOptimize()
            .duration(5120 * 64)
            .eut(7680 * 64)
            .addTo(ISA);
    }
}
