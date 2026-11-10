package com.newmaa.othtech.common.materials;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.Nxer.TwistSpaceTechnology.common.material.MaterialPool;

import bartworks.system.material.Werkstoff;
import bartworks.system.material.WerkstoffLoader;

public final class AbstractSanctumContent {

    /** TST Werkstoff name is {@code Liquid Mana}, BartWorks registers the fluid as lower case. */
    private static final String MANA_FLUID = "liquid mana";
    public static final String BLOOD_FLUID = "lifeessence";

    private static Fluid manaFluid;

    private AbstractSanctumContent() {}

    public static Fluid getManaFluid() {
        if (manaFluid == null) {
            FluidStack stack = getManaStack(1);
            if (stack != null) manaFluid = stack.getFluid();
        }
        return manaFluid;
    }

    public static FluidStack getManaStack(int amount) {
        if (amount <= 0) return null;

        Werkstoff liquidMana = MaterialPool.LiquidMana;
        if (liquidMana != null) {
            FluidStack stack = liquidMana.getFluidOrGas(amount);
            if (stack != null) return stack;
        }

        // Fallback for the case TST ever renames the Werkstoff: look the fluid up by name.
        Fluid fluid = FluidRegistry.getFluid(MANA_FLUID);
        return fluid == null ? null : new FluidStack(fluid, amount);
    }

    public static FluidStack getBloodStack(int amount) {
        Fluid fluid = FluidRegistry.getFluid(BLOOD_FLUID);
        return fluid == null || amount <= 0 ? null : new FluidStack(fluid, amount);
    }

    public static FluidStack getOganessonStack(int amount) {
        return amount <= 0 ? null : WerkstoffLoader.Oganesson.getFluidOrGas(amount);
    }
}
