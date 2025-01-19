package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.Utils.Utils.setStackSize;

import net.minecraftforge.fluids.FluidRegistry;

import com.newmaa.othtech.Utils.RecipeBuilder;
import com.newmaa.othtech.common.recipemap.Recipemaps;

import gregtech.api.enums.Materials;
import gregtech.api.util.GTModHandler;

public class recipesMISA implements IRecipePool {

    public void loadRecipes() {
        // Pyrope
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(Materials.Magnesium.getDust(1), 16 * 110),
                setStackSize(Materials.Manganese.getDust(1), 16 * 70),
                setStackSize(Materials.Borax.getDust(1), 16 * 60),
                setStackSize(GTModHandler.getModItem("miscutils", "itemDustRhenium", 1), 16 * 20))
            .itemInputs(
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 5835), 16 * 256),
                setStackSize(Materials.Carbon.getDust(1), 40),
                Materials.Sulfur.getDust(80),
                Materials.Sodium.getDust(20))
            .fluidInputs(FluidRegistry.getFluidStack("bioethanol", 20000), FluidRegistry.getFluidStack("steam", 4000))
            .fluidOutputs(
                FluidRegistry.getFluidStack("water", 32000),
                FluidRegistry.getFluidStack("mud.red.slurry", 32000))
            .specialValue(1)
            .eut(30720)
            .duration(400 * 20)
            .addTo(Recipemaps.MISA);
        // Inf
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(Materials.Infinity.getDust(1), 16 * 5),
                setStackSize(Materials.InfinityCatalyst.getDust(1), 16 * 100),
                setStackSize(Materials.CosmicNeutronium.getDust(1), 16 * 10),
                setStackSize(Materials.Neutronium.getDust(1), 16 * 20))
            .itemInputs(
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 5394), 16 * 256),
                setStackSize(Materials.Carbon.getDust(1), 40),
                Materials.Sulfur.getDust(80),
                Materials.Sodium.getDust(20))
            .fluidInputs(FluidRegistry.getFluidStack("bioethanol", 20000), Materials.Nitrogen.getPlasma(32000))
            .fluidOutputs(
                Materials.Nitrogen.getGas(32000),
                FluidRegistry.getFluidStack("mud.red.slurry", 32000))
            .specialValue(3)
            .eut(16777216)
            .duration(500 * 20)
            .addTo(Recipemaps.MISA);
        //Dragon Blood
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(Materials.DraconiumAwakened.getDust(1), 16 * 110),
                setStackSize(Materials.Draconium.getDust(1), 16 * 40),
                setStackSize(Materials.NetherStar.getDust(1), 16 * 35),
                setStackSize(GTModHandler.getModItem("miscutils", "itemDustDragonblood", 1), 16 * 4))
            .itemInputs(
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 5976), 16 * 256),
                setStackSize(Materials.Carbon.getDust(1), 40),
                Materials.Sulfur.getDust(80),
                Materials.Sodium.getDust(20))
            .fluidInputs(FluidRegistry.getFluidStack("bioethanol", 20000), Materials.Oxygen.getPlasma(32000))
            .fluidOutputs(
                Materials.Oxygen.getGas(32000),
                FluidRegistry.getFluidStack("mud.red.slurry", 32000))
            .specialValue(3)
            .eut(67108864)
            .duration(600 * 20)
            .addTo(Recipemaps.MISA);
    }
}
