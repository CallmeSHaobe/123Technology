package com.newmaa.othtech.common.item.itemWeapons;

import net.minecraft.item.Item;
import net.minecraft.item.ItemSword;
import net.minecraftforge.common.util.EnumHelper;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;

public class ShikanokoNoko extends ItemSword {

    public static final Item.ToolMaterial ShikanokoNoko = EnumHelper
        .addToolMaterial("ShikanokoNoko", 1, 2147483647, 16F, 43, 200);

    public ShikanokoNoko() {
        super(ShikanokoNoko);
        this.setUnlocalizedName("ShikanokoNoko");
        this.setTextureName("123technology:ShikanokoNoko");
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
    }
}
