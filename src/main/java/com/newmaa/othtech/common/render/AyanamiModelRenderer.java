package com.newmaa.othtech.common.render;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.Vertex;
import net.minecraftforge.client.model.obj.WavefrontObject;

import org.apache.logging.log4j.LogManager;
import org.lwjgl.opengl.GL11;

import com.newmaa.othtech.common.blocks.AyanamiAction;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** Shared OBJ geometry and bone traversal for the statue, its inventory model and the NPC. */
@SideOnly(Side.CLIENT)
public final class AyanamiModelRenderer {

    public static final ResourceLocation TEXTURE = new ResourceLocation("123technology", "models/ayanamiTexture.png");
    private static final ResourceLocation MODEL = new ResourceLocation("123technology", "models/Ayanami.obj");
    private static final ResourceLocation RIG = new ResourceLocation("123technology", "models/Ayanami.rig.json");
    private static final ResourceLocation BLINK = new ResourceLocation("123technology", "models/AyanamiBlink.obj");
    private static final float LID_HINGE_Y = 1.2652144F;
    private static final float LID_HINGE_Z = -0.1970207F;
    private static final float FACE_TILT = -6;

    private final IModelCustom model = AdvancedModelLoader.loadModel(MODEL);
    private final AyanamiRig rig = loadRig(model);
    private final float[] backFallLift = sampleBackFallLift(model);
    private final float[] rotation = new float[3];
    private final float[] pupilOffset = new float[3];

    private AyanamiModelRenderer() {}

    private static final class Assets {

        private static final AyanamiModelRenderer INSTANCE = new AyanamiModelRenderer();
    }

    private static final class BlinkAssets {

        private static final IModelCustom MODEL = AdvancedModelLoader.loadModel(BLINK);
    }

    public static void renderStanding() {
        Assets.INSTANCE.model.renderAll();
    }

    static void render(AyanamiAction action, long elapsed, float partialTick, float stride, float aimYaw,
        float aimPitch, AyanamiRig.Gaze gaze) {
        render(action, elapsed, partialTick, stride, aimYaw, aimPitch, gaze, 0);
    }

    static void render(AyanamiAction action, long elapsed, float partialTick, float stride, float aimYaw,
        float aimPitch, AyanamiRig.Gaze gaze, float eyeClosure) {
        render(action, elapsed, partialTick, stride, aimYaw, aimPitch, gaze, eyeClosure, null);
    }

    static void render(AyanamiAction action, long elapsed, float partialTick, float stride, float aimYaw,
        float aimPitch, AyanamiRig.Gaze gaze, float eyeClosure, Runnable heldItem) {
        render(action, elapsed, partialTick, stride, aimYaw, aimPitch, gaze, eyeClosure, 0, heldItem);
    }

    static void render(AyanamiAction action, long elapsed, float partialTick, float stride, float aimYaw,
        float aimPitch, AyanamiRig.Gaze gaze, float eyeClosure, float unfocused, Runnable heldItem) {
        AyanamiModelRenderer renderer = Assets.INSTANCE;
        if (renderer.rig == null || (action == AyanamiAction.STANDING && stride == 0
            && aimYaw == 0
            && aimPitch == 0
            && gaze == null
            && eyeClosure == 0
            && unfocused == 0
            && heldItem == null)) {
            renderStanding();
            return;
        }
        AyanamiRig.Clip clip = renderer.rig.clip(action);
        float tick = action == AyanamiAction.AWAKENING || action == AyanamiAction.STRIKE
            || action == AyanamiAction.THRUST ? clip.oneShotTime(elapsed, partialTick)
                : clip.loopTime(elapsed, partialTick);
        renderer.renderBone(
            renderer.rig.root(),
            clip,
            tick,
            stride,
            aimYaw,
            aimPitch,
            gaze,
            eyeClosure,
            unfocused,
            heldItem);
    }

    private static AyanamiRig loadRig(IModelCustom model) {
        try (Reader reader = new InputStreamReader(
            Minecraft.getMinecraft()
                .getResourceManager()
                .getResource(RIG)
                .getInputStream(),
            StandardCharsets.UTF_8)) {
            AyanamiRig rig = AyanamiRig.read(reader);
            Set<String> groups = new HashSet<>();
            if (model instanceof WavefrontObject) {
                for (GroupObject group : ((WavefrontObject) model).groupObjects) {
                    groups.add(group.name);
                }
            }
            if (!rig.matchesMeshes(groups)) throw new IllegalArgumentException("OBJ groups do not match Ayanami rig");
            return rig;
        } catch (IOException | RuntimeException e) {
            LogManager.getLogger("123technology")
                .warn("Could not load Ayanami rig; using standing mesh", e);
            return null;
        }
    }

