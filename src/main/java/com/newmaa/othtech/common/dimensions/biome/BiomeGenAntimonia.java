package com.newmaa.othtech.common.dimensions.biome;

import net.minecraftforge.common.BiomeDictionary;

import com.newmaa.othtech.common.blocks.antimonia.AntimoniaBlocks;

public class BiomeGenAntimonia extends BiomeGenBaseAntimonia {

    public BiomeGenAntimonia(int var1) {
        super(var1);
        this.enableRain = false;
        this.enableSnow = true;
        this.topBlock = AntimoniaBlocks.antimoniaBlockGrass;
        this.topMeta = 0;
        this.fillerBlock = AntimoniaBlocks.antimoniaBlockDirt;
        this.fillerMeta = 0;
        this.stoneBlock = AntimoniaBlocks.antimoniaBlockStone;
        this.stoneMeta = 0;
        this.spawnableCaveCreatureList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableMonsterList.clear();
        this.spawnableWaterCreatureList.clear();
        this.temperature = 116F;
        BiomeDictionary.registerBiomeType(
            this,
            BiomeDictionary.Type.COLD,
            BiomeDictionary.Type.DRY,
            BiomeDictionary.Type.DEAD,
            BiomeDictionary.Type.SPOOKY);
    }
}
