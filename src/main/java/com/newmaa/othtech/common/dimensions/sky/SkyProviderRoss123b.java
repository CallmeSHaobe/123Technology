package com.newmaa.othtech.common.dimensions.sky;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.newmaa.othtech.common.dimensions.ResourcesDimensions;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import galaxyspace.core.render.sky.SkyProviderBase;

/**
 * OMG 有GPT?!
 * 也许GPT修好了.
 * biebie.
 */

@SideOnly(Side.CLIENT)
public class SkyProviderRoss123b extends SkyProviderBase implements ResourcesDimensions {

    public SkyProviderRoss123b() {}

    /** 天空里黑之月所在的深度平面。 */
    private static final double MOON_Z = -100.0D;
    /** 画圆盘/光带用的细分数。 */
    private static final int SEGMENTS = 96;
    /** 贯穿天空的红色光带: 半径、半宽(格)、以及中心处的不透明度。 */
    private static final float BAND_RADIUS = 100.0F;
    private static final float BAND_HALF_WIDTH = 6.0F;
    private static final float BAND_ALPHA = 0.3F;
    /** 银河: 光带半宽(1.0 = 90°)、光带条数、星点数量与星空半径。 */
    private static final double GALAXY_BAND = 0.46D;
    private static final int GALAXY_STRIPS = 10;
    private static final int GALAXY_STARS = 2600;
    private static final int GALAXY_BRIGHT_STARS = 400;
    /** 铺满整个天球的散星, 让天顶不是纯黑。 */
    private static final int SCATTER_STARS = 900;
    private static final double GALAXY_RADIUS = 100.0D;
    /** 银河盘面的法线, 决定这条银河斜跨天空的角度。 */
    private static final double[] AXIS_N = normalize(0.24D, 0.58D, 0.78D);
    private static final double[] AXIS_A = normalize(cross(AXIS_N, new double[] { 0.0D, 1.0D, 0.0D }));
    private static final double[] AXIS_B = cross(AXIS_N, AXIS_A);

    private static final double[] STAR_DIRS = new double[GALAXY_STARS * 3];
    private static final float[] STAR_COLORS = new float[GALAXY_STARS * 3];
    private static final double[] BRIGHT_DIRS = new double[GALAXY_BRIGHT_STARS * 3];
    private static final float[] BRIGHT_COLORS = new float[GALAXY_BRIGHT_STARS * 3];
    private static final double[] SCATTER_DIRS = new double[SCATTER_STARS * 3];
    private static final float[] SCATTER_COLORS = new float[SCATTER_STARS * 3];

    static {
        Random random = new Random(0x5EEDBEEFL);
        fillStars(random, STAR_DIRS, STAR_COLORS, 0.22D);
        fillStars(random, BRIGHT_DIRS, BRIGHT_COLORS, 0.10D);
        fillScatteredStars(random);
    }

    /**
     * 星星永远按满亮度画: 这是个没有太阳的死掉的世界, 原版那套"按天色明暗"的亮度在这里会让星空一半时间消失。
     * 画完星星再叠上银河。
     */
    @Override
    protected void renderStarList(float partialTicks, WorldClient world) {
        renderStarListFullBrighness(partialTicks, world);
        renderGalaxy(partialTicks, world);
    }

    /**
     * 父类给星系/星云用的那几张牌子是黑底的贴图, 直接画出来在红天上是几团黑斑(之前就是这么翻车的),
     * 所以这里关掉它, 银河改由 {@link #renderGalaxy} 自己画。
     */
    @Override
    protected void drawTexture(ResourceLocation texture, double radius) {}

