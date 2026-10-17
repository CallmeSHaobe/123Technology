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

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IItemSource;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.mojang.authlib.GameProfile;
import com.newmaa.othtech.machine.gui.OTHBeeyondsGui;
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
import forestry.core.errors.EnumErrorCode;
import forestry.core.errors.ErrorLogic;
import forestry.core.genetics.alleles.AlleleHelper;
import forestry.core.genetics.alleles.EnumAllele;
import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
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
    // Fixed EU/t cost per queen actively held in Production mode. Not itself doubled per tier - the
    // "overclock" comes for free from queenSlotCount() already doubling per named GT tier, so total
    // consumption (cost-per-queen x held queens) scales exactly the same way capacity does: twice the
    // tier, twice the queens, twice the EU/t, without needing a second multiplier layered on top.
    private static final long EU_COST_PER_QUEEN = 8L;
    // Same pacing Forestry's own BeekeepingLogic uses internally (queenWorkCycleThrottle >= 550,
    // ~27.5s) - kept so production timing feels the same as before, now driven by our own minimal
    // tick instead of Forestry's doWork() (see tickProduction()).
    private static final int QUEEN_WORK_CYCLE_TICKS = 550;

    // region parameters (machine settings panel)
    // NOTE: Parameter(value, langKey, nbtKey, ...) - langKey MUST be the translateToLocal() key
    // (the "otht.bee.param.xxx" strings, see en_US.lang) and nbtKey is the short internal save id.
    // These two used to be swapped on every field below, which made the settings panel show raw
    // untranslated keys ("mode", "traitSpeed", ...) instead of the proper labels.
    //
    // These fields are declared WITHOUT inline initializers on purpose: TTMultiblockBase's own
    // constructor calls initParameters() (see `if (this instanceof IParametrized parametrized)
    // parametrized.initParameters();` in TTMultiblockBase(...)) before ANY of this subclass's own
    // field initializers have run - a plain `= new EnumParameter<>(...)` here would still be null
    // at that point. That was silently building mParameters out of 15 nulls, which blew up as a
    // NullPointerException the moment loadNBTData()/saveNBTData() iterated it (every chunk
    // load/save), and the whole controller/simulation effectively never worked. Constructing the
    // Parameter objects inside initParameters() itself (called below) sidesteps the ordering issue
    // entirely, since it doesn't depend on this class's own field-initializer timing.
    private EnumParameter<OTHBeeyondsMode> pMode;
    private BooleanParameter pEjectHeldBees;
    private StringParameter pTargetSpecies;
    private EnumParameter<OTHBeeyondsOutputKind> pOutputKind;
    private EnumParameter<EnumAllele.Speed> pSpeed;
    private EnumParameter<EnumAllele.Fertility> pFertility;
    private EnumParameter<EnumAllele.Lifespan> pLifespan;
    private EnumParameter<EnumAllele.Flowering> pFlowering;
    private EnumParameter<EnumAllele.Territory> pTerritory;
    private EnumParameter<EnumAllele.Tolerance> pTempTolerance;
    private EnumParameter<EnumAllele.Tolerance> pHumidTolerance;
    private BooleanParameter pNocturnal;
    private BooleanParameter pTolerantFlyer;
    private BooleanParameter pCaveDwelling;
    private StringParameter pEffect;
    private StringParameter pFlowerProvider;

    private List<Parameter<?, ?>> mParameters;
    // subset of mParameters shown in TecTech's generic settings panel - see initParameters()
    private List<Parameter<?, ?>> mSettingsPanelParameters;
    // endregion

    // internal state, not part of the GT structure - purely virtual bookkeeping
    private final List<QueenCell> mQueenCells = new ArrayList<>();
    private ItemStack mParent0;
    private ItemStack mParent1;
    private int mBreedProgress = 0;
    private GameProfile mCachedOwner;
    // throttles the "no mutation" log in tickBreed() to once per distinct species pairing instead
    // of every tick, now that pairing without a mutation holds the parents instead of ejecting them.
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
        if (!checkPiece("main", 11, 20, 1, errors)) {
            return;
        }
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

    /**
     * {@code CommonMetaTileEntity.openGui()} only takes the MUI2 path (the one that calls
     * {@link #getGui()} and therefore ever shows {@link OTHBeeyondsGui}) when
     * {@code GTGuis.GLOBAL_SWITCH_MUI2 && useMui2() || forceUseMui2()} is true. Since
     * {@code GTGuis.GLOBAL_SWITCH_MUI2} isn't guaranteed to be enabled globally, forcing it here
     * (independent of that switch) is what makes the custom panel appear for this controller.
     */
    @Override
    protected boolean forceUseMui2() {
        return true;
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
    }

    public OTEBeeyonds(String aName) {
        super(aName);
    }

    /**
     * Deliberately does NOT call {@code super.onRemoval()}: {@link OTHTTMultiMachineBaseEM#onRemoval()}
     * detonates the controller ({@code explodeMultiblockOTH()}) whenever {@code ePowerPass && getEUVar()
     * > V[3]} - i.e. whenever "power pass" is on and the buffered EU in the energy hatches exceeds the
     * HV threshold - independently of {@code eDismantleBoom} (which this class no longer sets anyway).
     * That's a reactor-style safety mechanic inherited from the shared base class; Beeyonds Home is a
     * bee farm, not a reactor, and must never explode when its controller is broken, no matter how much
     * EU happens to be buffered at the time.
     */
    @Override
    public void onRemoval() {}

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        NBTTagCompound paramTag = new NBTTagCompound();
        // mParameters (the FULL list), not getParameters() (the settings-panel subset) - pTargetSpecies
        // is excluded from getParameters() on purpose (see initParameters()) but must still persist.
        for (Parameter<?, ?> parameter : mParameters) {
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
            cellTag.setInteger("workCycleThrottle", cell.workCycleThrottle);
            NBTTagList pendingList = new NBTTagList();
            for (ItemStack product : cell.pendingProducts) {
                pendingList.appendTag(product.writeToNBT(new NBTTagCompound()));
            }
            cellTag.setTag("pendingProducts", pendingList);
            queenList.appendTag(cellTag);
        }
        aNBT.setTag("othBeeyondsQueens", queenList);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        if (mParameters == null) initParameters();
        NBTTagCompound paramTag = aNBT.getCompoundTag("othBeeyondsParams");
        for (Parameter<?, ?> parameter : mParameters) {
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
            cell.workCycleThrottle = cellTag.getInteger("workCycleThrottle");
            NBTTagList pendingList = cellTag.getTagList("pendingProducts", 10);
            for (int j = 0; j < pendingList.tagCount(); j++) {
                cell.pendingProducts.add(ItemStack.loadItemStackFromNBT(pendingList.getCompoundTagAt(j)));
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
        pMode = new EnumParameter<>(OTHBeeyondsMode.class, OTHBeeyondsMode.PRODUCTION, "otht.bee.param.mode", "mode");
        pEjectHeldBees = new BooleanParameter(false, "otht.bee.param.eject", "ejectHeldBees");
        pTargetSpecies = new StringParameter("", "otht.bee.param.species", "targetSpecies");
        pOutputKind = new EnumParameter<>(
            OTHBeeyondsOutputKind.class,
            OTHBeeyondsOutputKind.QUEEN,
            "otht.bee.param.outputKind",
            "outputKind");
        pSpeed = new EnumParameter<>(
            EnumAllele.Speed.class,
            EnumAllele.Speed.NORMAL,
            "otht.bee.param.speed",
            "traitSpeed");
        pFertility = new EnumParameter<>(
            EnumAllele.Fertility.class,
            EnumAllele.Fertility.NORMAL,
            "otht.bee.param.fertility",
            "traitFertility");
        pLifespan = new EnumParameter<>(
            EnumAllele.Lifespan.class,
            EnumAllele.Lifespan.NORMAL,
            "otht.bee.param.lifespan",
            "traitLifespan");
        pFlowering = new EnumParameter<>(
            EnumAllele.Flowering.class,
            EnumAllele.Flowering.AVERAGE,
            "otht.bee.param.flowering",
            "traitFlowering");
        pTerritory = new EnumParameter<>(
            EnumAllele.Territory.class,
            EnumAllele.Territory.AVERAGE,
            "otht.bee.param.territory",
            "traitTerritory");
        pTempTolerance = new EnumParameter<>(
            EnumAllele.Tolerance.class,
            EnumAllele.Tolerance.NONE,
            "otht.bee.param.tempTolerance",
            "traitTempTolerance");
        pHumidTolerance = new EnumParameter<>(
            EnumAllele.Tolerance.class,
            EnumAllele.Tolerance.NONE,
            "otht.bee.param.humidTolerance",
            "traitHumidTolerance");
        pNocturnal = new BooleanParameter(false, "otht.bee.param.nocturnal", "traitNocturnal");
        pTolerantFlyer = new BooleanParameter(false, "otht.bee.param.tolerantFlyer", "traitTolerantFlyer");
        pCaveDwelling = new BooleanParameter(false, "otht.bee.param.caveDwelling", "traitCaveDwelling");
        pEffect = new StringParameter("", "otht.bee.param.effect", "traitEffect");
        pFlowerProvider = new StringParameter("", "otht.bee.param.flowerProvider", "traitFlowerProvider");

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

        // Everything EXCEPT pTargetSpecies - that one is driven entirely by the cycle-button picker
        // in OTHBeeyondsGui (only ever one of the currently valid options for the held parents), so
        // letting the player free-type an arbitrary species UID into the generic TecTech settings
        // panel would just offer a second, unvalidated way to set the same value. saveNBTData()/
        // loadNBTData() persist the FULL mParameters list directly (not this one), so pTargetSpecies
        // still saves/loads correctly - only its visibility in the settings panel is affected.
        mSettingsPanelParameters = Arrays.asList(
            pMode,
            pEjectHeldBees,
            pOutputKind,
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

    /**
     * Used by TecTech's generic settings panel (and by {@code TTMultiblockBase}'s own redundant
     * parameter persistence) - deliberately NOT the same list {@link #saveNBTData}/
     * {@link #loadNBTData} persist ({@code mParameters}, the full list): see the comment on
     * {@link #mSettingsPanelParameters} in {@link #initParameters()} for why {@code pTargetSpecies}
     * is excluded here specifically.
     */
    @Override
    public List<Parameter<?, ?>> getParameters() {
        return mSettingsPanelParameters;
    }

    private OTHBeeyondsMode getMode() {
        return pMode.getValue() == null ? OTHBeeyondsMode.PRODUCTION : pMode.getValue();
    }

    /** Exposed so {@link OTHBeeyondsGui} can drive its own mode-cycle button off the same state. */
    public EnumParameter<OTHBeeyondsMode> getModeParameter() {
        return pMode;
    }

    /**
     * The mode actually in effect right now, as opposed to {@link #pMode} (what the player last
     * requested - see {@link #mActiveMode}'s own field comment for why the two can briefly
     * disagree). Unlike {@code pMode} (a TecTech {@link Parameter}, whose client-side copy only
     * gets its real value once the GUI that's opening registers its own sync handlers - AFTER the
     * GUI's constructor already ran), {@code mActiveMode} is a plain field loaded through the
     * TileEntity's normal NBT/description-packet sync, so it's already correct by the time a GUI
     * is being built. {@link #getStatusText()} already relies on this same field for exactly that
     * reason - {@link OTHBeeyondsGui} should pick its content (queen grid vs. parent slots) off
     * this, not {@code getModeParameter().getValue()}, or the two can show different modes.
     */
    public OTHBeeyondsMode getActiveMode() {
        return mActiveMode;
    }

    /**
     * Drives {@link OTHBeeyondsGui}'s single mode-content trigger ({@code activeModeSync} there) -
     * not a valid {@link OTHBeeyondsMode} ordinal by itself, only a value that changes whenever the
     * panel needs a rebuild: on a genuine mode switch, or (in Breed mode) whenever the parent species
     * pairing changes, since {@link OTHBeeyondsGui#createTargetSpeciesWidget} needs a full rebuild to
     * pick up a new option count. Folded into this SAME int rather than a second sync-registered
     * value, since registering a second top-level sync value on that panel breaks it from opening.
     */
    public int getModeContentTrigger() {
        if (mActiveMode != OTHBeeyondsMode.BREED) return 0;
        IAlleleBeeSpecies species0 = parentSpecies(mParent0);
        IAlleleBeeSpecies species1 = parentSpecies(mParent1);
        int hash0 = species0 == null ? 0
            : species0.getUID()
                .hashCode();
        int hash1 = species1 == null ? 0
            : species1.getUID()
                .hashCode();
        return 1 + 31 * hash0 + hash1;
    }

    /** Exposed so {@link OTHBeeyondsGui} can drive its own eject button off the same state. */
    public BooleanParameter getEjectParameter() {
        return pEjectHeldBees;
    }

    /**
     * Live one-line status text shown directly on the main controller panel (like GTNH's own Industrial
     * Apiary / Mega Industrial Apiary and Space Elevator controllers show their own live state) - since
     * this machine never uses GT5's normal recipe map, the generic "Processing recipe / Progress" box
     * has nothing real to show otherwise. Called from the server side only (wrapped in a
     * {@code StringSyncValue} by {@link OTHBeeyondsGui}, which syncs the returned text to the client).
     */
    public String getStatusText() {
        if (mActiveMode == OTHBeeyondsMode.PRODUCTION) {
            int held = 0;
            for (QueenCell cell : mQueenCells) {
                if (cell.queenStack != null) held++;
            }
            return String.format(translateToLocal("otht.bee.gui.status.production"), held, mQueenCells.size());
        }
        if (mParent0 == null || mParent1 == null) {
            return translateToLocal("otht.bee.gui.status.breed.waiting");
        }
        if (targetSpeciesOptions().isEmpty()) {
            return translateToLocal("otht.bee.gui.status.breed.noMutation");
        }
        int percent = BREED_TIME_TICKS <= 0 ? 100 : Math.min(100, mBreedProgress * 100 / BREED_TIME_TICKS);
        return String.format(translateToLocal("otht.bee.gui.status.breed.progress"), percent);
    }

    // region effect/flower pickers (Breed mode)
    // pEffect/pFlowerProvider still store a raw allele UID string (so NBT/settings-panel text entry
    // keeps working for anyone who already knows one), but typing UIDs blind isn't a usable way to
    // pick a trait - these index-based helpers back a proper cycle-button picker in OTHBeeyondsGui
    // that lists every effect/flower provider actually registered in this Forestry instance (so the
    // options always match whatever bee mods are loaded) instead of leaving it to guesswork.
    private List<IAlleleBeeEffect> mEffectOptions;
    private List<IAlleleFlowers> mFlowerOptions;

    private List<IAlleleBeeEffect> effectOptions() {
        if (mEffectOptions == null) {
            mEffectOptions = new ArrayList<>();
            mEffectOptions.add(null); // index 0 - keep the mutation's own default effect
            for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles(EnumBeeChromosome.EFFECT)) {
                if (allele instanceof IAlleleBeeEffect) mEffectOptions.add((IAlleleBeeEffect) allele);
            }
        }
        return mEffectOptions;
    }

    private List<IAlleleFlowers> flowerOptions() {
        if (mFlowerOptions == null) {
            mFlowerOptions = new ArrayList<>();
            mFlowerOptions.add(null); // index 0 - keep the mutation's own default flower provider
            for (IAllele allele : AlleleManager.alleleRegistry
                .getRegisteredAlleles(EnumBeeChromosome.FLOWER_PROVIDER)) {
                if (allele instanceof IAlleleFlowers) mFlowerOptions.add((IAlleleFlowers) allele);
            }
        }
        return mFlowerOptions;
    }

    public int getEffectOptionCount() {
        return effectOptions().size();
    }

    public int getEffectSelectionIndex() {
        List<IAlleleBeeEffect> options = effectOptions();
        String current = pEffect.getValue();
        if (current == null || current.isEmpty()) return 0;
        for (int i = 1; i < options.size(); i++) {
            if (options.get(i)
                .getUID()
                .equals(current)) return i;
        }
        return 0;
    }

    public void setEffectSelectionIndex(int index) {
        List<IAlleleBeeEffect> options = effectOptions();
        if (index < 0 || index >= options.size()) return;
        IAlleleBeeEffect option = options.get(index);
        pEffect.setValue(option == null ? "" : option.getUID());
    }

    public String getEffectOptionName(int index) {
        List<IAlleleBeeEffect> options = effectOptions();
        if (index < 0 || index >= options.size()) return "";
        IAlleleBeeEffect option = options.get(index);
        return option == null ? translateToLocal("otht.bee.gui.effect.none") : option.getName();
    }

    public int getFlowerOptionCount() {
        return flowerOptions().size();
    }

    public int getFlowerSelectionIndex() {
        List<IAlleleFlowers> options = flowerOptions();
        String current = pFlowerProvider.getValue();
        if (current == null || current.isEmpty()) return 0;
        for (int i = 1; i < options.size(); i++) {
            if (options.get(i)
                .getUID()
                .equals(current)) return i;
        }
        return 0;
    }

    public void setFlowerSelectionIndex(int index) {
        List<IAlleleFlowers> options = flowerOptions();
        if (index < 0 || index >= options.size()) return;
        IAlleleFlowers option = options.get(index);
        pFlowerProvider.setValue(option == null ? "" : option.getUID());
    }

    public String getFlowerOptionName(int index) {
        List<IAlleleFlowers> options = flowerOptions();
        if (index < 0 || index >= options.size()) return "";
        IAlleleFlowers option = options.get(index);
        return option == null ? translateToLocal("otht.bee.gui.flower.none") : option.getName();
    }
    // endregion

    // region target species picker (Breed mode)
    // Same index-based picker pattern as effect/flower above, but the option list itself is NOT
    // static: it's recomputed from whichever two bees are currently held as Breed-mode parents (see
    // tickBreed()) - "target species" only ever makes sense relative to that specific pairing, either
    // the shared species itself (purebred pairing) or one of the DIFFERENT species Forestry has an
    // actual registered mutation for between those two exact species. Nothing else can ever be
    // produced by findMutation()/tickBreed() regardless of what pTargetSpecies holds, so restricting
    // the picker to this list keeps it from ever offering (or silently accepting) an impossible target.
    private IAlleleBeeSpecies parentSpecies(ItemStack parentStack) {
        if (parentStack == null) return null;
        IBee bee = BeeManager.beeRoot.getMember(parentStack);
        return bee == null ? null
            : bee.getGenome()
                .getPrimary();
    }

    private List<IAlleleBeeSpecies> targetSpeciesOptions() {
        List<IAlleleBeeSpecies> options = new ArrayList<>();
        IAlleleBeeSpecies species0 = parentSpecies(mParent0);
        IAlleleBeeSpecies species1 = parentSpecies(mParent1);
        if (species0 == null || species1 == null) return options;

        if (species0.getUID()
            .equals(species1.getUID())) {
            options.add(species0);
            return options;
        }

        for (IBeeMutation mutation : BeeManager.beeRoot.getMutations(false)) {
            boolean matchesPair = (mutation.getAllele0() == species0 && mutation.getAllele1() == species1)
                || (mutation.getAllele0() == species1 && mutation.getAllele1() == species0);
            if (!matchesPair) continue;
            IAllele resultAllele = mutation.getTemplate()[EnumBeeChromosome.SPECIES.ordinal()];
            if (resultAllele instanceof IAlleleBeeSpecies && !options.contains(resultAllele)) {
                options.add((IAlleleBeeSpecies) resultAllele);
            }
        }
        return options;
    }

    /** Always at least 1 so a picker widget never has to handle a zero-state count. */
    public int getTargetSpeciesOptionCount() {
        return Math.max(1, targetSpeciesOptions().size());
    }

    public int getTargetSpeciesSelectionIndex() {
        List<IAlleleBeeSpecies> options = targetSpeciesOptions();
        String current = pTargetSpecies.getValue();
        if (current == null || current.isEmpty() || options.isEmpty()) return 0;
        for (int i = 0; i < options.size(); i++) {
            if (options.get(i)
                .getUID()
                .equals(current)) return i;
        }
        return 0;
    }

    public void setTargetSpeciesSelectionIndex(int index) {
        List<IAlleleBeeSpecies> options = targetSpeciesOptions();
        if (index < 0 || index >= options.size()) return;
        pTargetSpecies.setValue(
            options.get(index)
                .getUID());
    }

    public String getTargetSpeciesOptionName(int index) {
        List<IAlleleBeeSpecies> options = targetSpeciesOptions();
        if (index < 0 || index >= options.size()) {
            return translateToLocal("otht.bee.gui.species.none");
        }
        return options.get(index)
            .getName();
    }
    // endregion

    /**
     * Read-only queen slot grid on the main panel - like GTNH's own Industrial Apiary/Mega Industrial
     * Apiary, which shows every queen it's currently working instead of hiding them entirely. The GUI
     * caps the visible grid at a fixed size; at higher tiers than that grid can show, the extra queens
     * still work normally, they just aren't individually pictured.
     */
    public ItemStack getQueenDisplayStack(int index) {
        if (index < 0 || index >= mQueenCells.size()) return null;
        ItemStack stack = mQueenCells.get(index).queenStack;
        // MUST be a defensive copy: the GUI's GenericSyncValue caches whatever this getter returns
        // (with ICopy.immutable(), meaning it keeps the exact reference, no copy of its own) and
        // pushes it through ModularUI2's network sync every tick the panel is open. Handing out the
        // SAME live ItemStack the production tick is actively reading/writing (age(), NBT rewrites on
        // cycle completion) let the GUI layer race with and corrupt the real simulation state - a held
        // queen was observed vanishing mid-cycle (well before her 550-tick work cycle or any aging)
        // only while her slot was visible in this grid. Returning a copy here keeps the display fully
        // decoupled from the actual bee being simulated.
        return stack == null ? null : stack.copy();
    }

    /**
     * Read-only display of a held Breed-mode parent (0 or 1) for the GUI - same copy-safety rule as
     * {@link #getQueenDisplayStack}: never hand out the live {@code mParent0}/{@code mParent1}
     * reference to the GUI sync layer.
     */
    public ItemStack getParentDisplayStack(int index) {
        ItemStack stack = index == 0 ? mParent0 : index == 1 ? mParent1 : null;
        return stack == null ? null : stack.copy();
    }

    /**
     * Global production preview tooltip for the main panel - one "Producing X x<i>N</i> in <i>T</i>s"
     * line per distinct product any currently-held queen can make, quantities summed across every queen
     * that can produce it and the time being the soonest of theirs (same idea as GTNH's Steam Space
     * Elevator showing one countdown for the whole machine, not a separate one per component). Not a
     * per-queen tooltip - deliberately aggregated across all of {@link #mQueenCells}, since each
     * individual queen's own countdown/products aren't meant to be inspected one by one here. Not every
     * product actually lands each cycle (each has its own independent chance) - this is "what the
     * machine is working towards producing next", not a guarantee. Lines are newline-separated; the GUI
     * splits on {@code \n} into separate tooltip lines. Computed server-side (like
     * {@link #getStatusText()}) since the client's own {@code mQueenCells} isn't ticked/populated.
     */
    public String getProductionPreviewTooltip() {
        // LinkedHashMap: first-seen order (roughly cell order) is a stable, deterministic tooltip
        // ordering instead of shuffling every time products get re-aggregated.
        java.util.Map<String, Object[]> aggregate = new java.util.LinkedHashMap<>();
        for (QueenCell cell : mQueenCells) {
            if (cell.queenStack == null) continue;
            IBee bee = BeeManager.beeRoot.getMember(cell.queenStack);
            if (bee == null) continue;
            int ticksRemaining = Math.max(0, QUEEN_WORK_CYCLE_TICKS - cell.workCycleThrottle);
            float secondsRemaining = ticksRemaining / 20.0F;
            for (ItemStack product : bee.getProduceList()) {
                aggregateProduct(aggregate, product, secondsRemaining);
            }
            for (ItemStack specialty : bee.getSpecialtyList()) {
                aggregateProduct(aggregate, specialty, secondsRemaining);
            }
        }

        if (aggregate.isEmpty()) return translateToLocal("otht.bee.gui.queen.tooltip.noProducts");
        StringBuilder text = new StringBuilder();
        for (Object[] entry : aggregate.values()) {
            if (text.length() > 0) text.append('\n');
            text.append(
                String.format(translateToLocal("otht.bee.gui.queen.tooltip.producing"), entry[0], entry[1], entry[2]));
        }
        return text.toString();
    }

    /** entry = {displayName (String), summed quantity (Integer), soonest seconds remaining (Float)} */
    private void aggregateProduct(java.util.Map<String, Object[]> aggregate, ItemStack product,
        float secondsRemaining) {
        if (product == null) return;
        String key = product.getUnlocalizedName() + ":" + product.getItemDamage();
        Object[] entry = aggregate.get(key);
        if (entry == null) {
            aggregate.put(key, new Object[] { product.getDisplayName(), product.stackSize, secondsRemaining });
        } else {
            entry[1] = (Integer) entry[1] + product.stackSize;
            entry[2] = Math.min((Float) entry[2], secondsRemaining);
        }
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new OTHBeeyondsGui(this);
    }
    // endregion

    // region tick logic
    /**
     * {@code onRunningTick()} is only ever invoked by the base class while {@code mMaxProgresstime > 0}
     * (see {@code TTMultiblockBase#onPostTick}) - i.e. while a recipe is "in progress". Beeyonds has no
     * real recipe map, so without this override {@code checkProcessing_EM()} would fall back to the
     * default no-op {@code checkRecipe_EM(ItemStack)} (always {@code false}), {@code mMaxProgresstime}
     * would stay 0 forever, and {@code onRunningTick} - and therefore the whole bee simulation - would
     * never run at all (machine sits idle showing "No valid recipe found", input hatches never drained).
     * Forcing a permanent 1-tick "recipe" here restores the simple every-tick semantics the rest of
     * this class is written against. {@code checkProcessing()} itself is {@code final} on
     * {@link TTMultiblockBase} - {@code checkProcessing_EM()} is the actual override point it delegates to.
     */
    @Override
    protected CheckRecipeResult checkProcessing_EM() {
        mMaxProgresstime = 1;
        mEUt = 0;
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

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

    /**
     * Named GT voltage tier (LV=1, MV=2, HV=3, EV=4, ...) of the single highest-voltage energy hatch
     * present - deliberately NOT based on {@link #getMaxInputVoltageSum()} (which adds up every
     * hatch's voltage together): two HV hatches summing past the HV threshold must still count as
     * "HV tier", not silently promote the machine to EV-tier slot counts just because more amperage is
     * plugged in. Hatch count should only affect throughput/EU-t, never which tier this machine reads
     * as.
     */
    private int currentTier() {
        long highestVoltage = 0;
        for (MTEHatchEnergy hatch : mEnergyHatches) {
            highestVoltage = Math.max(highestVoltage, hatch.maxEUInput());
        }
        return Math.max(1, GTUtility.getTier(highestVoltage));
    }

    /**
     * Doubles per named GT tier (LV=4, MV=8, HV=16, EV=32, IV=64, ...) rather than scaling linearly -
     * matches how most GT5 multiblocks reward higher voltage tiers with a step change in capability.
     */
    private int queenSlotCount() {
        return QUEEN_SLOTS_PER_TIER << (currentTier() - 1);
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
            flushPendingProducts(last);
            if ((last.queenStack != null && !addOutputAtomic(last.queenStack)) || !last.pendingProducts.isEmpty()) {
                break;
            }
            last.queenStack = null;
            mQueenCells.remove(mQueenCells.size() - 1);
        }

        IBeeRoot root = BeeManager.beeRoot;

        int heldQueens = 0;
        for (QueenCell cell : mQueenCells) {
            // Retried every tick regardless of whether this cell currently holds a live queen - a
            // comb that couldn't fit in the output bus the tick it was produced must never be voided,
            // even if her queen already died or got ejected in the meantime.
            flushPendingProducts(cell);
            if (cell.queenStack == null) {
                cell.queenStack = pullQueenFromInputs(root);
            }
            if (cell.queenStack != null) heldQueens++;
        }
        // Fixed EU/t cost per held queen, drawn straight from the buffered EU the energy hatches keep
        // topped up (same store getEUVar()/setEUVar() already track) - if there isn't enough to pay
        // every queen this tick, none of them work (no partial-payment/undercharging), so a
        // brownout is visible instead of silently rationing production.
        long powerCost = EU_COST_PER_QUEEN * heldQueens;
        boolean hasPower = powerCost <= 0 || getEUVar() >= powerCost;
        if (hasPower && powerCost > 0) {
            setEUVar(getEUVar() - powerCost);
        }

        if (!hasPower) return;

        for (QueenCell cell : mQueenCells) {
            if (cell.queenStack == null) continue;
            IBee bee = root.getMember(cell.queenStack);
            tickQueenCell(cell, bee);
        }
    }

    /**
     * Minimal, self-written replacement for Forestry's own {@code BeekeepingLogic#doWork()} - that
     * bundles honey/comb production together with {@code IBee.doEffect()} (bee special effects, fired
     * every single tick) and {@code IBee.plantFlowerRandom()} (placing real flower blocks in the world)
     * with no way to opt out of either individually. Beeyonds Home is meant to be a clean, self-
     * contained production line, not something that spontaneously alters the world around it or fires
     * unpredictable bee effects (explosions, ignition, mycophilic mushroom spawns, etc.) - so this only
     * calls the parts of Forestry's public {@link IBee} API needed for production and aging
     * ({@link IBee#produceStacks}, {@link IBee#age}), skipping doEffect/plantFlowerRandom/pollination
     * entirely. {@code bee.getCanWork(cell)} is still consulted, but only for
     * {@code NO_SPACE_INVENTORY} - everything else it might report (biome, temperature, flowers, ...)
     * is a real-world requirement this virtual apiary ignores by design.
     */
    private void tickQueenCell(QueenCell cell, IBee bee) {
        if (bee == null) return;
        if (!bee.isAlive()) {
            // no death products beyond whatever her own production already sent to output - matches
            // "no side effects/no spawns", just plain combs, and frees the cell for a fresh queen.
            cell.queenStack = null;
            return;
        }
        if (bee.getCanWork(cell)
            .contains(EnumErrorCode.NO_SPACE_INVENTORY)) {
            return; // output full - retry next tick, never voids the queen or her progress
        }

        cell.workCycleThrottle++;
        if (cell.workCycleThrottle < QUEEN_WORK_CYCLE_TICKS) return;
        cell.workCycleThrottle = 0;

        for (ItemStack product : bee.produceStacks(cell)) {
            if (product != null) cell.pendingProducts.add(product);
        }
        flushPendingProducts(cell);
        bee.age(getBaseMetaTileEntity().getWorld(), 1.0F);
        NBTTagCompound agedNbt = new NBTTagCompound();
        bee.writeToNBT(agedNbt);
        cell.queenStack.setTagCompound(agedNbt);
    }

    /**
     * Retries every product a queen has ever produced but couldn't yet fit into an output bus,
     * oldest first, stopping at the first one that still doesn't fit (so ordering is preserved and a
     * later, smaller product can't jump the queue past an earlier one still waiting for space). Mirrors
     * the same "never void a bee, only ever retry" rule {@link #ejectAllQueens()}/{@link #ejectParents()}
     * already apply to held bees - {@code addOutputAtomic}'s return value being silently ignored for
     * produced combs was the actual reason production could look like it "never comes out": a comb
     * produced while the output bus happened to be full was previously discarded on the spot instead of
     * being retried.
     */
    private void flushPendingProducts(QueenCell cell) {
        while (!cell.pendingProducts.isEmpty()) {
            ItemStack product = cell.pendingProducts.get(0);
            if (!addOutputAtomic(product)) break;
            cell.pendingProducts.remove(0);
        }
    }

    private ItemStack pullQueenFromInputs(IBeeRoot root) {
        for (ItemStack stack : getStoredInputs()) {
            // getStoredInputs() checks for null but not for an already-drained (stackSize <= 0) slot -
            // vanilla ItemStack.splitStack(1) has no bounds check either, it happily decrements below
            // zero and hands back a brand new size-1 stack regardless. Skipping depleted stacks here
            // (BEFORE splitting, not after) is what stops a single real queen from getting "split" over
            // and over into a full grid of phantom duplicates whenever the hatch doesn't null out the
            // slot the instant it empties.
            if (stack == null || stack.stackSize <= 0) continue;
            if (!root.isMember(stack)) continue;
            if (root.getType(stack) != EnumBeeType.QUEEN) continue;
            return stack.splitStack(1);
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
        boolean sameSpecies = species0.getUID()
            .equals(species1.getUID());

        // Two bees of the SAME species is plain purebred breeding, not a mutation - Forestry never
        // registers a mutation entry for a species paired with itself (getMutations() only lists
        // actual cross-species mutations), so findMutation() below always returned null for this,
        // the by far most common case (e.g. Forest Princess x Forest Drone), and the pair was
        // ejected the instant a mutation lookup happened rather than ever breeding. Pull the base
        // template straight from the species' own registered template instead of a mutation's.
        IAllele[] baseTemplate;
        if (sameSpecies) {
            baseTemplate = BeeManager.beeRoot.getTemplate(species0.getUID());
            if (baseTemplate == null) {
                returnParents();
                return;
            }
        } else {
            IBeeMutation mutation = findMutation(species0, species1);
            if (mutation == null) {
                // No registered mutation between these two species - HOLD the parents instead of
                // auto-ejecting every tick (that used to make the pair vanish the instant an
                // unsupported combo was inserted, with no chance to swap the target species picker
                // or even see what's wrong). Auto-eject is reserved for a genuinely unreadable
                // parent stack (bee0/bee1 == null above) or an explicit mode switch/manual eject
                // (drainActiveMode()) - never "just no mutation yet".
                mBreedProgress = 0;
                return;
            }
            baseTemplate = mutation.getTemplate();
        }

        mBreedProgress++;
        if (mBreedProgress < BREED_TIME_TICKS) {
            return;
        }

        // Deliberately NOT clearing mBreedProgress yet, and NOT clearing the parents until the
        // result actually makes it into an output bus (checked below) - both used to happen
        // unconditionally right after building the result stack, regardless of whether
        // addOutputAtomic() succeeded. With the output bus full, that silently voided the freshly
        // bred queen/drones AND consumed the parents for nothing, every single tick the output
        // stayed full. The template is fully deterministic (pristine homozygous, no randomness), so
        // recomputing the same result stack next tick and retrying is safe.
        IAllele[] template = buildCustomTemplate(baseTemplate);
        ItemStack resultStack;
        if (pOutputKind.getValue() == OTHBeeyondsOutputKind.DRONES || sameSpecies) {
            int fertility = pFertility.getValue()
                .getValue();
            int amount = Math.min(MAX_DRONES_PER_CYCLE, Math.max(1, Math.round(fertility * BREED_YIELD_MULTIPLIER)));
            IBee droneIndividual = (IBee) root.templateAsIndividual(template);
            resultStack = root.getMemberStack(droneIndividual, EnumBeeType.DRONE.ordinal());
            resultStack.stackSize = amount;
        } else {
            IBee mate = (IBee) root.templateAsIndividual(template);
            IBee queen = root.getBee(getBaseMetaTileEntity().getWorld(), mate.getGenome(), mate);
            resultStack = root.getMemberStack(queen, EnumBeeType.QUEEN.ordinal());
        }

        if (!addOutputAtomic(resultStack)) {
            return; // output full - retry next tick, never voids the result or the parents
        }

        mBreedProgress = 0;
        mParent0 = null;
        mParent1 = null;
    }

    /**
     * Returns both parents to the output when one of them turns out to be an unreadable/corrupted
     * bee stack (not simply "no mutation between them" - see {@link #tickBreed()}, which holds the
     * pair instead of ejecting for that case). Uses
     * the same safe eject as {@link #ejectParents()} - if the output is full, the parents stay held
     * (and are retried next tick) instead of being voided.
     */
    private void returnParents() {
        ejectParents();
    }

    private ItemStack pullParentFromInputs(IBeeRoot root) {
        for (ItemStack stack : getStoredInputs()) {
            // see pullQueenFromInputs() - must skip an already-drained stack BEFORE splitting from it.
            if (stack == null || stack.stackSize <= 0) continue;
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
     * the given base template (a mutation's result template, or a plain species template for purebred
     * pairings - see {@link #tickBreed()}), overriding every chromosome with the player's chosen alleles
     * from the settings panel.
     */
    private IAllele[] buildCustomTemplate(IAllele[] baseTemplate) {
        IAllele[] template = baseTemplate.clone();

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
        // mirrors Forestry's own BeekeepingLogic#queenWorkCycleThrottle pacing (550 ticks, ~27.5s)
        private int workCycleThrottle = 0;
        // combs produced but not yet delivered to an output bus (retried every tick by
        // flushPendingProducts() until they fit) - never voided, even if this cell's queen dies or
        // gets ejected before they're delivered.
        private final List<ItemStack> pendingProducts = new ArrayList<>();

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

    // doRandomMaintenanceDamage() intentionally left at MTEMultiBlockBase's default - it used to be
    // overridden here to unconditionally `return false`, presumably meaning "don't wear down over
    // time". But GT5's own tick loop (MTEMultiBlockBase#runMachine / TTMultiblockBase#onPostTick)
    // dual-purposes this same boolean as the master gate for whether onRunningTick() gets called AT
    // ALL this tick (`if (mMaxProgresstime > 0 && doRandomMaintenanceDamage()) onRunningTick(...)`).
    // Returning false unconditionally therefore silently disabled the entire bee simulation forever -
    // no queen/parent was ever pulled from the input bus and nothing was ever produced, regardless of
    // structure/power/mode state. The default implementation already skips real wear/repair
    // requirements here (gated by shouldCheckMaintenance(), which getDefaultHasMaintenanceChecks()
    // below keeps false) while still returning true so the tick actually runs.

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
