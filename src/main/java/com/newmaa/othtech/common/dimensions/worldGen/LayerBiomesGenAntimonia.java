package com.newmaa.othtech.common.dimensions.worldGen;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import com.newmaa.othtech.common.dimensions.biome.biomeGenBaseAntimonia;

public class LayerBiomesGenAntimonia extends GenLayer {

    protected BiomeGenBase[] commonBiomes;
    protected BiomeGenBase[] rareBiomes;

    public LayerBiomesGenAntimonia(long seed, GenLayer genlayer) {
        super(seed);
        this.commonBiomes = new BiomeGenBase[] { biomeGenBaseAntimonia.antimonia };
        this.rareBiomes = new BiomeGenBase[] { biomeGenBaseAntimonia.antimonia };
        this.parent = genlayer;
    }

    public LayerBiomesGenAntimonia(long seed) {
        super(seed);
        this.commonBiomes = new BiomeGenBase[] { biomeGenBaseAntimonia.antimonia };
        this.rareBiomes = new BiomeGenBase[] { biomeGenBaseAntimonia.antimonia };
    }

    public int[] getInts(int x, int z, int width, int depth) {
        int[] dest = IntCache.getIntCache(width * depth);

        for (int dz = 0; dz < depth; ++dz) {
            for (int dx = 0; dx < width; ++dx) {
                this.initChunkSeed((long) (dx + x), (long) (dz + z));
                if (this.nextInt(15) == 0) {
                    dest[dx + dz * width] = this.rareBiomes[this.nextInt(this.rareBiomes.length)].biomeID;
                } else {
                    dest[dx + dz * width] = this.commonBiomes[this.nextInt(this.commonBiomes.length)].biomeID;
                }
            }
        }

        return dest;
    }
}
