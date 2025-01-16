package com.newmaa.othtech.common.creativetab;

import static com.newmaa.othtech.common.OTHItemList.dustIrOsSmM;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CreativeTabsothtech extends CreativeTabs {

    public CreativeTabsothtech() {
        super("tabothtech");
    }

    @Override
    public Item getTabIconItem() {
        return dustIrOsSmM.getItem();
    }
}
