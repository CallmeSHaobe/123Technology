package com.newmaa.othtech.machine.hatch;

import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.gtnewhorizons.aspectrecipeindex.ModItems;
import com.gtnewhorizons.aspectrecipeindex.common.items.ItemAspect;

import appeng.api.networking.IGrid;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.storage.IMEMonitor;
import appeng.me.GridAccessException;
import appeng.util.Platform;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTUtility;
import gregtech.common.tileentities.machines.MTEHatchInputBusME;
import thaumcraft.api.aspects.Aspect;
import thaumicenergistics.common.storage.AEEssentiaStack;
import thaumicenergistics.common.storage.AEEssentiaStackType;

/**
 * Advanced stocking ME input bus whose stock is the essentia channel of the connected AE network.
 *
 * <p>
 * Unlike the item stocking bus, this hatch has no item filter. Each Void Nexus recipe check sees all essentia
 * currently available on the network. Essentia is extracted only after the recipe passes validation, using AE grid
 * power in the same way as the advanced item stocking bus.
 * </p>
 */
public class OTEEssentiaStockingInputBusME extends MTEHatchInputBusME {

    private boolean essentiaAutoPull = true;

    public OTEEssentiaStockingInputBusME(int id, String name, String regionalName) {
        super(id, true, name, regionalName);
        disableSort = true;
    }

