package com.newmaa.othtech;

import net.minecraft.block.Block;

import com.newmaa.othtech.common.OreDictionaryLoader;
import com.newmaa.othtech.common.blocks.oterender.BlockRocketRender;
import com.newmaa.othtech.common.blocks.oterender.OTERocketRender;
import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;
import com.newmaa.othtech.common.entity.EntityLoader;
import com.newmaa.othtech.common.item.ItemLoader;
import com.newmaa.othtech.common.materials.BWMaterialsLocalization;
import com.newmaa.othtech.machine.machineclass.OTHMobHandlerLoader;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy {

    public static final Block RocketRenderBlock = new BlockRocketRender();

    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        new BWMaterialsLocalization().loader();
        new CreativeTabsLoader(event);
        new ItemLoader(event);
        new OreDictionaryLoader(event);
        OTHMobHandlerLoader.init();
        GameRegistry.registerTileEntity(OTERocketRender.class, "RocketRender");
        GameRegistry.registerBlock(RocketRenderBlock, RocketRenderBlock.getUnlocalizedName());
    }

    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
        new EntityLoader();
    }

    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {

    }

    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {}

    public void onLoadComplete(FMLLoadCompleteEvent event) {}
}
