package com.newmaa.othtech.common.item.itemWeapons;

import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;
import com.newmaa.othtech.common.entity.EntityPositronShot;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.api.item.ISpecialElectricItem;

public class ItemPositronCannon extends Item implements ISpecialElectricItem {

    public static final double CAPACITY = 10_000_000_000D;
    public static final double SHOT_COST = 100_000_000D;
    private static final int CHARGE_TICKS = 40;

    public ItemPositronCannon() {
        setUnlocalizedName("itemPositronCannon");
        setTextureName("123technology:itemPositronCannon");
        setCreativeTab(CreativeTabsLoader.tabothtech);
        setMaxStackSize(1);
    }

    @Override
    public boolean canProvideEnergy(ItemStack stack) {
        return false;
    }

    @Override
    public Item getChargedItem(ItemStack stack) {
        return this;
    }

    @Override
    public Item getEmptyItem(ItemStack stack) {
        return this;
    }

    @Override
    public double getMaxCharge(ItemStack stack) {
        return CAPACITY;
    }

    @Override
    public int getTier(ItemStack stack) {
        return 8;
    }

    @Override
    public double getTransferLimit(ItemStack stack) {
        return 2_000_000D;
    }

    @Override
    public IElectricItemManager getManager(ItemStack stack) {
        return ElectricItem.rawManager;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player.capabilities.isCreativeMode || ElectricItem.manager.canUse(stack, SHOT_COST)) {
            player.setItemInUse(stack, getMaxItemUseDuration(stack));
        } else if (!world.isRemote) {
            player.addChatMessage(new ChatComponentTranslation("tt.positron.noEnergy"));
        }
        return stack;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.none;
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityPlayer player, int remaining) {
        if (getMaxItemUseDuration(stack) - remaining >= CHARGE_TICKS && !player.worldObj.isRemote) {
            shoot(stack, player.worldObj, player);
            player.clearItemInUse();
        }
    }

    private void shoot(ItemStack stack, World world, EntityPlayer player) {
        if (!player.capabilities.isCreativeMode && !ElectricItem.manager.use(stack, SHOT_COST, player)) {
            player.addChatMessage(new ChatComponentTranslation("tt.positron.noEnergy"));
            return;
        }
        world.spawnEntityInWorld(new EntityPositronShot(world, player, 500F));
        world.playSoundAtEntity(player, "random.explode", 1.5F, 1.4F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        tooltip.add(
            EnumChatFormatting.AQUA + translateToLocal("tt.positron.charge")
                + ": "
                + String.format("%,.0f / %,.0f EU", ElectricItem.manager.getCharge(stack), CAPACITY));
        tooltip.add(EnumChatFormatting.GRAY + translateToLocal("tt.positron.use"));
        tooltip.add(EnumChatFormatting.GRAY + translateToLocal("tt.positron.cost"));
    }
}
