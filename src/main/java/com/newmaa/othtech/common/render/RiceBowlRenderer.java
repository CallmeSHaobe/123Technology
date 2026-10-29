package com.newmaa.othtech.common.render;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 「一大碗冒尖白米饭」全息投影 —— 满 → 吃到空 → 再添满，无限循环。
 *
 * <pre>
 * ── 怎么接进你的机器 ────────────────────────────────────────────────
 * 1) 让承载渲染的 TileEntity 实现 {@link IRiceBowlHolo}（下面的 State 是现成实现）。
 * 2) 客户端注册：
 *      ClientRegistry.bindTileEntitySpecialRenderer(你的TE.class, new RiceBowlRenderer());
 * 3) 服务端在 onPostTick 里只更新「running」，毫秒插值交给客户端（GT 本体同款做法）。
 *
 * ── 尺寸 ────────────────────────────────────────────────────────────
 * 包围盒 x,z ∈ [-1.4, 1.4]，y ∈ [0, 1.9]  ⇒ 正好塞进 3×3×4 的空间。
 * 纯旋转体建模，任意水平角度看上去完全一样（不要再加任何"正面"元素）。
 *
 * ── 贴图 ────────────────────────────────────────────────────────────
 *   assets/123technology/textures/model/rice.png   256×256   米粒（世界坐标平面投影，正片叠底）
 *   assets/123technology/textures/model/bowl.png   1024×512  白瓷底 + 两只鲸鱼（柱面 UV）
 * 两张都必须是 GL_REPEAT（TextureManager 默认就是）。
 * </pre>
 */
@SideOnly(Side.CLIENT)
public class RiceBowlRenderer extends TileEntitySpecialRenderer {

    private static final ResourceLocation RICE_TEX = new ResourceLocation("123technology", "textures/model/rice.png");
    private static final ResourceLocation BOWL_TEX = new ResourceLocation("123technology", "textures/model/bowl.png");

    // ============================================================
    // 一、几何参数（想改胖瘦/高矮只动这里，全部与 ricebowl_preview.py 同源）
    // ============================================================
    /** 碗的圆周分段。必须是 4 的倍数，旋转对称才不会露方向感 */
    private static final int BOWL_SEG = 96;
    /** 米饭同心环的分段（GPU 会做顶点插值，24 足够；预览用 144 只是为了软渲染不糊） */
    private static final int RICE_SEG = 48;
    /** 米饭纵向层数 */
    private static final int RICE_LAYER = 40;

    private static final float BOWL_FOOT = 0.52F; // 圈足半径
    private static final float BOWL_TOP_R = 1.18F; // 碗口外半径
    private static final float BOWL_RIM_Y = 1.24F; // 碗口高度（宽高比约 1.9:1，像一只饭碗）
    private static final float INNER_RIM = 1.06F; // 碗口内半径
    private static final float BOWL_OUT_Y = 0.10F; // 外壁起点（圈足顶）
    private static final float BOWL_IN_Y = 0.16F; // 内底高度
    private static final float INNER_BOT = 0.46F; // 内底半径（必须比圈足小，否则会从碗底下穿出来）

    private static final float RICE_R0 = 1.04F; // 米饭在碗口处的半径（= 内壁半径，贴壁不留缝）
    private static final float FULL_TOP = 1.62F; // 满的时候米堆最高点（高出碗口 0.38，露一圈碗沿）
    private static final float EMPTY_TOP = 0.28F; // 吃空时剩的一层薄底
    /** 米饭开始鼓出碗口的高度 */
    private static final float RICE_RIM = BOWL_RIM_Y + 0.06F;
    /** 一张贴图在世界里占多少格：决定"米粒有多大"，1.10 格 ≈ 单颗米 7cm、碗口宽 32 颗 */
    private static final float TEX_BLOCK = 1.10F;
    /** 打包的满亮度 lightmap 坐标（u=240, v=240）：逐顶点写死，防止 Tessellator 复用上一批的亮度 */
    private static final int FULL_BRIGHT = 0xF000F0;

