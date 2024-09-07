package com.newmaa.othtech.common;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.newmaa.othtech.Utils.Utils;

public enum OTHItemList {

    inf_WirelessHatch,
    inf_infWirelessHatch,
    Mega_QFT,
    NineInOne,
    MEBFpp,
    MFREpp,

    EVACannon,

    legendary_WirelessHatch,

    WirelessMAX,

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

}
