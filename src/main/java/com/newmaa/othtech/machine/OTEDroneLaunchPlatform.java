package com.newmaa.othtech.machine;

import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.common.entity.DroneDownlinkTargets;
import com.newmaa.othtech.common.entity.EntitySuicideDrone;
import com.newmaa.othtech.common.item.itemMisc.ItemSuicideDrone;
import com.newmaa.othtech.common.render.RenderDrone;
import com.newmaa.othtech.machine.gui.OTEDroneLaunchPlatformGui;
import com.newmaa.othtech.machine.machineclass.OTHMultiMachineBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.casing.Casings;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.shutdown.ShutDownReason;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gregtech.common.render.IMTERenderer;
import gregtech.common.tileentities.machines.MTEHatchInputBusME;
import io.netty.buffer.ByteBuf;

public class OTEDroneLaunchPlatform extends OTHMultiMachineBase<OTEDroneLaunchPlatform> implements IMTERenderer {

    // ===================== Tuning Constants =====================

    /** 并行数 */
    private static final int MAX_PARALLEL = 1;

    /** 速度倍率（1F = 100% 耗时） */
    private static final float SPEED_BONUS = 1F;

    /** EU 消耗倍率（1F = 100% 耗电） */
    private static final float EU_MODIFIER = 1F;

    /** 是否无损超频 */
    private static final boolean PERFECT_OVERCLOCK = false;

    /** 每秒污染 */
    private static final int POLLUTION_PER_SECOND = 0;

    /** 两次发射间隔（tick）。 */
    private static final int LAUNCH_INTERVAL = 100;
    private static final int WORLD_LIMIT = 29999900;

    // ===================== Constructors =====================

