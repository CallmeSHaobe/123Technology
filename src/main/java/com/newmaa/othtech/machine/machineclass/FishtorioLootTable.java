package com.newmaa.othtech.machine.machineclass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameData;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.interfaces.IOreMaterial;
import gregtech.api.util.GTUtility;
import gtneioreplugin.util.DimensionHelper;
import gtneioreplugin.util.GT5OreLayerHelper;

/**
 * 异星渔场的抽奖池: 维度等级 → 该等级所有维度能挖到的矿粉, T2 起再挂上对应电压等级的机器/部件。
 * 另外保留一份全物品池, 供消耗恩情时抽。
 */
public final class FishtorioLootTable {

    private static final Map<Integer, List<ItemStack>> ORE_POOLS = new HashMap<>();
    private static final Map<Integer, List<ItemStack>> MACHINE_POOLS = new HashMap<>();
    private static final Map<Integer, List<ItemStack>> TIER_POOLS = new HashMap<>();
    private static List<ItemStack> everything = null;

    private FishtorioLootTable() {}

    /** 维度缩写(Ow / Aa / ...) → 等级, 认不出来返回 -1 */
    public static int dimensionTier(String abbreviation) {
        if (abbreviation == null) return -1;
        int index = DimensionHelper.getAllDisplayedNames()
            .indexOf(abbreviation);
        if (index < 0) return -1;
        DimensionHelper.Dimension dimension = DimensionHelper.getByIndex(index);
        if (dimension == null) return -1;
        String key = dimension.tierKey();
        try {
            return Integer.parseInt(key.substring(key.lastIndexOf('.') + 1));
        } catch (Throwable ignored) {
            return 0;
        }
    }

    public static List<ItemStack> poolForTier(int tier) {
        List<ItemStack> cached = TIER_POOLS.get(tier);
        if (cached != null) return cached;

        List<ItemStack> result = new ArrayList<>(orePool(tier));
        result.addAll(machinePool(tier));
        result = dedupe(result);
        TIER_POOLS.put(tier, result);
        return result;
    }

    /** 该等级所有维度矿脉里的矿石 → 矿粉 */
    public static List<ItemStack> orePool(int tier) {
        List<ItemStack> cached = ORE_POOLS.get(tier);
        if (cached != null) return cached;

        Set<IOreMaterial> materials = new LinkedHashSet<>();
        for (DimensionHelper.Dimension dimension : DimensionHelper.getAllDim()) {
            if (dimensionTier(dimension.abbr()) != tier) continue;
            GT5OreLayerHelper.NormalOreDimensionWrapper dimOres = GT5OreLayerHelper.getVeinByDim(dimension.abbr());
            if (dimOres == null) continue;
            for (GT5OreLayerHelper.OreLayerWrapper vein : dimOres.oreVeins) {
                if (vein == null || vein.ores == null) continue;
                for (IOreMaterial ore : vein.ores) {
                    if (ore != null) materials.add(ore);
                }
            }
        }

        List<ItemStack> result = new ArrayList<>(materials.size());
        for (IOreMaterial material : materials) {
            try {
                ItemStack dust = material.getPart(OrePrefixes.dust, 1);
                if (dust != null) result.add(dust);
            } catch (Throwable ignored) {}
        }
        ORE_POOLS.put(tier, result);
        return result;
    }

    /** T2 → LV, T3 → MV … 按显示名里带的电压等级筛物品 */
    public static List<ItemStack> machinePool(int tier) {
        if (tier < 2 || tier - 1 >= GTValues.VN.length) return Collections.emptyList();
        List<ItemStack> cached = MACHINE_POOLS.get(tier);
        if (cached != null) return cached;

        String token = GTValues.VN[tier - 1];
        List<ItemStack> result = new ArrayList<>();
        for (ItemStack stack : allItems()) {
            String name = displayName(stack);
            if (name != null && name.contains(token)) result.add(stack);
        }
        MACHINE_POOLS.put(tier, result);
        return result;
    }

    public static List<ItemStack> allItems() {
        if (everything == null) {
            List<ItemStack> collected = new ArrayList<>(8192);
            // GTNH 的 FMLControlledNamespacedRegistry 不是 java.util.Map, 只能走 typeSafeIterable
            try {
                for (Object value : GameData.getItemRegistry()
                    .typeSafeIterable()) {
                    if (!(value instanceof Item item)) continue;
                    try {
                        collected.add(new ItemStack(item, 1, 0));
                    } catch (Throwable ignored) {}
                }
            } catch (Throwable ignored) {}
            everything = dedupe(collected);
        }
        return everything;
    }

    private static String displayName(ItemStack stack) {
        try {
            return stack.getDisplayName();
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static List<ItemStack> dedupe(List<ItemStack> stacks) {
        Set<GTUtility.ItemId> seen = new LinkedHashSet<>();
        List<ItemStack> result = new ArrayList<>(stacks.size());
        for (ItemStack stack : stacks) {
            if (stack != null && seen.add(GTUtility.ItemId.createNoCopy(stack))) result.add(stack);
        }
        return result;
    }
}
