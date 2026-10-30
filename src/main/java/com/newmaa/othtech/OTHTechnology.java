package com.newmaa.othtech;

import static com.newmaa.othtech.CommonProxy.RocketRenderBlock;
import static com.newmaa.othtech.common.OTHItemList.SpaceElevatorModulePumpT4;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.newmaa.othtech.common.ItemAndBlockHandler;
import com.newmaa.othtech.common.OreDictionaryLoader;
import com.newmaa.othtech.common.beeyonds.OTHBeeyonds;
import com.newmaa.othtech.common.blocks.antimonia.AntimoniaBlocks;
import com.newmaa.othtech.common.blocks.deepseiko.DeepSeikoBlocks;
import com.newmaa.othtech.common.blocks.fluids.AntimoniaFluids;
import com.newmaa.othtech.common.blocks.fluids.RossFluids;
import com.newmaa.othtech.common.blocks.oterender.OTERocketRender;
import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;
import com.newmaa.othtech.common.dimensions.AntimoniaUndergroundFluids;
import com.newmaa.othtech.common.dimensions.OTHDimensionIntegration;
import com.newmaa.othtech.common.dimensions.RegisterDimensions;
import com.newmaa.othtech.common.dimensions.gtoregen.VisualProspectingOreVeinBridge;
import com.newmaa.othtech.common.item.ItemLoader;
import com.newmaa.othtech.common.materials.BWMaterialsLocalization;
import com.newmaa.othtech.common.materials.MaterialsLoader;
import com.newmaa.othtech.common.recipemap.DeepSeikoALRecipeLoader;
import com.newmaa.othtech.common.recipemap.DeepSeikoBECRecipeLoader;
import com.newmaa.othtech.common.recipemap.MegaCALRecipeLoader;
import com.newmaa.othtech.common.recipemap.NEIRecipeMaps;
import com.newmaa.othtech.event.EventPlayerDied;
import com.newmaa.othtech.event.EventWorldLoad;
import com.newmaa.othtech.machine.MachineLoader;
import com.newmaa.othtech.machine.machineclass.OTHMobHandlerLoader;
import com.newmaa.othtech.quest.QuestLoader;
import com.newmaa.othtech.recipe.RecipeLoader;

import codechicken.nei.api.API;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTechAPI;
import gtnhintergalactic.recipe.SpacePumpingRecipes;

@Mod(
    modid = OTHTechnology.MODID,
    version = OTHTechnology.VERSION,
    name = OTHTechnology.modName,
    dependencies = "required-after:IC2;" + "required-after:structurelib;"
        + "required-after:Avaritia;"
        + "required-after:eternalsingularity;"
        + "required-after:modularui;"
        + "required-after:GalacticraftCore;"
        + "required-after:bartworks;"
        + "required-after:miscutils;"
        + "required-after:dreamcraft;"
        + "required-after:gregtech;"
        // CropsNH 水稻作物：软依赖（没有 cropsnh 时 mixin 会跳过，不影响其它功能）
        + "after:cropsnh;"
        + "required-after:GalaxySpace;",
    acceptedMinecraftVersions = "1.7.10")
public class OTHTechnology {

    public static final String MODID = "123Technology";

    public static final String modName = Tags.MODNAME;

    public static final String VERSION = "2.1.5";

    public static final String Arthur = "Laodengs";

    public static final String modDescription = "123";
    public static final Logger LOG = LogManager.getLogger(MODID);
    public static final boolean isInDevMode = false;

    @SidedProxy(clientSide = "com.newmaa.othtech.ClientProxy", serverSide = "com.newmaa.othtech.CommonProxy")
    public static CommonProxy proxy;

    public OTHTechnology() {
        MinecraftForge.EVENT_BUS.register(new EventPlayerDied());
        // VisualProspecting（旅行地图的矿脉/流体叠层）的锑星矿脉补注册；世界加载时兜底执行
        MinecraftForge.EVENT_BUS.register(new EventWorldLoad());
    }

    @Mod.EventHandler
    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        // 锑星的 NEI 维度数据与 GT 矿脉注册（替代原 GTNEIDimensionHelperOTHMixin / GTOreGenOTHMixin）
        OTHDimensionIntegration.registerNEIDimension();
        OTHDimensionIntegration.scheduleOreVeinRegistration();
        // 进阶高能电路装配线的配方池：在 FML load complete 阶段（dreamcraft 注册完 CAL 配方之后）
        // 拷贝 CAL 配方并去掉压印电路需求
        if (GregTechAPI.sGTCompleteLoad != null) {
            GregTechAPI.sGTCompleteLoad.add(MegaCALRecipeLoader::load);
            // 迪普西科凝聚态配方池：拷贝 BEC 配方并把纠缠凝聚态换成普通形态流体（GT 会先跑完
            // BECRecipes.runLateRecipes() 再遍历 sGTCompleteLoad，时序必然安全）
            GregTechAPI.sGTCompleteLoad.add(DeepSeikoBECRecipeLoader::load);
            GregTechAPI.sGTCompleteLoad.add(DeepSeikoALRecipeLoader::load);
        } else {
            MegaCALRecipeLoader.load();
            DeepSeikoBECRecipeLoader.load();
            DeepSeikoALRecipeLoader.load();
        }

        new BWMaterialsLocalization().loader();
        new CreativeTabsLoader(event);
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
    }

    @Mod.EventHandler
    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new EventPlayerDied());
        proxy.init(event);
        MachineLoader.loadMachines();
        new RegisterDimensions().init();
        AntimoniaUndergroundFluids.register();
        NEIRecipeMaps.IMCSender();
        // 迪普西科的米饭投影影子方块是纯技术方块，别让它在 NEI 里冒出来
        API.hideItem(new ItemStack(DeepSeikoBlocks.dsHolo));
    }

    @SideOnly(Side.CLIENT)
    public static void registerRenderers() {}

    @Mod.EventHandler
    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
        LOG.info("The Isa Mill is invincible!");
        LOG.info("NewMaa的恩情还不完!");// 这个不是我写的。
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
        // ⚠ 这里抛任何异常都会被 FML 记为致命错误，后果是「所有存档进不去、新建世界失败」，
        // 2026-10-26 14:26 就因为一条 NEI 展示配方抛了 IllegalArgumentException 而炸过。
        // 所以每一步都单独兜底：失败只记日志，绝不阻塞世界加载。
        safeStep("RecipeLoader.loadRecipes", RecipeLoader::loadRecipes);
        safeStep("DeepSeikoBECRecipeLoader", DeepSeikoBECRecipeLoader::load);
        safeStep("DeepSeikoALRecipeLoader", DeepSeikoALRecipeLoader::load);
        safeStep("OTHBeeyonds", OTHBeeyonds::new);
        LOG.info("completeInit: finished.");
    }

    /** 执行一个初始化步骤；任何异常/错误都吞掉并记日志，保证 completeInit 不会把世界挡在门外 */
    private static void safeStep(String name, Runnable step) {
        try {
            step.run();
        } catch (Throwable t) {
            LOG.error("completeInit step '" + name + "' failed; skipped so world loading is not blocked", t);
        }
    }

    @Mod.EventHandler
    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);

    }

    @Mod.EventHandler
    public void serverAboutToStart(FMLServerAboutToStartEvent event) {
        // 服务端在 FMLServerStartingEvent 阶段会加载 VisualProspecting 的矿脉缓存，
        // 必须赶在那之前把锑星的自定义矿脉补进 VP 的矿脉表，否则旧存档里的锑星矿脉会被读成 NO_VEIN。
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

    private final Map<EntityPlayer, Integer> playerTimerMap = new HashMap<>();

}
