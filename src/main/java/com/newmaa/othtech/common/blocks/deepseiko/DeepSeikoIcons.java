package com.newmaa.othtech.common.blocks.deepseiko;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import gregtech.api.GregTechAPI;
import gregtech.api.interfaces.IIconContainer;

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
