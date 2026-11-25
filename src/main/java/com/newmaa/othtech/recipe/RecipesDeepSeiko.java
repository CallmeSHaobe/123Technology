package com.newmaa.othtech.recipe;

import static com.Nxer.TwistSpaceTechnology.util.TSTUtils.setStackSize;
import static com.Nxer.TwistSpaceTechnology.util.enums.TierEU.RECIPE_LuV;
import static com.Nxer.TwistSpaceTechnology.util.enums.TierEU.RECIPE_UHV;
import static com.Nxer.TwistSpaceTechnology.util.enums.TierEU.RECIPE_UV;
import static com.Nxer.TwistSpaceTechnology.util.enums.TierEU.RECIPE_UXV;
import static com.Nxer.TwistSpaceTechnology.util.enums.TierEU.RECIPE_ZPM;
import static com.google.common.math.LongMath.pow;
import static com.newmaa.othtech.common.recipemap.Recipemaps.DeepSeikoAL;
import static com.newmaa.othtech.common.recipemap.Recipemaps.DeepSeikoBEC;
import static com.newmaa.othtech.utils.Utils.hasNullItem;
import static gregtech.api.enums.ItemList.DysonSwarmModule;
import static gregtech.api.enums.Mods.GTPlusPlus;
import static gregtech.api.enums.Mods.GoodGenerator;
import static gregtech.api.enums.Mods.GraviSuite;
import static gregtech.api.enums.Mods.GregTech;
import static gregtech.api.enums.Mods.PamsHarvestCraft;
import static gregtech.api.enums.Mods.SuperSolarPanels;
import static gregtech.api.enums.TierEU.RECIPE_UMV;
import static gregtech.api.util.GTModHandler.getModItem;
import static gregtech.api.util.GTRecipeBuilder.INGOTS;
import static gregtech.api.util.GTRecipeBuilder.MINUTES;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;
import static gregtech.api.util.GTRecipeConstants.COMPRESSION_TIER;
import static gregtech.api.util.GTRecipeConstants.NANITE_TIERS;
import static gtPlusPlus.core.material.MaterialMisc.MUTATED_LIVING_SOLDER;
import static tectech.thing.CustomItemList.EOH_Reinforced_Spatial_Casing;
import static tectech.thing.CustomItemList.SpacetimeCompressionFieldGeneratorTier0;
import static tectech.thing.CustomItemList.SpacetimeCompressionFieldGeneratorTier1;
import static tectech.thing.CustomItemList.SpacetimeCompressionFieldGeneratorTier2;
import static tectech.thing.CustomItemList.SpacetimeCompressionFieldGeneratorTier3;
import static tectech.thing.CustomItemList.SpacetimeCompressionFieldGeneratorTier4;
import static tectech.thing.CustomItemList.SpacetimeCompressionFieldGeneratorTier5;
import static tectech.thing.CustomItemList.SpacetimeCompressionFieldGeneratorTier6;
import static tectech.thing.CustomItemList.SpacetimeCompressionFieldGeneratorTier7;
import static tectech.thing.CustomItemList.SpacetimeCompressionFieldGeneratorTier8;
import static tectech.thing.CustomItemList.TimeAccelerationFieldGeneratorTier0;
import static tectech.thing.CustomItemList.TimeAccelerationFieldGeneratorTier1;
import static tectech.thing.CustomItemList.TimeAccelerationFieldGeneratorTier2;
import static tectech.thing.CustomItemList.TimeAccelerationFieldGeneratorTier3;
import static tectech.thing.CustomItemList.TimeAccelerationFieldGeneratorTier4;
import static tectech.thing.CustomItemList.TimeAccelerationFieldGeneratorTier5;
import static tectech.thing.CustomItemList.TimeAccelerationFieldGeneratorTier6;
import static tectech.thing.CustomItemList.TimeAccelerationFieldGeneratorTier7;
import static tectech.thing.CustomItemList.TimeAccelerationFieldGeneratorTier8;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.Nxer.TwistSpaceTechnology.common.GTCMItemList;
import com.Nxer.TwistSpaceTechnology.common.api.ModItemHandler.ModItem;
import com.Nxer.TwistSpaceTechnology.util.recipes.TST_RecipeBuilder;
import com.dreammaster.item.NHItemList;
import com.newmaa.othtech.OTHTechnology;
import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.blocks.deepseiko.DeepSeikoBlocks;
import com.newmaa.othtech.common.blocks.deepseiko.DeepSeikoCasingMeta;
import com.newmaa.othtech.common.materials.BWLiquids;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.machine.OTEDeepSeiko;

import bartworks.system.material.WerkstoffLoader;
import goodgenerator.items.GGMaterial;
import goodgenerator.util.ItemRefer;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.CondensateType;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MetaTileEntityIDs;
import gregtech.api.enums.NaniteTier;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.objects.GTItemStack;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTRecipeConstants;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.material.MaterialMisc;
import gtPlusPlus.core.material.MaterialsAlloy;
import gtPlusPlus.core.material.MaterialsElements;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;
import gtnhlanth.common.register.LanthItemList;
import gtnhlanth.common.register.WerkstoffMaterialPool;
import tectech.recipe.TTRecipeAdder;
import tectech.recipe.TecTechRecipeMaps;
import wanion.avaritiaddons.block.chest.infinity.BlockInfinityChest;

public class RecipesDeepSeiko implements IRecipePool {

    private static final Logger AL_LOG = LogManager.getLogger("123Technology/DeepSeikoAL");
    private static final Logger BEC_LOG = LogManager.getLogger("123Technology/DeepSeikoBEC");
    private static Map<String, Fluid> entangledToSource;

    // ===================== [Recipe Registration] =====================

    @Override
    public void loadRecipes() {
        addRiceRecipe();
        addCasingRecipes();
        addMachineRecipes();
        addRiceSingularityRecipe();
        addSpacetimeCoolingCore();
        addDeepSeikoBecRecipe();
        addDsCasingAssemblyLineRecipe();
        addAssemblyCasingAssemblyLineRecipe();
        addExoticCasingRecipes();
        addFinCasingRecipes();
        addCondensateAdapterBecRecipe();
        addComputeCenterBecRecipes();
        addUpgradeRecipes();
    }

    // ===================== [Rice and Cooling Components] =====================

    private void addRiceRecipe() {
        if (!PamsHarvestCraft.isModLoaded()) return;
        ItemStack rice = getModItem(PamsHarvestCraft.ID, "riceItem", 1, 0);
        if (hasNullItem(rice)) {
            OTHTechnology.LOG.warn("Pam's rice not found, white rice recipe skipped");
            return;
        }
        GTModHandler.addCraftingRecipe(
            OTHItemList.WhiteRice.get(2),
            new Object[] { "RRR", " B ", 'R', rice.copy(), 'B', new ItemStack(Items.bowl, 1) });
    }

    private void addRiceSingularityRecipe() {
        GTValues.RA.stdBuilder()
            .itemInputs(OTHItemList.WhiteRice.get(1234))
            .itemOutputs(OTHItemList.RiceSingularity.get(1))
            .metadata(COMPRESSION_TIER, 2)
            .duration(1234 * 20)
            .eut(RECIPE_UMV)
            .addTo(RecipeMaps.neutroniumCompressorRecipes);
    }

    private void addSpacetimeCoolingCore() {
        ItemStack[] inputs = new ItemStack[] { new ItemStack(Blocks.ice, 64), new ItemStack(Blocks.packed_ice, 64),
            firstNonNull(getModItem("BiomesOPlenty", "hardIce", 64, 0), getModItem("BiomesOPlenty", "hard_ice", 64, 0)),
            getModItem("etfuturum", "blue_ice", 64, 0), getModItem(GraviSuite.ID, "itemSimpleItem", 64, 2),
            new ItemStack(GregTechAPI.sBlockCasings8, 64, 14), ItemList.StableAdhesive.get(32),
            ItemList.EnergisedTesseract.get(16), GTOreDictUnificator.get("plateSuperdenseCallistoIce", 64),
            getModItem("universalsingularities", "universal.vanilla.singularity", 64, 3) };

        if (Arrays.stream(inputs)
            .anyMatch(Objects::isNull)) {
            OTHTechnology.LOG.warn("Spacetime Cooling Core recipe skipped: missing item inputs");
            return;
        }

        FluidStack water = Materials.Water.getFluid(123321);
        FluidStack coolant = FluidRegistry.getFluidStack("ic2coolant", 123321);
        FluidStack superCoolant = Materials.SuperCoolant.getFluid(123321);
        FluidStack spacetime = FluidRegistry.getFluidStack("molten.spacetime", 1017);
        if (water == null || coolant == null || superCoolant == null || spacetime == null) {
            OTHTechnology.LOG.warn("Spacetime Cooling Core recipe skipped: missing fluid inputs");
            return;
        }

        ItemStack researchItem = new ItemStack(GregTechAPI.sBlockCasingsFoundry, 1, 9);
        boolean ok = TTRecipeAdder.addResearchableAssemblylineRecipe(
            researchItem,
            123_321_000,
            8192,
            2_000_000_000,
            4,
            inputs,
            new FluidStack[] { water, coolant, superCoolant, spacetime },
            OTHItemList.SpacetimeCoolingCore.get(1),
            6000,
            500_000_000);
        if (!ok) OTHTechnology.LOG.warn(
            "Failed to register Spacetime Cooling Core recipe (addResearchableAssemblylineRecipe returned false)");
    }

    // ===================== [Casings and Machine Components] =====================

