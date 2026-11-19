package com.newmaa.othtech.common.item;

import java.util.function.Supplier;

import net.minecraft.item.ItemStack;

import com.newmaa.othtech.common.OTHItemList;

import gregtech.common.tileentities.machines.multi.nanochip.util.CircuitComponent;

public enum OTHCircuitComponent {

    AF;

    public static final String AF_NAC_NAME = "OTHMiniActiveTransformer";
    public static final int AF_NAC_META = 23547;

    public ItemStack getFakeStack(int amount) {
        return CircuitComponent.valueOf(AF_NAC_NAME)
            .getFakeStack(amount);
    }

    public static Supplier<ItemStack> afRealStackSupplier() {
        return () -> OTHItemList.AF.get(1);
    }
}
