package com.newmaa.othtech.machine;

import static com.gtnewhorizon.cropsnh.utility.ModUtils.ModIDs.GregTech;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlockUnlocalizedName;
import static com.newmaa.othtech.utils.modsEnum.ThaumicBases;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_GLOW;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.chainAllGlasses;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.dreammaster.block.BlockList;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.Config;
import com.newmaa.othtech.OTHTechnology;
import com.newmaa.othtech.common.blocks.nailong.NailongCasings;
import com.newmaa.othtech.common.materials.AbstractSanctumContent;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.machine.gui.OTEAbstractSanctumGui;
import com.newmaa.othtech.machine.machineclass.ForcedCompletionSoundLoop;
import com.newmaa.othtech.machine.machineclass.OTHMultiMachineBase;
import com.newmaa.othtech.machine.machineclass.OTHProcessingLogic;
import com.newmaa.othtech.recipe.AbstractSanctumRecipes;
import com.newmaa.othtech.utils.ShapeLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fox.spiteful.avaritia.items.LudicrousItems;
import gregtech.api.enums.Textures;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrors;
import gregtech.api.util.ExoticEnergyInputHelper;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.GlassTier;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.api.util.ParallelHelper;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import tectech.thing.metaTileEntity.hatch.MTEHatchEnergyMulti;
import tectech.thing.metaTileEntity.hatch.MTEHatchEnergyTunnel;
import tectech.thing.metaTileEntity.hatch.MTEHatchWirelessMulti;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.items.wands.ItemWandCasting;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.block.ModFluffBlocks;

public class OTEAbstractSanctum extends OTHMultiMachineBase<OTEAbstractSanctum> {

    private static final String STRUCTURE_PIECE_MAIN = "main";

    private static final String STRUCTURE_RESOURCE = "/assets/123technology/structures/abstract_sanctum_shape.txt";

    private static final int STRUCTURE_WIDTH = 41;

    private static final int STRUCTURE_HEIGHT = 27;

    private static final int STRUCTURE_LAYERS = 41;

    /** Controller location in StructureLib coordinates: A=column, B=row, C=layer. */
    private static final int CONTROLLER_HORIZONTAL_OFFSET = 20;

    private static final int CONTROLLER_VERTICAL_OFFSET = 22;

    private static final int CONTROLLER_DEPTH_OFFSET = 20;

    private static final int CRAFT_TICKS = 40 * 20;

    private static final int DUPLICATE_TICKS = 64 * 20 / 10;

    private static final int DUPLICATE_BASE_PARALLEL = 64;

    private static final int OGANESSON_PER_RUN = 144;

    private static final int VIRTUAL_UPGRADE_RESOURCE_AMOUNT = 1_000_000;

    private static final int MANA_UPGRADE_SLOT = 0;

    private static final int SALT_UPGRADE_SLOT = 1;

    private static final int BLOOD_UPGRADE_SLOT = 2;

    private static IStructureDefinition<OTEAbstractSanctum> STRUCTURE_DEFINITION;

    private int glassTier = -1;

    private double manaMultiplier = 1.0D;

    private double saltMultiplier = 1.0D;

    private double bloodMultiplier = 1.0D;

    private int overclocksThisCheck;

    private int oganessonParallelThisCheck;

    private boolean oganessonActiveThisCheck;

    private final ItemStackHandler upgradeInventory = new UpgradeItemStackHandler();

    private static final String[][] shapeMain = ShapeLoader
        .load(STRUCTURE_RESOURCE, STRUCTURE_WIDTH, STRUCTURE_HEIGHT, STRUCTURE_LAYERS);

    private final class UpgradeItemStackHandler extends ItemStackHandler {

        private UpgradeItemStackHandler() {
            super(3);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return isUpgradeItem(slot, stack);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            if (getBaseMetaTileEntity() != null) getBaseMetaTileEntity().markDirty();
        }

        @Override
        protected void onLoad() {
            for (int slot = 0; slot < getSlots(); slot++) {
                if (!isItemValid(slot, getStackInSlot(slot))) {
                    setStackInSlot(slot, null);
                }
            }
        }
    }

    // ===================== Constructors =====================

    public OTEAbstractSanctum(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        alwaysMaxParallel = true;
    }

    public OTEAbstractSanctum(String aName) {
        super(aName);
        alwaysMaxParallel = true;
    }

    // ===================== NBT =====================

    @Override
    public void saveNBTData(NBTTagCompound tag) {
        super.saveNBTData(tag);
        tag.setDouble("manaMultiplier", manaMultiplier);
        tag.setDouble("saltMultiplier", saltMultiplier);
        tag.setDouble("bloodMultiplier", bloodMultiplier);
        tag.setTag("abstractSanctumUpgrades", upgradeInventory.serializeNBT());
    }

    @Override
    public void loadNBTData(NBTTagCompound tag) {
        super.loadNBTData(tag);
        alwaysMaxParallel = true;
        powerPanelMaxParallel = Math.max(1, getMaxParallelRecipes());
        manaMultiplier = clampMultiplier(tag.getDouble("manaMultiplier"));
        saltMultiplier = clampMultiplier(tag.getDouble("saltMultiplier"));
        bloodMultiplier = clampMultiplier(tag.getDouble("bloodMultiplier"));
        if (tag.hasKey("abstractSanctumUpgrades")) {
            upgradeInventory.deserializeNBT(tag.getCompoundTag("abstractSanctumUpgrades"));
        }
    }

    // ===================== Machine Parameters =====================

    @Override
    protected boolean isEnablePerfectOverclock() {
        return machineMode == 1;
    }

    @Override
    public int getMaxParallelRecipes() {
        return getMaxParallelForOverclocks(overclocksThisCheck);
    }

