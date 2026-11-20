package com.newmaa.othtech.common.blocks.nailong;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockNailongCasing extends ItemBlock {

    public ItemBlockNailongCasing(Block block) {
        super(block);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        return BlockNailongCasing.getCasingColor(stack.getItemDamage());
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int meta = Math.max(0, Math.min(BlockNailongCasing.METACOUNT - 1, stack.getItemDamage()));
        return "tile.nailongCasing." + meta;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        int meta = stack.getItemDamage();
        if (meta == NailongCasingMeta.ENDLESS_PSIONIC_GLASS || meta == NailongCasingMeta.SPACETIME_POTENTIAL_GLASS) {
            tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tt.void_nexus.glass." + meta + ".0"));
            tooltip.add(
                EnumChatFormatting.DARK_GRAY + StatCollector.translateToLocal("tt.void_nexus.glass." + meta + ".1"));
        }
    }

}
