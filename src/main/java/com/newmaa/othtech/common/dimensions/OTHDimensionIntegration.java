package com.newmaa.othtech.common.dimensions;

import static com.newmaa.othtech.common.dimensions.RegisterDimensions.ANTIMONIA_NAME;
import static com.newmaa.othtech.common.dimensions.RegisterDimensions.ROSS123B_NAME;

import java.util.Collections;

import com.newmaa.othtech.common.dimensions.gtoregen.AntimoniaGTOreVeinReg;
import com.newmaa.othtech.common.dimensions.gtoregen.Ross123bGTOreVeinReg;

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
        // 锑星地下也是锑石, 岩石类型同样按 Moon 处理(见 GTStoneTypeOTHMixin)
        DimensionHelper.register(
            ANTIMONIA_NAME, // fullName
            ANTIMONIA_NAME, // internalName
            "Antimonia", // trimmedName
            "Aa", // unique abbreviation
            "otht.tier.0", // display tier
            Collections.singletonList(StoneType.Moon));
        // 锑罗斯 123b: trimmedName 必须与地下流体 cfg 里的 "Dimension" 一致,
        // NEI 的 GT5UndergroundFluidHelper 就是按它反查缩写来挂 LCL 的井位页;
        // 岩石类型填 Moon, 和地下锑石在 GTStoneTypeOTHMixin 里被当成月岩的处理保持一致
        DimensionHelper.register(
            ROSS123B_NAME, // fullName
            ROSS123B_NAME, // internalName
            "Ross123b", // trimmedName
            "R3", // unique abbreviation
            "otht.tier.1", // display tier
            Collections.singletonList(StoneType.Moon));
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
            Ross123bGTOreVeinReg.registerAll();
            return;
        }

        GregTechAPI.sAfterGTPostload.add(AntimoniaGTOreVeinReg::registerAll);
        GregTechAPI.sAfterGTPostload.add(Ross123bGTOreVeinReg::registerAll);
    }
}
