package com.newmaa.othtech.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gtnewhorizon.cropsnh.loaders.CropLoader;
import com.newmaa.othtech.OTHTechnology;
import com.newmaa.othtech.common.crops.CropsNHPeachGrass;
import com.newmaa.othtech.common.crops.CropsNHRice;

@Mixin(value = CropLoader.class, remap = false)
public abstract class MixinCropLoader {

    @Inject(method = "postInit", at = @At("RETURN"), remap = false)
    private static void oth$registerCropsNH(CallbackInfo ci) {
        try {
            CropsNHRice.registerAll();
        } catch (Throwable t) {
            // Never take the whole modpack down because of an addon crop.
            OTHTechnology.LOG.error("CropsNH rice crop registration failed", t);
        }
        try {
            CropsNHPeachGrass.registerAll();
        } catch (Throwable t) {
            OTHTechnology.LOG.error("CropsNH yellow peach grass registration failed", t);
        }
    }
}
