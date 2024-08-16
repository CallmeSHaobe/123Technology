package com.newmaa.othtech;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(
    modid = MyMod.MODID,
    version = MyMod.VERSION,
    name = MyMod.modName,
    dependencies = "required-after:IC2;" + "required-after:structurelib;"
        + "required-after:modularui;"
        + "after:GalacticraftCore;"
        + "required-after:bartworks;"
        + "after:miscutils;"
        + "after:dreamcraft;"
        + "after:GalacticraftMars;"
        + "required-after:gregtech;"
        + "after:GalacticraftPlanets",
    acceptedMinecraftVersions = "1.7.10")
public class MyMod {

    public static final String MODID = "123Technology";

    public static final String modName = "123Technology";

    public static final String VERSION = "1.2.3";

    public static final String Arthor = "NewMaa & JulianChum";

    public static final String modDescription = "123";
    public static final Logger LOG = LogManager.getLogger(MODID);

    @SidedProxy(clientSide = "com.newmaa.othtech.ClientProxy", serverSide = "com.newmaa.othtech.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }
}
