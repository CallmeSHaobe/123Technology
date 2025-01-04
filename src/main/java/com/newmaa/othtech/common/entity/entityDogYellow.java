package com.newmaa.othtech.common.entity;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import com.newmaa.othtech.common.item.ItemLoader;

import cpw.mods.fml.common.registry.EntityRegistry;

public class entityDogYellow extends EntityWolf {

    public entityDogYellow(World worldIn) {
        super(worldIn);
        EntityRegistry.addSpawn(entityDogYellow.class, 6, 1, 5, EnumCreatureType.creature, BiomeGenBase.savanna);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
    }

    @Override
    protected void dropFewItems(boolean arg1, int arg2) {
        if (this.rand.nextInt(10) == 0) {
            this.dropItem(ItemLoader.itemLeekBox, 64);
        }
        super.dropFewItems(arg1, arg2);
    }
}
