package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.withChannel;
import static gregtech.api.GregTechAPI.sBlockCasings2;
import static gregtech.api.GregTechAPI.sBlockCasings3;
import static gregtech.api.GregTechAPI.sBlockCasings4;
import static gregtech.api.GregTechAPI.sBlockCasings8;
import static gregtech.api.GregTechAPI.sBlockConcretes;
import static gregtech.api.GregTechAPI.sBlockMetal1;
import static gregtech.api.GregTechAPI.sBlockMetal6;
import static gregtech.api.GregTechAPI.sBlockMetal7;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Mods.Chisel;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_GLOW;
import static gregtech.api.util.GTStructureUtility.*;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.List;
import java.util.Objects;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.machine.machineclass.OTHMultiMachineBase;
import com.newmaa.othtech.utils.Utils;

import bartworks.API.BorosilicateGlass;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class OTESINOPEC extends OTHMultiMachineBase<OTESINOPEC> {

    public OTESINOPEC(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTESINOPEC(String aName) {
        super(aName);
    }

    private boolean $123 = false;

    private HeatingCoilLevel coilLevel;

    public HeatingCoilLevel getCoilLevel() {
        return this.coilLevel;
    }

    public void setCoilLevel(HeatingCoilLevel coilLevel) {
        this.coilLevel = coilLevel;
    }

    public int getCoilTier() {
        return Utils.getCoilTier(coilLevel);
    }

    private static ItemStack dustIrOsSmM = null;

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        if (dustIrOsSmM == null) dustIrOsSmM = OTHItemList.dustIrOsSmM.get(1);
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (aTick % 20 == 0 && !$123) {
            ItemStack aGuiStack = this.getControllerSlot();
            if (aGuiStack != null) {
                if (GTUtility.areStacksEqual(aGuiStack, dustIrOsSmM)) {
                    this.$123 = true;
                }
            }
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setBoolean("$123", $123);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        $123 = aNBT.getBoolean("123");
    }

    @Override
    protected boolean isEnablePerfectOverclock() {
        return $123;
    }

    public int getMaxParallelRecipes() {
        if ($123) {
            return Integer.MAX_VALUE;
        } else {
            return 64;
        }
    }

    protected float getSpeedBonus() {
        if (getCoilTier() >= 10) {
            return 0.1F;
        }
        return 1 - getCoilTier() * 0.1F;
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        final IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity != null) {
            tag.setBoolean("123Processing", $123);
        }
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        currentTip.add(
            "123" + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + tag.getString("123Processing")
                + EnumChatFormatting.RESET);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return Recipemaps.SINOPEC;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {

        return new ProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult process() {
                setSpeedBonus(getSpeedBonus());
                return super.process();
            }

        }.setUnlimitedTierSkips()
            .setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        repairMachine();
        coilLevel = HeatingCoilLevel.None;

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

    private final int horizontalOffSet = 26;
    private final int verticalOffSet = 46;
    private final int depthOffSet = 2;
    private static final int HORIZONTAL_DIRT_METAID = 10;
    private static IStructureDefinition<OTESINOPEC> STRUCTURE_DEFINITION = null;
    private static final String[] description = new String[] { EnumChatFormatting.AQUA + translateToLocal("搭建细节") + ":",
        translateToLocal("1 - 消声仓, 能源仓, 输入输出总线, 输入输出仓 : 替换钨钢机械方块, 支持TecTech能源仓") + ":",
        translateToLocal("2 - 消声仓 : 烟囱顶端中央机械方块, 其实放主机旁边也行") // 1 - Energy IO Hatches or High
    };

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return description;
    }

    @Override
    public IStructureDefinition<OTESINOPEC> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTESINOPEC>builder()
                .addShape(STRUCTURE_PIECE_MAIN, shapeMain)
                .addElement('A', BorosilicateGlass.ofBoroGlass(3))
                .addElement('B', BorosilicateGlass.ofBoroGlass(3))
                .addElement('C', ofBlock(sBlockCasings2, 13))
                .addElement('D', ofBlock(sBlockCasings2, 15))
                .addElement('E', ofBlock(sBlockCasings3, 15))
                .addElement('G', ofBlock(sBlockCasings4, 1))
                .addElement('H', withChannel("coil", ofCoil(OTESINOPEC::setCoilLevel, OTESINOPEC::getCoilLevel)))
                .addElement('I', ofBlock(sBlockCasings8, 0))
                .addElement('J', ofBlock(sBlockCasings8, 1))
                .addElement('K', ofBlock(sBlockMetal1, 12))
                .addElement('L', ofBlock(sBlockMetal6, 13))
                .addElement('M', ofBlock(sBlockMetal7, 11))
                .addElement('P', ofFrame(Materials.Steel))
                .addElement(
                    'N',
                    (Chisel.isModLoaded() && Block.getBlockFromName(Chisel.ID + ":concrete") != null) ? ofBlock(
                        Objects.requireNonNull(Block.getBlockFromName(Chisel.ID + ":concrete")),
                        HORIZONTAL_DIRT_METAID) : ofBlock(sBlockConcretes, 0))
                .addElement(
                    'F',
                    buildHatchAdder(OTESINOPEC.class)
                        .atLeast(Energy.or(ExoticEnergy), InputBus, OutputBus, InputHatch, OutputHatch)
                        .adder(OTESINOPEC::addToMachineList)
                        .dot(1)
                        .casingIndex(48)
                        .buildAndChain(sBlockCasings4, 0))
                .addElement(
                    'O',
                    buildHatchAdder(OTESINOPEC.class).atLeast(Muffler)
                        .adder(OTESINOPEC::addToMachineList)
                        .dot(2)
                        .casingIndex(48)
                        .buildAndChain(sBlockCasings4, 0))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    // Structured by LyeeR
    @SuppressWarnings("SpellCheckingInspection")
    private final String[][] shapeMain = new String[][] {
        { "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN" },
        { "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                        GGGGG                   ", "                        P   P                   ",
            "                        P   P                   ", "                        P   P                   ",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN" },
        { "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "G   G   G   G   G   G   GGGGG                   ", "P   P   P   P   P   P    FFF                    ",
            "CCCCCCCCCCCCCCCCCCCCCC   F~F                    ", "P   P   P   P   P   PC   FFF                    ",
            "NNNNNNNNNNNNNNNNNNNNNNNNNFFFNNNNNNNNNNNNNNNNNNNN" },
        { "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            " DDDDDDDDDDDDDDDDDDDDDDDDGGGG                   ", "                         FDF                    ",
            "CJJJJJJJJJJJJJJJJJJJJJ   FDF                    ", "                     J   FDF                    ",
            "NNNNNNNNNNNNNNNNNNNNNNNNNFFFNNNNNNNNNNNNNNNNNNNN" },
        { "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                      GGGGGGGGG ", "                                      P       P ",
            "                                      P       P ", "                                      P       P ",
            " DG G   G   G   G   G   GGGGG         P       P ", "  P P   P   P   P   P    FFF          P       P ",
            "CJCCCCCCCCCCCCCCCCCCCC   FFF          P       P ", "  P P   P   P   P   PC   FFF          P       P ",
            "NNNNNNNNNNNNNNNNNNNNNNNNNFFFNNNNNNNNNNNNNNNNNNNN" },
        { "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                      GGGGGGGGG ", "                                                ",
            "                                                ", "                                         GGG    ",
            " D                      GGGGG            GGG    ", "                        P   P            GGG    ",
            "CJC                     P C P             C     ", "                        P C P             C     ",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN" },
        { "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                      GGGGGGGGG ", "                                        P   P   ",
            "                                        PGGGP   ", "                                        GHHHG   ",
            "GDG                                     GHJHG   ", "P P                                     GHHHG   ",
            "CJC                                     PGGGP   ", "P P                                     P   P   ",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN" },
        { "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                      GGGGGGGGG ", "                                                ",
            "                                         GAG    ", "                                        G   G   ",
            " D                                      A J A   ", "                                        G   G   ",
            "CJC                                      GGG    ", "                                                ",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN" },
        { "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                  GGG                           ", "                  GAG                           ",
            "                  GAG                           ", "                  GAG                           ",
            "                  GAG                           ", "                  GAG                           ",
            "                  GAG                           ", "                  GAG                           ",
            "                  GAG                           ", "                  GAG                           ",
            "                  GAG                           ", "                  GAG                           ",
            "                  GAG                           ", "                  GAG                           ",
            "                  GAG                           ", "                  GAG                           ",
            "      GGG   GGG   GAG                           ", "      GAG   GAG   GAG                           ",
            "      GAG   GAG   GAG                           ", "      GAG   GAG   GAG                           ",
            "      GAG   GAG   GAG                           ", "      GAGPPPGAGPPPGAG                           ",
            "      GAG P GAG P GAG                           ", "      GAG P GAG P GAG                           ",
            "      GAGPPPGAGPPPGAG                           ", "      GAG P GAG P GAG                           ",
            "      GAG P GAG P GAG                 GGGGGGGGG ", "      GAGPPPGAGPPPGAG   GGGGGGGGGGGG  P       P ",
            "      GAG P GAG P GAG   P   P  P   P  P  GAG  P ", "      GAG P GAG P GAG   P   P  P   P  P G   G P ",
            " D    GAGPPPGAGPPPGAG   PGGGP  PGGGP  P A J A P ", "      GAG P GAG P GAG   PGAGP  PGAGP  P G   G P ",
            "CJC   GAG P GAG P GAG   PGAGP  PGAGP  P  GGG  P ", "      GGG P GGG P GGG   PGGGP  PGGGP  P       P ",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN" },
        { "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                  GGG                           ",
            "                 G   G                          ", "                 G   G                          ",
            "                 G   G                          ", "                 GHHHG                          ",
            "                 G   G                          ", "                 G   G                          ",
            "                 GHHHG                          ", "                 G   G                          ",
            "                 G   G                          ", "                 GHHHG                          ",
            "                 G   G                          ", "                 G   G                          ",
            "                 GHHHG                          ", "                 G   G                          ",
            "                 G   G                          ", "      GGG   GGG  GHHHG                          ",
            "     G   G G   G G   G                          ", "     G   G G   G G   G                          ",
            "     GHHHG GHHHG GHHHG                          ", "     G   G G   G G   G                          ",
            "     G   G G   G G   G                          ", "     GHHHGPGHHHGPGHHHG                          ",
            "     G   G G   G G   G                          ", "     G   G G   G G   G                          ",
            "     GHHHGPGHHHGPGHHHG                          ", "     G   G G   G G   G                          ",
            "     G   G G   G G   G                GGGGGGGGG ", "     GHHHGPGHHHGPGHHHG  GGGGGGGGGGGG            ",
            "     G   G G   G G   G                   GAG    ", "     G   G G   G G   G   III    III     G   G   ",
            " D   GHHHGPGHHHGPGHHHG  G   G  G   G    A J A   ", "     G   G G   G G   G  G   G  G   G    G   G   ",
            "CJC  G   G G   G G   G  G   G  G   G     GGG    ", "     GHHHG GHHHG GHHHG  GHHHG  GHHHG            ",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN" },
        { "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                  GGG                           ",
            "                 G   G                          ", "                 G   G                          ",
            "                 G   G                          ", "                 GHCHG                          ",
            "                 G   G                          ", "                 G   G                          ",
            "                 GHCHG                          ", "                 G   G                          ",
            "                 G   G                          ", "                 GHCHG                          ",
            "                 G   G                          ", "                 G   G                          ",
            "                 GHCHG                          ", "                 G   G                          ",
            "                 G   G                          ", "      GGG   GGG  GHCHG                          ",
            "     G   G G   G G   G                          ", "     G   G G   G G   G                          ",
            "     GHCHG GHCHG GHCHG                          ", "     G   G G   G G   G                          ",
            "     G   G G   G G   G                          ", "     GHCHGPGHCHGPGHCHG                          ",
            "     G   G G   G G   G                          ", "     G   G G   G G   G                          ",
            "     GHCHGPGHCHGPGHCHG                          ", "     G   G G   G G   G                          ",
            "     G   G G   G G   G                GGGGGGGGG ", "     GHCHGPGHCHGPGHCHG  GGGGGGGGGGGG    P   P   ",
            "     G   G G   G G   G    J      J      PGAGP   ", "     G   G G   G G   G   IJJCCCCIJJCCC  G   G   ",
            "GDG  GHCHGPGHCHGPGHCHG  G J G  G J G C  A J A   ", "P P  G   G G   G G   G  A J A  A J A C  G   G   ",
            "CJC  G   G G   G G   G  A J A  A J A C  PGGGP   ", "P P  GHGHCCCHGHCCCHGHCCCGHGHGIIGHGHG C  P   P   ",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNCNNNNNNNNNN" },
        { "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                  GGG                           ",
            "                 G   G                          ", "                 G   G                          ",
            "                 G   G                          ", "                 GHHHG                          ",
            "                 G   G                          ", "                 G   G                          ",
            "                 GHHHG                          ", "                 G   G                          ",
            "                 G   G                          ", "                 GHHHG                          ",
            "                 G   G                          ", "                 G   G                          ",
            "                 GHHHG                          ", "                 G   G                          ",
            "                 G   G                          ", "      GGG   GGG  GHHHG                          ",
            "     G   G G   G G   G                          ", "     G   G G   G G   G                          ",
            "     GHHHG GHHHG GHHHG                          ", "     G   G G   G G   G                          ",
            "     G   G G   G G   G                          ", "     GHHHGPGHHHGPGHHHG                          ",
            "     G   G G   G G   G                          ", "     G   G G   G G   G                          ",
            "     GHHHGPGHHHGPGHHHG                          ", "     G   G G   G G   G                          ",
            "     G   G G   G G   G                GGGGGGGGG ", "     GHHHGPGHHHGPGHHHG  GGGGGGGGGGGG            ",
            "     G   G G   G G   G                   GAG    ", "     G   G G   G G   G   III    III     G   G   ",
            " D   GHHHGPGHHHGPGHHHG  G   G  G   G    A J A   ", "     G   G G   G G   G  G   G  G   G    G   G   ",
            "CJC  G   G G   G G   G  G   G  G   G     GGG    ", "     GHHHG GHHHG GHHHG  GHHHG  GHHHG            ",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN" },
        { "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                  GGG                           ", "                  GGG                           ",
            "                  GGG                           ", "                  GGG                           ",
            "                  GGG                           ", "                  GGG                           ",
            "                  GGG                           ", "                  GGG                           ",
            "                  GGG                           ", "                  GGG                           ",
            "                  GGG                           ", "                  GGG                           ",
            "                  GGG                           ", "                  GGG                           ",
            "                  GGG                           ", "                  GGG                           ",
            "      GGG   GGG   GGG                           ", "      GGG   GGG   GGG                           ",
            "      GGG   GGG   GGG                           ", "      GGG   GGG   GGG                           ",
            "      GGG   GGG   GGG                           ", "      GGGPPPGGGPPPGGG                           ",
            "      GGG   GGG   GGG                           ", "      GGG   GGG   GGG                           ",
            "      GGGPPPGGGPPPGGG                           ", "      GGG   GGG   GGG                           ",
            "      GGG   GGG   GGG                 GGGGGGGGG ", "      GGGPPPGGGPPPGGG   GGGGGGGGGGGG  P       P ",
            "      GGG   GGG   GGG   P   P  P   P  P  GAG  P ", "      GGG   GGG   GGG   P   P  P   P  P G   G P ",
            " D    GGGPPPGGGPPPGGG   PGGGP  PGGGP  P A J A P ", "      GGG P GGG P GGG   PGAGP  PGAGP  P G   G P ",
            "CJC   GGG P GGG P GGG   PGAGP  PGAGP  P  GGG  P ", "      GGG P GGG P GGG   PGGGP  PGGGP  P       P ",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN" },
        { "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                      GGGGGGGGG ", "                                                ",
            "                                         GAG    ", "                                        G   G   ",
            " D                                      A J A   ", "                                        G   G   ",
            "CJC                                      GGG    ", "                                                ",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN" },
        { "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                      GGGGGGGGG ", "                                        P   P   ",
            "                                        PGGGP   ", "                                        GHHHG   ",
            "GDG                                     GHJHG   ", "P P                                     GHHHG   ",
            "CJC                                     PGGGP   ", "P P                                     P   P   ",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN" },
        { "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "        GGGGGGGGG                     GGGGGGGGG ", "        P       P                               ",
            "        P       P                         C     ", "        P       P                        GGG    ",
            " D      P       P    LLL      LLL        GGG    ", "        P       P    LBL      LBL        GGG    ",
            "CJC     P       P    LBL      LBL               ", "        P       P    LLL      LLL               ",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN" },
        { "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "        GGGGGGGGG                     GGGGGGGGG ", "                                      P       P ",
            "                                      P   C   P ", "           GGG       LLL      LLL     P       P ",
            " D         GGG      L   L    L   L    P       P ", "           GGG      L   L    L   L    P       P ",
            "CJC         C       L   L    L   L    P       P ", "            C       LLLLL    LLLLL    P       P ",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN" },
        { "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "        GGGGGGGGG                               ", "          P   P                                 ",
            "          PGCGP                           C     ", "          GHHHG     LLLLL    LLLLL        C     ",
            " D        EH HE    L     L  L     L       C     ", "          GHHHG    L     L  L     L       C     ",
            "CJC       PGGGP    L     L  L     L       C     ", "          P   P    LLLLLLL  LLLLLLL       C     ",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNCNNNNN" },
        { "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "        GGGGGGGGG                               ", "                                                ",
            "           GCG                                  ", "          G   G     LLLLL    LLLLL              ",
            "GDG       E   E    L     L  L     L             ", "P P       G   G    B     B  B     B             ",
            "CJC        GGG     B     B  B     B             ", "P P                LLLCLLL  LLLCLLL             ",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNCNNNNN" },
        { "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "        GGGGGGGGG                               ", "        P       P                               ",
            "        P  GCG  P                               ", "        P GHHHG P   LLLLL    LLLLL              ",
            " D      P EH HE P  L     L  L     L             ", "        P GHHHG P  L     L  L     L             ",
            "CJC     P  GGG  P  L     L  L     L             ", "        P       P  LLLLLLL  LLLLLLL             ",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNCNNNNN" },
        { "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                       LLLLL    ", "                                       LLBLL    ",
            "                                       LLBLL    ", "                                       LLBLL    ",
            "        GGGGGGGGG                      LLBLL    ", "                                       LLBLL    ",
            "           GCG                         LLBLL    ", "          G   G      LLL      LLL      LLBLL    ",
            " D        E   E     L   L    L   L     LLBLL    ", "          G   G     L   L    L   L     LLBLL    ",
            "CJC        GGG      L   L    L   L     LLBLL    ", "                    LLLLL    LLLLL     LLLLL    ",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNCNNNNN" },
        { "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                       LLLLL    ",
            "                                      L     L   ", "                                      L     L   ",
            "                                      L     L   ", "                                      L     L   ",
            "        GGGGGGGGG                     L     L   ", "          P   P                       L     L   ",
            "          PGCGP                       L     L   ", "          GHHHG       J        J      L     L   ",
            " D        EH HE      LLL      LLL     L     L   ", "          GHHHG      LBL      LBL     L     L   ",
            "CJC       PGGGP      LBL      LBL     L     L   ", "          P   P      LLL      LLL     LLLLLLL   ",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN" },
        { "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                      LLLLLLL   ",
            "                                     L       L  ", "                                     L       L  ",
            "                                     L       L  ", "                                     L       L  ",
            "        GGGGGGGGG                    L       L  ", "                                     L       L  ",
            "           GCG                       L       L  ", "          G   G       J        J     L       L  ",
            "GDG       E   E                      L       L  ", "P P       G   G                      L       L  ",
            "CJC        GGG                       L       L  ", "P P                                  LLLLLLLLL  ",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN" },
        { "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                      LLLLLLL   ",
            "                                     L       L  ", "                                     L       L  ",
            "                                     L       L  ", "                                     L       L  ",
            "        GGGGGGGGG                    L       L  ", "        P       P                    L       L  ",
            "        P  GCG  P                    L       L  ", "        P GHHHG P     J        J     L       L  ",
            " D      P EH HE P     P        P     L       L  ", "        P GHHHJJJJJJJJJJJJJJJJJJJJJJ L       L  ",
            "CJC     P  GGG  P     P        P   J L       L  ", "        P       P     P        P   J LLLLLLLLL  ",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNJJJNNNNNNNNNN" },
        { "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                      LLLLLLL   ",
            "                                     L       L  ", "                                     B       B  ",
            "                                     B       B  ", "                                     B       B  ",
            "        GGGGGGGGG                    B       B  ", "                                     B       B  ",
            "           GCG                       B       B  ", "          G   G       J        J     B       B  ",
            " D        E   E                      B       B  ", "          G   G                      B       B  ",
            "CJC        GGG                       B       B  ", "                                     LLLLCLLLL  ",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNLNNNNNNNNNN" },
        { "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                      LLLLLLL   ",
            "                                     L       L  ", "                                     L       L  ",
            "                                     L       L  ", "                                     L       L  ",
            "        GGGGGGGGG                    L       L  ", "          P   P                      L       L  ",
            "          PGCGP                      L       L  ", "          GHHHG       J        J     L       L  ",
            " D        EH HE      LLL      LLL    L       L  ", "          GHHHG      LBL      LBL    L       L  ",
            "CJC       PGGGP      LBL      LBL    L       L  ", "          P   P      LLL      LLL    LLLLLLLLL  ",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN" },
        { "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                      LLLLLLL   ",
            "                                     L       L  ", "                                     L       L  ",
            "                                     L       L  ", "                                     L       L  ",
            "        GGGGGGGGG                    L       L  ", "                                     L       L  ",
            "                                     L       L  ", "           GGG       LLL      LLL    L       L  ",
            "GDG        GCG      L   L    L   L   L       L  ", "P P        GGG      L   L    L   L   L       L  ",
            "CJC                 L   L    L   L   L       L  ", "P P                 LLLLL    LLLLL   LLLLLLLLL  ",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN" },
        { "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                       LLLLL    ",
            "                                      L     L   ", "                                      L     L   ",
            "                                      L     L   ", "                                      L     L   ",
            "        GGGGGGGGG                     L     L   ", "        P       P                     L     L   ",
            "        P       P                     L     L   ", "        P       P   LLLLL    LLLLL    L     L   ",
            " D      P   C   P  L     L  L     L   L     L   ", "        P       P  L     L  L     L   L     L   ",
            "CJC     P       P  L     L  L     L   L     L   ", "        P       P  LLLLLLL  LLLLLLL   LLLLLLL   ",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN" },
        { "            M                                   ", "            K                                   ",
            "            L                                   ", "            L                                   ",
            "            L                                   ", "            L                                   ",
            "            L                                   ", "            L                                   ",
            "            L                                   ", "            L                                   ",
            "            L                                   ", "            L                                   ",
            "            L                                   ", "            L                                   ",
            "            L                                   ", "            L                                   ",
            "            L                                   ", "            L                                   ",
            "            L                                   ", "            L                                   ",
            "            L                                   ", "            L                                   ",
            "            L                                   ", "            L                                   ",
            "            L                                   ", "            L                                   ",
            "            L                                   ", "            L                                   ",
            "            L                                   ", "            L                                   ",
            "            L                                   ", "            L                                   ",
            "            L                                   ", "            L                                   ",
            "            L                                   ", "           PLP                                  ",
            "           PLP                         LLLLL    ", "           PLP                         LLBLL    ",
            "           PLP                         LLBLL    ", "           PLP                         LLBLL    ",
            "        GGGLLLGGG                      LLBLL    ", "           LLL                         LLBLL    ",
            "           LLL                         LLBLL    ", "           LLL      LLLLL    LLLLL     LLBLL    ",
            " D         LCL     L     L  L     L    LLBLL    ", "           LEL     B     B  B     B    LLBLL    ",
            "CJC        LEL     B     B  B     B    LLBLL    ", "           LEL     LLLCLLL  LLLCLLL    LLLLL    ",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN" },
        { "           M M                                  ", "           KOK                                  ",
            "           L L                                  ", "           L L                                  ",
            "           L L                                  ", "           L L                                  ",
            "           L L                                  ", "           L L                                  ",
            "           L L                                  ", "           L L                                  ",
            "           L L                                  ", "           L L                                  ",
            "           L L                                  ", "           L L                                  ",
            "           L L                                  ", "           L L                                  ",
            "           L L                                  ", "           L L                                  ",
            "           L L                                  ", "           L L                                  ",
            "           L L                                  ", "           L L                                  ",
            "           L L                                  ", "           L L                                  ",
            "           L L                                  ", "           L L                                  ",
            "           L L                                  ", "           L L                                  ",
            "           L L                                  ", "           L L                                  ",
            "           L L                                  ", "           L L                                  ",
            "           L L                                  ", "           L L                                  ",
            "           L L                                  ", "           L L                                  ",
            "           L L                                  ", "           L L                                  ",
            "           L L                                  ", "           L L                                  ",
            "        GGGL LGGG                               ", "           L L                                  ",
            "           L L                                  ", "           L L      LLLLL    LLLLL              ",
            " DDDDDDDDDDE E     L     L  L     L             ", " P     P   E E     L     L  L     L             ",
            "CJC    P   E E     L     L  L     L             ", "       P   E E     LLLLLLL  LLLLLLL             ",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN" },
        { "            M                                   ", "            K                                   ",
            "            L                                   ", "            L                                   ",
            "            L                                   ", "            L                                   ",
            "            L                                   ", "            L                                   ",
            "            L                                   ", "            L                                   ",
            "            L                                   ", "            L                                   ",
            "            L                                   ", "            L                                   ",
            "            L                                   ", "            L                                   ",
            "            L                                   ", "            L                                   ",
            "            L                                   ", "            L                                   ",
            "            L                                   ", "            L                                   ",
            "            L                                   ", "            L                                   ",
            "            L                                   ", "            L                                   ",
            "            L                                   ", "            L                                   ",
            "            L                                   ", "            L                                   ",
            "            L                                   ", "            L                                   ",
            "            L                                   ", "            L                                   ",
            "            L                                   ", "           PLP                                  ",
            "           PLP                                  ", "           PLP                                  ",
            "           PLP                                  ", "           PLP                                  ",
            "        GGGLLLGGG                               ", "           LLL                                  ",
            "           LLL                                  ", "           LLL       LLL      LLL               ",
            "           LEL      L   L    L   L              ", "           LEL      L   L    L   L              ",
            "CJC        LEL      L   L    L   L              ", "CJC        LEL      LLLLL    LLLLL              ",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN" },
        { "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "        GGGGGGGGG                               ", "        P       P                               ",
            "        P       P                               ", "        P       P                               ",
            "        P       P    LLL      LLL               ", "        P       P    LBL      LBL               ",
            "        P       P    LBL      LBL               ", "        P       P    LLL      LLL               ",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN" },
        { "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "                                                ", "                                                ",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN" } };

    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return super.addToMachineList(aTileEntity, aBaseCasingIndex)
            || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
    }

    @Override
    public int getPollutionPerSecond(final ItemStack aStack) {
        return 6400;
    }

    /*
     * @Override
     * protected MultiblockTooltipBuilder createTooltip() {
     * final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
     * tt.addMachineType("§q§l重工业计划 - 中国石化集成工厂")
     * .addInfo("§l§a黑金的最终流处...")
     * .addInfo("§l一步到位.")
     * .addInfo("线圈等级<10时 耗时倍率 = 1 - 线圈等级 * 0.1, ≥10时耗时倍率固定为0.1")
     * .addInfo("主机放入铱锇钐合金粉解锁无损超频以及并行限制, 并行默认为64")
     * .addTecTechHatchInfo()
     * .addPollutionAmount(6400)
     * .addSeparator()
     * .addController("中国石化")
     * .beginStructureBlock(33, 49, 48, false)
     * .addInputBus("AnyInputBus", 1)
     * .addOutputBus("AnyOutputBus", 1)
     * .addInputHatch("AnyInputHatch", 1)
     * .addOutputHatch("AnyOutputHatch", 1)
     * .addEnergyHatch("AnyEnergyHatch", 1)
     * .addMufflerHatch("AnyMufflerHatch", 1)
     * .toolTipFinisher("§a123Technology - Heavy industry - SINOPEC");
     * return tt;
     * }
     */
    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(translateToLocal("ote.tm.sinopec.0"))
            .addInfo(translateToLocal("ote.tm.sinopec.1"))
            .addInfo(translateToLocal("ote.tm.sinopec.2"))
            .addInfo(translateToLocal("ote.tm.sinopec.3"))
            .addInfo(translateToLocal("ote.tm.sinopec.4"))
            .addTecTechHatchInfo()
            .addPollutionAmount(6400)
            .addSeparator()
            .addController(translateToLocal("ote.tn.sinopec"))
            .beginStructureBlock(33, 49, 48, false)
            .addInputBus("AnyInputBus", 1)
            .addOutputBus("AnyOutputBus", 1)
            .addInputHatch("AnyInputHatch", 1)
            .addOutputHatch("AnyOutputHatch", 1)
            .addEnergyHatch("AnyEnergyHatch", 1)
            .addMufflerHatch("AnyMufflerHatch", 1)
            .toolTipFinisher("§a123Technology - Heavy industry - SINOPEC");
        return tt;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTESINOPEC(this.mName);
    }

    @Override
    public ITexture[] getTexture(final IGregTechTileEntity baseMetaTileEntity, final ForgeDirection sideDirection,
        final ForgeDirection facing, final int aColorIndex, final boolean active, final boolean aRedstone) {

        if (sideDirection == facing) {
            if (active) return new ITexture[] {
                Textures.BlockIcons.getCasingTextureForId(GTUtility.getCasingTextureIndex(sBlockCasings4, 0)),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE_GLOW)
                    .extFacing()
                    .build() };
            return new ITexture[] {
                Textures.BlockIcons.getCasingTextureForId(GTUtility.getCasingTextureIndex(sBlockCasings4, 0)),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] {
            Textures.BlockIcons.getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings4, 0)) };
    }

    @Override
    protected SoundResource getProcessStartSound() {
        return SoundResource.GT_MACHINES_DISTILLERY_LOOP;
    }
}
