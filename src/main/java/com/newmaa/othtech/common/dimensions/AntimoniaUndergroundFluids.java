package com.newmaa.othtech.common.dimensions;

import java.util.ArrayList;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.newmaa.othtech.common.materials.BWLiquids;

import gregtech.GTMod;
import gregtech.api.enums.Materials;
import gregtech.api.objects.GTUODimension;
import gregtech.api.util.GTConfig;

/**
 * 锑星的地下流体池: HCl10 / HCl100 / HCl1000 / 王水, GT 石油钻机与探矿仪都能直接抽到。
 * <p>
 * cfg 的 "Dimension" 必须写维度名: NEI 的 {@code GT5UndergroundFluidHelper} 按 DimensionHelper 的
 * trimmedName 反查缩写, 而 GT 运行时 {@code GTUODimensionList#GetDimension} 在 id 查不到时用
 * WorldProvider 类名 contains 匹配, WorldProviderAntimonia 里正好含这个词。
 * 维度本身已由 {@code OTHDimensionIntegration#registerNEIDimension()} 在 preInit 注册进 NEI。
 */
public final class AntimoniaUndergroundFluids {

    private static final String DIM_NAME = "Antimonia";
    private static final String BASE = "undergroundfluid.antimonia";

    private AntimoniaUndergroundFluids() {}

    public static void register() {
        Configuration cfg = GTConfig.undergroundFluidsFile;
        if (cfg == null || GTMod.proxy == null || GTMod.proxy.mUndergroundOil == null) return;

        /// 旧版本可能留下了 water 之类的分类, 直接清空, 只按下面这份表重建
        ConfigCategory root = cfg.getCategory(BASE);
        if (root != null) {
            for (ConfigCategory child : new ArrayList<>(root.getChildren())) {
                root.removeChild(child);
            }
        }

        cfg.get(BASE, "Dimension", DIM_NAME)
            .getString();

        addFluid(cfg, "hcl10", safe(() -> BWLiquids.HCl10.getFluidOrGas(1)), 100, 300);
        addFluid(cfg, "hcl100", safe(() -> BWLiquids.HCl100.getFluidOrGas(1)), 90, 350);
        addFluid(cfg, "hcl1000", safe(() -> BWLiquids.HCl1000.getFluidOrGas(1)), 50, 120);
        addFluid(cfg, "aquaregia", safe(() -> Materials.AquaRegia.getFluid(1)), 150, 800);
        cfg.save();

        ConfigCategory category = cfg.getCategory(BASE);
        if (category == null || category.getChildren()
            .isEmpty()) return;

        // GT 只在 preInit 的 getConfig 里重建这张表, 这里直接塞进去才能不重启生效
        GTMod.proxy.mUndergroundOil.getDimensionList()
            .put(DIM_NAME, new GTUODimension(category));
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
