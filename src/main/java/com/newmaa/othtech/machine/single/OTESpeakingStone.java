package com.newmaa.othtech.machine.single;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import com.newmaa.othtech.machine.machineclass.OTHSingleMachineBase;

import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMapBuilder;
import gregtech.api.render.TextureFactory;

public final class OTESpeakingStone extends OTHSingleMachineBase {

    // GT5U requires a nonzero UI capacity even for a placeholder map. The stone itself has no I/O slots.
    private static final RecipeMap<?> NO_RECIPES = RecipeMapBuilder.of("otht.recipe.speaking_stone")
        .maxIO(1, 1, 0, 0)
        .disableRegisterNEI()
        .build();
    private static final ITexture[] STONE = { TextureFactory.of(Blocks.stone, 0, ForgeDirection.NORTH) };
    private static final ITexture[] TOP = { TextureFactory.of(Blocks.jukebox, 0, ForgeDirection.UP) };

    private boolean broadcasting;

    public OTESpeakingStone(int id, String name, String regionalName, String[] description) {
        super(
            id,
            name,
            regionalName,
            0,
            description,
            NO_RECIPES,
            0,
            0,
            false,
            false,
            SoundResource.NONE,
            SpecialEffects.NONE);
    }

    private OTESpeakingStone(OTESpeakingStone prototype) {
        super(prototype);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tile) {
        return new OTESpeakingStone(this);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity tile, ForgeDirection side, ForgeDirection facing, int color,
        boolean active, boolean redstone) {
        return side == ForgeDirection.UP ? TOP : STONE;
    }

    @Override
    public ITexture[][] getInventoryTextures() {
        ITexture[][] textures = new ITexture[6][];
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            textures[side.ordinal()] = side == ForgeDirection.UP ? TOP : STONE;
        }
        return textures;
    }

    @Override
    public void initDefaultModes(NBTTagCompound nbt) {
        mMainFacing = ForgeDirection.UP;
    }

    @Override
    protected boolean isValidMainFacing(ForgeDirection side) {
        return side == ForgeDirection.UP;
    }

    @Override
    public boolean isFacingValid(ForgeDirection side) {
        return side != ForgeDirection.UNKNOWN;
    }

    @Override
    public boolean isEnetInput() {
        return false;
    }

    @Override
    public int checkRecipe() {
        return DID_NOT_FIND_RECIPE;
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity tile, EntityPlayer player) {
        return false;
    }

    public boolean isBroadcasting() {
        return broadcasting;
    }

    public void setBroadcasting(boolean enabled) {
        IGregTechTileEntity tile = getBaseMetaTileEntity();
        if (tile == null || !tile.isServerSide() || broadcasting == enabled) return;
        broadcasting = enabled;
        tile.setActive(enabled);
        markDirty();
    }

    @Override
    public void onPostTick(IGregTechTileEntity tile, long tick) {
        // Keep the TE's state independent of the basic machine's recipe-processing loop.
        if (tile.isServerSide() && tile.isActive() != broadcasting) tile.setActive(broadcasting);
    }

    @Override
    public void saveNBTData(NBTTagCompound nbt) {
        super.saveNBTData(nbt);
        nbt.setBoolean("SpeakingStoneBroadcasting", broadcasting);
    }

    @Override
    public void loadNBTData(NBTTagCompound nbt) {
        super.loadNBTData(nbt);
        mMainFacing = ForgeDirection.UP;
        broadcasting = nbt.getBoolean("SpeakingStoneBroadcasting");
    }
}
