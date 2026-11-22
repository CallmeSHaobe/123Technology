package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static com.newmaa.othtech.common.compat.thaumcraft.VoidNexusUpgrade.*;
import static goodgenerator.loader.Loaders.gravityStabilizationCasing;
import static goodgenerator.loader.Loaders.magicCasing;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTUtility.filterValidMTEs;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import com.dreammaster.block.BlockList;
import com.google.common.io.ByteArrayDataInput;
import com.gtnewhorizon.structurelib.StructureLibAPI;
import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.AutoPlaceEnvironment;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.gtnewhorizon.structurelib.util.ItemStackPredicate.NBTMode;
import com.newmaa.othtech.OTHTechnology;
import com.newmaa.othtech.client.OTEVoidNexusClientEffects;
import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.blocks.nailong.NailongCasingMeta;
import com.newmaa.othtech.common.blocks.nailong.NailongCasings;
import com.newmaa.othtech.common.compat.thaumcraft.VoidNexusFuelProfiles;
import com.newmaa.othtech.common.compat.thaumcraft.VoidNexusUpgrade;
import com.newmaa.othtech.common.item.ItemLoader;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.machine.gui.OTEVoidNexusMatrixGui;
import com.newmaa.othtech.machine.hatch.OTEEssentiaStockingInputBusME;
import com.newmaa.othtech.machine.machineclass.OTHTTMultiMachineBaseEM;
import com.newmaa.othtech.machine.machineclass.voidnexus.PsionicOutputQueue;
import com.newmaa.othtech.machine.machineclass.voidnexus.VoidNexusCalculator;
import com.newmaa.othtech.machine.machineclass.voidnexus.VoidNexusControllerTextures;
import com.newmaa.othtech.machine.machineclass.voidnexus.VoidNexusEssentiaBuffer;
import com.newmaa.othtech.utils.ShapeLoader;

import appeng.api.networking.IGrid;
import appeng.me.GridAccessException;
import bartworks.common.loaders.ItemRegistry;
import bartworks.system.material.TileEntityMetaGeneratedBlock;
import bartworks.system.material.WerkstoffLoader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.interfaces.IOutputBus;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchDynamo;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrors;
import gregtech.api.util.ExoticEnergyInputHelper;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gregtech.common.misc.WirelessNetworkManager;
import gregtech.common.render.IMTERenderer;
import gregtech.common.tileentities.machines.MTEHatchInputBusME;
import gtPlusPlus.core.block.ModBlocks;
import io.netty.buffer.ByteBuf;
import kubatech.loaders.BlockLoader;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import tectech.thing.metaTileEntity.hatch.MTEHatchDynamoMulti;
import tectech.thing.metaTileEntity.hatch.MTEHatchDynamoTunnel;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketSyncWarp;
import thaumcraft.common.lib.network.playerdata.PacketWarpMessage;
import thaumicenergistics.common.storage.EnumEssentiaStorageTypes;

