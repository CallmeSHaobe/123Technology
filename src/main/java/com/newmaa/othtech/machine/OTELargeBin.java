package com.newmaa.othtech.machine;

import static com.newmaa.othtech.common.recipemap.Recipemaps.BIN;
import static gregtech.api.GregTechAPI.sBlockCasings2;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Mods.AppliedEnergistics2;
import static gregtech.api.recipe.RecipeMaps.recyclerRecipes;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IItemSource;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.machine.machineclass.OTH_MultiMachineBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.blocks.BlockCasings2;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import tectech.thing.metaTileEntity.multi.base.*;
import tectech.thing.metaTileEntity.multi.base.render.TTRenderedExtendedFacingTexture;

public class OTELargeBin extends OTH_MultiMachineBase<OTELargeBin> implements IConstructable, ISurvivalConstructable {

    private int mode = 0;
    public int mSA = 0;

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        repairMachine();
        return checkPiece(STRUCTURE_PIECE_MAIN, horizontalOffSet, verticalOffSet, depthOffSet);

    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        if (mode == 1) return recyclerRecipes;
        if (mode == 2) return Recipemaps.AE2;
        if (mode == 3) return RecipeMaps.neutroniumCompressorRecipes;
        return BIN;
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(recyclerRecipes, Recipemaps.AE2, RecipeMaps.neutroniumCompressorRecipes);
    }

    // region structure
    private static final String[] description = new String[] { EnumChatFormatting.AQUA + translateToLocal("搭建细节") + ":",
        translateToLocal("1 - 输入输出总线, 输入仓, 能源仓, 动力仓: 替换脱氧钢机械方块"),
        // Power Casing
    };
    private final int horizontalOffSet = 0;
    private final int verticalOffSet = 1;
    private final int depthOffSet = 0;
    private static IStructureDefinition<OTELargeBin> STRUCTURE_DEFINITION = null;
    private static final String STRUCTURE_PIECE_MAIN = "main";

    @Override
    public IStructureDefinition<OTELargeBin> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTELargeBin>builder()
                .addShape(STRUCTURE_PIECE_MAIN, shapeMain)
                .addElement(
                    '1',
                    buildHatchAdder(OTELargeBin.class)
                        .atLeast(InputBus, InputHatch, Energy.or(ExoticEnergy), Dynamo, OutputBus)
                        .dot(1)
                        .casingIndex(((BlockCasings2) GregTechAPI.sBlockCasings2).getTextureIndex(0))
                        .buildAndChain(sBlockCasings2, 0))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    private final String[][] shapeMain = new String[][] { { "1111", "~", "1111", } };

    // endregion
    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        aNBT.setInteger("mode", this.mode);
        aNBT.setInteger("MSA", this.mSA);
        super.saveNBTData(aNBT);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        this.mode = aNBT.getInteger("mode");
        mSA = aNBT.getInteger("MSA");
        super.loadNBTData(aNBT);

    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        final IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity != null) {
            String Mode = GTUtility.formatNumbers(mode);
            tag.setString("Tier", Mode);
            tag.setLong("MM", mSA);
        }
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        currentTip.add(
            "模式" + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + tag.getString("Tier")
                + EnumChatFormatting.RESET);
        currentTip.add(
            "奇点模式物品输入数量" + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + tag.getLong("MM")
                + EnumChatFormatting.RESET);
    }

    public OTELargeBin(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTELargeBin(String aName) {
        super(aName);
    }

    @Override
    public void onRemoval() {
        super.onRemoval();
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTELargeBin(mName);
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                return super.validateRecipe(recipe);
            }
        };
    }

    @Override
    protected boolean isEnablePerfectOverclock() {
        return mode == 1;
    }

    @Override
    protected float getSpeedBonus() {
        return 1;
    }

    @Override
    protected int getMaxParallelRecipes() {
        return mode == 1 ? (GTUtility.getTier(this.getMaxInputVoltage()) * 256) : 256;
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        if (mode == 0) {
            mEUt = 0;
            mMaxProgresstime = 10;
            ItemStack[] itemStack = getAllStoredInputs().toArray(new ItemStack[0]);
            FluidStack[] fluidStack = getStoredFluids().toArray(new FluidStack[0]);
            if (getAllStoredInputs() != null) {
                for (ItemStack item : itemStack) {
                    item.stackSize -= item.stackSize;
                }
            }
            if (getStoredFluids() != null) {
                for (FluidStack fluid : fluidStack) {
                    fluid.amount -= fluid.amount;
                }
            }

        } else if (mode == 1) {
            mMaxProgresstime = 10;
            int a;
            ItemStack[] itemStack = getAllStoredInputs().toArray(new ItemStack[0]);
            for (ItemStack item : itemStack) {
                if (item.stackSize <= getMaxParallelRecipes()) {
                    a = item.stackSize;
                    mEUt = (30 * a);
                    item.stackSize -= item.stackSize;
                    mOutputItems = new ItemStack[] { ItemList.IC2_Scrap.get((long) Math.floor(a * 0.5)) };
                }
            }
        } else if (mode == 2) {
            mMaxProgresstime = 10;
            ItemStack[] itemStack = getAllStoredInputs().toArray(new ItemStack[0]);
            for (ItemStack item : itemStack) {
                if (item.stackSize <= getMaxParallelRecipes()) {
                    mSA += item.stackSize;
                    item.stackSize -= item.stackSize;
                    if (mSA >= 256000) {
                        mOutputItems = new ItemStack[] {
                            GTModHandler.getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 1, 47) };
                        mSA -= 256000;
                    }
                } else if (item.stackSize > getMaxParallelRecipes()) {
                    mSA += getMaxParallelRecipes();
                    item.stackSize -= getMaxParallelRecipes();
                    if (mSA >= 256000) {
                        mOutputItems = new ItemStack[] {
                            GTModHandler.getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 1, 47) };
                        mSA -= 256000;
                    }
                }

            }
        }
        return CheckRecipeResultRegistry.NO_RECIPE;
    }

    @Override
    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        if (getBaseMetaTileEntity().isServerSide()) {
            if (mode < 3) {
                mode++;
            } else {
                mode = 0;
            }
            GTUtility.sendChatToPlayer(
                aPlayer,
                StatCollector.translateToLocal(
                    mode == 0 ? "垃圾桶模式"
                        : mode == 1 ? "回收机模式" : mode == 2 ? "AE奇点制造机模式" : mode == 3 ? "中子态素压缩机模式" : "Null"));
        }
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(translateToLocal("垃圾桶丨回收机丨AE奇点制造机丨中子态素压缩机"))
            .addInfo(translateToLocal("销毁一切, 仅此而已吗?"))
            .addInfo("回收机模式功率为并行 * 30EU/t, 中子态素压缩机有损超频")
            .addInfo("于回收机模式下, 电压等级 + 1, 并行 + 256, 完全固定输出输入物品数量50%的废料")
            .addTecTechHatchInfo()
            .beginStructureBlock(1, 3, 1, false)
            .addController(translateToLocal("结构正中心")) // Controller: Front center
            .addCasingInfoMin(translateToLocal("0x 脱氧钢机械方块(最低)"), 5, false) // 0x High Power Casing
            // (minimum)
            .addEnergyHatch(translateToLocal("任意输入总线, 输入仓, 输出总线, 能源仓, 动力仓"), 1) // Energy Hatch: Any
            // High Power Casing
            .toolTipFinisher("§a123Technology - JUST A WASTE BIN");
        return tt;
    }

    protected static Textures.BlockIcons.CustomIcon ScreenOFF;
    protected static Textures.BlockIcons.CustomIcon ScreenON;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister aBlockIconRegister) {
        ScreenOFF = new Textures.BlockIcons.CustomIcon("iconsets/EM_CONTROLLER");
        ScreenON = new Textures.BlockIcons.CustomIcon("iconsets/EM_CONTROLLER_ACTIVE");
        super.registerIcons(aBlockIconRegister);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            return new ITexture[] {
                Textures.BlockIcons.getCasingTextureForId(GTUtility.getCasingTextureIndex(sBlockCasings2, 0)),
                new TTRenderedExtendedFacingTexture(aActive ? ScreenON : ScreenOFF) };
        }
        return new ITexture[] {
            Textures.BlockIcons.getCasingTextureForId(GTUtility.getCasingTextureIndex(sBlockCasings2, 0)) };
    }

    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.TECTECH_MACHINES_NOISE;
    }

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        return true;
    }

    @Override
    public boolean doRandomMaintenanceDamage() {
        return true;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        repairMachine();
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, horizontalOffSet, verticalOffSet, depthOffSet);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, IItemSource source, EntityPlayerMP actor) {
        if (mMachine) return -1;
        return survivialBuildPiece("main", stackSize, 0, 1, 0, elementBudget, source, actor, false, true);
    }

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return description;
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
    public boolean getDefaultHasMaintenanceChecks() {
        return false;
    }
}
