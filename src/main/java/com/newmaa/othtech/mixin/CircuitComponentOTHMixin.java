package com.newmaa.othtech.mixin;

import java.util.Arrays;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.newmaa.othtech.common.item.OTHCircuitComponent;

import gregtech.common.tileentities.machines.multi.nanochip.util.CircuitComponent;

@Mixin(value = CircuitComponent.class, remap = false)
public abstract class CircuitComponentOTHMixin {

    @Shadow
    @Final
    @Mutable
    private static CircuitComponent[] $VALUES;

    @Invoker("<init>")
    private static CircuitComponent oth$newComponent(String name, int ordinal, int meta, String nameKey,
        Supplier<ItemStack> realComponent) {
        throw new AssertionError();
    }

    // Run before GT5U snapshots values() and builds its metadata lookup.
    @Inject(
        method = "<clinit>",
        at = @At(
            value = "INVOKE",
            target = "Lgregtech/common/tileentities/machines/multi/nanochip/util/CircuitComponent;values()[Lgregtech/common/tileentities/machines/multi/nanochip/util/CircuitComponent;"),
        require = 1)
    private static void oth$registerComponents(CallbackInfo ci) {
        OTHCircuitComponent[] additions = OTHCircuitComponent.values();
        int ordinal = $VALUES.length;
        CircuitComponent[] extended = Arrays.copyOf($VALUES, ordinal + additions.length);
        for (OTHCircuitComponent addition : additions) {
            for (int i = 0; i < ordinal; i++) {
                CircuitComponent existing = extended[i];
                if (existing.metaId == addition.metaId || existing.name()
                    .equals(addition.nacName)) {
                    throw new IllegalStateException(
                        "NAC component collision for " + addition.nacName + " (meta " + addition.metaId + ")");
                }
            }
            extended[ordinal] = oth$newComponent(
                addition.nacName,
                ordinal,
                addition.metaId,
                addition.nameKey,
                addition.realStack);
            ordinal++;
        }
        $VALUES = extended;
    }
}