    public OTEDroneLaunchPlatform(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTEDroneLaunchPlatform(String aName) {
        super(aName);
    }

    // ===================== NBT =====================

    /** 当前无人机模型类型；0 表示不显示预览。 */
    private int droneVariant;
    private int targetX;
    private int targetY = 64;
    private int targetZ;
    private boolean targetConfigured;
    private boolean launchEnabled;
    private int launchSpeedKmh = 200;
    private boolean autoDownlinkTarget;

    public boolean isAutoDownlinkTarget() {
        return autoDownlinkTarget;
    }

    public void setAutoDownlinkTarget(boolean enabled) {
        if (autoDownlinkTarget == enabled) return;
        autoDownlinkTarget = enabled;
        launchEnabled = false;
        markDirty();
    }

    public int getLaunchSpeedKmh() {
        return launchSpeedKmh;
    }

    public void setLaunchSpeedKmh(int value) {
        if (value < 50 || value > 300 || value == launchSpeedKmh) return;
        launchSpeedKmh = value;
        launchEnabled = false;
        markDirty();
    }

    public int getTargetX() {
        return targetX;
    }

    public int getTargetY() {
        return targetY;
    }

    public int getTargetZ() {
        return targetZ;
    }

    public boolean isLaunchEnabled() {
        return launchEnabled;
    }

    public void setTargetX(int value) {
        if (value < -WORLD_LIMIT || value > WORLD_LIMIT) return;
        targetX = value;
        targetChanged();
    }

    public void setTargetY(int value) {
        IGregTechTileEntity tile = getBaseMetaTileEntity();
        int height = tile == null ? 256
            : tile.getWorld()
                .getActualHeight();
        if (value < 0 || value >= height) return;
        targetY = value;
        targetChanged();
    }

    public void setTargetZ(int value) {
        if (value < -WORLD_LIMIT || value > WORLD_LIMIT) return;
        targetZ = value;
        targetChanged();
    }

    private void targetChanged() {
        targetConfigured = true;
        // Editing the destination disarms the launcher until the operator enables it again.
        launchEnabled = false;
        markDirty();
    }

    public void setLaunchEnabled(boolean enabled) {
        if (enabled) targetConfigured = true;
        launchEnabled = enabled;
        markDirty();
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new OTEDroneLaunchPlatformGui(this);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("droneVariant", droneVariant);
        aNBT.setInteger("DroneTargetX", targetX);
        aNBT.setInteger("DroneTargetY", targetY);
        aNBT.setInteger("DroneTargetZ", targetZ);
        aNBT.setBoolean("DroneTargetConfigured", targetConfigured);
        aNBT.setBoolean("DroneLaunchEnabled", launchEnabled);
        aNBT.setInteger("DroneLaunchSpeedKmh", launchSpeedKmh);
        aNBT.setBoolean("DroneAutoDownlinkTarget", autoDownlinkTarget);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        int variant = aNBT.getInteger("droneVariant");
        droneVariant = ItemSuicideDrone.isValidVariant(variant) ? variant : 0;
        targetX = Math.max(-WORLD_LIMIT, Math.min(WORLD_LIMIT, aNBT.getInteger("DroneTargetX")));
        targetY = aNBT.hasKey("DroneTargetY") ? Math.max(0, Math.min(255, aNBT.getInteger("DroneTargetY"))) : 64;
        targetZ = Math.max(-WORLD_LIMIT, Math.min(WORLD_LIMIT, aNBT.getInteger("DroneTargetZ")));
        targetConfigured = aNBT.getBoolean("DroneTargetConfigured");
        autoDownlinkTarget = aNBT.getBoolean("DroneAutoDownlinkTarget");
        launchEnabled = (targetConfigured || autoDownlinkTarget) && aNBT.getBoolean("DroneLaunchEnabled");
        launchSpeedKmh = aNBT.hasKey("DroneLaunchSpeedKmh")
            ? Math.max(50, Math.min(300, aNBT.getInteger("DroneLaunchSpeedKmh")))
            : 200;
    }

    // ===================== Machine Parameters =====================

    @Override
    protected boolean isEnablePerfectOverclock() {
        return PERFECT_OVERCLOCK;
    }

    @Override
    public int getMaxParallelRecipes() {
        return MAX_PARALLEL;
    }

    @Override
    protected float getSpeedBonus() {
        return SPEED_BONUS;
    }

    @Override
    protected float getEuModifier() {
        return EU_MODIFIER;
    }

    // 当前直接检查无人机物品，不使用配方表。

    @Override
    public int getPollutionPerSecond(final ItemStack aStack) {
        return POLLUTION_PER_SECOND;
    }

    // ===================== Processing Logic =====================

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        IGregTechTileEntity tile = getBaseMetaTileEntity();
        if (tile == null || !tile.isServerSide() || !launchEnabled || (!targetConfigured && !autoDownlinkTarget))
            return CheckRecipeResultRegistry.NO_RECIPE;
        int aimX = targetX, aimY = targetY, aimZ = targetZ;
        if (autoDownlinkTarget) {
            ChunkCoordinates target = DroneDownlinkTargets.findNearest(
                tile.getWorld(),
                tile.getXCoord() + 0.5D,
                tile.getYCoord() + 0.5D,
                tile.getZCoord() + 0.5D);
            if (target == null) return CheckRecipeResultRegistry.NO_RECIPE;
            aimX = target.posX;
            aimY = target.posY;
            aimZ = target.posZ;
        }
        if (aimY < 0 || aimY >= tile.getWorld()
            .getActualHeight()) return CheckRecipeResultRegistry.NO_RECIPE;
        ForgeDirection back = getExtendedFacing().getRelativeBackInWorld();
        double x = tile.getXCoord() + 0.5D + back.offsetX;
        double y = tile.getYCoord() + 1.5D;
        double z = tile.getZCoord() + 0.5D + back.offsetZ;
        for (MTEHatchInputBus bus : mInputBusses) {
            if (!bus.isValid()) continue;
            IGregTechTileEntity inventory = bus.getBaseMetaTileEntity();
            for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
                ItemStack stack = inventory.getStackInSlot(slot);
                if (stack == null || stack.stackSize <= 0 || !(stack.getItem() instanceof ItemSuicideDrone drone))
                    continue;
                EntitySuicideDrone entity = new EntitySuicideDrone(
                    tile.getWorld(),
                    x,
                    y,
                    z,
                    drone.getVariant(),
                    aimX,
                    aimY,
                    aimZ,
                    drone.getExplosionSize(),
                    drone.getEntityDamage(),
                    drone.getShockwaveRange());
                ForgeDirection launchDirection = tile.getFrontFacing();
                float launchYaw = (float) Math.toDegrees(Math.atan2(-launchDirection.offsetX, launchDirection.offsetZ));
                entity.initializeLaunch(launchYaw, launchSpeedKmh);
                // Ticket limits or canceled spawns must not consume an item or leak forced chunks.
                if (!entity.ensureFlightChunks() || !tile.getWorld()
                    .spawnEntityInWorld(entity)) {
                    entity.setDead();
                    return CheckRecipeResultRegistry.NO_RECIPE;
                }
                inventory.decrStackSize(slot, 1);
                inventory.markDirty();
                updateSlots();
                mMaxProgresstime = LAUNCH_INTERVAL;
                mEfficiency = 10000;
                mEfficiencyIncrease = 10000;
                lEUt = 0;
                markDirty();
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }
        }
        return CheckRecipeResultRegistry.NO_RECIPE;
    }

    private int findDroneVariant() {
        for (ItemStack stack : getStoredInputs()) {
            if (stack != null && stack.stackSize > 0 && stack.getItem() instanceof ItemSuicideDrone drone) {
                return drone.getVariant();
            }
        }
        return 0;
    }

    private void setDroneVariant(int variant) {
        if (droneVariant == variant) return;
        droneVariant = variant;
        IGregTechTileEntity tile = getBaseMetaTileEntity();
        if (tile != null && tile.isServerSide()) tile.issueTileUpdate();
    }

    @Override
    public void onPostTick(IGregTechTileEntity tile, long tick) {
        super.onPostTick(tile, tick);
        if (tile.isServerSide()) {
            if (tick % 10 == 0) setDroneVariant(mMachine ? findDroneVariant() : 0);
        }
    }

    @Override
    public void stopMachine(@NotNull ShutDownReason reason) {
        setDroneVariant(0);
        super.stopMachine(reason);
    }

    @Override
    protected boolean supportsCraftingMEBuffer() {
        return false;
    }

    @Override
    public boolean addInputBusToMachineList(IGregTechTileEntity tile, int casingIndex) {
        if (tile != null && tile.getMetaTileEntity() instanceof MTEHatchInputBusME) return false;
        return super.addInputBusToMachineList(tile, casingIndex);
    }

    // ===================== Structure =====================

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, horizontalOffSet, verticalOffSet, depthOffSet, errors)) return;
        checkHasInputBus(errors);
        repairMachine();
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, horizontalOffSet, verticalOffSet, depthOffSet);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (this.mMachine) return -1;
        return this.survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            horizontalOffSet,
            verticalOffSet,
            depthOffSet,
            elementBudget,
            env,
            false,
            true);
    }

    private static final String STRUCTURE_PIECE_MAIN = "main";

    private final int horizontalOffSet = 1;
    private final int verticalOffSet = 0;
    private final int depthOffSet = 0;

    private static IStructureDefinition<OTEDroneLaunchPlatform> STRUCTURE_DEFINITION = null;

    @Override
    public IStructureDefinition<OTEDroneLaunchPlatform> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTEDroneLaunchPlatform>builder()
                .addShape(STRUCTURE_PIECE_MAIN, shapeMain)
                .addElement(
                    'A',
                    buildHatchAdder(OTEDroneLaunchPlatform.class).atLeast(InputBus)
                        .casingIndex(Casings.SolidSteelMachineCasing.textureId)
                        .hint(1)
                        .buildAndChain(Casings.SolidSteelMachineCasing.asElement()))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    // 临时结构：3x1x3 平台，控制器位于正面中央，输入总线替换坚实钢机器方块。
    private final String[][] shapeMain = com.newmaa.othtech.utils.ShapeLoader
        .loadCompressed("/assets/123technology/structures/ote/OTEDroneLaunchPlatform_shapeMain.shape.gz");

    @Override
    protected IAlignmentLimits getInitialAlignmentLimits() {
        return (direction, rotation, flip) -> direction != ForgeDirection.UP && direction != ForgeDirection.DOWN
            && rotation.isNotRotated()
            && !flip.isVerticallyFliped();
    }

    // ===================== Tooltip =====================

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt
            // TODO: 正式机器名与 Tooltip 待定，修改中英文 .lang 中的对应键。
            .addMachineType(translateToLocal("ote.tn.drone_launch_platform"))
            .addInfo(translateToLocal("ote.tm.drone_launch_platform.0"))
            .addInfo(translateToLocal("ote.drone.info.1"))
            .addInfo(translateToLocal("ote.drone.info.2"))
            .addInfo(translateToLocal("ote.drone.info.3"))
            .addInfo(translateToLocal("ote.drone.info.4"))
            .beginStructureBlock(3, 1, 3, false)
            .addController(translateToLocal("oth.hint.front_bottom"))
            .addInputBus(translateToLocal("oth.hint.any_bottom_casing"), 1)
            .toolTipFinisher();
        return tt;
    }

    // ===================== Standard Overrides =====================

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTEDroneLaunchPlatform(this.mName);
    }

    @Override
    public boolean getDefaultHasMaintenanceChecks() {
        return false;
    }

    @Override
    public boolean supportsSingleRecipeLocking() {
        return false;
    }

    @Override
    public void writeToStream(ByteBuf buffer) {
        super.writeToStream(buffer);
        buffer.writeInt(droneVariant);
    }

    @Override
    public void readFromStream(ByteBuf buffer) {
        super.readFromStream(buffer);
        droneVariant = buffer.readInt();
    }

    // ===================== Textures =====================

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        final ITexture casing = Casings.SolidSteelMachineCasing.getCasingTexture();
        if (side != facing) return new ITexture[] { casing };
        return new ITexture[] { casing, TextureFactory.builder()
            .addIcon(
                aActive ? Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE
                    : Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE)
            .extFacing()
            .build() };
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderTESR(double x, double y, double z, float partialTicks) {
        IGregTechTileEntity tile = getBaseMetaTileEntity();
        if (tile == null || droneVariant == 0) return;
        ForgeDirection back = getExtendedFacing().getRelativeBackInWorld();
        GL11.glPushMatrix();
        try {
            GL11.glTranslated(x + 0.5 + back.offsetX, y + 1.05, z + 0.5 + back.offsetZ);
            float yaw = switch (tile.getFrontFacing()) {
                case SOUTH -> 180F;
                case WEST -> 90F;
                case EAST -> -90F;
                default -> 0F;
            };
            GL11.glRotatef(yaw, 0F, 1F, 0F);
            RenderDrone.render(droneVariant);
        } finally {
            GL11.glPopMatrix();
        }
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox(int x, int y, int z) {
        return IMTERenderer.super.getRenderBoundingBox(x, y, z).expand(6, 10, 6);
    }
}