    // ============================================================
    // 二、时间轴（秒）—— 一个完整循环 14 秒
    // ============================================================
    private static final float LOOP = 14.0F;
    private static final float T_EAT_S = 3.0F; // 开始吃
    private static final float T_EAT_E = 7.6F; // 吃空
    private static final float T_REF_S = 9.0F; // 开始添
    private static final float T_REF_E = 12.4F; // 添满
    // 0–3 满着停留；12.4–14 同样满着停留，然后进入下一轮

    // ============================================================
    // 三、颜色（0..1）
    // ============================================================
    private static final float[] C_BOWL = { 0.93F, 0.94F, 0.96F }; // 白瓷
    private static final float[] C_INNER = { 0.62F, 0.66F, 0.74F }; // 碗内壁：白瓷落在阴影里
    private static final float[] C_RIM = { 0.55F, 0.78F, 1.00F }; // 碗口内缘一条细亮线
    private static final float[] C_RING = { 0.32F, 0.52F, 1.00F };
    private static final float[] C_WHITE = { 1.00F, 1.00F, 1.00F }; // 贴图原色，不额外染色

    /** 光线方向（与预览一致，归一化后） */
    private static final float LX = -0.4208F, LY = 0.7814F, LZ = 0.4608F;

    // 面的着色方式
    private static final int KIND_PLAIN = 0; // 纯色瓷面（圈足/碗沿/内底以外的实心件）
    private static final int KIND_BOWL_TEX = 1; // 外壁：贴 bowl.png
    private static final int KIND_INNER = 2; // 内壁：上亮下暗
    private static final int KIND_RICE = 3; // 米饭：贴 rice.png + 深度压暗

    // 剖面法线朝向
    private static final int MODE_OUT = 0;
    private static final int MODE_IN = 1;
    private static final int MODE_UP = 2;
    private static final int MODE_DOWN = 3;

    // ============================================================
    // 五、入口
    // ============================================================
    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        if (!(te instanceof IRiceBowlHolo)) return;
        IRiceBowlHolo holo = (IRiceBowlHolo) te;

        // ---- 1) 毫秒差插值：帧率无关，卡顿不跳帧
        long now = System.currentTimeMillis();
        long last = holo.getHoloLastMillis();
        holo.setHoloLastMillis(now);
        float dt = last == 0L ? 0F : (now - last) / 1000F;
        if (dt < 0F || dt > 0.5F) dt = 0F; // 掉帧/切窗口就丢掉这一帧

        float timer = holo.getHoloTimer();
        float fade = holo.getHoloFade();

        if (holo.isHoloRunning()) {
            timer += dt;
            // ★ 只在运行中回绕：停机时时钟冻结，消散完全交给 fade，
            // 两者共用一个变量会导致 timer%LOOP 在停机瞬间跳回开头（旧版的 bug）
            if (timer >= LOOP) timer -= LOOP;
            fade = Math.min(1F, fade + dt * 2.2F);
        } else {
            fade = Math.max(0F, fade - dt * 2.2F);
        }
        holo.setHoloTimer(timer);
        holo.setHoloFade(fade);
        if (fade <= 0.002F) return; // 停机且已消散完 → 什么都不画

        ORIGIN_Y = (float) y; // 必须在 shade 之前设置
        float fill = fillAt(timer);

        // 相机位置（世界坐标）：背面剔除必须拿"顶点 - 相机"，不能拿瓦片原点
        TileEntityRendererDispatcher d = TileEntityRendererDispatcher.instance;
        float camX = (float) d.staticPlayerX;
        float camY = (float) d.staticPlayerY;
        float camZ = (float) d.staticPlayerZ;

        // 瓦片原点（世界坐标）：x/z 各自 +0.5，模型才在方块正中
        float ox = (float) x + 0.5F;
        float oy = (float) y;
        float oz = (float) z + 0.5F;

