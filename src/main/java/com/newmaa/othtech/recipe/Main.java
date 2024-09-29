package com.newmaa.othtech.recipe;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.github.technus.tectech.recipe.TT_recipeAdder;

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

public class Main implements IRecipePool {

    @Override
    public void loadRecipes()

    {

        Fluid water = FluidRegistry.getFluid("water");
        Fluid Ewater = FluidRegistry.getFluid("grade8purifiedwater");
        Fluid songyou = FluidRegistry.getFluid("pineoil");
        Fluid white = FluidRegistry.getFluid("molten.whitedwarfmatter");
        Fluid cuihuaji = FluidRegistry.getFluid("exciteddtsc");
        final RecipeMap<?> Nan = RecipeMaps.nanoForgeRecipes;
        final RecipeMap<?> Assem = RecipeMaps.assemblerRecipes;
        final RecipeMap<?> Chem = RecipeMaps.multiblockChemicalReactorRecipes;

        // dustIrOsSm assembler
        // GT_Values.RA.stdBuilder()
        // .metadata(RESEARCH_ITEM, ItemList.Field_Generator_EV.get(1))
        // .metadata(RESEARCH_TIME, 123 * MINUTES)
        // .itemInputs(
        // GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 1),
        // GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 1),
        // GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Samarium, 1))
        // .fluidInputs(new FluidStack(water, 123123))
        // .itemOutputs(GT_ModHandler.getModItem("123Technology", "dustIrOsSm", 1))
        // .noOptimize()
        // .eut(123123)
        // .duration(8100)
        // .addTo(GT_RecipeConstants.AssemblyLine);
        TT_recipeAdder.addResearchableAssemblylineRecipe(
            ItemList.Field_Generator_EV.get(1),
            (int) TierEU.UV,
            114,
            (int) TierEU.UV,
            16,
            new ItemStack[] { GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 1),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 1),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 1),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 1),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 2),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 2),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 2),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 2),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Samarium, 3),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Samarium, 3),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Samarium, 3),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Samarium, 3) },
            new FluidStack[] { new FluidStack(water, 123123), new FluidStack(water, 123123),
                new FluidStack(water, 123123), new FluidStack(water, 123123) },
            GT_ModHandler.getModItem("123Technology", "dustIrOsSm", 1),
            114514,
            1919810);
        // isaforgea
        TT_recipeAdder.addResearchableAssemblylineRecipe(
            GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 31027),
            (int) 409600000,
            32000,
            (int) 123123123,
            514,
            new ItemStack[] { GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 31027),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 31028),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 1004),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 13532),
                GT_ModHandler.getModItem("dreamcraft", "item.QuantumCircuit", 32),
                GT_ModHandler.getModItem("dreamcraft", "item.QuantumCircuit", 32),
                GT_ModHandler.getModItem("tectech", "gt.spacetime_compression_field_generator", 32),
                GT_ModHandler.getModItem("tectech", "gt.time_acceleration_field_generator", 32),
                GT_ModHandler.getModItem("tectech", "gt.stabilisation_field_generator", 32),
                GT_ModHandler.getModItem("eternalsingularity", "eternal_singularity", 64),
                ItemList.Field_Generator_UMV.get(64), GT_ModHandler.getModItem("miscutils", "milledMonazite", 64),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 2083),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 2092),
                GT_ModHandler.getModItem("123Technology", "dustIrOsSm", 64),
                GT_ModHandler.getModItem("123Technology", "LookNEIdust", 64) },
            new FluidStack[] { new FluidStack(white, 4608), new FluidStack(cuihuaji, 123123),
                new FluidStack(Ewater, 4096000), new FluidStack(songyou, 32000000) },
            GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 23521),
            105000,
            123123123);
        // IsaBee
        GT_Values.RA.stdBuilder()
            .itemInputs(
                GT_ModHandler.getModItem("miscutils", "MU-metaitem.01", 0, 32140),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 31027),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 31028),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4585),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4586),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4139))
            .itemOutputs(GT_ModHandler.getModItem("123Technology", "IsaBee", 1))
            .fluidInputs(new FluidStack(songyou, 256000))
            .noOptimize()
            .duration(500 * 20)
            .eut(Integer.MAX_VALUE)
            .specialValue(3)
            .addTo(Nan);
        // Mega QFT
        TT_recipeAdder.addResearchableAssemblylineRecipe(
            GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 31151),
            512000000,
            4096,
            123123123,
            256,
            new ItemStack[] { GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 31151),
                GT_ModHandler.getModItem("miscutils", "gtplusplus.blockcasings.5", 64, 10),
                GT_ModHandler.getModItem("miscutils", "gtplusplus.blockcasings.5", 64, 14),
                GT_ModHandler.getModItem("dreamcraft", "item.QuantumCircuit", 64),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 32417),
                GT_ModHandler.getModItem("dreamcraft", "item.MysteriousCrystalLens", 64),
                GT_ModHandler.getModItem("123Technology", "dustIrOsSm", 64),
                GT_ModHandler.getModItem("123Technology", "LookNEIdust", 64),
                GT_ModHandler.getModItem("appliedenergistics2", "item.ItemExtremeStorageCell.Singularity", 1),
                GT_ModHandler.getModItem("appliedenergistics2", "item.ItemExtremeStorageCell.Singularity", 1),
                GT_ModHandler.getModItem("appliedenergistics2", "item.ItemExtremeStorageCell.Singularity", 1),
                GT_ModHandler.getModItem("miscutils", "MU-metaitem.01", 64, 32105),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 32429),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 32428),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 32427),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 4581), },
            new FluidStack[] { new FluidStack(FluidRegistry.getFluidID("molten.spacetime"), 36000000),
                new FluidStack(FluidRegistry.getFluidID("exciteddtsc"), 256000),
                new FluidStack(FluidRegistry.getFluidID("plasma.neptunium"), 256000),
                new FluidStack(FluidRegistry.getFluidID("plasma.fermium"), 256000) },
            GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 23526),
            1234 * 20,
            123123123);
        // 9in1
        GT_Values.RA.stdBuilder()
            .itemOutputs(GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 23520))
            .itemInputs(
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 860),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 31027),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 31028),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 995),
                ItemList.Robot_Arm_LuV.get(16),
                GT_OreDictUnificator.get(OrePrefixes.circuit, Materials.Superconductor, 64),
                GT_Utility.getIntegratedCircuit(17))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("molten.indalloy140"), 128 * 144))
            .noOptimize()
            .duration(123 * 20)
            .eut(32000)
            .addTo(Assem);
        // MEBF
        GT_Values.RA.stdBuilder()
            .itemOutputs(GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 23527))
            .itemInputs(
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 963),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 963),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 963),
                ItemList.Field_Generator_LuV.get(16),
                GT_OreDictUnificator.get(OrePrefixes.circuit, Materials.Superconductor, 64),
                GT_Utility.getIntegratedCircuit(17))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("molten.indalloy140"), 128 * 144))
            .noOptimize()
            .duration(123 * 20)
            .eut(32000)
            .addTo(Assem);
        // Freezer
        GT_Values.RA.stdBuilder()
            .itemOutputs(GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 23528))
            .itemInputs(
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 910),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 910),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 910),
                ItemList.Electric_Piston_LuV.get(16),
                GT_OreDictUnificator.get(OrePrefixes.circuit, Materials.Superconductor, 64),
                GT_Utility.getIntegratedCircuit(17))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("molten.indalloy140"), 128 * 144))
            .noOptimize()
            .duration(123 * 20)
            .eut(32000)
            .addTo(Assem);
        // SINOPEC
        GT_Values.RA.stdBuilder()
            .itemOutputs(GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 23533))
            .itemInputs(
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 16, 31021),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 16, 1160),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 16, 1169),
                ItemList.Robot_Arm_IV.get(8),
                ItemList.Pump_IV.get(8),
                GT_OreDictUnificator.get(OrePrefixes.circuit, Materials.Ultimate, 16),
                GT_Utility.getIntegratedCircuit(17))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("molten.tungstensteel"), 128 * 144))
            .noOptimize()
            .duration(123 * 20)
            .eut(8000)
            .addTo(Assem);
        // CHEM
        GT_Values.RA.stdBuilder()
            .itemOutputs(GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 23534))
            .itemInputs(
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 2, 13366),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 16, 998),
                ItemList.Electric_Motor_LuV.get(16),
                ItemList.Electric_Pump_LuV.get(16),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 4, 31030),
                GT_ModHandler.getModItem("miscutils", "item.BasicAgrichemItem", 64, 13),
                GT_OreDictUnificator.get(OrePrefixes.circuit, Materials.Elite, 16),
                GT_OreDictUnificator.get(OrePrefixes.circuit, Materials.Master, 16),
                GT_OreDictUnificator.get(OrePrefixes.circuit, Materials.Ultimate, 16),
                GT_Utility.getIntegratedCircuit(17))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("molten.zeron100"), 4 * 144))
            .noOptimize()
            .duration(123 * 20)
            .eut(12300)
            .addTo(Assem);
        // Indium Beeyonds Chem From GTNH 2.6.1
        GT_Values.RA.stdBuilder()
            .fluidInputs(FluidRegistry.getFluidStack("phtalicacid", 2688))
            .itemInputs(GT_ModHandler.getModItem("gregtech", "gt.comb", 4, 159))
            .itemOutputs(GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 4, 6056))
            .noOptimize()
            .duration(288)
            .eut(98304)
            .addTo(Chem);
        // Mega EEC
        TT_recipeAdder.addResearchableAssemblylineRecipe(
            GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 14201),
            5120000,
            4096,
            (int) TierEU.UEV,
            64,
            new ItemStack[] { GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 14201),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 14201),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 14201),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 14201),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 16, 4493),
                GT_ModHandler.getModItem("miscutils", "blockFrameGtHypogen", 16),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 16, 4491),
                GT_ModHandler.getModItem("tectech", "gt.blockcasingsTT", 16, 9), ItemList.Robot_Arm_UEV.get(64),
                ItemList.Robot_Arm_UEV.get(64), GT_OreDictUnificator.get(OrePrefixes.circuit, Materials.Optical, 64),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 4054),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 24, 21397), new ItemStack(Items.skull, 64, 1),
                GT_ModHandler.getModItem("123Technology", "dustIrOsSm", 64),
                GT_ModHandler.getModItem("eternalsingularity", "eternal_singularity", 4), },
            new FluidStack[] { new FluidStack(FluidRegistry.getFluidID("molten.infinity"), 123 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.quantium"), 123 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.neutronium"), 123 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.mutatedlivingsolder"), 123 * 144) },
            GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 23536),
            1200 * 20,
            (int) TierEU.UIV);
        // TSSF
        GT_Values.RA.stdBuilder()
            .itemOutputs(GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 23535))
            .fluidInputs(FluidRegistry.getFluidStack("molten.solderingalloy", 123 * 144))
            .itemInputs(
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 4, 12730),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 8, 791),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 16, 861),
                ItemList.Sensor_LuV.get(8),
                ItemList.Electric_Pump_LuV.get(8),
                ItemList.Electric_Motor_LuV.get(16),
                ItemList.Conveyor_Module_LuV.get(8),
                ItemList.Robot_Arm_LuV.get(4),
                GT_OreDictUnificator.get(OrePrefixes.circuit, Materials.Ultimate, 24),
                GT_Utility.getIntegratedCircuit(17))
            .noOptimize()
            .duration(123 * 20)
            .eut(TierEU.IV)
            .addTo(Assem);
        // 9In1
        GT_Values.RA.stdBuilder()
            .itemOutputs(GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 860))
            .fluidInputs(FluidRegistry.getFluidStack("molten.staballoy", 2 * 144))
            .itemInputs(
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 245),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 295),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 555),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 505),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 515),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 275),
                GT_ModHandler.getModItem("miscutils", "blockProjectBench", 1))
            .noOptimize()
            .duration(4 * 20)
            .eut(TierEU.IV)
            .addTo(Assem);
        // 78water
        GT_Values.RA.stdBuilder()
            .itemOutputs(GT_ModHandler.getModItem("miscutils", "item.BasicGenericChemItem", 1, 30))
            .fluidInputs(FluidRegistry.getFluidStack("molten.shirabon", 92160))
            .itemInputs(
                GT_Utility.getIntegratedCircuit(10),
                GT_ModHandler.getModItem("miscutils", "item.BasicAgrichemItem", 1, 13),
                GT_ModHandler.getModItem("bartworks", "gt.bwMetaGeneratedlens", 64, 25),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 4054),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 32722))
            .noOptimize()
            .duration(60 * 20)
            .eut(TierEU.UMV)
            .addTo(Assem);
        // 二硫化碳
        GT_Values.RA.stdBuilder()
            .itemInputs(
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 64),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 32))
            .fluidOutputs(FluidRegistry.getFluidStack("carbondisulfide", 32000))
            .noOptimize()
            .duration(123 * 20)
            .eut(30)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
        //Sun
        GT_Values.RA.stdBuilder()
            .itemInputs(
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 3)
            )
            .itemOutputs(
                GT_ModHandler.getModItem("123Technology", "itemEnqing", 1)
            )
            .noOptimize()
            .duration(2024 * 20)
            .eut(194899)
            .addTo(RecipeMaps.laserEngraverRecipes);
        //SunFactory
        TT_recipeAdder.addResearchableAssemblylineRecipe(
            GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 23520),
            64000000,
            10240,
            (int) TierEU.UMV,
            640,
            new ItemStack[] {
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 23520),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 23527),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 23528),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 10886),
                GT_ModHandler.getModItem("123Technology", "itemEnqing", 64),
                GT_ModHandler.getModItem("123Technology", "itemEnqing", 64),
                GT_ModHandler.getModItem("123Technology", "itemEnqing", 64),
                GT_ModHandler.getModItem("123Technology", "itemEnqing", 64),
                ItemList.Robot_Arm_UMV.get(64),
                GT_OreDictUnificator.get(OrePrefixes.circuit, Materials.Quantum, 64),
                GT_OreDictUnificator.get(OrePrefixes.circuit, Materials.Quantum, 64),
                ItemList.Field_Generator_UMV.get(64),
                new ItemStack(Items.bread, 64),
                new ItemStack(Items.nether_star, 64),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 15297),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 15197),

            },
            new FluidStack[] {
                new FluidStack(FluidRegistry.getFluidID("molten.gold"), 114 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.gold"), 514 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.gold"), 1919 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.mutatedlivingsolder"), 810 * 144) },
            GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 23537),
            1984 * 20,
            (int) TierEU.UXV);
    }
}
