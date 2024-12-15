package com.newmaa.othtech.common.beeyonds;

import net.minecraft.item.ItemStack;

import gregtech.api.util.GTLanguageManager;
import gtPlusPlus.core.material.Material;
import gtPlusPlus.core.util.Utils;

public enum combTypes {

    HYPOGEN(0, "Hypogen", true, 30, Utils.rgbtoHexValue(234, 66, 0), Utils.rgbtoHexValue(20, 20, 20)),
    CHROMATICGLASS(1, "ChromaticGlass", true, 30, Utils.rgbtoHexValue(232, 227, 227), Utils.rgbtoHexValue(200, 200, 5)),

    NORMALGLASS(2, "Glass", true, 30, Utils.rgbtoHexValue(255, 255, 255), Utils.rgbtoHexValue(255, 255, 255));

    public boolean mShowInList;
    public final Material mMaterial;
    public final int mChance;
    public final int mID;

    private final String mName;
    private final String mNameUnlocal;
    private final int[] mColour;

    private static void map(int aId, combTypes aType) {
        OTHBeeyonds.sCombMappings.put(aId, aType);
    }

    public static combTypes get(int aID) {
        return OTHBeeyonds.sCombMappings.get(aID);
    }

    combTypes(int aID, String aName, boolean aShow, int aChance, int... aColour) {
        this.mID = aID;
        this.mName = aName;
        this.mNameUnlocal = aName.toLowerCase()
            .replaceAll(" ", "");
        this.mChance = aChance;
        this.mShowInList = aShow;
        this.mColour = aColour;
        map(aID, this);
        this.mMaterial = OTHBeeyonds.sMaterialMappings.get(
            aName.toLowerCase()
                .replaceAll(" ", ""));
        GTLanguageManager.addStringLocalization("oth.comb." + this.mNameUnlocal, this.mName + " Comb");
    }

    public void setHidden() {
        this.mShowInList = false;
    }

    public String getName() {
        return GTLanguageManager.getTranslation("oth.comb." + this.mNameUnlocal);
    }

    public int[] getColours() {
        return mColour == null || mColour.length != 2 ? new int[] { 0, 0 } : mColour;
    }

    public ItemStack getStackForType(int count) {
        return new ItemStack(OTHBeeyonds.combs, count, mID);
    }
}
