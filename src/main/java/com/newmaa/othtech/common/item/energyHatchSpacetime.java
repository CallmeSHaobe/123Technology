package com.newmaa.othtech.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class energyHatchSpacetime extends Item {

    public energyHatchSpacetime() {
        super();

        this.setUnlocalizedName("energyHatchSpacetime");
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
        this.setTextureName("123technology:energyHatchSpacetime");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List toolTip,
        final boolean advancedToolTips) {

        toolTip.add("§a对于一般的超时空电路，通过外置能量输入亦可进行有限的计算");

    }
}
