package com.newmaa.othtech.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class socCosmic extends Item {

    public socCosmic() {
        super();

        this.setUnlocalizedName("socCosmic");
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
        this.setTextureName("123technology:socCosmic");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List toolTip,
        final boolean advancedToolTips) {

        toolTip.add("§4§o银河系的星河纹丝不动地横过天穹，亦像是被§b§l§o冻结§4§o了。...");

    }

}
