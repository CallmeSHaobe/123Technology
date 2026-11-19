package com.newmaa.othtech.common.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.newmaa.othtech.common.item.itemMisc.ItemSuicideDrone;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;

/** Server-authoritative, same-dimension point-target drone. The large model is cosmetic. */
public class EntitySuicideDrone extends Entity implements IEntityAdditionalSpawnData {

    private static final int ASCEND = 0;
    private static final int CRUISE = 1;
    private static final int DIVE = 2;
    private static final int MAX_AGE = 12000;
    private static final int MAX_UNLOADED_WAIT = 600;
    private static final double WORLD_LIMIT = 29999900D;
    private static final double COLLISION_RADIUS = 0.45D;

    private int variant = ItemSuicideDrone.PIG;
    private int targetX;
    private int targetY;
    private int targetZ;
    private float explosionSize;
    private float entityDamage;
    private float shockwaveRange;
    private double speed = 0.8D;
    private int phase = ASCEND;
    private double cruiseHeight;
    private int flightAge;
    private int unloadedWait;
    private int launchDimension;
    private boolean configured;
    private double launchX;
    private double launchY;
    private double launchZ;
    private boolean virtualFlight;
    private boolean pendingImpact;

    private static final int BANK_WATCHER = 20;
    private final FlightDynamics.State flight = new FlightDynamics.State();
    private float previousBank;
    private boolean launchInitialized;

    private int interpolationTicks;
    private double interpolationX;
    private double interpolationY;
    private double interpolationZ;
    private float interpolationYaw;
    private float interpolationPitch;

    public EntitySuicideDrone(World world) {
        super(world);
        setSize(0.9F, 0.9F);
        noClip = true; // Movement is swept explicitly; vanilla gravity/collision is not used.
        ignoreFrustumCheck = true; // RenderDrone's 8.75-block model exceeds the physical hitbox.
        renderDistanceWeight = 8D;
        launchDimension = world.provider.dimensionId;
    }

    /** explosionSize is the terrain and damage radius in blocks; entityDamage is the peak damage. */
    public EntitySuicideDrone(World world, double x, double y, double z, int variant, int targetX, int targetY,
        int targetZ, float explosionSize, float entityDamage, float shockwaveRange) {
        this(world);
        this.variant = normalizeVariant(variant);
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
        this.explosionSize = sanitizeExplosionSize(explosionSize, this.variant);
        this.entityDamage = sanitizeEntityDamage(entityDamage, this.variant);
        this.shockwaveRange = sanitizeShockwaveRange(shockwaveRange, this.variant);
        if (!validPosition(x, y, z) || !validTarget()) {
            setDead();
            return;
        }
        setPosition(x, y, z);
        launchX = x;
        launchY = y;
        launchZ = z;
        cruiseHeight = Math.min(250D, Math.max(y + 16D, targetY + 24D));
        configured = true;
        initializeLaunch(0F);
    }

    public int getVariant() {
        return variant;
    }

    private static DamageSource blastDamageSource(Entity drone) {
        return new EntityDamageSource("droneBlast", drone).setExplosion();
    }

    private static int normalizeVariant(int variant) {
        return ItemSuicideDrone.isValidVariant(variant) ? variant : ItemSuicideDrone.PIG;
    }

    private static float sanitizeExplosionSize(float value, int variant) {
        return Float.isNaN(value) || Float.isInfinite(value) || value <= 0F
            ? ItemSuicideDrone.getRegisteredVariant(variant)
                .getExplosionSize()
            : Math.min(64F, value);
    }

    private static float sanitizeEntityDamage(float value, int variant) {
        return Float.isNaN(value) || Float.isInfinite(value) || value < 0F
            ? ItemSuicideDrone.getRegisteredVariant(variant)
                .getEntityDamage()
            : Math.min(100000F, value);
    }

    private static float sanitizeShockwaveRange(float value, int variant) {
        return Float.isNaN(value) || Float.isInfinite(value) || value < 0F
            ? ItemSuicideDrone.getRegisteredVariant(variant)
                .getShockwaveRange()
            : Math.min(256F, value);
    }

    private static boolean finite(double value) {
        return !Double.isNaN(value) && !Double.isInfinite(value);
    }

    private static boolean validPosition(double x, double y, double z) {
        return finite(x) && finite(y)
            && finite(z)
            && Math.abs(x) <= WORLD_LIMIT
            && Math.abs(z) <= WORLD_LIMIT
            && y >= 0D
            && y <= 255D;
    }

