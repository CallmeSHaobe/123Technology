package com.newmaa.othtech.common.dimensions.structure.ross123b;

import java.io.IOException;
import java.io.InputStream;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import com.newmaa.othtech.common.blocks.fluids.RossFluids;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.GregTechAPI;

/** Places only the vanilla quartz and redstone shell from the exported schematic. */
public final class NervHeadquarters {

    private static final String RESOURCE = "/assets/123technology/structures/nervvvv.schematic";
    private static final int WIDTH = 99;
    private static final int LENGTH = 99;
    private static final int HEIGHT = 67;
    private static final int SCALE = 4;
    private static final int MIN_X = -(WIDTH * SCALE) / 2;
    private static final int MIN_Z = -(LENGTH * SCALE) / 2;
    private static final int BASE_Y = 56;
    private static final int BUILT_HEIGHT = 50;
    private static final byte[] BLOCKS = loadBlocks();

    private NervHeadquarters() {}

    public static boolean intersects(int chunkX, int chunkZ) {
        int x = chunkX << 4;
        int z = chunkZ << 4;
        return x <= MIN_X + WIDTH * SCALE - 1 && x + 15 >= MIN_X && z <= MIN_Z + LENGTH * SCALE - 1 && z + 15 >= MIN_Z;
    }

    public static void stamp(Block[] blocks, byte[] metadata, int chunkX, int chunkZ, long seed) {
        if (!intersects(chunkX, chunkZ)) return;
        Block factory = chiselBlock("factoryblock", Blocks.stonebrick);
        Block laboratory = chiselBlock("laboratoryblock", Blocks.quartz_block);
        Block concrete = chiselBlock("concrete", Blocks.stonebrick);
        Block neon = chiselBlock("glowstone", Blocks.glowstone);
        Block casing = GregTechAPI.sBlockCasings2 != null ? GregTechAPI.sBlockCasings2 : Blocks.iron_block;
        int factoryMeta = factory == Blocks.stonebrick ? 0 : 6;
        int concreteMeta = concrete == Blocks.stonebrick ? 0 : 4;
        int neonMeta = neon == Blocks.glowstone ? 0 : 4;
        int casingMeta = 0;

        for (int x = 0; x < 16; x++) {
            int scaledX = (chunkX << 4) + x - MIN_X;
            if (scaledX < 0 || scaledX >= WIDTH * SCALE) continue;
            for (int z = 0; z < 16; z++) {
                int scaledZ = (chunkZ << 4) + z - MIN_Z;
                if (scaledZ < 0 || scaledZ >= LENGTH * SCALE) continue;

                int column = (x * 16 + z) * 256;
                // Only the first 50 schematic layers contain blocks; the scaled roof reaches y=255.
                for (int y = BASE_Y; y < 256; y++) {
                    blocks[column + y] = null;
                    metadata[column + y] = 0;
                }
                int sourceX = scaledX / SCALE;
                int sourceZ = scaledZ / SCALE;
                for (int y = BASE_Y; y < 256; y++) {
                    int sourceY = (y - BASE_Y) / SCALE;
                    if (sourceY >= BUILT_HEIGHT) break;
                    int id = BLOCKS[(sourceY * LENGTH + sourceZ) * WIDTH + sourceX] & 0xFF;
                    if (id == 155 || id == 152) {
                        blocks[column + y] = id == 155 ? Blocks.quartz_block : Blocks.redstone_block;
                    }
                }
                // Close the paired side apertures of the source pyramid only at their
                // original four layers; leave the intact center rib and north entrance alone.
                if ((sourceZ >= 44 && sourceZ <= 48) || (sourceZ >= 50 && sourceZ <= 54)) {
                    for (int sourceY = 1; sourceY <= 4; sourceY++) {
                        // Match the tapered outer boundary on both sides without
                        // thickening it or disturbing the inner schematic details.
                        if (sourceX == sourceY || sourceX == 98 - sourceY) {
                            for (int dy = 0; dy < SCALE; dy++) {
                                int index = column + BASE_Y + sourceY * SCALE + dy;
                                if (blocks[index] == null) put(
                                    blocks,
                                    metadata,
                                    column,
                                    BASE_Y + sourceY * SCALE + dy,
                                    Blocks.quartz_block,
                                    0);
                            }
                        }
                    }
                }
                stampInterior(blocks, metadata, column, (chunkX << 4) + x, (chunkZ << 4) + z);
                stampFacilities(
                    blocks,
                    metadata,
                    column,
                    (chunkX << 4) + x,
                    (chunkZ << 4) + z,
                    factory,
                    factoryMeta,
                    laboratory,
                    concrete,
                    concreteMeta,
                    neon,
                    neonMeta,
                    casing,
                    casingMeta);
                stampFinish(
                    blocks,
                    metadata,
                    column,
                    (chunkX << 4) + x,
                    (chunkZ << 4) + z,
                    laboratory,
                    factory,
                    factoryMeta,
                    neon,
                    neonMeta,
                    casing,
                    casingMeta);
                stampUnderworld(blocks, metadata, column, (chunkX << 4) + x, (chunkZ << 4) + z, concrete, concreteMeta);
                stampCorruption(blocks, metadata, column, (chunkX << 4) + x, (chunkZ << 4) + z, seed);
            }
        }
    }

