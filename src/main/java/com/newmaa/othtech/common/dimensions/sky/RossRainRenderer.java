package com.newmaa.othtech.common.dimensions.sky;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.IRenderHandler;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** Ross123b-only crimson rain; vanilla's rain texture supplies the falling streaks. */
@SideOnly(Side.CLIENT)
public final class RossRainRenderer extends IRenderHandler {

    public static final RossRainRenderer INSTANCE = new RossRainRenderer();
    private static final ResourceLocation RAIN = new ResourceLocation("textures/environment/rain.png");

    private RossRainRenderer() {}

    @Override
    public void render(float partialTicks, WorldClient world, Minecraft mc) {
        float strength = world.getRainStrength(partialTicks);
        EntityLivingBase camera = mc.renderViewEntity;
        if (strength <= 0.0F || camera == null) return;

        double cameraX = camera.lastTickPosX + (camera.posX - camera.lastTickPosX) * partialTicks;
        double cameraY = camera.lastTickPosY + (camera.posY - camera.lastTickPosY) * partialTicks;
        double cameraZ = camera.lastTickPosZ + (camera.posZ - camera.lastTickPosZ) * partialTicks;
        int centerX = MathHelper.floor_double(cameraX);
        int centerY = MathHelper.floor_double(cameraY);
        int centerZ = MathHelper.floor_double(cameraZ);
        int range = mc.gameSettings.fancyGraphics ? 10 : 5;
        float time = (float) (world.getTotalWorldTime() % 100000L) + partialTicks;

        mc.entityRenderer.enableLightmap(partialTicks);
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        try {
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            mc.getTextureManager()
                .bindTexture(RAIN);
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            tessellator.setTranslation(-cameraX, -cameraY, -cameraZ);
            for (int z = centerZ - range; z <= centerZ + range; z++) {
                for (int x = centerX - range; x <= centerX + range; x++) {
                    BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
                    if (!biome.canSpawnLightningBolt() || world.getWorldChunkManager()
                        .getTemperatureAtHeight(
                            biome.getFloatTemperature(x, centerY, z),
                            world.getPrecipitationHeight(x, z))
                        < 0.15F) continue;
                    int top = world.getPrecipitationHeight(x, z);
                    int bottom = Math.max(centerY - range, top);
                    int upper = Math.max(centerY + range, top);
                    if (bottom >= upper) continue;
                    double dx = x + 0.5D - cameraX;
                    double dz = z + 0.5D - cameraZ;
                    double distance = Math.sqrt(dx * dx + dz * dz);
                    double length = Math.max(0.001D, distance);
                    float halfX = (float) (-dz / length * 0.5D);
                    float halfZ = (float) (dx / length * 0.5D);
                    float fade = 1.0F - (float) (distance / (range + 1.0D));
                    if (fade <= 0.0F) continue;
                    int hash = x * x * 3121 + x * 45238971 ^ z * z * 418711 + z * 13761;
                    float scroll = ((hash & 31) + time) / 32.0F * 3.0F;
                    tessellator.setBrightness(world.getLightBrightnessForSkyBlocks(x, Math.max(top, centerY), z, 0));
                    tessellator.setColorRGBA_F(0.72F, 0.11F, 0.14F, strength * (0.5F + fade * 0.35F));
                    tessellator.addVertexWithUV(x + 0.5D - halfX, bottom, z + 0.5D - halfZ, 0, bottom / 4.0F + scroll);
                    tessellator.addVertexWithUV(x + 0.5D + halfX, bottom, z + 0.5D + halfZ, 1, bottom / 4.0F + scroll);
                    tessellator.addVertexWithUV(x + 0.5D + halfX, upper, z + 0.5D + halfZ, 1, upper / 4.0F + scroll);
                    tessellator.addVertexWithUV(x + 0.5D - halfX, upper, z + 0.5D - halfZ, 0, upper / 4.0F + scroll);
                }
            }
            tessellator.draw();
        } finally {
            Tessellator.instance.setTranslation(0, 0, 0);
            GL11.glPopAttrib();
            mc.entityRenderer.disableLightmap(partialTicks);
        }
    }
}
