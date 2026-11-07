package com.newmaa.othtech.mixin;

import static com.newmaa.othtech.common.dimensions.RegisterDimensions.ANTIMONIA_NAME;
import static com.newmaa.othtech.common.dimensions.RegisterDimensions.ROSS123B_NAME;

import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import galacticgreg.api.Enums;
import galacticgreg.api.ModDimensionDef;
import galacticgreg.api.enums.DimensionDef;

@Mixin(value = DimensionDef.class, remap = false)
public class GTDimDefOTHMixin {

    @Shadow
    @Final
    private static Map<String, ModDimensionDef> DEF_BY_WORLD_NAME;

    @Inject(method = "<clinit>", at = @At("TAIL"), remap = false)
    private static void othd$registerDimension(CallbackInfo ci) {
        if (!DEF_BY_WORLD_NAME.containsKey(ANTIMONIA_NAME)) {
            DEF_BY_WORLD_NAME.put(
                ANTIMONIA_NAME,
                new ModDimensionDef(
                    ANTIMONIA_NAME,
                    "com.newmaa.othtech.common.dimensions.antimonia.ChunkProviderAntimonia",
                    Enums.DimensionType.Planet).setOreVeinChance(100));
        }

        if (!DEF_BY_WORLD_NAME.containsKey(ROSS123B_NAME)) {
            DEF_BY_WORLD_NAME.put(
                ROSS123B_NAME,
                new ModDimensionDef(
                    ROSS123B_NAME,
                    "com.newmaa.othtech.common.dimensions.ross123b.ChunkProviderRoss123b",
                    Enums.DimensionType.Planet).setOreVeinChance(100));
        }
    }
}
