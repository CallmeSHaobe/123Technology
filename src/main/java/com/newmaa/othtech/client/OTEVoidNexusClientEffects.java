package com.newmaa.othtech.client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;

import com.newmaa.othtech.machine.OTEVoidNexusMatrix;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.GTMod;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;

/**
 * Procedural client-side animation for running Void Nexus Matrix controllers.
 *
 * <p>
 * The animation deliberately uses no textures so the portal remains complete with resource packs.
 * It is centered one block above the controller and fades out with distance.
 * </p>
 */
@SideOnly(Side.CLIENT)
public final class OTEVoidNexusClientEffects {

    private static final int EFFECT_START_TICKS = 4;
    private static final int EFFECT_STOP_TICKS = 6;
    private static final double SKY_RADIUS = 120.0D;
    private static final double FULL_EFFECT_RADIUS = 96.0D;
    private static final float GOLDEN_ANGLE = 2.39996323F;
    private static final float EFFECT_SCALE = 9.45F;
    private static final int VORTEX_ARM_COUNT = 6;

    private final List<MachineFx> machines = new ArrayList<>();
    private static OTEVoidNexusClientEffects instance;
    private World trackedWorld;

    public static void install() {
        OTEVoidNexusClientEffects effects = new OTEVoidNexusClientEffects();
        instance = effects;
        MinecraftForge.EVENT_BUS.register(effects);
        FMLCommonHandler.instance()
            .bus()
            .register(effects);
    }

    public static void renderMatrixAt(OTEVoidNexusMatrix matrix, double x, double y, double z, float partialTicks) {
        registerMatrix(matrix);
    }

    public static void registerMatrix(OTEVoidNexusMatrix matrix) {
        OTEVoidNexusClientEffects effects = instance;
        if (effects == null) return;
        IGregTechTileEntity base = matrix.getBaseMetaTileEntity();
        if (base == null || base.getWorld() == null) return;
        if (effects.trackedWorld != base.getWorld()) {
            effects.trackedWorld = base.getWorld();
            effects.machines.clear();
        }
        MachineFx machine = effects.findMachine(base);
        if (machine == null) {
            machine = new MachineFx(base, base.getXCoord(), base.getYCoord(), base.getZCoord(), base.getWorld());
            effects.machines.add(machine);
        }
        machine.active = true;
        effects.updateFadeState(machine, base.getWorld());
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft minecraft = Minecraft.getMinecraft();
        World world = minecraft.theWorld;
        if (world != trackedWorld) {
            trackedWorld = world;
            machines.clear();
        }
        if (world == null) {
            machines.clear();
            return;
        }
        updateFadeStates(world);
    }

    private void updateFadeStates(World world) {
        for (int index = machines.size() - 1; index >= 0; index--) {
            MachineFx machine = machines.get(index);
            boolean remove = updateFadeState(machine, world);
            if (remove) {
                machines.remove(index);
            }
        }
    }

    private boolean updateFadeState(MachineFx machine, World world) {
        long worldTick = world.getTotalWorldTime();
        if (machine.lastFadeTick == worldTick) return false;
        machine.lastFadeTick = worldTick;

        if (machine.world != world) return true;
        boolean loaded = machine.isLoaded(world);
        Minecraft minecraft = Minecraft.getMinecraft();
        boolean inRange = minecraft.thePlayer == null
            || distanceSq(minecraft.thePlayer, machine) <= SKY_RADIUS * SKY_RADIUS;
        boolean animationRequested = inRange && machine.isLoadedAndMachineRunning(world);
        if (animationRequested) {
            machine.onTicks = Math.min(EFFECT_START_TICKS, machine.onTicks + 1);
            machine.offTicks = 0;
        } else {
            machine.onTicks = 0;
            machine.offTicks = Math.min(EFFECT_STOP_TICKS + 1, machine.offTicks + 1);
        }
        machine.previousFade = machine.fade;
        if (machine.onTicks >= EFFECT_START_TICKS) machine.fade = Math.min(1F, machine.fade + 0.045F);
        if (machine.offTicks > EFFECT_STOP_TICKS) machine.fade = Math.max(0F, machine.fade - 0.032F);
        return (!loaded || !inRange) && machine.fade <= 0.001F;
    }

    private MachineFx findMachine(IGregTechTileEntity base) {
        for (MachineFx machine : machines) {
            if (machine.base == base) return machine;
        }
        return null;
    }

    @SubscribeEvent
    public void onFogColors(EntityViewRenderEvent.FogColors event) {
        MachineFx machine = nearestMachine(event.entity, SKY_RADIUS);
        if (machine == null) return;
        float strength = distanceFade(distanceSq(event.entity, machine));
        event.red = lerp(event.red, 0.115F, 0.62F * strength);
        event.green = lerp(event.green, 0.012F, 0.72F * strength);
        event.blue = lerp(event.blue, 0.235F, 0.68F * strength);
    }

