package com.newmaa.othtech.common.entity;

import static net.minecraft.util.StatCollector.translateToLocal;
import static net.minecraft.util.StatCollector.translateToLocalFormatted;

import java.util.ArrayList;

import net.minecraft.entity.EntityLiving;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;

import com.kuba6000.mobsinfo.api.IMobInfoProvider;
import com.kuba6000.mobsinfo.api.MobDrop;
import com.newmaa.othtech.Config;
import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.item.ItemLoader;

import gtPlusPlus.core.item.food.BaseItemMetaFood;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;

public class EntityFakePlayer extends EntityLiving implements IMobInfoProvider {

    private static final int DATAWATCHER_ID_SITTING = 20;
    private int sitCooldown = 0;
    public static BaseItemMetaFood itemMetaFood;

    public EntityFakePlayer(World world) {
        super(world);
        this.setSize(0.6F, 1.8F);
        this.dataWatcher.addObject(DATAWATCHER_ID_SITTING, (byte) 0);
    }

    public static Item metaFood() {
        if (itemMetaFood == null) {
            try {
                ItemStack stack = GregtechItemList.RawHumanMeat.get(1);
                if (stack != null && stack.getItem() instanceof BaseItemMetaFood) {
                    itemMetaFood = (BaseItemMetaFood) stack.getItem();
                }
            } catch (Throwable ignored) {
                // GT++ 没注册 meta food（或枚举尚未 set）时保持 null，掉落逻辑会自己跳过
            }
        }
        return itemMetaFood;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        this.dropItem(ItemLoader.itemBrain, 1);
        // 防御：物品解析失败时绝不能往世界里掉 ItemStack(null)，否则生成 ItemEntity 时会 NPE
        Item food = metaFood();
        if (food == null) return;
        int extraDrops = this.rand.nextInt(8 + lootingModifier);
        for (int i = 0; i < extraDrops; ++i) {
            this.dropItem(food, 36);
        }
    }

    public boolean isSitting() {
        return this.dataWatcher.getWatchableObjectByte(DATAWATCHER_ID_SITTING) == 1;
    }

    public void setSitting(boolean sitting) {
        if (!this.worldObj.isRemote) {
            this.dataWatcher.updateObject(DATAWATCHER_ID_SITTING, (byte) (sitting ? 1 : 0));
            if (sitting) {
                this.getNavigator()
                    .clearPathEntity();
                this.setAIMoveSpeed(0.0F);
            }
        }
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!this.worldObj.isRemote && this.sitCooldown-- <= 0) {
            if (this.rand.nextInt(5) == 0) {
                if (Config.enableFPChating) {
                    sendMessageToAll();
                }
                this.setSitting(!this.isSitting());
                this.sitCooldown = 100 + this.rand.nextInt(100); // 5-10秒冷却
            }
        }
    }

    private void sendMessageToAll() {
        MinecraftServer server = MinecraftServer.getServer();
        if (server == null) {
            return;
        }
        String message = translateToLocalFormatted("oth.clone.speak", translateToLocal("oth.clone.whereami"));
        server.getConfigurationManager()
            .sendChatMsg(new ChatComponentText(EnumChatFormatting.YELLOW + message));
    }

    @Override
    public void provideDropsInformation(@NotNull ArrayList<MobDrop> drops) {
        var c1 = MobDrop.create(OTHItemList.Brains.get(1))
            .withType(MobDrop.DropType.Normal)
            .withChance(1.0d);
        c1.clampChance();
        drops.add(c1);

        Item food = metaFood();
        if (food == null) return;

        var c2 = MobDrop.create(new ItemStack(food, 36, 0))
            .withType(MobDrop.DropType.Normal)
            .withChance(0.8d);
        c2.clampChance();
        drops.add(c2);
    }
}
