package com.newmaa.othtech.common.dimensions.structure.antimonia.graveyard;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import gregtech.api.enums.ItemList;
import micdoodle8.mods.galacticraft.core.items.GCItems;
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems;
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems;

/**
 * 锑星坟场本体（33x33）。
 * <p>
 * 组成：
 * <ul>
 * <li>整平的石台 + 一圈矮墙（南侧留 3 格门）；</li>
 * <li>三排十字墓碑，用各种石头砌成（圆石 / 苔石 / 石砖的各种 meta）；</li>
 * <li>三座刷怪笼石亭，分别刷黄狗 / 红狗 / 蓝狗；</li>
 * <li>南侧一座巨大的「F8」碑墙；</li>
 * <li>四角的箱子：固定 8 枚 GT 白铜币，另有几率开出星系 T1-T3 火箭。</li>
 * </ul>
 * 所有坐标都是「结构本地坐标」，随机数只用来决定战利品，不影响布局，
 * 所以同一座坟场跨区块生成时不会错位。
 */
public class StructureComponentGraveyard extends StructureComponent {

    /** 结构边长。 */
    public static final int SIZE = 33;
    /** 台面下方的补台深度。 */
    private static final int PLINTH_DEPTH = 6;
    /** 本地 y：地面（方块在这里，实体站在上面）。 */
    private static final int PLATFORM = PLINTH_DEPTH;
    /** 地面上方清空的高度。 */
    private static final int CLEAR_HEIGHT = 12;

    /** 三个刷怪笼的实体 id，顺序：黄 / 红 / 蓝。 */
    private static final String[] DOG_IDS = { "123Technology.DogYellow", "123Technology.DogRed",
        "123Technology.DogBlue" };

    /** 「F」和「8」的像素图（从上到下，每行最多 6 列）。 */
    private static final String[] LETTER_F = { "######", "#", "#", "#", "####", "#", "#", "#", "#" };
    private static final String[] LETTER_8 = { " #### ", "#    #", "#    #", "#    #", " #### ", "#    #", "#    #",
        "#    #", " #### " };

    private int baseY;
    private static WeightedRandomChestContent[] rocketLoot;

    public StructureComponentGraveyard() {}

    public StructureComponentGraveyard(Random rand, int originX, int baseY, int originZ) {
        super(0);
        this.coordBaseMode = 0;
        this.baseY = baseY;
        final int minX = originX - SIZE / 2;
        final int minZ = originZ - SIZE / 2;
        this.boundingBox = new StructureBoundingBox(
            minX,
            baseY - PLINTH_DEPTH,
            minZ,
            minX + SIZE - 1,
            baseY + CLEAR_HEIGHT + 1,
            minZ + SIZE - 1);
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbt) {
        nbt.setInteger("GraveyardBaseY", this.baseY);
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbt) {
        this.baseY = nbt.getInteger("GraveyardBaseY");
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox chunkBox) {
        this.placePlatform(world, chunkBox);
        this.buildWall(world, chunkBox);
        this.buildTombstones(world, chunkBox);
        this.buildSpawnerCages(world, chunkBox);
        this.buildLetterWall(world, chunkBox);
        this.buildChest(world, rand, chunkBox, 4, 4);
        this.buildChest(world, rand, chunkBox, 28, 4);
        this.buildChest(world, rand, chunkBox, 4, 28);
        this.buildChest(world, rand, chunkBox, 28, 28);
        return true;
    }

    // ------------------------------------------------------------------ 地面

    /** 清空上方、铺地面、往下补石台，把这块地整平。 */
    private void placePlatform(World world, StructureBoundingBox chunkBox) {
        // 地面上方全部清空（alwaysReplace = true）
        this.fillWithBlocks(
            world,
            chunkBox,
            0,
            PLATFORM + 1,
            0,
            SIZE - 1,
            PLATFORM + CLEAR_HEIGHT,
            SIZE - 1,
            Blocks.air,
            Blocks.air,
            true);

        for (int x = 0; x < SIZE; x++) {
            for (int z = 0; z < SIZE; z++) {
                // 地面：外圈石砖，内部圆石 + 碎石走道
                this.placeBlockAtCurrentPosition(world, floorBlock(x, z), 0, x, PLATFORM, z, chunkBox);

                // 往下补石台，遇到地形就停，避免整座坟场悬空
                for (int dy = 1; dy <= PLINTH_DEPTH; dy++) {
                    final int wx = this.getXWithOffset(x, z);
                    final int wy = this.getYWithOffset(PLATFORM - dy);
                    final int wz = this.getZWithOffset(x, z);
                    if (!chunkBox.isVecInside(wx, wy, wz)) {
                        continue;
                    }
                    final Block existing = world.getBlock(wx, wy, wz);
                    if (existing != Blocks.air && existing != Blocks.water && existing != Blocks.flowing_water) {
                        break;
                    }
                    this.placeBlockAtCurrentPosition(world, Blocks.stone, 0, x, PLATFORM - dy, z, chunkBox);
                }
            }
        }
    }

