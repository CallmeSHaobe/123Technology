package com.newmaa.othtech.event;

import net.minecraftforge.event.world.WorldEvent;

import com.newmaa.othtech.common.dimensions.gtoregen.VisualProspectingOreVeinBridge;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EventWorldLoad {

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        VisualProspectingOreVeinBridge.ensureRegistered();
    }
}
