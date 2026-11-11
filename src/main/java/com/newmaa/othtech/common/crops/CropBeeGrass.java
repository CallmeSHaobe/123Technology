package com.newmaa.othtech.common.crops;

import java.awt.Color;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.BiomeDictionary;

import com.newmaa.othtech.utils.NbtHelper;

public class CropBeeGrass extends CropPattern {

    public CropBeeGrass() {
        super("beeGrass", new Color(0x2DCB12), new Color(0x2DCB12));

        ItemStack queen = NbtHelper.grassothQueen();
        if (queen != null) {
            this.addDrop(queen, 10000);
        }

        this.addLikedBiomes(BiomeDictionary.Type.HOT, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.JUNGLE);
    }

    @Override
    public String getFlavourText() {
        return "cropsnh_crops.beeGrass.flavour";
    }

    @Override
    public int getTier() {
        return 1;
    }

    @Override
    public int getGrowthDuration() {
        return 1600;
    }
}