    /** 地面材质：只按坐标决定，保证跨区块一致。 */
    private static Block floorBlock(int x, int z) {
        if (x == 0 || z == 0 || x == SIZE - 1 || z == SIZE - 1) {
            return Blocks.stonebrick;
        }
        if (x == SIZE / 2 || z == SIZE / 2) {
            return Blocks.gravel;
        }
        return ((x + z) & 3) == 0 ? Blocks.cobblestone : Blocks.stonebrick;
    }

    // ------------------------------------------------------------------ 矮墙

    private void buildWall(World world, StructureBoundingBox chunkBox) {
        for (int x = 0; x < SIZE; x++) {
            for (int z = 0; z < SIZE; z++) {
                final boolean edge = x == 0 || z == 0 || x == SIZE - 1 || z == SIZE - 1;
                if (!edge) {
                    continue;
                }
                // 南门：z = SIZE-1 上的中央 3 格留空
                if (z == SIZE - 1 && x >= 15 && x <= 17) {
                    continue;
                }
                final Block block = ((x + z) % 5 == 0) ? Blocks.mossy_cobblestone : Blocks.stonebrick;
                for (int dy = 1; dy <= 2; dy++) {
                    this.placeBlockAtCurrentPosition(world, block, 0, x, PLATFORM + dy, z, chunkBox);
                }
            }
        }

        // 四角立柱
        final int[][] corners = { { 0, 0 }, { 0, SIZE - 1 }, { SIZE - 1, 0 }, { SIZE - 1, SIZE - 1 } };
        for (final int[] corner : corners) {
            for (int dy = 1; dy <= 4; dy++) {
                this.placeBlockAtCurrentPosition(
                    world,
                    Blocks.cobblestone,
                    0,
                    corner[0],
                    PLATFORM + dy,
                    corner[1],
                    chunkBox);
            }
        }
    }

    // ------------------------------------------------------------------ 墓碑

    private void buildTombstones(World world, StructureBoundingBox chunkBox) {
        final int[] rowsZ = { 8, 16, 24 };
        final int[] columnsX = { 4, 8, 12, 16, 20, 24, 28 };
        final Block[] materials = { Blocks.stone, Blocks.cobblestone, Blocks.mossy_cobblestone, Blocks.stonebrick };
        final int[] metas = { 0, 0, 0, 0, 1, 2, 3 };

        int index = 0;
        for (final int z : rowsZ) {
            for (final int x : columnsX) {
                final Block material = materials[index % materials.length];
                final int meta = metas[index % metas.length];

                // 十字墓碑：竖柱 3 格 + 横臂
                for (int dy = 1; dy <= 3; dy++) {
                    this.placeBlockAtCurrentPosition(world, material, meta, x, PLATFORM + dy, z, chunkBox);
                }
                this.placeBlockAtCurrentPosition(world, material, meta, x - 1, PLATFORM + 2, z, chunkBox);
                this.placeBlockAtCurrentPosition(world, material, meta, x + 1, PLATFORM + 2, z, chunkBox);
                index++;
            }
        }
    }

    // ------------------------------------------------------------------ 刷怪笼

