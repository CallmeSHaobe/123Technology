package com.newmaa.othtech.common.dimensions.structure.ross123b;

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
 * <li>用两级值噪声生成地形, 大部分区域低于海面(仍是 LCL 海), 只有少数岛屿/浅滩露出水面;</li>
 * <li>每 {@link #PERIOD} 个区块(128 格)一条 16 格宽的主干道, 街区里还有随机断头支路;</li>
 * <li>道路铺在地形表面上: 陆地就是地面, 水里就沉到海床上, 不会像以前那样浮在 y=63 的水面;</li>
 * <li>跨水的路段有概率架桥: 桥面从一岸的高度拱到另一岸, 用石砖桥墩撑到海床, 桥上有路面;</li>
 * <li>每隔 {@link #POLE_SPACING} 格沿路立一根地表电线杆(深色橡木杆身 + 横担 + 石英绝缘子,
 * 一半以上是歪的), 淹在水下的路段就是一排露出红海的歪杆, 电线以后再加;</li>
 * <li>只有与道路正交相邻的区块(且自身不是道路)才是建筑地块, 所以建筑严格排在道路两侧。</li>
 * </ul>
 * 路面材质: 石头铺面, 石英块画中线。建筑随机为塔楼/多层楼/坍塌废墟, 带破洞/塌顶/断墙/残骸,
 * 地下部分一直延伸到基岩层, 随机为干(抽空)或湿(灌满 LCL)的地下室。
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

    /** 地形噪声: 粗/细两级格子边长(方块), 以及相对海面的高度偏移与振幅。 */
    private static final int NOISE_COARSE = 64;
    private static final int NOISE_FINE = 20;
    private static final int TERRAIN_OFFSET = -10;
    private static final int TERRAIN_AMPLITUDE = 14;

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
    /** 杆体/横担用深色橡木, 绝缘子用石英。 */
    private static final Block POLE_BLOCK = Blocks.planks;
    private static final int POLE_META = 5;
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
    private static final BlockMeta CROSS_BASE = new BlockMeta(Blocks.obsidian, 0);

    private static final int KIND_RUIN = 1;
    private static final int KIND_BLOCK = 2;
    private static final int KIND_TOWER = 3;

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

        int mask = roadMask(chunkX, chunkZ, seed);
        if (mask != 0) {
            stampRoad(stamper, chunkX, chunkZ, mask, terrain);
        } else if (isPlotChunk(chunkX, chunkZ, seed)) {
            stampPlot(stamper, chunkX, chunkZ, terrain);
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
    // 路网
    // ------------------------------------------------------------------

    /**
     * 该区块的道路方向掩码: bit0 = 沿 Z 的道路, bit1 = 沿 X 的道路, 0 表示不是道路。
     * 同时具备两位就是路口。
     */
    static int roadMask(int chunkX, int chunkZ, long seed) {
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
        if ((mask & 1) == 0 && isVerticalSpur(seed, blockX, blockZ, localX, localZ)) {
            mask |= 1;
        }
        if ((mask & 2) == 0 && isHorizontalSpur(seed, blockX, blockZ, localX, localZ)) {
            mask |= 2;
        }
        return mask;
    }

    /** 该区块是不是道路(主干道或支路)。 */
    static boolean isRoadChunk(int chunkX, int chunkZ, long seed) {
        return roadMask(chunkX, chunkZ, seed) != 0;
    }

    /** 横向支路: 从街区左/右侧的主干道伸进来, 到某个随机的行、随机长度为止(断头)。 */
    private static boolean isHorizontalSpur(long seed, int blockX, int blockZ, int localX, int localZ) {
        int hash = rnd(seed, blockX, blockZ, 101);
        if (hash % 100 >= BRANCH_CHANCE) {
            return false;
        }
        int row = 2 + (hash >>> 7) % 5;
        if (localZ != row) {
            return false;
        }
        int length = 2 + (hash >>> 11) % 5;
        return ((hash >>> 16) & 1) == 0 ? localX <= length : localX >= PERIOD - length;
    }

    /** 纵向支路, 规则同上。 */
    private static boolean isVerticalSpur(long seed, int blockX, int blockZ, int localX, int localZ) {
        int hash = rnd(seed, blockX, blockZ, 202);
        if (hash % 100 >= BRANCH_CHANCE) {
            return false;
        }
        int column = 2 + (hash >>> 7) % 5;
        if (localX != column) {
            return false;
        }
        int length = 2 + (hash >>> 11) % 5;
        return ((hash >>> 16) & 1) == 0 ? localZ <= length : localZ >= PERIOD - length;
    }

    /** 临街地块: 自身不是道路, 且上下左右至少有一个邻区块是道路。 */
    static boolean isPlotChunk(int chunkX, int chunkZ, long seed) {
        if (isRoadChunk(chunkX, chunkZ, seed)) {
            return false;
        }
        return isRoadChunk(chunkX - 1, chunkZ, seed) || isRoadChunk(chunkX + 1, chunkZ, seed)
            || isRoadChunk(chunkX, chunkZ - 1, seed)
            || isRoadChunk(chunkX, chunkZ + 1, seed);
    }

    // ------------------------------------------------------------------
    // 地形
    // ------------------------------------------------------------------

    /** 该列的地形表面高度(纯函数: 只取决于世界种子与坐标)。 */
    static int terrainHeight(long seed, int seaLevel, int x, int z) {
        double noise = octave(seed, x, z, NOISE_COARSE, 601) * 0.65 + octave(seed, x, z, NOISE_FINE, 602) * 0.35;
        return seaLevel + TERRAIN_OFFSET + (int) Math.round(noise * TERRAIN_AMPLITUDE);
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
        if (containsJunction(seed, chunkX, chunkZ, alongZ, left, right)) {
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

    private static boolean containsJunction(long seed, int chunkX, int chunkZ, boolean alongZ, int left, int right) {
        int from = Math.floorDiv(Math.min(left, right), 16);
        int to = Math.floorDiv(Math.max(left, right), 16);
        for (int chunk = from; chunk <= to; chunk++) {
            if (roadMask(alongZ ? chunkX : chunk, alongZ ? chunk : chunkZ, seed) == 3) {
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
            stamper.put(px, baseY + i, pz, POLE_BLOCK, POLE_META);
        }

        // 横担: 垂直于道路走向, 跟着杆身在这一高度的倾斜偏移走
        int armIndex = height - 2;
        int armOffset = lean ? (armIndex * leanDir) / leanEvery : 0;
        int armX = leanOnX ? x + armOffset : x;
        int armZ = leanOnX ? z : z + armOffset;
        int armY = baseY + armIndex;
        for (int t = -2; t <= 2; t++) {
            if (roadAlongZ) {
                stamper.put(armX + t, armY, armZ, POLE_BLOCK, POLE_META);
            } else {
                stamper.put(armX, armY, armZ + t, POLE_BLOCK, POLE_META);
            }
        }
        int arm2Y = armY - 2;
        for (int t = -1; t <= 1; t++) {
            if (roadAlongZ) {
                stamper.put(armX + t, arm2Y, armZ, POLE_BLOCK, POLE_META);
            } else {
                stamper.put(armX, arm2Y, armZ + t, POLE_BLOCK, POLE_META);
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
            int plinth = Math.max(4, this.thickness);
            int beamIndex = (this.height * CROSS_BEAM_HEIGHT) / 100;
            int beamHalf = (this.height * CROSS_BEAM_HALF) / 10;

            // 黑曜石基座
            box(stamper, this.centerX, this.baseY, this.centerZ, plinth, 0, plinth, CROSS_BASE);

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
