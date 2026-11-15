package com.newmaa.othtech.mixin;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.newmaa.othtech.common.item.itemWeapons.ItemPositronCannon;

@Mixin(ModelBiped.class)
public class PositronCannonArmsMixin {

    @Shadow
    public ModelRenderer bipedRightArm;
    @Shadow
    public ModelRenderer bipedLeftArm;

    @Inject(method = "setRotationAngles", at = @At("TAIL"))
    private void oth$holdPositronCannon(float limbSwing, float limbSwingAmount, float age, float headYaw,
        float headPitch, float scale, Entity entity, CallbackInfo ci) {
        if (!(entity instanceof EntityPlayer)) return;
        ItemStack held = ((EntityPlayer) entity).getHeldItem();
        if (held == null || !(held.getItem() instanceof ItemPositronCannon)) return;

        float pitch = headPitch * ((float) Math.PI / 180F);
        bipedRightArm.rotateAngleX = -1.15F + pitch;
        bipedRightArm.rotateAngleY = -0.3F;
        bipedRightArm.rotateAngleZ = 0F;
        bipedLeftArm.rotateAngleX = -1.15F + pitch;
        bipedLeftArm.rotateAngleY = 0.35F;
        bipedLeftArm.rotateAngleZ = 0F;
    }
}
