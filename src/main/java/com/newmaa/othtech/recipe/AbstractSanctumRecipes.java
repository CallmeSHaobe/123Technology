package com.newmaa.othtech.recipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import com.newmaa.othtech.OTHTechnology;
import com.newmaa.othtech.common.materials.AbstractSanctumContent;
import com.newmaa.othtech.common.recipemap.Recipemaps;

import WayofTime.alchemicalWizardry.api.alchemy.AlchemyRecipe;
import WayofTime.alchemicalWizardry.api.alchemy.AlchemyRecipeRegistry;
import WayofTime.alchemicalWizardry.api.altarRecipeRegistry.AltarRecipe;
import WayofTime.alchemicalWizardry.api.altarRecipeRegistry.AltarRecipeRegistry;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.objects.OreDictItemStack;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.api.recipe.metadata.SimpleRecipeMetadataKey;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.InfusionEnchantmentRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.crafting.InfusionRunicAugmentRecipe;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipeElvenTrade;
import vazkii.botania.api.recipe.RecipeManaInfusion;
import vazkii.botania.api.recipe.RecipeRuneAltar;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.block.tile.TileTerraPlate;
import vazkii.botania.common.item.ModItems;

public class AbstractSanctumRecipes implements IRecipePool {

    public static final RecipeMetadataKey<Integer> INFUSION_ENCHANTMENT_EFFECT = SimpleRecipeMetadataKey
        .create(Integer.class, "otht.abstractSanctum.infusionEnchantment");
    public static final RecipeMetadataKey<Boolean> WAND_AUGMENTATION_RECIPE = SimpleRecipeMetadataKey
        .create(Boolean.class, "otht.abstractSanctum.wandAugmentation");
    public static final RecipeMetadataKey<DynamicInfusionOutput> DYNAMIC_INFUSION_OUTPUT = SimpleRecipeMetadataKey
        .create(DynamicInfusionOutput.class, "otht.abstractSanctum.dynamicInfusionOutput");

    private static final int CRAFT_TICKS = 40 * 20;
    private static final int DUPLICATE_TICKS = 64 * 20 / 10;
    private static final int DEFAULT_MANA_MB = 1000;
    private static final int DEFAULT_BLOOD_MB = 5000;
    private static final int CRAFT_BASE_SALT = 16;
    private static final int DUPLICATE_SALT = 2;
    private static final int DUPLICATE_MANA_MB = 100;
    private static final int DUPLICATE_BLOOD_MB = 100;
    private static final int[][] CRUCIBLE_SHARD_PAIRS = { { 4, 5 }, { 0, 1 }, { 2, 3 }, { 0, 2 }, { 0, 3 }, { 1, 2 },
        { 1, 3 }, { 2, 4 }, { 3, 5 }, { 1, 4 }, { 2, 5 }, { 0, 4 }, { 1, 5 }, { 0, 5 }, { 3, 4 } };
    private static final int MANA_INFUSION_CIRCUIT = 1;
    private static final int ALCHEMY_CIRCUIT = 2;
    private static final int TERRESTRIAL_AGGLOMERATION_CIRCUIT = 3;
    private static final int RUNE_ALTAR_CIRCUIT = 4;
    private static final int ELVEN_TRADE_CIRCUIT = 5;
    private static final int ARCANE_WORKBENCH_CIRCUIT = 11;
    private static final int INFUSION_CIRCUIT = 12;
    private static final int CRUCIBLE_CIRCUIT = 13;
    private static final int CRUCIBLE_DUPLICATE_CIRCUIT = 2;
    private static final int BLOOD_ALCHEMY_CIRCUIT = 21;
    private static final int BLOOD_ALTAR_CIRCUIT = 22;
    private static final int WORLD_SALT_DUPLICATE_CIRCUIT = 24;
    private static final Set<String> LOGGED_WAND_AUGMENTATIONS = new HashSet<>();
    private static final int MANA_INFUSION_EUT = (int) GTValues.VP[VoltageIndex.LV];
    private static final int ALCHEMY_EUT = (int) GTValues.VP[VoltageIndex.HV];
    private static final int TERRESTRIAL_AGGLOMERATION_EUT = (int) GTValues.VP[VoltageIndex.IV];
    private static final int RUNE_ALTAR_EUT = (int) GTValues.VP[VoltageIndex.EV];
    private static final int ELVEN_TRADE_EUT = (int) GTValues.VP[VoltageIndex.IV];
    private static final int INFUSION_EUT = (int) GTValues.VP[VoltageIndex.HV];
    private static final int CRUCIBLE_EUT = (int) GTValues.VP[VoltageIndex.LV];
    private static final int ARCANE_WORKBENCH_EUT = (int) GTValues.VP[VoltageIndex.MV];
    private static final int BLOOD_ALCHEMY_EUT = (int) GTValues.VP[VoltageIndex.MV];
    private static final int BLOOD_ALTAR_EUT = (int) GTValues.VP[VoltageIndex.HV];
    private static final int DUPLICATE_EUT = (int) GTValues.VP[VoltageIndex.LV];

    private static final Map<String, Integer> ASPECT_LEVELS = new HashMap<>();
    private static final List<GTRecipe> DYNAMIC_INFUSION_RUNTIME_RECIPES = new ArrayList<>();
    private static final List<GTRecipe> WAND_AUGMENTATION_RUNTIME_RECIPES = new ArrayList<>();
    private int crucibleShardFallbackCount;
    private int crucibleDuplicateCount;
    private int infusionEnchantmentCount;

    static {
        addAspectLevel(1, "perditio", "aqua", "ignis", "terra", "ordo", "aer");
        addAspectLevel(
            2,
            "lux",
            "vacuos",
            "potentia",
            "vitreus",
            "motus",
            "permutatio",
            "venenum",
            "gelum",
            "victus",
            "tempestas");
        addAspectLevel(
            3,
            "praecantatio",
            "radio",
            "sano",
            "iter",
            "herba",
            "fames",
            "volatus",
            "tenebrae",
            "bestia",
            "mortuus",
            "limus",
            "primordium",
            "vinculum",
            "metallum",
            "tempus");
        addAspectLevel(
            4,
            "magneto",
            "corpus",
            "infernus",
            "auram",
            "astrum",
            "spiritus",
            "alienis",
            "vitium",
            "exanimis",
            "arbor",
            "gula",
            "caelum",
            "superbia");
        addAspectLevel(5, "sensus", "luxuria", "desidia", "cognitio");
        addAspectLevel(6, "humanus", "aequalitas", "vesania", "strontio", "invidia");
        addAspectLevel(7, "perfodio", "instrumentum", "messis", "lucrum", "gloria");
        addAspectLevel(8, "nebrisum", "meto", "machina", "terminus", "telum", "pannus", "tutamen", "fabrico");
        addAspectLevel(9, "electrum", "ira", "tabernus");
    }

