package com.newmaa.othtech.common.dimensions.antimonia;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.MapGenCaves;
import net.minecraft.world.gen.MapGenRavine;
import net.minecraft.world.gen.structure.MapGenMineshaft;

import com.google.common.collect.Lists;
import com.newmaa.othtech.common.blocks.antimonia.AntimoniaBlocks;
import com.newmaa.othtech.common.blocks.fluids.AntimoniaFluids;
import com.newmaa.othtech.common.dimensions.antimonia.farlands.AntimoniaFarLands;
import com.newmaa.othtech.common.dimensions.biome.BiomeDecOreAntimonia;
import com.newmaa.othtech.common.dimensions.biome.BiomeGenBaseAntimonia;
import com.newmaa.othtech.common.dimensions.structure.antimonia.MapGenCityAntimonia;
import com.newmaa.othtech.common.dimensions.structure.antimonia.graveyard.MapGenGraveyard;

import galaxyspace.core.dimension.ChunkProviderSpaceLakes;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta;

public class ChunkProviderAntimonia extends ChunkProviderSpaceLakes {

    private final World worldObj;
    private final Random rand;
    private final boolean mapFeaturesEnabled;

    public ChunkProviderAntimonia(World world, long seed, boolean mapFeaturesEnabled) {
        super(world, seed, mapFeaturesEnabled);
        this.worldObj = world;
        this.rand = new Random(seed);
        this.mapFeaturesEnabled = mapFeaturesEnabled;
    }

    /**
     * 主世界版的洞穴生成器。
     * <p>
     * 不能再用 {@code MapGenCavesHell}：1.7.10 的地狱洞穴是按 128 格高的数组写死的（索引
     * {@code (x * 16 + z) * 128 + y}，高度上限写死 120，而且只替换地狱岩 / 泥土 / 草方块）。 直接套在 256 格高的锑星区块上，
     * 一来索引错位（挖出来的洞会落在完全无关的方块上），二来石头根本挖不动， 于是「洞穴」退化成地表泥土被随机啃掉几口。
     */
    private final MapGenCaves caveGenerator = new MapGenCaves();

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

    /**
     * 圈定结构起点（StructureStart）。
     * <p>
     * 原版在自己的 {@code provideChunk} 里会调用 mineshaft / village / stronghold 等结构生成器的
     * {@code func_151539_a}：它会按 17x17 区块的范围扫描，为命中位置创建 StructureStart 并写进
     * {@code structureMap}；之后 {@code populate} 阶段的 {@code generateStructuresInChunk} 才拿得到起点、
     * 把已经圈定的结构往区块里摆（该方法本身不生成起点，注释里写得很清楚：
     * "Does *not* generate StructureStarts"）。
     * <p>
     * GS 的 {@code ChunkProviderSpaceLakes.provideChunk} 完全没有这一步，原先只在
     * {@link #recreateStructures} 里补，而 {@code recreateStructures} 只有「区块从存档载入」时才被
     * {@code ChunkProviderServer#safeLoadChunk} 调用 —— 于是新生成的区块永远没有起点，
     * 锑星城 / 坟场 / 废弃矿井在新鲜世界里根本不会出现。这里补齐原版流程。
     */
    @Override
    public Chunk provideChunk(int chunkX, int chunkZ) {
        Chunk chunk = super.provideChunk(chunkX, chunkZ);

        if (this.mapFeaturesEnabled) {
            this.MapGenMineshaft.func_151539_a(this, this.worldObj, chunkX, chunkZ, null);
            this.villageGeneratorA.func_151539_a(this, this.worldObj, chunkX, chunkZ, null);
            this.graveyardGenerator.func_151539_a(this, this.worldObj, chunkX, chunkZ, null);
        }

        return chunk;
    }

