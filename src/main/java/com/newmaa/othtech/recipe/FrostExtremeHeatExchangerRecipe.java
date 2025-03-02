package com.newmaa.othtech.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.util.GTRecipe;

public class FrostExtremeHeatExchangerRecipe extends GTRecipe {

    public FrostExtremeHeatExchangerRecipe(ItemStack[] inputI, ItemStack[] outputI, FluidStack[] inputF,
        FluidStack[] outputF) {
        super(inputI, outputI, null, null, inputF, outputF, 20, 0, 0);
    }

    public int getWater() {
        if (this.mFluidInputs != null) {
            return this.mFluidInputs[0].amount;
        }
        return 0;
    }

    public Fluid getSteam() {
        if (this.mFluidOutputs != null) {
            return this.mFluidOutputs[0].getFluid();
        }
        return null;
    }

    public Item getInput() {
        if (this.mInputs != null) {
            return this.mInputs[0].getItem();
        }
        return null;
    }

    public Item getOutput() {
        if (this.mOutputs != null) {
            return this.mOutputs[0].getItem();
        }
        return null;
    }

    public int getEUt() {
        return 0;
    }
}
