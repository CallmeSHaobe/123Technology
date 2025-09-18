package com.newmaa.othtech.machine.machineclass;

import static com.newmaa.othtech.utils.Utils.filterValidMTEs;
import static gregtech.GTMod.GT;
import static gregtech.api.enums.GTValues.VN;
import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.drawable.UITexture;
import com.gtnewhorizons.modularui.api.widget.IWidgetBuilder;
import com.gtnewhorizons.modularui.api.widget.Widget;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.newmaa.othtech.Config;
import com.newmaa.othtech.common.machinelogic.MachineLogic123;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.SoundResource;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchDynamo;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.metatileentity.implementations.MTEHatchMuffler;
import gregtech.api.metatileentity.implementations.MTEHatchMultiInput;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.threads.RunnableSound;
import gregtech.api.util.GTUtility;
import gregtech.client.GTSoundLoop;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gregtech.common.tileentities.machines.IDualInputInventory;
import gregtech.common.tileentities.machines.MTEHatchInputBusME;
import gregtech.common.tileentities.machines.MTEHatchInputME;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public abstract class OTHMultiMachineBase<T extends OTHMultiMachineBase<T>> extends MTEExtendedPowerMultiBlockBase<T>
    implements IConstructable, ISurvivalConstructable {

    // region Class Constructor
    public OTHMultiMachineBase(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTHMultiMachineBase(String aName) {
        super(aName);
    }

    // endregion

    // region new methods
    public void repairMachine() {
        mHardHammer = true;
        mScrewdriver = true;
        mCrowbar = true;
        mSolderingTool = true;
        mWrench = true;
    }

    // endregion

    // region Processing Logic

    /**
     * Creates logic to run recipe check based on recipemap. This runs only once, on class instantiation.
     * <p>
     * If this machine doesn't use recipemap or does some complex things, override {@link #checkProcessing()}.
     */
    @ApiStatus.OverrideOnly
    protected ProcessingLogic createProcessingLogic() {
        return new MachineLogic123() {

            @NotNull
            @Override
            public CheckRecipeResult process() {

                setEuModifier(getEuModifier());
                setSpeedBonus(getSpeedBonus());
                setOverclock(isEnablePerfectOverclock() ? 4 : 2, 4);
                return super.process();
            }

        }.setMaxParallelSupplier(this::getLimitedMaxParallel);
    }

    /**
     * Proxy Perfect Overclock Supplier.
     *
     * @return If true, enable Perfect Overclock.
     */
    protected abstract boolean isEnablePerfectOverclock();

    /**
     * Proxy Standard Eu Modifier Supplier.
     *
     * @return The value (or a method to get the value) of Eu Modifier (dynamically) .
     */
    @ApiStatus.OverrideOnly
    protected float getEuModifier() {
        return 1.0F;
    }

    /**
     * Proxy Standard Speed Multiplier Supplier.
     *
     * @return The value (or a method to get the value) of Speed Multiplier (dynamically) .
     */
    @ApiStatus.OverrideOnly
    protected abstract float getSpeedBonus();

    /**
     * Proxy Standard Parallel Supplier.
     *
     * @return The value (or a method to get the value) of Max Parallel (dynamically) .
     */
    @ApiStatus.OverrideOnly
    public abstract int getMaxParallelRecipes();

    /**
     * Limit the max parallel to prevent overflow.
     *
     * @return Limited parallel.
     */
    protected int getLimitedMaxParallel() {
        return getMaxParallelRecipes();
    }

    /**
     * Prevent overflow during power consumption calculation.
     *
     * @return Eu consumption per tick.
     */
    @Override
    protected long getActualEnergyUsage() {
        return (long) (-this.lEUt * (10000.0 / Math.max(1000, mEfficiency)));
    }

    /**
     * Checks recipe and setup machine if it's successful.
     * <p>
     * For generic machine working with recipemap, use {@link #createProcessingLogic()} to make use of shared codebase.
     */
    @Nonnull
    @Override
    public CheckRecipeResult checkProcessing() {
        // If no logic is found, try legacy checkRecipe
        if (processingLogic == null) {
            return checkRecipe(mInventory[1]) ? CheckRecipeResultRegistry.SUCCESSFUL
                : CheckRecipeResultRegistry.NO_RECIPE;
        }

        setupProcessingLogic(processingLogic);

        CheckRecipeResult result = doCheckRecipe();
        result = postCheckRecipe(result, processingLogic);
        // inputs are consumed at this point
        updateSlots();
        if (!result.wasSuccessful()) return result;

        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        mMaxProgresstime = processingLogic.getDuration();
        setEnergyUsage(processingLogic);

        mOutputItems = processingLogic.getOutputItems();
        mOutputFluids = processingLogic.getOutputFluids();

        return result;
    }

    /**
     * <p>
     * Get inputting items without DualInputHatch, and no separation mode.
     * <p>
     * Always used to get some special input items.
     *
     * @return The inputting items.
     */
    public ArrayList<ItemStack> getStoredInputsWithoutDualInputHatch() {

        ArrayList<ItemStack> rList = new ArrayList<>();
        for (MTEHatchInputBus tHatch : filterValidMTEs(mInputBusses)) {
            tHatch.mRecipeMap = getRecipeMap();
            IGregTechTileEntity tileEntity = tHatch.getBaseMetaTileEntity();
            for (int i = tileEntity.getSizeInventory() - 1; i >= 0; i--) {
                ItemStack itemStack = tileEntity.getStackInSlot(i);
                if (itemStack != null) {
                    rList.add(itemStack);
                }
            }
        }

        if (getStackInSlot(1) != null && getStackInSlot(1).getUnlocalizedName()
            .startsWith("gt.integrated_circuit")) rList.add(getStackInSlot(1));
        return rList;
    }

    public ArrayList<ItemStack> getStoredInputItemsWithDualInputHatch() {

        if (supportsCraftingMEBuffer() && !mDualInputHatches.isEmpty()) {
            for (IDualInputHatch dualInputHatch : mDualInputHatches) {
                Iterator<? extends IDualInputInventory> inventoryIterator = dualInputHatch.inventories();
                while (inventoryIterator.hasNext()) {
                    ItemStack[] items = inventoryIterator.next()
                        .getItemInputs();
                    if (items == null || items.length == 0) continue;

                    ArrayList<ItemStack> rList = new ArrayList<>();
                    for (int i = 0; i < items.length; i++) {
                        if (items[i] != null) {
                            rList.add(items[i]);
                        }
                    }
                    return rList;
                }
            }
        }

        ArrayList<ItemStack> rList = new ArrayList<>();
        for (MTEHatchInputBus tHatch : filterValidMTEs(mInputBusses)) {
            tHatch.mRecipeMap = getRecipeMap();
            IGregTechTileEntity tileEntity = tHatch.getBaseMetaTileEntity();
            for (int i = tileEntity.getSizeInventory() - 1; i >= 0; i--) {
                ItemStack itemStack = tileEntity.getStackInSlot(i);
                if (itemStack != null) {
                    rList.add(itemStack);
                }
            }
        }

        if (getStackInSlot(1) != null && getStackInSlot(1).getUnlocalizedName()
            .startsWith("gt.integrated_circuit")) rList.add(getStackInSlot(1));
        return rList;
    }

    /**
     * Forced get all input items, include all Dual Input Hatch slot.
     *
     * @return The items list.
     */
    public ArrayList<ItemStack> getStoredInputsNoSeparation() {
        ArrayList<ItemStack> rList = new ArrayList<>();

        if (supportsCraftingMEBuffer()) {
            for (IDualInputHatch dualInputHatch : mDualInputHatches) {
                Iterator<? extends IDualInputInventory> inventoryIterator = dualInputHatch.inventories();
                while (inventoryIterator.hasNext()) {
                    ItemStack[] items = inventoryIterator.next()
                        .getItemInputs();
                    if (items == null || items.length == 0) continue;

                    for (int i = 0; i < items.length; i++) {
                        if (items[i] != null) {
                            rList.add(items[i]);
                        }
                    }

                }
            }
        }

        Map<GTUtility.ItemId, ItemStack> inputsFromME = new HashMap<>();
        for (MTEHatchInputBus tHatch : GTUtility.filterValidMTEs(mInputBusses)) {
            tHatch.mRecipeMap = getRecipeMap();
            IGregTechTileEntity tileEntity = tHatch.getBaseMetaTileEntity();
            boolean isMEBus = tHatch instanceof MTEHatchInputBusME;
            for (int i = tileEntity.getSizeInventory() - 1; i >= 0; i--) {
                ItemStack itemStack = tileEntity.getStackInSlot(i);
                if (itemStack != null) {
                    if (isMEBus) {
                        // Prevent the same item from different ME buses from being recognized
                        inputsFromME.put(GTUtility.ItemId.createNoCopy(itemStack), itemStack);
                    } else {
                        rList.add(itemStack);
                    }
                }
            }
        }

        if (getStackInSlot(1) != null && getStackInSlot(1).getUnlocalizedName()
            .startsWith("gt.integrated_circuit")) rList.add(getStackInSlot(1));
        if (!inputsFromME.isEmpty()) {
            rList.addAll(inputsFromME.values());
        }
        return rList;
    }

    /**
     * Forced get all input fluids, include all Dual Input Hatch slot.
     *
     * @return ArrayList of all fluid stacks, contains fluid stacks in Crafting Input Hatch.
     */
    public ArrayList<FluidStack> getStoredFluidsWithDualInput() {
        ArrayList<FluidStack> rList = new ArrayList<>();
        Map<Fluid, FluidStack> inputsFromME = new HashMap<>();
        for (MTEHatchInput tHatch : GTUtility.filterValidMTEs(mInputHatches)) {
            setHatchRecipeMap(tHatch);
            if (tHatch instanceof MTEHatchMultiInput multiInputHatch) {
                for (FluidStack tFluid : multiInputHatch.getStoredFluid()) {
                    if (tFluid != null) {
                        rList.add(tFluid);
                    }
                }
            } else if (tHatch instanceof MTEHatchInputME meHatch) {
                for (FluidStack fluidStack : meHatch.getStoredFluids()) {
                    if (fluidStack != null) {
                        // Prevent the same fluid from different ME hatches from being recognized
                        inputsFromME.put(fluidStack.getFluid(), fluidStack);
                    }
                }
            } else {
                if (tHatch.getFillableStack() != null) {
                    rList.add(tHatch.getFillableStack());
                }
            }
        }

        if (!inputsFromME.isEmpty()) {
            rList.addAll(inputsFromME.values());
        }

        // get all fluids from Dual input
        if (supportsCraftingMEBuffer()) {
            for (IDualInputHatch dualInputHatch : mDualInputHatches) {
                Iterator<? extends IDualInputInventory> inventoryIterator = dualInputHatch.inventories();
                while (inventoryIterator.hasNext()) {
                    FluidStack[] fluids = inventoryIterator.next()
                        .getFluidInputs();
                    if (fluids == null || fluids.length == 0) continue;

                    for (int i = 0; i < fluids.length; i++) {
                        if (fluids[i] != null && fluids[i].amount > 0) {
                            rList.add(fluids[i]);
                        }
                    }

                }
            }
        }

        return rList;
    }

    // region Overrides
    @Override
    public String[] getInfoData() {
        long storedEnergy = 0;
        long maxEnergy = 0;
        for (MTEHatch tHatch : getExoticAndNormalEnergyHatchList()) {
            storedEnergy += tHatch.getBaseMetaTileEntity()
                .getStoredEU();
            maxEnergy += tHatch.getBaseMetaTileEntity()
                .getEUCapacity();
        }
        long voltage = getAverageInputVoltage();
        long amps = getMaxInputAmps();

        return new String[] {
            /* 1 */ StatCollector.translateToLocal("GT5U.multiblock.Progress") + ": "
                + EnumChatFormatting.GREEN
                + GTUtility.formatNumbers(mProgresstime / 20)
                + EnumChatFormatting.RESET
                + " s / "
                + EnumChatFormatting.YELLOW
                + GTUtility.formatNumbers(mMaxProgresstime / 20)
                + EnumChatFormatting.RESET
                + " s",
            /* 2 */ StatCollector.translateToLocal("GT5U.multiblock.energy") + ": "
                + EnumChatFormatting.GREEN
                + GTUtility.formatNumbers(storedEnergy)
                + EnumChatFormatting.RESET
                + " EU / "
                + EnumChatFormatting.YELLOW
                + GTUtility.formatNumbers(maxEnergy)
                + EnumChatFormatting.RESET
                + " EU",
            /* 3 */ StatCollector.translateToLocal("GT5U.multiblock.usage") + ": "
                + EnumChatFormatting.RED
                + GTUtility.formatNumbers(getActualEnergyUsage())
                + EnumChatFormatting.RESET
                + " EU/t",
            /* 4 */ StatCollector.translateToLocal("GT5U.multiblock.mei") + ": "
                + EnumChatFormatting.YELLOW
                + GTUtility.formatNumbers(voltage)
                + EnumChatFormatting.RESET
                + " EU/t(*"
                + amps
                + " A)"
                + StatCollector.translateToLocal("GT5U.machines.tier")
                + ": "
                + EnumChatFormatting.YELLOW
                + VN[GTUtility.getTier(voltage)]
                + EnumChatFormatting.RESET,
            /* 5 */ StatCollector.translateToLocal("GT5U.multiblock.problems") + ": "
                + EnumChatFormatting.RED
                + (getIdealStatus() - getRepairStatus())
                + EnumChatFormatting.RESET
                + " "
                + StatCollector.translateToLocal("GT5U.multiblock.efficiency")
                + ": "
                + EnumChatFormatting.YELLOW
                + mEfficiency / 100.0F
                + EnumChatFormatting.RESET
                + " %",
            /* 6 */ StatCollector.translateToLocal("GT5U.multiblock.pollution") + ": "
                + EnumChatFormatting.GREEN
                + getAveragePollutionPercentage()
                + EnumChatFormatting.RESET
                + " %" };
    }

    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return super.addToMachineList(aTileEntity, aBaseCasingIndex)
            || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
    }

    public boolean addEnergyHatchOrExoticEnergyHatchToMachineList(IGregTechTileEntity aTileEntity,
        int aBaseCasingIndex) {
        return addEnergyInputToMachineList(aTileEntity, aBaseCasingIndex)
            || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
    }

    public boolean addInputBusOrOutputBusToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return addInputBusToMachineList(aTileEntity, aBaseCasingIndex)
            || addOutputBusToMachineList(aTileEntity, aBaseCasingIndex);
    }

    public boolean addInputHatchOrOutputHatchToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return addInputHatchToMachineList(aTileEntity, aBaseCasingIndex)
            || addOutputHatchToMachineList(aTileEntity, aBaseCasingIndex);
    }

    public boolean addFluidInputToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) return false;
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) return false;
        if (aMetaTileEntity instanceof MTEHatchInput) {
            ((MTEHatch) aMetaTileEntity).updateTexture(aBaseCasingIndex);
            ((MTEHatchInput) aMetaTileEntity).mRecipeMap = getRecipeMap();
            return mInputHatches.add((MTEHatchInput) aMetaTileEntity);
        } else if (aMetaTileEntity instanceof MTEHatchMuffler) {
            ((MTEHatch) aMetaTileEntity).updateTexture(aBaseCasingIndex);
            return mMufflerHatches.add((MTEHatchMuffler) aMetaTileEntity);
        }
        return false;
    }

    @Override
    public boolean addEnergyOutput(long aEU) {
        if (aEU <= 0) {
            return true;
        }
        if (!mDynamoHatches.isEmpty()) {
            return addEnergyOutputMultipleDynamos(aEU, true);
        }
        return false;
    }

    @Override
    public boolean addEnergyOutputMultipleDynamos(long aEU, boolean aAllowMixedVoltageDynamos) {
        int injected = 0;
        long totalOutput = 0;
        long aFirstVoltageFound = -1;
        boolean aFoundMixedDynamos = false;
        for (MTEHatchDynamo aDynamo : filterValidMTEs(mDynamoHatches)) {
            long aVoltage = aDynamo.maxEUOutput();
            long aTotal = aDynamo.maxAmperesOut() * aVoltage;
            // Check against voltage to check when hatch mixing
            if (aFirstVoltageFound == -1) {
                aFirstVoltageFound = aVoltage;
            } else {
                if (aFirstVoltageFound != aVoltage) {
                    aFoundMixedDynamos = true;
                }
            }
            totalOutput += aTotal;
        }

        /*
         * disable explosion
         * if (totalOutput < aEU || (aFoundMixedDynamos && !aAllowMixedVoltageDynamos)) {
         * explodeMultiblock();
         * return false;
         * }
         */

        long actualOutputEU;
        if (totalOutput < aEU) {
            actualOutputEU = totalOutput;
        } else {
            actualOutputEU = aEU;
        }

        long leftToInject;
        long aVoltage;
        int aAmpsToInject;
        int aRemainder;
        int ampsOnCurrentHatch;
        for (MTEHatchDynamo aDynamo : filterValidMTEs(mDynamoHatches)) {
            leftToInject = actualOutputEU - injected;
            aVoltage = aDynamo.maxEUOutput();
            aAmpsToInject = (int) (leftToInject / aVoltage);
            aRemainder = (int) (leftToInject - (aAmpsToInject * aVoltage));
            ampsOnCurrentHatch = (int) Math.min(aDynamo.maxAmperesOut(), aAmpsToInject);
            for (int i = 0; i < ampsOnCurrentHatch; i++) {
                aDynamo.getBaseMetaTileEntity()
                    .increaseStoredEnergyUnits(aVoltage, false);
            }
            injected += aVoltage * ampsOnCurrentHatch;
            if (aRemainder > 0 && ampsOnCurrentHatch < aDynamo.maxAmperesOut()) {
                aDynamo.getBaseMetaTileEntity()
                    .increaseStoredEnergyUnits(aRemainder, false);
                injected += aRemainder;
            }
        }
        return injected > 0;
    }

    @Override
    public boolean isCorrectMachinePart(ItemStack aStack) {
        return true;
    }

    /**
     * No more machine error
     */
    @Override
    public boolean doRandomMaintenanceDamage() {
        return true;
    }

    /**
     * No more machine error
     */
    @Override
    public void checkMaintenance() {}

    /**
     * No more machine error
     */
    @Override
    public boolean getDefaultHasMaintenanceChecks() {
        return false;
    }

    /**
     * No more machine error
     */
    @Override
    public final boolean shouldCheckMaintenance() {
        return false;
    }

    /**
     * Gets the maximum Efficiency that spare Part can get (0 - 10000)
     *
     * @param aStack
     */
    @Override
    public int getMaxEfficiency(ItemStack aStack) {
        return 10000;
    }

    /**
     * Gets the damage to the ItemStack, usually 0 or 1.
     *
     * @param aStack
     */
    @Override
    public int getDamageToComponent(ItemStack aStack) {
        return 0;
    }

    /**
     * no longer afraid of rain
     */
    @Override
    public boolean willExplodeInRain() {
        return false;
    }

    @Override
    public boolean supportsVoidProtection() {
        return true;
    }

    @Override
    public boolean supportsInputSeparation() {
        return true;
    }

    @Override
    public boolean supportsBatchMode() {
        return true;
    }

    @Override
    public boolean getDefaultBatchMode() {
        if (!supportsBatchMode()) return false;
        return Config.DEFAULT_BATCH_MODE;
    }

    @Override
    public boolean supportsSingleRecipeLocking() {
        return true;
    }

    @Override
    public int getRecipeCatalystPriority() {
        return -1;
    }

    protected SoundResourceOTH getProcessStartSoundOTH() {
        return null;
    }

    protected void sendStartMultiBlockSoundLoop() {
        if (getProcessStartSoundOTH() != null) {
            sendLoopStart(PROCESS_START_SOUND_INDEX);
        }
    }

    public static boolean doSoundAtClientOTH(String aSoundName, int aTimeUntilNextSound, float aSoundStrength) {
        if (aSoundName == null) return false;
        return doSoundAtClientOTH(aSoundName, aTimeUntilNextSound, aSoundStrength, GT.getThePlayer());
    }

    public static boolean doSoundAtClientOTH(SoundResource sound, int aTimeUntilNextSound, float aSoundStrength) {
        return doSoundAtClientOTH(sound.resourceLocation, aTimeUntilNextSound, aSoundStrength, GT.getThePlayer());
    }

    public static boolean doSoundAtClientOTH(ResourceLocation aSoundResourceLocation, int aTimeUntilNextSound,
        float aSoundStrength) {
        return doSoundAtClientOTH(aSoundResourceLocation, aTimeUntilNextSound, aSoundStrength, GT.getThePlayer());
    }

    public static boolean doSoundAtClientOTH(String aSoundName, int aTimeUntilNextSound, float aSoundStrength,
        Entity aEntity) {
        if (aEntity == null || aSoundName == null) return false;
        return doSoundAtClientOTH(
            aSoundName,
            aTimeUntilNextSound,
            aSoundStrength,
            aEntity.posX,
            aEntity.posY,
            aEntity.posZ);
    }

    public static boolean doSoundAtClientOTH(ResourceLocation aSoundResourceLocation, int aTimeUntilNextSound,
        float aSoundStrength, Entity aEntity) {
        if (aEntity == null) return false;
        return doSoundAtClientOTH(
            aSoundResourceLocation.toString(),
            aTimeUntilNextSound,
            aSoundStrength,
            aEntity.posX,
            aEntity.posY,
            aEntity.posZ);
    }

    public static boolean doSoundAtClientOTH(ResourceLocation aSoundResourceLocation, int aTimeUntilNextSound,
        float aSoundStrength, double aX, double aY, double aZ) {
        return doSoundAtClientOTH(aSoundResourceLocation, aTimeUntilNextSound, aSoundStrength, 1.01818028F, aX, aY, aZ);
    }

    @Deprecated
    public static boolean doSoundAtClientOTH(String aSoundName, int aTimeUntilNextSound, float aSoundStrength,
        double aX, double aY, double aZ) {
        if (aSoundName == null) return false;
        return doSoundAtClientOTH(
            new ResourceLocation(aSoundName),
            aTimeUntilNextSound,
            aSoundStrength,
            1.01818028F,
            aX,
            aY,
            aZ);
    }

    public static boolean doSoundAtClientOTH(SoundResourceOTH aSound, int aTimeUntilNextSound, float aSoundStrength,
        double aX, double aY, double aZ) {
        return doSoundAtClientOTH(aSound.resourceLocation, aTimeUntilNextSound, aSoundStrength, aX, aY, aZ);
    }

    public static boolean doSoundAtClientOTH(SoundResourceOTH aSound, int aTimeUntilNextSound, float aSoundStrength,
        float aSoundModulation, double aX, double aY, double aZ) {
        return doSoundAtClientOTH(
            aSound.resourceLocation,
            aTimeUntilNextSound,
            aSoundStrength,
            aSoundModulation,
            aX,
            aY,
            aZ);
    }

    public static boolean doSoundAtClientOTH(ResourceLocation aSoundResourceLocation, int aTimeUntilNextSound,
        float aSoundStrength, float aSoundModulation, double aX, double aY, double aZ) {
        if (!FMLCommonHandler.instance()
            .getEffectiveSide()
            .isClient() || GT.getThePlayer() == null || !GT.getThePlayer().worldObj.isRemote) return false;
        if (GregTechAPI.sMultiThreadedSounds) new Thread(
            new RunnableSound(
                GT.getThePlayer().worldObj,
                aX,
                aY,
                aZ,
                aTimeUntilNextSound,
                aSoundResourceLocation,
                aSoundStrength,
                aSoundModulation),
            "Sound Effect").start();
        else new RunnableSound(
            GT.getThePlayer().worldObj,
            aX,
            aY,
            aZ,
            aTimeUntilNextSound,
            aSoundResourceLocation,
            aSoundStrength,
            aSoundModulation).run();
        return true;
    }

    @Override
    public void startSoundLoop(byte aIndex, double aX, double aY, double aZ) {
        super.startSoundLoop(aIndex, aX, aY, aZ);
        if (aIndex == PROCESS_START_SOUND_INDEX) {
            if (getProcessStartSoundOTH() != null)
                doSoundAtClientOTH(getProcessStartSoundOTH(), getTimeBetweenProcessSounds(), 1.0F, aX, aY, aZ);
        }
    }

    @SideOnly(Side.CLIENT)
    protected void doActivitySound(SoundResource activitySound) {
        if (getBaseMetaTileEntity().isActive() && activitySound != null) {
            if (activitySoundLoop == null) {
                activitySoundLoop = new GTSoundLoop(
                    activitySound.resourceLocation,
                    getBaseMetaTileEntity(),
                    false,
                    true);
                Minecraft.getMinecraft()
                    .getSoundHandler()
                    .playSound(activitySoundLoop);
            }
        } else {
            if (activitySoundLoop != null) {
                activitySoundLoop = null;
            }
        }
    }

    /**
     * Set total mode count for the machine.
     * Also indicate whether this machine has multiple modes.
     * Use {@link #machineMode} to get current machine mode index.
     * Override {@link #getMachineModeName(int)} to set name for each mode.
     * Override {@link #setMachineModeIcons()} to set button icon.
     * Override {@link #setMachineMode(int)} or {@link #nextMachineMode()} to restrict mode change.
     */
    public int totalMachineMode() {
        return 1;
    }

    public String getMachineModeName(int mode) {
        return "Unknown Mode " + mode;
    }

    @Override
    public final String getMachineModeName() {
        return getMachineModeName(machineMode);
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return totalMachineMode() > 1;
    }

    @Override
    public int nextMachineMode() {
        if (machineMode + 1 >= totalMachineMode()) {
            return 0;
        }
        return machineMode + 1;
    }

    public boolean canButtonSwitchMode() {
        return supportsMachineModeSwitch();
    }

    @Override
    public ButtonWidget createModeSwitchButton(IWidgetBuilder<?> builder) {
        if (!supportsMachineModeSwitch()) return null;
        Widget button = new ButtonWidget().setOnClick((clickData, widget) -> {
            if (canButtonSwitchMode()) {
                onMachineModeSwitchClick();
                setMachineMode(nextMachineMode());
            }
        })
            .setPlayClickSound(supportsMachineModeSwitch())
            .setBackground(() -> {
                List<UITexture> ret = new ArrayList<>();
                if (supportsMachineModeSwitch()) {
                    ret.add(GTUITextures.BUTTON_STANDARD);
                    ret.add(getMachineModeIcon(getMachineMode()));
                } else return null;
                return ret.toArray(new IDrawable[0]);
            })
            .attachSyncer(new FakeSyncWidget.IntegerSyncer(this::getMachineMode, this::setMachineMode), builder)
            .addTooltip(StatCollector.translateToLocal("GT5U.gui.button.mode_switch"))
            .setTooltipShowUpDelay(TOOLTIP_DELAY)
            .setPos(getMachineModeSwitchButtonPos())
            .setSize(16, 16);
        return (ButtonWidget) button;
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        if (supportsMachineModeSwitch()) {
            tag.setInteger("mode", machineMode);
        }
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        if (tag.hasKey("mode")) {
            currentTip.add(
                StatCollector.translateToLocal("Mode :") + " "
                    + EnumChatFormatting.WHITE
                    + getMachineModeName(tag.getInteger("mode"))
                    + EnumChatFormatting.RESET);
        }
    }

    // endregion
}
