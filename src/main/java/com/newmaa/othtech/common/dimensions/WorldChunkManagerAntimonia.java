package com.newmaa.othtech.common.dimensions;

import net.minecraft.world.biome.BiomeGenBase;

import com.newmaa.othtech.common.dimensions.biome.BiomeGenBaseAntimonia;

import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldChunkManagerSpace;

public class WorldChunkManagerAntimonia extends WorldChunkManagerSpace {

    @Override
    public BiomeGenBase getBiome() {
        return BiomeGenBaseAntimonia.antimonia;
    }

}
