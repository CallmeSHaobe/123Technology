package com.newmaa.othtech.mixin;

import java.util.HashMap;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.ImmutableMap;
import com.newmaa.othtech.common.dimensions.gtoregen.AntimoniaGTOreVeinReg;

import gregtech.common.OreMixBuilder;
import gtneioreplugin.util.GT5OreLayerHelper;

@Mixin(value = GT5OreLayerHelper.class, remap = false)
public abstract class GTOreLayerHelperOTHMixin {

    @Shadow
    private static Map<String, GT5OreLayerHelper.OreLayerWrapper> ORE_VEINS_BY_NAME;

    @Shadow
    private static Map<String, GT5OreLayerHelper.NormalOreDimensionWrapper> ORE_VEINS_BY_DIM;

    @Inject(method = "init", at = @At("TAIL"), remap = false)
    private static void oth$appendCustomVeins(CallbackInfo ci) {

        // 1. 保留 GT 原有的 NEI 矿脉数据
        Map<String, GT5OreLayerHelper.OreLayerWrapper> byName = new HashMap<>(ORE_VEINS_BY_NAME);

        // 2. 将自定义 Builder 转换成 NEI 使用的包装对象
        for (OreMixBuilder builder : AntimoniaGTOreVeinReg.getBuilders()) {

            if (byName.containsKey(builder.oreMixName)) {
                continue;
            }

            GT5OreLayerHelper.OreLayerWrapper wrapper = new GT5OreLayerHelper.OreLayerWrapper(builder);

            byName.put(builder.oreMixName, wrapper);
        }

        // 3. 根据全部矿脉重新建立维度索引
        Map<String, GT5OreLayerHelper.NormalOreDimensionWrapper> byDim = new HashMap<>();

        for (GT5OreLayerHelper.OreLayerWrapper wrapper : byName.values()) {

            for (String dimAbbr : wrapper.abbrDimNames) {

                GT5OreLayerHelper.NormalOreDimensionWrapper dimension = byDim
                    .computeIfAbsent(dimAbbr, key -> new GT5OreLayerHelper.NormalOreDimensionWrapper());

                dimension.oreVeins.add(wrapper);
            }
        }

        // 4. 重新计算每个维度中各矿脉的相对权重
        for (GT5OreLayerHelper.NormalOreDimensionWrapper dimension : byDim.values()) {

            int totalWeight = 0;

            for (GT5OreLayerHelper.OreLayerWrapper vein : dimension.oreVeins) {
                totalWeight += vein.randomWeight;
            }

            if (totalWeight <= 0) {
                continue;
            }

            for (GT5OreLayerHelper.OreLayerWrapper vein : dimension.oreVeins) {

                dimension.oreVeinToProbabilityInDimension.put(vein, (double) vein.randomWeight / totalWeight);
            }
        }

        // 5. 替换为包含自定义矿脉的新缓存
        ORE_VEINS_BY_NAME = ImmutableMap.copyOf(byName);
        ORE_VEINS_BY_DIM = ImmutableMap.copyOf(byDim);
    }
}
