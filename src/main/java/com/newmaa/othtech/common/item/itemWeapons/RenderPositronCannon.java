package com.newmaa.othtech.common.item.itemWeapons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

public class RenderPositronCannon implements IItemRenderer {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
        "123technology",
        "models/Positron_e+_artiliary.png");
    private static final IModelCustom MODEL = AdvancedModelLoader
        .loadModel(new ResourceLocation("123technology", "models/Positron_e+_artiliary.obj"));

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return type != ItemRenderType.EQUIPPED || helper != ItemRendererHelper.BLOCK_3D;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();
        try {
            GL11.glColor4f(1F, 1F, 1F, 1F);
            float scale;
            switch (type) {
                case EQUIPPED_FIRST_PERSON:
                    scale = 15F;
                    break;
                case EQUIPPED:
                    scale = 16.875F;
                    break;
                case ENTITY:
                    scale = 7.5F;
                    break;
                default:
                    scale = 5.25F;
                    break;
            }
            // Position the model's center at the render origin before scaling.
            if (type == ItemRenderType.EQUIPPED) {
                // Cancel the flat-item transform, then compensate for the fixed raised right arm.
                GL11.glRotatef(-20F, 0F, 0F, 1F);
                GL11.glRotatef(90F, 1F, 0F, 0F);
                GL11.glRotatef(-60F, 0F, 0F, 1F);
                GL11.glRotatef(66F, 1F, 0F, 0F);
                GL11.glTranslatef(-0.35F, 0.6F, 0.15F);
            }
            boolean held = type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON;
            if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) GL11.glTranslatef(0F, 0.32F, 0F);
            GL11.glScalef(held ? -scale : scale, scale, scale);
            if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) GL11.glRotatef(12F, 1F, 0F, 0F);
            GL11.glRotatef(
                type == ItemRenderType.EQUIPPED ? 90F : type == ItemRenderType.EQUIPPED_FIRST_PERSON ? 315F : 180F,
                0F,
                1F,
                0F);
            GL11.glTranslatef(0.72F, 0.7F, 0F);
            Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
            boolean culling = GL11.glIsEnabled(GL11.GL_CULL_FACE);
            if (held && culling) GL11.glDisable(GL11.GL_CULL_FACE);
            try {
                MODEL.renderAll();
            } finally {
                if (held && culling) GL11.glEnable(GL11.GL_CULL_FACE);
            }
        } finally {
            GL11.glPopMatrix();
        }
        if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            renderFirstPersonHands();
        }
    }

    private void renderFirstPersonHands() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.thePlayer.isInvisible()) return;
        if (!(RenderManager.instance.getEntityRenderObject(mc.thePlayer) instanceof RenderPlayer renderer)) return;
        mc.renderEngine.bindTexture(mc.thePlayer.getLocationSkin());
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glPushMatrix();
        try {
            GL11.glTranslatef(0F, 0.32F, 0F);
            GL11.glPushMatrix();
            try {
                GL11.glTranslatef(-0.34F, -0.32F, 0.08F);
                GL11.glRotatef(-35F, 0F, 1F, 0F);
                renderer.renderFirstPersonArm(mc.thePlayer);
            } finally {
                GL11.glPopMatrix();
            }
            GL11.glPushMatrix();
            try {
                GL11.glTranslatef(0.2F, -0.38F, -0.8F);
                GL11.glRotatef(-25F, 0F, 1F, 0F);
                renderer.modelBipedMain.bipedLeftArm.rotateAngleX = -1.3F;
                renderer.modelBipedMain.bipedLeftArm.rotateAngleY = 0.2F;
                renderer.modelBipedMain.bipedLeftArm.rotateAngleZ = 0F;
                renderer.modelBipedMain.bipedLeftArm.render(0.0625F);
            } finally {
                GL11.glPopMatrix();
            }
        } finally {
            GL11.glPopMatrix();
        }
    }
}
