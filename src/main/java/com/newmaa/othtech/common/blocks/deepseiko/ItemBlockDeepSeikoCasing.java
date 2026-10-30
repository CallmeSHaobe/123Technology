package com.newmaa.othtech.common.blocks.deepseiko;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * 迪普西科机壳的 ItemBlock：按 meta 给出各自的 unlocalized 名称（tile.dsCasing.N）。
 */
public class ItemBlockDeepSeikoCasing extends ItemBlock {

    public ItemBlockDeepSeikoCasing(Block block) {
        super(block);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int meta = Math.max(0, Math.min(BlockDeepSeikoCasing.METACOUNT - 1, stack.getItemDamage()));
        return "tile.dsCasing." + meta;
    }
}
