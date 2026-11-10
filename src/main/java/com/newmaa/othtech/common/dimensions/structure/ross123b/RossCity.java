package com.newmaa.othtech.common.dimensions.structure.ross123b;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import com.newmaa.othtech.common.blocks.antimonia.AntimoniaBlocks;

import cpw.mods.fml.common.registry.GameRegistry;

public final class RossCity {

    /** 主干道间隔(区块), 即每 128 格一条 16 格宽的路。 */
    private static final int PERIOD = 8;
    /** 每个街区里每条支路出现的概率(百分比)。 */
    private static final int BRANCH_CHANCE = 62;
    /** 临街地块留空(只有残骸)的概率(百分比)。 */
    private static final int EMPTY_LOT_CHANCE = 8;
    /** 建筑占地最小边长, 以及尺寸跨度: 边长 = 9..14, 四周至少留 1 格。 */
    private static final int MIN_FOOTPRINT = 9;
    private static final int FOOTPRINT_SPAN = 6;
    /** 楼层高度。 */
    private static final int FLOOR_HEIGHT = 4;
    /** 地下部分最低的一层楼板: 紧贴基岩(y = 0, 1)之上。 */
    private static final int BASEMENT_BOTTOM = 2;
    /** 地下室保留水的概率(百分比)。 */
    private static final int WET_BASEMENT_CHANCE = 55;

    /** Large-scale continents and a smaller coastal layer give the LCL sea long, irregular shorelines. */
    private static final int NOISE_CONTINENT = 3200;
    private static final int NOISE_COAST = 1200;
    private static final double SHORELINE = 0.54;
    private static final int NOISE_PLAIN = 110;
    private static final int NOISE_RIDGE = 290;
    private static final int NOISE_RIDGE_DETAIL = 95;
    private static final int NOISE_MOUNTAIN_GATE = 750;
    /** Keep the headquarters at sea level without forcing a sharp circular island boundary. */
    private static final int HQ_FLAT_RADIUS = 265;
    private static final int HQ_BLEND_RADIUS = 540;
    private static final int NOISE_RIVER = 1300;
    private static final int NOISE_RIVER_WARP = 390;
    private static final int NOISE_RIFT = 1700;
    private static final int CAVE_CELL = 12;
    private static final int CAVE_CHANCE = 48;
    private static final int CAVE_ENTRANCE_CHANCE = 22;
    private static final int TERRAIN_MIN = BASEMENT_BOTTOM + 4;
    private static final int TERRAIN_MAX = 226;

    private static final int CITY_CELL = 32;
    private static final int CITY_RADIUS_MIN = 5;
    private static final int CITY_RADIUS_SPAN = 6;
    /** 平坦度采样间距(格)、允许的高度偏差(格)、以及必须落在偏差内的采样点比例(百分比)。 */
    private static final int CITY_SAMPLE_STEP = 8;
    private static final int CITY_FLAT_TOLERANCE = 4;
    private static final int CITY_FLAT_PERCENT = 88;
    /** 城市海拔必须贴着海面(否则楼会被埋进山里或被淹得看不见)。 */
    private static final int CITY_HEIGHT_BAND = 5;
    private static final int HILL_CITY_CHANCE = 18;
    private static final int HILL_CITY_MAX_RISE = 42;
    private static final int VILLAGE_CELL = 21;
    private static final int VILLAGE_CHANCE = 24;

    /** 桥: 跨度下限(格) / 架桥概率(%) / 桥面离水高度 / 沿路找岸的最远距离。 */
    private static final int BRIDGE_MIN_SPAN = 16;
    private static final int BRIDGE_CHANCE = 70;
    private static final int BRIDGE_CLEARANCE = 2;
    private static final int BRIDGE_MAX_SCAN = 192;

    /** 车道中线所在的区块内坐标(路宽 16, 中线在第 8 格)。 */
    private static final int LANE = 8;
    /** 路面/标线/桥体材质: 石头铺面, 石英画线, 石砖做桥栏与桥墩。 */
    private static final Block ROAD_BLOCK = Blocks.stone;
    private static final Block LANE_BLOCK = Blocks.quartz_block;
    private static final Block BRIDGE_BLOCK = Blocks.stonebrick;

    /** 地表电线杆: 沿路每 POLE_SPACING 格一根, 立在路侧; 部分歪斜(末日感)。 */
    private static final int POLE_SPACING = 16;
    private static final int POLE_LATERAL = 4;
    private static final int POLE_MIN_HEIGHT = 7;
    private static final int POLE_HEIGHT_SPAN = 5;
    private static final int POLE_LEAN_CHANCE = 55;
    /** 杆体/横担用 IC2 的铁栅栏(没装 IC2 就退回铁栏杆), 杆顶一盏亮着的红石灯当路灯。 */
    private static final Block POLE_BLOCK = resolvePoleFence();
    private static final Block POLE_LAMP = Blocks.lit_redstone_lamp;
    private static final Block POLE_INSULATOR = Blocks.quartz_block;

    /** 巨型倾斜十字架: 每 CROSS_CELL 个区块一个候选点 / 出现概率(%) / 高度范围 / 倾斜坡度(每几格偏 1 格)。 */
    private static final int CROSS_CELL = 18;
    private static final int CROSS_CHANCE = 45;
    private static final int CROSS_MIN_HEIGHT = 110;
    private static final int CROSS_HEIGHT_SPAN = 60;
    private static final int CROSS_SLOPE = 9;
    /** 横臂中心高度(占全高百分比)与半长(占全高十分之几): 又高又短。 */
    private static final int CROSS_BEAM_HEIGHT = 78;
    private static final int CROSS_BEAM_HALF = 2;
    /** 十字架最高不能顶穿世界高度。 */
    private static final int CROSS_TOP_LIMIT = 250;
    /** 十字架主体: Chisel 的 redstone_block, meta 1 = "smooth"(光滑红石块); 没装 Chisel 就退回原版红石块。 */
    private static final int CROSS_BODY_META = 1;
    private static final BlockMeta CROSS_BODY = resolveCrossBody();
    private static final BlockMeta CROSS_CORE = new BlockMeta(Blocks.glowstone, 0);

    private static final int KIND_RUIN = 1;
    private static final int KIND_BLOCK = 2;
    private static final int KIND_TOWER = 3;

    /** 建筑墙面里掺 chisel 红石块(与巨型十字架同款)的比例(百分比), 以 2×2×2 的块团分布。 */
    private static final int REDSTONE_VEIN_PERCENT = 10;

    /** 荒野地物: 枯树出现概率(%) / 枯草密度(0..255) / 墓碑、弹坑、天使骸骨的出现概率(%)。 */
    private static final int TREE_CHANCE = 22;
    private static final int BUSH_DENSITY = 2;
    private static final int GRAVE_CHANCE = 9;
    private static final int CRATER_CHANCE = 7;
    private static final int BONES_CELL = 7;
    private static final int BONES_CHANCE = 24;
    /** 城内空地上的枯树概率(%): 比荒野稀一些。 */
    private static final int LOT_TREE_CHANCE = 30;

    private RossCity() {}

    /** 地形与液体材质(由区块生成器提供, 复用该维度自己的锑石/锑土/锑草)。 */
    public static final class Terrain {

        public final Block water;
        public final byte waterMeta;
        public final Block stone;
        public final byte stoneMeta;
        public final Block soil;
        public final byte soilMeta;
        public final Block grass;
        public final byte grassMeta;
        public final int seaLevel;

        public Terrain(Block water, byte waterMeta, Block stone, byte stoneMeta, Block soil, byte soilMeta, Block grass,
            byte grassMeta, int seaLevel) {
            this.water = water;
            this.waterMeta = waterMeta;
            this.stone = stone;
            this.stoneMeta = stoneMeta;
            this.soil = soil;
            this.soilMeta = soilMeta;
            this.grass = grass;
            this.grassMeta = grassMeta;
            this.seaLevel = seaLevel;
        }
    }

    /**
     * 把该区块的地形、道路/桥、建筑写进方块数组。
     *
     * @param blocks   长度为 16*16*256 的方块数组, 下标 {@code (x * 16 + z) * 256 + y}
     * @param metadata 与 {@code blocks} 等长的元数据数组
     * @param seed     世界种子, 保证同一世界每次生成的布局一致
     * @param terrain  地形/液体材质与海平面
     */
    public static void stamp(Block[] blocks, byte[] metadata, int chunkX, int chunkZ, long seed, Terrain terrain) {
        Stamper stamper = new Stamper(blocks, metadata, chunkX, chunkZ, seed);
        fillTerrain(stamper, chunkX, chunkZ, terrain);
        if (!NervHeadquarters.intersects(chunkX, chunkZ)) {
            stampCaves(stamper, chunkX, chunkZ, terrain);
        }
        if (NervHeadquarters.intersects(chunkX, chunkZ)) {
            NervHeadquarters.stamp(blocks, metadata, chunkX, chunkZ, seed);
            return;
        }

        int mask = roadMask(chunkX, chunkZ, seed, terrain.seaLevel);
        if (mask != 0) {
            stampRoad(stamper, chunkX, chunkZ, mask, terrain);
        } else if (isPlotChunk(chunkX, chunkZ, seed, terrain.seaLevel)) {
            stampPlot(stamper, chunkX, chunkZ, terrain);
        } else {
            stampWilderness(stamper, chunkX, chunkZ, terrain);
        }

        // 地标最后盖上去: 十字架会压过道路/建筑, 像第三次冲击后插在城市上的墓碑
        stampCrosses(stamper, chunkX, chunkZ, terrain);
        stampDeadTrees(stamper, chunkX, chunkZ, terrain, TREE_CHANCE);
        stampAngelBones(stamper, chunkX, chunkZ, terrain);
        stampVillage(stamper, chunkX, chunkZ, terrain);
    }

    /** 临街地块: 一栋楼, 或者只剩残骸的空地。 */
    private static void stampPlot(Stamper stamper, int chunkX, int chunkZ, Terrain terrain) {
        int baseY = terrain.seaLevel + 1;
        for (int dx = 2; dx <= 14; dx += 6) {
            for (int dz = 2; dz <= 14; dz += 6) {
                baseY = Math.max(
                    baseY,
                    terrainHeight(stamper.seed, terrain.seaLevel, (chunkX << 4) + dx, (chunkZ << 4) + dz) + 1);
            }
        }
        Building building = plan(chunkX, chunkZ, baseY, stamper.seed);
        if (building == null) {
            stampDebris(stamper, chunkX, chunkZ, terrain, 36);
            stampDeadTrees(stamper, chunkX, chunkZ, terrain, LOT_TREE_CHANCE);
            return;
        }

        stampBasement(stamper, building, terrain);
        if (building.kind == KIND_RUIN) {
            stampRuin(stamper, building);
        } else {
            stampBuilding(stamper, building);
        }
        stampDebris(stamper, chunkX, chunkZ, terrain, 12);
        if (building.kind != KIND_RUIN && rnd(stamper.seed, chunkX, chunkZ, 558) % 100 < 24) {
            int chestX = (chunkX << 4) + building.x0 + 2;
            int chestZ = (chunkZ << 4) + building.z0 + 2;
            for (int y = building.baseY + 1; y <= building.baseY + 3; y++) {
                stamper.clear(chestX, y, chestZ);
            }
            stamper.put(chestX, building.baseY + 1, chestZ, Blocks.chest, 0);
        }
    }

    /** 一座城: 中心的区块坐标与半径(区块), 以及它是哪张网格上的候选点(用于校验缓存)。 */
    private static final class City {

        final long seed;
        final int cellX;
        final int cellZ;
        final int centerChunkX;
        final int centerChunkZ;
        final int radius;

