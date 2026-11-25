package com.newmaa.othtech.common.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import com.newmaa.othtech.client.AyanamiDialogueClient;
import com.newmaa.othtech.common.blocks.AyanamiAction;
import com.newmaa.othtech.common.entity.EntityAyanami;
import com.newmaa.othtech.common.item.itemWeapons.ItemLonginus;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class RenderAyanamiEntity extends Render {

    // Display scale for a hand prop, without changing the player or projectile model.
    private static final float HELD_LONGINUS_LENGTH = 2.15F * 3F;

    private static final float BODY_SCALE = EntityAyanami.BODY_SCALE;

    public RenderAyanamiEntity() {
        shadowSize = 0.35F * BODY_SCALE;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTick) {
        if (entity.isInvisible()) return;
        EntityAyanami npc = (EntityAyanami) entity;
        boolean talking = npc.isInDialogue();
        float bodyYaw = talking ? interpolateAngle(npc.prevRotationYaw, npc.rotationYaw, partialTick)
            : interpolateAngle(npc.prevRenderYawOffset, npc.renderYawOffset, partialTick);
        float headYaw = interpolateAngle(npc.prevRotationYawHead, npc.rotationYawHead, partialTick);
        float aimYaw = -MathHelper.clamp_float(MathHelper.wrapAngleTo180_float(headYaw - bodyYaw), -45, 45);
        float aimPitch = -MathHelper.clamp_float(lerp(npc.prevRotationPitch, npc.rotationPitch, partialTick), -25, 25);
        float swingAmount = lerp(npc.prevLimbSwingAmount, npc.limbSwingAmount, partialTick);
        float swingPhase = npc.limbSwing - npc.limbSwingAmount * (1 - partialTick);
        ItemStack heldItem = npc.getHeldItem();
        AyanamiAction action = npc.getAction();
        if ((talking && action == AyanamiAction.LOOK_AROUND) || (heldItem != null && action == AyanamiAction.WAVE)) {
            action = AyanamiAction.STANDING;
        }
        // Use vanilla's interpolated horizontal-motion swing; onGround is not authoritative on the tracking client.
        float stride = talking || action == AyanamiAction.AWAKENING
            || action == AyanamiAction.STRIKE
            || action == AyanamiAction.THRUST ? 0 : AyanamiRig.walkingStride(swingPhase, swingAmount);
        long elapsed = npc.worldObj.getTotalWorldTime() - npc.getActionStartTick();
        float modelYaw = 180 - bodyYaw;
        int downedTicks = npc.getDownedTicksElapsed();
        // Watcher changes are sent even between movement updates. Interpolate within the reported tick.
        float downTime = downedTicks < 0 ? -1
            : Math.min(EntityAyanami.DOWNED_TICKS, downedTicks + MathHelper.clamp_float(partialTick, 0, 1));
        float riseStart = EntityAyanami.DOWNED_TICKS - EntityAyanami.RISE_TICKS;
        float tilt = downTime < 0 ? 0
            : smooth(downTime / EntityAyanami.FALL_TICKS)
                * (1 - smooth((downTime - riseStart) / EntityAyanami.RISE_TICKS));
        // Lose focus early in the fall, and recover it near the end of getting up.
        float unfocused = downTime < 0 ? 0
            : smooth(downTime / 4F) * (1 - smooth((downTime - (EntityAyanami.DOWNED_TICKS - 6)) / 6F));
        AyanamiRig.Gaze gaze = unfocused == 1 ? null : gazeTarget(npc, modelYaw, partialTick);
        float eyeClosure = unfocused == 1 ? 0 : npc.getEyeClosure(partialTick) * (1 - unfocused);

        bindEntityTexture(npc);
        GL11.glPushMatrix();
        try {
            GL11.glTranslated(x, y, z);
            // The OBJ's face is toward -Z; Minecraft yaw zero faces +Z.
            GL11.glRotatef(modelYaw, 0, 1, 0);
            GL11.glScalef(BODY_SCALE, BODY_SCALE, BODY_SCALE);
            if (downedTicks >= 0) {
                if (tilt > 0) {
                    float angle = 90F * tilt;
                    GL11.glTranslatef(0, AyanamiModelRenderer.backFallLift(angle), 0);
                    // Facing -Z, positive pitch lays the back down along +Z.
                    GL11.glRotatef(angle, 1, 0, 0);
                }
            } else if (npc.deathTime > 0) {
                float fall = MathHelper.sqrt_float(Math.max(0, (npc.deathTime + partialTick - 1) / 20F * 1.6F));
                GL11.glRotatef(Math.min(1, fall) * 90, 0, 0, 1);
            }
            AyanamiModelRenderer.render(
                action,
                elapsed,
                partialTick,
                stride,
                aimYaw,
                aimPitch,
                gaze,
                eyeClosure,
                unfocused,
                heldItem == null ? null : () -> renderHeldItem(npc, heldItem));
        } finally {
            GL11.glPopMatrix();
        }
        if (downedTicks < 0) AyanamiSpeechBubble.render(npc, AyanamiDialogueClient.speechFor(npc), x, y, z);
    }

    private void renderHeldItem(EntityAyanami npc, ItemStack stack) {
        Item item = stack.getItem();
        if (item == null) return;
        GL11.glPushAttrib(
            GL11.GL_ENABLE_BIT | GL11.GL_CURRENT_BIT
                | GL11.GL_TEXTURE_BIT
                | GL11.GL_COLOR_BUFFER_BIT
                | GL11.GL_DEPTH_BUFFER_BIT
                | GL11.GL_TRANSFORM_BIT);
        try {
            // Keep items at their own size; the already-positioned hand socket still follows the larger body.
            GL11.glScalef(1F / BODY_SCALE, 1F / BODY_SCALE, 1F / BODY_SCALE);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_NORMALIZE);
            if (item instanceof ItemLonginus) {
                GL11.glRotatef(AyanamiRig.LONGINUS_GRIP_PITCH, 1, 0, 0);
                LonginusModel.render(HELD_LONGINUS_LENGTH, LonginusModel.GRIP_Y);
                return;
            }
            // Convert the Y-up OBJ hand into vanilla's held-item basis, then use the usual equip transforms.
            GL11.glRotatef(180, 0, 0, 1);
            GL11.glScalef(0.82F, 0.82F, 0.82F);
            IItemRenderer custom = MinecraftForgeClient.getItemRenderer(stack, IItemRenderer.ItemRenderType.EQUIPPED);
            boolean customBlock = custom != null && custom.shouldUseRenderHelper(
                IItemRenderer.ItemRenderType.EQUIPPED,
                stack,
                IItemRenderer.ItemRendererHelper.BLOCK_3D);
            if (item instanceof ItemBlock && (customBlock || RenderBlocks.renderItemIn3d(
                Block.getBlockFromItem(item)
                    .getRenderType()))) {
                GL11.glTranslatef(0, 0.1875F, -0.3125F);
                GL11.glRotatef(20, 1, 0, 0);
                GL11.glRotatef(45, 0, 1, 0);
                GL11.glScalef(-0.375F, -0.375F, 0.375F);
            } else if (item == Items.bow) {
                GL11.glTranslatef(0, 0.125F, 0.3125F);
                GL11.glRotatef(-20, 0, 1, 0);
                renderToolBasis();
            } else if (item.isFull3D()) {
                if (item.shouldRotateAroundWhenRendering()) {
                    GL11.glRotatef(180, 0, 0, 1);
                    GL11.glTranslatef(0, -0.125F, 0);
                }
                GL11.glTranslatef(0, 0.1875F, 0);
                renderToolBasis();
            } else {
                GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
                GL11.glScalef(0.375F, 0.375F, 0.375F);
                GL11.glRotatef(60, 0, 1, 0);
                GL11.glRotatef(-90, 1, 0, 0);
                GL11.glRotatef(20, 0, 0, 1);
            }
            int passes = item.requiresMultipleRenderPasses() ? item.getRenderPasses(stack.getItemDamage()) : 1;
            for (int pass = 0; pass < passes; pass++) {
                int rgb = item.getColorFromItemStack(stack, pass);
                GL11.glColor4f((rgb >> 16 & 255) / 255F, (rgb >> 8 & 255) / 255F, (rgb & 255) / 255F, 1);
                renderManager.itemRenderer.renderItem(npc, stack, pass);
            }
        } finally {
            GL11.glPopAttrib();
        }
    }

    private static void renderToolBasis() {
        GL11.glScalef(0.625F, -0.625F, 0.625F);
        GL11.glRotatef(-100, 1, 0, 0);
        GL11.glRotatef(45, 0, 1, 0);
    }

    private static AyanamiRig.Gaze gazeTarget(EntityAyanami npc, float modelYaw, float partialTick) {
        EntityPlayer target = npc.getFocusedPlayer();
        if (target == null || target.isPlayerSleeping() || npc.deathTime > 0) return null;
        double x = lerp(target.lastTickPosX - npc.lastTickPosX, target.posX - npc.posX, partialTick);
        double y = lerp(target.lastTickPosY - npc.lastTickPosY, target.posY - npc.posY, partialTick);
        double z = lerp(target.lastTickPosZ - npc.lastTickPosZ, target.posZ - npc.posZ, partialTick);
        // Local and remote 1.7.10 player entities store different posY and default eye heights.
        y += 1.62F - target.yOffset + (target.getEyeHeight() - target.getDefaultEyeHeight());
        return AyanamiRig.Gaze.fromWorldOffset(x / BODY_SCALE, y / BODY_SCALE, z / BODY_SCALE, modelYaw);
    }

    private static float smooth(float t) {
        t = MathHelper.clamp_float(t, 0, 1);
        return t * t * (3 - 2 * t);
    }

    private static double lerp(double from, double to, float t) {
        return from + (to - from) * t;
    }

    private static float lerp(float from, float to, float t) {
        return from + (to - from) * t;
    }

    private static float interpolateAngle(float from, float to, float t) {
        return from + MathHelper.wrapAngleTo180_float(to - from) * t;
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return AyanamiModelRenderer.TEXTURE;
    }
}
