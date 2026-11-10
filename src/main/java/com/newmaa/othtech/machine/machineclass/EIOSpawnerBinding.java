package com.newmaa.othtech.machine.machineclass;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.jetbrains.annotations.Nullable;

import crazypants.enderio.EnderIO;

public final class EIOSpawnerBinding {

    private EIOSpawnerBinding() {}

    /** 找到一瓶真正装了灵魂的灵魂瓶（空瓶会被跳过，避免第一个空瓶挡住后面有灵魂的） */
    @Nullable
    public static ItemStack findSoulVessel(List<ItemStack> inputs) {
        for (ItemStack stack : inputs) {
            if (stack != null && stack.getItem() == EnderIO.itemSoulVessel
                && EnderIO.itemSoulVessel.containsSoul(stack)) {
                return stack;
            }
        }
        return null;
    }

    @Nullable
    public static ItemStack findBrokenSpawner(List<ItemStack> inputs) {
        for (ItemStack stack : inputs) {
            if (stack != null && stack.getItem() == EnderIO.itemBrokenSpawner) {
                return stack;
            }
        }
        return null;
    }

    public static ItemStack createBoundSpawner(@Nullable ItemStack source, String mobType) {
        ItemStack out = source != null && source.getItem() == EnderIO.itemBrokenSpawner ? source.copy()
            : new ItemStack(EnderIO.itemBrokenSpawner);

        out.stackSize = 1;

        NBTTagCompound tag = out.getTagCompound() != null ? (NBTTagCompound) out.getTagCompound()
            .copy() : new NBTTagCompound();
        tag.setString("mobType", mobType);
        out.setTagCompound(tag);

        return out;
    }
}
