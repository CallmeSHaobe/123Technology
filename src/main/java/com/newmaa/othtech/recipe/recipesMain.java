package com.newmaa.othtech.recipe;

import static gregtech.api.enums.Mods.Forestry;
import static gregtech.api.enums.Mods.IndustrialCraft2;
import static gregtech.api.enums.TierEU.*;
import static gregtech.api.recipe.RecipeMaps.assemblerRecipes;
import static gregtech.api.recipe.RecipeMaps.fluidExtractionRecipes;
import static gregtech.api.util.GTModHandler.addCraftingRecipe;
import static gregtech.api.util.GTModHandler.getModItem;
import static gregtech.api.util.GTRecipeBuilder.HOURS;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;
import static gregtech.api.util.GTRecipeConstants.*;
import static tectech.thing.CustomItemList.eM_Power;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.item.ItemLoader;
import com.newmaa.othtech.common.materials.liquids;
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
import gregtech.common.items.CombType;
import gregtech.loaders.misc.GTBees;
import tectech.recipe.TTRecipeAdder;

public class recipesMain implements IRecipePool {

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
        final int IDs = 23520;

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
            GTModHandler.getModItem("123Technology", "MetaItemOTH", 1, 0),
            123,
            1919810);
        // isaforgea
        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, IDs + 24),
            (int) 409600000,
            32000,
            (int) 123123123,
            514,
            new ItemStack[] { GTModHandler.getModItem("123Technology", "MetaItemOTH", 64, 13),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, IDs + 24),
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
                GTModHandler.getModItem("123Technology", "MetaItemOTH", 64, 0),
                GTModHandler.getModItem("123Technology", "MetaItemOTH", 64, 1) },
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
            .itemOutputs(GTModHandler.getModItem("123Technology", "MetaItemOTH", 1, 13))
            .fluidInputs(new FluidStack(songyou, 256000))
            .noOptimize()
            .duration(500 * 20)
            .eut(Integer.MAX_VALUE)
            .specialValue(3)
            .addTo(Nan);
        // Mega recipesQFTRecipes
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
                GTModHandler.getModItem("123Technology", "MetaItemOTH", 64, 0),
                GTModHandler.getModItem("123Technology", "MetaItemOTH", 64, 1),
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
        // recipesVacuumFreezerRecipes
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
            .fluidInputs(FluidRegistry.getFluidStack("phtalicacid", 2400))
            .itemInputs(GTUtility.getIntegratedCircuit(17), GTBees.combs.getStackForType(CombType.INDIUM, 4))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 4, 6056))
            .noOptimize()
            .duration(288)
            .eut(12300)
            .addTo(Chem);
        // 232
        RecipeBuilder.builder()
            .itemInputs(
                OTHItemList.dustLookNEIM.get(0),
                Materials.Thorium.getDust(1),
                GTUtility.getIntegratedCircuit(23),
                GTUtility.getIntegratedCircuit(2))
            .itemOutputs(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 30))
            .eut(12300)
            .duration(114)
            .addTo(Assem);
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
                GTModHandler.getModItem("123Technology", "MetaItemOTH", 64, 0),
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
            .itemOutputs(GTModHandler.getModItem("123Technology", "MetaItemOTH", 1, 15))
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
                GTModHandler.getModItem("123Technology", "MetaItemOTH", 64, 15),
                GTModHandler.getModItem("123Technology", "MetaItemOTH", 64, 15),
                GTModHandler.getModItem("123Technology", "MetaItemOTH", 64, 15),
                GTModHandler.getModItem("123Technology", "MetaItemOTH", 64, 15), ItemList.Robot_Arm_UMV.get(64),
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
                GTModHandler.getModItem("123Technology", "itemSunLighter", 1) });
        // SunLighter
        addCraftingRecipe(
            GTModHandler.getModItem("123Technology", "itemSunLighter", 1),
            new Object[] { "ABA", "ACA", "ADA", 'A',
                GTOreDictUnificator.get(OrePrefixes.itemCasing, Materials.Steel, 1), 'B',
                new ItemStack(Items.redstone, 1), 'C',
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.NetherQuartz, 1), 'D',
                GTModHandler.getModItem("Railcraft", "firestone.raw", 1) });
        // HV Hatches
        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 43),
            123123,
            512,
            (int) TierEU.HV,
            1048576,
            new ItemStack[] { GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 209),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 209),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 209),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 209),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 15300),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 4, 1445),
                new ItemStack(ItemLoader.itemLeekBox, 64, 0),
                GTModHandler.getModItem("GoodGenerator", "compactFusionCoil", 16, 2),
                ItemList.Field_Generator_EV.get(64), GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Elite, 64),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Elite, 64), ItemList.Field_Generator_EV.get(64),
                GTModHandler.getModItem("gregtech", "gt.1080k_Space_Coolantcell", 64),
                GTModHandler.getModItem("gregtech", "gt.1080k_Space_Coolantcell", 64),
                GTModHandler.getModItem("gregtech", "gt.1080k_Space_Coolantcell", 64), ItemList.Electric_Pump_HV.get(64)

            },
            new FluidStack[] { new FluidStack(FluidRegistry.getFluidID("molten.solderingalloy"), 64000),
                FluidRegistry.getFluidStack("cryotheum", 64000) },
            OTHItemList.GTTEEnergyULV.get(1),
            1024 * 20,
            (int) TierEU.HV);
        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 33),
            123123,
            512,
            (int) TierEU.HV,
            1048576,
            new ItemStack[] { GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 296),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 296),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 296),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 296),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 15300),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 4, 1445),
                new ItemStack(ItemLoader.itemLeekBox, 64, 0),
                GTModHandler.getModItem("GoodGenerator", "compactFusionCoil", 16, 2),
                ItemList.Field_Generator_EV.get(64), GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Elite, 64),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Elite, 64), ItemList.Field_Generator_EV.get(64),
                GTModHandler.getModItem("gregtech", "gt.1080k_Space_Coolantcell", 64),
                GTModHandler.getModItem("gregtech", "gt.1080k_Space_Coolantcell", 64),
                GTModHandler.getModItem("gregtech", "gt.1080k_Space_Coolantcell", 64), ItemList.Electric_Pump_HV.get(64)

            },
            new FluidStack[] { new FluidStack(FluidRegistry.getFluidID("molten.solderingalloy"), 64000),
                FluidRegistry.getFluidStack("cryotheum", 64000) },
            OTHItemList.GTTEDynamoULV.get(1),
            1024 * 20,
            (int) TierEU.HV);
        // Wood Fusion
        addCraftingRecipe(
            OTHItemList.WoodFusion.get(1),
            new Object[] { "AAA", "ABA", "AAA", 'A', new ItemStack(Blocks.log, 1, 0), 'B',
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 2) });
        // Records
        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Items.gold_ingot, 1), GTUtility.getIntegratedCircuit(1))
            .itemOutputs(new ItemStack(ItemLoader.RecordPRKA, 1))
            .noOptimize()
            .duration(1949 * 20)
            .eut(123)
            .addTo(RecipeMaps.laserEngraverRecipes);
        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Items.gold_ingot, 1), GTUtility.getIntegratedCircuit(2))
            .itemOutputs(new ItemStack(ItemLoader.RecordPRKB, 1))
            .noOptimize()
            .duration(1949 * 20)
            .eut(123)
            .addTo(RecipeMaps.laserEngraverRecipes);
        // LCL
        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Phosphorus, 7))
            .fluidInputs(
                Materials.Hydrogen.getGas(26000),
                Materials.Oxygen.getGas(30000),
                Materials.Nitrogen.getGas(21000))
            .fluidOutputs(liquids.LCL.getFluidOrGas(20000))
            .eut(480)
            .duration(1200 * 20)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
        // ALS
        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 7),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Phosphorus, 16))
            .fluidInputs(
                Materials.Hydrogen.getGas(26000),
                Materials.Oxygen.getGas(30000),
                Materials.Nitrogen.getGas(21000))
            .fluidOutputs(liquids.ALS.getFluidOrGas(20000))
            .eut(480)
            .duration(1200 * 20)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
        // Ayanami Model
        RecipeBuilder.builder()
            .fluidInputs(liquids.LCL.getFluidOrGas(50000))
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 8, 32429),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 8, 32420),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 17472))
            .itemOutputs(GTModHandler.getModItem("123Technology", "modelAyanami", 1))
            .eut(320)
            .duration(40 * 20)
            .addTo(RecipeMaps.assemblerRecipes);
        // Asuka Model
        RecipeBuilder.builder()
            .fluidInputs(liquids.ALS.getFluidOrGas(50000))
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 8, 32428),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 8, 32420),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 17472))
            .itemOutputs(GTModHandler.getModItem("123Technology", "modelAsuka", 1))
            .eut(320)
            .duration(40 * 20)
            .addTo(RecipeMaps.assemblerRecipes);
        // ZPM Module
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 32555),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 32575),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 8, 32697),
                GTModHandler.getModItem("miscutils", "gtplusplus.blockcasings.3", 16, 11),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 32605))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 32598))
            .duration(100 * 20)
            .eut(TierEU.UHV)
            .addTo(RecipeMaps.assemblerRecipes);
        // Record EVA
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GTModHandler.getModItem("123Technology", "modelAyanami", 0),
                new ItemStack(Items.diamond, 3))
            .itemOutputs(
                new ItemStack(ItemLoader.RecordEVAA, 1),
                new ItemStack(ItemLoader.RecordEVAB, 1),
                new ItemStack(ItemLoader.RecordEVAC, 1))
            .duration(20)
            .eut(30)
            .addTo(RecipeMaps.assemblerRecipes);
        // Record Never gonna give you up
        RecipeBuilder.builder()
            .itemOutputs(new ItemStack(ItemLoader.RecordNGGU))
            .itemInputs(GTUtility.getIntegratedCircuit(17), new ItemStack(ItemLoader.itemLeekBox))
            .duration(20)
            .eut(114)
            .addTo(RecipeMaps.assemblerRecipes);
        // MISA FACTORY
        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 31027),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 31028),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 995),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Infinite, 64))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, IDs + 24))
            .fluidInputs(FluidRegistry.getFluidStack("advancedglue", 262144))
            .duration(114514)
            .eut(123123123)
            .addTo(Chem);
        // FOOD GENERATOR
        RecipeBuilder.builder()
            .itemOutputs(OTHItemList.FooD.get(1))
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Electric_Motor_HV.get(64),
                ItemList.Casing_HV.get(16),
                ItemList.LargeSteamTurbine.get(1))
            .duration(123 * 20)
            .eut(123)
            .addTo(Assem);
        // ISAModule Recipes

        // DTPF timing
        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Blocks.cobblestone, 1))
            .itemOutputs(new ItemStack(Blocks.stone, 1))
            .duration(3600 * 64 * 20)
            .eut(7680)
            .addTo(RecipeMaps.plasmaForgeRecipes);
        // Lasers
        // LV

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Emitter_LV.get(64),
                ItemList.Emitter_LV.get(64),
                ItemList.Emitter_LV.get(64),
                ItemList.Emitter_LV.get(64),
                ItemList.Electric_Pump_LV.get(64),
                ItemList.Hatch_Energy_LV.get(64))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, IDs + 30))
            .fluidInputs(Materials.Hydrogen.getPlasma(32))
            .duration(3600 * 20)
            .eut(30)
            .addTo(Assem);
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Sensor_LV.get(64),
                ItemList.Sensor_LV.get(64),
                ItemList.Sensor_LV.get(64),
                ItemList.Sensor_LV.get(64),
                ItemList.Electric_Pump_LV.get(64),
                ItemList.Hatch_Energy_LV.get(64))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, IDs + 31))
            .fluidInputs(Materials.Hydrogen.getPlasma(32))
            .duration(3600 * 20)
            .eut(30)
            .addTo(Assem);
        // MV
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Emitter_MV.get(64),
                ItemList.Emitter_MV.get(64),
                ItemList.Emitter_MV.get(64),
                ItemList.Emitter_MV.get(64),
                ItemList.Electric_Pump_MV.get(64),
                ItemList.Hatch_Energy_MV.get(64))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, IDs + 32))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_MV))
            .duration(3600 * 20)
            .eut(RECIPE_MV)
            .addTo(Assem);
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Sensor_MV.get(64),
                ItemList.Sensor_MV.get(64),
                ItemList.Sensor_MV.get(64),
                ItemList.Sensor_MV.get(64),
                ItemList.Electric_Pump_MV.get(64),
                ItemList.Hatch_Energy_MV.get(64))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, IDs + 33))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_MV))
            .duration(3600 * 20)
            .eut(RECIPE_MV)
            .addTo(Assem);
        // HV
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Emitter_HV.get(64),
                ItemList.Emitter_HV.get(64),
                ItemList.Emitter_HV.get(64),
                ItemList.Emitter_HV.get(64),
                ItemList.Electric_Pump_HV.get(64),
                ItemList.Hatch_Energy_HV.get(64))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, IDs + 34))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_HV))
            .duration(3600 * 20)
            .eut(RECIPE_HV)
            .addTo(Assem);
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Sensor_HV.get(64),
                ItemList.Sensor_HV.get(64),
                ItemList.Sensor_HV.get(64),
                ItemList.Sensor_HV.get(64),
                ItemList.Electric_Pump_HV.get(64),
                ItemList.Hatch_Energy_HV.get(64))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, IDs + 35))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_HV))
            .duration(3600 * 20)
            .eut(RECIPE_HV)
            .addTo(Assem);
        // EV
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Emitter_EV.get(64),
                ItemList.Emitter_EV.get(64),
                ItemList.Emitter_EV.get(64),
                ItemList.Emitter_EV.get(64),
                ItemList.Electric_Pump_EV.get(64),
                ItemList.Hatch_Energy_EV.get(64))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, IDs + 36))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_EV))
            .duration(3600 * 20)
            .eut(RECIPE_EV)
            .addTo(Assem);
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Sensor_EV.get(64),
                ItemList.Sensor_EV.get(64),
                ItemList.Sensor_EV.get(64),
                ItemList.Sensor_EV.get(64),
                ItemList.Electric_Pump_EV.get(64),
                ItemList.Hatch_Energy_EV.get(64))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, IDs + 37))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_EV))
            .duration(3600 * 20)
            .eut(RECIPE_EV)
            .addTo(Assem);
        // IV
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Emitter_IV.get(64),
                ItemList.Emitter_IV.get(64),
                ItemList.Emitter_IV.get(64),
                ItemList.Emitter_IV.get(64),
                ItemList.Electric_Pump_IV.get(64),
                ItemList.Hatch_Energy_IV.get(64))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, IDs + 38))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_IV))
            .duration(3600 * 20)
            .eut(RECIPE_IV)
            .addTo(Assem);
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Sensor_IV.get(64),
                ItemList.Sensor_IV.get(64),
                ItemList.Sensor_IV.get(64),
                ItemList.Sensor_IV.get(64),
                ItemList.Electric_Pump_IV.get(64),
                ItemList.Hatch_Energy_IV.get(64))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, IDs + 39))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_IV))
            .duration(3600 * 20)
            .eut(RECIPE_IV)
            .addTo(Assem);
        // LuV
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Emitter_LuV.get(64),
                ItemList.Emitter_LuV.get(64),
                ItemList.Emitter_LuV.get(64),
                ItemList.Emitter_LuV.get(64),
                ItemList.Electric_Pump_LuV.get(64),
                ItemList.Hatch_Energy_LuV.get(64))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, IDs + 40))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_LuV))
            .duration(3600 * 20)
            .eut(RECIPE_LuV)
            .addTo(Assem);
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Sensor_LuV.get(64),
                ItemList.Sensor_LuV.get(64),
                ItemList.Sensor_LuV.get(64),
                ItemList.Sensor_LuV.get(64),
                ItemList.Electric_Pump_LuV.get(64),
                ItemList.Hatch_Energy_LuV.get(64))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, IDs + 41))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_LuV))
            .duration(3600 * 20)
            .eut(RECIPE_LuV)
            .addTo(Assem);
        // ZPM
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Emitter_ZPM.get(64),
                ItemList.Emitter_ZPM.get(64),
                ItemList.Emitter_ZPM.get(64),
                ItemList.Emitter_ZPM.get(64),
                ItemList.Electric_Pump_ZPM.get(64),
                ItemList.Hatch_Energy_ZPM.get(64))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, IDs + 42))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_ZPM))
            .duration(3600 * 20)
            .eut(RECIPE_ZPM)
            .addTo(Assem);
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Sensor_ZPM.get(64),
                ItemList.Sensor_ZPM.get(64),
                ItemList.Sensor_ZPM.get(64),
                ItemList.Sensor_ZPM.get(64),
                ItemList.Electric_Pump_ZPM.get(64),
                ItemList.Hatch_Energy_ZPM.get(64))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, IDs + 43))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_ZPM))
            .duration(3600 * 20)
            .eut(RECIPE_ZPM)
            .addTo(Assem);
        // UV
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Emitter_UV.get(64),
                ItemList.Emitter_UV.get(64),
                ItemList.Emitter_UV.get(64),
                ItemList.Emitter_UV.get(64),
                ItemList.Electric_Pump_UV.get(64),
                ItemList.Hatch_Energy_UV.get(64))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, IDs + 44))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_UV))
            .duration(3600 * 20)
            .eut(RECIPE_UV)
            .addTo(Assem);
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Sensor_UV.get(64),
                ItemList.Sensor_UV.get(64),
                ItemList.Sensor_UV.get(64),
                ItemList.Sensor_UV.get(64),
                ItemList.Electric_Pump_UV.get(64),
                ItemList.Hatch_Energy_UV.get(64))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, IDs + 45))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_UV))
            .duration(3600 * 20)
            .eut(RECIPE_UV)
            .addTo(Assem);
        // UHV
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Emitter_UHV.get(64),
                ItemList.Emitter_UHV.get(64),
                ItemList.Emitter_UHV.get(64),
                ItemList.Emitter_UHV.get(64),
                ItemList.Electric_Pump_UHV.get(64),
                ItemList.Hatch_Energy_UHV.get(64))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, IDs + 46))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_UHV))
            .duration(3600 * 20)
            .eut(RECIPE_UHV)
            .addTo(Assem);
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Sensor_UHV.get(64),
                ItemList.Sensor_UHV.get(64),
                ItemList.Sensor_UHV.get(64),
                ItemList.Sensor_UHV.get(64),
                ItemList.Electric_Pump_UHV.get(64),
                ItemList.Hatch_Energy_UHV.get(64))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, IDs + 47))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_UHV))
            .duration(3600 * 20)
            .eut(RECIPE_UHV)
            .addTo(Assem);
        // UEV
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Emitter_UEV.get(64),
                ItemList.Emitter_UEV.get(64),
                ItemList.Emitter_UEV.get(64),
                ItemList.Emitter_UEV.get(64),
                ItemList.Electric_Pump_UEV.get(64),
                ItemList.Hatch_Energy_UEV.get(64))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, IDs + 48))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_UHV))
            .duration(3600 * 20)
            .eut(RECIPE_UEV)
            .addTo(Assem);
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Sensor_UEV.get(64),
                ItemList.Sensor_UEV.get(64),
                ItemList.Sensor_UEV.get(64),
                ItemList.Sensor_UEV.get(64),
                ItemList.Electric_Pump_UEV.get(64),
                ItemList.Hatch_Energy_UEV.get(64))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, IDs + 49))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_UHV))
            .duration(3600 * 20)
            .eut(RECIPE_UEV)
            .addTo(Assem);
        // UIV
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Emitter_UIV.get(64),
                ItemList.Emitter_UIV.get(64),
                ItemList.Emitter_UIV.get(64),
                ItemList.Emitter_UIV.get(64),
                ItemList.Electric_Pump_UIV.get(64),
                ItemList.Hatch_Energy_UIV.get(64))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, IDs + 50))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_UHV))
            .duration(3600 * 20)
            .eut(RECIPE_UIV)
            .addTo(Assem);
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Sensor_UIV.get(64),
                ItemList.Sensor_UIV.get(64),
                ItemList.Sensor_UIV.get(64),
                ItemList.Sensor_UIV.get(64),
                ItemList.Electric_Pump_UIV.get(64),
                ItemList.Hatch_Energy_UIV.get(64))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, IDs + 51))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_UHV))
            .duration(3600 * 20)
            .eut(RECIPE_UIV)
            .addTo(Assem);
        // UMV
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Emitter_UMV.get(64),
                ItemList.Emitter_UMV.get(64),
                ItemList.Emitter_UMV.get(64),
                ItemList.Emitter_UMV.get(64),
                ItemList.Electric_Pump_UMV.get(64),
                ItemList.Hatch_Energy_UMV.get(64))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, IDs + 52))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_UHV))
            .duration(3600 * 20)
            .eut(RECIPE_UMV)
            .addTo(Assem);
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Sensor_UMV.get(64),
                ItemList.Sensor_UMV.get(64),
                ItemList.Sensor_UMV.get(64),
                ItemList.Sensor_UMV.get(64),
                ItemList.Electric_Pump_UMV.get(64),
                ItemList.Hatch_Energy_UMV.get(64))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, IDs + 53))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_UHV))
            .duration(3600 * 20)
            .eut(RECIPE_UMV)
            .addTo(Assem);
        // UXV
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Field_Generator_UXV.get(64),
                ItemList.Hatch_Energy_UXV.get(64))
            .itemOutputs(
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, IDs + 54),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, IDs + 55))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_UXV))
            .duration(36000 * 20)
            .eut(RECIPE_UXV)
            .addTo(Assem);
        // NQF
        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 32001))
            .metadata(RESEARCH_TIME, HOURS * 4)
            .itemInputs(
                ItemList.Casing_ZPM.get(16),
                ItemList.Electric_Motor_ZPM.get(64),
                ItemList.Electric_Pump_ZPM.get(32),
                ItemList.Field_Generator_ZPM.get(16),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 23562),
                eM_Power.get(64),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 15300),
                GTModHandler.getModItem("gregtech", "gt.blockcasings", 1, 15),
                ItemList.Emitter_ZPM.get(64),
                new Object[] { OrePrefixes.circuit.get(Materials.Superconductor), 64 },
                new Object[] { OrePrefixes.circuit.get(Materials.Superconductor), 64 },
                GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorZPM, 64),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 32001),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 12732),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 1188),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 32001))
            .fluidInputs(
                Materials.Naquadria.getMolten(144000),
                Materials.NaquadahEnriched.getMolten(288000),
                Materials.Naquadah.getMolten(576000),
                Materials.Trinium.getMolten(1152000))
            .itemOutputs(OTHItemList.NQF.get(1))
            .eut(123123)
            .duration(123 * 20)
            .addTo(AssemblyLine);
        //松油
        GTValues.RA.stdBuilder()
            .itemInputs(
                getModItem(IndustrialCraft2.ID,"itemFuelPlantBall",1)
            )
            .fluidOutputs(
                FluidRegistry.getFluidStack("pineoil",80)
            )
            .eut(RECIPE_IV)
            .duration(10 * SECONDS)
            .addTo(fluidExtractionRecipes);
    }
}
