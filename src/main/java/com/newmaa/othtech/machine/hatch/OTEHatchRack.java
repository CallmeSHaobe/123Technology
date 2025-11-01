package com.newmaa.othtech.machine.hatch;

import static gregtech.api.enums.Mods.GraviSuite;
import static gregtech.api.enums.Mods.NewHorizonsCoreMod;
import static gregtech.api.enums.Mods.OpenComputers;
import static gregtech.api.recipe.RecipeMaps.quantumComputerFakeRecipes;
import static gregtech.api.util.GTModHandler.getModItem;
import static gregtech.api.util.GTRecipeConstants.QUANTUM_COMPUTER_DATA;
import static net.minecraft.util.StatCollector.translateToLocal;
import static net.minecraft.util.StatCollector.translateToLocalFormatted;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.internal.wrapper.BaseSlot;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.modularui.IAddGregtechLogo;
import gregtech.api.interfaces.modularui.IAddUIWidgets;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;
import gregtech.api.util.recipe.QuantumComputerRecipeData;
import gregtech.mixin.interfaces.accessors.EntityPlayerMPAccessor;
import tectech.TecTech;
import tectech.thing.gui.TecTechUITextures;
import tectech.util.CommonValues;
import tectech.util.TTUtility;

/**
 * Created by Tec on 03.04.2017.
 */
public class OTEHatchRack extends MTEHatch implements IAddGregtechLogo, IAddUIWidgets {

    private static Textures.BlockIcons.CustomIcon EM_R;
    private static Textures.BlockIcons.CustomIcon EM_R_ACTIVE;
    private float overClock = 1, overVolt = 1;
    private static final Map<String, RackComponent> componentBinds = new HashMap<>();

    private String clientLocale = "en_US";

    public OTEHatchRack(int aID, String aName, String aNameRegional, int aTier) {
        super(
            aID,
            aName,
            aNameRegional,
            aTier,
            4,
            new String[] { CommonValues.TEC_MARK_EM, translateToLocal("gt.blockmachines.hatch.rack.desc.0"),
                EnumChatFormatting.AQUA + translateToLocal("gt.blockmachines.hatch.rack.desc.1") });
    }

