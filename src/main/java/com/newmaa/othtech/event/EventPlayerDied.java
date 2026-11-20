package com.newmaa.othtech.event;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import com.newmaa.othtech.common.OTHItemList;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EventPlayerDied {

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entity;

            if (player.worldObj.isRemote) return;

            ItemStack specialDrop = new ItemStack(OTHItemList.Brains.getItem(), 1);

            EntityItem itemEntity = new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, specialDrop);

            // 设置物品实体的随机运动（模仿自然掉落）
            itemEntity.motionY = 0.2;
            itemEntity.motionX = (Math.random() - 0.5) * 0.2;
            itemEntity.motionZ = (Math.random() - 0.5) * 0.2;

            player.worldObj.spawnEntityInWorld(itemEntity);
        }
    }
}
