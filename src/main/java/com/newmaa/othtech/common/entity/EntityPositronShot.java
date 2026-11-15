package com.newmaa.othtech.common.entity;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;

public class EntityPositronShot extends Entity implements IEntityAdditionalSpawnData {

    private static final int LIFETIME = 60;
    private static final double SPEED = 12D;
    private static final float EXPLOSION_RADIUS = 12F;
    private static final double BEAM_RADIUS = 1.5D;
    private static final double BLAST_DAMAGE_RADIUS = 18D;
    private final Set<Integer> hitEntities = new HashSet<>();
    private EntityPlayer shooter;
    private String shooterName = "";
    private float damage = 500F;
    private double originX;
    private double originY;
    private double originZ;

    public EntityPositronShot(World world) {
        super(world);
        setSize(0.6F, 0.6F);
        noClip = true;
        ignoreFrustumCheck = true;
    }

    public EntityPositronShot(World world, EntityPlayer player, float damage) {
        this(world);
        shooter = player;
        shooterName = player.getCommandSenderName();
        this.damage = damage;
        Vec3 look = player.getLookVec();
        Vec3 eye = Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3 aim = eye.addVector(look.xCoord * 150D, look.yCoord * 150D, look.zCoord * 150D);
        net.minecraft.util.MovingObjectPosition block = world.rayTraceBlocks(eye, aim, false);
        if (block != null) aim = block.hitVec;

        // Offset the origin toward the right shoulder, then converge on the crosshair target.
        double x = eye.xCoord + look.xCoord * 1.4D - look.zCoord * 0.45D;
        double y = eye.yCoord + look.yCoord * 1.4D - 0.3D;
        double z = eye.zCoord + look.zCoord * 1.4D + look.xCoord * 0.45D;
        setPosition(x, y, z);
        originX = x;
        originY = y;
        originZ = z;
        double dx = aim.xCoord - x;
        double dy = aim.yCoord - y;
        double dz = aim.zCoord - z;
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dx * look.xCoord + dy * look.yCoord + dz * look.zCoord <= 0D || length < 0.01D) {
            dx = look.xCoord;
            dy = look.yCoord;
            dz = look.zCoord;
            length = 1D;
        }
        motionX = dx / length * SPEED;
        motionY = dy / length * SPEED;
        motionZ = dz / length * SPEED;
    }

    public double getOriginX() {
        return originX;
    }

    public double getOriginY() {
        return originY;
    }

    public double getOriginZ() {
        return originZ;
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeDouble(originX);
        buffer.writeDouble(originY);
        buffer.writeDouble(originZ);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        originX = buffer.readDouble();
        originY = buffer.readDouble();
        originZ = buffer.readDouble();
    }

    @Override
    protected void entityInit() {}

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (worldObj.isRemote) return;
        if (ticksExisted >= LIFETIME) {
            detonate();
            return;
        }
        // Sample each block crossed by the projectile; an ordinary ray trace stops at the first block.
        double length = Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
        int steps = Math.max(1, (int) Math.ceil(length * 2D));
        for (int i = 1; i <= steps; i++) {
            double fraction = i / (double) steps;
            double x = posX + motionX * fraction;
            double y = posY + motionY * fraction;
            double z = posZ + motionZ * fraction;
            damageEntities(x, y, z);
            if (!clearBeamBlocks(x, y, z)) {
                setPosition(x, y, z);
                detonate();
                return;
            }
        }
        setPosition(posX + motionX, posY + motionY, posZ + motionZ);
    }

    private boolean clearBeamBlocks(double x, double y, double z) {
        int minX = MathHelper.floor_double(x - BEAM_RADIUS);
        int minY = MathHelper.floor_double(y - BEAM_RADIUS);
        int minZ = MathHelper.floor_double(z - BEAM_RADIUS);
        int maxX = MathHelper.floor_double(x + BEAM_RADIUS);
        int maxY = MathHelper.floor_double(y + BEAM_RADIUS);
        int maxZ = MathHelper.floor_double(z + BEAM_RADIUS);
        float dirtResistance = Blocks.dirt.getExplosionResistance(this);
        for (int bx = minX; bx <= maxX; bx++) {
            for (int by = minY; by <= maxY; by++) {
                for (int bz = minZ; bz <= maxZ; bz++) {
                    double dx = bx + 0.5D - x;
                    double dy = by + 0.5D - y;
                    double dz = bz + 0.5D - z;
                    if (dx * dx + dy * dy + dz * dz > BEAM_RADIUS * BEAM_RADIUS) continue;
                    Block block = worldObj.getBlock(bx, by, bz);
                    if (block != Blocks.air && !block.isAir(worldObj, bx, by, bz)
                        && block.getExplosionResistance(this, worldObj, bx, by, bz, x, y, z) > dirtResistance)
                        return false;
                }
            }
        }
        boolean blocked = false;
        for (int bx = minX; bx <= maxX; bx++) {
            for (int by = minY; by <= maxY; by++) {
                for (int bz = minZ; bz <= maxZ; bz++) {
                    double dx = bx + 0.5D - x;
                    double dy = by + 0.5D - y;
                    double dz = bz + 0.5D - z;
                    if (dx * dx + dy * dy + dz * dz > BEAM_RADIUS * BEAM_RADIUS) continue;
                    if (!breakBlock(bx, by, bz)) blocked = true;
                }
            }
        }
        return !blocked;
    }

    private boolean breakBlock(int x, int y, int z) {
        Block block = worldObj.getBlock(x, y, z);
        if (block == Blocks.air || block.isAir(worldObj, x, y, z)) return true;
        if (block.getBlockHardness(worldObj, x, y, z) < 0F || !(shooter instanceof EntityPlayerMP)
            || worldObj.getTileEntity(x, y, z) != null
            || ((EntityPlayerMP) shooter).mcServer.isBlockProtected(worldObj, x, y, z, shooter)
            || ForgeHooks
                .onBlockBreakEvent(
                    worldObj,
                    ((EntityPlayerMP) shooter).theItemInWorldManager.getGameType(),
                    (EntityPlayerMP) shooter,
                    x,
                    y,
                    z)
                .isCanceled())
            return false;
        worldObj.setBlockToAir(x, y, z);
        return true;
    }

    private void damageEntities(double x, double y, double z) {
        AxisAlignedBB area = AxisAlignedBB.getBoundingBox(
            x - BEAM_RADIUS,
            y - BEAM_RADIUS,
            z - BEAM_RADIUS,
            x + BEAM_RADIUS,
            y + BEAM_RADIUS,
            z + BEAM_RADIUS);
        for (Object candidate : worldObj.getEntitiesWithinAABBExcludingEntity(this, area)) {
            Entity entity = (Entity) candidate;
            if (entity == shooter || entity instanceof EntityPositronShot
                || !entity.canBeCollidedWith()
                || !hitEntities.add(entity.getEntityId())) continue;
            entity.attackEntityFrom(
                DamageSource.causeIndirectMagicDamage(this, shooter == null ? this : shooter),
                damage);
        }
    }

    private void detonate() {
        AxisAlignedBB area = AxisAlignedBB.getBoundingBox(
            posX - BLAST_DAMAGE_RADIUS,
            posY - BLAST_DAMAGE_RADIUS,
            posZ - BLAST_DAMAGE_RADIUS,
            posX + BLAST_DAMAGE_RADIUS,
            posY + BLAST_DAMAGE_RADIUS,
            posZ + BLAST_DAMAGE_RADIUS);
        for (Object candidate : worldObj.getEntitiesWithinAABBExcludingEntity(this, area)) {
            Entity entity = (Entity) candidate;
            double distance = entity.getDistance(posX, posY, posZ);
            if (distance < BLAST_DAMAGE_RADIUS) {
                float amount = (float) (damage * 3D * (1D - distance / BLAST_DAMAGE_RADIUS));
                entity.attackEntityFrom(
                    DamageSource.causeIndirectMagicDamage(this, shooter == null ? this : shooter),
                    amount);
            }
        }
        worldObj.newExplosion(this, posX, posY, posZ, EXPLOSION_RADIUS, false, false);
        int radius = (int) EXPLOSION_RADIUS;
        for (int x = MathHelper.floor_double(posX) - radius; x <= MathHelper.floor_double(posX) + radius; x++) {
            for (int y = Math.max(0, MathHelper.floor_double(posY) - radius); y
                <= Math.min(255, MathHelper.floor_double(posY) + radius); y++) {
                for (int z = MathHelper.floor_double(posZ) - radius; z <= MathHelper.floor_double(posZ) + radius; z++) {
                    double dx = x + 0.5D - posX;
                    double dy = y + 0.5D - posY;
                    double dz = z + 0.5D - posZ;
                    if (dx * dx + dy * dy + dz * dz > EXPLOSION_RADIUS * EXPLOSION_RADIUS) continue;
                    Block block = worldObj.getBlock(x, y, z);
                    if (block != Blocks.air && !block.isAir(worldObj, x, y, z)) breakBlock(x, y, z);
                }
            }
        }
        setDead();
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
        shooterName = tag.getString("Shooter");
        shooter = worldObj.getPlayerEntityByName(shooterName);
        damage = tag.getFloat("Damage");
        originX = tag.getDouble("OriginX");
        originY = tag.getDouble("OriginY");
        originZ = tag.getDouble("OriginZ");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {
        tag.setString("Shooter", shooterName);
        tag.setFloat("Damage", damage);
        tag.setDouble("OriginX", originX);
        tag.setDouble("OriginY", originY);
        tag.setDouble("OriginZ", originZ);
    }
}
