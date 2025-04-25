package com.newmaa.othtech.common.dimensions;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenNetherBridge;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;

import com.google.common.collect.Lists;
import com.newmaa.othtech.common.blocks.antimonia.antimoniaBlocks;
import com.newmaa.othtech.common.blocks.fluids.antimoniaFluids;
import com.newmaa.othtech.common.dimensions.biome.biomeDecOreAntimonia;
import com.newmaa.othtech.common.dimensions.biome.biomeGenBaseAntimonia;

import bartworks.system.worldgen.MapGenRuins;
import galaxyspace.core.dimension.ChunkProviderSpaceLakes;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta;
import micdoodle8.mods.galacticraft.core.world.gen.MapGenVillageMoon;
import twilightforest.world.MapGenTFHollowTree;
import twilightforest.world.MapGenTFMajorFeature;

public class chunkProviderAntimonia extends ChunkProviderSpaceLakes {

    private BiomeGenBase[] biomesForGeneration = this.getBiomesForGeneration();

    protected List<MapGenBaseMeta> getWorldGenerators() {
        return Lists.newArrayList();
    }

    public chunkProviderAntimonia(World par1World, long seed, boolean mapFeaturesEnabled) {
        super(par1World, seed, mapFeaturesEnabled);
    }

    protected BiomeDecoratorSpace getBiomeGenerator() {
        return new biomeDecOreAntimonia();
    }

    protected BiomeGenBase[] getBiomesForGeneration() {
        return new BiomeGenBase[] { biomeGenBaseAntimonia.antimonia };
    }

    public void onChunkProvider(int i, int i1, Block[] blocks, byte[] metadata) {

    }

    protected BiomeGenBase.SpawnListEntry[] getCreatures() {
        return new BiomeGenBase.SpawnListEntry[0];
    }

    public double getHeightModifier() {
        return 40;
    }

    public int getWaterLevel() {
        return 40;
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
    private final MapGenVillage MapGenVillage = new MapGenVillage();
    private final MapGenVillageMoon villageGeneratorA = new MapGenVillageMoon();
    private final MapGenNetherBridge MapGenNetherBridge = new MapGenNetherBridge();
    private final MapGenTFHollowTree MapGenTFHollowTree = new MapGenTFHollowTree();
    private final MapGenTFMajorFeature MapGenTFMajorFeature = new MapGenTFMajorFeature();
    private final MapGenRuins.RuinsBase ruinsBase = new MapGenRuins.RuinsBase();
    private final List structureGenerators = new ArrayList();

    @Override
    public void onPopulate(IChunkProvider arg0, int arg1, int arg2) {
        BlockFalling.fallInstantly = true;
        boolean flag = false;
        this.rand.setSeed(this.worldObj.getSeed());
        final long var7 = this.rand.nextLong() / 2L * 2L + 1L;
        final long var9 = this.rand.nextLong() / 2L * 2L + 1L;
        this.rand.setSeed(arg1 * var7 + arg2 * var9 ^ this.worldObj.getSeed());
        MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(arg0, worldObj, rand, arg1, arg2, flag));
        this.MapGenMineshaft.generateStructuresInChunk(this.worldObj, this.rand, arg1, arg2);
        this.MapGenVillage.generateStructuresInChunk(this.worldObj, this.rand, arg1, arg2);
        this.MapGenNetherBridge.generateStructuresInChunk(this.worldObj, this.rand, arg1, arg2);
        this.MapGenTFHollowTree.generateStructuresInChunk(this.worldObj, this.rand, arg1, arg2);
        this.MapGenTFMajorFeature.generateStructuresInChunk(this.worldObj, this.rand, arg1, arg2);
        flag = this.villageGeneratorA.generateStructuresInChunk(this.worldObj, this.rand, arg1, arg2);
        MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Post(arg0, worldObj, rand, arg1, arg2, flag));
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
        this.MapGenVillage.func_151539_a(this, this.worldObj, par1, par2, null);
        this.MapGenNetherBridge.func_151539_a(this, this.worldObj, par1, par2, null);
        this.MapGenTFHollowTree.func_151539_a(this, this.worldObj, par1, par2, null);
        this.MapGenTFMajorFeature.func_151539_a(this, this.worldObj, par1, par2, null);
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
