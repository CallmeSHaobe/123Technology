package com.newmaa.othtech.common.item;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class dioTrans extends Item {
    public dioTrans() {
        super();

        this.setUnlocalizedName("dioTrans");
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
        this.setTextureName("123technology:dioTrans");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List toolTip,
                               final boolean advancedToolTips) {

        toolTip.add("§a为防止多维时空的信息污染，每个时空在成型时单方面被注入一道信息流，这是独属于创世纪的火光");
    }
}
