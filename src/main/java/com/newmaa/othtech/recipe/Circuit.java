package com.newmaa.othtech.recipe;

import com.newmaa.othtech.common.materials.liquids;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import gregtech.api.enums.GT_Values;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_Utility;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class Circuit implements IRecipePool{

    @Override
    public void loadRecipes() {
        Fluid dtsc = FluidRegistry.getFluid("exciteddtsc");
        Fluid i140 = FluidRegistry.getFluid("molten.indalloy140");
        Fluid bio = FluidRegistry.getFluid("molten.mutatedlivingsolder");

        final RecipeMap<?> ISA = Recipemaps.MegaIsaForge;
        final RecipeMap<?> MQFT = Recipemaps.QFTMega;
        final RecipeMap<?> Assem = RecipeMaps.assemblerRecipes;
        final int eut = 1000000000;
        GT_Values.RA.stdBuilder()
            .itemOutputs(
                GT_ModHandler.getModItem("123Technology", "boardTrans", 2)
            )
            .itemInputs(
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 22588),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 4581),
                GT_ModHandler.getModItem("tectech","gt.spacetime_compression_field_generator", 8)
            )
            .fluidInputs(
                new FluidStack(dtsc, 2000)
            )
            .noOptimize()
            .duration(40 * 20)
            .eut(eut)
            .addTo(MQFT);
        GT_Values.RA.stdBuilder()
            .itemOutputs(
                GT_ModHandler.getModItem("123Technology", "capTrans", 2)
            )
            .itemInputs(
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 22397),
                GT_ModHandler.getModItem("dreamcraft","item.PikoCircuit", 8),
                GT_ModHandler.getModItem("tectech","gt.stabilisation_field_generator", 8)
            )
            .fluidInputs(
                new FluidStack(dtsc, 2000)
            )
            .noOptimize()
            .duration(40 * 20)
            .eut(eut)
            .addTo(MQFT);
        GT_Values.RA.stdBuilder()
            .itemOutputs(
                GT_ModHandler.getModItem("123Technology", "transTrans", 4)
            )
            .itemInputs(
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 22139),
                GT_ModHandler.getModItem("dreamcraft","item.QuantumCircuit", 8),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 4581),
                GT_ModHandler.getModItem("tectech","gt.spacetime_compression_field_generator", 8, 8),
                ItemList.Field_Generator_UXV.get(16),
                ItemList.Sensor_UXV.get(16)
            )
            .fluidInputs(
                new FluidStack(dtsc, 4000)
            )
            .noOptimize()
            .duration(80 * 20)
            .eut(eut)
            .addTo(MQFT);
        GT_Values.RA.stdBuilder()
            .itemOutputs(
                GT_ModHandler.getModItem("123Technology", "induTrans", 4)
            )
            .itemInputs(
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 22143),
                GT_ModHandler.getModItem("dreamcraft","item.QuantumCircuit", 16),
                GT_ModHandler.getModItem("123Technology", "IsaBee", 64),
                GT_ModHandler.getModItem("123Technology", "leCasimir", 16),
                GT_ModHandler.getModItem("tectech", "item.tm.itemAstralArrayFabricator", 1)
            )
            .fluidInputs(
                new FluidStack(dtsc, 4000)
            )
            .noOptimize()
            .duration(80 * 20)
            .eut(eut)
            .addTo(MQFT);
        GT_Values.RA.stdBuilder()
            .itemOutputs(
                GT_ModHandler.getModItem("123Technology", "resTrans", 4)
            )
            .itemInputs(
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 22588),
                GT_ModHandler.getModItem("dreamcraft","item.PikoCircuit", 8),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 4581),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 2089),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 2089),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 2089),
                ItemList.Field_Generator_UMV.get(8)
            )
            .fluidInputs(
                new FluidStack(dtsc, 4000)
            )
            .noOptimize()
            .duration(80 * 20)
            .eut(eut)
            .addTo(MQFT);
        GT_Values.RA.stdBuilder()
            .itemOutputs(
                GT_ModHandler.getModItem("123Technology", "dioTrans", 4)
            )
            .itemInputs(
                GT_ModHandler.getModItem("dreamcraft","item.QuantumCircuit", 16),
                GT_ModHandler.getModItem("123Technology", "IsaBee", 16),
                GT_ModHandler.getModItem("tectech","gt.time_acceleration_field_generator", 8, 8),
                ItemList.Field_Generator_UXV.get(16),
                ItemList.Emitter_UXV.get(16)
            )
            .fluidInputs(
                new FluidStack(dtsc, 4000)
            )
            .noOptimize()
            .duration(80 * 20)
            .eut(eut)
            .addTo(MQFT);
        GT_Values.RA.stdBuilder()
            .itemOutputs(
                GT_ModHandler.getModItem("123Technology", "leCasimir", 16)
            )
            .itemInputs(
                GT_ModHandler.getModItem("123Technology", "finCasimir", 16),
                GT_ModHandler.getModItem("123Technology", "IsaBee", 16),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 4141),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 32174),
                ItemList.Emitter_MAX.get(4)
            )
            .fluidInputs(
                new FluidStack(dtsc, 256 * 144),
                liquids.Stars.getFluidOrGas(16 * 144)
            )
            .noOptimize()
            .duration(256 * 20)
            .eut(eut)
            .addTo(ISA);
        GT_Values.RA.stdBuilder()
            .itemOutputs(
                GT_ModHandler.getModItem("123Technology", "finCasimir", 16)
            )
            .itemInputs(
                GT_ModHandler.getModItem("123Technology", "boardCasimir", 16),
                GT_ModHandler.getModItem("123Technology", "IsaBee", 4),
                GT_ModHandler.getModItem("123Technology", "MagBee", 4),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 29583),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 29581),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 29143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 23143),
                GT_ModHandler.getModItem("tectech","gt.stabilisation_field_generator", 2, 8)
            )
            .fluidInputs(
                new FluidStack(dtsc, 256 * 144),
                liquids.Stars.getFluidOrGas(12 * 144)
            )
            .noOptimize()
            .duration(256 * 20)
            .eut(eut)
            .addTo(ISA);
        GT_Values.RA.stdBuilder()
            .itemOutputs(
                GT_ModHandler.getModItem("123Technology", "boardCasimir", 16)
            )
            .itemInputs(
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 18583),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19583)
            )
            .fluidInputs(
                new FluidStack(dtsc, 64 * 144),
                liquids.Stars.getFluidOrGas(8 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.magmatter"), 64 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.magnetohydrodynamicallyconstrainedstarmatter"), 64 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.universium"), 64 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.spacetime"), 64 * 144)
            )
            .noOptimize()
            .duration(128 * 20)
            .eut(eut)
            .addTo(ISA);
        GT_Values.RA.stdBuilder()
            .itemOutputs(
                GT_ModHandler.getModItem("123Technology", "energyHatchSpacetime", 4)
            )
            .itemInputs(
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 4, 32145),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 15091),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 346),
                ItemList.Emitter_UMV.get(16)
            )
            .fluidInputs(
                new FluidStack(dtsc, 16 * 144),
                liquids.Stars.getFluidOrGas(4 * 144)
            )
            .noOptimize()
            .duration(512 * 20)
            .eut(eut)
            .addTo(ISA);
        //UIV
        GT_Values.RA.stdBuilder()
            .itemOutputs(
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32174)
            )
            .itemInputs(
                GT_ModHandler.getModItem("123Technology", "boardTrans", 2),
                GT_ModHandler.getModItem("123Technology", "energyHatchSpacetime", 1),
                GT_ModHandler.getModItem("123Technology", "capTrans", 16),
                GT_ModHandler.getModItem("123Technology", "resTrans", 16),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 2089)
            )
            .fluidInputs(
                liquids.Stars.getFluidOrGas(4 * 144)
            )
            .noOptimize()
            .duration(60 * 20)
            .eut(TierEU.MAX)
            .addTo(ISA);
        //UMV
        GT_Values.RA.stdBuilder()
            .itemOutputs(
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32175)
            )
            .itemInputs(
                GT_ModHandler.getModItem("123Technology", "boardTrans", 4),
                GT_ModHandler.getModItem("123Technology", "energyHatchSpacetime", 4),
                GT_ModHandler.getModItem("123Technology", "transTrans", 4),
                GT_ModHandler.getModItem("123Technology", "capTrans", 32),
                GT_ModHandler.getModItem("123Technology", "resTrans", 32),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 2090)
            )
            .fluidInputs(
                liquids.Stars.getFluidOrGas(8 * 144)
            )
            .noOptimize()
            .duration(120 * 20)
            .eut(TierEU.MAX)
            .addTo(ISA);
        //UXV
        GT_Values.RA.stdBuilder()
            .itemOutputs(
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32176)
            )
            .itemInputs(
                GT_ModHandler.getModItem("123Technology", "boardTrans", 8),
                GT_ModHandler.getModItem("123Technology", "leCasimir", 4),
                GT_ModHandler.getModItem("123Technology", "transTrans", 32),
                GT_ModHandler.getModItem("123Technology", "capTrans", 64),
                GT_ModHandler.getModItem("123Technology", "resTrans", 64),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 2091)
            )
            .fluidInputs(
                liquids.Stars.getFluidOrGas(16 * 144)
            )
            .noOptimize()
            .duration(240 * 20)
            .eut(TierEU.MAX)
            .addTo(ISA);
        GT_Values.RA.stdBuilder()
            .itemOutputs(
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32176)
            )
            .itemInputs(
                GT_ModHandler.getModItem("123Technology", "boardTrans", 8),
                GT_ModHandler.getModItem("123Technology", "socInf", 4),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 16, 2091)
            )
            .fluidInputs(
                liquids.Stars.getFluidOrGas(24 * 144)
            )
            .noOptimize()
            .duration(120 * 20)
            .eut(TierEU.MAX)
            .addTo(ISA);
        //MAX
        GT_Values.RA.stdBuilder()
            .itemOutputs(
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32177)
            )
            .itemInputs(
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 4, 4679),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 2, 32176),
                GT_ModHandler.getModItem("123Technology", "induTrans", 16),
                GT_ModHandler.getModItem("123Technology", "dioTrans", 16),
                GT_ModHandler.getModItem("123Technology", "transTrans", 64),
                GT_ModHandler.getModItem("123Technology", "capTrans", 64),
                GT_ModHandler.getModItem("123Technology", "resTrans", 64),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 2092)
            )
            .fluidInputs(
                liquids.Stars.getFluidOrGas(64 * 144)
            )
            .noOptimize()
            .duration(1200 * 20)
            .eut(TierEU.MAX)
            .addTo(ISA);
        //TransSoC
        GT_Values.RA.stdBuilder()
            .itemOutputs(
                GT_ModHandler.getModItem("123Technology", "socInf", 1)
            )
            .itemInputs(
                ItemList.Field_Generator_MAX.get(0),
                GT_Utility.getIntegratedCircuit(17),
                GT_ModHandler.getModItem("123Technology", "capTrans", 16),
                GT_ModHandler.getModItem("123Technology", "energyHatchSpacetime", 4),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19583)
            )
            .fluidInputs(
                liquids.Stars.getFluidOrGas(10 * 144)
            )
            .noOptimize()
            .duration(64 * 20)
            .eut(TierEU.MAX)
            .addTo(ISA);
        //WrapMAX
        GT_Values.RA.stdBuilder()
            .itemInputs(
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 16, 32177),
                GT_Utility.getIntegratedCircuit(16)
            )
            .itemOutputs(
                GT_ModHandler.getModItem("GoodGenerator", "circuitWrap", 12, 14)
            )
            .fluidInputs(
                new FluidStack(FluidRegistry.getFluidID("molten.plastic"), 72)
            )
            .noOptimize()
            .duration(30 * 20)
            .eut(30)
            .addTo(Assem);
        //Piko $ Quantum
        GT_Values.RA.stdBuilder()
            .itemInputs(
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32176),
                GT_Utility.getIntegratedCircuit(16)
            )
            .itemOutputs(
                GT_ModHandler.getModItem("dreamcraft","item.QuantumCircuit", 1)
            )
            .noOptimize()
            .duration(20)
            .eut(114514)
            .addTo(ISA);
        GT_Values.RA.stdBuilder()
            .itemInputs(
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32175),
                GT_Utility.getIntegratedCircuit(16)
            )
            .itemOutputs(
                GT_ModHandler.getModItem("dreamcraft","item.PikoCircuit", 1)
            )
            .noOptimize()
            .duration(20)
            .eut(114514)
            .addTo(ISA);
        //general soc
        GT_Values.RA.stdBuilder()
            .itemOutputs(
                GT_ModHandler.getModItem("123Technology", "socNor", 4)
            )
            .itemInputs(
                ItemList.Field_Generator_UMV.get(0),
                GT_Utility.getIntegratedCircuit(1),
                GT_Utility.getIntegratedCircuit(2),
                GT_Utility.getIntegratedCircuit(3),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32722),
                GT_ModHandler.getModItem("bartworks","gt.bwMetaGeneratedgemExquisite", 4, 11499),
                GT_ModHandler.getModItem("miscutils","MU-metaitem.01", 0, 32140),
                GT_ModHandler.getModItem("miscutils","MU-metaitem.02", 0, 18320)
            )
            .fluidInputs(
                new FluidStack(FluidRegistry.getFluidID("ic2uumatter"), 1600),
                new FluidStack(FluidRegistry.getFluidID("molten.hypogen"), 36),
                new FluidStack(FluidRegistry.getFluidID("biohmediumsterilized"), 3200)
            )
            .noOptimize()
            .duration(200 * 20)
            .requiresCleanRoom()
            .requiresLowGravity()
            .eut(TierEU.UMV)
            .addTo(MQFT);
        //ULV - ZPM general soc recipes
        //ULV
        GT_Values.RA.stdBuilder()
            .itemInputs(
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 32106),
                GT_ModHandler.getModItem("123Technology", "socNor", 1)
            )
            .itemOutputs(
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 32081)
            )
            .fluidInputs(
                new FluidStack(i140, 72)
            )
            .noOptimize()
            .duration(20)
            .eut(30)
            .addTo(ISA);
        //LV
        GT_Values.RA.stdBuilder()
            .itemInputs(
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 8, 32106),
                GT_ModHandler.getModItem("123Technology", "socNor", 1)
            )
            .itemOutputs(
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 32078)
            )
            .fluidInputs(
                new FluidStack(i140, 144)
            )
            .noOptimize()
            .duration(80)
            .eut(120)
            .addTo(ISA);
        //MV
        GT_Values.RA.stdBuilder()
            .itemInputs(
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 16, 32106),
                GT_ModHandler.getModItem("123Technology", "socNor", 1)
            )
            .itemOutputs(
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 32080)
            )
            .fluidInputs(
                new FluidStack(i140, 576)
            )
            .noOptimize()
            .duration(320)
            .eut(480)
            .addTo(ISA);
        //HV
        GT_Values.RA.stdBuilder()
            .itemInputs(
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 16, 32102),
                GT_ModHandler.getModItem("123Technology", "socNor", 2)
            )
            .itemOutputs(
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 32082)
            )
            .fluidInputs(
                new FluidStack(i140, 2304)
            )
            .noOptimize()
            .duration(1280)
            .eut(1920)
            .addTo(ISA);
        //EV
        GT_Values.RA.stdBuilder()
            .itemInputs(
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 32, 32103),
                GT_ModHandler.getModItem("123Technology", "socNor", 2)
            )
            .itemOutputs(
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 32085)
            )
            .fluidInputs(
                new FluidStack(i140, 2304 * 4)
            )
            .noOptimize()
            .duration(5120)
            .eut(7680)
            .addTo(ISA);
        //IV
        GT_Values.RA.stdBuilder()
            .itemInputs(
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 32, 32104),
                GT_ModHandler.getModItem("123Technology", "socNor", 4)
            )
            .itemOutputs(
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 32, 32089)
            )
            .fluidInputs(
                new FluidStack(bio, 576)
            )
            .noOptimize()
            .duration(5120 * 4)
            .eut(7680 * 4)
            .addTo(ISA);
        //LuV
        GT_Values.RA.stdBuilder()
            .itemInputs(
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 16, 32105),
                GT_ModHandler.getModItem("123Technology", "socNor", 8)
            )
            .itemOutputs(
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 16, 32092)
            )
            .fluidInputs(
                new FluidStack(bio, 576)
            )
            .noOptimize()
            .duration(5120 * 16)
            .eut(7680 * 16)
            .addTo(ISA);
        //ZPM
        GT_Values.RA.stdBuilder()
            .itemInputs(
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 8, 32107),
                GT_ModHandler.getModItem("123Technology", "socNor", 16)
            )
            .itemOutputs(
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 8, 32097)
            )
            .fluidInputs(
                new FluidStack(bio, 2304),
                new FluidStack(FluidRegistry.getFluidID("molten.chromaticglass"), 16 * 72)
            )
            .noOptimize()
            .duration(5120 * 64)
            .eut(7680 * 64)
            .addTo(ISA);
    }
}
