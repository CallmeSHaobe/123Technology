package com.newmaa.othtech.machine.hatch;

import static com.newmaa.othtech.recipe.RecipesMain.OTEquantumComputerFakeRecipes;
import static gregtech.api.enums.Mods.NewHorizonsCoreMod;
import static gregtech.api.enums.Mods.OpenComputers;
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

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.recipe.RecipesOTEFakeQuantumComputerData;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.modularui2.GTGuiTheme;
import gregtech.api.modularui2.GTGuiThemes;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTModHandler;
import gregtech.mixin.interfaces.accessors.EntityPlayerMPAccessor;
import tectech.TecTech;
import tectech.loader.ConfigHandler;
import tectech.util.CommonValues;
import tectech.util.TTUtility;

/**
 * Created by Tec on 03.04.2017.
 */
public class OTEHatchRack extends MTEHatch {

    private static IIconContainer EM_R;
    private static IIconContainer EM_R_ACTIVE;
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
        EM_R_ACTIVE = Textures.BlockIcons.custom("iconsets/EM_RACK_ACTIVE");
        EM_R = Textures.BlockIcons.custom("iconsets/EM_RACK");
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

    // region MUI2 GUI

    @Override
    protected boolean useMui2() {
        return true;
    }

    @Override
    protected GTGuiTheme getGuiTheme() {
        return GTGuiThemes.TECTECH_STANDARD;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings uiSettings) {
        return new OTEHatchRackGui(this).build(data, syncManager, uiSettings);
    }

    // endregion

    public static void run() { // 20k heat cap max!

        new RackComponent(ItemList.Circuit_Crystalprocessor.get(1), 60, 56, -1f, true); // IV
        new RackComponent(ItemList.Circuit_Crystalcomputer.get(1), 80, 54, -1f, true); // LuV
        new RackComponent(ItemList.Circuit_Ultimatecrystalcomputer.get(1), 100, 52, -1f, true); // ZPM
        new RackComponent(ItemList.Circuit_Crystalmainframe.get(1), 120, 50, -1f, true); // UV

        new RackComponent(ItemList.Circuit_Neuroprocessor.get(1), 160, 46, -1f, true); // LuV
        new RackComponent(ItemList.Circuit_Wetwarecomputer.get(1), 180, 44, -1f, true); // ZPM
        new RackComponent(ItemList.Circuit_Wetwaresupercomputer.get(1), 200, 42, -1f, true); // UV
        new RackComponent(ItemList.Circuit_Wetwaremainframe.get(1), 220, 40, -1f, true); // UHV

        new RackComponent(GTModHandler.getIC2Item("reactorVent", 1), 0, -1, 40f, false); // Heat Vent
        new RackComponent(GTModHandler.getIC2Item("reactorVentCore", 1), 0, -1, 80f, false); // Reactor Heat Vent
        new RackComponent(GTModHandler.getIC2Item("reactorVentGold", 1), 0, -1, 120f, false); // Overclocked Heat
        // Vent
        new RackComponent(GTModHandler.getIC2Item("reactorVentDiamond", 1), 0, -1, 160f, false); // Advanced Heat
        // Vent

        if (NewHorizonsCoreMod.isModLoaded()) {
            // GTNH-GT5u circuits (these components causes crashes when used with the original GT5u)
            new RackComponent(ItemList.Circuit_Bioprocessor.get(1), 2000, 36, -1f, true); // ZPM
            new RackComponent(ItemList.Circuit_Biowarecomputer.get(1), 2000, 34, -1f, true); // UV
            new RackComponent(ItemList.Circuit_Biowaresupercomputer.get(1), 2400, 32, -1f, true); // UHV
            new RackComponent(ItemList.Circuit_Biomainframe.get(1), 2600, 30, -1f, true); // UEV

            new RackComponent(ItemList.Circuit_OpticalProcessor.get(1), 2000, 26, -1f, true); // UV
            new RackComponent(ItemList.Circuit_OpticalAssembly.get(1), 2200, 24, -1f, true); // UHV
            new RackComponent(ItemList.Circuit_OpticalComputer.get(1), 2400, 22, -1f, true); // UEV
            new RackComponent(ItemList.Circuit_OpticalMainframe.get(1), 2600, 20, -1f, true); // UIV

            ItemStack pikoCircuit = getModItem(NewHorizonsCoreMod.ID, "PikoCircuit", 1);
            if (pikoCircuit != null) {
                new RackComponent(pikoCircuit, 2600, 12, -1f, true); // UMV
            }

            ItemStack quantumCircuit = getModItem(NewHorizonsCoreMod.ID, "QuantumCircuit", 1);
            if (quantumCircuit != null) {
                new RackComponent(quantumCircuit, 3200, 10, -1f, true); // UXV
            }

            ItemStack planckCircuit = getModItem(NewHorizonsCoreMod.ID, "PlanckCircuit", 1);
            if (planckCircuit != null) {
                new RackComponent(planckCircuit, 3600, 8, -1f, true); // MAX
            }

            ItemStack nukeStack = OTHItemList.NukeThrowable.get(1);
            if (nukeStack != null) {
                new RackComponent(nukeStack, 123123, 114514, -1f, true);
            }
        }

        if (OpenComputers.isModLoaded()) {
            ItemStack apuT3Stack = getModItem(OpenComputers.ID, "item", 1, 102);
            if (apuT3Stack != null) {
                new RackComponent(apuT3Stack, 1200, 42, -1f, true); // APU T3
            }

            ItemStack cpuT3Stack = getModItem(OpenComputers.ID, "item", 1, 43);
            if (cpuT3Stack != null) {
                new RackComponent(cpuT3Stack, 800, 46, -1f, true); // CPU T3
            }

            ItemStack gpuT3Stack = getModItem(OpenComputers.ID, "item", 1, 10);
            if (gpuT3Stack != null) {
                new RackComponent(gpuT3Stack, 1000, 44, -1f, true); // GPU T3
            }

            ItemStack apuCreativeStack = getModItem(OpenComputers.ID, "item", 1, 103);
            if (apuCreativeStack != null) {
                new RackComponent(apuCreativeStack, 2400, 40, -1f, true); // APU Creative
            }
        }
    }