    private void addCasingRecipes() {
        ItemStack[][] casings = {
            { stack(DeepSeikoCasingMeta.BLUBBER_FILLER, 4), stack(DeepSeikoCasingMeta.BLUBBER_CASING, 4),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Lead, 8) },
            { stack(DeepSeikoCasingMeta.TAIL_FIN_FRAME, 4), stack(DeepSeikoCasingMeta.BLUBBER_CASING, 4),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Titanium, 4) }, };
        for (ItemStack[] recipe : casings) {
            if (hasNullItem((Object[]) recipe)) continue;
            GTValues.RA.stdBuilder()
                .itemInputs(tail(recipe))
                .fluidInputs(new FluidStack(FluidRegistry.getFluid("molten.polytetrafluoroethylene"), 288))
                .itemOutputs(recipe[0])
                .duration(100)
                .eut(TierEU.RECIPE_IV)
                .addTo(RecipeMaps.assemblerRecipes);
        }
    }

    private void addMachineRecipes() {
        GTModHandler.addCraftingRecipe(
            OTHItemList.DeepSeikoNaniteCell.get(1),
            new Object[] { "BBB", "BGB", "BBB", 'B', stack(DeepSeikoCasingMeta.BLUBBER_CASING, 1), 'G',
                ItemList.Field_Generator_UIV.get(1) });
    }

    private void addDsCasingAssemblyLineRecipe() {
        ItemStack[] inputs = new ItemStack[] {
            new ItemStack(GregTechAPI.sBlockMachines, 64, MetaTileEntityIDs.HULL_UIV.ID),
            GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 32017),
            GTModHandler.getModItem("gregtech", "gt.metaitem.01", 48, 32041),
            GTModHandler.getModItem("gregtech", "gt.metaitem.01", 32, 32045),
            GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 6154),
            GTModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 31174),
            GTModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 18174),
            GTModHandler.getModItem("gregtech", "gt.blockcasings5", 32, 13),
            GTModHandler.getModItem("gregtech", "gt.foundrycasings", 8, 9),
            GTModHandler.getModItem("bartworks", "BW_ExtraGlass", 32, 4),
            GTModHandler.getModItem("gregtech", "gt.blockframes", 48, 588),
            GTModHandler.getModItem("gregtech", "gt.blockframes", 32, 154),
            GTModHandler.getModItem("gregtech", "gt.sheetmetal", 64, 582) };
        if (hasNullItem((Object[]) inputs)) {
            OTHTechnology.LOG.warn("[DeepSeiko] Blubber Casing assembly line recipe skipped: missing item inputs");
            return;
        }

        FluidStack spacetime = moltenOrFluid(Materials.SpaceTime, 1000);
        FluidStack solder = MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(12_300);
        FluidStack superconductor = moltenOrFluid(Materials.SuperconductorUIVBase, 5000);
        FluidStack shiftedSuperfluid = Materials.DimensionallyShiftedSuperfluid.getFluid(100_000);
        if (hasNullItem((Object[]) new FluidStack[] { spacetime, solder, superconductor, shiftedSuperfluid })) {
            OTHTechnology.LOG.warn("[DeepSeiko] Blubber Casing assembly line recipe skipped: missing fluid inputs");
            return;
        }

        ItemStack research = OTHItemList.RiceSingularity.get(1);
        if (hasNullItem((Object) research)) {
            OTHTechnology.LOG
                .warn("[DeepSeiko] Blubber Casing assembly line recipe skipped: Rice Singularity not found");
            return;
        }
        try {
            boolean ok = TTRecipeAdder.addResearchableAssemblylineRecipe(
                research,
                16_777_216,
                2048,
                201_234_576,
                1,
                inputs,
                new FluidStack[] { spacetime, solder, superconductor, shiftedSuperfluid },
                new ItemStack(DeepSeikoBlocks.dsCasing, 64, DeepSeikoCasingMeta.BLUBBER_CASING),
                20 * 300,
                (int) RECIPE_UMV);
            if (ok) {
                OTHTechnology.LOG.info(
                    "[DeepSeiko] Registered Blubber Casing Research Station/assembly line recipe (16777216/2048 → 8192 s of research, 6000 ticks of assembly)");
            } else {
                OTHTechnology.LOG.warn(
                    "[DeepSeiko] Failed to register Blubber Casing Research Station/assembly line recipe (TTRecipeAdder returned false)");
            }
        } catch (Throwable t) {
            OTHTechnology.LOG
                .error("[DeepSeiko] Exception registering Blubber Casing Research Station/assembly line recipe", t);
        }
    }

    private void addAssemblyCasingAssemblyLineRecipe() {
        ItemStack[] inputs = new ItemStack[] {
            GTModHandler.getModItem("GoodGenerator", "componentAssemblylineCasing", 1, 10),
            GTModHandler.getModItem("gregtech", "gt.metaitem.01", 4, 32033),
            GTModHandler.getModItem("gregtech", "gt.metaitem.02", 8, 21588),
            GTModHandler.getModItem("gregtech", "gt.metaitem.02", 16, 24588),
            new ItemStack(GregTechAPI.sBlockMachines, 16, 876), new ItemStack(GregTechAPI.sBlockMachines, 1, 2716),
            GTModHandler.getModItem("gtnhintergalactic", "dysonswarmparts", 8, 4),
            GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 32269),
            GTModHandler.getModItem("gregtech", "gt.metaitem.03", 2, 6148),
            GTModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 6154),
            GTModHandler.getModItem("gregtech", "gt.sheetmetal", 2, 588),
            new ItemStack(DeepSeikoBlocks.dsCasing, 1, DeepSeikoCasingMeta.BLUBBER_CASING) };
        if (hasNullItem((Object[]) inputs)) {
            OTHTechnology.LOG
                .warn("[DeepSeiko] Assembly Chamber Casing assembly line recipe skipped: missing item inputs");
            return;
        }
        FluidStack solder = mutatedLivingSolder(12_000);
        FluidStack spacetime = moltenOrFluid(Materials.SpaceTime, 24_000);
        FluidStack hypogen = fluidByNames(30_000, "molten.hypogen", "hypogen", "liquid_hypogen");
        FluidStack superconductor = moltenOrFluid(Materials.SuperconductorUIVBase, 6000);
        if (hasNullItem((Object[]) new FluidStack[] { solder, spacetime, hypogen, superconductor })) {
            OTHTechnology.LOG
                .warn("[DeepSeiko] Assembly Chamber Casing assembly line recipe skipped: missing fluid inputs");
            return;
        }
        ItemStack research = OTHItemList.SpacetimeCoolingCore.get(1);
        if (hasNullItem((Object) research)) {
            OTHTechnology.LOG.warn(
                "[DeepSeiko] Assembly Chamber Casing assembly line recipe skipped: Spacetime Cooling Core not found");
            return;
        }
        try {
            boolean ok = TTRecipeAdder.addResearchableAssemblylineRecipe(
                research,
                32_777_216,
                4096,
                Integer.MAX_VALUE,
                1,
                inputs,
                new FluidStack[] { solder, spacetime, hypogen, superconductor },
                new ItemStack(DeepSeikoBlocks.dsCasing, 12, DeepSeikoCasingMeta.ASSEMBLY_CASING),
                20 * 300,
                (int) RECIPE_UMV);
            if (ok) {
                OTHTechnology.LOG.info(
                    "[DeepSeiko] Registered Assembly Chamber Casing Research Station/assembly line recipe (32777216/4096 → 8000 s)");
            } else {
                OTHTechnology.LOG.warn(
                    "[DeepSeiko] Failed to register Assembly Chamber Casing Research Station/assembly line recipe (TTRecipeAdder returned false)");
            }
        } catch (Throwable t) {
            OTHTechnology.LOG.error(
                "[DeepSeiko] Exception registering Assembly Chamber Casing Research Station/assembly line recipe",
                t);
        }
    }

    private void addExoticCasingRecipes() {
        ItemStack blubber = new ItemStack(DeepSeikoBlocks.dsCasing, 8, DeepSeikoCasingMeta.BLUBBER_CASING);

        ItemStack[] darkInputs = new ItemStack[] { blubber,
            GTModHandler.getModItem("gregtech", "gt.sheetmetal", 64, 368),
            GTModHandler.getModItem("gregtech", "gt.blockmetal9", 32, 4), exoticCircuit(8),
            new ItemStack(GregTechAPI.sBlockMachines, 2, 2086) };
        if (!hasNullItem((Object[]) darkInputs)) {
            try {
                GTValues.RA.stdBuilder()
                    .itemInputs(darkInputs)
                    .fluidInputs(mutatedLivingSolder(16_000))
                    .itemOutputs(new ItemStack(DeepSeikoBlocks.dsCasing, 8, DeepSeikoCasingMeta.BLUBBER_DARK))
                    .duration(20 * 300)
                    .eut(TierEU.RECIPE_UIV)
                    .addTo(RecipeMaps.assemblerRecipes);
            } catch (Throwable t) {
                OTHTechnology.LOG.error("[DeepSeiko] Failed to register Blubber Casing (Dark) recipe", t);
            }
        } else {
            OTHTechnology.LOG.warn("[DeepSeiko] Blubber Casing (Dark) recipe skipped: missing item inputs");
        }

        ItemStack[] lightInputs = new ItemStack[] { blubber, GTUtility.getIntegratedCircuit(5),
            GTModHandler.getModItem("gregtech", "gt.blockframes", 16, 174),
            GTModHandler.getModItem("Avaritia", "Resource", 8, 8), exoticCircuit(8),
            GTModHandler.getModItem("gregtech", "gt.sheetmetal", 64, 80) };
        if (!hasNullItem((Object[]) lightInputs)) {
            try {
                GTValues.RA.stdBuilder()
                    .itemInputs(lightInputs)
                    .fluidInputs(mutatedLivingSolder(16_000))
                    .itemOutputs(new ItemStack(DeepSeikoBlocks.dsCasing, 8, DeepSeikoCasingMeta.BLUBBER_LIGHT))
                    .duration(20 * 300)
                    .eut(TierEU.RECIPE_UIV)
                    .addTo(RecipeMaps.assemblerRecipes);
            } catch (Throwable t) {
                OTHTechnology.LOG.error("[DeepSeiko] Failed to register Blubber Casing (Light Blue) recipe", t);
            }
        } else {
            OTHTechnology.LOG.warn("[DeepSeiko] Blubber Casing (Light Blue) recipe skipped: missing item inputs");
        }

        ItemStack[] grilleInputs = new ItemStack[] { GTUtility.getIntegratedCircuit(7),
            GTModHandler.getModItem("dreamcraft", "NeutroniumBars", 64),
            GTModHandler.getModItem("gregtech", "gt.metaitem.01", 4, 32025),
            new ItemStack(GregTechAPI.sBlockMachines, 8, 5306),
            GTModHandler.getModItem("gregtech", "gt.metaitem.01", 4, 32051),
            GTModHandler.getModItem("gregtech", "gt.metaitem.01", 32, 22154),
            GTModHandler.getModItem("gregtech", "gt.metaitem.03", 16, 32264),
            GTModHandler.getModItem("gregtech", "gt.metaitem.03", 16, 4581) };
        if (!hasNullItem((Object[]) grilleInputs)) {
            try {
                GTValues.RA.stdBuilder()
                    .itemInputs(grilleInputs)
                    .itemOutputs(new ItemStack(DeepSeikoBlocks.dsCasing, 8, DeepSeikoCasingMeta.BALEEN_GRILLE))
                    .duration(20 * 120)
                    .eut(TierEU.RECIPE_UIV)
                    .addTo(RecipeMaps.assemblerRecipes);
                OTHTechnology.LOG.info(
                    "[DeepSeiko] Registered Baleen Grille recipe in the Assembler because the original Macerator has only one input slot");
            } catch (Throwable t) {
                OTHTechnology.LOG.error("[DeepSeiko] Failed to register Baleen Grille recipe", t);
            }
        } else {
            OTHTechnology.LOG.warn("[DeepSeiko] Baleen Grille recipe skipped: missing item inputs");
        }
    }

    private void addFinCasingRecipes() {
        int[] circuits = { 2, 3, 4 };
        int[] metas = { DeepSeikoCasingMeta.FIN_TIME, DeepSeikoCasingMeta.FIN_SPACE, DeepSeikoCasingMeta.FIN_STAB };
        String[] names = { "Left Fin (time)", "Right Fin (space)", "Top Fin (stability)" };
        for (int i = 0; i < metas.length; ++i) {
            ItemStack casing = new ItemStack(DeepSeikoBlocks.dsCasing, 1, DeepSeikoCasingMeta.BLUBBER_CASING);
            ItemStack rice = OTHItemList.WhiteRice.get(1);
            ItemStack output = new ItemStack(DeepSeikoBlocks.dsCasing, 1, metas[i]);
            if (hasNullItem(casing, rice, output)) {
                OTHTechnology.LOG.warn("[DeepSeiko] {} recipe skipped: missing item inputs", names[i]);
                continue;
            }
            try {
                GTValues.RA.stdBuilder()
                    .itemInputs(casing, rice, GTUtility.getIntegratedCircuit(circuits[i]))
                    .itemOutputs(output)
                    .duration(20 * 30)
                    .eut(RECIPE_UMV)
                    .addTo(RecipeMaps.assemblerRecipes);
            } catch (Throwable t) {
                OTHTechnology.LOG.error("[DeepSeiko] Failed to register {} recipe", names[i], t);
            }
        }
        OTHTechnology.LOG.info(
            "[DeepSeiko] Registered all three fin recipes (programmed circuits: Left Fin 2 / Right Fin 3 / Top Fin 4)");
    }

    // ===================== [Condensate Assembly Recipes] =====================

    private void addDeepSeikoBecRecipe() {
        ItemStack exoticCircuit = GTOreDictUnificator.get("circuitExotic", 48);
        if (exoticCircuit == null) {
            exoticCircuit = ItemList.Circuit_ExoticMainframe.get(48);
            OTHTechnology.LOG.warn(
                "[DeepSeiko] Ore Dictionary entry circuitExotic not found. Falling back to Circuit_ExoticMainframe ×48");
        }
        ItemStack[] inputs = new ItemStack[] { OTHItemList.RiceSingularity.get(8),
            OTHItemList.SpacetimeCoolingCore.get(16), exoticCircuit, new ItemStack(GregTechAPI.sBlockCasings8, 32, 14),
            new ItemStack(GregTechAPI.sBlockMachines, 64, MetaTileEntityIDs.ASSEMBLING_LINE_CONTROLLER.ID),
            new ItemStack(GregTechAPI.sBlockMachines, 64, MetaTileEntityIDs.AdvancedAssline.ID),
            new ItemStack(GregTechAPI.sBlockMachines, 64, MetaTileEntityIDs.CircuitAssemblyLine.ID),
            new ItemStack(GregTechAPI.sBlockMachines, 64, MetaTileEntityIDs.ComponentAssemblyLine.ID),
            GTModHandler.getModItem("gregtech", "gt.metaitem.03", 32, 32277),
            new ItemStack(GregTechAPI.sBlockMachines, 16, MetaTileEntityIDs.UncertaintyResolution.ID),
            GTModHandler.getModItem("gregtech", "gt.metaitem.01", 48, 32045),
            GTModHandler.getModItem("OpenComputers", "item", 32, 44),
            GTModHandler.getModItem("OpenComputers", "item", 32, 10),
            GTModHandler.getModItem("computronics", "computronics.ocSpecialParts", 8) };
        if (hasNullItem((Object[]) inputs)) {
            OTHTechnology.LOG.warn(
                "[DeepSeiko] DeepSeiko the Big Fat Fish controller condensate recipe skipped: missing item inputs (check OpenComputers/computronics/gt.metaitem)");
            return;
        }
        final int condAmount = 123_264;
        FluidStack[] fluids = new FluidStack[] { entangled(CondensateType.Time, condAmount),
            entangled(CondensateType.Space, condAmount), entangled(CondensateType.Infinity, condAmount),
            entangled(CondensateType.TranscendentMetal, condAmount) };
        if (hasNullItem((Object[]) fluids)) {
            OTHTechnology.LOG.warn(
                "[DeepSeiko] DeepSeiko the Big Fat Fish controller condensate recipe skipped: missing condensate fluids");
            return;
        }
        NaniteTier[] nanites = new NaniteTier[] { NaniteTier.Gold, NaniteTier.Gold, NaniteTier.Gold, NaniteTier.Gold,
            NaniteTier.Carbon, NaniteTier.Carbon, NaniteTier.Silver, NaniteTier.Silver, NaniteTier.Transcendent,
            NaniteTier.Transcendent, NaniteTier.Transcendent, NaniteTier.Carbon, NaniteTier.Carbon,
            NaniteTier.Transcendent };
        try {
            GTValues.RA.stdBuilder()
                .itemInputs(inputs)
                .fluidInputs(fluids)
                .itemOutputs(OTHItemList.DeepSeiko.get(1))
                .metadata(NANITE_TIERS, nanites)
                .duration(20 * 300_000)
                .eut(RECIPE_UMV)
                .addTo(TecTechRecipeMaps.condensateAssemblingRecipes);
            OTHTechnology.LOG.info(
                "[DeepSeiko] Registered DeepSeiko the Big Fat Fish controller condensate (BEC) recipe (14 item inputs, 4 condensates, 6000000 ticks, condensate amount {})",
                condAmount);
        } catch (Throwable t) {
            OTHTechnology.LOG.error(
                "[DeepSeiko] Failed to register DeepSeiko the Big Fat Fish controller condensate (BEC) recipe",
                t);
        }
    }

    private void addCondensateAdapterBecRecipe() {
        ItemStack[] inputs = new ItemStack[] { OTHItemList.DeepSeiko.get(1),
            new ItemStack(DeepSeikoBlocks.dsCasing, 16, DeepSeikoCasingMeta.BLUBBER_CASING),
            new ItemStack(DeepSeikoBlocks.dsCasing, 16, DeepSeikoCasingMeta.BLUBBER_LIGHT),
            new ItemStack(DeepSeikoBlocks.dsCasing, 16, DeepSeikoCasingMeta.BLUBBER_DARK),
            new ItemStack(DeepSeikoBlocks.dsCasing, 32, DeepSeikoCasingMeta.BALEEN_GRILLE),
            GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 32307),
            GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 32310),
            GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 32313),
            GTModHandler.getModItem("gregtech", "gt.metaitem.03", 32, 32316), exoticCircuit(64),
            GTModHandler.getModItem("gregtech", "gt.blockframes", 16, 588),
            GTModHandler.getModItem("miscutils", "gtplusplus.blockcasings.5", 16, 10),
            GTModHandler.getModItem("appliedenergistics2", "item.ItemExtremeStorageCell.Singularity", 1),
            GTModHandler.getModItem("ae2fc", "fluid_storage.singularity", 1),
            GTModHandler.getModItem("dreamcraft", "HeavenlyPikoCircuit", 16),
            GTModHandler.getModItem(OTHTechnology.MODID, "WhiteRice", 64) };
        if (hasNullItem((Object[]) inputs)) {
            OTHTechnology.LOG.warn("[DeepSeiko] Condensate Adapter recipe skipped: missing item inputs");
            return;
        }
        FluidStack time = entangled(CondensateType.Time, 1_008_000);
        FluidStack space = entangled(CondensateType.Space, 1_008_000);
        FluidStack spacetime = entangled(CondensateType.SpaceTime, 3_024_000);
        FluidStack hypogen = entangled(CondensateType.Hypogen, 1_481_328);
        if (hasNullItem((Object[]) new FluidStack[] { time, space, spacetime, hypogen })) {
            OTHTechnology.LOG.warn("[DeepSeiko] Condensate Adapter recipe skipped: missing condensate fluids");
            return;
        }
        NaniteTier[] nanites = new NaniteTier[] { NaniteTier.SixPhasedCopper, NaniteTier.Carbon, NaniteTier.Silver,
            NaniteTier.Gold, NaniteTier.Transcendent, NaniteTier.Gold, NaniteTier.Gold, NaniteTier.Gold,
            NaniteTier.Gold, NaniteTier.Carbon, NaniteTier.Transcendent, NaniteTier.Carbon, NaniteTier.Transcendent,
            NaniteTier.Transcendent, NaniteTier.Carbon, NaniteTier.Carbon };
        try {
            GTValues.RA.stdBuilder()
                .itemInputs(inputs)
                .fluidInputs(time, space, spacetime, hypogen)
                .itemOutputs(new ItemStack(DeepSeikoBlocks.dsCasing, 4, DeepSeikoCasingMeta.CONDENSATE_ADAPTER))
                .metadata(NANITE_TIERS, nanites)
                .duration(20 * 999_999)
                .eut(RECIPE_UMV)
                .addTo(TecTechRecipeMaps.condensateAssemblingRecipes);
            OTHTechnology.LOG
                .info("[DeepSeiko] Registered Condensate Adapter condensate recipe (16 item inputs, 4 condensates)");
        } catch (Throwable t) {
            OTHTechnology.LOG.error("[DeepSeiko] Failed to register Condensate Adapter condensate recipe", t);
        }
    }

    private void addComputeCenterBecRecipes() {
        ItemStack[] d99Inputs = new ItemStack[] { cosmicCircuit(4),
            GTModHandler.getModItem("dreamcraft", "HeavenlyQuantumCircuit", 8),
            GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32758),
            GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 28585),
            GTModHandler.getModItem("gregtech", "gt.metaitem.01", 48, 28586),
            GTModHandler.getModItem("gregtech", "gt.metaitem.01", 32, 28139),
            OTHItemList.SpacetimeCoolingCore.get(16) };
        if (hasNullItem((Object[]) d99Inputs)) {
            OTHTechnology.LOG.warn("[DeepSeiko] D99 Compute Center recipe skipped: missing item inputs");
        } else {
            try {
                GTValues.RA.stdBuilder()
                    .itemInputs(d99Inputs)
                    .fluidInputs(
                        entangled(CondensateType.QuarkGluonPlasma, 7200),
                        entangled(CondensateType.BoundlessCosmicSolder, 15000),
                        entangled(CondensateType.Universium, 3600))
                    .itemOutputs(computeCenter(8, 0))
                    .metadata(
                        NANITE_TIERS,
                        new NaniteTier[] { NaniteTier.Gold, NaniteTier.Transcendent, NaniteTier.SixPhasedCopper,
                            NaniteTier.WhiteDwarf, NaniteTier.BlackDwarf, NaniteTier.Universium,
                            NaniteTier.Universium })
                    .duration(20 * 999_999)
                    .eut(TierEU.RECIPE_UXV)
                    .addTo(TecTechRecipeMaps.condensateAssemblingRecipes);
                OTHTechnology.LOG
                    .info("[DeepSeiko] Registered D99 Compute Center condensate recipe (7 item inputs, 3 condensates)");
            } catch (Throwable t) {
                OTHTechnology.LOG.error("[DeepSeiko] Failed to register D99 Compute Center recipe", t);
            }
        }

        ItemStack[] d199Inputs = new ItemStack[] { GTModHandler.getModItem("dreamcraft", "PlanckCircuit", 8),
            GTModHandler.getModItem("gregtech", "gt.metaitem.03", 16, 32278), OTHItemList.beeMagM.get(32),
            GTModHandler.getModItem("gregtech", "gt.metaitem.01", 2, 32043),
            GTModHandler.getModItem("gregtech", "gt.metaitem.01", 2, 32039),
            GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 32047), computeCenter(8, 0),
            OTHItemList.SpacetimeCoolingCore.get(32) };
        if (hasNullItem((Object[]) d199Inputs)) {
            OTHTechnology.LOG.warn("[DeepSeiko] D199 Compute Center recipe skipped: missing item inputs");
        } else {
            try {
                GTValues.RA.stdBuilder()
                    .itemInputs(d199Inputs)
                    .fluidInputs(
                        entangled(CondensateType.BoundlessCosmicSolder, 720_000),
                        entangled(CondensateType.MagMatter, 144_000),
                        entangled(CondensateType.Eternity, 2_880_000))
                    .itemOutputs(computeCenter(8, 1))
                    .metadata(
                        NANITE_TIERS,
                        new NaniteTier[] { NaniteTier.Transcendent, NaniteTier.SixPhasedCopper, NaniteTier.WhiteDwarf,
                            NaniteTier.BlackDwarf, NaniteTier.Universium, NaniteTier.Eternity, NaniteTier.Eternity,
                            NaniteTier.Eternity })
                    .duration(20 * 2_999_999)
                    .eut(TierEU.RECIPE_UXV)
                    .addTo(TecTechRecipeMaps.condensateAssemblingRecipes);
                OTHTechnology.LOG.info(
                    "[DeepSeiko] Registered D199 Compute Center condensate recipe (8 item inputs, 3 condensates)");
            } catch (Throwable t) {
                OTHTechnology.LOG.error("[DeepSeiko] Failed to register D199 Compute Center recipe", t);
            }
        }

        ItemStack[] d999Inputs = new ItemStack[] { GTModHandler.getModItem("dreamcraft", "PlanckCircuit", 64),
            GTModHandler.getModItem("tectech", "gt.godforgecasing", 32, 7),
            GTModHandler.getModItem("gregtech", "gt.metaitem.02", 16, 30192),
            GTModHandler.getModItem("appliedenergistics2", "item.ItemExtremeStorageCell.Universe", 1),
            GTModHandler.getModItem("ae2fc", "fluid_storage.Universe", 1),
            GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 4143),
            GTModHandler.getModItem("gregtech", "gt.metaitem.03", 32, 6141), computeCenter(16, 1),
            GTModHandler.getModItem("GoodGenerator", "componentAssemblylineCasing", 12, 12),
            GTModHandler.getModItem("appliedenergistics2", "tile.BlockSingularityCraftingStorage", 8),
            GTModHandler.getModItem("tectech", "gt.spacetime_compression_field_generator", 64, 8),
            GTModHandler.getModItem("tectech", "gt.time_acceleration_field_generator", 64, 8),
            GTModHandler.getModItem("tectech", "gt.stabilisation_field_generator", 64, 8),
            GTModHandler.getModItem("tectech", "item.tm.itemAstralArrayFabricator", 1),
            GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 6583),
            GTModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 32039) };
        if (hasNullItem((Object[]) d999Inputs)) {
            OTHTechnology.LOG.warn("[DeepSeiko] D999 Compute Center recipe skipped: missing item inputs");
        } else {
            try {
                GTValues.RA.stdBuilder()
                    .itemInputs(d999Inputs)
                    .fluidInputs(
                        entangled(CondensateType.MHDCSM, 1_008_000),
                        entangled(CondensateType.MagMatter, 1_008_000),
                        entangled(CondensateType.Universium, 1_008_000),
                        entangled(CondensateType.Eternity, 1_008_000))
                    .itemOutputs(computeCenter(8, 2))
                    .metadata(
                        NANITE_TIERS,
                        new NaniteTier[] { NaniteTier.SixPhasedCopper, NaniteTier.WhiteDwarf, NaniteTier.BlackDwarf,
                            NaniteTier.Universium, NaniteTier.Universium, NaniteTier.Eternity, NaniteTier.Eternity,
                            NaniteTier.Eternity, NaniteTier.MagMatter, NaniteTier.MagMatter, NaniteTier.Eternity,
                            NaniteTier.Universium, NaniteTier.BlackDwarf, NaniteTier.WhiteDwarf, NaniteTier.Eternity,
                            NaniteTier.MagMatter })
                    .duration(20 * 9_999_999)
                    .eut(TierEU.RECIPE_MAX)
                    .addTo(TecTechRecipeMaps.condensateAssemblingRecipes);
                OTHTechnology.LOG.info(
                    "[DeepSeiko] Registered D999 Compute Center condensate recipe (16 item inputs, 4 condensates)");
            } catch (Throwable t) {
                OTHTechnology.LOG.error("[DeepSeiko] Failed to register D999 Compute Center recipe", t);
            }
        }
    }

    // ===================== [Upgrade Display Recipes] =====================

    private void addUpgradeRecipes() {
        ItemStack[] req = OTEDeepSeiko.getUpgradeRequirements();
        ItemStack bec = req[0], ultCasing = req[2], ultMagmatter = req[3], ultStargate = req[4];
        int added = 0;

        if (!hasNullItem((Object) bec)) {
            added += addUpgradeRecipeSafely(
                "BEC unlock",
                bec,
                StatCollector.translateToLocal("oth.recipe.deepSeiko.unlockCondensate"),
                StatCollector.translateToLocal("oth.recipe.deepSeiko.condensateRequirement"));
        }

        ItemStack[] tierRefs = OTEDeepSeiko.getNaniteRefsByTier();
        int variants = 0;
        for (int tier = 1; tier <= tierRefs.length; tier++) {
            ItemStack ref = tierRefs[tier - 1];
            if (hasNullItem((Object) ref)) continue;
            ItemStack sixtyFour = ref.copy();
            sixtyFour.stackSize = OTEDeepSeiko.NANITE_PER_TIER;
            variants += addUpgradeRecipeSafely(
                "Nanite T" + (tier - 1) + " to T" + tier,
                sixtyFour,
                StatCollector.translateToLocalFormatted("oth.recipe.deepSeiko.naniteUpgrade", tier - 1, tier),
                StatCollector.translateToLocalFormatted(
                    "oth.recipe.deepSeiko.naniteRequirement",
                    OTEDeepSeiko.NANITE_PER_TIER,
                    tier));
        }
        if (variants == 0) {
            OTHTechnology.LOG
                .warn("[DeepSeiko] Skipped swarm upgrade NEI recipes because no nanites were available at any tier");
        }
        added += variants;

        if (!hasNullItem(ultCasing, ultMagmatter, ultStargate)) {
            try {
                GTValues.RA.stdBuilder()
                    .itemInputsUnsafe(ultCasing, ultMagmatter, ultStargate)
                    .duration(1)
                    .eut(1)
                    .fake()
                    .setNEIDesc(
                        StatCollector.translateToLocal("oth.recipe.deepSeiko.ultimateUpgrade"),
                        StatCollector.translateToLocalFormatted(
                            "oth.recipe.deepSeiko.ultimateRequirement",
                            OTEDeepSeiko.getUltCasingCount(),
                            OTEDeepSeiko.getUltMagmatterCount(),
                            OTEDeepSeiko.getUltStargateCount()))
                    .addTo(Recipemaps.DeepSeikoUpgrade);
                added++;
            } catch (Throwable t) {
                OTHTechnology.LOG.error(
                    "[DeepSeiko] Skipped Ultimate upgrade NEI recipe after a generation error (machine functionality is unaffected)",
                    t);
            }
        }

        OTHTechnology.LOG.info("[DeepSeiko] Added {} Machine Upgrade NEI recipes", added);
    }

    private int addUpgradeRecipeSafely(String label, ItemStack input, String... neiDesc) {
        try {
            GTValues.RA.stdBuilder()
                .itemInputsUnsafe(input)
                .duration(1)
                .eut(1)
                .fake()
                .setNEIDesc(neiDesc)
                .addTo(Recipemaps.DeepSeikoUpgrade);
            return 1;
        } catch (Throwable t) {
            OTHTechnology.LOG
                .error("[DeepSeiko] Failed to generate Machine Upgrade NEI recipe (" + label + "), skipped", t);
            return 0;
        }
    }

    // ===================== [Recipe Helpers] =====================

    private static ItemStack firstNonNull(ItemStack a, ItemStack b) {
        return a != null ? a : b;
    }

    private static FluidStack fluidByNames(int amount, String... names) {
        for (String name : names) {
            FluidStack stack = FluidRegistry.getFluidStack(name, amount);
            if (stack != null) return stack;
        }
        return null;
    }

    private static FluidStack moltenOrFluid(Materials material, int amount) {
        FluidStack molten = material.getMolten(amount);
        return molten != null ? molten : material.getFluid(amount);
    }

    private static ItemStack exoticCircuit(int amount) {
        ItemStack stack = GTOreDictUnificator.get("circuitExotic", amount);
        if (stack == null) {
            stack = ItemList.Circuit_ExoticMainframe.get(amount);
            OTHTechnology.LOG.warn(
                "[DeepSeiko] Ore Dictionary entry circuitExotic not found. Falling back to Circuit_ExoticMainframe ×{}",
                amount);
        }
        return stack;
    }

    private static FluidStack mutatedLivingSolder(int amount) {
        return MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(amount);
    }

    private static ItemStack cosmicCircuit(int amount) {
        ItemStack stack = GTOreDictUnificator.get("circuitCosmic", amount);
        if (stack == null) {
            stack = ItemList.Circuit_CosmicMainframe.get(amount);
            OTHTechnology.LOG.warn(
                "[DeepSeiko] Ore Dictionary entry circuitCosmic not found. Falling back to Circuit_CosmicMainframe ×{}",
                amount);
        }
        return stack;
    }

    private static FluidStack entangled(CondensateType type, int wanted) {
        int step = Math.max(1, type.getUnit() / 2);
        int down = (wanted / step) * step;
        int up = down + step;
        int amount = (wanted - down <= up - wanted) ? down : up;
        if (amount < step) amount = step;
        if (amount != wanted) {
            OTHTechnology.LOG.warn(
                "[DeepSeiko] Condensate {} amount {} is not a multiple of step size {}. Adjusted to {}",
                type.name(),
                wanted,
                step,
                amount);
        }
        return type.getEntangled(amount);
    }

    private static ItemStack computeCenter(int amount, int meta) {
        ItemStack base = OTHItemList.ComputeCenter.get(amount);
        if (base == null) return null;
        ItemStack copy = base.copy();
        copy.setItemDamage(meta);
        return copy;
    }

    private static ItemStack stack(int casingMeta, int amount) {
        return new ItemStack(DeepSeikoBlocks.dsCasing, amount, casingMeta);
    }

    private static ItemStack[] tail(ItemStack[] all) {
        ItemStack[] rest = new ItemStack[all.length - 1];
        System.arraycopy(all, 1, rest, 0, rest.length);
        return rest;
    }

    // ===================== [Assembly Line Import] =====================

    public static void loadAssemblyLineRecipes() {
        DeepSeikoAL.getBackend()
            .clearRecipes();
        loadSpecialAssemblyLineRecipes();
        Set<GTItemStack> specialOutputs = new HashSet<>();
        var specialRecipes = DeepSeikoAL.getAllRecipes();
        for (GTRecipe recipe : specialRecipes) {
            Arrays.stream(filled(recipe.mOutputs, GTValues.emptyItemStackArray))
                .map(GTItemStack::new)
                .forEach(specialOutputs::add);
        }
        int imported = specialRecipes.size();
        int copied = copyVisualAssemblyLine(specialOutputs);
        AL_LOG.info(
            "DeepSeikoAL recipe pool filled with {} visual and {} TST special assembly line recipes",
            copied,
            imported);
    }

    private static int copyVisualAssemblyLine(Set<GTItemStack> specialOutputs) {
        int copied = 0;
        String uhvCircuitOre = OrePrefixes.circuit.get(Materials.UHV)
            .toString();
        Set<GTItemStack> uhvCircuits = new HashSet<>();
        int uhvCircuitOreId = OreDictionary.getOreID(uhvCircuitOre);
        GTOreDictUnificator.getOres(uhvCircuitOre)
            .forEach(stack -> uhvCircuits.add(new GTItemStack(stack)));
        for (GTRecipe recipe : RecipeMaps.assemblylineVisualRecipes.getAllRecipes()) {
            if (recipe == null || recipe.mHidden || hasSpecialOutput(recipe, specialOutputs)) continue;
            try {
                GTRecipeBuilder builder = GTValues.RA.stdBuilder()
                    .fluidInputs(filled(recipe.mFluidInputs, GTValues.emptyFluidStackArray))
                    .itemOutputs(filled(recipe.mOutputs, GTValues.emptyItemStackArray))
                    .duration(recipe.mDuration)
                    .eut(recipe.mEUt);
                ItemStack[] inputs = filled(recipe.mInputs, GTValues.emptyItemStackArray);
                Object[] oreInputs = restoreUhvCircuitSlots(
                    recipe,
                    inputs,
                    uhvCircuitOre,
                    uhvCircuitOreId,
                    uhvCircuits);
                // Forwarding every visual alternative multiplies the paths in GT's recipe lookup tree.
                if (oreInputs == null) builder.itemInputs(inputs);
                else builder.itemInputs(oreInputs);
                copied += builder.addTo(DeepSeikoAL)
                    .size();
            } catch (Throwable t) {
                AL_LOG.warn("Failed to copy an AL visual recipe into DeepSeikoAL", t);
            }
        }
        return copied;
    }

    private static Object[] restoreUhvCircuitSlots(GTRecipe recipe, ItemStack[] inputs, String uhvCircuitOre,
        int uhvCircuitOreId, Set<GTItemStack> uhvCircuits) {
        if (uhvCircuits.size() < 2 || !(recipe instanceof GTRecipe.GTRecipe_WithAlt withAlt) || recipe.mInputs == null)
            return null;
        Object[] oreInputs = null;
        int target = 0;
        for (int i = 0; i < recipe.mInputs.length; i++) {
            ItemStack input = recipe.mInputs[i];
            if (input == null) continue;
            ItemStack[] alternatives = withAlt.mOreDictAlt != null && i < withAlt.mOreDictAlt.length
                ? withAlt.mOreDictAlt[i]
                : null;
            int oreId = withAlt.mOreDictIds != null && i < withAlt.mOreDictIds.length ? withAlt.mOreDictIds[i] : -1;
            boolean tagged = oreId == uhvCircuitOreId;
            if (tagged || isCompleteUhvGroup(input, alternatives, uhvCircuits)) {
                if (oreInputs == null) oreInputs = Arrays.copyOf(inputs, inputs.length, Object[].class);
                oreInputs[target] = new Object[] { uhvCircuitOre, input.stackSize };
            }
            target++;
        }
        return oreInputs;
    }

    private static boolean isCompleteUhvGroup(ItemStack input, ItemStack[] alternatives, Set<GTItemStack> uhvCircuits) {
        if (alternatives == null || alternatives.length < uhvCircuits.size()
            || !uhvCircuits.contains(new GTItemStack(input))) return false;
        Set<GTItemStack> candidates = new HashSet<>();
        for (ItemStack alternative : alternatives) {
            if (alternative == null || alternative.stackSize != input.stackSize) return false;
            GTItemStack key = new GTItemStack(alternative);
            if (!uhvCircuits.contains(key)) return false;
            candidates.add(key);
        }
        return candidates.size() == uhvCircuits.size();
    }

    private static boolean hasSpecialOutput(GTRecipe recipe, Set<GTItemStack> outputs) {
        return Arrays.stream(filled(recipe.mOutputs, GTValues.emptyItemStackArray))
            .filter(Objects::nonNull)
            .map(GTItemStack::new)
            .anyMatch(outputs::contains);
    }

    private static <T> T[] filled(T[] array, T[] empty) {
        if (array == null) return empty;
        return Arrays.stream(array)
            .filter(Objects::nonNull)
            .toArray(
                len -> (T[]) java.lang.reflect.Array.newInstance(
                    array.getClass()
                        .getComponentType(),
                    len));
    }

    // ===================== [TST Special Assembly Line Recipes] =====================

    /*
     * Recipe definitions from Twist Space Technology by Nxer and contributors.
     * Source:
     * https://github.com/Nxer/Twist-Space-Technology-Mod/blob/0.8.0/src/main/java/com/Nxer/TwistSpaceTechnology/recipe/
     * machineRecipe/expanded/AssemblyLineWithoutResearchRecipePool.java
     * SPDX-License-Identifier: GPL-3.0-only
     * Adapted for 123 Technology by redirecting registration to the DeepSeikoAL recipe map.
     */
    private static void loadSpecialAssemblyLineRecipes() {
        final RecipeMap<?> MASL = Recipemaps.DeepSeikoAL;
        final Fluid solderUEV = FluidRegistry.getFluid("molten.mutatedlivingsolder") != null
            ? FluidRegistry.getFluid("molten.mutatedlivingsolder")
            : FluidRegistry.getFluid("molten.solderingalloy");
        final Fluid solderIndalloy = MaterialsAlloy.INDALLOY_140.getFluid();
        final Fluid ic2coolant = FluidRegistry.getFluid("ic2coolant");

        {
            TST_RecipeBuilder.builder()
                .fluidInputs(
                    Materials.SolderingAlloy.getMolten(8000),
                    Materials.Gold.getMolten(2000),
                    Materials.Argon.getGas(1000))
                .itemInputs(
                    GTUtility.getIntegratedCircuit(1),
                    GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Aluminium, 1),
                    GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 4),
                    ItemList.Robot_Arm_LuV.get(4),
                    ItemList.Conveyor_Module_LuV.get(2),
                    GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.Gold, 32),
                    GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.Tungsten, 2))
                .itemOutputs(new ItemStack(LanthItemList.FOCUS_MANIPULATION_CASING))
                .duration(60 * GTRecipeBuilder.SECONDS)
                .eut(TierEU.RECIPE_LuV)
                .addTo(MASL);

            TST_RecipeBuilder.builder()
                .fluidInputs(
                    Materials.SolderingAlloy.getMolten(8000),
                    Materials.Gold.getMolten(2000),
                    Materials.Argon.getGas(1000))
                .itemInputs(
                    GTUtility.getIntegratedCircuit(2),
                    GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Aluminium, 1),
                    GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 4),
                    ItemList.Robot_Arm_LuV.get(4),
                    GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.Gold, 16),
                    GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.Tungsten, 2))
                .itemOutputs(new ItemStack(LanthItemList.TARGET_RECEPTACLE_CASING))
                .duration(60 * GTRecipeBuilder.SECONDS)
                .eut(TierEU.RECIPE_LuV)
                .addTo(MASL);

            TST_RecipeBuilder.builder()
                .fluidInputs(
                    Materials.SolderingAlloy.getMolten(64 * INGOTS),
                    Materials.Argon.getGas(1_000),
                    Materials.Helium.getGas(2_000))
                .itemInputs(
                    GTUtility.getIntegratedCircuit(1),
                    ItemList.Hull_LuV.get(1),
                    GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LuV, 2),
                    new ItemStack(LanthItemList.CAPILLARY_EXCHANGE, 2),
                    ItemList.Electric_Pump_LuV.get(1),
                    LanthItemList.BEAMLINE_PIPE,
                    WerkstoffMaterialPool.MuMetal.get(OrePrefixes.plate, 4))
                .itemOutputs(LanthItemList.LUV_BEAMLINE_INPUT_HATCH)
                .duration(2 * GTRecipeBuilder.MINUTES)
                .eut(TierEU.RECIPE_LuV)
                .addTo(MASL);

        }

        {

            TST_RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(10),
                    GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Tritanium, 2),
                    ItemList.Circuit_Wetwaresupercomputer.get(2),
                    ItemList.Circuit_Parts_InductorASMD.get(16),
                    ItemList.Circuit_Parts_CapacitorASMD.get(16),
                    ItemList.Circuit_Parts_ResistorASMD.get(16),
                    ItemList.Circuit_Parts_TransistorASMD.get(16),
                    ItemList.Circuit_Parts_DiodeASMD.get(16),
                    ItemList.Circuit_Chip_Ram.get(48),
                    GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorUV, 32),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.RubberSilicone, 64))
                .fluidInputs(
                    MaterialsAlloy.INDALLOY_140.getFluidStack(144 * 20),
                    new FluidStack(ic2coolant, 10000),
                    Materials.Radon.getGas(2500))
                .itemOutputs(ItemList.Circuit_Wetwaremainframe.get(1))
                .eut(300000)
                .duration(20 * 100)
                .addTo(MASL);
            TST_RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(10),
                    GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Tritanium, 2),
                    ItemList.Circuit_Wetwaresupercomputer.get(2),
                    ItemList.Circuit_Parts_InductorASMD.get(16),
                    ItemList.Circuit_Parts_CapacitorASMD.get(16),
                    ItemList.Circuit_Parts_ResistorASMD.get(16),
                    ItemList.Circuit_Parts_TransistorASMD.get(16),
                    ItemList.Circuit_Parts_DiodeASMD.get(16),
                    ItemList.Circuit_Chip_Ram.get(48),
                    GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorUV, 32),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.StyreneButadieneRubber, 64))
                .fluidInputs(
                    MaterialsAlloy.INDALLOY_140.getFluidStack(144 * 20),
                    new FluidStack(ic2coolant, 10000),
                    Materials.Radon.getGas(2500))
                .itemOutputs(ItemList.Circuit_Wetwaremainframe.get(1))
                .eut(300000)
                .duration(20 * 100)
                .addTo(MASL);
            TST_RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(10),
                    GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Tritanium, 2),
                    ItemList.Circuit_Wetwaresupercomputer.get(2),
                    ItemList.Circuit_Parts_InductorASMD.get(16),
                    ItemList.Circuit_Parts_CapacitorASMD.get(16),
                    ItemList.Circuit_Parts_ResistorASMD.get(16),
                    ItemList.Circuit_Parts_TransistorASMD.get(16),
                    ItemList.Circuit_Parts_DiodeASMD.get(16),
                    ItemList.Circuit_Chip_Ram.get(48),
                    GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorUHV, 16),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.RubberSilicone, 64))
                .fluidInputs(
                    MaterialsAlloy.INDALLOY_140.getFluidStack(144 * 20),
                    new FluidStack(ic2coolant, 10000),
                    Materials.Radon.getGas(2500))
                .itemOutputs(ItemList.Circuit_Wetwaremainframe.get(1))
                .eut(300000)
                .duration(20 * 100)
                .addTo(MASL);
            TST_RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(10),
                    GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Tritanium, 2),
                    ItemList.Circuit_Wetwaresupercomputer.get(2),
                    ItemList.Circuit_Parts_InductorASMD.get(16),
                    ItemList.Circuit_Parts_CapacitorASMD.get(16),
                    ItemList.Circuit_Parts_ResistorASMD.get(16),
                    ItemList.Circuit_Parts_TransistorASMD.get(16),
                    ItemList.Circuit_Parts_DiodeASMD.get(16),
                    ItemList.Circuit_Chip_Ram.get(48),
                    GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorUHV, 16),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.StyreneButadieneRubber, 64))
                .fluidInputs(
                    MaterialsAlloy.INDALLOY_140.getFluidStack(144 * 20),
                    new FluidStack(ic2coolant, 10000),
                    Materials.Radon.getGas(2500))
                .itemOutputs(ItemList.Circuit_Wetwaremainframe.get(1))
                .eut(300000)
                .duration(20 * 100)
                .addTo(MASL);
            TST_RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(10),
                    GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Tritanium, 2),
                    ItemList.Circuit_Wetwaresupercomputer.get(2),
                    ItemList.Circuit_Parts_InductorXSMD.get(4),
                    ItemList.Circuit_Parts_CapacitorXSMD.get(4),
                    ItemList.Circuit_Parts_ResistorXSMD.get(4),
                    ItemList.Circuit_Parts_TransistorXSMD.get(4),
                    ItemList.Circuit_Parts_DiodeXSMD.get(4),
                    ItemList.Circuit_Chip_Ram.get(48),
                    GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorUHV, 16),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.RubberSilicone, 64))
                .fluidInputs(
                    MaterialsAlloy.INDALLOY_140.getFluidStack(144 * 20),
                    new FluidStack(ic2coolant, 10000),
                    Materials.Radon.getGas(2500))
                .itemOutputs(ItemList.Circuit_Wetwaremainframe.get(1))
                .eut(300000)
                .duration(20 * 100)
                .addTo(MASL);
            TST_RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(10),
                    GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Tritanium, 2),
                    ItemList.Circuit_Wetwaresupercomputer.get(2),
                    ItemList.Circuit_Parts_InductorXSMD.get(4),
                    ItemList.Circuit_Parts_CapacitorXSMD.get(4),
                    ItemList.Circuit_Parts_ResistorXSMD.get(4),
                    ItemList.Circuit_Parts_TransistorXSMD.get(4),
                    ItemList.Circuit_Parts_DiodeXSMD.get(4),
                    ItemList.Circuit_Chip_Ram.get(48),
                    GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorUHV, 16),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.StyreneButadieneRubber, 64))
                .fluidInputs(
                    MaterialsAlloy.INDALLOY_140.getFluidStack(144 * 20),
                    new FluidStack(ic2coolant, 10000),
                    Materials.Radon.getGas(2500))
                .itemOutputs(ItemList.Circuit_Wetwaremainframe.get(1))
                .eut(300000)
                .duration(20 * 100)
                .addTo(MASL);
            TST_RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(10),
                    GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Tritanium, 2),
                    ItemList.Circuit_Wetwaresupercomputer.get(2),
                    ItemList.Circuit_Parts_InductorASMD.get(16),
                    ItemList.Circuit_Parts_CapacitorASMD.get(16),
                    ItemList.Circuit_Parts_ResistorASMD.get(16),
                    ItemList.Circuit_Parts_TransistorASMD.get(16),
                    ItemList.Circuit_Parts_DiodeASMD.get(16),
                    ItemList.Circuit_Chip_Ram.get(48),
                    GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorUEV, 8),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.RubberSilicone, 64))
                .fluidInputs(
                    MaterialsAlloy.INDALLOY_140.getFluidStack(144 * 20),
                    new FluidStack(ic2coolant, 10000),
                    Materials.Radon.getGas(2500))
                .itemOutputs(ItemList.Circuit_Wetwaremainframe.get(1))
                .eut(300000)
                .duration(20 * 100)
                .addTo(MASL);
            TST_RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(10),
                    GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Tritanium, 2),
                    ItemList.Circuit_Wetwaresupercomputer.get(2),
                    ItemList.Circuit_Parts_InductorASMD.get(16),
                    ItemList.Circuit_Parts_CapacitorASMD.get(16),
                    ItemList.Circuit_Parts_ResistorASMD.get(16),
                    ItemList.Circuit_Parts_TransistorASMD.get(16),
                    ItemList.Circuit_Parts_DiodeASMD.get(16),
                    ItemList.Circuit_Chip_Ram.get(48),
                    GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorUEV, 8),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.StyreneButadieneRubber, 64))
                .fluidInputs(
                    MaterialsAlloy.INDALLOY_140.getFluidStack(144 * 20),
                    new FluidStack(ic2coolant, 10000),
                    Materials.Radon.getGas(2500))
                .itemOutputs(ItemList.Circuit_Wetwaremainframe.get(1))
                .eut(300000)
                .duration(20 * 100)
                .addTo(MASL);
            TST_RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(10),
                    GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Tritanium, 2),
                    ItemList.Circuit_Wetwaresupercomputer.get(2),
                    ItemList.Circuit_Parts_InductorXSMD.get(4),
                    ItemList.Circuit_Parts_CapacitorXSMD.get(4),
                    ItemList.Circuit_Parts_ResistorXSMD.get(4),
                    ItemList.Circuit_Parts_TransistorXSMD.get(4),
                    ItemList.Circuit_Parts_DiodeXSMD.get(4),
                    ItemList.Circuit_Chip_Ram.get(48),
                    GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorUEV, 8),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.RubberSilicone, 64))
                .fluidInputs(
                    MaterialsAlloy.INDALLOY_140.getFluidStack(144 * 20),
                    new FluidStack(ic2coolant, 10000),
                    Materials.Radon.getGas(2500))
                .itemOutputs(ItemList.Circuit_Wetwaremainframe.get(1))
                .eut(300000)
                .duration(20 * 100)
                .addTo(MASL);
            TST_RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(10),
                    GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Tritanium, 2),
                    ItemList.Circuit_Wetwaresupercomputer.get(2),
                    ItemList.Circuit_Parts_InductorXSMD.get(4),
                    ItemList.Circuit_Parts_CapacitorXSMD.get(4),
                    ItemList.Circuit_Parts_ResistorXSMD.get(4),
                    ItemList.Circuit_Parts_TransistorXSMD.get(4),
                    ItemList.Circuit_Parts_DiodeXSMD.get(4),
                    ItemList.Circuit_Chip_Ram.get(48),
                    GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorUEV, 8),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.StyreneButadieneRubber, 64))
                .fluidInputs(
                    MaterialsAlloy.INDALLOY_140.getFluidStack(144 * 20),
                    new FluidStack(ic2coolant, 10000),
                    Materials.Radon.getGas(2500))
                .itemOutputs(ItemList.Circuit_Wetwaremainframe.get(1))
                .eut(300000)
                .duration(20 * 100)
                .addTo(MASL);

            TST_RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(10),
                    ItemList.Circuit_Board_Bio_Ultra.get(2),
                    ItemList.Circuit_Biowarecomputer.get(2),
                    ItemList.Circuit_Parts_TransistorASMD.get(16),
                    ItemList.Circuit_Parts_ResistorASMD.get(16),
                    ItemList.Circuit_Parts_CapacitorASMD.get(16),
                    ItemList.Circuit_Parts_DiodeASMD.get(16),
                    ItemList.Circuit_Chip_NOR.get(32),
                    ItemList.Circuit_Chip_Ram.get(64),
                    GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.NiobiumTitanium, 32),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.RubberSilicone, 64))
                .fluidInputs(
                    MUTATED_LIVING_SOLDER.getFluidStack(144 * 10),
                    Materials.BioMediumSterilized.getFluid(144 * 10),
                    Materials.SuperCoolant.getFluid(10000))
                .itemOutputs(ItemList.Circuit_Biowaresupercomputer.get(1))
                .eut(491520)
                .duration(20 * 200)
                .addTo(MASL);
            TST_RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(10),
                    ItemList.Circuit_Board_Bio_Ultra.get(2),
                    ItemList.Circuit_Biowarecomputer.get(2),
                    ItemList.Circuit_Parts_TransistorASMD.get(16),
                    ItemList.Circuit_Parts_ResistorASMD.get(16),
                    ItemList.Circuit_Parts_CapacitorASMD.get(16),
                    ItemList.Circuit_Parts_DiodeASMD.get(16),
                    ItemList.Circuit_Chip_NOR.get(32),
                    ItemList.Circuit_Chip_Ram.get(64),
                    GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.NiobiumTitanium, 32),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.StyreneButadieneRubber, 64))
                .fluidInputs(
                    MUTATED_LIVING_SOLDER.getFluidStack(144 * 10),
                    Materials.BioMediumSterilized.getFluid(144 * 10),
                    Materials.SuperCoolant.getFluid(10000))
                .itemOutputs(ItemList.Circuit_Biowaresupercomputer.get(1))
                .eut(491520)
                .duration(20 * 200)
                .addTo(MASL);
            TST_RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(10),
                    ItemList.Circuit_Board_Bio_Ultra.get(2),
                    ItemList.Circuit_Biowarecomputer.get(2),
                    ItemList.Circuit_Parts_TransistorXSMD.get(4),
                    ItemList.Circuit_Parts_ResistorXSMD.get(4),
                    ItemList.Circuit_Parts_CapacitorXSMD.get(4),
                    ItemList.Circuit_Parts_DiodeXSMD.get(4),
                    ItemList.Circuit_Chip_NOR.get(32),
                    ItemList.Circuit_Chip_Ram.get(64),
                    GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.NiobiumTitanium, 32),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.RubberSilicone, 64))
                .fluidInputs(
                    MUTATED_LIVING_SOLDER.getFluidStack(144 * 10),
                    Materials.BioMediumSterilized.getFluid(144 * 10),
                    Materials.SuperCoolant.getFluid(10000))
                .itemOutputs(ItemList.Circuit_Biowaresupercomputer.get(1))
                .eut(491520)
                .duration(20 * 200)
                .addTo(MASL);
            TST_RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(10),
                    ItemList.Circuit_Board_Bio_Ultra.get(2),
                    ItemList.Circuit_Biowarecomputer.get(2),
                    ItemList.Circuit_Parts_TransistorXSMD.get(4),
                    ItemList.Circuit_Parts_ResistorXSMD.get(4),
                    ItemList.Circuit_Parts_CapacitorXSMD.get(4),
                    ItemList.Circuit_Parts_DiodeXSMD.get(4),
                    ItemList.Circuit_Chip_NOR.get(32),
                    ItemList.Circuit_Chip_Ram.get(64),
                    GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.NiobiumTitanium, 32),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.StyreneButadieneRubber, 64))
                .fluidInputs(
                    MUTATED_LIVING_SOLDER.getFluidStack(144 * 10),
                    Materials.BioMediumSterilized.getFluid(144 * 10),
                    Materials.SuperCoolant.getFluid(10000))
                .itemOutputs(ItemList.Circuit_Biowaresupercomputer.get(1))
                .eut(491520)
                .duration(20 * 200)
                .addTo(MASL);

            TST_RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(10),
                    GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Tritanium, 4),
                    ItemList.Circuit_Biowaresupercomputer.get(2),
                    ItemList.Circuit_Parts_InductorASMD.get(24),
                    ItemList.Circuit_Parts_TransistorASMD.get(24),
                    ItemList.Circuit_Parts_ResistorASMD.get(24),
                    ItemList.Circuit_Parts_CapacitorASMD.get(24),
                    ItemList.Circuit_Parts_DiodeASMD.get(24),
                    ItemList.Circuit_Chip_Ram.get(64),
                    GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorUHV, 64),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.RubberSilicone, 64),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.Polybenzimidazole, 64))
                .fluidInputs(
                    MUTATED_LIVING_SOLDER.getFluidStack(144 * 20),
                    Materials.BioMediumSterilized.getFluid(144 * 20),
                    Materials.SuperCoolant.getFluid(20000))
                .itemOutputs(ItemList.Circuit_Biomainframe.get(1))
                .eut(1966080)
                .duration(20 * 300)
                .addTo(MASL);
            TST_RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(10),
                    GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Tritanium, 4),
                    ItemList.Circuit_Biowaresupercomputer.get(2),
                    ItemList.Circuit_Parts_InductorASMD.get(24),
                    ItemList.Circuit_Parts_TransistorASMD.get(24),
                    ItemList.Circuit_Parts_ResistorASMD.get(24),
                    ItemList.Circuit_Parts_CapacitorASMD.get(24),
                    ItemList.Circuit_Parts_DiodeASMD.get(24),
                    ItemList.Circuit_Chip_Ram.get(64),
                    GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorUHV, 64),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.StyreneButadieneRubber, 64),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.Polybenzimidazole, 64))
                .fluidInputs(
                    MUTATED_LIVING_SOLDER.getFluidStack(144 * 20),
                    Materials.BioMediumSterilized.getFluid(144 * 20),
                    Materials.SuperCoolant.getFluid(20000))
                .itemOutputs(ItemList.Circuit_Biomainframe.get(1))
                .eut(1966080)
                .duration(20 * 300)
                .addTo(MASL);
            TST_RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(10),
                    GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Tritanium, 4),
                    ItemList.Circuit_Biowaresupercomputer.get(2),
                    ItemList.Circuit_Parts_InductorXSMD.get(6),
                    ItemList.Circuit_Parts_TransistorXSMD.get(6),
                    ItemList.Circuit_Parts_ResistorXSMD.get(6),
                    ItemList.Circuit_Parts_CapacitorXSMD.get(6),
                    ItemList.Circuit_Parts_DiodeXSMD.get(6),
                    ItemList.Circuit_Chip_Ram.get(64),
                    GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorUHV, 64),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.RubberSilicone, 64),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.Polybenzimidazole, 64))
                .fluidInputs(
                    MUTATED_LIVING_SOLDER.getFluidStack(144 * 20),
                    Materials.BioMediumSterilized.getFluid(144 * 20),
                    Materials.SuperCoolant.getFluid(20000))
                .itemOutputs(ItemList.Circuit_Biomainframe.get(1))
                .eut(1966080)
                .duration(20 * 300)
                .addTo(MASL);
            TST_RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(10),
                    GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Tritanium, 4),
                    ItemList.Circuit_Biowaresupercomputer.get(2),
                    ItemList.Circuit_Parts_InductorXSMD.get(6),
                    ItemList.Circuit_Parts_TransistorXSMD.get(6),
                    ItemList.Circuit_Parts_ResistorXSMD.get(6),
                    ItemList.Circuit_Parts_CapacitorXSMD.get(6),
                    ItemList.Circuit_Parts_DiodeXSMD.get(6),
                    ItemList.Circuit_Chip_Ram.get(64),
                    GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorUHV, 64),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.StyreneButadieneRubber, 64),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.Polybenzimidazole, 64))
                .fluidInputs(
                    MUTATED_LIVING_SOLDER.getFluidStack(144 * 20),
                    Materials.BioMediumSterilized.getFluid(144 * 20),
                    Materials.SuperCoolant.getFluid(20000))
                .itemOutputs(ItemList.Circuit_Biomainframe.get(1))
                .eut(1966080)
                .duration(20 * 300)
                .addTo(MASL);
            TST_RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(10),
                    GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Tritanium, 4),
                    ItemList.Circuit_Biowaresupercomputer.get(2),
                    ItemList.Circuit_Parts_InductorASMD.get(24),
                    ItemList.Circuit_Parts_TransistorASMD.get(24),
                    ItemList.Circuit_Parts_ResistorASMD.get(24),
                    ItemList.Circuit_Parts_CapacitorASMD.get(24),
                    ItemList.Circuit_Parts_DiodeASMD.get(24),
                    ItemList.Circuit_Chip_Ram.get(64),
                    GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorUEV, 32),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.RubberSilicone, 64),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.Polybenzimidazole, 64))
                .fluidInputs(
                    MUTATED_LIVING_SOLDER.getFluidStack(144 * 20),
                    Materials.BioMediumSterilized.getFluid(144 * 20),
                    Materials.SuperCoolant.getFluid(20000))
                .itemOutputs(ItemList.Circuit_Biomainframe.get(1))
                .eut(1966080)
                .duration(20 * 300)
                .addTo(MASL);
            TST_RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(10),
                    GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Tritanium, 4),
                    ItemList.Circuit_Biowaresupercomputer.get(2),
                    ItemList.Circuit_Parts_InductorASMD.get(24),
                    ItemList.Circuit_Parts_TransistorASMD.get(24),
                    ItemList.Circuit_Parts_ResistorASMD.get(24),
                    ItemList.Circuit_Parts_CapacitorASMD.get(24),
                    ItemList.Circuit_Parts_DiodeASMD.get(24),
                    ItemList.Circuit_Chip_Ram.get(64),
                    GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorUEV, 32),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.StyreneButadieneRubber, 64),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.Polybenzimidazole, 64))
                .fluidInputs(
                    MUTATED_LIVING_SOLDER.getFluidStack(144 * 20),
                    Materials.BioMediumSterilized.getFluid(144 * 20),
                    Materials.SuperCoolant.getFluid(20000))
                .itemOutputs(ItemList.Circuit_Biomainframe.get(1))
                .eut(1966080)
                .duration(20 * 300)
                .addTo(MASL);
            TST_RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(10),
                    GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Tritanium, 4),
                    ItemList.Circuit_Biowaresupercomputer.get(2),
                    ItemList.Circuit_Parts_InductorXSMD.get(6),
                    ItemList.Circuit_Parts_TransistorXSMD.get(6),
                    ItemList.Circuit_Parts_ResistorXSMD.get(6),
                    ItemList.Circuit_Parts_CapacitorXSMD.get(6),
                    ItemList.Circuit_Parts_DiodeXSMD.get(6),
                    ItemList.Circuit_Chip_Ram.get(64),
                    GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorUEV, 32),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.RubberSilicone, 64),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.Polybenzimidazole, 64))
                .fluidInputs(
                    MUTATED_LIVING_SOLDER.getFluidStack(144 * 20),
                    Materials.BioMediumSterilized.getFluid(144 * 20),
                    Materials.SuperCoolant.getFluid(20000))
                .itemOutputs(ItemList.Circuit_Biomainframe.get(1))
                .eut(1966080)
                .duration(20 * 300)
                .addTo(MASL);
            TST_RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(10),
                    GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Tritanium, 4),
                    ItemList.Circuit_Biowaresupercomputer.get(2),
                    ItemList.Circuit_Parts_InductorXSMD.get(6),
                    ItemList.Circuit_Parts_TransistorXSMD.get(6),
                    ItemList.Circuit_Parts_ResistorXSMD.get(6),
                    ItemList.Circuit_Parts_CapacitorXSMD.get(6),
                    ItemList.Circuit_Parts_DiodeXSMD.get(6),
                    ItemList.Circuit_Chip_Ram.get(64),
                    GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorUEV, 32),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.StyreneButadieneRubber, 64),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.Polybenzimidazole, 64))
                .fluidInputs(
                    MUTATED_LIVING_SOLDER.getFluidStack(144 * 20),
                    Materials.BioMediumSterilized.getFluid(144 * 20),
                    Materials.SuperCoolant.getFluid(20000))
                .itemOutputs(ItemList.Circuit_Biomainframe.get(1))
                .eut(1966080)
                .duration(20 * 300)
                .addTo(MASL);
            TST_RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(10),
                    GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Tritanium, 4),
                    ItemList.Circuit_Biowaresupercomputer.get(2),
                    ItemList.Circuit_Parts_InductorASMD.get(24),
                    ItemList.Circuit_Parts_TransistorASMD.get(24),
                    ItemList.Circuit_Parts_ResistorASMD.get(24),
                    ItemList.Circuit_Parts_CapacitorASMD.get(24),
                    ItemList.Circuit_Parts_DiodeASMD.get(24),
                    ItemList.Circuit_Chip_Ram.get(64),
                    GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorUIV, 16),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.RubberSilicone, 64),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.Polybenzimidazole, 64))
                .fluidInputs(
                    MUTATED_LIVING_SOLDER.getFluidStack(144 * 20),
                    Materials.BioMediumSterilized.getFluid(144 * 20),
                    Materials.SuperCoolant.getFluid(20000))
                .itemOutputs(ItemList.Circuit_Biomainframe.get(1))
                .eut(1966080)
                .duration(20 * 300)
                .addTo(MASL);
            TST_RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(10),
                    GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Tritanium, 4),
                    ItemList.Circuit_Biowaresupercomputer.get(2),
                    ItemList.Circuit_Parts_InductorASMD.get(24),
                    ItemList.Circuit_Parts_TransistorASMD.get(24),
                    ItemList.Circuit_Parts_ResistorASMD.get(24),
                    ItemList.Circuit_Parts_CapacitorASMD.get(24),
                    ItemList.Circuit_Parts_DiodeASMD.get(24),
                    ItemList.Circuit_Chip_Ram.get(64),
                    GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorUIV, 16),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.StyreneButadieneRubber, 64),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.Polybenzimidazole, 64))
                .fluidInputs(
                    MUTATED_LIVING_SOLDER.getFluidStack(144 * 20),
                    Materials.BioMediumSterilized.getFluid(144 * 20),
                    Materials.SuperCoolant.getFluid(20000))
                .itemOutputs(ItemList.Circuit_Biomainframe.get(1))
                .eut(1966080)
                .duration(20 * 300)
                .addTo(MASL);
            TST_RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(10),
                    GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Tritanium, 4),
                    ItemList.Circuit_Biowaresupercomputer.get(2),
                    ItemList.Circuit_Parts_InductorXSMD.get(6),
                    ItemList.Circuit_Parts_TransistorXSMD.get(6),
                    ItemList.Circuit_Parts_ResistorXSMD.get(6),
                    ItemList.Circuit_Parts_CapacitorXSMD.get(6),
                    ItemList.Circuit_Parts_DiodeXSMD.get(6),
                    ItemList.Circuit_Chip_Ram.get(64),
                    GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorUIV, 16),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.RubberSilicone, 64),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.Polybenzimidazole, 64))
                .fluidInputs(
                    MUTATED_LIVING_SOLDER.getFluidStack(144 * 20),
                    Materials.BioMediumSterilized.getFluid(144 * 20),
                    Materials.SuperCoolant.getFluid(20000))
                .itemOutputs(ItemList.Circuit_Biomainframe.get(1))
                .eut(1966080)
                .duration(20 * 300)
                .addTo(MASL);
            TST_RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(10),
                    GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Tritanium, 4),
                    ItemList.Circuit_Biowaresupercomputer.get(2),
                    ItemList.Circuit_Parts_InductorXSMD.get(6),
                    ItemList.Circuit_Parts_TransistorXSMD.get(6),
                    ItemList.Circuit_Parts_ResistorXSMD.get(6),
                    ItemList.Circuit_Parts_CapacitorXSMD.get(6),
                    ItemList.Circuit_Parts_DiodeXSMD.get(6),
                    ItemList.Circuit_Chip_Ram.get(64),
                    GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorUIV, 16),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.StyreneButadieneRubber, 64),
                    GTOreDictUnificator.get(OrePrefixes.foil, Materials.Polybenzimidazole, 64))
                .fluidInputs(
                    MUTATED_LIVING_SOLDER.getFluidStack(144 * 20),
                    Materials.BioMediumSterilized.getFluid(144 * 20),
                    Materials.SuperCoolant.getFluid(20000))
                .itemOutputs(ItemList.Circuit_Biomainframe.get(1))
                .eut(1966080)
                .duration(20 * 300)
                .addTo(MASL);

        }

        {
            TST_RecipeBuilder.builder()
                .itemInputs(
                    GTCMItemList.SolarSail.get(16),
                    GTCMItemList.StellarConstructionFrameMaterial.get(4),
                    GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UIV, 24),
                    ItemList.Circuit_Wafer_QPIC.get(32),
                    ItemList.Emitter_UMV.get(4),
                    ItemList.Sensor_UMV.get(4))
                .fluidInputs(new FluidStack(solderUEV, 144 * 256))
                .itemOutputs(setStackSize(DysonSwarmModule.get(1), 16384))
                .eut(100000000)
                .duration(20 * 50)
                .addTo(MASL);
        }

        {
            TST_RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(24),
                    Materials.Neutronium.getNanite(1),
                    GTUtility.copyAmountUnsafe(4096, Materials.Lanthanum.getPlates(1)),
                    GTUtility.copyAmountUnsafe(6144, Materials.NaquadahAlloy.getPlates(1)),
                    ItemList.StableBosonContainmentUnit.get(1))
                .fluidInputs(
                    new FluidStack(solderUEV, 144 * 1024),
                    Materials.Lead.getMolten(144 * 16 * 1024),
                    Materials.SpaceTime.getMolten(144 * 8),
                    Materials.UUMatter.getFluid(1000 * 16))
                .itemOutputs(GTUtility.copyAmountUnsafe(2048, ItemRefer.Advanced_Radiation_Protection_Plate.get(1)))
                .eut(RECIPE_UXV)
                .duration(20 * 10)
                .addTo(MASL);
            TST_RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(10),
                    ItemRefer.Radiation_Protection_Plate.get(2),
                    Materials.ElectrumFlux.getPlates(4),
                    Materials.Trinium.getPlates(4),
                    Materials.NaquadahAlloy.getPlates(4),
                    Materials.Osmiridium.getPlates(4),
                    Materials.VibrantAlloy.getPlates(4),
                    ItemList.Radiation_Proof_Prismatic_Naquadah_Composite_Sheet.get(4))
                .fluidInputs(MaterialsAlloy.INDALLOY_140.getFluidStack(8 * INGOTS))
                .itemOutputs(ItemRefer.Advanced_Radiation_Protection_Plate.get(1))
                .eut(RECIPE_ZPM / 2)
                .duration(50 * SECONDS)
                .addTo(MASL);
        }

        {

            TST_RecipeBuilder.builder()
                .itemInputs(
                    GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Neutronium, 64),
                    GTUtility.copyAmountUnsafe(6144, ItemList.Circuit_Parts_Crystal_Chip_Master.get(1)),
                    GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UEV, 16),
                    ItemList.Field_Generator_UEV.get(8),

                    ItemList.Circuit_Wafer_PPIC.get(64),
                    NHItemList.HighEnergyFlowCircuit.get(64),
                    GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorUEV, 64))
                .fluidInputs(
                    new FluidStack(solderUEV, 144 * 64),
                    MaterialsElements.STANDALONE.HYPOGEN.getFluidStack(144 * 64),
                    Materials.UUMatter.getFluid(1000 * 64))
                .itemOutputs(ItemList.ZPM3.get(1))
                .eut(32_000_000)
                .duration(20 * 128)
                .addTo(MASL);

            TST_RecipeBuilder.builder()
                .itemInputs(
                    GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Infinity, 64),
                    GTUtility.copyAmountUnsafe(48912, ItemList.Circuit_Parts_Crystal_Chip_Master.get(1)),
                    GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UIV, 16),
                    ItemList.Field_Generator_UIV.get(8),

                    ItemList.Circuit_Wafer_QPIC.get(64),
                    NHItemList.HighEnergyFlowCircuit.get(64),
                    GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorUIV, 64))
                .fluidInputs(
                    new FluidStack(solderUEV, 144 * 128),
                    Materials.SpaceTime.getMolten(144 * 128),
                    Materials.UUMatter.getFluid(1000 * 128))
                .itemOutputs(ItemList.ZPM4.get(1))
                .eut(128_000_000)
                .duration(20 * 256)
                .addTo(MASL);

            TST_RecipeBuilder.builder()
                .itemInputs(
                    MaterialsElements.STANDALONE.HYPOGEN.getPlateDense(64),
                    GTUtility.copyAmountUnsafe(391296, ItemList.Circuit_Parts_Crystal_Chip_Master.get(1)),
                    GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UMV, 16),
                    ItemList.Field_Generator_UMV.get(8),

                    GTUtility.copyAmountUnsafe(2048, ItemList.Circuit_Wafer_QPIC.get(1)),
                    NHItemList.HighEnergyFlowCircuit.get(64),
                    GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorUMV, 64))
                .fluidInputs(
                    new FluidStack(solderUEV, 144 * 256),
                    Materials.Eternity.getMolten(144 * 256),
                    Materials.UUMatter.getFluid(1000 * 256))
                .itemOutputs(ItemList.ZPM5.get(1))
                .eut(512_000_000)
                .duration(20 * 512)
                .addTo(MASL);

            TST_RecipeBuilder.builder()
                .itemInputs(
                    MaterialsElements.STANDALONE.DRAGON_METAL.getBlock(64),
                    GTUtility.copyAmountUnsafe(3130368, ItemList.Circuit_Parts_Crystal_Chip_Master.get(1)),
                    GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UXV, 16),
                    ItemList.Field_Generator_UXV.get(2),

                    ItemList.EnergisedTesseract.get(64),
                    NHItemList.HighEnergyFlowCircuit.get(64),
                    GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUMV, 64))
                .fluidInputs(
                    new FluidStack(solderUEV, 144 * 512),
                    Materials.MHDCSM.getMolten(144 * 128),
                    Materials.UUMatter.getFluid(1000 * 512))
                .itemOutputs(ItemList.ZPM6.get(1))
                .eut(2_048_000_000)
                .duration(20 * 1024)
                .addTo(MASL);

        }

        {

            GTValues.RA.stdBuilder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(2),
                    GTOreDictUnificator.get(OrePrefixes.plate, Materials.Osmiridium, 4),
                    GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Ledox, 1),
                    GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.CallistoIce, 1),
                    ItemList.Reactor_Coolant_Sp_6.get(1L),
                    ModItem.getModItem(GTPlusPlus.ID, "itemScrewLaurenium", 12, 0),
                    new Object[] { OrePrefixes.circuit.get(Materials.IV), 2L },
                    GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorUHV, 2),
                    ItemList.Super_Chest_IV.get(1),
                    ItemList.Super_Tank_IV.get(1),
                    NHItemList.PicoWafer.get(1))
                .fluidInputs(
                    WerkstoffLoader.Oganesson.getFluidOrGas(1000),
                    new FluidStack(solderUEV, 576),
                    Materials.NaquadahEnriched.getMolten(288L))
                .itemOutputs(ItemList.Casing_Dim_Injector.get(1))
                .eut(32_000_000)
                .duration(20 * 20)
                .addTo(MASL);

            GTValues.RA.stdBuilder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(1),
                    GTOreDictUnificator.get(OrePrefixes.plate, Materials.Osmiridium, 6),
                    ModItem.getModItem(GTPlusPlus.ID, "itemScrewLaurenium", 12, 0),
                    ItemList.Reactor_Coolant_Sp_6.get(1L),
                    GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorUHV, 1))
                .fluidInputs(
                    WerkstoffLoader.Oganesson.getFluidOrGas(500),
                    new FluidStack(solderUEV, 288),
                    Materials.NaquadahEnriched.getMolten(144L))
                .itemOutputs(ItemList.Casing_Dim_Trans.get(1))
                .eut(32_000_000)
                .duration(20 * 20)
                .addTo(MASL);
        }

        {

            GTValues.RA.stdBuilder()
                .itemInputs(
                    ItemList.Hull_LuV.get(1),
                    GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorLuV, 2),
                    ItemList.Circuit_Chip_UHPIC.get(2),
                    new Object[] { OrePrefixes.circuit.get(Materials.LuV), 2 },
                    ItemList.LuV_Coil.get(2),
                    ItemList.Electric_Pump_LuV.get(1))
                .itemOutputs(ItemList.Hatch_Energy_LuV.get(1))
                .fluidInputs(new FluidStack(ic2coolant, 2000), new FluidStack(solderIndalloy, 720))
                .duration(20 * 20)
                .eut(RECIPE_LuV)
                .addTo(MASL);
            GTValues.RA.stdBuilder()
                .itemInputs(
                    ItemList.Hull_ZPM.get(1),
                    GTOreDictUnificator.get(OrePrefixes.wireGt02, Materials.SuperconductorZPM, 2),
                    ItemList.Circuit_Chip_NPIC.get(2),
                    new Object[] { OrePrefixes.circuit.get(Materials.ZPM), 2 },
                    ItemList.ZPM_Coil.get(2),
                    ItemList.Electric_Pump_ZPM.get(1))
                .itemOutputs(ItemList.Hatch_Energy_ZPM.get(1))
                .fluidInputs(new FluidStack(ic2coolant, 4000), new FluidStack(solderIndalloy, 1440))
                .duration(30 * 20)
                .eut(RECIPE_ZPM)
                .addTo(MASL);
            GTValues.RA.stdBuilder()
                .itemInputs(
                    ItemList.Hull_UV.get(1),
                    GTOreDictUnificator.get(OrePrefixes.wireGt02, Materials.SuperconductorUV, 2),
                    ItemList.Circuit_Chip_PPIC.get(2),
                    new Object[] { OrePrefixes.circuit.get(Materials.SuperconductorUHV), 2 },
                    ItemList.UV_Coil.get(2),
                    ItemList.Electric_Pump_UV.get(1))
                .itemOutputs(ItemList.Hatch_Energy_UV.get(1))
                .fluidInputs(new FluidStack(ic2coolant, 8000), new FluidStack(solderIndalloy, 2880))
                .duration(40 * 20)
                .eut(RECIPE_UV)
                .addTo(MASL);
            GTValues.RA.stdBuilder()
                .itemInputs(
                    ItemList.Hull_MAX.get(1L),
                    GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.SuperconductorUHV, 2L),
                    ItemList.Circuit_Chip_QPIC.get(2L),
                    new Object[] { OrePrefixes.circuit.get(Materials.UHV), 2L },
                    ItemList.UHV_Coil.get(2L),
                    ItemList.Electric_Pump_UHV.get(1L))
                .itemOutputs(ItemList.Hatch_Energy_UHV.get(1))
                .fluidInputs(new FluidStack(ic2coolant, 16000), new FluidStack(solderIndalloy, 40 * 144))
                .duration(50 * 20)
                .eut(RECIPE_UHV)
                .addTo(MASL);

            GTValues.RA.stdBuilder()
                .itemInputs(
                    ItemList.Hull_LuV.get(1),
                    GTOreDictUnificator.get(OrePrefixes.spring, Materials.SuperconductorLuVBase, 2),
                    ItemList.Circuit_Chip_UHPIC.get(2),
                    new Object[] { OrePrefixes.circuit.get(Materials.LuV), 2 },
                    ItemList.LuV_Coil.get(2),
                    ItemList.Electric_Pump_LuV.get(1))
                .itemOutputs(ItemList.Hatch_Dynamo_LuV.get(1))
                .fluidInputs(new FluidStack(ic2coolant, 2000), new FluidStack(solderIndalloy, 720))
                .duration(20 * 20)
                .eut(RECIPE_LuV)
                .addTo(MASL);
            GTValues.RA.stdBuilder()
                .itemInputs(
                    ItemList.Hull_ZPM.get(1),
                    GTOreDictUnificator.get(OrePrefixes.spring, Materials.SuperconductorZPMBase, 4),
                    ItemList.Circuit_Chip_NPIC.get(2),
                    new Object[] { OrePrefixes.circuit.get(Materials.ZPM), 2 },
                    ItemList.ZPM_Coil.get(2),
                    ItemList.Electric_Pump_ZPM.get(1))
                .itemOutputs(ItemList.Hatch_Dynamo_ZPM.get(1))
                .fluidInputs(new FluidStack(ic2coolant, 4000), new FluidStack(solderIndalloy, 1440))
                .duration(30 * 20)
                .eut(RECIPE_ZPM)
                .addTo(MASL);
            GTValues.RA.stdBuilder()
                .itemInputs(
                    ItemList.Hull_UV.get(1),
                    GTOreDictUnificator.get(OrePrefixes.spring, Materials.SuperconductorUVBase, 4),
                    ItemList.Circuit_Chip_PPIC.get(2),
                    new Object[] { OrePrefixes.circuit.get(Materials.SuperconductorUHV), 2 },
                    ItemList.UV_Coil.get(2),
                    ItemList.Electric_Pump_UV.get(1))
                .itemOutputs(ItemList.Hatch_Dynamo_UV.get(1))
                .fluidInputs(new FluidStack(ic2coolant, 8000), new FluidStack(solderIndalloy, 2880))
                .duration(40 * 20)
                .eut(RECIPE_UV)
                .addTo(MASL);
            GTValues.RA.stdBuilder()
                .itemInputs(
                    ItemList.Hull_MAX.get(1L),
                    GTOreDictUnificator.get(OrePrefixes.spring, Materials.SuperconductorUHVBase, 8L),
                    ItemList.Circuit_Chip_QPIC.get(2L),
                    new Object[] { OrePrefixes.circuit.get(Materials.UHV), 2L },
                    ItemList.UHV_Coil.get(2L),
                    ItemList.Electric_Pump_UHV.get(1L))
                .itemOutputs(ItemList.Hatch_Dynamo_UHV.get(1))
                .fluidInputs(new FluidStack(ic2coolant, 16000), new FluidStack(solderIndalloy, 40 * 144))
                .duration(50 * 20)
                .eut(RECIPE_UHV)
                .addTo(MASL);

        }

        {

            GTValues.RA.stdBuilder()
                .itemInputsUnsafe(
                    GTUtility.getIntegratedCircuit(1),
                    ItemList.Hull_UXV.get(1),
                    GTUtility.copyAmountUnsafe(128, GTOreDictUnificator.get(OrePrefixes.lens, Materials.Diamond, 1)),
                    GTUtility.copyAmountUnsafe(128, ItemList.Sensor_UXV.get(1)),
                    GTUtility.copyAmountUnsafe(128, ItemList.Electric_Pump_UXV.get(1)),
                    GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.BlackPlutonium, 32))
                .itemOutputs(tectech.thing.CustomItemList.eM_energyTunnel8_UXV.get(1))
                .fluidInputs(new FluidStack(solderUEV, 1_296 * 64 * 4))
                .duration(106 * MINUTES + 40 * SECONDS)
                .eut(RECIPE_UXV)
                .addTo(MASL);

            GTValues.RA.stdBuilder()
                .itemInputsUnsafe(
                    GTUtility.getIntegratedCircuit(1),
                    ItemList.Hull_UXV.get(1),
                    GTUtility.copyAmountUnsafe(128, GTOreDictUnificator.get(OrePrefixes.lens, Materials.Diamond, 1)),
                    GTUtility.copyAmountUnsafe(128, ItemList.Emitter_UXV.get(1)),
                    GTUtility.copyAmountUnsafe(128, ItemList.Electric_Pump_UXV.get(1)),
                    GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.BlackPlutonium, 32))
                .itemOutputs(tectech.thing.CustomItemList.eM_dynamoTunnel8_UXV.get(1))
                .fluidInputs(new FluidStack(solderUEV, 1_296 * 64 * 4))
                .duration(106 * MINUTES + 40 * SECONDS)
                .eut(RECIPE_UXV)
                .addTo(MASL);

            GTValues.RA.stdBuilder()
                .itemInputsUnsafe(
                    GTUtility.getIntegratedCircuit(2),
                    ItemList.Hull_UXV.get(1),
                    GTUtility.copyAmountUnsafe(256, GTOreDictUnificator.get(OrePrefixes.lens, Materials.Diamond, 1)),
                    GTUtility.copyAmountUnsafe(256, ItemList.Sensor_UXV.get(1)),
                    GTUtility.copyAmountUnsafe(256, ItemList.Electric_Pump_UXV.get(1)),
                    GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.BlackPlutonium, 64))
                .itemOutputs(tectech.thing.CustomItemList.eM_energyTunnel9_UXV.get(1))
                .fluidInputs(new FluidStack(solderUEV, 1_296 * 128 * 4))
                .duration(213 * MINUTES + 20 * SECONDS)
                .eut(RECIPE_UXV)
                .addTo(MASL);

            GTValues.RA.stdBuilder()
                .itemInputsUnsafe(
                    GTUtility.getIntegratedCircuit(2),
                    ItemList.Hull_UXV.get(1),
                    GTUtility.copyAmountUnsafe(256, GTOreDictUnificator.get(OrePrefixes.lens, Materials.Diamond, 1)),
                    GTUtility.copyAmountUnsafe(256, ItemList.Emitter_UXV.get(1)),
                    GTUtility.copyAmountUnsafe(256, ItemList.Electric_Pump_UXV.get(1)),
                    GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.BlackPlutonium, 64))
                .itemOutputs(tectech.thing.CustomItemList.eM_dynamoTunnel9_UXV.get(1))
                .fluidInputs(new FluidStack(solderUEV, 1_296 * 128 * 4))
                .duration(213 * MINUTES + 20 * SECONDS)
                .eut(RECIPE_UXV)
                .addTo(MASL);

        }

        {
            final ItemStack ME_Singularity = ModItem
                .getModItem("appliedenergistics2", "item.ItemExtremeStorageCell.Singularity", 1);

            final ItemStack[] plateList = new ItemStack[] { GTOreDictUnificator.get("boltShirabon", 2),
                GTOreDictUnificator.get(OrePrefixes.bolt, Materials.WhiteDwarfMatter, 2),
                GTOreDictUnificator.get(OrePrefixes.bolt, Materials.WhiteDwarfMatter, 8),
                GTOreDictUnificator.get(OrePrefixes.bolt, Materials.WhiteDwarfMatter, 32),
                GTOreDictUnificator.get(OrePrefixes.bolt, Materials.BlackDwarfMatter, 2),
                GTOreDictUnificator.get(OrePrefixes.bolt, Materials.BlackDwarfMatter, 8),
                GTOreDictUnificator.get(OrePrefixes.bolt, Materials.BlackDwarfMatter, 32),
                GTOreDictUnificator.get(OrePrefixes.bolt, Materials.MHDCSM, 2),
                GTOreDictUnificator.get(OrePrefixes.bolt, Materials.MHDCSM, 8) };

            final ItemStack[] boltList = new ItemStack[] { GTOreDictUnificator.get("boltShirabon", 2),
                GTOreDictUnificator.get(OrePrefixes.bolt, Materials.WhiteDwarfMatter, 2),
                GTOreDictUnificator.get(OrePrefixes.bolt, Materials.WhiteDwarfMatter, 8),
                GTOreDictUnificator.get(OrePrefixes.bolt, Materials.WhiteDwarfMatter, 32),
                GTOreDictUnificator.get(OrePrefixes.bolt, Materials.BlackDwarfMatter, 2),
                GTOreDictUnificator.get(OrePrefixes.bolt, Materials.BlackDwarfMatter, 8),
                GTOreDictUnificator.get(OrePrefixes.bolt, Materials.BlackDwarfMatter, 32),
                GTOreDictUnificator.get(OrePrefixes.bolt, Materials.MHDCSM, 2),
                GTOreDictUnificator.get(OrePrefixes.bolt, Materials.MHDCSM, 8) };
            GTValues.RA.stdBuilder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(1),

                    EOH_Reinforced_Spatial_Casing.get(1),
                    ItemRefer.YOTTank_Cell_T7.get(1),
                    ItemList.Quantum_Tank_IV.get(4),
                    new ItemStack(BlockInfinityChest.instance, 1),

                    GregtechItemList.CosmicFabricManipulator.get(1),
                    GTUtility.copyAmountUnsafe(1, ME_Singularity),
                    GGMaterial.shirabon.get(OrePrefixes.bolt, 2),
                    GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UXV, 1))
                .fluidInputs(
                    MUTATED_LIVING_SOLDER.getFluidStack(144 * 20),
                    Materials.Space.getMolten(144 * 10),
                    Materials.SpaceTime.getMolten(144 * 10))
                .itemOutputs(SpacetimeCompressionFieldGeneratorTier0.get(1))
                .eut(com.Nxer.TwistSpaceTechnology.util.enums.TierEU.RECIPE_UMV)
                .duration(20 * 4000)
                .addTo(MASL);

            GTValues.RA.stdBuilder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(2),

                    EOH_Reinforced_Spatial_Casing.get(1),
                    ItemRefer.YOTTank_Cell_T7.get(2),
                    ItemList.Quantum_Tank_IV.get(4 * 2),
                    new ItemStack(BlockInfinityChest.instance, 2),

                    GregtechItemList.CosmicFabricManipulator.get(2),
                    GTUtility.copyAmountUnsafe(2, ME_Singularity),
                    GTOreDictUnificator.get(OrePrefixes.bolt, Materials.WhiteDwarfMatter, 2),
                    GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UXV, 1))
                .fluidInputs(
                    MUTATED_LIVING_SOLDER.getFluidStack(144 * 40),
                    Materials.Space.getMolten(144 * 20),
                    Materials.SpaceTime.getMolten(144 * 10))
                .itemOutputs(SpacetimeCompressionFieldGeneratorTier1.get(1))
                .eut(com.Nxer.TwistSpaceTechnology.util.enums.TierEU.RECIPE_UMV)
                .duration(20 * 4000 * 2)
                .addTo(MASL);

            GTValues.RA.stdBuilder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(3),

                    EOH_Reinforced_Spatial_Casing.get(1),
                    ItemRefer.YOTTank_Cell_T7.get(3),
                    ItemList.Quantum_Tank_IV.get(4 * 3),
                    new ItemStack(BlockInfinityChest.instance, 3),

                    GregtechItemList.CosmicFabricManipulator.get(3),
                    GTUtility.copyAmountUnsafe(3, ME_Singularity),
                    GTOreDictUnificator.get(OrePrefixes.bolt, Materials.WhiteDwarfMatter, 8),
                    GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UXV, 1))
                .fluidInputs(
                    MUTATED_LIVING_SOLDER.getFluidStack(144 * 80),
                    Materials.Space.getMolten(144 * 30),
                    Materials.SpaceTime.getMolten(144 * 10))
                .itemOutputs(SpacetimeCompressionFieldGeneratorTier2.get(1))
                .eut(com.Nxer.TwistSpaceTechnology.util.enums.TierEU.RECIPE_UMV)
                .duration(20 * 4000 * 3)
                .addTo(MASL);

            GTValues.RA.stdBuilder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(4),

                    EOH_Reinforced_Spatial_Casing.get(1),
                    ItemRefer.YOTTank_Cell_T8.get(1),
                    ItemList.Quantum_Tank_IV.get(4 * 4),
                    new ItemStack(BlockInfinityChest.instance, 4),

                    GregtechItemList.InfinityInfusedManipulator.get(1),
                    GTUtility.copyAmountUnsafe(4, ME_Singularity),
                    GTOreDictUnificator.get(OrePrefixes.bolt, Materials.WhiteDwarfMatter, 32),
                    GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UXV, 2))
                .fluidInputs(
                    MUTATED_LIVING_SOLDER.getFluidStack(144 * 160),
                    Materials.Space.getMolten(144 * 40),
                    Materials.SpaceTime.getMolten(144 * 10))
                .itemOutputs(SpacetimeCompressionFieldGeneratorTier3.get(1))
                .eut(com.Nxer.TwistSpaceTechnology.util.enums.TierEU.RECIPE_UMV)
                .duration(20 * 4000 * 4)
                .addTo(MASL);

            GTValues.RA.stdBuilder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(5),

                    EOH_Reinforced_Spatial_Casing.get(1),
                    ItemRefer.YOTTank_Cell_T8.get(2),
                    ItemList.Quantum_Tank_IV.get(4 * 5),
                    new ItemStack(BlockInfinityChest.instance, 5),

                    GregtechItemList.InfinityInfusedManipulator.get(2),
                    GTUtility.copyAmountUnsafe(5, ME_Singularity),
                    GTOreDictUnificator.get(OrePrefixes.bolt, Materials.BlackDwarfMatter, 2),
                    GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UXV, 2))
                .fluidInputs(
                    MUTATED_LIVING_SOLDER.getFluidStack(144 * 320),
                    Materials.Space.getMolten(144 * 50),
                    Materials.SpaceTime.getMolten(144 * 10))
                .itemOutputs(SpacetimeCompressionFieldGeneratorTier4.get(1))
                .eut(com.Nxer.TwistSpaceTechnology.util.enums.TierEU.RECIPE_UMV)
                .duration(20 * 4000 * 5)
                .addTo(MASL);

            GTValues.RA.stdBuilder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(6),

                    EOH_Reinforced_Spatial_Casing.get(1),
                    ItemRefer.YOTTank_Cell_T8.get(3),
                    ItemList.Quantum_Tank_IV.get(4 * 6),
                    new ItemStack(BlockInfinityChest.instance, 6),

                    GregtechItemList.InfinityInfusedManipulator.get(3),
                    GTUtility.copyAmountUnsafe(6, ME_Singularity),
                    GTOreDictUnificator.get(OrePrefixes.bolt, Materials.BlackDwarfMatter, 8),
                    GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UXV, 2))
                .fluidInputs(
                    MUTATED_LIVING_SOLDER.getFluidStack(144 * 640),
                    Materials.Space.getMolten(144 * 60),
                    Materials.SpaceTime.getMolten(144 * 10))
                .itemOutputs(SpacetimeCompressionFieldGeneratorTier5.get(1))
                .eut(com.Nxer.TwistSpaceTechnology.util.enums.TierEU.RECIPE_UMV)
                .duration(20 * 4000 * 6)
                .addTo(MASL);

            GTValues.RA.stdBuilder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(7),

                    EOH_Reinforced_Spatial_Casing.get(1),
                    ItemRefer.YOTTank_Cell_T9.get(1),
                    ItemList.Quantum_Tank_IV.get(4 * 7),
                    new ItemStack(BlockInfinityChest.instance, 7),

                    GregtechItemList.SpaceTimeContinuumRipper.get(1),
                    GTUtility.copyAmountUnsafe(7, ME_Singularity),
                    GTOreDictUnificator.get(OrePrefixes.bolt, Materials.BlackDwarfMatter, 32),
                    GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UXV, 3))
                .fluidInputs(
                    MUTATED_LIVING_SOLDER.getFluidStack(144 * 1280),
                    Materials.Space.getMolten(144 * 70),
                    Materials.SpaceTime.getMolten(144 * 10))
                .itemOutputs(SpacetimeCompressionFieldGeneratorTier6.get(1))
                .eut(com.Nxer.TwistSpaceTechnology.util.enums.TierEU.RECIPE_UMV)
                .duration(20 * 4000 * 7)
                .addTo(MASL);

            GTValues.RA.stdBuilder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(8),

                    EOH_Reinforced_Spatial_Casing.get(1),
                    ItemRefer.YOTTank_Cell_T9.get(2),
                    ItemList.Quantum_Tank_IV.get(4 * 8),
                    new ItemStack(BlockInfinityChest.instance, 8),

                    GregtechItemList.SpaceTimeContinuumRipper.get(2),
                    GTUtility.copyAmountUnsafe(8, ME_Singularity),
                    GTOreDictUnificator.get(OrePrefixes.bolt, Materials.MHDCSM, 2),
                    GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UXV, 3))
                .fluidInputs(
                    MUTATED_LIVING_SOLDER.getFluidStack(144 * 2560),
                    Materials.Space.getMolten(144 * 80),
                    Materials.SpaceTime.getMolten(144 * 10))
                .itemOutputs(SpacetimeCompressionFieldGeneratorTier7.get(1))
                .eut(com.Nxer.TwistSpaceTechnology.util.enums.TierEU.RECIPE_UMV)
                .duration(20 * 4000 * 8)
                .addTo(MASL);

            GTValues.RA.stdBuilder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(9),

                    EOH_Reinforced_Spatial_Casing.get(1),
                    ItemRefer.YOTTank_Cell_T9.get(3),
                    ItemList.Quantum_Tank_IV.get(4 * 9),
                    new ItemStack(BlockInfinityChest.instance, 9),

                    GregtechItemList.SpaceTimeContinuumRipper.get(3),
                    GTUtility.copyAmountUnsafe(9, ME_Singularity),
                    GTOreDictUnificator.get(OrePrefixes.bolt, Materials.MHDCSM, 8),
                    GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UXV, 3))
                .fluidInputs(
                    MUTATED_LIVING_SOLDER.getFluidStack(144 * 5120),
                    Materials.Space.getMolten(144 * 90),
                    Materials.SpaceTime.getMolten(144 * 10))
                .itemOutputs(SpacetimeCompressionFieldGeneratorTier8.get(1))
                .eut(com.Nxer.TwistSpaceTechnology.util.enums.TierEU.RECIPE_UMV)
                .duration(20 * 4000 * 9)
                .addTo(MASL);

            {
                final ItemStack baseCasing = tectech.thing.CustomItemList.EOH_Reinforced_Temporal_Casing.get(1);

                final ItemStack[] fusionReactors = new ItemStack[] { ItemList.FusionComputer_ZPMV.get(1),
                    ItemList.FusionComputer_ZPMV.get(2), ItemList.FusionComputer_ZPMV.get(3),
                    ItemList.FusionComputer_UV.get(1), ItemList.FusionComputer_UV.get(2),
                    ItemList.FusionComputer_UV.get(3), ModItem.getModItem(GregTech.ID, "gt.blockmachines", 1, 965),
                    ModItem.getModItem(GregTech.ID, "gt.blockmachines", 2, 965),
                    ModItem.getModItem(GregTech.ID, "gt.blockmachines", 3, 965) };

                final ItemStack[] fusionCoils = new ItemStack[] {
                    ModItem.getModItem(GoodGenerator.ID, "compactFusionCoil", 1, 1),
                    ModItem.getModItem(GoodGenerator.ID, "compactFusionCoil", 2, 1),
                    ModItem.getModItem(GoodGenerator.ID, "compactFusionCoil", 3, 1),
                    ModItem.getModItem(GoodGenerator.ID, "compactFusionCoil", 1, 2),
                    ModItem.getModItem(GoodGenerator.ID, "compactFusionCoil", 2, 2),
                    ModItem.getModItem(GoodGenerator.ID, "compactFusionCoil", 3, 2),
                    ModItem.getModItem(GoodGenerator.ID, "compactFusionCoil", 1, 3),
                    ModItem.getModItem(GoodGenerator.ID, "compactFusionCoil", 2, 3),
                    ModItem.getModItem(GoodGenerator.ID, "compactFusionCoil", 3, 3) };

                final ItemStack[] researchStuff = new ItemStack[] { baseCasing,
                    TimeAccelerationFieldGeneratorTier0.get(1), TimeAccelerationFieldGeneratorTier1.get(1),
                    TimeAccelerationFieldGeneratorTier2.get(1), TimeAccelerationFieldGeneratorTier3.get(1),
                    TimeAccelerationFieldGeneratorTier4.get(1), TimeAccelerationFieldGeneratorTier5.get(1),
                    TimeAccelerationFieldGeneratorTier6.get(1), TimeAccelerationFieldGeneratorTier7.get(1),
                    TimeAccelerationFieldGeneratorTier8.get(1) };

                final ItemStack[] spectralComponents = new ItemStack[] {
                    ModItem.getModItem(SuperSolarPanels.ID, "redcomponent", 64),
                    ModItem.getModItem(SuperSolarPanels.ID, "greencomponent", 64),
                    ModItem.getModItem(SuperSolarPanels.ID, "bluecomponent", 64) };

                for (int absoluteTier = 0; absoluteTier < 9; absoluteTier++) {
                    GTValues.RA.stdBuilder()
                        .itemInputs(
                            GTUtility.getIntegratedCircuit(absoluteTier + 1),

                            baseCasing,
                            fusionReactors[absoluteTier],
                            fusionCoils[absoluteTier],
                            ItemList.Machine_UV_SolarPanel.get(absoluteTier + 1),

                            new Object[] { OrePrefixes.circuit.get(Materials.UXV), absoluteTier + 1 },
                            spectralComponents[absoluteTier % spectralComponents.length],
                            spectralComponents[(absoluteTier + 1) % spectralComponents.length],
                            spectralComponents[(absoluteTier + 2) % spectralComponents.length],

                            plateList[absoluteTier],
                            ItemList.DysonSwarmDeploymentUnitCasing.get((absoluteTier + 1) * 4),
                            ItemList.DysonSwarmReceiverDish.get((absoluteTier + 1) * 4),
                            ItemList.AcceleratorUV.get((absoluteTier + 1) * 4),

                            ItemList.Energy_Module.get(absoluteTier + 1),
                            GTOreDictUnificator
                                .get(OrePrefixes.wireGt01, Materials.SuperconductorUMV, (absoluteTier + 1) * 4)

                        )
                        .fluidInputs(
                            MUTATED_LIVING_SOLDER.getFluidStack((int) (2_880 * pow(2, absoluteTier))),
                            Materials.Time.getMolten(1_440 * (absoluteTier + 1)),
                            Materials.SpaceTime.getMolten(144 * 10))
                        .itemOutputs(researchStuff[absoluteTier + 1])
                        .eut(com.Nxer.TwistSpaceTechnology.util.enums.TierEU.RECIPE_UMV)
                        .duration((absoluteTier + 1) * 4_000 * 20)
                        .addTo(MASL);
                }

            }

            {
                final ItemStack baseCasing = tectech.thing.CustomItemList.EOH_Infinite_Energy_Casing.get(1);

                int baseCompPerSec = 16_384;

                final ItemStack[] researchStuff = new ItemStack[] { baseCasing,
                    tectech.thing.CustomItemList.StabilisationFieldGeneratorTier0.get(1),
                    tectech.thing.CustomItemList.StabilisationFieldGeneratorTier1.get(1),
                    tectech.thing.CustomItemList.StabilisationFieldGeneratorTier2.get(1),
                    tectech.thing.CustomItemList.StabilisationFieldGeneratorTier3.get(1),
                    tectech.thing.CustomItemList.StabilisationFieldGeneratorTier4.get(1),
                    tectech.thing.CustomItemList.StabilisationFieldGeneratorTier5.get(1),
                    tectech.thing.CustomItemList.StabilisationFieldGeneratorTier6.get(1),
                    tectech.thing.CustomItemList.StabilisationFieldGeneratorTier7.get(1),
                    tectech.thing.CustomItemList.StabilisationFieldGeneratorTier8.get(1) };

                final ItemStack[] timeCasings = new ItemStack[] {
                    tectech.thing.CustomItemList.TimeAccelerationFieldGeneratorTier0.get(1),
                    tectech.thing.CustomItemList.TimeAccelerationFieldGeneratorTier1.get(1),
                    tectech.thing.CustomItemList.TimeAccelerationFieldGeneratorTier2.get(1),
                    tectech.thing.CustomItemList.TimeAccelerationFieldGeneratorTier3.get(1),
                    tectech.thing.CustomItemList.TimeAccelerationFieldGeneratorTier4.get(1),
                    tectech.thing.CustomItemList.TimeAccelerationFieldGeneratorTier5.get(1),
                    tectech.thing.CustomItemList.TimeAccelerationFieldGeneratorTier6.get(1),
                    tectech.thing.CustomItemList.TimeAccelerationFieldGeneratorTier7.get(1),
                    tectech.thing.CustomItemList.TimeAccelerationFieldGeneratorTier8.get(1) };

                final ItemStack[] spatialCasings = new ItemStack[] {
                    tectech.thing.CustomItemList.SpacetimeCompressionFieldGeneratorTier0.get(1),
                    tectech.thing.CustomItemList.SpacetimeCompressionFieldGeneratorTier1.get(1),
                    tectech.thing.CustomItemList.SpacetimeCompressionFieldGeneratorTier2.get(1),
                    tectech.thing.CustomItemList.SpacetimeCompressionFieldGeneratorTier3.get(1),
                    tectech.thing.CustomItemList.SpacetimeCompressionFieldGeneratorTier4.get(1),
                    tectech.thing.CustomItemList.SpacetimeCompressionFieldGeneratorTier5.get(1),
                    tectech.thing.CustomItemList.SpacetimeCompressionFieldGeneratorTier6.get(1),
                    tectech.thing.CustomItemList.SpacetimeCompressionFieldGeneratorTier7.get(1),
                    tectech.thing.CustomItemList.SpacetimeCompressionFieldGeneratorTier8.get(1) };

                for (int absoluteTier = 0; absoluteTier < 9; absoluteTier++) {
                    GTValues.RA.stdBuilder()
                        .itemInputs(
                            timeCasings[absoluteTier],
                            spatialCasings[absoluteTier],
                            baseCasing,
                            ItemList.DysonSwarmModule.get(4 * (absoluteTier + 1)),

                            GTOreDictUnificator
                                .get(OrePrefixes.frameGt, Materials.SuperconductorUMVBase, 4 * (absoluteTier + 1)),
                            GTOreDictUnificator
                                .get(OrePrefixes.frameGt, Materials.SuperconductorUIVBase, 4 * (absoluteTier + 1)),
                            GTOreDictUnificator
                                .get(OrePrefixes.frameGt, Materials.SuperconductorUEVBase, 4 * (absoluteTier + 1)),
                            GTOreDictUnificator
                                .get(OrePrefixes.frameGt, Materials.SuperconductorUHVBase, 4 * (absoluteTier + 1)),

                            ModItem.getModItem(GraviSuite.ID, "itemSimpleItem", 64, 3),
                            ModItem.getModItem(GraviSuite.ID, "itemSimpleItem", 64, 3),
                            ModItem.getModItem(GraviSuite.ID, "itemSimpleItem", 64, 3),
                            ModItem.getModItem(GraviSuite.ID, "itemSimpleItem", 64, 3),

                            boltList[absoluteTier],
                            GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UXV, 2 * (absoluteTier + 1)),
                            GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.SpaceTime, absoluteTier + 1),
                            GTOreDictUnificator.get(OrePrefixes.gearGtSmall, Materials.SpaceTime, absoluteTier + 1))
                        .fluidInputs(
                            MUTATED_LIVING_SOLDER.getFluidStack((int) (2_880 * pow(2, absoluteTier))),
                            Materials.Time.getMolten(1_440 * (absoluteTier + 1)),
                            Materials.Space.getMolten(1_440 * (absoluteTier + 1)),
                            Materials.SpaceTime.getMolten(144 * 10))
                        .itemOutputs(researchStuff[absoluteTier + 1])
                        .eut(com.Nxer.TwistSpaceTechnology.util.enums.TierEU.RECIPE_UMV)
                        .duration((absoluteTier + 1) * 4_000 * 20)
                        .addTo(MASL);
                }

            }

        }

    }

    // ===================== [Condensate Recipe Import] =====================

    public static void loadCondensateRecipes() {
        if (entangledToSource == null) entangledToSource = buildEntangledToSource();
        DeepSeikoBEC.getBackend()
            .clearRecipes();
        int copied = copyBecRecipes();
        if (copied == 0) {
            BEC_LOG.warn("BEC recipe pool is empty, DeepSeikoBEC will have no recipes");
        }
        BEC_LOG.info(
            "DeepSeikoBEC recipe pool filled with {} recipes ({} fluid mappings)",
            copied,
            entangledToSource.size());
    }

    private static int copyBecRecipes() {
        int copied = 0;
        boolean sampleLogged = false;
        for (GTRecipe recipe : TecTechRecipeMaps.condensateAssemblingRecipes.getAllRecipes()) {
            if (!usable(recipe)) continue;
            try {
                GTRecipe copy = recipe.copy();
                // Recipe copies drop metadata; preserve the nanite requirement in mSpecialValue.
                NaniteTier[] tiers = recipe.getMetadata(GTRecipeConstants.NANITE_TIERS);
                int maxTier = 0;
                if (tiers != null) {
                    for (NaniteTier tier : tiers) {
                        if (tier != null) maxTier = Math.max(maxTier, tier.getTier());
                    }
                }
                copy.mSpecialValue = maxTier;
                // TecTech stores the actual fluid inputs in recipe metadata.
                FluidStack[] entangled = recipe.getMetadata(GTRecipeConstants.CONDENSATE_INPUT);
                if (entangled == null || entangled.length == 0) entangled = recipe.mFluidInputs;
                copy.mFluidInputs = toOrdinaryFluids(entangled);
                if (!sampleLogged) {
                    sampleLogged = true;
                    BEC_LOG.info(
                        "DeepSeikoBEC sample conversion: {} -> {}",
                        describeFluids(entangled),
                        describeFluids(copy.mFluidInputs));
                }
                if (DeepSeikoBEC.add(copy) != null) {
                    copied++;
                }
            } catch (Throwable t) {
                BEC_LOG.warn("Failed to copy a BEC recipe into DeepSeikoBEC", t);
            }
        }
        return copied;
    }

    private static boolean usable(GTRecipe recipe) {
        return recipe != null && recipe.mEnabled && !recipe.mFakeRecipe && !recipe.mHidden;
    }

    private static String describeFluids(FluidStack[] fluids) {
        if (fluids == null || fluids.length == 0) return "(none)";
        StringBuilder sb = new StringBuilder();
        for (FluidStack fs : fluids) {
            if (sb.length() > 0) sb.append('+');
            if (fs == null || fs.getFluid() == null) sb.append("null");
            else sb.append(
                fs.getFluid()
                    .getName())
                .append('x')
                .append(fs.amount);
        }
        return sb.toString();
    }

    private static FluidStack[] toOrdinaryFluids(FluidStack[] fluids) {
        if (fluids == null) return null;
        FluidStack[] result = new FluidStack[fluids.length];
        for (int i = 0; i < fluids.length; i++) {
            FluidStack fs = fluids[i];
            if (fs == null || fs.getFluid() == null) {
                result[i] = fs;
                continue;
            }
            Fluid source = entangledToSource.get(
                fs.getFluid()
                    .getName());
            if (source == null) {
                String name = fs.getFluid()
                    .getName();
                if (name.startsWith("entangled_")) {
                    BEC_LOG.warn("DeepSeikoBEC: no mapping for entangled fluid {}, left as-is", name);
                }
                result[i] = fs;
            } else {
                result[i] = new FluidStack(source, fs.amount);
            }
        }
        return result;
    }

    private static Map<String, Fluid> buildEntangledToSource() {
        Map<String, Fluid> map = new HashMap<>();
        put(map, "neutronium", Materials.Neutronium.getMolten(1));
        put(map, "cosmicneutronium", Materials.CosmicNeutronium.getMolten(1));
        put(map, "bedrockium", Materials.Bedrockium.getMolten(1));
        put(map, "chromaticglass", MaterialsElements.STANDALONE.CHRONOMATIC_GLASS.getFluidStack(1));
        put(map, "celestialtungsten", MaterialsElements.STANDALONE.CELESTIAL_TUNGSTEN.getFluidStack(1));
        put(map, "infinity", Materials.Infinity.getMolten(1));
        put(map, "hypogen", MaterialsElements.STANDALONE.HYPOGEN.getFluidStack(1));
        put(map, "transcendentmetal", Materials.TranscendentMetal.getMolten(1));
        put(map, "dimshiftedsuperfluid", Materials.DimensionallyShiftedSuperfluid.getFluid(1));
        put(map, "phononmedium", Materials.PhononMedium.getFluid(1));
        put(map, "quarkgluonplasma", Materials.QuarkGluonPlasma.getFluid(1));
        put(map, "spacetime", Materials.SpaceTime.getMolten(1));
        put(map, "time", Materials.Time.getMolten(1));
        put(map, "space", Materials.Space.getMolten(1));
        put(map, "cosmicsolder", Materials.BoundlessCosmicSolder.getFluid(1));
        put(map, "mhdcsm", Materials.MHDCSM.getMolten(1));
        put(map, "magmatter", Materials.MagMatter.getMolten(1));
        put(map, "universium", Materials.Universium.getMolten(1));
        put(map, "eternity", Materials.Eternity.getMolten(1));
        putOth(map, "void", BWLiquids.Void.getFluidOrGas(1));
        putOth(map, "stars", BWLiquids.Stars.getFluidOrGas(1));
        putOth(map, "superconductoruiv", FluidRegistry.getFluidStack("molten.superconductoruivbase", 1));
        putOth(map, "superconductorumv", FluidRegistry.getFluidStack("molten.superconductorumvbase", 1));
        return map;
    }

    private static void putOth(Map<String, Fluid> map, String id, FluidStack source) {
        if (source == null || source.getFluid() == null) {
            BEC_LOG.warn("DeepSeikoBEC: source fluid for entangled_oth_{} is null, mapping skipped", id);
            return;
        }
        map.put("entangled_oth_" + id, source.getFluid());
    }

    private static void put(Map<String, Fluid> map, String entangledId, FluidStack source) {
        if (source == null || source.getFluid() == null) {
            BEC_LOG.warn("DeepSeikoBEC: source fluid for entangled_{} is null, mapping skipped", entangledId);
            return;
        }
        map.put("entangled_" + entangledId, source.getFluid());
    }
}
