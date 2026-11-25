package com.newmaa.othtech.common.item.itemArmor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.newmaa.othtech.common.render.RenderTallHat;

public class ItemHelmTallHatRender implements IItemRenderer {

    public static final ResourceLocation Main = new ResourceLocation(ItemHelmTallHatLaodeng.HAT_TEXTURE);
    private static final ModelBiped WORN_MODEL = new ModelBiped() {

        @Override
        public void render(Entity entity, float limbSwing, float limbSwingAmount, float age, float yaw, float pitch,
            float scale) {
            setRotationAngles(limbSwing, limbSwingAmount, age, yaw, pitch, scale, entity);
            GL11.glPushMatrix();
            try {
                if (isChild) {
                    GL11.glScalef(0.75F, 0.75F, 0.75F);
                    GL11.glTranslatef(0F, 16F * scale, 0F);
                }
                bipedHead.postRender(scale);
                GL11.glTranslatef(0F, -7.5F * scale, 0F);
                // OBJ Y points up; ModelBiped Y points down. Rotate without reversing face winding.
                GL11.glRotatef(180F, 1F, 0F, 0F);
                float hatScale = 10F * scale;
                GL11.glScalef(hatScale, hatScale, hatScale);
                RenderTallHat.renderModel(ModelHelmTallHatLaodeng);
            } finally {
                GL11.glPopMatrix();
            }
        }
    };

    static ModelBiped getWornModel(EntityLivingBase entity) {
        WORN_MODEL.isSneak = entity.isSneaking();
        return WORN_MODEL;
    }

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
        RenderTallHat.renderModel(ModelHelmTallHatLaodeng);
        GL11.glPopMatrix();
    }
}
