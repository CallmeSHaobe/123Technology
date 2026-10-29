package com.newmaa.othtech.machine.machineclass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.newmaa.othtech.Config;

import cpw.mods.fml.common.registry.GameData;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.interfaces.IOreMaterial;
import gregtech.api.util.GTUtility;
import gtneioreplugin.util.DimensionHelper;
import gtneioreplugin.util.GT5OreLayerHelper;

/**
 * 异星渔场的抽奖池: 维度等级 → 该等级所有维度能挖到的矿粉。
 * 另外保留一份全物品池, 供消耗恩情时抽。
 */
public final class FishtorioLootTable {

    private static final Map<Integer, List<ItemStack>> ORE_POOLS = new HashMap<>();
    private static List<ItemStack> everything = null;

    /** 全物品池屏蔽的 mod: GTNH核心(dreamcraft) / 通用奇点 / 星门 */
    private static final Set<String> FULL_POOL_MOD_BLACKLIST = new HashSet<>(
        Arrays.asList("dreamcraft", "universalsingularities", "sgcraft"));

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

    public static List<ItemStack> allItems() {
        if (everything == null) {
            List<ItemStack> collected = new ArrayList<>(8192);
            // GTNH 的 FMLControlledNamespacedRegistry 不是 java.util.Map, 只能走 typeSafeIterable
            try {
                for (Object value : GameData.getItemRegistry()
                    .typeSafeIterable()) {
                    if (!(value instanceof Item item)) continue;
                    if (isFullPoolBlacklisted(item)) continue;
                    try {
                        collected.add(new ItemStack(item, 1, 0));
                    } catch (Throwable ignored) {}
                }
            } catch (Throwable ignored) {}
            everything = dedupe(collected);
        }
        return everything;
    }

    /** 按注册名/未本地化名判断物品是否被全物品池屏蔽, 名单见 {@link Config#fishtorioFullPoolItemBlacklist} */
    private static boolean isFullPoolBlacklisted(Item item) {
        String registryName = registryName(item);
        if (registryName != null && isModBlacklisted(registryName)) return true;
        return isItemNameBlacklisted(registryName, item);
    }

    private static String registryName(Item item) {
        try {
            Object name = Item.itemRegistry.getNameForObject(item);
            return name == null ? null : name.toString();
        } catch (Throwable ignored) {
            return null;
        }
    }

    /** 注册名前缀(modid)是否在整 mod 屏蔽名单里 */
    private static boolean isModBlacklisted(String registryName) {
        int separator = registryName.indexOf(':');
        String modid = (separator < 0 ? registryName : registryName.substring(0, separator)).toLowerCase(Locale.ROOT);
        return FULL_POOL_MOD_BLACKLIST.contains(modid);
    }

    /** 注册名或未本地化名包含黑名单子串就屏蔽 */
    private static boolean isItemNameBlacklisted(String registryName, Item item) {
        String[] patterns = Config.fishtorioFullPoolItemBlacklist;
        if (patterns == null || patterns.length == 0) return false;

        String registry = registryName == null ? "" : registryName.toLowerCase(Locale.ROOT);
        String unlocalized = "";
        try {
            String raw = item.getUnlocalizedName();
            if (raw != null) unlocalized = raw.toLowerCase(Locale.ROOT);
        } catch (Throwable ignored) {}

        for (String pattern : patterns) {
            if (pattern == null) continue;
            String needle = pattern.trim()
                .toLowerCase(Locale.ROOT);
            if (needle.isEmpty()) continue;
            if (registry.contains(needle) || unlocalized.contains(needle)) return true;
        }
        return false;
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
