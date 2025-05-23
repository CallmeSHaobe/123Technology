package com.newmaa.othtech.common.item.itemFoods;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class IngotHotDog extends ItemFood {

    public IngotHotDog() {
        super(114, 1.2F, true);
        this.setAlwaysEdible();
        this.setPotionEffect(Potion.fireResistance.id, 114514, -1, 1);
        this.setUnlocalizedName("ingotHotDog");
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
        this.setTextureName("123technology:ingotHotDog");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List toolTip,
        final boolean advancedToolTips) {

        toolTip.add("§7Dog♢");

    }
}
