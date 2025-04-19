package com.newmaa.othtech.common.dimensions.worldGen;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerRiver;
import net.minecraft.world.gen.layer.GenLayerVoronoiZoom;
import net.minecraft.world.gen.layer.GenLayerZoom;

public abstract class LayerGenAntimonia extends GenLayer {

    public LayerGenAntimonia(long seed) {
        super(seed);
    }

    public static GenLayer[] makeTheWorld(long seed) {
        GenLayer cbiomes = new LayerBiomesGenAntimonia(1L);
        GenLayer biomes = new GenLayerZoom(1000L, cbiomes);
        biomes = new GenLayerZoom(1001L, biomes);
        biomes = new GenLayerZoom(1002L, biomes);
        biomes = new GenLayerZoom(1003L, biomes);
        biomes = new GenLayerZoom(1004L, biomes);
        biomes = new GenLayerZoom(1005L, biomes);
        GenLayer genlayervoronoizoom = new GenLayerVoronoiZoom(10L, biomes);
        GenLayerRiver genlayerriver = new GenLayerRiver(1000L, biomes);
        genlayervoronoizoom.initWorldGenSeed(seed);
        return new GenLayer[] { biomes, genlayervoronoizoom, genlayerriver };
    }

    public int[] getInts(int x, int z, int width, int depth) {
        return null;
    }
}
