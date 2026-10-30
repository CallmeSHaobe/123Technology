package com.newmaa.othtech.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gtnewhorizon.cropsnh.loaders.CropLoader;
import com.newmaa.othtech.OTHTechnology;
import com.newmaa.othtech.common.crops.CropsNHRice;

/**
 * Hooks the tail of CropsNH's own crop loading so our crops are present for every later loader
 * (mutation / aspect / GT fake recipes / NEI).
 *
 * <p>
 * No {@code remap} is needed here: CropsNH is a mod, its class and method names are identical in
 * dev and production.
 */
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
    }
}
