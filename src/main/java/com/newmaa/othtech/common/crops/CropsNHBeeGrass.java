package com.newmaa.othtech.common.crops;

import static com.gtnewhorizon.cropsnh.api.CropsNHMutationPools.*;
import static com.gtnewhorizon.cropsnh.api.CropsNHMutationPools.LEAFY;

import com.gtnewhorizon.cropsnh.api.CropsNHCrops;
import com.gtnewhorizon.cropsnh.api.ICropCard;
import com.gtnewhorizon.cropsnh.farming.mutation.CropMutation;
import com.gtnewhorizon.cropsnh.farming.registries.CropRegistry;
import com.gtnewhorizon.cropsnh.farming.registries.MutationRegistry;
import com.newmaa.othtech.OTHTechnology;

public final class CropsNHBeeGrass {

    public static ICropCard CropNHBeeGrass;

    private CropsNHBeeGrass() {}

    public static void registerAll() {
        registerCrop();
        registerMutations();
    }

    private static void registerCrop() {
        CropNHBeeGrass = new CropBeeGrass();
        CropRegistry.instance.register(CropNHBeeGrass);
        OTHTechnology.LOG.info("Registered crop: {}", CropNHBeeGrass.getId());
    }

    private static void registerMutations() {
        MutationRegistry.instance.register(CropNHBeeGrass, YELLOW, WHEAT, EDIBLE, LEAFY);

        try {
            new CropMutation(CropNHBeeGrass, CropsNHCrops.Wheat, CropsNHRice.Rice).register();
            OTHTechnology.LOG.info("Added crop mutation: wheat + lemon -> {}", CropNHBeeGrass.getId());
        } catch (Throwable t) {
            // 父本作物可能因配置/版本差异不可用，退化到只靠杂交池
            OTHTechnology.LOG.warn("Could not register deterministic mutation for bee grass", t);
        }
    }

}
