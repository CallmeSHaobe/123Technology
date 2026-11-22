package com.newmaa.othtech.machine.hatch;

import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizons.aspectrecipeindex.ModItems;
import com.gtnewhorizons.aspectrecipeindex.common.items.ItemAspect;

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
        if (tag == null) return false;
        NBTTagCompound config = (NBTTagCompound) tag.copy();
        config.setBoolean("autoPull", !tag.hasKey("essentiaAutoPull") || tag.getBoolean("essentiaAutoPull"));
        if (!super.pasteCopiedData(player, config)) return false;
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
        tag.setBoolean("autoPull", essentiaAutoPull);
        if (essentiaAutoPull) tag.removeTag("itemsToStock");
        return tag;
    }

    private Object getNetworkIdentity() {
        try {
            return getProxy().getGrid();
        } catch (GridAccessException ignored) {
            return null;
        }
    }

    /** Buses on one grid share stock, but each bus can expose a different set of aspects. */
    public static void collectAvailableEssentia(Collection<OTEEssentiaStockingInputBusME> buses,
        Map<Aspect, Long> output, long limit) {
        if (limit <= 0L) return;
        Map<Object, Map<Aspect, Long>> stocks = new LinkedHashMap<>();
        for (OTEEssentiaStockingInputBusME bus : buses) {
            if (bus.getBaseMetaTileEntity() == null || bus.getBaseMetaTileEntity()
                .isDead() || !bus.isAllowedToWork()) continue;
            Object grid = bus.getNetworkIdentity();
            if (grid != null) bus.collectNetworkStock(stocks.computeIfAbsent(grid, ignored -> new LinkedHashMap<>()));
        }
        for (Map<Aspect, Long> stock : stocks.values()) {
            for (Map.Entry<Aspect, Long> entry : stock.entrySet()) {
                long amount = Math.min(limit, entry.getValue());
                output.merge(entry.getKey(), amount, Long::sum);
                limit -= amount;
                if (limit == 0L) return;
            }
        }
    }

    private void collectNetworkStock(Map<Aspect, Long> stock) {
        IMEMonitor<AEEssentiaStack> monitor = getEssentiaMonitor();
        if (monitor == null) return;

        Set<Aspect> filters = essentiaAutoPull ? Collections.emptySet() : getAspectFilters();
        for (AEEssentiaStack stack : monitor.getStorageList()) {
            if (stack == null || stack.getAspect() == null || stack.getStackSize() <= 0L) continue;
            if (!filters.isEmpty() && !filters.contains(stack.getAspect())) continue;
            stock.merge(stack.getAspect(), stack.getStackSize(), Math::max);
        }
    }

    public static long extractEssentia(Collection<OTEEssentiaStockingInputBusME> buses, Aspect aspect, long amount) {
        long remaining = amount;
        for (OTEEssentiaStockingInputBusME bus : buses) {
            if (bus.getBaseMetaTileEntity() == null || bus.getBaseMetaTileEntity()
                .isDead()) continue;
            remaining -= bus.extractEssentia(aspect, remaining);
            if (remaining == 0L) break;
        }
        return amount - remaining;
    }

    private long extractEssentia(Aspect aspect, long amount) {
        if (aspect == null || amount <= 0L || !isAllowedToWork()) return 0L;
        if (!essentiaAutoPull) {
            Set<Aspect> filters = getAspectFilters();
            if (!filters.isEmpty() && !filters.contains(aspect)) return 0L;
        }
        IMEMonitor<AEEssentiaStack> monitor = getEssentiaMonitor();
        if (monitor == null) return 0L;
        IEnergyGrid energy = getProxyEnergy();
        if (energy == null) return 0L;

        AEEssentiaStack request = new AEEssentiaStack(aspect, amount);
        AEEssentiaStack extracted = Platform.poweredExtraction(energy, monitor, request, getRequestSource());
        return extracted == null ? 0L : Math.min(amount, extracted.getStackSize());
    }

    /** The display uses item markers. Transactions use the AE essentia channel. */
    @Override
    public void startRecipeProcessing() {}

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
