package com.newmaa.othtech;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class Config {

    public static String greeting = "Hello World";

    public static void synchronizeConfiguration(File configFile) {

        Configuration configuration = new Configuration(configFile);

        greeting = configuration.getString("greeting", Configuration.CATEGORY_GENERAL, greeting, "How shall I greet?");
        BOOM_SWITCH = configuration.getBoolean("OTHTechnology : 控制TT模版机器爆炸", "憋憋", BOOM_SWITCH, "憋憋");
        NEIFrontend = configuration
            .getBoolean("OTHTechnology : 123机器配方池界面使用铱锇钐合金粉替换GT齿轮, true为是, false为替换成憋憋", "A", NEIFrontend, "A");
        ENQING_MULTI = configuration
            .getFloat("OTHTechnology : 恩情工厂配方产物倍率(float) , 倍率四舍五入", "不憋憋", 1.5f, 1.0f, 114514f, "不憋憋");
        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    public static int MAX_PARALLEL_LIMIT = Integer.MAX_VALUE;
    public static boolean DEFAULT_BATCH_MODE = false;
    public static boolean NEIFrontend = true;
    public static int Parallel_PerPiece_ISA_Forge = 64;
    public static float SpeedBonus_MultiplyPerVoltageTier_ISA_Forge = 0.5F;
    public static float SpeedMultiplier_ISA_Forge = 1F;
    public static float Piece_EnablePO_ISA = 1;
    public static boolean BOOM_SWITCH = true;
    public static float ENQING_MULTI = 1.5f;
}
