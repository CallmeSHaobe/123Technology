package com.newmaa.othtech.common.dimensions.ross123b;

import static net.minecraft.init.Blocks.water;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import com.google.common.collect.Lists;
import com.newmaa.othtech.common.blocks.antimonia.AntimoniaBlocks;
import com.newmaa.othtech.common.dimensions.biome.BiomeGenBaseRoss123b;
import com.newmaa.othtech.common.dimensions.structure.ross123b.MapGenBuildingRoss123b;

import galaxyspace.core.dimension.ChunkProviderSpaceLakes;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta;

public class ChunkProviderRoss123b extends ChunkProviderSpaceLakes {

    private final World world;
    public MapGenBuildingRoss123b buildingMapGen = new MapGenBuildingRoss123b();

    public ChunkProviderRoss123b(World world, long seed, boolean mapFeaturesEnabled) {
        super(world, seed, mapFeaturesEnabled);
        this.world = world;
    }

    @Override
    public Chunk provideChunk(int chunkX, int chunkZ) {
        Chunk chunk = new Chunk(this.world, chunkX, chunkZ);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                chunk.func_150807_a(x, 0, z, Blocks.bedrock, 0);
                chunk.func_150807_a(x, 1, z, Blocks.bedrock, 0);
            }
        }

        byte[] biomes = chunk.getBiomeArray();
        byte biomeId = (byte) BiomeGenBaseRoss123b.ross123b.biomeID;
        for (int i = 0; i < biomes.length; i++) {
            biomes[i] = biomeId;
        }

        chunk.generateSkylightMap();
        return chunk;
    }

    @Override
    public boolean chunkExists(int x, int z) {
        return true;
    }

    @Override
    public Chunk loadChunk(int x, int z) {
        return provideChunk(x, z);
    }

    @Override
    public void populate(IChunkProvider provider, int chunkX, int chunkZ) {
        int worldX = chunkX * 16;
        int worldZ = chunkZ * 16;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 2; y <= getWaterLevel(); y++) {
                    world.setBlock(
                        worldX + x,
                        y,
                        worldZ + z,
                        getWaterBlock().getBlock(),
                        getWaterBlock().getMetadata(),
                        2);
                }
            }
        }
        Random rand = new Random(world.getSeed() + (chunkX * 341873128712L) + (chunkZ * 132897987541L));
        buildingMapGen.generateStructuresInChunk(this.world, rand, chunkX, chunkZ);
        int roadWidth = 5;
        int spacingBlocks = 40 + roadWidth; // 大楼 + 道路间隔
        boolean roadX = ((worldX % spacingBlocks + spacingBlocks) % spacingBlocks) < roadWidth;
        boolean roadZ = ((worldZ % spacingBlocks + spacingBlocks) % spacingBlocks) < roadWidth;
        if (roadX || roadZ) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int y = 15;
                    world.setBlock(worldX + x, y, worldZ + z, Blocks.quartz_block);
                }
            }
        }

    }

    @Override
    public boolean saveChunks(boolean all, IProgressUpdate progress) {
        return true;
    }

    @Override
    public boolean canSave() {
        return true;
    }

    @Override
    public String makeString() {
        return "Ross123bSource";
    }

    @Override
    public List getPossibleCreatures(net.minecraft.entity.EnumCreatureType type, int x, int y, int z) {
        return Collections.emptyList();
    }

    @Override
    public int getLoadedChunkCount() {
        return 0;
    }

    @Override
    public void recreateStructures(int x, int z) {
        this.buildingMapGen.func_151539_a(this, this.world, x, z, null);
    }

    @Override
    protected BiomeDecoratorSpace getBiomeGenerator() {
        return null;
    }

    @Override
    protected BiomeGenBase[] getBiomesForGeneration() {
        return new BiomeGenBase[] { BiomeGenBaseRoss123b.ross123b };
    }

    @Override
    public void onChunkProvider(int i, int i1, Block[] blocks, byte[] bytes) {

    }

    @Override
    public void onPopulate(IChunkProvider iChunkProvider, int i, int i1) {

    }

    @Override
    protected BiomeGenBase.SpawnListEntry[] getMonsters() {
        return new BiomeGenBase.SpawnListEntry[0];
    }

    @Override
    protected BiomeGenBase.SpawnListEntry[] getCreatures() {
        return new BiomeGenBase.SpawnListEntry[0];
    }

    @Override
    protected BiomeGenBase.SpawnListEntry[] getWaterCreatures() {
        return new BiomeGenBase.SpawnListEntry[0];
    }

    @Override
    protected List<MapGenBaseMeta> getWorldGenerators() {
        return Lists.newArrayList();
    }

    @Override
    public double getHeightModifier() {
        return 0;
    }

    @Override
    public int getWaterLevel() {
        return 16;
    }

    @Override
    public boolean canGenerateWaterBlock() {
        return true;
    }

    @Override
    public boolean canGenerateIceBlock() {
        return true;
    }

    @Override
    protected BlockMetaPair getWaterBlock() {
        return new BlockMetaPair(water, (byte) 0);
    }

    @Override
    protected BlockMetaPair getGrassBlock() {
        return new BlockMetaPair(AntimoniaBlocks.antimoniaBlockGrass, (byte) 0);
    }

    @Override
    protected BlockMetaPair getDirtBlock() {
        return new BlockMetaPair(AntimoniaBlocks.antimoniaBlockDirt, (byte) 0);
    }

    @Override
    protected BlockMetaPair getStoneBlock() {
        return new BlockMetaPair(AntimoniaBlocks.antimoniaBlockStone, (byte) 0);
    }

    @Override
    protected boolean enableBiomeGenBaseBlock() {
        return false;
    }

    @Override
    public void saveExtraData() {}

}
