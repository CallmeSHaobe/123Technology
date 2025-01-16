package com.newmaa.othtech.common.item.itemsHelper;

import net.minecraft.item.Item;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;

public final class BasicItems {

    public static final Item MetaItem = new ItemAdder("MetaItemBase", "MetaItem", CreativeTabsLoader.tabothtech)
        .setTextureName("123technology:MetaItem/0");
}
