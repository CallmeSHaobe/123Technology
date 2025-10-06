package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static com.newmaa.othtech.CommonProxy.RocketRenderBlock;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.enums.Mods.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_GLOW;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.Objects;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.common.blocks.oterender.OTERocketRender;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.machine.machineclass.OTHMultiMachineBase;

import cpw.mods.fml.client.registry.ClientRegistry;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;

public class OTEFireRocketAssembler extends OTHMultiMachineBase<OTEFireRocketAssembler>
    implements ISurvivalConstructable, IConstructable {

    public OTEFireRocketAssembler(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTEFireRocketAssembler(String aName) {
        super(aName);
    }

    public ItemStack blueprint;
    public int tier;

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);

    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);

    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
    }

    @Override
    protected boolean isEnablePerfectOverclock() {
        return true;
    }

    public int getMaxParallelRecipes() {
        return 64;
    }

    protected float getSpeedBonus() {
        return 1;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return Recipemaps.NASA;
    }

    // TODO : Need a Special Items Check
    @Override
    protected ProcessingLogic createProcessingLogic() {

        return new ProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult process() {
                setSpeedBonus(getSpeedBonus());
                setOverclock(isEnablePerfectOverclock() ? 4 : 2, 4);
                return super.process();
            }

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @Override
    public boolean onRunningTick(ItemStack stack) {
        super.onRunningTick(stack);
        createRenderBlock();
        return true;
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {

        return super.checkProcessing();
    }

    @Override
    protected void setupProcessingLogic(ProcessingLogic logic) {
        super.setupProcessingLogic(logic);
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

        return this.survivalBuildPiece(
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

    private static final String STRUCTURE_PIECE_MAIN = "main";

    private final int horizontalOffSet = 10;
    private final int verticalOffSet = 15;
    private final int depthOffSet = 3;
    private static IStructureDefinition<OTEFireRocketAssembler> STRUCTURE_DEFINITION = null;
    private static final String[] description = new String[] {
        EnumChatFormatting.AQUA + translateToLocal("otht.con") + ":", translateToLocal("otht.cm.nasa.0") };

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return description;
    }

    @Override
    public IStructureDefinition<OTEFireRocketAssembler> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTEFireRocketAssembler>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shapeMain))
                .addElement(
                    'A',
                    ofBlock(Objects.requireNonNull(Block.getBlockFromName(IndustrialCraft2.ID + ":blockAlloy")), 0))
                .addElement(
                    'B',
                    ofBlock(
                        Objects.requireNonNull(Block.getBlockFromName(IndustrialCraft2.ID + ":blockAlloyGlass")),
                        0))
                .addElement('C', ofBlock(GregTechAPI.sBlockCasings2, 0))
                .addElement('D', ofBlock(GregTechAPI.sBlockCasings3, 11))
                .addElement('E', ofBlock(GregTechAPI.sBlockFrames, 335))
                .addElement('G', ofBlock(GregTechAPI.sBlockReinforced, 8))
                .addElement('H', ofBlock(GregTechAPI.sBlockReinforced, 9))
                .addElement(
                    'I',
                    (Chisel.isModLoaded() && Block.getBlockFromName(Chisel.ID + ":factoryblock") != null)
                        ? ofBlock(Objects.requireNonNull(Block.getBlockFromName(Chisel.ID + ":factoryblock")), 6)
                        : ofBlock(sBlockConcretes, 0))
                .addElement(
                    'F',
                    buildHatchAdder(OTEFireRocketAssembler.class)
                        .atLeast(OutputBus, InputBus, InputHatch, OutputHatch, Energy.or(ExoticEnergy))
                        .adder(OTEFireRocketAssembler::addToMachineList)
                        .dot(1)
                        .casingIndex(210)
                        .buildAndChain(sBlockReinforced, 2))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    private final String[][] shapeMain = new String[][] {
        { "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                  E  ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     " },
        { "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                  E  ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     " },
        { "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                  E  ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     " },
        { "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                 EE  ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     " },
        { "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                  E  ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     " },
        { "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                 EE  ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     " },
        { "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                  E  ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     " },
        { "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                 EE  ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     " },
        { "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                  E  ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     " },
        { "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            " FEEEEEEEEEEEEEEEEEF             EE  ", " FEDDDDDDDDDDDDDDDEF                 ",
            " FEEEEEEEEEEEEEEEEEF                 ", "                                     ",
            "                                     ", "                                     ",
            "                                     " },
        { "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", " FCCCCCCCCCCCCCCCCCF                 ",
            " F                 F              E  ", " F                 F                 ",
            " F                 F                 ", " FCCCCCCCCCCCCCCCCCF                 ",
            "                                     ", "                                     ",
            "                                     " },
        { "                                     ", "                                     ",
            "                                     ", "                                     ",
            " FBBBBBBBBBBBBBBBBBF                 ", " F                 F                 ",
            " F                 F             EE  ", " F                 F                 ",
            " F                 F                 ", " F                 F                 ",
            " FBBBBBBBBBBBBBBBBBF                 ", "                                     ",
            "                                     " },
        { "                            AAAAA    ", "                          AA     AA  ",
            "                         A         A ", "                         A         A ",
            " FBBBBBBBBBBBBBBBBBF    A           A", " F                 F    A           A",
            " F                 F    A         E A", " F                 F    A           A",
            " F                 F    A           A", " F                 F     A         A ",
            " FBBBBBBBBBBBBBBBBBF     A         A ", "                          AA     AA  ",
            "                            AAAAA    " },
        { "                            ABBBA    ", "                          BB     BB  ",
            "                         B         B ", "                         B         B ",
            " FBBBBBBBBBBBBBBBBBF    A           A", " F                 F    B           B",
            " F                 F    B        EE B", " F                 F    B           B",
            " F                 F    A           A", " F                 F     B         B ",
            " FBBBBBBBBBBBBBBBBBF     B         B ", "                          BB     BB  ",
            "                            ABBBA    " },
        { "                            ABBBA    ", "                          BB     BB  ",
            "                         B         B ", "                         B         B ",
            " FBBBBBBBBBBBBBBBBBF    A           A", " F                 F    B           B",
            " F                 F              E B", " F                 F    B           B",
            " F                 F    A           A", " F                 F     B         B ",
            " FBBBBBBBBBBBBBBBBBF     B         B ", "                          BB     BB  ",
            "                            ABBBA    " },
        { "                            ABBBA    ", "                          BB     BB  ",
            "                         B         B ", "        FF~FF            B         B ",
            " FAAAAAAAAAAAAAAAAAF    A           A", " F                 F    B         E B",
            " F                 F             EEEB", " F                 F    B         E B",
            " F                 F    A           A", " F                 F     B         B ",
            " FAAAAAAAAAAAAAAAAAF     B         B ", "                          BB     BB  ",
            "                            ABBBA    " },
        { "       IIIIIII              IIIII    ", "IIIIIIIICCCCCIIIIIIII     IIIAAAIII  ",
            "ICCCCCCCCCCCCCCCCCCCI    IIAAAAAAAII ", "ICCCCCCCCCCCCCCCCCCCI    IAAGGGGGAAI ",
            "ICCCCCCCCCCCCCCCCCCCI   IIAGGHHHGGAII", "IFFFFFFFFFFFFFFFFFFFIIIIIAAGHHHHHGAAI",
            "IFFFFFFFFFFFFFFFFFFFICCCIAAGHHHHHGAAI", "IFFFFFFFFFFFFFFFFFFFIIIIIAAGHHHHHGAAI",
            "IFFFFFFFFFFFFFFFFFFFI   IIAGGHHHGGAII", "IFFFFFFFFFFFFFFFFFFFI    IAAGGGGGAAI ",
            "ICCCCCCCCCCCCCCCCCCCI    IIAAAAAAAII ", "IIIIIIIIIIIIIIIIIIIII     IIIAAAIII  ",
            "                            IIIII    " } };

    @Override
    protected IAlignmentLimits getInitialAlignmentLimits() {
        return (d, r, f) -> d.offsetY == 0 && r.isNotRotated();
    }

    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return super.addToMachineList(aTileEntity, aBaseCasingIndex)
            || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(EnumChatFormatting.DARK_RED + translateToLocal("ote.tm.nasa.0"))
            .addInfo(EnumChatFormatting.ITALIC + translateToLocal("ote.tm.nasa.1"))
            .addInfo(EnumChatFormatting.ITALIC + translateToLocal("ote.tm.nasa.2"))
            .addInfo(EnumChatFormatting.ITALIC + translateToLocal("ote.tm.nasa.3"))
            .addInfo(EnumChatFormatting.WHITE + translateToLocal("ote.tm.nasa.4"))
            .addInfo(EnumChatFormatting.WHITE + translateToLocal("ote.tm.nasa.5"))
            .addInfo(translateToLocal("ote.tm.nasa.6"))
            .addTecTechHatchInfo()
            .addSeparator()
            .addController(translateToLocal("ote.tn.nasa"))
            .beginStructureBlock(13, 17, 37, false)
            .addInputBus("AnyInputBus", 1)
            .addOutputBus("AnyOutputBus", 1)
            .addInputHatch("AnyInputHatch", 1)
            .addOutputHatch("AnyOutputHatch", 1)
            .addEnergyHatch("AnyEnergyHatch", 1)
            .toolTipFinisher("§a123Technology - FIRE-RocketAssembler");
        return tt;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTEFireRocketAssembler(this.mName);
    }

    @Override
    public ITexture[] getTexture(final IGregTechTileEntity baseMetaTileEntity, final ForgeDirection sideDirection,
        final ForgeDirection facing, final int aColorIndex, final boolean active, final boolean aRedstone) {

        if (sideDirection == facing) {
            if (active) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(210), TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE_GLOW)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(210), TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(210) };
    }

    @Override
    protected SoundResource getProcessStartSound() {
        return SoundResource.GTCEU_LOOP_ASSEMBLER;
    }

    public void createRenderBlock() {
        int x = getBaseMetaTileEntity().getXCoord();
        int y = getBaseMetaTileEntity().getYCoord();
        int z = getBaseMetaTileEntity().getZCoord();

        double xOffset = 3 * getExtendedFacing().getRelativeBackInWorld().offsetX
            - 20 * getExtendedFacing().getRelativeRightInWorld().offsetX;
        double zOffset = 3 * getExtendedFacing().getRelativeBackInWorld().offsetZ
            - 20 * getExtendedFacing().getRelativeRightInWorld().offsetZ;
        double yOffset = 3 * getExtendedFacing().getRelativeBackInWorld().offsetY
            - 20 * getExtendedFacing().getRelativeRightInWorld().offsetY;
        this.getBaseMetaTileEntity()
            .getWorld()
            .setBlock((int) (x + xOffset), (int) (y + yOffset), (int) (z + zOffset), Blocks.air);
        this.getBaseMetaTileEntity()
            .getWorld()
            .setBlock((int) (x + xOffset), (int) (y + yOffset), (int) (z + zOffset), RocketRenderBlock);
    }

    public static void loadRender() {
        new RocketRenderOTH();
    }

    static class RocketRenderOTH extends TileEntitySpecialRenderer {

        private static final ResourceLocation ROCKETTEXTURES = new ResourceLocation(
            "galaxyspace",
            "textures/model/tier" + "8" + "rocket.png");
        private static final IModelCustom ROCKET = AdvancedModelLoader
            .loadModel(new ResourceLocation("galaxyspace", "models/tier" + "8" + "rocket.obj"));

        public RocketRenderOTH() {
            ClientRegistry.bindTileEntitySpecialRenderer(OTERocketRender.class, this);
        }

        @Override
        public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float timeSinceLastTick) {
            if (!(tile instanceof OTERocketRender rocket)) return;
            final double size = rocket.size;
            GL11.glPushMatrix();
            GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
            int meta = rocket.getBlockMetadata();

            switch (meta) {
                case 2:
                    GL11.glRotated(180, 0, 1, 0);
                    break;
                case 3:
                    GL11.glRotated(0, 0, 1, 0);
                    break;
                case 4:
                    GL11.glRotated(90, 0, 1, 0);
                    break;
                case 5:
                    GL11.glRotated(-90, 0, 1, 0);
                    break;
            }

            renderStar(size);
            GL11.glPopMatrix();
        }

        private void renderStar(double size) {
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            this.bindTexture(ROCKETTEXTURES);
            GL11.glScaled(size, size, size);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
            ROCKET.renderAll();
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_LIGHTING);
        }

    }

}
