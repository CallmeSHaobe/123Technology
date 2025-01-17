package com.newmaa.othtech.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.newmaa.othtech.Utils.Utils;

import gregtech.api.util.GTLog;

public enum OTHItemList {

    // items
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

    // Machines
    MISA,
    inf_WirelessHatch,
    inf_infWirelessHatch,
    Mega_QFT,
    NineInOne,
    MEBFpp,
    MFREpp,
    Sun,
    LCA,
    CoccOven,

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

    MegaIsaForge;

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

    public boolean hasBeenSet() {
        return !mHasNotBeenSet;
    }

    public ItemStack getInternalStack_unsafe() {
        return mStack;
    }

    public Item getItem() {
        sanityCheck();
        if (Utils.isStackInvalid(mStack)) return null;// TODO replace a default issue item
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
