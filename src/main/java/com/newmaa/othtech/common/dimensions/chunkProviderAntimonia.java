package com.newmaa.othtech.common.dimensions;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;

import com.google.common.collect.Lists;
import com.newmaa.othtech.common.blocks.antimonia.antimoniaBlocks;
import com.newmaa.othtech.common.blocks.fluids.antimoniaFluids;
import com.newmaa.othtech.common.entity.entityDogYellow;

import galaxyspace.VegaSystem.planets.vegaB.world.BiomeDecoratorVegaBOre;
import galaxyspace.core.dimension.ChunkProviderSpaceLakes;
import galaxyspace.core.world.GSBiomeGenBase;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta;

public class chunkProviderAntimonia extends ChunkProviderSpaceLakes {

    private BiomeGenBase[] biomesForGeneration = this.getBiomesForGeneration();

    protected List<MapGenBaseMeta> getWorldGenerators() {
        return Lists.newArrayList();
    }

    public chunkProviderAntimonia(World par1World, long seed, boolean mapFeaturesEnabled) {
        super(par1World, seed, mapFeaturesEnabled);
    }

    protected BiomeDecoratorSpace getBiomeGenerator() {
        return new BiomeDecoratorVegaBOre();
    }

    protected BiomeGenBase[] getBiomesForGeneration() {
        return new BiomeGenBase[] { GSBiomeGenBase.SpaceGS };
    }

    public void onChunkProvider(int i, int i1, Block[] blocks, byte[] metadata) {

    }

    protected BiomeGenBase.SpawnListEntry[] getCreatures() {
        return new BiomeGenBase.SpawnListEntry[0];
    }

    public double getHeightModifier() {
        return 100;
    }

    @Override
    public int getWaterLevel() {
        return 80;
    }

    protected BiomeGenBase.SpawnListEntry[] getMonsters() {
        BiomeGenBase.SpawnListEntry dog = new BiomeGenBase.SpawnListEntry(entityDogYellow.class, 100, 4, 4);
        return new BiomeGenBase.SpawnListEntry[] { dog };
    }

    // public double getMountainHeightModifier() {
    // return 800.0;
    // }
    //
    // protected int getSeaLevel() {
    // return 80;
    // }
    //
    // public double getSmallFeatureHeightModifier() {
    // return 100.0;
    // }
    //
    // public double getValleyHeightModifier() {
    // return 60.0;
    // }
    //
    // public void onChunkProvide(int cX, int cZ, Block[] blocks, byte[] metadata) {
    // }

    public void onPopulate(IChunkProvider arg0, int arg1, int arg2) {}

    public boolean chunkExists(int x, int y) {
        return false;
    }

    protected BiomeGenBase.SpawnListEntry[] getWaterCreatures() {
        return new BiomeGenBase.SpawnListEntry[0];
    }

    public boolean canGenerateWaterBlock() {
        return true;
    }

    @Override
    public boolean canGenerateIceBlock() {
        return false;
    }

    protected BlockMetaPair getWaterBlock() {
        return new BlockMetaPair(antimoniaFluids.BlockAquaRegia, (byte) 0);
    }

    protected BlockMetaPair getGrassBlock() {
        return new BlockMetaPair(antimoniaBlocks.antimoniaBlockGrass, (byte) 0);
    }

    protected BlockMetaPair getDirtBlock() {
        return new BlockMetaPair(antimoniaBlocks.antimoniaBlockDirt, (byte) 0);
    }

    protected BlockMetaPair getStoneBlock() {
        return new BlockMetaPair(antimoniaBlocks.antimoniaBlockStone, (byte) 0);
    }

    protected boolean enableBiomeGenBaseBlock() {
        return false;
    }
}
