package com.newmaa.othtech;

import com.newmaa.othtech.common.OreDictionaryLoader;
import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;
import com.newmaa.othtech.common.item.ItemLoader;
import com.newmaa.othtech.machine.MachineLoader;
import com.newmaa.othtech.recipe.*;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CommonProxy {

    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {

        new CreativeTabsLoader(event);
        new ItemLoader(event);
        new OreDictionaryLoader(event);
    }

    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {

    }

    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {
        MachineLoader.loadMachines();
        new loadRecipe().loadRecipes();
        new QFT().loadRecipes();
        new MAXs().loadRecipes();
        new EBF().loadRecipes();
        new Freezer().loadRecipes();
        new Mixer().loadRecipes();
        new MegaISA().loadRecipes();
        new Comass().loadRecipes();


    }

    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {}
}
