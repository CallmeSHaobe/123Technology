package com.newmaa.othtech.common.dimensions.antimonia.farlands;

import java.util.Random;

import com.newmaa.othtech.Config;

/**
 * 锑星边境之地（Far Lands）：人为把噪声地形生成器「撑爆」。
 * <p>
 * 经典边境之地的边界 12,550,820 其实是一个溢出点：噪声坐标 = 方块坐标 * (684.412 / 4) = 方块坐标 * 171.103，
 * 
 * <pre>
 *     12,550,820 * 171.103 = 2,147,482,594  →  正好顶到 2^31 (Integer.MAX_VALUE)
 * </pre>
 * 
 * 越过边界之后，两个溢出点会被同时触发：
 * <ol>
 * <li>{@code (int)} 强转饱和在 2^31-1，坐标减去强转结果剩下的「残差」是 10^10 量级的巨大数，
 * 它被原样拿去当 perlin 的插值权重，噪声密度直接爆掉，只剩符号还有意义；</li>
 * <li>坐标转 {@code float} 之后尾数只剩 256 / 512 的台阶，格点的梯度索引退化成伪随机序列。</li>
 * </ol>
 * 结果就是经典边境之地的三种形态：
 * <ul>
 * <li>只越过 x（或 z）：越界那条轴变成 1~2 格宽的柱子，高度直接顶到 255 或掉到 0（石墙 + 隧道），
 * 另一条轴仍是平滑 perlin，于是形成沿该轴延伸的长条山脊；</li>
 * <li>两个轴都越过：棋盘 / 对角条纹；</li>
 * <li>紧贴边界的那几格：直接立成一堵断崖。</li>
 * </ul>
 */
public final class AntimoniaFarLands {

    /** 经典边境之地坐标：噪声坐标 * 171.103 恰好顶到 2^31。 */
    public static final int DEFAULT_BOUNDARY = 12_550_820;

    /** 与 vanilla 主密度噪声一致的缩放：684.412 / 4。 */
    private static final double NOISE_SCALE = 684.412D / 4.0D;

    /** 贴边界的这几格直接立成断崖。 */
    private static final int CLIFF_WIDTH = 5;

    private static long cachedSeed = Long.MIN_VALUE;
    private static Octave[] cachedOctaves;

    private AntimoniaFarLands() {}

    public static boolean enabled() {
        return Config.enableAntimoniaFarLands;
    }

    public static int boundary() {
        return Math.max(64, Config.farLandsBoundary);
    }

    /** 该列是否已经越过边境。 */
    public static boolean isFar(int worldX, int worldZ, int boundary) {
        return Math.abs((long) worldX) >= boundary || Math.abs((long) worldZ) >= boundary;
    }

    /**
     * 越界列的实心高度（0..255）。高于该高度是空气（低于海平面则填液体）。
     */
    public static int heightAt(int worldX, int worldZ, long seed, int boundary) {
        boolean farX = Math.abs((long) worldX) >= boundary;
        boolean farZ = Math.abs((long) worldZ) >= boundary;
        Octave[] octaves = octaves(seed);

        // 溢出后的密度：量级 1e10 以上，只有符号是有效信息
        double density = 0.0D;
        double scale = 1.0D;
        for (Octave octave : octaves) {
            density += octave.noise(worldX * NOISE_SCALE * scale, worldZ * NOISE_SCALE * scale, farX, farZ);
            scale /= 2.0D;
        }

        // 一点没被溢出的细节，免得高度只有两档
        double detail = octaves[octaves.length - 1].noise(worldX * 0.35D, worldZ * 0.35D, false, false);
        int wall = 248 - (int) (Math.abs(detail) * 40.0D);
        int tunnel = 12 + (int) (Math.abs(detail) * 40.0D);
        int height = density > 0.0D ? wall : tunnel;

        // 边界那几格立成一堵断崖，接缝才像 classic 的「墙」
        long distance = Long.MAX_VALUE;
        if (farX) {
            distance = Math.min(distance, Math.abs((long) worldX) - boundary);
        }
        if (farZ) {
            distance = Math.min(distance, Math.abs((long) worldZ) - boundary);
        }
        if (distance < CLIFF_WIDTH) {
            height = 255;
        }
        return height;
    }

