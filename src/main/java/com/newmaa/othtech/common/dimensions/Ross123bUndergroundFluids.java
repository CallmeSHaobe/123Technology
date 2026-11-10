package com.newmaa.othtech.common.dimensions;

import java.util.ArrayList;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.newmaa.othtech.OTHTechnology;
import com.newmaa.othtech.common.blocks.fluids.RossFluids;
import com.newmaa.othtech.common.materials.BWLiquids;

import gregtech.GTMod;
import gregtech.api.objects.GTUODimension;
import gregtech.api.util.GTConfig;

public final class Ross123bUndergroundFluids {

    /** 必须同时对上 WorldProvider 类名与 DimensionHelper 的 trimmedName。 */
    private static final String DIM_NAME = "Ross123b";
    private static final String BASE = "undergroundfluid.ross123b";

    private Ross123bUndergroundFluids() {}

    public static void register() {
        Configuration cfg = GTConfig.undergroundFluidsFile;
        if (cfg == null || GTMod.proxy == null || GTMod.proxy.mUndergroundOil == null) {
            OTHTechnology.LOG
                .warn("[OTHT] GT underground fluid config is not ready, Ross123b LCL fluid wells will be missing");
            return;
        }

        /// 旧版本可能留下了别的分类, 直接清空, 只按下面这份表重建
        ConfigCategory root = cfg.getCategory(BASE);
        if (root != null) {
            for (ConfigCategory child : new ArrayList<>(root.getChildren())) {
                root.removeChild(child);
            }
        }

        cfg.get(BASE, "Dimension", DIM_NAME)
            .getString();

        // LCL 就是这一整个维度的海, 量给得比锑星那几种酸大一些
        addFluid(cfg, "lcl", lclStack(), 200, 2000);
        cfg.save();

        ConfigCategory category = cfg.getCategory(BASE);
        if (category == null || category.getChildren()
            .isEmpty()) {
            OTHTechnology.LOG
                .warn("[OTHT] No underground fluid entry was built for Ross123b, fluid wells will be empty");
            return;
        }

        // GT 只在 preInit 的 getConfig 里重建这张表, 这里直接塞进去才能不重启生效
        GTUODimension dimension = new GTUODimension(category);
        GTMod.proxy.mUndergroundOil.getDimensionList()
            .put(DIM_NAME, dimension);
        OTHTechnology.LOG.info(
            "[OTHT] Ross123b underground fluid wells registered: {} fluid types",
            dimension.getFluids()
                .size());
    }

    /** 优先用 BartWorks 注册的 L.C.L, 拿不到就退回本体自己注册的 lcl。 */
    private static FluidStack lclStack() {
        FluidStack stack = safe(() -> BWLiquids.LCL.getFluidOrGas(1));
        if (stack != null && stack.getFluid() != null) {
            return stack;
        }
        Fluid fluid = RossFluids.LCL != null ? RossFluids.LCL : FluidRegistry.getFluid("lcl");
        return fluid == null ? null : new FluidStack(fluid, 1);
    }

    private static void addFluid(Configuration cfg, String fallbackName, FluidStack stack, int minAmount,
        int maxAmount) {
        String name = stack != null && stack.getFluid() != null ? stack.getFluid()
            .getName() : fallbackName;
        if (FluidRegistry.getFluid(name) == null && FluidRegistry.getFluid(fallbackName) != null) {
            name = fallbackName;
        }

        String cat = BASE + "." + fallbackName;
        cfg.get(cat, "Registry", name)
            .getString();
        cfg.get(cat, "MinAmount", minAmount)
            .getInt(minAmount);
        cfg.get(cat, "MaxAmount", maxAmount)
            .getInt(maxAmount);
        cfg.get(cat, "Chance", 100)
            .getInt(100);
        cfg.get(cat, "DecreasePerOperationAmount", 5)
            .getInt(5);
    }

    /// Werkstoff 的流体表在 preInit 之后才可能就绪, 拿不到就用回退名
    private static FluidStack safe(java.util.function.Supplier<FluidStack> supplier) {
        try {
            return supplier.get();
        } catch (Throwable ignored) {
            return null;
        }
    }
}
