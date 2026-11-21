package com.newmaa.othtech.machine.voidnexus;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.ToLongBiFunction;

import net.minecraft.nbt.NBTTagCompound;

import thaumcraft.api.aspects.Aspect;

/** Holds paid ME extractions until the controller can start a complete round. */
public final class VoidNexusEssentiaBuffer {

    private final Map<Aspect, Long> amounts = new LinkedHashMap<>();

    public long addToPlan(Map<Aspect, Long> plan, long limit) {
        long remaining = limit;
        for (Map.Entry<Aspect, Long> entry : amounts.entrySet()) {
            if (remaining <= 0L) break;
            long amount = Math.min(remaining, entry.getValue());
            plan.merge(entry.getKey(), amount, Long::sum);
            remaining -= amount;
        }
        return limit - remaining;
    }

    public boolean pay(Map<Aspect, Long> plan, ToLongBiFunction<Aspect, Long> extract) {
        for (Map.Entry<Aspect, Long> entry : plan.entrySet()) {
            Aspect aspect = entry.getKey();
            long missing = entry.getValue() - amounts.getOrDefault(aspect, 0L);
            if (missing <= 0L) continue;
            long received = extract.applyAsLong(aspect, missing);
            if (received > 0L) amounts.merge(aspect, received, Long::sum);
            if (received < missing) return false;
        }
        for (Map.Entry<Aspect, Long> entry : plan.entrySet()) {
            Aspect aspect = entry.getKey();
            long left = amounts.get(aspect) - entry.getValue();
            if (left == 0L) amounts.remove(aspect);
            else amounts.put(aspect, left);
        }
        return true;
    }

    public void writeToNBT(NBTTagCompound tag) {
        if (amounts.isEmpty()) {
            tag.removeTag("voidBufferedEssentia");
            return;
        }
        NBTTagCompound saved = new NBTTagCompound();
        amounts.forEach((aspect, amount) -> saved.setLong(aspect.getTag(), amount));
        tag.setTag("voidBufferedEssentia", saved);
    }

    public void readFromNBT(NBTTagCompound tag) {
        amounts.clear();
        NBTTagCompound saved = tag.getCompoundTag("voidBufferedEssentia");
        for (Object key : saved.func_150296_c()) {
            String name = (String) key;
            Aspect aspect = Aspect.getAspect(name);
            long amount = saved.getLong(name);
            if (aspect != null && amount > 0L) amounts.put(aspect, amount);
        }
    }
}
