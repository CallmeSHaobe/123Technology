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

        Map<String, GT5OreLayerHelper.OreLayerWrapper> byName = new HashMap<>(ORE_VEINS_BY_NAME);

        for (OreMixBuilder builder : AntimoniaGTOreVeinReg.getBuilders()) {

            if (byName.containsKey(builder.oreMixName)) {
                continue;
            }

            GT5OreLayerHelper.OreLayerWrapper wrapper = new GT5OreLayerHelper.OreLayerWrapper(builder);

            byName.put(builder.oreMixName, wrapper);
        }

        Map<String, GT5OreLayerHelper.NormalOreDimensionWrapper> byDim = new HashMap<>();

        for (GT5OreLayerHelper.OreLayerWrapper wrapper : byName.values()) {

            for (String dimAbbr : wrapper.abbrDimNames) {

                GT5OreLayerHelper.NormalOreDimensionWrapper dimension = byDim
                    .computeIfAbsent(dimAbbr, key -> new GT5OreLayerHelper.NormalOreDimensionWrapper());

                dimension.oreVeins.add(wrapper);
            }
        }

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

        ORE_VEINS_BY_NAME = ImmutableMap.copyOf(byName);
        ORE_VEINS_BY_DIM = ImmutableMap.copyOf(byDim);
    }
}
