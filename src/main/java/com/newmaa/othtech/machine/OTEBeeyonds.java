package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static gregtech.api.GregTechAPI.sBlockCasings1;
import static gregtech.api.GregTechAPI.sBlockReinforced;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IItemSource;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.mojang.authlib.GameProfile;
import com.newmaa.othtech.machine.machineclass.OTHTTMultiMachineBaseEM;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IBeeMutation;
import forestry.api.apiculture.IBeeRoot;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.IErrorLogic;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleFlowers;
import forestry.core.errors.ErrorLogic;
import forestry.core.genetics.alleles.AlleleHelper;
import forestry.core.genetics.alleles.EnumAllele;
import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;
import tectech.thing.metaTileEntity.multi.base.parameter.BooleanParameter;
import tectech.thing.metaTileEntity.multi.base.parameter.EnumParameter;
import tectech.thing.metaTileEntity.multi.base.parameter.IParametrized;
import tectech.thing.metaTileEntity.multi.base.parameter.Parameter;
import tectech.thing.metaTileEntity.multi.base.parameter.StringParameter;

/**
 * Beeyonds Home / Beeyonds 之家.
 * <p>
 * Two operation modes, switchable from the machine's parameter panel:
 * <ul>
 * <li>{@link OTHBeeyondsMode#PRODUCTION} - behaves like an Alveary: up to (4 * energy tier) queens work in
 * parallel, each running Forestry's normal apiary simulation (aging, products, eventual queen death).</li>
 * <li>{@link OTHBeeyondsMode#BREED} - takes two parent bees (any combination of princess/queen/drone), looks up
 * a registered mutation matching their two species (ignoring biome/chance/resource requirements - only the
 * species pairing matters), and after a fixed 20s cycle produces a pristine (homozygous, no genetic decay)
 * offspring with fully player-selected alleles: either a mated queen, or a batch of up to 64 drones at 300%
 * of the normal yield.</li>
 * </ul>
 */
