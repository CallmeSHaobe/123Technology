package com.newmaa.othtech.common.crops;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import com.gtnewhorizon.cropsnh.api.ICropCard;
import com.gtnewhorizon.cropsnh.farming.SeedStats;
import com.gtnewhorizon.cropsnh.farming.registries.CropRegistry;
import com.newmaa.othtech.OTHTechnology;

import cpw.mods.fml.common.registry.GameRegistry;

public final class CropsNHRice {

    public static ICropCard Rice;

    private CropsNHRice() {}

    public static void registerAll() {
        registerCrop();
        registerRecipes();
    }

    private static void registerCrop() {
        Rice = new CropRice();
        CropRegistry.instance.register(Rice);
        OTHTechnology.LOG.info("Registered crop: {}", Rice.getId());
    }

    private static void registerRecipes() {
        if (OreDictionary.getOres("seedRice")
            .isEmpty()) {
            OTHTechnology.LOG.warn("No ore dict entry 'seedRice' found, rice seed recipe skipped");
            return;
        }

        ItemStack seed = Rice.getSeedItem(SeedStats.DEFAULT_ANALYZED);
        seed.stackSize = 1;
        GameRegistry.addRecipe(new ShapelessOreRecipe(seed, "seedRice"));
        OTHTechnology.LOG.info("Added shapeless recipe: seedRice -> {}", seed.getDisplayName());
    }
}
