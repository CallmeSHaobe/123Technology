package com.newmaa.othtech.mixin;

import net.minecraft.block.Block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.newmaa.othtech.common.blocks.antimonia.AntimoniaBlocks;

import gregtech.api.enums.StoneType;

@Mixin(value = StoneType.class, remap = false)
public abstract class GTStoneTypeOTHMixin {

    @Inject(method = "contains", at = @At("HEAD"), cancellable = true, remap = false)
    private void oth$antiStoneCountsAsMoon(Block block, int meta, CallbackInfoReturnable<Boolean> cir) {
        final Block antiStone = AntimoniaBlocks.antimoniaBlockStone;
        if (antiStone == null || block != antiStone) {
            return;
        }
        if ((StoneType) (Object) this != StoneType.Moon) {
            return;
        }
        cir.setReturnValue(true);
    }
}
