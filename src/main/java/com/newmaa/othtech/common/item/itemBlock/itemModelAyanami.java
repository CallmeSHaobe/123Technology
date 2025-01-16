package com.newmaa.othtech.common.item.itemBlock;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import com.newmaa.othtech.common.blocks.BaseBlockContainer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class itemModelAyanami extends ItemBlock {

    private final BaseBlockContainer blockType;

    public itemModelAyanami(Block id) {
        super(id);
        this.blockType = (BaseBlockContainer) id;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List toolTip,
        final boolean advancedToolTips) {
        blockType.addInformation(itemStack, player, toolTip, advancedToolTips);
    }
}
