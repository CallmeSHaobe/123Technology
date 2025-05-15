package com.newmaa.othtech.common.item.itemBlock;

import static com.newmaa.othtech.common.ItemAndBlockHandler.MODEL_ASUKA;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.newmaa.othtech.common.blocks.OTEModelAsuka;
import com.newmaa.othtech.common.render.RenderAsuka;

import cpw.mods.fml.client.registry.ClientRegistry;

public class ItemModelAsukaRender implements IItemRenderer {

    public static final ResourceLocation Main = new ResourceLocation("123technology", "models/asukaTexture.png");

    public ItemModelAsukaRender() {
        ClientRegistry.bindTileEntitySpecialRenderer(OTEModelAsuka.class, new RenderAsuka());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(MODEL_ASUKA), this);

    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    public static IModelCustom ModelAsuka = AdvancedModelLoader
        .loadModel(new ResourceLocation("123technology", "models/Asuka.obj"));

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();
        GL11.glScalef(0.8f, 0.8f, 0.8f);
        GL11.glRotated(0, 1, 0, 0);
        GL11.glTranslatef(-0.2f, 0f, -0.4f);
        GL11.glRotated(0, 1, 0, 0);
        Minecraft.getMinecraft().renderEngine.bindTexture(Main);
        ModelAsuka.renderAll();
        GL11.glPopMatrix();
    }

}
