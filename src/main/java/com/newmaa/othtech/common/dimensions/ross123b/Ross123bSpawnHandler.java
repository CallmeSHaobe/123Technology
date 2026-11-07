package com.newmaa.othtech.common.dimensions.ross123b;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/** Keep Ross123b devoid of living entities without blocking players or spacecraft. */
public final class Ross123bSpawnHandler {

    @SubscribeEvent
    public void onEntityJoin(EntityJoinWorldEvent event) {
        if (!event.world.isRemote && event.world.provider instanceof WorldProviderRoss123b
            && event.entity instanceof EntityLivingBase
            && !(event.entity instanceof EntityPlayer)) {
            event.setCanceled(true);
        }
    }
}