        // ---- 2) 全息渲染状态
        GL11.glPushMatrix();
        GL11.glTranslated(ox, oy, oz);
        // 整体压低一点（-0.35 格）+ 放大 2 倍
        GL11.glTranslatef(0F, -0.35F, 0F);
        GL11.glScalef(2F, 2F, 2F);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        // ★ 全息投影自发光：TESR 默认继承世界 lightmap，夜里会被压成黑色。
        // 强制把 lightmap 纹理坐标设成 240,240（=正午满亮度），白天晚上一个样。
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        // 普通 alpha 混合（不是加法）：白瓷碗需要真的"挡住"后面的东西
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDepthMask(false); // 地面辉光不参与深度

        Tessellator tes = Tessellator.instance;

        // ---- 3) （已按用户要求删掉地面光圈/光环）

        // ★ 碗与米饭都接近不透明（α 0.92~0.97），必须写深度，
        // 否则"碗壁先画、米饭后画"时米饭会直接从碗壁上糊出来
        GL11.glDepthMask(true);

        // ---- 4) 碗（贴图外壁 → 其余纯色件）
        // 实体部件不透明：关混合，避免碗壁/米饭与背后内壁半透明混出灰雾
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        // 有些渲染器会把 TEXTURE_ENV 改成 COMBINE/别的模式，导致贴图被额外乘暗；这里强制回 MODULATE
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        bindTexture(BOWL_TEX);
        drawBowlWall(tes, timer, fade, ox, oy, oz, camX, camY, camZ);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        drawBowlBody(tes, timer, fade, ox, oy, oz, camX, camY, camZ);

        // ---- 5) 米饭（只画上表面）
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        bindTexture(RICE_TEX);
        drawRice(tes, timer, fill, fade, ox, oy, oz, camX, camY, camZ);