    private void buildSpawnerCages(World world, StructureBoundingBox chunkBox) {
        final int[][] centers = { { 7, 4 }, { 15, 4 }, { 23, 4 } };
        for (int i = 0; i < centers.length; i++) {
            final int centerX = centers[i][0];
            final int centerZ = centers[i][1];

            // 底座
            this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, centerX, PLATFORM + 1, centerZ, chunkBox);
            // 四角石柱
            for (int dx = -1; dx <= 1; dx += 2) {
                for (int dz = -1; dz <= 1; dz += 2) {
                    for (int dy = 1; dy <= 4; dy++) {
                        this.placeBlockAtCurrentPosition(
                            world,
                            Blocks.stonebrick,
                            0,
                            centerX + dx,
                            PLATFORM + dy,
                            centerZ + dz,
                            chunkBox);
                    }
                }
            }
            // 刷怪笼
            this.placeBlockAtCurrentPosition(world, Blocks.mob_spawner, 0, centerX, PLATFORM + 2, centerZ, chunkBox);
            this.placeSpawner(world, chunkBox, centerX, centerZ, DOG_IDS[i]);
        }
    }

    private void placeSpawner(World world, StructureBoundingBox chunkBox, int x, int z, String entityId) {
        final int wx = this.getXWithOffset(x, z);
        final int wy = this.getYWithOffset(PLATFORM + 2);
        final int wz = this.getZWithOffset(x, z);
        if (!chunkBox.isVecInside(wx, wy, wz)) {
            return;
        }
        final TileEntity tile = world.getTileEntity(wx, wy, wz);
        if (tile instanceof TileEntityMobSpawner) {
            ((TileEntityMobSpawner) tile).func_145881_a()
                .setEntityName(entityId);
        }
    }

    // ------------------------------------------------------------------ F8

    private void buildLetterWall(World world, StructureBoundingBox chunkBox) {
        final int z = 30;
        final int topDy = 9;
        this.paintLetter(world, chunkBox, LETTER_F, 5, z, topDy);
        this.paintLetter(world, chunkBox, LETTER_8, 19, z, topDy);
    }

    /** @param pattern 从上到下的像素图 */
    private void paintLetter(World world, StructureBoundingBox chunkBox, String[] pattern, int baseX, int z,
        int topDy) {
        for (int row = 0; row < pattern.length; row++) {
            final int dy = topDy - row;
            final String line = pattern[row];
            for (int column = 0; column < line.length(); column++) {
                if (line.charAt(column) != '#') {
                    continue;
                }
                this.placeBlockAtCurrentPosition(
                    world,
                    Blocks.stonebrick,
                    0,
                    baseX + column,
                    PLATFORM + dy,
                    z,
                    chunkBox);
            }
        }
    }

    // ------------------------------------------------------------------ 箱子

    private void buildChest(World world, Random rand, StructureBoundingBox chunkBox, int x, int z) {
        this.placeBlockAtCurrentPosition(world, Blocks.chest, 0, x, PLATFORM + 1, z, chunkBox);

        final int wx = this.getXWithOffset(x, z);
        final int wy = this.getYWithOffset(PLATFORM + 1);
        final int wz = this.getZWithOffset(x, z);
        if (!chunkBox.isVecInside(wx, wy, wz)) {
            return;
        }

        final TileEntity tile = world.getTileEntity(wx, wy, wz);
        if (!(tile instanceof TileEntityChest)) {
            return;
        }
        final TileEntityChest chest = (TileEntityChest) tile;

        // 几率开出星系 T1-T3 火箭
        if (rand.nextInt(100) < 55) {
            final WeightedRandomChestContent[] loot = rocketLoot();
            if (loot.length > 0) {
                WeightedRandomChestContent.generateChestContents(rand, loot, chest, 1);
            }
        }

        // 固定 8 枚 GT 白铜币（格雷币），放进第一个空槽
        for (int slot = 0; slot < chest.getSizeInventory(); slot++) {
            if (chest.getStackInSlot(slot) == null) {
                chest.setInventorySlotContents(slot, ItemList.Credit_Greg_Cupronickel.get(8));
                break;
            }
        }
    }

    /** 懒加载：GT / 星系物品要等 preInit 之后才存在，不能在静态初始化里取。 */
    private static WeightedRandomChestContent[] rocketLoot() {
        if (rocketLoot == null) {
            final List<WeightedRandomChestContent> loot = new ArrayList<WeightedRandomChestContent>();
            if (GCItems.rocketTier1 != null) {
                loot.add(new WeightedRandomChestContent(new ItemStack(GCItems.rocketTier1), 1, 1, 20)); // T1
            }
            if (MarsItems.spaceship != null) {
                loot.add(new WeightedRandomChestContent(new ItemStack(MarsItems.spaceship), 1, 1, 12)); // T2
            }
            if (AsteroidsItems.tier3Rocket != null) {
                loot.add(new WeightedRandomChestContent(new ItemStack(AsteroidsItems.tier3Rocket), 1, 1, 6)); // T3
            }
            rocketLoot = loot.toArray(new WeightedRandomChestContent[0]);
        }
        return rocketLoot;
    }
}
