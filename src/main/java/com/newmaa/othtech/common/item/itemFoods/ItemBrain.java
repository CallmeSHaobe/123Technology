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

public class ItemBrain extends ItemFood {

    public ItemBrain() {
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
        toolTip.add(EnumChatFormatting.DARK_RED + translateToLocal("tt.br.0"));
        toolTip.add(EnumChatFormatting.DARK_RED + translateToLocal("tt.br.1"));
        toolTip.add(EnumChatFormatting.DARK_RED + translateToLocal("tt.br.2"));
        toolTip.add(EnumChatFormatting.RED + translateToLocal("tt.br.3"));
        toolTip.add(EnumChatFormatting.RED + translateToLocal("tt.br.4"));
        toolTip.add(EnumChatFormatting.RED + translateToLocal("tt.br.5"));

    }
}
