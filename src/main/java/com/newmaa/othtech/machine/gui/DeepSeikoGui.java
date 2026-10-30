package com.newmaa.othtech.machine.gui;

import static net.minecraft.util.StatCollector.translateToLocal;

import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.drawable.Rectangle;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.viewport.GuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.LongSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.newmaa.othtech.machine.OTEDeepSeiko;

import gregtech.api.util.GTUtility;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gregtech.common.modularui2.widget.builder.ItemSlotGridBuilder;

/**
 * 迪普西科（DeepSeiko）的 MUI2 界面。
 *
 * <p>
 * 主 GUI 沿用 GT5U 的多方块框架（终端 / 背包 / 模式按钮 / 消音）。
 * 我们的两套自绘面板都**挂在主面板右侧**（{@code relative(parent).leftRel(1)}），
 * 不再像以前那样居中压在背包上：
 * </p>
 * <ul>
 * <li>控制台（340×250）：进度环 / 三条数据条 / 三环等级与加速倍率；</li>
 * <li>机体升级（190×182）：3×3 暂存格 + 三个升级按钮 + 进度文本，从控制台右上角的 ↑ 打开。</li>
 * </ul>
 * <p>
 * 模式切换按钮不在这里画 —— GT5U 的 {@code createLeftPanelGapRow} 会自己加（终端左下那排、配方锁定右边），
 * 前提是机器覆写 {@code setMachineModeIcons()}。
 * </p>
 */
public class DeepSeikoGui extends MTEMultiBlockBaseGui<OTEDeepSeiko> {

    private static final int C_LINE = 0xFF1E3A66;
    private static final int C_LINE_HI = 0xFF3C6FCC;
    private static final int C_ACCENT = 0xFF4D6BFE;

    private static final int SW = 85;
    private static final int SH = 203;
    private static final int W = 340;
    private static final int H = 192;

    /** 升级面板按钮动作的 synced action id */
    private static final String UPGRADE_ACTION = "ds_upgrade_action";

    /** 右下角头像：assets/123technology/textures/gui/deepseiko.png */
    private static final UITexture AVATAR = UITexture.fullImage("123technology", "gui/deepseiko");

    /** 两块细面板的句柄 + 自动打开标记（用户要求：打开一级界面时自动弹出） */
    private IPanelHandler speedHandler;
    private IPanelHandler upgradeHandler;
    private boolean autoOpened = false;
    private int autoOpenTries = 0;

    public DeepSeikoGui(OTEDeepSeiko multiblock) {
        super(multiblock);
    }

    /**
     * 机器模式切换按钮的图标。
     * <p>
     * ⚠ MUI2 用的是 <b>GUI 侧</b>的 {@code MTEMultiBlockBaseGui.machineModeIcons}（MUI2 的 UITexture），
     * 只有在 GUI 里重写这个方法，{@code createLeftPanelGapRow} 才会把按钮画出来（位置正好在
     * 「配方锁定」右边）。在机器类里重写是 MUI1 的老路子，对 MUI2 界面无效。
     * </p>
     */
    @Override
    protected void setMachineModeIcons() {
        machineModeIcons.clear();
        // 用户要求：两个模式图标换成「银币 / 发光金币」，跟 NEI 页签的银币金风格一致
        machineModeIcons.add(UITexture.fullImage("123technology", "gui/ds_mode_line")); // 装配线：银币
        machineModeIcons.add(UITexture.fullImage("123technology", "gui/ds_mode_bec")); // 凝聚态：发光金币
    }