    private static void addAspectLevel(int level, String... names) {
        for (String name : names) ASPECT_LEVELS.put(name.toLowerCase(Locale.ROOT), level);
    }

    @Override
    public void loadRecipes() {
        crucibleShardFallbackCount = 0;
        crucibleDuplicateCount = 0;
        infusionEnchantmentCount = 0;
        DYNAMIC_INFUSION_RUNTIME_RECIPES.clear();
        WAND_AUGMENTATION_RUNTIME_RECIPES.clear();
        loadBotaniaRecipes();
        loadThaumcraftRecipes();
        loadBloodMagicRecipes();
        addWorldSaltDuplicateRecipe();
        OTHTechnology.LOG.info(
            "AbstractSanctum sources: Botania infusion={}, rune altar={}, elven trade={}, Thaumcraft={}, Blood altar={}, Blood alchemy={}",
            BotaniaAPI.manaInfusionRecipes.size(),
            BotaniaAPI.runeAltarRecipes.size(),
            BotaniaAPI.elvenTradeRecipes.size(),
            thaumcraft.api.ThaumcraftApi.getCraftingRecipes()
                .size(),
            AltarRecipeRegistry.altarRecipes.size(),
            AlchemyRecipeRegistry.recipes.size());
        OTHTechnology.LOG.info(
            "AbstractSanctum recipes loaded: craft={}, duplicate={}, dynamicInfusion={}, wandAugmentation={}, crucibleShardFallback={}, crucibleDuplicates={}, infusionEnchantments={}",
            Recipemaps.ABSTRACT_SANCTUM_CRAFT.getAllRecipes()
                .size(),
            Recipemaps.ABSTRACT_SANCTUM_DUPLICATE.getAllRecipes()
                .size(),
            DYNAMIC_INFUSION_RUNTIME_RECIPES.size(),
            WAND_AUGMENTATION_RUNTIME_RECIPES.size(),
            crucibleShardFallbackCount,
            crucibleDuplicateCount,
            infusionEnchantmentCount);
    }

    public static List<GTRecipe> getDynamicInfusionRuntimeRecipes() {
        return Collections.unmodifiableList(DYNAMIC_INFUSION_RUNTIME_RECIPES);
    }

    public static List<GTRecipe> getWandAugmentationRuntimeRecipes() {
        return Collections.unmodifiableList(WAND_AUGMENTATION_RUNTIME_RECIPES);
    }

    private void loadBotaniaRecipes() {
        for (RecipeManaInfusion recipe : BotaniaAPI.manaInfusionRecipes) {
            if (recipe == null || recipe.getOutput() == null) continue;
            try {
                if (recipe.isConjuration()) {
                    addDuplicateRecipe(recipe, MANA_INFUSION_CIRCUIT);
                } else {
                    addCraftRecipe(recipe, recipe.isAlchemy() ? ALCHEMY_CIRCUIT : MANA_INFUSION_CIRCUIT);
                }
            } catch (Throwable t) {
                OTHTechnology.LOG.warn("Failed to convert Botania recipe {}", recipe, t);
            }
        }

        loadRuneAltarRecipes();
        loadElvenTradeRecipes();
        loadTerrestrialAgglomerationRecipe();
    }

    private void loadRuneAltarRecipes() {
        for (RecipeRuneAltar source : BotaniaAPI.runeAltarRecipes) {
            if (source == null || source.getOutput() == null) continue;
            try {
                List<Object> inputs = normalizeBotaniaInputs(source.getInputs());
                if (!hasPayloadInput(inputs)) continue;
                if (ModBlocks.livingrock != null) {
                    inputs.add(new ItemStack(ModBlocks.livingrock, 1, 0));
                }
                addSaltToObjects(inputs, CRAFT_BASE_SALT);
                addCircuitToObjects(inputs, RUNE_ALTAR_CIRCUIT);

                List<FluidStack> fluids = new ArrayList<>();
                addFluid(fluids, AbstractSanctumContent.getManaStack(Math.max(1, source.getManaUsage() / 10)));
                addFluid(fluids, AbstractSanctumContent.getBloodStack(DEFAULT_BLOOD_MB));
                addConvertedCraftBuilder(
                    inputs,
                    source.getOutput()
                        .copy(),
                    fluids,
                    RUNE_ALTAR_EUT,
                    RUNE_ALTAR_CIRCUIT);
            } catch (Throwable t) {
                OTHTechnology.LOG.warn("Failed to convert Botania rune altar recipe {}", source, t);
            }
        }
    }

    private void loadElvenTradeRecipes() {
        int converted = 0;
        int skippedIdentityRecipes = 0;
        for (RecipeElvenTrade source : BotaniaAPI.elvenTradeRecipes) {
            if (source == null || source.getOutput() == null) continue;
            try {
                List<Object> inputs = normalizeBotaniaInputs(source.getInputs());
                if (!hasPayloadInput(inputs)) continue;
                ItemStack output = source.getOutput()
                    .copy();
                if (containsOutputAsInput(inputs, output)) {
                    skippedIdentityRecipes++;
                    continue;
                }
                addSaltToObjects(inputs, CRAFT_BASE_SALT);
                addCircuitToObjects(inputs, ELVEN_TRADE_CIRCUIT);

                List<FluidStack> fluids = new ArrayList<>();
                addFluid(fluids, AbstractSanctumContent.getManaStack(DEFAULT_MANA_MB));
                addFluid(fluids, AbstractSanctumContent.getBloodStack(DEFAULT_BLOOD_MB));
                addConvertedCraftBuilder(inputs, output, fluids, ELVEN_TRADE_EUT, ELVEN_TRADE_CIRCUIT);
                converted++;
            } catch (Throwable t) {
                OTHTechnology.LOG.warn("Failed to convert Botania elven trade recipe {}", source, t);
            }
        }
        OTHTechnology.LOG.info(
            "Botania elven trade conversion: converted={}, skippedIdentityRecipes={}",
            converted,
            skippedIdentityRecipes);
    }

    private void loadTerrestrialAgglomerationRecipe() {
        if (ModItems.manaResource == null) return;

        List<Object> inputs = new ArrayList<>();
        inputs.add(new ItemStack(ModItems.manaResource, 1, 0));
        inputs.add(new ItemStack(ModItems.manaResource, 1, 1));
        inputs.add(new ItemStack(ModItems.manaResource, 1, 2));
        addSaltToObjects(inputs, CRAFT_BASE_SALT);
        addCircuitToObjects(inputs, TERRESTRIAL_AGGLOMERATION_CIRCUIT);

        List<FluidStack> fluids = new ArrayList<>();
        addFluid(fluids, AbstractSanctumContent.getManaStack(TileTerraPlate.MAX_MANA / 10));
        addFluid(fluids, AbstractSanctumContent.getBloodStack(DEFAULT_BLOOD_MB));
        addConvertedCraftBuilder(
            inputs,
            new ItemStack(ModItems.manaResource, 1, 4),
            fluids,
            TERRESTRIAL_AGGLOMERATION_EUT,
            TERRESTRIAL_AGGLOMERATION_CIRCUIT);
    }

