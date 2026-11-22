package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static gregtech.api.GregTechAPI.sBlockCasings8;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTUtility.filterValidMTEs;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.common.compat.thaumcraft.VoidNexusUpgrade;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.machine.gui.OTEPsionicManifestationModuleGui;
import com.newmaa.othtech.machine.machineclass.OTHTTMultiMachineBaseEM;
import com.newmaa.othtech.machine.machineclass.voidnexus.PsionicOutputQueue;
import com.newmaa.othtech.machine.machineclass.voidnexus.VoidNexusCalculator;
import com.newmaa.othtech.machine.machineclass.voidnexus.VoidNexusControllerTextures;

import appeng.api.networking.IGrid;
import appeng.me.GridAccessException;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.NaniteTier;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.api.util.ParallelHelper;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gregtech.common.tileentities.machines.MTEHatchInputBusME;

public class OTEPsionicManifestationModule extends OTHTTMultiMachineBaseEM
    implements IConstructable, ISurvivalConstructable {

    private static final VoidNexusControllerTextures CONTROLLER_TEXTURES = VoidNexusControllerTextures.MODULE;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final int HORIZONTAL_OFFSET = 2;
    private static final int VERTICAL_OFFSET = 0;
    private static final int DEPTH_OFFSET = 2;
    private static final int INFINITY_OVERCLOCK_FLUID = 1_440;
    private static final int ADVANCED_RECIPE_MARKER = 1;
    private static final long MODULE_EU_BUFFER = 2_000_000_000_000_000L;

    private static final CheckRecipeResult MODULE_NOT_CONNECTED = SimpleCheckRecipeResult
        .ofFailurePersistOnShutdown("psionic_module_not_connected");
    private static final CheckRecipeResult PSIONIC_HARMONY_REQUIRED = SimpleCheckRecipeResult
        .ofFailurePersistOnShutdown("psionic_module_harmony_required");
    private static final CheckRecipeResult ADVANCED_RECIPE_LOCKED = SimpleCheckRecipeResult
        .ofFailurePersistOnShutdown("psionic_module_advanced_locked");
    private static final CheckRecipeResult INSUFFICIENT_BUFFER = SimpleCheckRecipeResult
        .ofFailurePersistOnShutdown("psionic_module_insufficient_buffer");

    private static IStructureDefinition<OTEPsionicManifestationModule> STRUCTURE_DEFINITION;

    private OTEVoidNexusMatrix host;
    private FluidStack pendingOverclockFluid;
    private int pendingOverclockAmountPerStep;
    private int pendingOverclockRounds;
    private int pendingOverclockDivisor = 2;
    private OverclockCalculator lastOverclockCalculator;
    private int lastOverclockSteps;
    private int lastOverclockDivisor = 2;
    private final PsionicOutputQueue pendingOutputs = new PsionicOutputQueue();
    private boolean recipePreflight;

    public OTEPsionicManifestationModule(int id, String name, String regionalName) {
        super(id, name, regionalName);
        useLongPower = true;
    }

    public OTEPsionicManifestationModule(String name) {
        super(name);
        useLongPower = true;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return Recipemaps.PSIONIC_MANIFESTATION;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new OTEPsionicManifestationModule(mName);
    }

    public void connect(OTEVoidNexusMatrix host) {
        this.host = host;
    }

    public void disconnect() {
        host = null;
        clearPendingOverclock();
    }

    public boolean isConnected() {
        return host != null;
    }

    public boolean ownsHatch(MTEHatch hatch) {
        return hatch != null
            && (mInputBusses.contains(hatch) || mInputHatches.contains(hatch) || mOutputBusses.contains(hatch));
    }

    public long increaseStoredEU(long maximumIncrease) {
        IGregTechTileEntity base = getBaseMetaTileEntity();
        if (base == null || maximumIncrease <= 0L) return 0L;
        long increased = Math.min(maxEUStore() - base.getStoredEU(), maximumIncrease);
        return increased > 0L && base.increaseStoredEnergyUnits(increased, false) ? increased : 0L;
    }

    @Override
    protected void chargeController_EM(IGregTechTileEntity base) {
        // The linked Void Nexus Matrix charges this controller.
    }

    @Override
    public long maxEUStore() {
        if (!isConnected()) return MODULE_EU_BUFFER;
        return Math.max(MODULE_EU_BUFFER, host.getConnectedInputEnergyCapacityEU());
    }

    @Override
    public long getMaxInputVoltage() {
        return GTValues.V[13];
    }

    @Override
    public boolean drainEnergyInput(long euPerTick, long amperes) {
        long required = GTUtility.mulSafe(Math.abs(euPerTick), Math.max(1L, amperes));
        IGregTechTileEntity base = getBaseMetaTileEntity();
        if (base == null || base.getStoredEU() < required) return false;
        return base.decreaseStoredEnergyUnits(required, false);
    }

    @Override
    protected void setProcessingLogicPower(ProcessingLogic logic) {
        logic.setAvailableVoltage(GTValues.V[13]);
        long sustainableAmperage = Math.max(1L, getSustainablePowerShare() / GTValues.V[13]);
        logic.setAvailableAmperage(Math.min(getAvailableModuleAmperage(), sustainableAmperage));
        logic.setAmperageOC(true);
    }

    public long getAvailableModuleAmperage() {
        return Math.max(1L, getModuleStoredEU() / GTValues.V[13]);
    }

    @Override
    protected @NotNull CheckRecipeResult postCheckRecipe(@NotNull CheckRecipeResult result,
        @NotNull ProcessingLogic logic) {
        if (!result.wasSuccessful()) return result;
        long requiredPerTick = Math.max(0L, logic.getCalculatedEut());
        if (requiredPerTick > maxEUStore() || requiredPerTick > getModuleStoredEU()
            || requiredPerTick > getSustainablePowerShare()) {
            return INSUFFICIENT_BUFFER;
        }
        return result;
    }

    @Override
    protected void setEnergyUsage(ProcessingLogic logic) {
        long calculatedEut = Math.max(0L, logic.getCalculatedEut());
        lEUt = -calculatedEut;
        mEUt = 0;
    }

    @Override
    protected long getActualEnergyUsage() {
        return Math.max(0L, -lEUt);
    }

    @Override
    public boolean onRunningTick(ItemStack stack) {
        long usage = Math.max(0L, -lEUt);
        if (usage <= 0L) return true;
        return drainEnergyInput(usage, 1L);
    }

    @Override
    public void onPostTick(IGregTechTileEntity base, long tick) {
        if (!base.isServerSide()) return;
        if (!isConnected() && (mMachine || mMaxProgresstime > 0)) {
            stopMachine(gregtech.api.util.shutdown.ShutDownReasonRegistry.POWER_LOSS);
        }
        super.onPostTick(base, tick);
        if (tick % 20L == 0L && !pendingOutputs.isEmpty() && pendingOutputs.flush(mOutputBusses)) markDirty();
        if (tick % 200L == 0L) fixAllIssues();
    }

    @Override
    public void onRemoval() {
        disconnect();
        super.onRemoval();
    }

    @Override
    public void onUnload() {
        disconnect();
        super.onUnload();
    }

    @Override
    public void checkMachine(IGregTechTileEntity base, ItemStack stack, List<StructureError> errors) {
        clearStructureHatches();
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFFSET, VERTICAL_OFFSET, DEPTH_OFFSET, errors)) return;
        pruneDuplicateStockingInputBuses();
        repairMachine();
    }

    private void pruneDuplicateStockingInputBuses() {
        Set<Object> networks = Collections.newSetFromMap(new IdentityHashMap<>());
        List<MTEHatchInputBus> duplicates = new ArrayList<>();
        for (MTEHatchInputBus inputBus : filterValidMTEs(mInputBusses)) {
            if (!(inputBus instanceof MTEHatchInputBusME stockingBus)) continue;
            try {
                IGrid grid = stockingBus.getProxy()
                    .getGrid();
                if (grid != null && !networks.add(grid)) duplicates.add(inputBus);
            } catch (GridAccessException ignored) {}
        }
        mInputBusses.removeAll(duplicates);
    }

    private void clearStructureHatches() {
        mInputBusses.clear();
        mInputHatches.clear();
        mOutputBusses.clear();
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        repairMachine();
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFFSET, VERTICAL_OFFSET, DEPTH_OFFSET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            HORIZONTAL_OFFSET,
            VERTICAL_OFFSET,
            DEPTH_OFFSET,
            Math.max(1, elementBudget),
            env,
            false,
            true);
    }

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return new String[] { translateToLocal("ote.pmm.struct.description") };
    }

    @Override
    public IStructureDefinition<OTEPsionicManifestationModule> getStructure_EM() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTEPsionicManifestationModule>builder()
                .addShape(STRUCTURE_PIECE_MAIN, moduleShape())
                .addElement(
                    'B',
                    ofChain(
                        buildHatchAdder(OTEPsionicManifestationModule.class).atLeast(InputBus, InputHatch, OutputBus)
                            .adder(OTEPsionicManifestationModule::addToMachineList)
                            .casingIndex(GTUtility.getCasingTextureIndex(sBlockCasings8, 8))
                            .hint(1)
                            .build(),
                        ofBlock(sBlockCasings8, 8)))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    private static String[][] moduleShape() {
        return new String[][] { { "     ", "BBBBB" }, { "     ", "BBBBB" }, { "  ~  ", "BBBBB" }, { "     ", "BBBBB" },
            { "     ", "BBBBB" } };
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @Override
            public @NotNull CheckRecipeResult process() {
                setSpeedBonus(naniteTimeMultiplier());
                return super.process();
            }

            @Override
            protected @NotNull OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                OverclockCalculator calculator = super.createOverclockCalculator(recipe).setUnlimitedTierSkips();
                OverclockPlan plan = planOverclock();
                pendingOverclockFluid = plan.fluid;
                pendingOverclockAmountPerStep = plan.amountPerStep;
                pendingOverclockRounds = plan.steps;
                pendingOverclockDivisor = plan.durationDivisor;
                lastOverclockCalculator = calculator.setMaxOverclocks(plan.steps)
                    .setEUtIncreasePerOC(4.0D)
                    .setDurationDecreasePerOC(plan.durationDivisor)
                    .setAmperageOC(true);
                return lastOverclockCalculator;
            }

            @Override
            protected @NotNull CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (!isConnected()) return MODULE_NOT_CONNECTED;
                if (!host.isUpgradeActive(VoidNexusUpgrade.PSIONIC_HARMONY)) return PSIONIC_HARMONY_REQUIRED;
                if (recipe.mSpecialValue == ADVANCED_RECIPE_MARKER
                    && !host.isUpgradeActive(VoidNexusUpgrade.FIND_ONES_OWN_PATH)) return ADVANCED_RECIPE_LOCKED;
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @Override
            protected @NotNull ParallelHelper createParallelHelper(@NotNull GTRecipe recipe) {
                return super.createParallelHelper(recipe).setConsumption(!mRunningOnLoad && !recipePreflight);
            }
        }.setMaxParallelSupplier(this::getParallelLimit);
    }

    public int getParallelLimit() {
        if (!isConnected() || !host.isUpgradeActive(VoidNexusUpgrade.TOOL_CONTRACT)) return 1;
        long parallel = host.getGeneratedPower() / GTUtility.mulSafe(GTValues.V[15], 100L);
        return (int) Math.min(Integer.MAX_VALUE, Math.max(1L, parallel) + 4L);
    }

    @Override
    public @NotNull CheckRecipeResult checkProcessing_EM() {
        clearPendingOverclock();
        CheckRecipeResult result = postCheckRecipe(runRecipeCheck(true), processingLogic);
        if (!result.wasSuccessful()) {
            clearPendingOverclock();
            updateSlots();
            return result;
        }

        result = runRecipeCheck(false);
        if (!result.wasSuccessful()) {
            clearPendingOverclock();
            updateSlots();
            return result;
        }
        lastOverclockSteps = performedOverclockCount();
        lastOverclockDivisor = pendingOverclockDivisor;
        if (!mRunningOnLoad) consumeOverclockFluids(lastOverclockSteps);
        mEfficiency = 10_000;
        mEfficiencyIncrease = 10_000;
        mMaxProgresstime = processingLogic.getDuration();
        mOutputItems = processingLogic.getOutputItems();
        mOutputFluids = processingLogic.getOutputFluids();
        setEnergyUsage(processingLogic);
        updateSlots();
        return result;
    }

    private CheckRecipeResult runRecipeCheck(boolean preflight) {
        // ParallelHelper must leave inputs intact until postCheckRecipe accepts the preview.
        recipePreflight = preflight;
        try {
            setupProcessingLogic(processingLogic);
            return doCheckRecipe();
        } finally {
            recipePreflight = false;
        }
    }

    private OverclockPlan planOverclock() {
        if (!isConnected()) return OverclockPlan.EMPTY;
        List<FluidStack> available = getStoredFluids();
        if (available == null || available.isEmpty()) return OverclockPlan.EMPTY;

        if (host.isUpgradeActive(VoidNexusUpgrade.REFORGE_THE_VOID)) {
            OverclockPlan extreme = bestFuel(
                available,
                8,
                new Materials[] { Materials.Universium, Materials.Eternity, Materials.MHDCSM },
                new int[] { 28_800, 14_400, 5_670 });
            if (extreme.steps > 0) return extreme;
        }
        if (host.isUpgradeActive(VoidNexusUpgrade.ENDLESS_TIDE)) {
            OverclockPlan lossless = bestFuel(
                available,
                4,
                new Materials[] { Materials.SpaceTime },
                new int[] { INFINITY_OVERCLOCK_FLUID });
            if (lossless.steps > 0) return lossless;
        }
        if (host.isUpgradeActive(VoidNexusUpgrade.MOON_CONTRACT)) {
            OverclockPlan lossy = bestFuel(
                available,
                2,
                new Materials[] { Materials.Infinity },
                new int[] { INFINITY_OVERCLOCK_FLUID });
            if (lossy.steps > 0) return lossy;
        }
        return OverclockPlan.EMPTY;
    }

    private static OverclockPlan bestFuel(List<FluidStack> available, int durationDivisor, Materials[] materials,
        int[] amountPerStep) {
        int steps = 0;
        FluidStack selected = null;
        int selectedAmountPerStep = 0;
        for (int index = 0; index < materials.length; index++) {
            FluidStack template = materials[index].getMolten(1);
            if (template == null) continue;
            long availableAmount = 0L;
            for (FluidStack stored : available) {
                if (stored == null || !stored.isFluidEqual(template)) continue;
                availableAmount = GTUtility.addSafe(availableAmount, stored.amount);
            }
            int candidateSteps = (int) Math.min(Integer.MAX_VALUE, availableAmount / amountPerStep[index]);
            if (candidateSteps > steps) {
                steps = candidateSteps;
                selected = template.copy();
                selected.amount = amountPerStep[index];
                selectedAmountPerStep = amountPerStep[index];
            }
        }
        return selected == null ? OverclockPlan.EMPTY
            : new OverclockPlan(steps, durationDivisor, selected, selectedAmountPerStep);
    }

    private int performedOverclockCount() {
        if (lastOverclockCalculator == null || pendingOverclockRounds <= 0) return 0;
        try {
            return Math.min(pendingOverclockRounds, lastOverclockCalculator.getPerformedOverclocks());
        } catch (IllegalStateException ignored) {
            return 0;
        }
    }

    private void consumeOverclockFluids(int performedRounds) {
        if (pendingOverclockFluid == null || performedRounds <= 0 || pendingOverclockAmountPerStep <= 0) {
            clearPendingOverclock();
            return;
        }
        long consumed = Math.min(
            (long) pendingOverclockRounds * pendingOverclockAmountPerStep,
            (long) performedRounds * pendingOverclockAmountPerStep);
        FluidStack cost = pendingOverclockFluid.copy();
        cost.amount = (int) Math.min(Integer.MAX_VALUE, consumed);
        depleteInput(cost);
        clearPendingOverclock();
    }

    @Override
    public boolean addItemOutputs(ItemStack[] outputItems) {
        pendingOutputs.add(outputItems);
        pendingOutputs.flush(mOutputBusses);
        markDirty();
        return pendingOutputs.isEmpty();
    }

    private void clearPendingOverclock() {
        pendingOverclockFluid = null;
        pendingOverclockAmountPerStep = 0;
        pendingOverclockRounds = 0;
        pendingOverclockDivisor = 2;
        lastOverclockCalculator = null;
    }

    @Override
    public void saveNBTData(NBTTagCompound tag) {
        super.saveNBTData(tag);
        tag.setInteger("psionicLastOverclockSteps", lastOverclockSteps);
        tag.setInteger("psionicLastOverclockDivisor", lastOverclockDivisor);
        pendingOutputs.writeToNBT(tag);
    }

    @Override
    public void setItemNBT(NBTTagCompound tag) {
        super.setItemNBT(tag);
        pendingOutputs.writeToNBT(tag);
    }

    @Override
    public void loadNBTData(NBTTagCompound tag) {
        super.loadNBTData(tag);
        lastOverclockSteps = Math.max(0, tag.getInteger("psionicLastOverclockSteps"));
        lastOverclockDivisor = tag.hasKey("psionicLastOverclockDivisor")
            ? Math.max(2, tag.getInteger("psionicLastOverclockDivisor"))
            : 2;
        pendingOutputs.readFromNBT(tag);
    }

    private double naniteTimeMultiplier() {
        double speedBonusPercent = getTotalSpeedPercent();
        if (speedBonusPercent <= 0.0D) return 1.0D;
        return 1.0D / (1.0D + speedBonusPercent / 100.0D);
    }

    public int getNaniteTierLevel() {
        if (!isConnected() || !host.isUpgradeActive(VoidNexusUpgrade.WEAVE_CONTRACT)) return 0;
        NaniteTier tier = NaniteTier.fromStack(getControllerSlot());
        return tier == null ? 0 : tier.getTier();
    }

    public double getNaniteSpeedPercent() {
        return getNaniteTierLevel() * 20.0D;
    }

    public double getPotentialSpeedPercent() {
        return isConnected() && host.isUpgradeActive(VoidNexusUpgrade.OUTER_PLANE_GUEST)
            ? VoidNexusCalculator.getVirtualPotentialSpeedPercent(host.getVirtualPotential())
            : 0.0D;
    }

    public double getPermanentSpeedPercent() {
        return isConnected() && host.isUpgradeActive(VoidNexusUpgrade.DARK_RIVER) ? host.getPsionicAcceleration()
            : 0.0D;
    }

    public double getTotalSpeedPercent() {
        return getNaniteSpeedPercent() + getPotentialSpeedPercent() + getPermanentSpeedPercent();
    }

    public double getDurationMultiplier() {
        double speed = getTotalSpeedPercent();
        return speed <= 0.0D ? 1.0D : 1.0D / (1.0D + speed / 100.0D);
    }

    public double getFinalDurationMultiplier() {
        double multiplier = getDurationMultiplier();
        if (lastOverclockSteps <= 0 || lastOverclockDivisor <= 1) return multiplier;
        return multiplier / Math.pow(lastOverclockDivisor, lastOverclockSteps);
    }

    public long getModuleStoredEU() {
        IGregTechTileEntity base = getBaseMetaTileEntity();
        return base == null ? 0L : Math.max(0L, base.getStoredEU());
    }

    public long getModuleMaxEU() {
        return maxEUStore();
    }

    public long getCurrentRecipePower() {
        return Math.max(0L, -lEUt);
    }

    public long getHostInputStoredEU() {
        return isConnected() ? host.getConnectedInputEnergyStoredEU() : 0L;
    }

    public long getSustainablePowerShare() {
        return isConnected() ? host.getPsionicModulePowerShareEU() : 0L;
    }

    public int getLastOverclockSteps() {
        return lastOverclockSteps;
    }

    public int getLastOverclockDivisor() {
        return lastOverclockDivisor;
    }

    public String getHostStageName() {
        return isConnected() ? host.getStageName() : "NONE";
    }

    public int getHostStructureTier() {
        return isConnected() ? host.getStructureTier() : 0;
    }

    private static final class OverclockPlan {

        private static final OverclockPlan EMPTY = new OverclockPlan(0, 2, null, 0);

        private final int steps;
        private final int durationDivisor;
        private final FluidStack fluid;
        private final int amountPerStep;

        private OverclockPlan(int steps, int durationDivisor, FluidStack fluid, int amountPerStep) {
            this.steps = steps;
            this.durationDivisor = durationDivisor;
            this.fluid = fluid;
            this.amountPerStep = amountPerStep;
        }
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity base, ForgeDirection side, ForgeDirection facing, int colorIndex,
        boolean active, boolean redstone) {
        return CONTROLLER_TEXTURES.get(side, facing, active);
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new OTEPsionicManifestationModuleGui(this);
    }

    @Override
    protected boolean useMui2() {
        return true;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tooltip = new MultiblockTooltipBuilder();
        tooltip.addMachineType(translateToLocal("ote.tm.pmm.machine_type"))
            .addInfo(translateToLocal("ote.tm.pmm.1"))
            .addInfo(EnumChatFormatting.LIGHT_PURPLE + translateToLocal("ote.tm.pmm.2"))
            .addInfo(translateToLocal("ote.tm.pmm.3"))
            .addInfo(translateToLocal("ote.tm.pmm.4"))
            .addInfo(translateToLocal("ote.tm.pmm.5"))
            .addInputBus(translateToLocal("oth.hint.any_input_bus"), 1)
            .addInputHatch(translateToLocal("oth.hint.any_input_hatch"), 1)
            .addOutputBus(translateToLocal("oth.hint.any_output_bus"), 1)
            .beginStructureBlock(5, 2, 5, false)
            .addController(translateToLocal("ote.tm.pmm.controller"))
            .toolTipFinisher();
        return tooltip;
    }
}
