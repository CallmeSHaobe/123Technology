package com.newmaa.othtech.mixin;

import static com.newmaa.othtech.common.dimensions.RegisterDimensions.ANTIMONIA_NAME;

import java.util.Collections;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import gregtech.api.enums.StoneType;
import gtneioreplugin.util.DimensionHelper;

@Mixin(value = DimensionHelper.class, remap = false)
public abstract class GTNEIDimensionHelperOTHMixin {

    @Inject(method = "<clinit>", at = @At("TAIL"), remap = false)
    private static void oth$registerDimension(CallbackInfo ci) {

        String name = ANTIMONIA_NAME;

        if (DimensionHelper.REGISTRY.containsKey(name)) {
            return;
        }

        DimensionHelper.register(
            name, // fullName
            name, // internalName
            "Antimonia", // trimmedName
            "Aa", // unique abbreviation
            "otht.tier.0", // display tier
            Collections.singletonList(StoneType.Stone));
    }
}
