package com.newmaa.othtech.machine.machineclass;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.util.ResourceLocation;

import gregtech.api.enums.SoundResource;

public enum SoundResourceOTH {

    ENQING(350, "123technology", "machines.enqing");

    /**
     * Internal mapping by {@code int} id
     */
    private static final Map<Integer, SoundResourceOTH> ID_SOUND_MAP = new ConcurrentHashMap<>();
    /**
     * Internal mapping by {@code String} ResourceLocation
     */
    private static final Map<String, SoundResourceOTH> RESOURCE_STR_SOUND_MAP = new ConcurrentHashMap<>();
    static {
        EnumSet.allOf(SoundResourceOTH.class)
            .forEach(sound -> {
                if (sound.id < 0) {
                    return;
                }

                if (ID_SOUND_MAP.containsKey(sound.id)) {
                    throw new IllegalStateException(String.format("Sound ID %s is already occupied!", sound.id));
                }
                ID_SOUND_MAP.put(sound.id, sound);
            });
        EnumSet.allOf(SoundResourceOTH.class)
            .forEach(sound -> RESOURCE_STR_SOUND_MAP.put(sound.resourceLocation.toString(), sound));
    }

    /**
     * This Sound's identifier
     */
    public final int id;

    /**
     * The {@link ResourceLocation} of this {@link SoundResource}
     */
    public final ResourceLocation resourceLocation;

    SoundResourceOTH(final int id, final ResourceLocation resourceLocation) {
        this.id = id;
        this.resourceLocation = resourceLocation;
    }

    SoundResourceOTH(final int id, final String resourcePath) {
        this(id, new ResourceLocation(resourcePath));
    }

    SoundResourceOTH(final int id, final String resourceDomain, final String resourcePath) {
        this(id, new ResourceLocation(resourceDomain.toLowerCase(Locale.ENGLISH), resourcePath));
    }
}
