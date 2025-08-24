package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.utils.Utils.setStackSize;

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
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

public class RecipesCircuit implements IRecipePool {

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
            .itemOutputs(OTHItemList.boardTransM.get(2))
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 22588),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 4581),
                GTModHandler.getModItem("tectech", "gt.spacetime_compression_field_generator", 8

                ))
            .fluidInputs(new FluidStack(dtsc, 2000))

            .duration(40 * 20)
            .eut(eut)
            .addTo(MQFT);
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.capTransM.get(2))
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 22397),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Exotic, 4),
                GTModHandler.getModItem("tectech", "gt.stabilisation_field_generator", 2))
            .fluidInputs(new FluidStack(dtsc, 2000))

            .duration(40 * 20)
            .eut(eut)
            .addTo(MQFT);
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.transTransM.get(4))
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 22139),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Cosmic, 2),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 4581),
                GTModHandler.getModItem("tectech", "gt.spacetime_compression_field_generator", 8, 4),
                ItemList.Field_Generator_UXV.get(1),
                ItemList.Sensor_UXV.get(1))
            .fluidInputs(new FluidStack(dtsc, 4000))

            .duration(80 * 20)
            .eut(eut)
            .addTo(MQFT);
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.induTransM.get(4))
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 22143),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Cosmic, 2),
                OTHItemList.beeISAM.get(64),
                OTHItemList.leCasimirM.get(16),
                GTModHandler.getModItem("tectech", "item.tm.itemAstralArrayFabricator", 1))
            .fluidInputs(new FluidStack(dtsc, 4000))

            .duration(80 * 20)
            .eut(eut)
            .addTo(MQFT);
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.resTransM.get(4))
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 22588),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Exotic, 4),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 4581),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 2089),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 2089),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 2089),
                ItemList.Field_Generator_UMV.get(1))
            .fluidInputs(new FluidStack(dtsc, 4000))

            .duration(80 * 20)
            .eut(eut)
            .addTo(MQFT);
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.dioTransM.get(4))
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Cosmic, 2),
                OTHItemList.beeISAM.get(16),
                GTModHandler.getModItem("tectech", "gt.time_acceleration_field_generator", 8, 4),
                ItemList.Field_Generator_UXV.get(1),
                ItemList.Emitter_UXV.get(2))
            .fluidInputs(new FluidStack(dtsc, 4000))

            .duration(80 * 20)
            .eut(eut)
            .addTo(MQFT);
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.leCasimirM.get(16))
            .itemInputs(
                OTHItemList.finCasimirM.get(16),
                OTHItemList.beeISAM.get(16),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 4141),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 32174))
            .fluidInputs(new FluidStack(dtsc, 256 * 144), BWLiquids.Stars.getFluidOrGas(16 * 144))

            .duration(256 * 20)
            .eut(eut)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.finCasimirM.get(16))
            .itemInputs(
                OTHItemList.boardCasimirM.get(16),
                OTHItemList.beeISAM.get(4),
                OTHItemList.beeMagM.get(4),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 29583),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 29581),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 29143),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 23143),
                GTModHandler.getModItem("tectech", "gt.stabilisation_field_generator", 2, 4))
            .fluidInputs(new FluidStack(dtsc, 256 * 144), BWLiquids.Stars.getFluidOrGas(12 * 144))

            .duration(256 * 20)
            .eut(eut)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.boardCasimirM.get(16))
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 18583),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19583))
            .fluidInputs(
                new FluidStack(dtsc, 64 * 144),
                BWLiquids.Stars.getFluidOrGas(8 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.magmatter"), 64 * 144),
                new FluidStack(
                    FluidRegistry.getFluidID("molten.magnetohydrodynamicallyconstrainedstarmatter"),
                    64 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.universium"), 64 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.spacetime"), 64 * 144))

            .duration(128 * 20)
            .eut(eut)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.energyHatchSpacetimeM.get(4))
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 4, 32145),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 15091),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 346),
                ItemList.Emitter_UMV.get(16))
            .fluidInputs(new FluidStack(dtsc, 16 * 144), BWLiquids.Stars.getFluidOrGas(4 * 144))

            .duration(512 * 20)
            .eut(eut)
            .addTo(ISA);
        // UIV
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32174))
            .itemInputs(
                OTHItemList.boardTransM.get(2),
                OTHItemList.energyHatchSpacetimeM.get(1),
                OTHItemList.capTransM.get(16),
                OTHItemList.resTransM.get(16),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 2089))
            .fluidInputs(BWLiquids.Stars.getFluidOrGas(4 * 144))

            .duration(60 * 20)
            .eut(TierEU.MAX)
            .addTo(ISA);
        // UMV
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32175))
            .itemInputs(
                OTHItemList.boardTransM.get(4),
                OTHItemList.energyHatchSpacetimeM.get(4),
                OTHItemList.transTransM.get(4),
                OTHItemList.capTransM.get(32),
                OTHItemList.resTransM.get(32),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 2090))
            .fluidInputs(BWLiquids.Stars.getFluidOrGas(8 * 144))

            .duration(120 * 20)
            .eut(TierEU.MAX)
            .addTo(ISA);
        // UXV
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32176))
            .itemInputs(
                OTHItemList.boardTransM.get(8),
                OTHItemList.leCasimirM.get(4),
                OTHItemList.transTransM.get(32),
                OTHItemList.capTransM.get(64),
                OTHItemList.resTransM.get(64),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 2091))
            .fluidInputs(BWLiquids.Stars.getFluidOrGas(16 * 144))

            .duration(240 * 20)
            .eut(TierEU.MAX)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32176))
            .itemInputs(
                OTHItemList.boardTransM.get(8),
                OTHItemList.socInfM.get(4),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 16, 2091))
            .fluidInputs(BWLiquids.Stars.getFluidOrGas(24 * 144))

            .duration(120 * 20)
            .eut(TierEU.MAX)
            .addTo(ISA);
        // MAX
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32177))
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.blockframes", 4, 583),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 2, 32176),
                OTHItemList.induTransM.get(16),
                OTHItemList.dioTransM.get(16),
                OTHItemList.transTransM.get(64),
                OTHItemList.capTransM.get(64),
                OTHItemList.resTransM.get(64),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 2092))
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
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19583))
            .fluidInputs(BWLiquids.Stars.getFluidOrGas(10 * 144))

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

            .duration(30 * 20)
            .eut(30)
            .addTo(Assem);
        // Piko $ Quantum
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32176),
                GTUtility.getIntegratedCircuit(16))
            .itemOutputs(GTModHandler.getModItem("dreamcraft", "item.QuantumCircuit", 1))

            .duration(20)
            .eut(114514)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32175),
                GTUtility.getIntegratedCircuit(16))
            .itemOutputs(GTModHandler.getModItem("dreamcraft", "item.PikoCircuit", 1))

            .duration(20)
            .eut(114514)
            .addTo(ISA);
        // general soc
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.socNorM.get(16))
            .itemInputs(
                ItemList.Field_Generator_UMV.get(0),
                GTUtility.getIntegratedCircuit(1),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32722),
                GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedgemExquisite", 4, 11499))
            .fluidInputs(
                new FluidStack(FluidRegistry.getFluidID("ic2uumatter"), 1600),
                new FluidStack(FluidRegistry.getFluidID("molten.hypogen"), 36),
                new FluidStack(FluidRegistry.getFluidID("biohmediumsterilized"), 3200))

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
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 32106),
                OTHItemList.socNorM.get(1))
            .itemOutputs(setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 32081), 4096))
            .fluidInputs(new FluidStack(i140, 72))

            .duration(20)
            .eut(30)
            .addTo(ISA);
        // LV
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 8, 32106),
                OTHItemList.socNorM.get(1))
            .itemOutputs(setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32078), 2048))
            .fluidInputs(new FluidStack(i140, 144))

            .duration(80)
            .eut(120)
            .addTo(ISA);
        // MV
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(3),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 16, 32106),
                OTHItemList.socNorM.get(1))
            .itemOutputs(setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32080), 1024))
            .fluidInputs(new FluidStack(i140, 576))

            .duration(320)
            .eut(480)
            .addTo(ISA);
        // HV
        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 16, 32102), OTHItemList.socNorM.get(1))
            .itemOutputs(setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32082), 512))
            .fluidInputs(new FluidStack(i140, 2304))

            .duration(1280)
            .eut(1920)
            .addTo(ISA);
        // EV
        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 32, 32103), OTHItemList.socNorM.get(2))
            .itemOutputs(setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32085), 256))
            .fluidInputs(new FluidStack(i140, 2304 * 4))

            .duration(5120)
            .eut(7680)
            .addTo(ISA);
        // IV
        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 32, 32104), OTHItemList.socNorM.get(2))
            .itemOutputs(setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 32089), 128))
            .fluidInputs(new FluidStack(bio, 576))

            .duration(5120 * 4)
            .eut(7680 * 4)
            .addTo(ISA);
        // LuV
        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 16, 32105), OTHItemList.socNorM.get(4))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 32092))
            .fluidInputs(new FluidStack(bio, 576))

            .duration(5120 * 16)
            .eut(7680 * 16)
            .addTo(ISA);
        // ZPM
        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 8, 32107), OTHItemList.socNorM.get(16))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 32, 32097))
            .fluidInputs(
                new FluidStack(bio, 2304),
                new FluidStack(FluidRegistry.getFluidID("molten.chromaticglass"), 16 * 72))

            .duration(5120 * 64)
            .eut(7680 * 64)
            .addTo(ISA);
    }
}
