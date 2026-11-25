package com.newmaa.othtech.common.blocks;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class OTEModelAyanami extends TileEntity {

    private static final String ACTION_TAG = "AyanamiAction";
    private static final String ACTION_START_TICK_TAG = "AyanamiActionStartTick";

    private AyanamiAction action = AyanamiAction.STANDING;
    private long actionStartTick;

    public AyanamiAction getAction() {
        return action;
    }

    public long getActionStartTick() {
        return actionStartTick;
    }

    void cycleAction() {
        if (worldObj == null || worldObj.isRemote) return;

        action = action.next();
        actionStartTick = worldObj.getTotalWorldTime();
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        writeAction(nbt);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        readAction(nbt);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        // The head is above the block and the raised arm can extend past any side.
        return AxisAlignedBB.getBoundingBox(xCoord - 0.5, yCoord, zCoord - 0.5, xCoord + 1.5, yCoord + 2, zCoord + 1.5);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        writeAction(nbt);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        readAction(packet.func_148857_g());
    }

    private void writeAction(NBTTagCompound nbt) {
        nbt.setString(ACTION_TAG, action.getId());
        nbt.setLong(ACTION_START_TICK_TAG, actionStartTick);
    }

    private void readAction(NBTTagCompound nbt) {
        action = AyanamiAction.fromId(nbt.getString(ACTION_TAG));
        actionStartTick = Math.max(0L, nbt.getLong(ACTION_START_TICK_TAG));
    }
}
