package com.newmaa.othtech.common.dimensions.biome;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;

import com.newmaa.othtech.common.blocks.antimonia.AntimoniaBlocks;

/** Ross123b biomes share the same weather and blocks; their boundaries follow the generated terrain. */
public class BiomeGenBaseRoss123b extends BiomeGenBaseAntimonia {

    public static final BiomeGenBase ross123b = new BiomeGenBaseRoss123b(
        124,
        "Ross123b Ocean",
        -0.6F,
        0.1F,
        BiomeDictionary.Type.OCEAN);
    public static final BiomeGenBase beach = new BiomeGenBaseRoss123b(
        240,
        "Ross123b Antimony Beach",
        0.0F,
        0.05F,
        BiomeDictionary.Type.BEACH);
    public static final BiomeGenBase river = new BiomeGenBaseRoss123b(
        241,
        "Ross123b LCL River",
        -0.3F,
        0.05F,
        BiomeDictionary.Type.RIVER);
    public static final BiomeGenBase land = new BiomeGenBaseRoss123b(
        242,
        "Ross123b Wasteland",
        0.1F,
        0.2F,
        BiomeDictionary.Type.PLAINS);
    public static final BiomeGenBase hills = new BiomeGenBaseRoss123b(
        243,
        "Ross123b Hills",
        0.6F,
        0.5F,
        BiomeDictionary.Type.HILLS);
    public static final BiomeGenBase mountains = new BiomeGenBaseRoss123b(
        244,
        "Ross123b Mountains",
        1.5F,
        1.0F,
        BiomeDictionary.Type.MOUNTAIN);

    private BiomeGenBaseRoss123b(int id, String name, float baseHeight, float variation,
        BiomeDictionary.Type terrainType) {
        super(id);
        this.setBiomeName(name);
        this.setHeight(new BiomeGenBase.Height(baseHeight, variation));
        this.topBlock = terrainType == BiomeDictionary.Type.BEACH ? AntimoniaBlocks.antimoniaBlockSand
            : AntimoniaBlocks.antimoniaBlockGrass;
        this.fillerBlock = terrainType == BiomeDictionary.Type.BEACH ? AntimoniaBlocks.antimoniaBlockSand
            : AntimoniaBlocks.antimoniaBlockDirt;
        this.stoneBlock = AntimoniaBlocks.antimoniaBlockStone;
        this.spawnableCaveCreatureList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableMonsterList.clear();
        this.spawnableWaterCreatureList.clear();
        this.enableRain = true;
        this.enableSnow = false;
        this.temperature = 0.8F;
        BiomeDictionary.registerBiomeType(this, terrainType, BiomeDictionary.Type.DEAD, BiomeDictionary.Type.SPOOKY);
    }

    public static BiomeGenBase[] all() {
        return new BiomeGenBase[] { ross123b, beach, river, land, hills, mountains };
    }
}
