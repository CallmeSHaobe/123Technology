package com.newmaa.othtech.common.crops;

import static com.gtnewhorizon.cropsnh.api.CropsNHMutationPools.EDIBLE;
import static com.gtnewhorizon.cropsnh.api.CropsNHMutationPools.LEAFY;
import static com.gtnewhorizon.cropsnh.api.CropsNHMutationPools.WHEAT;
import static com.gtnewhorizon.cropsnh.api.CropsNHMutationPools.YELLOW;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import com.gtnewhorizon.cropsnh.api.CropsNHCrops;
import com.gtnewhorizon.cropsnh.api.ICropCard;
import com.gtnewhorizon.cropsnh.farming.SeedStats;
import com.gtnewhorizon.cropsnh.farming.mutation.CropMutation;
import com.gtnewhorizon.cropsnh.farming.registries.CropRegistry;
import com.gtnewhorizon.cropsnh.farming.registries.MutationRegistry;
import com.newmaa.othtech.OTHTechnology;
import com.newmaa.othtech.common.OTHItemList;

import cpw.mods.fml.common.registry.GameRegistry;

public final class CropsNHLoader {

    private static ICropCard rice;
    private static ICropCard peach;
    private static ICropCard beeGrass;

    private CropsNHLoader() {}

    public static void registerRice() {
        rice = new CropRice();
        CropRegistry.instance.register(rice);
        OTHTechnology.LOG.info("Registered crop: {}", rice.getId());

        if (OreDictionary.getOres("seedRice")
            .isEmpty()) {
            OTHTechnology.LOG.warn("No ore dict entry 'seedRice' found, rice seed recipe skipped");
            return;
        }
        GameRegistry.addRecipe(new ShapelessOreRecipe(rice.getSeedItem(SeedStats.DEFAULT_ANALYZED), "seedRice"));
    }

    public static void registerPeachGrass() {
        peach = new CropYellowPeachGrass();
        CropRegistry.instance.register(peach);
        OTHTechnology.LOG.info("Registered crop: {}", peach.getId());
        MutationRegistry.instance.register(peach, YELLOW, WHEAT, EDIBLE, LEAFY);
        try {
            new CropMutation(peach, CropsNHCrops.Wheat, CropsNHCrops.Lemon).register();
        } catch (Throwable t) {
            OTHTechnology.LOG.warn("Could not register deterministic mutation for yellow peach grass", t);
        }
        GameRegistry.addRecipe(
            new ShapelessOreRecipe(
                peach.getSeedItem(SeedStats.DEFAULT_ANALYZED),
                new ItemStack(Items.wheat_seeds),
                OTHItemList.itemEnqingM.get(1)));
    }

    public static void registerBeeGrass() {
        beeGrass = new CropBeeGrass();
        CropRegistry.instance.register(beeGrass);
        OTHTechnology.LOG.info("Registered crop: {}", beeGrass.getId());
        MutationRegistry.instance.register(beeGrass, YELLOW, WHEAT, EDIBLE, LEAFY);
        try {
            new CropMutation(beeGrass, CropsNHCrops.Wheat, rice).register();
        } catch (Throwable t) {
            OTHTechnology.LOG.warn("Could not register deterministic mutation for bee grass", t);
        }
    }
}
