package com.newmaa.othtech.common.item.itemMisc;

import net.minecraft.item.Item;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;

public class ItemPsionicStabilizer extends Item {

    public ItemPsionicStabilizer() {
        setUnlocalizedName("PsionicStabilizer");
        setTextureName("123technology:psionic_stabilizer");
        setCreativeTab(CreativeTabsLoader.tabothtech);
        setMaxStackSize(16);
    }
}
