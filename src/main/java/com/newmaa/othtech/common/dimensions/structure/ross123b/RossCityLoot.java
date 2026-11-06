package com.newmaa.othtech.common.dimensions.structure.ross123b;

import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTOreDictUnificator;

/** Small, deterministic LV-HV salvage caches in ruined city buildings. */
public final class RossCityLoot {

    private RossCityLoot() {}

    public static void fill(TileEntityChest chest, long seed, int x, int z) {
        Random random = new Random(seed ^ ((long) x * 0x9E3779B97F4A7C15L) ^ ((long) z * 0xC2B2AE3D27D4EB4FL));
        int count = 2 + random.nextInt(3);
        int firstSlot = random.nextInt(chest.getSizeInventory());
        for (int i = 0; i < count; i++) {
            ItemStack stack = roll(random);
            if (stack != null) chest.setInventorySlotContents((firstSlot + i * 7) % chest.getSizeInventory(), stack);
        }
    }

    private static ItemStack roll(Random random) {
        switch (random.nextInt(10)) {
            case 0:
                return GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Steel, 2 + random.nextInt(5));
            case 1:
                return GTOreDictUnificator.get(OrePrefixes.plate, Materials.Aluminium, 1 + random.nextInt(4));
            case 2:
                return GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.AnnealedCopper, 2 + random.nextInt(5));
            case 3:
                return GTOreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 4 + random.nextInt(5));
            case 4:
                return GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 2 + random.nextInt(3));
            case 5:
                return GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tin, 3 + random.nextInt(5));
            case 6:
                return ItemList.Electric_Motor_LV.get(1);
            case 7:
                return ItemList.Electric_Pump_LV.get(1);
            case 8:
                return ItemList.Electric_Motor_MV.get(1);
            default:
                return random.nextInt(4) == 0 ? ItemList.Electric_Motor_HV.get(1)
                    : GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 3);
        }
    }
}
