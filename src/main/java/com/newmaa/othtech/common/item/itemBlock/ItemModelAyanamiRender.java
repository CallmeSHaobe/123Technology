package com.newmaa.othtech.common.item.itemBlock;

import static com.newmaa.othtech.common.ItemAndBlockHandler.MODEL_AYANAMI;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import com.newmaa.othtech.common.blocks.OTEModelAyanami;
import com.newmaa.othtech.common.render.AyanamiModelRenderer;
import com.newmaa.othtech.common.render.RenderAyanami;

import cpw.mods.fml.client.registry.ClientRegistry;

public class ItemModelAyanamiRender implements IItemRenderer {

    public ItemModelAyanamiRender() {
        ClientRegistry.bindTileEntitySpecialRenderer(OTEModelAyanami.class, new RenderAyanami());
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

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();
        try {
            GL11.glScalef(1.1f, 1.1f, 1.1f);
            GL11.glRotated(180, 0, 1, 0);
            GL11.glTranslatef(-0.2f, 0.3f, -0.4f);
            GL11.glRotated(45, 0, 1, 0);
            Minecraft.getMinecraft().renderEngine.bindTexture(AyanamiModelRenderer.TEXTURE);
            AyanamiModelRenderer.renderStanding();
        } finally {
            GL11.glPopMatrix();
        }
    }
}
