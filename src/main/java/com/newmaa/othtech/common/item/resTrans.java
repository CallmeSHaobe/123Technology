package com.newmaa.othtech.common.item;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class resTrans extends Item {
    public resTrans() {
        super();

        this.setUnlocalizedName("resTrans");
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
        this.setTextureName("123technology:resTrans");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List toolTip,
                               final boolean advancedToolTips) {

        toolTip.add("§a封装多维时空的发生装置，为计算时海量的时空退火提供了多维时空的基础");
    }
}
