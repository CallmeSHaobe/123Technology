package com.newmaa.othtech.common.render;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.Vertex;
import net.minecraftforge.client.model.obj.WavefrontObject;

import org.lwjgl.opengl.GL11;

import com.newmaa.othtech.common.item.itemMisc.ItemSuicideDrone;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** Client-only item renderer and shared machine-preview renderer for all drone variants. */
@SideOnly(Side.CLIENT)
public final class RenderDrone implements IItemRenderer {

    public static final int PIG = ItemSuicideDrone.PIG;
    public static final int ANGELICA = ItemSuicideDrone.ANGELICA;

    private static final float PREVIEW_EXTENT = 8.75F;
    // The propeller blades sit at this local OBJ Z in all four variants.
    private static final float PROPELLER_Z = 1.59F;
    private static final DroneModel[] MODELS = new DroneModel[ItemSuicideDrone.VARIANT_COUNT];
    private final int variant;

    /** Register one instance per drone item on the client; this constructor does not load model resources. */
    public RenderDrone(int variant) {
        if (!ItemSuicideDrone.isValidVariant(variant)) {
            throw new IllegalArgumentException("Unknown drone variant: " + variant);
        }
        this.variant = variant;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return type == ItemRenderType.ENTITY || type == ItemRenderType.INVENTORY
            || type == ItemRenderType.EQUIPPED
            || type == ItemRenderType.EQUIPPED_FIRST_PERSON;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        int matrixMode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        try {
            // Forge's 3D item helper supplies the inventory projection, entity bobbing and held-item transform.
            if (type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
                GL11.glTranslatef(0.5F, 0.25F, 0.5F);
            }
            float scale = type == ItemRenderType.ENTITY ? 0.45F : 0.65F;
            GL11.glScalef(scale, scale, scale);
            GL11.glRotatef(type == ItemRenderType.INVENTORY ? 215F : 35F, 0F, 1F, 0F);
            render(variant);
        } finally {
            GL11.glPopMatrix();
            GL11.glMatrixMode(matrixMode);
        }
    }

    /**
     * Render a registered drone variant at the caller's origin, centered on X/Z with its base at Y=0.
     * The longest model dimension is 8.75 blocks. The caller supplies world translation, facing and lighting.
     * Call on the client render thread, outside an active Tessellator batch. Unknown variants render nothing.
     * Matrix mode, model-view matrix, texture binding, color and other server GL attributes are restored.
     */
    public static void render(int variant) {
        if (!ItemSuicideDrone.isValidVariant(variant)) return;
        DroneModel model = getModel(variant);
        int matrixMode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        try {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_NORMALIZE);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glColor4f(1F, 1F, 1F, 1F);
            Minecraft.getMinecraft()
                .getTextureManager()
                .bindTexture(model.texture);
            GL11.glScalef(model.scale, model.scale, model.scale);
            GL11.glTranslatef(-model.centerX, -model.minY, -model.centerZ);
            // Angelica ends in an empty "pig.002" group with drawing mode -1. renderAll() would use that mode.
            for (GroupObject group : model.object.groupObjects) {
                if (group != null && !group.faces.isEmpty()) group.render();
            }
        } finally {
            GL11.glPopMatrix();
            GL11.glPopAttrib();
            GL11.glMatrixMode(matrixMode);
        }
    }

    /** Distance from the centered render origin to the propeller plane along positive Z. */
    public static double getPropellerOffset(int variant) {
        DroneModel model = getModel(variant);
        return (PROPELLER_Z - model.centerZ) * model.scale;
    }

    private static DroneModel getModel(int variant) {
        int index = variant - 1;
        if (MODELS[index] == null) {
            MODELS[index] = switch (variant) {
                case ItemSuicideDrone.PIG -> new DroneModel("PIG.obj", "pig.PNG");
                case ItemSuicideDrone.ANGELICA -> new DroneModel("\u5b89\u6d01\u8389\u5361.obj", "error_block_0.png");
                case ItemSuicideDrone.NUCLEAR -> new DroneModel("pig_nuclear.obj", "pig_nuclear.png");
                case ItemSuicideDrone.NVIDIA -> new DroneModel("pig_nvidia.obj", "pig_nvidia.png");
                default -> throw new IllegalArgumentException("Unknown drone variant: " + variant);
            };
        }
        return MODELS[index];
    }

    private static final class DroneModel {

        private final WavefrontObject object;
        private final ResourceLocation texture;
        private final float centerX;
        private final float minY;
        private final float centerZ;
        private final float scale;

        private DroneModel(String objName, String textureName) {
            object = new WavefrontObject(new ResourceLocation("123technology", "models/" + objName));
            // Forge ignores MTL texture bindings, so each variant binds its atlas explicitly.
            texture = new ResourceLocation("123technology", "models/" + textureName);
            float minX = Float.POSITIVE_INFINITY;
            float lowY = Float.POSITIVE_INFINITY;
            float minZ = Float.POSITIVE_INFINITY;
            float maxX = Float.NEGATIVE_INFINITY;
            float maxY = Float.NEGATIVE_INFINITY;
            float maxZ = Float.NEGATIVE_INFINITY;
            for (Vertex vertex : object.vertices) {
                minX = Math.min(minX, vertex.x);
                lowY = Math.min(lowY, vertex.y);
                minZ = Math.min(minZ, vertex.z);
                maxX = Math.max(maxX, vertex.x);
                maxY = Math.max(maxY, vertex.y);
                maxZ = Math.max(maxZ, vertex.z);
            }
            float extent = Math.max(maxX - minX, Math.max(maxY - lowY, maxZ - minZ));
            if (object.vertices.isEmpty() || !Float.isFinite(extent) || extent <= 0F) {
                throw new IllegalArgumentException("Drone model has invalid bounds: " + objName);
            }
            centerX = (minX + maxX) * 0.5F;
            minY = lowY;
            centerZ = (minZ + maxZ) * 0.5F;
            scale = PREVIEW_EXTENT / extent;
        }
    }
}
