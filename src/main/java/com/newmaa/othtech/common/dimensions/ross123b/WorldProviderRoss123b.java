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
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.client.IRenderHandler;

import com.newmaa.othtech.common.dimensions.RegisterDimensions;

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

    /** 阵雨周期(ticks): 每 6 分钟一个循环。 */
    private static final int SHOWER_CYCLE = 7200;
    /** 每次阵雨的最短/额外时长(ticks): 60 ~ 150 秒。 */
    private static final int SHOWER_MIN = 1200;
    private static final int SHOWER_SPAN = 1800;

    public WorldProviderRoss123b() {

    }

    /**
     * 天气: 不再是"永远下雨", 而是每 {@link #SHOWER_CYCLE} tick 来一场 60~150 秒的阵雨, 其余时间放晴。
     * <p>
     * 先调基类: {@code WorldProviderSpace.updateWeather()} 负责维度时间偏移/存档, 并在
     * {@link #canRainOrSnow()} 为 true 时触发原版的 {@code updateWeatherBody()}(降雨强度的过渡也靠它)。
     * 之后按世界时间把 {@code raining/rainTime} 改写成我们自己的阵雨节奏 —— 原版那套一次下
     * 10~20 分钟的雨在末日废墟里太久了。下雨/放晴的切换会由 {@code WorldServer.updateWeather()}
     * 检测到并自动给该维度的玩家发包, 所以客户端能正常看到雨。
     */
    @Override
    public void updateWeather() {
        super.updateWeather();
        if (this.worldObj == null || this.worldObj.isRemote) {
            return;
        }

        WorldInfo info = this.worldObj.getWorldInfo();
        long time = this.worldObj.getTotalWorldTime();
        long cycle = Math.floorDiv(time, SHOWER_CYCLE);
        long phase = Math.floorMod(time, SHOWER_CYCLE);
        // 每个周期换一个时长, 但由种子+周期决定, 存档重载后节奏不变
        long roll = Math.floorMod(cycle * 0x9E3779B97F4A7C15L ^ this.worldObj.getSeed(), SHOWER_SPAN + 1);
        int duration = SHOWER_MIN + (int) roll;

        boolean raining = phase < duration;
        info.setRaining(raining);
        // 保持 rainTime 恒正, 免得原版自己的定时器把天气又切回去
        info.setRainTime((int) (raining ? (duration - phase) : (SHOWER_CYCLE - phase)));
        info.setThundering(false);
        info.setThunderTime(SHOWER_CYCLE);
    }

    @Override
    public boolean canDoRainSnowIce(Chunk chunk) {
        return false;
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
        return 2000L;
    }

    @Override
    public boolean hasBreathableAtmosphere() {
        return true;
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
        return true;
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
