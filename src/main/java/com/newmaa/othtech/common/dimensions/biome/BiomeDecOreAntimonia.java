package com.newmaa.othtech.common.dimensions.biome;

import static com.newmaa.othtech.common.blocks.antimonia.AntimoniaBlocks.antimoniaBlockStone;
import static gregtech.api.GregTechAPI.*;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;

import bartworks.API.BorosilicateGlass;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;

public class BiomeDecOreAntimonia extends BiomeDecoratorSpace {

    private World world;
    private final WorldGenerator blockAntimony;
    private final WorldGenerator blockNiobium;
    private final WorldGenerator oreSalfur;
    private final WorldGenerator glassBoron;

    public BiomeDecOreAntimonia() {
        this.blockAntimony = new WorldGenMinable(sBlockMetal1, 4, 50, antimoniaBlockStone);
        this.blockNiobium = new WorldGenMinable(sBlockMetal5, 5, 40, antimoniaBlockStone);
        this.oreSalfur = new WorldGenMinable(sBlockReinforced, 5, 20, antimoniaBlockStone);
        this.glassBoron = new WorldGenMinable(BorosilicateGlass.getGlassBlock(), 15, antimoniaBlockStone);
    }

    protected void decorate() {
        this.generateOre(5, this.blockAntimony, 1, 100);
        this.generateOre(10, this.blockNiobium, 1, 60);
        this.generateOre(12, this.oreSalfur, 1, 20);
        this.generateOre(10, this.glassBoron, 1, 90);
    }

    protected void setCurrentWorld(World world) {
        this.world = world;
    }

    protected World getCurrentWorld() {
        return this.world;
    }
}
