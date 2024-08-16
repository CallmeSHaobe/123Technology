package com.newmaa.othtech.common.creativetab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import com.newmaa.othtech.common.item.ItemLoader;

public class CreativeTabsothtech extends CreativeTabs {

    public CreativeTabsothtech() {
        super("tabothtech");
    }

    @Override
    public Item getTabIconItem() {
        return ItemLoader.dustIrOsSm;
    }
}
