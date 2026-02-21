package com.newmaa.othtech.machine.machineclass;

import static gregtech.api.enums.GTValues.V;
import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTUtility.filterValidMTEs;
import static gregtech.api.util.GTUtility.formatNumbers;
import static gregtech.api.util.GTUtility.validMTEList;
import static mcp.mobius.waila.api.SpecialChars.GREEN;
import static mcp.mobius.waila.api.SpecialChars.RED;
import static mcp.mobius.waila.api.SpecialChars.RESET;
import static net.minecraft.util.StatCollector.translateToLocalFormatted;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.GTMod;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SteamVariant;
import gregtech.api.enums.StructureError;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.CircularGaugeDrawable;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IItemLockable;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.interfaces.tileentity.IOverclockDescriptionProvider;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEBasicMachine;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.metatileentity.implementations.MTEHatchOutput;
import gregtech.api.metatileentity.implementations.MTEHatchOutputBus;
import gregtech.api.metatileentity.implementations.MTEHatchVoidBus;
import gregtech.api.objects.overclockdescriber.OverclockDescriber;
import gregtech.api.objects.overclockdescriber.SteamOverclockDescriber;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTUtility;
import gregtech.api.util.GTWaila;
import gregtech.api.util.HatchElementBuilder;
import gregtech.api.util.IGTHatchAdder;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gregtech.common.tileentities.machines.IDualInputInventory;
import gregtech.common.tileentities.machines.IDualInputInventoryWithPattern;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputME;
import gregtech.common.tileentities.machines.MTEHatchOutputBusME;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchSteamBusInput;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchSteamBusOutput;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.base.GTPPMultiBlockBase;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.base.MTEHatchCustomFluidBase;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public abstract class OTHSteamMultiBase<T extends OTHSteamMultiBase<T>> extends GTPPMultiBlockBase<T>
    implements IOverclockDescriptionProvider {

    private final OverclockDescriber overclockDescriber;

    public ArrayList<MTEHatchSteamBusInput> mSteamInputs = new ArrayList<>();
    public ArrayList<MTEHatchSteamBusOutput> mSteamOutputs = new ArrayList<>();
    public ArrayList<MTEHatchCustomFluidBase> mSteamInputFluids = new ArrayList<>();

    public OTHSteamMultiBase(String aName) {
        super(aName);
        this.overclockDescriber = createOverclockDescriber();
    }

    public OTHSteamMultiBase(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        this.overclockDescriber = createOverclockDescriber();
    }

    @Override
    public ITexture[] getTexture(final IGregTechTileEntity aBaseMetaTileEntity, final ForgeDirection side,
        final ForgeDirection facing, final int aColorIndex, final boolean aActive, final boolean aRedstone) {
        if (side == facing) {
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureIndex()),
                aActive ? getFrontOverlayActive() : getFrontOverlay() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureIndex()) };
    }

    protected abstract ITexture getFrontOverlay();

    protected abstract ITexture getFrontOverlayActive();

    public abstract int getTierRecipes();

    private int getCasingTextureIndex() {
        return 10;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic().setMaxParallelSupplier(this::getTrueParallel);
    }

    @Override
    protected void setProcessingLogicPower(ProcessingLogic logic) {
        logic.setAvailableVoltage(V[getTierRecipes()]);
        // We need to trick the GT_ParallelHelper we have enough amps for all recipe parallels.
        logic.setAvailableAmperage(getMaxParallelRecipes());
        logic.setAmperageOC(false);
        logic.setMaxTierSkips(0);
    }

    public ArrayList<FluidStack> getAllSteamStacks() {
        ArrayList<FluidStack> aFluids = new ArrayList<>();
        FluidStack aSteam = Materials.Steam.getGas(1);
        for (FluidStack aFluid : this.getStoredFluids()) {
            if (aFluid.isFluidEqual(aSteam)) {
                aFluids.add(aFluid);
            }
        }
        return aFluids;
    }

    public int getTotalSteamStored() {
        int aSteam = 0;
        for (FluidStack aFluid : getAllSteamStacks()) {
            aSteam += aFluid.amount;
        }
        return aSteam;
    }

    public int getTotalSteamCapacity() {
        int aSteam = 0;
        for (MTEHatchCustomFluidBase tHatch : validMTEList(mSteamInputFluids)) {
            aSteam += tHatch.getRealCapacity();
        }
        return aSteam;
    }

    public boolean tryConsumeSteam(int aAmount) {
        if (getTotalSteamStored() <= 0) {
            return false;
        } else {
            return this.depleteInput(Materials.Steam.getGas(aAmount));
        }
    }

    @Override
    public int getMaxEfficiency(ItemStack arg0) {
        return 0;
    }

    @Override
    public void onPostTick(final IGregTechTileEntity aBaseMetaTileEntity, final long aTick) {
        if (aBaseMetaTileEntity.isServerSide()) {
            if (this.mUpdate == 1 || this.mStartUpCheck == 1) {
                this.mSteamInputs.clear();
                this.mSteamOutputs.clear();
                this.mInputHatches.clear();
                this.mSteamInputFluids.clear();
                this.mOutputHatches.clear();
            }
        }
        super.onPostTick(aBaseMetaTileEntity, aTick);
    }

    /**
     * Called every tick the Machine runs
     */
    @Override
    public boolean onRunningTick(ItemStack aStack) {
        if (lEUt < 0) {
            long aSteamVal = ((-lEUt * 10000) / Math.max(1000, mEfficiency));
            // Logger.INFO("Trying to drain "+aSteamVal+" steam per tick.");
            if (!tryConsumeSteam((int) aSteamVal)) {
                stopMachine(ShutDownReasonRegistry.POWER_LOSS);
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addToMachineList(final IGregTechTileEntity aTileEntity, final int aBaseCasingIndex) {
        if (aTileEntity == null) {
            log("Invalid IGregTechTileEntity");
            return false;
        }
        final IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) {
            log("Invalid IMetaTileEntity");
            return false;
        }

        // Use this to determine the correct value, then update the hatch texture after.
        boolean aDidAdd = false;

        if (aMetaTileEntity instanceof MTEHatchCustomFluidBase) {
            log("Adding Steam Input Hatch");
            aDidAdd = addToMachineListInternal(mSteamInputFluids, aMetaTileEntity, aBaseCasingIndex);
        } else if (aMetaTileEntity instanceof MTEHatchSteamBusInput) {
            log(
                "Trying to set recipe map. Type: "
                    + (getRecipeMap() != null ? getRecipeMap().unlocalizedName : "Null"));
            this.resetRecipeMapForHatch(aTileEntity, getRecipeMap());
            log("Adding Steam Input Bus");
            aDidAdd = addToMachineListInternal(mSteamInputs, aMetaTileEntity, aBaseCasingIndex);
        } else if (aMetaTileEntity instanceof MTEHatchSteamBusOutput || aMetaTileEntity instanceof MTEHatchVoidBus) {
            log("Adding Steam Output Bus");
            aDidAdd = addToMachineListInternal(mSteamOutputs, aMetaTileEntity, aBaseCasingIndex);
        } else if (aMetaTileEntity instanceof MTEHatchInput)
            aDidAdd = addToMachineListInternal(mInputHatches, aMetaTileEntity, aBaseCasingIndex);
        else if (aMetaTileEntity instanceof MTEHatchOutput)
            aDidAdd = addToMachineListInternal(mOutputHatches, aMetaTileEntity, aBaseCasingIndex);

        return aDidAdd;
    }

    /*
     * Handle I/O with custom hatches
     */

    @Override
    public boolean depleteInput(FluidStack aLiquid) {
        if (aLiquid == null) return false;
        for (MTEHatchCustomFluidBase tHatch : validMTEList(mSteamInputFluids)) {
            FluidStack tLiquid = tHatch.getFluid();
            if (tLiquid != null && tLiquid.isFluidEqual(aLiquid)) {
                tLiquid = tHatch.drain(aLiquid.amount, false);
                if (tLiquid != null && tLiquid.amount >= aLiquid.amount) {
                    tLiquid = tHatch.drain(aLiquid.amount, true);
                    return tLiquid != null && tLiquid.amount >= aLiquid.amount;
                }
            }
        }
        return false;
    }

    @Override
    public boolean depleteInput(ItemStack aStack) {
        if (GTUtility.isStackInvalid(aStack)) return false;
        FluidStack aLiquid = GTUtility.getFluidForFilledItem(aStack, true);
        if (aLiquid != null) return depleteInput(aLiquid);
        for (MTEHatchCustomFluidBase tHatch : validMTEList(mSteamInputFluids)) {
            if (GTUtility.areStacksEqual(
                aStack,
                tHatch.getBaseMetaTileEntity()
                    .getStackInSlot(0))) {
                if (tHatch.getBaseMetaTileEntity()
                    .getStackInSlot(0).stackSize >= aStack.stackSize) {
                    tHatch.getBaseMetaTileEntity()
                        .decrStackSize(0, aStack.stackSize);
                    return true;
                }
            }
        }
        for (MTEHatchSteamBusInput tHatch : validMTEList(mSteamInputs)) {
            tHatch.mRecipeMap = getRecipeMap();
            for (int i = tHatch.getBaseMetaTileEntity()
                .getSizeInventory() - 1; i >= 0; i--) {
                if (GTUtility.areStacksEqual(
                    aStack,
                    tHatch.getBaseMetaTileEntity()
                        .getStackInSlot(i))) {
                    if (tHatch.getBaseMetaTileEntity()
                        .getStackInSlot(0).stackSize >= aStack.stackSize) {
                        tHatch.getBaseMetaTileEntity()
                            .decrStackSize(0, aStack.stackSize);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public ArrayList<FluidStack> getStoredFluidsForColor(Optional<Byte> color) {
        ArrayList<FluidStack> rList = new ArrayList<>();
        for (MTEHatchCustomFluidBase tHatch : validMTEList(mSteamInputFluids)) {
            byte hatchColor = tHatch.getBaseMetaTileEntity()
                .getColorization();
            if (color.isPresent() && hatchColor != -1 && hatchColor != color.get()) continue;
            if (tHatch.getFillableStack() != null) {
                rList.add(tHatch.getFillableStack());
            }
        }
        for (MTEHatchInput hatch : this.mInputHatches) if (hatch.getFillableStack() != null) {
            byte hatchColor = hatch.getBaseMetaTileEntity()
                .getColorization();
            if (color.isPresent() && hatchColor != -1 && hatchColor != color.get()) continue;
            rList.add(hatch.getFillableStack());
        }
        return rList;
    }

    @Override
    public ArrayList<ItemStack> getStoredInputsForColor(Optional<Byte> color) {
        ArrayList<ItemStack> rList = new ArrayList<>();
        for (MTEHatchSteamBusInput tHatch : validMTEList(mSteamInputs)) {
            byte hatchColor = tHatch.getBaseMetaTileEntity()
                .getColorization();
            if (color.isPresent() && hatchColor != -1 && hatchColor != color.get()) continue;
            tHatch.mRecipeMap = getRecipeMap();
            for (int i = tHatch.getBaseMetaTileEntity()
                .getSizeInventory() - 1; i >= 0; i--) {
                if (tHatch.getBaseMetaTileEntity()
                    .getStackInSlot(i) != null) {
                    rList.add(
                        tHatch.getBaseMetaTileEntity()
                            .getStackInSlot(i));
                }
            }
        }
        return rList;
    }

    @Override
    public boolean addOutput(ItemStack aStack) {
        if (GTUtility.isStackInvalid(aStack)) return false;
        aStack = GTUtility.copyOrNull(aStack);

        // 先尝试普通输出总线
        final List<MTEHatchOutputBus> validBusses1 = filterValidMTEs(mOutputBusses);
        if (dumpItem(validBusses1, aStack, true, false)) return true;
        if (dumpItem(validBusses1, aStack, false, false)) return true;

        // 再尝试蒸汽输出总线
        final List<MTEHatchSteamBusOutput> validBusses2 = filterValidMTEs(mSteamOutputs);
        if (dumpItem(validBusses2, aStack, true, false)) return true;
        if (dumpItem(validBusses2, aStack, false, false)) return true;

        // 最后尝试通用输出仓（如物品输出仓）
        boolean outputSuccess = true;
        final List<MTEHatchOutput> filteredHatches = filterValidMTEs(mOutputHatches);
        while (outputSuccess && aStack.stackSize > 0) {
            outputSuccess = false;
            ItemStack single = aStack.splitStack(1);
            for (MTEHatchOutput tHatch : filteredHatches) {
                if (!outputSuccess && tHatch.outputsItems()) {
                    if (tHatch.getBaseMetaTileEntity()
                        .addStackToSlot(1, single)) {
                        outputSuccess = true;
                    }
                }
            }
        }
        return outputSuccess;
    }

    @Override
    public ArrayList<ItemStack> getStoredOutputs() {
        ArrayList<ItemStack> rList = new ArrayList<>();
        // 从普通输出总线获取
        for (MTEHatchOutputBus tHatch : validMTEList(mOutputBusses)) {
            IGregTechTileEntity base = tHatch.getBaseMetaTileEntity();
            for (int i = base.getSizeInventory() - 1; i >= 0; i--) {
                rList.add(base.getStackInSlot(i));
            }
        }
        // 从蒸汽输出总线获取
        for (MTEHatchSteamBusOutput tHatch : validMTEList(mSteamOutputs)) {
            IGregTechTileEntity base = tHatch.getBaseMetaTileEntity();
            for (int i = base.getSizeInventory() - 1; i >= 0; i--) {
                rList.add(base.getStackInSlot(i));
            }
        }
        return rList;
    }

    @Override
    public List<ItemStack> getItemOutputSlots(ItemStack[] toOutput) {
        List<ItemStack> ret = new ArrayList<>();
        // 处理普通输出总线（包含ME和锁定逻辑）
        for (final MTEHatch tBus : validMTEList(mOutputBusses)) {
            if (!(tBus instanceof MTEHatchOutputBusME meBus)) {
                final IInventory tBusInv = tBus.getBaseMetaTileEntity();
                for (int i = 0; i < tBusInv.getSizeInventory(); i++) {
                    final ItemStack stackInSlot = tBus.getStackInSlot(i);
                    if (stackInSlot == null && tBus instanceof IItemLockable lockable && lockable.isLocked()) {
                        // 锁定但空槽，用大小为0的假物品表示
                        assert lockable.getLockedItem() != null;
                        ItemStack fake = lockable.getLockedItem()
                            .copy();
                        fake.stackSize = 0;
                        ret.add(fake);
                    } else {
                        ret.add(stackInSlot);
                    }
                }
            } else {
                if (meBus.isLocked() && meBus.canAcceptItem()) {
                    for (ItemStack stack : meBus.getLockedItems()) {
                        ItemStack fake = stack.copy();
                        fake.stackSize = 65; // ME总线用65表示无限容量
                        ret.add(fake);
                    }
                }
            }
        }
        // 处理蒸汽输出总线（简单遍历槽位）
        for (final MTEHatch tBus : validMTEList(mSteamOutputs)) {
            final IInventory tBusInv = tBus.getBaseMetaTileEntity();
            for (int i = 0; i < tBusInv.getSizeInventory(); i++) {
                ret.add(tBus.getStackInSlot(i));
            }
        }
        return ret;
    }

    @Override
    public List<ItemStack> getVoidOutputSlots() {
        List<ItemStack> ret = new ArrayList<>();
        // 从普通输出总线中查找虚空总线
        for (final MTEHatch tBus : validMTEList(mOutputBusses)) {
            if (tBus instanceof MTEHatchVoidBus vBus && vBus.isLocked()) {
                for (ItemStack lockedItem : vBus.getLockedItems()) {
                    if (lockedItem == null) continue;
                    ret.add(lockedItem.copy());
                }
            }
        }
        // 从蒸汽输出总线中查找虚空总线
        for (final MTEHatch tBus : validMTEList(mSteamOutputs)) {
            if (tBus instanceof MTEHatchVoidBus vBus && vBus.isLocked()) {
                for (ItemStack lockedItem : vBus.getLockedItems()) {
                    if (lockedItem == null) continue;
                    ret.add(lockedItem.copy());
                }
            }
        }
        return ret;
    }

    @Override
    public void updateSlots() {
        for (MTEHatchCustomFluidBase tHatch : validMTEList(mSteamInputFluids)) tHatch.updateSlots();
        for (MTEHatchSteamBusInput tHatch : validMTEList(mSteamInputs)) tHatch.updateSlots();
        for (MTEHatchInputBus tHatch : validMTEList(mInputBusses)) tHatch.updateSlots();
    }

    @Override
    public boolean supportsBatchMode() {
        return false;
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        mInputHatches.clear();
        mSteamInputFluids.clear();
        mSteamInputs.clear();
        mSteamOutputs.clear();
        mOutputHatches.clear();
    }

    public static boolean isColorAbsent(short hatchColors, byte color) {
        return (hatchColors & (1 << color)) == 0;
    }

    public short getHatchColors() {
        short hatchColors = 0;

        for (var bus : mInputBusses) hatchColors |= (short) (1 << bus.getColor());
        for (var hatch : mInputHatches) hatchColors |= (short) (1 << hatch.getColor());

        for (var bus : mSteamInputs) hatchColors |= (short) (1 << bus.getColor());
        for (var hatch : mSteamInputFluids) hatchColors |= (short) (1 << hatch.getColor());

        return hatchColors;
    }

    @Override
    @Nonnull
    public CheckRecipeResult doCheckRecipe() {
        CheckRecipeResult result = CheckRecipeResultRegistry.NO_RECIPE;

        // check crafting input hatches first
        for (IDualInputHatch dualInputHatch : mDualInputHatches) {
            ItemStack[] sharedItems = dualInputHatch.getSharedItems();
            for (var it = dualInputHatch.inventories(); it.hasNext();) {
                IDualInputInventory slot = it.next();

                if (!slot.isEmpty()) {
                    // try to cache the possible recipes from pattern
                    if (slot instanceof IDualInputInventoryWithPattern withPattern) {
                        if (!processingLogic.tryCachePossibleRecipesFromPattern(withPattern)) {
                            // move on to next slots if it returns false, which means there is no possible recipes with
                            // given pattern.
                            continue;
                        }
                    }

                    processingLogic.setInputItems(ArrayUtils.addAll(sharedItems, slot.getItemInputs()));
                    processingLogic.setInputFluids(slot.getFluidInputs());

                    CheckRecipeResult foundResult = processingLogic.process();
                    if (foundResult.wasSuccessful()) {
                        return foundResult;
                    }
                    if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) {
                        // Recipe failed in interesting way, so remember that and continue searching
                        result = foundResult;
                    }
                }
            }
        }

        result = checkRecipeForCustomHatches(result);
        if (result.wasSuccessful()) {
            return result;
        }

        // Use hatch colors if any; fallback to color 1 otherwise.
        short hatchColors = getHatchColors();
        boolean doColorChecking = hatchColors != 0;
        if (!doColorChecking) hatchColors = 0b1;

        for (byte color = 0; color < (doColorChecking ? 16 : 1); color++) {
            if (isColorAbsent(hatchColors, color)) continue;
            processingLogic.setInputFluids(getStoredFluidsForColor(Optional.of(color)));
            if (isInputSeparationEnabled()) {
                if (mInputBusses.isEmpty() && mSteamInputs.isEmpty()) {
                    CheckRecipeResult foundResult = processingLogic.process();
                    if (foundResult.wasSuccessful()) return foundResult;
                    // Recipe failed in interesting way, so remember that and continue searching
                    if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) result = foundResult;
                } else {
                    for (MTEHatchInputBus bus : mInputBusses) {
                        if (bus instanceof MTEHatchCraftingInputME) continue;
                        byte busColor = bus.getColor();
                        if (busColor != -1 && busColor != color) continue;
                        List<ItemStack> inputItems = new ArrayList<>();
                        for (int i = bus.getSizeInventory() - 1; i >= 0; i--) {
                            ItemStack stored = bus.getStackInSlot(i);
                            if (stored != null) inputItems.add(stored);
                        }
                        if (canUseControllerSlotForRecipe() && getControllerSlot() != null) {
                            inputItems.add(getControllerSlot());
                        }
                        processingLogic.setInputItems(inputItems);
                        CheckRecipeResult foundResult = processingLogic.process();
                        if (foundResult.wasSuccessful()) return foundResult;
                        // Recipe failed in interesting way, so remember that and continue searching
                        if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) result = foundResult;
                    }
                    for (MTEHatchSteamBusInput bus : mSteamInputs) {
                        byte busColor = bus.getColor();
                        if (busColor != -1 && busColor != color) continue;
                        List<ItemStack> inputItems = new ArrayList<>();
                        for (int i = bus.getSizeInventory() - 1; i >= 0; i--) {
                            ItemStack stored = bus.getStackInSlot(i);
                            if (stored != null) inputItems.add(stored);
                        }
                        if (canUseControllerSlotForRecipe() && getControllerSlot() != null) {
                            inputItems.add(getControllerSlot());
                        }
                        processingLogic.setInputItems(inputItems);
                        CheckRecipeResult foundResult = processingLogic.process();
                        if (foundResult.wasSuccessful()) return foundResult;
                        // Recipe failed in interesting way, so remember that and continue searching
                        if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) result = foundResult;
                    }
                }
            } else {
                List<ItemStack> inputItems = getStoredInputsForColor(Optional.of(color));
                if (canUseControllerSlotForRecipe() && getControllerSlot() != null) {
                    inputItems.add(getControllerSlot());
                }
                processingLogic.setInputItems(inputItems);
                CheckRecipeResult foundResult = processingLogic.process();
                if (foundResult.wasSuccessful()) return foundResult;
                // Recipe failed in interesting way, so remember that
                if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) result = foundResult;
            }
        }
        return result;
    }

    @Override
    protected void validateStructure(Collection<StructureError> errors, NBTTagCompound context) {
        super.validateStructure(errors, context);

        if (mSteamInputFluids.isEmpty()) {
            errors.add(StructureError.MISSING_STEAM_HATCH);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected void localizeStructureErrors(Collection<StructureError> errors, NBTTagCompound context,
        List<String> lines) {
        super.localizeStructureErrors(errors, context, lines);

        if (errors.contains(StructureError.MISSING_STEAM_HATCH)) {
            lines.add(
                StatCollector.translateToLocalFormatted(
                    "GT5U.gui.missing_hatch",
                    GregtechItemList.Hatch_Input_Steam.get(1)
                        .getDisplayName()));
        }
    }

    @Override
    public boolean resetRecipeMapForAllInputHatches(RecipeMap<?> aMap) {
        boolean ret = super.resetRecipeMapForAllInputHatches(aMap);
        for (MTEHatchSteamBusInput hatch : mSteamInputs) {
            if (resetRecipeMapForHatch(hatch, aMap)) {
                ret = true;
            }
        }
        for (MTEHatchInput g : this.mInputHatches) {
            if (resetRecipeMapForHatch(g, aMap)) {
                ret = true;
            }
        }

        return ret;
    }

    private int uiSteamStored = 0;
    private int uiSteamCapacity = 0;

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        super.addUIWidgets(builder, buildContext);
        builder.widget(new FakeSyncWidget.IntegerSyncer(this::getTotalSteamCapacity, val -> uiSteamCapacity = val));
        builder.widget(new FakeSyncWidget.IntegerSyncer(this::getTotalSteamStored, val -> uiSteamStored = val));

        builder.widget(
            new DrawableWidget().setDrawable(GTUITextures.STEAM_GAUGE_BG_STEEL)
                .dynamicTooltip(
                    () -> Collections.singletonList(
                        translateToLocalFormatted(
                            MTEBasicMachine.STEAM_AMOUNT_LANGKEY,
                            numberFormat.format(uiSteamStored),
                            numberFormat.format(uiSteamCapacity))))
                .setTooltipShowUpDelay(TOOLTIP_DELAY)
                .setUpdateTooltipEveryTick(true)
                .setSize(48, 42)
                .setPos(-48, -8));

        builder.widget(
            new DrawableWidget().setDrawable(new CircularGaugeDrawable(() -> (float) uiSteamStored / uiSteamCapacity))
                .setPos(-48 + 21, -8 + 21)
                .setSize(18, 4));
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        final NBTTagCompound tag = accessor.getNBTData();

        if (tag.getBoolean("incompleteStructure")) {
            currentTip
                .add(RED + StatCollector.translateToLocalFormatted("GT5U.waila.multiblock.status.incomplete") + RESET);
        }
        String efficiency = RESET + StatCollector
            .translateToLocalFormatted("GT5U.waila.multiblock.status.efficiency", tag.getFloat("efficiency"));
        if (tag.getBoolean("hasProblems")) {
            currentTip
                .add(RED + StatCollector.translateToLocal("GT5U.waila.multiblock.status.has_problem") + efficiency);
        } else if (!tag.getBoolean("incompleteStructure")) {
            currentTip
                .add(GREEN + StatCollector.translateToLocal("GT5U.waila.multiblock.status.running_fine") + efficiency);
        }

        boolean isActive = tag.getBoolean("isActive");
        if (isActive) {
            long actualEnergyUsage = tag.getLong("energyUsage");
            if (actualEnergyUsage > 0) {
                currentTip.add(
                    StatCollector
                        .translateToLocalFormatted("GTPP.waila.steam.use", formatNumbers(actualEnergyUsage * 20)));
            }
        }
        currentTip.add(
            GTWaila.getMachineProgressString(
                isActive,
                tag.getBoolean("isAllowedToWork"),
                tag.getInteger("maxProgress"),
                tag.getInteger("progress")));
        // Show ns on the tooltip
        if (GTMod.proxy.wailaAverageNS && tag.hasKey("averageNS")) {
            int tAverageTime = tag.getInteger("averageNS");
            currentTip.add(
                StatCollector
                    .translateToLocalFormatted("GT5U.waila.multiblock.status.cpu_load", formatNumbers(tAverageTime)));
        }
        super.getMTEWailaBody(itemStack, currentTip, accessor, config);
    }

    protected static String getSteamTierTextForWaila(NBTTagCompound tag) {
        int tierMachine = tag.getInteger("tierMachine");
        String tierMachineText;
        if (tierMachine == 1) {
            tierMachineText = "Basic";
        } else if (tierMachine == 2) {
            tierMachineText = "High Pressure";
        } else {
            tierMachineText = String.valueOf(tierMachine);
        }
        return tierMachineText;
    }

    protected static <T extends OTHSteamMultiBase<T>> HatchElementBuilder<T> buildSteamInput(Class<T> typeToken) {
        return buildHatchAdder(typeToken).adder(OTHSteamMultiBase::addToMachineList)
            .hatchIds(31040)
            .shouldReject(t -> !t.mSteamInputFluids.isEmpty());
    }

    protected static OverclockDescriber createOverclockDescriber() {
        return new SteamOverclockDescriber(SteamVariant.BRONZE, 1, 2);
    }

    @Override
    public @Nullable OverclockDescriber getOverclockDescriber() {
        return overclockDescriber;
    }

    protected enum SteamHatchElement implements IHatchElement<OTHSteamMultiBase<?>> {

        InputBus_Steam {

            @Override
            public List<? extends Class<? extends IMetaTileEntity>> mteClasses() {
                return Collections.singletonList(MTEHatchSteamBusInput.class);
            }

            @Override
            public long count(OTHSteamMultiBase<?> t) {
                return t.mSteamInputs.size();
            }
        },
        OutputBus_Steam {

            @Override
            public List<? extends Class<? extends IMetaTileEntity>> mteClasses() {
                return Collections.singletonList(MTEHatchSteamBusOutput.class);
            }

            @Override
            public long count(OTHSteamMultiBase<?> t) {
                return t.mSteamOutputs.size();
            }
        },;

        @Override
        public IGTHatchAdder<? super OTHSteamMultiBase<?>> adder() {
            return OTHSteamMultiBase::addToMachineList;
        }
    }

    @Override
    public boolean getDefaultInputSeparationMode() {
        return true;
    }

    @Override
    public boolean getDefaultHasMaintenanceChecks() {
        return false;
    }
}
