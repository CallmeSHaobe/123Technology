package com.newmaa.othtech;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.newmaa.othtech.common.ItemAndBlockHandler;
import com.newmaa.othtech.common.beeyonds.OTHBeeyonds;
import com.newmaa.othtech.common.materials.MaterialsLoader;
import com.newmaa.othtech.common.recipemap.NEIRecipeMaps;
import com.newmaa.othtech.event.EventLogin;
import com.newmaa.othtech.machine.MachineLoader;
import com.newmaa.othtech.recipe.RecipeLoader;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(
    modid = OTHTechnology.MODID,
    version = OTHTechnology.VERSION,
    name = OTHTechnology.modName,
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
public class OTHTechnology {

    public static final String MODID = "123Technology";

    public static final String modName = Tags.MODNAME;

    public static final String VERSION = "2.0.5";

    public static final String Arthur = "Laodengs";

    public static final String modDescription = "123";
    public static final Logger LOG = LogManager.getLogger(MODID);
    public static final boolean isInDevMode = false;

    @SidedProxy(clientSide = "com.newmaa.othtech.ClientProxy", serverSide = "com.newmaa.othtech.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
        MaterialsLoader.load();
        ItemAndBlockHandler.INSTANCE.run();
    }

    @Mod.EventHandler
    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new EventLogin());
        proxy.init(event);
        MachineLoader.loadMachines();
        NEIRecipeMaps.IMCSender();
    }

    @Mod.EventHandler
    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
        // RecipeLoader.loadRecipesPostInit();
    }

    @Mod.EventHandler
    public void completeInit(FMLLoadCompleteEvent event) {
        RecipeLoader.loadRecipes();
        new OTHBeeyonds();
    }

    @Mod.EventHandler
    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);

    }

    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {

    }

    @Mod.EventHandler
    public void earlyGame(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());
    }

    @Mod.EventHandler
    public void onLoadComplete(FMLLoadCompleteEvent event) {

        proxy.onLoadComplete(event);

    }

    private final Map<EntityPlayer, Integer> playerTimerMap = new HashMap<>();
}
