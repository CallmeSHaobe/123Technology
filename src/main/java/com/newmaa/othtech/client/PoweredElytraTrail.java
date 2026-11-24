package com.newmaa.othtech.client;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFireworkSparkFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.newmaa.othtech.common.item.itemArmor.ItemPoweredElytra;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import ganymedes01.etfuturum.api.elytra.IElytraPlayer;
import ganymedes01.etfuturum.items.equipment.ItemArmorElytra;

public final class PoweredElytraTrail {

    private static final double VIEW_DISTANCE_SQUARED = 32 * 32;
    private static final double MAX_STEP_SQUARED = 8 * 8;
    private static final int SPARK_BLUE = 0xACDAFF;
    private static final int SPARK_FADE = 0xDEF4FF;
    private final Random random = new Random();

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.renderViewEntity == null
            || mc.effectRenderer == null
            || mc.isGamePaused()
            || mc.gameSettings.particleSetting >= 2) return;

        boolean reduced = mc.gameSettings.particleSetting == 1;
        for (Object entity : mc.theWorld.playerEntities) {
            if (!(entity instanceof EntityPlayer) || !(entity instanceof IElytraPlayer)) continue;
            EntityPlayer player = (EntityPlayer) entity;
            if (player.isDead || player.onGround
                || player.isInvisible()
                || !((IElytraPlayer) player).etfu$isElytraFlying()
                || player.getDistanceSqToEntity(mc.renderViewEntity) > VIEW_DISTANCE_SQUARED) continue;
            ItemStack wings = ItemArmorElytra.getElytra(player);
            if (wings == null || !(wings.getItem() instanceof ItemPoweredElytra) || ItemArmorElytra.isBroken(wings)) {
                continue;
            }
            spawnTrail(mc, player, reduced);
        }
    }

    private void spawnTrail(Minecraft mc, EntityPlayer player, boolean reduced) {
        double dx = player.posX - player.lastTickPosX;
        double dy = player.posY - player.lastTickPosY;
        double dz = player.posZ - player.lastTickPosZ;
        double distanceSquared = dx * dx + dy * dy + dz * dz;
        // Ignore stationary frames and large position corrections rather than drawing a line through a teleport.
        if (distanceSquared < 0.0025D || distanceSquared > MAX_STEP_SQUARED) return;
        double distance = Math.sqrt(distanceSquared);
        double back = 0.5D / distance;
        double yOrigin = player.boundingBox.minY + Math.min(1.12D, player.height * 0.7D);
        int puffs = Math.min(reduced ? 2 : 5, (int) Math.ceil(distance / (reduced ? 1.8D : 0.95D)));
        for (int i = 0; i < puffs; i++) {
            // Sample the entire last tick of travel so the trail also holds together at boost speed.
            double offset = back + (i + 0.5D) / puffs;
            double x = player.posX - dx * offset + jitter(0.24D);
            double y = yOrigin - dy * offset + jitter(0.18D);
            double z = player.posZ - dz * offset + jitter(0.24D);
            EntitySmokeFX smoke = new TrailSmoke(mc.theWorld, x, y, z, 0.85F + random.nextFloat() * 0.35F);
            float shade = random.nextFloat() * 0.10F;
            smoke.setRBGColorF(0.58F + shade, 0.79F + shade, 1F);
            mc.effectRenderer.addEffect(smoke);

            if (i == 0 && player.ticksExisted % (reduced ? 4 : 2) == 0) {
                int sparks = reduced ? 1 : 2;
                for (int j = 0; j < sparks; j++) {
                    EntityFireworkSparkFX spark = new EntityFireworkSparkFX(
                        mc.theWorld,
                        x,
                        y,
                        z,
                        jitter(0.13D),
                        jitter(0.10D),
                        jitter(0.13D),
                        mc.effectRenderer);
                    spark.setColour(SPARK_BLUE);
                    spark.setFadeColour(SPARK_FADE);
                    spark.multipleParticleScaleBy(0.65F);
                    mc.effectRenderer.addEffect(spark);
                }
            }
        }
    }

    private double jitter(double spread) {
        return (random.nextDouble() - 0.5D) * spread;
    }

    private static final class TrailSmoke extends EntitySmokeFX {

        private static final float OPACITY = 0.67F;

        private TrailSmoke(World world, double x, double y, double z, float scale) {
            super(world, x, y, z, 0D, 0D, 0D, scale);
            setAlphaF(OPACITY);
        }

        @Override
        public void onUpdate() {
            super.onUpdate();
            if (!isDead) setAlphaF(OPACITY * (1F - (float) particleAge / particleMaxAge));
        }
    }
}
