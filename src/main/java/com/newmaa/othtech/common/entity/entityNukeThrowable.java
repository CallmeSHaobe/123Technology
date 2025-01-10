package com.newmaa.othtech.common.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class entityNukeThrowable extends EntityThrowable {

    public entityNukeThrowable(World worldIn) {
        super(worldIn);
    }

    public entityNukeThrowable(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
    }

    public entityNukeThrowable(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    protected void onImpact(MovingObjectPosition movingObjectPosition) {
        if (movingObjectPosition.typeOfHit != null && movingObjectPosition.entityHit != null) {
            movingObjectPosition.entityHit
                .attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 114514.0F);
        }
        if (!this.worldObj.isRemote) {
            explode();
            this.setDead();
        }
    }

    private void explode() {
        final float f = 100.0F;

        ExplosionHandlerNuke explode = new ExplosionHandlerNuke();
        explode.createExplosion(this.worldObj, this, this.posX, this.posY, this.posZ, f, false, true);
    }
}
