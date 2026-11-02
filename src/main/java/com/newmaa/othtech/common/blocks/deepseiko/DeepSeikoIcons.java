package com.newmaa.othtech.common.blocks.deepseiko;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import gregtech.api.GregTechAPI;
import gregtech.api.interfaces.IIconContainer;

/**
 * 迪普西科控制器的专属贴图（写法照抄 {@code ggfab.BlockIcons}：构造时挂进 GT 的
 * sGTBlockIconload，客户端贴图拼接阶段统一 registerIcon）。
 * <p>
 * 正面走 GT 标准的<b>覆盖图层</b>：{@link #getIcon()} 是 16x16 机壳基底，
 * {@link #getOverlayIcon()} 是带透明通道的立绘覆盖层。{@code GTRenderedTexture}
 * 渲染同一个面时会先画基底、再把覆盖层叠上去，所以不需要第二张 ITexture，
 * 也不用往 {@code TextureFactory.builder()} 里 addIcon 两次（那个 builder 只收一个 icon，会抛异常）。
 * <p>
 * 文件位于 {@code assets/123technology/textures/blocks/deepseiko/}。
 * {@code controller_side.png} 仍由 {@code E:\NHMOD\tools\gen_ds_controller_textures.py} 生成；
 * 正面基底（16x16，手绘像素面板）与覆盖层（128x128，由立绘裁边补方 + 12% 留白后二值化 alpha 得到）是后处理的，
 * 重新生成贴图时别覆盖这两张。
 */
public enum DeepSeikoIcons implements IIconContainer, Runnable {

    /** 正面基底：16x16 蓝色机壳面板（金色铆钉 + 内嵌面板线），覆盖层从它上面透出来 */
    CONTROLLER_FRONT_BASE("deepseiko/controller_front_base", "deepseiko/controller_front_overlay"),
    /** 其余五面：蓝底 + 金色 T 字机壳 */
    CONTROLLER_SIDE("deepseiko/controller_side", null);

    public static final String RES_PATH = "123technology:";
    private final String path;
    /** 覆盖层贴图路径；null = 该面没有覆盖层 */
    private final String overlayPath;
    private IIcon mIcon;
    private IIcon mOverlayIcon;

    DeepSeikoIcons(String path, String overlayPath) {
        // 路径必须显式小写：文件名是小写，不能拼枚举名（大写会注册到不存在的图标 → 紫黑方块）
        this.path = path;
        this.overlayPath = overlayPath;
        GregTechAPI.sGTBlockIconload.add(this);
    }

    @Override
    public IIcon getIcon() {
        return mIcon;
    }

    @Override
    public IIcon getOverlayIcon() {
        return mOverlayIcon;
    }

    @Override
    public ResourceLocation getTextureFile() {
        return TextureMap.locationBlocksTexture;
    }

    @Override
    public void run() {
        mIcon = GregTechAPI.sBlockIcons.registerIcon(RES_PATH + path);
        mOverlayIcon = overlayPath == null ? null : GregTechAPI.sBlockIcons.registerIcon(RES_PATH + overlayPath);
    }
}