    @Override
    public ModularPanel build(com.cleanroommc.modularui.factory.PosGuiData guiData, PanelSyncManager syncManager,
        com.cleanroommc.modularui.screen.UISettings uiSettings) {
        ModularPanel panel = super.build(guiData, syncManager, uiSettings);
        // 主面板底色：淡蓝 + 水波纹（自绘 256x256，拉伸铺满 GT 面板；文字区仍是深色，保证可读性）
        panel.background(UITexture.fullImage("123technology", "gui/ds_panel_blue"));
        // 头像气泡（点右下角头像时出现，3 秒后自动消失）
        panel.child(createAvatarText());
        // 打开一级界面时自动把两侧面板拉起来。
        // 注意：必须在「屏幕已经挂上」之后再 openPanel，否则第一次 tick 会静默失败，
        // 所以这里不是只试一次，而是最多重试 40 tick（成功即停）。
        panel.onUpdateListener(w -> {
            if (autoOpened) return;
            try {
                if (multiblock.getBaseMetaTileEntity() == null || !multiblock.getBaseMetaTileEntity()
                    .isClientSide()) {
                    return;
                }
                if (++autoOpenTries > 40) {
                    autoOpened = true;
                    return;
                }
                boolean speedOk = speedHandler == null || speedHandler.isPanelOpen();
                boolean upgOk = upgradeHandler == null || upgradeHandler.isPanelOpen();
                if (!speedOk && speedHandler != null) speedHandler.openPanel();
                if (!upgOk && upgradeHandler != null) upgradeHandler.openPanel();
                autoOpened = speedOk && upgOk;
            } catch (Throwable ignored) {
                // 自动打开只是省一次点击，失败就保持关闭，玩家点齿轮也能开
            }
        });
        return panel;
    }

    /** 控制台入口放在「右侧按钮行」（电源面板按钮旁边），不再挤占右边那列按钮，避免和电源键叠在一起 */
    @Override
    protected Flow createRightPanelGapRow(ModularPanel parent, PanelSyncManager syncManager) {
        return super.createRightPanelGapRow(parent, syncManager).child(createConsoleButton(parent, syncManager));
    }

    // ============================================================
    // 一级界面美化：换掉 GT 的大 G logo + 终端黑底换深蓝
    // ============================================================

    /** 头像点击状态：气泡 3 秒消失；连点 3 下换台词 */
    private long bubbleUntil = 0L;
    private String bubbleText = "";
    private int clickStreak = 0;
    private long lastClickAt = 0L;

    private void onAvatarClick() {
        long now = System.currentTimeMillis();
        if (now - lastClickAt > 900L) clickStreak = 0;
        lastClickAt = now;
        ++clickStreak;
        if (clickStreak >= 3) {
            bubbleText = translateToLocal("ote.ds.gui.avatar2");
            clickStreak = 0;
        } else {
            bubbleText = translateToLocal("ote.ds.gui.avatar1");
        }
        bubbleUntil = now + 3000L;
    }

    /** 右下角那个大 G 就是 PICTURE_LOGO 主题，覆写这里换成迪普西科头像（可点击，弹气泡） */
    @Override
    protected Widget<? extends Widget<?>> makeLogoWidget(PanelSyncManager syncManager, ModularPanel parent) {
        return new ButtonWidget<>().size(26)
            .marginTop(4)
            .background(new Rectangle().color(0x00000000))
            .overlay(AVATAR)
            .onMousePressed(d -> {
                onAvatarClick();
                return true;
            });
    }

    /** 点头像后显示的文字（3 秒后消失；连点 3 下换台词） */
    private IWidget createAvatarText() {
        final java.util.function.BooleanSupplier on = () -> System.currentTimeMillis() < bubbleUntil;
        ParentWidget<?> box = new ParentWidget<>().size(132, 26)
            .pos(14, 52);
        box.child(
            IKey.dynamic(() -> on.getAsBoolean() ? bubbleText : "")
                .asWidget()
                .left(0)
                .top(0)
                .size(132, 12));
        return box;
    }

    /** 中间那块黑色终端区：换成深蓝渐变，跟控制台配色统一 */
    @Override
    protected ParentWidget<?> createTerminalParentWidget(ModularPanel panel, PanelSyncManager syncManager) {
        return super.createTerminalParentWidget(panel, syncManager)
            .background(new Rectangle().verticalGradient(0xFF0A1424, 0xFF060B14));
    }

