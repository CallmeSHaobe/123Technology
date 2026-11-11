package com.newmaa.othtech.utils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

/**
 * 晚注册的 mixins。
 *
 * <p>
 * CropsNH 是普通 mod（jar 里没有 {@code TweakClass}/{@code FMLCorePlugin}），FML 要等到
 * {@code LoaderState.CONSTRUCTING} 阶段才会把它的 jar 加进 {@code LaunchClassLoader}。
 * 而清单 {@code MixinConfigs} 里的配置在启动期（tweaker 阶段）就会 prepare，此时读不到
 * {@code com.gtnewhorizon.cropsnh.loaders.CropLoader} 的字节，Mixin 会把这次失败以 null
 * 写进 {@code ClassInfo} 的 metadata cache，并丢弃该目标——{@code MixinCropLoader} 在本次
 * 启动中就永久失效了。
 *
 * <p>
 * 因此该配置不再写进 {@code MixinConfigs}，改由这里在 CONSTRUCTING（所有 mod 的 jar 已就位）
 * 之后再排队：目标类可正常解析，注入点仍落在 {@code CropLoader.postInit} 末尾，CropsNH
 * 随后生成的 NEI 假配方（种子生成器/杂交机/合成机/基因提取器）就能看到本 mod 的作物。
 */
@LateMixin
public class LateMixins implements ILateMixinLoader {

    private static final String MIXIN_CONFIG = "mixins.123technology.late.json";
    private static final String CROPSNH_MODID = "cropsnh";
    /** 相对 config 的 {@code package}（com.newmaa.othtech.mixin）的短类名。 */
    private static final String CROP_LOADER_MIXIN = "MixinCropLoader";

    @Override
    public String getMixinConfig() {
        return MIXIN_CONFIG;
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        if (!loadedMods.contains(CROPSNH_MODID)) {
            return Collections.emptyList();
        }
        return Collections.singletonList(CROP_LOADER_MIXIN);
    }
}
