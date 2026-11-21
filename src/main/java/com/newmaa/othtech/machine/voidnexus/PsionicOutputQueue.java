package com.newmaa.othtech.machine.voidnexus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import gregtech.api.interfaces.IOutputBus;

public final class PsionicOutputQueue {

    private final List<ItemStack> pending = new ArrayList<>();

    public void add(ItemStack[] outputs) {
        if (outputs == null) return;
        for (ItemStack stack : outputs) {
            if (stack != null && stack.stackSize > 0) pending.add(stack.copy());
        }
    }

    public boolean isEmpty() {
        return pending.isEmpty();
    }

    public boolean flush(Iterable<? extends IOutputBus> busses) {
        boolean changed = false;
        for (Iterator<ItemStack> iterator = pending.iterator(); iterator.hasNext();) {
            ItemStack stack = iterator.next();
            int offered = stack.stackSize;
            insert(stack, busses);
            if (stack.stackSize == 0) iterator.remove();
            if (stack.stackSize < offered) changed = true;
        }
        return changed;
    }

    public static void insert(ItemStack remaining, Iterable<? extends IOutputBus> busses) {
        for (IOutputBus bus : busses) {
            if (bus.storePartial(remaining, false)) break;
        }
    }

    public void writeToNBT(NBTTagCompound tag) {
        if (pending.isEmpty()) {
            tag.removeTag("psionicPendingOutputs");
            return;
        }
        NBTTagList outputs = new NBTTagList();
        for (ItemStack stack : pending) {
            NBTTagCompound item = stack.writeToNBT(new NBTTagCompound());
            item.setInteger("Count", stack.stackSize);
            outputs.appendTag(item);
        }
        tag.setTag("psionicPendingOutputs", outputs);
    }

    public void readFromNBT(NBTTagCompound tag) {
        pending.clear();
        NBTTagList outputs = tag.getTagList("psionicPendingOutputs", 10);
        for (int index = 0; index < outputs.tagCount(); index++) {
            NBTTagCompound item = outputs.getCompoundTagAt(index);
            ItemStack stack = ItemStack.loadItemStackFromNBT(item);
            if (stack == null) continue;
            if (item.hasKey("Count", 3)) stack.stackSize = item.getInteger("Count");
            if (stack.stackSize > 0) pending.add(stack);
        }
    }
}
