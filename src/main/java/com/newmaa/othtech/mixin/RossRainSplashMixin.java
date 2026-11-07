package com.newmaa.othtech.mixin;

import net.minecraft.client.particle.EntityRainFX;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.newmaa.othtech.common.dimensions.ross123b.WorldProviderRoss123b;

/** Tint vanilla rain impact particles only in Ross123b. */
@Mixin(EntityRainFX.class)
public abstract class RossRainSplashMixin {

    @Inject(method = "<init>(Lnet/minecraft/world/World;DDD)V", at = @At("TAIL"))
    private void oth$tintRossSplash(World world, double x, double y, double z, CallbackInfo ci) {
        if (world.provider instanceof WorldProviderRoss123b) {
            ((EntityRainFX) (Object) this).setRBGColorF(0.72F, 0.11F, 0.14F);
        }
    }
}
