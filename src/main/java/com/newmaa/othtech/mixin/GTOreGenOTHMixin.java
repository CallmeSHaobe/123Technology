package com.newmaa.othtech.mixin;

import static com.newmaa.othtech.common.dimensions.RegisterDimensions.ANTIMONIA_NAME;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.newmaa.othtech.common.dimensions.gtoregen.AntimoniaGTOreVeinReg;

import gregtech.api.enums.Materials;
import gregtech.common.OreMixBuilder;
import gregtech.common.WorldgenGTOreLayer;
import gregtech.loaders.postload.GTWorldgenloader;

@Mixin(value = GTWorldgenloader.class, remap = false)
public class GTOreGenOTHMixin {

    @Inject(method = "run", at = @At("TAIL"), remap = false)
    private void oth$registerVeins(CallbackInfo ci) {
        oth$registerVein(
            new OreMixBuilder().name("otht.vein.sb")
                .enableInDim(ANTIMONIA_NAME)
                .heightRange(5, 28)
                .weight(30)
                .density(2)
                .size(16)
                .primary(Materials.Antimony)
                .secondary(Materials.Borax)
                .inBetween(Materials.Sulfur)
                .sporadic(Materials.Naquadah));
        oth$registerVein(
            new OreMixBuilder().name("otht.vein.ar")
                .enableInDim(ANTIMONIA_NAME)
                .heightRange(5, 28)
                .weight(30)
                .density(20)
                .size(2)
                .primary(Materials.HydrochloricAcid)
                .secondary(Materials.Nitrogen)
                .inBetween(Materials.Chlorine)
                .sporadic(Materials.Hydrogen));
        oth$registerVein(
            new OreMixBuilder().name("otht.vein.air")
                .enableInDim(ANTIMONIA_NAME)
                .heightRange(2, 200)
                .weight(3)
                .density(20)
                .size(1)
                .primary(Materials.Air)
                .secondary(Materials.Air)
                .inBetween(Materials.Air)
                .sporadic(Materials.Air));
        oth$registerVein(
            new OreMixBuilder().name("otht.vein.st")
                .enableInDim(ANTIMONIA_NAME)
                .heightRange(2, 200)
                .weight(3)
                .density(20)
                .size(1)
                .primary(Materials.GraniteBlack)
                .secondary(Materials.GraniteRed)
                .inBetween(Materials.Basalt)
                .sporadic(Materials.Stone));

    }

    @Unique
    private void oth$registerVein(OreMixBuilder builder) {

        for (WorldgenGTOreLayer vein : WorldgenGTOreLayer.sList) {
            if (builder.oreMixName.equals(vein.getName())) {
                return;
            }
        }

        // 注册独立的大矿脉生成器
        AntimoniaGTOreVeinReg.register(builder);
    }
}
