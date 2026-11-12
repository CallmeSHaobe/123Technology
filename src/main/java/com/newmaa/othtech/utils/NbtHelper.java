package com.newmaa.othtech.utils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.gtnewhorizon.cropsnh.farming.SeedStats;
import com.newmaa.othtech.OTHTechnology;
import com.newmaa.othtech.common.beeyonds.OTHBeeDefinition;

import cpw.mods.fml.common.registry.GameRegistry;
import forestry.core.genetics.alleles.EnumAllele;

public final class NbtHelper {

    private NbtHelper() {}

    public static ItemStack cropsnhSeed(String cropId, int growth, int gain, int resistance, boolean analyzed) {
        Item item = GameRegistry.findItem("cropsnh", "genericSeed");
        if (item == null) return null;
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("crop", cropId);
        new SeedStats((byte) growth, (byte) gain, (byte) resistance, analyzed).writeToNBT(tag);
        ItemStack seed = new ItemStack(item, 1, 0);
        seed.setTagCompound(tag);
        return seed;
    }

    public static ItemStack cropsnhSeed(String cropId) {
        return cropsnhSeed(cropId, 1, 1, 1, true);
    }

    public static ItemStack grassothQueen() {
        return beeQueen(
            OTHBeeDefinition.GRASSOTH.getSpecies()
                .getUID(),
            EnumAllele.Lifespan.SHORT.getValue());
    }

    public static ItemStack beeQueen(String speciesUid, int health) {
        Item item = GameRegistry.findItem("Forestry", "beeQueenGE");
        if (item == null) {
            OTHTechnology.LOG.warn("Cannot build queen: forestry:beeQueenGE missing");
            return null;
        }

        NBTTagCompound species = new NBTTagCompound();
        species.setByte("Slot", (byte) 0);
        species.setString("UID0", speciesUid);
        species.setString("UID1", speciesUid);

        NBTTagList chromosomes = new NBTTagList();
        chromosomes.appendTag(species);

        NBTTagCompound genome = new NBTTagCompound();
        genome.setTag("Chromosomes", chromosomes);

        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("IsAnalyzed", true);
        tag.setInteger("Health", health);
        tag.setInteger("MaxH", health);
        tag.setTag("Genome", genome);
        tag.setTag("Mate", genome.copy());

        ItemStack queen = new ItemStack(item, 1, 0);
        queen.setTagCompound(tag);
        return queen;
    }
}
