package com.newmaa.othtech.common.entity;

import net.minecraft.client.model.ModelWolf;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 三色狗共用的渲染器：模型都是 {@link ModelWolf}，区别只有一张贴图。
 * <p>
 * 贴图由原版狼皮程序化染色而成（只染低饱和的毛发像素，眼睛 / 鼻子 / 项圈保持原色），
 * 见 {@code assets/123technology/textures/entity/dog_*.png}。
 */
@SideOnly(Side.CLIENT)
public class RenderDogColored extends RenderLiving {

    public static final ResourceLocation YELLOW = new ResourceLocation("123technology:textures/entity/dog_yellow.png");
    public static final ResourceLocation RED = new ResourceLocation("123technology:textures/entity/dog_red.png");
    public static final ResourceLocation BLUE = new ResourceLocation("123technology:textures/entity/dog_blue.png");

    private final ResourceLocation texture;

    public RenderDogColored(ResourceLocation texture) {
        super(new ModelWolf(), 0.5F);
        this.texture = texture;
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return this.texture;
    }
}
