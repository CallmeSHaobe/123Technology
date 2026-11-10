package com.newmaa.othtech.common.dimensions.ross123b;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
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
import com.newmaa.othtech.common.dimensions.structure.ross123b.NervHeadquarters;
import com.newmaa.othtech.common.dimensions.structure.ross123b.RossCity;
import com.newmaa.othtech.common.dimensions.structure.ross123b.RossCityLoot;

import galaxyspace.core.dimension.ChunkProviderSpaceLakes;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.ItemList;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta;

public class ChunkProviderRoss123b extends ChunkProviderSpaceLakes {

    /** LCL 海面所在高度, 即最高一格水所在的 y。 */
    public static final int SEA_LEVEL = 63;

    /** 1.7.10 区块方块数组: 16 * 16 * 256, 下标为 (x * 16 + z) * 256 + y。 */
    private static final int CHUNK_VOLUME = 16 * 16 * 256;
    private static final int COLUMN_HEIGHT = 256;

    private static final class MachinePlacement {

        final int x;
        final int z;
        final short id;
        final TileEntity tile;

        MachinePlacement(int x, int z, short id, TileEntity tile) {
            this.x = x;
            this.z = z;
            this.id = id;
            this.tile = tile;
        }
    }

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

        List<MachinePlacement> machines = new ArrayList<>();
        if (!this.world.isRemote && NervHeadquarters.intersects(chunkX, chunkZ) && GregTechAPI.sBlockMachines != null) {
            ItemList[] choices = { ItemList.Machine_LV_Macerator, ItemList.Machine_EV_Compressor,
                ItemList.Machine_IV_E_Furnace, ItemList.Machine_IV_Assembler, ItemList.Machine_HV_Lathe,
                ItemList.Machine_EV_Extractor, ItemList.Machine_MV_Cutter, ItemList.Machine_HV_Wiremill };
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int worldX = (chunkX << 4) + x;
                    int worldZ = (chunkZ << 4) + z;
                    int variant = NervHeadquarters.machineVariant(this.world.getSeed(), worldX, worldZ);
                    int index = (x * 16 + z) * COLUMN_HEIGHT + 65;
                    if (variant < 0 || blocks[index] != null || blocks[index - 1] == null || blocks[index + 1] != null)
                        continue;
                    ItemList choice = choices[variant];
                    if (!choice.hasBeenSet()) continue;
                    ItemStack stack = choice.get(1);
                    if (stack == null || Item.getItemFromBlock(GregTechAPI.sBlockMachines) != stack.getItem()) continue;
                    int id = stack.getItemDamage();
                    if (id <= 0 || id > Short.MAX_VALUE
                        || id >= GregTechAPI.METATILEENTITIES.length
                        || GregTechAPI.METATILEENTITIES[id] == null) continue;
                    IMetaTileEntity prototype = GregTechAPI.METATILEENTITIES[id];
                    int baseMeta = prototype.getTileEntityBaseType();
                    if (baseMeta < 0 || baseMeta > 15) continue;
                    TileEntity tile = GregTechAPI.createTileEntity(baseMeta);
                    if (!(tile instanceof IGregTechTileEntity)) continue;
                    blocks[index] = GregTechAPI.sBlockMachines;
                    metadata[index] = (byte) baseMeta;
                    machines.add(new MachinePlacement(x, z, (short) id, tile));
                }
            }
        }

        Chunk chunk = new Chunk(this.world, blocks, metadata, chunkX, chunkZ);
        for (MachinePlacement machine : machines) {
            chunk.func_150812_a(machine.x, 65, machine.z, machine.tile);
            IGregTechTileEntity gtTile = (IGregTechTileEntity) machine.tile;
            gtTile.setInitialValuesAsNBT(null, machine.id);
            if (gtTile.getMetaTileEntity() == null) {
                throw new IllegalStateException("Uninitialized NERV GT machine ID " + machine.id);
            }
            // World generation has no placing player. GT's initDefaultModes(null) reads
            // player preferences and crashes on a null owner; retain machine defaults.
            chunk.setChunkModified();
        }
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = SEA_LEVEL + 1; y <= 160; y++) {
                    if (blocks[(x * 16 + z) * COLUMN_HEIGHT + y] != Blocks.chest) continue;
                    TileEntityChest chest = new TileEntityChest();
                    RossCityLoot.fill(chest, this.world.getSeed(), (chunkX << 4) + x, (chunkZ << 4) + z);
                    chunk.func_150812_a(x, y, z, chest);
                    chunk.setChunkModified();
                }
            }
        }

        // EndlessIDs 环境下调用原版 getBiomeArray() 会直接崩, 必须走短数组接口
        if (chunk instanceof ChunkBiomeHook hook) {
            short[] biomes = hook.getBiomeShortArray();
            if (biomes == null || biomes.length != 256) {
                biomes = new short[256];
                hook.setBiomeShortArray(biomes);
            }
            BiomeGenBase[] kinds = BiomeGenBaseRoss123b.all();
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int worldX = (chunkX << 4) + x;
                    int worldZ = (chunkZ << 4) + z;
                    biomes[(z << 4) | x] = (short) kinds[RossCity
                        .biomeKind(this.world.getSeed(), SEA_LEVEL, worldX, worldZ)].biomeID;
                }
            }
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
    public void populate(IChunkProvider provider, int chunkX, int chunkZ) {}

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
        return BiomeGenBaseRoss123b.all();
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
