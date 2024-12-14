package com.newmaa.othtech.recipe;

import static gregtech.api.util.GTModHandler.addCraftingRecipe;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.newmaa.othtech.common.OTHItemList;

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
        // GTValues.RA.stdBuilder()
        // .metadata(RESEARCH_ITEM, ItemList.Field_Generator_EV.get(1))
        // .metadata(RESEARCH_TIME, 123 * MINUTES)
        // .itemInputs(
        // GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 1),
        // GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 1),
        // GTOreDictUnificator.get(OrePrefixes.dust, Materials.Samarium, 1))
        // .fluidInputs(new FluidStack(water, 123123))
        // .itemOutputs(GTModHandler.getModItem("123Technology", "dustIrOsSm", 1))
        // .noOptimize()
        // .eut(123123)
        // .duration(8100)
        // .addTo(GTRecipeConstants.AssemblyLine);
        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemList.Field_Generator_EV.get(1),
            (int) TierEU.UV,
            114,
            (int) TierEU.UV,
            16,
            new ItemStack[] { GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Samarium, 3),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Samarium, 3),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Samarium, 3),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Samarium, 3) },
            new FluidStack[] { new FluidStack(water, 123123) },
            GTModHandler.getModItem("123Technology", "dustIrOsSm", 1),
            123,
            1919810);
        // isaforgea
        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 31027),
            (int) 409600000,
            32000,
            (int) 123123123,
            514,
            new ItemStack[] { GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 31027),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 31028),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 1004),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 13532),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Cosmic, 64),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Cosmic, 64),
                GTModHandler.getModItem("tectech", "gt.spacetime_compression_field_generator", 32),
                GTModHandler.getModItem("tectech", "gt.time_acceleration_field_generator", 32),
                GTModHandler.getModItem("tectech", "gt.stabilisation_field_generator", 32),
                GTModHandler.getModItem("eternalsingularity", "eternal_singularity", 64),
                ItemList.Field_Generator_UMV.get(64), GTModHandler.getModItem("miscutils", "milledMonazite", 64),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 2083),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 2092),
                GTModHandler.getModItem("123Technology", "dustIrOsSm", 64),
                GTModHandler.getModItem("123Technology", "LookNEIdust", 64) },
            new FluidStack[] { new FluidStack(white, 4608), new FluidStack(cuihuaji, 123123),
                new FluidStack(Ewater, 4096000), new FluidStack(songyou, 32000000) },
            GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 23521),
            105000,
            123123123);
        // IsaBee
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTModHandler.getModItem("miscutils", "MU-metaitem.01", 0, 32140),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 31027),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 31028),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4585),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4586),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4139))
            .itemOutputs(GTModHandler.getModItem("123Technology", "IsaBee", 1))
            .fluidInputs(new FluidStack(songyou, 256000))
            .noOptimize()
            .duration(500 * 20)
            .eut(Integer.MAX_VALUE)
            .specialValue(3)
            .addTo(Nan);
        // Mega QFT
        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 31151),
            512000000,
            4096,
            123123123,
            256,
            new ItemStack[] { GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 31151),
                GTModHandler.getModItem("miscutils", "gtplusplus.blockcasings.5", 64, 10),
                GTModHandler.getModItem("miscutils", "gtplusplus.blockcasings.5", 64, 14),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Cosmic, 64),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 32417),
                GTModHandler.getModItem("dreamcraft", "item.MysteriousCrystalLens", 64),
                GTModHandler.getModItem("123Technology", "dustIrOsSm", 64),
                GTModHandler.getModItem("123Technology", "LookNEIdust", 64),
                GTModHandler.getModItem("appliedenergistics2", "item.ItemExtremeStorageCell.Singularity", 1),
                GTModHandler.getModItem("appliedenergistics2", "item.ItemExtremeStorageCell.Singularity", 1),
                GTModHandler.getModItem("appliedenergistics2", "item.ItemExtremeStorageCell.Singularity", 1),
                GTModHandler.getModItem("miscutils", "MU-metaitem.01", 64, 32105),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 32429),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 32428),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 32427),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 4581), },
            new FluidStack[] { new FluidStack(FluidRegistry.getFluidID("molten.spacetime"), 36000000),
                new FluidStack(FluidRegistry.getFluidID("exciteddtsc"), 256000),
                new FluidStack(FluidRegistry.getFluidID("plasma.neptunium"), 256000),
                new FluidStack(FluidRegistry.getFluidID("plasma.fermium"), 256000) },
            GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 23526),
            1234 * 20,
            123123123);
        // 9in1
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 23520))
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 860),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 31027),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 31028),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 995),
                ItemList.Robot_Arm_LuV.get(16),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Superconductor, 64),
                GTUtility.getIntegratedCircuit(17))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("molten.indalloy140"), 128 * 144))
            .noOptimize()
            .duration(123 * 20)
            .eut(32000)
            .addTo(Assem);
        // MEBF
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 23527))
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 963),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 963),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 963),
                ItemList.Field_Generator_LuV.get(16),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Superconductor, 64),
                GTUtility.getIntegratedCircuit(17))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("molten.indalloy140"), 128 * 144))
            .noOptimize()
            .duration(123 * 20)
            .eut(32000)
            .addTo(Assem);
        // Freezer
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 23528))
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 910),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 910),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 910),
                ItemList.Electric_Piston_LuV.get(16),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Superconductor, 64),
                GTUtility.getIntegratedCircuit(17))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("molten.indalloy140"), 128 * 144))
            .noOptimize()
            .duration(123 * 20)
            .eut(32000)
            .addTo(Assem);
        // SINOPEC
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 23533))
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 16, 31021),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 16, 1160),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 16, 1169),
                ItemList.Robot_Arm_IV.get(8),
                ItemList.Electric_Pump_IV.get(8),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Ultimate, 16),
                GTUtility.getIntegratedCircuit(17))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("molten.tungstensteel"), 128 * 144))
            .noOptimize()
            .duration(123 * 20)
            .eut(8000)
            .addTo(Assem);
        // CHEM
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 23534))
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 2, 13366),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 16, 998),
                ItemList.Electric_Motor_LuV.get(16),
                ItemList.Electric_Pump_LuV.get(16),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 4, 31030),
                GTModHandler.getModItem("miscutils", "item.BasicAgrichemItem", 64, 13),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Elite, 16),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Master, 16),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Ultimate, 16),
                GTUtility.getIntegratedCircuit(17))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("molten.zeron100"), 4 * 144))
            .noOptimize()
            .duration(123 * 20)
            .eut(12300)
            .addTo(Assem);
        // Indium Beeyonds Chem From GTNH 2.6.1
        GTValues.RA.stdBuilder()
            .fluidInputs(FluidRegistry.getFluidStack("phtalicacid", 2688))
            .itemInputs(GTModHandler.getModItem("gregtech", "gt.comb", 4, 159))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 4, 6056))
            .noOptimize()
            .duration(288)
            .eut(98304)
            .addTo(Chem);
        // Mega EEC
        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 14201),
            5120000,
            4096,
            (int) TierEU.UEV,
            64,
            new ItemStack[] { GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 14201),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 14201),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 14201),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 14201),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 16, 4493),
                GTModHandler.getModItem("miscutils", "blockFrameGtHypogen", 16),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 16, 4491),
                GTModHandler.getModItem("tectech", "gt.blockcasingsTT", 16, 9), ItemList.Robot_Arm_UEV.get(64),
                ItemList.Robot_Arm_UEV.get(64), GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Optical, 64),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 4054),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 24, 21397), new ItemStack(Items.skull, 64, 1),
                GTModHandler.getModItem("123Technology", "dustIrOsSm", 64),
                GTModHandler.getModItem("eternalsingularity", "eternal_singularity", 4), },
            new FluidStack[] { new FluidStack(FluidRegistry.getFluidID("molten.infinity"), 123 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.quantium"), 123 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.neutronium"), 123 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.mutatedlivingsolder"), 123 * 144) },
            GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 23536),
            1200 * 20,
            (int) TierEU.UIV);
        // TSSF
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 23535))
            .fluidInputs(FluidRegistry.getFluidStack("molten.solderingalloy", 123 * 144))
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 4, 12730),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 8, 791),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 16, 861),
                ItemList.Sensor_LuV.get(8),
                ItemList.Electric_Pump_LuV.get(8),
                ItemList.Electric_Motor_LuV.get(16),
                ItemList.Conveyor_Module_LuV.get(8),
                ItemList.Robot_Arm_LuV.get(4),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Ultimate, 24),
                GTUtility.getIntegratedCircuit(17))
            .noOptimize()
            .duration(123 * 20)
            .eut(TierEU.IV)
            .addTo(Assem);
        // 9In1
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 860))
            .fluidInputs(FluidRegistry.getFluidStack("molten.staballoy", 2 * 144))
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 245),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 295),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 555),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 505),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 515),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 275),
                GTModHandler.getModItem("miscutils", "blockProjectBench", 1))
            .noOptimize()
            .duration(4 * 20)
            .eut(TierEU.IV)
            .addTo(Assem);
        // 78water
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("miscutils", "item.BasicGenericChemItem", 1, 30))
            .fluidInputs(FluidRegistry.getFluidStack("molten.shirabon", 92160))
            .itemInputs(
                GTUtility.getIntegratedCircuit(10),
                GTModHandler.getModItem("miscutils", "item.BasicAgrichemItem", 1, 13),
                GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedlens", 64, 25),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 4054),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 32722))
            .noOptimize()
            .duration(60 * 20)
            .eut(TierEU.UMV)
            .addTo(Assem);
        // 二硫化碳
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 32))
            .fluidOutputs(FluidRegistry.getFluidStack("carbondisulfide", 32000))
            .noOptimize()
            .duration(123 * 20)
            .eut(30)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
        // Sun
        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 3))
            .itemOutputs(GTModHandler.getModItem("123Technology", "itemEnqing", 1))
            .noOptimize()
            .duration(2024 * 20)
            .eut(194899)
            .addTo(RecipeMaps.laserEngraverRecipes);
        // SunFactory
        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 23520),
            64000000,
            10240,
            (int) TierEU.UMV,
            640,
            new ItemStack[] { GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 23520),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 23527),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 23528),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 10886),
                GTModHandler.getModItem("123Technology", "itemEnqing", 64),
                GTModHandler.getModItem("123Technology", "itemEnqing", 64),
                GTModHandler.getModItem("123Technology", "itemEnqing", 64),
                GTModHandler.getModItem("123Technology", "itemEnqing", 64), ItemList.Robot_Arm_UMV.get(64),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Cosmic, 64),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Cosmic, 64),
                ItemList.Field_Generator_UMV.get(64), new ItemStack(Items.bread, 64),
                new ItemStack(Items.nether_star, 64),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 15297),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 15197),

            },
            new FluidStack[] { new FluidStack(FluidRegistry.getFluidID("molten.gold"), 114 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.gold"), 514 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.gold"), 1919 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.mutatedlivingsolder"), 810 * 144) },
            GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 23537),
            1984 * 20,
            (int) TierEU.UXV);
        // LCA
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 23538))
            .fluidInputs(FluidRegistry.getFluidStack("molten.staballoy", 16 * 144))
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 1184),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Ultimate, 64),
                ItemList.Electric_Pump_IV.get(64),
                GTModHandler.getModItem("GoodGenerator", "huiCircuit", 16),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 48, 32052),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 4, 12005),
                GTUtility.getIntegratedCircuit(17))
            .noOptimize()
            .duration(4 * 20)
            .eut(TierEU.IV)
            .addTo(Assem);
        // LCR Recipes Test
        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Blocks.dirt, 1))
            .fluidOutputs(FluidRegistry.getFluidStack("water", 1000))
            .noOptimize()
            .duration(1048576)
            .eut(536870912)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
        // CoccOven
        addCraftingRecipe(
            OTHItemList.CoccOven.get(1),
            new Object[] { "AAA", "ABA", "AAA", 'A', ItemList.Firebrick, 'B',
                GTModHandler.getModItem("123Technology", "itemSunLighter", 1)});
        // SunLighter
        addCraftingRecipe(
            GTModHandler.getModItem("123Technology", "itemSunLighter", 1),
            new Object[] { "ABA", "ACA", "ADA", 'A', GTOreDictUnificator.get(OrePrefixes.itemCasing, Materials.Steel, 1), 'B',
                new ItemStack(Items.redstone, 1), 'C', GTOreDictUnificator.get(OrePrefixes.dust, Materials.NetherQuartz, 1), 'D', GTModHandler.getModItem("Railcraft", "firestone.raw", 1)});
    }
}
