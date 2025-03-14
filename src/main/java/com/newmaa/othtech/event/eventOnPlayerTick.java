package com.newmaa.othtech.event;

import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

import com.newmaa.othtech.common.item.ItemLoader;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class eventOnPlayerTick {

    private final Map<EntityPlayer, Integer> wearTimeMap = new WeakHashMap<>();

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        EntityPlayer player = event.player;
        if (player.worldObj.isRemote) return;

        // 获取头盔物品
        ItemStack helmetStack = player.getCurrentArmor(0); // 3对应头盔槽位

        if (helmetStack != null && helmetStack.getItem() == ItemLoader.itemAmulet) {

            int wearTime = wearTimeMap.getOrDefault(player, 0) + 1;
            wearTimeMap.put(player, wearTime);

            if (wearTime >= 100) { // 5秒计时
                // 爆炸特效
                player.worldObj.createExplosion(player, player.posX, player.posY + 1, player.posZ, 3.0F, false);

                // 强制击杀
                player.attackEntityFrom(new DamageSource("amulet_explosion").setDamageBypassesArmor(), Float.MAX_VALUE);
                player.setDead();

                // 服务端处理物品消耗
                if (!player.worldObj.isRemote) {
                    helmetStack.damageItem(1, player);

                    if (helmetStack.getItemDamage() >= helmetStack.getMaxDamage()) {
                        player.setCurrentItemOrArmor(4, null); // 4对应头盔槽位
                    }
                }

                wearTimeMap.put(player, 0);
            }
        } else {
            wearTimeMap.remove(player);
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        wearTimeMap.remove(event.player);
    }
}
