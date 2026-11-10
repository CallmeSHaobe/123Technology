package com.newmaa.othtech.common.blocks.deepseiko;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

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
