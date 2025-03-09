package com.newmaa.othtech.common.item.foods;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class itemBrain extends ItemFood {

    public itemBrain() {
        super(1, 8F, true);
        this.setAlwaysEdible();
        this.setPotionEffect(Potion.hunger.id, 10, 40, 1);
        this.setUnlocalizedName("BrainsHuman");
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
        this.setTextureName("123technology:BrainsHuman");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List toolTip,
        final boolean advancedToolTips) {

        toolTip.add("他还在跳动...");

    }
}
