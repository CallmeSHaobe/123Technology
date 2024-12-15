package com.newmaa.othtech.common.beeyonds;

import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.core.IClimateProvider;
import forestry.api.genetics.*;
import forestry.core.genetics.alleles.Allele;
import forestry.core.utils.StringUtil;
import gregtech.GTMod;
import gregtech.api.metatileentity.BaseMetaTileEntity;

import gregtech.common.items.ItemComb;
import gregtech.common.items.ItemDrop;
import gregtech.common.items.ItemPollen;
import gregtech.common.items.ItemPropolis;

import gregtech.loaders.misc.GTBeeDefinition;
import gregtech.loaders.misc.bees.GTAlleleEffect;
import gregtech.loaders.misc.bees.GTEffectMachineBoost;
import gregtech.loaders.misc.bees.GTEffectTreeTwister;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import static gregtech.api.enums.Mods.*;

public class OTHBeeyonds {
    public static IAlleleInteger noFertility;
    public static IAlleleInteger superFertility;

    public static IAlleleInteger noFlowering;
    public static IAlleleInteger superFlowering;

    public static IAlleleArea noTerritory;
    public static IAlleleArea superTerritory;

    public static IAlleleFloat noWork;
    public static IAlleleFloat speedBlinding;
    public static IAlleleFloat superSpeed;

    public static IAlleleInteger blinkLife;
    public static IAlleleInteger superLife;

    public static IAlleleBeeEffect treetwisterEffect;
    public static IAlleleBeeEffect machineBoostEffect;

    public static ItemPropolis propolis;
    public static ItemPollen pollen;
    public static ItemDrop drop;
    public static ItemComb combs;
    public OTHBeeyonds(){
        setupOTHAlleles();
        OTHBeeDefinition.initBees();
    }



    private static void setupOTHAlleles() {

        noFertility = new OTHBeeyonds.AlleleInteger("fertilitySterile", 0, false, EnumBeeChromosome.FERTILITY);
        superFertility = new OTHBeeyonds.AlleleInteger("fertilityMultiply", 8, false, EnumBeeChromosome.FERTILITY);

        noFlowering = new OTHBeeyonds.AlleleInteger("floweringNonpollinating", 0, false, EnumBeeChromosome.FLOWERING);
        superFlowering = new OTHBeeyonds.AlleleInteger("floweringNaturalistic", 240, false, EnumBeeChromosome.FLOWERING);

        noTerritory = new OTHBeeyonds.AlleleArea("areaLethargic", 1, 1, false);
        superTerritory = new OTHBeeyonds.AlleleArea("areaExploratory", 32, 16, false);

        noWork = new OTHBeeyonds.AlleleFloat("speedUnproductive", 0, false);
        superSpeed = new OTHBeeyonds.AlleleFloat("speedAccelerated", 4F, false);
        speedBlinding = AlleleManager.alleleRegistry.getAllele("magicbees.speedBlinding") == null
            ? new OTHBeeyonds.AlleleFloat("speedBlinding", 2f, false)
            : (IAlleleFloat) AlleleManager.alleleRegistry.getAllele("magicbees.speedBlinding");

        blinkLife = new OTHBeeyonds.AlleleInteger("lifeBlink", 2, false, EnumBeeChromosome.LIFESPAN);
        superLife = new OTHBeeyonds.AlleleInteger("lifeEon", 600, false, EnumBeeChromosome.LIFESPAN);
        machineBoostEffect = new GTEffectMachineBoost();

        if (GalaxySpace.isModLoaded() && TwilightForest.isModLoaded()) {
            GTMod.GT_FML_LOGGER.info("treetwisterEffect: GalaxySpace and TwilightForest loaded, using default impl");
            treetwisterEffect = new GTEffectTreeTwister();
        } else {
            GTMod.GT_FML_LOGGER
                .info("treetwisterEffect: GalaxySpace or TwilightForest was not loaded, using fallback impl");
            treetwisterEffect = GTAlleleEffect.FORESTRY_BASE_EFFECT;
        }
    }

    private static class AlleleFloat extends Allele implements IAlleleFloat {

        private final float value;

        public AlleleFloat(String id, float val, boolean isDominant) {
            super("gregtech." + id, "gregtech." + id, isDominant);
            this.value = val;
            AlleleManager.alleleRegistry.registerAllele(this, EnumBeeChromosome.SPEED);
        }

        @Override
        public float getValue() {
            return this.value;
        }
    }

    private static class AlleleInteger extends Allele implements IAlleleInteger {

        private final int value;

        public AlleleInteger(String id, int val, boolean isDominant, EnumBeeChromosome c) {
            super("gregtech." + id, "gregtech." + id, isDominant);
            this.value = val;
            AlleleManager.alleleRegistry.registerAllele(this, c);
        }

        @Override
        public int getValue() {
            return this.value;
        }
    }

    private static class AlleleArea extends Allele implements IAlleleArea {

        private final int[] value;

        public AlleleArea(String id, int rangeXZ, int rangeY, boolean isDominant) {
            super("gregtech." + id, "gregtech." + id, isDominant);
            this.value = new int[] { rangeXZ, rangeY, rangeXZ };
            AlleleManager.alleleRegistry.registerAllele(this, EnumBeeChromosome.TERRITORY);
        }

        @Override
        public int[] getValue() {
            return this.value;
        }
    }

    public static class DimensionMutationCondition implements IMutationCondition {

        final int dimID;
        final String dimName;

        public DimensionMutationCondition(int id, String name) {
            dimID = id;
            dimName = name;
        }

        @Override
        public float getChance(World world, int x, int y, int z, IAllele allele0, IAllele allele1, IGenome genome0,
                               IGenome genome1, IClimateProvider climate) {
            if (world.provider.dimensionId == dimID) return 1;
            return 0;
        }

        @Override
        public String getDescription() {
            return StringUtil.localizeAndFormat("mutation.condition.dim") + " " + dimName;
        }
    }

    public static class BiomeIDMutationCondition implements IMutationCondition {

        final int biomeID;
        final String biomeName;

        public BiomeIDMutationCondition(int id, String name) {
            biomeID = id;
            biomeName = name;
        }

        @Override
        public float getChance(World world, int x, int y, int z, IAllele allele0, IAllele allele1, IGenome genome0,
                               IGenome genome1, IClimateProvider climate) {
            if (climate.getBiome().biomeID == biomeID) return 1;
            return 0;
        }

        @Override
        public String getDescription() {
            if (BiomeGenBase.getBiome(biomeID) != null) {
                return StringUtil.localizeAndFormat("mutation.condition.biomeid") + " " + biomeName;
            }
            return "";
        }
    }

    public static class ActiveGTMachineMutationCondition implements IMutationCondition {

        public ActiveGTMachineMutationCondition() {

        }

        @Override
        public float getChance(World world, int x, int y, int z, IAllele allele0, IAllele allele1, IGenome genome0,
                               IGenome genome1, IClimateProvider climate) {
            TileEntity tileEntity = world.getTileEntity(x, y - 1, z);
            if (tileEntity instanceof BaseMetaTileEntity machine) {
                if (machine.isActive()) {
                    return 1;
                }
            }
            return 0;
        }

        @Override
        public String getDescription() {
            return "Needs a running GT Machine below to breed";
        }
    }
}
