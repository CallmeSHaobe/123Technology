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
 * 文件位于 {@code assets/123technology/textures/blocks/deepseiko/}，
 * 由 {@code E:\NHMOD\tools\gen_ds_controller_textures.py} 生成，勿手改产物。
 */
public enum DeepSeikoIcons implements IIconContainer, Runnable {

    /** 正面：鲸帽少女吉祥物（seiko.png 裁边补方缩到 64x64） */
    CONTROLLER_FRONT("deepseiko/controller_front"),
    /** 其余五面：淡蓝纯色 */
    CONTROLLER_SIDE("deepseiko/controller_side");

    public static final String RES_PATH = "123technology:";
    private final String path;
    private IIcon mIcon;

    DeepSeikoIcons(String path) {
        // 路径必须显式小写：文件名是小写，不能拼枚举名（大写会注册到不存在的图标 → 紫黑方块）
        this.path = path;
        GregTechAPI.sGTBlockIconload.add(this);
    }

    @Override
    public IIcon getIcon() {
        return mIcon;
    }

    @Override
    public IIcon getOverlayIcon() {
        return null;
    }

    @Override
    public ResourceLocation getTextureFile() {
        return TextureMap.locationBlocksTexture;
    }

    @Override
    public void run() {
        mIcon = GregTechAPI.sBlockIcons.registerIcon(RES_PATH + path);
    }
}