    /**
     * 一级界面（主面板）也要看到冷却/加速状态 —— 用户反馈「没冷却仓只有二级界面才提示」。
     * 在 GT 自带的终端文本列表下面追加一行。
     */
    @Override
    protected ListWidget<IWidget, ?> createTerminalTextWidget(PanelSyncManager syncManager, ModularPanel parent) {
        IntSyncValue mode = new IntSyncValue(multiblock::getModeId);
        LongSyncValue progPts = new LongSyncValue(multiblock::getProgressPointScaled);
        LongSyncValue totalPts = new LongSyncValue(multiblock::getProgressPointTotal);
        StringSyncValue rate = new StringSyncValue(
            () -> GTUtility.formatShortenedLong((long) Math.max(0F, multiblock.getBecCurrentRate())));
        syncManager.syncValue("ds_main_mode", mode);
        syncManager.syncValue("ds_main_pp", progPts);
        syncManager.syncValue("ds_main_pt", totalPts);
        syncManager.syncValue("ds_main_rate", rate);
        return super.createTerminalTextWidget(syncManager, parent)
            // 点数进度（仅凝聚态）
            .child(
                IKey.dynamic(
                    () -> translateToLocal("ote.ds.info.points") + ": "
                        + String.format("%,d", progPts.getLongValue())
                        + " / "
                        + String.format("%,d", totalPts.getLongValue()))
                    .asWidget()
                    .marginBottom(1)
                    .fullWidth()
                    .setEnabledIf(w -> mode.getIntValue() == 1))
            // 处理速度（仅凝聚态）
            .child(
                IKey.dynamic(() -> String.format(translateToLocal("ote.ds.gui.throughput"), rate.getStringValue()))
                    .asWidget()
                    .marginBottom(1)
                    .fullWidth()
                    .setEnabledIf(w -> mode.getIntValue() == 1));
    }

    /**
     * 把面板放到主 GUI 右侧；**屏幕（GUI 缩放后）放不下就保持默认居中**。
     * <p>
     * 用户反馈：界面尺寸调大到 5、或分辨率小的时候，340 宽的控制台右侧会被裁掉。
     * 这里按 ScaledResolution 判断「主面板 + 控制台 + 边距」是否放得下，放不下就退回居中（至少完整可见）。
     * </p>
     */
    private void placeBeside(ModularPanel p, ModularPanel parent) {
        if (!fitsBesideGui()) return; // 不设置 relative ⇒ ModularPanel 默认屏幕居中
        p.relative(parent)
            .leftRel(1)
            .topRel(0);
    }

    private boolean fitsBesideGui() {
        try {
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
            net.minecraft.client.gui.ScaledResolution sr = new net.minecraft.client.gui.ScaledResolution(
                mc,
                mc.displayWidth,
                mc.displayHeight);
            int needW = getBasePanelWidth() + W + 16;
            return sr.getScaledWidth() >= needW && sr.getScaledHeight() >= H + 16;
        } catch (Throwable t) {
            return false;
        }
    }

    // ============================================================
    // 两侧细面板 75×120 + 升级覆盖层
    // ============================================================

    private IWidget createConsoleButton(ModularPanel parent, PanelSyncManager sm) {
        final IPanelHandler[] speedRef = new IPanelHandler[1];
        final IPanelHandler[] upgRef = new IPanelHandler[1];
        speedRef[0] = sm.syncedPanel("ds_speed", true, (m, sh) -> buildSpeedPanel(parent, m));
        upgRef[0] = sm.syncedPanel("ds_upgrade_slim", true, (m, sh) -> buildUpgradePanel(parent, m));
        this.speedHandler = speedRef[0];
        this.upgradeHandler = upgRef[0];
        return new ButtonWidget<>().size(18)
            .overlay(GuiTextures.GEAR)
            .tooltip(t -> t.addLine(EnumChatFormatting.AQUA + translateToLocal("ote.ds.gui.btn.console")))
            .tooltipShowUpTimer(0)
            .onMousePressed(d -> {
                if (speedRef[0].isPanelOpen()) {
                    speedRef[0].closePanel();
                    if (upgRef[0].isPanelOpen()) upgRef[0].closePanel();
                } else {
                    speedRef[0].openPanel();
                    upgRef[0].openPanel();
                }
                return true;
            });
    }

