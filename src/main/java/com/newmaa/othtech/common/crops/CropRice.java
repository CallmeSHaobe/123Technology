package com.newmaa.othtech.common.crops;

import java.awt.Color;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.oredict.OreDictionary;

import com.gtnewhorizon.cropsnh.api.ISeedShape;
import com.gtnewhorizon.cropsnh.api.SeedShape;
import com.gtnewhorizon.cropsnh.crops.abstracts.CropFood;

public class CropRice extends CropFood {

    public CropRice() {
        // 两个颜色决定种子的两层染色：主色偏黄褐，副色偏浅绿
        super("rice", new Color(0xC9B458), new Color(0xE8F0C0));

        ItemStack rice = firstOre("cropRice");
        if (rice != null) {
            this.addDrop(rice, 10000); // 10000 = 100%
        }

        this.addLikedBiomes(BiomeDictionary.Type.WET, BiomeDictionary.Type.SWAMP, BiomeDictionary.Type.JUNGLE);
    }

    @Override
    public String getCreator() {
        return "CropsNH Addon";
    }

    @Override
    public ISeedShape getSeedShape() {
        return SeedShape.grains;
    }

    @Override
    public int getTier() {
        return 1;
    }

    @Override
    public int getGrowthDuration() {
        return 800;
    }

    /** 贴图用 0~3 四个阶段（对应 harvestcraft 的 ricecrop_0..3.png）。 */
    @Override
    public int getMinGrowthStage() {
        return 0;
    }

    @Override
    public int getMaxGrowthStage() {
        return 3;
    }

    private static ItemStack firstOre(String name) {
        List<ItemStack> ores = OreDictionary.getOres(name);
        if (ores == null || ores.isEmpty()) {
            return null;
        }
        ItemStack stack = ores.get(0)
            .copy();
        stack.stackSize = 1;
        return stack;
    }
}
