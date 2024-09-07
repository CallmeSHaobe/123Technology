package com.newmaa.othtech.machine;

import static com.github.technus.tectech.thing.casing.TT_Container_Casings.sBlockCasingsBA0;
import static com.github.technus.tectech.thing.casing.TT_Container_Casings.sBlockCasingsTT;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.newmaa.othtech.Utils.Utils.NEGATIVE_ONE;
import static gregtech.api.enums.GT_HatchElement.OutputBus;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_DTPF_OFF;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_DTPF_ON;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FUSION1_GLOW;
import static gregtech.common.misc.WirelessNetworkManager.addEUToGlobalEnergyMap;
import static gregtech.common.tileentities.machines.multi.GT_MetaTileEntity_FusionComputer.STRUCTURE_PIECE_MAIN;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.github.bartimaeusnek.bartworks.API.BorosilicateGlass;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.machine.machineclass.OTH_MultiMachineBase;
import com.newmaa.othtech.machine.machineclass.OTH_processingLogics.OTH_ProcessingLogic;

import alkalus.main.core.util.MathUtils;
import galaxyspace.core.register.GSBlocks;
import gregtech.api.GregTech_API;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IGlobalWirelessEnergy;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_HatchElementBuilder;
import gregtech.api.util.GT_Multiblock_Tooltip_Builder;
import gregtech.api.util.GT_OverclockCalculator;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_Utility;
import gregtech.common.items.GT_IntegratedCircuit_Item;
import gtPlusPlus.api.objects.data.AutoMap;
import gtPlusPlus.api.objects.minecraft.BlockPos;
import gtPlusPlus.core.block.ModBlocks;
import gtPlusPlus.core.util.minecraft.EntityUtils;
import gtPlusPlus.core.util.minecraft.PlayerUtils;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class GT_TE_EVACannon extends OTH_MultiMachineBase<GT_TE_EVACannon> implements IGlobalWirelessEnergy {

    public GT_TE_EVACannon(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_TE_EVACannon(String aName) {
        super(aName);
    }

    private UUID ownerUUID;
    private String costingWirelessEU = "0";
    private int overclockParameter = 1;

    @Override
    public ITexture[] getTexture(final IGregTechTileEntity baseMetaTileEntity, final ForgeDirection sideDirection,
        final ForgeDirection facing, final int aColorIndex, final boolean active, final boolean aRedstone) {

        if (sideDirection == facing) {
            if (active) return new ITexture[] {
                Textures.BlockIcons
                    .getCasingTextureForId(GT_Utility.getCasingTextureIndex(GregTech_API.sBlockCasings8, 5)),
                TextureFactory.builder()
                    .addIcon(OVERLAY_DTPF_ON)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FUSION1_GLOW)
                    .extFacing()
                    .build() };
            return new ITexture[] {
                Textures.BlockIcons
                    .getCasingTextureForId(GT_Utility.getCasingTextureIndex(GregTech_API.sBlockCasings8, 5)),
                TextureFactory.builder()
                    .addIcon(OVERLAY_DTPF_OFF)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_DTPF_OFF)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons
            .getCasingTextureForId(GT_Utility.getCasingTextureIndex(GregTech_API.sBlockCasings8, 5)) };
    }

    @Override
    public boolean isCorrectMachinePart(ItemStack aStack) {
        return true;
    }

    @Override
    public int getMaxEfficiency(ItemStack aStack) {
        return 10000;
    }

    @Override
    public int getDamageToComponent(ItemStack aStack) {
        return 0;
    }

    @Override
    public boolean explodesOnComponentBreak(ItemStack aStack) {
        return false;
    }

    @Override
    public boolean supportsVoidProtection() {
        return true;
    }

    @Override
    public boolean supportsInputSeparation() {
        return true;
    }

    @Override
    public boolean supportsSingleRecipeLocking() {
        return true;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_TE_EVACannon(this.mName);
    }

    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return super.addToMachineList(aTileEntity, aBaseCasingIndex)
            || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);

    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);

    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        final IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity != null) {
            tag.setString("costingWirelessEU", costingWirelessEU);
        }
    }

    @Override
    protected boolean isEnablePerfectOverclock() {
        return true;
    }

    protected int getMaxParallelRecipes() {
        return 1;
    }

    protected float getSpeedBonus() {
        return 1;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {

        return Recipemaps.Cannon;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new OTH_ProcessingLogic() {

            @Nonnull
            @Override
            protected GT_OverclockCalculator createOverclockCalculator(@Nonnull GT_Recipe recipe) {
                return GT_OverclockCalculator.ofNoOverclock(recipe);
            }
        }.setMaxParallel(1);
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        this.ownerUUID = aBaseMetaTileEntity.getOwnerUuid();
    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        setupProcessingLogic(processingLogic);

        CheckRecipeResult result = doCheckRecipe();
        // inputs are consumed at this point
        updateSlots();
        if (!result.wasSuccessful()) return result;

        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        flushOverclockParameter();
        BigInteger costingWirelessEUTemp = BigInteger.valueOf(processingLogic.getCalculatedEut())
            .multiply(BigInteger.valueOf((long) 4096 * getOverclockEUCostMultiplier()));
        costingWirelessEU = GT_Utility.formatNumbers(costingWirelessEUTemp);
        if (!addEUToGlobalEnergyMap(ownerUUID, costingWirelessEUTemp.multiply(NEGATIVE_ONE))) {
            return CheckRecipeResultRegistry.insufficientPower(costingWirelessEUTemp.longValue());
        }

        // set progress time a fixed value
        mMaxProgresstime = getProgressTime(10 * 20);
        mOutputItems = processingLogic.getOutputItems();
        mOutputFluids = processingLogic.getOutputFluids();

        return result;
    }

    private int damageEvaCannon = 0;

    private void flushOverclockParameter() {
        ItemStack items = getControllerSlot();
        if (items != null && items.getItem() instanceof GT_IntegratedCircuit_Item
            && items.getItemDamage() > 0
            && items.getItemDamage() < 2
            && items.stackSize > 0) {
            this.overclockParameter = 16;
            damageEvaCannon = 32767;
        } else {
            this.overclockParameter = 256;
            damageEvaCannon = Integer.MAX_VALUE;
        }
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        currentTip.add(
            "Current EU cost" + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + tag.getString("costingWirelessEU")
                + EnumChatFormatting.RESET
                + " EU");
    }

    private int getOverclockEUCostMultiplier() {
        return this.overclockParameter;
    }

    private int getProgressTime(int basicTickCost) {
        return basicTickCost;
    }

    @Override
    protected void setProcessingLogicPower(ProcessingLogic logic) {
        // The voltage is only used for recipe finding
        logic.setAvailableVoltage(Long.MAX_VALUE);
        logic.setAvailableAmperage(1);
        logic.setAmperageOC(false);
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        repairMachine();
        return checkPiece(STRUCTURE_PIECE_MAIN, horizontalOffSet, verticalOffSet, depthOffSet);

    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        repairMachine();
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, horizontalOffSet, verticalOffSet, depthOffSet);

    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (this.mMachine) return -1;
        int realBudget = elementBudget >= 200 ? elementBudget : Math.min(200, elementBudget * 5);

        return this.survivialBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            horizontalOffSet,
            verticalOffSet,
            depthOffSet,
            realBudget,
            env,
            false,
            true);

    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aTick % 20 == 0 && isMachineRunning()) {
            checkForEntities(aBaseMetaTileEntity, aTick);
        }
        super.onPostTick(aBaseMetaTileEntity, aTick);
    }

    private final AutoMap<BlockPos> mFrontBlockPosCache = new AutoMap<>();

    public void checkForEntities(IGregTechTileEntity aBaseMetaTileEntity, long aTime) {

        if (aTime % 100 == 0) {
            mFrontBlockPosCache.clear();
        }
        if (mFrontBlockPosCache.isEmpty()) {
            ForgeDirection tSide = aBaseMetaTileEntity.getBackFacing();
            int aTileX = aBaseMetaTileEntity.getXCoord();
            int aTileY = aBaseMetaTileEntity.getYCoord();
            int aTileZ = aBaseMetaTileEntity.getZCoord();
            boolean xFacing = tSide.offsetX != 0;
            boolean zFacing = tSide.offsetZ != 0;

            // Check Casings
            int aX = aTileX - 3;
            int aY = aTileY + 9;
            int aZ = aTileZ + 141;
            mFrontBlockPosCache.add(new BlockPos(aX, aY, aZ, aBaseMetaTileEntity.getWorld()));

        }

        AutoMap<EntityLivingBase> aEntities = getEntities(mFrontBlockPosCache, aBaseMetaTileEntity.getWorld());
        if (!aEntities.isEmpty()) {
            for (EntityLivingBase aFoundEntity : aEntities) {
                if (aFoundEntity instanceof EntityPlayer aPlayer) {
                    if (PlayerUtils.isCreative(aPlayer) || !PlayerUtils.canTakeDamage(aPlayer)) {
                        continue;
                    } else {
                        if (aFoundEntity.getHealth() > 0) {
                            EntityUtils
                                .doDamage(aFoundEntity, EVACannon, getPlayerDamageValue(aPlayer, damageEvaCannon));
                            if ((aBaseMetaTileEntity.isClientSide()) && (aBaseMetaTileEntity.isActive())) {
                                generateParticles(aFoundEntity);
                            }
                        }
                    }
                } else if (aFoundEntity.getHealth() > 0) {
                    EntityUtils.doDamage(aFoundEntity, EVACannon, damageEvaCannon);
                    if ((aBaseMetaTileEntity.isClientSide()) && (aBaseMetaTileEntity.isActive())) {
                        generateParticles(aFoundEntity);
                    }
                }
            }
        }
    }

    private static void generateParticles(EntityLivingBase aEntity) {
        BlockPos aPlayerPosBottom = EntityUtils.findBlockPosOfEntity(aEntity);
        BlockPos aPlayerPosTop = aPlayerPosBottom.getUp();
        AutoMap<BlockPos> aEntityPositions = new AutoMap<>();
        aEntityPositions.add(aPlayerPosBottom);
        aEntityPositions.add(aPlayerPosTop);
        for (int i = 0; i < 64; i++) {
            BlockPos aEffectPos = aEntityPositions.get(aEntity.height > 1f ? MathUtils.randInt(0, 1) : 0);
            float aOffsetX = MathUtils.randFloat(-0.05f, 0.35f);
            float aOffsetY = MathUtils.randFloat(-0.05f, 0.35f);
            float aOffsetZ = MathUtils.randFloat(-0.05f, 0.35f);
            aEntity.worldObj.spawnParticle(
                "yellowdust",
                aEffectPos.xPos + aOffsetX,
                aEffectPos.yPos + 0.3f + aOffsetY,
                aEffectPos.zPos + aOffsetZ,
                0.0D,
                0.0D,
                0.0D);
        }
    }

    private int getPlayerDamageValue(EntityPlayer player, int damage) {
        int armorValue = player.getTotalArmorValue();
        return Math.max(damageEvaCannon, 0);
    }

    private static AutoMap<EntityLivingBase> getEntities(AutoMap<BlockPos> aPositionsToCheck, World aWorld) {
        AutoMap<EntityLivingBase> aEntities = new AutoMap<>();
        HashSet<Chunk> aChunksToCheck = new HashSet<>();
        if (!aPositionsToCheck.isEmpty()) {
            Chunk aLocalChunk;
            for (BlockPos aPos : aPositionsToCheck) {
                aLocalChunk = aWorld.getChunkFromBlockCoords(aPos.xPos, aPos.zPos);
                aChunksToCheck.add(aLocalChunk);
            }
        }
        if (!aChunksToCheck.isEmpty()) {
            AutoMap<EntityLivingBase> aEntitiesFound = new AutoMap<>();
            for (Chunk aChunk : aChunksToCheck) {
                if (aChunk.isChunkLoaded) {
                    List[] aEntityLists = aChunk.entityLists;
                    for (List aEntitySubList : aEntityLists) {
                        for (Object aEntity : aEntitySubList) {
                            if (aEntity instanceof EntityLivingBase aPlayer) {
                                aEntitiesFound.add(aPlayer);
                            }
                        }
                    }
                }
            }
            if (!aEntitiesFound.isEmpty()) {
                for (EntityLivingBase aEntity : aEntitiesFound) {
                    BlockPos aPlayerPos = EntityUtils.findBlockPosOfEntity(aEntity);
                    for (BlockPos aBlockSpaceToCheck : aPositionsToCheck) {
                        if (aBlockSpaceToCheck.equals(aPlayerPos)) {
                            aEntities.add(aEntity);
                        }
                    }
                }
            }
        }
        return aEntities;
    }

    private static final DamageSource EVACannon = new DamageSource("otht.evaCannon").setDamageBypassesArmor();
    private static final String STRUCTURE_PIECE_MAIN = "main";

    private final int horizontalOffSet = 141;
    private final int verticalOffSet = 37;
    private final int depthOffSet = 9;
    private static IStructureDefinition<GT_TE_EVACannon> STRUCTURE_DEFINITION = null;

    @Override
    public IStructureDefinition<GT_TE_EVACannon> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<GT_TE_EVACannon>builder()
                .addShape(STRUCTURE_PIECE_MAIN, shapeMain)
                .addElement('A', BorosilicateGlass.ofBoroGlass(10))
                .addElement(
                    'C',
                    GT_HatchElementBuilder.<GT_TE_EVACannon>builder()
                        .atLeast(OutputBus)
                        .adder(GT_TE_EVACannon::addToMachineList)
                        .dot(1)
                        .casingIndex(1024 + 12)
                        .buildAndChain(GregTech_API.sBlockCasings8, 5))
                .addElement('D', ofBlock(GregTech_API.sBlockCasings8, 10))
                .addElement('E', ofBlock(GregTech_API.sBlockCasings8, 13))
                .addElement('F', ofBlock(GregTech_API.sBlockCasings8, 14))
                .addElement('G', ofBlock(sBlockCasingsBA0, 11))
                .addElement('H', ofBlock(sBlockCasingsTT, 8))
                .addElement('I', ofBlock(sBlockCasingsTT, 10))
                .addElement('J', ofBlock(sBlockCasingsTT, 12))
                .addElement('K', ofBlock(ModBlocks.blockCasings2Misc, 1))
                .addElement('L', ofBlock(ModBlocks.blockCasings6Misc, 0))
                .addElement('M', ofBlock(GSBlocks.DysonSwarmBlocks, 9))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    // Structure by NewMaa
    private final String[][] shapeMain = new String[][] { {
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                                                                                                                                                                                                                      ",
        "                                        MMMM                                                                                                                                                                                          " },
        { "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                            MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM                                                                               " },
        { "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                       ",
            "                                                                                                                                                                                                                                     ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                            MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM                                                                               " },
        { "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                            MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM                                                                               " },
        { "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                            MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM                                                                               " },
        { "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "   C   C                                                                                                                                                                                                                              ",
            "   C   C                                                                                                                                                                                                                              ",
            "   C   C                                                                                                                                                                                                                              ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                            MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM                                                                               " },
        { "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "   C   C                                                                                                                                                                                                                              ",
            "   CCCCC                                                                                                                                                                                                                              ",
            "   C   C                                                                                                                                                                                                                              ",
            "   CCCCC                                                                                                                                                                                                                              ",
            "   C   C                                                                                                                                                                                                                              ",
            "   CCCCC                                                                                                                                                                                                                              ",
            "   C   C                                                                                                                                                                                                                              ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                            MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM                                                                              " },
        { "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "   C   C                                                                                                                                                                                                                              ",
            "   CCCCC                                                                                                                                                                                                                              ",
            "   CCCCC                                                                                                                                                                                                                              ",
            " CCC   C                                                                                                                                                                                                                              ",
            " CCCCCCC                                                                                                                                                                                                                              ",
            " CCC   C                                                                                                                                                                                                                              ",
            "   CCCCC                                                                                                                                                                                                                              ",
            "   CCCCC                                                                                                                                                                                                                              ",
            "   C   C                                                                                                                                                                                                                              ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                            MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM                                                                              " },
        { "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "   C   C                                                                                                                                                                                                                              ",
            "   CCCCC                                                                                                                                                                                                                              ",
            " CCCCCCC                                                                                                                                                                                                                              ",
            " CCCCCCC                                 CC                                                             GG                                                                                                                            ",
            " CCCAAAC                                 CC                                                             GGCCCCCCC                                                                                                                     ",
            " CCCCCCC                                 CC                                                             GGCCCCCCCC                                                                                                                    ",
            " CCCAAAC                                 CC                                                             GGCCCCCCCC                                                                                                                    ",
            " CCCCCCC                                 CC                                                             GG   CCCCC                                                                                                                    ",
            " CCCCCCC                                                                                                                                                                                                                              ",
            "   CCCCC                                                                                                                                                                                                                              ",
            "   C   C                                                                                                                                                                                                                              ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                        CCCC                                                                                                                                                                    JJJ                   ",
            "                                                                                                                                                                                                           JJJJJJJJ                   ",
            "                                                                                                                                                                                                            JJJJJJ                    ",
            "                                                                                                                                                                                                             JJJJJ                    ",
            "                                                                                                                                                                                                                J                     ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                            MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM                                                                             " },
        { "                                                                                                                                                                                                                             JJJJJJJJ ",
            "                                                                                                                                                                                                                            JJJJJJJJJ ",
            "                                                                                                                                                                                                                           JJJJJJJJJJJ",
            "                                                                                                                                                                                                                          JJJJJJJJJJJJ",
            "                                                                                                                                                                                                                         JJJJJJJJJJJJJ",
            "                                                                                                                                                                                                                        JJJJJJJJJJJJJJ",
            "                                                                                                                                                                                                                       JJJJJJJJJJJJJJJ",
            "                                                                                                                                                                                                                      JJJJJJJJJJJJJJJ ",
            "                                                                                                                                                                                                                     JJJJJJJJJJJJJJJJ ",
            "                                                                                                                                                                                                                    JJJJJJJJJJJ       ",
            "                                                                                                                                                                                                                   JJJJJJJJJJJ        ",
            "                                                                                                                                                                                                                  JJJJJJJJJJJJ        ",
            "                                                                                                                                                                                                                 JJJJJJJJJJJJ         ",
            "                                                                                                                                                                                                                JJJJJJJJJJJJJ         ",
            "                                                                                                                                                                                                               JJJJJJJJJJJJJ          ",
            "                                                                                                                                                                                                              JJJJJJJJJJJJJJ          ",
            "                                                                                                                                                                                                             JJJJJJJJJJJJJJ           ",
            "                                                                                                                                                                                                            JJJJJJJJJJJJJJJ           ",
            "                                                                                                                                                                                                           JJJJJJJJJJJJJJJ            ",
            "                                  EEEEEDEEEEEEEEDEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEDLLLDEEEEEEEEEEEEEEE                                                                                        JJJJJJJJJJJJJJJJJJJJJJJJJJ            ",
            "                                 EEEEEEDEEEEEEEEDEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEDLLLDEEEEEEEEEEEEEEE                                                                                       JJJJJJJJJJJJJJJJJJJJJJJJJJ             ",
            "   C                              EEEEEDEEEEEEEEDEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEDLLLDEEEEEEEEEEEEEEE                                                                                      JJJJJJJJJJJJJJJJJJJJJJJJJJJ             ",
            "   CCCCC                                                                                                                                                                                     JJJJJJJJJJJJJJJJJJJJJJJJJJJ              ",
            "   CCCCC                                                                                                                                                                                    JJJJJJJJJJJJJJJJJJJJJJJJJJJJ              ",
            " CCCCCCC                                                                                                  CCCCCCC                                                                          JJJJJJJJJJJJJJJJJJJJJJJJJJJJ               ",
            " CCCCCCCC                              CCCCCC                                                           GGCCCCCCC                                                                         JJJJJJJJJJJJJJJJJJJJJJJJJJJJJ               ",
            "CFFFFFFCC                              CCCCCCCCCCCCDCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC                     CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC                JJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                ",
            "CFFFFFFCC                              CCCCCCCCCCCCDCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC                    CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC             JJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                ",
            "CFFFFFFCC                              CCCCCCCCCCCCDCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC                   CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC          JJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                 ",
            " CCCCCCCC                              CCCCCC                                                           GGCCCCCCCCCCCCCC                 CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC       JJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                 ",
            " CCCCCCC                                CCCC                                                               GCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                  ",
            "   CCCCC                                                                                                   GCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                  ",
            "   CCCCC                                                                                                   GCCCCCCCCCCCCCCCCCKCKKKCKKKCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                   ",
            "   C                                    CCCC                                                                CCCCCCCCCCCCCCCCCKCCCKCCCKCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC        CCCJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                   ",
            "                                        CCCC                                                                   CCCCCCCCCCCCCCKCCKCCCKCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC                 JJJJJJJJJJJJJJJJJJJJJJJJJJJJ                    ",
            "                                        CCCC                                                                      CCCCCCCCCCCKCKCCCCCKCCCCCCCCCCCCCCCCCCCCC     CCCCC                   JJJJJJJJJJJJJJJJJJJJJJJJJ                     ",
            "                                                                                                                     CCCCCCCCKCKKKCKKKCCCCCCEECCECCCCCCCCCC      CCCCC                    JJJJJJJJJJJJJJJJJJJJJJJ                     ",
            "                                                                                                                        CCCCCCCCCCCCCCCCCCCCE~EEEEECCCCCCC       CCCCC                      JJJJJJJJJJJJJJJJJJJJJJJJ                  ",
            "                                                                                                                           CCCCCCCCCCCCCCCCCCEEKKKEECCCCCC        CCCC                        JJJJJJJJJJJJJJJJJJJJJJ                  ",
            "                                                                                                                              CCCCCCCCCCCCCCCEKCCCCECCCCCC        CCCCC                         JJJJJJJJJJJJJJJJJJ                    ",
            "                                                                                                                               CCCCCCCCCCCCCEEKCKKKEECCCC          CCCC                           JJJJJJJJJJJJJJJJ                    ",
            "                                                                                                                               CCCCCCCCCCCCCCEKCCKCECCCCC          CCCCC                            JJJJJJJJJJJJJ                     ",
            "                                                                                                                                CCCCCCCCCCCCCEEKKKEECCCC            CCCC                              JJJJJJJJJJJ                     ",
            "                                                                                                                                CCCCCCCCCCCCCCEEEEECCCCC            CCCCC                               JJJJJJJJJJ                    ",
            "                                                                                                                               CCCCCCCCCCCCCCCCCECCCCCC              CCCC                                 JJJJJJJJ                    ",
            "                                                                                                                              CCCCCCCCCCCCCCCCCCCCCCCC               CCCC                                                             ",
            "                                                                                                                              CCCCCCCCCCCCCCCCCCCCCCCC                                                                                ",
            "                                                                                                                             CCCCCCCCCCCCCCCCCCCCCCCCC                                                                                ",
            "                                                                                                                             CCCCCCCCCCCCCCCCCCCCCCC                                                                                  ",
            "                                                                                                                             CCCCCCCCCCCCCCCCCCCCCCC                                                                                  ",
            "                                                                                                                             CCCCCCCCCCCCCCCCCCCCCCC                                                                                  ",
            "                            MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM                                                                             " },
        { "                                                                                                                                                                                                                             JJJJJJJJ ",
            "                                                                                                                                                                                                                            JJJJJJJJJ ",
            "                                                                                                                                                                                                                           JJJJJJJJJJJ",
            "                                                                                                                                                                                                                          JJJJJJJJJJJJ",
            "                                                                                                                                                                                                                         JJJJJJJJJJJJJ",
            "                                                                                                                                                                                                                        JJJJJJJJJJJJJJ",
            "                                                                                                                                                                                                                       JJJJJJJJJJJJJJJ",
            "                                                                                                                                                                                                                      JJJJJJJJJJJJJJJ ",
            "                                                                                                                                                                                                                     JJJJJJJJJJJJJJJJ ",
            "                                                                                                                                                                                                                    JJJJJJJJJJJ       ",
            "                                                                                                                                                                                                                   JJJJJJJJJJJ        ",
            "                                                                                                                                                                                                                  JJJJJJJJJJJJ        ",
            "                                                                                                                                                                                                                 JJJJJJJJJJJJ         ",
            "                                                                                                                                                                                                                JJJJJJJJJJJJJ         ",
            "                                                                                                                                                                                                               JJJJJJJJJJJJJ          ",
            "                                                                                                                     G                                                                                        JJJJJJJJJJJJJJ          ",
            "                                                                                                                     G                                                                                       JJJJJJJJJJJJJJ           ",
            "                                                                                                                     G                                                                                      JJJJJJJJJJJJJJJ           ",
            "                                  EEEEEDEEEEEEEEDEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEDLLLDEEEEEEEEEEEEEEEG            G                                                                                     JJJJJJJJJJJJJJJ            ",
            "                                EEEEEEEDEEEEEEEEDEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEDLLLDEEEEEEEEEEECCCCG            G                                                                          JJJJJJJJJJJJJJJJJJJJJJJJJJ            ",
            "                                EA     A        A                     A               A             AAAAG            G                                                                         JJJJJJJJJJJJJJJJJJJJJJJJJJ             ",
            "   C   C                        EEEEEEEDEEEEEEEEDEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEDLLLDEEEEEEEEEEECCCCGG           G                                                                        JJJJJJJJJJJJJJJJJJJJJJJJJJJ             ",
            "   C   C                          EEEEEDEEEEEEEEDEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEDLLLDEEEEEEEEEEECCCCGG          GG                                                                       JJJJJJJJJJJJJJJJJJJJJJJJJJJ              ",
            " CCC   C                                                                                            CC  GG CCCCCGGGGG                           CCC                                         JJJJJJJJJJJJJJJJJJJJJJJJJJJJ              ",
            " CCCAAAC                                                                                            CC  GGCCCCCCCG                          CCCCCCC                                        JJJJJJJJJJJJJJJJJJJJJJJJJJJJ               ",
            "CFFFFFFCC                              CCCCCCCCCCCCDCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC                         CCCCCCCCCCCCCCCCCCCCCCCCCCCCC                   JJJJJJJJJJJJJJJJJJJJJJJJJJJJJ               ",
            "GHHHHHHHHCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCDCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC                     CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC                JJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                ",
            "GHHHHHHHHCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC                                                                     CCCCKKKKKKKK        KKKKCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC             JJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                ",
            "GHHHHHHHHCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCDCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCKKKKKKKKKKKKKKKKKKKKCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC          JJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                 ",
            "CFFFFFFCC                              CCCCCCCCCCCCDCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCKKKKKKKKKKKKKKKKKKKKCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC       JJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                 ",
            " CCCAAAC                                CCCC                                                            GGGGCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                  ",
            " CCC   C                                CCCC                                                              GGCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                  ",
            "   C   C                                CCCC                                                              GGCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                   ",
            "   C   C                                CCCC                                                              GGCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC        CCCJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                   ",
            "                                        CCCC                                                              GG   CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC                 JJJJJJJJJJJJJJJJJJJJJJJJJJJJ                    ",
            "                                                                                                                  CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC     CCCCC                   JJJJJJJJJJJJJJJJJJJJJJJJJJ                    ",
            "                                                                                                                     CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC      CCCCC                    JJJJJJJJJJJJJJJJJJJJJJJJ                    ",
            "                                                                                                                        CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC       CCCCC                      JJJJJJJJJJJJJJJJJJJJJJJJ                  ",
            "                                                                                                                           CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC        CCCC                        JJJJJJJJJJJJJJJJJJJJJJ                  ",
            "                                                                                                                              CCCCCCCCCCCCCCCCCCCCCCCCCCCC        CCCCC                         JJJJJJJJJJJJJJJJJJJ                   ",
            "                                                                                                                               CCCCCCCCCCCCCCCCCCCCCCCCCC          CCCC                           JJJJJJJJJJJJJJJJJ                   ",
            "                                                                                                                               CCCCCCCCCCCCCCCCCCCCCCCCCC          CCCCC                            JJJJJJJJJJJJJ                     ",
            "                                                                                                                                CCCCCCCCCCCCCCCCCCCCCCCC            CCCC                              JJJJJJJJJJJ                     ",
            "                                                                                                                                CCCCCCCCCCCCCCCCCCCCCCCC            CCCCC                               JJJJJJJJJJ                    ",
            "                                                                                                                               CCCCCCCCCCCCCCCCCCCCCCCC              CCCC                                 JJJJJJJJ                    ",
            "                                                                                                                              CCCCCCCCCCCCCCCCCCCCCCCC               CCCC                                                             ",
            "                                                                                                                              CCCCCCCCCCCCCCCCCCCCCCCC                                                                                ",
            "                                                                                                                             CCCCCCCCCCCCCCCCCCCCCCCCC                                                                                ",
            "                                                                                                                             CCCCCCCCCCCCCCCCCCCCCCC                                                                                  ",
            "                                                                                                                             CCCCCCCCCCCCCCCCCCCCCCC                                                                                  ",
            "                                                                                                                             CCCCCCCCCCCCCCCCCCCCCCC                                                                                  ",
            "                            MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM                                                                             " },
        { "                                                                                                                                                                                                                             JJJJJJJJ ",
            "                                                                                                                                                                                                                            JJJJJJJJJ ",
            "                                                                                                                                                                                                                           JJJJJJJJJJJ",
            "                                                                                                                                                                                                                          JJJJJJJJJJJJ",
            "                                                                                                                                                                                                                         JJJJJJJJJJJJJ",
            "                                                                                                                                                                                                                        JJJJJJJJJJJJJJ",
            "                                                                                                                                                                                                                       JJJJJJJJJJJJJJJ",
            "                                                                                                                                                                                                                      JJJJJJJJJJJJJJJ ",
            "                                                                                                                                                                                                                     JJJJJJJJJJJJJJJJ ",
            "                                                                                                                                                                                                                    JJJJJJJJJJJ       ",
            "                                                                                                                                                                                                                   JJJJJJJJJJJ        ",
            "                                                                                                                                                                                                                  JJJJJJJJJJJJ        ",
            "                                                                                                                                                                                                                 JJJJJJJJJJJJ         ",
            "                                                                                                                     G                                                                                          JJJJJJJJJJJJJ         ",
            "                                                                                                                     G                                                                                         JJJJJJJJJJJJJ          ",
            "                                                                                                                     G                                                                                        JJJJJJJJJJJJJJ          ",
            "                                                                                                                    GG                                                                                       JJJJJJJJJJJJJJ           ",
            "                                                                                                                    GG                                                                                      JJJJJJJJJJJJJJJ           ",
            "                                 EEEEEEDEEEEEEEEDEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEDLLLDEEEEEEEEEEEEEEEG           GG                                                                                     JJJJJJJJJJJJJJJ            ",
            "                                EA     A        A                     A               A             AAAA            GG                                                                          JJJJJJJJJJJJJJJJJJJJJJJJJJ            ",
            "                                 A     A        A                     A               A             AAAA            GG                                                                         JJJJJJJJJJJJJJJJJJJJJJJJJJ             ",
            "   C   C                        EA     A        A                     A               A             AAAAGG          GG                                                                        JJJJJJJJJJJJJJJJJJJJJJJJJJJ             ",
            "   CCCCC                         EEEEEEDEEEEEEEEDEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEDLLLDEEEEEEEEEEECCCCGG       GGGGG                                                                       JJJJJJJJJJJJJJJJJJJJJJJJJJJ              ",
            " CCCCCCC                             DDDD                                                         CCCCC GG CCCCCGGGGG                           CCC                                         JJJJJJJJJJJJJJJJJJJJJJJJJJJJ              ",
            " CCCCCCC                              DDD                                                           CCC GGCCCCCGGG                          CCCCCCC                                        JJJJJJJJJJJJJJJJJJJJJJJJJJJJ               ",
            "CFFFFFFCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCDCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC                         CCCCCCCCCCCCCCCCCCCCCCCCCCCCC                   JJJJJJJJJJJJJJJJJJJJJJJJJJJJJ               ",
            "GIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII                                                                     CCC                     CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC                JJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                ",
            "                                                                                                                 CCCCK       KKKKKKKKKKKKCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC             JJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                ",
            "GIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII                                                                     CCCCK                   CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC          JJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                 ",
            "CFFFFFFCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCDCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCKKKKKKKKKKKKKKKKKKKKCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC       JJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                 ",
            " CCCCCCC                                CCCC                                                            GGGGCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                  ",
            " CCCCCCC                                CCCC                                                              GGCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                  ",
            "   CCCCC                                CCCC                                                              GGCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                   ",
            "   C   C                                CCCC                                                              GGCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC        CCCJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                   ",
            "                                        CCCC                                                              GG   CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC                 JJJJJJJJJJJJJJJJJJJJJJJJJJJJ                    ",
            "                                                                                                                  CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC     CCCCC                   JJJJJJJJJJJJJJJJJJJJJJJJJJ                    ",
            "                                                                                                                     CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC      CCCCC                    JJJJJJJJJJJJJJJJJJJJJJJJ                    ",
            "                                                                                                                        CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC       CCCCC                      JJJJJJJJJJJJJJJJJJJJJJJJ                  ",
            "                                                                                                                           CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC        CCCC                        JJJJJJJJJJJJJJJJJJJJJJ                  ",
            "                                                                                                                              CCCCCCCCCCCCCCCCCCCCCCCCCCCC        CCCCC                         JJJJJJJJJJJJJJJJJJJ                   ",
            "                                                                                                                               CCCCCCCCCCCCCCCCCCCCCCCCCC          CCCC                           JJJJJJJJJJJJJJJJJ                   ",
            "                                                                                                                               CCCCCCCCCCCCCCCCCCCCCCCCCC          CCCCC                            JJJJJJJJJJJJJ                     ",
            "                                                                                                                                CCCCCCCCCCCCCCCCCCCCCCCC            CCCC                              JJJJJJJJJJJ                     ",
            "                                                                                                                                CCCCCCCCCCCCCCCCCCCCCCCC            CCCCC                               JJJJJJJJJJ                    ",
            "                                                                                                                               CCCCCCCCCCCCCCCCCCCCCCCC              CCCC                                 JJJJJJJJ                    ",
            "                                                                                                                              CCCCCCCCCCCCCCCCCCCCCCCC               CCCC                                                             ",
            "                                                                                                                              CCCCCCCCCCCCCCCCCCCCCCCC                                                                                ",
            "                                                                                                                             CCCCCCCCCCCCCCCCCCCCCCCCC                                                                                ",
            "                                                                                                                             CCCCCCCCCCCCCCCCCCCCCCC                                                                                  ",
            "                                                                                                                             CCCCCCCCCCCCCCCCCCCCCCC                                                                                  ",
            "                                                                                                                             CCCCCCCCCCCCCCCCCCCCCCC                                                                                  ",
            "                            MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM                                                                             " },
        { "                                                                                                                                                                                                                             JJJJJJJJ ",
            "                                                                                                                                                                                                                            JJJJJJJJJ ",
            "                                                                                                                                                                                                                           JJJJJJJJJJJ",
            "                                                                                                                                                                                                                          JJJJJJJJJJJJ",
            "                                                                                                                                                                                                                         JJJJJJJJJJJJJ",
            "                                                                                                                                                                                                                        JJJJJJJJJJJJJJ",
            "                                                                                                                                                                                                                       JJJJJJJJJJJJJJJ",
            "                                                                                                                                                                                                                      JJJJJJJJJJJJJJJ ",
            "                                                                                                                                                                                                                     JJJJJJJJJJJJJJJJ ",
            "                                                                                                                                                                                                                    JJJJJJJJJJJ       ",
            "                                                                                                                                                                                                                   JJJJJJJJJJJ        ",
            "                                                                                                                                                                                                                  JJJJJJJJJJJJ        ",
            "                                                                                                                                                                                                                 JJJJJJJJJJJJ         ",
            "                                                                                                                                                                                                                JJJJJJJJJJJJJ         ",
            "                                                                                                                                                                                                               JJJJJJJJJJJJJ          ",
            "                                                                                                                     G                                                                                        JJJJJJJJJJJJJJ          ",
            "                                                                                                                     G                                                                                       JJJJJJJJJJJJJJ           ",
            "                                                                                                                     G                                                                                      JJJJJJJJJJJJJJJ           ",
            "                                  EEEEEDEEEEEEEEDEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEDLLLDEEEEEEEEEEEEEEEG            G                                                                                     JJJJJJJJJJJJJJJ            ",
            "                                EEEEEEEDEEEEEEEEDEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEDLLLDEEEEEEEEEEECCCCG            G                                                                          JJJJJJJJJJJJJJJJJJJJJJJJJJ            ",
            "                                EA     A        A                     A               A             AAAAG            G                                                                         JJJJJJJJJJJJJJJJJJJJJJJJJJ             ",
            "   C   C                        EEEEEEEDEEEEEEEEDEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEDLLLDEEEEEEEEEEECCCCGG           G                                                                        JJJJJJJJJJJJJJJJJJJJJJJJJJJ             ",
            "   C   C                          EEEEEDEEEEEEEEDEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEDLLLDEEEEEEEEEEECCCCGG          GG                                                                       JJJJJJJJJJJJJJJJJJJJJJJJJJJ              ",
            " CCC   C                                                                                            CC  GG CCCCCGGGGG                           CCC                                         JJJJJJJJJJJJJJJJJJJJJJJJJJJJ              ",
            " CCCAAAC                                                                                            CC  GGCCCCCCCG                          CCCCCCC                                        JJJJJJJJJJJJJJJJJJJJJJJJJJJJ               ",
            "CFFFFFFCC                              CCCCCCCCCCCCDCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC                         CCCCCCCCCCCCCCCCCCCCCCCCCCCCC                   JJJJJJJJJJJJJJJJJJJJJJJJJJJJJ               ",
            "GHHHHHHHHCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCDCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC                     CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC                JJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                ",
            "GHHHHHHHHCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC                                                                     CCCCKKKKKKKK        KKKKCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC             JJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                ",
            "GHHHHHHHHCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCDCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCKKKKKKKKKKKKKKKKKKKKCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC          JJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                 ",
            "CFFFFFFCC                              CCCCCCCCCCCCDCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCKKKKKKKKKKKKKKKKKKKKCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC       JJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                 ",
            " CCCAAAC                                CCCC                                                            GGGGCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                  ",
            " CCC   C                                CCCC                                                              GGCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                  ",
            "   C   C                                CCCC                                                              GGCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                   ",
            "   C   C                                CCCC                                                              GGCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC        CCCJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                   ",
            "                                        CCCC                                                              GG   CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC                 JJJJJJJJJJJJJJJJJJJJJJJJJJJJ                    ",
            "                                                                                                                  CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC     CCCCC                   JJJJJJJJJJJJJJJJJJJJJJJJJJ                    ",
            "                                                                                                                     CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC      CCCCC                    JJJJJJJJJJJJJJJJJJJJJJJJ                    ",
            "                                                                                                                        CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC       CCCCC                      JJJJJJJJJJJJJJJJJJJJJJJJ                  ",
            "                                                                                                                           CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC        CCCC                        JJJJJJJJJJJJJJJJJJJJJJ                  ",
            "                                                                                                                              CCCCCCCCCCCCCCCCCCCCCCCCCCCC        CCCCC                         JJJJJJJJJJJJJJJJJJJ                   ",
            "                                                                                                                               CCCCCCCCCCCCCCCCCCCCCCCCCC          CCCC                           JJJJJJJJJJJJJJJJJ                   ",
            "                                                                                                                               CCCCCCCCCCCCCCCCCCCCCCCCCC          CCCCC                            JJJJJJJJJJJJJ                     ",
            "                                                                                                                                CCCCCCCCCCCCCCCCCCCCCCCC            CCCC                              JJJJJJJJJJJ                     ",
            "                                                                                                                                CCCCCCCCCCCCCCCCCCCCCCCC            CCCCC                               JJJJJJJJJJ                    ",
            "                                                                                                                               CCCCCCCCCCCCCCCCCCCCCCCC              CCCC                                 JJJJJJJJ                    ",
            "                                                                                                                              CCCCCCCCCCCCCCCCCCCCCCCC               CCCC                                                             ",
            "                                                                                                                              CCCCCCCCCCCCCCCCCCCCCCCC                                                                                ",
            "                                                                                                                             CCCCCCCCCCCCCCCCCCCCCCCCC                                                                                ",
            "                                                                                                                             CCCCCCCCCCCCCCCCCCCCCCC                                                                                  ",
            "                                                                                                                             CCCCCCCCCCCCCCCCCCCCCCC                                                                                  ",
            "                                                                                                                             CCCCCCCCCCCCCCCCCCCCCCC                                                                                  ",
            "                            MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM                                                                             " },
        { "                                                                                                                                                                                                                             JJJJJJJJ ",
            "                                                                                                                                                                                                                            JJJJJJJJJ ",
            "                                                                                                                                                                                                                           JJJJJJJJJJJ",
            "                                                                                                                                                                                                                          JJJJJJJJJJJJ",
            "                                                                                                                                                                                                                         JJJJJJJJJJJJJ",
            "                                                                                                                                                                                                                        JJJJJJJJJJJJJJ",
            "                                                                                                                                                                                                                       JJJJJJJJJJJJJJJ",
            "                                                                                                                                                                                                                      JJJJJJJJJJJJJJJ ",
            "                                                                                                                                                                                                                     JJJJJJJJJJJJJJJJ ",
            "                                                                                                                                                                                                                    JJJJJJJJJJJ       ",
            "                                                                                                                                                                                                                   JJJJJJJJJJJ        ",
            "                                                                                                                                                                                                                  JJJJJJJJJJJJ        ",
            "                                                                                                                                                                                                                 JJJJJJJJJJJJ         ",
            "                                                                                                                                                                                                                JJJJJJJJJJJJJ         ",
            "                                                                                                                                                                                                               JJJJJJJJJJJJJ          ",
            "                                                                                                                                                                                                              JJJJJJJJJJJJJJ          ",
            "                                                                                                                                                                                                             JJJJJJJJJJJJJJ           ",
            "                                                                                                                                                                                                            JJJJJJJJJJJJJJJ           ",
            "                                                                                                                                                                                                           JJJJJJJJJJJJJJJ            ",
            "                                  EEEEEDEEEEEEEEDEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEDLLLDEEEEEEEEEEEEEEE                                                                                        JJJJJJJJJJJJJJJJJJJJJJJJJJ            ",
            "                                 EEEEEEDEEEEEEEEDEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEDLLLDEEEEEEEEEEEEEEE                                                                                       JJJJJJJJJJJJJJJJJJJJJJJJJJ             ",
            "                                  EEEEEDEEEEEEEEDEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEDLLLDEEEEEEEEEEEEEEE                                                                                      JJJJJJJJJJJJJJJJJJJJJJJJJJJ             ",
            "   CCCCC                                                                                                                                                                                     JJJJJJJJJJJJJJJJJJJJJJJJJJJ              ",
            "   CCCCC                                                                                                                                                                                    JJJJJJJJJJJJJJJJJJJJJJJJJJJJ              ",
            " CCCCCCC                                                                                                  CCCCCCC                                                                          JJJJJJJJJJJJJJJJJJJJJJJJJJJJ               ",
            " CCCCCCCC                              CCCCCC                                                           GGCCCCCCC                                                                         JJJJJJJJJJJJJJJJJJJJJJJJJJJJJ               ",
            "CFFFFFFCC                              CCCCCCCCCCCCDCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC                     CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC                JJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                ",
            "CFFFFFFCC                              CCCCCCCCCCCCDCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC                    CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC             JJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                ",
            "CFFFFFFCC                              CCCCCCCCCCCCDCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC                   CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC          JJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                 ",
            " CCCCCCCC                              CCCCCC                                                           GGCCCCCCCCCCCCCC                 CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC       JJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                 ",
            " CCCCCCC                                CCCC                                                               GCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                  ",
            "   CCCCC                                                                                                   GCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCJ  JJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                  ",
            "   CCCCC                                                                                                   GCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                   ",
            "                                        CCCC                                                                CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC        CCCJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ                   ",
            "                                        CCCC                                                                   CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC                 JJJJJJJJJJJJJJJJJJJJJJJJJJJJ                    ",
            "                                        CCCC                                                                      CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC     CCCCC                   JJJJJJJJJJJJJJJJJJJJJJJJJ                     ",
            "                                                                                                                     CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC      CCCCC                    JJJJJJJJJJJJJJJJJJJJJJJ                     ",
            "                                                                                                                        CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC       CCCCC                      JJJJJJJJJJJJJJJJJJJJJJJJ                  ",
            "                                                                                                                           CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC        CCCC                        JJJJJJJJJJJJJJJJJJJJJJ                  ",
            "                                                                                                                              CCCCCCCCCCCCCCCCCCCCCCCCCCCC        CCCCC                         JJJJJJJJJJJJJJJJJJ                    ",
            "                                                                                                                               CCCCCCCCCCCCCCCCCCCCCCCCCC          CCCC                           JJJJJJJJJJJJJJJJ                    ",
            "                                                                                                                               CCCCCCCCCCCCCCCCCCCCCCCCCC          CCCCC                            JJJJJJJJJJJJJ                     ",
            "                                                                                                                                CCCCCCCCCCCCCCCCCCCCCCCC            CCCC                              JJJJJJJJJJJ                     ",
            "                                                                                                                                CCCCCCCCCCCCCCCCCCCCCCCC            CCCCC                               JJJJJJJJJJ                    ",
            "                                                                                                                               CCCCCCCCCCCCCCCCCCCCCCCC              CCCC                                 JJJJJJJJ                    ",
            "                                                                                                                              CCCCCCCCCCCCCCCCCCCCCCCC               CCCC                                                             ",
            "                                                                                                                              CCCCCCCCCCCCCCCCCCCCCCCC                                                                                ",
            "                                                                                                                             CCCCCCCCCCCCCCCCCCCCCCCCC                                                                                ",
            "                                                                                                                             CCCCCCCCCCCCCCCCCCCCCCC                                                                                  ",
            "                                                                                                                             CCCCCCCCCCCCCCCCCCCCCCC                                                                                  ",
            "                                                                                                                             CCCCCCCCCCCCCCCCCCCCCCC                                                                                  ",
            "                            MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM                                                                             " },
        { "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "   C   C                                                                                                                                                                                                                              ",
            "   CCCCC                                                                                                                                                                                                                              ",
            " CCCCCCC                                                                                                                                                                                                                              ",
            " CCCCCCC                                 CC                                                             GG                                                                                                                            ",
            " CCCAAAC                                 CC                                                             GGCCCCCCC                                                                                                                     ",
            " CCCCCCC                                 CC                                                             GGCCCCCCCC                                                                                                                    ",
            " CCCAAAC                                 CC                                                             GGCCCCCCCC                                                                                                                    ",
            " CCCCCCC                                 CC                                                             GG   CCCCC                                                                                                                    ",
            " CCCCCCC                                                                                                                                                                                                                              ",
            "   CCCCC                                                                                                                                                                                                                              ",
            "   C   C                                                                                                                                                                                                                              ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                        CCCC                                                                                                                                                                    JJJ                   ",
            "                                                                                                                                                                                                           JJJJJJJJ                   ",
            "                                                                                                                                                                                                            JJJJJJ                    ",
            "                                                                                                                                                                                                             JJJJJ                    ",
            "                                                                                                                                                                                                                J                     ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                            MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM                                                                             " },
        { "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "   C   C                                                                                                                                                                                                                              ",
            "   CCCCC                                                                                                                                                                                                                              ",
            "   CCCCC                                                                                                                                                                                                                              ",
            " CCC   C                                                                                                                                                                                                                              ",
            " CCCCCCC                                                                                                                                                                                                                              ",
            " CCC   C                                                                                                                                                                                                                              ",
            "   CCCCC                                                                                                                                                                                                                              ",
            "   CCCCC                                                                                                                                                                                                                              ",
            "   C   C                                                                                                                                                                                                                              ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                            MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM                                                                              " },
        { "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "   C   C                                                                                                                                                                                                                              ",
            "   CCCCC                                                                                                                                                                                                                              ",
            "   C   C                                                                                                                                                                                                                              ",
            "   CCCCC                                                                                                                                                                                                                              ",
            "   C   C                                                                                                                                                                                                                              ",
            "   CCCCC                                                                                                                                                                                                                              ",
            "   C   C                                                                                                                                                                                                                              ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                            MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM                                                                              " },
        { "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "   C   C                                                                                                                                                                                                                              ",
            "   C   C                                                                                                                                                                                                                              ",
            "   C   C                                                                                                                                                                                                                              ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                            MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM                                                                               " },
        { "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                            MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM                                                                               " },
        { "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                            MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM                                                                               " },
        { "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                                        CCCC                                                                                                                                                                                          ",
            "                            MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM                                                                               " },
        { "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                            MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM                                                                               " },
        { "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                                                                                                                                                                                                                      ",
            "                                        MMMM                                                                                                                                                                                          " } };

    @Override
    protected GT_Multiblock_Tooltip_Builder createTooltip() {
        final GT_Multiblock_Tooltip_Builder tt = new GT_Multiblock_Tooltip_Builder();
        tt.addMachineType("§l§5老登的终极造物 - 阳电子炮")
            .addInfo("§6§l来自另一世界的恐怖武器, 功率消耗极高")
            .addInfo("§6§l编程电路决定伤害, 1 = 32768, 2 = 2147483647")
            .addInfo("§4最好保持炮口前方为空...")
            .addInfo("§q耗时固定10s")
            .addInfo("§q只支持无线电网直接供给EU")
            .addSeparator()
            .addController("EVA专用阳电子炮")
            .beginStructureBlock(23, 52, 230, false)
            .toolTipFinisher("§a123Technology - For EVA");
        return tt;
    }
}
