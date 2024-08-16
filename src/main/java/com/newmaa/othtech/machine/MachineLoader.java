package com.newmaa.othtech.machine;

import net.minecraft.item.ItemStack;

public class MachineLoader {

    public static ItemStack MegaIsaForge;

    public static void loadMachines() {
        MegaIsaForge = new Mega_ISA_Forge(20000, "MegaISAForge", "神之艾萨锻炉").getStackForm(1);

    }
}
