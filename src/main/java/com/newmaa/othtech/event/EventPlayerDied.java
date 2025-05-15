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
        // 检查死亡的实体是否是玩家
        if (event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entity;

            // 防止在客户端执行
            if (player.worldObj.isRemote) return;

            // 创建要掉落的特殊物品（这里以钻石为例）
            ItemStack specialDrop = new ItemStack(OTHItemList.Brains.getItem(), 1); // 数量1

            // 生成物品实体
            EntityItem itemEntity = new EntityItem(
                player.worldObj,
                player.posX, // 玩家死亡X坐标
                player.posY, // 玩家死亡Y坐标
                player.posZ, // 玩家死亡Z坐标
                specialDrop);

            // 设置物品实体的随机运动（模仿自然掉落）
            itemEntity.motionY = 0.2;
            itemEntity.motionX = (Math.random() - 0.5) * 0.2;
            itemEntity.motionZ = (Math.random() - 0.5) * 0.2;

            // 将物品实体加入世界
            player.worldObj.spawnEntityInWorld(itemEntity);
        }
    }
}
