package com.newmaa.othtech.mixin;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.newmaa.othtech.common.dimensions.antimonia.ChunkProviderAntimonia;
import com.newmaa.othtech.common.dimensions.antimonia.WorldProviderAntimonia;

import galacticgreg.api.Enums;
import galacticgreg.api.ModDimensionDef;
import galacticgreg.api.enums.DimensionDef;

@Mixin(value = DimensionDef.class, remap = false)
public class GTOreVeinMixin {

    @Unique
    private static final String DIM_ANTIMONIA = "antimonia";

    @Unique
    private static final ModDimensionDef OTHT$ANTIMONIA_DEF = new ModDimensionDef(
        DIM_ANTIMONIA,
        ChunkProviderAntimonia.class.getName(),
        Enums.DimensionType.Planet);

    @Inject(method = "getDefForWorld", at = @At("HEAD"), cancellable = true, remap = false)
    private static void otht$antimonia(World world, CallbackInfoReturnable<ModDimensionDef> cir) {
        if (world.provider instanceof WorldProviderAntimonia) {
            cir.setReturnValue(OTHT$ANTIMONIA_DEF);
        }
    }

    @Inject(method = "getDefByName", at = @At("HEAD"), cancellable = true, remap = false)
    private static void otht$antimoniaByName(String worldName, CallbackInfoReturnable<ModDimensionDef> cir) {
        if (DIM_ANTIMONIA.equalsIgnoreCase(worldName)) {
            cir.setReturnValue(OTHT$ANTIMONIA_DEF);

        }
    }
}