    /** 银河: 一条斜跨天球的亮带(十层渐变光晕) + 大量集中在带内的星点, 跟着星空一起转。 */
    private void renderGalaxy(float partialTicks, WorldClient world) {
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_POINT_BIT);
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        // 加法混合: 星点越叠越亮, 才有"璀璨"的味道
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(world.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(-19.0F, 0.0F, 1.0F, 0.0F);

        drawGalaxyGlow();
        drawGalaxyStars(SCATTER_DIRS, SCATTER_COLORS, 1.8F);
        drawGalaxyStars(STAR_DIRS, STAR_COLORS, 2.5F);
        drawGalaxyStars(BRIGHT_DIRS, BRIGHT_COLORS, 3.5F);

        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private static void drawGalaxyGlow() {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        for (int i = 0; i < SEGMENTS; i++) {
            double angle0 = Math.PI * 2.0D * i / SEGMENTS;
            double angle1 = Math.PI * 2.0D * (i + 1) / SEGMENTS;
            for (int strip = 0; strip < GALAXY_STRIPS; strip++) {
                double offset0 = -GALAXY_BAND + 2.0D * GALAXY_BAND * strip / GALAXY_STRIPS;
                double offset1 = -GALAXY_BAND + 2.0D * GALAXY_BAND * (strip + 1) / GALAXY_STRIPS;
                float alpha0 = galaxyAlpha(offset0);
                float alpha1 = galaxyAlpha(offset1);
                addGalaxyVertex(tessellator, angle0, offset0, alpha0);
                addGalaxyVertex(tessellator, angle1, offset0, alpha0);
                addGalaxyVertex(tessellator, angle1, offset1, alpha1);
                addGalaxyVertex(tessellator, angle0, offset1, alpha1);
            }
        }
        tessellator.draw();
    }

    private static void addGalaxyVertex(Tessellator tessellator, double angle, double offset, float alpha) {
        double x = Math.cos(angle) * AXIS_A[0] + Math.sin(angle) * AXIS_B[0] + offset * AXIS_N[0];
        double y = Math.cos(angle) * AXIS_A[1] + Math.sin(angle) * AXIS_B[1] + offset * AXIS_N[1];
        double z = Math.cos(angle) * AXIS_A[2] + Math.sin(angle) * AXIS_B[2] + offset * AXIS_N[2];
        double length = Math.sqrt(x * x + y * y + z * z);
        float edge = (float) Math.min(1.0D, Math.abs(offset) / GALAXY_BAND);
        tessellator.setColorRGBA_F(0.62F - 0.26F * edge, 0.68F - 0.34F * edge, 0.98F - 0.38F * edge, alpha);
        tessellator.addVertex(x / length * GALAXY_RADIUS, y / length * GALAXY_RADIUS, z / length * GALAXY_RADIUS);
    }

    /** 光带中心最亮, 往两侧按高斯衰减到完全透明。 */
    private static float galaxyAlpha(double offset) {
        double falloff = offset / (GALAXY_BAND * 0.52D);
        return (float) (0.50D * Math.exp(-falloff * falloff));
    }

    private static void drawGalaxyStars(double[] dirs, float[] colors, float pointSize) {
        GL11.glPointSize(pointSize);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(GL11.GL_POINTS);
        for (int i = 0; i < dirs.length / 3; i++) {
            tessellator.setColorRGBA_F(colors[i * 3], colors[i * 3 + 1], colors[i * 3 + 2], 1.0F);
            tessellator.addVertex(
                dirs[i * 3] * GALAXY_RADIUS,
                dirs[i * 3 + 1] * GALAXY_RADIUS,
                dirs[i * 3 + 2] * GALAXY_RADIUS);
        }
        tessellator.draw();
    }

    /** 在银河盘面附近撒星点: 沿带子随机取角度, 垂直方向按高斯偏移, 越靠中线越密。 */
    private static void fillStars(Random random, double[] dirs, float[] colors, double spread) {
        for (int i = 0; i < dirs.length / 3; i++) {
            double angle = random.nextDouble() * Math.PI * 2.0D;
            double offset = gaussian(random) * spread;
            double x = Math.cos(angle) * AXIS_A[0] + Math.sin(angle) * AXIS_B[0] + offset * AXIS_N[0];
            double y = Math.cos(angle) * AXIS_A[1] + Math.sin(angle) * AXIS_B[1] + offset * AXIS_N[1];
            double z = Math.cos(angle) * AXIS_A[2] + Math.sin(angle) * AXIS_B[2] + offset * AXIS_N[2];
            double length = Math.sqrt(x * x + y * y + z * z);
            dirs[i * 3] = x / length;
            dirs[i * 3 + 1] = y / length;
            dirs[i * 3 + 2] = z / length;

            float warm = random.nextFloat();
            float brightness = 0.45F + 0.55F * random.nextFloat();
            colors[i * 3] = (0.86F + 0.14F * warm) * brightness;
            colors[i * 3 + 1] = (0.90F + 0.08F * warm) * brightness;
            colors[i * 3 + 2] = (1.0F - 0.10F * warm) * brightness;
        }
    }

    /** 天球上均匀撒一层暗一些的散星(高斯取三个分量再归一化, 方向就是球面均匀的)。 */
    private static void fillScatteredStars(Random random) {
        for (int i = 0; i < SCATTER_STARS; i++) {
            double x = random.nextGaussian();
            double y = random.nextGaussian();
            double z = random.nextGaussian();
            double length = Math.sqrt(x * x + y * y + z * z);
            SCATTER_DIRS[i * 3] = x / length;
            SCATTER_DIRS[i * 3 + 1] = y / length;
            SCATTER_DIRS[i * 3 + 2] = z / length;

            float brightness = 0.30F + 0.55F * random.nextFloat();
            SCATTER_COLORS[i * 3] = 0.82F * brightness;
            SCATTER_COLORS[i * 3 + 1] = 0.86F * brightness;
            SCATTER_COLORS[i * 3 + 2] = 0.96F * brightness;
        }
    }

    private static double gaussian(Random random) {
        return (random.nextDouble() + random.nextDouble() + random.nextDouble() - 1.5D) * 1.4D;
    }

    private static double[] normalize(double x, double y, double z) {
        double length = Math.sqrt(x * x + y * y + z * z);
        return new double[] { x / length, y / length, z / length };
    }

    private static double[] normalize(double[] vector) {
        return normalize(vector[0], vector[1], vector[2]);
    }

    private static double[] cross(double[] a, double[] b) {
        return new double[] { a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2], a[0] * b[1] - a[1] * b[0] };
    }

