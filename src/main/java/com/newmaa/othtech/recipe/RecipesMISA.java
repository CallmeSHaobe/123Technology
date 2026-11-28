package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.utils.Utils.setStackSize;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;

import com.newmaa.othtech.Config;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.utils.RecipeFluids;
import com.newmaa.othtech.utils.RecipeStacks;

import bartworks.system.material.WerkstoffLoader;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.common.items.CombType;
import gregtech.common.items.MetaGeneratedItem01;
import gregtech.loaders.misc.GTBees;
import gtPlusPlus.core.item.base.BaseItemComponent.ComponentTypes;
import gtPlusPlus.core.material.MaterialsElements;
import gtPlusPlus.core.material.MaterialsElements.STANDALONE;
import gtnhlanth.common.register.WerkstoffMaterialPool;

public class RecipesMISA implements IRecipePool {

    public void loadRecipes() {
        // Pyrope
        GTValues.RA.stdBuilder()
            .itemOutputs(
                setStackSize(Materials.Magnesium.getDust(1), 16 * 110),
                setStackSize(Materials.Manganese.getDust(1), 16 * 70),
                setStackSize(Materials.Borax.getDust(1), 16 * 60),
                setStackSize(
                    RecipeStacks.gtppComponent(1, MaterialsElements.getInstance().RHENIUM, ComponentTypes.DUST),
                    16 * 20))
            .itemInputsUnsafe(
                setStackSize(
                    RecipeStacks.generated(1, MetaGeneratedItem01.INSTANCE, OrePrefixes.crushed, Materials.Pyrope),
                    16 * 256),
                setStackSize(Materials.Carbon.getDust(1), 40),
                Materials.Sulfur.getDust(80),
                Materials.Sodium.getDust(20))
            .fluidInputs(
                FluidRegistry.getFluidStack(RecipeFluids.registryName(Materials.Ethanol.mFluid), 20000),
                Materials.Steam.getGas(4000))
            .fluidOutputs(
                FluidRegistry.getFluidStack(FluidRegistry.WATER.getName(), 32000),
                FluidRegistry.getFluidStack("mud.red.slurry", 32000))
            .specialValue(1)
            .eut(30720)
            .duration(400 * 20)
            .addTo(Recipemaps.MISA);

        // Copper
        GTValues.RA.stdBuilder()
            .itemOutputs(
                setStackSize(Materials.Copper.getDust(1), 16 * 180),
                setStackSize(Materials.Iron.getDust(1), 16 * 120),
                setStackSize(Materials.Cadmium.getDust(1), 16 * 50),
                setStackSize(Materials.Indium.getDust(1), 16 * 10))
            .itemInputsUnsafe(
                setStackSize(
                    RecipeStacks
                        .generated(1, MetaGeneratedItem01.INSTANCE, OrePrefixes.crushed, Materials.Chalcopyrite),
                    16 * 256),
                setStackSize(Materials.Carbon.getDust(1), 40),
                Materials.Sulfur.getDust(80),
                Materials.Sodium.getDust(20))
            .fluidInputs(
                FluidRegistry.getFluidStack(RecipeFluids.registryName(Materials.Ethanol.mFluid), 20000),
                Materials.Steam.getGas(4000))
            .fluidOutputs(
                FluidRegistry.getFluidStack(FluidRegistry.WATER.getName(), 32000),
                FluidRegistry.getFluidStack("mud.red.slurry", 32000))
            .specialValue(1)
            .eut(122880)
            .duration(400 * 20)
            .addTo(Recipemaps.MISA);
        // Nickel
        GTValues.RA.stdBuilder()
            .itemOutputs(
                setStackSize(Materials.Magnesium.getDust(1), 16 * 150),
                setStackSize(Materials.Manganese.getDust(1), 16 * 110),
                setStackSize(Materials.Borax.getDust(1), 16 * 32),
                setStackSize(RecipeStacks.werkstoff(1, OrePrefixes.dust, WerkstoffLoader.Ruthenium), 16 * 16))
            .itemInputsUnsafe(
                setStackSize(
                    RecipeStacks.generated(1, MetaGeneratedItem01.INSTANCE, OrePrefixes.crushed, Materials.Nickel),
                    16 * 256),
                setStackSize(Materials.Carbon.getDust(1), 40),
                Materials.Sulfur.getDust(80),
                Materials.Sodium.getDust(20))
            .fluidInputs(
                FluidRegistry.getFluidStack(RecipeFluids.registryName(Materials.Ethanol.mFluid), 20000),
                Materials.Steam.getGas(4000))
            .fluidOutputs(
                FluidRegistry.getFluidStack(FluidRegistry.WATER.getName(), 32000),
                FluidRegistry.getFluidStack("mud.red.slurry", 32000))
            .specialValue(1)
            .eut(122880)
            .duration(400 * 20)
            .addTo(Recipemaps.MISA);
        // Red Stone
        GTValues.RA.stdBuilder()
            .itemOutputs(
                setStackSize(Materials.Redstone.getDust(1), 16 * 300),
                setStackSize(Materials.Chrome.getDust(1), 16 * 60),
                setStackSize(Materials.Firestone.getDust(1), 16 * 45),
                setStackSize(Materials.Dysprosium.getDust(1), 16 * 16))
            .itemInputsUnsafe(
                setStackSize(
                    RecipeStacks.generated(1, MetaGeneratedItem01.INSTANCE, OrePrefixes.crushed, Materials.Redstone),
                    16 * 256),
                setStackSize(Materials.Carbon.getDust(1), 40),
                Materials.Sulfur.getDust(80),
                Materials.Sodium.getDust(20))
            .fluidInputs(
                FluidRegistry.getFluidStack(RecipeFluids.registryName(Materials.Ethanol.mFluid), 20000),
                Materials.Steam.getGas(4000))
            .fluidOutputs(
                FluidRegistry.getFluidStack(FluidRegistry.WATER.getName(), 32000),
                FluidRegistry.getFluidStack("mud.red.slurry", 32000))
            .specialValue(1)
            .eut(122880)
            .duration(400 * 20)
            .addTo(Recipemaps.MISA);
        // Zinc
        GTValues.RA.stdBuilder()
            .itemOutputs(
                setStackSize(Materials.Zinc.getDust(1), 16 * 180),
                setStackSize(Materials.Iron.getDust(1), 16 * 120),
                setStackSize(Materials.Indium.getDust(1), 16 * 64),
                setStackSize(
                    RecipeStacks.gtppComponent(1, MaterialsElements.getInstance().GERMANIUM, ComponentTypes.DUST),
                    16 * 15))
            .itemInputsUnsafe(
                setStackSize(
                    RecipeStacks.generated(1, MetaGeneratedItem01.INSTANCE, OrePrefixes.crushed, Materials.Sphalerite),
                    16 * 256),
                setStackSize(Materials.Carbon.getDust(1), 40),
                Materials.Sulfur.getDust(80),
                Materials.Sodium.getDust(20))
            .fluidInputs(
                FluidRegistry.getFluidStack(RecipeFluids.registryName(Materials.Ethanol.mFluid), 20000),
                Materials.Steam.getGas(4000))
            .fluidOutputs(
                FluidRegistry.getFluidStack(FluidRegistry.WATER.getName(), 32000),
                FluidRegistry.getFluidStack("mud.red.slurry", 32000))
            .specialValue(1)
            .eut(491520)
            .duration(400 * 20)
            .addTo(Recipemaps.MISA);
        // Platinum
        GTValues.RA.stdBuilder()
            .itemOutputs(
                setStackSize(Materials.Platinum.getDust(1), 16 * 128),
                setStackSize(Materials.Tellurium.getDust(1), 16 * 70),
                setStackSize(RecipeStacks.werkstoff(1, OrePrefixes.dust, WerkstoffLoader.Rhodium), 16 * 60),
                setStackSize(
                    RecipeStacks.gtppComponent(1, MaterialsElements.getInstance().SELENIUM, ComponentTypes.DUST, 0),
                    16 * 40))
            .itemInputsUnsafe(
                setStackSize(
                    RecipeStacks.generated(1, MetaGeneratedItem01.INSTANCE, OrePrefixes.crushed, Materials.Platinum),
                    16 * 256),
                setStackSize(Materials.Carbon.getDust(1), 40),
                Materials.Sulfur.getDust(80),
                Materials.Sodium.getDust(20))
            .fluidInputs(
                FluidRegistry.getFluidStack(RecipeFluids.registryName(Materials.Ethanol.mFluid), 20000),
                Materials.Steam.getGas(4000))
            .fluidOutputs(
                FluidRegistry.getFluidStack(FluidRegistry.WATER.getName(), 32000),
                FluidRegistry.getFluidStack("mud.red.slurry", 32000))
            .specialValue(1)
            .eut(491520)
            .duration(400 * 20)
            .addTo(Recipemaps.MISA);
        // Pentlandite
        GTValues.RA.stdBuilder()
            .itemOutputs(
                setStackSize(Materials.Iron.getDust(1), 16 * 150),
                setStackSize(Materials.Nickel.getDust(1), 16 * 100),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(
                            1,
                            WerkstoffLoader.items.get(OrePrefixes.dust),
                            (Mods.GTNHLanthanides.isModLoaded() ? WerkstoffMaterialPool.Hafnium.getmID() : 11000))
                        : null),
                    16 * 10),
                setStackSize(Materials.Promethium.getDust(1), 16 * 20))
            .itemInputsUnsafe(
                setStackSize(
                    RecipeStacks.generated(1, MetaGeneratedItem01.INSTANCE, OrePrefixes.crushed, Materials.Pentlandite),
                    16 * 256),
                setStackSize(Materials.Carbon.getDust(1), 40),
                Materials.Sulfur.getDust(80),
                Materials.Sodium.getDust(20))
            .fluidInputs(
                FluidRegistry.getFluidStack(RecipeFluids.registryName(Materials.Ethanol.mFluid), 20000),
                Materials.Steam.getGas(4000))
            .fluidOutputs(
                FluidRegistry.getFluidStack(FluidRegistry.WATER.getName(), 32000),
                FluidRegistry.getFluidStack("mud.red.slurry", 32000))
            .specialValue(1)
            .eut(491520)
            .duration(400 * 20)
            .addTo(Recipemaps.MISA);
        // Spessartine
        GTValues.RA.stdBuilder()
            .itemOutputs(
                setStackSize(Materials.Manganese.getDust(1), 16 * 150),
                setStackSize(Materials.Aluminium.getDust(1), 16 * 90),
                setStackSize(Materials.Osmium.getDust(1), 16 * 30),
                setStackSize(Materials.Strontium.getDust(1), 16 * 20))
            .itemInputsUnsafe(
                setStackSize(
                    RecipeStacks.generated(1, MetaGeneratedItem01.INSTANCE, OrePrefixes.crushed, Materials.Spessartine),
                    16 * 256),
                setStackSize(Materials.Carbon.getDust(1), 40),
                Materials.Sulfur.getDust(80),
                Materials.Sodium.getDust(20))
            .fluidInputs(
                FluidRegistry.getFluidStack(RecipeFluids.registryName(Materials.Ethanol.mFluid), 20000),
                Materials.Steam.getGas(4000))
            .fluidOutputs(
                FluidRegistry.getFluidStack(FluidRegistry.WATER.getName(), 32000),
                FluidRegistry.getFluidStack("mud.red.slurry", 32000))
            .specialValue(1)
            .eut(491520)
            .duration(400 * 20)
            .addTo(Recipemaps.MISA);
        // Grossualr
        GTValues.RA.stdBuilder()
            .itemOutputs(
                setStackSize(Materials.Calcium.getDust(1), 16 * 180),
                setStackSize(Materials.Aluminium.getDust(1), 16 * 110),
                setStackSize(Materials.Tungsten.getDust(1), 16 * 60),
                setStackSize(
                    RecipeStacks.gtppComponent(1, MaterialsElements.getInstance().THALLIUM, ComponentTypes.DUST),
                    16 * 15))
            .itemInputsUnsafe(
                setStackSize(
                    RecipeStacks.generated(1, MetaGeneratedItem01.INSTANCE, OrePrefixes.crushed, Materials.Grossular),
                    16 * 256),
                setStackSize(Materials.Carbon.getDust(1), 40),
                Materials.Sulfur.getDust(80),
                Materials.Sodium.getDust(20))
            .fluidInputs(
                FluidRegistry.getFluidStack(RecipeFluids.registryName(Materials.Ethanol.mFluid), 20000),
                Materials.Steam.getGas(4000))
            .fluidOutputs(
                FluidRegistry.getFluidStack(FluidRegistry.WATER.getName(), 32000),
                FluidRegistry.getFluidStack("mud.red.slurry", 32000))
            .specialValue(1)
            .eut(491520)
            .duration(400 * 20)
            .addTo(Recipemaps.MISA);
        // Almandine
        GTValues.RA.stdBuilder()
            .itemOutputs(
                setStackSize(Materials.Aluminium.getDust(1), 16 * 150),
                setStackSize(Materials.Magnesium.getDust(1), 16 * 75),
                setStackSize(Materials.Yttrium.getDust(1), 16 * 25),
                setStackSize(Materials.Ytterbium.getDust(1), 16 * 15))
            .itemInputsUnsafe(
                setStackSize(
                    RecipeStacks.generated(1, MetaGeneratedItem01.INSTANCE, OrePrefixes.crushed, Materials.Almandine),
                    16 * 256),
                setStackSize(Materials.Carbon.getDust(1), 40),
                Materials.Sulfur.getDust(80),
                Materials.Sodium.getDust(20))
            .fluidInputs(
                FluidRegistry.getFluidStack(RecipeFluids.registryName(Materials.Ethanol.mFluid), 20000),
                Materials.Steam.getGas(4000))
            .fluidOutputs(
                FluidRegistry.getFluidStack(FluidRegistry.WATER.getName(), 32000),
                FluidRegistry.getFluidStack("mud.red.slurry", 32000))
            .specialValue(1)
            .eut(491520)
            .duration(400 * 20 * 4)
            .addTo(Recipemaps.MISA);
        // Monazite
        GTValues.RA.stdBuilder()
            .itemOutputs(
                setStackSize(Materials.Erbium.getDust(1), 16 * 64),
                setStackSize(Materials.Lanthanum.getDust(1), 16 * 32),
                setStackSize(Materials.Lutetium.getDust(1), 16 * 16),
                setStackSize(Materials.Europium.getDust(1), 16 * 8))
            .itemInputsUnsafe(
                setStackSize(
                    RecipeStacks.generated(1, MetaGeneratedItem01.INSTANCE, OrePrefixes.crushed, Materials.Monazite),
                    16 * 256),
                setStackSize(Materials.Carbon.getDust(1), 40),
                Materials.Sulfur.getDust(80),
                Materials.Potassium.getDust(20))
            .fluidInputs(
                FluidRegistry.getFluidStack(RecipeFluids.registryName(Materials.Ethanol.mFluid), 20000),
                Materials.Steam.getGas(4000))
            .fluidOutputs(
                FluidRegistry.getFluidStack(FluidRegistry.WATER.getName(), 32000),
                FluidRegistry.getFluidStack("mud.red.slurry", 32000))
            .specialValue(2)
            .eut(7864320)
            .duration(200 * 20)
            .addTo(Recipemaps.MISA);
        // Indium Plus
        GTValues.RA.stdBuilder()
            .itemOutputs(setStackSize(Materials.Indium.getDust(1), 16 * 256))
            .itemInputsUnsafe(setStackSize(GTBees.combs.getStackForType(CombType.INDIUM, 1), 256))
            .fluidInputs(Materials.HydrochloricAcid.getFluid(32000))
            .duration(240 * 20)
            .eut(123123)
            .specialValue(2)
            .addTo(Recipemaps.MISA);
        // Osmium
        GTValues.RA.stdBuilder()
            .itemOutputs(
                setStackSize(Materials.Iridium.getDust(1), 16 * 60),
                setStackSize(Materials.Osmium.getDust(1), 16 * 20))
            .itemInputsUnsafe(
                setStackSize(
                    RecipeStacks.generated(1, MetaGeneratedItem01.INSTANCE, OrePrefixes.crushed, Materials.Osmium),
                    16 * 256),
                setStackSize(Materials.Carbon.getDust(1), 40),
                Materials.Sulfur.getDust(80),
                Materials.Sodium.getDust(20))
            .fluidInputs(
                FluidRegistry.getFluidStack(RecipeFluids.registryName(Materials.Ethanol.mFluid), 20000),
                Materials.Steam.getGas(4000),
                Materials.HydrochloricAcid.getFluid(16000))
            .fluidOutputs(
                FluidRegistry.getFluidStack(FluidRegistry.WATER.getName(), 32000),
                FluidRegistry.getFluidStack("mud.red.slurry", 32000))
            .specialValue(2)
            .eut(491520)
            .duration(400 * 20)
            .addTo(Recipemaps.MISA);
        // Netherite
        GTValues.RA.stdBuilder()
            .itemOutputs(
                setStackSize(Materials.Sulfur.getDust(1), 16 * 256),
                setStackSize(Materials.Antimony.getDust(1), 16 * 55),
                setStackSize(Materials.CertusQuartz.getDust(1), 16 * 40),
                setStackSize(Materials.NetherQuartz.getDust(1), 16 * 40),
                setStackSize(Materials.Ardite.getDust(1), 16 * 32),
                setStackSize(Materials.Cobalt.getDust(1), 16 * 32))
            .itemInputsUnsafe(setStackSize(new ItemStack(Blocks.netherrack, 1), 2048))
            .fluidInputs(Materials.NefariousGas.getFluid(64000 * 3))
            .fluidOutputs(Materials.PoorNetherWaste.getFluid(256_000))
            .specialValue(1)
            .eut(123123)
            .duration(600 * 20)
            .addTo(Recipemaps.MISA);
        if (Config.is_MISA_IMBA_Recipes_Enabled) {
            // Cosmic Neut
            GTValues.RA.stdBuilder()
                .itemInputsUnsafe(
                    setStackSize(
                        RecipeStacks
                            .generated(1, MetaGeneratedItem01.INSTANCE, OrePrefixes.crushed, Materials.BlackPlutonium),
                        16 * 256))
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
            GTValues.RA.stdBuilder()
                .itemOutputs(
                    setStackSize(Materials.Infinity.getDust(1), 16 * 5),
                    setStackSize(Materials.InfinityCatalyst.getDust(1), 16 * 100),
                    setStackSize(Materials.CosmicNeutronium.getDust(1), 16 * 10),
                    setStackSize(Materials.Neutronium.getDust(1), 16 * 20))
                .itemInputsUnsafe(
                    setStackSize(
                        RecipeStacks.generated(
                            1,
                            MetaGeneratedItem01.INSTANCE,
                            OrePrefixes.crushed,
                            Materials.InfinityCatalyst),
                        16 * 256),
                    setStackSize(Materials.Carbon.getDust(1), 40),
                    Materials.Sulfur.getDust(80),
                    Materials.Sodium.getDust(20))
                .fluidInputs(
                    FluidRegistry.getFluidStack(RecipeFluids.registryName(Materials.Ethanol.mFluid), 20000),
                    Materials.Nitrogen.getPlasma(32000))
                .fluidOutputs(Materials.Nitrogen.getGas(32000), FluidRegistry.getFluidStack("mud.red.slurry", 32000))
                .specialValue(3)
                .eut(16777216)
                .duration(500 * 20)
                .addTo(Recipemaps.MISA);
            // Dragon Blood
            GTValues.RA.stdBuilder()
                .itemOutputs(
                    setStackSize(Materials.DraconiumAwakened.getDust(1), 16 * 110),
                    setStackSize(Materials.Draconium.getDust(1), 16 * 40),
                    setStackSize(Materials.NetherStar.getDust(1), 16 * 35),
                    setStackSize(RecipeStacks.gtppComponent(1, STANDALONE.DRAGON_METAL, ComponentTypes.DUST), 16 * 4))
                .itemInputsUnsafe(
                    setStackSize(
                        RecipeStacks.generated(
                            1,
                            MetaGeneratedItem01.INSTANCE,
                            OrePrefixes.crushed,
                            Materials.DraconiumAwakened),
                        16 * 256),
                    setStackSize(Materials.Carbon.getDust(1), 40),
                    Materials.Sulfur.getDust(80),
                    Materials.Sodium.getDust(20))
                .fluidInputs(
                    FluidRegistry.getFluidStack(RecipeFluids.registryName(Materials.Ethanol.mFluid), 20000),
                    Materials.Oxygen.getPlasma(32000))
                .fluidOutputs(Materials.Oxygen.getGas(32000), FluidRegistry.getFluidStack("mud.red.slurry", 32000))
                .specialValue(3)
                .eut(67108864)
                .duration(600 * 20)
                .addTo(Recipemaps.MISA);

        } else {
            // Cosmic Neut
            GTValues.RA.stdBuilder()
                .itemInputsUnsafe(
                    setStackSize(
                        RecipeStacks
                            .generated(1, MetaGeneratedItem01.INSTANCE, OrePrefixes.crushed, Materials.BlackPlutonium),
                        16 * 256))
                .itemOutputs(setStackSize(Materials.BlackPlutonium.getDust(1), 32 * 120))
                .fluidInputs(Materials.Helium.getPlasma(16000))
                .fluidOutputs(Materials.Helium.getGas(16000))
                .duration(600 * 20)
                .eut(1919810)
                .specialValue(2)
                .addTo(Recipemaps.MISA);
            // Inf
            GTValues.RA.stdBuilder()
                .itemOutputs(
                    setStackSize(Materials.InfinityCatalyst.getDust(1), 32 * 100),
                    setStackSize(Materials.CosmicNeutronium.getDust(1), 16 * 10),
                    setStackSize(Materials.Neutronium.getDust(1), 16 * 20))
                .itemInputsUnsafe(
                    setStackSize(
                        RecipeStacks.generated(
                            1,
                            MetaGeneratedItem01.INSTANCE,
                            OrePrefixes.crushed,
                            Materials.InfinityCatalyst),
                        16 * 256),
                    setStackSize(Materials.Carbon.getDust(1), 40),
                    Materials.Sulfur.getDust(80),
                    Materials.Sodium.getDust(20))
                .fluidInputs(
                    FluidRegistry.getFluidStack(RecipeFluids.registryName(Materials.Ethanol.mFluid), 20000),
                    Materials.Nitrogen.getPlasma(32000))
                .fluidOutputs(Materials.Nitrogen.getGas(32000), FluidRegistry.getFluidStack("mud.red.slurry", 32000))
                .specialValue(3)
                .eut(16777216)
                .duration(500 * 20)
                .addTo(Recipemaps.MISA);
            // Dragon Blood
            GTValues.RA.stdBuilder()
                .itemOutputs(
                    setStackSize(Materials.DraconiumAwakened.getDust(1), 16 * 110),
                    setStackSize(Materials.Draconium.getDust(1), 16 * 40),
                    setStackSize(Materials.NetherStar.getDust(1), 16 * 35),
                    setStackSize(RecipeStacks.gtppComponent(1, STANDALONE.DRAGON_METAL, ComponentTypes.DUST), 2 * 4))
                .itemInputsUnsafe(
                    setStackSize(
                        RecipeStacks.generated(
                            1,
                            MetaGeneratedItem01.INSTANCE,
                            OrePrefixes.crushed,
                            Materials.DraconiumAwakened),
                        16 * 256),
                    setStackSize(Materials.Carbon.getDust(1), 40),
                    Materials.Sulfur.getDust(80),
                    Materials.Sodium.getDust(20))
                .fluidInputs(
                    FluidRegistry.getFluidStack(RecipeFluids.registryName(Materials.Ethanol.mFluid), 20000),
                    Materials.Oxygen.getPlasma(32000))
                .fluidOutputs(Materials.Oxygen.getGas(32000), FluidRegistry.getFluidStack("mud.red.slurry", 32000))
                .specialValue(3)
                .eut(67108864)
                .duration(600 * 20)
                .addTo(Recipemaps.MISA);
        }

    }
}