    /** Sealed descent to a contained, subterranean LCL basin beneath the south archive. */
    private static void stampUnderworld(Block[] blocks, byte[] metadata, int column, int x, int z, Block concrete,
        int concreteMeta) {
        int ax = Math.abs(x);
        // The stair begins in the archive aisle; each three blocks lower the tread by one.
        // Solid side walls isolate it from surrounding stone and prevent liquid ingress.
        if (ax <= 6 && z >= 103 && z <= 139) {
            int floor = 64 - (z - 103) / 3;
            if (ax <= 4) {
                put(blocks, metadata, column, floor, concrete, concreteMeta);
                for (int y = floor + 1; y <= floor + 4; y++) put(blocks, metadata, column, y, null, 0);
                put(blocks, metadata, column, floor + 5, Blocks.stonebrick, 0);
            } else {
                for (int y = floor; y <= floor + 5; y++) put(blocks, metadata, column, y, Blocks.stonebrick, 0);
            }
        }
        // A roofed observation gallery meets the last step at y51 and overlooks the LCL.
        if (ax <= 17 && z >= 140 && z <= 171) {
            put(blocks, metadata, column, 51, concrete, concreteMeta);
            for (int y = 52; y <= 55; y++) {
                boolean wall = ax >= 16 || z == 171;
                boolean passage = ax == 16 && z >= 155 && z <= 157 && y >= 52 && y <= 54;
                boolean window = ax == 16 && z >= 150 && z <= 165 && y <= 54;
                put(
                    blocks,
                    metadata,
                    column,
                    y,
                    wall ? passage ? null : window ? Blocks.stained_glass : Blocks.stonebrick : null,
                    window && !passage ? 14 : 0);
            }
            put(blocks, metadata, column, 56, Blocks.stonebrick, 0);
            if (ax <= 5 && z % 9 == 0) put(blocks, metadata, column, 56, Blocks.redstone_block, 0);
        }
        // The surrounding stone is left intact below y39. A complete floor, rim and roof
        // enclose the pool so no LCL flows into the stair or unrelated generated terrain.
        if (x >= 17 && x <= 90 && z >= 145 && z <= 190) {
            boolean rim = x == 17 || x == 90 || z == 145 || z == 190;
            put(blocks, metadata, column, 39, Blocks.stonebrick, 0);
            for (int y = 40; y <= 55; y++) {
                boolean passage = x == 17 && z >= 155 && z <= 157 && y >= 52 && y <= 54;
                boolean window = x == 17 && z >= 150 && z <= 165 && y >= 51 && y <= 54;
                Block lining = rim ? passage ? null : window ? Blocks.stained_glass : Blocks.stonebrick : null;
                int meta = window && !passage ? 14 : 0;
                if (!rim && y <= 49 && RossFluids.BlockLCL != null) lining = RossFluids.BlockLCL;
                // The dry landing continues across the rim through the three-wide door.
                if (y == 51 && z >= 153 && z <= 159 && x <= 28 && (!rim || z >= 155 && z <= 157)) lining = concrete;
                put(blocks, metadata, column, y, lining, y == 51 && lining == concrete ? concreteMeta : meta);
            }
            put(blocks, metadata, column, 56, Blocks.stonebrick, 0);
        }
    }

