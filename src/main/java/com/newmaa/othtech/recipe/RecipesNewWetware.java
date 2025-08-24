package com.newmaa.othtech.recipe;

import static gregtech.api.enums.TierEU.RECIPE_ZPM;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;

import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.materials.BWLiquids;
import com.newmaa.othtech.common.recipemap.Recipemaps;

import crazypants.enderio.EnderIO;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;

public class RecipesNewWetware implements IRecipePool {

    @Override
    public void loadRecipes() {
        GTRecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.Osmiridium, 24),
                OTHItemList.Brains.get(1))
            .itemOutputs(OTHItemList.slicedBrains.get(4))
            .fluidInputs(FluidRegistry.getFluidStack("lifeessence", 100))
            .eut(RECIPE_ZPM)
            .duration(10 * GTRecipeBuilder.SECONDS)
            .addTo(RecipeMaps.cutterRecipes);
        GTRecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.Osmiridium, 24),
                OTHItemList.Brains.get(1))
            .itemOutputs(OTHItemList.slicedBrains.get(4))
            .fluidInputs(Materials.UUMatter.getFluid(400))
            .eut(RECIPE_ZPM)
            .duration(10 * GTRecipeBuilder.SECONDS)
            .addTo(RecipeMaps.cutterRecipes);

        GTRecipeBuilder.builder()
            .itemInputs(OTHItemList.slicedBrains.get(16))
            .itemOutputs(OTHItemList.encapsulatedBranins.get(1))
            .fluidInputs(FluidRegistry.getFluidStack("molten.indalloy140", 72))
            .eut(TierEU.RECIPE_LuV)
            .requiresCleanRoom()
            .duration(4 * GTRecipeBuilder.SECONDS)
            .addTo(RecipeMaps.assemblerRecipes);
        // Wetware
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Wetware_Extreme.get(1),
                OTHItemList.slicedBrains.get(2),
                ItemList.Circuit_Parts_Reinforced_Glass_Tube.get(16),
                GTOreDictUnificator.get(OrePrefixes.pipeTiny, Materials.Polybenzimidazole, 12),
                GTOreDictUnificator.get(OrePrefixes.itemCasing, Materials.Naquadria, 16),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.Silicone, 64),
                GTOreDictUnificator.get(OrePrefixes.bolt, Materials.HSSS, 32),
                GTOreDictUnificator.get(OrePrefixes.bolt, Materials.Thaumium, 32))
            .itemOutputs(ItemList.Circuit_Chip_NeuroCPU.get(1))
            .fluidInputs(
                Materials.GrowthMediumSterilized.getFluid(150),
                Materials.UUMatter.getFluid(125),
                Materials.SuperCoolant.getFluid(100))
            .duration(16 * 20)
            .eut(RECIPE_ZPM)
            .addTo(Recipemaps.EIO);

        // Bio
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Bio_Ultra.get(1),
                OTHItemList.slicedBrains.get(4),
                ItemList.Circuit_Parts_Reinforced_Glass_Tube.get(16),
                GTOreDictUnificator.get(OrePrefixes.pipeTiny, Materials.Polybenzimidazole, 12),
                GTOreDictUnificator.get(OrePrefixes.itemCasing, Materials.ElectrumFlux, 16),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.Silicone, 64),
                GTOreDictUnificator.get(OrePrefixes.bolt, Materials.HSSS, 64),
                GTOreDictUnificator.get(OrePrefixes.bolt, Materials.Thaumium, 64))
            .itemOutputs(ItemList.Circuit_Chip_BioCPU.get(1))
            .fluidInputs(
                Materials.BioMediumSterilized.getFluid(400),
                Materials.UUMatter.getFluid(250),
                Materials.SuperCoolant.getFluid(200))
            .duration(16 * 20)
            .eut(RECIPE_ZPM)
            .addTo(Recipemaps.EIO);
        // Adv Recipes
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32746),
                OTHItemList.encapsulatedBranins.get(4),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Polybenzimidazole, 32),
                GTOreDictUnificator.get(OrePrefixes.plateQuadruple, Materials.Silicone, 64),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.HSSS, 64),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Thaumium, 64))
            .fluidInputs(Materials.BioMediumSterilized.getFluid(6400))
            .itemOutputs(ItemList.Circuit_Chip_BioCPU.get(16))
            .eut(RECIPE_ZPM)
            .duration(16 * 12 * 20)
            .addTo(Recipemaps.EIO);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32750),
                OTHItemList.encapsulatedBranins.get(2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Polybenzimidazole, 32),
                GTOreDictUnificator.get(OrePrefixes.plateQuadruple, Materials.Silicone, 64),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.HSSS, 64),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Thaumium, 64))
            .fluidInputs(Materials.GrowthMediumSterilized.getFluid(6400))
            .itemOutputs(ItemList.Circuit_Chip_NeuroCPU.get(16))
            .eut(RECIPE_ZPM)
            .duration(16 * 12 * 20)
            .addTo(Recipemaps.EIO);
        // FakePlayer
        final ItemStack YourTag = GTModHandler.getModItem(EnderIO.MODID, "itemSoulVessel", 1);
        NBTTagCompound You = new NBTTagCompound();
        You.setString("id", "123Technology.CustomPlayer");
        YourTag.setTagCompound(You);

        GTRecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                OTHItemList.Brains.get(1),
                GTModHandler.getModItem(EnderIO.MODID, "itemSoulVessel", 1),
                Materials.Titanium.getPlates(4),
                GTOreDictUnificator.get(OrePrefixes.bolt, Materials.Iridium, 16),
                Materials.Calcium.getPlates(10),
                Materials.Polycaprolactam.getIngots(10),
                ItemList.Electric_Motor_LuV.get(4),
                ItemList.Electric_Pump_LuV.get(1))
            .itemOutputs(YourTag.setStackDisplayName("§b§n人类复制体-RK800-01"))
            .fluidInputs(BWLiquids.LCL.getFluidOrGas(64000))
            .requiresCleanRoom()
            .duration(800 * 20)
            .eut(TierEU.UV)
            .addTo(RecipeMaps.assemblerRecipes);

    }
}
