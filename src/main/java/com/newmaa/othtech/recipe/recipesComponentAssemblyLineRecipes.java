package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.utils.Utils.setStackSize;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.materials.liquids;
import com.newmaa.othtech.utils.RecipeBuilder;

import goodgenerator.api.recipe.GoodGeneratorRecipeMaps;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;
import tectech.recipe.TTRecipeAdder;

public class recipesComponentAssemblyLineRecipes implements IRecipePool {

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
        ItemStack coil = GTModHandler.getModItem("gregtech", "gt.blockcasings5", 1, 13);

        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem("eternalsingularity", "combined_singularity", 48, 15),
                setStackSize(coil, 6 * 64),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 48, 4141))
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
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Electric_Motor_MAX.get(48),
                setStackSize(
                    GTModHandler.getModItem("tectech", "gt.spacetime_compression_field_generator", 1, 8),
                    64 + 32),
                setStackSize(coil, 6 * 64),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 48, 4141),
                GTUtility.getIntegratedCircuit(3))
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
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Electric_Motor_MAX.get(48),
                setStackSize(coil, 6 * 64),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 48, 4141),
                GTUtility.getIntegratedCircuit(2))
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
        GTValues.RA.stdBuilder()
            .itemInputs(
                setStackSize(ItemList.Electric_Motor_MAX.get(1), 96),
                setStackSize(coil, 6 * 64),
                setStackSize(GTModHandler.getModItem("tectech", "gt.time_acceleration_field_generator", 1, 8), 64 + 32),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 48, 4141),
                GTUtility.getIntegratedCircuit(5))
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
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Electric_Motor_MAX.get(48),
                GTModHandler.getModItem("GoodGenerator", "circuitWrap", 12, 14),
                setStackSize(coil, 6 * 64),
                setStackSize(GTModHandler.getModItem("tectech", "gt.stabilisation_field_generator", 1, 8), 96),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 4141),
                setStackSize(OTHItemList.glassSingularityM.get(1), 192),
                GTUtility.getIntegratedCircuit(6))
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
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Electric_Motor_MAX.get(48),
                GTModHandler.getModItem("GoodGenerator", "circuitWrap", 12, 14),
                GTModHandler.getModItem("gregtech", "gt.blockframes", 48, 141),
                setStackSize(coil, 6 * 64),
                setStackSize(GTModHandler.getModItem("tectech", "gt.stabilisation_field_generator", 1, 8), 96),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 4141),
                setStackSize(OTHItemList.glassSingularityM.get(1), 192),
                GTUtility.getIntegratedCircuit(7))
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
        GTValues.RA.stdBuilder()
            .itemInputs(
                setStackSize(ItemList.Electric_Motor_MAX.get(1), 96),
                setStackSize(ItemList.Electric_Piston_MAX.get(1), 48),
                GTModHandler.getModItem("GoodGenerator", "circuitWrap", 6, 14),
                GTModHandler.getModItem("GoodGenerator", "circuitWrap", 12, 13),
                GTModHandler.getModItem("GoodGenerator", "circuitWrap", 24, 12),
                GTModHandler.getModItem("tectech", "item.tm.itemAstralArrayFabricator", 1),
                GTUtility.getIntegratedCircuit(4))
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
        GTValues.RA.stdBuilder()
            .itemInputs(
                setStackSize(ItemList.Emitter_MAX.get(1), 64 * 3),
                GTModHandler.getModItem("tectech", "item.tm.itemAstralArrayFabricator", 50),
                GTModHandler.getModItem("gregtech", "gt.blockframes", 48, 141),
                GTModHandler.getModItem("GoodGenerator", "circuitWrap", 24, 14),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 4141),
                setStackSize(OTHItemList.glassSingularityM.get(1), 192),
                setStackSize(OTHItemList.machineSingularityM.get(1), 192),
                GTUtility.getIntegratedCircuit(7))
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
        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTModHandler.getModItem("GoodGenerator", "componentAssemblylineCasing", 1, 12),
            (int) TierEU.UMV,
            4096,
            (int) TierEU.UMV,
            123,
            new ItemStack[] { GTModHandler.getModItem("gregtech", "gt.blockframes", 1, 141),
                GTModHandler.getModItem("dreamcraft", "item.StargateShieldingFoil", 2),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 12, 22143),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 12, 22139), ItemList.Robot_Arm_MAX.get(8),
                ItemList.Electric_Piston_MAX.get(10), ItemList.Electric_Motor_MAX.get(16),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 4, 31143),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 16, 20143),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 4, 31139),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 16, 20139),
                GTModHandler.getModItem("tectech", "item.tm.itemAstralArrayFabricator", 2),
                GTModHandler.getModItem("gregtech", "gt.blockcasings5", 64, 13),
                OTHItemList.machineSingularityM.get(12),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 16, 4139),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 16, 4141) },
            new FluidStack[] { new FluidStack(Um, 114000),
                new FluidStack(FluidRegistry.getFluid("molten.whitedwarfmatter"), 514000),
                new FluidStack(FluidRegistry.getFluid("molten.blackdwarfmatter"), 191000),
                new FluidStack(FluidRegistry.getFluidID("rawstarmatter"), 9810) },
            GTModHandler.getModItem("GoodGenerator", "componentAssemblylineCasing", 1, 13),
            2460,
            2000000000);

    }
}
