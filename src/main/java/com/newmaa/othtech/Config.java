package com.newmaa.othtech;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class Config {

    public static String greeting = "Hello World";

    public static void synchronizeConfiguration(File configFile) {

        Configuration configuration = new Configuration(configFile);
        // TODO NEED ENGLISH TRANSLATION O.O

        // greeting = configuration.getString("greeting", Configuration.CATEGORY_GENERAL, greeting, "How shall I
        // greet?");
        is_TT_Boom = configuration.getBoolean("OTHTechnology : 控制TT模版机器爆炸", "憋憋", is_TT_Boom, "憋憋");
        NEIFrontend = configuration
            .getBoolean("OTHTechnology : 123机器配方池界面使用铱锇钐合金粉替换GT齿轮, true为是, false为替换成憋憋", "A", NEIFrontend, "A");
        is_MISA_IMBA_Recipes_Enabled = configuration.getBoolean(
            "OTHTechnology : 艾萨集成工厂超模配方是否开启(e.g. 无尽催化剂矿出无尽粉 黑钚矿出黑中子粉), true为是, false为否",
            "A",
            is_MISA_IMBA_Recipes_Enabled,
            "A");
        is_EggMachine_Recipes_For_NHU = configuration.getBoolean(
            "OTHTechnology : 丰矿的NHU配方兼容从123大龙研机(e.g. 一步到位顶级蛋机 龙蛋馄饨龙蛋远古龙蛋配方), true为是, false为否",
            "A",
            is_EggMachine_Recipes_For_NHU,
            "A");
        ENQING_MULTI = configuration
            .getFloat("OTHTechnology : 恩情工厂配方产物倍率(float) , 倍率四舍五入", "不憋憋", 1.5f, 1.0f, 114514f, "不憋憋");
        tier_Antimonia = configuration.getInt("OTHTechnology : 锑星登陆需求等级(Tier)", "不憋憋", 4, 1, 10, "笑笑");
        tier_Ross123b = configuration.getInt("OTHTechnology : 锑罗斯登陆需求等级(Tier)", "不憋憋", 1, 1, 10, "笑笑");
        is_Enqing_Song_Play = configuration
            .getBoolean("OTHTechnology : 开启将军工厂的开机音效, true为是, false为否", "A", is_Enqing_Song_Play, "A");
        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    public static boolean DEFAULT_BATCH_MODE = false;
    public static boolean NEIFrontend = true;
    public static boolean is_TT_Boom = true;
    public static float ENQING_MULTI = 1.5f;
    public static boolean is_MISA_IMBA_Recipes_Enabled = true;
    public static boolean is_EggMachine_Recipes_For_NHU = true;
    public static int tier_Antimonia = 4;
    public static int tier_Ross123b = 1;
    public static boolean is_Enqing_Song_Play = true;
}
