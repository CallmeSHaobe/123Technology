package com.newmaa.othtech.common.entity;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.world.World;

import com.newmaa.othtech.common.dimensions.biome.BiomeGenBaseAntimonia;
import com.newmaa.othtech.common.item.ItemLoader;

import cpw.mods.fml.common.registry.EntityRegistry;
import galaxyspace.core.world.GSBiomeGenBase;
import micdoodle8.mods.galacticraft.api.entity.IEntityBreathable;

public class EntityDogYellow extends EntityWolf implements IEntityBreathable {

    public EntityDogYellow(World worldIn) {
        super(worldIn);
        EntityRegistry.addSpawn(EntityDogYellow.class, 6, 1, 5, EnumCreatureType.creature, GSBiomeGenBase.SpaceGS);
        EntityRegistry
            .addSpawn(EntityDogYellow.class, 8, 1, 8, EnumCreatureType.creature, BiomeGenBaseAntimonia.antimonia);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
    }

    @Override
    protected void dropFewItems(boolean arg1, int arg2) {
        if (this.rand.nextInt(10) == 0) {
            this.dropItem(ItemLoader.itemLeekBox, 4);
        }
        super.dropFewItems(arg1, arg2);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth)
            .setBaseValue(50);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed)
            .setBaseValue(2F);
    }

    @Override
    public boolean canBreath() {
        return true;
    }
}