    /** 左：加速（85×203，与主界面同高；总算速 + 6 个乘区 + 三环 + 冷却） */
    private ModularPanel buildSpeedPanel(ModularPanel parent, PanelSyncManager sm) {
        StringSyncValue vTotal = new StringSyncValue(() -> fmtNum(multiblock.getSpeedMultiplier()));
        StringSyncValue vCompute = new StringSyncValue(() -> fmtNum(multiblock.getComputeFactor()));
        StringSyncValue vRing = new StringSyncValue(() -> fmt2(multiblock.getRingSpeedFactor()));
        StringSyncValue vEu = new StringSyncValue(() -> fmt2(multiblock.getEuFactor()));
        StringSyncValue vUlt = new StringSyncValue(() -> fmt2(multiblock.isSpeedUnlocked() ? 2.0F : 1.0F));
        StringSyncValue vT = new StringSyncValue(() -> "T" + multiblock.getTimeLevel());
        StringSyncValue vS = new StringSyncValue(() -> "T" + multiblock.getSpaceLevel());
        StringSyncValue vB = new StringSyncValue(() -> "T" + multiblock.getStabLevel());
        // ⚠ 推进必须走同步值：以前这里是客户端现算的 lambda，客户端读不到三环/算力卡，
        // 显示成「基础 300,000」而终端显示「实际 540,000」，两边对不上。
        // 用和终端同一个压缩格式（k/M/G），否则调试币的 5,400,000,000 会把 85 宽的面板撑成两三行。
        // 只同步数字，单位在客户端 IKey.dynamic 里翻译（服务端没有语言文件，translateToLocal 会返回键名）
        StringSyncValue vRate = new StringSyncValue(
            () -> GTUtility.formatShortenedLong((long) Math.max(0F, multiblock.getBecCurrentRate())));
        sm.syncValue("ds_sp_total", vTotal);
        sm.syncValue("ds_sp_compute", vCompute);
        sm.syncValue("ds_sp_ring", vRing);
        sm.syncValue("ds_sp_eu", vEu);
        sm.syncValue("ds_sp_ult", vUlt);
        sm.syncValue("ds_sp_t", vT);
        sm.syncValue("ds_sp_s", vS);
        sm.syncValue("ds_sp_b", vB);
        sm.syncValue("ds_sp_rate", vRate);

        IntSyncValue vTokLeft = new IntSyncValue(() -> multiblock.isTokenInserted() ? 1 : 0);
        sm.syncValue("ds_sp_tok", vTokLeft);

        ModularPanel p = slimPanel("ds_speed_panel", parent, SW, SH, -1, "ote.ds.gui.speed");
        p.child(
            IKey.dynamic(vTotal::getStringValue)
                .style(EnumChatFormatting.DARK_BLUE)
                .asWidget()
                .left(5)
                .top(16)
                .size(SW - 10, 16));

        IntSyncValue vMode = new IntSyncValue(multiblock::getModeId);
        sm.syncValue("ds_sp_mode", vMode);
        // 装配线模式：没有算力，只有三环（「终极」行始终 ×1.00，已按用户要求删掉）
        slimRow(p, 36, translateToLocal("ote.ds.gui.compute"), vCompute);
        slimRow(p, 58, translateToLocal("ote.ds.gui.boost"), vRing);
        slimRow(p, 80, translateToLocal("ote.ds.gui.power"), vEu);
        slimRowText(
            p,
            102,
            translateToLocal("ote.ds.gui.rate"),
            () -> vMode.getIntValue() == 0 ? "—"
                : vRate.getStringValue() + " " + translateToLocal("ote.ds.info.perTick"));

        p.child(box(5, 116, SW - 10, 1, solid(C_LINE)));

        slimRow(p, 124, translateToLocal("ote.ds.gui.ring.time"), vT);
        slimRow(p, 138, translateToLocal("ote.ds.gui.ring.space"), vS);
        slimRow(p, 152, translateToLocal("ote.ds.gui.ring.stab"), vB);

        // 底部居中：调试币动画（未插卡时压暗，插卡后点亮）
        p.child(
            new Widget<>().size(COIN_SIZE)
                .pos((SW - COIN_SIZE) / 2, COIN_Y)
                .background(new CoinSpin(() -> vTokLeft.getIntValue() == 1)));

        return p;
    }

