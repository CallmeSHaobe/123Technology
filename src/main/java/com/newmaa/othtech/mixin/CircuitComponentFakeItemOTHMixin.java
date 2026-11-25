package com.newmaa.othtech.mixin;

import java.util.Map;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.newmaa.othtech.common.item.OTHCircuitComponent;

import gregtech.api.items.CircuitComponentFakeItem;

@Mixin(value = CircuitComponentFakeItem.class, remap = false)
public abstract class CircuitComponentFakeItemOTHMixin {

    @Shadow
    @Final
    private Map<Integer, IIcon> iconMap;

    // registerIcons overrides a vanilla method; accept both its development and SRG names.
    @Inject(method = { "registerIcons", "func_94581_a" }, at = @At("RETURN"), require = 1)
    private void oth$registerUniversalSocIcon(IIconRegister icons, CallbackInfo ci) {
        iconMap.put(OTHCircuitComponent.UNIVERSAL_SOC.metaId, icons.registerIcon("123technology:nac/universal_soc"));
    }
}
