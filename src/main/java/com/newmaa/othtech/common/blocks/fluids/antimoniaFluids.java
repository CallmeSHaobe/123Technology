package com.newmaa.othtech.common.blocks.fluids;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.newmaa.othtech.common.item.itemTools.itemBucketAqua;

import cpw.mods.fml.common.registry.GameRegistry;
import galaxyspace.BarnardsSystem.BRItems;
import gregtech.api.enums.ItemList;

public class antimoniaFluids {

    public static Fluid AquaRegia;
    public static Block BlockAquaRegia;
    public static Item UnknowWaterBucket;

    public antimoniaFluids() {}

    public static void initialize() {
        FluidRegistry.registerFluid(
            AquaRegia = (new Fluid("aquaregia")).setDensity(100)
                .setViscosity(300));
        AquaRegia = FluidRegistry.getFluid("aquaregia");
        GameRegistry.registerBlock(
            BlockAquaRegia = (new fluidAquaRegia(AquaRegia, Material.water)).setBlockName("AquaRegia"),
            "aquaregia");
        BRItems.registerItem(UnknowWaterBucket = new itemBucketAqua(BlockAquaRegia));
        FluidContainerRegistry.registerFluidContainer(
            new FluidContainerRegistry.FluidContainerData(
                new FluidStack(AquaRegia, 1000),
                new ItemStack(UnknowWaterBucket),
                ItemList.Cell_Empty.get(1)));
    }
}
