package com.newmaa.othtech.common.item.tools;

import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraftforge.common.util.EnumHelper;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;

public class Dasima extends ItemPickaxe {

    public static final Item.ToolMaterial Dasima = EnumHelper.addToolMaterial("Dasima", 114, -1, 8F, 20, 20);

    public Dasima() {
        super(Dasima);
        this.setUnlocalizedName("Dasima");
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
        this.setTextureName("123technology:pickaxeDasima");
    }
}
