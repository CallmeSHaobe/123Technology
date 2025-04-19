package com.newmaa.othtech.common.dimensions.biome;

import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;

import galaxyspace.core.world.GSBiomeGenBase;

public class biomeGenBaseAntimonia extends GSBiomeGenBase {

    public static BiomeGenBase antimonia;

    public biomeGenBaseAntimonia(int id) {
        super(id);
        this.enableRain = true;
        this.enableSnow = true;
        this.topBlock = Blocks.grass;
        this.fillerBlock = Blocks.dirt;
    }

    public BiomeDecorator createBiomeDecorator() {
        return new biomeDecAntimonia();
    }

    protected biomeDecAntimonia getBiomeDecorator() {
        return (biomeDecAntimonia) this.theBiomeDecorator;
    }

    static {
        antimonia = (new biomeGenAntimonia(123)).setBiomeName("Antimonia")
            .setHeight(new BiomeGenBase.Height(0.1F, 0.2F));
    }
}
