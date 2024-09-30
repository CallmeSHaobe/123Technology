package com.newmaa.othtech.recipe;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.newmaa.othtech.common.materials.liquids;
import com.newmaa.othtech.common.recipemap.Recipemaps;

import gregtech.api.enums.GT_Values;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.util.GT_Utility;

public class Mega_QFT_Pool implements IRecipePool {

    @Override
    public void loadRecipes() {
        Fluid H2 = FluidRegistry.getFluid("hydrogen");
        Fluid Cl2 = FluidRegistry.getFluid("chlorine");
        Fluid O2 = FluidRegistry.getFluid("oxygen");
        Fluid F2 = FluidRegistry.getFluid("fluorine");
        Fluid N2 = FluidRegistry.getFluid("nitrogen");

        final RecipeMap<?> MQFT = Recipemaps.QFTMega;
        GT_Values.RA.stdBuilder()
            .fluidOutputs(liquids.MagmatterA.getFluidOrGas(4 * 144))
            .itemInputs(
                GT_ModHandler.getModItem("123Technology", "MagBee", 1),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 4, 17583))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("molten.tengampurified"), 256 * 144))
            .noOptimize()
            .duration(160 * 20)
            .eut(TierEU.UXV)
            .addTo(MQFT);
        GT_Values.RA.stdBuilder()
            .itemInputs(
                GT_Utility.getIntegratedCircuit(1),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 64))
            .fluidInputs(
                new FluidStack(F2, 64000),
                new FluidStack(H2, 64000),
                new FluidStack(O2, 64000),
                new FluidStack(Cl2, 64000))
            .fluidOutputs(
                new FluidStack(FluidRegistry.getFluidID("molten.silicone"), 256 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.polybenzimidazole"), 256 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.polyvinylchloride"), 256 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.polytetrafluoroethylene"), 256 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.polyphenylenesulfide"), 256 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.epoxid"), 128 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.epoxidfiberreinforced"), 128 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.plastic"), 256 * 144))
            .noOptimize()
            .duration(200 * 20)
            .eut(TierEU.UHV)
            .addTo(MQFT);
        GT_Values.RA.stdBuilder()
            .itemInputs(
                GT_Utility.getIntegratedCircuit(1),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Uranium, 64),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Thorium, 64))
            .itemOutputs(
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Uranium235, 64),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Plutonium241, 64),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Plutonium, 64),
                GT_ModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 64, 30),
                GT_ModHandler.getModItem("miscutils", "itemDustPlutonium238", 64),
                GT_ModHandler.getModItem("miscutils", "itemDustUranium233", 64))
            .noOptimize()
            .duration(200 * 20)
            .eut(TierEU.UIV)
            .addTo(MQFT);
        GT_Values.RA.stdBuilder()
            .itemInputs(
                GT_Utility.getIntegratedCircuit(2),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 64),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Bismuth, 64))
            .itemOutputs(GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 32427))
            .fluidOutputs(new FluidStack(H2, 40000), new FluidStack(O2, 40000))
            .fluidOutputs(
                new FluidStack(FluidRegistry.getFluidID("molten.indalloy140"), 512 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.ethylcyanoacrylatesuperglue"), 256 * 1000),
                new FluidStack(FluidRegistry.getFluidID("advancedglue"), 128 * 1000),
                new FluidStack(FluidRegistry.getFluidID("molten.solderingalloy"), 1024 * 144))
            .noOptimize()
            .duration(200 * 20)
            .eut(TierEU.UIV)
            .addTo(MQFT);
        GT_Values.RA.stdBuilder()
            .itemInputs(GT_Utility.getIntegratedCircuit(3), Materials.Carbon.getDust(64), Materials.Osmium.getDust(24))
            .fluidInputs(new FluidStack(N2, 64000), new FluidStack(H2, 64000))
            .fluidOutputs(
                new FluidStack(FluidRegistry.getFluidID("xenoxene"), 32 * 1000),
                new FluidStack(FluidRegistry.getFluidID("molten.radoxpoly"), 256 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.kevlar"), 128 * 144))
            .noOptimize()
            .duration(200 * 20)
            .eut(TierEU.UXV)
            .addTo(MQFT);
        GT_Values.RA.stdBuilder()
            .itemInputs(
                GT_Utility.getIntegratedCircuit(1),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Monazite, 8),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Bastnasite, 8))
            .itemOutputs(
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Cerium, 64),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Samarium, 64),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Gadolinium, 64),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Holmium, 32),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Lanthanum, 32),
                GT_ModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 32, 11007),
                GT_ModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 16, 11000),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 4, 32428))
            .noOptimize()
            .duration(200 * 20)
            .eut(TierEU.UIV)
            .addTo(MQFT);
        GT_Values.RA.stdBuilder()
            .itemInputs(
                GT_Utility.getIntegratedCircuit(2),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Monazite, 8),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Bastnasite, 8))
            .itemOutputs(
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Cerium, 64),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Samarium, 64),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Gadolinium, 64),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Holmium, 32),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Lanthanum, 32),
                GT_ModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 32, 11007),
                GT_ModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 32, 11002),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 4, 32428))
            .noOptimize()
            .duration(200 * 20)
            .eut(TierEU.UIV)
            .addTo(MQFT);
        GT_Values.RA.stdBuilder()
            .itemOutputs(
                Materials.Barium.getDust(64),
                Materials.Indium.getDust(64),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 4, 32429))
            .itemInputs(
                GT_ModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 16, 10072),
                Materials.Magnesium.getDust(64),
                Materials.Phosphorus.getDust(8))
            .fluidOutputs(new FluidStack(FluidRegistry.getFluidID("molten.naquadria"), 96 * 144))
            .noOptimize()
            .duration(200 * 20)
            .eut(TierEU.MAX)
            .addTo(MQFT);
        GT_Values.RA.stdBuilder()
            .itemInputs(Materials.Naquadria.getDust(64))
            .itemOutputs(GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 32429))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("exciteddtsc"), 500))
            .fluidOutputs(new FluidStack(FluidRegistry.getFluidID("molten.naquadah"), 32 * 144))
            .duration(123 * 20)
            .eut(TierEU.UXV)
            .addTo(MQFT);
        GT_Values.RA.stdBuilder()
            .fluidInputs(
                new FluidStack(H2, 40000),
                Materials.Helium.getPlasma(30000),
                Materials.Americium.getPlasma(30000),
                new FluidStack(FluidRegistry.getFluidID("plasma.celestialtungsten"), 30000))
            .fluidOutputs(Materials.Hydrogen.getPlasma(10000))
            .itemOutputs(
                GT_ModHandler.getModItem("miscutils", "particleBase", 1, 24),
                GT_ModHandler.getModItem("miscutils", "particleBase", 1),
                GT_ModHandler.getModItem("miscutils", "particleBase", 2, 18),
                GT_ModHandler.getModItem("miscutils", "particleBase", 2, 20),
                GT_ModHandler.getModItem("miscutils", "particleBase", 2, 16),
                GT_ModHandler.getModItem("miscutils", "particleBase", 2, 15),
                GT_ModHandler.getModItem("miscutils", "particleBase", 2, 21),
                GT_ModHandler.getModItem("miscutils", "particleBase", 2, 17),
                GT_ModHandler.getModItem("miscutils", "particleBase", 2, 7))
            .duration(400 * 20)
            .eut(TierEU.MAX)
            .noOptimize()
            .addTo(MQFT);
        GT_Values.RA.stdBuilder()
            .itemInputs(
                Materials.InfinityCatalyst.getDust(8),
                Materials.Calcium.getDust(64),
                GT_ModHandler.getModItem("dreamcraft", "GTNHBioItems", 64, 2))
            .fluidOutputs(
                new FluidStack(FluidRegistry.getFluidID("biohmediumsterilized"), 1536000),
                new FluidStack(FluidRegistry.getFluidID("growthmediumsterilized"), 3072000),
                new FluidStack(FluidRegistry.getFluidID("molten.mutatedlivingsolder"), 512 * 144))
            .noOptimize()
            .duration(400 * 20)
            .eut(TierEU.UXV)
            .addTo(MQFT);
        GT_Values.RA.stdBuilder()
            .itemInputs(
                GT_Utility.getIntegratedCircuit(18),
                GT_ModHandler.getModItem("tectech", "item.tm.itemAstralArrayFabricator", 0),
                GT_ModHandler.getModItem("eternalsingularity", "eternal_singularity", 64),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 32417))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("primordialmatter"), 1152 * 64))
            .itemOutputs(GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 32757))
            .fluidOutputs(
                new FluidStack(FluidRegistry.getFluidID("molten.eternity"), 63 * 64 * 144 / 4),
                new FluidStack(FluidRegistry.getFluidID("molten.shirabon"), 64 * 64 * 144 / 4),
                new FluidStack(FluidRegistry.getFluidID("temporalfluid"), 128 * 64 * 144 / 4),
                new FluidStack(FluidRegistry.getFluidID("molten.infinity"), 64 * 144 * 4))
            .noOptimize()
            .duration(400 * 20)
            .eut(TierEU.MAX)
            .addTo(MQFT);
        GT_Values.RA.stdBuilder()
            .itemOutputs(GT_ModHandler.getModItem("dreamcraft", "item.PicoWafer", 64))
            .itemInputs(
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 32722),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 2387),
                GT_ModHandler.getModItem("GalaxySpace", "tcetiedandelions", 64))
            .fluidInputs(
                new FluidStack(FluidRegistry.getFluidID("molten.mysteriouscrystal"), 2 * 64 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.neutronium"), 64 * 144),
                new FluidStack(FluidRegistry.getFluidID("unknowwater"), 25000 * 4))
            .fluidOutputs(
                new FluidStack(FluidRegistry.getFluidID("iodine"), 64 * 4 * 1000),
                new FluidStack(FluidRegistry.getFluidID("seaweedbroth"), 16 * 4 * 1000))
            .noOptimize()
            .duration(1000 * 20)
            .eut(TierEU.UXV)
            .addTo(MQFT);
        GT_Values.RA.stdBuilder()
            .fluidOutputs(liquids.Void.getFluidOrGas(4000))
            .itemInputs(GT_Utility.getIntegratedCircuit(24), GT_ModHandler.getModItem("123Technology", "IsaBee", 0))
            .fluidInputs(
                new FluidStack(FluidRegistry.getFluidID("lubricant"), 64000),
                new FluidStack(FluidRegistry.getFluidID("cryotheum"), 32000),
                liquids.Galaxy.getFluidOrGas(20 * 144))
            .noOptimize()
            .duration(100 * 20)
            .eut(TierEU.UXV)
            .addTo(MQFT);
        GT_Values.RA.stdBuilder()
            .fluidOutputs(liquids.Stars.getFluidOrGas(4 * 144))
            .itemInputs(
                GT_Utility.getIntegratedCircuit(24),
                GT_ModHandler.getModItem("123Technology", "MagBee", 0),
                GT_ModHandler.getModItem("GalacticraftAmunRa", "item.baseItem", 4, 26))
            .fluidInputs(
                new FluidStack(FluidRegistry.getFluidID("exciteddtsc"), 10000),
                new FluidStack(FluidRegistry.getFluidID("molten.mutatedlivingsolder"), 24 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.magmatter"), 144))
            .noOptimize()
            .duration(120 * 20)
            .eut(TierEU.UXV / 2)
            .addTo(MQFT);
        //test
        GT_Values.RA.stdBuilder()
            .itemOutputs(
                new ItemStack(Blocks.dirt, 1)
            )
            .itemInputs(
                new ItemStack(Blocks.dirt, 1)
            )
            .noOptimize()
            .duration(114514)
            .eut(1919810)
            .addTo(MQFT);

    }
}
