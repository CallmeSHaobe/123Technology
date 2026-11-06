package com.newmaa.othtech.common.dimensions.structure.ross123b;

import java.io.IOException;
import java.io.InputStream;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

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

    public static void stamp(Block[] blocks, byte[] metadata, int chunkX, int chunkZ) {
        if (!intersects(chunkX, chunkZ)) return;

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
            }
        }
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
