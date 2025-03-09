package com.newmaa.othtech.common.entity;

import net.minecraft.entity.Entity;

import com.newmaa.othtech.OTHTechnology;

import cpw.mods.fml.common.registry.EntityRegistry;

public class entityLoader {

    public entityLoader() {
        registerEntity(entityDogYellow.class, "DogYellow", 2, 80, 3, true);
        registerEntity(entityNukeThrowable.class, "entityNukeThrowable", 1, 64, 10, true);
        registerEntity(entityFakePlayer.class, "CustomPlayer", 0, 80, 3, true);
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

}
