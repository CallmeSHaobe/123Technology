package com.newmaa.othtech;

import static com.newmaa.othtech.common.OTHItemList.SpaceElevatorModulePumpT4;

import java.util.HashMap;
import java.util.Map;

import com.newmaa.othtech.recipe.RecipesEXH;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.newmaa.othtech.common.ItemAndBlockHandler;
import com.newmaa.othtech.common.beeyonds.OTHBeeyonds;
import com.newmaa.othtech.common.blocks.antimonia.AntimoniaBlocks;
import com.newmaa.othtech.common.blocks.fluids.AntimoniaFluids;
import com.newmaa.othtech.common.dimensions.RegisterAntimonia;
import com.newmaa.othtech.common.materials.MaterialsLoader;
import com.newmaa.othtech.common.recipemap.NEIRecipeMaps;
import com.newmaa.othtech.event.EventPlayerDied;
import com.newmaa.othtech.machine.MachineLoader;
import com.newmaa.othtech.recipe.RecipeLoader;

import codechicken.nei.api.API;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gtnhintergalactic.recipe.SpacePumpingRecipes;

@Mod(
    modid = OTHTechnology.MODID,
    version = OTHTechnology.VERSION,
    name = OTHTechnology.modName,
    dependencies = "required-after:IC2;" + "required-after:structurelib;"
        + "required-after:Avaritia;"
        + "required-after:eternalsingularity;"
        + "required-after:modularui;"
        + "after:GalacticraftCore;"
        + "required-after:bartworks;"
        + "after:miscutils;"
        + "after:dreamcraft;"
        + "after:GalacticraftMars;"
        + "required-after:gregtech;"
        + "after:GalacticraftPlanets;"
        + "required-after:GalaxySpace;",
    acceptedMinecraftVersions = "1.7.10")
public class OTHTechnology {

    public static final String MODID = "123Technology";

    public static final String modName = Tags.MODNAME;

    public static final String VERSION = "2.1.2";

    public static final String Arthur = "Laodengs";

    public static final String modDescription = "123";
    public static final Logger LOG = LogManager.getLogger(MODID);
    public static final boolean isInDevMode = false;

    @SidedProxy(clientSide = "com.newmaa.othtech.ClientProxy", serverSide = "com.newmaa.othtech.CommonProxy")
    public static CommonProxy proxy;

    public OTHTechnology() {
        MinecraftForge.EVENT_BUS.register(new EventPlayerDied());
    }

    @Mod.EventHandler
    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
        MaterialsLoader.load();
        ItemAndBlockHandler.INSTANCE.run();
        AntimoniaBlocks.initialize();
        AntimoniaFluids.initialize();
    }

    @Mod.EventHandler
    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new EventPlayerDied());
        proxy.init(event);
        MachineLoader.loadMachines();
        new RegisterAntimonia().init();
        NEIRecipeMaps.IMCSender();
    }

    @SideOnly(Side.CLIENT)
    public static void registerRenderers() {}

    @Mod.EventHandler
    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
        MachineLoader.loadMachinePostInit();
        // RecipeLoader.loadRecipesPostInit();
        // 添加自定义配方
        SpacePumpingRecipes.RECIPES.put(Pair.of(2, 2), FluidRegistry.getFluidStack("lava", 1792000));
        SpacePumpingRecipes.RECIPES.put(Pair.of(2, 3), FluidRegistry.getFluidStack("cryotheum", 1792000));
        SpacePumpingRecipes.RECIPES.put(Pair.of(2, 4), FluidRegistry.getFluidStack("pyrotheum", 1792000));
        SpacePumpingRecipes.RECIPES.put(Pair.of(2, 5), FluidRegistry.getFluidStack("liquiddna", 1792000));
        SpacePumpingRecipes.RECIPES.put(Pair.of(2, 6), FluidRegistry.getFluidStack("chlorine", 1230000));
        // // load SpacePumpingT4 to NEIRecipes
        API.addRecipeCatalyst(
            SpaceElevatorModulePumpT4.getInternalStack_unsafe(),
            "gtnhintergalactic.nei.SpacePumpModuleRecipeHandler");

    }

    @Mod.EventHandler
    public void completeInit(FMLLoadCompleteEvent event) {
        RecipeLoader.loadRecipes();
        new OTHBeeyonds();
        // 添加自定义配方
    }

    @Mod.EventHandler
    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);

    }

    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {}

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
