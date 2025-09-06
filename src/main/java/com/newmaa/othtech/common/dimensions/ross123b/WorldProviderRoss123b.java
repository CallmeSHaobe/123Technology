package com.newmaa.othtech.common.dimensions.ross123b;

import static com.newmaa.othtech.Config.tier_Ross123b;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.IRenderHandler;

import com.newmaa.othtech.common.dimensions.RegisterDimensions;
import com.newmaa.othtech.common.dimensions.sky.SkyProviderRoss123b;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import galaxyspace.core.handler.api.IHostileBody;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.IExitHeight;
import micdoodle8.mods.galacticraft.api.world.ISolarLevel;
import micdoodle8.mods.galacticraft.api.world.ITeleportType;
import micdoodle8.mods.galacticraft.core.client.CloudRenderer;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.planets.mars.entities.EntityLandingBalloons;

public class WorldProviderRoss123b extends WorldProviderSpace
    implements IExitHeight, ISolarLevel, ITeleportType, IHostileBody {

    public WorldProviderRoss123b() {
        this.setSkyRenderer(new SkyProviderRoss123b());
    }

    @Override
    public boolean canDoRainSnowIce(Chunk chunk) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IRenderHandler getSkyRenderer() {
        return new SkyProviderRoss123b();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getCloudHeight() {
        return 100000f;
    }

    @Override
    public boolean canDoLightning(Chunk chunk) {
        return true;
    }

    @Override
    public boolean canSpaceshipTierPass(int tier) {
        return tier >= tier_Ross123b;
    }

    @Override
    public double getHorizon() {
        return 44.0;
    }

    @Override
    public float getFallDamageModifier() {
        return 10F;
    }

    @Override
    public double getFuelUsageMultiplier() {
        return 0.8;
    }

    @Override
    public float getGravity() {
        return -0.5F;
    }

    @Override
    public double getMeteorFrequency() {
        return 3.0;
    }

    @Override
    public float getSoundVolReductionAmount() {
        return 1;
    }

    @Override
    public float getThermalLevelModifier() {
        return 5.0F;
    }

    @Override
    public float getWindLevel() {
        return 0.0F;
    }

    @Override
    public boolean canRainOrSnow() {
        return true;
    }

    @Override
    public CelestialBody getCelestialBody() {
        return RegisterDimensions.planetRoss123b;
    }

    @Override
    public Class<? extends IChunkProvider> getChunkProviderClass() {
        return ChunkProviderRoss123b.class;
    }

    @Override
    public long getDayLength() {
        return 160000L;
    }

    @Override
    public boolean hasBreathableAtmosphere() {
        return false;
    }

    @Override
    public Vector3 getFogColor() {
        return new Vector3(0.75, 0.75, 0.75);
    }

    @Override
    public Vector3 getSkyColor() {
        return new Vector3(0.54, 0.0, 0.0);
    }

    @Override
    public Class<? extends WorldChunkManager> getWorldChunkManagerClass() {
        return WorldChunkManagerRoss123b.class;
    }

    @Override
    public boolean hasSunset() {
        return false;
    }

    @Override
    public boolean shouldForceRespawn() {
        return false;
    }

    @Override
    public double getSolarEnergyMultiplier() {
        return 114514;
    }

    @Override
    public double getYCoordinateToTeleport() {
        return 1200;
    }

    @Override
    public Vector3 getEntitySpawnLocation(WorldServer arg0, Entity arg1) {
        return new Vector3(arg1.posX, ConfigManagerCore.disableLander ? 250.0 : 900.0, arg1.posZ);
    }

    @Override
    public Vector3 getParaChestSpawnLocation(WorldServer arg0, EntityPlayerMP arg1, Random arg2) {
        if (ConfigManagerCore.disableLander) {
            double x = (arg2.nextDouble() * 2.0 - 1.0) * 5.0;
            double z = (arg2.nextDouble() * 2.0 - 1.0) * 5.0;
            return new Vector3(x, 220.0, z);
        } else {
            return null;
        }
    }

    @Override
    public Vector3 getPlayerSpawnLocation(WorldServer arg0, EntityPlayerMP arg1) {
        if (arg1 != null) {
            GCPlayerStats stats = GCPlayerStats.get(arg1);
            return new Vector3(
                stats.coordsTeleportedFromX,
                ConfigManagerCore.disableLander ? 250.0 : 900.0,
                stats.coordsTeleportedFromZ);
        } else {
            return null;
        }
    }

    @Override
    public void onSpaceDimensionChanged(World arg0, EntityPlayerMP player, boolean arg2) {
        if (player != null && GCPlayerStats.get(player).teleportCooldown <= 0) {
            if (player.capabilities.isFlying) {
                player.capabilities.isFlying = false;
            }

            EntityLandingBalloons lander = new EntityLandingBalloons(player);
            if (!arg0.isRemote) {
                arg0.spawnEntityInWorld(lander);
            }

            GCPlayerStats.get(player).teleportCooldown = 10;
        }

    }

    @Override
    public boolean useParachute() {
        return ConfigManagerCore.disableLander;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getStarBrightness(float par1) {
        float var2 = this.worldObj.getCelestialAngle(par1);
        float var3 = 1.0F - (MathHelper.cos(var2 * 6.2831855F) * 2.0F + 0.25F);
        if (var3 < 0.0F) {
            var3 = 0.0F;
        }

        if (var3 > 1.0F) {
            var3 = 1.0F;
        }

        return var3 * var3 * 0.5F + 0.3F;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getSunBrightness(float par1) {
        return 0.01F;
    }

    @Override
    public IRenderHandler getCloudRenderer() {
        return new CloudRenderer();
    }

    @Override
    public void setupAdventureSpawn(EntityPlayerMP player) {}

    @Override
    public int AtmosphericPressure() {
        return 10;
    }

    @Override
    public boolean SolarRadiation() {
        return true;
    }
}
