package com.newmaa.othtech.common.materials;

import java.util.function.Supplier;

import net.minecraft.item.ItemStack;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTOreDictUnificator;

public final class BWMaterials implements Runnable {

    public static final SolidMat IrOsSm = new SolidMat(() -> OTHMaterials.IrOsSm);
    public static final SolidMat Gunmu = new SolidMat(() -> OTHMaterials.Gunmu);
    public static final SolidMat deGunmu = new SolidMat(() -> OTHMaterials.DeGunmu);

    private BWMaterials() {}

    @Override
    public void run() {}

    public static final class SolidMat {

        private final Supplier<Materials> supplier;

        private SolidMat(Supplier<Materials> supplier) {
            this.supplier = supplier;
        }

        public Materials getMaterial() {
            return supplier.get();
        }

        public ItemStack get(OrePrefixes prefix, int amount) {
            final Materials material = supplier.get();
            return material == null ? null : GTOreDictUnificator.get(prefix, material, amount);
        }
    }
}
