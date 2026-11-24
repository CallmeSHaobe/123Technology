package com.newmaa.othtech.common.render;

import static net.minecraftforge.client.IItemRenderer.ItemRenderType.ENTITY;
import static net.minecraftforge.client.IItemRenderer.ItemRenderType.INVENTORY;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.newmaa.othtech.common.blocks.oterender.OTERocketRender;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RocketRenderOTH extends TileEntitySpecialRenderer {

    private static final float MODEL_SCALE = 3F;

    private final RenderBlocks itemBlocks = new RenderBlocks();
    private final Random itemRandom = new Random();
    private EntityItem renderItem;

    public RocketRenderOTH() {
        ClientRegistry.bindTileEntitySpecialRenderer(OTERocketRender.class, this);
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
        if (!(tile instanceof OTERocketRender display)) return;
        ItemStack stack = display.getDisplayStack();
        if (stack == null || stack.getItem() == null) return;

        TextureManager textures = Minecraft.getMinecraft()
            .getTextureManager();
        float lightX = OpenGlHelper.lastBrightnessX;
        float lightY = OpenGlHelper.lastBrightnessY;
        GL11.glPushAttrib(
            GL11.GL_ENABLE_BIT | GL11.GL_CURRENT_BIT
                | GL11.GL_COLOR_BUFFER_BIT
                | GL11.GL_DEPTH_BUFFER_BIT
                | GL11.GL_TEXTURE_BIT);
        GL11.glPushMatrix();
        try {
            GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
            GL11.glColor4f(1F, 1F, 1F, 1F);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            switch (display.getBlockMetadata()) {
                case 2 -> GL11.glRotatef(180F, 0F, 1F, 0F);
                case 4 -> GL11.glRotatef(90F, 0F, 1F, 0F);
                case 5 -> GL11.glRotatef(-90F, 0F, 1F, 0F);
                default -> {}
            }
            float rotation = ((tile.getWorldObj()
                .getTotalWorldTime() % 300L) + partialTicks) * 1.2F;
            GL11.glRotatef(rotation, 0F, 1F, 0F);
            renderDisplayItem(display, stack, textures);
        } finally {
            GL11.glPopMatrix();
            GL11.glPopAttrib();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightX, lightY);
        }
    }

    private void renderDisplayItem(OTERocketRender display, ItemStack stack, TextureManager textures) {
        if (MinecraftForgeClient.getItemRenderer(stack, ENTITY) != null) {
            // Some Forge renderers expect an EntityItem as context. This proxy never enters the world.
            if (renderItem == null || renderItem.worldObj != display.getWorldObj()) {
                renderItem = new EntityItem(display.getWorldObj());
            }
            renderItem.setPosition(display.xCoord + 0.5, display.yCoord + 0.5, display.zCoord + 0.5);
            renderItem.setEntityItemStack(stack);
            itemRandom.setSeed(187L);
            GL11.glPushMatrix();
            try {
                GL11.glScalef(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
                if (ForgeHooksClient.renderEntityItem(renderItem, stack, 0F, 0F, itemRandom, textures, itemBlocks, 1))
                    return;
            } finally {
                GL11.glPopMatrix();
            }
        }

        Block block = stack.getItem() instanceof ItemBlock ? Block.getBlockFromItem(stack.getItem()) : null;
        if (MinecraftForgeClient.getItemRenderer(stack, INVENTORY) != null) {
            // Map the Forge inventory renderer's 16-pixel canvas to the horizontal display plane.
            GL11.glPushMatrix();
            try {
                GL11.glRotatef(90F, 1F, 0F, 0F);
                float scale = MODEL_SCALE / 16F;
                GL11.glScalef(scale, scale, scale);
                if (ForgeHooksClient.renderInventoryItem(itemBlocks, textures, stack, true, 3F, -8F, -8F)) return;
            } finally {
                GL11.glPopMatrix();
            }
        }
        if (block != null && RenderBlocks.renderItemIn3d(block.getRenderType())) {
            textures.bindTexture(TextureMap.locationBlocksTexture);
            float scale = 0.8F * MODEL_SCALE;
            GL11.glScalef(scale, scale, scale);
            if (block.getRenderBlockPass() > 0) {
                GL11.glEnable(GL11.GL_BLEND);
                OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
            }
            itemBlocks.renderBlockAsItem(block, stack.getItemDamage(), 1F);
            return;
        }
        renderFlatIcon(stack, textures);
    }

    private static void renderFlatIcon(ItemStack stack, TextureManager textures) {
        GL11.glRotatef(90F, 1F, 0F, 0F);
        GL11.glScalef(4F, 4F, 4F);
        Item item = stack.getItem();
        ResourceLocation atlas = textures.getResourceLocation(stack.getItemSpriteNumber());
        textures.bindTexture(atlas);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        Tessellator tessellator = Tessellator.instance;
        int passes = item.requiresMultipleRenderPasses() ? item.getRenderPasses(stack.getItemDamage()) : 1;
        for (int pass = 0; pass < passes; pass++) {
            IIcon icon = item.getIcon(stack, pass);
            if (icon == null && pass == 0 && textures.getTexture(atlas) instanceof TextureMap map) {
                icon = map.getAtlasSprite("missingno");
            }
            if (icon == null) continue;
            double y = pass * 0.001;
            tessellator.startDrawingQuads();
            tessellator.setColorOpaque_I(item.getColorFromItemStack(stack, pass));
            tessellator.addVertexWithUV(-0.5, y, 0.5, icon.getMinU(), icon.getMaxV());
            tessellator.addVertexWithUV(0.5, y, 0.5, icon.getMaxU(), icon.getMaxV());
            tessellator.addVertexWithUV(0.5, y, -0.5, icon.getMaxU(), icon.getMinV());
            tessellator.addVertexWithUV(-0.5, y, -0.5, icon.getMinU(), icon.getMinV());
            tessellator.draw();
        }
    }
}
