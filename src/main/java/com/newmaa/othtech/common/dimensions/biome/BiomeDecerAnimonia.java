package com.newmaa.othtech.common.dimensions.biome;

import java.util.Random;

import net.minecraft.world.World;

public class BiomeDecerAnimonia {

    private World worldObj;
    private Random randomGenerator;

    private int chunkX;
    private int chunkZ;

    public void decorate(World worldObj, Random rand, int chunkX, int chunkZ) {
        if (this.worldObj != null) {
            throw new RuntimeException("Already decorating!!");
        }
        this.worldObj = worldObj;
        this.randomGenerator = rand;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.worldObj = null;
        this.randomGenerator = null;
    }
}
