package com.newmaa.othtech;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.common.MinecraftForge;

import com.newmaa.othtech.client.OTEEntangledFluidRenderer;
import com.newmaa.othtech.client.OTEMaterialRenderEffects;
import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.blocks.deepseiko.TEDeepSeikoHolo;
import com.newmaa.othtech.common.blocks.fluids.RossFluids;
import com.newmaa.othtech.common.dimensions.sky.RossFogHandler;
import com.newmaa.othtech.common.dimensions.sky.SkyProviderHandler;
import com.newmaa.othtech.common.entity.EntityDogBlue;
import com.newmaa.othtech.common.entity.EntityDogRed;
import com.newmaa.othtech.common.entity.EntityDogYellow;
import com.newmaa.othtech.common.entity.EntityFakePlayer;
import com.newmaa.othtech.common.entity.EntityNukeThrowable;
import com.newmaa.othtech.common.entity.RenderDogColored;
import com.newmaa.othtech.common.entity.RenderPlayer;
import com.newmaa.othtech.common.item.ItemLoader;
import com.newmaa.othtech.common.item.itemArmor.ItemHelmTallHatRender;
import com.newmaa.othtech.common.item.itemBlock.ItemModelAsukaRender;
import com.newmaa.othtech.common.item.itemBlock.ItemModelAyanamiRender;
import com.newmaa.othtech.common.item.itemBlock.ItemModelTallHatRender;
import com.newmaa.othtech.common.materials.OTHMaterials;
import com.newmaa.othtech.common.render.RiceBowlRenderer;
import com.newmaa.othtech.common.render.RocketRenderOTH;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {

    public static IModelCustom objHelmetRaw;
    public static ModelBiped objHelmetModel;

    public void register_event(Object obj) {
        FMLCommonHandler.instance()
            .bus()
            .register(obj);
        MinecraftForge.EVENT_BUS.register(obj);
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        SkyProviderHandler sky = new SkyProviderHandler();
        this.register_event(sky);
        this.register_event(new RossFogHandler());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        new RocketRenderOTH();

        new ItemModelAyanamiRender();
        new ItemModelAsukaRender();
        new ItemModelTallHatRender();

        registerRenderers();
        RenderingRegistry.registerEntityRenderingHandler(EntityFakePlayer.class, new RenderPlayer());

        OTEMaterialRenderEffects.install();
        OTEEntangledFluidRenderer.register();

        ClientRegistry.bindTileEntitySpecialRenderer(TEDeepSeikoHolo.class, new RiceBowlRenderer());

        RenderingRegistry
            .registerEntityRenderingHandler(EntityDogYellow.class, new RenderDogColored(RenderDogColored.YELLOW));
        RenderingRegistry
            .registerEntityRenderingHandler(EntityDogRed.class, new RenderDogColored(RenderDogColored.RED));
        RenderingRegistry
            .registerEntityRenderingHandler(EntityDogBlue.class, new RenderDogColored(RenderDogColored.BLUE));
        RenderingRegistry.registerEntityRenderingHandler(
            EntityNukeThrowable.class,
            new RenderSnowball(ItemLoader.itemNukeThrowable));

    }

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Post event) {
        if (event.map.getTextureType() != 0 || OTHMaterials.LCL == null || OTHMaterials.LCL.mFluid == null
            || RossFluids.BlockLCL == null) return;
        OTHMaterials.LCL.mFluid.setIcons(RossFluids.BlockLCL.getIcon(1, 0), RossFluids.BlockLCL.getIcon(2, 0));
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }

    // Override CommonProxy methods here, if you want a different behaviour on the client (e.g. registering renders).
    // Don't forget to call the super methods as well.
    public void registerRenderers() {
        // 注册物品渲染器
        MinecraftForgeClient
            .registerItemRenderer(OTHItemList.Tallhat.getItem(), new ItemHelmTallHatRender(objHelmetRaw));
    }
}
