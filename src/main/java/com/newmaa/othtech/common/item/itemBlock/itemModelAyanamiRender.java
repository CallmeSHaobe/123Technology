package com.newmaa.othtech.common.item.itemBlock;

import static com.newmaa.othtech.common.ItemAndBlockHandler.MODEL_AYANAMI;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.newmaa.othtech.common.blocks.tileEntityModelAyanami;
import com.newmaa.othtech.common.render.renderAyanami;

import cpw.mods.fml.client.registry.ClientRegistry;

public class itemModelAyanamiRender implements IItemRenderer {

    public static final ResourceLocation Main = new ResourceLocation("123technology", "models/ayanamiTexture.png");

    public itemModelAyanamiRender() {
        ClientRegistry.bindTileEntitySpecialRenderer(tileEntityModelAyanami.class, new renderAyanami());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(MODEL_AYANAMI), this);

    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    public static IModelCustom ModelAyanami = AdvancedModelLoader
        .loadModel(new ResourceLocation("123technology", "models/Ayanami.obj"));

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();
        GL11.glScalef(1.1f, 1.1f, 1.1f);
        GL11.glRotated(180, 0, 1, 0);
        GL11.glTranslatef(-0.2f, 0.3f, -0.4f);
        GL11.glRotated(-90, 0, 1, 0);
        Minecraft.getMinecraft().renderEngine.bindTexture(Main);
        ModelAyanami.renderAll();
        GL11.glPopMatrix();
    }

}
