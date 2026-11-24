package com.newmaa.othtech;

import static com.newmaa.othtech.CommonProxy.RocketRenderBlock;
import static com.newmaa.othtech.common.OTHItemList.SpaceElevatorModulePumpT4;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.newmaa.othtech.common.ItemAndBlockHandler;
import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.OreDictionaryLoader;
import com.newmaa.othtech.common.beeyonds.OTHBeeyonds;
import com.newmaa.othtech.common.blocks.antimonia.AntimoniaBlocks;
import com.newmaa.othtech.common.blocks.deepseiko.DeepSeikoBlocks;
import com.newmaa.othtech.common.blocks.fluids.AntimoniaFluids;
import com.newmaa.othtech.common.blocks.fluids.RossFluids;
import com.newmaa.othtech.common.blocks.nailong.NailongCasings;
import com.newmaa.othtech.common.blocks.oterender.BlockRocketRender;
import com.newmaa.othtech.common.blocks.oterender.OTERocketRender;
import com.newmaa.othtech.common.compat.thaumcraft.EssentiaGeneratorCompat;
import com.newmaa.othtech.common.compat.thaumcraft.ThaumcraftResearchCompat;
import com.newmaa.othtech.common.compat.thaumcraft.VoidNexusResearchCompat;
import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;
import com.newmaa.othtech.common.dimensions.AntimoniaUndergroundFluids;
import com.newmaa.othtech.common.dimensions.OTHDimensionIntegration;
import com.newmaa.othtech.common.dimensions.RegisterDimensions;
import com.newmaa.othtech.common.dimensions.Ross123bUndergroundFluids;
import com.newmaa.othtech.common.dimensions.gtoregen.VisualProspectingOreVeinBridge;
import com.newmaa.othtech.common.item.ItemLoader;
import com.newmaa.othtech.common.materials.MaterialsLoader;
import com.newmaa.othtech.common.materials.OTHMaterials;
import com.newmaa.othtech.common.recipemap.NEIRecipeMaps;
import com.newmaa.othtech.event.EventPlayerDied;
import com.newmaa.othtech.event.EventWorldLoad;
import com.newmaa.othtech.machine.MachineLoader;
import com.newmaa.othtech.machine.machineclass.OTHMobHandlerLoader;
import com.newmaa.othtech.quest.QuestLoader;
import com.newmaa.othtech.recipe.RecipeLoader;
import com.newmaa.othtech.recipe.RecipesDeepSeiko;
import com.newmaa.othtech.recipe.RecipesMCATransformer;

import codechicken.nei.api.API;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.Type;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTOreDictUnificator;
import gtnhintergalactic.recipe.SpacePumpingRecipes;

@Mod(
    modid = OTHTechnology.MODID,
    version = OTHTechnology.VERSION,
    name = OTHTechnology.modName,
    dependencies = "required-after:IC2;" + "required-after:structurelib;"
        + "required-after:ForbiddenMagic;"
        + "required-after:ThaumicTinkerer;"
        + "required-after:BloodArsenal;"
        + "required-after:Avaritia;"
        + "required-after:eternalsingularity;"
        + "required-after:modularui;"
        + "required-after:GalacticraftCore;"
        + "required-after:bartworks;"
        + "required-after:miscutils;"
        + "required-after:dreamcraft;"
        + "required-after:gregtech;"
        + "required-after:Thaumcraft;"
        + "required-before:TwistSpaceTechnology;"
        + "after:cropsnh;"
        + "after:etfuturum;"
        + "required-after:GalaxySpace;",
    acceptedMinecraftVersions = "1.7.10")
public class OTHTechnology {

    public static final String MODID = "123technology";
    private static final String LEGACY_MODID = "123Technology";

    public static final String modName = Tags.MODNAME;

    public static final String VERSION = "2.3.0";

    public static final String Arthur = "Laodengs";

    public static final String modDescription = "123";
    public static final Logger LOG = LogManager.getLogger(MODID);
    public static final boolean isInDevMode = false;

    @SidedProxy(clientSide = "com.newmaa.othtech.ClientProxy", serverSide = "com.newmaa.othtech.CommonProxy")
    public static CommonProxy proxy;

