package com.newmaa.othtech.common.crops;

import java.awt.Color;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.oredict.OreDictionary;

public class CropRice extends CropPattern {

    public CropRice() {
        super("rice", new Color(0xC9B458), new Color(0xE8F0C0));

        List<ItemStack> rice = OreDictionary.getOres("cropRice");
        if (!rice.isEmpty()) {
            ItemStack drop = rice.get(0)
                .copy();
            drop.stackSize = 1;
            this.addDrop(drop, 10000);
        }

        this.addLikedBiomes(BiomeDictionary.Type.WET, BiomeDictionary.Type.SWAMP, BiomeDictionary.Type.JUNGLE);
    }

    @Override
    public int getTier() {
        return 1;
    }

    @Override
    public int getGrowthDuration() {
        return 800;
    }
}
