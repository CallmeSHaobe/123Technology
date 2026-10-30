package com.newmaa.othtech.common.crops;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import com.gtnewhorizon.cropsnh.api.ICropCard;
import com.gtnewhorizon.cropsnh.farming.SeedStats;
import com.gtnewhorizon.cropsnh.farming.registries.CropRegistry;
import com.newmaa.othtech.OTHTechnology;

import cpw.mods.fml.common.registry.GameRegistry;

/**
 * 注册入口。
 *
 * <p>
 * 由 {@link com.newmaa.othtech.mixin.MixinCropLoader} 在 CropsNH 自己的
 * {@code CropLoader.postInit()} 末尾调用，这样后面的 MutationLoader / AspectLoader /
 * GTRecipeLoader 都能看到我们注册的作物，与内置作物完全同等待遇。
 */
public final class CropsNHRice {

    public static ICropCard Rice;

    private CropsNHRice() {}

    public static void registerAll() {
        registerCrops();
        registerRecipes();
    }

    private static void registerCrops() {
        Rice = new CropRice();
        CropRegistry.instance.register(Rice);
        OTHTechnology.LOG.info("Registered crop: {}", Rice.getId());
    }

    /**
     * 工作台无序合成：1 个 harvestcraft 稻种（矿辞 seedRice）→ 1 个 CropsNH 水稻种子。
     *
     * <p>
     * 用 {@link SeedStats#DEFAULT_ANALYZED} 生成种子，玩家拿到就是「已分析、1/1/1」的种子，
     * 可以直接种，不需要先用作物分析仪扫描。
     */
    private static void registerRecipes() {
        List<ItemStack> pamSeeds = OreDictionary.getOres("seedRice");
        if (pamSeeds == null || pamSeeds.isEmpty()) {
            OTHTechnology.LOG.warn("No ore dict entry 'seedRice' found, rice seed recipe skipped");
            return;
        }

        ItemStack seed = Rice.getSeedItem(SeedStats.DEFAULT_ANALYZED);
        seed.stackSize = 1;

        // ShapelessOreRecipe 的输入用矿辞字符串，这样 Pam's 换 meta/物品也能匹配
        GameRegistry.addRecipe(new ShapelessOreRecipe(seed, "seedRice"));
        OTHTechnology.LOG.info("Added shapeless recipe: seedRice -> {}", seed.getDisplayName());
    }
}
