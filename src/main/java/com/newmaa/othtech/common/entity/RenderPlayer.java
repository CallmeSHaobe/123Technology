package com.newmaa.othtech.common.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.TwilightForestMod;
import twilightforest.client.renderer.entity.RenderTFGiantSkinportIntegration;

/**
 * 复制体渲染器：模型用 {@link SatFakePlayer}，皮肤跟本地玩家走。
 * <p>
 * 这里不能直接用原版 {@code net.minecraft.client.renderer.entity.RenderPlayer}：它会把实体强转成
 * {@code AbstractClientPlayer}，对 {@link EntityFakePlayer} 直接 CCE。
 */
@SideOnly(Side.CLIENT)
public class RenderPlayer extends RenderBiped {

    private static final ResourceLocation STEVE_SKIN = new ResourceLocation("textures/entity/steve.png");

    private final ModelRenderer normalRightArm;
    private final ModelRenderer normalLeftArm;
    private ModelRenderer slimRightArm;
    private ModelRenderer slimLeftArm;

    public RenderPlayer() {
        super(new SatFakePlayer(), 0.5F);
        // 记下默认（Steve）手臂，瘦皮肤切换回来时要还原
        this.normalRightArm = this.modelBipedMain.bipedRightArm;
        this.normalLeftArm = this.modelBipedMain.bipedLeftArm;
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        // 世界加载 / 退出时 Minecraft.thePlayer 会是 null，之前直接 take skin 会 NPE
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        if (player == null) {
            return STEVE_SKIN;
        }

        ResourceLocation skin = player.getLocationSkin();
        if (skin == null) {
            skin = STEVE_SKIN;
        }
        if (TwilightForestMod.isSkinportLoaded) {
            skin = RenderTFGiantSkinportIntegration.getSkin(player, skin, STEVE_SKIN);
        }
        return skin;
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entity, float partialTicks) {
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        boolean slim = player != null && TwilightForestMod.isSkinportLoaded
            && RenderTFGiantSkinportIntegration.isSlim(player);
        this.applyArms(slim);
    }

    /**
     * 瘦（Alex）手臂只建一次并缓存，避免之前每帧 new 两个 {@link ModelRenderer}；
     * 同时保证从瘦皮肤换回普通皮肤时手臂能还原（之前是永久替换掉的）。
     */
    private void applyArms(boolean slim) {
        if (!slim) {
            this.modelBipedMain.bipedRightArm = this.normalRightArm;
            this.modelBipedMain.bipedLeftArm = this.normalLeftArm;
            return;
        }

        if (this.slimRightArm == null) {
            this.slimRightArm = new ModelRenderer(this.modelBipedMain, 40, 16);
            this.slimRightArm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F);
            this.slimRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);

            this.slimLeftArm = new ModelRenderer(this.modelBipedMain, 40, 16);
            this.slimLeftArm.mirror = true;
            this.slimLeftArm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F);
            this.slimLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
        }

        this.modelBipedMain.bipedRightArm = this.slimRightArm;
        this.modelBipedMain.bipedLeftArm = this.slimLeftArm;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        EntityFakePlayer customPlayer = (EntityFakePlayer) entity;

        // 调整坐下时的渲染位置
        if (customPlayer.isSitting()) {
            y -= 0.6D; // 降低高度
        }

        // 保存当前 OpenGL 状态
        GL11.glPushMatrix();

        // 调整旋转（坐下时固定朝向）
        if (customPlayer.isSitting()) {
            GL11.glTranslatef((float) x, (float) y, (float) z);
            GL11.glRotatef(180.0F - yaw, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(-(float) x, -(float) y, -(float) z);
        }

        // 调用父类渲染逻辑
        super.doRender(entity, x, y, z, yaw, partialTicks);

        // 恢复 OpenGL 状态
        GL11.glPopMatrix();
    }
}
