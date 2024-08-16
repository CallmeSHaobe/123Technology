package com.newmaa.othtech.common;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class OreDictionaryLoader {

    public OreDictionaryLoader(FMLPreInitializationEvent event) {
        List<ItemStack> dustIrOsSm = OreDictionary.getOres("dustIrOsSm");
        List<ItemStack> LookNEIdust = OreDictionary.getOres("LookNEIdust");
        List<ItemStack> itemPikoCircuit = OreDictionary.getOres("circuitPiko");
        List<ItemStack> itemQuantumCircuit = OreDictionary.getOres("circuitQuantum");
        List<ItemStack> ingotHotDog = OreDictionary.getOres("ingotHotDog");
        List<ItemStack> ingotDog = OreDictionary.getOres("ingotDog");

        for (ItemStack itemStack : dustIrOsSm) {
            OreDictionary.registerOre("dustIrOsSm", itemStack);
        }
        for (ItemStack itemStack : LookNEIdust) {
            OreDictionary.registerOre("dustLookNEI", itemStack);
        }
        for (ItemStack itemStack : itemPikoCircuit) {
            OreDictionary.registerOre("circuitExotic", itemStack);
        }
        for (ItemStack itemStack : itemQuantumCircuit) {
            OreDictionary.registerOre("circuitCosmic", itemStack);
        }
        for (ItemStack itemStack : ingotHotDog) {
            OreDictionary.registerOre("ingotHotDog", itemStack);
        }
        for (ItemStack itemStack : ingotDog) {
            OreDictionary.registerOre("ingotDog", itemStack);
        }
    }

}
