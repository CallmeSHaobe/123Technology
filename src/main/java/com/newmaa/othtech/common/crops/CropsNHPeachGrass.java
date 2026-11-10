package com.newmaa.othtech.common.crops;

import static com.gtnewhorizon.cropsnh.api.CropsNHMutationPools.EDIBLE;
import static com.gtnewhorizon.cropsnh.api.CropsNHMutationPools.LEAFY;
import static com.gtnewhorizon.cropsnh.api.CropsNHMutationPools.WHEAT;
import static com.gtnewhorizon.cropsnh.api.CropsNHMutationPools.YELLOW;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
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

public final class CropsNHPeachGrass {

    public static ICropCard YellowPeachGrass;

    private CropsNHPeachGrass() {}

    public static void registerAll() {
        registerCrop();
        registerMutations();
        registerRecipes();
    }

    private static void registerCrop() {
        YellowPeachGrass = new CropYellowPeachGrass();
        CropRegistry.instance.register(YellowPeachGrass);
        OTHTechnology.LOG.info("Registered crop: {}", YellowPeachGrass.getId());
    }

    private static void registerMutations() {
        MutationRegistry.instance.register(YellowPeachGrass, YELLOW, WHEAT, EDIBLE, LEAFY);

        try {
            new CropMutation(YellowPeachGrass, CropsNHCrops.Wheat, CropsNHCrops.Lemon).register();
            OTHTechnology.LOG.info("Added crop mutation: wheat + lemon -> {}", YellowPeachGrass.getId());
        } catch (Throwable t) {
            // 父本作物可能因配置/版本差异不可用，退化到只靠杂交池
            OTHTechnology.LOG.warn("Could not register deterministic mutation for yellow peach grass", t);
        }
    }

    private static void registerRecipes() {
        ItemStack seed = YellowPeachGrass.getSeedItem(SeedStats.DEFAULT_ANALYZED);
        seed.stackSize = 1;

        GameRegistry
            .addRecipe(new ShapelessOreRecipe(seed, new ItemStack(Items.wheat_seeds), OTHItemList.itemEnqingM.get(1)));
        OTHTechnology.LOG.info("Added shapeless recipe: wheat seeds + enqing -> {}", seed.getDisplayName());
    }
}
