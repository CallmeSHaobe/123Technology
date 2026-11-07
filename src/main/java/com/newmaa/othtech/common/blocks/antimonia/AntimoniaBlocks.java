package com.newmaa.othtech.common.blocks.antimonia;

import cpw.mods.fml.common.registry.GameRegistry;

public class AntimoniaBlocks {

    public static BlockAntimoniaGrass antimoniaBlockGrass;
    public static BlockAntimoniaDirt antimoniaBlockDirt;
    public static BlockAntimoniaStone antimoniaBlockStone;
    public static BlockAntimoniaSand antimoniaBlockSand;

    public AntimoniaBlocks() {}

    public static void initialize() {
        antimoniaBlockGrass = new BlockAntimoniaGrass();
        antimoniaBlockDirt = new BlockAntimoniaDirt();
        antimoniaBlockStone = new BlockAntimoniaStone();
        antimoniaBlockSand = new BlockAntimoniaSand();
        GameRegistry.registerBlock(antimoniaBlockGrass, "antimoniaGrassBlock");
        GameRegistry.registerBlock(antimoniaBlockDirt, "antimoniaDirtBlock");
        GameRegistry.registerBlock(antimoniaBlockStone, "antimoniaStoneBlock");
        GameRegistry.registerBlock(antimoniaBlockSand, "antimoniaSandBlock");
        oreDictRegistration();
    }

    public static void oreDictRegistration() {}
}
