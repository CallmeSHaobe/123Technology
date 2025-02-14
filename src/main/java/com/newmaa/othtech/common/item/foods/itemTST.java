package com.newmaa.othtech.common.item.foods;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class itemTST extends ItemFood {

    public itemTST() {
        super(4096, 10F, true);
        this.setAlwaysEdible();
        this.setPotionEffect(Potion.damageBoost.id, 114514, 64, 1);
        this.setUnlocalizedName("itemTST");
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
        this.setTextureName("123technology:itemTST");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List toolTip,
        final boolean advancedToolTips) {

        toolTip.add("§7哦塔斯汀, 哦塔斯汀, 有了你生活美好没烦恼~");

    }
}
