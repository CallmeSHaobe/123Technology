package com.newmaa.othtech.mixin;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.newmaa.othtech.common.item.itemWeapons.ItemLonginus;

@Mixin(ModelBiped.class)
public abstract class LonginusThrowPoseMixin {

    @Shadow
    public ModelRenderer bipedRightArm;
    @Shadow
    public ModelRenderer bipedLeftArm;

    @Inject(method = "setRotationAngles", at = @At("TAIL"))
    private void oth$windUpLonginus(float limbSwing, float limbSwingAmount, float age, float headYaw, float headPitch,
        float scale, Entity entity, CallbackInfo ci) {
        if (!(entity instanceof EntityPlayer player) || player.getItemInUseCount() <= 0) return;
        ItemStack held = player.getHeldItem();
        if (held == null || !(held.getItem() instanceof ItemLonginus)) return;

        float charge = MathHelper
            .clamp_float(player.getItemInUseDuration() / (float) ItemLonginus.CHARGE_TICKS, 0F, 1F);
        float radians = (float) Math.PI / 180F;
        bipedRightArm.rotateAngleX = -2.25F - 0.55F * charge + headPitch * radians;
        bipedRightArm.rotateAngleY = headYaw * radians - 0.25F;
        bipedRightArm.rotateAngleZ = 0.18F;
        bipedLeftArm.rotateAngleX = -0.55F + headPitch * radians * 0.5F;
        bipedLeftArm.rotateAngleY = 0.12F;
        bipedLeftArm.rotateAngleZ = -0.15F;
    }
}
