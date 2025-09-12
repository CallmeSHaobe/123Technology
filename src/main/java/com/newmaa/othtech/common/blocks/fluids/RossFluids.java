package com.newmaa.othtech.common.blocks.fluids;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import cpw.mods.fml.common.registry.GameRegistry;

public class RossFluids {

    public static Fluid LCL;
    public static Block BlockLCL;

    public RossFluids() {}

    public static void initialize() {
        FluidRegistry.registerFluid(
            LCL = (new Fluid("lcl")).setDensity(100)
                .setViscosity(300));
        LCL = FluidRegistry.getFluid("lcl");
        GameRegistry.registerBlock(BlockLCL = (new FluidLCL(LCL, Material.water)).setBlockName("LCL"), "LCL");
    }

}