    @Override
    protected float getSpeedBonus() {
        if (machineMode != 0) return 1.0F;
        return (float) (1.0D / getAccelerationFactor());
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return machineMode == 1 ? Recipemaps.ABSTRACT_SANCTUM_DUPLICATE : Recipemaps.ABSTRACT_SANCTUM_CRAFT;
    }

    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(Recipemaps.ABSTRACT_SANCTUM_CRAFT, Recipemaps.ABSTRACT_SANCTUM_DUPLICATE);
    }

    @Override
    public int getPollutionPerSecond(ItemStack stack) {
        return 0;
    }

    @Override
    public int totalMachineMode() {
        return 2;
    }

    @Override
    public String getMachineModeName(int mode) {
        return translateToLocal(mode == 1 ? "ote.abstract_sanctum.mode.duplicate" : "ote.abstract_sanctum.mode.craft");
    }

    @Override
    public void setMachineMode(int mode) {
        if (mode < 0 || mode >= totalMachineMode()) return;
        // Do not let an in-flight crafting/duplication operation continue under the other mode.
        if (mode != machineMode && mMaxProgresstime > 0 && mMachine) return;

        int previousMode = machineMode;
        super.setMachineMode(mode);
        if (machineMode != previousMode && getBaseMetaTileEntity() != null && getBaseMetaTileEntity().isServerSide()) {
            mProgresstime = 0;
            mMaxProgresstime = 0;
            if (processingLogic != null) processingLogic.clear();
        }
    }

    // ===================== Processing Logic =====================

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new OTHProcessingLogic() {

            private final Set<GTRecipe> dynamicWandRecipes = Collections.newSetFromMap(new WeakHashMap<>());
            private boolean wandFallbackLogged;
            private String lastWandDiagnostic;
            private String lastFailureDiagnostic;
            private int lastProcessedMode = -1;
            private RecipeMap<?> lastFallbackMap;
            private String lastFallbackSignature;
            private List<GTRecipe> lastFallbackMatches = Collections.emptyList();

            @NotNull
            @Override
            public gregtech.api.recipe.check.CheckRecipeResult process() {
                if (lastProcessedMode != machineMode) {
                    lastProcessedMode = machineMode;
                    lastRecipe = null;
                    dynamicWandRecipes.clear();
                    lastWandDiagnostic = null;
                    lastFailureDiagnostic = null;
                    lastFallbackMap = null;
                    lastFallbackSignature = null;
                    lastFallbackMatches = Collections.emptyList();
                    wandFallbackLogged = false;
                }
                if (machineMode == 1 || hasAnyUpgradeInstalled() || findInputWand() != null) {
                    isRecipeLocked = false;
                }
                setSpeedBonus(getSpeedBonus());
                setEuModifier(getEuModifier());
                setOverclock(isEnablePerfectOverclock() ? 4 : 2, 4);
                CheckRecipeResult result = super.process();
                if (!result.wasSuccessful()) {
                    String status = result instanceof SimpleCheckRecipeResult simple ? simple.getTranslationKey()
                        : result.getID();
                    String itemDescription = describeItemStacks(inputItems);
                    String fluidDescription = describeFluidStacks(inputFluids);
                    String signature = machineMode + "|" + status + '|' + itemDescription + '|' + fluidDescription;
                    if (!signature.equals(lastFailureDiagnostic)) {
                        lastFailureDiagnostic = signature;
                        OTHTechnology.LOG.warn(
                            "AbstractSanctum recipe check failed: mode={}, status={}, items={}, fluids={}",
                            machineMode,
                            status,
                            itemDescription,
                            fluidDescription);
                    }
                } else {
                    lastFailureDiagnostic = null;
                }
                return result;
            }

            @NotNull
            @Override
            protected Stream<GTRecipe> findRecipeMatches(RecipeMap<?> map) {
                if (map == null) return Stream.empty();

                ItemStack[] queryItems = inputItems;
                FluidStack[] queryFluids = inputFluids;
                if (isUpgradeInstalled(SALT_UPGRADE_SLOT)) {
                    queryItems = Arrays.copyOf(queryItems, queryItems.length + 1);
                    queryItems[queryItems.length - 1] = AbstractSanctumRecipes
                        .getWorldSaltStack(VIRTUAL_UPGRADE_RESOURCE_AMOUNT);
                }
                if (isUpgradeInstalled(MANA_UPGRADE_SLOT)) {
                    queryFluids = appendVirtualFluid(
                        queryFluids,
                        AbstractSanctumContent.getManaStack(VIRTUAL_UPGRADE_RESOURCE_AMOUNT));
                }
                if (isUpgradeInstalled(BLOOD_UPGRADE_SLOT)) {
                    queryFluids = appendVirtualFluid(
                        queryFluids,
                        AbstractSanctumContent.getBloodStack(VIRTUAL_UPGRADE_RESOURCE_AMOUNT));
                }

                final ItemStack[] matchingItems = queryItems;
                final FluidStack[] matchingFluids = queryFluids;
                List<GTRecipe> indexedMatches = map.findRecipeQuery()
                    .caching(recipeCaching)
                    .items(matchingItems)
                    .fluids(matchingFluids)
                    .specialSlot(specialSlotItem)
                    .cachedRecipe(lastRecipe)
                    .findAll()
                    // Dynamic recipes use placeholder inputs and must be resolved by the dedicated
                    // matcher below, once their runtime input and output have been resolved.
                    .filter(recipe -> !isDedicatedRuntimeRecipe(recipe))
                    .collect(java.util.stream.Collectors.toList());
                Stream<GTRecipe> regularMatches = indexedMatches.stream();
                if (indexedMatches.isEmpty()) {
                    String fallbackSignature = buildFallbackSignature(matchingItems, matchingFluids);
                    if (lastFallbackMap != map || !fallbackSignature.equals(lastFallbackSignature)) {
                        lastFallbackMap = map;
                        lastFallbackSignature = fallbackSignature;
                        lastFallbackMatches = map.getAllRecipes()
                            .stream()
                            .filter(recipe -> recipe.mEnabled && !recipe.mFakeRecipe)
                            .filter(recipe -> !isDedicatedRuntimeRecipe(recipe))
                            .filter(recipe -> recipe.isRecipeInputEqual(false, false, matchingFluids, matchingItems))
                            .collect(java.util.stream.Collectors.toList());
                    }
                    regularMatches = lastFallbackMatches.stream();
                }
                Stream<GTRecipe> dynamicMatches = Stream.empty();
                Stream<GTRecipe> wandAugmentations = Stream.empty();
                if (machineMode == 0) {
                    final ItemStack[] dynamicQueryItems = queryItems;
                    final FluidStack[] dynamicQueryFluids = queryFluids;
                    dynamicMatches = AbstractSanctumRecipes.getDynamicInfusionRuntimeRecipes()
                        .stream()
                        .filter(recipe -> recipe.mEnabled && !recipe.mFakeRecipe)
                        .filter(recipe -> hasItemRequirements(recipe, dynamicQueryItems))
                        .filter(recipe -> hasFluidRequirements(recipe, dynamicQueryFluids));
                    wandAugmentations = findWandAugmentationRecipes(queryItems, queryFluids);
                }
                return Stream.concat(Stream.concat(regularMatches, dynamicMatches), wandAugmentations)
                    .distinct()
                    .map(this::prepareRuntimeRecipe)
                    .filter(Objects::nonNull);
            }

            private String buildFallbackSignature(ItemStack[] items, FluidStack[] fluids) {
                StringBuilder signature = new StringBuilder();
                if (items != null) {
                    for (ItemStack item : items) {
                        if (item == null) continue;
                        signature.append(System.identityHashCode(item.getItem()))
                            .append('#')
                            .append(item.getItemDamage())
                            .append('x')
                            .append(item.stackSize)
                            .append('@')
                            .append(
                                item.hasTagCompound() ? item.getTagCompound()
                                    .hashCode() : 0)
                            .append(';');
                    }
                }
                signature.append('|');
                if (fluids != null) {
                    for (FluidStack fluid : fluids) {
                        if (fluid == null) continue;
                        signature.append(System.identityHashCode(fluid.getFluid()))
                            .append('x')
                            .append(fluid.amount)
                            .append(';');
                    }
                }
                return signature.toString();
            }

            private boolean isDedicatedRuntimeRecipe(GTRecipe recipe) {
                return recipe.getMetadata(AbstractSanctumRecipes.DYNAMIC_INFUSION_OUTPUT) != null
                    || recipe.getMetadataOrDefault(AbstractSanctumRecipes.WAND_AUGMENTATION_RECIPE, false);
            }

            private Stream<GTRecipe> findWandAugmentationRecipes(ItemStack[] queryItems, FluidStack[] queryFluids) {
                ItemStack wand = null;
                for (ItemStack input : inputItems) {
                    if (input != null && input.getItem() instanceof ItemWandCasting) {
                        wand = input;
                        break;
                    }
                }
                if (wand == null) return Stream.empty();

                final ItemStack actualWand = wand;
                List<GTRecipe> wandRecipes = AbstractSanctumRecipes.getWandAugmentationRuntimeRecipes();
                List<GTRecipe> matches = new ArrayList<>();
                for (GTRecipe recipe : wandRecipes) {
                    if (!recipe.mEnabled || recipe.mFakeRecipe) continue;
                    boolean itemsMatch = hasItemRequirements(recipe, queryItems);
                    boolean fluidsMatch = hasFluidRequirements(recipe, queryFluids);
                    boolean wandMatches = matchesWandAugmentation(recipe, actualWand);
                    if (itemsMatch && fluidsMatch && wandMatches) {
                        matches.add(recipe);
                    }
                }
                if (matches.isEmpty()) {
                    String itemDescription = describeItemStacks(queryItems);
                    String signature = itemDescription + '|' + describeFluidStacks(queryFluids);
                    if (!signature.equals(lastWandDiagnostic)) {
                        lastWandDiagnostic = signature;
                        StringBuilder rejected = new StringBuilder();
                        for (GTRecipe recipe : wandRecipes) {
                            if (!recipe.mEnabled || recipe.mFakeRecipe || rejected.length() >= 1200) continue;
                            rejected.append("\n  ")
                                .append(getAugmentationOutputIds(recipe))
                                .append(" items=")
                                .append(hasItemRequirements(recipe, queryItems))
                                .append(" fluids=")
                                .append(hasFluidRequirements(recipe, queryFluids))
                                .append(" wand=")
                                .append(matchesWandAugmentation(recipe, actualWand))
                                .append(" missing=")
                                .append(describeMissingItemRequirements(recipe, queryItems));
                        }
                        OTHTechnology.LOG.warn(
                            "Wand augmentation found no match. wand={}, augments={}, items={}, candidates={}{}",
                            actualWand,
                            getAugmentationList(actualWand),
                            itemDescription,
                            wandRecipes.size(),
                            rejected);
                    }
                }
                return matches.stream();
            }

            private String getAugmentationOutputIds(GTRecipe recipe) {
                if (recipe.mOutputs == null || recipe.mOutputs.length == 0
                    || recipe.mOutputs[0] == null
                    || !recipe.mOutputs[0].hasTagCompound()) {
                    return "unknown";
                }
                return recipe.mOutputs[0].getTagCompound()
                    .getTagList("Augmentations", 8)
                    .toString();
            }

            private boolean hasItemRequirements(GTRecipe recipe, ItemStack[] availableItems) {
                if (recipe.mInputs == null) return true;
                List<ItemStack> availablePool = new ArrayList<>();
                for (ItemStack availableItem : availableItems) {
                    if (availableItem != null
                        && (availableItem.stackSize > 0 || GTUtility.isAnyIntegratedCircuit(availableItem))) {
                        availablePool.add(availableItem.copy());
                    }
                }
                for (ItemStack required : recipe.mInputs) {
                    if (required == null) continue;
                    int requiredAmount = required.stackSize;
                    if (requiredAmount <= 0) {
                        if (AbstractSanctumRecipes.isNonConsumedInfusionCatalyst(required)) {
                            if (!hasAvailableItemType(availablePool, required)) return false;
                            continue;
                        }
                        if (GTUtility.isAnyIntegratedCircuit(required)) requiredAmount = 1;
                    }
                    if (requiredAmount <= 0) continue;
                    if (consumeAvailableItem(availablePool, required, requiredAmount) < requiredAmount) return false;
                }
                return true;
            }

            private String describeMissingItemRequirements(GTRecipe recipe, ItemStack[] availableItems) {
                if (recipe == null || recipe.mInputs == null) return "[]";
                List<String> missing = new ArrayList<>();
                List<ItemStack> availablePool = new ArrayList<>();
                for (ItemStack availableItem : availableItems) {
                    if (availableItem != null
                        && (availableItem.stackSize > 0 || GTUtility.isAnyIntegratedCircuit(availableItem))) {
                        availablePool.add(availableItem.copy());
                    }
                }
                for (ItemStack required : recipe.mInputs) {
                    if (required == null) continue;
                    int requiredAmount = required.stackSize;
                    if (requiredAmount <= 0) {
                        if (AbstractSanctumRecipes.isNonConsumedInfusionCatalyst(required)) {
                            if (!hasAvailableItemType(availablePool, required)) {
                                missing.add(required + " x1 (non-consumed catalyst)");
                            }
                            continue;
                        }
                        if (GTUtility.isAnyIntegratedCircuit(required)) requiredAmount = 1;
                    }
                    if (requiredAmount <= 0) continue;
                    int consumed = consumeAvailableItem(availablePool, required, requiredAmount);
                    if (consumed < requiredAmount) {
                        missing.add(required + " x" + requiredAmount + " (short " + (requiredAmount - consumed) + ')');
                    }
                }
                return missing.toString();
            }

            private boolean hasAvailableItemType(List<ItemStack> availablePool, ItemStack required) {
                for (ItemStack available : availablePool) {
                    if (matchesRequiredItem(available, required)) return true;
                }
                return false;
            }

            private int consumeAvailableItem(List<ItemStack> availablePool, ItemStack required, int amount) {
                int remaining = amount;
                for (ItemStack available : availablePool) {
                    if (remaining <= 0) break;
                    if (!matchesRequiredItem(available, required)) continue;
                    if (available.stackSize <= 0 && GTUtility.isAnyIntegratedCircuit(available)) {
                        remaining -= Math.min(remaining, 1);
                        continue;
                    }
                    if (available.stackSize <= 0) continue;
                    int consumed = Math.min(remaining, available.stackSize);
                    available.stackSize -= consumed;
                    remaining -= consumed;
                }
                return amount - remaining;
            }

            private String describeItemStacks(ItemStack[] stacks) {
                StringBuilder result = new StringBuilder("[");
                if (stacks != null) {
                    boolean first = true;
                    for (ItemStack stack : stacks) {
                        if (stack == null) continue;
                        if (!first) result.append(", ");
                        first = false;
                        result.append(stack.stackSize)
                            .append('x')
                            .append(
                                stack.getItem() == null ? "null"
                                    : stack.getItem()
                                        .getUnlocalizedName())
                            .append('@')
                            .append(stack.getItemDamage());
                        if (stack.hasTagCompound()) result.append("+nbt");
                    }
                }
                return result.append(']')
                    .toString();
            }

            private String describeFluidStacks(FluidStack[] stacks) {
                StringBuilder result = new StringBuilder("[");
                if (stacks != null) {
                    boolean first = true;
                    for (FluidStack stack : stacks) {
                        if (stack == null) continue;
                        if (!first) result.append(", ");
                        first = false;
                        result.append(
                            stack.getFluid() == null ? "null"
                                : stack.getFluid()
                                    .getName())
                            .append(" x")
                            .append(stack.amount);
                    }
                }
                return result.append(']')
                    .toString();
            }

            private boolean hasFluidRequirements(GTRecipe recipe, FluidStack[] availableFluids) {
                if (recipe.mFluidInputs == null) return true;
                for (FluidStack required : recipe.mFluidInputs) {
                    if (required == null || required.amount <= 0) continue;
                    long available = 0;
                    for (FluidStack availableFluid : availableFluids) {
                        if (availableFluid != null && availableFluid.isFluidEqual(required)) {
                            available += availableFluid.amount;
                        }
                    }
                    if (available < required.amount) return false;
                }
                return true;
            }

            private boolean matchesWandAugmentation(GTRecipe recipe, ItemStack wand) {
                if (recipe.mInputs == null || recipe.mInputs.length == 0
                    || recipe.mOutputs == null
                    || recipe.mOutputs.length == 0
                    || recipe.mOutputs[0] == null
                    || !recipe.mOutputs[0].hasTagCompound()) {
                    return false;
                }

                NBTTagList required = getAugmentationList(recipe.mInputs[0]);
                NBTTagList result = recipe.mOutputs[0].getTagCompound()
                    .getTagList("Augmentations", 8);
                if (!hasAugmentations(wand, required)) return false;
                boolean hasNewAugmentation = false;
                for (int i = 0; i < result.tagCount(); i++) {
                    String id = result.getStringTagAt(i);
                    if (containsAugmentation(required, id)) continue;
                    if (hasAugmentation(wand, id)) return false;
                    hasNewAugmentation = true;
                }
                return hasNewAugmentation;
            }

            private NBTTagList getAugmentationList(ItemStack stack) {
                if (stack == null || !stack.hasTagCompound()) return new NBTTagList();
                return stack.getTagCompound()
                    .getTagList("Augmentations", 8);
            }

            private boolean hasAugmentations(ItemStack wand, NBTTagList augmentations) {
                for (int i = 0; i < augmentations.tagCount(); i++) {
                    if (!hasAugmentation(wand, augmentations.getStringTagAt(i))) return false;
                }
                return true;
            }

            private boolean hasAugmentation(ItemStack wand, String id) {
                NBTTagList augmentations = getAugmentationList(wand);
                for (int i = 0; i < augmentations.tagCount(); i++) {
                    if (id.equals(augmentations.getStringTagAt(i))) return true;
                }
                return false;
            }

            private GTRecipe prepareRuntimeRecipe(GTRecipe recipe) {
                Integer enchantmentId = recipe
                    .getMetadataOrDefault(AbstractSanctumRecipes.INFUSION_ENCHANTMENT_EFFECT, -1);
                GTRecipe prepared;
                if (enchantmentId >= 0) {
                    prepared = prepareEnchantmentOutput(recipe, enchantmentId);
                } else {
                    boolean wandAugmentation = recipe
                        .getMetadataOrDefault(AbstractSanctumRecipes.WAND_AUGMENTATION_RECIPE, false);
                    if (wandAugmentation) {
                        prepared = prepareWandAugmentationOutput(recipe);
                    } else {
                        AbstractSanctumRecipes.DynamicInfusionOutput dynamicOutput = recipe
                            .getMetadata(AbstractSanctumRecipes.DYNAMIC_INFUSION_OUTPUT);
                        prepared = dynamicOutput != null ? prepareDynamicInfusionOutput(recipe, dynamicOutput) : recipe;
                    }
                }
                return unifyRuntimeOutputs(prepared);
            }

            private GTRecipe unifyRuntimeOutputs(GTRecipe recipe) {
                if (recipe == null || recipe.mOutputs == null) return recipe;
                GTRecipe result = recipe.copy();
                for (int i = 0; i < result.mOutputs.length; i++) {
                    ItemStack output = AbstractSanctumRecipes.unifyRecipeOutput(result.mOutputs[i]);
                    if (output != null) result.mOutputs[i] = output;
                }
                return result;
            }

            private GTRecipe prepareEnchantmentOutput(GTRecipe recipe, int enchantmentId) {
                if (enchantmentId >= Enchantment.enchantmentsList.length) return null;
                Enchantment enchantment = Enchantment.enchantmentsList[enchantmentId];
                if (enchantment == null) return null;

                ItemStack target = findEnchantmentTarget(recipe, enchantment);
                if (target == null) return null;
                int currentLevel = EnchantmentHelper.getEnchantmentLevel(enchantmentId, target);
                if (currentLevel >= enchantment.getMaxLevel()) return null;

                ItemStack output = target.copy();
                Map<Integer, Integer> enchantments = EnchantmentHelper.getEnchantments(output);
                enchantments.put(enchantmentId, currentLevel + 1);
                EnchantmentHelper.setEnchantments(enchantments, output);

                GTRecipe result = recipe.copy();
                result.mOutputs = new ItemStack[] { output };
                return result;
            }

            private GTRecipe prepareWandAugmentationOutput(GTRecipe recipe) {
                ItemStack wand = null;
                for (ItemStack input : inputItems) {
                    if (input != null && input.getItem() instanceof ItemWandCasting) {
                        wand = input;
                        break;
                    }
                }
                if (wand == null || recipe.mOutputs == null
                    || recipe.mOutputs.length == 0
                    || recipe.mOutputs[0] == null
                    || !recipe.mOutputs[0].hasTagCompound()) {
                    return null;
                }

                NBTTagCompound sourceTag = recipe.mOutputs[0].getTagCompound();
                if (!sourceTag.hasKey("Augmentations", 9)) return null;

                ItemStack output = wand.copy();
                NBTTagCompound outputTag = output.getTagCompound();
                if (outputTag == null) {
                    outputTag = new NBTTagCompound();
                    output.setTagCompound(outputTag);
                }
                NBTTagList augmentations = outputTag.getTagList("Augmentations", 8);
                NBTTagList additions = sourceTag.getTagList("Augmentations", 8);
                for (int i = 0; i < additions.tagCount(); i++) {
                    String augmentation = additions.getStringTagAt(i);
                    if (!containsAugmentation(augmentations, augmentation)) {
                        augmentations.appendTag(new NBTTagString(augmentation));
                    }
                }
                outputTag.setTag("Augmentations", augmentations);

                GTRecipe result = recipe.copy();
                // The output and the wand input are both resolved from the live input bus.
                // Do not let GT cache this recipe together with the previous wand's NBT.
                result.mCanBeBuffered = false;
                if (result.mInputs != null) {
                    for (int i = 0; i < result.mInputs.length; i++) {
                        if (result.mInputs[i] != null && result.mInputs[i].getItem() instanceof ItemWandCasting) {
                            result.mInputs[i] = wand.copy();
                            if (result instanceof GTRecipe.GTRecipe_WithAlt withAlt && withAlt.mOreDictAlt != null
                                && i < withAlt.mOreDictAlt.length) {
                                withAlt.mOreDictAlt[i] = new ItemStack[] { wand.copy() };
                                withAlt.mOreDictIds[i] = -1;
                            }
                            break;
                        }
                    }
                }
                result.mOutputs = new ItemStack[] { output };
                dynamicWandRecipes.add(result);
                return result;
            }

            private GTRecipe prepareDynamicInfusionOutput(GTRecipe recipe,
                AbstractSanctumRecipes.DynamicInfusionOutput dynamicOutput) {
                if (recipe.mInputs == null || recipe.mInputs.length == 0) return null;
                ItemStack placeholder = recipe.mInputs[0];
                if (placeholder == null) return null;
                ItemStack actualCenter = null;
                for (ItemStack input : inputItems) {
                    if (input != null && input.getItem() == placeholder.getItem()) {
                        actualCenter = input;
                        break;
                    }
                }
                if (actualCenter == null) return null;

                ItemStack output = dynamicOutput.resolve(actualCenter);
                if (output == null) return null;

                GTRecipe result = recipe.copy();
                result.mCanBeBuffered = false;
                result.mInputs[0] = actualCenter.copy();
                if (result instanceof GTRecipe.GTRecipe_WithAlt withAlt && withAlt.mOreDictAlt != null
                    && withAlt.mOreDictAlt.length > 0) {
                    withAlt.mOreDictAlt[0] = new ItemStack[] { actualCenter.copy() };
                    withAlt.mOreDictIds[0] = -1;
                }
                result.mOutputs = new ItemStack[] { output };
                if (dynamicOutput.isRevolverRecipe()) {
                    OTHTechnology.LOG.info(
                        "Revolver infusion output resolved from actual input: input={}, output={}",
                        actualCenter,
                        output);
                }
                return result;
            }

            private ItemStack findEnchantmentTarget(GTRecipe recipe, Enchantment enchantment) {
                List<ItemStack> pool = new ArrayList<>();
                for (ItemStack input : inputItems) {
                    if (input != null) pool.add(input.copy());
                }

                if (recipe.mInputs != null) {
                    for (int i = 1; i < recipe.mInputs.length; i++) {
                        removeMatchingInput(pool, recipe.mInputs[i]);
                    }
                }

                for (ItemStack candidate : pool) {
                    try {
                        if (candidate.getItem()
                            .isItemTool(candidate) && enchantment.canApply(candidate)
                            && EnchantmentHelper.getEnchantmentLevel(enchantment.effectId, candidate)
                                < enchantment.getMaxLevel()) {
                            return candidate;
                        }
                    } catch (RuntimeException ignored) {
                        // Modded items can throw on invalid metadata; try the next remaining input.
                    }
                }
                return null;
            }

            private void removeMatchingInput(List<ItemStack> pool, ItemStack expected) {
                if (expected == null) return;
                int remaining = expected.stackSize;
                for (int i = 0; i < pool.size() && remaining > 0; i++) {
                    ItemStack candidate = pool.get(i);
                    if (!GTUtility.areStacksEqual(candidate, expected, true)) continue;
                    int removed = Math.min(remaining, candidate.stackSize);
                    candidate.stackSize -= removed;
                    remaining -= removed;
                    if (candidate.stackSize <= 0) {
                        pool.remove(i);
                        i--;
                    }
                }
            }

            @NotNull
            @Override
            protected ParallelHelper createParallelHelper(@NotNull GTRecipe recipe) {
                boolean dynamicWand = dynamicWandRecipes.remove(recipe);
                GTRecipe effectiveRecipe = scaleRecipeCosts(recipe);
                if (machineMode == 1) {
                    effectiveRecipe = hideDuplicateCatalysts(effectiveRecipe);
                }
                ParallelHelper helper = super.createParallelHelper(effectiveRecipe)
                    .setMaxParallel(getMaxParallelForOverclocks(calculateRecipeOverclocks(effectiveRecipe)))
                    .setConsumption(true);
                if (dynamicWand) {
                    double vanillaParallel = effectiveRecipe.maxParallelCalculatedByInputs(1, inputFluids, inputItems);
                    if (vanillaParallel <= 0 && !wandFallbackLogged) {
                        wandFallbackLogged = true;
                        OTHTechnology.LOG.warn(
                            "Wand augmentation inputs matched, but GT calculated 0 parallel; using the validated single-run path. inputs={}",
                            Arrays.toString(effectiveRecipe.mInputs));
                    }
                    helper.setMaxParallel(1)
                        .setMaxParallelCalculator(
                            (effective, maxParallel, fluids,
                                items) -> hasItemRequirements(effective, items)
                                    && hasFluidRequirements(effective, fluids) ? 1 : 0)
                        .setInputConsumer(OTEAbstractSanctum::consumeWandAugmentationInputs);
                }
                if (machineMode == 1) helper.setRecipeLocked(null, false);
                return helper;
            }

            private int calculateRecipeOverclocks(GTRecipe recipe) {
                try {
                    OverclockCalculator calculator = createOverclockCalculator(recipe).calculate();
                    return calculator.getPerformedOverclocks();
                } catch (RuntimeException ignored) {
                    return overclocksThisCheck;
                }
            }
        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    @Override
    public gregtech.api.recipe.check.CheckRecipeResult checkProcessing() {
        int energyTier = GTUtility.getTier(getMaxInputVoltage());
        overclocksThisCheck = Math.max(0, energyTier - VoltageIndex.LuV);
        oganessonActiveThisCheck = hasOganesson();
        oganessonParallelThisCheck = machineMode == 1 ? DUPLICATE_BASE_PARALLEL : 4;
        alwaysMaxParallel = true;
        powerPanelMaxParallel = Math.max(1, getMaxParallelRecipes());

        gregtech.api.recipe.check.CheckRecipeResult result = super.checkProcessing();
        if (result.wasSuccessful()) {
            mMaxProgresstime = Math.max(1, mMaxProgresstime);
            if (oganessonActiveThisCheck) {
                FluidStack gas = AbstractSanctumContent.getOganessonStack(OGANESSON_PER_RUN);
                if (gas != null) depleteInput(gas);
            }
        }
        return result;
    }

    // ===================== Structure =====================

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        readTopGlassTier(aBaseMetaTileEntity);
        if (!checkPiece(
            STRUCTURE_PIECE_MAIN,
            CONTROLLER_HORIZONTAL_OFFSET,
            CONTROLLER_VERTICAL_OFFSET,
            CONTROLLER_DEPTH_OFFSET,
            errors)) return;
        for (Object hatch : getExoticEnergyHatches()) {
            if (!(hatch instanceof IMetaTileEntity meta)) continue;
            if (meta instanceof MTEHatchWirelessMulti) {
                errors.add(StructureErrors.glassTierNotEnough(VoltageIndex.UIV));
                return;
            }
            int requiredGlassTier = requiredGlassTierFor(meta);
            if (requiredGlassTier >= 0 && glassTier < requiredGlassTier) {
                errors.add(StructureErrors.glassTierNotEnough(requiredGlassTier));
                return;
            }
        }
        repairMachine();
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            hintsOnly,
            CONTROLLER_HORIZONTAL_OFFSET,
            CONTROLLER_VERTICAL_OFFSET,
            CONTROLLER_DEPTH_OFFSET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        if (isPreviewWorld()) {
            IGregTechTileEntity base = getBaseMetaTileEntity();
            World world = base.getWorld();
            int[] anchorOffset = new int[3];
            getExtendedFacing().getWorldOffset(
                new int[] { CONTROLLER_HORIZONTAL_OFFSET, CONTROLLER_VERTICAL_OFFSET + 1, CONTROLLER_DEPTH_OFFSET },
                anchorOffset);
            int anchorX = base.getXCoord() + anchorOffset[0];
            int anchorY = base.getYCoord() + anchorOffset[1];
            int anchorZ = base.getZCoord() + anchorOffset[2];
            // BlockRenderer keeps the same dummy world but clears it whenever a slider changes.
            // Rebuild only when the fixed anchor is missing, so repeated build rounds stop.
            if (world.isAirBlock(anchorX, anchorY, anchorZ)) {
                buildPiece(
                    STRUCTURE_PIECE_MAIN,
                    stackSize,
                    false,
                    CONTROLLER_HORIZONTAL_OFFSET,
                    CONTROLLER_VERTICAL_OFFSET,
                    CONTROLLER_DEPTH_OFFSET);
            }
            return 0;
        }
        return survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            CONTROLLER_HORIZONTAL_OFFSET,
            CONTROLLER_VERTICAL_OFFSET,
            CONTROLLER_DEPTH_OFFSET,
            Math.max(1, elementBudget),
            env,
            false,
            true);
    }

    @Override
    public IStructureDefinition<OTEAbstractSanctum> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTEAbstractSanctum>builder()
                .addShape(STRUCTURE_PIECE_MAIN, shapeMain)
                // A：玻璃。
                .addElement(
                    'A',
                    chainAllGlasses(-1, (machine, tier) -> machine.glassTier = tier, machine -> machine.glassTier))
                // B：血腥灵宝块。
                .addElement('B', ofBlock(BlockList.BloodyIchorium.block, 0))
                // C：盖亚之魂块。
                .addElement('C', ofBlock(BlockList.Gaia.block, 0))
                // D-H：神秘时代蜡烛、透明装饰方块与奥术石。
                .addElement('D', ofBlock(ConfigBlocks.blockCandle, 4))
                .addElement('E', ofBlock(ConfigBlocks.blockCosmeticOpaque, 0))
                .addElement('F', ofBlock(ConfigBlocks.blockCosmeticOpaque, 1))
                .addElement('G', ofBlock(ConfigBlocks.blockCosmeticSolid, 6))
                .addElement('H', ofBlock(ConfigBlocks.blockCosmeticSolid, 7))
                // I/J、L、S、T、V：血魔法符文与仪式石。
                .addElement('I', ofBlock(WayofTime.alchemicalWizardry.ModBlocks.bloodRune, 0))
                .addElement('J', ofBlock(WayofTime.alchemicalWizardry.ModBlocks.bloodRune, 4))
                // K：原黄色染色陶瓦位置，替换为黄桃罐头机械方块，不参与仓室替换。
                .addElement('K', ofBlock(NailongCasings.nailongCasing, 1))
                .addElement('L', ofBlock(WayofTime.alchemicalWizardry.ModBlocks.efficiencyRune, 0))
                // M-R：原版石英及植物魔法特殊石英。
                .addElement('M', ofBlock(Blocks.quartz_block, 0))
                .addElement('N', ofBlock(ModFluffBlocks.darkQuartzSlab, 0))
                .addElement('O', ofBlock(ModFluffBlocks.sunnyQuartzSlab, 0))
                .addElement('P', ofBlock(ModFluffBlocks.elfQuartz, 0))
                .addElement('Q', ofBlock(ModFluffBlocks.sunnyQuartz, 0))
                .addElement('R', ofBlock(ModFluffBlocks.sunnyQuartz, 1))
                .addElement('S', ofBlock(WayofTime.alchemicalWizardry.ModBlocks.ritualStone, 0))
                .addElement('T', ofBlock(WayofTime.alchemicalWizardry.ModBlocks.runeOfSelfSacrifice, 0))
                // U：原砂岩位置，替换为奈隆机械方块，也允许放置仓室。
                .addElement('U', nailongCasingWithHatches(0))
                .addElement('V', ofBlock(WayofTime.alchemicalWizardry.ModBlocks.speedRune, 0))
                // W：神秘基础学虚空方块。
                .addElement(
                    'W',
                    ThaumicBases.isModLoaded() ? ofBlockUnlocalizedName("thaumicbases", "voidBlock", 0)
                        : ofBlock(Blocks.stone, 0))
                // X/Z：神秘时代魔杖基座和普通基座。
                .addElement('X', ofBlock(ConfigBlocks.blockStoneDevice, 5))
                // Y：原 GT 仓室位置；允许仓室，也可用奈隆机械方块。
                .addElement('Y', nailongCasingWithHatches(0))
                .addElement('Z', ofBlock(ConfigBlocks.blockStoneDevice, 1))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        IMetaTileEntity meta = aTileEntity == null ? null : aTileEntity.getMetaTileEntity();
        if (meta instanceof MTEHatchWirelessMulti) return false;
        int requiredGlassTier = requiredGlassTierFor(meta);
        if (requiredGlassTier >= 0 && glassTier < requiredGlassTier) return false;
        return super.addToMachineList(aTileEntity, aBaseCasingIndex);
    }

    // ===================== Tooltip =====================

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(translateToLocal("ote.abstract_sanctum.tooltip.0"))
            .addInfo(translateToLocal("ote.abstract_sanctum.tooltip.1"))
            .addInfo(translateToLocal("ote.abstract_sanctum.tooltip.2"))
            .addInfo(translateToLocal("ote.abstract_sanctum.tooltip.3"))
            .addInfo(translateToLocal("ote.abstract_sanctum.tooltip.4"))
            .addInfo(translateToLocal("ote.abstract_sanctum.tooltip.5"))
            .addInfo(translateToLocal("ote.abstract_sanctum.tooltip.6"))
            .addInfo(translateToLocal("ote.abstract_sanctum.tooltip.7"))
            .addInfo(translateToLocal("ote.abstract_sanctum.tooltip.8"))
            .addInfo(translateToLocal("ote.abstract_sanctum.tooltip.9"))
            .addInfo(translateToLocal("ote.abstract_sanctum.tooltip.10"))
            .addInfo(translateToLocal("ote.abstract_sanctum.tooltip.11"))
            .addInfo(translateToLocal("ote.abstract_sanctum.tooltip.12"))
            .addInfo(translateToLocal("ote.abstract_sanctum.tooltip.13"))
            .addInfo(translateToLocal("ote.abstract_sanctum.tooltip.14"))
            .addInfo(translateToLocal("ote.abstract_sanctum.tooltip.15"))
            .addInfo(translateToLocal("ote.abstract_sanctum.tooltip.16"))
            .addInfo(translateToLocal("ote.abstract_sanctum.tooltip.17"))
            .addInfo(translateToLocal("ote.abstract_sanctum.tooltip.18"))
            .addInfo(translateToLocal("ote.abstract_sanctum.tooltip.20"))
            .addInfo(translateToLocal("ote.abstract_sanctum.tooltip.21"))
            .addInfo(translateToLocal("ote.abstract_sanctum.tooltip.19"))
            .addSeparator()
            .addInfo("§aAdded by: 123Technology - AbstractSanctum")

            .addController(translateToLocal("ote.tn.abstract_sanctum"))
            .beginStructureBlock(3, 3, 3, false)
            .addInputBus("Input Bus", 1)
            .addOutputBus("Output Bus", 1)
            .addInputHatch("Fluid Input Hatch", 1)
            .addEnergyHatch("Energy Hatch", 1)
            .toolTipFinisher();
        return tt;
    }

    // ===================== Standard Overrides =====================

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTEAbstractSanctum(mName);
    }

    // ===================== Textures =====================

    @Override
    public ITexture[] getTexture(IGregTechTileEntity baseMetaTileEntity, ForgeDirection sideDirection,
        ForgeDirection facing, int colorIndex, boolean active, boolean redstone) {
        if (sideDirection == facing) {
            if (active) return new ITexture[] {
                Textures.BlockIcons.getCasingTextureForId(NailongCasings.nailongCasing.getTextureIndex(0)),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] {
                Textures.BlockIcons.getCasingTextureForId(NailongCasings.nailongCasing.getTextureIndex(0)),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] {
            Textures.BlockIcons.getCasingTextureForId(NailongCasings.nailongCasing.getTextureIndex(0)) };
    }

    // ===================== Misc =====================

    private int getMaxParallelForOverclocks(int overclockCount) {
        long parallel;
        if (machineMode == 1) {
            int shift = Math.min(Math.max(0, overclockCount), 24);
            parallel = (long) DUPLICATE_BASE_PARALLEL << shift;
        } else {
            parallel = 1L + 2L * Math.max(0, overclockCount);
        }
        parallel += oganessonActiveThisCheck ? oganessonParallelThisCheck : 0;
        return (int) Math.min(Integer.MAX_VALUE, Math.max(1L, parallel));
    }

    private boolean hasOganesson() {
        FluidStack required = AbstractSanctumContent.getOganessonStack(OGANESSON_PER_RUN);
        if (required == null) return false;
        long stored = 0;
        for (FluidStack stack : getStoredFluidsWithDualInput()) {
            if (stack != null && stack.isFluidEqual(required)) stored += stack.amount;
        }
        return stored >= required.amount;
    }

    private GTRecipe scaleRecipeCosts(GTRecipe source) {
        GTRecipe recipe = source.copy();
        if (recipe.mFluidInputs != null) {
            FluidStack[] fluids = new FluidStack[recipe.mFluidInputs.length];
            for (int i = 0; i < fluids.length; i++) {
                FluidStack input = recipe.mFluidInputs[i];
                if (input == null) continue;
                fluids[i] = scaleFluidCost(input);
            }
            recipe.mFluidInputs = fluids;
            if (recipe.mAltFluidInputs != null) {
                FluidStack[][] altFluids = new FluidStack[recipe.mAltFluidInputs.length][];
                for (int i = 0; i < altFluids.length; i++) {
                    FluidStack[] alternatives = recipe.mAltFluidInputs[i];
                    if (alternatives == null) continue;
                    altFluids[i] = new FluidStack[alternatives.length];
                    for (int j = 0; j < alternatives.length; j++) {
                        FluidStack alternative = alternatives[j];
                        if (alternative != null) altFluids[i][j] = scaleFluidCost(alternative);
                    }
                }
                recipe.mAltFluidInputs = altFluids;
            }
        }

        if (recipe.mInputs != null) {
            List<ItemStack> items = new ArrayList<>();
            List<Integer> sourceIndexes = new ArrayList<>();
            boolean ignoreSalt = isUpgradeInstalled(SALT_UPGRADE_SLOT);
            int resourceSaltIndex = findResourceSaltIndex(recipe);
            for (int i = 0; i < recipe.mInputs.length; i++) {
                ItemStack input = recipe.mInputs[i];
                if (input == null) continue;
                if (ignoreSalt && i == resourceSaltIndex) continue;

                ItemStack scaled = input.copy();
                if (AbstractSanctumRecipes.isNonConsumedInfusionCatalyst(input)) {
                    scaled.stackSize = 0;
                } else if (i == resourceSaltIndex) {
                    scaled.stackSize = Math.max(1, (int) Math.floor(input.stackSize * saltMultiplier));
                }
                items.add(scaled);
                sourceIndexes.add(i);
            }
            replaceItemInputs(
                recipe,
                items.toArray(new ItemStack[0]),
                sourceIndexes.stream()
                    .mapToInt(Integer::intValue)
                    .toArray());
        }
        return recipe;
    }

    private static int findResourceSaltIndex(GTRecipe recipe) {
        if (recipe.mInputs == null) return -1;
        for (int i = recipe.mInputs.length - 1; i >= 0; i--) {
            if (AbstractSanctumRecipes.isWorldSalt(recipe.mInputs[i])) return i;
        }
        return -1;
    }

    private FluidStack scaleFluidCost(FluidStack input) {
        String name = input.getFluid()
            .getName();
        boolean mana = isManaFluid(input);
        if (mana && isUpgradeInstalled(MANA_UPGRADE_SLOT)) {
            FluidStack free = input.copy();
            free.amount = 0;
            return free;
        }
        if (AbstractSanctumContent.BLOOD_FLUID.equalsIgnoreCase(name) && isUpgradeInstalled(BLOOD_UPGRADE_SLOT)) {
            FluidStack free = input.copy();
            free.amount = 0;
            return free;
        }

        double multiplier = 1.0D;
        if (mana) multiplier = manaMultiplier;
        else if (AbstractSanctumContent.BLOOD_FLUID.equalsIgnoreCase(name)) multiplier = bloodMultiplier;
        FluidStack scaled = input.copy();
        scaled.amount = Math.max(1, (int) Math.floor(input.amount * multiplier));
        return scaled;
    }

    private static GTRecipe hideDuplicateCatalysts(GTRecipe source) {
        if (source.mInputs == null || source.mInputs.length == 0) return source;

        GTRecipe recipe = source.copy();
        boolean duplicateWorldSalt = hasIntegratedCircuit(recipe, 24);
        List<ItemStack> keptInputs = new ArrayList<>();
        List<Integer> sourceIndexes = new ArrayList<>();
        for (int i = 0; i < recipe.mInputs.length; i++) {
            ItemStack input = recipe.mInputs[i];
            if (input == null) continue;
            boolean keep = GTUtility.isAnyIntegratedCircuit(input)
                || (!duplicateWorldSalt && AbstractSanctumRecipes.isWorldSalt(input));
            if (!keep) continue;

            keptInputs.add(input.copy());
            sourceIndexes.add(i);
        }
        replaceItemInputs(
            recipe,
            keptInputs.toArray(new ItemStack[0]),
            sourceIndexes.stream()
                .mapToInt(Integer::intValue)
                .toArray());
        return recipe;
    }

    private static void replaceItemInputs(GTRecipe recipe, ItemStack[] inputs, int[] sourceIndexes) {
        int[] inputChances = null;
        if (recipe.mInputChances != null) {
            inputChances = new int[sourceIndexes.length];
            for (int i = 0; i < sourceIndexes.length; i++) {
                inputChances[i] = recipe.getInputChance(sourceIndexes[i]);
            }
        }

        ItemStack[][] alternatives = null;
        int[] oreDictIds = null;
        if (recipe instanceof GTRecipe.GTRecipe_WithAlt withAlt) {
            if (withAlt.mOreDictAlt != null) {
                alternatives = new ItemStack[sourceIndexes.length][];
                for (int i = 0; i < sourceIndexes.length; i++) {
                    int sourceIndex = sourceIndexes[i];
                    if (sourceIndex < withAlt.mOreDictAlt.length) {
                        alternatives[i] = withAlt.mOreDictAlt[sourceIndex];
                    }
                }
            }
            if (withAlt.mOreDictIds != null) {
                oreDictIds = new int[sourceIndexes.length];
                for (int i = 0; i < sourceIndexes.length; i++) {
                    int sourceIndex = sourceIndexes[i];
                    oreDictIds[i] = sourceIndex < withAlt.mOreDictIds.length ? withAlt.mOreDictIds[sourceIndex] : -1;
                }
            }
        }

        recipe.mInputs = inputs;
        recipe.mInputChances = inputChances;
        if (recipe instanceof GTRecipe.GTRecipe_WithAlt withAlt) {
            withAlt.mOreDictAlt = alternatives;
            withAlt.mOreDictIds = oreDictIds;
        }
    }

    private static boolean hasIntegratedCircuit(GTRecipe recipe, int circuit) {
        if (recipe.mInputs == null) return false;
        for (ItemStack input : recipe.mInputs) {
            if (input != null && GTUtility.isAnyIntegratedCircuit(input) && input.getItemDamage() == circuit)
                return true;
        }
        return false;
    }

    private static FluidStack[] appendVirtualFluid(FluidStack[] inputs, FluidStack virtualFluid) {
        if (virtualFluid == null) return inputs;
        FluidStack[] result = Arrays.copyOf(inputs, inputs.length + 1);
        result[result.length - 1] = virtualFluid;
        return result;
    }

    private static void consumeWandAugmentationInputs(GTRecipe recipe, int multiplier, FluidStack[] inputFluids,
        ItemStack[] inputItems) {
        if (multiplier <= 0) return;
        if (recipe.mInputs != null) {
            for (ItemStack required : recipe.mInputs) {
                if (required == null || required.stackSize <= 0
                    || AbstractSanctumRecipes.isNonConsumedInfusionCatalyst(required)) {
                    continue;
                }
                int remaining = required.stackSize * multiplier;
                for (ItemStack available : inputItems) {
                    if (remaining <= 0) break;
                    if (available == null || available.stackSize <= 0 || !matchesRequiredItem(available, required)) {
                        continue;
                    }
                    int consumed = Math.min(remaining, available.stackSize);
                    available.stackSize -= consumed;
                    remaining -= consumed;
                }
            }
        }
        if (recipe.mFluidInputs != null) {
            for (FluidStack required : recipe.mFluidInputs) {
                if (required == null || required.amount <= 0) continue;
                long remaining = (long) required.amount * multiplier;
                for (FluidStack available : inputFluids) {
                    if (remaining <= 0) break;
                    if (available == null || available.amount <= 0 || !available.isFluidEqual(required)) continue;
                    int consumed = (int) Math.min(remaining, available.amount);
                    available.amount -= consumed;
                    remaining -= consumed;
                }
            }
        }
    }

    private static boolean matchesRequiredItem(ItemStack available, ItemStack required) {
        if (available == null || required == null) return false;
        if (available.getItem() instanceof ItemWandCasting && required.getItem() instanceof ItemWandCasting) {
            return true;
        }
        if (required.getItemDamage() == OreDictionary.WILDCARD_VALUE && available.getItem() == required.getItem()) {
            return true;
        }
        return GTUtility.areStacksEqual(available, required);
    }

    private ItemStack findInputWand() {
        if (processingLogic == null) return null;
        for (ItemStack stack : getStoredInputs()) {
            if (stack != null && stack.getItem() instanceof ItemWandCasting) return stack;
        }
        return null;
    }

    private static boolean containsAugmentation(NBTTagList augmentations, String augmentation) {
        for (int i = 0; i < augmentations.tagCount(); i++) {
            if (augmentation.equals(augmentations.getStringTagAt(i))) return true;
        }
        return false;
    }

    /** Liquid Mana is TST's fluid, so compare against the fluid instance instead of a name. */
    private static boolean isManaFluid(FluidStack input) {
        Fluid mana = AbstractSanctumContent.getManaFluid();
        return input != null && mana != null && input.getFluid() == mana;
    }

    public double getAccelerationFactor() {
        double integralMana = integrateQ(160.0D, 1.05D, manaMultiplier);
        double integralSalt = integrateQ(8.0D, 3.50D, saltMultiplier);
        double integralBlood = integrateQ(40.0D, 1.20D, bloodMultiplier);
        return Math.round((1.0D + 3.23D * (integralMana + integralSalt + integralBlood)) * 100.0D) / 100.0D;
    }

    private static double integrateQ(double k, double center, double value) {
        double t = Math.max(1.0D, value);
        double base = t - 1.0D;
        double logistic = (softplus(k * (t - center)) - softplus(k * (1.0D - center))) / k;
        return 0.01D * base + base - logistic;
    }

    private static double softplus(double value) {
        if (value > 0.0D) return value + Math.log1p(Math.exp(-value));
        return Math.log1p(Math.exp(value));
    }

    public int getMaxInputVoltageTier() {
        return GTUtility.getTier(getMaxInputVoltage());
    }

    public int getCurrentParallelCount() {
        return processingLogic == null ? 0 : processingLogic.getCurrentParallels();
    }

    public ItemStackHandler getUpgradeInventory() {
        return upgradeInventory;
    }

    private static double clampMultiplier(double value) {
        if (Double.isNaN(value) || value < 1.0D) return 1.0D;
        return Math.min(10.0D, Math.round(value * 10.0D) / 10.0D);
    }

    public double getManaMultiplier() {
        return manaMultiplier;
    }

    public double getSaltMultiplier() {
        return saltMultiplier;
    }

    public double getBloodMultiplier() {
        return bloodMultiplier;
    }

    public void setManaMultiplier(double value) {
        manaMultiplier = clampMultiplier(value);
    }

    public void setSaltMultiplier(double value) {
        saltMultiplier = clampMultiplier(value);
    }

    public void setBloodMultiplier(double value) {
        bloodMultiplier = clampMultiplier(value);
    }

    private boolean isUpgradeInstalled(int slot) {
        ItemStack stack = upgradeInventory.getStackInSlot(slot);
        return isUpgradeItem(slot, stack);
    }

    private boolean hasAnyUpgradeInstalled() {
        for (int slot = 0; slot < upgradeInventory.getSlots(); slot++) {
            if (isUpgradeInstalled(slot)) return true;
        }
        return false;
    }

    private static boolean isUpgradeItem(int slot, ItemStack stack) {
        if (stack == null || stack.getItem() == null) return false;
        if (slot == MANA_UPGRADE_SLOT) {
            return ModBlocks.pool != null && stack.getItem() == net.minecraft.item.Item.getItemFromBlock(ModBlocks.pool)
                && stack.getItemDamage() == 1;
        }
        if (slot == SALT_UPGRADE_SLOT) {
            return LudicrousItems.akashic_record != null && stack.getItem() == LudicrousItems.akashic_record;
        }
        if (slot == BLOOD_UPGRADE_SLOT) {
            return LudicrousItems.armok_orb != null && stack.getItem() == LudicrousItems.armok_orb;
        }
        return false;
    }

    private void readTopGlassTier(IGregTechTileEntity base) {
        glassTier = -1;
        if (base == null || base.getWorld() == null) return;

        // Relative ABC offset from the bottom-fifth-row controller to the center glass at
        // layer 25, row 25, column 21. Convert it to world coordinates so rotation works.
        int[] worldOffset = new int[3];
        getExtendedFacing().getWorldOffset(new int[] { 0, 2, 4 }, worldOffset);
        int x = base.getXCoord() + worldOffset[0];
        int y = base.getYCoord() + worldOffset[1];
        int z = base.getZCoord() + worldOffset[2];
        Block block = base.getWorld()
            .getBlock(x, y, z);
        Integer tier = GlassTier.getGlassBlockTier(
            block,
            base.getWorld()
                .getBlockMetadata(x, y, z));
        if (tier != null) glassTier = tier;
    }

    /** BlockRenderer6343 creates a preview world whose class name contains one of these markers. */
    private boolean isPreviewWorld() {
        IGregTechTileEntity base = getBaseMetaTileEntity();
        if (base == null || base.getWorld() == null) return false;
        String name = base.getWorld()
            .getClass()
            .getName();
        return name.contains("Dummy") || name.contains("Observer") || name.contains("Tracked");
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected void startActivitySound() {
        if (activitySoundLoop != null && activitySoundLoop.isDonePlaying()) {
            activitySoundLoop = null;
        }

        boolean active = getBaseMetaTileEntity().isActive() && !getBaseMetaTileEntity().isMuffled();
        if (active && activitySoundLoop == null) {
            ForcedCompletionSoundLoop sound = Config.is_Abstract_Sanctum_HAHAHAH
                ? new ForcedCompletionSoundLoop(
                    new ResourceLocation("123technology:machines.abstract_sanctum"),
                    getBaseMetaTileEntity())
                : new ForcedCompletionSoundLoop(
                    new ResourceLocation(GregTech + ":" + "machines.DistilleryLoop"),
                    getBaseMetaTileEntity());
            activitySoundLoop = sound;
            Minecraft.getMinecraft()
                .getSoundHandler()
                .playSound(sound);
        } else if (activitySoundLoop instanceof ForcedCompletionSoundLoop sound) {
            sound.setMachineActive(active);
        }
    }

    private static IStructureElement<OTEAbstractSanctum> nailongCasingWithHatches(int meta) {
        return buildHatchAdder(OTEAbstractSanctum.class)
            .atLeast(Energy.or(ExoticEnergy), InputBus, OutputBus, InputHatch)
            .adder(OTEAbstractSanctum::addToMachineList)
            .hint(1)
            .casingIndex(NailongCasings.nailongCasing.getTextureIndex(meta))
            .buildAndChain(ofBlock(NailongCasings.nailongCasing, meta));
    }

    private static Block findBlockByUnlocalizedName(String unlocalizedName) {
        for (Object entry : Block.blockRegistry) {
            if (entry instanceof Block block && unlocalizedName.equals(block.getUnlocalizedName())) {
                return block;
            }
        }
        throw new IllegalStateException("Missing required AbstractSanctum structure block: " + unlocalizedName);
    }

    private static int requiredGlassTierFor(IMetaTileEntity meta) {
        if (meta instanceof MTEHatchEnergyTunnel) return VoltageIndex.UIV;
        if (meta instanceof MTEHatchEnergyMulti) return VoltageIndex.ZPM;
        if (ExoticEnergyInputHelper.isExoticEnergyInput(meta)) return VoltageIndex.UIV;
        return -1;
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new OTEAbstractSanctumGui(this);
    }

    @Override
    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer player, float x, float y, float z,
        ItemStack tool) {
        if (!getBaseMetaTileEntity().isServerSide()) return;
        int previousMode = machineMode;
        setMachineMode(nextMachineMode());
        if (machineMode == previousMode) {
            GTUtility.sendChatTrans(player, "ote.abstract_sanctum.chat.mode_locked");
            return;
        }
        GTUtility.sendChatTrans(
            player,
            machineMode == 1 ? "ote.abstract_sanctum.chat.duplicate" : "ote.abstract_sanctum.chat.craft");
    }
}
