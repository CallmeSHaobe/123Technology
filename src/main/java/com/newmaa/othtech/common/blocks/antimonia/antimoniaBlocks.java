package com.newmaa.othtech.common.blocks.antimonia;

import cpw.mods.fml.common.registry.GameRegistry;

public class antimoniaBlocks {

    public static blockAntimorass antimoniaBlockGrass;
    public static blockAntirt antimoniaBlockDirt;
    public static blockAntistone antimoniaBlockStone;

    public antimoniaBlocks() {}

    public static void initialize() {
        antimoniaBlockGrass = new blockAntimorass();
        antimoniaBlockDirt = new blockAntirt();
        antimoniaBlockStone = new blockAntistone();
        GameRegistry.registerBlock(antimoniaBlockGrass, "antimoniaGrassBlock");
        GameRegistry.registerBlock(antimoniaBlockDirt, "antimoniaDirtBlock");
        GameRegistry.registerBlock(antimoniaBlockStone, "antimoniaStoneBlock");
        oreDictRegistration();
    }

    public static void oreDictRegistration() {}
}
