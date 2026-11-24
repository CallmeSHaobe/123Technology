package com.newmaa.othtech.common.dimensions.structure.ross123b;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

/** TV-series command tower, tactical plot and MAGI banks, facing the screen to the south. */
final class TacticalCommandRoom {

    private TacticalCommandRoom() {}

    static void stamp(Block[] blocks, byte[] meta, int column, int x, int z) {
        int ax = Math.abs(x);
        if (ax > 32 || z < -30 || z > 29) return;

        // Double-height central well. The office floor and its columns at |x| or |z| = 18 stay intact.
        if (ax <= 16 && Math.abs(z) <= 16) {
            for (int y = 76; y <= 84; y++) put(blocks, meta, column, y, null, 0);
        }

        // The narrowing tower rises away from the display. Two side stairways join all three decks.
        if (z <= -7 && z >= -28 && ax <= 12) {
            int depth = -7 - z;
            int level = depth < 7 ? 66 : depth < 14 ? 68 : 70;
            if (ax <= 12 - depth / 7) {
                boolean stair = ax >= 6 && ax <= 8;
                int floor = stair ? Math.min(level, 65 + depth / 3) : level;
                for (int y = 65; y <= floor; y++) put(blocks, meta, column, y, Blocks.stained_hardened_clay, 15);
                put(blocks, meta, column, floor, Blocks.stonebrick, 0);
                // Three operator consoles below; operations desk and commander's seat above.
                boolean station = z == -9 && (ax <= 1 || ax >= 3 && ax <= 4) || z == -16 && ax <= 3
                    || z == -24 && ax <= 2;
                if (station) {
                    put(blocks, meta, column, floor + 1, Blocks.iron_block, 0);
                    put(blocks, meta, column, floor + 2, Blocks.stained_glass, 9);
                }
            }
        }

        // Stepped cyan contour lines stand above a dark plotting surface like the tactical projection.
        int dx = Math.abs(x), dz = Math.abs(z - 5);
        if (dx <= 9 && dz <= 6 && dx + dz <= 12) {
            put(blocks, meta, column, 65, Blocks.stained_hardened_clay, 15);
            int contour = Math.max(dx, dz + 2);
            if (contour == 8 || contour == 6 || contour == 4) {
                int top = 67 + (8 - contour) / 2;
                put(blocks, meta, column, 66, Blocks.glowstone, 0);
                for (int y = 67; y <= top; y++) put(blocks, meta, column, y, Blocks.stained_glass, 9);
            }
        }

        // A faceted panoramic screen; the floor-level path to the south door runs beneath it.
        int screenZ = 28 - Math.max(0, ax - 12) / 3;
        if (ax <= 27 && (z == screenZ || z == screenZ + 1)) {
            boolean backing = z > screenZ;
            for (int y = 68; y <= 75; y++) {
                boolean frame = ax == 27 || ax == 12 || y == 68 || y == 75;
                boolean trace = ax < 12 ? y == 70 + (ax < 4 ? ax : (12 - ax) / 2) : y == 70 && ax % 6 < 4;
                Block block = frame ? Blocks.iron_block
                    : trace ? (backing ? Blocks.glowstone : Blocks.stained_glass) : Blocks.stained_hardened_clay;
                put(blocks, meta, column, y, block, frame || backing && trace ? 0 : trace ? 3 : 15);
            }
            if (ax == 27) for (int y = 65; y < 68; y++) put(blocks, meta, column, y, Blocks.iron_block, 0);
        }

        // Melchior-1, Balthasar-2, Casper-3. Light bars on each face also encode their numbers.
        if (x < -15) magi(blocks, meta, column, x + 24, z + 6, 1);
        else if (x > 15) magi(blocks, meta, column, x - 24, z + 6, 2);
        else magi(blocks, meta, column, x, z - 18, 3);
    }

    private static void magi(Block[] blocks, byte[] meta, int column, int x, int z, int number) {
        int ax = Math.abs(x), az = Math.abs(z);
        if (ax > 5 || az > 3 || ax + az > 7) return;
        put(blocks, meta, column, 65, Blocks.stonebrick, 0);
        put(blocks, meta, column, 66, Blocks.iron_block, 0);
        put(blocks, meta, column, 67, Blocks.stained_hardened_clay, 15);
        if (ax <= 3 && az <= 2) {
            boolean lamp = z == 0 && x >= 1 - number && x <= number - 1 && (x + number) % 2 == 1;
            put(blocks, meta, column, 67, lamp ? Blocks.glowstone : Blocks.iron_block, 0);
            put(blocks, meta, column, 68, Blocks.stained_glass, 1);
        }
    }

    private static void put(Block[] blocks, byte[] meta, int column, int y, Block block, int damage) {
        blocks[column + y] = block;
        meta[column + y] = (byte) damage;
    }
}
