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

public class ItemDeepSeikoToken extends Item {

    public ItemDeepSeikoToken() {
        super();
        this.setUnlocalizedName("DeepSeikoToken");
        this.setTextureName("123technology:DeepSeikoToken");
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
        this.setMaxStackSize(1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List toolTip,
        final boolean advancedToolTips) {
        toolTip.add(EnumChatFormatting.GOLD + translateToLocal("tt.ds.token.0"));
        toolTip.add(EnumChatFormatting.GRAY + translateToLocal("tt.ds.token.1"));
        toolTip.add(EnumChatFormatting.DARK_GRAY + translateToLocal("tt.ds.token.2"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack, int pass) {
        return true; // 附魔光效，一眼能认出是特殊物品
    }
}
