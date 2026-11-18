package com.newmaa.othtech.common;

import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTOreDictUnificator;

public class OreDictionaryLoader {

    public OreDictionaryLoader(FMLPreInitializationEvent event) {
        // Keep the 690 as its own item while allowing it to substitute for UHV circuits.
        ItemStack tacticalGraphicsCard = OTHItemList.NukeThrowable.get(1);
        GTOreDictUnificator.addToBlacklist(tacticalGraphicsCard);
        // GT names the UHV circuit material "Infinite", yielding circuitInfinite.
        GTOreDictUnificator.registerOre(OrePrefixes.circuit, Materials.UHV, tacticalGraphicsCard);
        GTOreDictUnificator.addAssociation(OrePrefixes.circuit, Materials.UHV, tacticalGraphicsCard);
    }
}
