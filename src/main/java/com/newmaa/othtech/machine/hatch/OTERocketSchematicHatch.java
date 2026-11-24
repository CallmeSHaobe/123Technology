package com.newmaa.othtech.machine.hatch;

import static gregtech.api.enums.Mods.NewHorizonsCoreMod;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_DATA_ACCESS;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.render.TextureFactory;
import gregtech.common.gui.modularui.hatch.base.MTEHatchBaseGui;
import gregtech.common.modularui2.widget.builder.ItemSlotGridBuilder;
import gregtech.common.tileentities.machines.ISmartInputHatch;

public class OTERocketSchematicHatch extends MTEHatch implements ISmartInputHatch {

    private static final int COLUMNS = 4;
    private static final int ROWS = 2;
    private static final int SLOT_COUNT = COLUMNS * ROWS;
    private static final List<String> SCHEMATICS = Arrays.asList(
        "SchematicsTier1",
        "SchematicsTier2",
        "SchematicsTier3",
        "SchematicsTier4",
        "SchematicsTier5",
        "SchematicsTier6",
        "SchematicsTier7",
        "SchematicsTier8");

    public static final String NAME_KEY = "ote.tn.rocket_schematic_hatch";

    public OTERocketSchematicHatch(int id, String name, String regionalName) {
        super(
            id,
            name,
            regionalName,
            1,
            SLOT_COUNT,
            new String[] { translateToLocal("ote.tt.rocket_schematic_hatch.0"),
                translateToLocal("ote.tt.rocket_schematic_hatch.1") });
    }

    private OTERocketSchematicHatch(String name, int tier, String[] description, ITexture[][][] textures) {
        super(name, tier, SLOT_COUNT, description, textures);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity base) {
        return new OTERocketSchematicHatch(mName, mTier, mDescriptionArray, mTextures);
    }

    public boolean containsSchematic(ItemStack required) {
        if (!isRocketSchematic(required)) return false;
        for (ItemStack stored : mInventory) {
            if (stored != null && stored.stackSize > 0 && required.isItemEqual(stored)) return true;
        }
        return false;
    }

    private static boolean isRocketSchematic(ItemStack stack) {
        if (stack == null || stack.getItem() == null || stack.getItemDamage() != 0) return false;
        GameRegistry.UniqueIdentifier id = GameRegistry.findUniqueIdentifierFor(stack.getItem());
        return id != null && NewHorizonsCoreMod.ID.equals(id.modId) && SCHEMATICS.contains(id.name);
    }

    @Override
    public boolean isFacingValid(ForgeDirection facing) {
        return facing != null && facing != ForgeDirection.UNKNOWN;
    }

    @Override
    public boolean isValidSlot(int slot) {
        return slot >= 0 && slot < SLOT_COUNT;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return isValidSlot(slot) && isRocketSchematic(stack);
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity base, int slot, ForgeDirection side, ItemStack stack) {
        return side == base.getFrontFacing() && isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean allowPullStack(IGregTechTileEntity base, int slot, ForgeDirection side, ItemStack stack) {
        return side == base.getFrontFacing() && isValidSlot(slot);
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public ITexture[] getTexturesActive(ITexture baseTexture) {
        return getTexturesInactive(baseTexture);
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture baseTexture) {
        return new ITexture[] { baseTexture, TextureFactory.of(OVERLAY_DATA_ACCESS) };
    }

    @Override
    public void onPostTick(IGregTechTileEntity base, long tick) {
        super.onPostTick(base, tick);
        if (base.isServerSide()) detectInventoryChange();
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity base, EntityPlayer player) {
        if (base.isServerSide()) openGui(player);
        return true;
    }

    @Override
    protected boolean forceUseMui2() {
        return true;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager sync, UISettings settings) {
        return new MTEHatchBaseGui<OTERocketSchematicHatch>(this) {

            @Override
            protected ParentWidget<?> createContentSection(ModularPanel panel, PanelSyncManager syncManager) {
                return super.createContentSection(panel, syncManager).child(
                    new ItemSlotGridBuilder(inventoryHandler, syncManager).size(COLUMNS, ROWS)
                        .filter(OTERocketSchematicHatch::isRocketSchematic)
                        .build()
                        .center());
            }
        }.build(data, sync, settings);
    }
}
