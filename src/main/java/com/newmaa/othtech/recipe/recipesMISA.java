package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.utils.Utils.setStackSize;

import net.minecraftforge.fluids.FluidRegistry;

import com.newmaa.othtech.Config;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.utils.RecipeBuilder;

import gregtech.api.enums.Materials;
import gregtech.api.util.GTModHandler;
import gregtech.common.items.CombType;
import gregtech.loaders.misc.GTBees;

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

        // Copper
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(Materials.Copper.getDust(1), 16 * 180),
                setStackSize(Materials.Iron.getDust(1), 16 * 120),
                setStackSize(Materials.Cadmium.getDust(1), 16 * 50),
                setStackSize(Materials.Indium.getDust(1), 16 * 10))
            .itemInputs(
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 5855), 16 * 256),
                setStackSize(Materials.Carbon.getDust(1), 40),
                Materials.Sulfur.getDust(80),
                Materials.Sodium.getDust(20))
            .fluidInputs(FluidRegistry.getFluidStack("bioethanol", 20000), FluidRegistry.getFluidStack("steam", 4000))
            .fluidOutputs(
                FluidRegistry.getFluidStack("water", 32000),
                FluidRegistry.getFluidStack("mud.red.slurry", 32000))
            .specialValue(1)
            .eut(122880)
            .duration(400 * 20)
            .addTo(Recipemaps.MISA);
        // Nickel
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(Materials.Magnesium.getDust(1), 16 * 150),
                setStackSize(Materials.Manganese.getDust(1), 16 * 110),
                setStackSize(Materials.Borax.getDust(1), 16 * 32),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 64), 16 * 16))
            .itemInputs(
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 5034), 16 * 256),
                setStackSize(Materials.Carbon.getDust(1), 40),
                Materials.Sulfur.getDust(80),
                Materials.Sodium.getDust(20))
            .fluidInputs(FluidRegistry.getFluidStack("bioethanol", 20000), FluidRegistry.getFluidStack("steam", 4000))
            .fluidOutputs(
                FluidRegistry.getFluidStack("water", 32000),
                FluidRegistry.getFluidStack("mud.red.slurry", 32000))
            .specialValue(1)
            .eut(122880)
            .duration(400 * 20)
            .addTo(Recipemaps.MISA);
        // Red Stone
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(Materials.Redstone.getDust(1), 16 * 300),
                setStackSize(Materials.Chrome.getDust(1), 16 * 60),
                setStackSize(Materials.Firestone.getDust(1), 16 * 45),
                setStackSize(Materials.Dysprosium.getDust(1), 16 * 16))
            .itemInputs(
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 5810), 16 * 256),
                setStackSize(Materials.Carbon.getDust(1), 40),
                Materials.Sulfur.getDust(80),
                Materials.Sodium.getDust(20))
            .fluidInputs(FluidRegistry.getFluidStack("bioethanol", 20000), FluidRegistry.getFluidStack("steam", 4000))
            .fluidOutputs(
                FluidRegistry.getFluidStack("water", 32000),
                FluidRegistry.getFluidStack("mud.red.slurry", 32000))
            .specialValue(1)
            .eut(122880)
            .duration(400 * 20)
            .addTo(Recipemaps.MISA);
        // Zinc
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(Materials.Zinc.getDust(1), 16 * 180),
                setStackSize(Materials.Iron.getDust(1), 16 * 120),
                setStackSize(Materials.Indium.getDust(1), 16 * 64),
                setStackSize(GTModHandler.getModItem("miscutils", "itemDustGermanium", 1), 16 * 15))
            .itemInputs(
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 5839), 16 * 256),
                setStackSize(Materials.Carbon.getDust(1), 40),
                Materials.Sulfur.getDust(80),
                Materials.Sodium.getDust(20))
            .fluidInputs(FluidRegistry.getFluidStack("bioethanol", 20000), FluidRegistry.getFluidStack("steam", 4000))
            .fluidOutputs(
                FluidRegistry.getFluidStack("water", 32000),
                FluidRegistry.getFluidStack("mud.red.slurry", 32000))
            .specialValue(1)
            .eut(491520)
            .duration(400 * 20)
            .addTo(Recipemaps.MISA);
        // Platinum
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(Materials.Platinum.getDust(1), 16 * 128),
                setStackSize(Materials.Tellurium.getDust(1), 16 * 70),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 78), 16 * 60),
                setStackSize(GTModHandler.getModItem("miscutils", "itemDustSelenium", 1, 64), 16 * 40))
            .itemInputs(
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 5085), 16 * 256),
                setStackSize(Materials.Carbon.getDust(1), 40),
                Materials.Sulfur.getDust(80),
                Materials.Sodium.getDust(20))
            .fluidInputs(FluidRegistry.getFluidStack("bioethanol", 20000), FluidRegistry.getFluidStack("steam", 4000))
            .fluidOutputs(
                FluidRegistry.getFluidStack("water", 32000),
                FluidRegistry.getFluidStack("mud.red.slurry", 32000))
            .specialValue(1)
            .eut(491520)
            .duration(400 * 20)
            .addTo(Recipemaps.MISA);
        // Pentlandite
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(Materials.Iron.getDust(1), 16 * 150),
                setStackSize(Materials.Nickel.getDust(1), 16 * 100),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 11000), 16 * 10),
                setStackSize(Materials.Prometheum.getDust(1), 16 * 20))
            .itemInputs(
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 5909), 16 * 256),
                setStackSize(Materials.Carbon.getDust(1), 40),
                Materials.Sulfur.getDust(80),
                Materials.Sodium.getDust(20))
            .fluidInputs(FluidRegistry.getFluidStack("bioethanol", 20000), FluidRegistry.getFluidStack("steam", 4000))
            .fluidOutputs(
                FluidRegistry.getFluidStack("water", 32000),
                FluidRegistry.getFluidStack("mud.red.slurry", 32000))
            .specialValue(1)
            .eut(491520)
            .duration(400 * 20)
            .addTo(Recipemaps.MISA);
        // Spessartine
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(Materials.Manganese.getDust(1), 16 * 150),
                setStackSize(Materials.Aluminium.getDust(1), 16 * 90),
                setStackSize(Materials.Osmium.getDust(1), 16 * 30),
                setStackSize(Materials.Strontium.getDust(1), 16 * 20))
            .itemInputs(
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 5838), 16 * 256),
                setStackSize(Materials.Carbon.getDust(1), 40),
                Materials.Sulfur.getDust(80),
                Materials.Sodium.getDust(20))
            .fluidInputs(FluidRegistry.getFluidStack("bioethanol", 20000), FluidRegistry.getFluidStack("steam", 4000))
            .fluidOutputs(
                FluidRegistry.getFluidStack("water", 32000),
                FluidRegistry.getFluidStack("mud.red.slurry", 32000))
            .specialValue(1)
            .eut(491520)
            .duration(400 * 20)
            .addTo(Recipemaps.MISA);
        // Grossualr
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(Materials.Calcium.getDust(1), 16 * 180),
                setStackSize(Materials.Aluminium.getDust(1), 16 * 110),
                setStackSize(Materials.Tungsten.getDust(1), 16 * 60),
                setStackSize(GTModHandler.getModItem("miscutils", "itemDustThallium", 1), 16 * 15))
            .itemInputs(
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 5831), 16 * 256),
                setStackSize(Materials.Carbon.getDust(1), 40),
                Materials.Sulfur.getDust(80),
                Materials.Sodium.getDust(20))
            .fluidInputs(FluidRegistry.getFluidStack("bioethanol", 20000), FluidRegistry.getFluidStack("steam", 4000))
            .fluidOutputs(
                FluidRegistry.getFluidStack("water", 32000),
                FluidRegistry.getFluidStack("mud.red.slurry", 32000))
            .specialValue(1)
            .eut(491520)
            .duration(400 * 20)
            .addTo(Recipemaps.MISA);
        // Almandine
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(Materials.Aluminium.getDust(1), 16 * 150),
                setStackSize(Materials.Magnesium.getDust(1), 16 * 75),
                setStackSize(Materials.Yttrium.getDust(1), 16 * 25),
                setStackSize(Materials.Ytterbium.getDust(1), 16 * 15))
            .itemInputs(
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 5820), 16 * 256),
                setStackSize(Materials.Carbon.getDust(1), 40),
                Materials.Sulfur.getDust(80),
                Materials.Sodium.getDust(20))
            .fluidInputs(FluidRegistry.getFluidStack("bioethanol", 20000), FluidRegistry.getFluidStack("steam", 4000))
            .fluidOutputs(
                FluidRegistry.getFluidStack("water", 32000),
                FluidRegistry.getFluidStack("mud.red.slurry", 32000))
            .specialValue(1)
            .eut(491520)
            .duration(400 * 20 * 4)
            .addTo(Recipemaps.MISA);
        // Monazite
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(Materials.Erbium.getDust(1), 16 * 64),
                setStackSize(Materials.Lanthanum.getDust(1), 16 * 32),
                setStackSize(Materials.Lutetium.getDust(1), 16 * 16),
                setStackSize(Materials.Europium.getDust(1), 16 * 8))
            .itemInputs(
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 5520), 16 * 256),
                setStackSize(Materials.Carbon.getDust(1), 40),
                Materials.Sulfur.getDust(80),
                Materials.Potassium.getDust(20))
            .fluidInputs(FluidRegistry.getFluidStack("bioethanol", 20000), FluidRegistry.getFluidStack("steam", 4000))
            .fluidOutputs(
                FluidRegistry.getFluidStack("water", 32000),
                FluidRegistry.getFluidStack("mud.red.slurry", 32000))
            .specialValue(2)
            .eut(7864320)
            .duration(200 * 20)
            .addTo(Recipemaps.MISA);
        // Indium Plus
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(Materials.Indium.getDust(1), 16 * 256))
            .itemInputs(setStackSize(GTBees.combs.getStackForType(CombType.INDIUM, 1), 256))
            .fluidInputs(Materials.HydrochloricAcid.getFluid(32000))
            .duration(240 * 20)
            .eut(123123)
            .specialValue(2)
            .addTo(Recipemaps.MISA);
        // Osmium
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(Materials.Iridium.getDust(1), 16 * 60),
                setStackSize(Materials.Osmium.getDust(1), 16 * 20))
            .itemInputs(
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 5083), 16 * 256),
                setStackSize(Materials.Carbon.getDust(1), 40),
                Materials.Sulfur.getDust(80),
                Materials.Sodium.getDust(20))
            .fluidInputs(
                FluidRegistry.getFluidStack("bioethanol", 20000),
                FluidRegistry.getFluidStack("steam", 4000),
                Materials.HydrochloricAcid.getFluid(16000))
            .fluidOutputs(
                FluidRegistry.getFluidStack("water", 32000),
                FluidRegistry.getFluidStack("mud.red.slurry", 32000))
            .specialValue(2)
            .eut(491520)
            .duration(400 * 20)
            .addTo(Recipemaps.MISA);
        if (Config.is_MISA_IMBA_Recipes_Enabled) {
            // Cosmic Neut
            RecipeBuilder.builder()
                .itemInputs(setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 5388), 16 * 256))
                .itemOutputs(
                    setStackSize(Materials.BlackPlutonium.getDust(1), 16 * 120),
                    setStackSize(Materials.CosmicNeutronium.getDust(1), 16 * 32))
                .fluidInputs(Materials.Helium.getPlasma(16000))
                .fluidOutputs(Materials.Helium.getGas(16000))
                .duration(600 * 20)
                .eut(1919810)
                .specialValue(2)
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
                .fluidOutputs(Materials.Nitrogen.getGas(32000), FluidRegistry.getFluidStack("mud.red.slurry", 32000))
                .specialValue(3)
                .eut(16777216)
                .duration(500 * 20)
                .addTo(Recipemaps.MISA);
            // Dragon Blood
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
                .fluidOutputs(Materials.Oxygen.getGas(32000), FluidRegistry.getFluidStack("mud.red.slurry", 32000))
                .specialValue(3)
                .eut(67108864)
                .duration(600 * 20)
                .addTo(Recipemaps.MISA);
        } else {
            // Cosmic Neut
            RecipeBuilder.builder()
                .itemInputs(setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 5388), 16 * 256))
                .itemOutputs(setStackSize(Materials.BlackPlutonium.getDust(1), 32 * 120))
                .fluidInputs(Materials.Helium.getPlasma(16000))
                .fluidOutputs(Materials.Helium.getGas(16000))
                .duration(600 * 20)
                .eut(1919810)
                .specialValue(2)
                .addTo(Recipemaps.MISA);
            // Inf
            RecipeBuilder.builder()
                .itemOutputs(
                    setStackSize(Materials.InfinityCatalyst.getDust(1), 32 * 100),
                    setStackSize(Materials.CosmicNeutronium.getDust(1), 16 * 10),
                    setStackSize(Materials.Neutronium.getDust(1), 16 * 20))
                .itemInputs(
                    setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 5394), 16 * 256),
                    setStackSize(Materials.Carbon.getDust(1), 40),
                    Materials.Sulfur.getDust(80),
                    Materials.Sodium.getDust(20))
                .fluidInputs(FluidRegistry.getFluidStack("bioethanol", 20000), Materials.Nitrogen.getPlasma(32000))
                .fluidOutputs(Materials.Nitrogen.getGas(32000), FluidRegistry.getFluidStack("mud.red.slurry", 32000))
                .specialValue(3)
                .eut(16777216)
                .duration(500 * 20)
                .addTo(Recipemaps.MISA);
            // Dragon Blood
            RecipeBuilder.builder()
                .itemOutputs(
                    setStackSize(Materials.DraconiumAwakened.getDust(1), 16 * 110),
                    setStackSize(Materials.Draconium.getDust(1), 16 * 40),
                    setStackSize(Materials.NetherStar.getDust(1), 16 * 35),
                    setStackSize(GTModHandler.getModItem("miscutils", "itemDustDragonblood", 1), 2 * 4))
                .itemInputs(
                    setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 5976), 16 * 256),
                    setStackSize(Materials.Carbon.getDust(1), 40),
                    Materials.Sulfur.getDust(80),
                    Materials.Sodium.getDust(20))
                .fluidInputs(FluidRegistry.getFluidStack("bioethanol", 20000), Materials.Oxygen.getPlasma(32000))
                .fluidOutputs(Materials.Oxygen.getGas(32000), FluidRegistry.getFluidStack("mud.red.slurry", 32000))
                .specialValue(3)
                .eut(67108864)
                .duration(600 * 20)
                .addTo(Recipemaps.MISA);
        }

    }
}
