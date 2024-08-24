package com.newmaa.othtech.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class singularity1 extends Item {

    public singularity1() {
        super();

        this.setUnlocalizedName("singularity1");
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
        this.setTextureName("123technology:singularity1");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List toolTip,
        final boolean advancedToolTips) {

        toolTip.add("§5§l「发射生命解离彩虹」");

    }
}