    private void renderBone(AyanamiRig.Bone bone, AyanamiRig.Clip clip, float tick, float stride, float aimYaw,
        float aimPitch, AyanamiRig.Gaze gaze, float eyeClosure, float unfocused, Runnable heldItem) {
        clip.samplePose(bone.name, tick, stride, aimYaw, aimPitch, rotation);
        if (unfocused > 0) {
            rotation[0] *= 1 - unfocused;
            rotation[1] *= 1 - unfocused;
            rotation[2] *= 1 - unfocused;
        }
        boolean dilated = bone.pupilTravel != null && unfocused > 0;
        boolean rotated = rotation[0] != 0 || rotation[1] != 0 || rotation[2] != 0;
        boolean moved = gaze != null && bone.pupilTravel != null && gaze.pupilOffset(bone, pupilOffset);
        if (rotated || moved || dilated) GL11.glPushMatrix();
        try {
            if (moved) GL11.glTranslatef(
                pupilOffset[0] * (1 - unfocused),
                pupilOffset[1] * (1 - unfocused),
                pupilOffset[2] * (1 - unfocused));
            if (rotated) {
                float[] pivot = bone.pivot;
                GL11.glTranslatef(pivot[0], pivot[1], pivot[2]);
                if (rotation[2] != 0) GL11.glRotatef(rotation[2], 0, 0, 1);
                if (rotation[1] != 0) GL11.glRotatef(rotation[1], 0, 1, 0);
                if (rotation[0] != 0) GL11.glRotatef(rotation[0], 1, 0, 0);
                GL11.glTranslatef(-pivot[0], -pivot[1], -pivot[2]);
            }
            if (dilated) {
                // The two eye meshes are pupil-only quads. Scale in their tilted plane about their rig pivots,
                // keeping their 0.0025-block face clearance and the separate lids unchanged.
                float[] pivot = bone.pivot;
                GL11.glTranslatef(pivot[0], pivot[1], pivot[2]);
                GL11.glRotatef(FACE_TILT, 1, 0, 0);
                GL11.glScalef(1 + 0.28F * unfocused, 1 + 0.25F * unfocused, 1);
                GL11.glRotatef(-FACE_TILT, 1, 0, 0);
                GL11.glTranslatef(-pivot[0], -pivot[1], -pivot[2]);
            }
            for (String mesh : bone.meshes) model.renderPart(mesh);
            if (!bone.children.isEmpty()) {
                AyanamiRig.Gaze localGaze = gaze != null && rotated ? gaze.inBoneSpace(bone.pivot, rotation) : gaze;
                for (AyanamiRig.Bone child : bone.children) {
                    renderBone(child, clip, tick, stride, aimYaw, aimPitch, localGaze, eyeClosure, unfocused, heldItem);
                }
            }
            if (heldItem != null && AyanamiRig.HELD_ITEM_SOCKET.equals(bone.name)) {
                GL11.glPushMatrix();
                try {
                    GL11.glTranslatef(bone.pivot[0], bone.pivot[1], bone.pivot[2]);
                    heldItem.run();
                } finally {
                    GL11.glPopMatrix();
                }
            }
            if (eyeClosure > 0 && "head".equals(bone.name)) renderBlink(eyeClosure);
        } finally {
            if (rotated || moved || dilated) GL11.glPopMatrix();
        }
    }

    static float backFallLift(float angle) {
        float[] samples = Assets.INSTANCE.backFallLift;
        int lower = (int) angle;
        if (lower >= 90) return samples[90];
        return samples[lower] + (samples[lower + 1] - samples[lower]) * (angle - lower);
    }

    private static float[] sampleBackFallLift(IModelCustom model) {
        float[] samples = new float[91];
        if (!(model instanceof WavefrontObject obj)) return samples;
        // Cache the mesh's ground clearance over the 0..90-degree fall, not a guessed hip pivot.
        for (int angle = 1; angle <= 90; angle++) {
            double a = Math.toRadians(angle);
            float sin = (float) Math.sin(a);
            float cos = (float) Math.cos(a);
            for (Vertex v : obj.vertices) samples[angle] = Math.max(samples[angle], v.z * sin - v.y * cos);
        }
        return samples;
    }

    private static void renderBlink(float closure) {
        GL11.glPushMatrix();
        try {
            GL11.glTranslatef(0, LID_HINGE_Y, LID_HINGE_Z);
            GL11.glRotatef(FACE_TILT, 1, 0, 0);
            GL11.glScalef(1, closure, 1);
            GL11.glRotatef(-FACE_TILT, 1, 0, 0);
            GL11.glTranslatef(0, -LID_HINGE_Y, -LID_HINGE_Z);
            BlinkAssets.MODEL.renderAll();
        } finally {
            GL11.glPopMatrix();
        }
    }
}
