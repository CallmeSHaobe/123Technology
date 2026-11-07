package com.newmaa.othtech.common.dimensions.ross123b;

import java.util.List;
import java.util.Random;

import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import com.newmaa.othtech.common.dimensions.biome.BiomeGenBaseRoss123b;
import com.newmaa.othtech.common.dimensions.structure.ross123b.RossCity;

import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldChunkManagerSpace;

public class WorldChunkManagerRoss123b extends WorldChunkManagerSpace {

    private final long seed;
    private final BiomeGenBase[] biomes = BiomeGenBaseRoss123b.all();

    public WorldChunkManagerRoss123b(World world) {
        this.seed = world.getSeed();
    }

    @Override
    public BiomeGenBase getBiome() {
        return BiomeGenBaseRoss123b.ross123b;
    }

    @Override
    public BiomeGenBase getBiomeGenAt(int x, int z) {
        return biomeAt(x, z);
    }

    @Override
    public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] result, int x, int z, int width, int length) {
        return fill(result, x, z, width, length);
    }

    @Override
    public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] result, int x, int z, int width, int length, boolean useCache) {
        return fill(result, x, z, width, length);
    }

    @Override
    public boolean areBiomesViable(int x, int z, int radius, List<BiomeGenBase> allowed) {
        for (int sampleX = x - radius; sampleX <= x + radius; sampleX += 4) {
            for (int sampleZ = z - radius; sampleZ <= z + radius; sampleZ += 4) {
                if (!allowed.contains(biomeAt(sampleX, sampleZ))) return false;
            }
        }
        return true;
    }

    @Override
    public ChunkPosition findBiomePosition(int x, int z, int radius, List<BiomeGenBase> allowed, Random random) {
        ChunkPosition choice = null;
        int count = 0;
        for (int sampleX = x - radius; sampleX <= x + radius; sampleX += 4) {
            for (int sampleZ = z - radius; sampleZ <= z + radius; sampleZ += 4) {
                if (allowed.contains(biomeAt(sampleX, sampleZ)) && random.nextInt(++count) == 0) {
                    choice = new ChunkPosition(sampleX, 0, sampleZ);
                }
            }
        }
        return choice;
    }

    private BiomeGenBase[] fill(BiomeGenBase[] result, int x, int z, int width, int length) {
        if (result == null || result.length < width * length) result = new BiomeGenBase[width * length];
        for (int dz = 0; dz < length; dz++) {
            for (int dx = 0; dx < width; dx++) {
                result[dx + dz * width] = biomeAt(x + dx, z + dz);
            }
        }
        return result;
    }

    private BiomeGenBase biomeAt(int x, int z) {
        return biomes[RossCity.biomeKind(seed, ChunkProviderRoss123b.SEA_LEVEL, x, z)];
    }
}