public class OTEVoidNexusMatrix extends OTHTTMultiMachineBaseEM
    implements IConstructable, ISurvivalConstructable, IMTERenderer {

    // ===================== Tuning Constants =====================

    private static final VoidNexusControllerTextures CONTROLLER_TEXTURES = VoidNexusControllerTextures.MATRIX;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final char CONTROLLER_HATCH_SYMBOL = 'X';
    private static final int CONTROLLER_HATCH_RADIUS = 6;
    private static final int MAX_STRUCTURE_TIER = 5;
    private static final int HORIZONTAL_OFFSET = 133;
    private static final int VERTICAL_OFFSET = 115;
    private static final int DEPTH_OFFSET = 129;
    private static final int VOID_NEXUS_TOOLTIP_LINES = 56;
    private static final int TAIRITSU_ADVANCED_CASING_META = 10106;
    private static final int METASTABLE_OGANESSON_ADVANCED_CASING_META = 10111;
    private static final String AVARITIA_COSMIC_NEUTRONIUM_MOLTEN_FLUID = "molten.cosmicneutronium";
    private static final char[] TIERED_SLOT_SYMBOLS = { 'C', 'E', 'G', 'H' };
    private static final int[] TIERED_SLOT_INDEXES = { 0, 2, 3, 4 };
    private static final int INPUT_BUS_MIN = 1;
    private static final int ROUND_TICKS = VoidNexusCalculator.BURN_TICKS;
    private static final int STAR_ROUND_TICKS = 72_000;
    private static final long UNIFIED_PARALLEL = 2_000_000L;
    private static final int BLOOD_PER_TICK = 1_440;
    private static final int MAX_WARP_RADIUS = 64;
    private static final long SOURCE_PER_POTENTIAL = 1_000_000_000L;
    public static final long MAX_VIRTUAL_POTENTIAL = 200L;
    private static final long MAX_PSIONIC_ACCELERATION = 1_000L;
    private static final long U26_INTERVAL_TICKS = 1_145L * 20L;
    private static final int U26_GAIN_PERCENT = 14;
    private static final int U26_MAX_PERCENT = 1_919_810;
    private static final int U26_LOSS_PERCENT_PER_10_SECONDS = 1_000;
    private static final int U26_DOWNTIME_INTERVAL_TICKS = 10 * 20;
    private static final byte WARP_PERMANENT = 0;
    private static final byte WARP_STICKY = 1;
    private static final byte WARP_TEMPORARY = 2;

    private static final CheckRecipeResult WAND_CHARGE_MISSING = SimpleCheckRecipeResult
        .ofFailurePersistOnShutdown("void_nexus_wand_charge");

    private static volatile IStructureDefinition<OTEVoidNexusMatrix> STRUCTURE_DEFINITION;
    private static volatile List<PreviewCell> PREVIEW_CELLS = Collections.emptyList();

    // ===================== Machine State =====================

    private VoidNexusCalculator.Stage stage = VoidNexusCalculator.Stage.UHV;
    private final VoidNexusEssentiaBuffer bufferedEssentia = new VoidNexusEssentiaBuffer();
    private int structureTier = 1;
    private long activePower;
    private long activeGeneratedPower;
    private long activePowerBeforeBonuses;
    private long lastConsumedEssentia;
    private double activeCatalystMultiplier = 1.0D;
    private boolean activeCatalystBlocked;
    private long roundCounter;
    private long continuousTicks;
    private long balancedTicks;
    private long virtualPotential;
    private long sourcePotentialRemainder;
    private long psionicAcceleration;
    private long u26UptimeTicks;
    private long u26DowntimeTicks;
    private int u26BonusPercent;
    private int modulePowerRotation;
    private long stargateCoreCheckTick = Long.MIN_VALUE;
    private boolean stargateCorePresent;
    private double lastDeviation;
    private double lastAverageHeat;
    private boolean activeBalanced;
    private boolean activeStarArray;
    private boolean runningTickSeen;
    private boolean wirelessMode;
    private boolean materialInputMode;
    private boolean voidAnimationEnabled = true;
    private boolean clientRenderActive;
    private boolean lastSentRenderActive;
    private boolean automaticPotentialOutput;
    private long purchasedUpgradeMask;
    private String upgradeStatusKey = "";
    private String potentialTransferStatusKey = "";
    private UUID ownerUUID;
    private final int[] upgradeVariants = new int[5];
    private final List<OTEPsionicManifestationModule> psionicModules = new ArrayList<>();

    // ===================== Input Consumption Types =====================

    private static final class StackConsumption {

        private final ItemStack stack;
        private final int amount;

        private StackConsumption(ItemStack stack, int amount) {
            this.stack = stack;
            this.amount = amount;
        }

        private void consume() {
            stack.stackSize -= amount;
        }
    }

    private final class CatalystPlan {

        private final List<FluidRequirement> fluids = new ArrayList<>();
        private final List<StackConsumption> items = new ArrayList<>();
        private boolean blocked;
        private double penaltyMultiplier = 1.0D;

        private boolean canRun() {
            return !blocked;
        }

        private void consume() {
            if (blocked) return;
            for (FluidRequirement requirement : fluids) requirement.consume(OTEVoidNexusMatrix.this);
            for (StackConsumption item : items) item.consume();
        }
    }

    private final class FluidRequirement {

        private final FluidStack fluid;
        private final long amount;
        private final boolean critical;
        private final double missingPenalty;

        private FluidRequirement(FluidStack fluid, long amount, boolean critical, double missingPenalty) {
            this.fluid = fluid;
            this.amount = amount;
            this.critical = critical;
            this.missingPenalty = missingPenalty;
        }

        private void consume(OTEVoidNexusMatrix machine) {
            machine.consumeFluidAmount(fluid, amount);
        }
    }

    // ===================== Constructors =====================

    public OTEVoidNexusMatrix(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        useLongPower = true;
    }

    public OTEVoidNexusMatrix(String aName) {
        super(aName);
        useLongPower = true;
    }

    // ===================== Standard Overrides =====================

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTEVoidNexusMatrix(mName);
    }

    // ===================== Recipe Map =====================

    @Override
    public RecipeMap<?> getRecipeMap() {
        return Recipemaps.VOID_NEXUS;
    }

    // ===================== NBT =====================

    @Override
    public void saveNBTData(NBTTagCompound tag) {
        bufferedEssentia.writeToNBT(tag);
        tag.setLong("voidRound", roundCounter);
        tag.setLong("voidRunTicks", continuousTicks);
        tag.setLong("voidBalancedTicks", balancedTicks);
        tag.setLong("voidPotential", virtualPotential);
        tag.setLong("voidSourceRemainder", sourcePotentialRemainder);
        tag.setLong("voidPsionicAcceleration", psionicAcceleration);
        tag.setLong("voidGeneratedPower", activeGeneratedPower);
        tag.setLong("voidBasePower", activePowerBeforeBonuses);
        tag.setLong("voidConsumedEssentia", lastConsumedEssentia);
        tag.setDouble("voidCatalystMultiplier", activeCatalystMultiplier);
        tag.setBoolean("voidCatalystBlocked", activeCatalystBlocked);
        tag.setBoolean("voidBalanced", activeBalanced);
        tag.setBoolean("voidStarArray", activeStarArray);
        tag.setDouble("voidDeviation", lastDeviation);
        tag.setDouble("voidAverageHeat", lastAverageHeat);
        tag.setLong("voidU26Uptime", u26UptimeTicks);
        tag.setLong("voidU26Downtime", u26DowntimeTicks);
        tag.setInteger("voidU26Bonus", u26BonusPercent);
        tag.setLong("voidPurchasedUpgrades", purchasedUpgradeMask);
        tag.setBoolean("voidWireless", wirelessMode);
        tag.setBoolean("voidMaterialInput", materialInputMode);
        tag.setBoolean("voidAnimationEnabled", voidAnimationEnabled);
        tag.setBoolean("automaticPotentialOutput", automaticPotentialOutput);
        super.saveNBTData(tag);
    }

    @Override
    public void setItemNBT(NBTTagCompound tag) {
        super.setItemNBT(tag);
        bufferedEssentia.writeToNBT(tag);
    }

    @Override
    public void loadNBTData(NBTTagCompound tag) {
        bufferedEssentia.readFromNBT(tag);
        roundCounter = tag.getLong("voidRound");
        continuousTicks = tag.getLong("voidRunTicks");
        balancedTicks = tag.getLong("voidBalancedTicks");
        virtualPotential = Math.max(0L, Math.min(MAX_VIRTUAL_POTENTIAL, tag.getLong("voidPotential")));
        sourcePotentialRemainder = tag.getLong("voidSourceRemainder");
        psionicAcceleration = Math.max(0L, Math.min(MAX_PSIONIC_ACCELERATION, tag.getLong("voidPsionicAcceleration")));
        activeGeneratedPower = Math.max(0L, tag.getLong("voidGeneratedPower"));
        activePowerBeforeBonuses = Math.max(0L, tag.getLong("voidBasePower"));
        lastConsumedEssentia = Math.max(0L, tag.getLong("voidConsumedEssentia"));
        activeCatalystMultiplier = tag.hasKey("voidCatalystMultiplier")
            ? Math.max(0.0D, Math.min(1.0D, tag.getDouble("voidCatalystMultiplier")))
            : 1.0D;
        activeCatalystBlocked = tag.getBoolean("voidCatalystBlocked");
        activeBalanced = tag.getBoolean("voidBalanced");
        activeStarArray = tag.getBoolean("voidStarArray");
        lastDeviation = Math.max(0.0D, Math.min(1.0D, tag.getDouble("voidDeviation")));
        lastAverageHeat = Math.max(0.0D, tag.getDouble("voidAverageHeat"));
        u26UptimeTicks = tag.getLong("voidU26Uptime");
        u26DowntimeTicks = tag.getLong("voidU26Downtime");
        u26BonusPercent = Math.max(0, Math.min(U26_MAX_PERCENT, tag.getInteger("voidU26Bonus")));
        purchasedUpgradeMask = tag.getLong("voidPurchasedUpgrades");
        wirelessMode = tag.getBoolean("voidWireless");
        materialInputMode = tag.getBoolean("voidMaterialInput");
        voidAnimationEnabled = !tag.hasKey("voidAnimationEnabled") || tag.getBoolean("voidAnimationEnabled");
        automaticPotentialOutput = tag.getBoolean("automaticPotentialOutput");
        stargateCorePresent = false;
        stargateCoreCheckTick = Long.MIN_VALUE;
        super.loadNBTData(tag);
    }

    // ===================== Lifecycle =====================

    @Override
    public void onFirstTick_EM(IGregTechTileEntity base) {
        super.onFirstTick_EM(base);
        ownerUUID = base.getOwnerUuid();
    }

    @Override
    public void onRemoval() {
        disconnectPsionicModules();
        super.onRemoval();
    }

    @Override
    public void onUnload() {
        disconnectPsionicModules();
        super.onUnload();
    }

    // ===================== Machine Parameters =====================

    @Override
    public int getMaxEfficiency(ItemStack stack) {
        return 10_000;
    }

    @Override
    public int getDamageToComponent(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean isCorrectMachinePart(ItemStack stack) {
        return true;
    }

    @Override
    public int getMaxParallelRecipes() {
        return (int) Math.min(Integer.MAX_VALUE, getMaxVoidParallel());
    }

    public long getMaxVoidParallel() {
        return UNIFIED_PARALLEL;
    }

    @Override
    public boolean supportsVoidProtection() {
        return false;
    }

    @Override
    public boolean supportsInputSeparation() {
        return false;
    }

    @Override
    public boolean supportsSingleRecipeLocking() {
        return false;
    }

    @Override
    public boolean supportsBatchMode() {
        return false;
    }

    // ===================== Processing Logic =====================

    @Override
    public @NotNull CheckRecipeResult checkProcessing_EM() {
        List<ItemStack> storedInputs = getStoredInputs();
        if (storedInputs == null) storedInputs = new ArrayList<>();
        stargateCorePresent = containsStargateCoreCrystal(storedInputs);
        stargateCoreCheckTick = getVoidWorldTime();

        boolean hasPsionicStabilizer = false;
        ItemStack stabilizerStack = null;
        ItemStack starArrayStack = null;
        List<StackConsumption> fuelStacks = new ArrayList<>();

        for (ItemStack stack : storedInputs) {
            if (stack == null || stack.getItem() == null || stack.stackSize <= 0) continue;

            if (isPsionicStabilizer(stack)) {
                hasPsionicStabilizer = true;
                stabilizerStack = stack;
                continue;
            }
            if (isStarArray(stack) && hasUpgrade(END_OF_REINCARNATION)) {
                starArrayStack = stack;
            }
        }

        boolean starArray = starArrayStack != null;
        int duration = starArray ? STAR_ROUND_TICKS : ROUND_TICKS;
        if (!canUseHostWand(duration)) {
            stopRoundBeforeStart();
            return WAND_CHARGE_MISSING;
        }

        Map<Aspect, Long> fuelAmounts = new LinkedHashMap<>();
        long remainingParallel = getMaxVoidParallel();
        boolean useMaterials = isMaterialInputModeEnabled();
        List<OTEEssentiaStockingInputBusME> essentiaBusses = useMaterials ? Collections.emptyList()
            : getEssentiaBusses();
        if (useMaterials) {
            ItemStack catalystCell = stage.index >= VoidNexusCalculator.Stage.UMV.index
                ? EnumEssentiaStorageTypes.Type_Creative.getCell()
                : null;
            for (ItemStack stack : storedInputs) {
                if (stack == null || stack.getItem() == null
                    || stack.stackSize <= 0
                    || isPsionicStabilizer(stack)
                    || isVoidPotential(stack)
                    || isStarArray(stack)
                    || isStargateCoreCrystal(stack)
                    || (catalystCell != null && stack.isItemEqual(catalystCell))
                    || remainingParallel <= 0L) continue;
                AspectList aspects = ThaumcraftApiHelper.getObjectAspects(stack);
                if (aspects == null || aspects.size() <= 0) continue;
                int usedItems = (int) Math.min(stack.stackSize, remainingParallel);
                boolean added = false;
                for (Aspect aspect : aspects.getAspects()) {
                    int perItem = aspects.getAmount(aspect);
                    if (aspect == null || perItem <= 0) continue;
                    long total = (long) perItem * usedItems;
                    fuelAmounts.merge(aspect, total, OTEVoidNexusMatrix::saturatingAdd);
                    added = true;
                }
                if (added) {
                    fuelStacks.add(new StackConsumption(stack, usedItems));
                    remainingParallel -= usedItems;
                }
            }
        } else {
            remainingParallel -= bufferedEssentia.addToPlan(fuelAmounts, remainingParallel);
            OTEEssentiaStockingInputBusME.collectAvailableEssentia(essentiaBusses, fuelAmounts, remainingParallel);
        }

        boolean idleMode = fuelAmounts.isEmpty() && hasUpgrade(THRONE_OF_EMPTINESS)
            && stage.index >= VoidNexusCalculator.Stage.UMV.index;
        if (fuelAmounts.isEmpty() && !idleMode) {
            stopRoundBeforeStart();
            return CheckRecipeResultRegistry.NO_FUEL_FOUND;
        }

        boolean bloodApplied = !fuelAmounts.isEmpty() && hasBloodForRound(duration);
        List<VoidNexusCalculator.Fuel> fuels = new ArrayList<>(fuelAmounts.size());
        for (Map.Entry<Aspect, Long> entry : fuelAmounts.entrySet()) {
            fuels.add(new VoidNexusCalculator.Fuel(entry.getKey(), entry.getValue()));
        }

        long nextRound = roundCounter == Long.MAX_VALUE ? Long.MAX_VALUE : roundCounter + 1L;
        VoidNexusCalculator.Config config = buildCalculatorConfig(
            starArray,
            hasPsionicStabilizer,
            bloodApplied,
            hasUpgrade(HOLLOW_POINT) && positiveModulo(nextRound, 3L) == 0L);

        VoidNexusCalculator.Result result = idleMode ? createIdleResult()
            : VoidNexusCalculator.calculate(stage, fuels, config);
        CatalystPlan catalystPlan = buildCatalystPlan(result.totalEssentia, storedInputs);
        activePowerBeforeBonuses = result.power;
        activeCatalystBlocked = !catalystPlan.canRun();
        activeCatalystMultiplier = activeCatalystBlocked ? 0.0D : catalystPlan.penaltyMultiplier;
        applyCatalystPenalties(result, catalystPlan);
        if (result.power <= 0L) {
            stopRoundBeforeStart();
            return CheckRecipeResultRegistry.NO_FUEL_FOUND;
        }

        if (!useMaterials && !bufferedEssentia.pay(
            fuelAmounts,
            (aspect, amount) -> OTEEssentiaStockingInputBusME.extractEssentia(essentiaBusses, aspect, amount))) {
            markDirty();
            stopRoundBeforeStart();
            return CheckRecipeResultRegistry.NO_FUEL_FOUND;
        }

        roundCounter = nextRound;
        lastConsumedEssentia = result.totalEssentia;
        if (bloodApplied) consumeBloodForRound(duration);
        catalystPlan.consume();
        for (StackConsumption fuel : fuelStacks) fuel.consume();
        if (stabilizerStack != null) stabilizerStack.stackSize--;
        if (starArrayStack != null) starArrayStack.stackSize--;
        creditSourcePotential(result.totalEssentia, starArray);

        activeGeneratedPower = calculateCurrentPower(activePowerBeforeBonuses);
        activePower = activeGeneratedPower;
        activeBalanced = starArray || result.isBalanced();
        activeStarArray = starArray;
        lastDeviation = result.deviation;
        lastAverageHeat = result.averageHeat;
        lEUt = activePower;
        mEUt = 0;
        mMaxProgresstime = duration;
        mEfficiency = 10_000;
        updateSlots();
        markDirty();
        return CheckRecipeResultRegistry.GENERATING;
    }

    // ===================== Runtime Ticks and Bonuses =====================

    @Override
    public boolean onRunningTick(ItemStack stack) {
        if (getBaseMetaTileEntity() == null || !getBaseMetaTileEntity().isServerSide()) return true;

        if (!consumeHostWandVisPerTick()) {
            runningTickSeen = false;
            activeGeneratedPower = 0L;
            activePower = 0L;
            return false;
        }
        runningTickSeen = true;
        refreshStargateCoreState();
        accumulateStargateCoreUptime();
        accumulateRuntimeBonuses();
        activeGeneratedPower = calculateCurrentPower(activePowerBeforeBonuses);
        activePower = activeGeneratedPower;
        if (activePower > 0L) {
            if (wirelessMode && hasUpgrade(ENTER_THE_VOID) && ownerUUID != null) {
                WirelessNetworkManager.addEUToGlobalEnergyMap(ownerUUID, BigInteger.valueOf(activePower));
            } else {
                addEnergyOutput_EM(activePower, 1);
            }
        }
        return true;
    }

    @Override
    public void onPostTick(IGregTechTileEntity base, long tick) {
        super.onPostTick(base, tick);
        if (!base.isServerSide()) return;

        allocatePsionicModulePower();
        if (automaticPotentialOutput && virtualPotential > 0L) withdrawPotentialToOutputBus();
        boolean renderActive = isRenderActiveOnServer();
        if (renderActive != lastSentRenderActive || tick % 20L == 0L) {
            lastSentRenderActive = renderActive;
            sendRenderDataToClient(this);
        }
        refreshStargateCoreState();
        boolean activeCore = hasUpgrade(WHISPER_OF_VOID) && hasStargateCoreCrystal();
        if (!activeCore) {
            u26UptimeTicks = 0L;
            u26DowntimeTicks = 0L;
            u26BonusPercent = 0;
            runningTickSeen = false;
            return;
        }

        if (runningTickSeen) {
            u26DowntimeTicks = 0L;
        } else {
            u26DowntimeTicks++;
            if (u26DowntimeTicks >= U26_DOWNTIME_INTERVAL_TICKS) {
                u26DowntimeTicks -= U26_DOWNTIME_INTERVAL_TICKS;
                u26BonusPercent = Math.max(0, u26BonusPercent - U26_LOSS_PERCENT_PER_10_SECONDS);
            }
        }
        runningTickSeen = false;
    }

    private void accumulateStargateCoreUptime() {
        if (!hasUpgrade(WHISPER_OF_VOID) || !hasStargateCoreCrystal() || u26BonusPercent >= U26_MAX_PERCENT) return;
        u26UptimeTicks++;
        while (u26UptimeTicks >= U26_INTERVAL_TICKS && u26BonusPercent < U26_MAX_PERCENT) {
            u26UptimeTicks -= U26_INTERVAL_TICKS;
            u26BonusPercent = Math.min(U26_MAX_PERCENT, u26BonusPercent + U26_GAIN_PERCENT);
        }
        if (u26BonusPercent >= U26_MAX_PERCENT) {
            u26UptimeTicks = Math.min(u26UptimeTicks, U26_INTERVAL_TICKS - 1L);
        }
    }

    private long calculateCurrentPower(long basePower) {
        if (activeCatalystBlocked || basePower <= 0L) return 0L;
        double power = basePower;
        if (hasUpgrade(WHISPER_OF_VOID) && hasStargateCoreCrystal() && u26BonusPercent > 0) {
            power *= 1.0D + u26BonusPercent / 100.0D;
        }
        // Catalyst penalties are intentionally the final multiplier in the calculation chain.
        power *= activeCatalystMultiplier;
        return (long) Math.min(Long.MAX_VALUE, Math.floor(power));
    }

    private void accumulateRuntimeBonuses() {
        if (continuousTicks < Long.MAX_VALUE - 1L) continuousTicks++;

        if (activeBalanced) {
            if (balancedTicks < Long.MAX_VALUE - 1L) balancedTicks++;
        } else {
            balancedTicks = 0L;
        }

        long potentialMultiplier = activeStarArray ? 4L : 1L;
        if (hasUpgrade(VOID_LINK_THEORY)
            && positiveModulo(continuousTicks, VoidNexusCalculator.getContinuousPotentialIntervalTicks()) == 0L) {
            addVirtualPotential(potentialMultiplier);
        }
        if (hasUpgrade(VOID_LINK_THEORY) && activeBalanced
            && positiveModulo(balancedTicks, VoidNexusCalculator.getBalancePotentialIntervalTicks()) == 0L) {
            addVirtualPotential(potentialMultiplier);
        }
        if (hasUpgrade(DARK_RIVER)
            && positiveModulo(continuousTicks, VoidNexusCalculator.getDarkRiverIntervalTicks()) == 0L) {
            psionicAcceleration = Math.min(MAX_PSIONIC_ACCELERATION, psionicAcceleration + 1L);
        }

        if (!hasUpgrade(VAST_MOUNTAIN)) {
            if (positiveModulo(continuousTicks, VoidNexusCalculator.getWarpCheckIntervalTicks()) == 0L) {
                double warpChance = hasUpgrade(PSIONIC_HARMONY) ? 0.75D : hasUpgrade(VOID_LINK_THEORY) ? 0.45D : 0.0D;
                if (warpChance > 0.0D && getBaseMetaTileEntity().getWorld().rand.nextDouble() < warpChance) {
                    applyWarpAround(hasUpgrade(PSIONIC_HARMONY) ? WARP_STICKY : WARP_TEMPORARY);
                }
            }
            if (hasUpgrade(ENTER_THE_VOID)
                && positiveModulo(continuousTicks, VoidNexusCalculator.getPermanentWarpIntervalTicks()) == 0L) {
                applyWarpAround(WARP_PERMANENT);
            }
        }
    }

    private void applyWarpAround(byte warpType) {
        World world = getBaseMetaTileEntity().getWorld();
        int x = getBaseMetaTileEntity().getXCoord();
        int y = getBaseMetaTileEntity().getYCoord();
        int z = getBaseMetaTileEntity().getZCoord();
        AxisAlignedBB area = AxisAlignedBB.getBoundingBox(
            x - MAX_WARP_RADIUS,
            y - MAX_WARP_RADIUS,
            z - MAX_WARP_RADIUS,
            x + MAX_WARP_RADIUS,
            y + MAX_WARP_RADIUS,
            z + MAX_WARP_RADIUS);
        List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, area);
        for (EntityPlayer player : players) {
            if (!(player instanceof EntityPlayerMP)) continue;
            String name = player.getCommandSenderName();
            switch (warpType) {
                case WARP_PERMANENT -> Thaumcraft.proxy.getPlayerKnowledge()
                    .addWarpPerm(name, 1);
                case WARP_STICKY -> Thaumcraft.proxy.getPlayerKnowledge()
                    .addWarpSticky(name, 1);
                default -> Thaumcraft.proxy.getPlayerKnowledge()
                    .addWarpTemp(name, 1);
            }
            PacketHandler.INSTANCE.sendTo(new PacketSyncWarp(player, warpType), (EntityPlayerMP) player);
            PacketHandler.INSTANCE.sendTo(new PacketWarpMessage(player, warpType, 1), (EntityPlayerMP) player);
        }
    }

    private void creditSourcePotential(long totalEssentia, boolean starArray) {
        if (!hasUpgrade(VOID_LINK_THEORY) || totalEssentia <= 0L) return;
        sourcePotentialRemainder = saturatingAdd(sourcePotentialRemainder, totalEssentia);
        long gained = sourcePotentialRemainder / SOURCE_PER_POTENTIAL;
        if (gained <= 0L) return;
        sourcePotentialRemainder %= SOURCE_PER_POTENTIAL;
        if (starArray && gained <= Long.MAX_VALUE / 4L) gained *= 4L;
        addVirtualPotential(gained);
    }

    private VoidNexusCalculator.Config buildCalculatorConfig(boolean starArray, boolean manifestedPsionics,
        boolean bloodSupply, boolean cycleBonus) {
        VoidNexusCalculator.Config config = new VoidNexusCalculator.Config();
        config.stage = stage;
        config.structureTier = structureTier;
        config.improvedCommunication = hasUpgrade(GAIA_COMMUNICATOR);
        config.endlessWandSystem = hasUpgrade(ENDLESS_WAND_SYSTEM);
        config.bloodRuneSaver = hasUpgrade(BLOOD_RUNE_SAVER);
        config.dimensionalBeyond = hasUpgrade(BEYOND_DIMENSIONS);
        config.voidLinkTheory = hasUpgrade(VOID_LINK_THEORY);
        config.moonNest = hasUpgrade(SOUL_MOON_NEST);
        config.hungryTool = hasUpgrade(HUNGRY_TOOL);
        config.lifeWeave = hasUpgrade(LIFE_WEAVE);
        config.worldDevourer = hasUpgrade(WORLD_DEVOURER);
        config.psionicHarmony = hasUpgrade(PSIONIC_HARMONY);
        config.moonContract = hasUpgrade(MOON_CONTRACT);
        config.toolContract = hasUpgrade(TOOL_CONTRACT);
        config.weaveContract = hasUpgrade(WEAVE_CONTRACT);
        config.devourerContract = hasUpgrade(DEVOURER_CONTRACT);
        config.enteredVoid = hasUpgrade(ENTER_THE_VOID);
        config.materialInputMode = isMaterialInputModeEnabled();
        config.alienVisitor = hasUpgrade(OUTER_PLANE_GUEST);
        config.goldenDream = hasUpgrade(GOLDEN_DREAM);
        config.endlessTide = hasUpgrade(ENDLESS_TIDE);
        config.unshakenMountain = hasUpgrade(VAST_MOUNTAIN);
        config.hollowPoint = hasUpgrade(HOLLOW_POINT);
        config.emptyThrone = hasUpgrade(THRONE_OF_EMPTINESS);
        config.darkRiver = hasUpgrade(DARK_RIVER);
        config.reforgedVoid = hasUpgrade(REFORGE_THE_VOID);
        config.starArray = starArray;
        config.manifestedPsionics = manifestedPsionics;
        config.bloodSupply = bloodSupply;
        config.cycleBonus = cycleBonus;
        config.virtualPotential = virtualPotential;
        return config;
    }

    private boolean hasStargateCoreCrystal() {
        return stargateCorePresent;
    }

    private void refreshStargateCoreState() {
        if (!hasUpgrade(WHISPER_OF_VOID)) {
            stargateCorePresent = false;
            return;
        }
        long now = getVoidWorldTime();
        if (stargateCoreCheckTick != Long.MIN_VALUE && now >= stargateCoreCheckTick
            && now - stargateCoreCheckTick < 20L) return;
        List<ItemStack> inputs = getStoredInputs();
        stargateCorePresent = containsStargateCoreCrystal(inputs == null ? Collections.emptyList() : inputs);
        stargateCoreCheckTick = now;
    }

    private long getVoidWorldTime() {
        IGregTechTileEntity base = getBaseMetaTileEntity();
        return base == null || base.getWorld() == null ? 0L
            : base.getWorld()
                .getTotalWorldTime();
    }

    private boolean containsStargateCoreCrystal(List<ItemStack> stacks) {
        if (isStargateCoreCrystal(getControllerSlot())) return true;
        for (ItemStack stack : stacks) {
            if (isStargateCoreCrystal(stack)) return true;
        }
        for (MTEHatchInputBus inputBus : filterValidMTEs(mInputBusses)) {
            ItemStack[] inventory = inputBus.getRealInventory();
            if (inventory == null) continue;
            for (ItemStack stack : inventory) {
                if (isStargateCoreCrystal(stack)) return true;
            }
        }
        return false;
    }

    private boolean isStargateCoreCrystal(ItemStack stack) {
        if (stack == null || stack.stackSize <= 0) return false;
        ItemStack required = GTModHandler.getModItem("SGCraft", "sgCoreCrystal", 1);
        return required != null && stack.getItem() == required.getItem()
            && stack.getItemDamage() == required.getItemDamage();
    }

    private void clearRunState() {
        continuousTicks = 0L;
        balancedTicks = 0L;
        activeGeneratedPower = 0L;
        activePowerBeforeBonuses = 0L;
        lastConsumedEssentia = 0L;
        activeCatalystMultiplier = 1.0D;
        activeCatalystBlocked = false;
        activeBalanced = false;
        activeStarArray = false;
    }

    private void stopRoundBeforeStart() {
        clearRunState();
        activePower = 0L;
        lEUt = 0L;
        mEUt = 0;
        mMaxProgresstime = 0;
    }

    private VoidNexusCalculator.Result createIdleResult() {
        VoidNexusCalculator.Result result = new VoidNexusCalculator.Result();
        result.power = GTValues.V[15];
        result.deviation = 1.0D;
        return result;
    }

    // ===================== Fuel and Catalyst Inputs =====================

    private boolean hasBloodForRound(int duration) {
        if (!hasUpgrade(BLOOD_RUNE_SAVER)) return false;
        Fluid blood = FluidRegistry.getFluid("lifeessence");
        if (blood == null) return false;
        long amount = (long) BLOOD_PER_TICK * duration;
        return findLiquidAmount(new FluidStack(blood, 1), getStoredFluids()) >= amount;
    }

    private void consumeBloodForRound(int duration) {
        Fluid blood = FluidRegistry.getFluid("lifeessence");
        if (blood == null) return;
        consumeFluidAmount(new FluidStack(blood, 1), (long) BLOOD_PER_TICK * duration);
    }

    private CatalystPlan buildCatalystPlan(long totalEssentia, List<ItemStack> storedInputs) {
        CatalystPlan plan = new CatalystPlan();
        List<FluidStack> storedFluids = getStoredFluids();

        if (stage.index >= VoidNexusCalculator.Stage.UEV.index) {
            long amount = safeMultiply(144L, totalEssentia / 1_000L);
            addFluidRequirement(plan, storedFluids, getAvaritiaCosmicNeutroniumMolten(1), amount, false, 0.60D);
        }
        if (stage.index >= VoidNexusCalculator.Stage.UIV.index) {
            long amount = safeMultiply(10L, totalEssentia / 1_000_000L);
            addFluidRequirement(plan, storedFluids, Materials.Antimatter.getFluid(1), amount, false, 0.01D);
        }
        if (stage.index >= VoidNexusCalculator.Stage.UMV.index) {
            long rounds = totalEssentia / 100_000L;
            addFluidRequirement(
                plan,
                storedFluids,
                Materials.Protomatter.getFluid(1),
                safeMultiply(1_000L, rounds),
                true,
                0.0D);
        }
        if (stage.index >= VoidNexusCalculator.Stage.UXV.index) {
            addFluidRequirement(plan, storedFluids, Materials.Eternity.getMolten(1), 6_000L, true, 0.0D);
        }

        long cells = 0L;
        if (stage.index >= VoidNexusCalculator.Stage.UMV.index) {
            cells = saturatingAdd(cells, totalEssentia / 100_000L);
        }
        if (stage.index >= VoidNexusCalculator.Stage.UXV.index) {
            cells = saturatingAdd(cells, 64L);
        }
        if (cells > 0L) {
            ItemStack cell = EnumEssentiaStorageTypes.Type_Creative.getCell();
            for (ItemStack stack : storedInputs) {
                if (stack == null || stack.stackSize <= 0 || !stack.isItemEqual(cell)) continue;
                int amount = (int) Math.min(cells, stack.stackSize);
                plan.items.add(new StackConsumption(stack, amount));
                cells -= amount;
                if (cells == 0L) break;
            }
            if (cells > 0L) plan.blocked = true;
        }

        for (FluidRequirement requirement : plan.fluids) {
            long available = findLiquidAmount(requirement.fluid, storedFluids);
            if (available < requirement.amount) {
                if (requirement.critical) {
                    plan.blocked = true;
                } else {
                    plan.penaltyMultiplier *= requirement.missingPenalty;
                }
            }
        }
        return plan;
    }

    private static FluidStack getAvaritiaCosmicNeutroniumMolten(int amount) {
        Fluid fluid = FluidRegistry.getFluid(AVARITIA_COSMIC_NEUTRONIUM_MOLTEN_FLUID);
        return fluid == null ? null : new FluidStack(fluid, amount);
    }

    private void addFluidRequirement(CatalystPlan plan, List<FluidStack> storedFluids, FluidStack prototype,
        long amount, boolean critical, double missingPenalty) {
        if (amount <= 0L) return;
        if (prototype == null || prototype.getFluid() == null) {
            if (critical) {
                plan.blocked = true;
            } else {
                plan.penaltyMultiplier *= missingPenalty;
            }
            return;
        }
        plan.fluids.add(new FluidRequirement(prototype, amount, critical, missingPenalty));
    }

    private void applyCatalystPenalties(VoidNexusCalculator.Result result, CatalystPlan plan) {
        if (!plan.canRun()) {
            result.power = 0L;
            return;
        }
        if (plan.penaltyMultiplier < 1.0D) {
            result.power = (long) Math.floor(result.power * plan.penaltyMultiplier);
        }
    }

    private long findLiquidAmount(FluidStack target, List<FluidStack> available) {
        if (target == null || available == null) return 0L;
        long result = 0L;
        for (FluidStack stack : available) {
            if (stack != null && stack.isFluidEqual(target)) result = saturatingAdd(result, stack.amount);
        }
        return result;
    }

    private void consumeFluidAmount(FluidStack target, long amount) {
        long remaining = amount;
        while (remaining > 0L) {
            int chunk = (int) Math.min(Integer.MAX_VALUE, remaining);
            if (!depleteInput(new FluidStack(target.getFluid(), chunk))) return;
            remaining -= chunk;
        }
    }

    private long consumeItemAmount(ItemStack target, long amount) {
        long remaining = amount;
        ItemStack unit = copyOne(target);
        while (remaining > 0L) {
            if (!depleteInput(unit)) break;
            remaining--;
        }
        return amount - remaining;
    }

    // ===================== Host Wand and Special Items =====================

    private ItemStack getHostWand() {
        ItemStack stack = getControllerSlot();
        return stack != null && stack.getItem() instanceof ItemWandCasting ? stack : null;
    }

    private boolean canUseHostWand(int duration) {
        ItemStack stack = getHostWand();
        if (stack == null) return false;
        if (hasUpgrade(ENDLESS_WAND_SYSTEM)) return true;
        ItemWandCasting wand = (ItemWandCasting) stack.getItem();
        long usableVis = 0L;
        for (int i = 0; i < VoidNexusFuelProfiles.PRIMAL_COUNT; i++) {
            Aspect aspect = VoidNexusFuelProfiles.getPrimal(i);
            if (aspect != null) usableVis += wand.getVis(stack, aspect) / 100;
        }
        return usableVis >= duration;
    }

    private boolean consumeHostWandVisPerTick() {
        ItemStack stack = getHostWand();
        if (stack == null) return false;
        if (hasUpgrade(ENDLESS_WAND_SYSTEM)) return true;

        ItemWandCasting wand = (ItemWandCasting) stack.getItem();
        List<Aspect> available = new ArrayList<>(VoidNexusFuelProfiles.PRIMAL_COUNT);
        for (int i = 0; i < VoidNexusFuelProfiles.PRIMAL_COUNT; i++) {
            Aspect aspect = VoidNexusFuelProfiles.getPrimal(i);
            if (aspect != null && wand.getVis(stack, aspect) >= 100) available.add(aspect);
        }
        if (available.isEmpty()) return false;

        Aspect aspect = available.get(getBaseMetaTileEntity().getWorld().rand.nextInt(available.size()));
        wand.storeVis(stack, aspect, Math.max(0, wand.getVis(stack, aspect) - 100));
        updateSlots();
        return true;
    }

    private boolean isPsionicStabilizer(ItemStack stack) {
        return stack != null && OTHItemList.PsionicStabilizer.hasBeenSet()
            && GTUtility.areStacksEqual(copyOne(stack), OTHItemList.PsionicStabilizer.get(1));
    }

    private boolean isVoidPotential(ItemStack stack) {
        return stack != null && stack.getItem() == ItemLoader.itemVoidPotential;
    }

    private boolean isStarArray(ItemStack stack) {
        ItemStack starArray = GTModHandler.getModItem("tectech", "item.tm.itemAstralArrayFabricator", 1);
        return stack != null && starArray != null && GTUtility.areStacksEqual(copyOne(stack), starArray);
    }

    // ===================== Upgrades =====================

    private boolean hasUpgrade(VoidNexusUpgrade upgrade) {
        if (upgrade == null || stage.index < upgrade.minimumStage.index
            || structureTier < upgrade.minimumStructureTier
            || (purchasedUpgradeMask & (1L << (upgrade.id - 1))) == 0L
            || getBaseMetaTileEntity() == null
            || !areUpgradePrerequisitesPurchased(upgrade)) return false;
        return true;
    }

    private boolean areUpgradePrerequisitesPurchased(VoidNexusUpgrade upgrade) {
        for (int prerequisiteId : upgrade.getPrerequisiteIds()) {
            if ((purchasedUpgradeMask & (1L << (prerequisiteId - 1))) == 0L) return false;
        }
        return true;
    }

    public synchronized boolean tryPurchaseUpgrade(int upgradeId) {
        IGregTechTileEntity base = getBaseMetaTileEntity();
        if (base == null || !base.isServerSide()) return false;
        VoidNexusUpgrade upgrade = VoidNexusUpgrade.byId(upgradeId);
        if (upgrade == null) return false;
        long bit = 1L << (upgrade.id - 1);
        if ((purchasedUpgradeMask & bit) != 0L) {
            upgradeStatusKey = "ote.vnm.gui.purchase.already";
            return false;
        }
        if (stage.index < upgrade.minimumStage.index) {
            upgradeStatusKey = "ote.vnm.gui.purchase.stage_locked";
            return false;
        }
        if (structureTier < upgrade.minimumStructureTier) {
            upgradeStatusKey = "ote.vnm.gui.purchase.tier_locked";
            return false;
        }
        if (!areUpgradePrerequisitesPurchased(upgrade)) {
            upgradeStatusKey = "ote.vnm.gui.purchase.prerequisites";
            return false;
        }

        purchasedUpgradeMask |= bit;
        upgradeStatusKey = "ote.vnm.gui.purchase.success";
        connectPsionicModules();
        updateSlots();
        return true;
    }

    // ===================== Utility Methods =====================

    private static ItemStack copyOne(ItemStack stack) {
        ItemStack result = stack.copy();
        result.stackSize = 1;
        return result;
    }

    private static long positiveModulo(long value, long modulus) {
        return modulus <= 0L ? -1L : Math.floorMod(value, modulus);
    }

    private static long saturatingAdd(long left, long right) {
        if (right > 0L && left > Long.MAX_VALUE - right) return Long.MAX_VALUE;
        return left + right;
    }

    private static long safeMultiply(long left, long right) {
        if (left <= 0L || right <= 0L) return 0L;
        return left > Long.MAX_VALUE / right ? Long.MAX_VALUE : left * right;
    }

    // ===================== Virtual Potential =====================

    private long addVirtualPotential(long amount) {
        if (amount <= 0L || virtualPotential >= MAX_VIRTUAL_POTENTIAL) return 0L;
        long accepted = Math.min(amount, MAX_VIRTUAL_POTENTIAL - virtualPotential);
        virtualPotential += accepted;
        return accepted;
    }

    public synchronized boolean depositPotentialFromInputBus() {
        IGregTechTileEntity base = getBaseMetaTileEntity();
        if (base == null || !base.isServerSide()) return false;
        if (!hasUpgrade(FIND_ONES_OWN_PATH)) {
            potentialTransferStatusKey = "ote.vnm.potential.u28_required";
            return false;
        }
        if (virtualPotential >= MAX_VIRTUAL_POTENTIAL) {
            potentialTransferStatusKey = "ote.vnm.potential.full";
            return false;
        }

        ItemStack potentialItem = new ItemStack(ItemLoader.itemVoidPotential, 1);
        long deposited = consumeItemAmount(potentialItem, MAX_VIRTUAL_POTENTIAL - virtualPotential);
        if (deposited == 0L) {
            potentialTransferStatusKey = "ote.vnm.potential.no_input";
            return false;
        }

        virtualPotential += deposited;
        markDirty();
        potentialTransferStatusKey = "ote.vnm.potential.deposit_success";
        updateSlots();
        return true;
    }

    public synchronized boolean withdrawPotentialToOutputBus() {
        IGregTechTileEntity base = getBaseMetaTileEntity();
        if (base == null || !base.isServerSide()) return false;
        if (!hasUpgrade(FIND_ONES_OWN_PATH)) {
            potentialTransferStatusKey = "ote.vnm.potential.u28_required";
            return false;
        }
        if (virtualPotential <= 0L) {
            potentialTransferStatusKey = "ote.vnm.potential.empty";
            return false;
        }
        List<IOutputBus> outputBusses = getOutputBusses();
        if (outputBusses.isEmpty()) {
            potentialTransferStatusKey = "ote.vnm.potential.no_output";
            return false;
        }

        ItemStack remaining = new ItemStack(ItemLoader.itemVoidPotential, (int) virtualPotential);
        PsionicOutputQueue.insert(remaining, outputBusses);
        if (remaining.stackSize == virtualPotential) {
            potentialTransferStatusKey = "ote.vnm.potential.no_output";
            return false;
        }

        virtualPotential = remaining.stackSize;
        markDirty();
        potentialTransferStatusKey = "ote.vnm.potential.withdraw_success";
        updateSlots();
        return true;
    }

    // ===================== Essentia Input Buses =====================

    @Override
    protected boolean shouldCheckRecipeThisTick(long tick) {
        return super.shouldCheckRecipeThisTick(tick) || (tick % 20L == 0L && !isMaterialInputModeEnabled()
            && mInputBusses.stream()
                .anyMatch(OTEEssentiaStockingInputBusME.class::isInstance));
    }

    private List<OTEEssentiaStockingInputBusME> getEssentiaBusses() {
        List<OTEEssentiaStockingInputBusME> busses = new ArrayList<>();
        for (MTEHatchInputBus inputBus : filterValidMTEs(mInputBusses)) {
            if (inputBus instanceof OTEEssentiaStockingInputBusME essentiaBus) busses.add(essentiaBus);
        }
        return busses;
    }

    // ===================== Energy Output =====================

    @Override
    public boolean addEnergyOutput_EM(long EU, long amperes) {
        if (EU <= 0L) return false;
        return addLongEnergyOutput(EU, Math.max(1L, amperes));
    }

    private boolean addLongEnergyOutput(long euPerAmp, long amperes) {
        if (amperes <= 0L) return false;
        long remainingEU;
        if (euPerAmp > Long.MAX_VALUE / amperes) {
            remainingEU = Long.MAX_VALUE;
        } else {
            remainingEU = euPerAmp * amperes;
        }
        for (MTEHatchDynamo hatch : filterValidMTEs(mDynamoHatches)) {
            long room = hatch.maxEUStore() - hatch.getBaseMetaTileEntity()
                .getStoredEU();
            if (room <= 0L) continue;
            long add = Math.min(room, remainingEU);
            hatch.setEUVar(
                hatch.getBaseMetaTileEntity()
                    .getStoredEU() + add);
            remainingEU -= add;
            if (remainingEU <= 0L) return true;
        }
        for (MTEHatchDynamoMulti hatch : filterValidMTEs(eDynamoMulti)) {
            long room = hatch.maxEUStore() - hatch.getBaseMetaTileEntity()
                .getStoredEU();
            if (room <= 0L) continue;
            long add = Math.min(room, remainingEU);
            hatch.setEUVar(
                hatch.getBaseMetaTileEntity()
                    .getStoredEU() + add);
            remainingEU -= add;
            if (remainingEU <= 0L) return true;
        }
        return false;
    }

    // ===================== Structure =====================

    @Override
    public void checkMachine(IGregTechTileEntity base, ItemStack stack, List<StructureError> errors) {
        clearVoidState();
        clearStructureHatches();
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFFSET, VERTICAL_OFFSET, DEPTH_OFFSET, errors)) {
            if (errors.isEmpty()) {
                errors.add(StructureErrors.of("ote.vnm.struct.incomplete"));
            }
            return;
        }
        prunePsionicModuleHatches();
        pruneDuplicateStockingBuses();
        if (!validateUpgradeVariants(errors)) return;
        structureTier = upgradeVariants[0];
        stage = VoidNexusCalculator.Stage.fromCasingTier(structureTier - 1);
        ItemStack controller = getControllerSlot();
        if (controller == null || !(controller.getItem() instanceof ItemWandCasting)) {
            errors.add(StructureErrors.of("ote.vnm.struct.wands"));
        }
        if (mInputBusses.size() + mDualInputHatches.size() < INPUT_BUS_MIN) {
            errors.add(StructureErrors.of("GT5U.gui.text.structure_error.no_input"));
        }
        boolean hasLaser = false;
        boolean invalidDynamo = false;
        for (MTEHatchDynamo hatch : filterValidMTEs(mDynamoHatches)) {
            if (hatch instanceof MTEHatchDynamoTunnel) {
                hasLaser = true;
            } else {
                invalidDynamo = true;
            }
        }
        for (MTEHatchDynamoMulti hatch : filterValidMTEs(eDynamoMulti)) {
            if (hatch instanceof MTEHatchDynamoTunnel) {
                hasLaser = true;
            } else {
                invalidDynamo = true;
            }
        }
        if (invalidDynamo) errors.add(StructureErrors.of("ote.vnm.struct.laser_only"));
        if (!hasLaser) errors.add(StructureErrors.of("ote.vnm.struct.no_laser"));
        repairMachine();
        if (errors.isEmpty()) connectPsionicModules();
    }

    private void clearStructureHatches() {
        disconnectPsionicModules();
        clearHatches();
        eDynamoMulti.clear();
    }

    private void prunePsionicModuleHatches() {
        mInputBusses.removeIf(this::isPsionicModuleHatch);
        mInputHatches.removeIf(this::isPsionicModuleHatch);
        mOutputBusses.removeIf(this::isPsionicModuleHatch);
        mOutputHatches.removeIf(this::isPsionicModuleHatch);
        mDualInputHatches.removeIf(hatch -> hatch instanceof MTEHatch && isPsionicModuleHatch((MTEHatch) hatch));
    }

    private void pruneDuplicateStockingBuses() {
        Set<Object> networks = Collections.newSetFromMap(new IdentityHashMap<>());
        List<MTEHatchInputBus> duplicates = new ArrayList<>();
        for (MTEHatchInputBus inputBus : filterValidMTEs(mInputBusses)) {
            if (!(inputBus instanceof MTEHatchInputBusME stockingBus)
                || inputBus instanceof OTEEssentiaStockingInputBusME) continue;
            try {
                IGrid grid = stockingBus.getProxy()
                    .getGrid();
                if (grid != null && !networks.add(grid)) duplicates.add(inputBus);
            } catch (GridAccessException ignored) {}
        }
        if (!duplicates.isEmpty()) {
            mInputBusses.removeAll(duplicates);
            OTHTechnology.LOG.warn(
                "Void Nexus Matrix ignored {} duplicate stocking input bus(es) on the same ME network.",
                duplicates.size());
        }
    }

    private boolean isPsionicModuleHatch(MTEHatch hatch) {
        if (hatch == null) return false;
        IGregTechTileEntity hatchBase = hatch.getBaseMetaTileEntity();
        if (hatchBase == null) return false;

        for (OTEPsionicManifestationModule module : psionicModules) {
            if (module.ownsHatch(hatch)) return true;
            IGregTechTileEntity moduleBase = module.getBaseMetaTileEntity();
            if (moduleBase == null || moduleBase.getWorld() != hatchBase.getWorld()) continue;
            int dx = Math.abs(moduleBase.getXCoord() - hatchBase.getXCoord());
            int dy = Math.abs(moduleBase.getYCoord() - hatchBase.getYCoord());
            int dz = Math.abs(moduleBase.getZCoord() - hatchBase.getZCoord());
            if (dx <= 4 && dy <= 4 && dz <= 4) return true;
        }
        return false;
    }

    // ===================== Psionic Modules =====================

    private void connectPsionicModules() {
        int connected = 0;
        int limit = getPsionicModuleLimit();
        for (OTEPsionicManifestationModule module : psionicModules) {
            if (connected < limit) {
                module.connect(this);
                connected++;
            } else {
                module.disconnect();
            }
        }
    }

    private int getPsionicModuleLimit() {
        if (!hasUpgrade(PSIONIC_HARMONY)) return 0;
        return hasUpgrade(DEVOURER_CONTRACT) ? 4 : 1;
    }

    private void allocatePsionicModulePower() {
        IGregTechTileEntity hostBase = getBaseMetaTileEntity();
        if (!mMachine || hostBase == null || !hostBase.isAllowedToWork() || !hasUpgrade(PSIONIC_HARMONY)) {
            for (OTEPsionicManifestationModule module : psionicModules) module.disconnect();
            return;
        }
        connectPsionicModules();
        long available = getConnectedInputEnergyStoredEU();
        if (available <= 0L || psionicModules.isEmpty()) return;

        for (int pass = 0; pass < 2 && available > 0L; pass++) {
            int activeModules = 0;
            for (OTEPsionicManifestationModule module : psionicModules) {
                IGregTechTileEntity moduleBase = module.getBaseMetaTileEntity();
                if (module.isConnected() && module.mMachine
                    && moduleBase != null
                    && moduleBase.getStoredEU() < module.maxEUStore()) {
                    activeModules++;
                }
            }
            if (activeModules <= 0) return;

            long share = Math.max(1L, available / activeModules);
            modulePowerRotation = Math.floorMod(modulePowerRotation, psionicModules.size());
            for (int offset = 0; offset < psionicModules.size() && available > 0L; offset++) {
                OTEPsionicManifestationModule module = psionicModules
                    .get((modulePowerRotation + offset) % psionicModules.size());
                IGregTechTileEntity moduleBase = module.getBaseMetaTileEntity();
                if (!module.isConnected() || !module.mMachine || moduleBase == null) continue;
                long missing = Math.max(0L, module.maxEUStore() - moduleBase.getStoredEU());
                if (missing <= 0L) continue;
                long accepted = module.increaseStoredEU(Math.min(share, Math.min(missing, available)));
                if (accepted <= 0L) continue;
                if (!ExoticEnergyInputHelper.drainEnergy(accepted, getExoticAndNormalEnergyHatchList())) {
                    moduleBase.decreaseStoredEnergyUnits(accepted, false);
                    continue;
                }
                available -= accepted;
            }
            modulePowerRotation = (modulePowerRotation + 1) % psionicModules.size();
        }
    }

    public long getConnectedInputEnergyStoredEU() {
        long stored = 0L;
        for (MTEHatch hatch : getExoticAndNormalEnergyHatchList()) {
            IGregTechTileEntity hatchBase = hatch.getBaseMetaTileEntity();
            if (hatchBase == null) continue;
            long hatchStored = Math.max(0L, hatchBase.getStoredEU());
            if (Long.MAX_VALUE - stored < hatchStored) return Long.MAX_VALUE;
            stored += hatchStored;
        }
        return stored;
    }

    public long getConnectedInputEnergyCapacityEU() {
        long capacity = 0L;
        for (MTEHatch hatch : getExoticAndNormalEnergyHatchList()) {
            IGregTechTileEntity hatchBase = hatch.getBaseMetaTileEntity();
            if (hatchBase == null) continue;
            capacity = GTUtility.addSafe(capacity, Math.max(0L, hatchBase.getEUCapacity()));
        }
        return capacity;
    }

    public long getPsionicModulePowerShareEU() {
        long inputPower = ExoticEnergyInputHelper.getTotalEuMulti(getExoticAndNormalEnergyHatchList());
        int modules = 0;
        for (OTEPsionicManifestationModule module : psionicModules) {
            if (module.isConnected() && module.mMachine) modules++;
        }
        modules = Math.max(1, modules);
        return inputPower <= 0L ? 0L : inputPower / modules;
    }

    private void addPsionicModule(OTEPsionicManifestationModule module) {
        if (module != null && !psionicModules.contains(module)) psionicModules.add(module);
    }

    private void disconnectPsionicModules() {
        for (OTEPsionicManifestationModule module : psionicModules) module.disconnect();
        psionicModules.clear();
    }

    // ===================== Structure Definition and Construction =====================

    private void clearVoidState() {
        java.util.Arrays.fill(upgradeVariants, 0);
        structureTier = 1;
        stage = VoidNexusCalculator.Stage.UHV;
        stargateCorePresent = false;
        stargateCoreCheckTick = Long.MIN_VALUE;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        if (isPreviewWorld()) {
            buildPreviewPieceOnce(stackSize);
            return;
        }
        repairMachine();
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFFSET, VERTICAL_OFFSET, DEPTH_OFFSET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        if (isPreviewWorld()) {
            buildPreviewPieceOnce(stackSize);
            return -1;
        }
        return survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            HORIZONTAL_OFFSET,
            VERTICAL_OFFSET,
            DEPTH_OFFSET,
            elementBudget,
            env,
            false,
            true);
    }

    private boolean isPreviewWorld() {
        IGregTechTileEntity base = getBaseMetaTileEntity();
        return base != null && isPreviewWorld(base.getWorld());
    }

    private static boolean isPreviewWorld(World world) {
        if (world == null) return false;
        String name = world.getClass()
            .getName();
        return name.contains("Dummy") || name.contains("Observer") || name.contains("Tracked");
    }

    private void buildPreviewPieceOnce(ItemStack stackSize) {
        IGregTechTileEntity base = getBaseMetaTileEntity();
        World world = base == null ? null : base.getWorld();
        if (world == null || !isPreviewWorld(world)) return;
        List<PreviewCell> cells = PREVIEW_CELLS;
        if (cells.isEmpty()) return;

        int tier = stackSize == null ? 1 : Math.max(1, Math.min(MAX_STRUCTURE_TIER, stackSize.stackSize));
        int baseY = base.getYCoord();
        int[] abc = new int[3];
        int[] xyz = new int[3];
        for (PreviewCell cell : cells) {
            BlockMeta target = previewBlock(cell.symbol, tier);
            if (target == null) continue;
            abc[0] = cell.a;
            abc[1] = cell.b;
            abc[2] = cell.c;
            getExtendedFacing().getWorldOffset(abc, xyz);
            int x = base.getXCoord() + xyz[0];
            int y = baseY + xyz[1];
            int z = base.getZCoord() + xyz[2];
            if (cell.symbol == 'A') {
                placePsionicModulePreview(world, x, y, z, base.getXCoord(), base.getYCoord(), base.getZCoord());
            } else {
                placePreviewBlock(world, x, y, z, target);
            }
        }
    }

    private static void placePreviewBlock(World world, int x, int y, int z, BlockMeta target) {
        world.setBlock(x, y, z, target.block, target.meta, 2);
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile == null && target.block.hasTileEntity(target.meta)) {
            tile = target.block.createTileEntity(world, target.meta);
            if (tile != null) world.setTileEntity(x, y, z, tile);
        }
        setGeneratedBlockMetadata(world, x, y, z, target.meta);
    }

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return new String[] { EnumChatFormatting.AQUA + translateToLocal("otht.con"),
            translateToLocal("ote.vnm.struct.description") };
    }

    @Override
    public IStructureDefinition<OTEVoidNexusMatrix> getStructure_EM() {
        IStructureDefinition<OTEVoidNexusMatrix> definition = STRUCTURE_DEFINITION;
        if (definition != null) return definition;
        return createStructureDefinition();
    }

    private static synchronized IStructureDefinition<OTEVoidNexusMatrix> createStructureDefinition() {
        IStructureDefinition<OTEVoidNexusMatrix> definition = STRUCTURE_DEFINITION;
        if (definition != null) return definition;
        String[][] shape = ShapeLoader
            .loadCompressed("/assets/123technology/structures/ote/OTEVoidNexusMatrix_main.shape.gz");
        markControllerHatchPositions(shape);
        preparePreviewCells(shape);
        definition = StructureDefinition.<OTEVoidNexusMatrix>builder()
            .addShape(STRUCTURE_PIECE_MAIN, shape)
            .addElement('A', new PsionicModuleSlotElement())
            .addElement('C', tieredElement(0, tierPalette('C')))
            .addElement('D', voidNexusGlassElement())
            .addElement('E', tieredElement(2, tierPalette('E')))
            .addElement('F', snowHatchElement())
            .addElement('G', metadataAwareTieredElement(3, tierPalette('G')))
            .addElement('H', tieredElement(4, tierPalette('H')))
            .addElement(CONTROLLER_HATCH_SYMBOL, controllerSnowHatchElement())
            .build();
        STRUCTURE_DEFINITION = definition;
        return definition;
    }

    private static BlockMeta block(net.minecraft.block.Block block, int meta) {
        return new BlockMeta(block, meta);
    }

    private static void preparePreviewCells(String[][] shape) {
        List<PreviewCell> cells = new ArrayList<>(40_000);
        for (int c = 0; c < shape.length; c++) {
            String[] slice = shape[c];
            for (int b = 0; b < slice.length; b++) {
                String line = slice[b];
                for (int a = 0; a < line.length(); a++) {
                    char symbol = line.charAt(a);
                    if (symbol == ' ' || symbol == '~') continue;
                    cells.add(new PreviewCell(a - HORIZONTAL_OFFSET, b - VERTICAL_OFFSET, c - DEPTH_OFFSET, symbol));
                }
            }
        }
        PREVIEW_CELLS = Collections.unmodifiableList(cells);
    }

    private static void markControllerHatchPositions(String[][] shape) {
        if (shape.length == 0) return;
        for (int c = DEPTH_OFFSET - CONTROLLER_HATCH_RADIUS; c <= DEPTH_OFFSET + CONTROLLER_HATCH_RADIUS; c++) {
            if (c < 0 || c >= shape.length) continue;
            for (int b = VERTICAL_OFFSET - CONTROLLER_HATCH_RADIUS; b
                <= VERTICAL_OFFSET + CONTROLLER_HATCH_RADIUS; b++) {
                if (b < 0 || b >= shape[c].length) continue;
                char[] row = shape[c][b].toCharArray();
                boolean changed = false;
                for (int a = HORIZONTAL_OFFSET - CONTROLLER_HATCH_RADIUS; a
                    <= HORIZONTAL_OFFSET + CONTROLLER_HATCH_RADIUS; a++) {
                    if (a < 0 || a >= row.length || row[a] != 'F') continue;
                    int distance = Math.abs(a - HORIZONTAL_OFFSET) + Math.abs(b - VERTICAL_OFFSET)
                        + Math.abs(c - DEPTH_OFFSET);
                    if (distance < 2 || distance > CONTROLLER_HATCH_RADIUS) continue;
                    row[a] = CONTROLLER_HATCH_SYMBOL;
                    changed = true;
                }
                if (changed) shape[c][b] = new String(row);
            }
        }
    }

    private static BlockMeta previewBlock(char symbol, int tier) {
        switch (symbol) {
            case 'A':
                if (OTHItemList.PsionicManifestationModule.hasBeenSet()) {
                    net.minecraft.block.Block block = OTHItemList.PsionicManifestationModule.getBlock();
                    if (block != null) {
                        return new BlockMeta(block, OTHItemList.PsionicManifestationModule.getMeta());
                    }
                }
                return new BlockMeta(BlockList.BloodyIchorium.block, 0);
            case 'F':
            case 'X':
                return block(sBlockCasings8, 8);
            case 'H':
                return pickTier(tier, tierPalette('H'));
            default:
                return pickTier(tier, tierPalette(symbol));
        }
    }

    private static BlockMeta[] tierPalette(char symbol) {
        switch (symbol) {
            case 'C':
                return new BlockMeta[] { block(BlockLoader.defcCasingBlock, 9), block(BlockLoader.defcCasingBlock, 10),
                    block(BlockLoader.defcCasingBlock, 11), block(BlockLoader.defcCasingBlock, 12),
                    block(NailongCasings.nailongCasing, NailongCasingMeta.SUBSPACE_SHIELD_CASING) };
            case 'D':
                return new BlockMeta[] { block(ItemRegistry.bw_realglas, 6), block(ItemRegistry.bw_realglas, 6),
                    block(sBlockGlass1, 2),
                    block(NailongCasings.nailongCasing, NailongCasingMeta.ENDLESS_PSIONIC_GLASS),
                    block(NailongCasings.nailongCasing, NailongCasingMeta.SPACETIME_POTENTIAL_GLASS) };
            case 'E':
                return new BlockMeta[] { block(sBlockCasings9, 11), block(sBlockCasings9, 11),
                    block(sBlockCasings9, 11), block(gravityStabilizationCasing, 0),
                    block(gravityStabilizationCasing, 0) };
            case 'H':
                return new BlockMeta[] { block(magicCasing, 0), block(magicCasing, 0), block(magicCasing, 0),
                    block(ModBlocks.blockCasings6Misc, 0),
                    block(NailongCasings.nailongCasing, NailongCasingMeta.PSYCHIC_WRAPPED_CASING) };
            case 'G':
                return new BlockMeta[] { block(WerkstoffLoader.BWBlockCasingsAdvanced, TAIRITSU_ADVANCED_CASING_META),
                    block(WerkstoffLoader.BWBlockCasingsAdvanced, TAIRITSU_ADVANCED_CASING_META),
                    block(WerkstoffLoader.BWBlockCasingsAdvanced, METASTABLE_OGANESSON_ADVANCED_CASING_META),
                    block(NailongCasings.nailongCasing, NailongCasingMeta.PSIONIC_REALITY_CASING),
                    block(NailongCasings.nailongCasing, NailongCasingMeta.VOID_STABILIZATION_CASING) };
            default:
                return null;
        }
    }

    private static BlockMeta pickTier(int tier, BlockMeta... blocks) {
        if (blocks == null || blocks.length == 0) return null;
        return blocks[Math.max(0, Math.min(blocks.length - 1, tier - 1))];
    }

    private static IStructureElement<OTEVoidNexusMatrix> tieredElement(int slot, BlockMeta... tiers) {
        List<Pair<net.minecraft.block.Block, Integer>> hints = new ArrayList<>(tiers.length);
        for (BlockMeta tier : tiers) hints.add(Pair.of(tier.block, tier.meta));
        return StructureUtility.ofBlocksTiered((block, meta) -> {
            for (int i = 0; i < tiers.length; i++) {
                if (tiers[i].block == block && tiers[i].meta == meta) return i + 1;
            }
            return null;
        }, hints, 0, (machine, tier) -> machine.upgradeVariants[slot] = tier, machine -> machine.upgradeVariants[slot]);
    }

    private static IStructureElement<OTEVoidNexusMatrix> metadataAwareTieredElement(int slot, BlockMeta... tiers) {
        return new IStructureElement<>() {

            @Override
            public boolean check(OTEVoidNexusMatrix machine, World world, int x, int y, int z) {
                int tier = findMetadataAwareTier(world, x, y, z, tiers);
                if (tier < 0) return false;
                machine.upgradeVariants[slot] = tier + 1;
                return true;
            }

            @Override
            public boolean couldBeValid(OTEVoidNexusMatrix machine, World world, int x, int y, int z,
                ItemStack trigger) {
                return findMetadataAwareTier(world, x, y, z, tiers) >= 0;
            }

            @Override
            public boolean spawnHint(OTEVoidNexusMatrix machine, World world, int x, int y, int z, ItemStack trigger) {
                BlockMeta target = tierForTrigger(tiers, trigger);
                if (target == null) return false;
                StructureLibAPI.hintParticle(world, x, y, z, target.block, target.meta);
                return true;
            }

            @Override
            public boolean placeBlock(OTEVoidNexusMatrix machine, World world, int x, int y, int z, ItemStack trigger) {
                BlockMeta target = tierForTrigger(tiers, trigger);
                if (target == null) return false;
                placePreviewBlock(world, x, y, z, target);
                return true;
            }

            @Override
            public IStructureElement.BlocksToPlace getBlocksToPlace(OTEVoidNexusMatrix machine, World world, int x,
                int y, int z, ItemStack trigger, AutoPlaceEnvironment env) {
                BlockMeta target = tierForTrigger(tiers, trigger);
                return target == null ? IStructureElement.BlocksToPlace.errored
                    : IStructureElement.BlocksToPlace.create(target.block, target.meta);
            }

            @Override
            public IStructureElement.PlaceResult survivalPlaceBlock(OTEVoidNexusMatrix machine, World world, int x,
                int y, int z, ItemStack trigger, AutoPlaceEnvironment env) {
                if (check(machine, world, x, y, z)) return IStructureElement.PlaceResult.SKIP;
                BlockMeta target = tierForTrigger(tiers, trigger);
                if (target == null) return IStructureElement.PlaceResult.REJECT;
                IStructureElement.PlaceResult result = StructureUtility.survivalPlaceBlock(
                    target.block,
                    target.meta,
                    world,
                    x,
                    y,
                    z,
                    env.getSource(),
                    env.getActor(),
                    env.getChatter());
                setGeneratedBlockMetadata(world, x, y, z, target.meta);
                return result;
            }
        };
    }

    private static IStructureElement<OTEVoidNexusMatrix> voidNexusGlassElement() {
        BlockMeta[] allowed = tierPalette('D');
        return new IStructureElement<>() {

            @Override
            public boolean check(OTEVoidNexusMatrix machine, World world, int x, int y, int z) {
                return isAllowedGlass(world.getBlock(x, y, z), world.getBlockMetadata(x, y, z), allowed);
            }

            @Override
            public boolean couldBeValid(OTEVoidNexusMatrix machine, World world, int x, int y, int z,
                ItemStack trigger) {
                return check(machine, world, x, y, z);
            }

            @Override
            public boolean spawnHint(OTEVoidNexusMatrix machine, World world, int x, int y, int z, ItemStack trigger) {
                BlockMeta target = tierForTrigger(allowed, trigger);
                if (target == null) return false;
                StructureLibAPI.hintParticle(world, x, y, z, target.block, target.meta);
                return true;
            }

            @Override
            public boolean placeBlock(OTEVoidNexusMatrix machine, World world, int x, int y, int z, ItemStack trigger) {
                BlockMeta target = tierForTrigger(allowed, trigger);
                if (target == null) return false;
                placePreviewBlock(world, x, y, z, target);
                return true;
            }

            @Override
            public IStructureElement.BlocksToPlace getBlocksToPlace(OTEVoidNexusMatrix machine, World world, int x,
                int y, int z, ItemStack trigger, AutoPlaceEnvironment env) {
                BlockMeta target = tierForTrigger(allowed, trigger);
                return target == null ? IStructureElement.BlocksToPlace.errored
                    : IStructureElement.BlocksToPlace.create(target.block, target.meta);
            }

            @Override
            public IStructureElement.PlaceResult survivalPlaceBlock(OTEVoidNexusMatrix machine, World world, int x,
                int y, int z, ItemStack trigger, AutoPlaceEnvironment env) {
                if (check(machine, world, x, y, z)) return IStructureElement.PlaceResult.SKIP;
                BlockMeta target = tierForTrigger(allowed, trigger);
                if (target == null) return IStructureElement.PlaceResult.REJECT;
                return StructureUtility.survivalPlaceBlock(
                    target.block,
                    target.meta,
                    world,
                    x,
                    y,
                    z,
                    env.getSource(),
                    env.getActor(),
                    env.getChatter());
            }
        };
    }

    private static boolean isAllowedGlass(net.minecraft.block.Block block, int meta, BlockMeta[] allowed) {
        for (BlockMeta candidate : allowed) {
            if (candidate.block == block && candidate.meta == meta) return true;
        }
        return false;
    }

    private static int findMetadataAwareTier(World world, int x, int y, int z, BlockMeta... tiers) {
        net.minecraft.block.Block worldBlock = world.getBlock(x, y, z);
        int worldMeta = world.getBlockMetadata(x, y, z);
        if (worldBlock == WerkstoffLoader.BWBlockCasingsAdvanced) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (!(tile instanceof TileEntityMetaGeneratedBlock generatedBlock)) return -1;
            worldMeta = generatedBlock.mMetaData & 0xFFFF;
        }
        for (int i = 0; i < tiers.length; i++) {
            if (tiers[i].block == worldBlock && tiers[i].meta == worldMeta) return i;
        }
        return -1;
    }

    private static BlockMeta tierForTrigger(BlockMeta[] tiers, ItemStack trigger) {
        if (tiers == null || tiers.length == 0) return null;
        int stackSize = trigger == null ? 1 : trigger.stackSize;
        return tiers[Math.max(0, Math.min(tiers.length - 1, stackSize - 1))];
    }

    private static void setGeneratedBlockMetadata(World world, int x, int y, int z, int meta) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityMetaGeneratedBlock generatedBlock) {
            generatedBlock.mMetaData = (short) meta;
        }
    }

    private static void placePsionicModulePreview(World world, int x, int y, int z, int hostX, int hostY, int hostZ) {
        BlockMeta module = previewBlock('A', 1);
        if (module != null) placePreviewBlock(world, x, y, z, module);
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof IGregTechTileEntity machine)
            || !(machine.getMetaTileEntity() instanceof OTEPsionicManifestationModule)) {
            placePreviewBlock(world, x, y, z, block(magicCasing, 0));
            return;
        }
        int dx = hostX - x;
        int dz = hostZ - z;
        ForgeDirection facing;
        if (Math.abs(dx) >= Math.abs(dz) && dx != 0) {
            facing = dx > 0 ? ForgeDirection.EAST : ForgeDirection.WEST;
        } else {
            facing = dz > 0 ? ForgeDirection.SOUTH : ForgeDirection.NORTH;
        }
        machine.setFrontFacing(facing);
    }

    private static final class PsionicModuleSlotElement implements IStructureElement<OTEVoidNexusMatrix> {

        @Override
        public boolean check(OTEVoidNexusMatrix machine, World world, int x, int y, int z) {
            net.minecraft.block.Block block = world.getBlock(x, y, z);
            if (block == BlockList.BloodyIchorium.block && world.getBlockMetadata(x, y, z) == 0) return true;
            TileEntity tile = world.getTileEntity(x, y, z);
            if (!(tile instanceof IGregTechTileEntity)) return false;
            IMetaTileEntity metaTile = ((IGregTechTileEntity) tile).getMetaTileEntity();
            if (!(metaTile instanceof OTEPsionicManifestationModule module)) return false;
            machine.addPsionicModule(module);
            return true;
        }

        @Override
        public boolean couldBeValid(OTEVoidNexusMatrix machine, World world, int x, int y, int z, ItemStack trigger) {
            if (world.getBlock(x, y, z) == BlockList.BloodyIchorium.block && world.getBlockMetadata(x, y, z) == 0)
                return true;
            TileEntity tile = world.getTileEntity(x, y, z);
            return tile instanceof IGregTechTileEntity
                && ((IGregTechTileEntity) tile).getMetaTileEntity() instanceof OTEPsionicManifestationModule;
        }

        @Override
        public boolean spawnHint(OTEVoidNexusMatrix machine, World world, int x, int y, int z, ItemStack trigger) {
            BlockMeta selected = selectedBlock(world);
            StructureLibAPI.hintParticle(world, x, y, z, selected.block, selected.meta);
            return true;
        }

        @Override
        public boolean placeBlock(OTEVoidNexusMatrix machine, World world, int x, int y, int z, ItemStack trigger) {
            BlockMeta selected = selectedBlock(world);
            placePreviewBlock(world, x, y, z, selected);
            return true;
        }

        @Override
        public BlocksToPlace getBlocksToPlace(OTEVoidNexusMatrix machine, World world, int x, int y, int z,
            ItemStack trigger, AutoPlaceEnvironment env) {
            ItemStack selected = selectedStack(world);
            return selected == null ? BlocksToPlace.create(BlockList.BloodyIchorium.block, 0)
                : BlocksToPlace.create(selected);
        }

        @Override
        public PlaceResult survivalPlaceBlock(OTEVoidNexusMatrix machine, World world, int x, int y, int z,
            ItemStack trigger, AutoPlaceEnvironment env) {
            if (check(machine, world, x, y, z)) return PlaceResult.SKIP;
            ItemStack selected = selectedStack(world);
            if (selected == null) return PlaceResult.REJECT;
            return StructureUtility.survivalPlaceBlock(
                selected,
                NBTMode.EXACT,
                selected.stackTagCompound,
                true,
                world,
                x,
                y,
                z,
                env.getSource(),
                env.getActor(),
                env.getChatter());
        }

        private ItemStack selectedStack(World world) {
            if (isPreviewWorld(world) && OTHItemList.PsionicManifestationModule.hasBeenSet()) {
                ItemStack module = OTHItemList.PsionicManifestationModule.get(1);
                if (module != null) return module;
            }
            return new ItemStack(BlockList.BloodyIchorium.block, 1, 0);
        }

        private BlockMeta selectedBlock(World world) {
            if (isPreviewWorld(world) && OTHItemList.PsionicManifestationModule.hasBeenSet()) {
                net.minecraft.block.Block block = OTHItemList.PsionicManifestationModule.getBlock();
                if (block != null) {
                    return new BlockMeta(block, OTHItemList.PsionicManifestationModule.getMeta());
                }
            }
            return new BlockMeta(BlockList.BloodyIchorium.block, 0);
        }
    }

    private static final class BlockMeta {

        private final net.minecraft.block.Block block;
        private final int meta;

        private BlockMeta(net.minecraft.block.Block block, int meta) {
            this.block = block;
            this.meta = meta;
        }
    }

    private static final class PreviewCell {

        private final int a;
        private final int b;
        private final int c;
        private final char symbol;

        private PreviewCell(int a, int b, int c, char symbol) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.symbol = symbol;
        }
    }

    private boolean validateUpgradeVariants(List<StructureError> errors) {
        int level = upgradeVariants[0];
        if (level < 1 || level > MAX_STRUCTURE_TIER) {
            errors.add(StructureErrors.of("ote.vnm.struct.upgrade_mismatch"));
            return false;
        }
        for (int slot = 0; slot < TIERED_SLOT_SYMBOLS.length; slot++) {
            int expected = canonicalTierToken(TIERED_SLOT_SYMBOLS[slot], level);
            if (upgradeVariants[TIERED_SLOT_INDEXES[slot]] != expected) {
                errors.add(StructureErrors.of("ote.vnm.struct.upgrade_mismatch"));
                return false;
            }
        }
        return true;
    }

    private static int canonicalTierToken(char symbol, int level) {
        BlockMeta[] palette = tierPalette(symbol);
        if (palette == null || level < 1 || level > palette.length) return -1;
        BlockMeta selected = palette[level - 1];
        for (int i = 0; i < palette.length; i++) {
            BlockMeta candidate = palette[i];
            if (candidate.block == selected.block && candidate.meta == selected.meta) return i + 1;
        }
        return -1;
    }

    private static IStructureElement<OTEVoidNexusMatrix> snowHatchElement() {
        return new PositionAwareSnowElement(controllerSnowHatchElement());
    }

    private static IStructureElement<OTEVoidNexusMatrix> controllerSnowHatchElement() {
        return ofChain(
            buildHatchAdder(OTEVoidNexusMatrix.class)
                .atLeast(
                    InputBus,
                    InputHatch,
                    OutputBus,
                    OutputHatch,
                    Energy,
                    ExoticEnergy,
                    MultiAmpEnergy,
                    Dynamo,
                    LaserSource)
                .adder(OTEVoidNexusMatrix::addToMachineList)
                .hint(1)
                .casingIndex(GTUtility.getCasingTextureIndex(sBlockCasings8, 8))
                .build(),
            ofBlock(sBlockCasings8, 8));
    }

    /**
     * The four module platforms also use arcane casings as their structure. A hatch installed there
     * belongs to the module, not to the controller. Mirror the space elevator's ignored-hatch
     * approach: accept the casing replacement without adding it to the controller's hatch lists.
     * Only the 24 casings surrounding the main controller are functional controller hatch
     * positions; the single casing directly touching the controller stays a fixed anchor.
     */
    private static final class PositionAwareSnowElement implements IStructureElement<OTEVoidNexusMatrix> {

        private final IStructureElement<OTEVoidNexusMatrix> mainHatches;

        private PositionAwareSnowElement(IStructureElement<OTEVoidNexusMatrix> mainHatches) {
            this.mainHatches = mainHatches;
        }

        @Override
        public boolean check(OTEVoidNexusMatrix machine, World world, int x, int y, int z) {
            if (isMainHatchPosition(machine, x, y, z)) {
                return mainHatches.check(machine, world, x, y, z);
            }
            return isModuleHatchOrCasing(world, x, y, z);
        }

        @Override
        public boolean couldBeValid(OTEVoidNexusMatrix machine, World world, int x, int y, int z, ItemStack trigger) {
            if (isMainHatchPosition(machine, x, y, z)) {
                return mainHatches.couldBeValid(machine, world, x, y, z, trigger);
            }
            return isModuleHatchOrCasing(world, x, y, z);
        }

        @Override
        public boolean spawnHint(OTEVoidNexusMatrix machine, World world, int x, int y, int z, ItemStack trigger) {
            if (isMainHatchPosition(machine, x, y, z)) {
                return mainHatches.spawnHint(machine, world, x, y, z, trigger);
            }
            StructureLibAPI.hintParticle(world, x, y, z, sBlockCasings8, 8);
            return true;
        }

        @Override
        public boolean placeBlock(OTEVoidNexusMatrix machine, World world, int x, int y, int z, ItemStack trigger) {
            if (isMainHatchPosition(machine, x, y, z)) {
                return mainHatches.placeBlock(machine, world, x, y, z, trigger);
            }
            placePreviewBlock(world, x, y, z, block(sBlockCasings8, 8));
            return true;
        }

        @Override
        public IStructureElement.BlocksToPlace getBlocksToPlace(OTEVoidNexusMatrix machine, World world, int x, int y,
            int z, ItemStack trigger, AutoPlaceEnvironment env) {
            if (isMainHatchPosition(machine, x, y, z)) {
                return mainHatches.getBlocksToPlace(machine, world, x, y, z, trigger, env);
            }
            return IStructureElement.BlocksToPlace.create(sBlockCasings8, 8);
        }

        @Override
        public IStructureElement.PlaceResult survivalPlaceBlock(OTEVoidNexusMatrix machine, World world, int x, int y,
            int z, ItemStack trigger, AutoPlaceEnvironment env) {
            if (isMainHatchPosition(machine, x, y, z)) {
                return mainHatches.survivalPlaceBlock(machine, world, x, y, z, trigger, env);
            }
            if (isModuleHatchOrCasing(world, x, y, z)) {
                return IStructureElement.PlaceResult.SKIP;
            }
            return StructureUtility.survivalPlaceBlock(
                sBlockCasings8,
                8,
                world,
                x,
                y,
                z,
                env.getSource(),
                env.getActor(),
                env.getChatter());
        }
    }

    private static boolean isMainHatchPosition(OTEVoidNexusMatrix machine, int x, int y, int z) {
        IGregTechTileEntity base = machine.getBaseMetaTileEntity();
        if (base == null) return false;
        int distance = Math.abs(x - base.getXCoord()) + Math.abs(y - base.getYCoord()) + Math.abs(z - base.getZCoord());
        return distance >= 2 && distance <= 6;
    }

    private static boolean isModuleHatchOrCasing(World world, int x, int y, int z) {
        if (world.getBlock(x, y, z) == sBlockCasings8 && world.getBlockMetadata(x, y, z) == 8) {
            return true;
        }
        TileEntity tile = world.getTileEntity(x, y, z);
        return tile instanceof IGregTechTileEntity
            && ((IGregTechTileEntity) tile).getMetaTileEntity() instanceof MTEHatch;
    }

    // ===================== Tooltip =====================

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tooltip = new MultiblockTooltipBuilder();
        tooltip.addMachineType(translateToLocal("ote.tt.vnm"));
        for (int line = 1; line <= VOID_NEXUS_TOOLTIP_LINES; line++) {
            tooltip.addInfo(translateToLocal("ote.tm.vnm." + line));
        }
        tooltip.addTecTechHatchInfo()
            .addSeparator()
            .addInfo("§aAdded by: 123Technology - Void Nexus Matrix")
            .addController(translateToLocal("ote.tt.vnm"))
            .beginStructureBlock(266, 233, 266, false)
            .addInputBus(translateToLocal("oth.hint.any_input_bus"), 1)
            .addOutputBus(translateToLocal("oth.hint.any_output_bus"), 1)
            .addEnergyHatch(translateToLocal("ote.vnm.struct.module_power"), 1)
            .addDynamoHatch(translateToLocal("ote.vnm.struct.laser_only"), 1)
            .toolTipFinisher();
        return tooltip;
    }

    // ===================== Textures =====================

    @Override
    public ITexture[] getTexture(IGregTechTileEntity base, ForgeDirection side, ForgeDirection facing, int colorIndex,
        boolean active, boolean redstone) {
        return CONTROLLER_TEXTURES.get(side, facing, active);
    }

    // ===================== Player Interaction =====================

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer player, float x, float y, float z,
        ItemStack tool) {
        if (!getBaseMetaTileEntity().isServerSide()) return;
        if (player.isSneaking()) {
            wirelessMode = hasUpgrade(ENTER_THE_VOID) && !wirelessMode;
        } else {
            setMaterialInputModeEnabled(!isMaterialInputModeEnabled());
        }
        player.addChatMessage(
            new net.minecraft.util.ChatComponentTranslation(
                "ote.vnm.mode",
                wirelessMode ? translateToLocal("ote.vnm.wireless") : translateToLocal("ote.vnm.laser"),
                isMaterialInputModeEnabled() ? translateToLocal("ote.vnm.material")
                    : translateToLocal("ote.vnm.essentia")));
    }

    // ===================== Waila =====================

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        tag.setString("vnPower", GTUtility.formatShortenedLong(activeGeneratedPower));
        tag.setString("vnStage", stage.name());
        tag.setLong("vnPotential", virtualPotential);
        tag.setDouble("vnDeviation", lastDeviation);
        tag.setDouble("vnAverageHeat", lastAverageHeat);
        tag.setLong("vnParallel", getMaxVoidParallel());
        tag.setLong("vnConsumedEssentia", lastConsumedEssentia);
        tag.setInteger("vnModules", getConnectedPsionicModuleCount());
        tag.setInteger("vnU26Bonus", u26BonusPercent);
        tag.setBoolean("vnWireless", wirelessMode);
        tag.setBoolean("vnMaterial", isMaterialInputModeEnabled());
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        NBTTagCompound tag = accessor.getNBTData();
        currentTip.add(
            translateToLocal("ote.vnm.waila.power") + ": "
                + EnumChatFormatting.GOLD
                + tag.getString("vnPower")
                + " EU/t");
        currentTip
            .add(translateToLocal("ote.vnm.waila.stage") + ": " + EnumChatFormatting.AQUA + tag.getString("vnStage"));
        currentTip.add(
            translateToLocal("ote.vnm.waila.potential") + ": "
                + EnumChatFormatting.LIGHT_PURPLE
                + tag.getLong("vnPotential")
                + " / "
                + MAX_VIRTUAL_POTENTIAL);
        currentTip.add(
            translateToLocal("ote.vnm.waila.deviation") + ": "
                + EnumChatFormatting.YELLOW
                + String.format("%.4f", tag.getDouble("vnDeviation")));
        currentTip.add(
            translateToLocal("ote.vnm.waila.heat") + ": "
                + EnumChatFormatting.GOLD
                + String.format("%.2f", tag.getDouble("vnAverageHeat")));
        currentTip.add(
            translateToLocal("ote.vnm.waila.parallel") + ": "
                + EnumChatFormatting.AQUA
                + GTUtility.formatShortenedLong(tag.getLong("vnConsumedEssentia"))
                + " / "
                + GTUtility.formatShortenedLong(tag.getLong("vnParallel")));
        currentTip.add(
            translateToLocal("ote.vnm.waila.modules") + ": "
                + EnumChatFormatting.LIGHT_PURPLE
                + tag.getInteger("vnModules"));
        currentTip.add(
            translateToLocal("ote.vnm.waila.u26") + ": "
                + EnumChatFormatting.LIGHT_PURPLE
                + tag.getInteger("vnU26Bonus")
                + "%");
    }

    // ===================== Machine Status =====================

    public long getVirtualPotential() {
        return virtualPotential;
    }

    public long getPsionicAcceleration() {
        return psionicAcceleration;
    }

    public double getPsionicSpeedBonusPercent() {
        double bonus = 0.0D;
        if (hasUpgrade(DARK_RIVER)) bonus += psionicAcceleration;
        if (hasUpgrade(OUTER_PLANE_GUEST))
            bonus += VoidNexusCalculator.getVirtualPotentialSpeedPercent(virtualPotential);
        return bonus;
    }

    public int getU26BonusPercent() {
        return u26BonusPercent;
    }

    public long getActivePower() {
        return activePower;
    }

    public long getGeneratedPower() {
        return activeGeneratedPower;
    }

    public long getLastConsumedEssentia() {
        return lastConsumedEssentia;
    }

    public double getLastDeviation() {
        return lastDeviation;
    }

    public boolean isStarArrayActive() {
        return activeStarArray;
    }

    public boolean isStargateCoreDetected() {
        return hasStargateCoreCrystal();
    }

    public double getLastAverageHeat() {
        return lastAverageHeat;
    }

    public int getStructureTier() {
        return structureTier;
    }

    public String getStageName() {
        return stage.name();
    }

    public int getStructureScanTier() {
        return MAX_STRUCTURE_TIER;
    }

    public int getConnectedPsionicModuleCount() {
        return Math.min(psionicModules.size(), getPsionicModuleLimit());
    }

    // ===================== Rendering =====================

    @Override
    @SideOnly(Side.CLIENT)
    public void renderTESR(double x, double y, double z, float partialTicks) {
        OTEVoidNexusClientEffects.renderMatrixAt(this, x, y, z, partialTicks);
    }

    @Override
    public void encodeRenderData(ByteBuf buffer) {
        buffer.writeBoolean(isRenderActiveOnServer());
    }

    @Override
    public void decodeRenderData(ByteArrayDataInput buffer) {
        clientRenderActive = buffer.readBoolean();
        registerClientRenderEffect();
    }

    @SideOnly(Side.CLIENT)
    private void registerClientRenderEffect() {
        com.newmaa.othtech.client.OTEVoidNexusClientEffects.registerMatrix(this);
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return 120.0D * 120.0D;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox(int x, int y, int z) {
        return AxisAlignedBB.getBoundingBox(x - 2D, y, z - 2D, x + 3D, y + 6D, z + 3D);
    }

    private boolean isRenderActiveOnServer() {
        return voidAnimationEnabled && mMaxProgresstime > 0
            && getBaseMetaTileEntity() != null
            && getBaseMetaTileEntity().isActive();
    }

    public boolean isClientRenderActive() {
        return clientRenderActive;
    }

    // ===================== Upgrade Status and Machine Settings =====================

    public boolean isUpgradeActive(VoidNexusUpgrade upgrade) {
        return hasUpgrade(upgrade);
    }

    public long getUpgradeMask() {
        return purchasedUpgradeMask;
    }

    public String getUpgradeStatusKey() {
        return upgradeStatusKey;
    }

    public String getPotentialTransferStatusKey() {
        return potentialTransferStatusKey;
    }

    public boolean isWirelessModeEnabled() {
        return wirelessMode;
    }

    public void setWirelessModeEnabled(boolean enabled) {
        wirelessMode = enabled && hasUpgrade(ENTER_THE_VOID);
    }

    public boolean isMaterialInputModeEnabled() {
        return materialInputMode && hasUpgrade(EARTH_SHAPER);
    }

    public void setMaterialInputModeEnabled(boolean enabled) {
        boolean nextMode = enabled && hasUpgrade(EARTH_SHAPER);
        if (nextMode == materialInputMode) return;
        materialInputMode = nextMode;
        scheduleRecipeCheckImmediate();
        markDirty();
    }

    public boolean isVoidAnimationEnabled() {
        return voidAnimationEnabled;
    }

    public void setVoidAnimationEnabled(boolean enabled) {
        voidAnimationEnabled = enabled;
    }

    public boolean isAutomaticPotentialOutputEnabled() {
        return automaticPotentialOutput;
    }

    public void setAutomaticPotentialOutputEnabled(boolean enabled) {
        automaticPotentialOutput = enabled && hasUpgrade(FIND_ONES_OWN_PATH);
    }

    public int getHostWandVisPoints() {
        ItemStack stack = getHostWand();
        if (stack == null) return 0;
        return ((ItemWandCasting) stack.getItem()).getAllVis(stack)
            .visSize() / 100;
    }

    // ===================== GUI =====================

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new OTEVoidNexusMatrixGui(this);
    }

    @Override
    protected boolean useMui2() {
        return true;
    }
}
