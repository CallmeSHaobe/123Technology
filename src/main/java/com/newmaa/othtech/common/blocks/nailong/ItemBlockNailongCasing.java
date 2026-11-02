package com.newmaa.othtech.common.blocks.nailong;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * 奈隆系机壳的 ItemBlock：按 meta 给出各自的 unlocalized 名称（tile.nailongCasing.N）。
 */
public class ItemBlockNailongCasing extends ItemBlock {

    public ItemBlockNailongCasing(Block block) {
        super(block);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int meta = Math.max(0, Math.min(BlockNailongCasing.METACOUNT - 1, stack.getItemDamage()));
        return "tile.nailongCasing." + meta;
    }
}