        City(long seed, int cellX, int cellZ, int centerChunkX, int centerChunkZ, int radius) {
            this.seed = seed;
            this.cellX = cellX;
            this.cellZ = cellZ;
            this.centerChunkX = centerChunkX;
            this.centerChunkZ = centerChunkZ;
            this.radius = radius;
        }

        /** 区块是否落在城内(按区块的方形范围, 正好对齐路网)。 */
        boolean contains(int chunkX, int chunkZ) {
            return Math.abs(chunkX - this.centerChunkX) <= this.radius
                && Math.abs(chunkZ - this.centerChunkZ) <= this.radius;
        }
    }

    /** 平坦度采样与判定结果都很贵, 按候选格缓存; 没有城市的格子也缓存(用 radius 0 表示)。 */
    private static final Map<Long, City> CITY_CACHE = new ConcurrentHashMap<>();

    /** 该区块是否落在某座城里(会顺带检查周围 3×3 个候选格, 因为城可能跨格)。 */
    static boolean isCityChunk(int chunkX, int chunkZ, long seed, int seaLevel) {
        int cellX = Math.floorDiv(chunkX, CITY_CELL);
        int cellZ = Math.floorDiv(chunkZ, CITY_CELL);
        for (int offsetX = -1; offsetX <= 1; offsetX++) {
            for (int offsetZ = -1; offsetZ <= 1; offsetZ++) {
                City city = cityFor(seed, seaLevel, cellX + offsetX, cellZ + offsetZ);
                if (city != null && city.contains(chunkX, chunkZ)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static City cityFor(long seed, int seaLevel, int cellX, int cellZ) {
        long key = mix(seed, cellX, cellZ, 901);
        City cached = CITY_CACHE.get(key);
        if (cached != null && cached.seed == seed && cached.cellX == cellX && cached.cellZ == cellZ) {
            return cached.radius == 0 ? null : cached;
        }
        City city = planCity(seed, seaLevel, cellX, cellZ);
        if (CITY_CACHE.size() > 8192) {
            CITY_CACHE.clear(); // 走得太远就清一次, 反正重建只是花点时间
        }
        CITY_CACHE.put(key, city == null ? new City(seed, cellX, cellZ, 0, 0, 0) : city);
        return city;
    }

    private static City planCity(long seed, int seaLevel, int cellX, int cellZ) {
        int radius = CITY_RADIUS_MIN + rnd(seed, cellX, cellZ, 902) % CITY_RADIUS_SPAN;
        int margin = radius + 2;
        int slack = CITY_CELL - 2 * margin;
        int centerChunkX = cellX * CITY_CELL + margin + (slack > 0 ? rnd(seed, cellX, cellZ, 903) % slack : 0);
        int centerChunkZ = cellZ * CITY_CELL + margin + (slack > 0 ? rnd(seed, cellX, cellZ, 904) % slack : 0);

        int span = ((radius * 2 + 1) << 4) - 1;
        int samples = span / CITY_SAMPLE_STEP + 1;
        int[] heights = new int[samples * samples];
        int baseX = (centerChunkX - radius) << 4;
        int baseZ = (centerChunkZ - radius) << 4;
        int count = 0;
        for (int i = 0; i < samples; i++) {
            for (int j = 0; j < samples; j++) {
                heights[count++] = terrainHeight(
                    seed,
                    seaLevel,
                    baseX + i * CITY_SAMPLE_STEP,
                    baseZ + j * CITY_SAMPLE_STEP);
            }
        }
        Arrays.sort(heights);
        int median = heights[count / 2];
        boolean hill = median > seaLevel + CITY_HEIGHT_BAND;
        if (median < seaLevel - CITY_HEIGHT_BAND || hill
            && (median > seaLevel + HILL_CITY_MAX_RISE || rnd(seed, cellX, cellZ, 905) % 100 >= HILL_CITY_CHANCE))
            return null;

        int within = 0;
        for (int i = 0; i < count; i++) {
            if (Math.abs(heights[i] - median) <= (hill ? 15 : CITY_FLAT_TOLERANCE)) {
                within++;
            }
        }
        if (within * 100 < count * CITY_FLAT_PERCENT) {
            return null;
        }
        return new City(seed, cellX, cellZ, centerChunkX, centerChunkZ, radius);
    }

    static int roadMask(int chunkX, int chunkZ, long seed, int seaLevel) {
        if (!isCityChunk(chunkX, chunkZ, seed, seaLevel)) {
            return 0;
        }
        int localX = Math.floorMod(chunkX, PERIOD);
        int localZ = Math.floorMod(chunkZ, PERIOD);
        int mask = 0;
        if (localX == 0) {
            mask |= 1;
        }
        if (localZ == 0) {
            mask |= 2;
        }
        if (mask == 3) {
            return 3;
        }

        int blockX = Math.floorDiv(chunkX, PERIOD);
        int blockZ = Math.floorDiv(chunkZ, PERIOD);
        if ((mask & 1) == 0 && isVerticalSpur(seed, seaLevel, blockX, blockZ, localX, localZ)) {
            mask |= 1;
        }
        if ((mask & 2) == 0 && isHorizontalSpur(seed, seaLevel, blockX, blockZ, localX, localZ)) {
            mask |= 2;
        }
        return mask;
    }

    /** 该区块是不是道路(主干道或支路)。 */
    static boolean isRoadChunk(int chunkX, int chunkZ, long seed, int seaLevel) {
        return roadMask(chunkX, chunkZ, seed, seaLevel) != 0;
    }

    /** 横向支路: 从街区左/右侧的主干道伸进来, 到某个随机的行、随机长度为止(断头)。 */
    private static boolean isHorizontalSpur(long seed, int seaLevel, int blockX, int blockZ, int localX, int localZ) {
        int hash = rnd(seed, blockX, blockZ, 101);
        if (hash % 100 >= BRANCH_CHANCE) {
            return false;
        }
        int row = 2 + (hash >>> 7) % 5;
        if (localZ != row) {
            return false;
        }
        int length = 2 + (hash >>> 11) % 5;
        boolean fromLeft = ((hash >>> 16) & 1) == 0;
        if (fromLeft ? localX > length : localX < PERIOD - length) {
            return false;
        }
        // 支路必须接在城内那根主干道上, 否则城市边界会把支路切成一截孤零零的路
        int anchorX = blockX * PERIOD + (fromLeft ? 0 : PERIOD);
        return isCityChunk(anchorX, blockZ * PERIOD + localZ, seed, seaLevel);
    }

    /** 纵向支路, 规则同上。 */
    private static boolean isVerticalSpur(long seed, int seaLevel, int blockX, int blockZ, int localX, int localZ) {
        int hash = rnd(seed, blockX, blockZ, 202);
        if (hash % 100 >= BRANCH_CHANCE) {
            return false;
        }
        int column = 2 + (hash >>> 7) % 5;
        if (localX != column) {
            return false;
        }
        int length = 2 + (hash >>> 11) % 5;
        boolean fromTop = ((hash >>> 16) & 1) == 0;
        if (fromTop ? localZ > length : localZ < PERIOD - length) {
            return false;
        }
        int anchorZ = blockZ * PERIOD + (fromTop ? 0 : PERIOD);
        return isCityChunk(blockX * PERIOD + localX, anchorZ, seed, seaLevel);
    }

    /** 临街地块: 城内、自身不是道路, 且上下左右至少有一个邻区块是道路。 */
    static boolean isPlotChunk(int chunkX, int chunkZ, long seed, int seaLevel) {
        if (!isCityChunk(chunkX, chunkZ, seed, seaLevel)) {
            return false;
        }
        if (isRoadChunk(chunkX, chunkZ, seed, seaLevel)) {
            return false;
        }
        return isRoadChunk(chunkX - 1, chunkZ, seed, seaLevel) || isRoadChunk(chunkX + 1, chunkZ, seed, seaLevel)
            || isRoadChunk(chunkX, chunkZ - 1, seed, seaLevel)
            || isRoadChunk(chunkX, chunkZ + 1, seed, seaLevel);
    }

    /** Broad continental noise controls LCL seas; elevation and ridges rise gradually inland. */
    static int terrainHeight(long seed, int seaLevel, int x, int z) {
        double continent = octave(seed, x, z, NOISE_CONTINENT, 601) * 0.78
            + octave(seed, x, z, NOISE_COAST, 602) * 0.22;
        double inland = Math.max(0.0, (continent - SHORELINE) / (1.0 - SHORELINE));
        double deepWater = Math.max(0.0, (SHORELINE - continent) / SHORELINE);
        double relief = smoothstep(clamp01((inland - 0.10) / 0.55));
        double mountainZone = smoothstep(clamp01((inland - 0.13) / 0.32))
            * smoothstep(clamp01((octave(seed, x, z, NOISE_MOUNTAIN_GATE, 604) - 0.24) / 0.23));
        double ridge = smoothRidge(octave(seed, x, z, NOISE_RIDGE, 605)) * 0.62
            + smoothRidge(octave(seed, x, z, NOISE_RIDGE_DETAIL, 606)) * 0.38;
        double plain = (octave(seed, x, z, NOISE_PLAIN, 607) - 0.5) * 3.0;
        double height = seaLevel + 1
            + inland * 18
            + relief * (8 + ridge * 19)
            + mountainZone * (18 + ridge * ridge * 105 + smoothRidge(octave(seed, x, z, 55, 611)) * 11)
            - deepWater * (12 + deepWater * 28)
            + plain * (0.35 + relief * 1.65);

        double river = riverStrength(seed, x, z);
        height -= river * (Math.max(0, height - seaLevel) + 4);

        double riftDistance = Math.abs(octave(seed, x, z, NOISE_RIFT, 610) - 0.5);
        double rift = 1.0 - smoothstep(clamp01((riftDistance - 0.005) / 0.025));
        height -= rift * rift * 45 * smoothstep(clamp01((inland - 0.05) / 0.18));

        double distance = Math.hypot((double) x, (double) z);
        double hqBlend = smoothstep(clamp01((distance - HQ_FLAT_RADIUS) / (HQ_BLEND_RADIUS - HQ_FLAT_RADIUS)));
        height = (seaLevel + 1) * (1.0 - hqBlend) + height * hqBlend;
        return Math.max(TERRAIN_MIN, Math.min(TERRAIN_MAX, (int) Math.round(height)));
    }

    private static double smoothstep(double value) {
        return value * value * (3.0 - 2.0 * value);
    }

    private static double clamp01(double value) {
        if (value <= 0.0) {
            return 0.0;
        }
        return value >= 1.0 ? 1.0 : value;
    }

    /** 把 0..1 的噪声折成"0.5 处最高"的三角波, 再用 smoothstep 抹掉尖角, 得到平滑的山脊线。 */
    private static double smoothRidge(double value) {
        double ridge = 1.0 - Math.abs(2.0 * value - 1.0);
        return ridge * ridge * (3.0 - 2.0 * ridge);
    }

    private static double octave(long seed, int x, int z, int scale, int salt) {
        int gridX = Math.floorDiv(x, scale);
        int gridZ = Math.floorDiv(z, scale);
        double fracX = (x - gridX * scale) / (double) scale;
        double fracZ = (z - gridZ * scale) / (double) scale;
        double smoothX = fracX * fracX * (3.0 - 2.0 * fracX);
        double smoothZ = fracZ * fracZ * (3.0 - 2.0 * fracZ);
        double v00 = lattice(seed, gridX, gridZ, salt);
        double v10 = lattice(seed, gridX + 1, gridZ, salt);
        double v01 = lattice(seed, gridX, gridZ + 1, salt);
        double v11 = lattice(seed, gridX + 1, gridZ + 1, salt);
        double a = v00 + (v10 - v00) * smoothX;
        double b = v01 + (v11 - v01) * smoothX;
        return a + (b - a) * smoothZ;
    }

    private static double lattice(long seed, int gridX, int gridZ, int salt) {
        return (mix(seed, gridX, gridZ, salt) >>> 11) * 0x1.0p-53;
    }

    /** 铺地形: 石头/泥土/草皮, 海平面以下灌上 LCL。 */
    private static void fillTerrain(Stamper stamper, int chunkX, int chunkZ, Terrain terrain) {
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = baseX + x;
                int worldZ = baseZ + z;
                int height = terrainHeight(stamper.seed, terrain.seaLevel, worldX, worldZ);
                for (int y = BASEMENT_BOTTOM; y < height - 1; y++) {
                    stamper.put(worldX, y, worldZ, terrain.stone, terrain.stoneMeta);
                }
                boolean beach = height >= terrain.seaLevel - 2 && height <= terrain.seaLevel + 3
                    && hasLclShore(stamper.seed, terrain.seaLevel, worldX, worldZ);
                if (beach) {
                    for (int y = Math.max(BASEMENT_BOTTOM, height - 3); y <= height; y++) {
                        stamper.put(worldX, y, worldZ, AntimoniaBlocks.antimoniaBlockSand, 0);
                    }
                } else {
                    stamper.put(worldX, height - 1, worldZ, terrain.soil, terrain.soilMeta);
                    boolean land = height >= terrain.seaLevel;
                    stamper.put(
                        worldX,
                        height,
                        worldZ,
                        land ? terrain.grass : terrain.soil,
                        land ? terrain.grassMeta : terrain.soilMeta);
                }
                for (int y = height + 1; y <= terrain.seaLevel; y++) {
                    stamper.put(worldX, y, worldZ, terrain.water, terrain.waterMeta);
                }
            }
        }
    }

    /** Large chambers are planned per cell, then each chunk carves only its own columns. */
    private static void stampCaves(Stamper stamper, int chunkX, int chunkZ, Terrain terrain) {
        int cellX = Math.floorDiv(chunkX, CAVE_CELL);
        int cellZ = Math.floorDiv(chunkZ, CAVE_CELL);
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;
        for (int cx = cellX - 1; cx <= cellX + 1; cx++) {
            for (int cz = cellZ - 1; cz <= cellZ + 1; cz++) {
                if (rnd(stamper.seed, cx, cz, 1500) % 100 >= CAVE_CHANCE) continue;
                int centerX = (cx * CAVE_CELL + 2 + rnd(stamper.seed, cx, cz, 1501) % (CAVE_CELL - 4)) * 16;
                int centerZ = (cz * CAVE_CELL + 2 + rnd(stamper.seed, cx, cz, 1502) % (CAVE_CELL - 4)) * 16;
                int radiusX = 38 + rnd(stamper.seed, cx, cz, 1503) % 28;
                int radiusZ = 40 + rnd(stamper.seed, cx, cz, 1504) % 28;
                if (centerX + radiusX < baseX || centerX - radiusX > baseX + 15
                    || centerZ + radiusZ < baseZ
                    || centerZ - radiusZ > baseZ + 15) continue;
                int centerY = 27 + rnd(stamper.seed, cx, cz, 1505) % 15;
                int radiusY = 15 + rnd(stamper.seed, cx, cz, 1506) % 10;
                boolean entrance = rnd(stamper.seed, cx, cz, 1507) % 100 < CAVE_ENTRANCE_CHANCE
                    && terrainHeight(stamper.seed, terrain.seaLevel, centerX, centerZ) > terrain.seaLevel + 17;
                if (entrance) {
                    for (int offsetX = -12; offsetX <= 12; offsetX += 12) {
                        for (int offsetZ = -12; offsetZ <= 12; offsetZ += 12) {
                            if (terrainHeight(stamper.seed, terrain.seaLevel, centerX + offsetX, centerZ + offsetZ)
                                <= terrain.seaLevel + 9) entrance = false;
                        }
                    }
                }
                for (int x = Math.max(baseX, centerX - radiusX); x <= Math.min(baseX + 15, centerX + radiusX); x++) {
                    for (int z = Math.max(baseZ, centerZ - radiusZ); z
                        <= Math.min(baseZ + 15, centerZ + radiusZ); z++) {
                        double dx = (x - centerX) / (double) radiusX;
                        double dz = (z - centerZ) / (double) radiusZ;
                        double horizontal = dx * dx + dz * dz;
                        int surface = terrainHeight(stamper.seed, terrain.seaLevel, x, z);
                        double variation = (noise(stamper.seed, x / 5, z / 5, 1508) - 128) / 900.0;
                        double room = 1.0 - horizontal + variation;
                        if (room > 0) {
                            int halfHeight = (int) Math.floor(radiusY * Math.sqrt(room));
                            int bottom = Math.max(BASEMENT_BOTTOM + 2, centerY - halfHeight);
                            int top = Math.min(surface - 6, centerY + halfHeight);
                            for (int y = bottom; y <= top; y++) stamper.clear(x, y, z);
                        }
                        double shaft = Math.pow((x - centerX) / 6.0, 2) + Math.pow((z - centerZ) / 6.0, 2);
                        if (entrance && shaft < 1 && surface > terrain.seaLevel + 8) {
                            int openingBottom = Math.max(BASEMENT_BOTTOM + 3, centerY + radiusY / 2);
                            for (int y = openingBottom; y <= surface + 2; y++) stamper.clear(x, y, z);
                        }
                    }
                }
            }
        }
    }

    private static boolean hasLclShore(long seed, int seaLevel, int x, int z) {
        int distance = 16;
        return terrainHeight(seed, seaLevel, x - distance, z) < seaLevel
            || terrainHeight(seed, seaLevel, x + distance, z) < seaLevel
            || terrainHeight(seed, seaLevel, x, z - distance) < seaLevel
            || terrainHeight(seed, seaLevel, x, z + distance) < seaLevel;
    }

    /** Uses the same height and shoreline rules as terrain filling. */
    public static int biomeKind(long seed, int seaLevel, int x, int z) {
        int height = terrainHeight(seed, seaLevel, x, z);
        if (height >= seaLevel - 2 && height <= seaLevel + 3 && hasLclShore(seed, seaLevel, x, z)) return 1;
        double continent = octave(seed, x, z, NOISE_CONTINENT, 601) * 0.78
            + octave(seed, x, z, NOISE_COAST, 602) * 0.22;
        double inland = Math.max(0.0, (continent - SHORELINE) / (1.0 - SHORELINE));
        double river = riverStrength(seed, x, z);
        if (height < seaLevel) return inland > 0.02 && river > 0.12 ? 2 : 0;
        if (river > 0.4 && inland > 0.02 && height <= seaLevel + 5) return 2;
        if (height >= seaLevel + 58) return 5;
        if (height >= seaLevel + 20) return 4;
        return 3;
    }

    private static double riverStrength(long seed, int x, int z) {
        double warp = (octave(seed, x, z, NOISE_RIVER_WARP, 608) - 0.5) * 100;
        double distance = Math.abs(octave(seed, x + (int) warp, z - (int) warp, NOISE_RIVER, 609) - 0.5);
        return 1.0 - smoothstep(clamp01((distance - 0.008) / 0.035));
    }

    /** 把路面(或桥面)铺在地形表面上: 陆地就是地面, 水里就是海床。 */
    private static void stampRoad(Stamper stamper, int chunkX, int chunkZ, int mask, Terrain terrain) {
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;
        int seaLevel = terrain.seaLevel;
        boolean alongZ = (mask & 1) != 0;
        boolean alongX = (mask & 2) != 0;
        Bridge bridge = bridgeFor(stamper.seed, terrain, chunkX, chunkZ, mask);

        for (int x = 0; x < 16; x++) {
            int worldX = baseX + x;
            for (int z = 0; z < 16; z++) {
                int worldZ = baseZ + z;
                int surface = bridge != null ? bridge.surfaceAt(alongZ ? worldZ : worldX, seaLevel)
                    : terrainHeight(stamper.seed, seaLevel, worldX, worldZ);
                if (bridge == null && noise(stamper.seed, worldX, worldZ, 11) < 8) {
                    continue; // 坑洞: 路面消失, 露出地面/海床
                }
                boolean lane = (alongX && z == LANE) || (alongZ && x == LANE);
                boolean crossing = mask == 3 && (x == 3 || x == 12) && z >= 3 && z <= 12;
                boolean edge = (alongX && (z == 0 || z == 15)) || (alongZ && (x == 0 || x == 15));
                int wear = noise(stamper.seed, worldX, worldZ, 12);
                Block pavement = lane || crossing ? LANE_BLOCK
                    : edge ? Blocks.stonebrick
                        : wear < 30 ? Blocks.stonebrick : wear < 42 ? Blocks.cobblestone : ROAD_BLOCK;
                stamper.put(worldX, surface, worldZ, pavement, pavement == Blocks.stonebrick && wear < 12 ? 2 : 0);
                if (bridge != null) {
                    stamper.put(worldX, surface - 1, worldZ, ROAD_BLOCK, 0); // 桥面板厚度
                }
            }
        }

        if (bridge != null) {
            stampBridgeSupports(stamper, chunkX, chunkZ, alongZ, bridge, terrain);
        } else {
            stampPoles(stamper, chunkX, chunkZ, mask, terrain);
            stampVehicle(stamper, chunkX, chunkZ, mask, terrain);
        }
    }

    private static Bridge bridgeFor(long seed, Terrain terrain, int chunkX, int chunkZ, int mask) {
        if (mask != 1 && mask != 2) {
            return null;
        }
        boolean alongZ = mask == 1;
        int seaLevel = terrain.seaLevel;
        int laneX = (chunkX << 4) + LANE;
        int laneZ = (chunkZ << 4) + LANE;
        int origin = alongZ ? laneZ : laneX;

        int left = scanShore(seed, seaLevel, alongZ, laneX, laneZ, origin, -1);
        int right = scanShore(seed, seaLevel, alongZ, laneX, laneZ, origin, 1);
        if (left < 0 || right < 0 || right - left < BRIDGE_MIN_SPAN) {
            return null;
        }
        if (containsJunction(seed, seaLevel, chunkX, chunkZ, alongZ, left, right)) {
            return null;
        }
        if (rnd(seed, left, alongZ ? 1 : 0, 611) % 100 >= BRIDGE_CHANCE) {
            return null;
        }
        return new Bridge(
            left,
            right,
            sampleHeight(seed, seaLevel, alongZ, laneX, laneZ, left),
            sampleHeight(seed, seaLevel, alongZ, laneX, laneZ, right));
    }

    /** 沿道路轴向 direction 方向找第一块陆地(地形不低于海面)。 */
    private static int scanShore(long seed, int seaLevel, boolean alongZ, int laneX, int laneZ, int origin,
        int direction) {
        for (int distance = 0; distance <= BRIDGE_MAX_SCAN; distance++) {
            int pos = origin + distance * direction;
            if (sampleHeight(seed, seaLevel, alongZ, laneX, laneZ, pos) >= seaLevel) {
                return pos;
            }
        }
        return -1;
    }

    private static int sampleHeight(long seed, int seaLevel, boolean alongZ, int laneX, int laneZ, int pos) {
        return alongZ ? terrainHeight(seed, seaLevel, laneX, pos) : terrainHeight(seed, seaLevel, pos, laneZ);
    }

    private static boolean containsJunction(long seed, int seaLevel, int chunkX, int chunkZ, boolean alongZ, int left,
        int right) {
        int from = Math.floorDiv(Math.min(left, right), 16);
        int to = Math.floorDiv(Math.max(left, right), 16);
        for (int chunk = from; chunk <= to; chunk++) {
            if (roadMask(alongZ ? chunkX : chunk, alongZ ? chunk : chunkZ, seed, seaLevel) == 3) {
                return true;
            }
        }
        return false;
    }

    /** 桥栏 + 桥墩。 */
    private static void stampBridgeSupports(Stamper stamper, int chunkX, int chunkZ, boolean alongZ, Bridge bridge,
        Terrain terrain) {
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;
        int seaLevel = terrain.seaLevel;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = baseX + x;
                int worldZ = baseZ + z;
                int lateral = alongZ ? x : z;
                int along = alongZ ? worldZ : worldX;
                int surface = bridge.surfaceAt(along, seaLevel);
                if (lateral == 0 || lateral == 15) {
                    stamper.put(worldX, surface + 1, worldZ, BRIDGE_BLOCK, 0);
                }
                if ((along & 3) == 0 && (lateral == 2 || lateral == 13)) {
                    int ground = terrainHeight(stamper.seed, seaLevel, worldX, worldZ);
                    for (int y = ground + 1; y < surface; y++) {
                        stamper.put(worldX, y, worldZ, BRIDGE_BLOCK, 0);
                    }
                }
            }
        }
    }