    /** 右：升级（85×203）：3 行状态 + 打开升级按钮 + 旋转金币 */
    private ModularPanel buildUpgradePanel(ModularPanel parent, PanelSyncManager sm) {
        final IPanelHandler[] overlayRef = new IPanelHandler[1];
        overlayRef[0] = sm.syncedPanel("ds_overlay", true, (m, sh) -> buildOverlay(parent, m));

        IntSyncValue vBec = new IntSyncValue(() -> multiblock.isBecUnlocked() ? 1 : 0);
        IntSyncValue vNan = new IntSyncValue(multiblock::getNaniteTier);
        IntSyncValue vUlt = new IntSyncValue(() -> multiblock.isSpeedUnlocked() ? 1 : 0);
        IntSyncValue vTok = new IntSyncValue(() -> multiblock.isTokenInserted() ? 1 : 0);
        sm.syncValue("ds_up_bec", vBec);
        sm.syncValue("ds_up_nan", vNan);
        sm.syncValue("ds_up_ult", vUlt);
        sm.syncValue("ds_up_tok", vTok);

        ModularPanel p = slimPanel("ds_upgrade_panel", parent, SW, SH, 1, "ote.ds.gui.upgrade");

        slimRowText(
            p,
            36,
            translateToLocal("ote.ds.gui.mode.bec"),
            () -> vBec.getIntValue() == 1 ? translateToLocal("ote.ds.gui.state.unlocked")
                : translateToLocal("ote.ds.gui.state.locked"));
        slimRowText(p, 58, translateToLocal("ote.ds.gui.nanite"), () -> "T" + vNan.getIntValue() + "/10");
        slimRowText(
            p,
            80,
            translateToLocal("ote.ds.gui.ultimate"),
            () -> vUlt.getIntValue() == 1 ? translateToLocal("ote.ds.gui.state.done")
                : translateToLocal("ote.ds.gui.state.todo"));
        IntSyncValue vMode = new IntSyncValue(multiblock::getModeId);
        sm.syncValue("ds_up_mode", vMode);
        p.child(
            new ButtonWidget<>().size(SW - 10, 26)
                .pos(5, 126)
                .overlay(
                    IKey.dynamic(
                        () -> vMode.getIntValue() == 0 ? translateToLocal("ote.ds.gui.alNoUpgrade")
                            : translateToLocal("ote.ds.gui.btn.openUpgrade")))
                .onMousePressed(d -> {
                    if (vMode.getIntValue() == 0) return true; // 装配线模式没有升级
                    if (!overlayRef[0].isPanelOpen()) overlayRef[0].openPanel();
                    return true;
                }));

        // 底部居中：调试币动画（和左面板完全对称：同尺寸、同高度；未插卡时压暗）
        p.child(
            new Widget<>().size(COIN_SIZE)
                .pos((SW - COIN_SIZE) / 2, COIN_Y)
                .background(new CoinSpin(() -> vTok.getIntValue() == 1)));
        return p;
    }

