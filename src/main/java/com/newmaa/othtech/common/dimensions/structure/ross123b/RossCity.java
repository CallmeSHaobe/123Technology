package com.newmaa.othtech.common.dimensions.structure.ross123b;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import cpw.mods.fml.common.registry.GameRegistry;

/**
 * 锑罗斯的城市生成器。
 * <p>
 * 布局完全由 {@code (世界种子, 区块坐标)} 决定: 同一个区块无论何时生成、以什么顺序生成,
 * 结果都完全一样, 而且每个区块只写自己那 16×16, 从不写邻区块。这样既不会在生成过程中
 * 触发级联区块加载(历史上爆过 StackOverflowError), 也不会出现"从存档重新载入后建筑才长出来"
 * 或者"建筑被邻区块的液体灌掉一半"的问题。
 * <p>
 * 地形与路网:
 * <ul>
 * <li>地形以贴着海面的平原为主, 夹杂少量孤峰与盆地(湖/浅海), 平原上还会切出又长又窄的裂谷;</li>
 * <li>城市不再必定出现: 每隔 {@link #CITY_CELL} 个区块有一个候选中心, 只有那一片足够平坦、
 * 海拔又贴着海面时才会长出一座城;</li>
 * <li>道路只伴随城市出现: 城内每 {@link #PERIOD} 个区块(128 格)一条 16 格宽的主干道, 街区里还有随机断头支路;</li>
 * <li>道路铺在地形表面上: 陆地就是地面, 水里就沉到海床上, 不会像以前那样浮在 y=63 的水面;</li>
 * <li>跨水的路段有概率架桥: 桥面从一岸的高度拱到另一岸, 用石砖桥墩撑到海床, 桥上有路面;</li>
 * <li>每隔 {@link #POLE_SPACING} 格沿路立一根路灯(IC2 铁栅栏杆身 + 横担 + 石英绝缘子 + 杆顶红石灯,
 * 一半以上是歪的), 电线以后再加;</li>
 * <li>只有与道路正交相邻的区块(且自身不是道路)才是建筑地块, 所以建筑严格排在道路两侧。</li>
 * </ul>
 * 路面材质: 石头铺面, 石英块画中线。建筑随机为塔楼/多层楼/坍塌废墟, 带破洞/塌顶/断墙/残骸,
 * 其中约一成的墙面掺着 chisel 红石块; 地下部分一直延伸到基岩层, 随机为干(抽空)或湿(灌满 LCL)的地下室。
 */
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

    /**
     * 地形: 大部分是贴着海面的平原, 起伏很小; 少量丘陵/孤峰, 少量盆地(湖与浅海), 以及长条裂谷。
     * <p>
     * 平原噪声的格子边长; 平原的基准高度与起伏幅度(格)。
     */
    private static final int NOISE_COARSE = 64;
    private static final int NOISE_FINE = 20;
    private static final int PLAIN_OFFSET = 2;
    private static final int PLAIN_AMPLITUDE = 7;
    /** 丘陵/孤峰用两层脊状噪声相乘: 大尺度决定走向, 小尺度把它切碎。 */
    private static final int NOISE_RIDGE = 190;
    private static final int NOISE_RIDGE_FINE = 80;
    private static final int MOUNTAIN_HEIGHT = 32;
    /** 再叠一道低频"山脉分布"闸门: 只有它高的地方才允许隆起, 于是山成片成岭, 别处全是平原。 */
    private static final int NOISE_RIDGE_GATE = 420;
    private static final double RIDGE_SHARPNESS = 14.0;
    private static final double RIDGE_MODULATOR = 4.0;
    /** 盆地(湖/浅海): 低频噪声低于阈值的成片区域往下压, 越低于阈值压得越深。 */
    private static final int NOISE_BASIN = 300;
    private static final double BASIN_THRESHOLD = 0.2;
    private static final int BASIN_DEPTH = 34;
    /** 裂谷: 噪声零值线附近向下挖, 又长又窄, 中线最深。 */
    private static final int NOISE_RIFT = 300;
    /** 裂谷中心的扭曲: 用另一层噪声把采样点横向推来推去, 免得裂谷是一条笔直的缝。 */
    private static final int NOISE_RIFT_WARP = 140;
    private static final int RIFT_WARP = 44;
    private static final double RIFT_WIDTH = 0.025;
    private static final int RIFT_DEPTH = 26;
    /** 地表高度的硬边界, 防止噪声越界挖穿基岩或顶穿世界。 */
    private static final int TERRAIN_MIN = BASEMENT_BOTTOM + 4;
    private static final int TERRAIN_MAX = 200;

    /**
     * 城市: 每隔 {@link #CITY_CELL} 个区块一个候选中心, 半径 {@link #CITY_RADIUS_MIN} ~
     * {@link #CITY_RADIUS_MIN}+{@link #CITY_RADIUS_SPAN},
     * 只有当这一片地形足够平坦、且高度贴着海面时才真的长出一座城; 道路与楼只跟着城市走。
     */
    private static final int CITY_CELL = 32;
    private static final int CITY_RADIUS_MIN = 5;
    private static final int CITY_RADIUS_SPAN = 6;
    /** 平坦度采样间距(格)、允许的高度偏差(格)、以及必须落在偏差内的采样点比例(百分比)。 */
    private static final int CITY_SAMPLE_STEP = 8;
    private static final int CITY_FLAT_TOLERANCE = 4;
    private static final int CITY_FLAT_PERCENT = 88;
    /** 城市海拔必须贴着海面(否则楼会被埋进山里或被淹得看不见)。 */
    private static final int CITY_HEIGHT_BAND = 5;

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
    private static final int TREE_CHANCE = 55;
    private static final int BUSH_DENSITY = 3;
    private static final int GRAVE_CHANCE = 18;
    private static final int CRATER_CHANCE = 12;
    private static final int BONES_CHANCE = 7;
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
    }

    /** 临街地块: 一栋楼, 或者只剩残骸的空地。 */
    private static void stampPlot(Stamper stamper, int chunkX, int chunkZ, Terrain terrain) {
        int baseY = terrain.seaLevel + 1;
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
    }

    // ------------------------------------------------------------------
    // 城市的选址
    // ------------------------------------------------------------------

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
    private static final City NO_CITY = new City(0L, 0, 0, 0, 0, 0);

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
        CITY_CACHE.put(key, city == null ? NO_CITY : city);
        return city;
    }

    /**
     * 候选城市: 先按种子定下半径与中心, 再在整片范围上采样地形, 只有"高度普遍贴着海面、
     * 而且彼此差不多平"的地方才真的立城。这样城市不会落在山坡、深海或者裂谷正中间。
     */
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
        if (Math.abs(median - (seaLevel + 1)) > CITY_HEIGHT_BAND) {
            return null;
        }

        int within = 0;
        for (int i = 0; i < count; i++) {
            if (Math.abs(heights[i] - median) <= CITY_FLAT_TOLERANCE) {
                within++;
            }
        }
        if (within * 100 < count * CITY_FLAT_PERCENT) {
            return null;
        }
        return new City(seed, cellX, cellZ, centerChunkX, centerChunkZ, radius);
    }

    // ------------------------------------------------------------------
    // 路网
    // ------------------------------------------------------------------

    /**
     * 该区块的道路方向掩码: bit0 = 沿 Z 的道路, bit1 = 沿 X 的道路, 0 表示不是道路。
     * 同时具备两位就是路口。城市之外一律返回 0: 道路只伴随城市出现。
     */
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

    // ------------------------------------------------------------------
    // 地形
    // ------------------------------------------------------------------

    /**
     * 该列的地形表面高度(纯函数: 只取决于世界种子与坐标)。
     * <p>
     * 以平原为主: 平原噪声给出贴着海面的小起伏; 丘陵/孤峰用两层脊状噪声相乘, 只有两层脊线叠在一起
     * 的地方才隆起; 盆地用低频噪声的阈值切出成片的湖与浅海; 裂谷沿噪声零值线往下挖成长条深沟。
     */
    static int terrainHeight(long seed, int seaLevel, int x, int z) {
        double plain = (octave(seed, x, z, NOISE_COARSE, 601) * 0.65 + octave(seed, x, z, NOISE_FINE, 602) * 0.35) * 2.0
            - 1.0;
        double mountain = Math.pow(smoothRidge(octave(seed, x, z, NOISE_RIDGE, 603)), RIDGE_SHARPNESS)
            * Math.pow(smoothRidge(octave(seed, x, z, NOISE_RIDGE_FINE, 605)), RIDGE_MODULATOR);
        mountain *= clamp01((octave(seed, x, z, NOISE_RIDGE_GATE, 608) - 0.2) / 0.8);
        double basin = clamp01(1.0 - octave(seed, x, z, NOISE_BASIN, 607) / BASIN_THRESHOLD);
        int warpX = x + (int) Math.round((octave(seed, x, z, NOISE_RIFT_WARP, 609) - 0.5) * RIFT_WARP);
        double rift = riftCarve(octave(seed, warpX, z, NOISE_RIFT, 606));

        double height = seaLevel + PLAIN_OFFSET
            + plain * PLAIN_AMPLITUDE
            + mountain * MOUNTAIN_HEIGHT
            - basin * basin * BASIN_DEPTH
            - rift;
        int rounded = (int) Math.round(height);
        return Math.max(TERRAIN_MIN, Math.min(TERRAIN_MAX, rounded));
    }

    /** 裂谷: 噪声越接近 0.5 越深, 于是沿着一条条长曲线挖出又窄又深的沟。 */
    private static double riftCarve(double noise) {
        double t = 1.0 - Math.abs(noise - 0.5) / RIFT_WIDTH;
        if (t <= 0.0) {
            return 0.0;
        }
        return t * t * (3.0 - 2.0 * t) * RIFT_DEPTH;
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
                stamper.put(worldX, height - 1, worldZ, terrain.soil, terrain.soilMeta);
                boolean land = height >= terrain.seaLevel;
                stamper.put(
                    worldX,
                    height,
                    worldZ,
                    land ? terrain.grass : terrain.soil,
                    land ? terrain.grassMeta : terrain.soilMeta);
                for (int y = height + 1; y <= terrain.seaLevel; y++) {
                    stamper.put(worldX, y, worldZ, terrain.water, terrain.waterMeta);
                }
            }
        }
    }

    // ------------------------------------------------------------------
    // 道路与桥
    // ------------------------------------------------------------------

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
                stamper.put(worldX, surface, worldZ, lane ? LANE_BLOCK : ROAD_BLOCK, 0);
                if (bridge != null) {
                    stamper.put(worldX, surface - 1, worldZ, ROAD_BLOCK, 0); // 桥面板厚度
                }
            }
        }

        if (bridge != null) {
            stampBridgeSupports(stamper, chunkX, chunkZ, alongZ, bridge, terrain);
        } else {
            stampPoles(stamper, chunkX, chunkZ, mask, terrain);
        }
    }

    /**
     * 判断该道路区块是否落在一段值得架桥的水面上。
     * <p>
     * 沿道路中线向两侧找第一块陆地; 跨度太短就照旧把路铺在海床上, 跨度够长时按种子决定是否架桥。
     * 跨度内只要出现路口就放弃架桥(否则两条路的桥面会打在同一个区块里)。
     */
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

    // ------------------------------------------------------------------
    // 地表电线杆
    // ------------------------------------------------------------------

    /**
     * 沿路每 {@link #POLE_SPACING} 格立一根电线杆, 立在路的一侧(区块内坐标 {@link #POLE_LATERAL}),
     * 杆高 7~11 格, 有 {@link #POLE_LEAN_CHANCE}% 的概率歪斜; 顶部两根横担与石英绝缘子, 方向垂直于道路。
     * <p>
     * 杆脚直接坐在该列的地形表面上(陆地是地面, 水里是海床), 所以淹在水下的路段会变成一排露出红海的歪杆。
     * 电线以后再加: 杆顶横担两端的位置是固定的, 到时按相邻杆的横担端点连过去就行。
     */
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

    // ------------------------------------------------------------------
    // 地标: 巨型倾斜十字架
    // ------------------------------------------------------------------

    /**
     * 第三次冲击式的地标: 每 {@link #CROSS_CELL} 个区块有一个候选点, 按种子决定是否立一根巨大的、
     * 倾斜的鲜红十字架(发光石核心 + 黑曜石基座)。横臂高而短, 四个末端逐渐收窄成尖。
     * <p>
     * 十字架会跨区块, 所以每个区块检查自己周围 3×3 个候选点, 只写落在自己范围内的方块; 候选点的
     * 位置、尺寸、倾斜方向都是"种子 + 候选格坐标"的纯函数, 相邻区块看到的十字架完全一致。
     */
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

    /**
     * 十字架主体: Chisel 的 redstone_block(meta 1 = 光滑红石块)最鲜红, 找不到就退回原版红石块。
     */
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

    // ------------------------------------------------------------------
    // 建筑
    // ------------------------------------------------------------------

    /** 按地块坐标决定这一栋楼的类型与尺寸; 返回 null 表示空地。 */
    private static Building plan(int chunkX, int chunkZ, int baseY, long seed) {
        if (rnd(seed, chunkX, chunkZ, 201) % 100 < EMPTY_LOT_CHANCE) {
            return null;
        }

        Building building = new Building();
        building.baseY = baseY;
        building.width = MIN_FOOTPRINT + rnd(seed, chunkX, chunkZ, 202) % FOOTPRINT_SPAN;
        building.length = MIN_FOOTPRINT + rnd(seed, chunkX, chunkZ, 203) % FOOTPRINT_SPAN;
        building.x0 = 1 + rnd(seed, chunkX, chunkZ, 204) % (15 - building.width);
        building.z0 = 1 + rnd(seed, chunkX, chunkZ, 205) % (15 - building.length);

        int roll = rnd(seed, chunkX, chunkZ, 206) % 100;
        building.kind = roll < 15 ? KIND_RUIN : roll < 58 ? KIND_BLOCK : KIND_TOWER;
        building.damage = 25 + rnd(seed, chunkX, chunkZ, 207) % 70;
        building.palette = rnd(seed, chunkX, chunkZ, 208) % 3;

        if (building.kind == KIND_RUIN) {
            building.height = 2 + rnd(seed, chunkX, chunkZ, 209) % 3;
        } else if (building.kind == KIND_BLOCK) {
            building.height = 12 + rnd(seed, chunkX, chunkZ, 210) % 22;
        } else {
            building.height = 34 + rnd(seed, chunkX, chunkZ, 211) % 56;
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

    /**
     * 地下部分: 楼体一直延伸到基岩层(最低一层楼板压在 y=2), 形成地下室。
     * <p>
     * 内部随机灌满 LCL(湿)或全部抽空(干); 楼板从 y=2 开始每 {@link #FLOOR_HEIGHT} 一层,
     * 竖井位置留空, 于是地面层能看到、也能下到地下。塌掉的废墟只保留底层楼板。
     */
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

                    if (windowBand && ((i + j) & 1) == 0) {
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
                stamper.rubble(worldX, ground, worldZ);
            }
        }
    }

    // ------------------------------------------------------------------
    // 地物: 第三次冲击之后留在荒野上的东西
    // ------------------------------------------------------------------

    /** 荒野地物总入口: 弹坑、枯树、枯草、墓碑、天使骸骨, 全按种子独立决定。 */
    private static void stampWilderness(Stamper stamper, int chunkX, int chunkZ, Terrain terrain) {
        stampCrater(stamper, chunkX, chunkZ, terrain);
        stampDeadTrees(stamper, chunkX, chunkZ, terrain, TREE_CHANCE);
        stampDeadBushes(stamper, chunkX, chunkZ, terrain);
        stampGraveCrosses(stamper, chunkX, chunkZ, terrain);
        stampAngelBones(stamper, chunkX, chunkZ, terrain);
    }

    private static void stampDeadTrees(Stamper stamper, int chunkX, int chunkZ, Terrain terrain, int chance) {
        long seed = stamper.seed;
        if (rnd(seed, chunkX, chunkZ, 1101) % 100 >= chance) {
            return;
        }
        int count = 1 + rnd(seed, chunkX, chunkZ, 1102) % 3;
        for (int i = 0; i < count; i++) {
            int x = (chunkX << 4) + 2 + rnd(seed, chunkX, chunkZ, 1110 + i) % 12;
            int z = (chunkZ << 4) + 2 + rnd(seed, chunkX, chunkZ, 1120 + i) % 12;
            stampDeadTree(stamper, x, z, terrain, rnd(seed, chunkX, chunkZ, 1130 + i));
        }
    }

    /** 一棵完全枯死的树: 立着的(带断枝) / 只剩树桩 / 已经倒下的, 一部分从根烧成焦炭。 */
    private static void stampDeadTree(Stamper stamper, int x, int z, Terrain terrain, int hash) {
        long seed = stamper.seed;
        int ground = terrainHeight(seed, terrain.seaLevel, x, z) + 1;
        int meta = (hash >>> 16) & 1;
        boolean charred = (hash >>> 8) % 100 < 30;
        int roll = hash % 100;
        if (roll < 12) {
            stampFallenTrunk(stamper, x, z, ground, hash, meta, charred);
            return;
        }
        int height = roll < 38 ? 1 + (hash >>> 4) % 3 : 4 + (hash >>> 4) % 6;
        boolean leanOnX = ((hash >>> 12) & 1) == 0;
        int leanAt = Math.max(1, height - 2);
        for (int i = 0; i < height; i++) {
            boolean bend = i >= leanAt;
            int px = leanOnX && bend ? x + 1 : x;
            int pz = !leanOnX && bend ? z + 1 : z;
            stamper.put(px, ground + i, pz, charred && i < 2 ? Blocks.coal_block : Blocks.log, meta);
        }
        int branches = 2 + (hash >>> 6) % 3;
        for (int b = 0; b < branches; b++) {
            int branchHash = rnd(seed, x + b * 7, z - b * 13, 1150);
            int span = Math.max(1, height - 2);
            int y = ground + 1 + branchHash % span;
            boolean alongX = ((branchHash >>> 7) & 1) == 0;
            int dir = ((branchHash >>> 4) & 1) == 0 ? 1 : -1;
            int length = 2 + (branchHash >>> 5) % 2;
            boolean bent = y - ground >= leanAt;
            int bx = leanOnX && bent ? x + 1 : x;
            int bz = !leanOnX && bent ? z + 1 : z;
            for (int t = 1; t <= length; t++) {
                int px = alongX ? bx + dir * t : bx;
                int pz = alongX ? bz : bz + dir * t;
                stamper.put(px, y + (t == length ? 1 : 0), pz, Blocks.log, meta);
            }
        }
    }

    private static void stampFallenTrunk(Stamper stamper, int x, int z, int ground, int hash, int meta,
        boolean charred) {
        boolean alongX = ((hash >>> 10) & 1) == 0;
        int dir = ((hash >>> 13) & 1) == 0 ? 1 : -1;
        int length = 3 + (hash >>> 11) % 4;
        for (int i = 0; i < length; i++) {
            int step = dir > 0 ? i : -i;
            int px = alongX ? x + step : x;
            int pz = alongX ? z : z + step;
            stamper.put(px, ground, pz, charred && i % 3 == 0 ? Blocks.coal_block : Blocks.log, meta);
        }
        int stubStep = dir > 0 ? -1 : 1;
        int sx = alongX ? x + stubStep : x;
        int sz = alongX ? z : z + stubStep;
        stamper.put(sx, ground, sz, Blocks.log, meta);
        stamper.put(sx, ground + 1, sz, Blocks.log, meta);
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
                if (ground < terrain.seaLevel) {
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
        int count = 3 + rnd(seed, chunkX, chunkZ, 1173) % 4;
        for (int i = 0; i < count; i++) {
            int x = originX + rnd(seed, chunkX, chunkZ, 1180 + i) % 5 - 2;
            int z = originZ + rnd(seed, chunkX, chunkZ, 1190 + i) % 5 - 2;
            int ground = terrainHeight(seed, terrain.seaLevel, x, z);
            if (ground < terrain.seaLevel) {
                continue;
            }
            int hash = rnd(seed, x, z, 1200);
            int height = 2 + hash % 2;
            if ((hash >>> 5) % 100 < 22) {
                for (int t = 0; t <= height; t++) {
                    stamper.put(x, ground, z - t, Blocks.planks, 5);
                }
                continue;
            }
            int shift = (hash >>> 3) % 100 < 40 ? 1 : 0;
            for (int t = 0; t < height; t++) {
                stamper.put(x + (t >= height - 1 ? shift : 0), ground + t, z, Blocks.planks, 5);
            }
            int barY = ground + height - 1;
            stamper.put(x - 1 + shift, barY, z, Blocks.planks, 5);
            stamper.put(x + 1 + shift, barY, z, Blocks.planks, 5);
        }
    }

    /** 弹坑: 把地表往下挖成一个碗, 坑底铺黑曜石/碎石, 低于海面的部分重新灌满 LCL。 */
    private static void stampCrater(Stamper stamper, int chunkX, int chunkZ, Terrain terrain) {
        long seed = stamper.seed;
        if (rnd(seed, chunkX, chunkZ, 1210) % 100 >= CRATER_CHANCE) {
            return;
        }
        int radius = 2 + rnd(seed, chunkX, chunkZ, 1211) % 3;
        int depth = 1 + rnd(seed, chunkX, chunkZ, 1212) % 3;
        int margin = radius + 1;
        int centerX = (chunkX << 4) + margin + rnd(seed, chunkX, chunkZ, 1213) % (16 - margin * 2);
        int centerZ = (chunkZ << 4) + margin + rnd(seed, chunkX, chunkZ, 1214) % (16 - margin * 2);
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                double distance = Math.sqrt(dx * dx + dz * dz) / radius;
                if (distance > 1.0D) {
                    continue;
                }
                int worldX = centerX + dx;
                int worldZ = centerZ + dz;
                int ground = terrainHeight(seed, terrain.seaLevel, worldX, worldZ);
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

    /** 天使骸骨: 半埋在地里的巨大肋骨 —— 脊椎 + 一排向下弯的肋骨 + 一颗带眼窝的头骨。 */
    private static void stampAngelBones(Stamper stamper, int chunkX, int chunkZ, Terrain terrain) {
        long seed = stamper.seed;
        if (rnd(seed, chunkX, chunkZ, 1220) % 100 >= BONES_CHANCE) {
            return;
        }
        boolean alongX = ((rnd(seed, chunkX, chunkZ, 1221) >>> 3) & 1) == 0;
        int length = 6 + rnd(seed, chunkX, chunkZ, 1222) % 4;
        int baseX = (chunkX << 4) + 5 + rnd(seed, chunkX, chunkZ, 1223) % 6;
        int baseZ = (chunkZ << 4) + 5 + rnd(seed, chunkX, chunkZ, 1224) % 6;
        int spineY = terrainHeight(seed, terrain.seaLevel, baseX, baseZ) + 2;
        for (int i = 0; i < length; i++) {
            int x = alongX ? baseX + i : baseX;
            int z = alongX ? baseZ : baseZ + i;
            stamper.put(x, spineY, z, Blocks.quartz_block, i % 3 == 0 ? 1 : 0);
            if (i % 2 != 0) {
                continue;
            }
            for (int side = -1; side <= 1; side += 2) {
                for (int rib = 1; rib <= 3; rib++) {
                    if (noise(seed, x * 3 + rib, spineY - rib, z * 5 - rib, 1225) % 100 < 14) {
                        continue;
                    }
                    int px = alongX ? x : x + side * rib;
                    int pz = alongX ? z + side * rib : z;
                    stamper.put(px, spineY - (rib - 1), pz, Blocks.quartz_block, 0);
                }
            }
        }
        int headX = alongX ? baseX - 2 : baseX;
        int headZ = alongX ? baseZ : baseZ - 2;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (int dy = 0; dy < 2; dy++) {
                    boolean socket = dy == 1 && dz == -1 && dx != 0;
                    if (!socket) {
                        stamper.put(headX + dx, spineY + dy, headZ + dz, Blocks.quartz_block, 0);
                    }
                }
            }
        }
    }
    // ------------------------------------------------------------------
    // 工具
    // ------------------------------------------------------------------

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
                default -> put(worldX, y, worldZ, hash < 28 ? Blocks.mossy_cobblestone : Blocks.cobblestone, 0);
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
                default -> put(worldX, y, worldZ, Blocks.glass, 0);
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
