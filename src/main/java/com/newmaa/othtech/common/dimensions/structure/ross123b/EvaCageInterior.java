package com.newmaa.othtech.common.dimensions.structure.ross123b;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

/** Unit-01's empty restraint cage in the southeast service room. Coordinates are world block coordinates. */
public final class EvaCageInterior {

    private static final int FLOOR = 64;
    private static final int WALK = 69;
    private static final int ROOF = 86;
    private static final int GRAY = 7, BLACK = 15, PURPLE = 10, LIME = 5, YELLOW = 4;

    // Negative space is intentional: the shaft is empty, not an EVA statue or a functional machine.
    private static final String[] UNIT_NUMBER = { "1110010", "1010110", "1010010", "1010010", "1110111" };

    private EvaCageInterior() {}

    /** Reserve the entire apron, including circulation space, from the normal workshop machine scatter. */
    public static boolean contains(int x, int z) {
        return x >= 94 && x <= 136 && z >= 24 && z <= 59;
    }

    /** Run after the workshop's casing racks and finishes. Writes only the supplied chunk column. */
    public static void stamp(Block[] blocks, byte[] metadata, int column, int x, int z) {
        if (!contains(x, z)) return;
        // Clear the old free-standing racks, not the original floor, wing boundary walls or access spine.
        for (int y = FLOOR + 1; y <= 73; y++) put(blocks, metadata, column, y, null, 0);
        if (z <= 28) return; // Flat five-wide access from the existing service connector, open end to end.

        int dx = Math.abs(x - 116);
        boolean chamber = dx <= 14 && z >= 34 && z <= 58;
        if (chamber) {
            // Lift only this bay of the factory ceiling. The original schematic is empty above it to y86.
            boolean rim = dx == 14 || z == 34 || z == 58;
            for (int y = 74; y <= ROOF; y++) {
                boolean beam = y == ROOF || rim && (y % 4 == 2 || dx == 14 && (z - 34) % 6 == 0);
                Block block = beam ? Blocks.iron_block : rim ? Blocks.stained_hardened_clay : null;
                put(blocks, metadata, column, y, block, block == Blocks.stained_hardened_clay ? GRAY : 0);
            }
        }

        // Recessed-looking lift shoe, yellow/black pit lip and a split overhead shutter. No open fall or liquid.
        if (dx <= 8 && z >= 43 && z <= 55) {
            boolean lip = dx == 8 || z == 43 || z == 55;
            int color = lip ? (((x + z) / 2 & 1) == 0 ? YELLOW : BLACK) : dx <= 1 ? GRAY : BLACK;
            put(blocks, metadata, column, FLOOR, Blocks.stained_hardened_clay, color);
            put(blocks, metadata, column, ROOF, Blocks.stained_hardened_clay, dx == 0 ? BLACK : GRAY);
            if (dx == 6 && (z == 44 || z == 54)) put(blocks, metadata, column, ROOF, Blocks.glowstone, 0);
        }
        // Two back-wall guide rails, with periodic mounting shoes and visible signal lamps.
        if (dx >= 4 && dx <= 6 && z >= 53 && z <= 54) {
            for (int y = FLOOR + 1; y < ROOF; y++) {
                if (dx == 5 && z == 53) put(blocks, metadata, column, y, Blocks.iron_block, 0);
                else if (z == 54 && (y - FLOOR) % 4 == 1)
                    put(blocks, metadata, column, y, dx == 5 ? Blocks.glowstone : Blocks.stonebrick, 0);
            }
        }
        // Mirrored elevated shoulder locks: anchored pylons, piston-like arms and inward-facing padded jaws.
        if (z >= 47 && z <= 50 && dx >= 5 && dx <= 11) {
            if (dx >= 9) {
                for (int y = FLOOR + 1; y <= 80; y++) {
                    boolean stripe = y == 73 || y == 78;
                    put(blocks, metadata, column, y, Blocks.stained_hardened_clay, stripe ? LIME : PURPLE);
                }
            } else if (z >= 48 && z <= 49) {
                for (int y = 74; y <= 76; y++) put(blocks, metadata, column, y, Blocks.iron_block, 0);
                if (dx == 5) {
                    for (int y = 73; y <= 78; y++)
                        put(blocks, metadata, column, y, Blocks.stained_hardened_clay, y == 77 ? LIME : BLACK);
                }
            }
        }

        // Three-wide service stair: every two blocks rise one. It joins the west landing at exactly y69.
        if (x >= 96 && x <= 98 && z >= 30 && z <= 39) {
            int top = FLOOR + 1 + (z - 30) / 2;
            for (int y = FLOOR + 1; y <= top; y++) put(blocks, metadata, column, y, Blocks.stonebrick, 0);
        }
        boolean landing = x >= 96 && x <= 101 && z >= 40 && z <= 43;
        boolean gantry = x >= 102 && x <= 132 && z >= 40 && z <= 43;
        if (landing || gantry) {
            put(blocks, metadata, column, WALK, Blocks.iron_block, 0);
            boolean post = (x == 96 || x == 101 || x == 116 || x == 132) && (z == 40 || z == 43);
            if (post) for (int y = FLOOR + 1; y < WALK; y++) put(blocks, metadata, column, y, Blocks.iron_bars, 0);
            boolean rail = z == 43 || x == 132 || x == 96 || z == 40 && x > 98;
            // Leave the full three-wide stair mouth open, including its western edge.
            if (rail && !(z == 40 && x <= 98)) put(blocks, metadata, column, WALK + 1, Blocks.iron_bars, 0);
        }

        // Freestanding, one-block-thick 01 placard faces north toward the access apron.
        if (x >= 123 && x <= 131 && z == 32) {
            if (x == 123 || x == 131) put(blocks, metadata, column, 65, Blocks.iron_block, 0);
            for (int y = 66; y <= 72; y++) {
                boolean glyph = x >= 124 && x <= 130
                    && y >= 67
                    && y <= 71
                    && UNIT_NUMBER[71 - y].charAt(x - 124) == '1';
                put(
                    blocks,
                    metadata,
                    column,
                    y,
                    glyph ? Blocks.quartz_block : Blocks.stained_hardened_clay,
                    glyph ? 0 : PURPLE);
            }
            if (x == 123 || x == 131) put(blocks, metadata, column, 73, Blocks.glowstone, 0);
        }
    }

    private static void put(Block[] blocks, byte[] metadata, int column, int y, Block block, int meta) {
        blocks[column + y] = block;
        metadata[column + y] = (byte) meta;
    }
}
