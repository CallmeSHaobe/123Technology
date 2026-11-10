package com.newmaa.othtech.machine.machineclass;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.client.GTSoundLoop;

@SideOnly(Side.CLIENT)
public final class ForcedCompletionSoundLoop extends GTSoundLoop {

    private static final long SOUND_CYCLE_MILLIS = 10_000L;

    private final IGregTechTileEntity machine;
    private final int worldId;
    private final int tileX;
    private final int tileY;
    private final int tileZ;
    private long cycleStartedAtMillis = System.currentTimeMillis();
    private boolean machineActive = true;

    public ForcedCompletionSoundLoop(ResourceLocation sound, IGregTechTileEntity machine) {
        super(sound, machine, false, false, 1.0F);
        this.machine = machine;
        this.worldId = machine.getWorld().provider.dimensionId;
        this.tileX = machine.getXCoord();
        this.tileY = machine.getYCoord();
        this.tileZ = machine.getZCoord();
        this.repeat = true;
    }

    public void setMachineActive(boolean active) {
        this.machineActive = active;
    }

    @Override
    public void update() {
        if (donePlaying) return;

        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.thePlayer == null) {
            stop();
            return;
        }

        World world = minecraft.thePlayer.worldObj;
        if (world.provider.dimensionId != worldId
            || !world.checkChunksExist(tileX, tileY, tileZ, tileX, tileY, tileZ)) {
            stop();
            return;
        }

        TileEntity tile = world.getTileEntity(tileX, tileY, tileZ);
        if (tile != machine) {
            stop();
            return;
        }

        machineActive = machine.isActive() && !machine.isMuffled();

        // Keep looping while running. After the machine stops, do not start another cycle.
        repeat = machineActive;
        if (machineActive) {
            if (System.currentTimeMillis() - cycleStartedAtMillis >= SOUND_CYCLE_MILLIS) {
                cycleStartedAtMillis = System.currentTimeMillis();
            }
        } else if (System.currentTimeMillis() - cycleStartedAtMillis >= SOUND_CYCLE_MILLIS) {
            // Some OpenAL playback paths keep an already-started looping source alive even after
            // repeat becomes false. Stop explicitly at the end of the current ten-second cycle.
            stop();
        }
    }
}
