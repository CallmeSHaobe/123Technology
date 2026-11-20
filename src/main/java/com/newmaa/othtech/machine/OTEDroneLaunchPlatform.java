package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import com.dreammaster.block.BlockList;
import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.alignment.constructable.ChannelDataAccessor;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.common.entity.DroneDownlinkTargets;
import com.newmaa.othtech.common.entity.EntitySuicideDrone;
import com.newmaa.othtech.common.item.itemMisc.ItemSuicideDrone;
import com.newmaa.othtech.common.render.RenderDrone;
import com.newmaa.othtech.machine.gui.OTEDroneLaunchPlatformGui;
import com.newmaa.othtech.machine.machineclass.OTHMultiMachineBase;
import com.newmaa.othtech.utils.ShapeLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTechAPI;
import gregtech.api.casing.Casings;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrorRegistry;
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
    private static final int STRUCTURE_HEIGHT = 5;
    private static final String RAIL_CHANNEL = "rail";
    private static final int MIN_RAIL_PIECES = 7;
    private static final int MAX_RAIL_PIECES = 64;
    private static final int LAUNCH_HEIGHT = STRUCTURE_HEIGHT;
    // The rail sits one block above the controller. Leave clearance above its top surface.
    private static final double DRONE_RENDER_HEIGHT = 1.55D;

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
    private int formedRailPieces;
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
        ForgeDirection up = getExtendedFacing().getRelativeUpInWorld();
        double x = tile.getXCoord() + 0.5D + back.offsetX + up.offsetX * LAUNCH_HEIGHT;
        double y = tile.getYCoord() + 0.5D + back.offsetY + up.offsetY * LAUNCH_HEIGHT;
        double z = tile.getZCoord() + 0.5D + back.offsetZ + up.offsetZ * LAUNCH_HEIGHT;
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
        int rails = 0;
        while (hasRailMarker(aBaseMetaTileEntity, rails + 1)) {
            if (rails == MAX_RAIL_PIECES) {
                errors.add(StructureErrorRegistry.TOO_LONG);
                return;
            }
            if (!checkPiece(STRUCTURE_PIECE_RAIL, horizontalOffSet, verticalOffSet, -1 - rails, errors)) return;
            rails++;
        }
        if (rails < MIN_RAIL_PIECES) {
            errors.add(StructureErrorRegistry.TOO_SHORT_LENGTH);
            return;
        }
        if (!checkPiece(STRUCTURE_PIECE_END, horizontalOffSet, verticalOffSet, -1 - rails, errors)) return;
        checkHasInputBus(errors);
        if (errors.isEmpty()) {
            repairMachine();
            if (formedRailPieces != rails) {
                formedRailPieces = rails;
                if (aBaseMetaTileEntity.isServerSide()) aBaseMetaTileEntity.issueTileUpdate();
            }
        }
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, horizontalOffSet, verticalOffSet, depthOffSet);
        int rails = requestedRailPieces(stackSize);
        for (int i = 0; i < rails; i++) {
            buildPiece(STRUCTURE_PIECE_RAIL, stackSize, hintsOnly, horizontalOffSet, verticalOffSet, -1 - i);
        }
        buildPiece(STRUCTURE_PIECE_END, stackSize, hintsOnly, horizontalOffSet, verticalOffSet, -1 - rails);
    }

    private static int requestedRailPieces(ItemStack blueprint) {
        int requested = blueprint == null ? MIN_RAIL_PIECES
            : ChannelDataAccessor.getChannelData(blueprint, RAIL_CHANNEL);
        return Math.max(MIN_RAIL_PIECES, Math.min(MAX_RAIL_PIECES, requested));
    }

    /** The magnetic block distinguishes a rail slice from the solid end cap without probing hatch adders twice. */
    private boolean hasRailMarker(IGregTechTileEntity tile, int depth) {
        int[] xyz = new int[3];
        getExtendedFacing().getWorldOffset(new int[] { 0, 0, depth }, xyz);
        int x = tile.getXCoord() + xyz[0];
        int y = tile.getYCoord() + xyz[1];
        int z = tile.getZCoord() + xyz[2];
        return tile.getWorld()
            .blockExists(x, y, z)
            && tile.getWorld()
                .getBlock(x, y, z) == GregTechAPI.sBlockMetal5
            && tile.getWorld()
                .getBlockMetadata(x, y, z) == 1;
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        int rails = requestedRailPieces(stackSize);
        int built = 0;
        for (int depth = 0; depth <= rails + 1; depth++) {
            String piece = depth == 0 ? STRUCTURE_PIECE_MAIN
                : depth == rails + 1 ? STRUCTURE_PIECE_END : STRUCTURE_PIECE_RAIL;
            int result = survivalBuildPiece(
                piece,
                stackSize,
                horizontalOffSet,
                verticalOffSet,
                -depth,
                elementBudget - built,
                env,
                false,
                false);
            if (result >= 0) {
                built += result;
                checkStructure(true, getBaseMetaTileEntity());
                return built;
            }
        }
        checkStructure(true, getBaseMetaTileEntity());
        return -1;
    }

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String STRUCTURE_PIECE_RAIL = "rail";
    private static final String STRUCTURE_PIECE_END = "end";

    private final int horizontalOffSet = 1;
    private final int verticalOffSet = STRUCTURE_HEIGHT - 1;
    private final int depthOffSet = 0;

    private static IStructureDefinition<OTEDroneLaunchPlatform> STRUCTURE_DEFINITION = null;

    @Override
    public IStructureDefinition<OTEDroneLaunchPlatform> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTEDroneLaunchPlatform>builder()
                // NEI's auto-detection stops at identical sizes 1 and 2 (both clamp to seven rails).
                .addChannel(RAIL_CHANNEL, MIN_RAIL_PIECES, MAX_RAIL_PIECES)
                .addShape(STRUCTURE_PIECE_MAIN, shapeMain)
                .addShape(STRUCTURE_PIECE_RAIL, shapeRail)
                .addShape(STRUCTURE_PIECE_END, shapeEnd)
                .addElement(
                    'A',
                    buildHatchAdder(OTEDroneLaunchPlatform.class).atLeast(InputBus)
                        .casingIndex(Casings.SolidSteelMachineCasing.textureId)
                        .hint(1)
                        .buildAndChain(Casings.SolidSteelMachineCasing.asElement()))
                .addElement('B', Casings.IronFence.asElement())
                .addElement('C', ofBlock(GregTechAPI.sBlockMetal5, 1))
                .addElement('D', ofBlock(BlockList.TungstenSteelBars.block, 0))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    // The scan is stored depth-first, with each slice ordered from top to bottom.
    private final String[][] shapeMain = ShapeLoader
        .loadCompressed("/assets/123technology/structures/ote/OTEDroneLaunchPlatform_shapeMain.shape.gz");
    private final String[][] shapeRail = ShapeLoader
        .loadCompressed("/assets/123technology/structures/ote/OTEDroneLaunchPlatform_shapeRail.shape.gz");
    private final String[][] shapeEnd = ShapeLoader
        .loadCompressed("/assets/123technology/structures/ote/OTEDroneLaunchPlatform_shapeEnd.shape.gz");

    @Override
    protected IAlignmentLimits getInitialAlignmentLimits() {
        return (direction, rotation, flip) -> direction != ForgeDirection.UNKNOWN && direction.offsetY == 0
            && !rotation.isUpsideDown()
            && !flip.isVerticallyFliped();
    }

    // ===================== Tooltip =====================

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(translateToLocal("ote.tt.drone_launch_platform"))
            .addInfo(EnumChatFormatting.RED + translateToLocal("ote.tm.drone_launch_platform.0"))
            .addInfo(translateToLocal("ote.drone.info.1"))
            .addInfo(translateToLocal("ote.drone.info.2"))
            .addInfo(translateToLocal("ote.drone.info.3"))
            .addInfo(translateToLocal("ote.drone.info.4"))
            .addInfo(translateToLocal("ote.drone.info.rail_length"))
            .addSeparator()
            .addInfo("§aAdded by: 123Technology - SuicideDronePlatform")
            .beginVariableStructureBlock(
                3,
                3,
                STRUCTURE_HEIGHT,
                STRUCTURE_HEIGHT,
                MIN_RAIL_PIECES + 2,
                MAX_RAIL_PIECES + 2,
                false)
            .addController(translateToLocal("oth.hint.front_bottom"))
            .addInputBus(translateToLocal("ote.drone.hint.input_bus"), 1)
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
        buffer.writeByte(formedRailPieces);
    }

    @Override
    public void readFromStream(ByteBuf buffer) {
        super.readFromStream(buffer);
        droneVariant = buffer.readInt();
        formedRailPieces = buffer.readUnsignedByte();
    }

    // ===================== Textures =====================
    private static IIconContainer ScreenON;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister aBlockIconRegister) {
        ScreenON = Textures.BlockIcons.custom("123technology:iconSetOTH/fire");
        super.registerIcons(aBlockIconRegister);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        final ITexture casing = Casings.SolidSteelMachineCasing.getCasingTexture();
        if (side != facing) return new ITexture[] { casing };
        return new ITexture[] { casing, TextureFactory.builder()
            .addIcon(ScreenON)
            .extFacing()
            .build() };
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderTESR(double x, double y, double z, float partialTicks) {
        IGregTechTileEntity tile = getBaseMetaTileEntity();
        if (tile == null || formedRailPieces < MIN_RAIL_PIECES || !ItemSuicideDrone.isValidVariant(droneVariant))
            return;
        ForgeDirection back = getExtendedFacing().getRelativeBackInWorld();
        ForgeDirection up = getExtendedFacing().getRelativeUpInWorld();
        // Depth zero is the controller. The fixed end cap follows the last repeatable rail slice.
        double renderDepth = formedRailPieces + 1 - RenderDrone.getPropellerOffset(droneVariant);
        GL11.glPushMatrix();
        try {
            GL11.glTranslated(
                x + 0.5D + back.offsetX * renderDepth + up.offsetX * DRONE_RENDER_HEIGHT,
                y + 0.5D + back.offsetY * renderDepth + up.offsetY * DRONE_RENDER_HEIGHT,
                z + 0.5D + back.offsetZ * renderDepth + up.offsetZ * DRONE_RENDER_HEIGHT);
            // The OBJ's propeller is on +Z. Align +Z with the structure's back.
            float yaw = switch (back) {
                case NORTH -> 180F;
                case EAST -> 90F;
                case WEST -> -90F;
                default -> 0F;
            };
            GL11.glRotatef(yaw, 0F, 1F, 0F);
            int upAlongModelX = up.offsetX * back.offsetZ - up.offsetZ * back.offsetX;
            if (upAlongModelX != 0) GL11.glRotatef(-90F * upAlongModelX, 0F, 0F, 1F);
            RenderDrone.render(droneVariant);
        } finally {
            GL11.glPopMatrix();
        }
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox(int x, int y, int z) {
        AxisAlignedBB bounds = IMTERenderer.super.getRenderBoundingBox(x, y, z);
        if (formedRailPieces < MIN_RAIL_PIECES) return bounds;
        ForgeDirection back = getExtendedFacing().getRelativeBackInWorld();
        int endDepth = formedRailPieces + 1;
        return bounds.addCoord(back.offsetX * endDepth, back.offsetY * endDepth, back.offsetZ * endDepth)
            .expand(3, 4, 3);
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        // GT measures distance from the controller, even when the model is at the far end.
        double distance = Math.sqrt(IMTERenderer.super.getMaxRenderDistanceSquared()) + formedRailPieces;
        return distance * distance;
    }
}
