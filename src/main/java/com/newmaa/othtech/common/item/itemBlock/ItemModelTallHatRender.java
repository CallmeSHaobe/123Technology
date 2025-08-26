package com.newmaa.othtech.common.item.itemBlock;

import static com.newmaa.othtech.common.ItemAndBlockHandler.TALL_HAT;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.newmaa.othtech.common.blocks.OTEModelTallHat;
import com.newmaa.othtech.common.render.RenderTallHat;

import cpw.mods.fml.client.registry.ClientRegistry;

public class ItemModelTallHatRender implements IItemRenderer {

    public static final ResourceLocation Main = new ResourceLocation(
        "123technology",
        "models/tallHatLaodengTexture.png");

    public ItemModelTallHatRender() {
        ClientRegistry.bindTileEntitySpecialRenderer(OTEModelTallHat.class, new RenderTallHat());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TALL_HAT), this);

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