    /** 一段跨水的桥: 两端落在岸上, 桥面按正弦拱起, 保证中间高过水面。 */
    private static final class Bridge {

        final int left;
        final int right;
        final int leftHeight;
        final int rightHeight;

        Bridge(int left, int right, int leftHeight, int rightHeight) {
            this.left = left;
            this.right = right;
            this.leftHeight = leftHeight;
            this.rightHeight = rightHeight;
        }

        /** 沿桥轴方向 pos 处的桥面高度。 */
        int surfaceAt(int pos, int seaLevel) {
            double t = (pos - this.left) / (double) (this.right - this.left);
            if (t < 0.0) {
                t = 0.0;
            } else if (t > 1.0) {
                t = 1.0;
            }
            double height = this.leftHeight + (this.rightHeight - this.leftHeight) * t;
            int lift = Math.max(0, seaLevel + BRIDGE_CLEARANCE - Math.min(this.leftHeight, this.rightHeight));
            return (int) Math.round(height + lift * Math.sin(Math.PI * t));
        }
    }

    private static void stampPoles(Stamper stamper, int chunkX, int chunkZ, int mask, Terrain terrain) {
        if (mask != 1 && mask != 2) {
            return; // 路口不立杆
        }
        boolean alongZ = mask == 1;
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;
        for (int i = 0; i < 16; i++) {
            int worldX = alongZ ? baseX : baseX + i;
            int worldZ = alongZ ? baseZ + i : baseZ;
            int axial = alongZ ? worldZ : worldX;
            if (Math.floorMod(axial, POLE_SPACING) != 0) {
                continue;
            }
            int poleX = alongZ ? baseX + POLE_LATERAL : worldX;
            int poleZ = alongZ ? worldZ : baseZ + POLE_LATERAL;
            stampPole(stamper, poleX, poleZ, alongZ, terrain);
        }
    }

