package com.newmaa.othtech.common.materials;

import java.util.function.Supplier;

import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.Materials;

public final class BWLiquids implements Runnable {

    public static final FluidMat Galaxy = new FluidMat(() -> OTHMaterials.Galaxy);
    public static final FluidMat Void = new FluidMat(() -> OTHMaterials.VoidEntropy);
    public static final FluidMat Stars = new FluidMat(() -> OTHMaterials.Stars);
    public static final FluidMat LCL = new FluidMat(() -> OTHMaterials.LCL);
    public static final FluidMat FUELA = new FluidMat(() -> OTHMaterials.FUELA);
    public static final FluidMat FUELA_DE = new FluidMat(() -> OTHMaterials.FUELA_DE);
    public static final FluidMat PromoterZPM = new FluidMat(() -> OTHMaterials.PromoterZPM);
    public static final FluidMat PromoterUEV = new FluidMat(() -> OTHMaterials.PromoterUEV);
    public static final FluidMat HCl10 = new FluidMat(() -> OTHMaterials.HCl10);
    public static final FluidMat HCl100 = new FluidMat(() -> OTHMaterials.HCl100);
    public static final FluidMat HCl1000 = new FluidMat(() -> OTHMaterials.HCl1000);
    public static final FluidMat HCl10000 = new FluidMat(() -> OTHMaterials.HCl10000);
    public static final FluidMat HCl100000 = new FluidMat(() -> OTHMaterials.HCl100000);

    private BWLiquids() {}

    @Override
    public void run() {}

    public static final class FluidMat {

        private final Supplier<Materials> supplier;

        private FluidMat(Supplier<Materials> supplier) {
            this.supplier = supplier;
        }

        public Materials getMaterial() {
            return supplier.get();
        }

        public FluidStack getFluidOrGas(int amount) {
            final Materials material = supplier.get();
            if (material == null) return null;
            final FluidStack fluid = material.getFluid(amount);
            return fluid != null ? fluid : material.getGas(amount);
        }

        public FluidStack getFluid(int amount) {
            final Materials material = supplier.get();
            return material == null ? null : material.getFluid(amount);
        }
    }
}
