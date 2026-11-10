package com.newmaa.othtech.machine.machineclass;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.util.ResourceLocation;

public enum OTHSoundResource {

    ENQING(350, "123technology", "machines.enqing");

    private static final Map<Integer, OTHSoundResource> ID_SOUND_MAP = new ConcurrentHashMap<>();
    private static final Map<String, OTHSoundResource> RESOURCE_STR_SOUND_MAP = new ConcurrentHashMap<>();
    static {
        EnumSet.allOf(OTHSoundResource.class)
            .forEach(sound -> {
                if (sound.id < 0) {
                    return;
                }

                if (ID_SOUND_MAP.containsKey(sound.id)) {
                    throw new IllegalStateException(String.format("Sound ID %s is already occupied!", sound.id));
                }
                ID_SOUND_MAP.put(sound.id, sound);
            });
        EnumSet.allOf(OTHSoundResource.class)
            .forEach(sound -> RESOURCE_STR_SOUND_MAP.put(sound.resourceLocation.toString(), sound));
    }

    public final int id;

    public final ResourceLocation resourceLocation;

    OTHSoundResource(final int id, final ResourceLocation resourceLocation) {
        this.id = id;
        this.resourceLocation = resourceLocation;
    }

    OTHSoundResource(final int id, final String resourcePath) {
        this(id, new ResourceLocation(resourcePath));
    }

    OTHSoundResource(final int id, final String resourceDomain, final String resourcePath) {
        this(id, new ResourceLocation(resourceDomain.toLowerCase(Locale.ENGLISH), resourcePath));
    }
}
