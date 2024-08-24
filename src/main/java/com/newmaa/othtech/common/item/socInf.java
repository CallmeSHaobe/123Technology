package com.newmaa.othtech.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class socInf extends Item {

    public socInf() {
        super();

        this.setUnlocalizedName("socInf");
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
        this.setTextureName("123technology:socInf");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List toolTip,
        final boolean advancedToolTips) {

        toolTip.add("§7§k//§9§l给时光以生命，给岁月以文明。§7§k//");

    }

}
