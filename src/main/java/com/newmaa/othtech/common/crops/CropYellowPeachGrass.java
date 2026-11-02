package com.newmaa.othtech.common.crops;

import java.awt.Color;

import net.minecraftforge.common.BiomeDictionary;

import com.gtnewhorizon.cropsnh.api.ISeedShape;
import com.gtnewhorizon.cropsnh.api.SeedShape;
import com.gtnewhorizon.cropsnh.crops.abstracts.CropFood;
import com.newmaa.othtech.common.OTHItemList;

/**
 * 黄桃草（Yellow Peach Grass）。
 *
 * <ul>
 * <li>土壤：农田（{@link CropFood} 默认 {@code CropsNHSoilTypes.farmland}）</li>
 * <li>档次：tier 2</li>
 * <li>产出：本模组的黄桃（{@code OTHItemList.YellowPeach}，ItemFood：回 12 饥饿 + 30 秒速度 V）100%</li>
 * <li>杂交：已注册进 YELLOW / WHEAT / EDIBLE / LEAFY 杂交池，并有一条定向杂交「小麦 × 柠檬」（见
 * {@link CropsNHPeachGrass}）</li>
 * <li>种子来源：工作台无序合成「普通小麦种子 + 恩情」（见 {@link CropsNHPeachGrass#registerRecipes()}）</li>
 * </ul>
 */
public class CropYellowPeachGrass extends CropFood {

    /** 贴图目录：assets/123technology/textures/blocks/crops/yellowPeachGrass/{0..3}.png */
    private static final String TEXTURE_PATH = "123technology:crops/yellowPeachGrass/";

    public CropYellowPeachGrass() {
        // 主色偏黄桃橙黄，副色偏浅奶油黄（决定种子图标的两层染色）
        super("yellowPeachGrass", new Color(0xF2C14E), new Color(0xFFE9A8));

        // 产物：本模组的黄桃物品（ItemFood）
        this.addDrop(OTHItemList.YellowPeach.get(1), 10000); // 10000 = 100%

        // 黄桃喜温，林地/丛林里长势最好
        this.addLikedBiomes(BiomeDictionary.Type.HOT, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.JUNGLE);
    }

    @Override
    public String getCreator() {
        return "CropsNH Addon";
    }

    /** 语言文件里的风味文本（会被 StatCollector 翻译） */
    @Override
    public String getFlavourText() {
        return "cropsnh_crops.yellowPeachGrass.flavour";
    }

    @Override
    public ISeedShape getSeedShape() {
        // 禾草类种子外形，跟小麦一致
        return SeedShape.grains;
    }

    @Override
    public int getTier() {
        return 2;
    }

    @Override
    public int getGrowthDuration() {
        return 1600;
    }

    /** 0~3 共四个可见生长阶段 */
    @Override
    public int getMinGrowthStage() {
        return 0;
    }

    @Override
    public int getMaxGrowthStage() {
        return 3;
    }

    /** 贴图用本模组自己的命名空间，而不是 CropsNH 的 crops/&lt;id&gt;/ 目录。 */
    @Override
    protected String getBaseTexturePath() {
        return TEXTURE_PATH;
    }
}
