package com.newmaa.othtech.common.dimensions.structure.antimonia.graveyard;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureStart;

public class StructureStartGraveyard extends StructureStart {

    public StructureStartGraveyard() {}

    public StructureStartGraveyard(World world, Random rand, int chunkX, int chunkZ) {
        super(chunkX, chunkZ);

        final int originX = (chunkX << 4) + 8;
        final int originZ = (chunkZ << 4) + 8;
        final Random siteRandom = new Random(world.getSeed() ^ (originX * 341873128712L) ^ (originZ * 132897987541L));
        // 台面落在 64~69：锑星水位是 64，保证坟场不会半截泡在水里
        final int baseY = 64 + siteRandom.nextInt(6);

        this.components.add(new StructureComponentGraveyard(siteRandom, originX, baseY, originZ));
        this.updateBoundingBox();
    }

    @Override
    public boolean isSizeableStructure() {
        return true;
    }
}
