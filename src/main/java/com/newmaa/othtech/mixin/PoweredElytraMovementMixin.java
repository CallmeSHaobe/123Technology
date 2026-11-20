package com.newmaa.othtech.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.item.itemArmor.ItemPoweredElytra;

@Mixin(EntityLivingBase.class)
public abstract class PoweredElytraMovementMixin extends Entity {

    @Unique
    private boolean oth$movingWithHeading;

    public PoweredElytraMovementMixin(World world) {
        super(world);
    }

    @WrapMethod(method = "moveEntityWithHeading")
    private void oth$trackMovementScope(float strafe, float forward, Operation<Void> original) {
        boolean previous = oth$movingWithHeading;
        oth$movingWithHeading = true;
        try {
            original.call(strafe, forward);
        } finally {
            oth$movingWithHeading = previous;
        }
    }

    @Override
    public void moveEntity(double x, double y, double z) {
        // ETFR calculates drag first, then passes the resulting velocity to moveEntity.
        if (oth$movingWithHeading && (Entity) this instanceof EntityPlayer player
            && OTHItemList.PoweredElytra.hasBeenSet()
            && ItemPoweredElytra.boost(player)) {
            super.moveEntity(motionX, motionY, motionZ);
            return;
        }
        super.moveEntity(x, y, z);
    }
}