    /**
     * 锑星的边境之地（Far Lands）。
     * <p>
     * 先让原版流程生成一遍，再把越过 ±farLandsBoundary 的那些列整列覆盖掉：
     * 越界列的地形由 {@link AntimoniaFarLands} 用「噪声坐标溢出」的方式复现，
     * 接缝落在方块坐标 12,550,820 上，而不是整区块一刀切。
     */
    @Override
    public void generateTerrain(int chunkX, int chunkZ, Block[] blocks, byte[] metadata) {
        super.generateTerrain(chunkX, chunkZ, blocks, metadata);

        if (!AntimoniaFarLands.enabled()) {
            return;
        }

        int boundary = AntimoniaFarLands.boundary();
        int minX = chunkX * 16;
        int minZ = chunkZ * 16;
        // 整块都在边界以内，直接跳过（正常地形保持原样）
        if (Math.abs((long) minX) < boundary && Math.abs((long) minX + 15) < boundary
            && Math.abs((long) minZ) < boundary
            && Math.abs((long) minZ + 15) < boundary) {
            return;
        }

        Block stone = this.getStoneBlock()
            .getBlock();
        byte stoneMeta = this.getStoneBlock()
            .getMetadata();
        Block water = this.getWaterBlock()
            .getBlock();
        byte waterMeta = this.getWaterBlock()
            .getMetadata();
        int waterLevel = this.getWaterLevel();
        boolean waterEnabled = this.canGenerateWaterBlock();
        long seed = this.worldObj.getSeed();

        for (int x = 0; x < 16; x++) {
            int worldX = minX + x;
            for (int z = 0; z < 16; z++) {
                int worldZ = minZ + z;
                if (!AntimoniaFarLands.isFar(worldX, worldZ, boundary)) {
                    continue;
                }

                int height = AntimoniaFarLands.heightAt(worldX, worldZ, seed, boundary);
                int column = (x * 16 + z) * 256;
                for (int y = 0; y < 256; y++) {
                    int index = column + y;
                    if (y < height) {
                        blocks[index] = stone;
                        metadata[index] = stoneMeta;
                    } else if (waterEnabled && y < waterLevel) {
                        blocks[index] = water;
                        metadata[index] = waterMeta;
                    } else {
                        blocks[index] = null;
                        metadata[index] = 0;
                    }
                }
            }
        }
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

    private final MapGenMineshaft MapGenMineshaft = new MapGenMineshaft();
    private final MapGenCityAntimonia villageGeneratorA = new MapGenCityAntimonia();
    private final MapGenGraveyard graveyardGenerator = new MapGenGraveyard();

    @Override
    public void onPopulate(IChunkProvider arg0, int arg1, int arg2) {
        BlockFalling.fallInstantly = true;
        this.rand.setSeed(this.worldObj.getSeed());
        final long var7 = this.rand.nextLong() / 2L * 2L + 1L;
        final long var9 = this.rand.nextLong() / 2L * 2L + 1L;
        this.rand.setSeed(arg1 * var7 + arg2 * var9 ^ this.worldObj.getSeed());
        this.MapGenMineshaft.generateStructuresInChunk(this.worldObj, this.rand, arg1, arg2);
        this.villageGeneratorA.generateStructuresInChunk(this.worldObj, this.rand, arg1, arg2);
        this.graveyardGenerator.generateStructuresInChunk(this.worldObj, this.rand, arg1, arg2);
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
        this.graveyardGenerator.func_151539_a(this, this.worldObj, par1, par2, null);
    }

    /**
     * 原版实现（{@code ChunkProviderGenerate}）恒为 true，这里跟着返回 true。
     * <p>
     * 返回 false 会让任何经 {@code IChunkProvider#chunkExists} 查询本维度的代码以为「这块地从来没有区块」，
     * 属于纯粹的谎报；真正的「区块是否已加载」由 {@code ChunkProviderServer} 自己判断。
     */
    @Override
    public boolean chunkExists(int x, int z) {
        return true;
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
        return new BlockMetaPair(Blocks.stone, (byte) 0);
    }

    protected boolean enableBiomeGenBaseBlock() {
        return false;
    }

    public String makeString() {
        return "AntimoniaLevelSource";
    }
}