    public OTEEssentiaStockingInputBusME(String name, int tier, String[] description, ITexture[][][] textures) {
        super(name, true, tier, description, textures);
        disableSort = true;
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new OTEEssentiaStockingInputBusME(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public String[] getDescription() {
        return new String[] { EnumChatFormatting.AQUA + translateToLocal("ote.tm.esib.0"),
            EnumChatFormatting.GRAY + translateToLocal("ote.tm.esib.1"),
            EnumChatFormatting.GRAY + translateToLocal("ote.tm.esib.2") };
    }

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer player, float x, float y, float z,
        ItemStack tool) {
        setAutoPullItemList(!essentiaAutoPull);
        player.addChatMessage(
            new ChatComponentTranslation(
                "GT5U.machines.stocking_bus.auto_pull_toggle." + (essentiaAutoPull ? "enabled" : "disabled")));
    }

    @Override
    public void onPostTick(IGregTechTileEntity base, long timer) {
        super.onPostTick(base, timer);
        if (base.isServerSide() && essentiaAutoPull && timer % 20L == 0L) refreshEssentiaStock();
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings uiSettings) {
        return super.buildUI(data, syncManager, uiSettings);
    }

    @Override
    public boolean isAutoPullItemList() {
        return essentiaAutoPull;
    }

    @Override
    public void setAutoPullItemList(boolean autoPull) {
        if (essentiaAutoPull == autoPull) {
            if (autoPull && getBaseMetaTileEntity() != null && getBaseMetaTileEntity().isServerSide()) {
                refreshEssentiaStock();
            }
            return;
        }
        essentiaAutoPull = autoPull;
        Arrays.fill(slots, null);
        if (autoPull) {
            refreshEssentiaStock();
        }
    }

    @Override
    public void setSlotConfig(int index, ItemStack config) {
        if (index < 0 || index >= SLOT_COUNT) return;
        if (essentiaAutoPull && config != null) return;
        if (config != null && !isAspectFilterItem(config)) return;
        super.setSlotConfig(index, config == null ? null : GTUtility.copyAmount(1, config));
    }

    @Override
    public void updateInformationSlot(int index) {
        if (essentiaAutoPull || index < 0 || index >= SLOT_COUNT) return;
        Slot slot = slots[index];
        if (slot == null || !isAspectFilterItem(slot.config)) return;
        Aspect aspect = ItemAspect.getAspect(slot.config);
        long available = getAvailableEssentia(aspect);
        if (available <= 0L) {
            slot.resetExtracted();
            return;
        }
        slot.extracted = slot.config.copy();
        slot.extractedAmount = (int) Math.min(Integer.MAX_VALUE, available);
    }

    @Override
    public void saveNBTData(NBTTagCompound tag) {
        super.saveNBTData(tag);
        tag.setBoolean("essentiaAutoPull", essentiaAutoPull);
    }

    @Override
    public void loadNBTData(NBTTagCompound tag) {
        super.loadNBTData(tag);
        essentiaAutoPull = !tag.hasKey("essentiaAutoPull") || tag.getBoolean("essentiaAutoPull");
        if (essentiaAutoPull) {
            refreshEssentiaStock();
        } else {
            removeInvalidFilterSlots();
        }
    }

    @Override
    public boolean pasteCopiedData(EntityPlayer player, NBTTagCompound tag) {
        if (!super.pasteCopiedData(player, tag)) return false;
        essentiaAutoPull = !tag.hasKey("essentiaAutoPull") || tag.getBoolean("essentiaAutoPull");
        Arrays.fill(slots, null);
        if (essentiaAutoPull) {
            refreshEssentiaStock();
        } else {
            removeInvalidFilterSlots();
        }
        return true;
    }

    @Override
    public NBTTagCompound getCopiedData(EntityPlayer player) {
        NBTTagCompound tag = super.getCopiedData(player);
        tag.setBoolean("essentiaAutoPull", essentiaAutoPull);
        return tag;
    }

    public Object getNetworkIdentity() {
        try {
            IGrid grid = getProxy().getGrid();
            return grid == null ? null : grid;
        } catch (GridAccessException ignored) {
            return null;
        }
    }

    /**
     * Adds every available essentia aspect to {@code output}, stopping after {@code limit} units.
     *
     * @return number of essentia units added
     */
    public long collectAvailableEssentia(Map<Aspect, Long> output, long limit) {
        if (limit <= 0L || !isActive()) return 0L;
        IMEMonitor<AEEssentiaStack> monitor = getEssentiaMonitor();
        if (monitor == null) return 0L;

        Set<Aspect> filters = essentiaAutoPull ? Collections.emptySet() : getAspectFilters();
        long remaining = limit;
        for (AEEssentiaStack stack : monitor.getStorageList()) {
            if (remaining <= 0L) break;
            if (stack == null || stack.getAspect() == null || stack.getStackSize() <= 0L) continue;
            if (!filters.isEmpty() && !filters.contains(stack.getAspect())) continue;
            long amount = Math.min(remaining, stack.getStackSize());
            output.merge(stack.getAspect(), amount, Long::sum);
            remaining -= amount;
        }
        return limit - remaining;
    }

    /**
     * Extracts essentia from the ME network with AE power. A partial result is returned when the network does not
     * contain the full request.
     */
    public long extractEssentia(Aspect aspect, long amount) {
        if (aspect == null || amount <= 0L || !isActive()) return 0L;
        IMEMonitor<AEEssentiaStack> monitor = getEssentiaMonitor();
        if (monitor == null) return 0L;
        IEnergyGrid energy = getProxyEnergy();
        if (energy == null) return 0L;

        AEEssentiaStack request = new AEEssentiaStack(aspect, amount);
        AEEssentiaStack extracted = Platform.poweredExtraction(energy, monitor, request, getRequestSource());
        return extracted == null ? 0L : Math.min(amount, extracted.getStackSize());
    }

    /**
     * Do not participate in GT's item-stocking transaction. Aspect markers in the display slots are metadata,
     * not item requests, and Void Nexus extracts them through the essentia channel itself.
     */
    @Override
    public void startRecipeProcessing() {
        // Custom essentia extraction is performed by VoidNexusMatrix after recipe validation.
    }

    @Override
    public CheckRecipeResult endRecipeProcessing(MTEMultiBlockBase machine) {
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @SuppressWarnings("unchecked")
    private IMEMonitor<AEEssentiaStack> getEssentiaMonitor() {
        if (!isActive()) return null;
        try {
            return (IMEMonitor<AEEssentiaStack>) getProxy().getStorage()
                .getMEMonitor(AEEssentiaStackType.ESSENTIA_STACK_TYPE);
        } catch (GridAccessException ignored) {
            return null;
        }
    }

    private void refreshEssentiaStock() {
        if (!essentiaAutoPull) return;
        IGregTechTileEntity base = getBaseMetaTileEntity();
        if (base == null || !base.isServerSide()) return;
        IMEMonitor<AEEssentiaStack> monitor = getEssentiaMonitor();
        if (monitor == null) {
            Arrays.fill(slots, null);
            return;
        }

        int index = 0;
        for (AEEssentiaStack stack : monitor.getStorageList()) {
            if (index >= SLOT_COUNT) break;
            if (stack == null || stack.getAspect() == null || stack.getStackSize() <= 0L) continue;
            ItemStack marker = aspectMarker(stack.getAspect());
            if (marker == null) continue;
            Slot slot = new Slot(marker);
            slot.extracted = marker.copy();
            slot.extractedAmount = (int) Math.min(Integer.MAX_VALUE, stack.getStackSize());
            slots[index++] = slot;
        }
        Arrays.fill(slots, index, SLOT_COUNT, null);
    }

    private long getAvailableEssentia(Aspect aspect) {
        IMEMonitor<AEEssentiaStack> monitor = getEssentiaMonitor();
        if (monitor == null || aspect == null) return 0L;
        for (AEEssentiaStack stack : monitor.getStorageList()) {
            if (stack != null && stack.getAspect() == aspect) return Math.max(0L, stack.getStackSize());
        }
        return 0L;
    }

    private static ItemStack aspectMarker(Aspect aspect) {
        if (aspect == null || ModItems.itemAspect == null) return null;
        ItemStack marker = new ItemStack(ModItems.itemAspect, 1, 0);
        ItemAspect.setAspect(marker, aspect);
        return marker;
    }

    private IEnergyGrid getProxyEnergy() {
        try {
            return getProxy().getEnergy();
        } catch (GridAccessException ignored) {
            return null;
        }
    }

    private Set<Aspect> getAspectFilters() {
        Set<Aspect> filters = new LinkedHashSet<>();
        for (Slot slot : slots) {
            if (slot == null || !isAspectFilterItem(slot.config)) continue;
            Aspect aspect = ItemAspect.getAspect(slot.config);
            if (aspect != null) filters.add(aspect);
        }
        return filters;
    }

    private static boolean isAspectFilterItem(ItemStack stack) {
        return stack != null && stack.getItem() == ModItems.itemAspect && ItemAspect.getAspect(stack) != null;
    }

    private void removeInvalidFilterSlots() {
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] != null && !isAspectFilterItem(slots[i].config)) slots[i] = null;
        }
    }
}
