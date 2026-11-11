package com.newmaa.othtech.common.crops.abstracts;

import java.awt.Color;

import com.gtnewhorizon.cropsnh.api.ISeedShape;
import com.gtnewhorizon.cropsnh.api.SeedShape;
import com.gtnewhorizon.cropsnh.crops.abstracts.CropFood;

/**
 * 本 mod 附加作物卡的公共基类。
 *
 * <p>
 * 对齐 CropsNH 官方 {@code com.gtnewhorizon.cropsnh.crops.abstracts} 的分层：可复用的作物基类放
 * {@code abstracts} 包，具体作物放上级包。这里统一了附加作物的贴图路径（{@code 123technology:crops/<id>/}，
 * 贴图 0~3 依次对应 {@code getMinGrowthStage()}~{@code getMaxGrowthStage()}）、禾草类种子外观与作者名。
 */
public abstract class CropPattern extends CropFood {

    /**
     * @param id     CropsNH 内部作物 id，同时决定 {@code cropsnh_crops.<id>} 的语言键与贴图目录
     * @param color1 主色，用于 NEI/种子染色
     * @param color2 次色
     */
    protected CropPattern(String id, Color color1, Color color2) {
        super(id, color1, color2);
    }

    @Override
    public String getCreator() {
        return "CropsNH Addon";
    }

    @Override
    public ISeedShape getSeedShape() {
        return SeedShape.grains;
    }

    @Override
    public int getMinGrowthStage() {
        return 0;
    }

    @Override
    public int getMaxGrowthStage() {
        return 3;
    }

    @Override
    protected String getBaseTexturePath() {
        return "123technology:crops/" + this.internalId + "/";
    }
}