    private boolean validTarget() {
        return Math.abs((double) targetX) <= WORLD_LIMIT && Math.abs((double) targetZ) <= WORLD_LIMIT
            && targetY >= 0
            && targetY <= 255;
    }

    @Override
    protected void entityInit() {
        dataWatcher.addObject(BANK_WATCHER, Float.valueOf(0F));
    }

    /** Platform yaw uses Minecraft's (-sin(yaw), cos(yaw)) forward convention. */
    public void initializeLaunch(float yaw) {
        initializeLaunch(yaw, 200);
    }

    /** 1 block = 1 metre, 20 ticks/second: km/h / 72 = blocks/tick. */
    public void initializeLaunch(float yaw, int speedKmh) {
        if (!finite(yaw)) yaw = 0F;
        FlightDynamics.launch(flight, yaw, speedKmh);
        flight.cruiseHeight = cruiseHeight;
        motionX = flight.vx;
        motionY = flight.vy;
        motionZ = flight.vz;
        rotationYaw = prevRotationYaw = yaw;
        rotationPitch = prevRotationPitch = -12F;
        phase = flight.phase;
        previousBank = 0F;
        flight.x = posX;
        flight.y = posY;
        flight.z = posZ;
        launchInitialized = true;
    }

    public float getBankAngle(float partialTicks) {
        float bank = worldObj.isRemote ? dataWatcher.getWatchableObjectFloat(BANK_WATCHER) : (float) flight.bank;
        return previousBank + (bank - previousBank) * Math.max(0F, Math.min(1F, partialTicks));
    }

    @Override
    public void onUpdate() {
        if (isDead) return;
        if (worldObj.isRemote) {
            previousBank = dataWatcher.getWatchableObjectFloat(BANK_WATCHER);
            super.onUpdate();
            interpolateClient();
            return;
        }
        if (!configured || !validPosition(posX, posY, posZ)
            || !validTarget()
            || launchDimension != worldObj.provider.dimensionId
            || ++flightAge > MAX_AGE) {
            setDead();
            return;
        }
        if (pendingImpact) {
            detonate();
            return;
        }
        if (!ensureFlightChunks()) {
            pauseForChunks();
            return;
        }
        if (!virtualFlight) super.onUpdate();
        if (isDead) return;
        if (!launchInitialized) initializeLaunch(rotationYaw);

        double fromX = flight.x, fromY = flight.y, fromZ = flight.z;
        double aimX = targetX + 0.5D, aimY = targetY + 0.5D, aimZ = targetZ + 0.5D;
        previousBank = (float) flight.bank;
        FlightDynamics.step(flight, aimX, aimY, aimZ);
        if (!validPosition(flight.x, flight.y, flight.z)) {
            setDead();
            return;
        }
        phase = flight.phase;
        motionX = flight.vx;
        motionY = flight.vy;
        motionZ = flight.vz;
        rotationYaw = (float) flight.yaw;
        rotationPitch = (float) flight.pitch;
        dataWatcher.updateObject(BANK_WATCHER, Float.valueOf((float) flight.bank));

        boolean nextLoaded = loadedArea(flight.x, flight.z, flight.x, flight.z, 1D);
        if (!nextLoaded) {
            // The launch chunk stays loaded even if players unload the visible part of the route.
            setVirtualFlight(true);
            setPosition(launchX, launchY, launchZ);
            unloadedWait = 0;
            return;
        }

        boolean inLanding = inLandingWindow(flight.x, flight.z);
        setVirtualFlight(false);
        if (loadedArea(fromX, fromZ, flight.x, flight.z, 1D)) {
            setPosition(fromX, fromY, fromZ);
        } else {
            // On re-entry do not ray trace from the parked entity across an unloaded gap.
            setPosition(flight.x, flight.y, flight.z);
        }
        double traceX = flight.x - posX, traceY = flight.y - posY, traceZ = flight.z - posZ;
        MovingObjectPosition hit = traceX != 0D || traceY != 0D || traceZ != 0D ? traceMovement(traceX, traceY, traceZ)
            : null;
        if (hit != null) {
            setPosition(hit.hitVec.xCoord, hit.hitVec.yCoord, hit.hitVec.zCoord);
            unloadedWait = 0;
            detonate();
            return;
        }
        setPosition(flight.x, flight.y, flight.z);
        unloadedWait = 0;
        if (inLanding && phase == DIVE
            && FlightDynamics.segmentDistance(fromX, fromY, fromZ, flight.x, flight.y, flight.z, aimX, aimY, aimZ)
                <= 0.9D) {
            detonate();
        }
    }

