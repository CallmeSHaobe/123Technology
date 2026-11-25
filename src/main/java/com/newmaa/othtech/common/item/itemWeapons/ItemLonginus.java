package com.newmaa.othtech.common.item.itemWeapons;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;
import com.newmaa.othtech.common.entity.EntityLonginus;

public class ItemLonginus extends Item {

    public static final String REGISTRY_NAME = "itemLonginus";
    public static final int CHARGE_TICKS = 10;

    public ItemLonginus() {
        setUnlocalizedName(REGISTRY_NAME);
        setTextureName("minecraft:arrow"); // Fallback icon for renderers that do not use the OBJ.
        setCreativeTab(CreativeTabsLoader.tabothtech);
        setMaxStackSize(1);
        setMaxDamage(0);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        player.setItemInUse(stack, getMaxItemUseDuration(stack));
        return stack;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.bow;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72_000;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int timeLeft) {
        if (getMaxItemUseDuration(stack) - timeLeft < CHARGE_TICKS) return;
        if (world.isRemote) {
            if (player.isClientWorld()) player.swingItem();
            return;
        }
        if (!world.spawnEntityInWorld(new EntityLonginus(world, player, stack))) return;
        player.swingItem();
        if (!player.capabilities.isCreativeMode) {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
        }
        world.playSoundEffect(
            player.posX,
            player.posY + player.getEyeHeight() * 0.8D,
            player.posZ,
            "123technology:weapons.longinus.throw",
            0.85F,
            1F);
    }

}
