package com.newmaa.othtech.common.dimensions.biome;

import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;

import galaxyspace.core.world.GSBiomeGenBase;

public class BiomeGenBaseAntimonia extends GSBiomeGenBase {

    public static BiomeGenBase antimonia;

    public BiomeGenBaseAntimonia(int id) {
        super(id);
        this.enableRain = true;
        this.enableSnow = true;
        this.topBlock = Blocks.grass;
        this.fillerBlock = Blocks.dirt;
    }

    static {
        antimonia = (new BiomeGenAntimonia(123)).setBiomeName("Antimonia")
            .setHeight(new BiomeGenBase.Height(0.1F, 0.2F));
    }
}
