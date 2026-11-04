package com.newmaa.othtech.common.dimensions.biome;

import net.minecraft.world.biome.BiomeGenBase;

/**
 * 锑罗斯的群系(红海海面)。
 * <p>
 * 以前这里的静态实例其实是 {@link BiomeGenAntimonia}(锑星群系)的实例, 导致本类构造器里的设置从未生效,
 * 雨滴能显示也只是靠锑星群系 {@code enableSnow = true} 的副作用。现在改成自己的实例, 并把降雨打开
 * (雨滴真正落下)、关掉降雪、温度取温带值(下的是雨不是雪)。降雨节奏由
 * {@code WorldProviderRoss123b#updateWeather} 控制(间歇阵雨)。
 */
public class BiomeGenBaseRoss123b extends BiomeGenAntimonia {

    public static BiomeGenBase ross123b;

    static {
        ross123b = new BiomeGenBaseRoss123b(124);
    }

    public BiomeGenBaseRoss123b(int id) {
        super(id);
        this.setBiomeName("Ross123b Ocean");
        this.setHeight(new BiomeGenBase.Height(0.1F, 0.2F));
        this.enableRain = true;
        this.enableSnow = false;
        this.temperature = -10.6F;
    }
}