    /**
     * 黑之月 + 横贯天空的红色光带 —— 第三次冲击后挂在天上的那颗, 以及它前方那道血色天光。
     * <p>
     * 光带画在 z = 0 这一个平面上, 而黑之月在 z = {@link #MOON_Z}; 两个平面平行, 所以光带在屏幕上
     * 永远与黑之月"平行": 它绕过整个天球、从一侧地平线升到头顶再落到对面, 中线正好压在天空正中央。
     * 两侧靠不透明度渐变淡出, 中间最实。
     */
    @Override
    public void renderCelestialBodies(float partialTicks, WorldClient world, Minecraft mc) {
        GL11.glPushAttrib(
            GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_LIGHTING_BIT);
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        // 先清深度再关深度写入, 否则 glClear 会被深度掩码挡掉
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glDepthMask(false);
        GL11.glShadeModel(GL11.GL_SMOOTH);

        // 固定悬在固定方位, 不随昼夜转动(死掉的月亮不该再走)
        GL11.glRotatef(18.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(64.0F, 0.0F, 1.0F, 0.0F);

        drawBand(BAND_RADIUS, BAND_HALF_WIDTH, 0.82F, 0.05F, 0.03F, BAND_ALPHA);

        float radius = 78.0F;
        drawDisc(radius, 0.01F, 0.0F, 0.0F);
        drawRing(radius, radius * 1.06F, 1.0F, 0.08F, 0.05F, 0.95F, 0.80F);
        drawRing(radius * 1.06F, radius * 1.75F, 0.75F, 0.03F, 0.02F, 0.0F, 0.55F);

        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private static void drawDisc(float radius, float red, float green, float blue) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(GL11.GL_TRIANGLE_FAN);
        tessellator.setColorRGBA_F(red, green, blue, 1.0F);
        tessellator.addVertex(0.0D, 0.0D, MOON_Z);
        for (int i = 0; i <= SEGMENTS; i++) {
            double angle = Math.PI * 2.0D * i / SEGMENTS;
            tessellator.setColorRGBA_F(red, green, blue, 1.0F);
            tessellator.addVertex(Math.cos(angle) * radius, Math.sin(angle) * radius, MOON_Z);
        }
        tessellator.draw();
    }

    private static void drawRing(float inner, float outer, float red, float green, float blue, float alphaOuter,
        float alphaInner) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        for (int i = 0; i < SEGMENTS; i++) {
            double angle0 = Math.PI * 2.0D * i / SEGMENTS;
            double angle1 = Math.PI * 2.0D * (i + 1) / SEGMENTS;
            float cos0 = (float) Math.cos(angle0);
            float sin0 = (float) Math.sin(angle0);
            float cos1 = (float) Math.cos(angle1);
            float sin1 = (float) Math.sin(angle1);

            tessellator.setColorRGBA_F(red, green, blue, alphaOuter);
            tessellator.addVertex(cos0 * outer, sin0 * outer, MOON_Z);
            tessellator.addVertex(cos1 * outer, sin1 * outer, MOON_Z);
            tessellator.setColorRGBA_F(red, green, blue, alphaInner);
            tessellator.addVertex(cos1 * inner, sin1 * inner, MOON_Z);
            tessellator.addVertex(cos0 * inner, sin0 * inner, MOON_Z);
        }
        tessellator.draw();
    }

    /** 这个维度没有太阳: 天上只有银河、黑之月和那道血色天光。 */
    @Override
    public void renderSun(float partialTicks, WorldClient world, Minecraft mc) {}

    /** 绕天球一圈的红色光带: 中线最实, 往前/往后(朝向与背向黑之月的两个方向)逐渐淡到透明。 */
    private static void drawBand(float radius, float halfWidth, float red, float green, float blue, float coreAlpha) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        for (int i = 0; i < SEGMENTS; i++) {
            double angle0 = Math.PI * 2.0D * i / SEGMENTS;
            double angle1 = Math.PI * 2.0D * (i + 1) / SEGMENTS;
            float x0 = (float) Math.cos(angle0) * radius;
            float y0 = (float) Math.sin(angle0) * radius;
            float x1 = (float) Math.cos(angle1) * radius;
            float y1 = (float) Math.sin(angle1) * radius;

            tessellator.setColorRGBA_F(red, green, blue, 0.0F);
            tessellator.addVertex(x0, y0, -halfWidth);
            tessellator.addVertex(x1, y1, -halfWidth);
            tessellator.setColorRGBA_F(red, green, blue, coreAlpha);
            tessellator.addVertex(x1, y1, 0.0D);
            tessellator.addVertex(x0, y0, 0.0D);

            tessellator.setColorRGBA_F(red, green, blue, coreAlpha);
            tessellator.addVertex(x0, y0, 0.0D);
            tessellator.addVertex(x1, y1, 0.0D);
            tessellator.setColorRGBA_F(red, green, blue, 0.0F);
            tessellator.addVertex(x1, y1, halfWidth);
            tessellator.addVertex(x0, y0, halfWidth);
        }
        tessellator.draw();
    }
}
