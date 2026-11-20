package com.newmaa.othtech.common.item.itemMisc;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;

public class ItemVoidPotential extends Item {

    public ItemVoidPotential() {
        setUnlocalizedName("VoidPotential");
        setTextureName("123technology:void_potential");
        setCreativeTab(CreativeTabsLoader.tabothtech);
        setMaxStackSize(64);
    }

    public static long getPotential(ItemStack stack) {
        return stack == null ? 0L : Math.max(0, stack.stackSize);
    }

}
