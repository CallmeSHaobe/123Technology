package com.newmaa.othtech.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.TextureStitchEvent;

import com.newmaa.othtech.common.materials.AbstractSanctumContent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class AbstractSanctumClientEvents {

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Post event) {
        if (event.map != Minecraft.getMinecraft()
            .getTextureMapBlocks()) return;
        AbstractSanctumContent.bindClientFluidIcons(event.map);
    }
}
