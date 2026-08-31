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
    private final EnumParameter<OTHBeeyondsMode> pMode = new EnumParameter<>(
        OTHBeeyondsMode.class,
        OTHBeeyondsMode.PRODUCTION,
        "mode",
        "otht.bee.param.mode");
    private final StringParameter pTargetSpecies = new StringParameter("", "targetSpecies", "otht.bee.param.species");
    private final EnumParameter<OTHBeeyondsOutputKind> pOutputKind = new EnumParameter<>(
        OTHBeeyondsOutputKind.class,
        OTHBeeyondsOutputKind.QUEEN,
        "outputKind",
        "otht.bee.param.outputKind");
    private final EnumParameter<EnumAllele.Speed> pSpeed = new EnumParameter<>(
        EnumAllele.Speed.class,
        EnumAllele.Speed.NORMAL,
        "traitSpeed",
        "otht.bee.param.speed");
    private final EnumParameter<EnumAllele.Fertility> pFertility = new EnumParameter<>(
        EnumAllele.Fertility.class,
        EnumAllele.Fertility.NORMAL,
        "traitFertility",
        "otht.bee.param.fertility");
    private final EnumParameter<EnumAllele.Lifespan> pLifespan = new EnumParameter<>(
        EnumAllele.Lifespan.class,
        EnumAllele.Lifespan.NORMAL,
        "traitLifespan",
        "otht.bee.param.lifespan");
    private final EnumParameter<EnumAllele.Flowering> pFlowering = new EnumParameter<>(
        EnumAllele.Flowering.class,
        EnumAllele.Flowering.AVERAGE,
        "traitFlowering",
        "otht.bee.param.flowering");
    private final EnumParameter<EnumAllele.Territory> pTerritory = new EnumParameter<>(
        EnumAllele.Territory.class,
        EnumAllele.Territory.AVERAGE,
        "traitTerritory",
        "otht.bee.param.territory");
    private final EnumParameter<EnumAllele.Tolerance> pTempTolerance = new EnumParameter<>(
        EnumAllele.Tolerance.class,
        EnumAllele.Tolerance.NONE,
        "traitTempTolerance",
        "otht.bee.param.tempTolerance");
    private final EnumParameter<EnumAllele.Tolerance> pHumidTolerance = new EnumParameter<>(
        EnumAllele.Tolerance.class,
        EnumAllele.Tolerance.NONE,
        "traitHumidTolerance",
        "otht.bee.param.humidTolerance");
    private final BooleanParameter pNocturnal = new BooleanParameter(
        false,
        "traitNocturnal",
        "otht.bee.param.nocturnal");
    private final BooleanParameter pTolerantFlyer = new BooleanParameter(
        false,
        "traitTolerantFlyer",
        "otht.bee.param.tolerantFlyer");
    private final BooleanParameter pCaveDwelling = new BooleanParameter(
        false,
        "traitCaveDwelling",
        "otht.bee.param.caveDwelling");
    private final StringParameter pEffect = new StringParameter("", "traitEffect", "otht.bee.param.effect");
    private final StringParameter pFlowerProvider = new StringParameter(
        "",
        "traitFlowerProvider",
        "otht.bee.param.flowerProvider");

    private List<Parameter<?, ?>> mParameters;
    // endregion

    // internal state, not part of the GT structure - purely virtual bookkeeping
    private final List<QueenCell> mQueenCells = new ArrayList<>();
    private ItemStack mParent0;
    private ItemStack mParent1;
    private int mBreedProgress = 0;
    private GameProfile mCachedOwner;

    @Override
    public void checkMachine(IGregTechTileEntity iGregTechTileEntity, ItemStack itemStack,
        List<StructureError> errors) {
        if (!checkPiece("main", 0, 1, 0, errors)) return;
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
            if (getMode() == OTHBeeyondsMode.PRODUCTION) {
                tickProduction();
            } else {
                tickBreed();
            }
        }
        return true;
    }

    private int currentTier() {
        return Math.max(1, GTUtility.getTier(getMaxInputVoltageSum()));
    }

    private int queenSlotCount() {
        return currentTier() * QUEEN_SLOTS_PER_TIER;
    }

    private void tickProduction() {
        int wanted = queenSlotCount();
        while (mQueenCells.size() < wanted) {
            mQueenCells.add(new QueenCell());
        }
        while (mQueenCells.size() > wanted) {
            QueenCell removed = mQueenCells.remove(mQueenCells.size() - 1);
            if (removed.queenStack != null) {
                addOutputAtomic(removed.queenStack);
                removed.queenStack = null;
            }
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

    private void returnParents() {
        if (mParent0 != null) addOutputAtomic(mParent0);
        if (mParent1 != null) addOutputAtomic(mParent1);
        mParent0 = null;
        mParent1 = null;
        mBreedProgress = 0;
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
        buildPiece("main", stackSize, hintsOnly, 0, 1, 0);
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
