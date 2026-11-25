package com.newmaa.othtech;

import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;

import com.newmaa.othtech.common.blocks.oterender.BlockRocketRender;
import com.newmaa.othtech.common.dialogue.AyanamiDialogueNetwork;
import com.newmaa.othtech.common.dimensions.ross123b.Ross123bBiomeMigration;
import com.newmaa.othtech.common.dimensions.ross123b.Ross123bSpawnHandler;
import com.newmaa.othtech.common.entity.AyanamiHostileTargeting;
import com.newmaa.othtech.common.entity.DroneChunkLoader;
import com.newmaa.othtech.common.entity.EntityLoader;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CommonProxy {

    public static final Block RocketRenderBlock = new BlockRocketRender();

    public void preInit(FMLPreInitializationEvent event) {
        DroneChunkLoader.register(
            cpw.mods.fml.common.Loader.instance()
                .getIndexedModList()
                .get(OTHTechnology.MODID)
                .getMod());
    }

    public void init(FMLInitializationEvent event) {
        new EntityLoader();
        AyanamiDialogueNetwork.register();
        MinecraftForge.EVENT_BUS.register(new AyanamiHostileTargeting());
        MinecraftForge.EVENT_BUS.register(new Ross123bSpawnHandler());
        MinecraftForge.EVENT_BUS.register(new Ross123bBiomeMigration());
    }

    public void postInit(FMLPostInitializationEvent event) {}

    public void serverStarting(FMLServerStartingEvent event) {}

    public void queueAyanamiReply(int dimension, int entityId, String text) {}

    public void queueAyanamiLocalReply(int dimension, int entityId, String key) {}

    public void queueAyanamiAmbientReply(int dimension, int entityId, String text, boolean localize) {}

    public void onLoadComplete(FMLLoadCompleteEvent event) {}
}
