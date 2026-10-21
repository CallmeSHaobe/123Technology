package com.newmaa.othtech.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import gtneioreplugin.plugin.renderer.ItemDimensionDisplayRenderer;

@Mixin(value = ItemDimensionDisplayRenderer.class, remap = false)
public abstract class ItemDimensionDisplayRendererOTHMixin {

    @Inject(method = "getPrefix", at = @At("HEAD"), cancellable = true, remap = false)
    private static void oth$customDimensionTier(String dimName, CallbackInfoReturnable<Long> cir) {
        if ("Aa".equals(dimName)) {
            cir.setReturnValue(4L);
        }
    }
}
