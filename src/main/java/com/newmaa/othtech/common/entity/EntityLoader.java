package com.newmaa.othtech.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;

import com.newmaa.othtech.OTHTechnology;
import com.newmaa.othtech.common.dimensions.biome.BiomeGenBaseAntimonia;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import galaxyspace.core.world.GSBiomeGenBase;

public class EntityLoader {

    public EntityLoader() {
        registerEntity(EntityDogYellow.class, "DogYellow", 2, 80, 3, true);
        registerEntity(EntityDogRed.class, "DogRed", 3, 80, 3, true);
        registerEntity(EntityDogBlue.class, "DogBlue", 4, 80, 3, true);
        registerEntity(EntityNukeThrowable.class, "entityNukeThrowable", 1, 64, 10, true);
        registerEntity(EntityPositronShot.class, "entityPositronShot", 5, 512, 1, true);
        registerEntity(EntitySuicideDrone.class, "entitySuicideDrone", 6, 512, 1, true);
        registerEntity(EntityLonginus.class, "entityLonginus", 7, 96, 1, true);
        registerEntity(EntityFakePlayer.class, "CustomPlayer", 0, 80, 3, true);
        registerSpawns();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void allowDroneUpdate(EntityEvent.CanUpdate event) {
        // Drones perform their own loaded-area checks and must still age at a loaded chunk's edge.
        if (event.entity instanceof EntitySuicideDrone && event.entity.worldObj.blockExists(
            net.minecraft.util.MathHelper.floor_double(event.entity.posX),
            64,
            net.minecraft.util.MathHelper.floor_double(event.entity.posZ))) {
            event.canUpdate = true;
        }
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

    private static void registerSpawns() {
        addDogSpawns(EntityDogYellow.class);
        addDogSpawns(EntityDogRed.class);
        addDogSpawns(EntityDogBlue.class);
    }

    /** 三色狗刷怪权重完全一致，所以三种颜色会均匀混战。 */
    private static void addDogSpawns(Class<? extends EntityDogBase> dogClass) {
        addDogSpawn(dogClass, GSBiomeGenBase.SPACE, 6, 1, 5);
        addDogSpawn(dogClass, BiomeGenBaseAntimonia.antimonia, 8, 1, 8);
    }

    private static void addDogSpawn(Class<? extends EntityDogBase> dogClass, BiomeGenBase biome, int weight, int min,
        int max) {
        if (biome == null) {
            return;
        }
        EntityRegistry.addSpawn(dogClass, weight, min, max, EnumCreatureType.creature, biome);
    }
}
