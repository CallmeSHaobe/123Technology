package com.newmaa.othtech.common.dimensions.sky;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent;

import com.newmaa.othtech.common.dimensions.ross123b.WorldProviderRoss123b;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RossFogHandler {

    @SubscribeEvent
    public void onFogColors(EntityViewRenderEvent.FogColors event) {
        World world = event.entity == null ? null : event.entity.worldObj;
        if (world == null || !(world.provider instanceof WorldProviderRoss123b)) {
            return;
        }
        Block block = event.block;
        if (block == null || block.getMaterial() != Material.water) {
            return;
        }
        event.red = 0.34F;
        event.green = 0.02F;
        event.blue = 0.02F;
    }
}