    private void addCraftRecipe(RecipeManaInfusion source, int circuit) {
        List<Object> inputs = normalizeBotaniaInputs(Collections.singletonList(source.getInput()));
        if (inputs.isEmpty()) return;
        addSaltToObjects(inputs, CRAFT_BASE_SALT);
        addCircuitToObjects(inputs, circuit);

        List<FluidStack> fluids = new ArrayList<>();
        addFluid(fluids, AbstractSanctumContent.getManaStack(Math.max(1, source.getManaToConsume() / 10)));
        addFluid(fluids, AbstractSanctumContent.getBloodStack(DEFAULT_BLOOD_MB));
        addConvertedCraftBuilder(
            inputs,
            source.getOutput()
                .copy(),
            fluids,
            circuit == ALCHEMY_CIRCUIT ? ALCHEMY_EUT : MANA_INFUSION_EUT,
            circuit);
    }

    private void addDuplicateRecipe(RecipeManaInfusion source, int circuit) {
        if (isWorldSalt(source.getOutput())) return;
        ItemStack duplicateInput = normalizeDuplicateInput(source.getInput());
        if (duplicateInput == null) return;
        List<ItemStack> inputs = new ArrayList<>();
        inputs.add(duplicateInput);
        addSalt(inputs, DUPLICATE_SALT);
        addCircuit(inputs, circuit);

        List<FluidStack> fluids = new ArrayList<>();
        addFluid(fluids, AbstractSanctumContent.getManaStack(DUPLICATE_MANA_MB));
        addFluid(fluids, AbstractSanctumContent.getBloodStack(DUPLICATE_BLOOD_MB));
        addBuilder(
            inputs,
            source.getOutput()
                .copy(),
            fluids,
            Recipemaps.ABSTRACT_SANCTUM_DUPLICATE,
            DUPLICATE_TICKS,
            DUPLICATE_EUT,
            circuit);
    }

    private void loadThaumcraftRecipes() {
        List<CrucibleRecipe> crucibleRecipes = new ArrayList<>();
        for (Object candidate : thaumcraft.api.ThaumcraftApi.getCraftingRecipes()) {
            if (candidate instanceof IArcaneRecipe arcaneRecipe) {
                try {
                    addArcaneWorkbenchRecipe(arcaneRecipe);
                } catch (Throwable t) {
                    OTHTechnology.LOG.warn("Failed to convert Thaumcraft arcane workbench recipe {}", arcaneRecipe, t);
                }
            } else if (candidate instanceof InfusionEnchantmentRecipe enchantmentRecipe) {
                try {
                    addInfusionEnchantmentRecipes(enchantmentRecipe);
                } catch (Throwable t) {
                    OTHTechnology.LOG
                        .warn("Failed to convert Thaumcraft infusion enchantment recipe {}", enchantmentRecipe, t);
                }
            } else if (candidate instanceof InfusionRecipe infusion) {
                try {
                    addInfusionRecipe(infusion);
                } catch (Throwable t) {
                    OTHTechnology.LOG.warn("Failed to convert Thaumcraft infusion recipe {}", infusion, t);
                }
            } else if (candidate instanceof CrucibleRecipe crucible) {
                crucibleRecipes.add(crucible);
            }
        }

        // Keep hard-coded shard signatures stable regardless of recipe registration order.
        crucibleRecipes.sort(Comparator.comparing(AbstractSanctumRecipes::crucibleSortKey));
        int shardPairIndex = 0;
        for (CrucibleRecipe crucible : crucibleRecipes) {
            try {
                if (addCrucibleCraftRecipe(crucible, shardPairIndex)) shardPairIndex++;
            } catch (Throwable t) {
                OTHTechnology.LOG.warn("Failed to convert Thaumcraft crucible crafting recipe {}", crucible, t);
            }
            try {
                addCrucibleDuplicateRecipe(crucible);
            } catch (Throwable t) {
                OTHTechnology.LOG.warn("Failed to convert Thaumcraft crucible duplicate recipe {}", crucible, t);
            }
        }
    }

    private static String crucibleSortKey(CrucibleRecipe recipe) {
        ItemStack output = recipe.getRecipeOutput();
        StringBuilder key = new StringBuilder();
        key.append(recipe.key)
            .append('|');
        if (output != null && output.getItem() != null) {
            key.append(
                output.getItem()
                    .getUnlocalizedName())
                .append('|')
                .append(output.getItemDamage())
                .append('|')
                .append(output.stackSize);
        }

        if (recipe.aspects != null && recipe.aspects.getAspects() != null) {
            List<String> aspects = new ArrayList<>();
            for (Aspect aspect : recipe.aspects.getAspects()) {
                if (aspect == null) continue;
                aspects.add(aspect.getTag() + ':' + recipe.aspects.getAmount(aspect));
            }
            Collections.sort(aspects);
            for (String aspect : aspects) key.append('|')
                .append(aspect);
        }
        return key.toString();
    }

    private void addArcaneWorkbenchRecipe(IArcaneRecipe source) {
        ItemStack output = source.getRecipeOutput();
        AspectList aspects = source.getAspects();
        if (aspects == null || aspects.visSize() <= 0) return;
        Object[] rawInputs;
        if (source instanceof ShapedArcaneRecipe shapedRecipe) {
            rawInputs = shapedRecipe.getInput();
        } else if (source instanceof ShapelessArcaneRecipe shapelessRecipe) {
            rawInputs = shapelessRecipe.getInput()
                .toArray();
        } else {
            return;
        }

        int salt = saltFor(aspects);
        List<Object> inputs = normalizeArcaneInputs(rawInputs);
        if (output == null || salt < 0 || inputs.isEmpty()) return;

        addSaltToObjects(inputs, salt);
        addCircuitToObjects(inputs, ARCANE_WORKBENCH_CIRCUIT);

        List<FluidStack> fluids = new ArrayList<>();
        addFluid(fluids, AbstractSanctumContent.getManaStack(DEFAULT_MANA_MB));
        addFluid(fluids, AbstractSanctumContent.getBloodStack(DEFAULT_BLOOD_MB));
        addConvertedCraftBuilder(inputs, output.copy(), fluids, ARCANE_WORKBENCH_EUT, ARCANE_WORKBENCH_CIRCUIT);
    }

