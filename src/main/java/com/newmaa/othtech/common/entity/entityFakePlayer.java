package com.newmaa.othtech.common.entity;

import net.minecraft.entity.EntityCreature;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.newmaa.othtech.common.item.ItemLoader;

import gtPlusPlus.core.item.ModItems;

public class entityFakePlayer extends EntityCreature {

    private static final int DATAWATCHER_ID_SITTING = 20;
    private int sitCooldown = 0;

    public entityFakePlayer(World world) {
        super(world);
        this.setSize(0.6F, 1.8F);
        this.dataWatcher.addObject(DATAWATCHER_ID_SITTING, (byte) 0);
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        {
            this.dropItem(ItemLoader.itemBrain, 1);
        }
        int extraDrops = this.rand.nextInt(8 + lootingModifier);
        for (int i = 0; i < extraDrops; ++i) {
            this.dropItem(ModItems.itemMetaFood, 36);
        }
    }

    public boolean isSitting() {
        return this.dataWatcher.getWatchableObjectByte(DATAWATCHER_ID_SITTING) == 1;
    }

    public void setSitting(boolean sitting) {
        if (!this.worldObj.isRemote) { // 仅在服务端操作
            // 更新字节值（1 = 坐下）
            this.dataWatcher.updateObject(DATAWATCHER_ID_SITTING, (byte) (sitting ? 1 : 0));

            // 坐下时禁止移动
            if (sitting) {
                this.getNavigator()
                    .clearPathEntity();
                this.setMoveForward(0.0F);
            }
        }
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        // 每100 ticks（5秒）随机切换状态
        if (!this.worldObj.isRemote && this.sitCooldown-- <= 0) {
            if (this.rand.nextInt(5) == 0) { // 20%概率触发
                sendMessageToAll("我在哪里?");
                this.setSitting(!this.isSitting());
                this.sitCooldown = 100 + this.rand.nextInt(100); // 5-10秒冷却
            }
        }
    }

    // 向所有玩家发送消息
    private void sendMessageToAll(String msg) {
        MinecraftServer.getServer()
            .getConfigurationManager()
            .sendChatMsg(new ChatComponentText(EnumChatFormatting.YELLOW + "复制体: " + "我是谁--?" + msg));
    }

}
