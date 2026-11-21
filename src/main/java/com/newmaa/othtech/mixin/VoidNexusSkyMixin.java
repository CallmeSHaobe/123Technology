package com.newmaa.othtech.mixin;

import net.minecraft.client.renderer.EntityRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.newmaa.othtech.client.OTEVoidNexusClientEffects;

@Mixin(EntityRenderer.class)
public abstract class VoidNexusSkyMixin {

    @Inject(
        method = "renderWorld",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;setupFog(IF)V", ordinal = 1))
    private void oth$renderVoidNexusSky(float partialTicks, long finishTimeNano, CallbackInfo ci) {
        OTEVoidNexusClientEffects.renderSky(partialTicks);
    }
}
