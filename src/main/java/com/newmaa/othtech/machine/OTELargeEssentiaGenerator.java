package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.defer;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofSpecificTileAdder;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.withChannel;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.common.compat.thaumcraft.EssentiaGeneratorCompat;
import com.newmaa.othtech.machine.gui.OTELargeEssentiaGeneratorGui;
import com.newmaa.othtech.machine.hatch.OTEEssentiaInputHatch;
import com.newmaa.othtech.machine.hatch.OTEEssentiaStockingInputBusME;
import com.newmaa.othtech.utils.ShapeLoader;

import goodgenerator.loader.Loaders;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchInputDebug;
import gregtech.api.modularui2.GTGuiTheme;
import gregtech.api.modularui2.GTGuiThemes;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrors;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.config.ConfigBlocks;

public final class OTELargeEssentiaGenerator extends TTMultiblockBase implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "reactor";
    private static final String SHAPE_PATH = "/assets/123technology/structures/ote/OTELargeEssentiaGenerator_shapeMain.shape.gz";
    private static final int HORIZONTAL_OFFSET = 4;
    private static final int VERTICAL_OFFSET = 0;
    private static final int DEPTH_OFFSET = 4;
    private static final int CASING_TEXTURE = 1536;
    private static final int[] STABILITY = { 1, 2, 5, 10 };
    private static final int[] HATCH_TIERS = { 4, 5, 6, 8 };
    private static final int MAX_STRUCTURE_TIER = STABILITY.length;
    private static final int DIFFUSION_CELLS = 25;
    private static final int MAX_STABILITY = DIFFUSION_CELLS * STABILITY[MAX_STRUCTURE_TIER - 1];
    private static IStructureDefinition<OTELargeEssentiaGenerator> STRUCTURE_DEFINITION;

    private final List<OTEEssentiaInputHatch> intakes = new ArrayList<>();
    private final List<OTEEssentiaStockingInputBusME> networkIntakes = new ArrayList<>();
    private AspectList bufferedEssentia = new AspectList();
    private int stability;
    private int structureTier;
    private int families = 1;
    private long reserveEU;
    private EssentiaGeneratorFuel.Family lastFuelFamily;
    private String lastReagent = "";
    private String burnReagent = "";

    public OTELargeEssentiaGenerator(int id, String name, String display) {
        super(id, name, display);
        useLongPower = true;
    }

    private OTELargeEssentiaGenerator(String name) {
        super(name);
        useLongPower = true;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity base) {
        return new OTELargeEssentiaGenerator(mName);
    }

    @Override
    public void saveNBTData(NBTTagCompound tag) {
        super.saveNBTData(tag);
        tag.setInteger("fuelFamilies", families);
        tag.setLong("reserveEU", reserveEU);
        NBTTagCompound buffer = new NBTTagCompound();
        bufferedEssentia.writeToNBT(buffer);
        tag.setTag("essentiaBuffer", buffer);
    }

    @Override
    public void loadNBTData(NBTTagCompound tag) {
        super.loadNBTData(tag);
        families = tag.getInteger("fuelFamilies") | 1;
        reserveEU = Math.max(0L, tag.getLong("reserveEU"));
        bufferedEssentia = new AspectList();
        bufferedEssentia.readFromNBT(tag.getCompoundTag("essentiaBuffer"));
    }

    private static IStructureElement<OTELargeEssentiaGenerator> cell(int meta) {
        return onElementPass(machine -> {
            machine.stability += STABILITY[meta];
            machine.structureTier = Math.max(machine.structureTier, meta + 1);
        }, ofBlock(Loaders.essentiaCell, meta));
    }

    @Override
    public IStructureDefinition<OTELargeEssentiaGenerator> getStructure_EM() {
        if (STRUCTURE_DEFINITION == null) {
            IStructureElement<OTELargeEssentiaGenerator> casing = ofBlock(Loaders.magicCasing, 0);
            List<IStructureElement<OTELargeEssentiaGenerator>> cells = Arrays
                .asList(cell(0), cell(1), cell(2), cell(3));
            IStructureElement<OTELargeEssentiaGenerator> mixedCells = ofChain(cells);
            STRUCTURE_DEFINITION = StructureDefinition.<OTELargeEssentiaGenerator>builder()
                .addShape(STRUCTURE_PIECE_MAIN, ShapeLoader.loadCompressed(SHAPE_PATH))
                .addElement('p', ofBlock(ConfigBlocks.blockCosmeticOpaque, 1))
                .addElement('t', ofBlock(ConfigBlocks.blockCosmeticSolid, 7))
                .addElement('c', casing)
                .addElement(
                    'd',
                    withChannel(
                        "diffusion",
                        defer(
                            machine -> mixedCells,
                            (machine, trigger) -> cells
                                .get(Math.max(1, Math.min(MAX_STRUCTURE_TIER, trigger.stackSize)) - 1))))
                .addElement(
                    'h',
                    ofChain(
                        casing,
                        buildHatchAdder(OTELargeEssentiaGenerator.class).hatchClass(OTEEssentiaStockingInputBusME.class)
                            .adder(OTELargeEssentiaGenerator::addNetworkIntake)
                            .casingIndex(CASING_TEXTURE)
                            .hint(1)
                            .build(),
                        buildHatchAdder(OTELargeEssentiaGenerator.class)
                            .atLeast(
                                HatchElement.DynamoMulti.or(gregtech.api.enums.HatchElement.Dynamo),
                                gregtech.api.enums.HatchElement.InputHatch,
                                gregtech.api.enums.HatchElement.Maintenance)
                            .casingIndex(CASING_TEXTURE)
                            .hint(1)
                            .build(),
                        ofSpecificTileAdder(
                            (OTELargeEssentiaGenerator machine, OTEEssentiaInputHatch intake) -> machine.intakes
                                .add(intake),
                            OTEEssentiaInputHatch.class,
                            EssentiaGeneratorCompat.INTAKE,
                            0)))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public void construct(ItemStack stack, boolean hints) {
        buildPiece(STRUCTURE_PIECE_MAIN, stack, hints, HORIZONTAL_OFFSET, VERTICAL_OFFSET, DEPTH_OFFSET);
    }

    @Override
    public int survivalConstruct(ItemStack stack, int budget, ISurvivalBuildEnvironment env) {
        return mMachine ? -1
            : survivalBuildPiece(
                STRUCTURE_PIECE_MAIN,
                stack,
                HORIZONTAL_OFFSET,
                VERTICAL_OFFSET,
                DEPTH_OFFSET,
                budget,
                env,
                false,
                true);
    }

    @Override
    public void checkMachine(IGregTechTileEntity base, ItemStack stack, List<StructureError> errors) {
        intakes.clear();
        networkIntakes.clear();
        stability = 0;
        structureTier = 0;
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFFSET, VERTICAL_OFFSET, DEPTH_OFFSET, errors)) return;
        if (mDynamoHatches.size() + eDynamoMulti.size() != 1) {
            errors.add(StructureErrors.of("oth.essentia.one_dynamo"));
            return;
        }
        int hatchTier = HATCH_TIERS[structureTier - 1];
        if (dynamo().mTier > hatchTier || mInputHatches.stream()
            .anyMatch(h -> !(h instanceof MTEHatchInputDebug) && h.mTier > hatchTier)) {
            errors.add(StructureErrors.of("oth.essentia.hatch_tier"));
            return;
        }
        if (!networkIntakes.isEmpty() && stability < MAX_STABILITY) {
            errors.add(StructureErrors.of("oth.essentia.me_tier"));
            return;
        }
        for (OTEEssentiaInputHatch intake : intakes) intake.configure(families);
    }

    private boolean addNetworkIntake(IGregTechTileEntity tile, int casing) {
        if (!(tile.getMetaTileEntity() instanceof OTEEssentiaStockingInputBusME intake)) return false;
        intake.updateTexture(casing);
        return networkIntakes.add(intake);
    }

    @Override
    protected boolean shouldCheckRecipeThisTick(long tick) {
        return super.shouldCheckRecipeThisTick(tick)
            || (tick % 20L == 0L && (!intakes.isEmpty() || !networkIntakes.isEmpty()));
    }

    private MTEHatch dynamo() {
        return mDynamoHatches.isEmpty() ? eDynamoMulti.get(0) : mDynamoHatches.get(0);
    }

    @Override
    public CheckRecipeResult checkProcessing_EM() {
        lastFuelFamily = null;
        lastReagent = "";
        if (mDynamoHatches.size() + eDynamoMulti.size() != 1) return CheckRecipeResultRegistry.NO_RECIPE;
        MTEHatch dynamo = dynamo();
        long voltage = dynamo.maxEUOutput();
        long amps = dynamo.maxAmperesOut();
        if (voltage <= 0 || amps <= 0 || stability <= 0) return CheckRecipeResultRegistry.NO_RECIPE;
        long limit = voltage > Long.MAX_VALUE / amps ? Long.MAX_VALUE : voltage * amps;
        for (OTEEssentiaInputHatch intake : intakes) {
            if (reserveEU >= limit) break;
            if (intake.isInvalid()) continue;
            for (Aspect aspect : intake.getAspects()
                .getAspects()) {
                int burned = burn(aspect, intake.containerContains(aspect), dynamo.mTier, limit);
                if (burned > 0) intake.burnEssentia(aspect, burned);
            }
        }
        if (stability == MAX_STABILITY && reserveEU < limit) burnNetworkEssentia(dynamo.mTier, limit);
        if (reserveEU == 0L) {
            setPowerFlow(0L);
            return CheckRecipeResultRegistry.NO_FUEL_FOUND;
        }
        // Keep partial packets for the next tick when sending full-voltage amperes.
        long packetEU = Math.min(voltage, reserveEU);
        setPowerFlow(packetEU);
        eAmpereFlow = Math.max(1L, Math.min(amps, reserveEU / voltage));
        reserveEU -= packetEU * eAmpereFlow;
        mMaxProgresstime = 1;
        mEfficiency = 10000;
        return CheckRecipeResultRegistry.GENERATING;
    }

    private int burn(Aspect aspect, int amount, int dynamoTier, long limit) {
        EssentiaGeneratorFuel fuel = EssentiaGeneratorFuel.forAspect(aspect);
        if (fuel == null || !fuel.enabled(families)) return 0;
        int burned = 0;
        while (burned < amount && reserveEU < limit) {
            burnReagent = "";
            long energy = fuel.burn(this, stability, dynamoTier);
            if (energy <= 0L) break;
            reserveEU = energy > Long.MAX_VALUE - reserveEU ? Long.MAX_VALUE : reserveEU + energy;
            lastFuelFamily = fuel.family;
            lastReagent = burnReagent;
            burned++;
        }
        return burned;
    }

    private void burnNetworkEssentia(int dynamoTier, long limit) {
        for (Aspect aspect : bufferedEssentia.getAspects()) {
            if (reserveEU >= limit) return;
            int burned = burn(aspect, bufferedEssentia.getAmount(aspect), dynamoTier, limit);
            if (burned > 0) bufferedEssentia.remove(aspect, burned);
        }
        if (reserveEU >= limit || networkIntakes.isEmpty()) return;

        Map<Aspect, Long> available = new LinkedHashMap<>();
        OTEEssentiaStockingInputBusME.collectAvailableEssentia(networkIntakes, available, Long.MAX_VALUE);
        for (Map.Entry<Aspect, Long> entry : available.entrySet()) {
            if (reserveEU >= limit) break;
            Aspect aspect = entry.getKey();
            EssentiaGeneratorFuel fuel = EssentiaGeneratorFuel.forAspect(aspect);
            if (fuel == null || !fuel.enabled(families) || bufferedEssentia.getAmount(aspect) > 0) continue;
            long energyPerUnit = fuel.preview(this, stability, dynamoTier);
            if (energyPerUnit <= 0L) continue;
            long needed = 1L + (limit - reserveEU - 1L) / energyPerUnit;
            long requested = Math.min(entry.getValue(), Math.min(Integer.MAX_VALUE, needed));
            int extracted = (int) OTEEssentiaStockingInputBusME.extractEssentia(networkIntakes, aspect, requested);
            int remaining = extracted - burn(aspect, extracted, dynamoTier, limit);
            if (remaining > 0) bufferedEssentia.add(aspect, remaining);
            if (extracted > 0) markDirty();
        }
    }

    boolean useFluid(FluidStack demand, boolean simulate) {
        if (demand == null) return false;
        boolean available = demand.amount > 0 ? depleteInput(demand, true)
            : getStoredFluids().stream()
                .anyMatch(stored -> stored != null && stored.amount > 0 && stored.isFluidEqual(demand));
        if (!available) return false;
        if (!simulate) {
            if (demand.amount > 0 && !depleteInput(demand)) return false;
            burnReagent = FluidRegistry.getFluidName(demand.getFluid());
        }
        return true;
    }

    void releaseFlux(int percent) {
        IGregTechTileEntity base = getBaseMetaTileEntity();
        World world = base.getWorld();
        if (world.rand.nextInt(100) >= percent) return;
        int x = base.getXCoord() + world.rand.nextInt(5) - 2;
        int y = base.getYCoord();
        int z = base.getZCoord() + world.rand.nextInt(5) - 2;
        if (world.isAirBlock(x, y, z)) world.setBlock(x, y, z, ConfigBlocks.blockFluxGas, world.rand.nextInt(8), 3);
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity base, EntityPlayer player) {
        ItemStack held = player.getHeldItem();
        int bit = EssentiaGeneratorCompat.upgradeBit(held);
        if (bit == 0) return super.onRightclick(base, player);
        if (base.isServerSide() && (families & bit) == 0) {
            families |= bit;
            if (!player.capabilities.isCreativeMode) held.stackSize--;
            for (OTEEssentiaInputHatch intake : intakes) intake.configure(families);
            player.inventory.markDirty();
            base.markDirty();
            player.addChatMessage(new ChatComponentTranslation("oth.essentia.installed", held.getDisplayName()));
        }
        return true;
    }

    public int getDiffusionTier() {
        return mMachine ? structureTier : 0;
    }

    public int getDiffusionStability() {
        return mMachine ? stability : 0;
    }

    public long getGeneratedEUt() {
        return mMachine && getBaseMetaTileEntity().isActive() ? getPowerFlow() * eAmpereFlow : 0L;
    }

    public String getLastFuelFamily() {
        return !getBaseMetaTileEntity().isActive() || lastFuelFamily == null ? ""
            : lastFuelFamily.name()
                .toLowerCase(Locale.ROOT);
    }

    public String getLastReagent() {
        return lastReagent;
    }

    @Override
    protected boolean forceUseMui2() {
        return true;
    }

    @Override
    public GTGuiTheme getGuiTheme() {
        return GTGuiThemes.STANDARD;
    }

    @Override
    protected MTEMultiBlockBaseGui<?> getGui() {
        return new OTELargeEssentiaGeneratorGui(this);
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        return new MultiblockTooltipBuilder().addMachineType(translateToLocal("ote.tn.essentia_generator"))
            .addInfo(translateToLocal("oth.essentia.structure"))
            .addInfo(translateToLocal("oth.essentia.output"))
            .addInfo(translateToLocal("oth.essentia.me_tier"))
            .addInfo(translateToLocal("oth.essentia.upgrades"))
            .toolTipFinisher();
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity base, ForgeDirection side, ForgeDirection facing, int color,
        boolean active, boolean redstone) {
        ITexture casing = Textures.BlockIcons.getCasingTextureForId(CASING_TEXTURE);
        if (side != facing) return new ITexture[] { casing };
        return new ITexture[] { casing,
            TextureFactory.of(
                active ? Textures.BlockIcons.MACHINE_CASING_DRAGONEGG_GLOW
                    : Textures.BlockIcons.MACHINE_CASING_DRAGONEGG) };
    }
}
