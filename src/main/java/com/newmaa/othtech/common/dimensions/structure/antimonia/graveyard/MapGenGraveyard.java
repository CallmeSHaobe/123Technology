package com.newmaa.othtech.common.dimensions.structure.antimonia.graveyard;

import java.util.Random;

import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureStart;

/**
 * 锑星坟场：每 24x24 个区块随机挑一个位置生成一座 33x33 的坟场。
 * <p>
 * 里面有三只狗的刷怪笼、各种石头墓碑、南侧一座巨大的「F8」碑墙，
 * 四个角落的箱子固定给 8 枚 GT 白铜币，另有几率开出星系 T1-T3 火箭。
 */
public class MapGenGraveyard extends MapGenStructure {

    /** 结构间距（区块）：每个 24x24 区块的区域最多一座坟场。 */
    private static final int SPACING = 24;
    /** 随机偏移上限：SPACING - OFFSET 就是偏移范围。 */
    private static final int OFFSET = 18;

    private static boolean initialized;

    static {
        try {
            initiateStructures();
        } catch (final Throwable ignored) {
            // MapGenStructureIO 还没就绪时忽略，构造时还会再注册一次
        }
    }

    public MapGenGraveyard() {
        try {
            initiateStructures();
        } catch (final Throwable ignored) {}
    }

    public static void initiateStructures() throws Throwable {
        if (!initialized) {
            MapGenStructureIO.registerStructure(StructureStartGraveyard.class, "AntimoniaGraveyard");
            MapGenStructureIO.func_143031_a(StructureComponentGraveyard.class, "AntimoniaGraveyardComponent");
        }
        initialized = true;
    }

    @Override
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
        int i = chunkX;
        int j = chunkZ;
        if (i < 0) {
            i -= SPACING - 1;
        }
        if (j < 0) {
            j -= SPACING - 1;
        }

        final int cellX = i / SPACING;
        final int cellZ = j / SPACING;
        final Random rand = this.worldObj.setRandomSeed(i, j, 0xF8F8);
        final int targetX = cellX * SPACING + rand.nextInt(SPACING - OFFSET);
        final int targetZ = cellZ * SPACING + rand.nextInt(SPACING - OFFSET);
        return chunkX == targetX && chunkZ == targetZ;
    }

    @Override
    protected StructureStart getStructureStart(int chunkX, int chunkZ) {
        return new StructureStartGraveyard(this.worldObj, this.rand, chunkX, chunkZ);
    }

    @Override
    public String func_143025_a() {
        return "AntimoniaGraveyard";
    }
}