    private static void stampPole(Stamper stamper, int x, int z, boolean roadAlongZ, Terrain terrain) {
        long seed = stamper.seed;
        int baseY = terrainHeight(seed, terrain.seaLevel, x, z) + 1;
        int height = POLE_MIN_HEIGHT + rnd(seed, x, z, 811) % POLE_HEIGHT_SPAN;
        boolean lean = rnd(seed, x, z, 812) % 100 < POLE_LEAN_CHANCE;
        // 只朝路侧歪(垂直于道路), 这样杆身/横担始终留在本区块里, 不会被区块边界切掉
        boolean leanOnX = roadAlongZ;
        int leanDir = (rnd(seed, x, z, 814) & 1) == 0 ? -1 : 1;
        int leanEvery = 5 + rnd(seed, x, z, 815) % 3;

        for (int i = 0; i < height; i++) {
            int offset = lean ? (i * leanDir) / leanEvery : 0;
            int px = leanOnX ? x + offset : x;
            int pz = leanOnX ? z : z + offset;
            stamper.put(px, baseY + i, pz, POLE_BLOCK, 0);
        }

        // 横担: 垂直于道路走向, 跟着杆身在这一高度的倾斜偏移走
        int armIndex = height - 2;
        int armOffset = lean ? (armIndex * leanDir) / leanEvery : 0;
        int armX = leanOnX ? x + armOffset : x;
        int armZ = leanOnX ? z : z + armOffset;
        int armY = baseY + armIndex;
        for (int t = -2; t <= 2; t++) {
            if (roadAlongZ) {
                stamper.put(armX + t, armY, armZ, POLE_BLOCK, 0);
            } else {
                stamper.put(armX, armY, armZ + t, POLE_BLOCK, 0);
            }
        }
        int arm2Y = armY - 2;
        for (int t = -1; t <= 1; t++) {
            if (roadAlongZ) {
                stamper.put(armX + t, arm2Y, armZ, POLE_BLOCK, 0);
            } else {
                stamper.put(armX, arm2Y, armZ + t, POLE_BLOCK, 0);
            }
        }

        // 绝缘子(以后往这里挂电线)
        if (roadAlongZ) {
            stamper.put(armX - 2, armY + 1, armZ, POLE_INSULATOR, 0);
            stamper.put(armX + 2, armY + 1, armZ, POLE_INSULATOR, 0);
        } else {
            stamper.put(armX, armY + 1, armZ - 2, POLE_INSULATOR, 0);
            stamper.put(armX, armY + 1, armZ + 2, POLE_INSULATOR, 0);
        }

        // 杆顶挂一盏亮着的红石灯: 这就是路灯本体
        int topOffset = lean ? ((height - 1) * leanDir) / leanEvery : 0;
        stamper.put(leanOnX ? x + topOffset : x, baseY + height, leanOnX ? z : z + topOffset, POLE_LAMP, 0);
    }

    private static void stampCrosses(Stamper stamper, int chunkX, int chunkZ, Terrain terrain) {
        int cellSize = CROSS_CELL << 4;
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;
        int cellX = Math.floorDiv(baseX, cellSize);
        int cellZ = Math.floorDiv(baseZ, cellSize);
        for (int offsetX = -1; offsetX <= 1; offsetX++) {
            for (int offsetZ = -1; offsetZ <= 1; offsetZ++) {
                Cross cross = crossFor(stamper.seed, terrain, cellX + offsetX, cellZ + offsetZ, cellSize);
                if (cross != null && cross.intersects(baseX, baseZ)) {
                    cross.stamp(stamper);
                }
            }
        }
    }

    private static Cross crossFor(long seed, Terrain terrain, int cellX, int cellZ, int cellSize) {
        if (rnd(seed, cellX, cellZ, 701) % 100 >= CROSS_CHANCE) {
            return null;
        }
        int centerX = cellX * cellSize + 32 + rnd(seed, cellX, cellZ, 702) % (cellSize - 64);
        int centerZ = cellZ * cellSize + 32 + rnd(seed, cellX, cellZ, 703) % (cellSize - 64);
        int baseY = terrainHeight(seed, terrain.seaLevel, centerX, centerZ);
        int height = CROSS_MIN_HEIGHT + rnd(seed, cellX, cellZ, 704) % CROSS_HEIGHT_SPAN;
        height = Math.min(height, CROSS_TOP_LIMIT - baseY);
        if (height < CROSS_MIN_HEIGHT / 2) {
            return null;
        }
        int thickness = 6 + rnd(seed, cellX, cellZ, 705) % 4;
        boolean leanX = (rnd(seed, cellX, cellZ, 706) & 1) == 0;
        int leanDir = (rnd(seed, cellX, cellZ, 707) & 1) == 0 ? -1 : 1;
        int beamTilt = (rnd(seed, cellX, cellZ, 708) & 1) == 0 ? -1 : 1;
        return new Cross(centerX, centerZ, baseY, height, thickness, leanX, leanDir, beamTilt);
    }

    /** 一根巨大、倾斜的十字架。 */
    private static final class Cross {

        final int centerX;
        final int centerZ;
        final int baseY;
        final int height;
        final int thickness;
        final boolean leanX;
        final int leanDir;
        final int beamTilt;

        Cross(int centerX, int centerZ, int baseY, int height, int thickness, boolean leanX, int leanDir,
            int beamTilt) {
            this.centerX = centerX;
            this.centerZ = centerZ;
            this.baseY = baseY;
            this.height = height;
            this.thickness = thickness;
            this.leanX = leanX;
            this.leanDir = leanDir;
            this.beamTilt = beamTilt;
        }

        /** 该十字架是否可能伸进这个区块(用于跳过不相干的候选点)。 */
        boolean intersects(int chunkX16, int chunkZ16) {
            int radius = this.height / CROSS_SLOPE + (this.height * 3) / 10 + this.thickness + 3;
            return this.centerX + radius >= chunkX16 && this.centerX - radius <= chunkX16 + 15
                && this.centerZ + radius >= chunkZ16
                && this.centerZ - radius <= chunkZ16 + 15;
        }

        void stamp(Stamper stamper) {
            int fullHalf = this.thickness / 2;
            int shaftBase = this.baseY + 1;
            int beamIndex = (this.height * CROSS_BEAM_HEIGHT) / 100;
            int beamHalf = (this.height * CROSS_BEAM_HALF) / 10;

            // 倾斜的竖臂, 上下两端逐渐收窄成尖
            int shaftTaper = Math.max(8, this.height / 5);
            for (int i = 0; i < this.height; i++) {
                int nearTip = Math.min(i, this.height - 1 - i);
                int half = taperedHalf(fullHalf, nearTip, shaftTaper);
                int y = shaftBase + i;
                int lean = (i * this.leanDir) / CROSS_SLOPE;
                int x = this.leanX ? this.centerX + lean : this.centerX;
                int z = this.leanX ? this.centerZ : this.centerZ + lean;
                box(stamper, x, y, z, half, 0, half, CROSS_BODY);
            }

            // 横臂: 位置高、长度短、两端同样收尖, 并跟着竖臂的倾斜走
            int beamY = shaftBase + beamIndex;
            int beamLean = (beamIndex * this.leanDir) / CROSS_SLOPE;
            int beamCenterX = this.leanX ? this.centerX + beamLean : this.centerX;
            int beamCenterZ = this.leanX ? this.centerZ : this.centerZ + beamLean;
            int beamTaper = Math.max(4, beamHalf / 2);
            for (int t = -beamHalf; t <= beamHalf; t++) {
                int half = taperedHalf(fullHalf, beamHalf - Math.abs(t), beamTaper);
                int y = beamY + (t * this.beamTilt) / 8;
                if (this.leanX) {
                    box(stamper, beamCenterX, y, beamCenterZ + t, half, half, 0, CROSS_BODY);
                } else {
                    box(stamper, beamCenterX + t, y, beamCenterZ, 0, half, half, CROSS_BODY);
                }
            }

            // 发光核心
            box(stamper, beamCenterX, beamY, beamCenterZ, 2, 2, 2, CROSS_CORE);
        }
    }

    /** 离末端 distanceFromTip 格处的半宽: 末端 1 格, 往里走 sharpLength 格后恢复 fullHalf。 */
    private static int taperedHalf(int fullHalf, int distanceFromTip, int sharpLength) {
        if (distanceFromTip >= sharpLength) {
            return fullHalf;
        }
        int half = fullHalf * distanceFromTip / sharpLength;
        return Math.max(0, Math.min(fullHalf, half));
    }

    private static BlockMeta resolveCrossBody() {
        try {
            Block chisel = findChiselRedstone();
            if (chisel != null) {
                return new BlockMeta(chisel, CROSS_BODY_META);
            }
        } catch (Throwable ignored) {
            // 没装 Chisel / 方块名变了就退回原版
        }
        return new BlockMeta(Blocks.redstone_block, 0);
    }

