package com.newmaa.othtech.common.dimensions.biome;

import net.minecraft.world.biome.BiomeGenBase;

import galaxyspace.core.world.GSBiomeGenBase;

public class BiomeGenBaseRoss123b extends GSBiomeGenBase {

    public static BiomeGenBase ross123b;

    static {
        ross123b = (new BiomeGenAntimonia(124)).setBiomeName("Ross123b Ocean")
            .setHeight(new BiomeGenBase.Height(0.1F, 0.2F));
    }

    public BiomeGenBaseRoss123b(int id) {
        super(id);
        this.setBiomeName("Ross123b Ocean");
        this.enableRain = true;
        this.enableSnow = true;
        this.temperature = -10f;
        // 可以设置颜色、温度之类
    }
}
