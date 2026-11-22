package com.newmaa.othtech.common.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import roadhog360.simpleskinbackport.SimpleSkinBackport;
import roadhog360.simpleskinbackport.core.DefaultPlayerSkin;
import roadhog360.simpleskinbackport.ducks.IArmsState;
import roadhog360.simpleskinbackport.ducks.INewBipedModel;
import twilightforest.TwilightForestMod;
import twilightforest.client.renderer.entity.RenderTFGiantSkinportIntegration;

@SideOnly(Side.CLIENT)
public class RenderPlayer extends RenderBiped {

    private static final ResourceLocation STEVE_SKIN = new ResourceLocation("textures/entity/steve.png");
    private static final double SITTING_OFFSET = 0.6D;

    private final ModelRenderer normalRightArm;
    private final ModelRenderer normalLeftArm;
    private final ModelRenderer slimRightArm;
    private final ModelRenderer slimLeftArm;

    public RenderPlayer() {
        super(new SatFakePlayer(), 0.5F);
        normalRightArm = modelBipedMain.bipedRightArm;
        normalLeftArm = modelBipedMain.bipedLeftArm;
        slimRightArm = createSlimArm(false);
        slimLeftArm = createSlimArm(true);
    }

    private ModelRenderer createSlimArm(boolean left) {
        ModelRenderer arm = new ModelRenderer(modelBipedMain, 40, 16);
        arm.mirror = left;
        arm.addBox(left ? -1.0F : -2.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F);
        arm.setRotationPoint(left ? 5.0F : -5.0F, 2.0F, 0.0F);
        return arm;
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityLiving entity) {
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        ResourceLocation skin = player == null ? null : player.getLocationSkin();

        if (Loader.isModLoaded(SimpleSkinBackport.MOD_ID) && modelBipedMain instanceof INewBipedModel model) {
            // This mob is not an EntityPlayer, so SimpleSkinBackport does not upgrade its model automatically.
            model.ssb$set64x();
            // getLocationSkin() above also updates the arm type when the player uses a default skin.
            model.ssb$setSlim(skin != null && player instanceof IArmsState arms && arms.ssb$isSlim());
            return skin != null ? skin : DefaultPlayerSkin.STEVE.getResource();
        }

        boolean useSkinport = player != null && TwilightForestMod.isSkinportLoaded;
        boolean slim = useSkinport && RenderTFGiantSkinportIntegration.isSlim(player);
        modelBipedMain.bipedRightArm = slim ? slimRightArm : normalRightArm;
        modelBipedMain.bipedLeftArm = slim ? slimLeftArm : normalLeftArm;

        if (skin == null) skin = STEVE_SKIN;
        return useSkinport ? RenderTFGiantSkinportIntegration.getSkin(player, skin, STEVE_SKIN) : skin;
    }

    @Override
    public void doRender(EntityLiving entity, double x, double y, double z, float yaw, float partialTicks) {
        if (!(entity instanceof EntityFakePlayer fakePlayer) || !fakePlayer.isSitting()) {
            super.doRender(entity, x, y, z, yaw, partialTicks);
            return;
        }

        double sittingY = y - SITTING_OFFSET;
        GL11.glPushMatrix();
        try {
            GL11.glTranslated(x, sittingY, z);
            GL11.glRotatef(180.0F - yaw, 0.0F, 1.0F, 0.0F);
            GL11.glTranslated(-x, -sittingY, -z);
            super.doRender(entity, x, sittingY, z, yaw, partialTicks);
        } finally {
            GL11.glPopMatrix();
        }
    }
}