    /** Coordinate-seeded decay changes solid finishes, but never opens the sealed LCL basin. */
    private static void stampCorruption(Block[] blocks, byte[] metadata, int column, int x, int z, long seed) {
        for (int y = 64; y <= 95; y++) {
            int index = column + y;
            Block original = blocks[index];
            if (original != Blocks.quartz_block && original != Blocks.stonebrick && original != Blocks.stained_glass)
                continue;
            // Keep the new stairs and their sealed walls intact; only the visible headquarters ages.
            if (Math.abs(x) <= 17 && z >= 104 && z <= 171 && y <= 65) continue;
            long hash = seed ^ (long) x * 0x9E3779B97F4A7C15L
                ^ (long) z * 0xC2B2AE3D27D4EB4FL
                ^ (long) y * 0xBF58476D1CE4E5B9L;
            hash ^= hash >>> 30;
            hash *= 0xBF58476D1CE4E5B9L;
            hash ^= hash >>> 27;
            hash *= 0x94D049BB133111EBL;
            hash ^= hash >>> 31;
            int roll = (int) (hash & 255);
            if (original == Blocks.stained_glass) {
                if (roll < 5 && !(Math.abs(x) <= 17 && z >= 104)) put(blocks, metadata, column, y, null, 0);
                else if (roll < 17) put(blocks, metadata, column, y, Blocks.iron_bars, 0);
            } else if (roll < 11) {
                put(blocks, metadata, column, y, Blocks.stained_hardened_clay, roll < 4 ? 14 : 15);
            } else if (roll < 17) {
                put(blocks, metadata, column, y, Blocks.redstone_block, 0);
            } else if (roll < 22 && original == Blocks.stonebrick) {
                put(blocks, metadata, column, y, Blocks.stonebrick, 2);
            }
        }
        // The high, exposed pyramid faces carry older red staining and sparse
        // missing quartz tiles. Damage starts above the enclosed occupied rooms.
        for (int y = 105; y <= 175; y++) {
            int index = column + y;
            if (blocks[index] != Blocks.quartz_block) continue;
            long hash = seed ^ (long) x * 0x9E3779B97F4A7C15L
                ^ (long) z * 0xC2B2AE3D27D4EB4FL
                ^ (long) y * 0xBF58476D1CE4E5B9L;
            hash ^= hash >>> 30;
            hash *= 0xBF58476D1CE4E5B9L;
            hash ^= hash >>> 27;
            hash *= 0x94D049BB133111EBL;
            hash ^= hash >>> 31;
            int roll = (int) (hash & 255);
            if (roll < 2) put(blocks, metadata, column, y, null, 0);
            else if (roll < 13) put(blocks, metadata, column, y, Blocks.stained_hardened_clay, 14);
            else if (roll < 18) put(blocks, metadata, column, y, Blocks.redstone_block, 0);
        }
    }

