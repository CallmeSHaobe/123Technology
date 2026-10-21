package com.newmaa.othtech.mixin;

import static com.newmaa.othtech.common.dimensions.RegisterDimensions.ANTIMONIA_NAME;

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
        String name = ANTIMONIA_NAME;

        if (DEF_BY_WORLD_NAME.containsKey(name)) {
            return;
        }

        ModDimensionDef def = new ModDimensionDef(
            name,
            "com.newmaa.othtech.common.dimensions.antimonia.ChunkProviderAntimonia",
            Enums.DimensionType.Planet).setOreVeinChance(100);

        DEF_BY_WORLD_NAME.put(name, def);
    }
}
