package com.newmaa.othtech.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class singularity2 extends Item {

    public singularity2() {
        super();

        this.setUnlocalizedName("singularity2");
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
        this.setTextureName("123technology:singularity2");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List toolTip,
        final boolean advancedToolTips) {

        toolTip.add("§a§l压缩成巴掌大小。");
    }
}