    private void addInfusionRecipe(InfusionRecipe source) {
        ItemStack centerInput = source.getRecipeInput();
        if (centerInput == null || centerInput.getItem() == null) return;
        Object rawOutput = source.getRecipeOutput(centerInput);
        if (source instanceof InfusionRunicAugmentRecipe || isRunicShieldOutput(rawOutput)) return;

        ItemStack center = centerInput.copy();
        ItemStack output = resolveInfusionOutput(source, centerInput);
        if (output == null) return;

        AspectList aspects = source.getAspects(centerInput);
        int salt = saltFor(aspects);
        if (salt < 0) return;

        List<ItemStack> inputs = new ArrayList<>();
        addMergedInput(inputs, center);
        ItemStack[] components = source.getComponents();
        if (components != null) {
            for (ItemStack component : components) {
                addMergedInputExact(inputs, component);
            }
        }
        addSalt(inputs, salt);
        addCircuit(inputs, INFUSION_CIRCUIT);

        List<FluidStack> fluids = new ArrayList<>();
        addFluid(fluids, AbstractSanctumContent.getManaStack(DEFAULT_MANA_MB));
        addFluid(fluids, AbstractSanctumContent.getBloodStack(DEFAULT_BLOOD_MB));
        WandAugmentationSpec wandAugmentation = inspectWandAugmentation(source);
        if (wandAugmentation != null) {
            markWandAugmentationCenter(center, wandAugmentation);
            markWandAugmentationOutput(output, wandAugmentation);
            OTHTechnology.LOG
                .info("Thaumic Machina wand augmentation items: result={}, items={}", wandAugmentation.id, inputs);
            addCraftBuilderWithMetadata(
                inputs,
                output,
                fluids,
                Recipemaps.ABSTRACT_SANCTUM_CRAFT,
                INFUSION_EUT,
                INFUSION_CIRCUIT,
                WAND_AUGMENTATION_RECIPE,
                true);
        } else if (centerInput.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
            addCraftBuilderWithDynamicInfusionOutput(inputs, output, fluids, new DynamicInfusionOutput(source));
        } else {
            addCraftBuilder(inputs, output, fluids, Recipemaps.ABSTRACT_SANCTUM_CRAFT, INFUSION_EUT, INFUSION_CIRCUIT);
        }
    }

    private void addInfusionEnchantmentRecipes(InfusionEnchantmentRecipe source) {
        if (source == null || source.enchantment == null) return;
        int salt = saltFor(source.aspects);
        if (salt < 0) return;

        for (int currentLevel = 0; currentLevel < source.enchantment.getMaxLevel(); currentLevel++) {
            List<ItemStack> alternatives = collectEnchantmentCandidates(source, currentLevel);
            if (alternatives.isEmpty()) continue;

            List<Object> inputs = new ArrayList<>();
            inputs.add(alternatives.toArray(new ItemStack[0]));
            if (source.components != null) {
                for (ItemStack component : source.components) {
                    addMergedObjectInput(inputs, component);
                }
            }
            addSaltToObjects(inputs, salt);
            addCircuitToObjects(inputs, INFUSION_CIRCUIT);

            ItemStack displayOutput = alternatives.get(0)
                .copy();
            applyEnchantment(displayOutput, source.enchantment, currentLevel + 1);

            List<FluidStack> fluids = new ArrayList<>();
            addFluid(fluids, AbstractSanctumContent.getManaStack(DEFAULT_MANA_MB));
            addFluid(fluids, AbstractSanctumContent.getBloodStack(DEFAULT_BLOOD_MB));
            if (addConvertedCraftBuilderWithMetadata(
                inputs,
                displayOutput,
                fluids,
                INFUSION_EUT,
                INFUSION_CIRCUIT,
                INFUSION_ENCHANTMENT_EFFECT,
                source.enchantment.effectId)) {
                infusionEnchantmentCount++;
            }
        }
    }

    private static List<ItemStack> collectEnchantmentCandidates(InfusionEnchantmentRecipe source, int currentLevel) {
        List<ItemStack> candidates = new ArrayList<>();
        for (Object registryEntry : Item.itemRegistry) {
            if (!(registryEntry instanceof Item item)) continue;
            int maxMeta = item.getHasSubtypes() ? 15 : 0;
            for (int meta = 0; meta <= maxMeta; meta++) {
                ItemStack candidate = new ItemStack(item, 1, meta);
                try {
                    if (!item.isItemTool(candidate) || !source.enchantment.canApply(candidate)) continue;
                } catch (RuntimeException ignored) {
                    continue;
                }
                applyEnchantment(candidate, source.enchantment, currentLevel);
                candidates.add(candidate);
            }
        }
        return candidates;
    }

    private static void applyEnchantment(ItemStack stack, Enchantment enchantment, int level) {
        Map<Integer, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        if (level <= 0) {
            enchantments.remove(enchantment.effectId);
        } else {
            enchantments.put(enchantment.effectId, level);
        }
        EnchantmentHelper.setEnchantments(enchantments, stack);
    }

    private static boolean isWandAugmentationRecipe(InfusionRecipe source) {
        return "jcm2606.thaumicmachina.core.crafting.infusion.RecipeInfusionWandAugmentation".equals(
            source.getClass()
                .getName());
    }

    private static WandAugmentationSpec inspectWandAugmentation(InfusionRecipe source) {
        if (!isWandAugmentationRecipe(source)) return null;
        try {
            Object augmentation = source.getClass()
                .getField("augmentation")
                .get(source);
            String id = (String) augmentation.getClass()
                .getMethod("getID")
                .invoke(augmentation);
            Object[] prerequisiteObjects = (Object[]) augmentation.getClass()
                .getMethod("getPrerequisiteAugmentations")
                .invoke(augmentation);
            List<String> prerequisites = new ArrayList<>();
            if (prerequisiteObjects != null) {
                for (Object prerequisite : prerequisiteObjects) {
                    if (prerequisite == null) continue;
                    String prerequisiteId = (String) prerequisite.getClass()
                        .getMethod("getID")
                        .invoke(prerequisite);
                    if (!prerequisites.contains(prerequisiteId)) prerequisites.add(prerequisiteId);
                }
            }
            if (LOGGED_WAND_AUGMENTATIONS.add(id)) {
                OTHTechnology.LOG
                    .info("Thaumic Machina wand augmentation: result={}, prerequisites={}", id, prerequisites);
            }
            return new WandAugmentationSpec(id, prerequisites);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not inspect Thaumic Machina wand augmentation recipe", e);
        }
    }

    private static void markWandAugmentationCenter(ItemStack center, WandAugmentationSpec augmentation) {
        setAugmentationIds(center, augmentation.prerequisites);
    }

    private static void markWandAugmentationOutput(ItemStack output, WandAugmentationSpec augmentation) {
        List<String> augmentations = new ArrayList<>(augmentation.prerequisites);
        if (!augmentations.contains(augmentation.id)) augmentations.add(augmentation.id);
        setAugmentationIds(output, augmentations);
    }

