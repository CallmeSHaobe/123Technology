package com.newmaa.othtech.client;

import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import com.newmaa.othtech.common.item.itemArmor.ItemPoweredElytra;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ganymedes01.etfuturum.api.elytra.IElytraPlayer;
import ganymedes01.etfuturum.items.equipment.ItemArmorElytra;

public final class PoweredElytraSpeedHud {

    private static final int TICKS_PER_SECOND = 20;
    private static final int CROSSHAIR_OFFSET = 12;

    @SubscribeEvent
    public void onRenderCrosshair(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.CROSSHAIRS) return;

        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayer player = minecraft.thePlayer;
        if (!(player instanceof IElytraPlayer elytraPlayer) || !elytraPlayer.etfu$isElytraFlying() || player.onGround) {
            return;
        }

        ItemStack wings = ItemArmorElytra.getElytra(player);
        if (wings == null || !(wings.getItem() instanceof ItemPoweredElytra) || ItemArmorElytra.isBroken(wings)) return;

        double speed = Math
            .sqrt(player.motionX * player.motionX + player.motionY * player.motionY + player.motionZ * player.motionZ)
            * TICKS_PER_SECOND;
        String label = String.format(Locale.ROOT, "%.1f mps", speed);
        FontRenderer font = minecraft.fontRenderer;
        int x = (event.resolution.getScaledWidth() - font.getStringWidth(label)) / 2;
        int y = event.resolution.getScaledHeight() / 2 + CROSSHAIR_OFFSET;
        font.drawStringWithShadow(label, x, y, 0xFFFFFF);
    }
}
