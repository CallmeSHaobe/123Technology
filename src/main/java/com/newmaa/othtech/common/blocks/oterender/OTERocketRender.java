package com.newmaa.othtech.common.blocks.oterender;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.Constants.NBT;

public class OTERocketRender extends TileEntity {

    private ItemStack displayStack;
    private boolean hasOwner;
    private int ownerX;
    private int ownerY;
    private int ownerZ;

    public ItemStack getDisplayStack() {
        return displayStack;
    }

    public boolean isUnclaimed() {
        return !hasOwner;
    }

    public boolean isOwnedBy(int x, int y, int z) {
        return hasOwner && ownerX == x && ownerY == y && ownerZ == z;
    }

    public void setDisplay(int x, int y, int z, ItemStack stack) {
        boolean ownerChanged = !isOwnedBy(x, y, z);
        boolean stackChanged = displayStack == null || !displayStack.isItemEqual(stack)
            || !ItemStack.areItemStackTagsEqual(displayStack, stack);
        if (!ownerChanged && !stackChanged) return;

        hasOwner = true;
        ownerX = x;
        ownerY = y;
        ownerZ = z;
        if (stackChanged) {
            displayStack = stack.copy();
            displayStack.stackSize = 1;
        }
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return 65536;
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        writeDisplay(nbt);
        if (hasOwner) {
            nbt.setBoolean("hasOwner", true);
            nbt.setInteger("ownerX", ownerX);
            nbt.setInteger("ownerY", ownerY);
            nbt.setInteger("ownerZ", ownerZ);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        readDisplay(nbt);
        hasOwner = nbt.getBoolean("hasOwner");
        ownerX = nbt.getInteger("ownerX");
        ownerY = nbt.getInteger("ownerY");
        ownerZ = nbt.getInteger("ownerZ");
    }

    private void writeDisplay(NBTTagCompound nbt) {
        if (displayStack != null) nbt.setTag("displayStack", displayStack.writeToNBT(new NBTTagCompound()));
    }

    private void readDisplay(NBTTagCompound nbt) {
        displayStack = nbt.hasKey("displayStack", NBT.TAG_COMPOUND)
            ? ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("displayStack"))
            : null;
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        writeDisplay(nbt);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        readDisplay(packet.func_148857_g());
    }
}
