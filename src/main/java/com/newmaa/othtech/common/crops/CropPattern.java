package com.newmaa.othtech.common.crops;

import java.awt.Color;

import com.gtnewhorizon.cropsnh.api.ISeedShape;
import com.gtnewhorizon.cropsnh.api.SeedShape;
import com.gtnewhorizon.cropsnh.crops.abstracts.CropFood;

/** 新作物模板：贴图 0~3（123technology:crops/&lt;id&gt;/）、禾草种子、固定作者名。 */
public abstract class CropPattern extends CropFood {

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
