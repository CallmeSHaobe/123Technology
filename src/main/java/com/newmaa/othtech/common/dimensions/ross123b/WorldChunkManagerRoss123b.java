package com.newmaa.othtech.common.dimensions.ross123b;

import net.minecraft.world.biome.BiomeGenBase;

import com.newmaa.othtech.common.dimensions.biome.BiomeGenBaseRoss123b;

import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldChunkManagerSpace;

public class WorldChunkManagerRoss123b extends WorldChunkManagerSpace {

    @Override
    public BiomeGenBase getBiome() {
        return BiomeGenBaseRoss123b.ross123b;
    }

}
