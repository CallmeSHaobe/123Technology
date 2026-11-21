package com.newmaa.othtech.machine.gui;

import java.util.function.Supplier;

import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.LongSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.newmaa.othtech.machine.OTELargeEssentiaGenerator;

import gregtech.api.util.GTUtility;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;

public final class OTELargeEssentiaGeneratorGui extends MTEMultiBlockBaseGui<OTELargeEssentiaGenerator> {

    private static final String GUI = "oth.essentia.gui.";

    public OTELargeEssentiaGeneratorGui(OTELargeEssentiaGenerator machine) {
        super(machine);
    }

    @Override
    protected void registerSyncValues(PanelSyncManager sync) {
        super.registerSyncValues(sync);
        sync.syncValue("diffusionTier", new IntSyncValue(multiblock::getDiffusionTier));
        sync.syncValue("diffusionStability", new IntSyncValue(multiblock::getDiffusionStability));
        sync.syncValue("essentiaOutput", new LongSyncValue(multiblock::getGeneratedEUt));
        sync.syncValue("essentiaFamily", new StringSyncValue(multiblock::getLastFuelFamily));
        sync.syncValue("essentiaReagent", new StringSyncValue(multiblock::getLastReagent));
    }

    @Override
    protected ListWidget<IWidget, ?> createTerminalTextWidget(PanelSyncManager sync, ModularPanel panel) {
        IntSyncValue tier = sync.findSyncHandler("diffusionTier", IntSyncValue.class);
        IntSyncValue stability = sync.findSyncHandler("diffusionStability", IntSyncValue.class);
        LongSyncValue power = sync.findSyncHandler("essentiaOutput", LongSyncValue.class);
        StringSyncValue family = sync.findSyncHandler("essentiaFamily", StringSyncValue.class);
        StringSyncValue reagent = sync.findSyncHandler("essentiaReagent", StringSyncValue.class);

        return super.createTerminalTextWidget(sync, panel).child(
            readout(
                () -> StatCollector
                    .translateToLocalFormatted(GUI + "output", GTUtility.formatShortenedLong(power.getLongValue()))))
            .child(
                readout(
                    () -> tier.getIntValue() == 0 ? StatCollector.translateToLocal(GUI + "unformed")
                        : StatCollector
                            .translateToLocalFormatted(GUI + "structure", tier.getIntValue(), stability.getIntValue())))
            .child(
                readout(
                    () -> StatCollector.translateToLocalFormatted(
                        GUI + "fuel",
                        family.getValue()
                            .isEmpty() ? "—" : StatCollector.translateToLocal(GUI + "family." + family.getValue()))))
            .child(
                readout(
                    () -> StatCollector.translateToLocalFormatted(
                        GUI + "reagent",
                        reagentName(family.getValue(), reagent.getValue()))));
    }

    private static IWidget readout(Supplier<String> value) {
        return IKey.dynamic(value)
            .color(0xFFF5EEFF)
            .shadow(true)
            .asWidget();
    }

    private static String reagentName(String family, String registryName) {
        if (family.isEmpty()) return "—";
        Fluid fluid = FluidRegistry.getFluid(registryName);
        return fluid == null ? StatCollector.translateToLocal(GUI + "none")
            : fluid.getLocalizedName(new FluidStack(fluid, 1));
    }
}