    /** 找 Chisel 的红石块; 先按注册名查, 查不到就扫一遍方块注册表(只认 redstone_block, 不碰红石灯)。 */
    private static Block findChiselRedstone() {
        Block byName = GameRegistry.findBlock("chisel", "redstone_block");
        if (byName != null && byName != Blocks.air) {
            return byName;
        }
        for (Object key : Block.blockRegistry.getKeys()) {
            String name = String.valueOf(key);
            if (name.endsWith(":redstone_block")) {
                Object candidate = Block.blockRegistry.getObject(name);
                if (candidate instanceof Block && candidate != Blocks.air) {
                    return (Block) candidate;
                }
            }
        }
        return null;
    }

    /** 路灯杆身: IC2 的铁栅栏(blockFenceIron)最像末日街头那种细杆; 找不到就退回原版铁栏杆。 */
    private static Block resolvePoleFence() {
        try {
            Block ic2 = findIc2IronFence();
            if (ic2 != null) {
                return ic2;
            }
        } catch (Throwable ignored) {
            // 没装 IC2 / 方块名变了就退回原版
        }
        return Blocks.iron_bars;
    }

    /** 找 IC2 的铁栅栏: 先按注册名查两个常见大小写, 查不到就扫一遍方块注册表。 */
    private static Block findIc2IronFence() {
        for (String name : new String[] { "blockFenceIron", "blockIronFence" }) {
            Block byName = GameRegistry.findBlock("IC2", name);
            if (byName != null && byName != Blocks.air) {
                return byName;
            }
        }
        for (Object key : Block.blockRegistry.getKeys()) {
            String name = String.valueOf(key);
            if (name.endsWith(":blockFenceIron") || name.endsWith(":blockIronFence")) {
                Object candidate = Block.blockRegistry.getObject(name);
                if (candidate instanceof Block && candidate != Blocks.air) {
                    return (Block) candidate;
                }
            }
        }
        return null;
    }

    /** 方块 + 元数据(GT 的材质方块要靠 meta 区分)。 */
    private static final class BlockMeta {

        final Block block;
        final int meta;

        BlockMeta(Block block, int meta) {
            this.block = block;
            this.meta = meta;
        }
    }

    private static void box(Stamper stamper, int x, int y, int z, int radiusX, int radiusY, int radiusZ,
        BlockMeta material) {
        if (!stamper.touches(x, y, z, radiusX, radiusY, radiusZ)) {
            return;
        }
        for (int dx = -radiusX; dx <= radiusX; dx++) {
            for (int dy = -radiusY; dy <= radiusY; dy++) {
                for (int dz = -radiusZ; dz <= radiusZ; dz++) {
                    stamper.put(x + dx, y + dy, z + dz, material.block, material.meta);
                }
            }
        }
    }

    private static void stampVehicle(Stamper stamper, int chunkX, int chunkZ, int mask, Terrain terrain) {
        if (mask == 3 || rnd(stamper.seed, chunkX, chunkZ, 815) % 100 >= 10) return;
        boolean alongZ = mask == 1;
        int x = (chunkX << 4) + (alongZ ? 9 : 8);
        int z = (chunkZ << 4) + (alongZ ? 8 : 9);
        int ground = terrainHeight(stamper.seed, terrain.seaLevel, x, z);
        if (ground <= terrain.seaLevel + 2 || ground > 235) return;
        for (int forward = -3; forward <= 3; forward++) {
            for (int side = -2; side <= 2; side++) {
                int px = x + (alongZ ? side : forward);
                int pz = z + (alongZ ? forward : side);
                int surface = terrainHeight(stamper.seed, terrain.seaLevel, px, pz);
                if (Math.abs(surface - ground) > 2) return;
            }
        }
        boolean bus = rnd(stamper.seed, chunkX, chunkZ, 816) % 3 == 0;
        int length = bus ? 5 : 3;
        for (int forward = -length; forward <= length; forward++) {
            for (int side = -1; side <= 1; side++) {
                int px = x + (alongZ ? side : forward);
                int pz = z + (alongZ ? forward : side);
                if (noise(stamper.seed, px, pz, 817) < 12) continue;
                stamper.put(px, ground + 1, pz, Blocks.iron_block, 0);
                if (Math.abs(forward) < length - 1 && noise(stamper.seed, px, pz, 818) > 45) {
                    stamper.put(
                        px,
                        ground + 2,
                        pz,
                        side == 0 ? Blocks.stained_hardened_clay : Blocks.iron_bars,
                        side == 0 ? 14 : 0);
                }
                if (Math.abs(forward) == length - 1 && side != 0) {
                    stamper.put(px, ground, pz, Blocks.coal_block, 0);
                }
            }
        }
    }

    /** 按地块坐标决定这一栋楼的类型与尺寸; 返回 null 表示空地。 */
    private static Building plan(int chunkX, int chunkZ, int baseY, long seed) {
        if (rnd(seed, chunkX, chunkZ, 201) % 100 < EMPTY_LOT_CHANCE) {
            return null;
        }

        Building building = new Building();
        building.baseY = baseY;
        int silhouette = rnd(seed, chunkX, chunkZ, 218) % 5;
        building.width = silhouette == 0 ? 6 + rnd(seed, chunkX, chunkZ, 202) % 3
            : silhouette == 1 ? 12 + rnd(seed, chunkX, chunkZ, 202) % 3
                : MIN_FOOTPRINT + rnd(seed, chunkX, chunkZ, 202) % FOOTPRINT_SPAN;
        building.length = silhouette == 2 ? 6 + rnd(seed, chunkX, chunkZ, 203) % 3
            : silhouette == 1 ? 12 + rnd(seed, chunkX, chunkZ, 203) % 3
                : MIN_FOOTPRINT + rnd(seed, chunkX, chunkZ, 203) % FOOTPRINT_SPAN;
        building.x0 = 1 + rnd(seed, chunkX, chunkZ, 204) % (15 - building.width);
        building.z0 = 1 + rnd(seed, chunkX, chunkZ, 205) % (15 - building.length);

        int roll = rnd(seed, chunkX, chunkZ, 206) % 100;
        building.kind = roll < 15 ? KIND_RUIN : roll < 68 ? KIND_BLOCK : KIND_TOWER;
        building.damage = 25 + rnd(seed, chunkX, chunkZ, 207) % 70;
        building.palette = rnd(seed, chunkX, chunkZ, 208) % 5;

        if (building.kind == KIND_RUIN) {
            building.height = 2 + rnd(seed, chunkX, chunkZ, 209) % 3;
        } else if (building.kind == KIND_BLOCK) {
            building.height = silhouette == 1 ? 7 + rnd(seed, chunkX, chunkZ, 210) % 8
                : 10 + rnd(seed, chunkX, chunkZ, 210) % 29;
        } else {
            building.height = silhouette == 0 || silhouette == 2 ? 50 + rnd(seed, chunkX, chunkZ, 211) % 55
                : 27 + rnd(seed, chunkX, chunkZ, 211) % 50;
        }

        // 竖着被削掉一条(整栋楼缺了一翼), 让轮廓不那么规整
        if (building.kind != KIND_RUIN) {
            int gouge = rnd(seed, chunkX, chunkZ, 212);
            if (gouge % 100 < 55) {
                building.gougeStart = gouge % building.width;
                building.gougeWidth = 1 + (gouge >>> 8) % 3;
            }
        }

        // 底层留一个 2x2 的破门洞, 否则整栋楼是密封的, 玩家只能拆墙进去
        building.doorSide = rnd(seed, chunkX, chunkZ, 213) % 4;
        int doorSpan = building.doorSide < 2 ? building.width : building.length;
        building.doorOffset = 1 + rnd(seed, chunkX, chunkZ, 214) % (doorSpan - 3);

        // 地下室: 随机含水/抽干, 并在内部留一个 2x2 的竖井通到地面层
        building.wet = rnd(seed, chunkX, chunkZ, 215) % 100 < WET_BASEMENT_CHANCE;
        building.shaftI = 1 + rnd(seed, chunkX, chunkZ, 216) % (building.width - 3);
        building.shaftJ = 1 + rnd(seed, chunkX, chunkZ, 217) % (building.length - 3);

        return building;
    }

    private static void stampBasement(Stamper stamper, Building building, Terrain terrain) {
        int baseX = (stamper.chunkX << 4) + building.x0;
        int baseZ = (stamper.chunkZ << 4) + building.z0;
        long seed = stamper.seed;
        boolean collapsed = building.kind == KIND_RUIN;

        // 地下外墙: 水线以下没有窗, 只有随损坏程度增加的掉块
        for (int y = BASEMENT_BOTTOM; y < building.baseY; y++) {
            for (int i = 0; i < building.width; i++) {
                for (int j = 0; j < building.length; j++) {
                    boolean edge = i == 0 || i == building.width - 1 || j == 0 || j == building.length - 1;
                    if (!edge) {
                        continue;
                    }
                    int worldX = baseX + i;
                    int worldZ = baseZ + j;
                    if (noise(seed, worldX, y, worldZ, 511) < 4 + building.damage / 8) {
                        continue;
                    }
                    stamper.wall(worldX, y, worldZ, building.palette);
                }
            }
        }

        // 地下室内部: 湿的灌满 LCL, 干的抽成空气
        for (int y = BASEMENT_BOTTOM; y < building.baseY; y++) {
            for (int i = 1; i < building.width - 1; i++) {
                for (int j = 1; j < building.length - 1; j++) {
                    int worldX = baseX + i;
                    int worldZ = baseZ + j;
                    if (building.wet) {
                        stamper.put(worldX, y, worldZ, terrain.water, terrain.waterMeta);
                    } else {
                        stamper.clear(worldX, y, worldZ);
                    }
                }
            }
        }

        // 楼板, 以及每层楼板上的残骸/蛛网
        for (int y = BASEMENT_BOTTOM; y < building.baseY; y += FLOOR_HEIGHT) {
            boolean bottom = y == BASEMENT_BOTTOM;
            if (collapsed && !bottom) {
                break;
            }
            for (int i = 0; i < building.width; i++) {
                for (int j = 0; j < building.length; j++) {
                    if (building.isShaft(i, j)) {
                        continue;
                    }
                    int worldX = baseX + i;
                    int worldZ = baseZ + j;
                    if (!bottom && noise(seed, worldX, y, worldZ, 512) < 12 + building.damage / 2) {
                        continue; // 塌掉的楼板
                    }
                    stamper.put(worldX, y, worldZ, Blocks.stone_slab, 0);
                }
            }
            for (int i = 1; i < building.width - 1; i++) {
                for (int j = 1; j < building.length - 1; j++) {
                    if (building.isShaft(i, j)) {
                        continue;
                    }
                    int worldX = baseX + i;
                    int worldZ = baseZ + j;
                    int roll = noise(seed, worldX, y + 1, worldZ, 513);
                    if (roll < 2) {
                        stamper.rubble(worldX, y + 1, worldZ);
                    } else if (roll < 4) {
                        stamper.put(worldX, y + 1, worldZ, Blocks.web, 0);
                    }
                }
            }
        }
    }