    public static class RackComponent implements Comparable<OTEHatchRack.RackComponent> {

        private final String unlocalizedName;
        private final float heatConstant, coolConstant, computation;
        private final boolean subZero;

        RackComponent(ItemStack is, float computation, float heatConstant, float coolConstant, boolean subZero) {
            this(TTUtility.getUniqueIdentifier(is), computation, heatConstant, coolConstant, subZero);

            if (is == null) {
                TecTech.LOGGER
                    .warn("Attempted to register a null ItemStack in RackComponent — skipping recipe registration.");
                return;
            }

            GTValues.RA.stdBuilder()
                .itemInputs(is)
                .metadata(
                    QUANTUM_COMPUTER_DATA,
                    new RecipesOTEFakeQuantumComputerData(heatConstant, coolConstant, computation, subZero))
                .duration(0)
                .eut(0)
                .fake()
                .addTo(OTEquantumComputerFakeRecipes);
        }

        RackComponent(String is, float computation, float heatConstant, float coolConstant, boolean subZero) {
            unlocalizedName = is;
            this.computation = computation;
            this.heatConstant = heatConstant;
            this.coolConstant = coolConstant;
            this.subZero = subZero;

            if (unlocalizedName != null) {
                componentBinds.put(unlocalizedName, this);
            }

            if (ConfigHandler.debug.DEBUG_MODE) {
                TecTech.LOGGER.info("Component registered: " + unlocalizedName);
            }
        }

        @Override
        public int compareTo(RackComponent o) {
            if (unlocalizedName == null) return o.unlocalizedName == null ? 0 : -1;
            if (o.unlocalizedName == null) return 1;
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
}
