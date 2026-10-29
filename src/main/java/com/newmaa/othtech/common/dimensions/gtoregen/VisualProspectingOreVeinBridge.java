package com.newmaa.othtech.common.dimensions.gtoregen;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.newmaa.othtech.OTHTechnology;
import com.sinthoras.visualprospecting.database.veintypes.VeinType;
import com.sinthoras.visualprospecting.database.veintypes.VeinTypeCaching;

import gregtech.api.enums.Mods;
import gregtech.api.interfaces.IOreMaterial;
import gregtech.common.OreMixBuilder;

/**
 * 把锑星的自定义 GT 大矿脉喂给 VisualProspecting。
 * <p>
 * 旅行地图上那几个「GT 矿脉 / 地下流体 / 神秘节点」叠层其实是 VisualProspecting（VP）画的，
 * 而它的矿脉表 {@code VeinTypeCaching} 只在 {@code FMLLoadCompleteEvent} 里遍历 GT 的 {@code OreMixes} 枚举建表。
 * 自定义矿脉（{@code new WorldgenGTOreLayer(builder)} 注册的那种，比如 {@code otht.vein.sb}）不在枚举里，
 * 于是 {@code getVeinType("otht.vein.sb")} 永远返回 {@code NO_VEIN}：
 * <ul>
 * <li>{@code ServerCache#onVeinGenerated} 收到 {@code VeinGenerateEvent} 后直接把未知矿脉丢掉 —— 地图上什么都不显示；</li>
 * <li>{@code OreVeinLayerManager} 会过滤掉 {@code NO_VEIN} —— 就算本地缓存里有也不画；</li>
 * <li>{@code dimensionsWithVeins} 里没有 {@code antimonia}，重建缓存的 {@code WorldAnalysis} 会直接跳过整个维度。</li>
 * </ul>
 * VP 没有公开的矿脉注册 API，所以这里反射它那三张表（名字 → 矿脉、维度集合、按主/副矿索引），把锑星的矿脉补进去。
 * 调用时机必须在 VP 建表之后（世界加载 / 服务端 about-to-start），早于它在 {@code FMLServerStartingEvent}
 * 里加载矿脉缓存，否则旧存档里的锑星矿脉会被读成 {@code NO_VEIN}。
 * <p>
 * 地下流体本来就走 GT 的 {@code UndergroundOil} + {@code GTUODimension}（{@code AntimoniaUndergroundFluids} 负责配置），
 * VP 那边按世界和区块读，不依赖矿脉表，所以不用额外处理。
 */
public final class VisualProspectingOreVeinBridge {

    /** VP 的三张内部表（{@code VeinTypeCaching#init} 里建的那些）。 */
    private static final String VEIN_TYPES = "veinTypes";
    private static final String DIMENSIONS_WITH_VEINS = "dimensionsWithVeins";
    private static final String VEIN_TYPES_BY_ORE = "veinTypesByPrimaryOrSecondary";

    private static boolean registered;
    private static boolean warned;

    private VisualProspectingOreVeinBridge() {}

    /** 幂等：第一次成功后就不再动。VP 没装时直接返回。 */
    public static void ensureRegistered() {
        if (registered || !Mods.VisualProspecting.isModLoaded()) {
            return;
        }

        try {
            if (inject()) {
                registered = true;
                OTHTechnology.LOG.info(
                    "[OTHT] Registered {} Antimonia ore veins into VisualProspecting",
                    AntimoniaGTOreVeinReg.getBuilders()
                        .size());
            }
        } catch (Throwable t) {
            warnOnce(t);
        }
    }

    private static boolean inject() {
        final List<OreMixBuilder> builders = AntimoniaGTOreVeinReg.getBuilders();
        if (builders.isEmpty()) {
            // 矿脉还没注册（GT postload 没跑到），下次世界加载再试
            return false;
        }

        final Map<String, VeinType> veinTypes = asMap(staticValue(VeinTypeCaching.class, VEIN_TYPES));
        if (veinTypes == null) {
            warnOnce(null);
            return false;
        }
        final Set<String> dimensionsWithVeins = asSet(staticValue(VeinTypeCaching.class, DIMENSIONS_WITH_VEINS));
        final Map<IOreMaterial, List<VeinType>> veinTypesByOre = asMap(
            staticValue(VeinTypeCaching.class, VEIN_TYPES_BY_ORE));

        for (OreMixBuilder builder : builders) {
            if (builder == null || builder.oreMixName == null || veinTypes.containsKey(builder.oreMixName)) {
                continue;
            }

            try {
                final VeinType veinType = new VeinType(builder);
                veinTypes.put(veinType.name, veinType);

                if (dimensionsWithVeins != null) {
                    dimensionsWithVeins.addAll(veinType.getAllowedDimensions());
                }

                if (veinTypesByOre != null) {
                    indexByPrimaryOrSecondary(veinTypesByOre, veinType.primaryOre, veinType);
                    if (veinType.secondaryOre != null && veinType.secondaryOre != veinType.primaryOre) {
                        indexByPrimaryOrSecondary(veinTypesByOre, veinType.secondaryOre, veinType);
                    }
                }
            } catch (Throwable t) {
                // 单条矿脉构造失败不能拖累其它矿脉（梗矿脉的「矿石」是流体材料，VP 那边的工具方法未必扛得住）
                OTHTechnology.LOG.warn(
                    "[OTHT] Could not mirror Antimonia ore vein {} into VisualProspecting",
                    builder.oreMixName,
                    t);
            }
        }

        return verifyRegistered(builders);
    }

    /** 真的能从 VP 的表里查到才算成功（万一我们的注入被 VP 的晚一步 init 清掉了，下次世界加载会重试）。 */
    private static boolean verifyRegistered(List<OreMixBuilder> builders) {
        for (OreMixBuilder builder : builders) {
            if (builder == null || builder.oreMixName == null) {
                continue;
            }
            if (VeinTypeCaching.getVeinType(builder.oreMixName) != VeinType.NO_VEIN) {
                return true;
            }
        }
        return false;
    }

    /** 与 {@code VeinTypeCaching#init} 里建索引的方式保持一致。 */
    private static void indexByPrimaryOrSecondary(Map<IOreMaterial, List<VeinType>> byOre, IOreMaterial ore,
        VeinType veinType) {
        if (ore == null) {
            return;
        }
        List<VeinType> matching = byOre.get(ore);
        if (matching == null) {
            matching = new ArrayList<>();
            byOre.put(ore, matching);
        }
        if (!matching.contains(veinType)) {
            matching.add(veinType);
        }
    }

    private static Object staticValue(Class<?> owner, String name) {
        try {
            final Field field = owner.getDeclaredField(name);
            field.setAccessible(true);
            return field.get(null);
        } catch (Throwable ignored) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static <K, V> Map<K, V> asMap(Object value) {
        return value instanceof Map ? (Map<K, V>) value : null;
    }

    @SuppressWarnings("unchecked")
    private static Set<String> asSet(Object value) {
        return value instanceof Set ? (Set<String>) value : null;
    }

    private static void warnOnce(Throwable t) {
        if (warned) {
            return;
        }
        warned = true;
        if (t == null) {
            OTHTechnology.LOG.warn(
                "[OTHT] VisualProspecting's VeinTypeCaching layout changed, Antimonia ore veins won't show on the map");
        } else {
            OTHTechnology.LOG.warn("[OTHT] Failed to register Antimonia ore veins into VisualProspecting", t);
        }
    }
}
