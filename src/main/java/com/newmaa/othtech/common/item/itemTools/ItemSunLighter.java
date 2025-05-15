package com.newmaa.othtech.common.item.itemTools;

import static net.minecraft.client.gui.GuiScreen.isShiftKeyDown;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;

import gtPlusPlus.core.util.Utils;

public class ItemSunLighter extends Item {

    public ItemSunLighter() {
        super();
        this.maxStackSize = 1;
        setMaxDamage(19208039);
        setCreativeTab(CreativeTabsLoader.tabothtech);
        this.setTextureName("123technology:itemSunLighter");
    }

    @Override
    public Item setMaxStackSize(int int1) {
        return super.setMaxStackSize(1);
    }

    @Override
    public EnumRarity getRarity(ItemStack thisItem) {
        return EnumRarity.epic;
    }

    @Override
    public boolean hasEffect(ItemStack par1ItemStack, int pass) {
        return true;
    }

    @Override
    public String getItemStackDisplayName(final ItemStack p_77653_1_) {
        return EnumChatFormatting.YELLOW + "太阳能打火机 ["
            + EnumChatFormatting.RED
            + "老玩家专属"
            + EnumChatFormatting.YELLOW
            + "]";
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void addInformation(final ItemStack stack, final EntityPlayer aPlayer, final List list, final boolean bool) {
        if (isShiftKeyDown()) {
            list.add(EnumChatFormatting.BLACK + "除了隐藏在光芒里的");
        } else {
            list.add(EnumChatFormatting.BLUE + "光能驱逐一切黑暗");
        }
        super.addInformation(stack, aPlayer, list, bool);
    }

    @Override
    public int getColorFromItemStack(final ItemStack stack, int HEX_OxFFFFFF) {
        return Utils.rgbtoHexValue(255, 128, 0);
    }

    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4,
        int par5, int par6, int par7, float par8, float par9, float par10) {
        if (par7 == 0) {
            par5--;
        }
        if (par7 == 1) {
            par5++;
        }
        if (par7 == 2) {
            par6--;
        }
        if (par7 == 3) {
            par6++;
        }
        if (par7 == 4) {
            par4--;
        }
        if (par7 == 5) {
            par4++;
        }
        if (!par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack)) {
            return false;
        }
        Block i1 = par3World.getBlock(par4, par5, par6);
        if (i1 == Blocks.air) {
            par3World.playSoundEffect(
                par4 + 0.5D,
                par5 + 0.5D,
                par6 + 0.5D,
                "fire.ignite",
                1.0F,
                itemRand.nextFloat() * 0.4F + 0.8F);
            if (!par3World.isRemote) {
                par3World.setBlock(par4, par5, par6, Blocks.fire, 0, 3);
            }

        }
        par1ItemStack.damageItem(1, par2EntityPlayer);
        return true;
    }
}
