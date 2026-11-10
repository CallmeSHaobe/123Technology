package com.newmaa.othtech.common.item.itemMisc;

import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemDeepSeikoTokenSilver extends Item {

    public ItemDeepSeikoTokenSilver() {
        super();
        this.setUnlocalizedName("DeepSeikoTokenSilver");
        this.setTextureName("123technology:DeepSeikoTokenSilver");
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
        this.setMaxStackSize(1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List toolTip,
        final boolean advancedToolTips) {
        toolTip.add(EnumChatFormatting.GRAY + translateToLocal("tt.ds.token.silver"));
    }
}
