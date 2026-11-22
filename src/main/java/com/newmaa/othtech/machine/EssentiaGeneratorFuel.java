package com.newmaa.othtech.machine;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.IntFunction;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import bartworks.system.material.WerkstoffLoader;
import goodgenerator.items.GGMaterial;
import gregtech.api.enums.Materials;
import gregtech.api.util.GTModHandler;
import thaumcraft.api.aspects.Aspect;

public final class EssentiaGeneratorFuel {

    // Bit positions are persisted on the machine and intake.
    enum Family {
        NORMAL,
        AIR,
        THERMAL,
        UNSTABLE,
        VICTUS,
        TAINTED,
        MECHANICS,
        SPRITE,
        RADIATION,
        ELECTRIC
    }

    final Family family;
    private final long baseEU;
    private final int reagentUnits;

    private EssentiaGeneratorFuel(JsonObject row) {
        family = Family.valueOf(
            row.get("category")
                .getAsString());
        baseEU = row.get("fuelValue")
            .getAsLong();
        // Legacy recipes round the coefficient down before applying the category's volume.
        reagentUnits = (int) row.get("consumeCeo")
            .getAsDouble();
    }

    private static final Map<String, EssentiaGeneratorFuel> FUELS = new HashMap<>();

    public static void load() {
        String path = "/assets/goodgenerator/data/essentia.json";
        try (InputStream stream = EssentiaGeneratorFuel.class.getResourceAsStream(path)) {
            if (stream == null) throw new IllegalStateException("Missing " + path);
            InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
            for (JsonElement element : new JsonParser().parse(reader)
                .getAsJsonObject()
                .getAsJsonArray("Essentia")) {
                JsonObject row = element.getAsJsonObject();
                FUELS.put(
                    row.get("name")
                        .getAsString()
                        .toLowerCase(Locale.ROOT),
                    new EssentiaGeneratorFuel(row));
            }
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Cannot read " + path, e);
        }
    }

    public static EssentiaGeneratorFuel forAspect(Aspect aspect) {
        return aspect == null ? null : FUELS.get(aspect.getTag());
    }

    public boolean enabled(int mask) {
        return (mask & (1 << family.ordinal())) != 0;
    }

    private static final class Reagent {

        final IntFunction<FluidStack> stack;
        final double multiplier;

        Reagent(IntFunction<FluidStack> stack, double multiplier) {
            this.stack = stack;
            this.multiplier = multiplier;
        }
    }

    private static final Reagent[] AIR = { new Reagent(n -> Materials.LiquidAir.getFluid(n), 1.5),
        new Reagent(n -> Materials.Air.getGas(n), 1) };
    private static final Reagent[] COOLANTS = { new Reagent(n -> Materials.SuperCoolant.getFluid(n), 9),
        new Reagent(n -> FluidRegistry.getFluidStack("cryotheum", n), 5),
        new Reagent(n -> GTModHandler.getIC2Coolant(n), 1.5), new Reagent(n -> Materials.Ice.getSolid(n), 1.2),
        new Reagent(n -> GTModHandler.getDistilledWater(n), 1), new Reagent(n -> Materials.Water.getFluid(n), 0.5) };
    private static final Reagent[] GASES = { new Reagent(n -> WerkstoffLoader.Xenon.getFluidOrGas(n), 4),
        new Reagent(n -> WerkstoffLoader.Krypton.getFluidOrGas(n), 3),
        new Reagent(n -> Materials.Argon.getFluid(n), 2.5),
        new Reagent(n -> WerkstoffLoader.Neon.getFluidOrGas(n), 2.2), new Reagent(n -> Materials.Helium.getFluid(n), 2),
        new Reagent(n -> Materials.Nitrogen.getFluid(n), 1) };
    private static final Reagent[] LIVING = { new Reagent(n -> FluidRegistry.getFluidStack("xpjuice", n), 2),
        new Reagent(n -> FluidRegistry.getFluidStack("lifeessence", n), 6) };
    private static final Reagent[] METALS = { new Reagent(n -> Materials.Caesium.getMolten(n), 2),
        new Reagent(n -> Materials.Uranium235.getMolten(n), 3), new Reagent(n -> Materials.Naquadah.getMolten(n), 4),
        new Reagent(n -> GGMaterial.atomicSeparationCatalyst.getMolten(n), 16) };

    private double withReagent(OTELargeEssentiaGenerator machine, int volume, double fallback, Reagent[] choices,
        boolean simulate) {
        for (Reagent choice : choices) {
            if (machine.useFluid(choice.stack.apply(volume * reagentUnits), simulate)) return choice.multiplier;
        }
        return fallback;
    }

    private double multiplier(OTELargeEssentiaGenerator machine, int stability, int tier, boolean simulate) {
        switch (family) {
            case NORMAL:
                return 1;
            case AIR:
                return withReagent(machine, 8, 0, AIR, simulate);
            case THERMAL:
                return withReagent(machine, 2, 0, COOLANTS, simulate);
            case UNSTABLE:
                return withReagent(machine, 4, 0, GASES, simulate);
            case VICTUS:
                return withReagent(machine, 18, 1, LIVING, simulate);
            case MECHANICS:
                return machine.useFluid(Materials.Lubricant.getFluid(20 * reagentUnits), simulate) ? 1 : 0;
            case RADIATION:
                return withReagent(machine, 6, 1, METALS, simulate);
            case ELECTRIC:
                return Math.pow(3, tier);
            case SPRITE:
                if (machine.useFluid(FluidRegistry.getFluidStack("witchery:fluidspirit", 2 * reagentUnits), simulate))
                    return 10 * (1 + stability / 100.0);
                if (machine.useFluid(FluidRegistry.getFluidStack("witchery:hollowtears", 2 * reagentUnits), simulate))
                    return 15 * (1 + 100.0 / stability);
                return 1;
            case TAINTED:
                if (machine.useFluid(FluidRegistry.getFluidStack("fluidpure", 3 * reagentUnits), simulate)) return 60;
                boolean death = machine.useFluid(FluidRegistry.getFluidStack("fluiddeath", 3 * reagentUnits), simulate);
                if (!simulate) machine.releaseFlux(death ? 40 : 20);
                return death ? Math.pow(25000.0 / baseEU, 4) : 1;
            default:
                throw new AssertionError(family);
        }
    }

    long burn(OTELargeEssentiaGenerator machine, int stability, int tier) {
        return energy(machine, stability, tier, false);
    }

    long preview(OTELargeEssentiaGenerator machine, int stability, int tier) {
        return energy(machine, stability, tier, true);
    }

    private long energy(OTELargeEssentiaGenerator machine, int stability, int tier, boolean simulate) {
        long heat = (long) (baseEU * multiplier(machine, stability, tier, simulate));
        return (heat / 25) * stability + (heat % 25) * stability / 25;
    }
}