    private static void stampBuilding(Stamper stamper, Building building) {
        int baseX = (stamper.chunkX << 4) + building.x0;
        int baseZ = (stamper.chunkZ << 4) + building.z0;
        int topY = building.baseY + building.height;
        int jagMax = 1 + (building.damage * building.height) / 480;

        // 每一列自己的塌陷高度: 楼顶被削成参差不齐的形状
        int[] tops = new int[building.width * building.length];
        for (int i = 0; i < building.width; i++) {
            for (int j = 0; j < building.length; j++) {
                int worldX = baseX + i;
                int worldZ = baseZ + j;
                int columnHash = rnd(stamper.seed, worldX, worldZ, 301);
                int top = topY - (columnHash % 256) * jagMax / 255;
                if (building.gougeStart >= 0 && i >= building.gougeStart
                    && i < building.gougeStart + building.gougeWidth) {
                    int cut = building.baseY + Math.max(2, building.height / 3) + (columnHash & 3);
                    if (cut < top) {
                        top = cut;
                    }
                }
                tops[i * building.length + j] = Math.max(top, building.baseY);
            }
        }

        // Excavate the entry floor before walls are stamped; the natural terrain may rise above y=64.
        for (int i = 0; i < building.width; i++) {
            for (int j = 0; j < building.length; j++) {
                for (int y = building.baseY + 1; y <= building.baseY + 3; y++) {
                    stamper.clear(baseX + i, y, baseZ + j);
                }
            }
        }

        // 地基(竖井位置留空, 否则从屋里下不去)
        for (int i = 0; i < building.width; i++) {
            for (int j = 0; j < building.length; j++) {
                if (building.isShaft(i, j)) {
                    continue;
                }
                stamper.wall(baseX + i, building.baseY, baseZ + j, building.palette);
            }
        }

        // 外墙 / 窗 / 破洞
        for (int y = building.baseY + 1; y < topY; y++) {
            int level = y - building.baseY;
            boolean windowBand = level % FLOOR_HEIGHT == 1 || level % FLOOR_HEIGHT == 2;
            for (int i = 0; i < building.width; i++) {
                for (int j = 0; j < building.length; j++) {
                    boolean edge = i == 0 || i == building.width - 1 || j == 0 || j == building.length - 1;
                    if (!edge || y > tops[i * building.length + j]) {
                        continue;
                    }

                    int worldX = baseX + i;
                    int worldZ = baseZ + j;
                    // 底层破门洞
                    if (level <= 2 && building.isDoorway(i, j)) {
                        continue;
                    }
                    // 2×2 聚簇的大块破洞
                    if (noise(stamper.seed, worldX >> 1, y >> 1, worldZ >> 1, 311) < building.damage / 3) {
                        continue;
                    }
                    // 单块风化
                    if (noise(stamper.seed, worldX, y, worldZ, 312) < 6) {
                        continue;
                    }

                    if (level % FLOOR_HEIGHT == 0 && (i == 0 || j == 0) && (i + j) % 3 == 0) {
                        stamper.put(worldX, y, worldZ, Blocks.stonebrick, building.palette == 2 ? 1 : 0);
                    } else if (windowBand && ((i + j) & 1) == 0) {
                        if (noise(stamper.seed, worldX, y, worldZ, 313) < 40 + building.damage / 2) {
                            continue; // 碎掉的窗
                        }
                        stamper.window(worldX, y, worldZ, building.palette);
                    } else {
                        stamper.wall(worldX, y, worldZ, building.palette);
                    }
                }
            }
        }

        // 楼板, 带塌孔
        for (int y = building.baseY + FLOOR_HEIGHT; y < topY; y += FLOOR_HEIGHT) {
            for (int i = 1; i < building.width - 1; i++) {
                for (int j = 1; j < building.length - 1; j++) {
                    if (y > tops[i * building.length + j]) {
                        continue;
                    }
                    int worldX = baseX + i;
                    int worldZ = baseZ + j;
                    if (noise(stamper.seed, worldX, y, worldZ, 321) < 22 + building.damage / 2) {
                        continue; // 塌掉的楼板
                    }
                    stamper.put(worldX, y, worldZ, Blocks.stone_slab, 0);
                }
            }
        }

        // Roof silhouettes vary between collapsed parapets and surviving service equipment.
        int roofY = topY - jagMax - 1;
        if (roofY > building.baseY + 5) {
            for (int i = 0; i < building.width; i++) {
                for (int j = 0; j < building.length; j++) {
                    if (i != 0 && j != 0 && i != building.width - 1 && j != building.length - 1) continue;
                    int x = baseX + i;
                    int z = baseZ + j;
                    if (tops[i * building.length + j] >= roofY
                        && noise(stamper.seed, x, roofY, z, 343) > building.damage) {
                        stamper.put(
                            x,
                            roofY + 1,
                            z,
                            building.palette == 0 ? Blocks.quartz_block : Blocks.stonebrick,
                            building.palette == 0 ? 0 : 2);
                    }
                }
            }
            if (building.kind == KIND_BLOCK) {
                int centerX = baseX + building.width / 2;
                int centerZ = baseZ + building.length / 2;
                stamper.put(centerX, roofY + 1, centerZ, Blocks.iron_block, 0);
                stamper.put(centerX, roofY + 2, centerZ, Blocks.iron_bars, 0);
                stamper.put(centerX, roofY + 3, centerZ, Blocks.redstone_torch, 0);
            }
        }

        // 底层的蛛网, 增加荒废感
        int webTop = Math.min(building.baseY + 12, topY);
        for (int y = building.baseY + 1; y < webTop; y++) {
            for (int i = 1; i < building.width - 1; i++) {
                for (int j = 1; j < building.length - 1; j++) {
                    if (noise(stamper.seed, baseX + i, y, baseZ + j, 331) < 3) {
                        stamper.put(baseX + i, y, baseZ + j, Blocks.web, 0);
                    }
                }
            }
        }
    }

    /** 彻底塌掉的楼: 只剩瓦砾堆和几截断墙。 */
    private static void stampRuin(Stamper stamper, Building building) {
        int baseX = (stamper.chunkX << 4) + building.x0;
        int baseZ = (stamper.chunkZ << 4) + building.z0;

        for (int i = 0; i < building.width; i++) {
            for (int j = 0; j < building.length; j++) {
                int worldX = baseX + i;
                int worldZ = baseZ + j;
                int pile = rnd(stamper.seed, worldX, worldZ, 401) % (building.height + 1);
                for (int y = 0; y < pile; y++) {
                    stamper.rubble(worldX, building.baseY + y, worldZ);
                }
            }
        }

        int stubs = 2 + rnd(stamper.seed, stamper.chunkX, stamper.chunkZ, 402) % 4;
        for (int stub = 0; stub < stubs; stub++) {
            int i = rnd(stamper.seed, stamper.chunkX, stamper.chunkZ, 403 + stub) % building.width;
            int j = (stub & 1) == 0 ? 0 : building.length - 1;
            int stubHeight = 3 + noise(stamper.seed, baseX + i, stub, baseZ + j, 404) % 7;
            for (int y = 0; y < stubHeight; y++) {
                stamper.wall(baseX + i, building.baseY + y, baseZ + j, building.palette);
            }
        }
    }

    /** 地面上(水底也算)的散落残骸。 */
    private static void stampDebris(Stamper stamper, int chunkX, int chunkZ, Terrain terrain, int perMille) {
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;
        for (int x = 0; x < 16; x++) {
            int worldX = baseX + x;
            for (int z = 0; z < 16; z++) {
                int worldZ = baseZ + z;
                if (rnd(stamper.seed, worldX, worldZ, 501) % 1000 >= perMille) {
                    continue;
                }
                int ground = terrainHeight(stamper.seed, terrain.seaLevel, worldX, worldZ);
                if (ground > terrain.seaLevel + 1) {
                    stamper.rubble(worldX, ground + 1, worldZ);
                }
            }
        }
    }

    private static void stampVillage(Stamper stamper, int chunkX, int chunkZ, Terrain terrain) {
        if (isCityChunk(chunkX, chunkZ, stamper.seed, terrain.seaLevel)) return;
        int cellX = Math.floorDiv(chunkX, VILLAGE_CELL);
        int cellZ = Math.floorDiv(chunkZ, VILLAGE_CELL);
        for (int cx = cellX - 1; cx <= cellX + 1; cx++) {
            for (int cz = cellZ - 1; cz <= cellZ + 1; cz++) {
                if (rnd(stamper.seed, cx, cz, 1400) % 100 >= VILLAGE_CHANCE) continue;
                int centerX = (cx * VILLAGE_CELL + 4 + rnd(stamper.seed, cx, cz, 1401) % 13) * 16;
                int centerZ = (cz * VILLAGE_CELL + 4 + rnd(stamper.seed, cx, cz, 1402) % 13) * 16;
                if (Math.abs(centerX - (chunkX << 4)) > 90 || Math.abs(centerZ - (chunkZ << 4)) > 90) continue;
                int ground = terrainHeight(stamper.seed, terrain.seaLevel, centerX, centerZ);
                if (ground < terrain.seaLevel + 5 || ground > terrain.seaLevel + 35
                    || isCityChunk(
                        Math.floorDiv(centerX, 16),
                        Math.floorDiv(centerZ, 16),
                        stamper.seed,
                        terrain.seaLevel))
                    continue;
                boolean suitable = true;
                for (int dx = -48; dx <= 48 && suitable; dx += 24) {
                    for (int dz = -48; dz <= 48; dz += 24) {
                        int height = terrainHeight(stamper.seed, terrain.seaLevel, centerX + dx, centerZ + dz);
                        if (height <= terrain.seaLevel + 2 || Math.abs(height - ground) > 8) {
                            suitable = false;
                            break;
                        }
                    }
                }
                if (!suitable) continue;
                stampVillagePart(stamper, centerX, centerZ, terrain);
            }
        }
    }

