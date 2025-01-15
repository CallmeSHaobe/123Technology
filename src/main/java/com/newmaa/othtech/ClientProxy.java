package com.newmaa.othtech;



import com.newmaa.othtech.common.blocks.modelAyanami;
import com.newmaa.othtech.common.blocks.tileEntityModelAyanami;
import com.newmaa.othtech.common.item.itemModelAyanamiRender;
import com.newmaa.othtech.common.render.renderAyanami;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.BlockContainer;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        new itemModelAyanamiRender();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);

    }


    // Override CommonProxy methods here, if you want a different behaviour on the client (e.g. registering renders).
    // Don't forget to call the super methods as well.

}