    /** North access, a walled command hall, surrounding concourse and an upper gallery. */
    private static void stampInterior(Block[] blocks, byte[] metadata, int column, int x, int z) {
        int ax = Math.abs(x);
        int az = Math.abs(z);
        int radius = Math.max(ax, az);
        boolean entrance = ax <= 6 && z >= MIN_Z && z <= -48;
        boolean accessCorridor = ax <= 9 && z >= MIN_Z && z <= -54;
        boolean accessWall = ax >= 8 && ax <= 9 && z >= MIN_Z && z <= -54;
        boolean lowerRing = radius >= 64 && radius <= 75;
        boolean concourse = radius <= 63;
        boolean crosswalk = (ax <= 5 || az <= 5) && radius <= 75;
        boolean hall = radius <= 55;
        boolean dais = ax <= 15 && az <= 15;
        boolean upperRing = radius >= 33 && radius <= 43;
        boolean upperBridge = ax >= 15 && ax <= 48 && az <= 5;
        boolean stairs = x >= 44 && x <= 48 && z >= -43 && z <= -5;
        boolean office = radius <= 21;
        boolean officeStairs = x >= 19 && x <= 43 && az <= 4;
        boolean lower = entrance || accessCorridor || lowerRing || concourse || crosswalk;

        // The original tapered shell has solid north walls at y60-67: open both layers.
        if (entrance) {
            for (int y = 60; y <= 72; y++) put(blocks, metadata, column, y, null, 0);
        }
        if (lower) {
            put(blocks, metadata, column, 64, radius >= 54 ? Blocks.stonebrick : Blocks.quartz_block, 0);
            if (!entrance) {
                for (int y = 65; y <= 75; y++) put(blocks, metadata, column, y, null, 0);
            }
            if ((ax == 5 && z < -76 && z % 12 == 0) || (radius == 70 && (x % 12 == 0 || z % 12 == 0))) {
                put(blocks, metadata, column, 64, Blocks.redstone_block, 0);
            }
            if (lowerRing && radius == 74 && (x % 12 == 0 || z % 12 == 0)) {
                put(blocks, metadata, column, 64, Blocks.glowstone, 0);
            }
        }
        // The covered approach runs from the cut in the outer shell to the north command-room doors.
        // Keep the middle seven blocks clear for players; glass bands let the concourse see inside.
        if (accessCorridor) {
            // Enclose the approach from the outer-shell opening through to the command-room doors.
            if (accessWall) {
                for (int y = 65; y <= 73; y++) {
                    boolean window = y >= 68 && y <= 71 && z % 12 >= -8 && z % 12 <= -4;
                    put(blocks, metadata, column, y, window ? Blocks.stained_glass : Blocks.stonebrick, window ? 8 : 0);
                }
            }
            put(blocks, metadata, column, 74, Blocks.stonebrick, 0);
            if (ax <= 6 && z % 16 == 0 && z >= MIN_Z + 10) {
                put(blocks, metadata, column, 74, Blocks.glowstone, 0);
            }
        }
        // Fully enclosed square concourse: glazed inner/outer walls and cardinal doorways.
        if (radius >= 63 && radius <= 76) {
            boolean cardinal = ax <= 5 || az <= 5;
            if (!cardinal && (radius == 63 || radius == 76)) {
                for (int y = 65; y <= 73; y++) {
                    boolean window = y >= 68 && y <= 71 && (x + z) % 12 >= 3 && (x + z) % 12 <= 8;
                    put(blocks, metadata, column, y, window ? Blocks.stained_glass : Blocks.stonebrick, window ? 8 : 0);
                }
            }
            if (radius >= 64 && radius <= 75) put(blocks, metadata, column, 74, Blocks.stonebrick, 0);
        }
        if (crosswalk && radius >= 55 && radius <= 76) {
            boolean north = z < 0 && ax <= 5;
            boolean side = az <= 5 && ax >= 56;
            if (!north && !side) put(blocks, metadata, column, 74, Blocks.stonebrick, 0);
        }
        // The quartz-and-stonebrick walls enclose a 111x111 hall. Two wide doors join the north
        // approach and eastern concourse; glass bays and lit pilasters make the long walls legible.
        if (hall) {
            boolean wall = radius >= 54;
            boolean northDoor = z <= -54 && ax <= 6;
            boolean eastDoor = x >= 54 && az <= 5;
            boolean westDoor = x <= -54 && az <= 5;
            boolean southDoor = z >= 54 && ax <= 5;
            if (wall) {
                for (int y = 65; y <= 75; y++) {
                    Block block = y == 65 || y >= 74 ? Blocks.stonebrick : Blocks.quartz_block;
                    int meta = 0;
                    if ((northDoor || eastDoor || westDoor || southDoor) && y <= 70) block = null;
                    else if (y >= 68 && y <= 71
                        && ((ax > 13 && ax < 48 && z <= -54) || (az > 13 && az < 48 && x >= 54)
                            || (az > 13 && az < 48 && x <= -54)
                            || (ax > 13 && ax < 48 && z >= 54))) {
                                block = Blocks.stained_glass;
                                meta = 8;
                            }
                    put(blocks, metadata, column, y, block, meta);
                }
            }
            // A real ceiling frames an open skylight above the dais, rather than a tiny island
            // floating beneath the empty headquarters shell.
            if (radius >= 9) {
                put(
                    blocks,
                    metadata,
                    column,
                    76,
                    (x % 10 == 0 || z % 10 == 0) ? Blocks.stonebrick : Blocks.quartz_block,
                    0);
                if (radius < 53 && x % 12 == 0 && z % 12 == 0) put(blocks, metadata, column, 76, Blocks.glowstone, 0);
            }
        }
        // Regular supports connect the much larger command hall to its surrounding concourse.
        if (radius == 60 && ((ax == 60 && z % 12 == 0) || (az == 60 && x % 12 == 0))) {
            for (int y = 65; y <= 76; y++) {
                put(blocks, metadata, column, y, y == 71 ? Blocks.glowstone : Blocks.stonebrick, 0);
            }
        }
        if (dais) {
            int platformY = z <= -10 ? 65 : z <= -7 ? 66 : 67;
            for (int y = 65; y <= platformY + 3; y++) put(blocks, metadata, column, y, null, 0);
            put(blocks, metadata, column, platformY, Blocks.stonebrick, 0);
            if (z == 11 && ax <= 9) {
                for (int y = 68; y <= 73; y++) {
                    put(
                        blocks,
                        metadata,
                        column,
                        y,
                        y == 69 || y == 73 ? Blocks.redstone_block : Blocks.stained_hardened_clay,
                        y == 69 || y == 73 ? 0 : 15);
                }
            }
            if ((x == -10 || x == 10) && (z == -8 || z == 6)) put(blocks, metadata, column, 70, Blocks.glowstone, 0);
        }
        if (upperRing || upperBridge) {
            if (radius == 40 && ((ax == 40 && (z == 0 || az == 40)) || (az == 40 && x == 0))) {
                for (int y = 65; y <= 76; y++) put(blocks, metadata, column, y, Blocks.stonebrick, 0);
            }
            put(blocks, metadata, column, 77, Blocks.stonebrick, 0);
            for (int y = 78; y <= 81; y++) put(blocks, metadata, column, y, null, 0);
            // Leave headroom as the office staircase climbs through the bridge roof.
            if (!(officeStairs && x <= 37)) put(blocks, metadata, column, 82, Blocks.quartz_block, 0);
            // Both gallery edges are full-height walls with glazing, except for the
            // eastern staircase landing and the bridge into the office stairs.
            if (upperRing && (radius == 33 || radius == 43) && !(x >= 33 && az <= 5)) {
                for (int y = 78; y <= 81; y++) {
                    boolean window = y >= 79 && y <= 80 && (x + z) % 10 >= 3 && (x + z) % 10 <= 6;
                    put(blocks, metadata, column, y, window ? Blocks.stained_glass : Blocks.stonebrick, window ? 8 : 0);
                }
            }
            if (upperRing && radius == 42 && (x % 10 == 0 || z % 10 == 0))
                put(blocks, metadata, column, 77, Blocks.glowstone, 0);
        }
        // One-block rises every three blocks; the last step meets the gallery at y77.
        if (stairs) {
            int stepY = Math.min(77, 65 + (z + 43) / 3);
            for (int y = 65; y <= stepY + 3; y++) put(blocks, metadata, column, y, null, 0);
            put(blocks, metadata, column, stepY, Blocks.stonebrick, 0);
            for (int y = stepY + 4; y <= stepY + 6; y++) put(blocks, metadata, column, y, Blocks.stonebrick, 0);
            if (x == 44 || x == 48) {
                for (int y = stepY + 1; y <= stepY + 3; y++) put(blocks, metadata, column, y, Blocks.stonebrick, 0);
            }
        }
        // A smaller commander's office sits above the hall, reached from the east gallery.
        // Build its own floor and supports: the source schematic has no upper interior floors.
        if (office) {
            if ((ax == 18 && (az == 18 || z == 0)) || (az == 18 && x == 0)) {
                for (int y = 77; y <= 84; y++) put(blocks, metadata, column, y, Blocks.stonebrick, 0);
            }
            put(blocks, metadata, column, 85, radius >= 19 ? Blocks.stonebrick : Blocks.quartz_block, 0);
            boolean wall = radius >= 20;
            boolean door = x >= 20 && az <= 4;
            for (int y = 86; y <= 94; y++) {
                Block block = null;
                int meta = 0;
                if (wall) {
                    block = y == 86 || y >= 93 ? Blocks.stonebrick : Blocks.quartz_block;
                    if (door && y <= 89) block = null;
                    else if (y >= 88 && y <= 91 && !door && (ax >= 13 || az >= 13) && x % 8 != 0 && z % 8 != 0) {
                        block = Blocks.stained_glass;
                        meta = 8;
                    }
                }
                put(blocks, metadata, column, y, block, meta);
            }
            put(blocks, metadata, column, 95, Blocks.quartz_block, 0);
            if ((x == -14 || x == 14) && (z == -14 || z == 14)) put(blocks, metadata, column, 95, Blocks.glowstone, 0);
            if (ax <= 8 && z >= 7 && z <= 9) {
                put(blocks, metadata, column, 86, Blocks.stonebrick, 0);
                if (z == 9 && ax <= 6) put(blocks, metadata, column, 87, Blocks.redstone_block, 0);
            }
        }
        if (officeStairs) {
            int stepY = Math.min(85, 77 + (43 - x) / 3);
            for (int y = 78; y <= stepY + 3; y++) put(blocks, metadata, column, y, null, 0);
            put(blocks, metadata, column, stepY, Blocks.stonebrick, 0);
            for (int y = stepY + 4; y <= stepY + 6; y++) put(blocks, metadata, column, y, Blocks.quartz_block, 0);
            if (az == 4) {
                for (int y = stepY + 1; y <= stepY + 3; y++) put(blocks, metadata, column, y, Blocks.quartz_block, 0);
            }
        }
    }

