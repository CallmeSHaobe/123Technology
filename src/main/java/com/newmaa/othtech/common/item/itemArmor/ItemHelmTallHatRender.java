package com.newmaa.othtech.common.item.itemArmor;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

public class ItemHelmTallHatRender implements IItemRenderer {

    public static final ResourceLocation Main = new ResourceLocation(
        "123technology",
        "models/tallHatLaodengTexture.png");
    private IModelCustom model;

    public ItemHelmTallHatRender(IModelCustom model) {
        this.model = model;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    public static IModelCustom ModelHelmTallHatLaodeng = AdvancedModelLoader
        .loadModel(new ResourceLocation("123technology", "models/TallHatLaodeng.obj"));

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();
        GL11.glScalef(0.8f, 0.8f, 0.8f);
        GL11.glRotated(0, 1, 0, 0);
        GL11.glTranslatef(-0.2f, 0f, -0.4f);
        GL11.glRotated(0, 1, 0, 0);
        Minecraft.getMinecraft().renderEngine.bindTexture(Main);
        ModelHelmTallHatLaodeng.renderAll();
        GL11.glPopMatrix();
    }
}
