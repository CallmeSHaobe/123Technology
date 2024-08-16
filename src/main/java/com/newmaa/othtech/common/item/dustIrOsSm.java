package com.newmaa.othtech.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class dustIrOsSm extends Item

{

    public dustIrOsSm() {
        super();

        this.setUnlocalizedName("dustIrOsSm");
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
        this.setTextureName("123technology:dustIrOsSm");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List toolTip,
        final boolean advancedToolTips) {

        toolTip.add("§7IrOsSm☆");

    }

}
