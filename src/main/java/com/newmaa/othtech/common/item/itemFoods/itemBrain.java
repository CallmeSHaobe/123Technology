package com.newmaa.othtech.common.item.itemFoods;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumChatFormatting;

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
        toolTip.add(EnumChatFormatting.DARK_RED + "我还活着...吗?");
        toolTip.add(EnumChatFormatting.DARK_RED + "我曾活着...吗?");
        toolTip.add(EnumChatFormatting.DARK_RED + "我思故我在.");
        toolTip.add(EnumChatFormatting.RED + "他看起来还在跳动...");
        toolTip.add(EnumChatFormatting.RED + "第一个脑子, 要从哪里来呢..?");
        toolTip.add(EnumChatFormatting.RED + "你知道的, 做这种事自己总要付出一些代价.");

    }
}
