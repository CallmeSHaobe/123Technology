package com.newmaa.othtech.common.blocks.fluids;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import cpw.mods.fml.common.registry.GameRegistry;

public class RossFluids {

    /** 第三次冲击的 LCL 红, 管道/储罐/NEI 里显示的颜色。 */
    public static final int LCL_COLOR = 0xFFC41E12;

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
