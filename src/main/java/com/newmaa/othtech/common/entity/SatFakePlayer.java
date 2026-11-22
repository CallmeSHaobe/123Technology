package com.newmaa.othtech.common.entity;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SatFakePlayer extends ModelBiped {

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
        float headPitch, float scaleFactor, Entity entity) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity);

        if (entity instanceof EntityFakePlayer && ((EntityFakePlayer) entity).isSitting()) {
            this.bipedLeftLeg.rotateAngleX = -1.5F;
            this.bipedRightLeg.rotateAngleX = -1.5F;
            this.bipedLeftLeg.rotateAngleY = -0.1F;
            this.bipedRightLeg.rotateAngleY = 0.1F;

            this.bipedLeftArm.rotateAngleX = -0.5F;
            this.bipedRightArm.rotateAngleX = -0.5F;

            this.bipedHead.rotateAngleX = this.bipedHeadwear.rotateAngleX = 0.0F;
            this.bipedHead.rotateAngleY = this.bipedHeadwear.rotateAngleY = 0.0F;
        }
    }
}
