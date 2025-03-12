package com.newmaa.othtech.common.entity;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class satFakePlayer extends ModelBiped {

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
        float headPitch, float scaleFactor, Entity entity) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity);

        if (entity instanceof entityFakePlayer && ((entityFakePlayer) entity).isSitting()) {
            // 腿部弯曲（模拟坐下）
            this.bipedLeftLeg.rotateAngleX = -1.5F;
            this.bipedRightLeg.rotateAngleX = -1.5F;
            this.bipedLeftLeg.rotateAngleY = -0.1F;
            this.bipedRightLeg.rotateAngleY = 0.1F;

            // 手臂自然下垂
            this.bipedLeftArm.rotateAngleX = -0.5F;
            this.bipedRightArm.rotateAngleX = -0.5F;

            // 头部保持水平
            this.bipedHead.rotateAngleX = 0.0F;
            this.bipedHead.rotateAngleY = 0.0F;
        }
    }
}