    public OTHTechnology() {
        MinecraftForge.EVENT_BUS.register(new EventPlayerDied());
        MinecraftForge.EVENT_BUS.register(new EventWorldLoad());
        OTHMaterials.install();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        OTHDimensionIntegration.registerNEIDimension();
        OTHDimensionIntegration.scheduleOreVeinRegistration();
        if (GregTechAPI.sGTCompleteLoad != null) {
            GregTechAPI.sGTCompleteLoad.add(RecipesMCATransformer::load);
            GregTechAPI.sGTCompleteLoad.add(RecipesDeepSeiko::loadCondensateRecipes);
            GregTechAPI.sGTCompleteLoad.add(RecipesDeepSeiko::loadAssemblyLineRecipes);
        } else {
            RecipesMCATransformer.load();
            RecipesDeepSeiko.loadCondensateRecipes();
            RecipesDeepSeiko.loadAssemblyLineRecipes();
        }

        new CreativeTabsLoader(event);
        EssentiaGeneratorCompat.registerParts();
        new ItemLoader(event);
        new OreDictionaryLoader(event);
        OTHMobHandlerLoader.init();
        GameRegistry.registerTileEntity(OTERocketRender.class, "RocketRender");
        GameRegistry.registerBlock(RocketRenderBlock, RocketRenderBlock.getUnlocalizedName());
        proxy.preInit(event);
        MaterialsLoader.load();
        ItemAndBlockHandler.INSTANCE.run();
        AntimoniaBlocks.initialize();
        AntimoniaFluids.initialize();
        RossFluids.initialize();
        DeepSeikoBlocks.initialize();
        NailongCasings.initialize();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new EventPlayerDied());
        proxy.init(event);
        if (OTHMaterials.ManifestedPsionics != null) {
            ItemStack psionicIngot = GTOreDictUnificator.get(OrePrefixes.ingot, OTHMaterials.ManifestedPsionics, 1);
            if (psionicIngot != null) OTHItemList.ManifestedPsionics.set(psionicIngot);
        }
        MachineLoader.loadMachines();
        new RegisterDimensions().init();
        AntimoniaUndergroundFluids.register();
        Ross123bUndergroundFluids.register();
        NEIRecipeMaps.IMCSender();
        API.hideItem(new ItemStack(DeepSeikoBlocks.dsHolo));
        API.hideItem(new ItemStack(AntimoniaFluids.BlockAquaRegia));
        API.hideItem(new ItemStack(RossFluids.BlockLCL));
        API.hideItem(new ItemStack(new BlockRocketRender()));
    }

    @Mod.EventHandler
    public void remapLegacyRegistrations(FMLMissingMappingsEvent event) {
        String legacyPrefix = LEGACY_MODID + ":";
        for (FMLMissingMappingsEvent.MissingMapping mapping : event.getAll()) {
            if ((MODID + ":speaking_stone_vanilla").equals(mapping.name)) {
                // The temporary comparison block is intentionally removed; do not block world loading for it.
                mapping.ignore();
                continue;
            }
            if (!mapping.name.startsWith(legacyPrefix)) continue;

            String path = mapping.name.substring(legacyPrefix.length());
            if (mapping.type == Type.BLOCK) {
                Block target = GameRegistry.findBlock(MODID, path);
                if (target != null) {
                    mapping.remap(target);
                } else {
                    LOG.warn("No replacement block for legacy mapping {}", mapping.name);
                }
            } else if (mapping.type == Type.ITEM) {
                Item target = GameRegistry.findItem(MODID, path);
                if (target != null) {
                    mapping.remap(target);
                } else {
                    LOG.warn("No replacement item for legacy mapping {}", mapping.name);
                }
            }
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
        // I Love Isa Mill
        LOG.info("The Isa Mill is invincible!");
        /**
         * By czqwq
         *
         */
        LOG.info("We can never repay NewMaa's kindness!");

        MachineLoader.loadMachinePostInit();
        ThaumcraftResearchCompat.register();
        SpacePumpingRecipes.RECIPES.put(Pair.of(2, 2), FluidRegistry.getFluidStack("lava", 1792000));
        SpacePumpingRecipes.RECIPES.put(Pair.of(2, 3), FluidRegistry.getFluidStack("cryotheum", 1792000));
        SpacePumpingRecipes.RECIPES.put(Pair.of(2, 4), FluidRegistry.getFluidStack("pyrotheum", 1792000));
        SpacePumpingRecipes.RECIPES.put(Pair.of(2, 5), FluidRegistry.getFluidStack("liquiddna", 1792000));
        SpacePumpingRecipes.RECIPES.put(Pair.of(2, 6), FluidRegistry.getFluidStack("chlorine", 1230000));
        API.addRecipeCatalyst(
            SpaceElevatorModulePumpT4.getInternalStack_unsafe(),
            "gtnhintergalactic.nei.SpacePumpModuleRecipeHandler");

    }

    @Mod.EventHandler
    public void completeInit(FMLLoadCompleteEvent event) {
        safeStep("RecipeLoader.loadRecipes", RecipeLoader::loadRecipes);
        safeStep("RecipesDeepSeiko.loadCondensateRecipes", RecipesDeepSeiko::loadCondensateRecipes);
        safeStep("RecipesDeepSeiko.loadAssemblyLineRecipes", RecipesDeepSeiko::loadAssemblyLineRecipes);
        safeStep("VoidNexusResearchCompat", VoidNexusResearchCompat::register);
        safeStep("EssentiaGeneratorResearch", EssentiaGeneratorCompat::registerResearch);
        safeStep("OTHBeeyonds", OTHBeeyonds::new);
        LOG.info("completeInit: finished.");
    }

    private static void safeStep(String name, Runnable step) {
        try {
            step.run();
        } catch (Throwable t) {
            LOG.error("completeInit step '" + name + "' failed; skipped so world loading is not blocked", t);
        }
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);

    }

    @Mod.EventHandler
    public void serverAboutToStart(FMLServerAboutToStartEvent event) {
        VisualProspectingOreVeinBridge.ensureRegistered();
    }

    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {}

    @Mod.EventHandler
    public void earlyGame(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());
        if (gregtech.api.enums.Mods.BetterQuesting.isModLoaded() && Config.enableQuestInjection) {
            QuestLoader.registry();
        }
    }

    @Mod.EventHandler
    public void onLoadComplete(FMLLoadCompleteEvent event) {
        proxy.onLoadComplete(event);

    }

}
