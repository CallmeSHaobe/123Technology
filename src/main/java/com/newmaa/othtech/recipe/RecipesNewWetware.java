package com.newmaa.othtech.recipe;

import static gregtech.api.enums.TierEU.RECIPE_ZPM;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.materials.BWLiquids;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.utils.ExternalRecipeItems;
import com.newmaa.othtech.utils.RecipeFluids;
import com.newmaa.othtech.utils.RecipeStacks;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.objects.SubstituteFluidStack;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;
import gregtech.loaders.postload.recipes.FakeCuttingSpecialInfo;
import gtPlusPlus.core.material.MaterialsAlloy;

public class RecipesNewWetware implements IRecipePool {

    @Override
    public void loadRecipes() {
        addSlicedBrainRecipe(
            GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.Osmiridium, 24),
            OTHItemList.Brains.get(1),
            OTHItemList.slicedBrains.get(4),
            Materials.LifeEssence.getFluid(100));
        addSlicedBrainRecipe(
            GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.Osmiridium, 24),
            OTHItemList.Brains.get(1),
            OTHItemList.slicedBrains.get(4),
            Materials.UUMatter.getFluid(400));

        GTRecipeBuilder.builder()
            .itemInputs(OTHItemList.slicedBrains.get(16))
            .itemOutputs(OTHItemList.encapsulatedBranins.get(1))
            .fluidInputs(FluidRegistry.getFluidStack(RecipeFluids.gtppMoltenName(MaterialsAlloy.INDALLOY_140), 72))
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
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.RubberSilicone, 64),
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
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.RubberSilicone, 64),
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
                (GregTechAPI.sPreloadStarted && Mods.BartWorks.isModLoaded()
                    ? RecipeStacks.exact(1, ItemList.Wrap_UltraBioMutatedCircuitBoards)
                    : null),
                OTHItemList.encapsulatedBranins.get(4),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Polybenzimidazole, 32),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.RubberSilicone, 64),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.RubberSilicone, 64),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.HSSS, 64),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Thaumium, 64))
            .fluidInputs(Materials.BioMediumSterilized.getFluid(6400))
            .itemOutputs(ItemList.Circuit_Chip_BioCPU.get(16))
            .eut(RECIPE_ZPM)
            .duration(16 * 12 * 20)
            .addTo(Recipemaps.EIO);

        GTValues.RA.stdBuilder()
            .itemInputs(
                (GregTechAPI.sPreloadStarted && Mods.BartWorks.isModLoaded()
                    ? RecipeStacks.exact(1, ItemList.Wrap_ExtremeWetwareLifesupportCircuitBoards)
                    : null),
                OTHItemList.encapsulatedBranins.get(2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Polybenzimidazole, 32),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.RubberSilicone, 64),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.RubberSilicone, 64),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.HSSS, 64),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Thaumium, 64))
            .fluidInputs(Materials.GrowthMediumSterilized.getFluid(6400))
            .itemOutputs(ItemList.Circuit_Chip_NeuroCPU.get(16))
            .eut(RECIPE_ZPM)
            .duration(16 * 12 * 20)
            .addTo(Recipemaps.EIO);
        // FakePlayer
        final ItemStack YourTag = ExternalRecipeItems.enderIo(1, ExternalRecipeItems.EnderItem.SOUL_VESSEL, 0);
        NBTTagCompound You = new NBTTagCompound();
        You.setString("id", "123technology.CustomPlayer");
        YourTag.setTagCompound(You);

        GTRecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                OTHItemList.Brains.get(1),
                ExternalRecipeItems.enderIo(1, ExternalRecipeItems.EnderItem.SOUL_VESSEL, 0),
                Materials.Titanium.getPlates(4),
                GTOreDictUnificator.get(OrePrefixes.bolt, Materials.Iridium, 16),
                Materials.Calcium.getPlates(10),
                Materials.Polycaprolactam.getIngots(10),
                ItemList.Electric_Motor_LuV.get(4),
                ItemList.Electric_Pump_LuV.get(1))
            .itemOutputs(YourTag.setStackDisplayName(StatCollector.translateToLocal("oth.recipe.wetware.humanReplica")))
            .fluidInputs(BWLiquids.LCL.getFluidOrGas(64000))
            .requiresCleanRoom()
            .duration(800 * 20)
            .eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.assemblerRecipes);

    }

    private static void addSlicedBrainRecipe(ItemStack wire, ItemStack brain, ItemStack output, FluidStack fluid) {
        if (wire == null || brain == null || output == null || fluid == null) return;

        GTValues.RA.stdBuilder()
            .itemInputs(wire, brain)
            .itemOutputs(output)
            .fluidInputs(fluid)
            .eut(RECIPE_ZPM)
            .duration(10 * SECONDS)
            .addTo(RecipeMaps.cutterRecipes);

        FakeCuttingSpecialInfo specialInfo = new FakeCuttingSpecialInfo();
        specialInfo.add(fluid, 10 * SECONDS);
        GTValues.RA.stdBuilder()
            .itemInputs(wire, brain)
            .itemOutputs(output)
            .fluidInputs(new SubstituteFluidStack(fluid))
            .eut(RECIPE_ZPM)
            .duration(10 * SECONDS)
            .special(specialInfo)
            .fake()
            .addTo(RecipeMaps.cutterFakeRecipes);
    }
}
