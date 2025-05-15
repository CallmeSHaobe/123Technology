package com.newmaa.othtech.common.blocks.antimonia;

import cpw.mods.fml.common.registry.GameRegistry;

public class AntimoniaBlocks {

    public static BlockAntimoniaGrass antimoniaBlockGrass;
    public static BlockAntimoniaDirt antimoniaBlockDirt;
    public static BlockAntimoniaStone antimoniaBlockStone;

    public AntimoniaBlocks() {}

    public static void initialize() {
        antimoniaBlockGrass = new BlockAntimoniaGrass();
        antimoniaBlockDirt = new BlockAntimoniaDirt();
        antimoniaBlockStone = new BlockAntimoniaStone();
        GameRegistry.registerBlock(antimoniaBlockGrass, "antimoniaGrassBlock");
        GameRegistry.registerBlock(antimoniaBlockDirt, "antimoniaDirtBlock");
        GameRegistry.registerBlock(antimoniaBlockStone, "antimoniaStoneBlock");
        oreDictRegistration();
    }

    public static void oreDictRegistration() {}
}
