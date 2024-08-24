package com.newmaa.othtech;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class Config {

    public static String greeting = "Hello World";

    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        greeting = configuration.getString("greeting", Configuration.CATEGORY_GENERAL, greeting, "How shall I greet?");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    public static int MAX_PARALLEL_LIMIT = Integer.MAX_VALUE;
    public static boolean DEFAULT_BATCH_MODE = false;

    public static int Parallel_PerPiece_ISA_Forge = 64;
    public static float SpeedBonus_MultiplyPerVoltageTier_ISA_Forge = 0.5F;
    public static float SpeedMultiplier_ISA_Forge = 1F;
    public static float Piece_EnablePO_ISA = 1;
}
