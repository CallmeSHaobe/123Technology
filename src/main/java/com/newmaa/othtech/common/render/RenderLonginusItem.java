package com.newmaa.othtech.common.render;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.newmaa.othtech.common.item.itemWeapons.ItemLonginus;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class RenderLonginusItem implements IItemRenderer {

    private static final float INVENTORY_SCALE = 4F;

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return switch (type) {
            case INVENTORY, EQUIPPED, EQUIPPED_FIRST_PERSON, ENTITY -> true;
            default -> false;
        };
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return switch (type) {
            case ENTITY -> helper == ItemRendererHelper.ENTITY_BOBBING || helper == ItemRendererHelper.ENTITY_ROTATION;
            case EQUIPPED -> helper == ItemRendererHelper.EQUIPPED_BLOCK || helper == ItemRendererHelper.BLOCK_3D;
            case EQUIPPED_FIRST_PERSON -> helper == ItemRendererHelper.EQUIPPED_BLOCK;
            default -> false;
        };
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();
        try {
            switch (type) {
                case INVENTORY -> {
                    // Forge's flat inventory hook centers the model at (8, 8), with Y pointing down.
                    GL11.glTranslatef(8F, 8F, 0F);
                    GL11.glRotatef(-135F, 0F, 0F, 1F);
                    LonginusModel.render(18F * INVENTORY_SCALE, LonginusModel.CENTER_Y);
                }
                case EQUIPPED -> {
                    // Undo Forge's block offset and RenderPlayer's block transform to reach the hand anchor.
                    GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                    GL11.glScalef(-1F / 0.375F, -1F / 0.375F, 1F / 0.375F);
                    GL11.glRotatef(-45F, 0F, 1F, 0F);
                    GL11.glRotatef(-20F, 1F, 0F, 0F);
                    GL11.glTranslatef(0F, -0.1875F, 0.3125F);
                    float gripPitch = -80F;
                    if (data.length > 1 && data[1] instanceof EntityPlayer player && player.getItemInUseCount() > 0) {
                        // The throwing pose pulls the arm back. Counter that pitch to keep the tip pointed ahead.
                        gripPitch = 34F + 31.5F * getCharge(player);
                    }
                    GL11.glRotatef(gripPitch, 1F, 0F, 0F);
                    LonginusModel.render(LonginusModel.WORLD_LENGTH, LonginusModel.GRIP_Y);
                }
                case EQUIPPED_FIRST_PERSON -> renderFirstPerson(item);
                case ENTITY -> {
                    GL11.glTranslatef(0F, 0.35F, 0F);
                    GL11.glRotatef(65F, 0F, 0F, 1F);
                    LonginusModel.render(1.45F, LonginusModel.CENTER_Y);
                }
                default -> {}
            }
        } finally {
            GL11.glPopMatrix();
        }
    }

    private static void renderFirstPerson(ItemStack item) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        float draw = 0F;
        if (player != null && player.getItemInUse() == item) {
            draw = getCharge(player);
            draw = draw * draw * (3F - 2F * draw);
        }

        // Start from the vanilla first-person transform, then raise and pull the spear back to aim.
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        GL11.glTranslatef(0.3F, 0.25F + 0.65F * draw, -0.5F + 0.35F * draw);
        GL11.glRotatef(-35F, 0F, 1F, 0F);
        GL11.glRotatef(-28F - 50F * draw, 1F, 0F, 0F);
        GL11.glRotatef(14F * (1F - draw), 0F, 0F, 1F);
        LonginusModel.render(LonginusModel.WORLD_LENGTH, LonginusModel.GRIP_Y);
    }

    private static float getCharge(EntityPlayer player) {
        return MathHelper.clamp_float(player.getItemInUseDuration() / (float) ItemLonginus.CHARGE_TICKS, 0F, 1F);
    }
}
