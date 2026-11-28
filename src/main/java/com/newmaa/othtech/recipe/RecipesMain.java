package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.common.recipemap.Recipemaps.AE2;
import static com.newmaa.othtech.common.recipemap.Recipemaps.RCY;
import static com.newmaa.othtech.recipe.RecipesComponentAssemblyLineRecipes.getNanites;
import static com.newmaa.othtech.utils.Utils.hasNullItem;
import static com.newmaa.othtech.utils.Utils.setStackSize;
import static goodgenerator.api.recipe.GoodGeneratorRecipeMaps.preciseAssemblerRecipes;
import static gregtech.api.enums.Mods.*;
import static gregtech.api.enums.TierEU.*;
import static gregtech.api.recipe.RecipeMaps.assemblerRecipes;
import static gregtech.api.recipe.RecipeMaps.centrifugeRecipes;
import static gregtech.api.recipe.RecipeMaps.fluidExtractionRecipes;
import static gregtech.api.util.GTModHandler.addCraftingRecipe;
import static gregtech.api.util.GTModHandler.getModItem;
import static gregtech.api.util.GTRecipeBuilder.HOURS;
import static gregtech.api.util.GTRecipeBuilder.INGOTS;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;
import static gregtech.api.util.GTRecipeConstants.*;
import static tectech.thing.CustomItemList.eM_Power;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.Nxer.TwistSpaceTechnology.common.GTCMItemList;
import com.dreammaster.item.NHItemList;
import com.newmaa.othtech.OTHTechnology;
import com.newmaa.othtech.common.ItemAndBlockHandler;
import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.blocks.nailong.NailongCasingMeta;
import com.newmaa.othtech.common.blocks.nailong.NailongCasings;
import com.newmaa.othtech.common.item.ItemLoader;
import com.newmaa.othtech.common.materials.BWLiquids;
import com.newmaa.othtech.common.materials.BWMaterials;
import com.newmaa.othtech.common.materials.OTHMaterials;
import com.newmaa.othtech.utils.ExternalRecipeItems;
import com.newmaa.othtech.utils.RecipeFluids;
import com.newmaa.othtech.utils.RecipeStacks;

import bartworks.system.material.WerkstoffLoader;
import goodgenerator.loader.Loaders;
import gregtech.api.GregTechAPI;
import gregtech.api.casing.Casings;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MetaTileEntityIDs;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;
import gregtech.api.util.recipe.Scanning;
import gregtech.common.items.CombType;
import gregtech.common.items.MetaGeneratedItem01;
import gregtech.common.items.MetaGeneratedItem02;
import gregtech.common.items.MetaGeneratedItem03;
import gregtech.loaders.misc.GTBees;
import gtPlusPlus.core.block.ModBlocks;
import gtPlusPlus.core.item.base.BaseItemComponent.ComponentTypes;
import gtPlusPlus.core.material.MaterialMisc;
import gtPlusPlus.core.material.MaterialsAlloy;
import gtPlusPlus.core.material.MaterialsElements.STANDALONE;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;
import gtPlusPlus.xmod.gregtech.common.items.MetaGeneratedItemIDs;
import gtnhlanth.common.register.WerkstoffMaterialPool;
import ic2.core.IC2;
import ic2.core.init.InternalName;
import tectech.recipe.TTRecipeAdder;
import tectech.thing.CustomItemList;
import tectech.thing.casing.TTCasingsContainer;

public class RecipesMain implements IRecipePool {

    public static ItemStack getGM(int aMeta, int aAmount) {
        return GTModHandler.getModItem("gregtech", "gt.blockmachines", aAmount, aMeta);
    }

    @Override
    public void loadRecipes() {

        Fluid water = FluidRegistry.getFluid(FluidRegistry.WATER.getName());
        Fluid Ewater = FluidRegistry.getFluid(RecipeFluids.registryName(Materials.Grade8PurifiedWater.mFluid));
        Fluid songyou = FluidRegistry.getFluid("pineoil");
        Fluid white = FluidRegistry
            .getFluid(RecipeFluids.registryName(Materials.WhiteDwarfMatter.mStandardMoltenFluid));
        Fluid cuihuaji = FluidRegistry.getFluid(RecipeFluids.registryName(Materials.ExcitedDTSC.mFluid));
        final RecipeMap<?> Nan = RecipeMaps.nanoForgeRecipes;
        final RecipeMap<?> Assem = assemblerRecipes;
        final RecipeMap<?> Chem = RecipeMaps.multiblockChemicalReactorRecipes;
        // final RecipeMap<?> MT = new getRecipeMapsFromOtherMods().get();
        final int IDs = 23520;

        GTValues.RA.stdBuilder()
            .itemInputs(
                Materials.Samarium.getBlocks(64),
                Materials.Cerium.getDust(64),
                Materials.Europium.getIngots(64),
                WerkstoffLoader.Zirconium.get(OrePrefixes.dust, 64),
                GTUtility.getIntegratedCircuit(17))
            .itemOutputs(OTHItemList.PrimitiveRareEarthCatalyst.get(1))
            .duration(150 * SECONDS)
            .eut(RECIPE_LuV)
            .addTo(assemblerRecipes);

        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTOreDictUnificator.get(OrePrefixes.crushed, Materials.Monazite, 64),
                Materials.Sodium.getDust(100),
                new ItemStack(Items.sugar, 64),
                OTHItemList.PrimitiveRareEarthCatalyst.get(0))
            .fluidInputs(
                Materials.NitricAcid.getFluid(14_400),
                Materials.Water.getFluid(144_000),
                WerkstoffMaterialPool.AmmoniumNitrate.getFluidOrGas(25_600),
                WerkstoffLoader.AmmoniumChloride.getFluidOrGas(32_000),
                Materials.Acetone.getFluid(14_400),
                Materials.HydrofluoricAcid.getFluid(24_000))
            .itemOutputs(
                WerkstoffLoader.Zirconium.get(OrePrefixes.dust, 6),
                WerkstoffMaterialPool.Hafnia.get(OrePrefixes.dust, 6),
                Materials.Cerium.getDust(9),
                Materials.Europium.getDust(24),
                Materials.Samarium.getDust(108),
                Materials.Gadolinium.getDust(54),
                WerkstoffLoader.Zirconium.get(OrePrefixes.dust, 2))
            .fluidOutputs(
                Materials.Hydrogen.getGas(270_000),
                Materials.Oxygen.getGas(50_000),
                Materials.Ammonia.getGas(330_000))
            .duration(160 * SECONDS)
            .eut(RECIPE_ZPM)
            .addTo(RecipeMaps.chemicalPlantRecipes);