    /** 升级覆盖层：盖住主界面中间，6×4 暂存格 + 3 个动作 + 补料 + ✕ */
    private ModularPanel buildOverlay(ModularPanel parent, PanelSyncManager sm) {
        // 尺寸对齐主面板：宽 198，高 = 203 - 76（背包行），刚好盖住「终端 + 物品栏」这块机器区，
        // 下面的玩家背包行（76px）露在外面照常点。
        final int ow = getBasePanelWidth();
        final int oh = getBasePanelHeight() - 76 - 8;
        ModularPanel p = new FixedPanel("ds_overlay_panel").size(ow, oh)
            .background(new PanelWaterDark())
            .disableHoverBackground();
        p.relative(parent)
            .leftRelOffset(0f, 0)
            .topRelOffset(0f, 0);
        p.child(box(0, 0, ow, oh, hollow(C_LINE_HI, 1)));

        p.child(box(1, 1, ow - 2, 11, vert(0xFF193460, 0xFF0C1A30)));
        p.child(box(1, 12, ow - 2, 1, solid(C_LINE)));
        p.child(box(4, 3, 2, 7, solid(0xFFC9A227)));
        p.child(
            IKey.lang("ote.ds.upg.title")
                .asWidget()
                .left(8)
                .top(2));

        p.child(
            new ButtonWidget<>().size(12)
                .pos(ow - 14, 1)
                .overlay(IKey.str("X"))
                .onMousePressed(d -> {
                    p.closeIfOpen();
                    return true;
                }));

        sm.registerServerSyncedAction(UPGRADE_ACTION, buf -> {
            int code = buf.readByte();
            if (code == 1) multiblock.tryUpgradeBec();
            else if (code == 2) multiblock.tryUpgradeNanite();
            else if (code == 3) multiblock.tryUpgradeUltimate();
            else if (code == 4) multiblock.fillUpgradeStage(sm.getPlayer());
        });

        p.child(
            new ItemSlotGridBuilder(multiblock.getUpgradeStage(), sm).size(6, 4)
                .slotGroupKey("ds_upgrade_stage")
                .build()
                .pos(5, 15));
        IntSyncValue vBec = new IntSyncValue(() -> multiblock.isBecUnlocked() ? 1 : 0);
        IntSyncValue vNan = new IntSyncValue(multiblock::getNaniteTier);
        IntSyncValue vUlt = new IntSyncValue(() -> multiblock.isSpeedUnlocked() ? 1 : 0);
        sm.syncValue("ds_ov_bec", vBec);
        sm.syncValue("ds_ov_nan", vNan);
        sm.syncValue("ds_ov_ult", vUlt);
        final int bx = ow - 78;
        p.child(
            overlayButton(
                () -> vBec.getIntValue() == 1 ? translateToLocal("ote.ds.upg.done.bec")
                    : translateToLocal("ote.ds.upg.bec"),
                1,
                sm,
                bx,
                13));
        p.child(
            overlayButton(
                () -> vNan.getIntValue() >= 10 ? translateToLocal("ote.ds.upg.done.nanite")
                    : translateToLocal("ote.ds.upg.nanite"),
                2,
                sm,
                bx,
                33));
        p.child(
            overlayButton(
                () -> vUlt.getIntValue() == 1 ? translateToLocal("ote.ds.upg.done.ultimate")
                    : translateToLocal("ote.ds.upg.ultimate"),
                3,
                sm,
                bx,
                53));
        p.child(
            overlayButton(
                () -> vBec.getIntValue() == 1 && vNan.getIntValue() >= 10 && vUlt.getIntValue() == 1
                    ? translateToLocal("ote.ds.upg.allDone")
                    : translateToLocal("ote.ds.upg.fill"),
                4,
                sm,
                bx,
                73));
        // 完成的按钮加一圈蓝边（画在按钮之上，只描边不挡字）
        p.child(
            box(
                bx,
                13,
                72,
                18,
                new DynamicDrawable(() -> vBec.getIntValue() == 1 ? hollow(0xFF4D6BFE, 1) : hollow(0x00000000, 1))));
        p.child(
            box(
                bx,
                33,
                72,
                18,
                new DynamicDrawable(() -> vNan.getIntValue() >= 10 ? hollow(0xFF4D6BFE, 1) : hollow(0x00000000, 1))));
        p.child(
            box(
                bx,
                53,
                72,
                18,
                new DynamicDrawable(() -> vUlt.getIntValue() == 1 ? hollow(0xFF4D6BFE, 1) : hollow(0x00000000, 1))));
        p.child(
            box(
                bx,
                73,
                72,
                18,
                new DynamicDrawable(
                    () -> vBec.getIntValue() == 1 && vNan.getIntValue() >= 10 && vUlt.getIntValue() == 1
                        ? hollow(0xFF4D6BFE, 1)
                        : hollow(0x00000000, 1))));

        return p;
    }

    private IWidget overlayButton(java.util.function.Supplier<String> label, int code, PanelSyncManager sm, int x,
        int y) {
        return new ButtonWidget<>().size(72, 18)
            .pos(x, y)
            .overlay(IKey.dynamic(label))
            .onMousePressed(d -> {
                sm.callSyncedAction(UPGRADE_ACTION, buf -> buf.writeByte(code));
                return true;
            });
    }

    /** 两侧细面板 / 升级覆盖层的底：和主面板同款水波纹，但压暗保证白字可读 */
    private static final class PanelWaterDark implements IDrawable {

        private static final UITexture TEX = UITexture.fullImage("123technology", "gui/ds_panel_blue");

        @Override
        public void draw(GuiContext ctx, int x, int y, int w, int h, WidgetTheme theme) {
            TEX.draw(ctx, x, y, w, h, theme);
        }
    }

    /** 两侧面板底部金币的统一尺寸与 y（保证左右对称） */
    private static final int COIN_SIZE = 34;
    private static final int COIN_Y = 164;

    /** 旋转的金色调试硬币动图：8 帧循环，按系统时间切帧；未插卡时压暗 */
    private static final UITexture[] COIN_FRAMES = new UITexture[8];

    static {
        for (int i = 0; i < COIN_FRAMES.length; i++) {
            COIN_FRAMES[i] = UITexture.fullImage("123technology", "gui/ds_coin_spin_" + i);
        }
    }

