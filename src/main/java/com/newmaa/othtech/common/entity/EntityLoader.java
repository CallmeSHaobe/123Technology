package com.newmaa.othtech.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;

import com.newmaa.othtech.OTHTechnology;
import com.newmaa.othtech.common.dimensions.biome.BiomeGenBaseAntimonia;
import com.newmaa.othtech.common.dimensions.biome.BiomeGenBaseRoss123b;

import cpw.mods.fml.common.registry.EntityRegistry;
import galaxyspace.core.world.GSBiomeGenBase;

public class EntityLoader {

    public EntityLoader() {
        registerEntity(EntityDogYellow.class, "DogYellow", 2, 80, 3, true);
        registerEntity(EntityDogRed.class, "DogRed", 3, 80, 3, true);
        registerEntity(EntityDogBlue.class, "DogBlue", 4, 80, 3, true);
        registerEntity(EntityNukeThrowable.class, "entityNukeThrowable", 1, 64, 10, true);
        registerEntity(EntityFakePlayer.class, "CustomPlayer", 0, 80, 3, true);
        registerSpawns();
    }

    private static void registerEntity(Class<? extends Entity> entityClass, String name, int IDs, int trackingRange,
        int updateFrequency, boolean sendsVelocityUpdates) {
        EntityRegistry.registerModEntity(
            entityClass,
            name,
            IDs,
            OTHTechnology.MODID,
            trackingRange,
            updateFrequency,
            sendsVelocityUpdates);
    }

    /**
     * 刷怪注册必须只跑一次，绝不能放在实体构造函数里。
     * <p>
     * FML 的 {@code EntityRegistry.addSpawn} 会无条件往群系的刷怪列表里追加条目（即使已经有同名条目，
     * 也只会改一下已有条目的权重然后照样追加），所以每 new 一只狗就会多出好几条刷怪条目：
     * 自然刷怪、{@code /summon}、EnderIO 灵魂瓶、mobsinfo/EEC 的 {@code createEntityCopy()} 全都会构造实体，
     * 列表只增不减，最终把刷怪权重稀释成「狗灾」。
     */
    private static void registerSpawns() {
        addDogSpawns(EntityDogYellow.class);
        addDogSpawns(EntityDogRed.class);
        addDogSpawns(EntityDogBlue.class);
    }

    /** 三色狗刷怪权重完全一致，所以三种颜色会均匀混战。 */
    private static void addDogSpawns(Class<? extends EntityDogBase> dogClass) {
        addDogSpawn(dogClass, GSBiomeGenBase.SPACE, 6, 1, 5);
        addDogSpawn(dogClass, BiomeGenBaseAntimonia.antimonia, 8, 1, 8);
        addDogSpawn(dogClass, BiomeGenBaseRoss123b.ross123b, 8, 1, 8);
    }

    private static void addDogSpawn(Class<? extends EntityDogBase> dogClass, BiomeGenBase biome, int weight, int min,
        int max) {
        if (biome == null) {
            return;
        }
        EntityRegistry.addSpawn(dogClass, weight, min, max, EnumCreatureType.creature, biome);
    }
}
