package com.newmaa.othtech.common.dimensions;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.MapGenCavesHell;
import net.minecraft.world.gen.MapGenRavine;
import net.minecraft.world.gen.structure.MapGenMineshaft;

import com.google.common.collect.Lists;
import com.newmaa.othtech.common.blocks.antimonia.AntimoniaBlocks;
import com.newmaa.othtech.common.blocks.fluids.AntimoniaFluids;
import com.newmaa.othtech.common.dimensions.biome.BiomeDecOreAntimonia;
import com.newmaa.othtech.common.dimensions.biome.BiomeGenBaseAntimonia;
import com.newmaa.othtech.common.dimensions.structure.MapGenCityAntimonia;

import galaxyspace.core.dimension.ChunkProviderSpaceLakes;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta;

public class ChunkProviderAntimonia extends ChunkProviderSpaceLakes {

    private final World worldObj;
    private final Random rand;

    public ChunkProviderAntimonia(World world, long seed, boolean mapFeaturesEnabled) {
        super(world, seed, mapFeaturesEnabled);
        this.worldObj = world;
        this.rand = new Random(seed);
    }

    private final MapGenCavesHell caveGenerator = new MapGenCavesHell();

    private BiomeGenBase[] biomesForGeneration = this.getBiomesForGeneration();
    private final MapGenRavine ravineGenerator = new MapGenRavine();

    protected List<MapGenBaseMeta> getWorldGenerators() {
        return Lists.newArrayList();
    }

    protected BiomeDecoratorSpace getBiomeGenerator() {
        return new BiomeDecOreAntimonia();
    }

    protected BiomeGenBase[] getBiomesForGeneration() {
        return new BiomeGenBase[] { BiomeGenBaseAntimonia.antimonia };
    }

    public void onChunkProvider(int cX, int cZ, Block[] blocks, byte[] metadata) {
        this.ravineGenerator.func_151539_a(this, this.worldObj, cX, cZ, blocks);
        this.caveGenerator.func_151539_a(this, this.worldObj, cX, cZ, blocks);
    }

    protected BiomeGenBase.SpawnListEntry[] getCreatures() {
        return new BiomeGenBase.SpawnListEntry[0];
    }

    public double getHeightModifier() {
        return 25;
    }

    public int getWaterLevel() {
        return 64;
    }

    public double getMountainHeightModifier() {
        return 800.0;
    }

    protected int getSeaLevel() {
        return 80;
    }

    public double getSmallFeatureHeightModifier() {
        return 100.0;
    }

    public double getValleyHeightModifier() {
        return 60.0;
    }

    public int getCraterProbability() {
        return 100;
    }

    public void onChunkProvide(int cX, int cZ, Block[] blocks, byte[] metadata) {}

    private final MapGenMineshaft MapGenMineshaft = new MapGenMineshaft();
    private final MapGenCityAntimonia villageGeneratorA = new MapGenCityAntimonia();

    @Override
    public void onPopulate(IChunkProvider arg0, int arg1, int arg2) {
        BlockFalling.fallInstantly = true;
        this.rand.setSeed(this.worldObj.getSeed());
        final long var7 = this.rand.nextLong() / 2L * 2L + 1L;
        final long var9 = this.rand.nextLong() / 2L * 2L + 1L;
        this.rand.setSeed(arg1 * var7 + arg2 * var9 ^ this.worldObj.getSeed());
        this.MapGenMineshaft.generateStructuresInChunk(this.worldObj, this.rand, arg1, arg2);
        this.villageGeneratorA.generateStructuresInChunk(this.worldObj, this.rand, arg1, arg2);
        BlockFalling.fallInstantly = false;
    }

    @Override
    protected BiomeGenBase.SpawnListEntry[] getMonsters() {
        return new BiomeGenBase.SpawnListEntry[0];
    }

    @Override
    public void recreateStructures(int par1, int par2) {
        this.MapGenMineshaft.func_151539_a(this, this.worldObj, par1, par2, null);
        this.villageGeneratorA.func_151539_a(this, this.worldObj, par1, par2, null);
    }

    public boolean chunkExists(int x, int y) {
        return false;
    }

    protected BiomeGenBase.SpawnListEntry[] getWaterCreatures() {
        return new BiomeGenBase.SpawnListEntry[0];
    }

    public boolean canGenerateWaterBlock() {
        return true;
    }

    public boolean canGenerateIceBlock() {
        return false;
    }

    protected BlockMetaPair getWaterBlock() {
        return new BlockMetaPair(AntimoniaFluids.BlockAquaRegia, (byte) 0);
    }

    protected BlockMetaPair getGrassBlock() {
        return new BlockMetaPair(AntimoniaBlocks.antimoniaBlockGrass, (byte) 0);
    }

    protected BlockMetaPair getDirtBlock() {
        return new BlockMetaPair(AntimoniaBlocks.antimoniaBlockDirt, (byte) 0);
    }

    protected BlockMetaPair getStoneBlock() {
        return new BlockMetaPair(AntimoniaBlocks.antimoniaBlockStone, (byte) 0);
    }

    protected boolean enableBiomeGenBaseBlock() {
        return false;
    }

    public String makeString() {
        return "AntimoniaLevelSource";
    }
}
