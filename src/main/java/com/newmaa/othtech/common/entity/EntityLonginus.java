package com.newmaa.othtech.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class EntityLonginus extends EntityThrowable implements IEntityAdditionalSpawnData {

    private static final int IMPACT_WATCHER = 16;
    private static final int EMBEDDED_FACE_WATCHER = 17;
    private static final float MIN_INWARD_COMPONENT = 0.25F;
    private static final double PICKUP_SURFACE_OFFSET = 0.05D;
    private static final int EMBEDDED_FLAG = 1;
    private static final int HIT_ENTITY_FLAG = 2;
    private static final float DAMAGE = 1000F;

    private ItemStack spear;
    private boolean creativeThrow;
    private int blockX;
    private int blockY;
    private int blockZ;
    private float embeddedYaw;
    private float embeddedPitch;
    private Vec3 entityImpactPosition;

    private double targetX;
    private double targetY;
    private double targetZ;
    private float targetYaw;
    private float targetPitch;
    private int interpolationTicks;

    public EntityLonginus(World world) {
        super(world);
    }

    public EntityLonginus(World world, EntityPlayer player, ItemStack stack) {
        super(world, player);
        spear = stack.copy();
        spear.stackSize = 1;
        creativeThrow = player.capabilities.isCreativeMode;
        Vec3 look = player.getLookVec();
        setThrowableHeading(look.xCoord, look.yCoord, look.zCoord, 2.5F, 1F);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataWatcher.addObject(IMPACT_WATCHER, (byte) 0);
        dataWatcher.addObject(EMBEDDED_FACE_WATCHER, (byte) ForgeDirection.UNKNOWN.ordinal());
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeDouble(motionX);
        buffer.writeDouble(motionY);
        buffer.writeDouble(motionZ);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        motionX = buffer.readDouble();
        motionY = buffer.readDouble();
        motionZ = buffer.readDouble();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int increments) {
        // The spear tip can overlap the block's collision box while embedded.
        targetX = x;
        targetY = y;
        targetZ = z;
        targetYaw = yaw;
        targetPitch = pitch;
        interpolationTicks = Math.max(1, increments);
    }

    private void interpolateServerPosition() {
        if (interpolationTicks <= 0) return;
        double step = 1D / interpolationTicks--;
        setPosition(posX + (targetX - posX) * step, posY + (targetY - posY) * step, posZ + (targetZ - posZ) * step);
        rotationYaw += MathHelper.wrapAngleTo180_float(targetYaw - rotationYaw) * step;
        rotationPitch += MathHelper.wrapAngleTo180_float(targetPitch - rotationPitch) * step;
    }

    @Override
    public void onUpdate() {
        if (worldObj.isRemote) {
            interpolateServerPosition();
            return;
        }
        if (isEmbedded()) {
            if (worldObj.isAirBlock(blockX, blockY, blockZ)) {
                setImpactFlag(EMBEDDED_FLAG, false);
                motionY = -getGravityVelocity();
                velocityChanged = true;
            } else {
                lastTickPosX = posX;
                lastTickPosY = posY;
                lastTickPosZ = posZ;
                return;
            }
        }
        super.onUpdate();
        if (entityImpactPosition != null) {
            // Start the fall on the next tick so it gets a fresh block collision check.
            setPosition(entityImpactPosition.xCoord, entityImpactPosition.yCoord, entityImpactPosition.zCoord);
            entityImpactPosition = null;
        }
        if (isEmbedded()) {
            // EntityThrowable recalculates rotation from zero velocity after onImpact.
            rotationYaw = prevRotationYaw = embeddedYaw;
            rotationPitch = prevRotationPitch = embeddedPitch;
            motionY = 0D;
        }
    }

    @Override
    protected void onImpact(MovingObjectPosition hit) {
        if (hit.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            blockX = hit.blockX;
            blockY = hit.blockY;
            blockZ = hit.blockZ;
            ForgeDirection face = ForgeDirection.getOrientation(hit.sideHit);
            dataWatcher.updateObject(EMBEDDED_FACE_WATCHER, (byte) face.ordinal());
            setImpactFlag(EMBEDDED_FLAG, true);

            Vec3 direction = Vec3.createVectorHelper(motionX, motionY, motionZ)
                .normalize();
            double inward = -(direction.xCoord * face.offsetX + direction.yCoord * face.offsetY
                + direction.zCoord * face.offsetZ);
            double tilt = Math.max(0D, MIN_INWARD_COMPONENT - inward);
            Vec3 axis = direction.addVector(-face.offsetX * tilt, -face.offsetY * tilt, -face.offsetZ * tilt);
            embeddedYaw = (float) Math.toDegrees(Math.atan2(axis.xCoord, axis.zCoord));
            embeddedPitch = (float) Math.toDegrees(Math.atan2(axis.yCoord, Math.hypot(axis.xCoord, axis.zCoord)));

            // Leave the pickup box just outside the struck face.
            setPosition(
                hit.hitVec.xCoord + face.offsetX * PICKUP_SURFACE_OFFSET,
                hit.hitVec.yCoord + face.offsetY * PICKUP_SURFACE_OFFSET,
                hit.hitVec.zCoord + face.offsetZ * PICKUP_SURFACE_OFFSET);
            velocityChanged = true;
            motionX = 0D;
            motionY = 0D;
            motionZ = 0D;
            return;
        }
        if (hasImpactFlag(HIT_ENTITY_FLAG)) {
            // A nearby entity can mask the block hit in EntityThrowable's collision check.
            Vec3 start = Vec3.createVectorHelper(posX, posY, posZ);
            MovingObjectPosition blockHit = worldObj.rayTraceBlocks(start, start.addVector(motionX, motionY, motionZ));
            if (blockHit != null && blockHit.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                onImpact(blockHit);
            }
            return;
        }
        if (hit.entityHit == null) return;

        Entity target = hit.entityHit;
        EntityLivingBase thrower = getThrower();
        if (target instanceof EntityPlayer player && (player.capabilities.disableDamage
            || thrower instanceof EntityPlayer owner && !owner.canAttackPlayer(player))) return;
        // EntityThrowable reports the target but discards the ray intersection for entity hits.
        Vec3 start = Vec3.createVectorHelper(posX, posY, posZ);
        Vec3 end = start.addVector(motionX, motionY, motionZ);
        MovingObjectPosition contact = target.boundingBox.expand(0.3F, 0.3F, 0.3F)
            .calculateIntercept(start, end);
        entityImpactPosition = contact == null ? start : contact.hitVec;

        setImpactFlag(HIT_ENTITY_FLAG, true);
        target.attackEntityFrom(new EntityDamageSourceIndirect("longinus", this, thrower).setProjectile(), DAMAGE);
        // Lose forward momentum after the hit and let gravity carry the spear to the ground.
        motionX *= 0.02D;
        motionY = -0.1D;
        motionZ *= 0.02D;
        velocityChanged = true;
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer player) {
        if (worldObj.isRemote || isDead || !isEmbedded()) return;
        if (creativeThrow ? !player.capabilities.isCreativeMode
            : spear == null || !player.inventory.addItemStackToInventory(spear)) return;
        playSound("random.pop", 0.2F, 1F);
        setDead();
    }

    public boolean hasImpacted() {
        return dataWatcher.getWatchableObjectByte(IMPACT_WATCHER) != 0;
    }

    public ForgeDirection getEmbeddedFace() {
        return isEmbedded() ? ForgeDirection.getOrientation(dataWatcher.getWatchableObjectByte(EMBEDDED_FACE_WATCHER))
            : ForgeDirection.UNKNOWN;
    }

    private boolean isEmbedded() {
        return hasImpactFlag(EMBEDDED_FLAG);
    }

    private boolean hasImpactFlag(int flag) {
        return (dataWatcher.getWatchableObjectByte(IMPACT_WATCHER) & flag) != 0;
    }

    private void setImpactFlag(int flag, boolean enabled) {
        int flags = dataWatcher.getWatchableObjectByte(IMPACT_WATCHER);
        dataWatcher.updateObject(IMPACT_WATCHER, (byte) (enabled ? flags | flag : flags & ~flag));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        if (spear != null) tag.setTag("Spear", spear.writeToNBT(new NBTTagCompound()));
        tag.setBoolean("CreativeThrow", creativeThrow);
        tag.setBoolean("HitEntity", hasImpactFlag(HIT_ENTITY_FLAG));
        tag.setBoolean("Embedded", isEmbedded());
        tag.setByte("EmbeddedFace", (byte) getEmbeddedFace().ordinal());
        tag.setInteger("BlockX", blockX);
        tag.setInteger("BlockY", blockY);
        tag.setInteger("BlockZ", blockZ);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        spear = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("Spear"));
        creativeThrow = tag.getBoolean("CreativeThrow");
        setImpactFlag(HIT_ENTITY_FLAG, tag.getBoolean("HitEntity"));
        setImpactFlag(EMBEDDED_FLAG, tag.getBoolean("Embedded"));
        if (tag.hasKey("EmbeddedFace")) {
            dataWatcher.updateObject(EMBEDDED_FACE_WATCHER, tag.getByte("EmbeddedFace"));
        }
        blockX = tag.getInteger("BlockX");
        blockY = tag.getInteger("BlockY");
        blockZ = tag.getInteger("BlockZ");
    }
}