    @SubscribeEvent
    public void onFogDensity(EntityViewRenderEvent.FogDensity event) {
        MachineFx machine = nearestMachine(event.entity, SKY_RADIUS);
        if (machine == null) return;
        float strength = distanceFade(distanceSq(event.entity, machine));
        event.density = Math.max(event.density, 0.018F + 0.042F * strength);
    }

    public static void renderSky(float partialTicks) {
        if (instance == null) return;
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityLivingBase viewer = minecraft.renderViewEntity;
        MachineFx machine = instance.nearestMachine(viewer, SKY_RADIUS);
        if (machine == null) return;
        if (ActiveRenderInfo.getBlockAtEntityViewpoint(viewer.worldObj, viewer, partialTicks)
            .getMaterial()
            .isLiquid()) return;
        float strength = machine.interpolatedFade(partialTicks) * atmosphereFade(distanceSq(viewer, machine));
        if (strength <= 0.002F) return;

        GL11.glPushAttrib(
            GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT
                | GL11.GL_DEPTH_BUFFER_BIT
                | GL11.GL_CURRENT_BIT
                | GL11.GL_LIGHTING_BIT
                | GL11.GL_TRANSFORM_BIT);
        // Tint the background before terrain draws, using clip space so the horizon and FOV cannot leave a gap.
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        try {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_FOG);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glShadeModel(GL11.GL_SMOOTH);
            Tessellator mesh = Tessellator.instance;
            mesh.startDrawingQuads();
            mesh.setColorRGBA_F(0.245F, 0.085F, 0.36F, 0.63F * strength);
            mesh.addVertex(-1D, -1D, 0D);
            mesh.addVertex(1D, -1D, 0D);
            mesh.setColorRGBA_F(0.135F, 0.03F, 0.275F, 0.82F * strength);
            mesh.addVertex(1D, 1D, 0D);
            mesh.addVertex(-1D, 1D, 0D);
            mesh.draw();
        } finally {
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.thePlayer == null || minecraft.theWorld == null || trackedWorld != minecraft.theWorld) return;
        float time = GTMod.clientProxy()
            .getAnimationRenderTicks();
        drawMachineEffectsInWorld(minecraft, event.partialTicks, time);
    }

    private void drawMachineEffectsInWorld(Minecraft minecraft, float partialTicks, float time) {
        Entity viewer = minecraft.renderViewEntity;
        if (viewer == null) return;
        double cameraX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * partialTicks;
        double cameraY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partialTicks;
        double cameraZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * partialTicks;
        double maxDistanceSq = SKY_RADIUS * SKY_RADIUS;

        List<MachineFx> visible = new ArrayList<>();
        for (MachineFx machine : machines) {
            if (!machine.isLoaded(trackedWorld) || machine.fade <= 0.001F) continue;
            double dx = machine.x + 0.5D - cameraX;
            double dy = machine.y + 1.5D - cameraY;
            double dz = machine.z + 0.5D - cameraZ;
            machine.cameraDistanceSq = dx * dx + dy * dy + dz * dz;
            if (machine.cameraDistanceSq <= maxDistanceSq) visible.add(machine);
        }
        visible.sort((left, right) -> Double.compare(right.cameraDistanceSq, left.cameraDistanceSq));

        for (MachineFx machine : visible) {
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glPushMatrix();
            GL11.glTranslated(machine.x + 0.5D - cameraX, machine.y - 0.5D - cameraY, machine.z + 0.5D - cameraZ);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
            GL11.glDepthMask(false);
            GL11.glShadeModel(GL11.GL_SMOOTH);

            drawMachineEffects(machine, partialTicks, time);

            GL11.glColor4f(1F, 1F, 1F, 1F);
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    private static void drawMachineEffects(MachineFx machine, float partialTicks, float time) {
        float pulse = 1F + 0.055F * (float) Math.sin(time * 0.11D);
        float fade = machine.interpolatedFade(partialTicks) * distanceFade(machine.cameraDistanceSq);
        if (fade <= 0.001F) return;
        float distance = (float) Math.sqrt(machine.cameraDistanceSq);
        float detailFade = clamp((72F - distance) / 24F, 0F, 1F);
        GL11.glPushMatrix();
        GL11.glScalef(EFFECT_SCALE, EFFECT_SCALE, EFFECT_SCALE);
        float coreRadius = 0.62F * pulse;
        float mistFade = fade * (0.65F + 0.35F * clamp((distance - 7F) / 12F, 0F, 1F));
        drawPortalVortex(time, coreRadius, fade, mistFade, detailFade);
        drawPortalCore(coreRadius, fade);
        drawEventHorizonShells(time, fade, detailFade > 0.01F);
        drawOrbitingRiftRings(time, fade, 7);
        drawFracturedVoidLattice(time, fade);
        drawPrismaticShards(time, fade);
        drawNeuralWeb(time, fade * detailFade);
        drawEscapingParticles(time, fade * detailFade);
        drawMachineMist(time, fade);
        GL11.glPopMatrix();
    }

    private static void drawEventHorizonShells(float time, float fade, boolean highDetail) {
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        drawSphere(
            0.78F + 0.018F * (float) Math.sin(time * 0.031D),
            0.055F,
            0.002F,
            0.16F,
            0.16F * fade,
            time * 0.003F,
            14,
            32);
        if (highDetail) {
            drawSphere(
                0.94F + 0.025F * (float) Math.sin(time * 0.019D + 1.2D),
                0.14F,
                0.008F,
                0.32F,
                0.08F * fade,
                -time * 0.0022F,
                12,
                28);
            drawSphere(
                1.13F + 0.035F * (float) Math.sin(time * 0.013D + 2.1D),
                0.14F,
                0.18F,
                0.33F,
                0.022F * fade,
                time * 0.0015F,
                10,
                24);
            drawSphere(
                1.34F + 0.05F * (float) Math.sin(time * 0.009D + 0.6D),
                0.34F,
                0.015F,
                0.74F,
                0.024F * fade,
                -time * 0.001F,
                9,
                22);
        }
    }

    private static void drawOrbitingRiftRings(float time, float fade, int ringCount) {
        for (int ring = 0; ring < ringCount; ring++) {
            float phase = time * (0.012F + ring * 0.0021F);
            float blockRadius = 30F + ring * 5F + 0.22F * (float) Math.sin(time * 0.024D + ring * 1.7D);
            float radius = blockRadius / EFFECT_SCALE;
            float tube = 0.020F + ring * 0.0032F;
            float rotationSpeed = (ring % 2 == 0 ? 1F : -1F) * (0.12F + ring * 0.055F);
            GL11.glPushMatrix();
            GL11.glTranslatef(0F, (ring % 3 - 1) * 0.075F, 0F);
            GL11.glRotatef(time * rotationSpeed, 0.82F, 0.42F + ring * 0.11F, 0.24F);
            GL11.glRotatef(18F * ring + 11F * (float) Math.sin(phase), 1F, 0F, 0F);
            drawRiftTorus(radius, tube, 48, 8, 0.20F + ring * 0.035F, 0.41F + ring * 0.036F, fade * 0.30F);
            GL11.glPopMatrix();
        }
    }

    private static void drawRiftTorus(float radius, float tube, int majorSegments, int minorSegments, float red,
        float blue, float fade) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        for (int major = 0; major < majorSegments; major++) {
            float angle0 = (float) (Math.PI * 2D * major / majorSegments);
            float angle1 = (float) (Math.PI * 2D * (major + 1) / majorSegments);
            float centerX0 = (float) Math.cos(angle0) * radius;
            float centerZ0 = (float) Math.sin(angle0) * radius;
            float centerX1 = (float) Math.cos(angle1) * radius;
            float centerZ1 = (float) Math.sin(angle1) * radius;
            float normalX0 = (float) Math.cos(angle0);
            float normalZ0 = (float) Math.sin(angle0);
            float normalX1 = (float) Math.cos(angle1);
            float normalZ1 = (float) Math.sin(angle1);
            for (int minor = 0; minor < minorSegments; minor++) {
                float tubeAngle0 = (float) (Math.PI * 2D * minor / minorSegments);
                float tubeAngle1 = (float) (Math.PI * 2D * (minor + 1) / minorSegments);
                float radial0 = (float) Math.cos(tubeAngle0) * tube;
                float vertical0 = (float) Math.sin(tubeAngle0) * tube;
                float radial1 = (float) Math.cos(tubeAngle1) * tube;
                float vertical1 = (float) Math.sin(tubeAngle1) * tube;
                float shimmer0 = 0.55F + 0.45F * (float) Math.sin(major * 0.35F + minor + 0.5F);
                float shimmer1 = 0.55F + 0.45F * (float) Math.sin((major + 1) * 0.35F + minor + 0.5F);
                tessellator.setColorRGBA_F(red * shimmer0, 0.025F, blue * shimmer0, (0.18F + 0.18F * shimmer0) * fade);
                tessellator.addVertex(centerX0 + normalX0 * radial0, vertical0, centerZ0 + normalZ0 * radial0);
                tessellator.setColorRGBA_F(red * shimmer1, 0.025F, blue * shimmer1, (0.18F + 0.18F * shimmer1) * fade);
                tessellator.addVertex(centerX1 + normalX1 * radial0, vertical0, centerZ1 + normalZ1 * radial0);
                tessellator.addVertex(centerX1 + normalX1 * radial1, vertical1, centerZ1 + normalZ1 * radial1);
                tessellator.addVertex(centerX0 + normalX0 * radial1, vertical1, centerZ0 + normalZ0 * radial1);
            }
        }
        tessellator.draw();
    }

    private static void drawPortalCore(float radius, float fade) {
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        drawSphere(radius, 0.012F, 0.002F, 0.027F, 0.92F * fade, 0F, 18, 40);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
    }

    private static void drawSphere(float radius, float red, float green, float blue, float alpha, float rotation,
        int latitudeSegments, int longitudeSegments) {
        Tessellator tessellator = Tessellator.instance;
        for (int latitude = 0; latitude < latitudeSegments; latitude++) {
            float phi0 = (float) (Math.PI * latitude / latitudeSegments);
            float phi1 = (float) (Math.PI * (latitude + 1) / latitudeSegments);
            tessellator.startDrawingQuads();
            for (int longitude = 0; longitude < longitudeSegments; longitude++) {
                float theta0 = (float) (Math.PI * 2D * longitude / longitudeSegments) + rotation;
                float theta1 = (float) (Math.PI * 2D * (longitude + 1) / longitudeSegments) + rotation;
                addSphereVertex(tessellator, radius, phi0, theta0, red, green, blue, alpha);
                addSphereVertex(tessellator, radius, phi0, theta1, red, green, blue, alpha);
                addSphereVertex(tessellator, radius, phi1, theta1, red, green, blue, alpha);
                addSphereVertex(tessellator, radius, phi1, theta0, red, green, blue, alpha);
            }
            tessellator.draw();
        }
    }

    private static void addSphereVertex(Tessellator tessellator, float radius, float phi, float theta, float red,
        float green, float blue, float alpha) {
        float sinPhi = (float) Math.sin(phi);
        float shimmer = 0.48F + 0.52F * (float) Math.abs(Math.sin(theta * 2.5F + phi * 3F));
        tessellator.setColorRGBA_F(red * shimmer, green * shimmer, blue * shimmer, alpha * (0.35F + 0.65F * shimmer));
        tessellator
            .addVertex(sinPhi * Math.cos(theta) * radius, Math.cos(phi) * radius, sinPhi * Math.sin(theta) * radius);
    }

    private static void drawPortalVortex(float time, float coreRadius, float fade, float mistFade, float detailFade) {
        Tessellator mesh = Tessellator.instance;
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        mesh.startDrawingQuads();
        int segments = detailFade > 0.01F ? 48 : 28;
        for (int arm = 0; arm < VORTEX_ARM_COUNT; arm++) {
            double start = vortexArmAngle(arm, time);
            float spread = 0.82F + 0.27F * hash(arm + 7F);
            for (int segment = 0; segment < segments; segment++) {
                float a = segment / (float) segments;
                float b = (segment + 1F) / segments;
                for (int strip = -1; strip < 1; strip++) {
                    addVortexVertex(mesh, start, spread, time, coreRadius, a, strip, mistFade);
                    addVortexVertex(mesh, start, spread, time, coreRadius, b, strip, mistFade);
                    addVortexVertex(mesh, start, spread, time, coreRadius, b, strip + 1, mistFade);
                    addVortexVertex(mesh, start, spread, time, coreRadius, a, strip + 1, mistFade);
                }
            }
        }
        mesh.draw();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        if (detailFade > 0.01F) {
            GL11.glLineWidth(2.1F);
            drawVortexTrails(mesh, coreRadius, time, fade * detailFade);
        }
        drawPortalRim(mesh, coreRadius, time, fade);
    }

    private static void addVortexVertex(Tessellator mesh, double start, float spread, float time, float coreRadius,
        float inward, int edge, float fade) {
        float outside = 1F - inward;
        float pulse = 0.5F + 0.5F * (float) Math.cos(inward * 16D - time * 0.24D + start);
        float taper = clamp(inward * 6F, 0F, 1F) * clamp(outside * 12F, 0F, 1F);
        float lip = 0.35F + 0.65F * inward;
        mesh.setColorRGBA_F(
            lerp(0.24F, 0.48F, inward),
            lerp(0.35F, 0.59F, inward),
            lerp(0.52F, 0.82F, inward),
            edge == 0 ? taper * lip * (0.44F + 0.19F * pulse * pulse) * fade : 0F);
        addVortexPoint(mesh, start, spread, coreRadius, inward, edge);
    }

    private static double vortexArmAngle(int arm, float time) {
        return 2D * Math.PI * arm / VORTEX_ARM_COUNT - time * 0.017D;
    }

    private static void addVortexPoint(Tessellator mesh, double start, float spread, float coreRadius, float inward,
        float edge) {
        double angle = start + inward * (3.2D + 2.7D * inward);
        float outside = 1F - inward;
        float width = spread * (0.060F + 0.37F * outside);
        double radius = coreRadius - 0.065D + (2.38D + 0.18D * spread) * outside * outside + edge * width;
        double crest = Math.sin(inward * 8.5D + start);
        double height = outside * crest * 0.12D + edge * width * 0.30D;
        mesh.addVertex(Math.cos(angle) * radius, height, Math.sin(angle) * radius);
    }

    private static void drawVortexTrails(Tessellator mesh, float coreRadius, float time, float fade) {
        mesh.startDrawing(GL11.GL_LINES);
        for (int arm = 0; arm < VORTEX_ARM_COUNT; arm++) {
            double start = vortexArmAngle(arm, time);
            float spread = 0.82F + 0.27F * hash(arm + 7F);
            for (int trace = 0; trace < 2; trace++) {
                float phase = (time * 0.013F + arm * 0.173F + trace * 0.43F) % 1F;
                float glow = fade * Math.min(1F, phase * 8F) * Math.min(1F, (1F - phase) * 10F);
                float head = 0.16F + 0.81F * phase;
                float tail = head - lerp(0.064F, 0.11F, phase);
                float offset = trace == 0 ? -0.27F : 0.23F;
                for (int segment = 0; segment < 5; segment++) {
                    float a = segment / 5F;
                    float b = (segment + 1F) / 5F;
                    mesh.setColorRGBA_F(0.62F, 0.81F, 1F, glow * a * a * 0.86F);
                    addVortexPoint(mesh, start, spread, coreRadius, lerp(tail, head, a), offset);
                    mesh.setColorRGBA_F(0.62F, 0.81F, 1F, glow * b * b * 0.86F);
                    addVortexPoint(mesh, start, spread, coreRadius, lerp(tail, head, b), offset);
                }
            }
        }
        mesh.draw();
    }

    private static void drawPortalRim(Tessellator mesh, float coreRadius, float time, float fade) {
        int segments = 64;
        mesh.startDrawingQuads();
        for (int segment = 0; segment < segments; segment++) {
            double a = 2D * Math.PI * segment / segments;
            double b = 2D * Math.PI * (segment + 1) / segments;
            for (int strip = -1; strip < 1; strip++) {
                addRimVertex(mesh, coreRadius, a, strip, time, fade);
                addRimVertex(mesh, coreRadius, b, strip, time, fade);
                addRimVertex(mesh, coreRadius, b, strip + 1, time, fade);
                addRimVertex(mesh, coreRadius, a, strip + 1, time, fade);
            }
        }
        mesh.draw();
    }

    private static void addRimVertex(Tessellator mesh, float coreRadius, double angle, int edge, float time,
        float fade) {
        float sheen = Math.max(0F, (float) Math.cos(angle - vortexArmAngle(0, time)));
        sheen *= sheen;
        sheen *= sheen;
        mesh.setColorRGBA_F(0.48F, 0.63F + 0.12F * sheen, 0.85F, edge == 0 ? (0.35F + 0.22F * sheen) * fade : 0F);
        double radius = coreRadius + 0.09D + 0.16D * edge;
        mesh.addVertex(Math.cos(angle) * radius, 0D, Math.sin(angle) * radius);
    }

    private static void drawFracturedVoidLattice(float time, float fade) {
        Tessellator tessellator = Tessellator.instance;
        int shards = 42;
        tessellator.startDrawingQuads();
        for (int shard = 0; shard < shards; shard++) {
            float y = 1F - 2F * (shard + 0.5F) / shards;
            float radial = (float) Math.sqrt(Math.max(0F, 1F - y * y));
            float theta = shard * GOLDEN_ANGLE + time * 0.008F;
            float nx = radial * (float) Math.cos(theta);
            float ny = y;
            float nz = radial * (float) Math.sin(theta);
            float tx = -nz;
            float tz = nx;
            float tl = (float) Math.sqrt(tx * tx + tz * tz);
            if (tl < 1.0E-4F) {
                tx = 1F;
                tz = 0F;
                tl = 1F;
            }
            tx /= tl;
            tz /= tl;
            float bx = ny * tz;
            float by = nz * tx - nx * tz;
            float bz = -ny * tx;
            float bl = (float) Math.sqrt(bx * bx + by * by + bz * bz);
            bx /= bl;
            by /= bl;
            bz /= bl;
            float twist = time * 0.018F + shard * 0.37F;
            float cos = (float) Math.cos(twist);
            float sin = (float) Math.sin(twist);
            float ax = tx * cos + bx * sin;
            float ay = by * sin;
            float az = tz * cos + bz * sin;
            float cx = -tx * sin + bx * cos;
            float cy = by * cos;
            float cz = -tz * sin + bz * cos;
            float shell = 1.35F + 0.09F * (float) Math.sin(time * 0.055D + shard * 1.7F);
            float width = 0.075F + 0.075F * (float) (Math.sin(shard * 2.17D) * 0.5D + 0.5D);
            float height = width * (0.55F + 0.35F * (float) (Math.cos(shard * 1.31D) * 0.5D + 0.5D));
            addShardVertex(
                tessellator,
                nx,
                ny,
                nz,
                shell,
                ax,
                ay,
                az,
                cx,
                cy,
                cz,
                -width,
                -height,
                0.28F,
                0.03F,
                0.68F,
                0.46F * fade);
            addShardVertex(
                tessellator,
                nx,
                ny,
                nz,
                shell,
                ax,
                ay,
                az,
                cx,
                cy,
                cz,
                width,
                -height * 0.8F,
                0.10F,
                0.55F,
                1F,
                0.52F * fade);
            addShardVertex(
                tessellator,
                nx,
                ny,
                nz,
                shell,
                ax,
                ay,
                az,
                cx,
                cy,
                cz,
                width * 0.45F,
                height,
                0.78F,
                0.08F,
                1F,
                0.44F * fade);
            addShardVertex(
                tessellator,
                nx,
                ny,
                nz,
                shell,
                ax,
                ay,
                az,
                cx,
                cy,
                cz,
                -width * 0.75F,
                height * 0.6F,
                0.36F,
                0.08F,
                0.86F,
                0.34F * fade);
        }
        tessellator.draw();

        GL11.glLineWidth(1.4F);
        tessellator.startDrawing(GL11.GL_LINES);
        for (int crack = 0; crack < 34; crack++) {
            float y0 = 0.88F - crack * 1.76F / 34F;
            float y1 = y0 - 1.76F / 34F;
            float angle0 = crack * GOLDEN_ANGLE + time * 0.012F;
            float angle1 = angle0 + 0.82F;
            float r0 = (float) Math.sqrt(Math.max(0F, 1F - y0 * y0)) * 1.38F;
            float r1 = (float) Math.sqrt(Math.max(0F, 1F - y1 * y1)) * 1.38F;
            tessellator.setColorRGBA_F(0.42F, 0.10F, 1F, 0.42F * fade);
            tessellator.addVertex(Math.cos(angle0) * r0, y0 * 1.38F, Math.sin(angle0) * r0);
            tessellator.setColorRGBA_F(0.04F, 0.68F, 1F, 0.06F * fade);
            tessellator.addVertex(Math.cos(angle1) * r1, y1 * 1.38F, Math.sin(angle1) * r1);
        }
        tessellator.draw();
    }

    private static void addShardVertex(Tessellator tessellator, float nx, float ny, float nz, float shell, float ax,
        float ay, float az, float cx, float cy, float cz, float offsetA, float offsetC, float red, float green,
        float blue, float alpha) {
        tessellator.setColorRGBA_F(red, green, blue, alpha);
        tessellator.addVertex(
            nx * shell + ax * offsetA + cx * offsetC,
            ny * shell + ay * offsetA + cy * offsetC,
            nz * shell + az * offsetA + cz * offsetC);
    }

    private static void drawPrismaticShards(float time, float fade) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(GL11.GL_TRIANGLES);
        for (int shard = 0; shard < 18; shard++) {
            float scatter = hash(shard * 17.17F + 0.41F);
            float blockOrbit = 20F + 80F * scatter + 1.8F * (float) Math.sin(time * 0.018D + shard * 2.07D);
            float orbit = blockOrbit / EFFECT_SCALE;
            float angle = shard * GOLDEN_ANGLE + time * (0.010F + (shard % 4) * 0.0022F) * (shard % 2 == 0 ? 1F : -1F);
            float x = (float) Math.cos(angle) * orbit;
            float z = (float) Math.sin(angle) * orbit;
            float y = 1.55F * (float) Math.sin(shard * 1.73D) + 0.42F * (float) Math.sin(time * 0.027D + shard * 0.73D);
            float size = 0.060F + 0.060F * (float) (Math.sin(time * 0.07D + shard) * 0.5D + 0.5D);
            float blueMix = shard % 6 == 0 ? 0.11F : 0.025F;
            tessellator.setColorRGBA_F(0.62F, 0.045F, 0.94F, 0.48F * fade);
            tessellator.addVertex(x, y + size, z);
            tessellator.setColorRGBA_F(0.10F, blueMix + 0.24F, 0.88F, 0.30F * fade);
            tessellator.addVertex(x + size * 0.86F, y - size * 0.45F, z + size * 0.32F);
            tessellator.setColorRGBA_F(0.18F, 0.015F, 0.42F, 0.20F * fade);
            tessellator.addVertex(x - size * 0.64F, y - size * 0.55F, z - size * 0.48F);
        }
        tessellator.draw();
    }

    private static float hash(float value) {
        float hashed = (float) (Math.sin(value * 12.9898D) * 43758.5453D);
        return hashed - (float) Math.floor(hashed);
    }

    private static void drawNeuralWeb(float time, float fade) {
        GL11.glLineWidth(1.8F);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(GL11.GL_LINES);
        for (int strand = 0; strand < 26; strand++) {
            float theta = strand * GOLDEN_ANGLE + time * 0.013F;
            float y = 0.92F - strand * 1.84F / 26F;
            float radius = (float) Math.sqrt(Math.max(0F, 1F - y * y)) * 1.18F;
            float lastX = (float) Math.cos(theta) * radius;
            float lastY = y;
            float lastZ = (float) Math.sin(theta) * radius;
            for (int segment = 1; segment <= 7; segment++) {
                float progress = segment / 7F;
                float wave = (float) Math.sin(time * 0.08D + strand * 0.8D + progress * 5F) * 0.08F;
                float reach = 1F - progress * 0.82F;
                float nextX = lastX * 0.62F + (float) Math.cos(theta + progress * 1.2F) * radius * reach * 0.28F + wave;
                float nextY = y * (1F - progress * 0.72F) + wave * 0.7F;
                float nextZ = lastZ * 0.62F + (float) Math.sin(theta + progress * 1.2F) * radius * reach * 0.28F + wave;
                tessellator.setColorRGBA_F(0.62F, 0.12F, 1F, (1F - progress) * 0.38F * fade);
                tessellator.addVertex(lastX, lastY, lastZ);
                tessellator.setColorRGBA_F(0.06F, 0.74F, 1F, (1F - progress) * 0.12F * fade);
                tessellator.addVertex(nextX, nextY, nextZ);
                lastX = nextX;
                lastY = nextY;
                lastZ = nextZ;
            }
        }
        tessellator.draw();
    }

    private static void drawEscapingParticles(float time, float fade) {
        GL11.glPointSize(3.4F);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(GL11.GL_POINTS);
        for (int particle = 0; particle < 58; particle++) {
            float phase = (time * 0.022F + particle * 0.137F) % 1F;
            float angle = particle * GOLDEN_ANGLE + time * 0.035F + phase * 2.4F;
            float radius = 0.55F + 0.9F * (float) Math.sin(Math.PI * phase) + 0.16F * (float) Math.sin(time * 0.1F);
            float y = -1.05F + phase * 4.25F;
            float alpha = (1F - phase) * 0.8F;
            tessellator.setColorRGBA_F(0.58F, 0.18F, 1F, alpha * fade);
            tessellator.addVertex(Math.cos(angle) * radius, y, Math.sin(angle) * radius);
        }
        tessellator.draw();
    }

    private static void drawMachineMist(float time, float fade) {
        for (int layer = 0; layer < 5; layer++) {
            float layerPhase = time * 0.033F + layer * 0.41F;
            float y = -1.38F + layer * 0.17F;
            float radius = 0.88F + 0.24F * (float) Math.sin(layerPhase) + layer * 0.025F;
            GL11.glPushMatrix();
            GL11.glTranslatef(0F, y, 0F);
            GL11.glRotatef(time * (0.7F + layer * 0.09F) * (layer % 2 == 0 ? 1F : -1F), 0F, 1F, 0F);
            drawMistBand(radius, 0.13F + layer * 0.008F, 0.31F, 0.025F, 0.58F, (0.085F - layer * 0.008F) * fade);
            GL11.glPopMatrix();
        }
    }

    private static void drawMistBand(float radius, float width, float red, float green, float blue, float alpha) {
        Tessellator tessellator = Tessellator.instance;
        int segments = 64;
        tessellator.startDrawingQuads();
        for (int index = 0; index < segments; index++) {
            float angle0 = (float) (Math.PI * 2D * index / segments);
            float angle1 = (float) (Math.PI * 2D * (index + 1) / segments);
            float inner0 = radius - width * (0.65F + 0.35F * (float) Math.sin(angle0 * 5F));
            float inner1 = radius - width * (0.65F + 0.35F * (float) Math.sin(angle1 * 5F));
            float middle0 = radius + width * 0.08F * (float) Math.sin(angle0 * 3F);
            float middle1 = radius + width * 0.08F * (float) Math.sin(angle1 * 3F);
            float outer0 = radius + width * (0.86F + 0.14F * (float) Math.sin(angle0 * 4F));
            float outer1 = radius + width * (0.86F + 0.14F * (float) Math.sin(angle1 * 4F));
            tessellator.setColorRGBA_F(red, green, blue, 0F);
            tessellator.addVertex(Math.cos(angle0) * inner0, 0F, Math.sin(angle0) * inner0);
            tessellator.addVertex(Math.cos(angle1) * inner1, 0F, Math.sin(angle1) * inner1);
            tessellator.setColorRGBA_F(red, green, blue, Math.max(0F, alpha));
            tessellator.addVertex(Math.cos(angle1) * middle1, 0F, Math.sin(angle1) * middle1);
            tessellator.addVertex(Math.cos(angle0) * middle0, 0F, Math.sin(angle0) * middle0);
            tessellator.setColorRGBA_F(red, green, blue, Math.max(0F, alpha));
            tessellator.addVertex(Math.cos(angle0) * middle0, 0F, Math.sin(angle0) * middle0);
            tessellator.addVertex(Math.cos(angle1) * middle1, 0F, Math.sin(angle1) * middle1);
            tessellator.setColorRGBA_F(red, green, blue, 0F);
            tessellator.addVertex(Math.cos(angle1) * outer1, 0F, Math.sin(angle1) * outer1);
            tessellator.addVertex(Math.cos(angle0) * outer0, 0F, Math.sin(angle0) * outer0);
        }
        tessellator.draw();
    }

    private MachineFx nearestMachine(Entity viewer, double radius) {
        if (viewer == null || trackedWorld != viewer.worldObj) return null;
        MachineFx nearest = null;
        double nearestDistance = radius * radius;
        for (MachineFx machine : machines) {
            if (machine.fade <= 0.001F || !machine.isLoaded(viewer.worldObj)) continue;
            double distance = distanceSq(viewer, machine);
            if (distance <= nearestDistance) {
                nearestDistance = distance;
                nearest = machine;
            }
        }
        return nearest;
    }

    private static double distanceSq(Entity viewer, MachineFx machine) {
        double dx = viewer.posX - (machine.x + 0.5D);
        double dy = viewer.posY - (machine.y - 0.5D);
        double dz = viewer.posZ - (machine.z + 0.5D);
        return dx * dx + dy * dy + dz * dz;
    }

    private static float atmosphereFade(double distanceSquared) {
        float near = clamp((float) ((SKY_RADIUS - Math.sqrt(distanceSquared)) / 92D), 0F, 1F);
        return near * near * (3F - 2F * near);
    }

    private static float distanceFade(double distanceSquared) {
        double distance = Math.sqrt(distanceSquared);
        if (distance >= SKY_RADIUS) return 0F;
        if (distance <= FULL_EFFECT_RADIUS) return 1F;
        float linear = (float) ((SKY_RADIUS - distance) / (SKY_RADIUS - FULL_EFFECT_RADIUS));
        return linear * linear * (3F - 2F * linear);
    }

    private static float lerp(float from, float to, float amount) {
        return from + (to - from) * amount;
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private static final class MachineFx {

        private final IGregTechTileEntity base;
        private final int x;
        private final int y;
        private final int z;
        private final World world;
        private boolean active;
        private float fade;
        private float previousFade;
        private int onTicks;
        private int offTicks;
        private long lastFadeTick = Long.MIN_VALUE;
        private double cameraDistanceSq;

        private MachineFx(IGregTechTileEntity base, int x, int y, int z, World world) {
            this.base = base;
            this.x = x;
            this.y = y;
            this.z = z;
            this.world = world;
        }

        private boolean isLoaded(World activeWorld) {
            return active && world == activeWorld
                && base.getWorld() == activeWorld
                && base.getMetaTileEntity() instanceof OTEVoidNexusMatrix
                && activeWorld.getChunkProvider()
                    .chunkExists(x >> 4, z >> 4);
        }

        private boolean isLoadedAndMachineRunning(World activeWorld) {
            if (!isLoaded(activeWorld)) return false;
            OTEVoidNexusMatrix matrix = (OTEVoidNexusMatrix) base.getMetaTileEntity();
            return matrix.isClientRenderActive();
        }

        private float interpolatedFade(float partialTicks) {
            return lerp(previousFade, fade, partialTicks);
        }
    }
}
