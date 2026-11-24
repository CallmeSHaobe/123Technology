package com.newmaa.othtech.common.dimensions.structure.ross123b;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

/** Interior dressing for the south observation gallery, applied after the basin has been lined and filled. */
final class TerminalDogmaInterior {

    // The gallery door and the existing platform both face east along this line.
    private static final int AXIS_Z = 156;
    private static final int WEST = 29, EAST = 55, NORTH = 146, SOUTH = 166;
    private static final int FLOOR = 40, WATER_TOP = 49, WATERSTOP = WATER_TOP + 1;

    // West-facing relief. Each row runs north to south, with its centre at AXIS_Z.
    // White mask surround, violet mask, seven lit eyes, extended wrists and an unfinished lower body.
    private static final String[] FIGURE = { "       www       ", // y55
        "      vvvvv      ", "      evvve      ", "pww   vevev   wwp", "  www evvve www  ", "    wwwvevwww    ",
        "      wwwww      ", "      wwwww      ", "      wwwww      ", "       www       ", "       www       ",
        "      wwwww      ", "      w ww       ", "      w  w       ", "         w       " // y41
    };

    private TerminalDogmaInterior() {}

    /** x and z are absolute world coordinates; column is the base index of this one local chunk column. */
    static void stamp(Block[] blocks, byte[] metadata, int column, int x, int z) {
        if (x < 12 || x > EAST || z < NORTH || z > SOUTH) return;
        int offset = z - AXIS_Z;
        int side = Math.abs(offset);

        // Heaven's Door: two parked blast leaves, a lintel, interlock lights and a three-wide open throat.
        // Built on the gallery side: neither the original x16/17 passage nor its glazing is replaced.
        if (x == 12 || x == 13) {
            if (side > 6) return;
            set(blocks, metadata, column, 55, side == 0 ? Blocks.glowstone : Blocks.iron_block, 0);
            if (side <= 1) return; // y52..54 remain open under the lintel
            for (int y = 52; y <= 54; y++) {
                if (side == 6) set(blocks, metadata, column, y, Blocks.obsidian, 0);
                else if (x == 12 && side == 2 && y == 53) set(blocks, metadata, column, y, Blocks.redstone_block, 0);
                else if (x == 12 && side == y - 49) set(blocks, metadata, column, y, Blocks.stained_hardened_clay, 4);
                else set(blocks, metadata, column, y, Blocks.iron_block, 0);
            }
            return;
        }
        if (x < WEST) return; // do not touch the existing dry landing (ends at x28)

        // A small dry recess *inside* the LCL basin, not an opening in its perimeter.
        // Its four walls stand one block above liquid level, including the west sill UNDER the new stairs.
        // Place solid glass blocks, not panes/bars. Never clear the reservoir's floor (y39) or roof (y56).
        boolean boundary = x == WEST || x == EAST || z == NORTH || z == SOUTH;
        if (boundary) {
            for (int y = FLOOR; y <= WATERSTOP; y++) {
                boolean frame = y == FLOOR || y == WATERSTOP
                    || (x % 6 == 0 && z % 2 == 0)
                    || (z % 6 == 0 && (x == WEST || x == EAST));
                set(blocks, metadata, column, y, frame ? Blocks.stonebrick : Blocks.glass, 0);
            }
        } else {
            set(blocks, metadata, column, FLOOR, Blocks.stonebrick, 0);
            for (int y = FLOOR + 1; y <= WATER_TOP; y++) set(blocks, metadata, column, y, null, 0);
        }

        // Continue the old platform at its exact y51 elevation, then descend behind the sealed sill.
        // Bottom landing at y40: all treads differ by at most one block, with four or more blocks overhead.
        if (x <= WEST + 12 && side <= 2) {
            int tread = Math.max(FLOOR, 51 - Math.max(0, x - WEST - 1));
            for (int y = FLOOR; y <= tread; y++) set(blocks, metadata, column, y, Blocks.stonebrick, 0);
            if (side == 2 && (x - WEST) % 3 == 0) set(blocks, metadata, column, tread, Blocks.glowstone, 0);
        }

        // The cross is two blocks deep. A redstone edge and dark red faces keep it visible behind the white arms.
        if (x == 52 || x == 53) {
            for (int y = FLOOR + 1; y <= 55; y++) {
                if (side <= 2 || (side <= 9 && y >= 50 && y <= 53)) {
                    if (x == 52 && (side == 2 || side == 9 || y == 53))
                        set(blocks, metadata, column, y, Blocks.redstone_block, 0);
                    else set(blocks, metadata, column, y, Blocks.stained_hardened_clay, 14);
                }
            }
        } else if (x == 50 || x == 51) {
            if (side > 8) return;
            for (int row = 0; row < FIGURE.length; row++) {
                char pixel = FIGURE[row].charAt(offset + 8);
                if (pixel == ' ') continue;
                boolean mask = pixel == 'v' || pixel == 'e';
                if (x == 50 && !mask && (side > 2 || row < 6 || row > 11)) continue;
                if (pixel == 'p') set(blocks, metadata, column, 55 - row, Blocks.iron_block, 0);
                else if (mask && x == 50) {
                    set(
                        blocks,
                        metadata,
                        column,
                        55 - row,
                        pixel == 'e' ? Blocks.glowstone : Blocks.stained_hardened_clay,
                        pixel == 'e' ? 0 : 10);
                } else set(blocks, metadata, column, 55 - row, Blocks.quartz_block, 0);
            }
        }
    }

    private static void set(Block[] blocks, byte[] metadata, int column, int y, Block block, int meta) {
        blocks[column + y] = block;
        metadata[column + y] = (byte) meta;
    }
}
