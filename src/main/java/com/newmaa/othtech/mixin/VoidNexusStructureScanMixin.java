package com.newmaa.othtech.mixin;

import java.util.function.Consumer;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.newmaa.othtech.machine.OTEVoidNexusMatrix;

import blockrenderer6343.client.world.ObserverWorld;
import blockrenderer6343.integration.gregtech.GTConstructableScan;

@Mixin(value = GTConstructableScan.class, remap = false)
public abstract class VoidNexusStructureScanMixin {

    @WrapOperation(
        method = "run",
        at = @At(
            value = "INVOKE",
            target = "Lblockrenderer6343/client/world/ObserverWorld;estimateTierFromConstructable(Ljava/util/function/Consumer;Lcom/gtnewhorizon/structurelib/alignment/constructable/IConstructable;)I"),
        require = 1)
    private int oth$useDeclaredVoidNexusTier(ObserverWorld world, Consumer<ItemStack> result, IConstructable machine,
        Operation<Integer> original) {
        if (machine instanceof OTEVoidNexusMatrix voidNexus) return voidNexus.getStructureScanTier();
        return original.call(world, result, machine);
    }
}
