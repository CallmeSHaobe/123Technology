package com.newmaa.othtech.common.dimensions;

import net.minecraft.world.biome.BiomeGenBase;

import com.newmaa.othtech.common.dimensions.biome.biomeGenBaseAntimonia;

import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldChunkManagerSpace;

public class worldChunkManagerAntimonia extends WorldChunkManagerSpace {

    protected worldChunkManagerAntimonia() {}

    public BiomeGenBase getBiome() {
        return biomeGenBaseAntimonia.antimonia;
    }

}
