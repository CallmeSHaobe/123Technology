package com.newmaa.othtech.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class leCasimir extends Item {

    public leCasimir() {
        super();

        this.setUnlocalizedName("leCasimir");
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
        this.setTextureName("123technology:leCasimir");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List toolTip,
        final boolean advancedToolTips) {

        toolTip.add("§a一块将鳍片伸向多维宇宙间汲取能量的模块化电源，是高阶超时空电路必不可少的部件");
    }
}
