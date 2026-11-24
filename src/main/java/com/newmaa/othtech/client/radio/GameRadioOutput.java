package com.newmaa.othtech.client.radio;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.UUID;

import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;

import cpw.mods.fml.relauncher.ReflectionHelper;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;

/** Owns one streaming source inside Minecraft's existing sound system, not a separate native device. */
public final class GameRadioOutput {

    private static final Field MANAGER = ReflectionHelper.findField(SoundHandler.class, "sndManager", "field_147694_f");
    private static final Field SYSTEM = ReflectionHelper.findField(SoundManager.class, "sndSystem", "field_148620_e");
    private static final Field LOADED = ReflectionHelper.findField(SoundManager.class, "loaded", "field_148617_f");
    private static final Field LIBRARY = ReflectionHelper.findField(SoundSystem.class, "soundLibrary");

    private final RadioPcmCodec.Stream stream = new RadioPcmCodec.Stream();
    private final String name = "oth-radio-" + UUID.randomUUID();
    private final URL key;
    private final SoundSystem system;
    private boolean attached;
    private float appliedGain;

    public GameRadioOutput(SoundSystem system) {
        this.system = system;
        key = stream.url(name);
    }

    public static void registerCodec() {
        try {
            SoundSystemConfig.setCodec(RadioPcmCodec.EXTENSION, RadioPcmCodec.class);
        } catch (SoundSystemException ex) {
            throw new IllegalStateException("Couldn't register the radio PCM codec", ex);
        }
    }

    public static SoundSystem current(SoundHandler handler) {
        if (handler == null) return null;
        try {
            SoundManager manager = (SoundManager) MANAGER.get(handler);
            return LOADED.getBoolean(manager) ? (SoundSystem) SYSTEM.get(manager) : null;
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException("Couldn't access Minecraft's sound system", ex);
        }
    }

    public boolean belongsTo(SoundSystem current) {
        return system == current;
    }

    /** Called on the client thread; the actual sound commands run on the game's audio engine. */
    public boolean update(float gain) {
        if (!attached && stream.ready()) {
            system.newStreamingSource(
                false,
                name,
                key,
                key.getPath(),
                false,
                0,
                0,
                0,
                SoundSystemConfig.ATTENUATION_NONE,
                0);
            system.setVolume(name, gain);
            system.play(name);
            appliedGain = gain;
            attached = true;
            return true;
        }
        if (attached && appliedGain != gain) {
            system.setVolume(name, gain);
            appliedGain = gain;
        }
        return false;
    }

    public void write(byte[] pcm, int count, int rate, int channels) throws IOException {
        stream.write(pcm, count, rate, channels);
    }

    public String libraryName() {
        try {
            Object library = LIBRARY.get(system);
            return library == null ? "unavailable"
                : library.getClass()
                    .getName();
        } catch (IllegalAccessException ex) {
            return "unknown";
        }
    }

    public float millisecondsPlayed() {
        return attached && system.playing(name) ? system.millisecondsPlayed(name) : -1;
    }

    public void close(SoundSystem current) {
        stream.close();
        if (!attached) return;
        attached = false;
        if (belongsTo(current)) {
            system.stop(name);
            system.removeSource(name);
        }
    }
}
