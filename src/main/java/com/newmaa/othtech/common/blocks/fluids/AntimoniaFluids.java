package com.newmaa.othtech.common.blocks.fluids;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import cpw.mods.fml.common.registry.GameRegistry;

public class AntimoniaFluids {

    public static Fluid AquaRegia;
    public static Block BlockAquaRegia;

    public AntimoniaFluids() {}

    public static void initialize() {
        FluidRegistry.registerFluid(
            AquaRegia = (new Fluid("aquaregiaoth")).setDensity(100)
                .setViscosity(300));
        AquaRegia = FluidRegistry.getFluid("aquaregiaoth");
        GameRegistry.registerBlock(
            BlockAquaRegia = (new FluidAquaRegia(AquaRegia, Material.water)).setBlockName("AquaRegia"),
            "aquaregiaoth");

    }
}
