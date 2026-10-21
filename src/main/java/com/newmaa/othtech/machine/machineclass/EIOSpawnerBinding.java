package com.newmaa.othtech.machine.machineclass;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.jetbrains.annotations.Nullable;

import crazypants.enderio.EnderIO;

/**
 * 灵魂瓶 → 破损刷怪笼 的绑定逻辑，供末影接口综合体(OTEEIO)与鱼之... (OTEFishtorio) 共用。
 * <p>
 * 行为对齐 EnderIO 自带的 {@code crazypants.enderio.machine.soul.SoulBinderSpawnerRecipe}：
 * 消耗 1 个装满灵魂的灵魂瓶 + 1 个破损刷怪笼，产出 1 个写入 {@code mobType} 的破损刷怪笼 + 1 个空灵魂瓶。
 */
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

    /**
     * 生成绑定后的破损刷怪笼。
     * <p>
     * 注意：这里必须基于输入的刷怪笼 {@code copy()} 后<b>保留原有 NBT</b> 再写 {@code mobType}。
     * 早期实现直接 {@code new NBTTagCompound()} 覆盖，会把刷怪笼上已有的 NBT（显示名、GTNH 附加数据等）
     * 全部丢掉，是 issue #254「绑定后得到空刷怪笼」的一部分成因。
     */
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