    /** The room shells stay inside the sloping schematic and use only the current column. */
    private static void stampFacilities(Block[] blocks, byte[] metadata, int column, int x, int z, Block factory,
        int factoryMeta, Block laboratory, Block concrete, int concreteMeta, Block neon, int neonMeta, Block casing,
        int casingMeta) {
        int ax = Math.abs(x);
        int az = Math.abs(z);
        // Polished Chisel surfaces connect the old command room to the newly built wings.
        if (ax <= 53 && az <= 53 && !(ax <= 15 && az <= 15)) {
            put(blocks, metadata, column, 64, laboratory, 0);
        }
        if (ax <= 19 && az <= 19) {
            put(blocks, metadata, column, 85, laboratory, 0);
        }
        // Refurbish existing interiors without replacing their doorways or stair treads.
        if (ax <= 6 && z >= MIN_Z + 10 && z < -54 && z % 8 == 0) {
            put(blocks, metadata, column, 74, neon, neonMeta);
        }
        if (ax <= 55 && az <= 55 && Math.max(ax, az) >= 54 && ax > 7 && az > 7) {
            for (int y = 66; y <= 67; y++) put(blocks, metadata, column, y, laboratory, 0);
        }
        // East and west service spines connect both wings to the command-room side doors.
        boolean spine = ax >= 56 && ax <= 91 && az <= 7;
        boolean connector = ax >= 85 && ax <= 90 && az >= 8 && az <= 23;
        boolean wing = ax >= 84 && ax <= 144 && az >= 12 && az <= 66;
        if (spine || connector || wing) {
            put(blocks, metadata, column, 64, concrete, concreteMeta);
            for (int y = 65; y <= 73; y++) put(blocks, metadata, column, y, null, 0);
            put(blocks, metadata, column, 74, factory, factoryMeta);
        }
        if (connector && z <= 11 && (ax == 85 || ax == 90)) {
            for (int y = 65; y <= 73; y++) put(blocks, metadata, column, y, laboratory, 0);
        }
        if (spine && (az == 7 || ax == 91)) {
            for (int y = 65; y <= 73; y++) {
                boolean door = ax >= 85 && ax <= 90 && az == 7 && y <= 69;
                boolean window = y >= 68 && y <= 70 && ax % 11 > 3 && ax % 11 < 8;
                put(
                    blocks,
                    metadata,
                    column,
                    y,
                    door ? null : window ? Blocks.stained_glass : factory,
                    door ? 0 : window ? 8 : factoryMeta);
            }
        }
        if (wing) {
            boolean wall = ax >= 142 || ax <= 85 || az >= 64 || az <= 13;
            // The short connector crosses the wing's inner wall and joins its two service rooms.
            if (connector) {
                for (int y = 65; y <= 69; y++) put(blocks, metadata, column, y, null, 0);
                wall = false;
            }
            if (wall) {
                for (int y = 65; y <= 73; y++) {
                    boolean window = y >= 68 && y <= 71 && ax % 14 >= 4 && ax % 14 <= 10;
                    put(blocks, metadata, column, y, window ? Blocks.stained_glass : laboratory, window ? 8 : 0);
                }
            }
            put(blocks, metadata, column, 74, factory, factoryMeta);
            if (ax % 12 == 0 && az % 12 == 0) put(blocks, metadata, column, 74, neon, neonMeta);
            // Inert GT casings make maintenance stations; no machine tile entities or tier are implied.
            if (ax >= 103 && ax <= 132 && az >= 32 && az <= 54 && (ax % 12 == 0 && az % 10 == 0)) {
                put(blocks, metadata, column, 65, casing, casingMeta);
                put(blocks, metadata, column, 66, laboratory, 0);
            }
        }
        // South access continues the command room's south door into a records and communications hall.
        boolean southPassage = ax <= 9 && z >= 56 && z <= 96;
        boolean archive = ax <= 64 && z >= 94 && z <= 148;
        if (southPassage || archive) {
            put(blocks, metadata, column, 64, concrete, concreteMeta);
            for (int y = 65; y <= 73; y++) put(blocks, metadata, column, y, null, 0);
        }
        if (southPassage) {
            if (ax >= 8 && z < 94) {
                for (int y = 65; y <= 73; y++) {
                    boolean window = y >= 68 && y <= 71 && z % 12 >= 3 && z % 12 <= 8;
                    put(blocks, metadata, column, y, window ? Blocks.stained_glass : laboratory, window ? 8 : 0);
                }
            }
            if (z < 94) put(blocks, metadata, column, 74, factory, factoryMeta);
            if (ax <= 5 && z < 94 && z % 12 == 0) put(blocks, metadata, column, 74, neon, neonMeta);
        }
        if (archive) {
            boolean wall = ax >= 62 || z <= 96 || z >= 146;
            boolean door = z <= 96 && ax <= 6;
            if (wall) {
                for (int y = 65; y <= 73; y++) {
                    boolean window = y >= 68 && y <= 71 && ax % 12 >= 4 && ax % 12 <= 8;
                    put(
                        blocks,
                        metadata,
                        column,
                        y,
                        door && y <= 70 ? null : window ? Blocks.stained_glass : factory,
                        door && y <= 70 ? 0 : window ? 8 : factoryMeta);
                }
            }
            put(blocks, metadata, column, 74, laboratory, 0);
            if (ax % 12 == 0 && z % 12 == 0) put(blocks, metadata, column, 74, neon, neonMeta);
            // Separate archive stacks from the central six-block-wide circulation aisle.
            if (ax >= 14 && ax <= 52 && z >= 107 && z <= 136 && z % 8 <= 1 && ax % 10 <= 6) {
                for (int y = 65; y <= 67; y++) put(blocks, metadata, column, y, Blocks.bookshelf, 0);
            }
            // Inert GT casing desks in the far corners, deliberately not functional machine tiles.
            if (ax >= 46 && ax <= 50 && z >= 138 && z <= 141) put(blocks, metadata, column, 65, casing, casingMeta);
        }
    }

