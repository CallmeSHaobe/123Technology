package com.newmaa.othtech.common.entity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.WeakHashMap;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.common.tileentities.machines.multi.drone.MTEHatchDroneDownLink;

/** Loaded, same-world drone beacons, independent of their connection to a drone centre. */
public final class DroneDownlinkTargets {

    public static final int SEARCH_RANGE = 4096;
    public static final int MIN_RANGE = 16;

    private static final long CACHE_TICKS = 100L;
    private static final int WORLD_BORDER = 30000000;
    // Values contain coordinates only, so they cannot strongly retain their weak World keys.
    private static final WeakHashMap<World, Cache> CACHES = new WeakHashMap<>();

    private DroneDownlinkTargets() {}

    /**
     * Call on the server thread. Distances are measured to block centres in three dimensions;
     * the allowed interval is (MIN_RANGE, SEARCH_RANGE]. Equal distances use ascending x, y, z.
     * Newly loaded beacons may take 100 ticks to appear, but stale entries are checked on every call.
     * Returns a fresh coordinate value, or null when no loaded, valid beacon is in range.
     */
    public static ChunkCoordinates findNearest(World world, double x, double y, double z) {
        if (world == null || world.isRemote || !finite(x) || !finite(y) || !finite(z)) return null;

        long tick = world.getTotalWorldTime();
        Cache cache = CACHES.get(world);
        if (cache == null || tick < cache.tick || tick - cache.tick >= CACHE_TICKS) {
            List<ChunkCoordinates> coordinates = new ArrayList<>();
            for (Object entry : world.loadedTileEntityList) {
                if (!(entry instanceof TileEntity)) continue;
                TileEntity tile = (TileEntity) entry;
                if (isDownlink(world, tile) && validCoordinates(world, tile.xCoord, tile.yCoord, tile.zCoord)) {
                    coordinates.add(new ChunkCoordinates(tile.xCoord, tile.yCoord, tile.zCoord));
                }
            }
            cache = new Cache(tick, coordinates);
            CACHES.put(world, cache);
        }

        List<ChunkCoordinates> candidates = new ArrayList<>();
        for (ChunkCoordinates position : cache.coordinates) {
            double distance = distanceSquared(position, x, y, z);
            if (distance > (double) MIN_RANGE * MIN_RANGE && distance <= (double) SEARCH_RANGE * SEARCH_RANGE) {
                candidates.add(position);
            }
        }
        candidates.sort(
            Comparator.comparingDouble((ChunkCoordinates position) -> distanceSquared(position, x, y, z))
                .thenComparingInt(position -> position.posX)
                .thenComparingInt(position -> position.posY)
                .thenComparingInt(position -> position.posZ));

        for (ChunkCoordinates position : candidates) {
            if (!validCoordinates(world, position.posX, position.posY, position.posZ)
                || !world.blockExists(position.posX, position.posY, position.posZ)) continue;
            // blockExists must precede getTileEntity: never load/generate chunks to resolve a beacon.
            TileEntity tile = world.getTileEntity(position.posX, position.posY, position.posZ);
            if (isDownlink(world, tile) && tile.xCoord == position.posX
                && tile.yCoord == position.posY
                && tile.zCoord == position.posZ) {
                return new ChunkCoordinates(position.posX, position.posY, position.posZ);
            }
        }
        return null;
    }

    private static boolean isDownlink(World world, TileEntity tile) {
        return tile != null && !tile.isInvalid()
            && tile.getWorldObj() == world
            && tile instanceof IGregTechTileEntity
            && ((IGregTechTileEntity) tile).getMetaTileEntity() instanceof MTEHatchDroneDownLink;
    }

    private static boolean validCoordinates(World world, int x, int y, int z) {
        return y >= 0 && y < world.getHeight()
            && x >= -WORLD_BORDER
            && x < WORLD_BORDER
            && z >= -WORLD_BORDER
            && z < WORLD_BORDER;
    }

    private static double distanceSquared(ChunkCoordinates position, double x, double y, double z) {
        double dx = position.posX + 0.5D - x;
        double dy = position.posY + 0.5D - y;
        double dz = position.posZ + 0.5D - z;
        return dx * dx + dy * dy + dz * dz;
    }

    private static boolean finite(double value) {
        return !Double.isNaN(value) && !Double.isInfinite(value);
    }

    private static final class Cache {

        private final long tick;
        private final List<ChunkCoordinates> coordinates;

        private Cache(long tick, List<ChunkCoordinates> coordinates) {
            this.tick = tick;
            this.coordinates = coordinates;
        }
    }
}
