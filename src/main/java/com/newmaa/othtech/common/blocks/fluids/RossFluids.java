package com.newmaa.othtech.common.blocks.fluids;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import cpw.mods.fml.common.registry.GameRegistry;

public class RossFluids {

    /** 与 LCL 材料一致的橙色, 用于管道/储罐/NEI 的流体着色。 */
    public static final int LCL_COLOR = 0xFFFA6F2A;

    public static Fluid LCL;
    public static Block BlockLCL;

    public RossFluids() {}

    public static void initialize() {
        FluidRegistry.registerFluid(LCL = (new Fluid("lcl") {

            @Override
            public int getColor() {
                return LCL_COLOR;
            }
        }).setDensity(100)
            .setViscosity(300));
        LCL = FluidRegistry.getFluid("lcl");
        GameRegistry.registerBlock(BlockLCL = (new FluidLCL(LCL, Material.water)).setBlockName("LCL"), "LCL");
    }

}