    private static synchronized Octave[] octaves(long seed) {
        if (cachedOctaves == null || cachedSeed != seed) {
            Random random = new Random(seed);
            cachedOctaves = new Octave[] { new Octave(random), new Octave(random), new Octave(random) };
            cachedSeed = seed;
        }
        return cachedOctaves;
    }

    /** 一个八度的梯度噪声，带两个溢出点。 */
    private static final class Octave {

        private final int[] permutation = new int[512];
        private final double offsetX;
        private final double offsetZ;

        private Octave(Random random) {
            int[] table = new int[256];
            for (int i = 0; i < 256; i++) {
                table[i] = i;
            }
            for (int i = 255; i > 0; i--) {
                int j = random.nextInt(i + 1);
                int tmp = table[i];
                table[i] = table[j];
                table[j] = tmp;
            }
            for (int i = 0; i < 512; i++) {
                permutation[i] = table[i & 255];
            }
            offsetX = random.nextDouble() * 256.0D;
            offsetZ = random.nextDouble() * 256.0D;
        }

        private double noise(double scaledX, double scaledZ, boolean farX, boolean farZ) {
            double x = scaledX + offsetX;
            double z = scaledZ + offsetZ;

            // 溢出点 B：float 量化后的位模式 = 该量级下还能分辨的格点索引，越界后每方块都变
            int latticeX = farX ? floatLattice(x) : floor(x);
            int latticeZ = farZ ? floatLattice(z) : floor(z);
            // 溢出点 A：(int) 强转饱和后的巨残差，直接当插值权重
            double fracX = farX ? overflowFraction(x) : fraction(x);
            double fracZ = farZ ? overflowFraction(z) : fraction(z);

            int x0 = latticeX & 255;
            int z0 = latticeZ & 255;
            int a = permutation[x0] + z0;
            int b = permutation[x0 + 1] + z0;

            double n00 = gradient(permutation[a], fracX, fracZ);
            double n10 = gradient(permutation[b], fracX - 1.0D, fracZ);
            double n01 = gradient(permutation[a + 1], fracX, fracZ - 1.0D);
            double n11 = gradient(permutation[b + 1], fracX - 1.0D, fracZ - 1.0D);

            double u = fade(fracX);
            double v = fade(fracZ);
            return lerp(v, lerp(u, n00, n10), lerp(u, n01, n11));
        }

        private static int floatLattice(double coordinate) {
            return Float.floatToIntBits((float) coordinate) & 0x7FFFFFFF;
        }

        private static double overflowFraction(double coordinate) {
            return coordinate - (double) (int) coordinate;
        }

        private static double fraction(double coordinate) {
            return coordinate - Math.floor(coordinate);
        }

        private static int floor(double value) {
            double floored = Math.floor(value);
            if (floored > Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            }
            if (floored < Integer.MIN_VALUE) {
                return Integer.MIN_VALUE;
            }
            return (int) floored;
        }

        private static double gradient(int hash, double x, double z) {
            switch (hash & 7) {
                case 0:
                    return x + z;
                case 1:
                    return x - z;
                case 2:
                    return -x + z;
                case 3:
                    return -x - z;
                case 4:
                    return x;
                case 5:
                    return -x;
                case 6:
                    return z;
                default:
                    return -z;
            }
        }

        private static double fade(double t) {
            return t * t * t * (t * (t * 6.0D - 15.0D) + 10.0D);
        }

        private static double lerp(double t, double a, double b) {
            return a + t * (b - a);
        }
    }
}