        // ---- 6) 还原
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glPopMatrix();
    }

    // ============================================================
    // 六、时间轴
    // ============================================================
    private static float fillAt(float time) {
        float t = time % LOOP;
        if (t < 0F) t += LOOP;
        if (t < T_EAT_S) return 1F;
        if (t < T_EAT_E) return 1F - smooth((t - T_EAT_S) / (T_EAT_E - T_EAT_S));
        if (t < T_REF_S) return 0F;
        if (t < T_REF_E) return easeOutBack((t - T_REF_S) / (T_REF_E - T_REF_S), 1.35F);
        return 1F;
    }

    // ============================================================
    // 七、碗
    // ============================================================
    /** 外壁：8 段剖面 + 柱面 UV（u 绕一圈，v=0 在碗口） */
    private void drawBowlWall(Tessellator tes, float time, float fade, float ox, float oy, float oz, float camX,
        float camY, float camZ) {
        int n = 24;
        float[] r = new float[n], yy = new float[n];
        for (int i = 0; i < n; i++) {
            float t = i / (float) (n - 1);
            yy[i] = BOWL_OUT_Y + (BOWL_RIM_Y - BOWL_OUT_Y) * t;
            r[i] = outerR(yy[i]);
        }
        lathe(
            tes,
            r,
            yy,
            BOWL_SEG,
            MODE_OUT,
            KIND_BOWL_TEX,
            C_WHITE,
            0.97F,
            true,
            time,
            fade,
            ox,
            oy,
            oz,
            camX,
            camY,
            camZ);
    }

    /** 底面 / 圈足 / 碗沿 / 内壁 / 内底：全部纯色 */
    private void drawBowlBody(Tessellator tes, float time, float fade, float ox, float oy, float oz, float camX,
        float camY, float camZ) {
        // 底面（法线朝下，从上看不到，但侧视时封住轮廓）
        lathe(
            tes,
            new float[] { 0.02F, BOWL_FOOT },
            new float[] { 0F, 0F },
            BOWL_SEG,
            MODE_DOWN,
            KIND_PLAIN,
            C_BOWL,
            0.96F,
            false,
            time,
            fade,
            ox,
            oy,
            oz,
            camX,
            camY,
            camZ);

        // 圈足
        lathe(
            tes,
            new float[] { BOWL_FOOT, BOWL_FOOT },
            new float[] { 0F, BOWL_OUT_Y },
            BOWL_SEG,
            MODE_OUT,
            KIND_PLAIN,
            C_BOWL,
            0.97F,
            false,
            time,
            fade,
            ox,
            oy,
            oz,
            camX,
            camY,
            camZ);

        // 碗沿：主体跟外壁同色，避免碗口出现一圈"奶白色围脖"
        lathe(
            tes,
            new float[] { BOWL_TOP_R, BOWL_TOP_R + 0.02F, INNER_RIM + 0.015F },
            new float[] { BOWL_RIM_Y, BOWL_RIM_Y + 0.03F, BOWL_RIM_Y + 0.04F },
            BOWL_SEG,
            MODE_UP,
            KIND_PLAIN,
            C_BOWL,
            0.97F,
            false,
            time,
            fade,
            ox,
            oy,
            oz,
            camX,
            camY,
            camZ);

        // 碗沿最内侧的一条细亮线（留一点蓝，白碗才不和米饭糊在一起）
        lathe(
            tes,
            new float[] { INNER_RIM + 0.015F, INNER_RIM },
            new float[] { BOWL_RIM_Y + 0.04F, BOWL_RIM_Y + 0.05F },
            BOWL_SEG,
            MODE_UP,
            KIND_PLAIN,
            C_RIM,
            0.85F,
            false,
            time,
            fade,
            ox,
            oy,
            oz,
            camX,
            camY,
            camZ);

        // 内壁：由底到口，法线朝轴心。接近不透明 —— 否则后面那层亮米饭
        // 会从内壁透上来，碗口下面出现一条说不清是什么的浅色带
        int n = 24;
        float[] ri = new float[n], yi = new float[n];
        for (int i = 0; i < n; i++) {
            float t = i / (float) (n - 1);
            yi[i] = BOWL_IN_Y + (BOWL_RIM_Y + 0.07F - BOWL_IN_Y) * t;
            ri[i] = innerR(yi[i]);
        }
        lathe(
            tes,
            ri,
            yi,
            BOWL_SEG,
            MODE_IN,
            KIND_INNER,
            C_INNER,
            0.92F,
            false,
            time,
            fade,
            ox,
            oy,
            oz,
            camX,
            camY,
            camZ);

        // 内底
        lathe(
            tes,
            new float[] { INNER_BOT, 0.02F },
            new float[] { BOWL_IN_Y, BOWL_IN_Y },
            BOWL_SEG,
            MODE_UP,
            KIND_INNER,
            C_INNER,
            0.92F,
            false,
            time,
            fade,
            ox,
            oy,
            oz,
            camX,
            camY,
            camZ);
    }

    // ============================================================
    // 八、米饭：只画上表面（同心环网格）
    // ============================================================
    /**
     * 米饭跟碗壁贴合的那一圈是"接触面"，正常视角根本看不见，做出来只会露出一条
     * 浅色围带（还会被半透明的内壁糊上一层）。所以这里只画顶面：
     * fill 低 → 一个水平圆盘（吃到哪儿，盘就在哪儿）
     * fill 高 → 从碗口往上鼓出的球冠
     * 两种形状都用平面投影 UV（u=px/TEX_BLOCK, v=pz/TEX_BLOCK），
     * 米粒大小恒定、不会在圆心收敛成风车或放射状条纹。
     */
    private void drawRice(Tessellator tes, float time, float fill, float fade, float ox, float oy, float oz, float camX,
        float camY, float camZ) {
        float yTop = EMPTY_TOP + (FULL_TOP - EMPTY_TOP) * Math.min(fill, 1.08F);
        if (yTop <= BOWL_IN_Y + 0.01F) return;

        // ---- 剖面：[(r, y), ...]，由外圈向中心收
        float[] pr = new float[RICE_LAYER + 3];
        float[] py = new float[RICE_LAYER + 3];
        int n = 0;
        if (yTop <= RICE_RIM) {
            // 还没鼓出碗口：一整片平盘，半径跟着饭量走
            float r0 = innerR(yTop) * 0.99F;
            pr[n] = r0;
            py[n++] = yTop;
            pr[n] = r0 * 0.35F;
            py[n++] = yTop;
            pr[n] = 0.0005F;
            py[n++] = yTop;
        } else {
            // 鼓出碗口：碗口处贴着内壁，再往上收成球冠
            for (int i = 0; i <= RICE_LAYER; i++) {
                float y = RICE_RIM + (yTop - RICE_RIM) * (i / (float) RICE_LAYER);
                pr[n] = Math.max(riceR(y, yTop), 0.0005F);
                py[n] = y;
                n++;
            }
        }

        tes.startDrawingQuads();
        tes.setBrightness(FULL_BRIGHT);
        for (int i = 0; i < n - 1; i++) {
            float r0 = pr[i], y0 = py[i];
            float r1 = pr[i + 1], y1 = py[i + 1];

            // 顶点法线：剖面切线转 90°，并保证朝上（米饭表面永远是朝上的面）
            float dr = r1 - r0, dy = y1 - y0;
            float l = (float) Math.sqrt(dr * dr + dy * dy);
            float nr = 0F, ny = 1F;
            if (l > 1e-6F) {
                nr = dy / l;
                ny = -dr / l;
                if (ny < 0F) {
                    nr = -nr;
                    ny = -ny;
                }
            }

            for (int j = 0; j < RICE_SEG; j++) {
                double a0 = 2 * Math.PI * j / RICE_SEG;
                double a1 = 2 * Math.PI * (j + 1) / RICE_SEG;
                float c0 = (float) Math.cos(a0), s0 = (float) Math.sin(a0);
                float c1 = (float) Math.cos(a1), s1 = (float) Math.sin(a1);

                // 四个角：外左 → 外右 → 内右 → 内左
                putRiceVertex(tes, r0, y0, c0, s0, nr, ny, time, fade, ox, oy, oz, camX, camY, camZ);
                putRiceVertex(tes, r0, y0, c1, s1, nr, ny, time, fade, ox, oy, oz, camX, camY, camZ);
                putRiceVertex(tes, r1, y1, c1, s1, nr, ny, time, fade, ox, oy, oz, camX, camY, camZ);
                putRiceVertex(tes, r1, y1, c0, s0, nr, ny, time, fade, ox, oy, oz, camX, camY, camZ);
            }
        }
        tes.draw();
    }

    /** 米饭顶点：逐顶点上色（切向法线做 Gouraud）+ 世界坐标平面投影 UV */
    private void putRiceVertex(Tessellator tes, float r, float y, float ca, float sa, float nr, float ny, float time,
        float fade, float ox, float oy, float oz, float camX, float camY, float camZ) {
        float lx = r * ca, lz = r * sa;
        // 3D 法线 = (nr*cos, ny, nr*sin)，已归一化（nr,ny 本身是单位向量）
        // 米饭直接给纯白顶点色：之前无论怎么调 shade 都偏灰，先排除"逐顶点乘暗"这条路径
        tes.setColorOpaque_F(1F, 1F, 1F);
        tes.addVertexWithUV(lx, y, lz, lx / TEX_BLOCK, lz / TEX_BLOCK);
    }

    // ============================================================
    // 九、圆台面片生成器
    // ============================================================
    /**
     * 把剖面 (rs[], ys[]) 绕 Y 轴旋转成一圈四边形，逐顶点上色（平滑着色）。
     *
     * @param mode   法线朝向：MODE_OUT/IN/UP/DOWN
     * @param kind   着色方式：KIND_PLAIN/BOWL_TEX/INNER
     * @param bowlUV true = 柱面贴图 UV（u 按角度、v=0 在碗口）
     */
    private void lathe(Tessellator tes, float[] rs, float[] ys, int segs, int mode, int kind, float[] col,
        float alphaBase, boolean bowlUV, float time, float fade, float ox, float oy, float oz, float camX, float camY,
        float camZ) {
        int np = rs.length;
        float[] nr = new float[np], ny = new float[np];
        profileNormals(rs, ys, mode, nr, ny);

        tes.startDrawingQuads();
        tes.setBrightness(FULL_BRIGHT);
        for (int i = 0; i < np - 1; i++) {
            float r0 = rs[i], y0 = ys[i];
            float r1 = rs[i + 1], y1 = ys[i + 1];
            if (r0 <= 1e-5F && r1 <= 1e-5F) continue;

            for (int j = 0; j < segs; j++) {
                double a0 = 2 * Math.PI * j / segs;
                double a1 = 2 * Math.PI * (j + 1) / segs;
                double am = (a0 + a1) * 0.5;
                float c0 = (float) Math.cos(a0), s0 = (float) Math.sin(a0);
                float c1 = (float) Math.cos(a1), s1 = (float) Math.sin(a1);

                if (bowlUV) {
                    // u = atan2(z,x)/2π；v = (y1 - y)/(y1 - y0) ← 贴图第 0 行在顶部，必须对应碗口
                    float u00 = ang(c0, s0), u01 = ang(c1, s1);
                    boolean seam = Math.abs(u01 - u00) > 0.5F;
                    if (seam) {
                        if (u00 < 0F) u00 += 1F;
                        if (u01 < 0F) u01 += 1F;
                    }
                    float va = (BOWL_RIM_Y - y0) / (BOWL_RIM_Y - BOWL_OUT_Y);
                    float vb = (BOWL_RIM_Y - y1) / (BOWL_RIM_Y - BOWL_OUT_Y);

                    shade(
                        kind,
                        col,
                        alphaBase,
                        fade,
                        nr[i] * c0,
                        ny[i],
                        nr[i] * s0,
                        ox + r0 * c0,
                        oy + y0,
                        oz + r0 * s0,
                        time,
                        camX,
                        camY,
                        camZ);
                    tes.setColorRGBA_F(OUT_R, OUT_G, OUT_B, OUT_A);
                    tes.addVertexWithUV(r0 * c0, y0, r0 * s0, u00, va);

                    shade(
                        kind,
                        col,
                        alphaBase,
                        fade,
                        nr[i] * c1,
                        ny[i],
                        nr[i] * s1,
                        ox + r0 * c1,
                        oy + y0,
                        oz + r0 * s1,
                        time,
                        camX,
                        camY,
                        camZ);
                    tes.setColorRGBA_F(OUT_R, OUT_G, OUT_B, OUT_A);
                    tes.addVertexWithUV(r0 * c1, y0, r0 * s1, u01, va);

                    shade(
                        kind,
                        col,
                        alphaBase,
                        fade,
                        nr[i + 1] * c1,
                        ny[i + 1],
                        nr[i + 1] * s1,
                        ox + r1 * c1,
                        oy + y1,
                        oz + r1 * s1,
                        time,
                        camX,
                        camY,
                        camZ);
                    tes.setColorRGBA_F(OUT_R, OUT_G, OUT_B, OUT_A);
                    tes.addVertexWithUV(r1 * c1, y1, r1 * s1, u01, vb);

                    shade(
                        kind,
                        col,
                        alphaBase,
                        fade,
                        nr[i + 1] * c0,
                        ny[i + 1],
                        nr[i + 1] * s0,
                        ox + r1 * c0,
                        oy + y1,
                        oz + r1 * s0,
                        time,
                        camX,
                        camY,
                        camZ);
                    tes.setColorRGBA_F(OUT_R, OUT_G, OUT_B, OUT_A);
                    tes.addVertexWithUV(r1 * c0, y1, r1 * s0, u00, vb);
                } else {
                    shade(
                        kind,
                        col,
                        alphaBase,
                        fade,
                        nr[i] * c0,
                        ny[i],
                        nr[i] * s0,
                        ox + r0 * c0,
                        oy + y0,
                        oz + r0 * s0,
                        time,
                        camX,
                        camY,
                        camZ);
                    tes.setColorRGBA_F(OUT_R, OUT_G, OUT_B, OUT_A);
                    tes.addVertex(r0 * c0, y0, r0 * s0);

                    shade(
                        kind,
                        col,
                        alphaBase,
                        fade,
                        nr[i] * c1,
                        ny[i],
                        nr[i] * s1,
                        ox + r0 * c1,
                        oy + y0,
                        oz + r0 * s1,
                        time,
                        camX,
                        camY,
                        camZ);
                    tes.setColorRGBA_F(OUT_R, OUT_G, OUT_B, OUT_A);
                    tes.addVertex(r0 * c1, y0, r0 * s1);

                    shade(
                        kind,
                        col,
                        alphaBase,
                        fade,
                        nr[i + 1] * c1,
                        ny[i + 1],
                        nr[i + 1] * s1,
                        ox + r1 * c1,
                        oy + y1,
                        oz + r1 * s1,
                        time,
                        camX,
                        camY,
                        camZ);
                    tes.setColorRGBA_F(OUT_R, OUT_G, OUT_B, OUT_A);
                    tes.addVertex(r1 * c1, y1, r1 * s1);

                    shade(
                        kind,
                        col,
                        alphaBase,
                        fade,
                        nr[i + 1] * c0,
                        ny[i + 1],
                        nr[i + 1] * s0,
                        ox + r1 * c0,
                        oy + y1,
                        oz + r1 * s0,
                        time,
                        camX,
                        camY,
                        camZ);
                    tes.setColorRGBA_F(OUT_R, OUT_G, OUT_B, OUT_A);
                    tes.addVertex(r1 * c0, y1, r1 * s0);
                }
            }
        }
        tes.draw();
    }

    /** 剖面 [(r,y)...] 的逐点 2D 单位法线，按 mode 统一朝向 */
    private static void profileNormals(float[] rs, float[] ys, int mode, float[] nr, float[] ny) {
        int np = rs.length;
        for (int i = 0; i < np; i++) {
            int a = Math.max(0, i - 1), b = Math.min(np - 1, i + 1);
            float dr = rs[b] - rs[a], dy = ys[b] - ys[a];
            float l = (float) Math.sqrt(dr * dr + dy * dy);
            float r_, y_;
            if (l < 1e-6F) {
                r_ = 0F;
                y_ = 1F;
            } else {
                r_ = dy / l;
                y_ = -dr / l;
            }
            if (mode == MODE_OUT && r_ < 0F) {
                r_ = -r_;
                y_ = -y_;
            } else if (mode == MODE_IN && r_ > 0F) {
                r_ = -r_;
                y_ = -y_;
            } else if (mode == MODE_UP && y_ < 0F) {
                r_ = -r_;
                y_ = -y_;
            } else if (mode == MODE_DOWN && y_ > 0F) {
                r_ = -r_;
                y_ = -y_;
            }
            nr[i] = r_;
            ny[i] = y_;
        }
    }

    /** 角度 → 柱面 u（-0.5..0.5） */
    private static float ang(float c, float s) {
        return (float) (Math.atan2(s, c) / (2 * Math.PI));
    }

    // ============================================================
    // 十、逐顶点着色（结果写在 OUT_R/G/B/A）
    // ============================================================
    private static float OUT_R, OUT_G, OUT_B, OUT_A;
    /** 当前模型原点（renderTileEntityAt 的 oy，相机相对坐标）：用来把 wy 还原成本地高度 */
    private static float ORIGIN_Y = 0F;

    private static void shade(int kind, float[] col, float alphaBase, float fade, float nx, float ny, float nz,
        float wx, float wy, float wz, float time, float camX, float camY, float camZ) {
        // wx/wy/wz 是"相机相对"坐标（tilePos - camera），所以相机就在原点：
        // 视线方向 = -w。之前用世界坐标 camX/camY/camZ 去减，方向是错的，
        // 视角一动 fres 就乱跳（表现为投影明暗交错）。
        float vx = -wx, vy = -wy, vz = -wz;
        float vl = (float) Math.sqrt(vx * vx + vy * vy + vz * vz);
        if (vl < 1e-6F) vl = 1e-6F;
        float ndv = Math.abs(nx * vx + ny * vy + nz * vz) / vl;
        float fres = (float) Math.pow(1F - ndv, 2.4);
        float lam = Math.max(0F, nx * LX + ny * LY + nz * LZ);
        float ly = wy - ORIGIN_Y; // 模型本地高度（0 = 碗底），不能直接用相机相对的 wy
        float scan = 0.5F + 0.5F * (float) Math.sin(ly * 7.0F - time * 2.6F);

        float r, g, b, a;
        switch (kind) {
            case KIND_RICE: {
                // 越靠碗口越亮；越在碗里越暗，但要保证"只剩碗底"时看着仍然是白米饭
                float sh = (0.98F + 0.12F * lam)
                    * clamp(0.86F + 0.14F * (ly - BOWL_RIM_Y + 0.10F) / 0.40F, 0.90F, 1.00F);
                float dk = clamp((ly - (BOWL_RIM_Y - 0.78F)) / 0.92F, 0F, 1F);
                sh *= 0.74F + 0.26F * (float) Math.pow(dk, 0.8F);
                r = sh;
                g = sh;
                b = Math.min(1F, sh * 1.06F); // 极轻的一点冷调，和纯白碗区分
                a = fade;
                break;
            }
            case KIND_BOWL_TEX: {
                // 贴图负责底色和鲸鱼，明暗/边缘光/扫描线逐顶点算（只改亮度，不改不透明度）
                float sh = clamp(0.62F + 0.40F * lam + 0.24F * fres, 0F, 1F) * (0.94F + 0.10F * scan);
                r = col[0] * sh;
                g = col[1] * sh;
                b = col[2] * sh;
                a = fade;
                break;
            }
            case KIND_INNER: {
                // 碗内壁：越靠碗口越亮（外面的光打进来），越深越暗
                float t = clamp((ly - BOWL_IN_Y) / (BOWL_RIM_Y + 0.07F - BOWL_IN_Y), 0F, 1F);
                float k = 0.62F + 0.36F * (float) Math.pow(t, 1.2F);
                r = col[0] * (k + 0.20F * fres);
                g = col[1] * (k + 0.20F * fres);
                b = col[2] * (k + 0.20F * fres);
                a = fade;
                break;
            }
            default: {
                float sh = clamp(0.62F + 0.42F * lam + 0.28F * fres, 0F, 1F);
                r = col[0] * sh * (0.86F + 0.20F * scan);
                g = col[1] * sh * (0.86F + 0.20F * scan);
                b = col[2] * sh * (0.86F + 0.20F * scan);
                a = alphaBase + 0.18F * fres;
                break;
            }
        }
        OUT_R = clamp(r, 0F, 1F);
        OUT_G = clamp(g, 0F, 1F);
        OUT_B = clamp(b, 0F, 1F);
        OUT_A = clamp(a, 0F, 1F) * fade;
    }

    // ============================================================
    // 十二、数学
    // ============================================================
    /** 外壁剖面半径：sin^0.7 —— 底部迅速外扩、上半段几乎竖直，才有"肚子" */
    private static float outerR(float y) {
        float t = clamp((y - BOWL_OUT_Y) / (BOWL_RIM_Y - BOWL_OUT_Y), 0F, 1F);
        return BOWL_FOOT + (BOWL_TOP_R - BOWL_FOOT) * (float) Math.pow(Math.sin(t * Math.PI * 0.5F), 0.7F);
    }

    private static float innerR(float y) {
        float t = clamp((y - BOWL_IN_Y) / (BOWL_RIM_Y + 0.08F - BOWL_IN_Y), 0F, 1F);
        return INNER_BOT + (INNER_RIM - INNER_BOT) * (float) Math.pow(Math.sin(t * Math.PI * 0.5F), 0.7F);
    }

    /** 米饭表面在高度 y 处的半径：碗口以下贴内壁，碗口以上走球冠 sqrt(1-u²) */
    private static float riceR(float y, float yTop) {
        if (y <= RICE_RIM) return innerR(y) * 0.985F;
        float u = clamp((y - RICE_RIM) / Math.max(1e-4F, yTop - RICE_RIM), 0F, 1F);
        return RICE_R0 * (float) Math.sqrt(Math.max(0F, 1F - u * u));
    }

    private static float clamp(float v, float a, float b) {
        return v < a ? a : (v > b ? b : v);
    }

    private static float smooth(float t) {
        t = clamp(t, 0F, 1F);
        return t * t * (3F - 2F * t);
    }

    /** 添饭时略微冲过头再回落，有点「堆上去」的手感 */
    private static float easeOutBack(float t, float s) {
        t = clamp(t, 0F, 1F) - 1F;
        return 1F + t * t * ((s + 1F) * t + s);
    }
}
