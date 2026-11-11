package com.newmaa.othtech.common.crops;

import java.awt.Color;

import net.minecraftforge.common.BiomeDictionary;

import com.newmaa.othtech.common.OTHItemList;

public class CropYellowPeachGrass extends CropPattern {

    public CropYellowPeachGrass() {
        super("yellowPeachGrass", new Color(0xF2C14E), new Color(0xFFE9A8));

        this.addDrop(OTHItemList.YellowPeach.get(1), 10000);

        this.addLikedBiomes(BiomeDictionary.Type.HOT, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.JUNGLE);
    }

    @Override
    public String getFlavourText() {
        return "cropsnh_crops.yellowPeachGrass.flavour";
    }

    @Override
    public int getTier() {
        return 2;
    }

    @Override
    public int getGrowthDuration() {
        return 1600;
    }
}
