package com.newmaa.othtech.common.dimensions.ross123b;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkEvent;

import com.falsepattern.endlessids.mixin.helpers.ChunkBiomeHook;
import com.newmaa.othtech.common.dimensions.biome.BiomeGenBaseRoss123b;
import com.newmaa.othtech.common.dimensions.structure.ross123b.RossCity;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/** Correct biome IDs written before Ross123b's IDs were separated from GalaxySpace. */
public final class Ross123bBiomeMigration {

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        if (event.world.isRemote || !(event.world.provider instanceof WorldProviderRoss123b)) return;
        Chunk chunk = event.getChunk();
        if (!(chunk instanceof ChunkBiomeHook hook)) return;
        short[] ids = hook.getBiomeShortArray();
        if (ids == null || ids.length != 256) return;
        BiomeGenBase[] biomes = BiomeGenBaseRoss123b.all();
        boolean changed = false;
        for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {
                int index = (z << 4) | x;
                int oldId = ids[index] & 0xffff;
                if (oldId < 200 || oldId > 204) continue;
                int worldX = (chunk.xPosition << 4) + x;
                int worldZ = (chunk.zPosition << 4) + z;
                ids[index] = (short) biomes[RossCity
                    .biomeKind(event.world.getSeed(), ChunkProviderRoss123b.SEA_LEVEL, worldX, worldZ)].biomeID;
                changed = true;
            }
        }
        if (changed) chunk.setChunkModified();
    }
}