    /** Finish exposed interiors without carving circulation paths or writing outside this chunk. */
    private static void stampFinish(Block[] blocks, byte[] metadata, int column, int x, int z, Block laboratory,
        Block factory, int factoryMeta, Block neon, int neonMeta, Block casing, int casingMeta) {
        int ax = Math.abs(x);
        int az = Math.abs(z);
        int radius = Math.max(ax, az);
        // Recessed geometric lines and light bays on the command hall floor; keep the dais intact.
        if (radius >= 18 && radius <= 50 && ax > 7 && az > 7) {
            if (ax % 12 == 0 || az % 12 == 0) put(blocks, metadata, column, 64, factory, factoryMeta);
            if (ax % 12 == 0 && az % 12 == 0) put(blocks, metadata, column, 64, neon, neonMeta);
        }
        // Elevated command booth: compact worktop away from its eastern door and the stairs.
        if (ax >= 9 && ax <= 14 && z >= 6 && z <= 8) {
            put(blocks, metadata, column, 86, laboratory, 0);
            if (ax == 9 || ax == 14) put(blocks, metadata, column, 87, neon, neonMeta);
        }
        // Matching casing-based process racks in both workshop rooms. These are decorative
        // multi-block assemblies, not powered GT multiblock machines or uninitialized tiles.
        if (ax >= 110 && ax <= 118 && z >= 37 && z <= 47) {
            boolean frame = ax == 110 || ax == 118 || z == 37 || z == 47;
            boolean serviceGap = z == 37 && ax >= 113 && ax <= 115;
            if (frame && !serviceGap) {
                for (int y = 65; y <= 69; y++) {
                    if (y == 65 || y == 69 || (ax == 110 || ax == 118) && y % 2 == 0)
                        put(blocks, metadata, column, y, casing, casingMeta);
                }
            }
            if (!frame && (ax == 112 || ax == 116) && z >= 39 && z <= 45 && z % 3 == 0) {
                put(blocks, metadata, column, 65, casing, casingMeta);
            }
        }
        // Archive terminals and index lighting stay off the six-block central aisle.
        if (ax >= 38 && ax <= 42 && z >= 138 && z <= 140) {
            put(blocks, metadata, column, 65, casing, casingMeta);
            if (z == 140) put(blocks, metadata, column, 66, laboratory, 0);
        }
        if (ax <= 6 && z >= 102 && z <= 141 && z % 9 == 0) put(blocks, metadata, column, 74, neon, neonMeta);
    }

