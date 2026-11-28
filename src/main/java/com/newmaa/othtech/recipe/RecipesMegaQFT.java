package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.recipe.RecipesCircuit.getAstralArray;
import static com.newmaa.othtech.utils.Utils.setStackSize;
import static gregtech.api.enums.TierEU.RECIPE_UIV;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.dreammaster.item.NHItemList;
import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.materials.BWLiquids;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.utils.RecipeFluids;
import com.newmaa.othtech.utils.RecipeStacks;

import bartworks.system.material.WerkstoffLoader;
import goodgenerator.items.GGMaterial;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.enums.UndergroundFluidNames;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gregtech.common.items.MetaGeneratedItem01;
import gtPlusPlus.core.item.base.BaseItemComponent.ComponentTypes;
import gtPlusPlus.core.material.MaterialMisc;
import gtPlusPlus.core.material.MaterialsAlloy;
import gtPlusPlus.core.material.MaterialsElements;
import gtPlusPlus.core.material.Particle;
import gtnhlanth.common.register.WerkstoffMaterialPool;

public class RecipesMegaQFT implements IRecipePool {

    @Override
    public void loadRecipes() {
        Fluid H2 = FluidRegistry.getFluid(RecipeFluids.registryName(Materials.Hydrogen.mGas));
        Fluid Cl2 = FluidRegistry.getFluid(RecipeFluids.registryName(Materials.Chlorine.mGas));
        Fluid O2 = FluidRegistry.getFluid(RecipeFluids.registryName(Materials.Oxygen.mGas));
        Fluid F2 = FluidRegistry.getFluid(RecipeFluids.registryName(Materials.Fluorine.mGas));
        Fluid N2 = FluidRegistry.getFluid(RecipeFluids.registryName(Materials.Nitrogen.mGas));

        final RecipeMap<?> MQFT = Recipemaps.QFTMega;

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 64))
            .fluidInputs(
                new FluidStack(F2, 64000),
                new FluidStack(H2, 64000),
                new FluidStack(O2, 64000),
                new FluidStack(Cl2, 64000))
            .fluidOutputs(
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.RubberSilicone.mStandardMoltenFluid)),
                    256 * 144),
                new FluidStack(
                    FluidRegistry
                        .getFluidID(RecipeFluids.registryName(Materials.Polybenzimidazole.mStandardMoltenFluid)),
                    256 * 144),
                new FluidStack(
                    FluidRegistry
                        .getFluidID(RecipeFluids.registryName(Materials.PolyvinylChloride.mStandardMoltenFluid)),
                    256 * 144),
                new FluidStack(
                    FluidRegistry
                        .getFluidID(RecipeFluids.registryName(Materials.Polytetrafluoroethylene.mStandardMoltenFluid)),
                    256 * 144),
                new FluidStack(
                    FluidRegistry
                        .getFluidID(RecipeFluids.registryName(Materials.PolyphenyleneSulfide.mStandardMoltenFluid)),
                    256 * 144),
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.Epoxid.mStandardMoltenFluid)),
                    128 * 144),
                new FluidStack(
                    FluidRegistry
                        .getFluidID(RecipeFluids.registryName(Materials.EpoxidFiberReinforced.mStandardMoltenFluid)),
                    128 * 144),
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.Polyethylene.mStandardMoltenFluid)),
                    256 * 144))

            .duration(200 * 20)
            .eut(TierEU.UHV)
            .addTo(MQFT);
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uranium, 64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Thorium, 64))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uranium235, 64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Plutonium241, 64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Plutonium, 64),
                RecipeStacks.werkstoff(64, OrePrefixes.dust, WerkstoffLoader.Thorium232),
                RecipeStacks.gtppComponent(64, MaterialsElements.getInstance().PLUTONIUM238, ComponentTypes.DUST),
                RecipeStacks.gtppComponent(64, MaterialsElements.getInstance().URANIUM233, ComponentTypes.DUST))

            .duration(200 * 20)
            .eut(TierEU.UIV)
            .addTo(MQFT);
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bismuth, 64))
            .itemOutputs(RecipeStacks.exact(64, ItemList.StableAdhesive))
            .fluidOutputs(new FluidStack(H2, 40000), new FluidStack(O2, 40000))
            .fluidOutputs(
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.gtppMoltenName(MaterialsAlloy.INDALLOY_140)),
                    512 * 144),
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.gtppMoltenName(MaterialMisc.ETHYL_CYANOACRYLATE)),
                    256 * 1000),
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.GlueAdvanced.mFluid)),
                    128 * 1000),
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.SolderingAlloy.mStandardMoltenFluid)),
                    1024 * 144))

            .duration(200 * 20)
            .eut(TierEU.UIV)
            .addTo(MQFT);
        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(3), Materials.Carbon.getDust(64), Materials.Osmium.getDust(24))
            .fluidInputs(new FluidStack(N2, 64000), new FluidStack(H2, 64000))
            .fluidOutputs(
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.Xenoxene.mFluid)),
                    32 * 1000),
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.RadoxPolymer.mStandardMoltenFluid)),
                    256 * 144),
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.Kevlar.mStandardMoltenFluid)),
                    128 * 144),
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.RadoxHeavy.mFluid)),
                    64 * 1000))

            .duration(200 * 20)
            .eut(TierEU.UXV)
            .addTo(MQFT);
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Monazite, 8),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bastnasite, 8))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cerium, 64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Samarium, 64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gadolinium, 64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Holmium, 32),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lanthanum, 32),
                RecipeStacks.werkstoff(32, OrePrefixes.dust, WerkstoffLoader.Zirconium),
                (GregTechAPI.sPreloadStarted && Mods.BartWorks.isModLoaded() ? RecipeStacks.exact(
                    16,
                    WerkstoffLoader.items.get(OrePrefixes.dust),
                    (Mods.GTNHLanthanides.isModLoaded() ? WerkstoffMaterialPool.Hafnium.getmID() : 11000)) : null),
                RecipeStacks.exact(4, ItemList.SuperconductorComposite))

            .duration(200 * 20)
            .eut(TierEU.UIV)
            .addTo(MQFT);
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Monazite, 8),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bastnasite, 8))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cerium, 64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Samarium, 64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gadolinium, 64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Holmium, 32),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lanthanum, 32),
                RecipeStacks.werkstoff(32, OrePrefixes.dust, WerkstoffLoader.Zirconium),
                (GregTechAPI.sPreloadStarted && Mods.BartWorks.isModLoaded() ? RecipeStacks.exact(
                    32,
                    WerkstoffLoader.items.get(OrePrefixes.dust),
                    (Mods.GTNHLanthanides.isModLoaded() ? WerkstoffMaterialPool.Hafnia.getmID() : 11002)) : null),
                RecipeStacks.exact(4, ItemList.SuperconductorComposite))

            .duration(200 * 20)
            .eut(TierEU.UIV)
            .addTo(MQFT);
        GTValues.RA.stdBuilder()
            .itemOutputs(
                Materials.Barium.getDust(64),
                Materials.Indium.getDust(64),
                RecipeStacks.exact(4, ItemList.NaquadriaSupersolid))
            .itemInputs(
                (GregTechAPI.sPreloadStarted && Mods.BartWorks.isModLoaded() ? RecipeStacks.exact(
                    16,
                    WerkstoffLoader.items.get(OrePrefixes.dust),
                    (Mods.GoodGenerator.isModLoaded() ? GGMaterial.naquadriaEarth.getmID() : 10072)) : null),
                Materials.Magnesium.getDust(64),
                Materials.Phosphorus.getDust(8))
            .fluidOutputs(
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.Naquadria.mStandardMoltenFluid)),
                    96 * 144))

            .duration(200 * 20)
            .eut(TierEU.MAX)
            .addTo(MQFT);
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Naquadria.getDust(64))
            .itemOutputs(RecipeStacks.exact(1, ItemList.NaquadriaSupersolid))
            .fluidInputs(
                new FluidStack(FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.ExcitedDTSC.mFluid)), 500))
            .fluidOutputs(
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.Naquadah.mStandardMoltenFluid)),
                    32 * 144))
            .duration(123 * 20)
            .eut(TierEU.UXV)
            .addTo(MQFT);
        GTValues.RA.stdBuilder()
            .fluidInputs(
                new FluidStack(H2, 40000),
                Materials.Helium.getPlasma(30000),
                Materials.Americium.getPlasma(30000),
                new FluidStack(
                    FluidRegistry
                        .getFluidID(RecipeFluids.gtppPlasmaName(MaterialsElements.STANDALONE.CELESTIAL_TUNGSTEN)),
                    30000))
            .fluidOutputs(Materials.Hydrogen.getPlasma(10000))
            .itemOutputs(
                (GregTechAPI.sPreloadStarted && Mods.GTPlusPlus.isModLoaded()
                    ? RecipeStacks.exact(1, Particle.getBaseParticle(Particle.UNKNOWN))
                    : null),
                (GregTechAPI.sPreloadStarted && Mods.GTPlusPlus.isModLoaded()
                    ? RecipeStacks.exact(1, Particle.getBaseParticle(Particle.GRAVITON))
                    : null),
                (GregTechAPI.sPreloadStarted && Mods.GTPlusPlus.isModLoaded()
                    ? RecipeStacks.exact(2, Particle.getBaseParticle(Particle.PROTON))
                    : null),
                (GregTechAPI.sPreloadStarted && Mods.GTPlusPlus.isModLoaded()
                    ? RecipeStacks.exact(2, Particle.getBaseParticle(Particle.LAMBDA))
                    : null),
                (GregTechAPI.sPreloadStarted && Mods.GTPlusPlus.isModLoaded()
                    ? RecipeStacks.exact(2, Particle.getBaseParticle(Particle.W_BOSON))
                    : null),
                (GregTechAPI.sPreloadStarted && Mods.GTPlusPlus.isModLoaded()
                    ? RecipeStacks.exact(2, Particle.getBaseParticle(Particle.Z_BOSON))
                    : null),
                (GregTechAPI.sPreloadStarted && Mods.GTPlusPlus.isModLoaded()
                    ? RecipeStacks.exact(2, Particle.getBaseParticle(Particle.OMEGA))
                    : null),
                (GregTechAPI.sPreloadStarted && Mods.GTPlusPlus.isModLoaded()
                    ? RecipeStacks.exact(2, Particle.getBaseParticle(Particle.HIGGS_BOSON))
                    : null),
                (GregTechAPI.sPreloadStarted && Mods.GTPlusPlus.isModLoaded()
                    ? RecipeStacks.exact(2, Particle.getBaseParticle(Particle.ETA_MESON))
                    : null),
                (GregTechAPI.sPreloadStarted && Mods.GTPlusPlus.isModLoaded()
                    ? RecipeStacks.exact(2, Particle.getBaseParticle(Particle.ELECTRON))
                    : null))
            .duration(400 * 20)
            .eut(TierEU.MAX)

            .addTo(MQFT);
        GTValues.RA.stdBuilder()
            .itemInputs(
                Materials.InfinityCatalyst.getDust(8),
                Materials.Calcium.getDust(64),
                GTModHandler.getModItem("dreamcraft", "GTNHBioItems", 64, 2))
            .fluidOutputs(
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.BioMediumSterilized.mFluid)),
                    1536000),
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.GrowthMediumSterilized.mFluid)),
                    3072000),
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.gtppMoltenName(MaterialMisc.MUTATED_LIVING_SOLDER)),
                    512 * 144))
            .itemOutputs(
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Chip_Stemcell), 256),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Chip_Biocell), 512))

            .duration(400 * 20)
            .eut(TierEU.UXV)
            .addTo(MQFT);
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(18),
                getAstralArray(0),
                GTModHandler.getModItem("eternalsingularity", "eternal_singularity", 64),
                RecipeStacks.exact(64, ItemList.EnergisedTesseract))
            .fluidInputs(
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.PrimordialMatter.mFluid)),
                    1152 * 64))
            .itemOutputs(RecipeStacks.exact(64, ItemList.Timepiece))
            .fluidOutputs(
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.Eternity.mStandardMoltenFluid)),
                    63 * 64 * 144 / 4),
                new FluidStack(
                    FluidRegistry.getFluidID(
                        Mods.GoodGenerator.isModLoaded() ? RecipeFluids.werkstoffMoltenName(GGMaterial.shirabon)
                            : "molten.shirabon"),
                    64 * 64 * 144 / 4),
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.Time.mStandardMoltenFluid)),
                    128 * 64 * 144 / 4),
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.Infinity.mStandardMoltenFluid)),
                    64 * 144 * 4))

            .duration(400 * 20)
            .eut(TierEU.MAX)
            .addTo(MQFT);
        GTValues.RA.stdBuilder()
            .itemOutputs(RecipeStacks.exact(64, NHItemList.PicoWafer.item, 0))
            .itemInputs(
                RecipeStacks.exact(4, ItemList.Circuit_Silicon_Wafer6),
                RecipeStacks.generated(64, MetaGeneratedItem01.INSTANCE, OrePrefixes.dust, Materials.Mytryl),
                GTModHandler.getModItem("GalaxySpace", "tcetiedandelions", 64))
            .fluidInputs(
                new FluidStack(
                    FluidRegistry
                        .getFluidID(RecipeFluids.registryName(Materials.MysteriousCrystal.mStandardMoltenFluid)),
                    2 * 64 * 144),
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.Neutronium.mStandardMoltenFluid)),
                    64 * 144),
                new FluidStack(FluidRegistry.getFluidID(UndergroundFluidNames.unknownWater.name), 25000 * 4))
            .fluidOutputs(
                new FluidStack(
                    FluidRegistry.getFluidID(
                        Mods.GTNHLanthanides.isModLoaded() ? RecipeFluids.werkstoffName(WerkstoffMaterialPool.Iodine)
                            : "iodine"),
                    64 * 4 * 1000),
                new FluidStack(FluidRegistry.getFluidID("seaweedbroth"), 16 * 4 * 1000))

            .duration(1000 * 20)
            .eut(TierEU.UXV)
            .addTo(MQFT);
        GTValues.RA.stdBuilder()
            .fluidOutputs(BWLiquids.Void.getFluidOrGas(4000))
            .itemInputs(GTUtility.getIntegratedCircuit(24), OTHItemList.beeISAM.get(0))
            .fluidInputs(Materials.BoundlessCosmicSolder.getFluid(1000), BWLiquids.Galaxy.getFluidOrGas(20 * 144))

            .duration(100 * 20)
            .eut(TierEU.UXV)
            .addTo(MQFT);
        GTValues.RA.stdBuilder()
            .fluidOutputs(BWLiquids.Stars.getFluidOrGas(4 * 144))
            .itemInputs(
                GTUtility.getIntegratedCircuit(24),
                OTHItemList.beeMagM.get(0),
                GTModHandler.getModItem("GalacticraftAmunRa", "item.baseItem", 4, 26))
            .fluidInputs(
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.ExcitedDTSC.mFluid)),
                    10000),
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.gtppMoltenName(MaterialMisc.MUTATED_LIVING_SOLDER)),
                    24 * 144),
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.MagMatter.mStandardMoltenFluid)),
                    144))

            .duration(120 * 20)
            .eut(TierEU.UXV / 2)
            .addTo(MQFT);
        // test
        GTValues.RA.stdBuilder()
            .itemOutputs(new ItemStack(Blocks.dirt, 1))
            .itemInputs(new ItemStack(Blocks.dirt, 1))

            .duration(114514)
            .eut(1919810)
            .addTo(MQFT);
        // Platinum
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                setStackSize(RecipeStacks.werkstoff(1, OrePrefixes.dust, WerkstoffLoader.PTMetallicPowder), 64))
            .itemOutputs(
                Materials.Platinum.getDust(64),
                Materials.Palladium.getDust(64),
                Materials.Iridium.getDust(64),
                Materials.Osmium.getDust(64),
                RecipeStacks.werkstoff(64, OrePrefixes.dust, WerkstoffLoader.Rhodium),
                RecipeStacks.werkstoff(64, OrePrefixes.dust, WerkstoffLoader.Ruthenium))
            .duration(10 * 20)
            .eut(TierEU.UHV)
            .addTo(MQFT);
        // wasser
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(GTModHandler.getModItem("miscutils", "item.BasicGenericChemItem", 0, 30))
            .fluidInputs(Materials.Water.getFluid(123123123))
            .fluidOutputs(
                Materials.Grade1PurifiedWater.getFluid(25600000),
                Materials.Grade2PurifiedWater.getFluid(12800000),
                Materials.Grade3PurifiedWater.getFluid(6400000),
                Materials.Grade4PurifiedWater.getFluid(3200000),
                Materials.Grade5PurifiedWater.getFluid(1600000),
                Materials.Grade6PurifiedWater.getFluid(800000),
                Materials.Grade7PurifiedWater.getFluid(400000),
                Materials.Grade8PurifiedWater.getFluid(200000),
                Materials.StableBaryonicMatter.getFluid(50000))
            .duration(1200 * 20)
            .eut(TierEU.RECIPE_MAX / 2)
            .addTo(MQFT);
        // Nq
        GTValues.RA.stdBuilder()
            .fluidInputs(
                Materials.NaquadahEnriched.getMolten(144 * 256),
                Materials.Hydrogen.getGas(32000),
                Materials.Nitrogen.getGas(32000 * 12),
                Materials.Oxygen.getGas(32000))
            .fluidOutputs(
                Materials.PrismaticNaquadah.getMolten(256 * 144),
                Materials.Strontium.getMolten(256 * 144),
                Materials.PrismaticAcid.getFluid(1000 * 1000))
            .itemInputsUnsafe(GTModHandler.getModItem(Mods.Botania.ID, "manaResource", 24, 10))
            .duration(123 * 20)
            .eut(RECIPE_UIV)
            .addTo(MQFT);
    }
}
