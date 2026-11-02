package com.newmaa.othtech.common.item.itemFoods;

import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 白米饭 —— 米饭奇点的原料（1234 个压成一个奇点）。
 */
public class ItemWhiteRice extends ItemFood {

    public ItemWhiteRice() {
        super(2, 0.6F, false);
        this.setAlwaysEdible();
        this.setUnlocalizedName("WhiteRice");
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
        this.setTextureName("123technology:WhiteRice");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List toolTip,
        final boolean advancedToolTips) {
        toolTip.add(EnumChatFormatting.AQUA + translateToLocal("tt.ds.rice.0"));
        toolTip.add(EnumChatFormatting.GRAY + translateToLocal("tt.ds.rice.1"));
    }
}