public class OTEBeeyonds extends OTHTTMultiMachineBaseEM
    implements IConstructable, ISurvivalConstructable, IParametrized {

    private static final int QUEEN_SLOTS_PER_TIER = 4;
    private static final int BREED_TIME_TICKS = 20 * 20; // 20 seconds
    private static final float BREED_YIELD_MULTIPLIER = 3.0F; // 300%
    private static final int MAX_DRONES_PER_CYCLE = 64;

    // region parameters (machine settings panel)
    // NOTE: Parameter(value, langKey, nbtKey, ...) - langKey MUST be the translateToLocal() key
    // (the "otht.bee.param.xxx" strings, see en_US.lang) and nbtKey is the short internal save id.
    // These two used to be swapped on every field below, which made the settings panel show raw
    // untranslated keys ("mode", "traitSpeed", ...) instead of the proper labels.
    private final EnumParameter<OTHBeeyondsMode> pMode = new EnumParameter<>(
        OTHBeeyondsMode.class,
        OTHBeeyondsMode.PRODUCTION,
        "otht.bee.param.mode",
        "mode");
    private final BooleanParameter pEjectHeldBees = new BooleanParameter(
        false,
        "otht.bee.param.eject",
        "ejectHeldBees");
    private final StringParameter pTargetSpecies = new StringParameter("", "otht.bee.param.species", "targetSpecies");
    private final EnumParameter<OTHBeeyondsOutputKind> pOutputKind = new EnumParameter<>(
        OTHBeeyondsOutputKind.class,
        OTHBeeyondsOutputKind.QUEEN,
        "otht.bee.param.outputKind",
        "outputKind");
    private final EnumParameter<EnumAllele.Speed> pSpeed = new EnumParameter<>(
        EnumAllele.Speed.class,
        EnumAllele.Speed.NORMAL,
        "otht.bee.param.speed",
        "traitSpeed");
    private final EnumParameter<EnumAllele.Fertility> pFertility = new EnumParameter<>(
        EnumAllele.Fertility.class,
        EnumAllele.Fertility.NORMAL,
        "otht.bee.param.fertility",
        "traitFertility");
    private final EnumParameter<EnumAllele.Lifespan> pLifespan = new EnumParameter<>(
        EnumAllele.Lifespan.class,
        EnumAllele.Lifespan.NORMAL,
        "otht.bee.param.lifespan",
        "traitLifespan");
    private final EnumParameter<EnumAllele.Flowering> pFlowering = new EnumParameter<>(
        EnumAllele.Flowering.class,
        EnumAllele.Flowering.AVERAGE,
        "otht.bee.param.flowering",
        "traitFlowering");
    private final EnumParameter<EnumAllele.Territory> pTerritory = new EnumParameter<>(
        EnumAllele.Territory.class,
        EnumAllele.Territory.AVERAGE,
        "otht.bee.param.territory",
        "traitTerritory");
    private final EnumParameter<EnumAllele.Tolerance> pTempTolerance = new EnumParameter<>(
        EnumAllele.Tolerance.class,
        EnumAllele.Tolerance.NONE,
        "otht.bee.param.tempTolerance",
        "traitTempTolerance");
    private final EnumParameter<EnumAllele.Tolerance> pHumidTolerance = new EnumParameter<>(
        EnumAllele.Tolerance.class,
        EnumAllele.Tolerance.NONE,
        "otht.bee.param.humidTolerance",
        "traitHumidTolerance");
    private final BooleanParameter pNocturnal = new BooleanParameter(
        false,
        "otht.bee.param.nocturnal",
        "traitNocturnal");
    private final BooleanParameter pTolerantFlyer = new BooleanParameter(
        false,
        "otht.bee.param.tolerantFlyer",
        "traitTolerantFlyer");
    private final BooleanParameter pCaveDwelling = new BooleanParameter(
        false,
        "otht.bee.param.caveDwelling",
        "traitCaveDwelling");
    private final StringParameter pEffect = new StringParameter("", "otht.bee.param.effect", "traitEffect");
    private final StringParameter pFlowerProvider = new StringParameter(
        "",
        "otht.bee.param.flowerProvider",
        "traitFlowerProvider");

    private List<Parameter<?, ?>> mParameters;
    // endregion

    // internal state, not part of the GT structure - purely virtual bookkeeping
    private final List<QueenCell> mQueenCells = new ArrayList<>();
    private ItemStack mParent0;
    private ItemStack mParent1;
    private int mBreedProgress = 0;
    private GameProfile mCachedOwner;
    // the mode the machine is actually operating as; lags pMode until the bees held by the mode
    // being left have been fully ejected, so a mode switch never silently destroys them
    private OTHBeeyondsMode mActiveMode = OTHBeeyondsMode.PRODUCTION;

    @Override
    public void checkMachine(IGregTechTileEntity iGregTechTileEntity, ItemStack itemStack,
        List<StructureError> errors) {
        // offset must match the '~' hint cell in STRUCTURE_DEFINITION (x=11, y=20, z=1) - same
        // value survivalConstruct() below already uses. This was previously (0, 1, 0), which made
        // StructureLib think the controller sat far away from where it actually was, so the
        // hologram/structure rendered wildly offset from the real controller block.
        if (!checkPiece("main", 11, 20, 1, errors)) return;
        checkHasEnergyHatch(errors);
        checkHasMaintenanceHatch(errors);
        checkHasOutputBus(errors);
    }

    @Override
    public void onFirstTick_EM(IGregTechTileEntity aBaseMetaTileEntity) {
        if (!mMachine) {
            aBaseMetaTileEntity.disableWorking();
        }
    }

    // region structure
    private static final String[] description = new String[] {
        EnumChatFormatting.AQUA + translateToLocal("otht.con") + ":", translateToLocal("ote.cm.bee.0"), };
    private static final IStructureDefinition<OTEBeeyonds> STRUCTURE_DEFINITION = IStructureDefinition
        .<OTEBeeyonds>builder()
        .addShape(
            "main",
            new String[][] {
                { "                       ", "                       ", "                       ",
                    "                       ", "                       ", "                       ",
                    "                       ", "                       ", "                       ",
                    "                       ", "                       ", "                       ",
                    "                       ", "        BBBBBBB        ", "       BCCCCCCCB       ",
                    "     BBBCCCCCCCBBB     ", "   BBBBBCCCCCCCBBBBB   ", "     BBBCCCCCCCBBB     ",
                    "       BCCCCCCCB       ", "        BBBBBBB        ", "                       ",
                    "                       " },
                { "                       ", "       BBB    BBB      ", "      BBBBB  BBBBB     ",
                    "      BBBBBBBBBBBB     ", "       BBBBBBBBBB      ", "        AAEAEAE        ",
                    "       AAAAEAEAE       ", "      AEAEAEAEAEA      ", "      AAAAAEAEAEAE     ",
                    "      AABAAEAEAEA      ", "       AAAAEAEAE       ", "        AAEAEAE        ",
                    "                       ", "       BBBBBBBBB       ", "    BBBBCCCCCCCBBBB    ",
                    "    BBBBCCCCCCCBBBB    ", "  BBBBBBC     CBBBBBB  ", "    BBBBC     CBBBB    ",
                    "    BBBBC     CBBBB    ", "       BBDDDDDBB       ", "          F~F          ",
                    "          FFF          " },
                { "       BBB    BBB      ", "      BBBBB  BBBBB     ", "     BBBBBBBBBBBBBB    ",
                    "     BBBBBBBBBBBBBB    ", "      BBBBBBBBBBBB     ", "       AAAEAEAEE       ",
                    "      AAAAAEAEAEA      ", "     AAEAEAEAEAEAE     ", "     AAAAAAEAEAEAEEE   ",
                    "     AAABAAEAEAEAE     ", "      AAAAAEAEAEA      ", "       AAAEAEAEE       ",
                    "        AAEAEAE        ", "       BBBBBBBBB       ", "    BBBBC     CBBBB    ",
                    "   BBBBBC     CBBBBB   ", "BBBBBBBBC     CBBBBBBBB", "   BBBBBC     CBBBBB   ",
                    "    BBBBC     CBBBB    ", "       BBDDDDDBB       ", "         FFBFF         ",
                    "          FFF          " },
                { "                       ", "       BBB    BBB      ", "      BBBBB  BBBBB     ",
                    "      BBBBBBBBBBBB     ", "       BBBBBBBBBB      ", "        AAEAEAE        ",
                    "       AAAAEAEAE       ", "      AAAAAEAEAEA      ", "      AAAAAEAEAEAE     ",
                    "      AAAAAEAEAEA      ", "       AAAAEAEAE       ", "        AAEAEAE        ",
                    "                       ", "       BBBBBBBBB       ", "    BBBBCCCCCCCBBBB    ",
                    "    BBBBCCCCCCCBBBB    ", "  BBBBBBC     CBBBBBB  ", "    BBBBC     CBBBB    ",
                    "    BBBBC     CBBBB    ", "       BBDDDDDBB       ", "          FFF          ",
                    "          FFF          " },
                { "                       ", "                       ", "                       ",
                    "                       ", "                       ", "                       ",
                    "                       ", "                       ", "                       ",
                    "                       ", "                       ", "                       ",
                    "                       ", "        BBBBBBB        ", "       BCCCCCCCB       ",
                    "     BBBCCCCCCCBBB     ", "   BBBBBCCCCCCCBBBBB   ", "     BBBCCCCCCCBBB     ",
                    "       BCCCCCCCB       ", "        BBBBBBB        ", "                       ",
                    "                       " } })
        .addElement('A', ofBlock(sBlockCasings1, 10))
        .addElement('B', ofBlock(sBlockReinforced, 2))
        .addElement('C', ofBlock(Blocks.glass, 0))
        .addElement('D', ofBlock(Blocks.grass, 0))
        .addElement('E', ofBlock(Blocks.obsidian, 0))
        .addElement(
            'F',
            buildHatchAdder(OTEBeeyonds.class)
                .atLeast(InputBus, OutputBus, InputHatch, OutputHatch, Energy.or(ExoticEnergy))
                .adder(OTEBeeyonds::addToMachineList)
                .hint(1)
                .casingIndex(210)
                .buildAndChain(sBlockReinforced, 2))
        .build();

    @Override
    public IStructureDefinition<OTEBeeyonds> getStructure_EM() {
        return STRUCTURE_DEFINITION;
    }
    // endregion

    public OTEBeeyonds(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        eDismantleBoom = true;
    }

    public OTEBeeyonds(String aName) {
        super(aName);
        eDismantleBoom = true;
    }

    @Override
    public void onRemoval() {
        super.onRemoval();
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        NBTTagCompound paramTag = new NBTTagCompound();
        for (Parameter<?, ?> parameter : getParameters()) {
            parameter.saveNBT(paramTag);
        }
        aNBT.setTag("othBeeyondsParams", paramTag);

        aNBT.setInteger("othBeeyondsActiveMode", mActiveMode.ordinal());
        aNBT.setInteger("othBeeyondsBreedProgress", mBreedProgress);
        if (mParent0 != null) aNBT.setTag("othBeeyondsParent0", mParent0.writeToNBT(new NBTTagCompound()));
        if (mParent1 != null) aNBT.setTag("othBeeyondsParent1", mParent1.writeToNBT(new NBTTagCompound()));

        NBTTagList queenList = new NBTTagList();
        for (QueenCell cell : mQueenCells) {
            NBTTagCompound cellTag = new NBTTagCompound();
            if (cell.queenStack != null) {
                cellTag.setTag("queen", cell.queenStack.writeToNBT(new NBTTagCompound()));
            }
            queenList.appendTag(cellTag);
        }
        aNBT.setTag("othBeeyondsQueens", queenList);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        if (mParameters == null) initParameters();
        NBTTagCompound paramTag = aNBT.getCompoundTag("othBeeyondsParams");
        for (Parameter<?, ?> parameter : getParameters()) {
            parameter.loadNBT(paramTag);
        }

        OTHBeeyondsMode[] modes = OTHBeeyondsMode.values();
        int activeModeOrdinal = aNBT.getInteger("othBeeyondsActiveMode");
        mActiveMode = activeModeOrdinal >= 0 && activeModeOrdinal < modes.length ? modes[activeModeOrdinal]
            : OTHBeeyondsMode.PRODUCTION;

        mBreedProgress = aNBT.getInteger("othBeeyondsBreedProgress");
        mParent0 = aNBT.hasKey("othBeeyondsParent0")
            ? ItemStack.loadItemStackFromNBT(aNBT.getCompoundTag("othBeeyondsParent0"))
            : null;
        mParent1 = aNBT.hasKey("othBeeyondsParent1")
            ? ItemStack.loadItemStackFromNBT(aNBT.getCompoundTag("othBeeyondsParent1"))
            : null;

        mQueenCells.clear();
        NBTTagList queenList = aNBT.getTagList("othBeeyondsQueens", 10);
        for (int i = 0; i < queenList.tagCount(); i++) {
            NBTTagCompound cellTag = queenList.getCompoundTagAt(i);
            QueenCell cell = new QueenCell();
            if (cellTag.hasKey("queen")) {
                cell.queenStack = ItemStack.loadItemStackFromNBT(cellTag.getCompoundTag("queen"));
            }
            mQueenCells.add(cell);
        }
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTEBeeyonds(mName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(translateToLocal("ote.tm.bee.0"))
            .addInfo(translateToLocal("ote.tm.bee.1"))
            .addInfo(translateToLocal("ote.tm.bee.2"))
            .addInfo(translateToLocal("ote.tm.bee.3"))
            .addInfo(translateToLocal("ote.tm.bee.4"))
            .addTecTechHatchInfo()
            .addSeparator()
            .toolTipFinisher("§a123Technology - Beeyonds");
        return tt;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity baseMetaTileEntity, ForgeDirection sideDirection,
        ForgeDirection facingDirection, int colorIndex, boolean active, boolean redstoneLevel) {
        if (sideDirection == facingDirection) {
            return new ITexture[] { TextureFactory.of(BLOCK_PLASCRETE), active
                ? TextureFactory.of(
                    TextureFactory.of(TTMultiblockBase.ScreenON),
                    TextureFactory.builder()
                        .addIcon(TTMultiblockBase.ScreenON)
                        .glow()
                        .build())
                : TextureFactory.of(
                    TextureFactory.of(TTMultiblockBase.ScreenOFF),
                    TextureFactory.builder()
                        .addIcon(TTMultiblockBase.ScreenOFF)
                        .glow()
                        .build()) };
        }
        return new ITexture[] { TextureFactory.of(BLOCK_PLASCRETE) };
    }

    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.TECTECH_MACHINES_NOISE;
    }

    // region parametrized (settings panel)
    @Override
    public void initParameters() {
        mParameters = Arrays.asList(
            pMode,
            pEjectHeldBees,
            pOutputKind,
            pTargetSpecies,
            pSpeed,
            pFertility,
            pLifespan,
            pFlowering,
            pTerritory,
            pTempTolerance,
            pHumidTolerance,
            pNocturnal,
            pTolerantFlyer,
            pCaveDwelling,
            pEffect,
            pFlowerProvider);
    }

    @Override
    public void loadLegacyParameters(NBTTagCompound aNBT) {
        // no legacy save format to migrate from - this machine's parameter system is new
    }

    @Override
    public List<Parameter<?, ?>> getParameters() {
        return mParameters;
    }

    private OTHBeeyondsMode getMode() {
        return pMode.getValue() == null ? OTHBeeyondsMode.PRODUCTION : pMode.getValue();
    }
    // endregion

    // region tick logic
    @Override
    public boolean onRunningTick(ItemStack aStack) {
        if (getBaseMetaTileEntity().isServerSide()) {
            OTHBeeyondsMode desired = getMode();
            if (mActiveMode != desired) {
                // Switching modes auto-ejects whatever the mode being left is holding. If the
                // output can't take it all right now, the switch stays blocked (and nothing is
                // destroyed) until there's room - drainActiveMode() is safe to call every tick.
                if (drainActiveMode()) {
                    mActiveMode = desired;
                }
            }
            if (mActiveMode == desired) {
                if (mActiveMode == OTHBeeyondsMode.PRODUCTION) {
                    tickProduction();
                } else {
                    tickBreed();
                }
            }
            handleManualEjectRequest();
        }
        return true;
    }

    private int currentTier() {
        return Math.max(1, GTUtility.getTier(getMaxInputVoltageSum()));
    }

    private int queenSlotCount() {
        return currentTier() * QUEEN_SLOTS_PER_TIER;
    }

    /**
     * Ejects everything held by {@link #mActiveMode} (the mode being left on a mode switch, or the
     * currently active mode for a manual eject). Never destroys a bee: a cell/parent slot is only
     * cleared once {@code addOutputAtomic} confirms the whole stack made it into an output bus.
     */
    private boolean drainActiveMode() {
        return mActiveMode == OTHBeeyondsMode.PRODUCTION ? ejectAllQueens() : ejectParents();
    }

    private void handleManualEjectRequest() {
        if (!pEjectHeldBees.getValue()) return;
        if (drainActiveMode()) {
            // fully drained - release the button so it doesn't look permanently pressed
            pEjectHeldBees.setValue(false);
        }
        // otherwise leave it "pressed": it keeps retrying every tick until there's room, without
        // losing any bee in the meantime.
    }

    private boolean ejectAllQueens() {
        boolean allClear = true;
        for (QueenCell cell : mQueenCells) {
            if (cell.queenStack == null) continue;
            if (addOutputAtomic(cell.queenStack)) {
                cell.queenStack = null;
            } else {
                allClear = false;
            }
        }
        if (allClear) mQueenCells.clear();
        return allClear;
    }

    private boolean ejectParents() {
        boolean clear0 = mParent0 == null || addOutputAtomic(mParent0);
        if (clear0) mParent0 = null;
        boolean clear1 = mParent1 == null || addOutputAtomic(mParent1);
        if (clear1) mParent1 = null;
        if (clear0 && clear1) mBreedProgress = 0;
        return clear0 && clear1;
    }

    private void tickProduction() {
        int wanted = queenSlotCount();
        while (mQueenCells.size() < wanted) {
            mQueenCells.add(new QueenCell());
        }
        // Shrink from the end, but only drop a cell once its bee (if any) safely made it into an
        // output bus - if the output is full, stop shrinking for now instead of voiding the bee.
        while (mQueenCells.size() > wanted) {
            QueenCell last = mQueenCells.get(mQueenCells.size() - 1);
            if (last.queenStack != null && !addOutputAtomic(last.queenStack)) {
                break;
            }
            last.queenStack = null;
            mQueenCells.remove(mQueenCells.size() - 1);
        }

        IBeeRoot root = BeeManager.beeRoot;
        for (QueenCell cell : mQueenCells) {
            if (cell.queenStack == null) {
                cell.queenStack = pullQueenFromInputs(root);
            }
            if (cell.queenStack == null) continue;

            IBeekeepingLogic logic = cell.getBeekeepingLogic();
            if (logic.canWork()) {
                logic.doWork();
            }
        }
    }

    private ItemStack pullQueenFromInputs(IBeeRoot root) {
        for (ItemStack stack : getStoredInputs()) {
            if (stack == null) continue;
            if (!root.isMember(stack)) continue;
            if (root.getType(stack) != EnumBeeType.QUEEN) continue;
            ItemStack single = stack.splitStack(1);
            if (stack.stackSize <= 0) stack.stackSize = 0;
            return single;
        }
        return null;
    }

    private void tickBreed() {
        IBeeRoot root = BeeManager.beeRoot;

        if (mParent0 == null) {
            mParent0 = pullParentFromInputs(root);
        }
        if (mParent1 == null) {
            mParent1 = pullParentFromInputs(root);
        }

        if (mParent0 == null || mParent1 == null) {
            mBreedProgress = 0;
            return;
        }

        IBee bee0 = root.getMember(mParent0);
        IBee bee1 = root.getMember(mParent1);
        if (bee0 == null || bee1 == null) {
            returnParents();
            return;
        }

        IAlleleBeeSpecies species0 = bee0.getGenome()
            .getPrimary();
        IAlleleBeeSpecies species1 = bee1.getGenome()
            .getPrimary();

        IBeeMutation mutation = findMutation(species0, species1);
        if (mutation == null) {
            returnParents();
            return;
        }

        mBreedProgress++;
        if (mBreedProgress < BREED_TIME_TICKS) {
            return;
        }
        mBreedProgress = 0;

        IAllele[] template = buildCustomTemplate(mutation, species0, species1);
        boolean sameSpecies = species0.getUID()
            .equals(species1.getUID());

        if (pOutputKind.getValue() == OTHBeeyondsOutputKind.DRONES || sameSpecies) {
            int fertility = pFertility.getValue()
                .getValue();
            int amount = Math.min(MAX_DRONES_PER_CYCLE, Math.max(1, Math.round(fertility * BREED_YIELD_MULTIPLIER)));
            IBee droneIndividual = (IBee) root.templateAsIndividual(template);
            ItemStack droneStack = root.getMemberStack(droneIndividual, EnumBeeType.DRONE.ordinal());
            droneStack.stackSize = amount;
            addOutputAtomic(droneStack);
        } else {
            IBee mate = (IBee) root.templateAsIndividual(template);
            IBee queen = root.getBee(getBaseMetaTileEntity().getWorld(), mate.getGenome(), mate);
            addOutputAtomic(root.getMemberStack(queen, EnumBeeType.QUEEN.ordinal()));
        }

        mParent0 = null;
        mParent1 = null;
    }

    /**
     * Returns both parents to the output when they don't have a valid mutation between them. Uses
     * the same safe eject as {@link #ejectParents()} - if the output is full, the parents stay held
     * (and are retried next tick) instead of being voided.
     */
    private void returnParents() {
        ejectParents();
    }

    private ItemStack pullParentFromInputs(IBeeRoot root) {
        for (ItemStack stack : getStoredInputs()) {
            if (stack == null) continue;
            if (!root.isMember(stack)) continue;
            EnumBeeType type = root.getType(stack);
            if (type != EnumBeeType.PRINCESS && type != EnumBeeType.QUEEN && type != EnumBeeType.DRONE) continue;
            return stack.splitStack(1);
        }
        return null;
    }

    /**
     * Finds a registered mutation whose two parent species match the given pair (either order), ignoring
     * chance/resource/biome requirements entirely - only the species pairing is checked, per design.
     */
    private IBeeMutation findMutation(IAlleleBeeSpecies species0, IAlleleBeeSpecies species1) {
        String wantedUid = pTargetSpecies.getValue();
        IBeeMutation fallback = null;
        for (IBeeMutation mutation : BeeManager.beeRoot.getMutations(false)) {
            boolean matchesPair = (mutation.getAllele0() == species0 && mutation.getAllele1() == species1)
                || (mutation.getAllele0() == species1 && mutation.getAllele1() == species0);
            if (!matchesPair) continue;
            if (fallback == null) fallback = mutation;
            if (wantedUid != null && !wantedUid.isEmpty()) {
                IAllele resultSpecies = mutation.getTemplate()[EnumBeeChromosome.SPECIES.ordinal()];
                if (wantedUid.equalsIgnoreCase(resultSpecies.getUID())
                    || wantedUid.equalsIgnoreCase(resultSpecies.getUnlocalizedName())) {
                    return mutation;
                }
            }
        }
        return fallback;
    }

    /**
     * Builds a fully homozygous (both chromosome halves identical -> pristine, no genetic decay) template from
     * the mutation's base template, overriding every chromosome with the player's chosen alleles from the
     * settings panel.
     */
    private IAllele[] buildCustomTemplate(IBeeMutation mutation, IAlleleBeeSpecies species0,
        IAlleleBeeSpecies species1) {
        IAllele[] template = mutation.getTemplate()
            .clone();

        AlleleHelper.instance.set(template, EnumBeeChromosome.SPEED, pSpeed.getValue());
        AlleleHelper.instance.set(template, EnumBeeChromosome.FERTILITY, pFertility.getValue());
        AlleleHelper.instance.set(template, EnumBeeChromosome.LIFESPAN, pLifespan.getValue());
        AlleleHelper.instance.set(template, EnumBeeChromosome.FLOWERING, pFlowering.getValue());
        AlleleHelper.instance.set(template, EnumBeeChromosome.TERRITORY, pTerritory.getValue());
        AlleleHelper.instance.set(template, EnumBeeChromosome.TEMPERATURE_TOLERANCE, pTempTolerance.getValue());
        AlleleHelper.instance.set(template, EnumBeeChromosome.HUMIDITY_TOLERANCE, pHumidTolerance.getValue());
        AlleleHelper.instance.set(template, EnumBeeChromosome.NOCTURNAL, boolAllele(pNocturnal.getValue()));
        AlleleHelper.instance.set(template, EnumBeeChromosome.TOLERANT_FLYER, boolAllele(pTolerantFlyer.getValue()));
        AlleleHelper.instance.set(template, EnumBeeChromosome.CAVE_DWELLING, boolAllele(pCaveDwelling.getValue()));

        IAlleleBeeEffect effect = resolveEffect(pEffect.getValue());
        if (effect != null) {
            AlleleHelper.instance.set(template, EnumBeeChromosome.EFFECT, effect);
        }
        IAlleleFlowers flowers = resolveFlowers(pFlowerProvider.getValue());
        if (flowers != null) {
            AlleleHelper.instance.set(template, EnumBeeChromosome.FLOWER_PROVIDER, flowers);
        }

        return template;
    }

    private IAllele boolAllele(boolean value) {
        IAllele allele = AlleleManager.alleleRegistry.getAllele("forestry.boolean" + (value ? "Yes" : "No"));
        return allele;
    }

    private IAlleleBeeEffect resolveEffect(String uidOrName) {
        if (uidOrName == null || uidOrName.isEmpty()) return null;
        IAllele allele = AlleleManager.alleleRegistry.getAllele(uidOrName);
        return allele instanceof IAlleleBeeEffect ? (IAlleleBeeEffect) allele : null;
    }

    private IAlleleFlowers resolveFlowers(String uidOrName) {
        if (uidOrName == null || uidOrName.isEmpty()) return null;
        IAllele allele = AlleleManager.alleleRegistry.getAllele(uidOrName);
        return allele instanceof IAlleleFlowers ? (IAlleleFlowers) allele : null;
    }
    // endregion

    // region IBeeHousing cell used to drive Forestry's own apiary simulation per production-mode queen
    private final class QueenCell implements IBeeHousing, IBeeHousingInventory {

        private ItemStack queenStack;
        private IBeekeepingLogic logic;
        private final IErrorLogic errorLogic = new ErrorLogic();

        public IBeekeepingLogic getBeekeepingLogic() {
            if (logic == null) {
                logic = BeeManager.beeRoot.createBeekeepingLogic(this);
            }
            return logic;
        }

        @Override
        public Iterable<IBeeModifier> getBeeModifiers() {
            return Collections.singletonList(new IBeeModifier() {

                @Override
                public float getTerritoryModifier(IBeeGenome genome, float currentModifier) {
                    return 1.0F;
                }

                @Override
                public float getMutationModifier(IBeeGenome genome, IBeeGenome mate, float currentModifier) {
                    return 0.0F;
                }

                @Override
                public float getLifespanModifier(IBeeGenome genome, IBeeGenome mate, float currentModifier) {
                    return 1.0F;
                }

                @Override
                public float getProductionModifier(IBeeGenome genome, float currentModifier) {
                    return 1.0F;
                }

                @Override
                public float getFloweringModifier(IBeeGenome genome, float currentModifier) {
                    return 1.0F;
                }

                @Override
                public float getGeneticDecay(IBeeGenome genome, float currentModifier) {
                    return 0.0F;
                }

                @Override
                public boolean isSealed() {
                    return true;
                }

                @Override
                public boolean isSelfLighted() {
                    return true;
                }

                @Override
                public boolean isSunlightSimulated() {
                    return true;
                }

                @Override
                public boolean isHellish() {
                    return false;
                }
            });
        }

        @Override
        public Iterable<IBeeListener> getBeeListeners() {
            return Collections.emptyList();
        }

        @Override
        public IBeeHousingInventory getBeeInventory() {
            return this;
        }

        @Override
        public int getBlockLightValue() {
            return 15;
        }

        @Override
        public boolean canBlockSeeTheSky() {
            return true;
        }

        @Override
        public World getWorld() {
            return getBaseMetaTileEntity().getWorld();
        }

        @Override
        public GameProfile getOwner() {
            if (mCachedOwner == null) {
                mCachedOwner = new GameProfile(null, getBaseMetaTileEntity().getOwnerName());
            }
            return mCachedOwner;
        }

        @Override
        public Vec3 getBeeFXCoordinates() {
            return Vec3.createVectorHelper(
                getBaseMetaTileEntity().getXCoord(),
                getBaseMetaTileEntity().getYCoord(),
                getBaseMetaTileEntity().getZCoord());
        }

        @Override
        public ChunkCoordinates getCoordinates() {
            return new ChunkCoordinates(
                getBaseMetaTileEntity().getXCoord(),
                getBaseMetaTileEntity().getYCoord(),
                getBaseMetaTileEntity().getZCoord());
        }

        @Override
        public BiomeGenBase getBiome() {
            return getWorld()
                .getBiomeGenForCoords(getBaseMetaTileEntity().getXCoord(), getBaseMetaTileEntity().getZCoord());
        }

        @Override
        public EnumTemperature getTemperature() {
            return EnumTemperature.NORMAL;
        }

        @Override
        public EnumHumidity getHumidity() {
            return EnumHumidity.NORMAL;
        }

        @Override
        public IErrorLogic getErrorLogic() {
            return errorLogic;
        }

        @Override
        public ItemStack getQueen() {
            return queenStack;
        }

        @Override
        public ItemStack getDrone() {
            return null;
        }

        @Override
        public void setQueen(ItemStack stack) {
            queenStack = stack;
        }

        @Override
        public void setDrone(ItemStack stack) {
            // Alveary-style housing does not keep a drone slot - queens are pre-mated.
        }

        @Override
        public boolean addProduct(ItemStack product, boolean allowPartial) {
            return addOutputAtomic(product);
        }
    }
    // endregion

    @Override
    public boolean doRandomMaintenanceDamage() {
        return false;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        // same (11, 20, 1) offset as checkMachine()/survivalConstruct() - see the comment there.
        buildPiece("main", stackSize, hintsOnly, 11, 20, 1);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, IItemSource source, EntityPlayerMP actor) {
        if (mMachine) return -1;
        return survivalBuildPiece("main", stackSize, 11, 20, 1, elementBudget, source, actor, false, true);
    }

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return description;
    }

    @Override
    public boolean isPowerPassButtonEnabled() {
        return true;
    }

    @Override
    public boolean isSafeVoidButtonEnabled() {
        return false;
    }

    @Override
    public boolean isAllowedToWorkButtonEnabled() {
        return true;
    }

    @Override
    public boolean getDefaultHasMaintenanceChecks() {
        return false;
    }
}
