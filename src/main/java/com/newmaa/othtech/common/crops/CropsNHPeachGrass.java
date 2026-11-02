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

/**
 * 黄桃草的注册入口（作物 / 杂交 / 合成）。
 *
 * <p>
 * 与 {@link CropsNHRice} 一样由 {@link com.newmaa.othtech.mixin.MixinCropLoader} 在 CropsNH 的
 * {@code CropLoader.postInit()} 末尾调用，因此后面跑 MutationLoader（会 prune 杂交池）时我们的作物
 * 已经在注册表里了。
 */
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

    /**
     * 杂交：
     * <ol>
     * <li>注册进 YELLOW / WHEAT / EDIBLE / LEAFY 四个杂交池 —— 池内作物两两相邻随机杂交时有可能出黄桃草；</li>
     * <li>再挂一条定向杂交：小麦 × 柠檬 → 黄桃草（不需要靠运气）。</li>
     * </ol>
     * 定向杂交的父本必须是 CropsNH 自带作物，所以这里引用 {@link CropsNHCrops} 的静态字段。
     */
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

    /**
     * 工作台无序合成：1 个普通小麦种子 + 1 个恩情 → 1 个黄桃草种子。
     *
     * <p>
     * 用 {@link SeedStats#DEFAULT_ANALYZED} 生成种子，玩家拿到就是「已分析、1/1/1」的种子，可以直接种，
     * 不需要先用作物分析仪扫。
     */
    private static void registerRecipes() {
        ItemStack seed = YellowPeachGrass.getSeedItem(SeedStats.DEFAULT_ANALYZED);
        seed.stackSize = 1;

        GameRegistry
            .addRecipe(new ShapelessOreRecipe(seed, new ItemStack(Items.wheat_seeds), OTHItemList.itemEnqingM.get(1)));
        OTHTechnology.LOG.info("Added shapeless recipe: wheat seeds + enqing -> {}", seed.getDisplayName());
    }
}
