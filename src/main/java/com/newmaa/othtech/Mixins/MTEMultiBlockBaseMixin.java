package com.newmaa.othtech.Mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;

@SuppressWarnings("UnusedMixin")
@Mixin(value = MTEMultiBlockBase.class, remap = false)
public abstract class MTEMultiBlockBaseMixin {

    @ModifyConstant(method = "polluteEnvironment", constant = @Constant(intValue = 10_000, ordinal = 0), require = 1)
    private int oth$modify(int constant) {
        return 50_000_000;
    }

}
