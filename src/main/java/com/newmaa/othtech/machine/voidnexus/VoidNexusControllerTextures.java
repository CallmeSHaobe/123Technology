package com.newmaa.othtech.machine.voidnexus;

import net.minecraftforge.common.util.ForgeDirection;

import com.newmaa.othtech.common.blocks.nailong.NailongCasingMeta;
import com.newmaa.othtech.common.blocks.nailong.NailongCasings;

import gregtech.api.enums.Textures.BlockIcons;
import gregtech.api.interfaces.ITexture;
import gregtech.api.render.TextureFactory;
import gregtech.client.iconContainers.blocks.GTCustomAlphaBlockIconContainer;

public final class VoidNexusControllerTextures {

    public static final VoidNexusControllerTextures MATRIX = new VoidNexusControllerTextures("matrix");
    public static final VoidNexusControllerTextures MODULE = new VoidNexusControllerTextures("controller");

    private final ITexture idleFront;
    private final ITexture activeFront;
    private final ITexture activeGlow;

    private VoidNexusControllerTextures(String prefix) {
        idleFront = front(prefix + "_off", false);
        activeFront = front(prefix + "_on", false);
        activeGlow = front(prefix + "_on_glow", true);
    }

    private static ITexture front(String name, boolean glow) {
        // Draw after the casing within its translucent render pass.
        var texture = TextureFactory.builder()
            .addIcon(GTCustomAlphaBlockIconContainer.create("123technology", "void_nexus/" + name))
            .extFacing();
        return (glow ? texture.glow() : texture).build();
    }

    public ITexture[] get(ForgeDirection side, ForgeDirection facing, boolean active) {
        ITexture casing = BlockIcons.getCasingTextureForId(
            NailongCasings.nailongCasing.getTextureIndex(NailongCasingMeta.PSYCHIC_WRAPPED_CASING));
        if (side != facing) return new ITexture[] { casing };
        return active ? new ITexture[] { casing, activeFront, activeGlow } : new ITexture[] { casing, idleFront };
    }
}