    private boolean inLandingWindow(double x, double z) {
        return DroneChunkLoader.inWindow(x, z, targetX + 0.5D, targetZ + 0.5D, DroneChunkLoader.LANDING_RADIUS);
    }

    private void setVirtualFlight(boolean hidden) {
        virtualFlight = hidden;
        if (isInvisible() != hidden) setInvisible(hidden);
    }

    /** Test the center and the eight corners of the small physical body, not the decorative wings. */
    private MovingObjectPosition traceMovement(double dx, double dy, double dz) {
        double centerY = (boundingBox.minY + boundingBox.maxY) * 0.5D;
        MovingObjectPosition hit = trace(posX, centerY, posZ, dx, dy, dz);
        if (hit != null) return hit;
        for (int x = -1; x <= 1; x += 2) {
            for (int y = -1; y <= 1; y += 2) {
                for (int z = -1; z <= 1; z += 2) {
                    hit = trace(
                        posX + x * COLLISION_RADIUS,
                        centerY + y * COLLISION_RADIUS,
                        posZ + z * COLLISION_RADIUS,
                        dx,
                        dy,
                        dz);
                    if (hit != null) return hit;
                }
            }
        }
        return null;
    }

    private MovingObjectPosition trace(double x, double y, double z, double dx, double dy, double dz) {
        return worldObj
            .rayTraceBlocks(Vec3.createVectorHelper(x, y, z), Vec3.createVectorHelper(x + dx, y + dy, z + dz), false);
    }