    private static final class CoinSpin implements IDrawable {

        private final java.util.function.BooleanSupplier inserted;

        CoinSpin(java.util.function.BooleanSupplier inserted) {
            this.inserted = inserted;
        }

        @Override
        public void draw(GuiContext ctx, int x, int y, int w, int h, WidgetTheme theme) {
            int idx = (int) ((System.currentTimeMillis() / 145L) % COIN_FRAMES.length);
            // 没插卡：暗一点（提示"空槽"）；插卡：全亮
            float a = inserted.getAsBoolean() ? 1.0F : 0.35F;
            GL11.glColor4f(1F, 1F, 1F, a);
            COIN_FRAMES[idx].draw(ctx, x, y, w, h, theme);
            GL11.glColor4f(1F, 1F, 1F, 1F);
        }
    }

    /** 不可拖动的面板（用户要求自绘的侧栏/升级层固定住） */
    private static final class FixedPanel extends ModularPanel {

        FixedPanel(String name) {
            super(name);
        }

        @Override
        public boolean isDraggable() {
            return false;
        }
    }

    /** 细长面板外壳：side = -1 贴主界面左侧，+1 贴右侧 */
    private ModularPanel slimPanel(String name, ModularPanel parent, int w, int h, int side, String titleKey) {
        ModularPanel p = new FixedPanel(name).size(w, h)
            .background(new PanelWaterDark())
            .disableHoverBackground();
        p.relative(parent)
            .leftRelOffset(side < 0 ? 0f : 1f, side < 0 ? -(w + 4) : 4)
            .topRelOffset(0f, 0);
        p.child(box(0, 0, w, h, hollow(C_LINE_HI, 1)));
        p.child(box(1, 1, w - 2, 10, vert(0xFF193460, 0xFF0C1A30)));
        p.child(box(1, 11, w - 2, 1, solid(C_LINE)));
        p.child(box(4, 3, 2, 6, solid(C_ACCENT)));
        p.child(
            IKey.str(translateToLocal(side < 0 ? "ote.ds.gui.speed" : "ote.ds.gui.upgrade"))
                .asWidget()
                .left(8)
                .top(1));
        return p;
    }

    private void slimRow(ModularPanel p, int y, String label, StringSyncValue value) {
        p.child(
            IKey.str(label)
                .style(EnumChatFormatting.DARK_GRAY)
                .asWidget()
                .left(4)
                .top(y));
        p.child(
            IKey.dynamic(value::getStringValue)
                .style(EnumChatFormatting.DARK_BLUE)
                .asWidget()
                .alignment(Alignment.CenterRight)
                .left(SW - 48)
                .top(y)
                .size(44, 9));
    }

    private void slimRowText(ModularPanel p, int y, String label, java.util.function.Supplier<String> value) {
        p.child(
            IKey.str(label)
                .style(EnumChatFormatting.DARK_GRAY)
                .asWidget()
                .left(4)
                .top(y));
        p.child(
            IKey.dynamic(value)
                .style(EnumChatFormatting.DARK_BLUE)
                .asWidget()
                .alignment(Alignment.CenterRight)
                .left(SW - 48)
                .top(y)
                .size(44, 9));
    }

    private static String fmtNum(float v) {
        if (v >= 1_000_000_000F) return String.format("×%.1fG", v / 1_000_000_000F);
        if (v >= 1_000_000F) return String.format("×%.1fM", v / 1_000_000F);
        if (v >= 1_000F) return String.format("×%.1fk", v / 1_000F);
        if (v >= 100F) return String.format("×%.0f", v);
        return String.format("×%.2f", v);
    }

    private static String fmt2(float v) {
        return String.format("×%.2f", v);
    }

    /** 一个绝对定位的色块：直接把 IDrawable 包成 widget */
    private static Widget<?> box(int x, int y, int w, int h, IDrawable bg) {
        return new ButtonWidget<>().left(x)
            .top(y)
            .size(w, h)
            .background(bg)
            .disableHoverBackground();
    }

    private static Rectangle solid(int argb) {
        return new Rectangle().color(argb);
    }

    private static Rectangle vert(int top, int bottom) {
        return new Rectangle().verticalGradient(top, bottom);
    }

    private static Rectangle hollow(int argb, int thickness) {
        return new Rectangle().hollow(thickness)
            .color(argb);
    }

}