    public OTEHatchRack(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, 4, aDescription, aTextures);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setFloat("eOverClock", overClock);
        aNBT.setFloat("eOverVolt", overVolt);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        overClock = aNBT.getFloat("eOverClock");
        overVolt = aNBT.getFloat("eOverVolt");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister aBlockIconRegister) {
        super.registerIcons(aBlockIconRegister);
        EM_R_ACTIVE = new Textures.BlockIcons.CustomIcon("iconsets/EM_RACK_ACTIVE");
        EM_R = new Textures.BlockIcons.CustomIcon("iconsets/EM_RACK");
    }

    @Override
    public ITexture[] getTexturesActive(ITexture aBaseTexture) {
        return new ITexture[] { aBaseTexture, TextureFactory.of(EM_R_ACTIVE) };
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
        return new ITexture[] { aBaseTexture, TextureFactory.of(EM_R) };
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTEHatchRack(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public boolean isFacingValid(ForgeDirection facing) {
        return true;
    }

    @Override
    public boolean isValidSlot(int aIndex) {
        return true;
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        return side == aBaseMetaTileEntity.getFrontFacing();
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        return side == aBaseMetaTileEntity.getFrontFacing();
    }

    @Override
    public int getSizeInventory() {
        return mInventory.length;
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (aBaseMetaTileEntity.isClientSide()) {
            return true;
        }
        if (aPlayer instanceof EntityPlayerMPAccessor) {
            clientLocale = ((EntityPlayerMPAccessor) aPlayer).gt5u$getTranslator();
        }
        openGui(aPlayer);
        return true;
    }

    private int getComputationPower(float overclock, float overvolt, boolean tickingComponents) {
        float computation = 0;
        for (int i = 0; i < mInventory.length; i++) {
            if (mInventory[i] == null || mInventory[i].stackSize != 1) {
                continue;
            }
            String itemIdentifier = TTUtility.getUniqueIdentifier(mInventory[i]);
            RackComponent comp = componentBinds.get(itemIdentifier);

            if (comp == null) {
                continue;
            }

            if (overvolt > TecTech.RANDOM.nextFloat()) {
                computation += comp.computation * (1 + overclock * overclock)
                    / (1 + (overclock - overvolt) * (overclock - overvolt));
            }
        }
        return (int) Math.floor(computation);
    }

    public int tickComponents(float oc, float ov) {
        overClock = oc;
        overVolt = ov;
        int computation = getComputationPower(overClock, overVolt, true);

        // 根据是否有计算力输出来设置激活状态
        if (computation > 0) {
            getBaseMetaTileEntity().setActive(true);
        } else {
            getBaseMetaTileEntity().setActive(false);
        }

        return computation;
    }

    @Override
    public boolean isGivingInformation() {
        return true;
    }

    @Override
    public String[] getInfoData() {
        int currentComputation = getComputationPower(overClock, overVolt, false);
        return new String[] {
            translateToLocalFormatted("tt.keyphrase.Base_computation", clientLocale) + ": "
                + EnumChatFormatting.AQUA
                + currentComputation,
            translateToLocalFormatted("tt.keyphrase.Components_Installed", clientLocale) + ": "
                + EnumChatFormatting.YELLOW
                + getInstalledComponentsCount() };
    }

    private int getInstalledComponentsCount() {
        int count = 0;
        for (int i = 0; i < mInventory.length; i++) {
            if (mInventory[i] != null && mInventory[i].stackSize == 1) {
                String itemIdentifier = TTUtility.getUniqueIdentifier(mInventory[i]);
                if (componentBinds.containsKey(itemIdentifier)) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder) {
        builder.widget(
            new DrawableWidget().setDrawable(TecTechUITextures.PICTURE_TECTECH_LOGO)
                .setSize(18, 18)
                .setPos(151, 63));
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        builder.widget(
            new DrawableWidget().setDrawable(TecTechUITextures.PICTURE_HEAT_SINK)
                .setPos(46, 17)
                .setSize(84, 60));

        Pos2d[] positions = new Pos2d[] { new Pos2d(68, 27), new Pos2d(90, 27), new Pos2d(68, 49), new Pos2d(90, 49), };
        for (int i = 0; i < positions.length; i++) {
            builder.widget(new SlotWidget(new BaseSlot(inventoryHandler, i) {

                @Override
                public int getSlotStackLimit() {
                    return 1;
                }

                @Override
                public boolean isEnabled() {
                    return !getBaseMetaTileEntity().isActive();
                }
            }).setBackground(getGUITextureSet().getItemSlot(), TecTechUITextures.OVERLAY_SLOT_RACK)
                .setPos(positions[i]));

            builder.widget(
                new DrawableWidget().setDrawable(TecTechUITextures.BUTTON_STANDARD_LIGHT_16x16)
                    .setPos(152, 24)
                    .setSize(16, 16))
                .widget(
                    new DrawableWidget()
                        .setDrawable(
                            () -> getBaseMetaTileEntity().isActive() ? TecTechUITextures.OVERLAY_BUTTON_POWER_SWITCH_ON
                                : TecTechUITextures.OVERLAY_BUTTON_POWER_SWITCH_DISABLED)
                        .setPos(152, 24)
                        .setSize(16, 16))
                .widget(
                    new FakeSyncWidget.BooleanSyncer(
                        () -> getBaseMetaTileEntity().isActive(),
                        val -> getBaseMetaTileEntity().setActive(val)));
        }
    }

    public static void run() { // 20k heat cap max!

        new RackComponent(ItemList.Circuit_Crystalprocessor.get(1), 60, 56, -1f, 20000, true); // IV
        new RackComponent(ItemList.Circuit_Crystalcomputer.get(1), 80, 54, -1f, 20000, true); // LuV
        new RackComponent(ItemList.Circuit_Ultimatecrystalcomputer.get(1), 100, 52, -1f, 20000, true); // ZPM
        new RackComponent(ItemList.Circuit_Crystalmainframe.get(1), 120, 50, -1f, 20000, true); // UV

        new RackComponent(ItemList.Circuit_Neuroprocessor.get(1), 160, 46, -1f, 40000, true); // LuV
        new RackComponent(ItemList.Circuit_Wetwarecomputer.get(1), 180, 44, -1f, 40000, true); // ZPM
        new RackComponent(ItemList.Circuit_Wetwaresupercomputer.get(1), 200, 42, -1f, 40000, true); // UV
        new RackComponent(ItemList.Circuit_Wetwaremainframe.get(1), 220, 40, -1f, 40000, true); // UHV

        new RackComponent(GTModHandler.getIC2Item("reactorVent", 1), 0, -1, 40f, 20000, false); // Heat Vent
        new RackComponent(GTModHandler.getIC2Item("reactorVentCore", 1), 0, -1, 80f, 40000, false); // Reactor Heat Vent
        new RackComponent(GTModHandler.getIC2Item("reactorVentGold", 1), 0, -1, 120f, 60000, false); // Overclocked Heat
        // Vent
        new RackComponent(GTModHandler.getIC2Item("reactorVentDiamond", 1), 0, -1, 160f, 80000, false); // Advanced Heat
        // Vent

        if (NewHorizonsCoreMod.isModLoaded()) {
            // GTNH-GT5u circuits (these components causes crashes when used with the original GT5u)
            new RackComponent(ItemList.Circuit_Bioprocessor.get(1), 200, 36, -1f, 60000, true); // ZPM
            new RackComponent(ItemList.Circuit_Biowarecomputer.get(1), 220, 34, -1f, 60000, true); // UV
            new RackComponent(ItemList.Circuit_Biowaresupercomputer.get(1), 240, 32, -1f, 60000, true); // UHV
            new RackComponent(ItemList.Circuit_Biomainframe.get(1), 260, 30, -1f, 60000, true); // UEV

            new RackComponent(ItemList.Circuit_OpticalProcessor.get(1), 200, 26, -1f, 80000, true); // UV
            new RackComponent(ItemList.Circuit_OpticalAssembly.get(1), 220, 24, -1f, 80000, true); // UHV
            new RackComponent(ItemList.Circuit_OpticalComputer.get(1), 240, 22, -1f, 80000, true); // UEV
            new RackComponent(ItemList.Circuit_OpticalMainframe.get(1), 260, 20, -1f, 80000, true); // UIV

            new RackComponent(getModItem(NewHorizonsCoreMod.ID, "item.PikoCircuit", 1), 260, 12, -1f, 95000, true); // UMV
            new RackComponent(getModItem(NewHorizonsCoreMod.ID, "item.QuantumCircuit", 1), 320, 10, -1f, 100000, true); // UXV
        }

        if (OpenComputers.isModLoaded()) {
            // 特别关注APU T3的注册
            ItemStack apuT3Stack = getModItem(OpenComputers.ID, "item", 1, 102);
            if (apuT3Stack != null) {
                new RackComponent(apuT3Stack, 120, 42, -1f, 20000, true); // APU T3
            }

            new RackComponent(getModItem(OpenComputers.ID, "item", 1, 43), 80, 46, -1f, 20000, true); // CPU T3
            new RackComponent(getModItem(OpenComputers.ID, "item", 1, 10), 100, 44, -1f, 20000, true); // GPU T3
            new RackComponent(getModItem(OpenComputers.ID, "item", 1, 103), 240, 40, -1f, 20000, true); // APU Creative
        }

        if (GraviSuite.isModLoaded()) {
            new RackComponent(getModItem(GraviSuite.ID, "itemSimpleItem", 1, 2), 0, -1, 2000f, 100000, false); // CC
        }

    }

    public static class RackComponent implements Comparable<RackComponent> {

        private final String unlocalizedName;
        private final float heatConstant, coolConstant, computation, maxHeat;
        private final boolean subZero;

        RackComponent(ItemStack is, float computation, float heatConstant, float coolConstant, float maxHeat,
            boolean subZero) {
            this(TTUtility.getUniqueIdentifier(is), computation, heatConstant, coolConstant, maxHeat, subZero);
        }

        RackComponent(String is, float computation, float heatConstant, float coolConstant, float maxHeat,
            boolean subZero) {
            this.unlocalizedName = is;
            this.computation = computation;
            this.heatConstant = heatConstant;
            this.coolConstant = coolConstant;
            this.maxHeat = maxHeat;
            this.subZero = subZero;

            // 注册到组件绑定
            componentBinds.put(unlocalizedName, this);

            // 同时注册到量子计算机配方
            GTValues.RA.stdBuilder()
                .itemInputs(GTUtility.copyAmount(1, getItemStackFromIdentifier(unlocalizedName)))
                .metadata(
                    QUANTUM_COMPUTER_DATA,
                    new QuantumComputerRecipeData(heatConstant, coolConstant, computation, maxHeat, subZero))
                .duration(0)
                .eut(0)
                .fake()
                .addTo(quantumComputerFakeRecipes);

        }

        @Override
        public int compareTo(RackComponent o) {
            return unlocalizedName.compareTo(o.unlocalizedName);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof RackComponent) {
                return compareTo((RackComponent) obj) == 0;
            }
            return false;
        }
    }

    /**
     * 从标识符获取ItemStack的辅助方法
     */
    private static ItemStack getItemStackFromIdentifier(String identifier) {
        // 这里需要根据你的标识符系统实现相应的逻辑
        // 这只是一个示例实现，你需要根据实际情况调整
        try {
            String[] parts = identifier.split(":");
            if (parts.length >= 3) {
                String modId = parts[0];
                String itemName = parts[1];
                int meta = Integer.parseInt(parts[2]);
                return getModItem(modId, itemName, 1, meta);
            }
        } catch (Exception e) {
            TecTech.LOGGER.error("无法从标识符解析物品: " + identifier, e);
        }
        return null;
    }
}