    private static void setAugmentationIds(ItemStack stack, List<String> augmentations) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        NBTTagList list = new NBTTagList();
        for (String augmentation : augmentations) {
            list.appendTag(new NBTTagString(augmentation));
        }
        tag.setTag("Augmentations", list);
    }

    public static final class DynamicInfusionOutput {

        private final InfusionRecipe source;

        private DynamicInfusionOutput(InfusionRecipe source) {
            this.source = source;
        }

        public ItemStack resolve(ItemStack center) {
            return resolveInfusionOutput(source, center);
        }

        public boolean isRevolverRecipe() {
            return source.getClass()
                .getName()
                .equals("tb.utils.RevolverInfusionRecipe");
        }
    }

    private static final class WandAugmentationSpec {

        private final String id;
        private final List<String> prerequisites;

        private WandAugmentationSpec(String id, List<String> prerequisites) {
            this.id = id;
            this.prerequisites = prerequisites;
        }
    }

    private static boolean isRunicShieldOutput(Object rawOutput) {
        if (rawOutput instanceof Object[]nbtOutput && nbtOutput.length >= 1 && nbtOutput[0] instanceof String label) {
            return "RS.HARDEN".equals(label);
        }
        return rawOutput instanceof ItemStack output && output.hasTagCompound()
            && output.getTagCompound()
                .hasKey("RS.HARDEN");
    }

    private static ItemStack resolveInfusionOutput(InfusionRecipe source, ItemStack centerInput) {
        Object rawOutput = source.getRecipeOutput(centerInput);
        if (rawOutput instanceof ItemStack output) return output.copy();

        ItemStack output = centerInput.copy();
        if (rawOutput instanceof Object[]nbtOutput && nbtOutput.length >= 2
            && nbtOutput[0] instanceof String label
            && nbtOutput[1] instanceof NBTBase tag) {
            output.setTagInfo(label, tag);
            return output;
        }

        if (rawOutput instanceof Enchantment enchantment) {
            Map<Integer, Integer> enchantments = EnchantmentHelper.getEnchantments(output);
            int currentLevel = EnchantmentHelper.getEnchantmentLevel(enchantment.effectId, output);
            enchantments.put(enchantment.effectId, currentLevel + 1);
            EnchantmentHelper.setEnchantments(enchantments, output);
            return output;
        }
        return null;
    }

    /** Adds a real crucible conversion unless it is actually a self-duplication recipe. */
    private boolean addCrucibleCraftRecipe(CrucibleRecipe source, int shardPairIndex) {
        ItemStack output = copyRecipeOutput(source.getRecipeOutput());
        int salt = saltFor(source.aspects);
        if (output == null || salt < 0 || catalystMatchesOutput(source.catalyst, output) || isBalancedShard(output)) {
            return false;
        }

        List<Object> inputs = new ArrayList<>();
        Object catalyst = normalizeArcaneInput(source.catalyst);
        boolean usesShardSignature = false;
        if (catalyst != null) {
            inputs.add(catalyst);
        }
        if (!hasPayloadInput(inputs) && addCrucibleShardSignature(inputs, shardPairIndex)) {
            crucibleShardFallbackCount++;
            usesShardSignature = true;
        }
        if (!hasPayloadInput(inputs)) return false;
        addSaltToObjects(inputs, salt);
        addCircuitToObjects(inputs, CRUCIBLE_CIRCUIT);

        List<FluidStack> fluids = new ArrayList<>();
        addFluid(fluids, AbstractSanctumContent.getManaStack(DEFAULT_MANA_MB));
        addFluid(fluids, AbstractSanctumContent.getBloodStack(DEFAULT_BLOOD_MB));
        addConvertedCraftBuilder(inputs, output, fluids, CRUCIBLE_EUT, CRUCIBLE_CIRCUIT);
        return usesShardSignature;
    }

    /**
     * Adds a self-duplication entry only when the crucible catalyst can also be represented by
     * the output item. The duplicate frontend removes that catalyst from consumption.
     */
    private void addCrucibleDuplicateRecipe(CrucibleRecipe source) {
        ItemStack output = copyRecipeOutput(source.getRecipeOutput());
        int salt = saltFor(source.aspects);
        if (output == null || salt < 0 || isWorldSalt(output) || !catalystMatchesOutput(source.catalyst, output)) {
            return;
        }

        List<ItemStack> inputs = new ArrayList<>();
        ItemStack input = normalizeDuplicateInput(output);
        if (input == null) return;
        inputs.add(input);
        addSalt(inputs, DUPLICATE_SALT);
        addCircuit(inputs, CRUCIBLE_DUPLICATE_CIRCUIT);

        List<FluidStack> fluids = new ArrayList<>();
        addFluid(fluids, AbstractSanctumContent.getManaStack(DUPLICATE_MANA_MB));
        addFluid(fluids, AbstractSanctumContent.getBloodStack(DUPLICATE_BLOOD_MB));
        addBuilder(
            inputs,
            output,
            fluids,
            Recipemaps.ABSTRACT_SANCTUM_DUPLICATE,
            DUPLICATE_TICKS,
            DUPLICATE_EUT,
            CRUCIBLE_DUPLICATE_CIRCUIT);
        crucibleDuplicateCount++;
    }

    private static boolean hasPayloadInput(List<Object> inputs) {
        for (Object input : inputs) {
            if (input instanceof ItemStack stack) {
                if (!isWorldSalt(stack) && !GTUtility.isAnyIntegratedCircuit(stack)) return true;
            } else if (input instanceof OreDictItemStack) {
                return true;
            } else if (input instanceof ItemStack[]alternatives) {
                for (ItemStack alternative : alternatives) {
                    if (alternative != null && !isWorldSalt(alternative)
                        && !GTUtility.isAnyIntegratedCircuit(alternative)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean addCrucibleShardSignature(List<Object> inputs, int shardPairIndex) {
        if (ConfigItems.itemShard == null) return false;
        int[] pair = CRUCIBLE_SHARD_PAIRS[Math.floorMod(shardPairIndex, CRUCIBLE_SHARD_PAIRS.length)];
        int firstShard = pair[0];
        int secondShard = pair[1];
        inputs.add(new ItemStack(ConfigItems.itemShard, 1, firstShard));
        inputs.add(new ItemStack(ConfigItems.itemShard, 1, secondShard));
        return true;
    }

    private static boolean catalystMatchesOutput(Object catalyst, ItemStack output) {
        if (catalyst instanceof ItemStack stack) return matchesIdentityRecipe(stack, output);
        if (catalyst instanceof String ore) {
            for (ItemStack stack : OreDictionary.getOres(ore)) {
                if (matchesIdentityRecipe(stack, output)) return true;
            }
        }
        if (catalyst instanceof OreDictItemStack ore) {
            for (ItemStack stack : OreDictionary.getOres(ore.mOreName)) {
                if (matchesIdentityRecipe(stack, output)) return true;
            }
        }
        if (catalyst instanceof ItemStack[]stacks) {
            for (ItemStack stack : stacks) {
                if (matchesIdentityRecipe(stack, output)) return true;
            }
        }
        if (catalyst instanceof List<?>list) {
            for (Object value : list) {
                if (value instanceof ItemStack stack && matchesIdentityRecipe(stack, output)) return true;
            }
        }
        return false;
    }

    private void loadBloodMagicRecipes() {
        for (AltarRecipe source : AltarRecipeRegistry.altarRecipes) {
            try {
                if (source == null || source.canBeFilled) continue;
                ItemStack result = source.getResult();
                ItemStack required = source.getRequiredItem();
                if (result == null || required == null) continue;

                List<ItemStack> inputs = new ArrayList<>();
                inputs.add(required.copy());
                addSalt(inputs, CRAFT_BASE_SALT);
                addCircuit(inputs, BLOOD_ALTAR_CIRCUIT);

                List<FluidStack> fluids = new ArrayList<>();
                addFluid(fluids, AbstractSanctumContent.getManaStack(DEFAULT_MANA_MB));
                int blood = source.getLiquidRequired() > 0 ? source.getLiquidRequired() : DEFAULT_BLOOD_MB;
                addFluid(fluids, AbstractSanctumContent.getBloodStack(blood));
                addCraftBuilder(
                    inputs,
                    result.copy(),
                    fluids,
                    Recipemaps.ABSTRACT_SANCTUM_CRAFT,
                    BLOOD_ALTAR_EUT,
                    BLOOD_ALTAR_CIRCUIT);
            } catch (Throwable t) {
                OTHTechnology.LOG.warn("Failed to convert Blood Altar recipe {}", source, t);
            }
        }

        for (AlchemyRecipe source : AlchemyRecipeRegistry.recipes) {
            try {
                if (source == null || source.getResult() == null) continue;
                ItemStack[] recipeInputs = source.getRecipe();
                if (recipeInputs == null) continue;
                List<ItemStack> inputs = new ArrayList<>();
                for (ItemStack stack : recipeInputs) {
                    ItemStack clean = stripNbt(stack);
                    if (clean != null) inputs.add(clean);
                }
                if (inputs.isEmpty()) continue;
                addSalt(inputs, CRAFT_BASE_SALT);
                addCircuit(inputs, BLOOD_ALCHEMY_CIRCUIT);

                List<FluidStack> fluids = new ArrayList<>();
                addFluid(fluids, AbstractSanctumContent.getManaStack(DEFAULT_MANA_MB));
                addFluid(fluids, AbstractSanctumContent.getBloodStack(source.getAmountNeeded()));
                addCraftBuilder(
                    inputs,
                    source.getResult()
                        .copy(),
                    fluids,
                    Recipemaps.ABSTRACT_SANCTUM_CRAFT,
                    BLOOD_ALCHEMY_EUT,
                    BLOOD_ALCHEMY_CIRCUIT);
            } catch (Throwable t) {
                OTHTechnology.LOG.warn("Failed to convert Blood Alchemy recipe {}", source, t);
            }
        }
    }

    private void addWorldSaltDuplicateRecipe() {
        ItemStack salt = worldSalt(1);
        if (salt == null) return;

        List<ItemStack> inputs = new ArrayList<>();
        inputs.add(salt);
        addCircuit(inputs, WORLD_SALT_DUPLICATE_CIRCUIT);

        List<FluidStack> fluids = new ArrayList<>();
        addFluid(fluids, AbstractSanctumContent.getManaStack(DUPLICATE_MANA_MB));
        addFluid(fluids, AbstractSanctumContent.getBloodStack(DUPLICATE_BLOOD_MB));
        addBuilder(
            inputs,
            worldSalt(1),
            fluids,
            Recipemaps.ABSTRACT_SANCTUM_DUPLICATE,
            DUPLICATE_TICKS,
            DUPLICATE_EUT,
            WORLD_SALT_DUPLICATE_CIRCUIT);
    }

    private static void addCraftBuilder(List<ItemStack> inputs, ItemStack output, List<FluidStack> fluids,
        RecipeMap<?> map, int eut, int circuit) {
        addBuilder(inputs, output, fluids, map, CRAFT_TICKS, eut, circuit);
    }

    private static <T> void addCraftBuilderWithMetadata(List<ItemStack> inputs, ItemStack output,
        List<FluidStack> fluids, RecipeMap<?> map, int eut, int circuit, RecipeMetadataKey<T> key, T value) {
        addBuilderWithMetadata(inputs, output, fluids, map, CRAFT_TICKS, eut, circuit, key, value);
    }

    private static void addCraftBuilderWithDynamicInfusionOutput(List<ItemStack> inputs, ItemStack output,
        List<FluidStack> fluids, DynamicInfusionOutput dynamicOutput) {
        if (inputs.isEmpty() || output == null) return;
        List<ItemStack> mergedInputs = mergeInputs(inputs);
        ensureCircuitInStacks(mergedInputs, INFUSION_CIRCUIT);
        moveCircuitToEnd(mergedInputs);
        Collection<GTRecipe> added = GTValues.RA.stdBuilder()
            .itemInputsUnsafe(mergedInputs.toArray(new ItemStack[0]))
            .itemOutputs(output)
            .fluidInputs(fluids.toArray(new FluidStack[0]))
            .duration(CRAFT_TICKS)
            .eut(INFUSION_EUT)
            .metadata(DYNAMIC_INFUSION_OUTPUT, dynamicOutput)
            .addTo(Recipemaps.ABSTRACT_SANCTUM_CRAFT);
        DYNAMIC_INFUSION_RUNTIME_RECIPES.addAll(added);
    }

    private static void addConvertedCraftBuilder(List<Object> inputs, ItemStack output, List<FluidStack> fluids,
        int eut, int circuit) {
        if (inputs.isEmpty() || output == null) return;
        List<Object> mergedInputs = mergeObjectInputs(inputs);
        ensureCircuitInObjects(mergedInputs, circuit);
        moveCircuitToEndObjects(mergedInputs);
        GTValues.RA.stdBuilder()
            .itemInputs(mergedInputs.toArray(new Object[0]))
            .itemOutputs(output)
            .fluidInputs(fluids.toArray(new FluidStack[0]))
            .duration(CRAFT_TICKS)
            .eut(eut)
            .addTo(Recipemaps.ABSTRACT_SANCTUM_CRAFT);
    }

    private static <T> boolean addConvertedCraftBuilderWithMetadata(List<Object> inputs, ItemStack output,
        List<FluidStack> fluids, int eut, int circuit, RecipeMetadataKey<T> key, T value) {
        if (inputs.isEmpty() || output == null) return false;
        List<Object> mergedInputs = mergeObjectInputs(inputs);
        ensureCircuitInObjects(mergedInputs, circuit);
        moveCircuitToEndObjects(mergedInputs);
        Collection<GTRecipe> added = GTValues.RA.stdBuilder()
            .itemInputs(mergedInputs.toArray(new Object[0]))
            .itemOutputs(output)
            .fluidInputs(fluids.toArray(new FluidStack[0]))
            .duration(CRAFT_TICKS)
            .eut(eut)
            .metadata(key, value)
            .addTo(Recipemaps.ABSTRACT_SANCTUM_CRAFT);
        return !added.isEmpty();
    }

    private static void addBuilder(List<ItemStack> inputs, ItemStack output, List<FluidStack> fluids, RecipeMap<?> map,
        int duration, int eut, int circuit) {
        if (inputs.isEmpty() || output == null) return;
        List<ItemStack> mergedInputs = mergeInputs(inputs);
        ensureCircuitInStacks(mergedInputs, circuit);
        moveCircuitToEnd(mergedInputs);
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(mergedInputs.toArray(new ItemStack[0]))
            .itemOutputs(output)
            .fluidInputs(fluids.toArray(new FluidStack[0]))
            .duration(duration)
            .eut(eut)
            .addTo(map);
    }

    private static <T> void addBuilderWithMetadata(List<ItemStack> inputs, ItemStack output, List<FluidStack> fluids,
        RecipeMap<?> map, int duration, int eut, int circuit, RecipeMetadataKey<T> key, T value) {
        if (inputs.isEmpty() || output == null) return;
        List<ItemStack> mergedInputs = mergeInputs(inputs);
        ensureCircuitInStacks(mergedInputs, circuit);
        moveCircuitToEnd(mergedInputs);
        Collection<GTRecipe> added = GTValues.RA.stdBuilder()
            .itemInputsUnsafe(mergedInputs.toArray(new ItemStack[0]))
            .itemOutputs(output)
            .fluidInputs(fluids.toArray(new FluidStack[0]))
            .duration(duration)
            .eut(eut)
            .metadata(key, value)
            .addTo(map);
        if (key == WAND_AUGMENTATION_RECIPE) {
            WAND_AUGMENTATION_RUNTIME_RECIPES.addAll(added);
        }
    }

    private static void addFluid(List<FluidStack> fluids, FluidStack fluid) {
        if (fluid != null) fluids.add(fluid);
    }

    private static void addSalt(List<ItemStack> inputs, int amount) {
        ItemStack salt = worldSalt(amount);
        if (salt != null) inputs.add(salt);
    }

    private static void addSaltToObjects(List<Object> inputs, int amount) {
        ItemStack salt = worldSalt(amount);
        if (salt != null) inputs.add(salt);
    }

    private static void addCircuit(List<ItemStack> inputs, int circuit) {
        inputs.add(GTUtility.getIntegratedCircuit(circuit));
        moveCircuitToEnd(inputs);
    }

    private static void addCircuitToObjects(List<Object> inputs, int circuit) {
        inputs.add(GTUtility.getIntegratedCircuit(circuit));
        moveCircuitToEndObjects(inputs);
    }

    private static void moveCircuitToEndObjects(List<Object> inputs) {
        for (int i = 0; i < inputs.size(); i++) {
            Object input = inputs.get(i);
            if (input instanceof ItemStack stack && GTUtility.isAnyIntegratedCircuit(stack)) {
                inputs.remove(i);
                inputs.add(stack);
                return;
            }
        }
    }

    private static void moveCircuitToEnd(List<ItemStack> inputs) {
        for (int i = 0; i < inputs.size(); i++) {
            ItemStack input = inputs.get(i);
            if (GTUtility.isAnyIntegratedCircuit(input)) {
                inputs.remove(i);
                inputs.add(input);
                return;
            }
        }
    }

    private static boolean hasCircuitInObjects(List<Object> inputs) {
        for (Object input : inputs) {
            if (input instanceof ItemStack stack && GTUtility.isAnyIntegratedCircuit(stack)) return true;
        }
        return false;
    }

    private static void ensureCircuitInObjects(List<Object> inputs, int circuit) {
        if (!hasCircuitInObjects(inputs)) inputs.add(GTUtility.getIntegratedCircuit(circuit));
    }

    private static void ensureCircuitInStacks(List<ItemStack> inputs, int circuit) {
        if (!hasCircuitInStacks(inputs)) inputs.add(GTUtility.getIntegratedCircuit(circuit));
    }

    private static boolean hasCircuitInStacks(List<ItemStack> inputs) {
        for (ItemStack input : inputs) {
            if (GTUtility.isAnyIntegratedCircuit(input)) return true;
        }
        return false;
    }

    private static ItemStack worldSalt(int amount) {
        if (ConfigItems.itemResource == null || amount <= 0) return null;
        ItemStack stack = new ItemStack(ConfigItems.itemResource, 1, 14);
        stack.stackSize = amount;
        return stack;
    }

    public static ItemStack getWorldSaltStack(int amount) {
        return worldSalt(amount);
    }

    public static boolean isWorldSalt(ItemStack stack) {
        return stack != null && ConfigItems.itemResource != null
            && stack.getItem() == ConfigItems.itemResource
            && stack.getItemDamage() == 14;
    }

    private static boolean isBalancedShard(ItemStack stack) {
        return stack != null && ConfigItems.itemShard != null
            && stack.getItem() == ConfigItems.itemShard
            && stack.getItemDamage() == 6;
    }

    private static int saltFor(AspectList aspects) {
        if (aspects == null) return 16;
        long weighted = 0;
        Aspect[] presentAspects = aspects.getAspects();
        if (presentAspects == null) return 16;
        for (Aspect aspect : presentAspects) {
            if (aspect == null) continue;
            String tag = aspect.getTag()
                .toLowerCase(Locale.ROOT);
            weighted += (long) aspects.getAmount(aspect) * ASPECT_LEVELS.getOrDefault(tag, 0);
        }
        return 16 + (int) Math.min(Integer.MAX_VALUE - 16L, weighted / 6L);
    }

    private static List<Object> normalizeArcaneInputs(Object[] sourceInputs) {
        List<Object> result = new ArrayList<>();
        if (sourceInputs == null) return result;

        for (Object source : sourceInputs) {
            Object normalized = normalizeArcaneInput(source);
            if (normalized != null) addMergedObjectInput(result, normalized);
        }
        return result;
    }

    private static Object normalizeArcaneInput(Object source) {
        if (source instanceof ItemStack stack) {
            return stripNbt(stack);
        }
        if (source instanceof String ore) {
            return ore.isEmpty() || OreDictionary.getOres(ore)
                .isEmpty() ? null : new OreDictItemStack(ore, 1);
        }

        List<?> options = null;
        if (source instanceof List<?>list) {
            options = list;
        } else if (source instanceof Object[]array) {
            options = java.util.Arrays.asList(array);
        }
        if (options == null || options.isEmpty()) return null;

        List<ItemStack> normalizedOptions = new ArrayList<>();
        for (Object option : options) {
            if (option instanceof ItemStack stack) {
                ItemStack clean = stripNbt(stack);
                if (clean != null) normalizedOptions.add(clean);
            }
        }
        if (normalizedOptions.isEmpty()) return null;
        return normalizedOptions.size() == 1 ? normalizedOptions.get(0) : normalizedOptions.toArray(new ItemStack[0]);
    }

    private static List<Object> normalizeBotaniaInputs(List<Object> sourceInputs) {
        List<Object> result = new ArrayList<>();
        if (sourceInputs == null) return result;
        for (Object source : sourceInputs) {
            if (source instanceof ItemStack stack) {
                ItemStack clean = stripNbt(stack);
                if (clean == null) continue;
                clean.stackSize = Math.max(1, clean.stackSize);
                addMergedObjectInput(result, clean);
            } else if (source instanceof String ore) {
                addMergedObjectInput(result, new OreDictItemStack(ore, 1));
            }
        }
        return result;
    }

    private static boolean containsOutputAsInput(List<Object> inputs, ItemStack output) {
        if (output == null) return false;
        for (Object input : inputs) {
            if (input instanceof ItemStack stack && matchesIdentityRecipe(stack, output)) return true;
            if (input instanceof OreDictItemStack ore) {
                for (ItemStack candidate : OreDictionary.getOres(ore.mOreName)) {
                    if (matchesIdentityRecipe(candidate, output)) return true;
                }
            }
            if (input instanceof ItemStack[]alternatives) {
                for (ItemStack candidate : alternatives) {
                    if (matchesIdentityRecipe(candidate, output)) return true;
                }
            }
        }
        return false;
    }

    private static boolean matchesIdentityRecipe(ItemStack input, ItemStack output) {
        if (input == null || output == null || input.getItem() != output.getItem()) return false;
        int inputDamage = input.getItemDamage();
        int outputDamage = output.getItemDamage();
        return (inputDamage == outputDamage || inputDamage == OreDictionary.WILDCARD_VALUE
            || outputDamage == OreDictionary.WILDCARD_VALUE) && ItemStack.areItemStackTagsEqual(input, output);
    }

    private static ItemStack normalizeDuplicateInput(Object input) {
        ItemStack source = null;
        if (input instanceof ItemStack stack) {
            source = stack;
        } else if (input instanceof String ore) {
            List<ItemStack> ores = OreDictionary.getOres(ore);
            if (!ores.isEmpty()) source = ores.get(0);
        }
        if (source == null) return null;

        ItemStack clean = stripNbt(source);
        if (clean == null) return null;
        ItemStack unified = GTOreDictUnificator.get(clean);
        if (unified == null) return null;
        unified.stackSize = 1;
        unified.setTagCompound(null);
        return unified;
    }

    private static void addMergedInput(List<ItemStack> inputs, ItemStack input) {
        if (input == null) return;
        for (ItemStack existing : inputs) {
            if (sameItemIgnoreNbt(existing, input)) {
                existing.stackSize += input.stackSize;
                return;
            }
        }
        inputs.add(input.copy());
    }

    private static void addMergedInputExact(List<ItemStack> inputs, ItemStack input) {
        if (input == null || input.getItem() == null || input.stackSize <= 0) return;
        // Keep recipe-defined world salt independent from the machine's resource cost.
        // Otherwise the resource multiplier/waiver is incorrectly applied to both stacks.
        if (isWorldSalt(input)) {
            inputs.add(input.copy());
            return;
        }
        for (ItemStack existing : inputs) {
            if (sameItemIgnoreNbt(existing, input) && ItemStack.areItemStackTagsEqual(existing, input)) {
                existing.stackSize += input.stackSize;
                return;
            }
        }
        inputs.add(input.copy());
    }

    private static List<ItemStack> mergeInputs(List<ItemStack> inputs) {
        List<ItemStack> merged = new ArrayList<>();
        for (ItemStack input : inputs) addMergedInputExact(merged, input);
        return merged;
    }

    private static void addMergedObjectInput(List<Object> inputs, Object input) {
        if (input instanceof ItemStack stack) {
            if (isWorldSalt(stack)) {
                inputs.add(stack.copy());
                return;
            }
            for (int i = 0; i < inputs.size(); i++) {
                Object existing = inputs.get(i);
                if (existing instanceof ItemStack existingStack && sameItemIgnoreNbt(existingStack, stack)) {
                    existingStack.stackSize += stack.stackSize;
                    return;
                }
            }
            inputs.add(stack.copy());
        } else if (input instanceof ItemStack[]alternatives) {
            if (alternatives.length == 1) {
                addMergedObjectInput(inputs, alternatives[0]);
            } else if (alternatives.length > 1) {
                for (Object existing : inputs) {
                    if (!(existing instanceof ItemStack[]existingAlternatives)
                        || !sameAlternatives(existingAlternatives, alternatives)) {
                        continue;
                    }
                    for (int i = 0; i < existingAlternatives.length; i++) {
                        existingAlternatives[i].stackSize += alternatives[i].stackSize;
                    }
                    return;
                }

                ItemStack[] copy = new ItemStack[alternatives.length];
                for (int i = 0; i < alternatives.length; i++) {
                    copy[i] = alternatives[i].copy();
                }
                inputs.add(copy);
            }
        } else if (input instanceof OreDictItemStack ore) {
            for (int i = 0; i < inputs.size(); i++) {
                Object existing = inputs.get(i);
                if (existing instanceof OreDictItemStack existingOre && existingOre.mOreName.equals(ore.mOreName)) {
                    inputs.set(i, new OreDictItemStack(existingOre.mOreName, existingOre.mAmount + ore.mAmount));
                    return;
                }
            }
            inputs.add(ore);
        }
    }

    private static boolean sameAlternatives(ItemStack[] first, ItemStack[] second) {
        if (first.length != second.length) return false;
        for (int i = 0; i < first.length; i++) {
            if (!sameItemIgnoreNbt(first[i], second[i])) return false;
        }
        return true;
    }

    private static List<Object> mergeObjectInputs(List<Object> inputs) {
        List<Object> merged = new ArrayList<>();
        for (Object input : inputs) addMergedObjectInput(merged, input);
        return merged;
    }

    private static ItemStack stripNbt(ItemStack source) {
        if (source == null || source.getItem() == null || source.stackSize <= 0) return null;
        ItemStack copy = source.copy();
        copy.setTagCompound(null);
        return copy;
    }

    private static ItemStack copyRecipeOutput(ItemStack source) {
        return source == null || source.getItem() == null || source.stackSize <= 0 ? null : source.copy();
    }

    private static boolean sameItemIgnoreNbt(ItemStack a, ItemStack b) {
        return a != null && b != null && a.getItem() == b.getItem() && a.getItemDamage() == b.getItemDamage();
    }
}
