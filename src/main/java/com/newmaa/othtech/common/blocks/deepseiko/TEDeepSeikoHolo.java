package com.newmaa.othtech.common.blocks.deepseiko;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import com.newmaa.othtech.common.render.IRiceBowlHolo;
import com.newmaa.othtech.machine.OTEDeepSeiko;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;

/**
 * 米饭全息投影的承载 TE（配合 {@link BlockDeepSeikoHolo} + {@code RiceBowlRenderer}）。
 *
 * <p>
 * 「是否在播放」直接读方块 metadata（0 = 停机 / 1 = 加工中）：metadata 变更走原版方块更新包，
 * 天然同步到客户端，不用自建网络通道。动画时钟与毫秒戳纯客户端插值，服务端不参与。
 * </p>
 *
 * <p>
 * 另外每 5 秒做一次宿主自检：控制器被拆、结构散了、甚至存档里遗留了无人认领的影块，
 * 都会被自己清掉，不会在世界里留下隐形垃圾方块。
 * </p>
 */
public class TEDeepSeikoHolo extends TileEntity implements IRiceBowlHolo {

    private static final int OWNER_NONE = Integer.MIN_VALUE;
    private static final int OWNER_CHECK_INTERVAL = 100;

    private int ownerX = OWNER_NONE;
    private int ownerY = OWNER_NONE;
    private int ownerZ = OWNER_NONE;

    private float timer = 0F;
    private float fade = 0F;
    private long lastMillis = 0L;

    private int ownerCheckCooldown = OWNER_CHECK_INTERVAL;

    /** 由机器在放下影块后立刻调用 */
    public void setOwner(int x, int y, int z) {
        ownerX = x;
        ownerY = y;
        ownerZ = z;
        markDirty();
    }

    private boolean hasOwner() {
        return ownerY != OWNER_NONE;
    }

    // region 状态（给渲染器用）
    //
    // metadata 三态：0 = 不存在/消散中，1 = 满满一碗（结构成型但没开工），2 = 吃循环（加工中）

    @Override
    public boolean isHoloRunning() {
        return getBlockMetadata() >= 1;
    }

    @Override
    public boolean isHoloEating() {
        return getBlockMetadata() >= 2;
    }

    @Override
    public float getHoloTimer() {
        return timer;
    }

    @Override
    public void setHoloTimer(float value) {
        timer = value;
    }

    @Override
    public float getHoloFade() {
        return fade;
    }

    @Override
    public void setHoloFade(float value) {
        fade = value;
    }

    @Override
    public long getHoloLastMillis() {
        return lastMillis;
    }

    @Override
    public void setHoloLastMillis(long millis) {
        lastMillis = millis;
    }

    // endregion

    // region 宿主自检

    @Override
    public void updateEntity() {
        if (worldObj == null || worldObj.isRemote) return;
        if (--ownerCheckCooldown > 0) return;
        ownerCheckCooldown = OWNER_CHECK_INTERVAL;
        if (!isOwnerAlive()) {
            worldObj.setBlockToAir(xCoord, yCoord, zCoord);
        }
    }

    private boolean isOwnerAlive() {
        // 刚放下、owner 还没写进来的那一两 tick 不要误杀
        if (!hasOwner()) return true;
        // 宿主所在区块没加载就无从判断，先留着
        if (!worldObj.blockExists(ownerX, ownerY, ownerZ)) return true;
        TileEntity tile = worldObj.getTileEntity(ownerX, ownerY, ownerZ);
        if (!(tile instanceof IGregTechTileEntity gregTile)) return false;
        IMetaTileEntity metaTile = gregTile.getMetaTileEntity();
        if (!(metaTile instanceof OTEDeepSeiko seiko) || !seiko.isStructureFormed()) return false;
        // 位置自检：投影必须正好在控制器正面 HOLO_FRONT_DISTANCE 格处（常量与机器侧共用，避免改一处漏一处）。
        // 改过距离/朝向以后，老位置留下来的影块会在下一次自检时被清掉（否则会出现"两个碗"）。
        net.minecraftforge.common.util.ForgeDirection front = gregTile.getFrontFacing();
        int expectX = ownerX + front.offsetX * OTEDeepSeiko.HOLO_FRONT_DISTANCE;
        int expectZ = ownerZ + front.offsetZ * OTEDeepSeiko.HOLO_FRONT_DISTANCE;
        int expectY = ownerY + front.offsetY * OTEDeepSeiko.HOLO_FRONT_DISTANCE;
        int expectMouthY = expectY - OTEDeepSeiko.HOLO_DROP;
        if (xCoord != expectX || zCoord != expectZ) return false;
        return yCoord == (front.offsetY == 0 ? expectMouthY : expectY);
    }

    // endregion

    // region 渲染范围

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        // 碗的包围盒 x,z ∈ [-1.4,1.4] / y ∈ [0,2.2]，罩住即可
        return AxisAlignedBB
            .getBoundingBox(xCoord - 2.5D, yCoord - 1.5D, zCoord - 2.5D, xCoord + 3.5D, yCoord + 7.0D, zCoord + 3.5D);
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return 65536.0D;
    }

    // endregion

    // region NBT

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if (hasOwner()) {
            nbt.setIntArray("dsHoloOwner", new int[] { ownerX, ownerY, ownerZ });
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        int[] owner = nbt.getIntArray("dsHoloOwner");
        if (owner.length == 3) {
            ownerX = owner[0];
            ownerY = owner[1];
            ownerZ = owner[2];
        }
    }

    // endregion
}
