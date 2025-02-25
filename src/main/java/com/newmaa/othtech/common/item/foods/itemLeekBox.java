package com.newmaa.othtech.common.item.foods;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class itemLeekBox extends ItemFood {

    public itemLeekBox() {
        super(16, 8F, true);
        this.setAlwaysEdible();
        this.setPotionEffect(Potion.hunger.id, 100, 10, 1);
        this.setUnlocalizedName("itemLeekBox");
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
        this.setTextureName("123technology:itemLeekBox");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List toolTip,
        final boolean advancedToolTips) {

        toolTip.add("§a吃得饱饱的");

    }
}
