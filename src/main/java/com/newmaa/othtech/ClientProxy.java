package com.newmaa.othtech;

import net.minecraftforge.common.MinecraftForge;

import com.newmaa.othtech.common.dimensions.sky.SkyProviderHandler;
import com.newmaa.othtech.common.entity.EntityFakePlayer;
import com.newmaa.othtech.common.entity.RenderPlayer;
import com.newmaa.othtech.common.item.itemBlock.ItemModelAsukaRender;
import com.newmaa.othtech.common.item.itemBlock.ItemModelAyanamiRender;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

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
        // 在这里添加你需要的预初始化逻辑
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        // 注册 itemModelAyanamiRender 渲染器
        new ItemModelAyanamiRender();

        // 注册 itemModelAsukaRender 渲染器
        new ItemModelAsukaRender(); // 你可以在这里创建 itemModelAsukaRender 对象并进行初始化
        RenderingRegistry.registerEntityRenderingHandler(EntityFakePlayer.class, new RenderPlayer());
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        // 在这里添加你需要的后初始化逻辑
    }

    // Override CommonProxy methods here, if you want a different behaviour on the client (e.g. registering renders).
    // Don't forget to call the super methods as well.

}