    /** Deterministic sparse workshop clutter, never in the central aisles or connecting doors. */
    public static int machineVariant(long seed, int x, int z) {
        int ax = Math.abs(x);
        int az = Math.abs(z);
        boolean workshop = ax >= 94 && ax <= 137 && az >= 24 && az <= 59;
        boolean records = ax >= 18 && ax <= 56 && z >= 104 && z <= 140;
        if (!workshop && !records) return -1;
        // Keep the casing rack entrance and its maintenance aisle clear of live machines.
        if (ax >= 110 && ax <= 118 && z >= 37 && z <= 47) return -1;
        // A second, deliberately irregular machine grid fills the workshop and archive
        // without putting machines in the north/south access routes or by any door.
        int cellX = Math.floorDiv(x, 7);
        int cellZ = Math.floorDiv(z, 7);
        long hash = seed ^ ((long) cellX * 0x9E3779B97F4A7C15L) ^ ((long) cellZ * 0xC2B2AE3D27D4EB4FL);
        hash ^= hash >>> 30;
        hash *= 0xBF58476D1CE4E5B9L;
        hash ^= hash >>> 27;
        hash *= 0x94D049BB133111EBL;
        hash ^= hash >>> 31;
        if (Math.floorMod(x, 7) != 2 + (int) (hash & 3) || Math.floorMod(z, 7) != 2 + (int) ((hash >>> 4) & 3))
            return -1;
        return (int) ((hash >>> 12) & 7);
    }

