package com.newmaa.othtech.common.item.itemTools;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.ItemList;

public class itemBucketAqua extends ItemBucket {

    public itemBucketAqua(Block block) {
        super(block);
        this.setUnlocalizedName("itemBucketAqua");
        this.setTextureName("123technology:itemBucketAqua");
        this.setContainerItem(ItemList.Cell_Empty.getItem());
    }

    @Override
    public void addInformation(final ItemStack stack, final EntityPlayer aPlayer, final List list, final boolean bool) {
        {
            list.add(EnumChatFormatting.GOLD + "凡人是无法在NEI看到锑场下的王水的样子的");
        }
        super.addInformation(stack, aPlayer, list, bool);
    }

    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTab() {
        return CreativeTabsLoader.tabothtech;
    }
}
