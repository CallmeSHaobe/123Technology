package com.newmaa.othtech.common.dimensions;

import static com.newmaa.othtech.common.dimensions.RegisterDimensions.ANTIMONIA_NAME;

import java.util.Collections;

import com.newmaa.othtech.common.dimensions.gtoregen.AntimoniaGTOreVeinReg;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.StoneType;
import gtneioreplugin.util.DimensionHelper;

/**
 * 锑星与 GT5U / GTNEIOrePlugin 的对接点。
 * <p>
 * 这里的三个方法替代了原先的三个 mixin（GTOreGenOTHMixin、GTNEIDimensionHelperOTHMixin、
 * BlockDimensionDisplayOTHMixin），全部走 GT5U 自身提供的公开 API。
 * <p>
 * 注意：{@code GTOrePrefixOTHMixin} 仍然保留。{@code OrePrefixes} 的静态字段会读取
 * {@code Gregtech.features.maxOreStackSize} 等配置（GTMod#onPreInitialization 里才加载），
 * 任何早于配置加载的类初始化都会让矿石堆叠上限退化成 1，所以该处无法用公开集合安全替代。
 */
public final class OTHDimensionIntegration {

    private OTHDimensionIntegration() {}

    /**
     * 必须在 preInit 调用（NEI 的 {@code GT5OreLayerHelper.init()} 在 FMLLoadCompleteEvent 才执行）。
     * <p>
     * 让锑星进入 NEI 矿脉页的维度表，{@code DimensionHelper#register} 本身是 public static 且自动去重，
     * 不需要再注入 {@code <clinit>}。
     */
    public static void registerNEIDimension() {
        DimensionHelper.register(
            ANTIMONIA_NAME, // fullName
            ANTIMONIA_NAME, // internalName
            "Antimonia", // trimmedName
            "Aa", // unique abbreviation
            "otht.tier.0", // display tier
            Collections.singletonList(StoneType.Stone));
    }

    /**
     * 必须在 preInit 调用：把矿脉注册挂到 GT 的 postload 尾部回调上。
     * <p>
     * GT5U 在 postInit 末尾会先跑 {@code GTWorldgenloader}（注册原版矿脉），随后遍历
     * {@code GregTechAPI.sAfterGTPostload}，此时 {@code WorldgenGTOreLayer.sList} 已经完整，
     * 新矿脉追加在尾部，不影响原版矿脉的权重顺序。
     */
    public static void scheduleOreVeinRegistration() {
        if (GregTechAPI.sAfterGTPostload == null) {
            // 极端情况下（postInit 之后才调用）GT 已清空回调列表，直接注册
            AntimoniaGTOreVeinReg.registerAll();
            return;
        }

        GregTechAPI.sAfterGTPostload.add(AntimoniaGTOreVeinReg::registerAll);
    }
}