        GTModHandler.addCraftingRecipe(
            OTHItemList.LargeSteamHatch.get(1),
            new Object[] { "ABA", "BXB", "ABA", 'A', GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 1L),
                'B', GregtechItemList.Hatch_Input_Steam.get(1), 'X',
                GTOreDictUnificator.get(OrePrefixes.gem, Materials.NetherStar, 1L) });

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
            RecipeStacks.exact(1, OTHItemList.MISA),
            409600000,
            32000,
            123123123,
            514,
            new Object[] { OTHItemList.beeISAM.get(64), RecipeStacks.exact(64, OTHItemList.MISA),
                RecipeStacks.exact(64, ItemList.Machine_Multi_PlasmaForge),
                getGM(MetaTileEntityIDs.AdvancedAssline.ID, 64),
                new Object[] { OrePrefixes.circuit.get(Materials.UXV), 64 },
                new Object[] { OrePrefixes.circuit.get(Materials.UXV), 64 },
                CustomItemList.SpacetimeCompressionFieldGeneratorTier0.get(32),
                CustomItemList.TimeAccelerationFieldGeneratorTier0.get(32),
                CustomItemList.StabilisationFieldGeneratorTier0.get(32),
                GTModHandler.getModItem("eternalsingularity", "eternal_singularity", 64),
                ItemList.Field_Generator_UMV.get(64),
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTPlusPlus.isModLoaded()
                    && GregtechItemList.MilledMonazite.hasBeenSet()
                        ? RecipeStacks.exact(64, GregtechItemList.MilledMonazite.getItem(), 0)
                        : null),
                getGM(2083, 64), getGM(2092, 64), OTHItemList.dustIrOsSmM.get(64), OTHItemList.dustLookNEIM.get(64) },
            new FluidStack[] { new FluidStack(white, 4608), new FluidStack(cuihuaji, 123123),
                new FluidStack(Ewater, 4096000), new FluidStack(songyou, 32000000) },
            RecipeStacks.exact(1, OTHItemList.MegaIsaForge),
            105000,
            123123123);
        // IsaBee
        GTValues.RA.stdBuilder()
            .itemInputs(
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTPlusPlus.isModLoaded()
                    && GregtechItemList.Laser_Lens_WoodsGlass.hasBeenSet()
                        ? RecipeStacks.exact(
                            0,
                            GregtechItemList.Laser_Lens_WoodsGlass.getItem(),
                            32000 + MetaGeneratedItemIDs.Laser_Lens_WoodsGlass.ID)
                        : null),
                getGM(MetaTileEntityIDs.Controller_IsaMill.ID, 64),
                RecipeStacks.exact(64, ItemList.FlotationCell),
                getNanites(4, Materials.WhiteDwarfMatter),
                getNanites(4, Materials.BlackDwarfMatter),
                getNanites(4, Materials.Universium))
            .itemOutputs(OTHItemList.beeISAM.get(1))
            .fluidInputs(new FluidStack(songyou, 256000))
            .duration(500 * 20)
            .eut(Integer.MAX_VALUE)
            .specialValue(3)
            .addTo(Nan);

        // Mega recipesQFTRecipes
        ItemStack megaQftResearch = getGM(MetaTileEntityIDs.QuantumForceTransformer.ID, 1);
        ItemStack megaQftOutput = RecipeStacks.exact(1, OTHItemList.Mega_QFT);
        Object[] megaQftInputs = new Object[] { getGM(MetaTileEntityIDs.QuantumForceTransformer.ID, 64),
            (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTPlusPlus.isModLoaded()
                ? RecipeStacks.exact(64, ModBlocks.blockCasings5Misc, 10)
                : null),
            (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTPlusPlus.isModLoaded()
                ? RecipeStacks.exact(64, ModBlocks.blockCasings5Misc, 14)
                : null),
            new Object[] { OrePrefixes.circuit.get(Materials.UXV), 64 },
            RecipeStacks.exact(64, ItemList.EnergisedTesseract),
            RecipeStacks.exact(64, NHItemList.MysteriousCrystalLens.item, 0), OTHItemList.dustIrOsSmM.get(64),
            OTHItemList.dustLookNEIM.get(64),
            ExternalRecipeItems.aeItem(1, ExternalRecipeItems.AeItem.SINGULARITY_CELL),
            ExternalRecipeItems.aeItem(1, ExternalRecipeItems.AeItem.SINGULARITY_CELL),
            ExternalRecipeItems.aeItem(1, ExternalRecipeItems.AeItem.SINGULARITY_CELL),
            (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTPlusPlus.isModLoaded()
                && GregtechItemList.Laser_Lens_Special.hasBeenSet()
                    ? RecipeStacks.exact(
                        64,
                        GregtechItemList.Laser_Lens_Special.getItem(),
                        32000 + MetaGeneratedItemIDs.Laser_Lens_Special.ID)
                    : null),
            RecipeStacks.exact(64, ItemList.NaquadriaSupersolid),
            RecipeStacks.exact(64, ItemList.SuperconductorComposite), RecipeStacks.exact(64, ItemList.StableAdhesive),
            RecipeStacks
                .generated(64, MetaGeneratedItem03.INSTANCE, OrePrefixes.nanite, Materials.TranscendentMetal), };
        if (hasNullItem(megaQftInputs) || megaQftResearch == null || megaQftOutput == null) {
            OTHTechnology.LOG.error("Skipping Quantum Force Transformer recipe due to null items");
        } else {
            TTRecipeAdder
                .addResearchableAssemblylineRecipe(
                    megaQftResearch,
                    512000000,
                    4096,
                    123123123,
                    256,
                    megaQftInputs,
                    new FluidStack[] {
                        new FluidStack(
                            FluidRegistry
                                .getFluidID(RecipeFluids.registryName(Materials.SpaceTime.mStandardMoltenFluid)),
                            36000000),
                        new FluidStack(
                            FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.ExcitedDTSC.mFluid)),
                            256000),
                        new FluidStack(
                            FluidRegistry.getFluidID(
                                RecipeFluids.gtppPlasmaName(
                                    gtPlusPlus.core.material.MaterialsElements.getInstance().NEPTUNIUM)),
                            256000),
                        new FluidStack(
                            FluidRegistry.getFluidID(
                                RecipeFluids
                                    .gtppPlasmaName(gtPlusPlus.core.material.MaterialsElements.getInstance().FERMIUM)),
                            256000) },
                    megaQftOutput,
                    1234 * 20,
                    123123123);
        }
        // 9in1
        GTValues.RA.stdBuilder()
            .itemOutputs(RecipeStacks.exact(1, OTHItemList.NineInOne))
            .itemInputs(
                OTHItemList.SteamNeinInOne.get(64),
                getGM(MetaTileEntityIDs.Controller_IsaMill.ID, 64),
                RecipeStacks.exact(64, ItemList.FlotationCell),
                getGM(MetaTileEntityIDs.Controller_Vacuum_Furnace.ID, 64),
                ItemList.Robot_Arm_LuV.get(16),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UV, 64))
            .metadata(PRECISE_ASSEMBLER_CASING_TIER, 1)
            .fluidInputs(
                FluidRegistry.getFluidStack(RecipeFluids.gtppMoltenName(MaterialsAlloy.INDALLOY_140), 128 * 144))

            .duration(123 * 20)
            .eut(32000)
            .addTo(preciseAssemblerRecipes);
        // MEBF
        GTValues.RA.stdBuilder()
            .itemOutputs(RecipeStacks.exact(1, OTHItemList.MEBFpp))
            .itemInputs(
                getGM(MetaTileEntityIDs.Machine_Adv_BlastFurnace.ID, 64),
                getGM(MetaTileEntityIDs.Machine_Adv_BlastFurnace.ID, 64),
                getGM(MetaTileEntityIDs.Machine_Adv_BlastFurnace.ID, 64),
                ItemList.Field_Generator_LuV.get(16),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UHV, 64),
                GTUtility.getIntegratedCircuit(17))
            .fluidInputs(
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.gtppMoltenName(MaterialsAlloy.INDALLOY_140)),
                    128 * 144))

            .duration(123 * 20)
            .eut(32000)
            .addTo(Assem);
        // recipesVacuumFreezerRecipes
        GTValues.RA.stdBuilder()
            .itemOutputs(RecipeStacks.exact(1, OTHItemList.MFREpp))
            .itemInputs(
                RecipeStacks.exact(64, ItemList.CryogenicFreezer),
                RecipeStacks.exact(64, ItemList.CryogenicFreezer),
                RecipeStacks.exact(64, ItemList.CryogenicFreezer),
                ItemList.Electric_Piston_LuV.get(16),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UHV, 64),
                GTUtility.getIntegratedCircuit(17))
            .fluidInputs(
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.gtppMoltenName(MaterialsAlloy.INDALLOY_140)),
                    128 * 144))

            .duration(123 * 20)
            .eut(32000)
            .addTo(Assem);
        // SINOPEC
        GTValues.RA.stdBuilder()
            .itemOutputs(RecipeStacks.exact(1, OTHItemList.SINOPECd))
            .itemInputs(
                getGM(MetaTileEntityIDs.Machine_Adv_DistillationTower.ID, 16),
                RecipeStacks.exact(16, ItemList.OilCracker),
                RecipeStacks.exact(16, ItemList.Machine_Multi_LargeChemicalReactor),
                ItemList.Robot_Arm_IV.get(8),
                ItemList.Electric_Pump_IV.get(8),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.ZPM, 16),
                GTUtility.getIntegratedCircuit(17))
            .fluidInputs(
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.TungstenSteel.mStandardMoltenFluid)),
                    128 * 144))

            .duration(123 * 20)
            .eut(8000)
            .addTo(Assem);
        // CHEM
        GTValues.RA.stdBuilder()
            .itemOutputs(RecipeStacks.exact(1, OTHItemList.Chem))
            .itemInputs(
                RecipeStacks.exact(2, ItemList.MegaChemicalReactor),
                getGM(MetaTileEntityIDs.ChemicalPlant_Controller.ID, 16),
                ItemList.Electric_Motor_LuV.get(16),
                ItemList.Electric_Pump_LuV.get(16),
                getGM(MetaTileEntityIDs.Bus_Catalysts.ID, 4),
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTPlusPlus.isModLoaded()
                    && GregtechItemList.EmptyCatalystCarrier.hasBeenSet()
                        ? RecipeStacks.exact(64, GregtechItemList.EmptyCatalystCarrier.getItem(), 13)
                        : null),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 16),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LuV, 16),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.ZPM, 16),
                GTUtility.getIntegratedCircuit(17))
            .fluidInputs(
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.gtppMoltenName(MaterialsAlloy.ZERON_100)),
                    4 * 144))

            .duration(123 * 20)
            .eut(12300)
            .addTo(Assem);
        // Indium Beeyonds Chem From GTNH 2.6.1
        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.PhthalicAcid.getFluid(2400))
            .itemInputs(GTUtility.getIntegratedCircuit(17), GTBees.combs.getStackForType(CombType.INDIUM, 4))
            .itemOutputs(
                RecipeStacks.generated(4, MetaGeneratedItem01.INSTANCE, OrePrefixes.crushedPurified, Materials.Indium))

            .duration(288)
            .eut(12300)
            .addTo(Chem);
        TTRecipeAdder.addResearchableAssemblylineRecipe(
            getGM(MetaTileEntityIDs.ExtremeEntityCrusherController.ID, 1),
            5120000,
            4096,
            (int) TierEU.UEV,
            64,
            new Object[] { getGM(MetaTileEntityIDs.ExtremeEntityCrusherController.ID, 64),
                getGM(MetaTileEntityIDs.ExtremeEntityCrusherController.ID, 64),
                getGM(MetaTileEntityIDs.ExtremeEntityCrusherController.ID, 64),
                getGM(MetaTileEntityIDs.ExtremeEntityCrusherController.ID, 64),
                RecipeStacks.exact(16, GregTechAPI.sBlockFrames, Materials.Infinity.getId()),
                RecipeStacks.exact(16, GregTechAPI.sBlockFrames, Materials.Bedrockium.getId()),
                RecipeStacks.exact(16, GregTechAPI.sBlockFrames, Materials.Bedrockium.getId()),
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.TecTech.isModLoaded()
                    ? RecipeStacks.exact(16, TTCasingsContainer.sBlockCasingsTT, 9)
                    : null),
                ItemList.Robot_Arm_UEV.get(64), ItemList.Robot_Arm_UEV.get(64),
                new Object[] { OrePrefixes.circuit.get(Materials.UHV), 64 },
                RecipeStacks.generated(64, MetaGeneratedItem03.INSTANCE, OrePrefixes.nanite, Materials.Silver),
                RecipeStacks.generated(24, MetaGeneratedItem02.INSTANCE, OrePrefixes.rotor, Materials.Infinity),
                new ItemStack(Items.skull, 64, 1), OTHItemList.dustIrOsSmM.get(64),
                GTModHandler.getModItem("eternalsingularity", "eternal_singularity", 4), },
            new FluidStack[] {
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.Infinity.mStandardMoltenFluid)),
                    123 * 144),
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.Quantium.mStandardMoltenFluid)),
                    123 * 144),
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.Neutronium.mStandardMoltenFluid)),
                    123 * 144),
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.gtppMoltenName(MaterialMisc.MUTATED_LIVING_SOLDER)),
                    123 * 144) },
            RecipeStacks.exact(1, OTHItemList.MegaEEC),
            1200 * 20,
            (int) TierEU.UIV);
        // TSSF
        GTValues.RA.stdBuilder()
            .itemOutputs(RecipeStacks.exact(1, OTHItemList.SF))
            .fluidInputs(Materials.SolderingAlloy.getMolten(123 * 144))
            .itemInputsUnsafe(
                RecipeStacks.exact(64, ItemList.Machine_Multi_BlastFurnace),
                RecipeStacks.exact(8, ItemList.IndustrialCokeOven),
                getGM(MetaTileEntityIDs.Hatch_Air_Intake.ID, 16),
                ItemList.Sensor_LuV.get(8),
                ItemList.Electric_Pump_LuV.get(8),
                ItemList.Electric_Motor_LuV.get(16),
                ItemList.Conveyor_Module_LuV.get(8),
                ItemList.Robot_Arm_LuV.get(4),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.ZPM, 24),
                GTUtility.getIntegratedCircuit(17))

            .duration(123 * 20)
            .eut(TierEU.IV)
            .addTo(Assem);
        // 9In1
        GTValues.RA.stdBuilder()
            .itemOutputs(
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 860)
                    .setStackDisplayName(StatCollector.translateToLocal("oth.recipe.main.formerNineInOne")))
            .fluidInputs(FluidRegistry.getFluidStack(RecipeFluids.gtppMoltenName(MaterialsAlloy.STABALLOY), 2 * 144))
            .itemInputs(
                RecipeStacks.exact(1, ItemList.Machine_IV_Compressor),
                RecipeStacks.exact(1, ItemList.Machine_IV_Lathe),
                RecipeStacks.exact(1, ItemList.Machine_IV_Polarizer),
                RecipeStacks.exact(1, ItemList.Machine_IV_Fermenter),
                RecipeStacks.exact(1, ItemList.Machine_IV_FluidExtractor),
                RecipeStacks.exact(1, ItemList.Machine_IV_Extractor))
            .duration(4 * 20)
            .eut(TierEU.IV)
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
            .eut(1948)
            .addTo(RecipeMaps.laserEngraverRecipes);
        // SunFactory
        TTRecipeAdder.addResearchableAssemblylineRecipe(
            RecipeStacks.exact(1, OTHItemList.NineInOne),
            64000000,
            10240,
            (int) TierEU.UMV,
            640,
            new Object[] { RecipeStacks.exact(64, OTHItemList.NineInOne), RecipeStacks.exact(64, OTHItemList.MEBFpp),
                RecipeStacks.exact(64, OTHItemList.MFREpp), RecipeStacks.exact(64, ItemList.ExtruderUMV),
                OTHItemList.itemEnqingM.get(64), OTHItemList.itemEnqingM.get(64), OTHItemList.itemEnqingM.get(64),
                OTHItemList.itemEnqingM.get(64), ItemList.Robot_Arm_UMV.get(64),
                new Object[] { OrePrefixes.circuit.get(Materials.UXV), 64 },
                new Object[] { OrePrefixes.circuit.get(Materials.UXV), 64 }, ItemList.Field_Generator_UMV.get(64),
                new ItemStack(Items.bread, 64), new ItemStack(Items.nether_star, 64),
                getGM(MetaTileEntityIDs.UMV1048576AtLaserSourceHatch.ID, 64),
                getGM(MetaTileEntityIDs.UMV1048576AtLaserTargetHatch.ID, 64),

            },
            new FluidStack[] {
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.Gold.mStandardMoltenFluid)),
                    114 * 144),
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.Gold.mStandardMoltenFluid)),
                    514 * 144),
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.Gold.mStandardMoltenFluid)),
                    1919 * 144),
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.gtppMoltenName(MaterialMisc.MUTATED_LIVING_SOLDER)),
                    810 * 144) },
            RecipeStacks.exact(1, OTHItemList.Sun),
            1984 * 20,
            (int) TierEU.UXV);
        // LCA
        GTValues.RA.stdBuilder()
            .itemOutputs(RecipeStacks.exact(1, OTHItemList.LCA))
            .fluidInputs(FluidRegistry.getFluidStack(RecipeFluids.gtppMoltenName(MaterialsAlloy.STABALLOY), 16 * 144))
            .itemInputs(
                RecipeStacks.exact(64, ItemList.Machine_IV_CircuitAssembler),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.ZPM, 64),
                ItemList.Electric_Pump_IV.get(64),
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GoodGenerator.isModLoaded()
                    ? RecipeStacks.exact(16, Loaders.huiCircuit, 0)
                    : null),
                RecipeStacks.exact(48, ItemList.Circuit_Wafer_HPIC),
                RecipeStacks.exact(4, ItemList.WetTransformer_LuV_IV),
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
        // 乌拉尔山脉热分离厂 - 巨型热力离心机
        GTValues.RA.stdBuilder()
            .itemOutputs(RecipeStacks.exact(1, OTHItemList.MegaThermalCentrifuge))
            .itemInputs(
                RecipeStacks.exact(64, ItemList.LargeThermalRefinery), // GT++ Industrial Thermal Centrifuge × 64 x 3
                RecipeStacks.exact(64, ItemList.LargeThermalRefinery),
                RecipeStacks.exact(64, ItemList.LargeThermalRefinery),
                ItemList.Electric_Piston_LuV.get(16),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UHV, 64),
                GTUtility.getIntegratedCircuit(17))
            .fluidInputs(
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.gtppMoltenName(MaterialsAlloy.INDALLOY_140)),
                    123 * 144))
            .duration(123 * 20)
            .eut(32000)
            .addTo(Assem);
        // HV Hatches
        TTRecipeAdder.addResearchableAssemblylineRecipe(
            RecipeStacks.exact(1, ItemList.Hatch_Energy_HV),
            123123,
            512,
            (int) TierEU.HV,
            1048576,
            new Object[] { RecipeStacks.exact(64, ItemList.Wireless_Hatch_Energy_HV),
                RecipeStacks.exact(64, ItemList.Wireless_Hatch_Energy_HV),
                RecipeStacks.exact(64, ItemList.Wireless_Hatch_Energy_HV),
                RecipeStacks.exact(64, ItemList.Wireless_Hatch_Energy_HV),
                getGM(MetaTileEntityIDs.ActiveTransformer.ID, 1), getGM(1445, 4),
                new ItemStack(ItemLoader.itemLeekBox, 64, 0),
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GoodGenerator.isModLoaded()
                    ? RecipeStacks.exact(16, Loaders.compactFusionCoil, 2)
                    : null),
                ItemList.Field_Generator_EV.get(64), new Object[] { OrePrefixes.circuit.get(Materials.IV), 64 },
                new Object[] { OrePrefixes.circuit.get(Materials.IV), 64 }, ItemList.Field_Generator_EV.get(64),
                RecipeStacks.exact(64, ItemList.Reactor_Coolant_Sp_6),
                RecipeStacks.exact(64, ItemList.Reactor_Coolant_Sp_6),
                RecipeStacks.exact(64, ItemList.Reactor_Coolant_Sp_6), ItemList.Electric_Pump_HV.get(64)

            },
            new FluidStack[] { new FluidStack(
                FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.SolderingAlloy.mStandardMoltenFluid)),
                64000), FluidRegistry.getFluidStack("cryotheum", 64000) },
            OTHItemList.GTTEEnergyULV.get(1),
            1024 * 20,
            (int) TierEU.HV);
        TTRecipeAdder.addResearchableAssemblylineRecipe(
            RecipeStacks.exact(1, ItemList.Hatch_Dynamo_HV),
            123123,
            512,
            (int) TierEU.HV,
            1048576,
            new Object[] { getGM(MetaTileEntityIDs.HV4ADynamoHatch.ID, 64),
                getGM(MetaTileEntityIDs.HV4ADynamoHatch.ID, 64), getGM(MetaTileEntityIDs.HV4ADynamoHatch.ID, 64),
                getGM(MetaTileEntityIDs.HV4ADynamoHatch.ID, 64), getGM(MetaTileEntityIDs.ActiveTransformer.ID, 1),
                getGM(1445, 4), new ItemStack(ItemLoader.itemLeekBox, 64, 0),
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GoodGenerator.isModLoaded()
                    ? RecipeStacks.exact(16, Loaders.compactFusionCoil, 2)
                    : null),
                ItemList.Field_Generator_EV.get(64), new Object[] { OrePrefixes.circuit.get(Materials.IV), 64 },
                new Object[] { OrePrefixes.circuit.get(Materials.IV), 64 }, ItemList.Field_Generator_EV.get(64),
                RecipeStacks.exact(64, ItemList.Reactor_Coolant_Sp_6),
                RecipeStacks.exact(64, ItemList.Reactor_Coolant_Sp_6),
                RecipeStacks.exact(64, ItemList.Reactor_Coolant_Sp_6), ItemList.Electric_Pump_HV.get(64)

            },
            new FluidStack[] { new FluidStack(
                FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.SolderingAlloy.mStandardMoltenFluid)),
                64000), FluidRegistry.getFluidStack("cryotheum", 64000) },
            OTHItemList.GTTEDynamoULV.get(1),
            1024 * 20,
            (int) TierEU.HV);
        // Wood Fusion
        addCraftingRecipe(
            OTHItemList.WoodFusion.get(1),
            new Object[] { "AAA", "ABA", "AAA", 'A', new ItemStack(Blocks.log, 1, 0), 'B',
                RecipeStacks.exact(1, ItemList.Hull_Bronze_Bricks) });
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
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
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
        GTValues.RA.stdBuilder()
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Phosphorus, 7))
            .fluidOutputs(
                Materials.Hydrogen.getGas(26000),
                Materials.Oxygen.getGas(30000),
                Materials.Nitrogen.getGas(21000))
            .fluidInputs(BWLiquids.LCL.getFluidOrGas(20000))
            .eut(1920)
            .duration(80 * 20)
            .addTo(RecipeMaps.electrolyzerNonCellRecipes);
        // Asuka Model
        GTValues.RA.stdBuilder()
            .fluidInputs(BWLiquids.LCL.getFluidOrGas(50000))
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                RecipeStacks.exact(8, ItemList.Color_14),
                RecipeStacks.exact(8, ItemList.Color_06),
                RecipeStacks.generated(16, MetaGeneratedItem01.INSTANCE, OrePrefixes.plate, Materials.Polycaprolactam))
            .itemOutputs(RecipeStacks.exact(1, ItemAndBlockHandler.MODEL_ASUKA, 0))
            .eut(320)
            .duration(40 * 20)
            .addTo(assemblerRecipes);
        // ZPM Module
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                RecipeStacks.exact(1, ItemList.BatteryHull_ZPM_Full),
                RecipeStacks.exact(1, ItemList.BatteryHull_UEV_Full),
                RecipeStacks.exact(8, ItemList.Sensor_UV),
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTPlusPlus.isModLoaded()
                    ? RecipeStacks.exact(16, ModBlocks.blockCasings3Misc, 11)
                    : null),
                RecipeStacks.exact(1, ItemList.ZPM2))
            .itemOutputs(RecipeStacks.exact(1, ItemList.ZPM))
            .duration(100 * 20)
            .eut(TierEU.UHV)
            .addTo(assemblerRecipes);
        // Record EVA
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(GTUtility.getIntegratedCircuit(17), new ItemStack(Items.diamond, 3))
            .fluidInputs(BWLiquids.LCL.getFluidOrGas(1000))
            .itemOutputs(
                new ItemStack(ItemLoader.RecordEVAA, 1),
                new ItemStack(ItemLoader.RecordEVAB, 1),
                new ItemStack(ItemLoader.RecordEVAC, 1))
            .duration(20)
            .eut(30)
            .addTo(assemblerRecipes);
        // Record Never gonna give you up
        GTValues.RA.stdBuilder()
            .itemOutputs(new ItemStack(ItemLoader.RecordNGGU))
            .itemInputsUnsafe(GTUtility.getIntegratedCircuit(17), new ItemStack(ItemLoader.itemLeekBox))
            .duration(20)
            .eut(114)
            .addTo(assemblerRecipes);
        // MISA FACTORY
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, MetaTileEntityIDs.Controller_IsaMill.ID),
                RecipeStacks.exact(64, ItemList.FlotationCell),
                GTModHandler
                    .getModItem("gregtech", "gt.blockmachines", 64, MetaTileEntityIDs.Controller_Vacuum_Furnace.ID),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UHV, 64))
            .itemOutputs(RecipeStacks.exact(1, OTHItemList.MISA))
            .fluidInputs(Materials.GlueAdvanced.getFluid(262144))
            .duration(114514)
            .eut(123123123)
            .addTo(Chem);
        // FOOD GENERATOR
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.FooD.get(1))
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Electric_Motor_HV.get(64),
                ItemList.Casing_HV.get(16),
                RecipeStacks.exact(64, ItemList.SteamTurbine))
            .duration(123 * 20)
            .eut(123)
            .addTo(Assem);

        // DTPF timing
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(new ItemStack(Blocks.cobblestone, 1))
            .itemOutputs(new ItemStack(Blocks.stone, 1))
            .duration(3600 * 64 * 20)
            .eut(7680)
            .addTo(RecipeMaps.plasmaForgeRecipes);
        // Lasers
        // LV

        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Emitter_LV.get(64),
                ItemList.Emitter_LV.get(64),
                ItemList.Emitter_LV.get(64),
                ItemList.Emitter_LV.get(64),
                ItemList.Electric_Pump_LV.get(64),
                ItemList.Hatch_Energy_LV.get(64))
            .itemOutputs(RecipeStacks.exact(1, OTHItemList.LVA))
            .fluidInputs(Materials.Hydrogen.getPlasma(32))
            .duration(3600 * 20)
            .eut(30)
            .addTo(Assem);
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Sensor_LV.get(64),
                ItemList.Sensor_LV.get(64),
                ItemList.Sensor_LV.get(64),
                ItemList.Sensor_LV.get(64),
                ItemList.Electric_Pump_LV.get(64),
                ItemList.Hatch_Energy_LV.get(64))
            .itemOutputs(RecipeStacks.exact(1, OTHItemList.LVB))
            .fluidInputs(Materials.Hydrogen.getPlasma(32))
            .duration(3600 * 20)
            .eut(30)
            .addTo(Assem);
        // MV
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Emitter_MV.get(64),
                ItemList.Emitter_MV.get(64),
                ItemList.Emitter_MV.get(64),
                ItemList.Emitter_MV.get(64),
                ItemList.Electric_Pump_MV.get(64),
                ItemList.Hatch_Energy_MV.get(64))
            .itemOutputs(RecipeStacks.exact(1, OTHItemList.MVA))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_MV))
            .duration(3600 * 20)
            .eut(RECIPE_MV)
            .addTo(Assem);
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Sensor_MV.get(64),
                ItemList.Sensor_MV.get(64),
                ItemList.Sensor_MV.get(64),
                ItemList.Sensor_MV.get(64),
                ItemList.Electric_Pump_MV.get(64),
                ItemList.Hatch_Energy_MV.get(64))
            .itemOutputs(RecipeStacks.exact(1, OTHItemList.MVB))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_MV))
            .duration(3600 * 20)
            .eut(RECIPE_MV)
            .addTo(Assem);
        // HV
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Emitter_HV.get(64),
                ItemList.Emitter_HV.get(64),
                ItemList.Emitter_HV.get(64),
                ItemList.Emitter_HV.get(64),
                ItemList.Electric_Pump_HV.get(64),
                ItemList.Hatch_Energy_HV.get(64))
            .itemOutputs(RecipeStacks.exact(1, OTHItemList.HVA))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_HV))
            .duration(3600 * 20)
            .eut(RECIPE_HV)
            .addTo(Assem);
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Sensor_HV.get(64),
                ItemList.Sensor_HV.get(64),
                ItemList.Sensor_HV.get(64),
                ItemList.Sensor_HV.get(64),
                ItemList.Electric_Pump_HV.get(64),
                ItemList.Hatch_Energy_HV.get(64))
            .itemOutputs(RecipeStacks.exact(1, OTHItemList.HVB))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_HV))
            .duration(3600 * 20)
            .eut(RECIPE_HV)
            .addTo(Assem);
        // EV
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Emitter_EV.get(64),
                ItemList.Emitter_EV.get(64),
                ItemList.Emitter_EV.get(64),
                ItemList.Emitter_EV.get(64),
                ItemList.Electric_Pump_EV.get(64),
                ItemList.Hatch_Energy_EV.get(64))
            .itemOutputs(RecipeStacks.exact(1, OTHItemList.EVA))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_EV))
            .duration(3600 * 20)
            .eut(RECIPE_EV)
            .addTo(Assem);
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Sensor_EV.get(64),
                ItemList.Sensor_EV.get(64),
                ItemList.Sensor_EV.get(64),
                ItemList.Sensor_EV.get(64),
                ItemList.Electric_Pump_EV.get(64),
                ItemList.Hatch_Energy_EV.get(64))
            .itemOutputs(RecipeStacks.exact(1, OTHItemList.EVB))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_EV))
            .duration(3600 * 20)
            .eut(RECIPE_EV)
            .addTo(Assem);
        // IV
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Emitter_IV.get(64),
                ItemList.Emitter_IV.get(64),
                ItemList.Emitter_IV.get(64),
                ItemList.Emitter_IV.get(64),
                ItemList.Electric_Pump_IV.get(64),
                ItemList.Hatch_Energy_IV.get(64))
            .itemOutputs(RecipeStacks.exact(1, OTHItemList.IVA))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_IV))
            .duration(3600 * 20)
            .eut(RECIPE_IV)
            .addTo(Assem);
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Sensor_IV.get(64),
                ItemList.Sensor_IV.get(64),
                ItemList.Sensor_IV.get(64),
                ItemList.Sensor_IV.get(64),
                ItemList.Electric_Pump_IV.get(64),
                ItemList.Hatch_Energy_IV.get(64))
            .itemOutputs(RecipeStacks.exact(1, OTHItemList.IVB))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_IV))
            .duration(3600 * 20)
            .eut(RECIPE_IV)
            .addTo(Assem);
        // LuV
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Emitter_LuV.get(64),
                ItemList.Emitter_LuV.get(64),
                ItemList.Emitter_LuV.get(64),
                ItemList.Emitter_LuV.get(64),
                ItemList.Electric_Pump_LuV.get(64),
                ItemList.Hatch_Energy_LuV.get(64))
            .itemOutputs(RecipeStacks.exact(1, OTHItemList.LUVA))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_LuV))
            .duration(3600 * 20)
            .eut(RECIPE_LuV)
            .addTo(Assem);
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Sensor_LuV.get(64),
                ItemList.Sensor_LuV.get(64),
                ItemList.Sensor_LuV.get(64),
                ItemList.Sensor_LuV.get(64),
                ItemList.Electric_Pump_LuV.get(64),
                ItemList.Hatch_Energy_LuV.get(64))
            .itemOutputs(RecipeStacks.exact(1, OTHItemList.LUVB))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_LuV))
            .duration(3600 * 20)
            .eut(RECIPE_LuV)
            .addTo(Assem);
        // ZPM
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Emitter_ZPM.get(64),
                ItemList.Emitter_ZPM.get(64),
                ItemList.Emitter_ZPM.get(64),
                ItemList.Emitter_ZPM.get(64),
                ItemList.Electric_Pump_ZPM.get(64),
                ItemList.Hatch_Energy_ZPM.get(64))
            .itemOutputs(RecipeStacks.exact(1, OTHItemList.ZPMA))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_ZPM))
            .duration(3600 * 20)
            .eut(RECIPE_ZPM)
            .addTo(Assem);
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Sensor_ZPM.get(64),
                ItemList.Sensor_ZPM.get(64),
                ItemList.Sensor_ZPM.get(64),
                ItemList.Sensor_ZPM.get(64),
                ItemList.Electric_Pump_ZPM.get(64),
                ItemList.Hatch_Energy_ZPM.get(64))
            .itemOutputs(RecipeStacks.exact(1, OTHItemList.ZPMB))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_ZPM))
            .duration(3600 * 20)
            .eut(RECIPE_ZPM)
            .addTo(Assem);
        // UV
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Emitter_UV.get(64),
                ItemList.Emitter_UV.get(64),
                ItemList.Emitter_UV.get(64),
                ItemList.Emitter_UV.get(64),
                ItemList.Electric_Pump_UV.get(64),
                ItemList.Hatch_Energy_UV.get(64))
            .itemOutputs(RecipeStacks.exact(1, OTHItemList.UVA))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_UV))
            .duration(3600 * 20)
            .eut(RECIPE_UV)
            .addTo(Assem);
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Sensor_UV.get(64),
                ItemList.Sensor_UV.get(64),
                ItemList.Sensor_UV.get(64),
                ItemList.Sensor_UV.get(64),
                ItemList.Electric_Pump_UV.get(64),
                ItemList.Hatch_Energy_UV.get(64))
            .itemOutputs(RecipeStacks.exact(1, OTHItemList.UVB))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_UV))
            .duration(3600 * 20)
            .eut(RECIPE_UV)
            .addTo(Assem);
        // UHV
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Emitter_UHV.get(64),
                ItemList.Emitter_UHV.get(64),
                ItemList.Emitter_UHV.get(64),
                ItemList.Emitter_UHV.get(64),
                ItemList.Electric_Pump_UHV.get(64),
                ItemList.Hatch_Energy_UHV.get(64))
            .itemOutputs(RecipeStacks.exact(1, OTHItemList.UHVA))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_UHV))
            .duration(3600 * 20)
            .eut(RECIPE_UHV)
            .addTo(Assem);
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Sensor_UHV.get(64),
                ItemList.Sensor_UHV.get(64),
                ItemList.Sensor_UHV.get(64),
                ItemList.Sensor_UHV.get(64),
                ItemList.Electric_Pump_UHV.get(64),
                ItemList.Hatch_Energy_UHV.get(64))
            .itemOutputs(RecipeStacks.exact(1, OTHItemList.UHVB))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_UHV))
            .duration(3600 * 20)
            .eut(RECIPE_UHV)
            .addTo(Assem);
        // UEV
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Emitter_UEV.get(64),
                ItemList.Emitter_UEV.get(64),
                ItemList.Emitter_UEV.get(64),
                ItemList.Emitter_UEV.get(64),
                ItemList.Electric_Pump_UEV.get(64),
                ItemList.Hatch_Energy_UEV.get(64))
            .itemOutputs(RecipeStacks.exact(1, OTHItemList.UEVA))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_UHV))
            .duration(3600 * 20)
            .eut(RECIPE_UEV)
            .addTo(Assem);
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Sensor_UEV.get(64),
                ItemList.Sensor_UEV.get(64),
                ItemList.Sensor_UEV.get(64),
                ItemList.Sensor_UEV.get(64),
                ItemList.Electric_Pump_UEV.get(64),
                ItemList.Hatch_Energy_UEV.get(64))
            .itemOutputs(RecipeStacks.exact(1, OTHItemList.UEVB))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_UHV))
            .duration(3600 * 20)
            .eut(RECIPE_UEV)
            .addTo(Assem);
        // UIV
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Emitter_UIV.get(64),
                ItemList.Emitter_UIV.get(64),
                ItemList.Emitter_UIV.get(64),
                ItemList.Emitter_UIV.get(64),
                ItemList.Electric_Pump_UIV.get(64),
                ItemList.Hatch_Energy_UIV.get(64))
            .itemOutputs(RecipeStacks.exact(1, OTHItemList.UIVA))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_UHV))
            .duration(3600 * 20)
            .eut(RECIPE_UIV)
            .addTo(Assem);
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Sensor_UIV.get(64),
                ItemList.Sensor_UIV.get(64),
                ItemList.Sensor_UIV.get(64),
                ItemList.Sensor_UIV.get(64),
                ItemList.Electric_Pump_UIV.get(64),
                ItemList.Hatch_Energy_UIV.get(64))
            .itemOutputs(RecipeStacks.exact(1, OTHItemList.UIVB))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_UHV))
            .duration(3600 * 20)
            .eut(RECIPE_UIV)
            .addTo(Assem);
        // UMV
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Emitter_UMV.get(64),
                ItemList.Emitter_UMV.get(64),
                ItemList.Emitter_UMV.get(64),
                ItemList.Emitter_UMV.get(64),
                ItemList.Electric_Pump_UMV.get(64),
                ItemList.Hatch_Energy_UMV.get(64))
            .itemOutputs(RecipeStacks.exact(1, OTHItemList.UMVA))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_UHV))
            .duration(3600 * 20)
            .eut(RECIPE_UMV)
            .addTo(Assem);
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Sensor_UMV.get(64),
                ItemList.Sensor_UMV.get(64),
                ItemList.Sensor_UMV.get(64),
                ItemList.Sensor_UMV.get(64),
                ItemList.Electric_Pump_UMV.get(64),
                ItemList.Hatch_Energy_UMV.get(64))
            .itemOutputs(RecipeStacks.exact(1, OTHItemList.UMVB))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_UHV))
            .duration(3600 * 20)
            .eut(RECIPE_UMV)
            .addTo(Assem);
        // UXV
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Field_Generator_UXV.get(64),
                ItemList.Hatch_Energy_UXV.get(64))
            .itemOutputs(RecipeStacks.exact(1, OTHItemList.UXVA), RecipeStacks.exact(1, OTHItemList.UXVB))
            .fluidInputs(Materials.Hydrogen.getPlasma(RECIPE_UXV))
            .duration(36000 * 20)
            .eut(RECIPE_UXV)
            .addTo(Assem);
        // NQF
        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, RecipeStacks.exact(1, ItemList.UniversalChemicalFuelEngine))
            .metadata(SCANNING, new Scanning(2400, RECIPE_LuV))
            .itemInputs(
                ItemList.Casing_ZPM.get(16),
                ItemList.Electric_Motor_ZPM.get(64),
                ItemList.Electric_Pump_ZPM.get(32),
                ItemList.Field_Generator_ZPM.get(16),
                RecipeStacks.exact(1, OTHItemList.ZPMA),
                eM_Power.get(64),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, MetaTileEntityIDs.ActiveTransformer.ID),
                RecipeStacks.casing(1, Casings.SuperconductingCoilBlock),
                ItemList.Emitter_ZPM.get(64),
                new Object[] { OrePrefixes.circuit.get(Materials.UHV), 64 },
                new Object[] { OrePrefixes.circuit.get(Materials.UHV), 64 },
                GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorZPM, 64),
                RecipeStacks.exact(64, ItemList.UniversalChemicalFuelEngine),
                RecipeStacks.exact(1, ItemList.LargeNaquadahReactor),
                RecipeStacks.exact(1, ItemList.Generator_Naquadah_Mark_IV),
                RecipeStacks.exact(64, ItemList.UniversalChemicalFuelEngine))
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
                BWMaterials.IrOsSm.get(OrePrefixes.plate, 64),
                OTHItemList.dustLookNEIM.get(64),
                OTHItemList.dustLookNEIM.get(64),
                BWMaterials.IrOsSm.get(OrePrefixes.plate, 64),
                BWMaterials.IrOsSm.get(OrePrefixes.plate, 64),
                OTHItemList.dustLookNEIM.get(64),
                OTHItemList.dustLookNEIM.get(64),
                BWMaterials.IrOsSm.get(OrePrefixes.plate, 64),
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
                RecipeStacks.gtppComponent(64, STANDALONE.HYPOGEN, ComponentTypes.PLATEDOUBLE),
                OTHItemList.itemEnqingM.get(64),
                OTHItemList.itemEnqingM.get(64),
                RecipeStacks.gtppComponent(64, STANDALONE.HYPOGEN, ComponentTypes.PLATEDOUBLE),
                RecipeStacks.gtppComponent(64, STANDALONE.HYPOGEN, ComponentTypes.PLATEDOUBLE),
                OTHItemList.itemEnqingM.get(64),
                OTHItemList.itemEnqingM.get(64),
                RecipeStacks.gtppComponent(64, STANDALONE.HYPOGEN, ComponentTypes.PLATEDOUBLE),
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
            .metadata(RESEARCH_ITEM, OTHItemList.UXVA.get(1))
            .metadata(SCANNING, new Scanning(10 * HOURS / 2, RECIPE_UXV))
            .itemInputsUnsafe(
                GTOreDictUnificator.get(OrePrefixes.block, Materials.Universium, 64),
                GTUtility.copyAmountUnsafe(640, ItemList.Machine_Multi_PlasmaForge.get(1)),
                GTUtility.copyAmountUnsafe(256, ItemList.Electric_Motor_UXV.get(1)),
                GTUtility.copyAmountUnsafe(256, ItemList.Electric_Piston_UXV.get(1)),
                GTUtility.copyAmountUnsafe(256, ItemList.Electric_Pump_UXV.get(1)),
                GTUtility.copyAmountUnsafe(256, ItemList.Electric_Piston_UXV.get(1)),
                GTUtility.copyAmountUnsafe(256, ItemList.Robot_Arm_UXV.get(1)),
                GTUtility.copyAmountUnsafe(128, ItemList.Field_Generator_UXV.get(1)),
                GTUtility.copyAmountUnsafe(2112, ItemList.Casing_Coil_Eternal.get(2112)),
                ItemList.EnergisedTesseract.get(64),
                OTHItemList.UXVA.get(4),
                OTHItemList.UEVB.get(4),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.MAX, 64),
                GTUtility.copyAmountUnsafe(128, GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UXV, 1)),
                GTUtility.copyAmountUnsafe(256, GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UMV, 1)),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SpaceTime, 64))
            .fluidInputs(
                Materials.DimensionallyShiftedSuperfluid.getFluid(123123),
                (Materials.Infinity.getMolten(114514)),
                (Materials.Eternity.getMolten(144 * 1024)),
                Materials.Universium.getMolten(144 * 256))
            .itemOutputs(OTHItemList.OTEBBPlasmaForge.get(1))
            .eut(RECIPE_MAX)
            .duration(HOURS * 24)
            .addTo(AssemblyLine);
        // OTEComputer
        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, OTHItemList.OTEHatchRack.get(1))
            .metadata(SCANNING, new Scanning((24 * HOURS) / 2, RECIPE_UV))
            .itemInputs(
                RecipeStacks.exact(1, ItemList.EndothermicFridge),
                // GTUtility.copyAmount(8, ItemRegistry.megaMachines[1]),
                CustomItemList.Machine_Multi_Computer.get(8),
                CustomItemList.Machine_Multi_Switch_Adv.get(8),
                CustomItemList.DATApipe.get(64),
                CustomItemList.dataOut_Hatch.get(1),
                GTModHandler.getModItem("OpenComputers", "item", 32, 103),
                GTModHandler.getModItem("OpenComputers", "item", 32, 103),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UV, 64),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UHV, 32))
            .fluidInputs(
                FluidRegistry.getFluidStack(RecipeFluids.ic2FluidName(InternalName.fluidUuMatter), 10000),
                Materials.SuperCoolant.getFluid(123123),
                Materials.Infinity.getMolten(9216),
                FluidRegistry.getFluidStack("cryotheum", 64000))
            .itemOutputs(OTHItemList.OTEComputer.get(1))
            .eut(RECIPE_UHV)
            .duration(120 * 20)
            .addTo(AssemblyLine);
        // HatchRack
        GTValues.RA.stdBuilder()
            .itemInputs(
                CustomItemList.eM_Computer_Bus.get(4),
                ItemList.Hatch_Input_Bus_UV.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UV, 2),
                CustomItemList.DATApipe.get(16),
                ItemList.Electric_Motor_UV.get(4),
                GTModHandler.getModItem("IC2", "reactorVentDiamond", 1, 1))
            .itemOutputs(OTHItemList.OTEHatchRack.get(1))
            .fluidInputs(Materials.Iridium.getMolten(9 * INGOTS))
            .requiresCleanRoom()
            .duration(40 * SECONDS)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(assemblerRecipes);
        // MINI NUKE
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.AF.get(1))
            .itemInputsUnsafe(
                OTHItemList.LargeQuHub.get(4),
                ItemList.IV_Coil.get(16),
                ItemList.Casing_IV.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorIV, 64),
                ItemList.Circuit_Chip_HPIC.get(64))
            .duration(114 * 20)
            .eut(4096)
            .addTo(Assem);
        // KOH
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                OTHItemList.dustLookNEIM.get(0),
                Materials.Potassium.getDust(64))
            .fluidInputs(Materials.Oxygen.getGas(64000), Materials.Hydrogen.getGas(64000))
            .itemOutputs(
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTPlusPlus.isModLoaded()
                    && GregtechItemList.PotassiumHydroxide.hasBeenSet()
                        ? RecipeStacks.exact(64, GregtechItemList.PotassiumHydroxide.getItem(), 12)
                        : null))
            .eut(114514)
            .duration(12)

            .addTo(Chem);
        // NAF
        GTValues.RA.stdBuilder()
            .metadata(
                RESEARCH_ITEM,
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GoodGenerator.isModLoaded()
                    ? RecipeStacks.exact(1, Loaders.FRF_Casings, 0)
                    : null))
            .metadata(SCANNING, new Scanning(120, RECIPE_UHV))
            .itemInputs(
                RecipeStacks.exact(64, ItemList.NaquadahFuelRefinery),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Naquadah, 64),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Naquadria, 64),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.NaquadahEnriched, 64),
                ItemList.Field_Generator_UHV.get(32),
                ItemList.Field_Generator_UV.get(64),
                GTOreDictUnificator.get(OrePrefixes.wireGt08, Materials.SuperconductorUHV, 64),
                new Object[] { OrePrefixes.circuit.get(Materials.ZPM), 64 },
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GoodGenerator.isModLoaded()
                    ? RecipeStacks.exact(64, Loaders.advancedRadiationProtectionPlate, 0)
                    : null),
                GTOreDictUnificator.get(OrePrefixes.pipeHuge, Materials.Infinity, 16),
                GTOreDictUnificator.get(OrePrefixes.pipeHuge, Materials.Infinity, 16),
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GoodGenerator.isModLoaded()
                    ? RecipeStacks.exact(64, Loaders.advancedRadiationProtectionPlate, 0)
                    : null),
                RecipeStacks.exact(64, NHItemList.RadoxPolymerLens.item, 0),
                ItemList.Circuit_Wafer_QPIC.get(64),
                GTOreDictUnificator.get(OrePrefixes.bolt, Materials.Thulium, 64),
                RecipeStacks.exact(64, NHItemList.RadoxPolymerLens.item, 0))
            .fluidInputs(
                Materials.CosmicNeutronium.getMolten(92160),
                FluidRegistry.getFluidStack(RecipeFluids.gtppMoltenName(MaterialMisc.MUTATED_LIVING_SOLDER), 114514),
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
                GTModHandler.getModItem(GregTech.ID, "gt.blockmachines", 64, MetaTileEntityIDs.CircuitAssemblyLine.ID),
                OTHItemList.LCA.get(64),
                new Object[] { OrePrefixes.circuit.get(Materials.UHV), 64 },
                new Object[] { OrePrefixes.circuit.get(Materials.UHV), 64 },
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
                FluidRegistry.getFluidStack(RecipeFluids.gtppMoltenName(MaterialsAlloy.INDALLOY_140), 288000))
            .itemOutputs(OTHItemList.MCA.get(1))
            .eut(RECIPE_UHV)
            .duration(480 * 20)
            .addTo(AssemblyLine);

        // 太空钻机模块MK-321 - Assembler alt
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                ItemList.SpaceElevatorModulePumpT3.get(4),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Universium, 8),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UXV, 16),
                ItemList.Electric_Pump_MAX.get(8),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Universium, 8),
                GTOreDictUnificator.get(OrePrefixes.screw, Materials.BlackDwarfMatter, 64),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Universium, 16))
            .fluidInputs(Materials.DimensionallyShiftedSuperfluid.getFluid(9216))
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
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Universium, 4),
                new Object[] { OrePrefixes.circuit.get(Materials.UXV), 16 }, ItemList.Electric_Pump_MAX.get(8),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Universium, 4),
                GTOreDictUnificator.get(OrePrefixes.screw, Materials.BlackDwarfMatter, 64) },
            new FluidStack[] { Materials.DimensionallyShiftedSuperfluid.getFluid(9216),
                Materials.Universium.getMolten(2304) },
            OTHItemList.SpaceElevatorModulePumpT4.getInternalStack_unsafe(),
            123 * 20,
            (int) TierEU.RECIPE_UXV);
        // bin
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.TP.get(1))
            .itemInputsUnsafe(GTUtility.getIntegratedCircuit(17), Materials.Steel.getPlates(4))
            .eut(1)
            .duration(1)
            .addTo(Assem);
        // Bedrock
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(Materials.Bedrockium.getDust(1))
            .itemOutputs(setStackSize(Materials.SiliconDioxide.getDust(1), 26244), Materials.Carbon.getDust(9))
            .eut(RECIPE_UIV)
            .duration(40)
            .addTo(RecipeMaps.centrifugeNonCellRecipes);
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(Materials.Bedrockium.getDust(1))
            .itemOutputs(setStackSize(Materials.SiliconDioxide.getDust(1), 26244), Materials.Carbon.getDust(9))
            .eut(RECIPE_UIV)
            .duration(40)
            .addTo(centrifugeRecipes);
        // AE2singularity
        GTValues.RA.stdBuilder()
            .itemOutputs(
                ExternalRecipeItems.aeItem(1, ExternalRecipeItems.AeItem.SINGULARITY)
                    .setStackDisplayName(StatCollector.translateToLocal("oth.recipe.main.any256000Items")))
            .duration(0)
            .eut(0)
            .addTo(AE2);
        // Scrap
        GTValues.RA.stdBuilder()
            .itemOutputs(ItemList.IC2_Scrap.get(1))
            .itemInputsUnsafe(
                new ItemStack(Blocks.cobblestone, 1)
                    .setStackDisplayName(StatCollector.translateToLocal("oth.recipe.main.anyItem")))
            .eut(30)
            .duration(0)
            .addTo(RCY);

        // EIOM
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.EIO.get(1))
            .itemInputsUnsafe(
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
            .metadata(
                RESEARCH_ITEM,
                GTModHandler.getModItem(
                    "gregtech",
                    "gt.blockmachines",
                    1,
                    MetaTileEntityIDs.DraconicEvolutionFusionCrafterController.ID))
            .metadata(SCANNING, new Scanning(HOURS, RECIPE_UV))
            .itemInputs(
                ItemList.Electric_Motor_UHV.get(64),
                ItemList.Electric_Pump_UHV.get(16),
                OTHItemList.AbstractSanctum.get(8),
                GTModHandler.getModItem(
                    "gregtech",
                    "gt.blockmachines",
                    8,
                    MetaTileEntityIDs.DraconicEvolutionFusionCrafterController.ID),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Void, 64),
                GTOreDictUnificator.get(OrePrefixes.wireGt02, Materials.SuperconductorUHV, 64),
                GTOreDictUnificator.get(OrePrefixes.wireGt02, Materials.SuperconductorUHV, 64),
                new Object[] { OrePrefixes.circuit.get(Materials.UIV), 64 },
                GTModHandler.getModItem(DraconicEvolution.ID, "draconicCore", 64),
                GTModHandler.getModItem(DraconicEvolution.ID, "draconiumEnergyCore", 64))
            .fluidInputs(
                FluidRegistry.getFluidStack(RecipeFluids.gtppMoltenName(MaterialMisc.MUTATED_LIVING_SOLDER), 9216),
                Materials.Infinity.getMolten(9216),
                Materials.Void.getMolten(9216))
            .itemOutputs(OTHItemList.OTEGraveDragon.get(1))
            .duration(600 * 20)
            .eut(TierEU.RECIPE_UIV)
            .addTo(AssemblyLine);
        // Egg
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Antimony, 1))
            .fluidInputs(Materials.Nitrogen.getGas(4000))
            .itemOutputs(new ItemStack(Items.egg, 3))
            .eut(114514)
            .duration(60)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
        // Salt Wasser
        GTValues.RA.stdBuilder()
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 2))
            .fluidInputs(Materials.SaltWater.getFluid(2000))
            .itemInputsUnsafe(GTUtility.getIntegratedCircuit(17), OTHItemList.dustIrOsSmM.get(0))
            .fluidOutputs(Materials.Nitrogen.getGas(1000), FluidRegistry.getFluidStack("fluid.formaldehyde", 2000))
            .duration(100)
            .eut(123123)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
        // TallHat
        addCraftingRecipe(
            RecipeStacks.exact(1, ItemAndBlockHandler.TALL_HAT, 0),
            new Object[] { "A~~", "~~~", "~~~", 'A', OTHItemList.Tallhat.get(1) });
        addCraftingRecipe(
            OTHItemList.Tallhat.get(1),
            new Object[] { "A~~", "~~~", "~~~", 'A', RecipeStacks.exact(1, ItemAndBlockHandler.TALL_HAT, 0) });
        addCraftingRecipe(
            OTHItemList.Tallhat.get(1),
            new Object[] { "~A~", "ABA", "A~A", 'A', new ItemStack(Items.paper, 1), 'B',
                new ItemStack(Items.dye, 1, 0) });
        // Steam 9in1
        addCraftingRecipe(
            OTHItemList.SteamNeinInOne.get(1),
            new Object[] { "ABC", "DEF", "GHI", 'A', getGM(MetaTileEntityIDs.Controller_SteamMaceratorMulti.ID, 1), 'B',
                RecipeStacks.exact(1, ItemList.Machine_Steel_Boiler), 'C',
                getGM(MetaTileEntityIDs.Controller_SteamCompressorMulti.ID, 1), 'D',
                getGM(MetaTileEntityIDs.Controller_SteamCentrifugeMulti.ID, 1), 'E',
                getGM(MetaTileEntityIDs.WaterPump.ID, 1), 'F',
                getGM(MetaTileEntityIDs.Controller_SteamWasherMulti.ID, 1), 'G',
                getGM(MetaTileEntityIDs.Controller_SteamForgeHammer.ID, 1), 'H',
                getGM(MetaTileEntityIDs.Controller_SteamMixerMulti.ID, 1), 'I',
                RecipeStacks.exact(1, OTHItemList.WoodFusion) });
        // Beeyonds House
        //
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                RecipeStacks.exact(4, ItemList.Machine_IndustrialApiary),
                ItemList.AcceleratorMV.get(4),
                ItemList.Field_Generator_LV.get(4))
            .itemOutputs(OTHItemList.Beeyonds.get(1))
            .fluidInputs(Materials.Honey.getFluid(123))
            .eut(RECIPE_HV)
            .duration(123 * SECONDS)
            .addTo(assemblerRecipes);
        // Fish
        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, ItemList.FishingPort.get(1))
            .metadata(SCANNING, new Scanning(1200, RECIPE_ZPM))
            .itemInputs(
                ItemList.FishingPort.get(64),
                ItemList.Field_Generator_LuV.get(16),
                ItemList.Sensor_LuV.get(4),
                ItemList.FishingPort.get(64),
                ItemList.Electric_Motor_LuV.get(63),
                ItemList.Electric_Pump_LuV.get(48),
                ItemList.Electric_Pump_LuV.get(48),
                ItemList.Electric_Motor_LuV.get(63),
                OTHItemList.itemEnqingM.get(64),
                OTHItemList.itemEnqingM.get(64),
                OTHItemList.LeekBox.get(1),
                ExternalRecipeItems.aeFluidItem(1, ExternalRecipeItems.AeFluidItem.WALRUS))
            .fluidInputs(
                Materials.Iridium.getMolten(123000),
                Materials.Osmium.getMolten(123000),
                Materials.Samarium.getMolten(123000),
                Materials.Water.getFluid(129000))
            .itemOutputs(OTHItemList.FISH.get(1))
            .eut(RECIPE_ZPM)
            .duration(1)
            .addTo(AssemblyLine);

        // Large Qu
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.LargeQuHub.get(1))
            .itemInputsUnsafe(
                ItemList.Casing_LV.get(1),
                ItemList.LV_Coil.get(4),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LV, 4))
            .fluidInputs(Materials.Lubricant.getFluid(123))
            .eut(RECIPE_ULV)
            .duration(120)
            .addTo(assemblerRecipes);

        // nail
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Casing_ZPM.get(4),
                OTHItemList.YellowPeach.get(16),
                ItemList.Electric_Motor_LuV.get(1),
                ItemList.Robot_Arm_LuV.get(1))
            .itemOutputs(new ItemStack(NailongCasings.nailongCasing, 4, NailongCasingMeta.NAILONG))
            .metadata(PRECISE_ASSEMBLER_CASING_TIER, 1)
            .duration(12 * SECONDS)
            .eut(RECIPE_LuV)
            .addTo(preciseAssemblerRecipes);
        // guantou
        GTValues.RA.stdBuilder()
            .itemInputs(
                new ItemStack(NailongCasings.nailongCasing, 1, NailongCasingMeta.NAILONG),
                OTHItemList.YellowPeach.get(16),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.BorosilicateGlass, 16),
                OTHItemList.LeekBox.get(64),
                ItemList.Electric_Motor_LuV.get(2),
                ItemList.Field_Generator_IV.get(1))
            .itemOutputs(new ItemStack(NailongCasings.nailongCasing, 1, NailongCasingMeta.PEACH_CAN))
            .eut(RECIPE_LuV)
            .duration(60 * 20)
            .addTo(assemblerRecipes);
        // Positron_e+_artiliary
        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, OTHItemList.itemEnqingM.get(1))
            .metadata(SCANNING, new Scanning(120, RECIPE_ZPM))
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Tritanium, 4),
                ItemList.Field_Generator_ZPM.get(16),
                ItemList.Emitter_ZPM.get(4),
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GoodGenerator.isModLoaded()
                    ? RecipeStacks.exact(16, Loaders.advancedRadiationProtectionPlate, 0)
                    : null),
                GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorZPM, 64),
                ItemList.Energy_Module.get(1),
                ItemList.Energy_Module.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorZPM, 64),
                RecipeStacks.exact(4, NHItemList.HighEnergyFlowCircuit.item, 0),
                GTModHandler.getModItem(GregTech.ID, "gt.blockmachines", 1, 10506),
                GTModHandler.getModItem(GregTech.ID, "gt.blockmachines", 1, 10507),
                GTModHandler.getModItem(GregTech.ID, "gt.blockmachines", 1, MetaTileEntityIDs.HATCH_BEAMLINE_OUTPUT.ID),
                new ItemStack(Blocks.stone_button, 1),
                WerkstoffLoader.HDCS.get(OrePrefixes.stickLong, 4),
                WerkstoffLoader.HDCS.get(OrePrefixes.plate, 6),
                new ItemStack(Items.clock, 1))
            .fluidInputs(
                Materials.Iridium.getMolten(1440),
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.gtppMoltenName(MaterialsAlloy.INDALLOY_140)),
                    144000),
                Materials.SuperCoolant.getFluid(32000))
            .itemOutputs(OTHItemList.PositronCannon.get(1))
            .eut(RECIPE_UV)
            .duration(120)
            .addTo(AssemblyLine);
        // TST Burger
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(GTCMItemList.ProofOfHeroes.get(1), new ItemStack(Items.bread, 2))
            .itemOutputs(OTHItemList.itemTST.get(1))
            .duration(123 * SECONDS)
            .eut(RECIPE_UXV)
            .addTo(RecipeMaps.neutroniumCompressorRecipes);
        // SnAs Dust
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(Materials.Tin.getDust(1), Materials.Arsenic.getDust(1))
            .itemOutputs(OTHItemList.dustSnAsM.get(1))
            .duration(20)
            .eut(RECIPE_LV)
            .addTo(RecipeMaps.mixerNonCellRecipes);
        GTValues.RA.stdBuilder()
            .itemInputs(OTHItemList.ingotSnAsM.get(1))
            .itemOutputs(OTHItemList.dustSnAsM.get(1))
            .duration(100)
            .eut(RECIPE_LV)
            .addTo(RecipeMaps.maceratorRecipes);
        // SnAs
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.ingotSnAsM.get(1))
            .itemInputsUnsafe(OTHItemList.dustSnAsM.get(1))
            .fluidInputs(OTHMaterials.LCL.getFluid(1000))
            .duration(20)
            .eut(RECIPE_EV)
            .addTo(RecipeMaps.autoclaveRecipes);
        // Elytra
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.PoweredElytra.get(1))
            .itemInputs(
                GTModHandler.getModItem(EtFuturumRequiem.ID, "elytra", 1),
                GTModHandler.getModItem(IC2.MODID, "itemPartCarbonPlate", 4),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LuV, 4),
                GTModHandler.getModItem(GraviSuite.ID, "itemSimpleItem", 2, 3),
                ItemList.Energy_LapotronicOrb.get(1))
            .fluidInputs(Materials.ReinforcedGlass.getMolten(2880))
            .eut(RECIPE_IV)
            .duration(120 * 20)
            .addTo(assemblerRecipes);
        // Drone Platform
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                RecipeStacks.exact(1, ItemList.Hull_EV),
                ItemList.Cover_SolarPanel_MV.get(1),
                ItemList.Cover_Screen.get(1),
                ItemList.Electric_Piston_EV.get(4),
                ItemList.Sensor_EV.get(2),
                ItemList.Emitter_EV.get(2),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 2))
            .itemOutputs(OTHItemList.DroneLaunchPlatform.get(1))
            .fluidInputs(Materials.StainlessSteel.getMolten(2880))
            .duration(20 * SECONDS)
            .eut(RECIPE_EV)
            .addTo(Assem);
        // Drone P1-G
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.SuicideDronePig.get(1))
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 5),
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Steel, 1),
                ItemList.Sensor_MV.get(1),
                ItemList.Emitter_MV.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LV, 2),
                new ItemStack(Items.porkchop, 1),
                new ItemStack(Items.gunpowder, 4))
            .fluidInputs(Materials.SolderingAlloy.getMolten(288))
            .duration(5 * SECONDS)
            .eut(RECIPE_MV)
            .addTo(Assem);
        // Drone Angelica
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.SuicideDroneAngelica.get(1))
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.RedSteel, 5),
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.TungstenSteel, 1),
                ItemList.Sensor_HV.get(1),
                ItemList.Emitter_HV.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.HV, 2),
                new ItemStack(Blocks.tnt, 4))
            .fluidInputs(Materials.SolderingAlloy.getMolten(576))
            .duration(5 * SECONDS)
            .eut(RECIPE_IV)
            .addTo(Assem);
        // Drone NVIDIA
        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, OTHItemList.SuicideDronePig.get(1))
            .metadata(SCANNING, new Scanning(400, RECIPE_IV))
            .itemOutputs(OTHItemList.SuicideDroneNvidia.get(1))
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.NaquadahAlloy, 10),
                GTOreDictUnificator.get(OrePrefixes.stickLong, Materials.TungstenSteel, 2),
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.NaquadahAlloy, 2),
                ItemList.Sensor_IV.get(1),
                ItemList.Emitter_IV.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 2),
                OTHItemList.NukeThrowable.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(14400), Materials.Lubricant.getFluid(1440))
            .duration(5 * SECONDS)
            .eut(RECIPE_LuV)
            .addTo(AssemblyLine);
        // Drone NUKE
        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, OTHItemList.SuicideDroneNvidia.get(1))
            .metadata(SCANNING, new Scanning(800, RECIPE_LuV))
            .itemOutputs(OTHItemList.SuicideDroneNuclear.get(1))
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Neutronium, 10),
                ItemList.Radiation_Proof_Prismatic_Naquadah_Composite_Sheet.get(4),
                GTOreDictUnificator.get(OrePrefixes.stickLong, Materials.Osmiridium, 2),
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Iridium, 3),
                ItemList.Sensor_LuV.get(1),
                ItemList.Emitter_LuV.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LuV, 3),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 5),
                ItemList.Cover_AdvancedRedstoneReceiver.get(1),
                GTModHandler.getModItem(IC2.MODID, "blockITNT", 2),
                GTModHandler.getModItem(IC2.MODID, "blockGenerator", 1, 5),
                GTModHandler.getModItem(IC2.MODID, "blockReactorChamber", 1),
                ItemList.Neutron_Reflector.get(2),
                ItemList.RodNaquadah32.get(2),
                ItemList.RodUranium4.get(2),
                ItemList.Neutron_Reflector.get(2))
            .fluidInputs(
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.gtppMoltenName(MaterialsAlloy.INDALLOY_140)),
                    57600),
                Materials.Lubricant.getFluid(2880),
                Materials.Uranium.getMolten(1440),
                Materials.Polybenzimidazole.getMolten(5760))
            .duration(5 * SECONDS)
            .eut(RECIPE_UV)
            .addTo(AssemblyLine);
        // Chiang Kai Shek
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Hull_LV.get(1),
                new ItemStack(Blocks.noteblock, 4),
                ItemList.Sensor_LV.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LV, 1L))
            .itemOutputs(OTHItemList.SpeakingStone.get(1))
            .duration(5 * SECONDS)
            .eut(RECIPE_LV)
            .addTo(assemblerRecipes);
        // NASA Hatch
        // Schematic chip hatch: crafted in a standard assembler, not in the NASA workshop.
        GTRecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Hatch_Input_Bus_HV.get(1),
                ItemList.Sensor_HV.get(1),
                ItemList.Robot_Arm_HV.get(2),
                Materials.StainlessSteel.getPlates(16))
            .fluidInputs(Materials.SolderingAlloy.getMolten(2880))
            .itemOutputs(OTHItemList.RocketSchematicHatch.get(1))
            .duration(100)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.assemblerRecipes);
        // NASA
        GTRecipeBuilder.builder()
            .itemOutputs(OTHItemList.NASA.get(1))
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GTModHandler.getModItem(GalacticraftCore.ID, "tile.rocketWorkbench", 1),
                GTModHandler.getModItem(GalacticraftCore.ID, "item.basicItem", 64, 14),
                GTModHandler.getModItem(GalaxySpace.ID, "item.RobotArm", 4),
                ItemList.Machine_HV_Compressor.get(1),
                ItemList.Machine_HV_Bender.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 8))
            .fluidInputs(Materials.Titanium.getMolten(5760))
            .duration(20 * SECONDS)
            .eut(RECIPE_HV)
            .addTo(Assem);
    }
}
