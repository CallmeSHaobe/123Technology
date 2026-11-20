package com.newmaa.othtech.mixin;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.newmaa.othtech.common.item.itemArmor.ItemPoweredElytra;

import ganymedes01.etfuturum.client.renderer.entity.elytra.LayerBetterElytra;
import ganymedes01.etfuturum.items.equipment.ItemArmorElytra;

@Mixin(value = LayerBetterElytra.class, remap = false)
public abstract class PoweredElytraTextureMixin {

    @Unique
    private static final ResourceLocation oth$poweredTexture = new ResourceLocation(
        "123technology",
        "textures/entity/powered_elytra.png");

    @WrapOperation(
        method = "doRenderLayer",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/texture/TextureManager;bindTexture(Lnet/minecraft/util/ResourceLocation;)V",
            remap = true),
        require = 1)
    private static void oth$bindPoweredTexture(TextureManager manager, ResourceLocation texture, Operation<Void> bind,
        EntityLivingBase wearer, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
        float scale) {
        ItemStack wings = ItemArmorElytra.getElytra(wearer);
        bind.call(
            manager,
            wings != null && wings.getItem() instanceof ItemPoweredElytra ? oth$poweredTexture : texture);
    }
}