    private static Block chiselBlock(String name, Block fallback) {
        Block block = GameRegistry.findBlock("chisel", name);
        return block == null || block == Blocks.air ? fallback : block;
    }

    private static void put(Block[] blocks, byte[] metadata, int column, int y, Block block, int meta) {
        blocks[column + y] = block;
        metadata[column + y] = (byte) meta;
    }

    private static byte[] loadBlocks() {
        try (InputStream stream = NervHeadquarters.class.getResourceAsStream(RESOURCE)) {
            if (stream == null) throw new IOException("Missing " + RESOURCE);
            NBTTagCompound schematic = CompressedStreamTools.readCompressed(stream);
            if (schematic.getShort("Width") != WIDTH || schematic.getShort("Length") != LENGTH
                || schematic.getShort("Height") != HEIGHT) {
                throw new IOException("Unexpected NERV schematic dimensions");
            }
            byte[] blocks = schematic.getByteArray("Blocks");
            byte[] addBlocks = schematic.getByteArray("AddBlocks");
            if (blocks.length != WIDTH * LENGTH * HEIGHT || addBlocks.length != (blocks.length + 1) / 2) {
                throw new IOException("Invalid NERV schematic block arrays");
            }
            // AddBlocks contains the high ID bits. Strip every non-vanilla block before stamping.
            for (int i = 0; i < blocks.length; i++) {
                if (((addBlocks[i >> 1] >> ((i & 1) * 4)) & 15) != 0) blocks[i] = 0;
            }
            return blocks;
        } catch (IOException e) {
            throw new IllegalStateException("Could not load NERV headquarters schematic", e);
        }
    }
}