    /** blockExists only checks loaded chunks; unlike getChunkFromChunkCoords it never requests one. */
    private boolean loadedArea(double x1, double z1, double x2, double z2, double margin) {
        int minX = MathHelper.floor_double(Math.min(x1, x2) - margin) >> 4;
        int maxX = MathHelper.floor_double(Math.max(x1, x2) + margin) >> 4;
        int minZ = MathHelper.floor_double(Math.min(z1, z2) - margin) >> 4;
        int maxZ = MathHelper.floor_double(Math.max(z1, z2) + margin) >> 4;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                if (!worldObj.blockExists(x << 4, 64, z << 4)) return false;
            }
        }
        return true;
    }

    /** Acquire both endpoint windows before the platform consumes an item. */
    public boolean ensureFlightChunks() {
        return !isDead && !worldObj.isRemote
            && configured
            && DroneChunkLoader.update(this, launchX, launchZ, targetX, targetZ, explosionSize, shockwaveRange);
    }

    @Override
    public void setDead() {
        DroneChunkLoader.release(this);
        super.setDead();
    }

    private void pauseForChunks() {
        // Do not erase momentum: update resumes with exactly the saved velocity and flight age.
        if (++unloadedWait >= MAX_UNLOADED_WAIT) setDead();
    }

    private void detonate() {
        if (worldObj.isRemote || isDead) return;
        if (virtualFlight) return;
        pendingImpact = true;
        double margin = DroneChunkLoader.blastMargin(explosionSize, shockwaveRange);
        if (!DroneChunkLoader.holdImpact(this, launchX, launchZ, explosionSize, shockwaveRange)
            || !loadedArea(posX, posZ, posX, posZ, margin)) {
            pauseForChunks();
            return;
        }
        try {
            applyBlast();
            clearBlastSphere();
        } finally {
            setDead();
        }
    }

    /** Clear the sphere by block centers, without ray tracing or generating drops. */
    private void clearBlastSphere() {
        double radius = explosionSize;
        double radiusSquared = radius * radius;
        int minX = (int) Math.ceil(posX - radius - 0.5D);
        int maxX = MathHelper.floor_double(posX + radius - 0.5D);
        int minY = Math.max(0, (int) Math.ceil(posY - radius - 0.5D));
        int maxY = Math.min(worldObj.getActualHeight() - 1, MathHelper.floor_double(posY + radius - 0.5D));
        for (int x = minX; x <= maxX; x++) {
            double dx = x + 0.5D - posX;
            double remainingX = radiusSquared - dx * dx;
            for (int y = minY; y <= maxY; y++) {
                double dy = y + 0.5D - posY;
                double remaining = remainingX - dy * dy;
                if (remaining < 0D) continue;
                double halfSpan = Math.sqrt(remaining);
                int minZ = (int) Math.ceil(posZ - halfSpan - 0.5D);
                int maxZ = MathHelper.floor_double(posZ + halfSpan - 0.5D);
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = worldObj.getBlock(x, y, z);
                    if (!block.isAir(worldObj, x, y, z) && block.getBlockHardness(worldObj, x, y, z) >= 0F) {
                        worldObj.setBlockToAir(x, y, z);
                    }
                }
            }
        }
    }

    private void applyBlast() {
        double horizontalRadius = Math.max(explosionSize, shockwaveRange);
        double shockwaveHeight = shockwaveRange * 0.65D;
        double verticalRadius = Math.max(explosionSize, shockwaveHeight);
        AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(
            posX - horizontalRadius,
            posY - verticalRadius,
            posZ - horizontalRadius,
            posX + horizontalRadius,
            posY + verticalRadius,
            posZ + horizontalRadius);
        double damageRadiusSquared = (double) explosionSize * explosionSize;
        double shockRadiusSquared = (double) shockwaveRange * shockwaveRange;
        DamageSource damageSource = blastDamageSource(this);
        for (Entity entity : worldObj.getEntitiesWithinAABBExcludingEntity(this, bounds)) {
            if (entity.isDead) continue;
            double dx = entity.posX - posX;
            double dy = entity.posY + entity.getEyeHeight() - posY;
            double dz = entity.posZ - posZ;
            double damageDistanceSquared = dx * dx + dy * dy + dz * dz;
            if (entityDamage > 0F && damageDistanceSquared < damageRadiusSquared) {
                double falloff = 1D - Math.sqrt(damageDistanceSquared) / explosionSize;
                entity.attackEntityFrom(damageSource, (float) (entityDamage * falloff));
            }
            if (entity.isDead || shockwaveRange <= 0F) continue;
            double normalizedShockDistance = (dx * dx + dz * dz) / shockRadiusSquared
                + (dy / shockwaveHeight) * (dy / shockwaveHeight);
            if (normalizedShockDistance >= 1D) continue;
            double impulse = 1.5D * (1D - Math.sqrt(normalizedShockDistance));
            double directionLength = Math.sqrt(damageDistanceSquared);
            if (directionLength < 1.0E-6D) {
                entity.addVelocity(0D, impulse, 0D);
            } else {
                double scale = impulse / directionLength;
                entity.addVelocity(dx * scale, dy * scale, dz * scale);
            }
            entity.velocityChanged = true;
        }
        worldObj.playSoundEffect(posX, posY, posZ, "random.explode", 4F, 0.7F);
        if (worldObj instanceof WorldServer server) {
            server.func_147487_a("hugeexplosion", posX, posY, posZ, 1, 0D, 0D, 0D, 0D);
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return !isDead && !isInvisible();
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (virtualFlight || isInvisible() || isEntityInvulnerable() || Float.isNaN(amount) || amount <= 0F || isDead) {
            return false;
        }
        if (!worldObj.isRemote) {
            setBeenAttacked();
            setDead(); // Shooting down a drone never triggers its warhead.
        }
        return true;
    }

    @Override
    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int ticks) {
        if (!validPosition(x, y, z) || !finite(yaw) || !finite(pitch)) return;
        // Do not interpolate an endpoint teleport across the hidden route.
        double dx = x - posX, dy = y - posY, dz = z - posZ;
        if (dx * dx + dy * dy + dz * dz > 32D * 32D) {
            setPosition(x, y, z);
            lastTickPosX = prevPosX = x;
            lastTickPosY = prevPosY = y;
            lastTickPosZ = prevPosZ = z;
            rotationYaw = prevRotationYaw = yaw;
            rotationPitch = prevRotationPitch = pitch;
            interpolationTicks = 0;
            return;
        }
        interpolationX = x;
        interpolationY = y;
        interpolationZ = z;
        interpolationYaw = yaw;
        interpolationPitch = pitch;
        interpolationTicks = Math.max(1, Math.min(10, ticks));
    }

    private void interpolateClient() {
        if (interpolationTicks <= 0) return;
        double fraction = 1D / interpolationTicks;
        setPosition(
            posX + (interpolationX - posX) * fraction,
            posY + (interpolationY - posY) * fraction,
            posZ + (interpolationZ - posZ) * fraction);
        rotationYaw += MathHelper.wrapAngleTo180_float(interpolationYaw - rotationYaw) * fraction;
        rotationPitch += (interpolationPitch - rotationPitch) * fraction;
        --interpolationTicks;
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {
        tag.setInteger("Variant", variant);
        tag.setInteger("TargetX", targetX);
        tag.setInteger("TargetY", targetY);
        tag.setInteger("TargetZ", targetZ);
        tag.setFloat("ExplosionPower", explosionSize);
        tag.setFloat("ExplosionSize", explosionSize);
        tag.setFloat("EntityDamage", entityDamage);
        tag.setFloat("ShockwaveRange", shockwaveRange);
        tag.setDouble("Speed", speed);
        tag.setInteger("Phase", phase);
        tag.setDouble("CruiseHeight", cruiseHeight);
        tag.setInteger("FlightAge", flightAge);
        tag.setInteger("UnloadedWait", unloadedWait);
        tag.setInteger("LaunchDimension", launchDimension);
        tag.setBoolean("Configured", configured);
        tag.setDouble("LaunchX", launchX);
        tag.setDouble("LaunchY", launchY);
        tag.setDouble("LaunchZ", launchZ);
        tag.setBoolean("VirtualFlight", virtualFlight);
        tag.setBoolean("PendingImpact", pendingImpact);
        tag.setDouble("FlightX", flight.x);
        tag.setDouble("FlightY", flight.y);
        tag.setDouble("FlightZ", flight.z);
        tag.setInteger("LaunchSpeedKmh", flight.speedKmh);
        tag.setInteger("FlightModel", 1);
        tag.setInteger("FlightTicks", flight.age);
        tag.setDouble("BodyYaw", flight.yaw);
        tag.setDouble("BodyPitch", flight.pitch);
        tag.setDouble("Bank", flight.bank);
        tag.setDouble("ApproachX", flight.approachX);
        tag.setDouble("ApproachZ", flight.approachZ);
        // Vanilla Entity writes the actual motion vector in its standard Motion list.
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
        variant = normalizeVariant(tag.getInteger("Variant"));
        targetX = tag.getInteger("TargetX");
        targetY = tag.getInteger("TargetY");
        targetZ = tag.getInteger("TargetZ");
        explosionSize = sanitizeExplosionSize(
            tag.hasKey("ExplosionSize") ? tag.getFloat("ExplosionSize") : tag.getFloat("ExplosionPower"),
            variant);
        entityDamage = sanitizeEntityDamage(
            tag.hasKey("EntityDamage") ? tag.getFloat("EntityDamage") : Float.NaN,
            variant);
        shockwaveRange = sanitizeShockwaveRange(
            tag.hasKey("ShockwaveRange") ? tag.getFloat("ShockwaveRange") : Float.NaN,
            variant);
        speed = tag.getDouble("Speed");
        if (!finite(speed) || speed < 0.1D || speed > 2D) speed = 0.8D;
        phase = tag.getInteger("Phase");
        cruiseHeight = tag.getDouble("CruiseHeight");
        flightAge = tag.getInteger("FlightAge");
        unloadedWait = tag.getInteger("UnloadedWait");
        launchDimension = tag.getInteger("LaunchDimension");
        launchX = tag.hasKey("LaunchX") ? tag.getDouble("LaunchX") : posX;
        launchY = tag.hasKey("LaunchY") ? tag.getDouble("LaunchY") : posY;
        launchZ = tag.hasKey("LaunchZ") ? tag.getDouble("LaunchZ") : posZ;
        setVirtualFlight(tag.getBoolean("VirtualFlight"));
        pendingImpact = !virtualFlight && tag.getBoolean("PendingImpact");
        flight.x = tag.hasKey("FlightX") ? tag.getDouble("FlightX") : posX;
        flight.y = tag.hasKey("FlightY") ? tag.getDouble("FlightY") : posY;
        flight.z = tag.hasKey("FlightZ") ? tag.getDouble("FlightZ") : posZ;
        configured = tag.getBoolean("Configured") && tag.hasKey("TargetX")
            && tag.hasKey("TargetY")
            && tag.hasKey("TargetZ")
            && tag.hasKey("LaunchDimension");
        launchInitialized = true;
        // Missing/zero setting preserves the original 1.4-block/tick powered flight.
        int savedSpeedKmh = tag.getInteger("LaunchSpeedKmh");
        flight.speedKmh = savedSpeedKmh == 0 ? 0 : FlightDynamics.sanitizeSpeedKmh(savedSpeedKmh);
        flight.yaw = tag.hasKey("BodyYaw") ? tag.getDouble("BodyYaw") : rotationYaw;
        flight.pitch = tag.hasKey("BodyPitch") ? tag.getDouble("BodyPitch") : rotationPitch;
        flight.bank = tag.getDouble("Bank");
        flight.age = tag.hasKey("FlightTicks") ? tag.getInteger("FlightTicks") : Math.max(30, flightAge);
        flight.phase = phase;
        flight.cruiseHeight = cruiseHeight;
        flight.approachX = tag.getDouble("ApproachX");
        flight.approachZ = tag.getDouble("ApproachZ");
        previousBank = (float) flight.bank;
        // Old stationary/vertical waypoint saves receive a forward flying restart, not zero momentum.
        if (!tag.hasKey("FlightModel") && motionX * motionX + motionZ * motionZ < 0.01D) {
            double yaw = Math.toRadians(rotationYaw);
            motionX = -Math.sin(yaw) * 1.2D;
            motionZ = Math.cos(yaw) * 1.2D;
        }
        flight.vx = motionX;
        flight.vy = motionY;
        flight.vz = motionZ;
        rotationYaw = (float) flight.yaw;
        rotationPitch = (float) flight.pitch;
        dataWatcher.updateObject(BANK_WATCHER, Float.valueOf((float) flight.bank));
        if (!configured || !validTarget()
            || !validPosition(posX, posY, posZ)
            || !validPosition(flight.x, flight.y, flight.z)
            || !validPosition(launchX, launchY, launchZ)
            || phase < ASCEND
            || phase > FlightDynamics.APPROACH
            || !finite(motionX)
            || !finite(motionY)
            || !finite(motionZ)
            || !finite(flight.yaw)
            || !finite(flight.pitch)
            || !finite(flight.bank)
            || Math.abs(flight.bank) > 55D
            || Math.abs(flight.pitch) > 90D
            || flight.age < 0
            || flight.age > MAX_AGE
            || !finite(flight.approachX)
            || !finite(flight.approachZ)
            || !finite(cruiseHeight)
            || cruiseHeight < 0D
            || cruiseHeight > 250D
            || flightAge < 0
            || flightAge >= MAX_AGE
            || unloadedWait < 0
            || unloadedWait >= MAX_UNLOADED_WAIT) {
            setDead();
        }
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeInt(variant);
        buffer.writeInt(targetX);
        buffer.writeInt(targetY);
        buffer.writeInt(targetZ);
        buffer.writeFloat(explosionSize);
        buffer.writeDouble(speed);
        buffer.writeInt(phase);
        buffer.writeDouble(cruiseHeight);
        buffer.writeInt(flightAge);
        buffer.writeFloat((float) flight.bank);
        buffer.writeInt(flight.speedKmh);
        buffer.writeFloat(entityDamage);
        buffer.writeFloat(shockwaveRange);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        variant = normalizeVariant(buffer.readInt());
        targetX = buffer.readInt();
        targetY = buffer.readInt();
        targetZ = buffer.readInt();
        explosionSize = sanitizeExplosionSize(buffer.readFloat(), variant);
        double receivedSpeed = buffer.readDouble();
        speed = finite(receivedSpeed) ? Math.max(0.1D, Math.min(2D, receivedSpeed)) : 0.8D;
        phase = Math.max(ASCEND, Math.min(FlightDynamics.APPROACH, buffer.readInt()));
        double receivedHeight = buffer.readDouble();
        cruiseHeight = finite(receivedHeight) ? Math.max(0D, Math.min(250D, receivedHeight)) : 128D;
        flightAge = Math.max(0, Math.min(MAX_AGE, buffer.readInt()));
        float bank = buffer.readableBytes() >= 4 ? buffer.readFloat() : 0F;
        int receivedSpeedKmh = buffer.readableBytes() >= 4 ? buffer.readInt() : 0;
        flight.speedKmh = receivedSpeedKmh == 0 ? 0 : FlightDynamics.sanitizeSpeedKmh(receivedSpeedKmh);
        entityDamage = sanitizeEntityDamage(buffer.readableBytes() >= 4 ? buffer.readFloat() : Float.NaN, variant);
        shockwaveRange = sanitizeShockwaveRange(buffer.readableBytes() >= 4 ? buffer.readFloat() : Float.NaN, variant);
        previousBank = finite(bank) ? Math.max(-55F, Math.min(55F, bank)) : 0F;
        dataWatcher.updateObject(BANK_WATCHER, Float.valueOf(previousBank));
    }
}
