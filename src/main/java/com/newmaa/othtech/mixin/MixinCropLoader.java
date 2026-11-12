package com.newmaa.othtech.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gtnewhorizon.cropsnh.loaders.CropLoader;
import com.newmaa.othtech.OTHTechnology;
import com.newmaa.othtech.common.crops.CropsNHLoader;

@Mixin(value = CropLoader.class, remap = false)
public abstract class MixinCropLoader {

    @Inject(method = "postInit", at = @At("RETURN"), remap = false)
    private static void oth$registerCropsNH(CallbackInfo ci) {
        try {
            CropsNHLoader.registerRice();
        } catch (Throwable t) {
            // Never take the whole modpack down because of an addon crop.
            OTHTechnology.LOG.error("CropsNH rice crop registration failed", t);
        }
        try {
            CropsNHLoader.registerPeachGrass();
        } catch (Throwable t) {
            OTHTechnology.LOG.error("CropsNH yellow peach grass registration failed", t);
        }
        try {
            CropsNHLoader.registerBeeGrass();
        } catch (Throwable t) {
            OTHTechnology.LOG.error("CropsNH bee grass registration failed", t);
        }
    }
}
