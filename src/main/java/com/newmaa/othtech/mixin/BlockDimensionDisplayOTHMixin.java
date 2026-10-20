package com.newmaa.othtech.mixin;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import gtneioreplugin.plugin.block.BlockDimensionDisplay;

@Mixin(value = BlockDimensionDisplay.class, remap = false)
public abstract class BlockDimensionDisplayOTHMixin {

    @Shadow
    @Final
    private String dimension;

    @Shadow
    @Final
    private IIcon[] icons;

    @Inject(method = "registerBlockIcons", at = @At("HEAD"), cancellable = true, remap = false)
    private void oth$registerCustomTextures(IIconRegister iconRegister, CallbackInfo ci) {
        // 只修改自己的维度
        if (!"Aa".equals(this.dimension)) {
            return;
        }

        icons[0] = iconRegister.registerIcon("123technology:Aa_bottom");
        icons[1] = iconRegister.registerIcon("123technology:Aa_top");
        icons[2] = iconRegister.registerIcon("123technology:Aa_back");
        icons[3] = iconRegister.registerIcon("123technology:Aa_front");
        icons[4] = iconRegister.registerIcon("123technology:Aa_left");
        icons[5] = iconRegister.registerIcon("123technology:Aa_right");

        // 阻止原方法覆盖我们注册的贴图
        ci.cancel();
    }
}
