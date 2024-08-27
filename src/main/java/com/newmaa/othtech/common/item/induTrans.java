package com.newmaa.othtech.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class induTrans extends Item {

    public induTrans() {
        super();

        this.setUnlocalizedName("induTrans");
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
        this.setTextureName("123technology:induTrans");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List toolTip,
        final boolean advancedToolTips) {

        toolTip.add("§a常规的供能无法轻易突破超维度的限制，因此需要集成一个可以同时为不同时空供电的微型装置");
    }
}
