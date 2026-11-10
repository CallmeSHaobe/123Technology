package com.newmaa.othtech.common.entity;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.world.World;

import com.newmaa.othtech.common.item.ItemLoader;

import micdoodle8.mods.galacticraft.api.entity.IEntityBreathable;

public abstract class EntityDogBase extends EntityWolf implements IEntityBreathable {

    public EntityDogBase(World worldIn) {
        super(worldIn);
        // 优先度 5：比原版狼的「主人被打 / 自己被打 / 抓羊」都低一档，但永远生效
        this.targetTasks.addTask(5, new EntityAINearestAttackableTarget(this, EntityDogBase.class, 0, true));
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        if (this.rand.nextInt(10) == 0) {
            this.dropItem(ItemLoader.itemLeekBox, 4);
        }
        super.dropFewItems(wasRecentlyHit, lootingModifier);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth)
            .setBaseValue(50.0D);
        // 与原版狼一致；不要写成 2.0，那实际是原版移动速度的 6 倍多
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed)
            .setBaseValue(0.3F);
    }

    @Override
    public boolean canBreath() {
        return true;
    }
}
