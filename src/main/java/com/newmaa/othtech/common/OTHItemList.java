package com.newmaa.othtech.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.newmaa.othtech.utils.Utils;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.util.GTLog;

@SuppressWarnings("SpellCheckingInspection")
public enum OTHItemList {

    // items
    Amulet,
    Brains,
    slicedBrains,
    encapsulatedBranins,
    boardCasimirM,
    boardTransM,
    capTransM,
    dioTransM,
    dustIrOsSmM,
    dustSnAsM,
    energyHatchSpacetimeM,
    finCasimirM,
    induTransM,
    ingotDogM,
    ingotSnAsM,
    beeISAM,
    itemEnqingM,
    leCasimirM,
    dustLookNEIM,
    beeMagM,
    resTransM,
    glassSingularityM,
    machineSingularityM,
    socCosmicM,
    socInfM,
    socNorM,
    transTransM,
    IsaNEI,
    ISAIOS,
    ISAHYP,
    ISASPE,
    // Lasers
    LVA,
    LVB,
    MVA,
    MVB,
    HVA,
    HVB,
    EVA,
    EVB,
    IVA,
    IVB,
    LUVA,
    LUVB,
    ZPMA,
    ZPMB,
    UVA,
    UVB,
    UHVA,
    UHVB,
    UEVA,
    UEVB,
    UIVA,
    UIVB,
    UMVA,
    UMVB,
    UXVA,
    UXVB,
    Zhangww,
    ingotHotDog,
    Dasima,
    ShikanokoNoko,
    SunLighter,
    LeekBox,
    itemTST,
    NukeThrowable,
    RecordPRKA,
    RecordPRKB,
    RecordEVAA,
    RecordEVAB,
    RecordEVAC,
    RecordNGGU,

    // Machines
    FISH,
    EXH,
    EIO,
    TP,
    AF,
    MCA,
    NQFF,
    MISA,
    NQF,
    FooD,
    inf_WirelessHatch,
    inf_infWirelessHatch,
    Mega_QFT,
    NineInOne,
    MEBFpp,
    MFREpp,
    Sun,
    LCA,
    CoccOven,
    SpaceElevatorModulePumpT4,

    EVACannon,
    HatchME,
    BusME,
    SINOPECd,
    SF,
    Chem,
    MegaEEC,
    WoodFusion,
    GTTEDynamoULV,
    GTTEEnergyULV,
    ImbaBlastFurnace,

    legendary_WirelessHatch,

    WirelessMAX,
    CHEMEEE,

    MegaIsaForge,

    OTEGraveDragon;

    private boolean mHasNotBeenSet;
    private boolean mDeprecated;
    private boolean mWarned;
    private ItemStack mStack;

    OTHItemList() {
        mHasNotBeenSet = true;
    }

    OTHItemList(boolean aDeprecated) {
        if (aDeprecated) {
            mDeprecated = true;
            mHasNotBeenSet = true;
        }
    }

    public int getMeta() {
        return mStack.getItemDamage();
    }

    public OTHItemList set(Item aItem) {
        mHasNotBeenSet = false;
        if (aItem == null) return this;
        ItemStack aStack = new ItemStack(aItem, 1, 0);
        mStack = Utils.copyAmount(1, aStack);
        return this;
    }

    public OTHItemList set(ItemStack aStack) {
        if (aStack != null) {
            mHasNotBeenSet = false;
            mStack = Utils.copyAmount(1, aStack);
        }
        return this;
    }

    public OTHItemList set(IMetaTileEntity metaTileEntity) {
        if (metaTileEntity == null) throw new IllegalArgumentException("Invalid Meta Tile Entity");
        set(metaTileEntity.getStackForm(1L));
        return this;
    }

    public boolean hasBeenSet() {
        return !mHasNotBeenSet;
    }

    public ItemStack getInternalStack_unsafe() {
        return mStack;
    }

    public Item getItem() {
        sanityCheck();
        if (Utils.isStackInvalid(mStack)) return null;
        return mStack.getItem();
    }

    public Block getBlock() {
        sanityCheck();
        return Block.getBlockFromItem(getItem());
    }

    public ItemStack get(int aAmount, Object... aReplacements) {
        sanityCheck();
        // if invalid, return a replacements
        if (Utils.isStackInvalid(mStack)) {
            GTLog.out.println("Object in the ItemList is null at:");
        }
        return Utils.copyAmount(aAmount, mStack);
    }

    private void sanityCheck() {
        if (mHasNotBeenSet)
            throw new IllegalAccessError("The Enum '" + name() + "' has not been set to an Item at this time!");
        if (mDeprecated && !mWarned) {
            new Exception(this + " is now deprecated").printStackTrace(GTLog.err);
            // warn only once
            mWarned = true;
        }
    }
}
