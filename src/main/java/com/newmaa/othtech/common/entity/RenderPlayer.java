package com.newmaa.othtech.common.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import twilightforest.TwilightForestMod;
import twilightforest.client.renderer.entity.RenderTFGiantSkinportIntegration;

public class RenderPlayer extends RenderBiped {

    private static final ResourceLocation STEVE_SKIN = new ResourceLocation("textures/entity/steve.png");

    public RenderPlayer() {
        super(new SatFakePlayer(), 0.5F);
    }

    protected ResourceLocation getEntityTexture(Entity par1Entity) {
        ResourceLocation skin;
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        if (player.getLocationSkin() != null) {
            skin = Minecraft.getMinecraft().thePlayer.getLocationSkin();
        } else {
            skin = STEVE_SKIN;
        }
        if (TwilightForestMod.isSkinportLoaded) {
            skin = RenderTFGiantSkinportIntegration.getSkin(player, skin, STEVE_SKIN);
        }
        return skin;
    }

    protected void preRenderCallback(EntityLivingBase par1EntityLivingBase, float par2) {
        if (TwilightForestMod.isSkinportLoaded) {
            if (RenderTFGiantSkinportIntegration.isSlim(Minecraft.getMinecraft().thePlayer)) {
                this.modelBipedMain.bipedRightArm = new ModelRenderer(this.modelBipedMain, 40, 16);
                this.modelBipedMain.bipedRightArm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, 0);
                this.modelBipedMain.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);

                this.modelBipedMain.bipedLeftArm = new ModelRenderer(this.modelBipedMain, 40, 16);
                this.modelBipedMain.bipedLeftArm.mirror = true;
                this.modelBipedMain.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, 0);
                this.modelBipedMain.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
            }
        }

        float scale = 1.0F;
        GL11.glScalef(scale, scale, scale);
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        EntityFakePlayer customPlayer = (EntityFakePlayer) entity;

        // 调整坐下时的渲染位置
        if (customPlayer.isSitting()) {
            y -= 0.6D; // 降低高度
        }

        // 保存当前 OpenGL 状态
        GL11.glPushMatrix();

        // 调整旋转（坐下时固定朝向）
        if (customPlayer.isSitting()) {
            GL11.glTranslatef((float) x, (float) y, (float) z);
            GL11.glRotatef(180.0F - yaw, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(-(float) x, -(float) y, -(float) z);
        }

        // 调用父类渲染逻辑
        super.doRender(entity, x, y, z, yaw, partialTicks);

        // 恢复 OpenGL 状态
        GL11.glPopMatrix();
    }
}