    private static void stampVillagePart(Stamper stamper, int centerX, int centerZ, Terrain terrain) {
        int baseX = stamper.chunkX << 4;
        int baseZ = stamper.chunkZ << 4;
        for (int x = baseX; x < baseX + 16; x++) {
            for (int z = baseZ; z < baseZ + 16; z++) {
                if (Math.abs(x - centerX) > 52 || Math.abs(z - centerZ) > 52) continue;
                if (Math.abs(x - centerX) <= 2 || Math.abs(z - centerZ) <= 2) {
                    int y = terrainHeight(stamper.seed, terrain.seaLevel, x, z);
                    if (y > terrain.seaLevel + 2 && noise(stamper.seed, x, z, 1410) > 32) {
                        stamper.put(x, y, z, Blocks.gravel, 0);
                        if (noise(stamper.seed, x, z, 1411) < 12) stamper.put(x, y + 1, z, Blocks.redstone_block, 0);
                    }
                }
            }
        }
        for (int sideX = -1; sideX <= 1; sideX++) {
            for (int sideZ = -1; sideZ <= 1; sideZ++) {
                if (sideX == 0 && sideZ == 0) continue;
                int x = centerX + sideX * 29;
                int z = centerZ + sideZ * 29;
                int hash = rnd(stamper.seed, x, z, 1420);
                if (hash % 100 < 22) continue;
                int halfX = 4 + (hash >>> 5) % 3;
                int halfZ = 4 + (hash >>> 9) % 3;
                if (!stamper.touches(x, 66, z, halfX + 1, 150, halfZ + 1)) continue;
                int floor = terrainHeight(stamper.seed, terrain.seaLevel, x, z) + 1;
                if (floor < terrain.seaLevel + 4 || floor > terrain.seaLevel + 44) continue;
                int roof = 4 + (hash >>> 13) % 3;
                for (int dx = -halfX; dx <= halfX; dx++) {
                    for (int dz = -halfZ; dz <= halfZ; dz++) {
                        int px = x + dx;
                        int pz = z + dz;
                        int localGround = terrainHeight(stamper.seed, terrain.seaLevel, px, pz);
                        if (localGround <= terrain.seaLevel + 2 || Math.abs(localGround - floor) > 8
                            || isCityChunk(
                                Math.floorDiv(px, 16),
                                Math.floorDiv(pz, 16),
                                stamper.seed,
                                terrain.seaLevel))
                            continue;
                        stamper.put(px, floor - 1, pz, Blocks.cobblestone, 0);
                        boolean edge = Math.abs(dx) == halfX || Math.abs(dz) == halfZ;
                        for (int dy = 0; dy <= roof; dy++) {
                            int y = floor + dy;
                            if (!edge) {
                                stamper.clear(px, y, pz);
                            } else if (dy == roof) {
                                if (noise(stamper.seed, px, y, pz, 1430) > 60) {
                                    stamper.put(px, y, pz, Blocks.planks, 0);
                                }
                            } else if (dz == -halfZ && dx >= -1 && dx <= 0 && dy < 3) {
                                stamper.clear(px, y, pz);
                            } else if (noise(stamper.seed, px, y, pz, 1431) > 52 + dy * 17) {
                                Block wall = (dx == -halfX || dx == halfX) && dy > 1 ? Blocks.log : Blocks.cobblestone;
                                stamper.put(px, y, pz, wall, 0);
                            }
                        }
                        if (edge && noise(stamper.seed, px, pz, 1432) < 54) {
                            stamper.put(px, floor + 1, pz, Blocks.stained_hardened_clay, 14);
                        }
                    }
                }
            }
        }
        int wellY = terrainHeight(stamper.seed, terrain.seaLevel, centerX, centerZ) + 1;
        if (stamper.touches(centerX, wellY, centerZ, 2, 4, 2)) {
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    if (Math.abs(dx) == 2 || Math.abs(dz) == 2) {
                        stamper.put(centerX + dx, wellY, centerZ + dz, Blocks.mossy_cobblestone, 0);
                        if (noise(stamper.seed, centerX + dx, centerZ + dz, 1440) < 95)
                            stamper.put(centerX + dx, wellY + 1, centerZ + dz, Blocks.redstone_block, 0);
                    }
                }
            }
        }
    }

    /** 荒野地物总入口: 弹坑、枯树、枯草、墓碑、天使骸骨, 全按种子独立决定。 */
    private static void stampWilderness(Stamper stamper, int chunkX, int chunkZ, Terrain terrain) {
        stampCrater(stamper, chunkX, chunkZ, terrain);
        stampDeadBushes(stamper, chunkX, chunkZ, terrain);
        stampGraveCrosses(stamper, chunkX, chunkZ, terrain);
    }

    private static void stampDeadTrees(Stamper stamper, int chunkX, int chunkZ, Terrain terrain, int chance) {
        long seed = stamper.seed;
        for (int sourceX = chunkX - 1; sourceX <= chunkX + 1; sourceX++) {
            for (int sourceZ = chunkZ - 1; sourceZ <= chunkZ + 1; sourceZ++) {
                if (rnd(seed, sourceX, sourceZ, 1101) % 100 >= chance
                    || isCityChunk(sourceX, sourceZ, seed, terrain.seaLevel)) continue;
                int count = 1 + rnd(seed, sourceX, sourceZ, 1102) % 2;
                for (int i = 0; i < count; i++) {
                    int x = (sourceX << 4) + 2 + rnd(seed, sourceX, sourceZ, 1110 + i) % 12;
                    int z = (sourceZ << 4) + 2 + rnd(seed, sourceX, sourceZ, 1120 + i) % 12;
                    stampDeadTree(stamper, x, z, terrain, rnd(seed, sourceX, sourceZ, 1130 + i));
                }
            }
        }
    }

    /** 一棵完全枯死的树: 立着的(带断枝) / 只剩树桩 / 已经倒下的, 一部分从根烧成焦炭。 */
    private static void stampDeadTree(Stamper stamper, int x, int z, Terrain terrain, int hash) {
        long seed = stamper.seed;
        int ground = terrainHeight(seed, terrain.seaLevel, x, z) + 1;
        if (ground <= terrain.seaLevel + 2) {
            return;
        }
        int meta = (hash >>> 16) & 1;
        boolean charred = (hash >>> 8) % 100 < 30;
        int roll = hash % 100;
        if (roll < 12) {
            stampFallenTrunk(stamper, x, z, ground, hash, meta, charred, terrain.seaLevel);
            return;
        }
        int height = roll < 24 ? 3 + (hash >>> 4) % 3 : 9 + (hash >>> 4) % 8;
        boolean leanOnX = ((hash >>> 12) & 1) == 0;
        int leanAt = Math.max(1, height - 2);
        for (int i = 0; i < height; i++) {
            boolean bend = i >= leanAt;
            int px = leanOnX && bend ? x + 1 : x;
            int pz = !leanOnX && bend ? z + 1 : z;
            putOnDryLand(
                stamper,
                terrain.seaLevel,
                px,
                ground + i,
                pz,
                charred && i < 2 ? Blocks.coal_block : Blocks.log,
                meta);
        }
        int branches = 2 + (hash >>> 6) % 3;
        for (int b = 0; b < branches; b++) {
            int branchHash = rnd(seed, x + b * 7, z - b * 13, 1150);
            int span = Math.max(1, height - 2);
            int y = ground + 1 + branchHash % span;
            boolean alongX = ((branchHash >>> 7) & 1) == 0;
            int dir = ((branchHash >>> 4) & 1) == 0 ? 1 : -1;
            int length = 3 + (branchHash >>> 5) % 3;
            boolean bent = y - ground >= leanAt;
            int bx = leanOnX && bent ? x + 1 : x;
            int bz = !leanOnX && bent ? z + 1 : z;
            for (int t = 1; t <= length; t++) {
                int px = alongX ? bx + dir * t : bx;
                int pz = alongX ? bz : bz + dir * t;
                putOnDryLand(stamper, terrain.seaLevel, px, y + (t == length ? 1 : 0), pz, Blocks.log, meta);
            }
        }
    }

    private static void stampFallenTrunk(Stamper stamper, int x, int z, int ground, int hash, int meta, boolean charred,
        int seaLevel) {
        boolean alongX = ((hash >>> 10) & 1) == 0;
        int dir = ((hash >>> 13) & 1) == 0 ? 1 : -1;
        int length = 7 + (hash >>> 11) % 5;
        for (int i = 0; i < length; i++) {
            int step = dir > 0 ? i : -i;
            int px = alongX ? x + step : x;
            int pz = alongX ? z : z + step;
            putOnDryLand(
                stamper,
                seaLevel,
                px,
                ground,
                pz,
                charred && i % 3 == 0 ? Blocks.coal_block : Blocks.log,
                meta);
        }
        int stubStep = dir > 0 ? -1 : 1;
        int sx = alongX ? x + stubStep : x;
        int sz = alongX ? z : z + stubStep;
        putOnDryLand(stamper, seaLevel, sx, ground, sz, Blocks.log, meta);
        putOnDryLand(stamper, seaLevel, sx, ground + 1, sz, Blocks.log, meta);
    }

    private static void stampDeadBushes(Stamper stamper, int chunkX, int chunkZ, Terrain terrain) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = (chunkX << 4) + x;
                int worldZ = (chunkZ << 4) + z;
                if (noise(stamper.seed, worldX, worldZ, 1160) >= BUSH_DENSITY) {
                    continue;
                }
                int ground = terrainHeight(stamper.seed, terrain.seaLevel, worldX, worldZ);
                if (ground <= terrain.seaLevel + 1) {
                    continue;
                }
                stamper.put(worldX, ground + 1, worldZ, Blocks.deadbush, 0);
            }
        }
    }

    /** 荒野里的墓群: 三到六块深色橡木小十字架, 一部分歪着, 一部分已经倒在地上。 */
    private static void stampGraveCrosses(Stamper stamper, int chunkX, int chunkZ, Terrain terrain) {
        long seed = stamper.seed;
        if (rnd(seed, chunkX, chunkZ, 1170) % 100 >= GRAVE_CHANCE) {
            return;
        }
        int originX = (chunkX << 4) + 4 + rnd(seed, chunkX, chunkZ, 1171) % 8;
        int originZ = (chunkZ << 4) + 4 + rnd(seed, chunkX, chunkZ, 1172) % 8;
        int count = 5 + rnd(seed, chunkX, chunkZ, 1173) % 5;
        for (int i = 0; i < count; i++) {
            int x = originX + rnd(seed, chunkX, chunkZ, 1180 + i) % 5 - 2;
            int z = originZ + rnd(seed, chunkX, chunkZ, 1190 + i) % 5 - 2;
            int ground = terrainHeight(seed, terrain.seaLevel, x, z);
            if (ground <= terrain.seaLevel + 1) {
                continue;
            }
            int hash = rnd(seed, x, z, 1200);
            int height = 4 + hash % 3;
            if ((hash >>> 5) % 100 < 22) {
                for (int t = 0; t <= height; t++) {
                    putOnDryLand(stamper, terrain.seaLevel, x, ground + 1, z - t, Blocks.planks, 5);
                }
                continue;
            }
            int shift = (hash >>> 3) % 100 < 40 ? 1 : 0;
            for (int t = 0; t < height; t++) {
                putOnDryLand(
                    stamper,
                    terrain.seaLevel,
                    x + (t >= height - 1 ? shift : 0),
                    ground + 1 + t,
                    z,
                    Blocks.planks,
                    5);
            }
            int barY = ground + height;
            putOnDryLand(stamper, terrain.seaLevel, x - 1 + shift, barY, z, Blocks.planks, 5);
            putOnDryLand(stamper, terrain.seaLevel, x + 1 + shift, barY, z, Blocks.planks, 5);
        }
    }

    /** 弹坑: 把地表往下挖成一个碗, 坑底铺黑曜石/碎石, 低于海面的部分重新灌满 LCL。 */
    private static void stampCrater(Stamper stamper, int chunkX, int chunkZ, Terrain terrain) {
        long seed = stamper.seed;
        if (rnd(seed, chunkX, chunkZ, 1210) % 100 >= CRATER_CHANCE) {
            return;
        }
        int radius = 3 + rnd(seed, chunkX, chunkZ, 1211) % 3;
        int depth = 1 + rnd(seed, chunkX, chunkZ, 1212) % 3;
        int margin = radius + 1;
        int centerX = (chunkX << 4) + margin + rnd(seed, chunkX, chunkZ, 1213) % (16 - margin * 2);
        int centerZ = (chunkZ << 4) + margin + rnd(seed, chunkX, chunkZ, 1214) % (16 - margin * 2);
        if (terrainHeight(seed, terrain.seaLevel, centerX, centerZ) <= terrain.seaLevel + depth + 2) return;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                double distance = Math.sqrt(dx * dx + dz * dz) / radius;
                if (distance > 1.0D) {
                    continue;
                }
                int worldX = centerX + dx;
                int worldZ = centerZ + dz;
                int ground = terrainHeight(seed, terrain.seaLevel, worldX, worldZ);
                if (ground <= terrain.seaLevel + depth + 1) continue;
                int floor = ground - (int) Math.round(depth * (1.0D - distance));
                for (int y = floor + 1; y <= ground; y++) {
                    stamper.clear(worldX, y, worldZ);
                }
                stamper.put(worldX, floor, worldZ, craterFloor(seed, worldX, floor, worldZ), 0);
                if (floor < terrain.seaLevel) {
                    for (int y = floor + 1; y <= terrain.seaLevel; y++) {
                        stamper.put(worldX, y, worldZ, terrain.water, terrain.waterMeta);
                    }
                }
            }
        }
    }

    private static Block craterFloor(long seed, int x, int y, int z) {
        int roll = noise(seed, x, y, z, 1215) % 100;
        if (roll < 18) {
            return Blocks.obsidian;
        }
        return roll < 60 ? Blocks.gravel : Blocks.cobblestone;
    }

    /** Sparse, chunk-order-independent remains: skull, vertebrae, arched ribs and a broken tail. */
    private static void stampAngelBones(Stamper stamper, int chunkX, int chunkZ, Terrain terrain) {
        int cellX = Math.floorDiv(chunkX, BONES_CELL);
        int cellZ = Math.floorDiv(chunkZ, BONES_CELL);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                int cx = cellX + dx;
                int cz = cellZ + dz;
                long seed = stamper.seed;
                if (rnd(seed, cx, cz, 1220) % 100 >= BONES_CHANCE) continue;
                int centerChunkX = cx * BONES_CELL + BONES_CELL / 2;
                int centerChunkZ = cz * BONES_CELL + BONES_CELL / 2;
                if (isCityChunk(centerChunkX, centerChunkZ, seed, terrain.seaLevel)) continue;
                int x = centerChunkX * 16 + rnd(seed, cx, cz, 1221) % 17 - 8;
                int z = centerChunkZ * 16 + rnd(seed, cx, cz, 1222) % 17 - 8;
                if (Math.abs(x - (chunkX << 4)) > 58 || Math.abs(z - (chunkZ << 4)) > 58) continue;
                if (!dryBoneSite(seed, terrain.seaLevel, x, z)) continue;
                boolean alongX = (rnd(seed, cx, cz, 1223) & 1) == 0;
                int length = 24 + rnd(seed, cx, cz, 1224) % 12;
                int spineY = terrainHeight(seed, terrain.seaLevel, x, z) + 4;
                for (int i = 0; i < length; i++) {
                    int bx = x + (alongX ? i - length / 2 : 0);
                    int bz = z + (alongX ? 0 : i - length / 2);
                    int curve = (int) Math.round(2.0 * Math.sin(i * Math.PI / length));
                    int axisX = bx + (alongX ? 0 : curve);
                    int axisZ = bz + (alongX ? curve : 0);
                    putBone(stamper, terrain.seaLevel, axisX, spineY, axisZ, Blocks.quartz_block);
                    putBone(stamper, terrain.seaLevel, axisX, spineY + 1, axisZ, Blocks.quartz_block);
                    if (i < 3 || i > length - 4 || i % 3 != 0) continue;
                    for (int side = -1; side <= 1; side += 2) {
                        for (int t = 1; t <= 10; t++) {
                            if (noise(seed, cx * 31 + i, cz * 17 + side, 1230) < 25 && t > 7) break;
                            int rx = axisX + (alongX ? 0 : side * t);
                            int rz = axisZ + (alongX ? side * t : 0);
                            int ry = spineY + 3 + (int) Math.round(5 * Math.sin(t * Math.PI / 11));
                            putBone(stamper, terrain.seaLevel, rx, ry, rz, Blocks.quartz_block);
                            if (t >= 7) putBone(stamper, terrain.seaLevel, rx, ry - 1, rz, Blocks.quartz_block);
                        }
                    }
                }
                int headX = x - (alongX ? length / 2 + 5 : 0);
                int headZ = z - (alongX ? 0 : length / 2 + 5);
                for (int hx = -4; hx <= 4; hx++) {
                    for (int hz = -4; hz <= 4; hz++) {
                        for (int hy = 0; hy <= 6; hy++) {
                            double shell = hx * hx / 18.0 + hz * hz / 18.0 + (hy - 2.5) * (hy - 2.5) / 12.0;
                            boolean socket = hy >= 2 && hy <= 4
                                && (alongX ? hx < -1 : hz < -1)
                                && Math.abs(alongX ? hz : hx) >= 1
                                && Math.abs(alongX ? hz : hx) <= 3;
                            if (shell > 1.1 || shell < 0.55 || socket) continue;
                            putBone(
                                stamper,
                                terrain.seaLevel,
                                headX + hx,
                                spineY + hy - 2,
                                headZ + hz,
                                Blocks.quartz_block);
                        }
                    }
                }
                for (int t = 1; t <= 8; t++) {
                    int tx = x + (alongX ? length / 2 + t : 0);
                    int tz = z + (alongX ? 0 : length / 2 + t);
                    if (t != 5 && t != 6)
                        putBone(stamper, terrain.seaLevel, tx, spineY - t / 3, tz, Blocks.quartz_block);
                }
            }
        }
    }

    private static boolean dryBoneSite(long seed, int seaLevel, int x, int z) {
        if (terrainHeight(seed, seaLevel, x, z) < seaLevel + 5) return false;
        for (int dx = -32; dx <= 32; dx += 16) {
            for (int dz = -16; dz <= 16; dz += 16) {
                if (terrainHeight(seed, seaLevel, x + dx, z + dz) < seaLevel + 3) return false;
            }
        }
        return true;
    }

    private static void putBone(Stamper stamper, int seaLevel, int x, int y, int z, Block block) {
        putOnDryLand(stamper, seaLevel, x, y, z, block, 0);
    }

    private static void putOnDryLand(Stamper stamper, int seaLevel, int x, int y, int z, Block block, int meta) {
        if (Math.floorDiv(x, 16) != stamper.chunkX || Math.floorDiv(z, 16) != stamper.chunkZ
            || isCityChunk(stamper.chunkX, stamper.chunkZ, stamper.seed, seaLevel)) return;
        if (terrainHeight(stamper.seed, seaLevel, x, z) > seaLevel + 1) {
            stamper.put(x, y, z, block, meta);
        }
    }

    /** 一栋楼的全部参数, 由 {@link #plan} 一次性算好。 */
    private static final class Building {

        int kind;
        int x0;
        int z0;
        int width;
        int length;
        int baseY;
        int height;
        int damage;
        int palette;
        int gougeStart = -1;
        int gougeWidth;
        int doorSide;
        int doorOffset;
        int shaftI;
        int shaftJ;
        boolean wet;

        /** 该格子是否属于贯穿地面的 2x2 竖井。 */
        boolean isShaft(int i, int j) {
            return i >= this.shaftI && i <= this.shaftI + 1 && j >= this.shaftJ && j <= this.shaftJ + 1;
        }

        /** 该墙面格子是否属于底层的破门洞(2 格宽, 2 格高)。 */
        boolean isDoorway(int i, int j) {
            switch (this.doorSide) {
                case 0:
                    return j == 0 && i >= this.doorOffset && i < this.doorOffset + 2;
                case 1:
                    return j == this.length - 1 && i >= this.doorOffset && i < this.doorOffset + 2;
                case 2:
                    return i == 0 && j >= this.doorOffset && j < this.doorOffset + 2;
                default:
                    return i == this.width - 1 && j >= this.doorOffset && j < this.doorOffset + 2;
            }
        }
    }

    /** 只写本区块的方块写入器, 越界写入直接丢弃(正常情况不会发生)。 */
    private static final class Stamper {

        final Block[] blocks;
        final byte[] metadata;
        final int chunkX;
        final int chunkZ;
        final long seed;

        Stamper(Block[] blocks, byte[] metadata, int chunkX, int chunkZ, long seed) {
            this.blocks = blocks;
            this.metadata = metadata;
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
            this.seed = seed;
        }

        void put(int worldX, int y, int worldZ, Block block, int meta) {
            int localX = worldX - (this.chunkX << 4);
            int localZ = worldZ - (this.chunkZ << 4);
            if (localX < 0 || localX > 15 || localZ < 0 || localZ > 15 || y < 0 || y > 255) {
                return;
            }
            int index = (localX * 16 + localZ) * 256 + y;
            this.blocks[index] = block;
            this.metadata[index] = (byte) meta;
        }

        /** 把格子清成空气(用于抽干地下室)。 */
        void clear(int worldX, int y, int worldZ) {
            int localX = worldX - (this.chunkX << 4);
            int localZ = worldZ - (this.chunkZ << 4);
            if (localX < 0 || localX > 15 || localZ < 0 || localZ > 15 || y < 0 || y > 255) {
                return;
            }
            int index = (localX * 16 + localZ) * 256 + y;
            this.blocks[index] = null;
            this.metadata[index] = 0;
        }

        /** 这个方盒是否可能和本区块相交(用来跳过巨型十字架里离得很远的部分)。 */
        boolean touches(int centerX, int centerY, int centerZ, int radiusX, int radiusY, int radiusZ) {
            int baseX = this.chunkX << 4;
            int baseZ = this.chunkZ << 4;
            return centerX + radiusX >= baseX && centerX - radiusX <= baseX + 15
                && centerZ + radiusZ >= baseZ
                && centerZ - radiusZ <= baseZ + 15
                && centerY + radiusY >= 0
                && centerY - radiusY <= 255;
        }

        void wall(int worldX, int y, int worldZ, int palette) {
            // 楼体里掺着和十字架同款的 chisel 红石块: 按 2×2×2 的块团出现, 约占墙面一成
            if (noise(this.seed, worldX >> 1, y >> 1, worldZ >> 1, 353) < (REDSTONE_VEIN_PERCENT * 256) / 100) {
                put(worldX, y, worldZ, CROSS_BODY.block, CROSS_BODY.meta);
                return;
            }
            int hash = noise(this.seed, worldX, y, worldZ, 351);
            switch (palette) {
                case 0 -> put(worldX, y, worldZ, Blocks.quartz_block, hash < 18 ? 1 : 0);
                case 1 -> put(worldX, y, worldZ, Blocks.stonebrick, hash < 16 ? 2 : hash < 30 ? 1 : 0);
                case 2 -> put(worldX, y, worldZ, hash < 28 ? Blocks.mossy_cobblestone : Blocks.cobblestone, 0);
                case 3 -> put(worldX, y, worldZ, hash < 20 ? Blocks.iron_block : Blocks.hardened_clay, 0);
                default -> put(worldX, y, worldZ, Blocks.stained_hardened_clay, hash < 24 ? 14 : 8);
            }
        }

        void window(int worldX, int y, int worldZ, int palette) {
            if (noise(this.seed, worldX, y, worldZ, 352) < 8) {
                put(worldX, y, worldZ, Blocks.iron_bars, 0); // 残存的窗框
                return;
            }
            switch (palette) {
                case 0 -> put(worldX, y, worldZ, Blocks.stained_glass, 15);
                case 1 -> put(worldX, y, worldZ, Blocks.stained_glass, 7);
                case 2 -> put(worldX, y, worldZ, Blocks.glass, 0);
                case 3 -> put(worldX, y, worldZ, Blocks.stained_glass, 8);
                default -> put(worldX, y, worldZ, Blocks.stained_glass, 14);
            }
        }

        void rubble(int worldX, int y, int worldZ) {
            int hash = noise(this.seed, worldX, y, worldZ, 362) % 100;
            if (hash < 45) {
                put(worldX, y, worldZ, Blocks.cobblestone, 0);
            } else if (hash < 65) {
                put(worldX, y, worldZ, Blocks.gravel, 0);
            } else if (hash < 85) {
                put(worldX, y, worldZ, Blocks.stonebrick, 2);
            } else {
                put(worldX, y, worldZ, Blocks.mossy_cobblestone, 0);
            }
        }
    }

    /** SplitMix 风格的确定性哈希, 只用于布局, 不参与游戏逻辑。 */
    private static long mix(long seed, int a, int b, int salt) {
        long hash = seed ^ ((long) a * 0x9E3779B97F4A7C15L)
            ^ ((long) b * 0xC2B2AE3D27D4EB4FL)
            ^ ((long) salt * 0x165667B19E3779F9L);
        hash ^= hash >>> 30;
        hash *= 0xBF58476D1CE4E5B9L;
        hash ^= hash >>> 27;
        hash *= 0x94D049BB133111EBL;
        hash ^= hash >>> 31;
        return hash;
    }

    /** 31 位非负随机数, 用于尺寸/概率这类决定。 */
    private static int rnd(long seed, int a, int b, int salt) {
        return (int) ((mix(seed, a, b, salt) >>> 32) & 0x7FFFFFFFL);
    }

    /** 0..255 的随机数, 用于与高度无关的逐格噪声。 */
    private static int noise(long seed, int x, int z, int salt) {
        return (int) ((mix(seed, x, z, salt) >>> 24) & 0xFF);
    }

    /** 0..255 的随机数, 用于逐格(含高度)噪声。 */
    private static int noise(long seed, int x, int y, int z, int salt) {
        return (int) ((mix(mix(seed, x, z, salt), y, x ^ z, salt) >>> 24) & 0xFF);
    }
}
