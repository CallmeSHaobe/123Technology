package com.newmaa.othtech.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class ExplosionHandlerNuke {

    public NukeExplosion createExplosion(final World world, final Entity entityObj, final double x, final double y,
        final double z, final float size, final boolean makesFlames, final boolean makesSmoke) {
        return this.newExplosion(world, entityObj, x, y, z, size, makesFlames, makesSmoke);
    }

    public NukeExplosion newExplosion(final World world, final Entity entityObj, final double x, final double y,
        final double z, final float size, final boolean makesFlames, final boolean makesSmoke) {
        final NukeExplosion explosion = new NukeExplosion(world, entityObj, x, y, z, size);
        explosion.isFlaming = makesFlames;
        explosion.isSmoking = makesSmoke;
        if (net.minecraftforge.event.ForgeEventFactory.onExplosionStart(world, explosion)) {
            return explosion;
        }
        explosion.doExplosionA();
        explosion.doExplosionB(true);
        return explosion;
    }
}
