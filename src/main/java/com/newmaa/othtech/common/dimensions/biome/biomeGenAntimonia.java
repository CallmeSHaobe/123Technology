package com.newmaa.othtech.common.dimensions.biome;

import net.minecraftforge.common.BiomeDictionary;

import com.newmaa.othtech.common.blocks.antimonia.antimoniaBlocks;

public class biomeGenAntimonia extends biomeGenBaseAntimonia {

    public biomeGenAntimonia(int var1) {
        super(var1);
        this.enableRain = false;
        this.enableSnow = true;
        this.topBlock = antimoniaBlocks.antimoniaBlockGrass;
        this.topMeta = 0;
        this.fillerBlock = antimoniaBlocks.antimoniaBlockDirt;
        this.fillerMeta = 0;
        this.stoneBlock = antimoniaBlocks.antimoniaBlockStone;
        this.stoneMeta = 0;
        this.spawnableCaveCreatureList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableMonsterList.clear();
        this.spawnableWaterCreatureList.clear();
        this.temperature = 11600F;
        BiomeDictionary.registerBiomeType(
            this,
            new BiomeDictionary.Type[] { BiomeDictionary.Type.COLD, BiomeDictionary.Type.DRY, BiomeDictionary.Type.DEAD,
                BiomeDictionary.Type.SPOOKY });
    }
}
