package com.newmaa.othtech.common.dimensions.ross123b;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import com.falsepattern.endlessids.mixin.helpers.ChunkBiomeHook;
import com.google.common.collect.Lists;
import com.newmaa.othtech.common.blocks.antimonia.AntimoniaBlocks;
import com.newmaa.othtech.common.blocks.fluids.RossFluids;
import com.newmaa.othtech.common.dimensions.biome.BiomeGenBaseRoss123b;
import com.newmaa.othtech.common.dimensions.structure.ross123b.RossCity;

import galaxyspace.core.dimension.ChunkProviderSpaceLakes;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta;

public class ChunkProviderRoss123b extends ChunkProviderSpaceLakes {

    /** LCL 海面所在高度, 即最高一格水所在的 y。 */
    public static final int SEA_LEVEL = 63;

    /** 1.7.10 区块方块数组: 16 * 16 * 256, 下标为 (x * 16 + z) * 256 + y。 */
    private static final int CHUNK_VOLUME = 16 * 16 * 256;
    private static final int COLUMN_HEIGHT = 256;

    private final World world;
    private final RossCity.Terrain terrain;

    public ChunkProviderRoss123b(World world, long seed, boolean mapFeaturesEnabled) {
        super(world, seed, mapFeaturesEnabled);
        this.world = world;
        BlockMetaPair water = getWaterBlock();
        BlockMetaPair stone = getStoneBlock();
        BlockMetaPair soil = getDirtBlock();
        BlockMetaPair grass = getGrassBlock();
        this.terrain = new RossCity.Terrain(
            water.getBlock(),
            water.getMetadata(),
            stone.getBlock(),
            stone.getMetadata(),
            soil.getBlock(),
            soil.getMetadata(),
            grass.getBlock(),
            grass.getMetadata(),
            getWaterLevel());
    }

    /**
     * 地形一次性写进方块数组, 再交给 {@link Chunk} 的数组构造器。
     * <p>
     * 旧实现是在 {@code populate} 里用 {@code world.setBlock} 逐格灌水: 每格都要做一次区块查找、
     * 标记更新和光照重算, 而且会把邻区块已经写好的建筑重新灌成液体。走数组后整列只是几次数组写,
     * 不会触发任何世界级通知, 也不会在生成过程中递归加载邻区块(历史上爆过 StackOverflowError)。
     * <p>
     * 地形(陆地/海床/LCL)、道路与桥、临街建筑、地下破损全部由 {@link RossCity} 写进这份数组,
     * 因此不需要 {@code MapGenStructure} 那套 planning/populate 两段式流程, 也不会跨区块写方块。
     * <p>
     * 数组布局与 {@code Chunk(World, Block[], byte[], int, int)} 一致:
     * {@code (x * 16 + z) * 256 + y}。
     */
    @Override
    public Chunk provideChunk(int chunkX, int chunkZ) {
        Block[] blocks = new Block[CHUNK_VOLUME];
        byte[] metadata = new byte[CHUNK_VOLUME];

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int column = (x * 16 + z) * COLUMN_HEIGHT;
                blocks[column] = Blocks.bedrock;
                blocks[column + 1] = Blocks.bedrock;
            }
        }

        RossCity.stamp(blocks, metadata, chunkX, chunkZ, this.world.getSeed(), this.terrain);

        Chunk chunk = new Chunk(this.world, blocks, metadata, chunkX, chunkZ);

        // EndlessIDs 环境下调用原版 getBiomeArray() 会直接崩, 必须走短数组接口
        if (chunk instanceof ChunkBiomeHook hook) {
            short biomeId = (short) BiomeGenBaseRoss123b.ross123b.biomeID;
            short[] biomes = hook.getBiomeShortArray();
            if (biomes == null || biomes.length != 256) {
                biomes = new short[256];
                hook.setBiomeShortArray(biomes);
            }
            Arrays.fill(biomes, biomeId);
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
        // 道路 / 建筑 / 破损已经在 provideChunk 的方块数组里一次成型, 这里不再需要后处理。
        // 仍然保留空实现: 不能走基类的 populate, 那个实现会用到为 null 的 getBiomeGenerator()
        // 和空的刷怪表。
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
        // 城市不再走 MapGenStructure, 没有需要"重建"的结构起点。
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
        return SEA_LEVEL;
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
        return new BlockMetaPair(RossFluids.BlockLCL, (byte) 0);
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
