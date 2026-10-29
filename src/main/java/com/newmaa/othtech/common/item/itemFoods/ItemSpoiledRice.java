package com.newmaa.othtech.common.item.itemFoods;

import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumChatFormatting;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 馊米饭 —— 放坏了的白米饭（不能拿去做米饭奇点）。
 * 喂了馊饭的迪普西科会闹脾气：速度再打五折。
 */
public class ItemSpoiledRice extends ItemFood {

    public ItemSpoiledRice() {
        super(1, 0.2F, false);
        this.setAlwaysEdible();
        this.setPotionEffect(Potion.hunger.id, 20, 20, 1);
        this.setUnlocalizedName("SpoiledRice");
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
        this.setTextureName("123technology:SpoiledRice");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List toolTip,
        final boolean advancedToolTips) {
        toolTip.add(EnumChatFormatting.DARK_GREEN + translateToLocal("tt.ds.rice.spoiled.0"));
        toolTip.add(EnumChatFormatting.DARK_GREEN + translateToLocal("tt.ds.rice.spoiled.1"));
    }
}
