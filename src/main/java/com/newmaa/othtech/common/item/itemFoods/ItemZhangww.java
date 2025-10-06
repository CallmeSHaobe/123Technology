package com.newmaa.othtech.common.item.itemFoods;

import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemZhangww extends ItemFood {

    public ItemZhangww() {
        super(114, 1.2F, false);
        this.setAlwaysEdible();
        this.setPotionEffect(Potion.regeneration.id, 114, 4, 1);
        this.setUnlocalizedName("itemZhangww");
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
        this.setTextureName("123technology:itemZhangww");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List toolTip,
        final boolean advancedToolTips) {

        toolTip.add(translateToLocal("tt.ww.0"));

    }
}
