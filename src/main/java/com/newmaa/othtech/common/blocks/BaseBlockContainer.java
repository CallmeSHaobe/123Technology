package com.newmaa.othtech.common.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

public abstract class BaseBlockContainer extends BlockContainer {
    public BaseBlockContainer(Material material) {
        super(material);
    }

    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> toolTip,
                               boolean advancedToolTips) {}

    public ItemStack stack() {
        return new ItemStack(this, 1);
    }
}
