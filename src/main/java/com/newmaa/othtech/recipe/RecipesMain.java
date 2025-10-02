package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.common.recipemap.Recipemaps.AE2;
import static com.newmaa.othtech.common.recipemap.Recipemaps.RCY;
import static com.newmaa.othtech.recipe.RecipesComponentAssemblyLineRecipes.getNanites;
import static com.newmaa.othtech.utils.Utils.setStackSize;
import static goodgenerator.api.recipe.GoodGeneratorRecipeMaps.preciseAssemblerRecipes;
import static gregtech.api.enums.Mods.*;
import static gregtech.api.enums.TierEU.RECIPE_EV;
import static gregtech.api.enums.TierEU.RECIPE_HV;
import static gregtech.api.enums.TierEU.RECIPE_IV;
import static gregtech.api.enums.TierEU.RECIPE_LuV;
import static gregtech.api.enums.TierEU.RECIPE_MV;
import static gregtech.api.enums.TierEU.RECIPE_UEV;
import static gregtech.api.enums.TierEU.RECIPE_UHV;
import static gregtech.api.enums.TierEU.RECIPE_UIV;
import static gregtech.api.enums.TierEU.RECIPE_UMV;
import static gregtech.api.enums.TierEU.RECIPE_UV;
import static gregtech.api.enums.TierEU.RECIPE_UXV;
import static gregtech.api.enums.TierEU.RECIPE_ZPM;
import static gregtech.api.recipe.RecipeMaps.assemblerRecipes;
import static gregtech.api.recipe.RecipeMaps.centrifugeRecipes;
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

import com.newmaa.othtech.OTHTechnology;
import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.item.ItemLoader;
import com.newmaa.othtech.common.materials.BWLiquids;
import com.newmaa.othtech.common.materials.BWMaterials;
import com.newmaa.othtech.utils.RecipeBuilder;

import goodgenerator.loader.Loaders;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gregtech.api.util.recipe.Scanning;
import gregtech.common.items.CombType;
import gregtech.loaders.misc.GTBees;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import tectech.recipe.TTRecipeAdder;
import tectech.thing.CustomItemList;

public class RecipesMain implements IRecipePool {

    public static ItemStack getGM(int aMeta, int aAmount) {
        return GTModHandler.getModItem("gregtech", "gt.blockmachines", aAmount, aMeta);
    }

