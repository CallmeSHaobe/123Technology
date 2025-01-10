package com.newmaa.othtech.common.entity;

import net.minecraft.entity.Entity;

import com.newmaa.othtech.OTHTechnology;

import cpw.mods.fml.common.registry.EntityRegistry;

public class entityLoader {

    private static int nextID = 0;

    public entityLoader() {
        registerEntity(entityDogYellow.class, "DogYellow", 80, 3, true);
        registerEntity(entityNukeThrowable.class, "entityNukeThrowable", 64, 10, true);
    }

    private static void registerEntity(Class<? extends Entity> entityClass, String name, int trackingRange,
        int updateFrequency, boolean sendsVelocityUpdates) {
        EntityRegistry.registerModEntity(
            entityClass,
            name,
            nextID++,
            OTHTechnology.MODID,
            trackingRange,
            updateFrequency,
            sendsVelocityUpdates);
    }

}
