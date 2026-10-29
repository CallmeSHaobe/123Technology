package com.newmaa.othtech.event;

import net.minecraftforge.event.world.WorldEvent;

import com.newmaa.othtech.common.dimensions.gtoregen.VisualProspectingOreVeinBridge;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * 世界加载时补一次 VisualProspecting 的矿脉注册。
 * <p>
 * VP 要到 {@code FMLLoadCompleteEvent} 才把自己的矿脉表建好，客户端连服务器时也没有服务端事件，
 * 所以客户端这边靠 {@code WorldEvent.Load} 兜底（客户端缓存在 {@code PlayerLoggedInEvent} 才读，来得及）。
 */
public class EventWorldLoad {

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        VisualProspectingOreVeinBridge.ensureRegistered();
    }
}