    @Override
    public void loadRecipes() {

        Fluid water = FluidRegistry.getFluid("water");
        Fluid Ewater = FluidRegistry.getFluid("grade8purifiedwater");
        Fluid songyou = FluidRegistry.getFluid("pineoil");
        Fluid white = FluidRegistry.getFluid("molten.whitedwarfmatter");
        Fluid cuihuaji = FluidRegistry.getFluid("exciteddtsc");
        final RecipeMap<?> Nan = RecipeMaps.nanoForgeRecipes;
        final RecipeMap<?> Assem = assemblerRecipes;
        final RecipeMap<?> Chem = RecipeMaps.multiblockChemicalReactorRecipes;
        // final RecipeMap<?> MT = new getRecipeMapsFromOtherMods().get();
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
        //
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
            OTHItemList.dustIrOsSmM.get(1),
            123,
            1919810);
        // isaforgea
        TTRecipeAdder.addResearchableAssemblylineRecipe(
            getGM(IDs + 24, 1),
            (int) 409600000,
            32000,
            (int) 123123123,
            514,
            new Object[] { OTHItemList.beeISAM.get(64), getGM(IDs + 24, 64), getGM(1004, 64), getGM(13532, 64),
                new Object[] { OrePrefixes.circuit.get(Materials.Cosmic), 64 },
                new Object[] { OrePrefixes.circuit.get(Materials.Cosmic), 64 },
                CustomItemList.SpacetimeCompressionFieldGeneratorTier0.get(32),
                CustomItemList.TimeAccelerationFieldGeneratorTier0.get(32),
                CustomItemList.StabilisationFieldGeneratorTier0.get(32),
                GTModHandler.getModItem("eternalsingularity", "eternal_singularity", 64),
                ItemList.Field_Generator_UMV.get(64), GTModHandler.getModItem("miscutils", "milledMonazite", 64),
                getGM(2083, 64), getGM(2092, 64), OTHItemList.dustIrOsSmM.get(64), OTHItemList.dustLookNEIM.get(64) },
            new FluidStack[] { new FluidStack(white, 4608), new FluidStack(cuihuaji, 123123),
                new FluidStack(Ewater, 4096000), new FluidStack(songyou, 32000000) },
            getGM(23521, 1),
            105000,
            123123123);
        // IsaBee
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTModHandler.getModItem("miscutils", "MU-metaitem.01", 0, 32140),
                getGM(31027, 64),
                getGM(31028, 64),
                getNanites(4, MaterialsUEVplus.WhiteDwarfMatter),
                getNanites(4, MaterialsUEVplus.BlackDwarfMatter),
                getNanites(4, MaterialsUEVplus.Universium))
            .itemOutputs(OTHItemList.beeISAM.get(1))
            .fluidInputs(new FluidStack(songyou, 256000))
            .duration(500 * 20)
            .eut(Integer.MAX_VALUE)
            .specialValue(3)
            .addTo(Nan);
        // Mega recipesQFTRecipes
        TTRecipeAdder.addResearchableAssemblylineRecipe(
            getGM(31151, 1),
            512000000,
            4096,
            123123123,
            256,
            new Object[] { getGM(31151, 64), GTModHandler.getModItem("miscutils", "gtplusplus.blockcasings.5", 64, 10),
                GTModHandler.getModItem("miscutils", "gtplusplus.blockcasings.5", 64, 14),
                new Object[] { OrePrefixes.circuit.get(Materials.Cosmic), 64 },
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 32417),
                GTModHandler.getModItem("dreamcraft", "item.MysteriousCrystalLens", 64),
                OTHItemList.dustIrOsSmM.get(64), OTHItemList.dustLookNEIM.get(64),
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
            getGM(23526, 1),
            1234 * 20,
            123123123);
        // 9in1
        GTValues.RA.stdBuilder()
            .itemOutputs(getGM(23520, 1))
            .itemInputs(
                OTHItemList.SteamNeinInOne.get(64),
                getGM(31027, 64),
                getGM(31028, 64),
                getGM(995, 64),
                ItemList.Robot_Arm_LuV.get(16),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Superconductor, 64))
            .metadata(PRECISE_ASSEMBLER_CASING_TIER, 1)
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("molten.indalloy140"), 128 * 144))

            .duration(123 * 20)
            .eut(32000)
            .addTo(preciseAssemblerRecipes);
        // MEBF
        GTValues.RA.stdBuilder()
            .itemOutputs(getGM(23527, 1))
            .itemInputs(
                getGM(963, 64),
                getGM(963, 64),
                getGM(963, 64),
                ItemList.Field_Generator_LuV.get(16),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Superconductor, 64),
                GTUtility.getIntegratedCircuit(17))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("molten.indalloy140"), 128 * 144))

            .duration(123 * 20)
            .eut(32000)
            .addTo(Assem);
        // recipesVacuumFreezerRecipes
        GTValues.RA.stdBuilder()
            .itemOutputs(getGM(23528, 1))
            .itemInputs(
                getGM(910, 64),
                getGM(910, 64),
                getGM(910, 64),
                ItemList.Electric_Piston_LuV.get(16),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Superconductor, 64),
                GTUtility.getIntegratedCircuit(17))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("molten.indalloy140"), 128 * 144))

            .duration(123 * 20)
            .eut(32000)
            .addTo(Assem);
        // SINOPEC
        GTValues.RA.stdBuilder()
            .itemOutputs(getGM(23533, 1))
            .itemInputs(
                getGM(31021, 16),
                getGM(1160, 16),
                getGM(1169, 16),
                ItemList.Robot_Arm_IV.get(8),
                ItemList.Electric_Pump_IV.get(8),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Ultimate, 16),
                GTUtility.getIntegratedCircuit(17))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("molten.tungstensteel"), 128 * 144))

            .duration(123 * 20)
            .eut(8000)
            .addTo(Assem);
        // CHEM
        GTValues.RA.stdBuilder()
            .itemOutputs(getGM(23534, 1))
            .itemInputs(
                getGM(13366, 2),
                getGM(998, 16),
                ItemList.Electric_Motor_LuV.get(16),
                ItemList.Electric_Pump_LuV.get(16),
                getGM(31030, 4),
                GTModHandler.getModItem("miscutils", "item.BasicAgrichemItem", 64, 13),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Elite, 16),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Master, 16),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Ultimate, 16),
                GTUtility.getIntegratedCircuit(17))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("molten.zeron100"), 4 * 144))

            .duration(123 * 20)
            .eut(12300)
            .addTo(Assem);
        // Indium Beeyonds Chem From GTNH 2.6.1
        GTValues.RA.stdBuilder()
            .fluidInputs(FluidRegistry.getFluidStack("phtalicacid", 2400))
            .itemInputs(GTUtility.getIntegratedCircuit(17), GTBees.combs.getStackForType(CombType.INDIUM, 4))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 4, 6056))

            .duration(288)
            .eut(12300)
            .addTo(Chem);
        // // 232
        // RecipeBuilder.builder()
        // .itemInputs(
        // OTHItemList.dustLookNEIM.get(0),
        // Materials.Thorium.getDust(1),
        // GTUtility.getIntegratedCircuit(23),
        // GTUtility.getIntegratedCircuit(2))
        // .itemOutputs(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 30))
        // .eut(12300)
        // .duration(114)
        // .addTo(Assem);
        // Mega EEC
        TTRecipeAdder.addResearchableAssemblylineRecipe(
            getGM(14201, 1),
            5120000,
            4096,
            (int) TierEU.UEV,
            64,
            new Object[] { getGM(14201, 64), getGM(14201, 64), getGM(14201, 64), getGM(14201, 64),
                GTModHandler.getModItem("gregtech", "gt.blockframes", 16, 397),
                GTModHandler.getModItem("gregtech", "gt.blockframes", 16, 395),
                GTModHandler.getModItem("gregtech", "gt.blockframes", 16, 395),
                GTModHandler.getModItem("tectech", "gt.blockcasingsTT", 16, 9), ItemList.Robot_Arm_UEV.get(64),
                ItemList.Robot_Arm_UEV.get(64), new Object[] { OrePrefixes.circuit.get(Materials.Optical), 64 },
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 4054),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 24, 21397), new ItemStack(Items.skull, 64, 1),
                OTHItemList.dustIrOsSmM.get(64),
                GTModHandler.getModItem("eternalsingularity", "eternal_singularity", 4), },
            new FluidStack[] { new FluidStack(FluidRegistry.getFluidID("molten.infinity"), 123 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.quantium"), 123 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.neutronium"), 123 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.mutatedlivingsolder"), 123 * 144) },
            getGM(23536, 1),
            1200 * 20,
            (int) TierEU.UIV);
        // TSSF
        GTValues.RA.stdBuilder()
            .itemOutputs(getGM(23535, 1))
            .fluidInputs(FluidRegistry.getFluidStack("molten.solderingalloy", 123 * 144))
            .itemInputs(
                getGM(12730, 4),
                getGM(791, 8),
                getGM(861, 16),
                ItemList.Sensor_LuV.get(8),
                ItemList.Electric_Pump_LuV.get(8),
                ItemList.Electric_Motor_LuV.get(16),
                ItemList.Conveyor_Module_LuV.get(8),
                ItemList.Robot_Arm_LuV.get(4),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Ultimate, 24),
                GTUtility.getIntegratedCircuit(17))

            .duration(123 * 20)
            .eut(TierEU.IV)
            .addTo(Assem);
        // 9In1
        GTValues.RA.stdBuilder()
            .itemOutputs(
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 860)
                    .setStackDisplayName("这曾经是九合一."))
            .fluidInputs(FluidRegistry.getFluidStack("molten.staballoy", 2 * 144))
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 245),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 295),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 555),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 505),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 515),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 275),
                GTModHandler.getModItem("miscutils", "blockProjectBench", 1))
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
            .duration(60 * 20)
            .eut(TierEU.UMV)
            .addTo(Assem);
        // 二硫化碳
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 32))
            .fluidOutputs(FluidRegistry.getFluidStack("carbondisulfide", 32000))
            .duration(123 * 20)
            .eut(30)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
        // Sun
        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 3))
            .itemOutputs(OTHItemList.itemEnqingM.get(1))

            .duration(2024 * 20)
            .eut(194899)
            .addTo(RecipeMaps.laserEngraverRecipes);
        // SunFactory
        TTRecipeAdder.addResearchableAssemblylineRecipe(
            getGM(23520, 1),
            64000000,
            10240,
            (int) TierEU.UMV,
            640,
            new Object[] { getGM(23520, 64), getGM(23527, 64), getGM(23528, 64), getGM(10886, 64),
                OTHItemList.itemEnqingM.get(64), OTHItemList.itemEnqingM.get(64), OTHItemList.itemEnqingM.get(64),
                OTHItemList.itemEnqingM.get(64), ItemList.Robot_Arm_UMV.get(64),
                new Object[] { OrePrefixes.circuit.get(Materials.Cosmic), 64 },
                new Object[] { OrePrefixes.circuit.get(Materials.Cosmic), 64 }, ItemList.Field_Generator_UMV.get(64),
                new ItemStack(Items.bread, 64), new ItemStack(Items.nether_star, 64), getGM(15297, 64),
                getGM(15197, 64),

            },
            new FluidStack[] { new FluidStack(FluidRegistry.getFluidID("molten.gold"), 114 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.gold"), 514 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.gold"), 1919 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.mutatedlivingsolder"), 810 * 144) },
            getGM(23537, 1),
            1984 * 20,
            (int) TierEU.UXV);
        // LCA
        GTValues.RA.stdBuilder()
            .itemOutputs(getGM(23538, 1))
            .fluidInputs(FluidRegistry.getFluidStack("molten.staballoy", 16 * 144))
            .itemInputs(
                getGM(1184, 64),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Ultimate, 64),
                ItemList.Electric_Pump_IV.get(64),
                GTModHandler.getModItem("GoodGenerator", "huiCircuit", 16),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 48, 32052),
                getGM(12005, 4),
                GTUtility.getIntegratedCircuit(17))

            .duration(4 * 20)
            .eut(TierEU.IV)
            .addTo(Assem);
        // CoccOven
        addCraftingRecipe(
            OTHItemList.CoccOven.get(1),
            new Object[] { "AAA", "ABA", "AAA", 'A', ItemList.Firebrick, 'B', OTHItemList.SunLighter.get(1) });
        // SunLighter
        addCraftingRecipe(
            OTHItemList.SunLighter.get(1),
            new Object[] { "ABA", "ACA", "ADA", 'A',
                GTOreDictUnificator.get(OrePrefixes.itemCasing, Materials.Steel, 1), 'B',
                new ItemStack(Items.redstone, 1), 'C',
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.NetherQuartz, 1), 'D',
                GTModHandler.getModItem("Railcraft", "firestone.raw", 1) });
        // HV Hatches
        TTRecipeAdder.addResearchableAssemblylineRecipe(
            getGM(43, 1),
            123123,
            512,
            (int) TierEU.HV,
            1048576,
            new Object[] { getGM(209, 64), getGM(209, 64), getGM(209, 64), getGM(209, 64), getGM(15300, 1),
                getGM(1445, 4), new ItemStack(ItemLoader.itemLeekBox, 64, 0),
                GTModHandler.getModItem("GoodGenerator", "compactFusionCoil", 16, 2),
                ItemList.Field_Generator_EV.get(64), new Object[] { OrePrefixes.circuit.get(Materials.Elite), 64 },
                new Object[] { OrePrefixes.circuit.get(Materials.Elite), 64 }, ItemList.Field_Generator_EV.get(64),
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
            getGM(33, 1),
            123123,
            512,
            (int) TierEU.HV,
            1048576,
            new Object[] { getGM(296, 64), getGM(296, 64), getGM(296, 64), getGM(296, 64), getGM(15300, 1),
                getGM(1445, 4), new ItemStack(ItemLoader.itemLeekBox, 64, 0),
                GTModHandler.getModItem("GoodGenerator", "compactFusionCoil", 16, 2),
                ItemList.Field_Generator_EV.get(64), new Object[] { OrePrefixes.circuit.get(Materials.Elite), 64 },
                new Object[] { OrePrefixes.circuit.get(Materials.Elite), 64 }, ItemList.Field_Generator_EV.get(64),
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

            .duration(1949 * 20)
            .eut(123)
            .addTo(RecipeMaps.laserEngraverRecipes);
        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Items.gold_ingot, 1), GTUtility.getIntegratedCircuit(2))
            .itemOutputs(new ItemStack(ItemLoader.RecordPRKB, 1))

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
            .fluidOutputs(BWLiquids.LCL.getFluidOrGas(20000))
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
            .fluidOutputs(BWLiquids.ALS.getFluidOrGas(20000))
            .eut(480)
            .duration(1200 * 20)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
        // Ayanami Model
        RecipeBuilder.builder()
            .fluidInputs(BWLiquids.LCL.getFluidOrGas(50000))
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 8, 32429),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 8, 32420),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 17472))
            .itemOutputs(GTModHandler.getModItem("123Technology", "modelAyanami", 1))
            .eut(320)
            .duration(40 * 20)
            .addTo(assemblerRecipes);
        // Asuka Model
        RecipeBuilder.builder()
            .fluidInputs(BWLiquids.ALS.getFluidOrGas(50000))
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 8, 32428),
                GTModHandler.getModItem("gregtech", "gt.metaitem.02", 8, 32420),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 17472))
            .itemOutputs(GTModHandler.getModItem("123Technology", "modelAsuka", 1))
            .eut(320)
            .duration(40 * 20)
            .addTo(assemblerRecipes);
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
            .addTo(assemblerRecipes);
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
            .addTo(assemblerRecipes);
        // Record Never gonna give you up
        RecipeBuilder.builder()
            .itemOutputs(new ItemStack(ItemLoader.RecordNGGU))
            .itemInputs(GTUtility.getIntegratedCircuit(17), new ItemStack(ItemLoader.itemLeekBox))
            .duration(20)
            .eut(114)
            .addTo(assemblerRecipes);
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
            .metadata(SCANNING, new Scanning(2400, RECIPE_LuV))
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
        // 松油
        GTValues.RA.stdBuilder()
            .itemInputs(getModItem(IndustrialCraft2.ID, "itemFuelPlantBall", 1))
            .fluidOutputs(FluidRegistry.getFluidStack("pineoil", 80))
            .eut(RECIPE_IV)
            .duration(10 * SECONDS)
            .addTo(fluidExtractionRecipes);
        // ISAModule Recipes
        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, OTHItemList.dustLookNEIM.get(1))
            .metadata(SCANNING, new Scanning(HOURS * 2, RECIPE_MV))
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Iridium, 64),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Iridium, 64),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Iridium, 64),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Iridium, 64),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Iridium, 64),
                OTHItemList.dustLookNEIM.get(64),
                OTHItemList.dustLookNEIM.get(64),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Iridium, 64),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Osmium, 64),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Samarium, 64),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Samarium, 64),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Osmium, 64),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Osmium, 64),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Osmium, 64),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Osmium, 64),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Osmium, 64))
            .fluidInputs(Materials.Water.getFluid(144000))
            .itemOutputs(OTHItemList.IsaNEI.get(1))
            .eut(RECIPE_ZPM)
            .duration(600 * 20)
            .addTo(AssemblyLine);
        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, OTHItemList.IsaNEI.get(1))
            .metadata(SCANNING, new Scanning(HOURS, RECIPE_MV))
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Iridium, 64),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Neutronium, 64),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Neutronium, 64),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Iridium, 64),
                BWMaterials.IrOsSm.get(OrePrefixes.plateDouble, 64),
                OTHItemList.dustLookNEIM.get(64),
                OTHItemList.dustLookNEIM.get(64),
                BWMaterials.IrOsSm.get(OrePrefixes.plateDouble, 64),
                BWMaterials.IrOsSm.get(OrePrefixes.plateDouble, 64),
                OTHItemList.dustLookNEIM.get(64),
                OTHItemList.dustLookNEIM.get(64),
                BWMaterials.IrOsSm.get(OrePrefixes.plateDouble, 64),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Iridium, 64),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Neutronium, 64),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Neutronium, 64),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Iridium, 64))
            .fluidInputs(Materials.Lava.getFluid(144000))
            .itemOutputs(OTHItemList.ISAIOS.get(1))
            .eut(RECIPE_UV)
            .duration(600 * 20)
            .addTo(AssemblyLine);
        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, OTHItemList.ISAIOS.get(1))
            .metadata(SCANNING, new Scanning(HOURS / 2, RECIPE_MV))
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plateTriple, Materials.Infinity, 64),
                GTOreDictUnificator.get(OrePrefixes.plateTriple, Materials.CosmicNeutronium, 64),
                GTOreDictUnificator.get(OrePrefixes.plateTriple, Materials.CosmicNeutronium, 64),
                GTOreDictUnificator.get(OrePrefixes.plateTriple, Materials.Infinity, 64),
                GTModHandler.getModItem(GTPlusPlus.ID, "itemPlateDoubleHypogen", 64),
                OTHItemList.itemEnqingM.get(64),
                OTHItemList.itemEnqingM.get(64),
                GTModHandler.getModItem(GTPlusPlus.ID, "itemPlateDoubleHypogen", 64),
                GTModHandler.getModItem(GTPlusPlus.ID, "itemPlateDoubleHypogen", 64),
                OTHItemList.itemEnqingM.get(64),
                OTHItemList.itemEnqingM.get(64),
                GTModHandler.getModItem(GTPlusPlus.ID, "itemPlateDoubleHypogen", 64),
                GTOreDictUnificator.get(OrePrefixes.plateTriple, Materials.Infinity, 64),
                GTOreDictUnificator.get(OrePrefixes.plateTriple, Materials.CosmicNeutronium, 64),
                GTOreDictUnificator.get(OrePrefixes.plateTriple, Materials.CosmicNeutronium, 64),
                GTOreDictUnificator.get(OrePrefixes.plateTriple, Materials.Infinity, 64))
            .fluidInputs(Materials.Iron.getMolten(144000))
            .itemOutputs(OTHItemList.ISAHYP.get(1))
            .eut(RECIPE_UHV)
            .duration(600 * 20)
            .addTo(AssemblyLine);
        // BBPF
        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, OTHItemList.UEVA.get(1))
            .metadata(SCANNING, new Scanning(HOURS / 2, RECIPE_MV))
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.block, Materials.Infinity, 64),
                ItemList.Machine_Multi_PlasmaForge.get(16),
                ItemList.Electric_Motor_UEV.get(64),
                ItemList.Electric_Piston_UEV.get(64),
                ItemList.Electric_Pump_UEV.get(64),
                ItemList.Electric_Piston_UEV.get(64),
                ItemList.Robot_Arm_UEV.get(64),
                ItemList.Field_Generator_UEV.get(16),
                ItemList.Casing_Coil_Infinity.get(64),
                ItemList.EnergisedTesseract.get(16),
                OTHItemList.UEVA.get(4),
                OTHItemList.UEVB.get(4),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Infinite, 64),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Bio, 64),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Optical, 64),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.SuperconductorUEV, 64))
            .fluidInputs(Materials.SuperCoolant.getFluid(123123))
            .fluidInputs(MaterialsUEVplus.TranscendentMetal.getFluid(114514))
            .fluidInputs(Materials.Infinity.getFluid(144 * 1024))
            .itemOutputs(OTHItemList.OTEBBPlasmaForge.get(1))
            .eut(RECIPE_UEV)
            .duration(600 * 20)
            .addTo(AssemblyLine);
        // MINI NUKE
        RecipeBuilder.builder()
            .itemOutputs(OTHItemList.AF.get(1))
            .itemInputs(
                ItemList.IV_Coil.get(16),
                ItemList.Casing_IV.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorIV, 64),
                ItemList.Circuit_Chip_HPIC.get(64))
            .duration(114 * 20)
            .eut(4096)
            .addTo(Assem);
        // KOH
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                OTHItemList.dustLookNEIM.get(0),
                Materials.Potassium.getDust(64))
            .fluidInputs(Materials.Oxygen.getGas(64000), Materials.Hydrogen.getGas(64000))
            .itemOutputs(GTModHandler.getModItem(GTPlusPlus.ID, "item.BasicGenericChemItem", 64, 12))
            .eut(114514)
            .duration(12)

            .addTo(Chem);
        // NAF
        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, GTModHandler.getModItem(GoodGenerator.ID, "FRF_Casings", 1))
            .metadata(SCANNING, new Scanning(120, RECIPE_UHV))
            .itemInputs(
                GTModHandler.getModItem(GregTech.ID, "gt.blockmachines", 64, 16999),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Naquadah, 64),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Naquadria, 64),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.NaquadahEnriched, 64),
                ItemList.Field_Generator_UHV.get(32),
                ItemList.Field_Generator_UV.get(64),
                GTOreDictUnificator.get(OrePrefixes.wireGt08, Materials.SuperconductorUHV, 64),
                new Object[] { OrePrefixes.circuit.get(Materials.Bio), 64 },
                GTModHandler.getModItem(GoodGenerator.ID, "advancedRadiationProtectionPlate", 64),
                GTOreDictUnificator.get(OrePrefixes.pipeHuge, Materials.Infinity, 16),
                GTOreDictUnificator.get(OrePrefixes.pipeHuge, Materials.Infinity, 16),
                GTModHandler.getModItem(GoodGenerator.ID, "advancedRadiationProtectionPlate", 64),
                GTModHandler.getModItem(NewHorizonsCoreMod.ID, "item.RadoxPolymerLens", 64),
                ItemList.Circuit_Wafer_QPIC.get(64),
                GTOreDictUnificator.get(OrePrefixes.bolt, Materials.Thulium, 64),
                GTModHandler.getModItem(NewHorizonsCoreMod.ID, "item.RadoxPolymerLens", 64))
            .fluidInputs(
                Materials.CosmicNeutronium.getMolten(92160),
                FluidRegistry.getFluidStack("molten.mutatedlivingsolder", 114514),
                Materials.DraconiumAwakened.getMolten(14400),
                Materials.Americium.getPlasma(32000))
            .itemOutputs(OTHItemList.NQFF.get(1))
            .eut(RECIPE_UEV)
            .duration(3600 * 20)
            .addTo(AssemblyLine);
        // MCA
        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, OTHItemList.LCA.get(1))
            .metadata(SCANNING, new Scanning(1200, RECIPE_ZPM))
            .itemInputs(
                GTModHandler.getModItem(GregTech.ID, "gt.blockmachines", 64, 12735),
                OTHItemList.LCA.get(64),
                new Object[] { OrePrefixes.circuit.get(Materials.Infinite), 64 },
                new Object[] { OrePrefixes.circuit.get(Materials.Infinite), 64 },
                ItemList.Robot_Arm_UV.get(16),
                ItemList.Electric_Motor_UV.get(64),
                ItemList.Electric_Pump_UV.get(32),
                ItemList.Field_Generator_UV.get(16),
                ItemList.Emitter_UV.get(48),
                ItemList.Sensor_UV.get(48),
                ItemList.Circuit_Chip_QPIC.get(48),
                GTOreDictUnificator.get(OrePrefixes.gear, Materials.Neutronium, 16),
                GTOreDictUnificator.get(OrePrefixes.wireGt02, Materials.SuperconductorUV, 64))
            .fluidInputs(
                Materials.Neutronium.getMolten(92160),
                FluidRegistry.getFluidStack("molten.indalloy140", 288000))
            .itemOutputs(OTHItemList.MCA.get(1))
            .eut(RECIPE_UHV)
            .duration(480 * 20)
            .addTo(AssemblyLine);

        // 太空钻机模块MK-321 - Assembler alt
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                ItemList.SpaceElevatorModulePumpT3.get(4),
                GTOreDictUnificator.get(OrePrefixes.frameGt, MaterialsUEVplus.Universium, 8),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UXV, 16),
                ItemList.Electric_Pump_MAX.get(8),
                GTOreDictUnificator.get(OrePrefixes.gearGt, MaterialsUEVplus.Universium, 8),
                GTOreDictUnificator.get(OrePrefixes.screw, MaterialsUEVplus.BlackDwarfMatter, 64),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, MaterialsUEVplus.Universium, 16))
            .fluidInputs(MaterialsUEVplus.DimensionallyShiftedSuperfluid.getFluid(9216))
            .itemOutputs(OTHItemList.SpaceElevatorModulePumpT4.get(1))
            .duration(123 * 20)
            .eut(TierEU.RECIPE_UXV)
            .addTo(assemblerRecipes);
        // 太空钻机模块MK-321
        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemList.SpaceElevatorModulePumpT3.getInternalStack_unsafe(),
            123123123,
            123123,
            256000000,
            4,
            new Object[] { ItemList.OilDrillInfinite.get(64),
                new ItemStack(ItemList.PlanetaryGasSiphonCasing.getItem(), 64),
                CustomItemList.enderLinkFluidCover.get(64), CustomItemList.enderLinkFluidCover.get(64),
                GTOreDictUnificator.get(OrePrefixes.frameGt, MaterialsUEVplus.Universium, 4),
                new Object[] { OrePrefixes.circuit.get(Materials.UXV), 16 }, ItemList.Electric_Pump_MAX.get(8),
                GTOreDictUnificator.get(OrePrefixes.gearGt, MaterialsUEVplus.Universium, 4),
                GTOreDictUnificator.get(OrePrefixes.screw, MaterialsUEVplus.BlackDwarfMatter, 64) },
            new FluidStack[] { MaterialsUEVplus.DimensionallyShiftedSuperfluid.getFluid(9216),
                MaterialsUEVplus.Universium.getMolten(2304) },
            OTHItemList.SpaceElevatorModulePumpT4.getInternalStack_unsafe(),
            123 * 20,
            (int) TierEU.RECIPE_UXV);
        // bin
        RecipeBuilder.builder()
            .itemOutputs(OTHItemList.TP.get(1))
            .itemInputs(GTUtility.getIntegratedCircuit(17), Materials.Steel.getPlates(4))
            .eut(1)
            .duration(1)
            .addTo(Assem);
        // Bedrock
        RecipeBuilder.builder()
            .itemInputs(Materials.Bedrockium.getDust(1))
            .itemOutputs(setStackSize(Materials.SiliconDioxide.getDust(1), 26244), Materials.Carbon.getDust(9))
            .eut(RECIPE_UIV)
            .duration(40)
            .addTo(GTPPRecipeMaps.centrifugeNonCellRecipes);
        RecipeBuilder.builder()
            .itemInputs(Materials.Bedrockium.getDust(1))
            .itemOutputs(setStackSize(Materials.SiliconDioxide.getDust(1), 26244), Materials.Carbon.getDust(9))
            .eut(RECIPE_UIV)
            .duration(40)
            .addTo(centrifugeRecipes);
        // AE2singularity
        RecipeBuilder.builder()
            .itemOutputs(
                GTModHandler.getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 1, 47)
                    .setStackDisplayName("任意256,000物品每个"))
            .addTo(AE2);
        // Scrap
        RecipeBuilder.builder()
            .itemOutputs(ItemList.IC2_Scrap.get(1))
            .itemInputs(new ItemStack(Blocks.cobblestone, 1).setStackDisplayName("任何物品"))
            .eut(30)
            .addTo(RCY);

        // EIOM
        RecipeBuilder.builder()
            .itemOutputs(OTHItemList.EIO.get(1))
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Electric_Motor_HV.get(16),
                ItemList.Casing_HV.get(1),
                Materials.Titanium.getPlates(16),
                new ItemStack(crazypants.enderio.EnderIO.blockSliceAndSplice, 1),
                new ItemStack(crazypants.enderio.EnderIO.blockSoulFuser, 1))
            .eut(114)
            .duration(514)
            .addTo(Assem);
        // EX
        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, Loaders.XHE)
            .metadata(SCANNING, new Scanning(HOURS, RECIPE_MV))
            .itemInputs(
                GTUtility.copyAmount(16, Loaders.XHE),
                ItemList.Machine_Multi_VacuumFreezer.get(16),
                ItemList.Electric_Pump_ZPM.get(16),
                ItemList.Electric_Motor_LuV.get(64),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.NaquadahAlloy, 64),
                GTOreDictUnificator.get(OrePrefixes.wireGt02, Materials.SuperconductorZPM, 32),
                GTOreDictUnificator.get(OrePrefixes.wireGt02, Materials.SuperconductorZPM, 32),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.NaquadahAlloy, 64))
            .fluidInputs(Materials.Iridium.getMolten(9216))
            .itemOutputs(OTHItemList.EXH.get(1))
            .duration(600 * 20)
            .eut(TierEU.RECIPE_UV)
            .addTo(AssemblyLine);
        // GraveDragon
        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 5001))
            .metadata(SCANNING, new Scanning(HOURS, RECIPE_UV))
            .itemInputs(
                ItemList.Electric_Motor_UHV.get(64),
                ItemList.Electric_Pump_UHV.get(16),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 8, 5001),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Void, 64),
                GTOreDictUnificator.get(OrePrefixes.wireGt02, Materials.SuperconductorUHV, 64),
                GTOreDictUnificator.get(OrePrefixes.wireGt02, Materials.SuperconductorUHV, 64),
                new Object[] { OrePrefixes.circuit.get(Materials.UIV), 64 },
                GTModHandler.getModItem(DraconicEvolution.ID, "draconicCore", 64),
                GTModHandler.getModItem(DraconicEvolution.ID, "draconiumEnergyCore", 64))
            .fluidInputs(
                FluidRegistry.getFluidStack("molten.mutatedlivingsolder", 9216),
                FluidRegistry.getFluidStack("molten.infinity", 9216),
                Materials.Void.getMolten(9216))
            .itemOutputs(OTHItemList.OTEGraveDragon.get(1))
            .duration(600 * 20)
            .eut(TierEU.RECIPE_UIV)
            .addTo(AssemblyLine);
        // Egg
        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Antimony, 1))
            .fluidInputs(Materials.Nitrogen.getGas(4000))
            .itemOutputs(new ItemStack(Items.egg, 3))
            .eut(114514)
            .duration(60)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
        // Salt Wasser
        RecipeBuilder.builder()
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 2))
            .fluidInputs(Materials.SaltWater.getFluid(2000))
            .itemInputs(GTUtility.getIntegratedCircuit(17), OTHItemList.dustIrOsSmM.get(0))
            .fluidOutputs(Materials.Nitrogen.getGas(1000), FluidRegistry.getFluidStack("fluid.formaldehyde", 2000))
            .duration(100)
            .eut(123123)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
        // TallHat
        addCraftingRecipe(
            GTModHandler.getModItem(OTHTechnology.MODID, "modelTallHat", 1),
            new Object[] { "A~~", "~~~", "~~~", 'A', OTHItemList.Tallhat.get(1) });
        addCraftingRecipe(
            OTHItemList.Tallhat.get(1),
            new Object[] { "A~~", "~~~", "~~~", 'A', GTModHandler.getModItem(OTHTechnology.MODID, "modelTallHat", 1) });
        addCraftingRecipe(
            OTHItemList.Tallhat.get(1),
            new Object[] { "~A~", "ABA", "A~A", 'A', new ItemStack(Items.paper, 1), 'B',
                new ItemStack(Items.dye, 1, 0) });
        // Steam 9in1
        addCraftingRecipe(
            OTHItemList.SteamNeinInOne.get(1),
            new Object[] { "ABC", "DEF", "GHI", 'A', getGM(31041, 1), 'B', getGM(31041, 1), 'C', getGM(31078, 1), 'D',
                getGM(31080, 1), 'E', getGM(31085, 1), 'F', getGM(31082, 1), 'G', getGM(31083, 1), 'H', getGM(31084, 1),
                'I', getGM(23540, 1) });
    }
}
