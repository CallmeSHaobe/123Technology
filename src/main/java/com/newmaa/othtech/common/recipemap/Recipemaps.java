package com.newmaa.othtech.common.recipemap;

import java.util.Comparator;

import net.minecraft.item.ItemStack;

import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.blocks.deepseiko.DeepSeikoBlocks;
import com.newmaa.othtech.common.blocks.deepseiko.DeepSeikoCasingMeta;
import com.newmaa.othtech.common.recipemap.formatter.MISASpecialValueFormatter;
import com.newmaa.othtech.common.recipemap.recipeMapFrontends.OTH_GeneralFrontend;
import com.newmaa.othtech.common.recipemap.recipeMapFrontends.OTH_GeneralFrontendFireRocket;

import goodgenerator.client.GUI.GGUITextures;
import goodgenerator.loader.Loaders;
import gregtech.api.enums.ItemList;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMapBackend;
import gregtech.api.recipe.RecipeMapBuilder;
import gregtech.api.recipe.maps.AssemblyLineFrontend;
import gregtech.api.recipe.maps.FluidOnlyFrontend;
import gregtech.api.recipe.maps.FuelBackend;
import gregtech.api.recipe.maps.QuantumComputerFrontend;
import gregtech.nei.formatter.HeatingCoilSpecialValueFormatter;
import gregtech.nei.formatter.SimpleSpecialValueFormatter;

public class Recipemaps {

    public static final RecipeMap<OTH_RecipeMapBackend> MegaIsaForge = RecipeMapBuilder
        .of("otht.recipe.MegaIsaForge", OTH_RecipeMapBackend::new)
        .maxIO(16, 16, 16, 16)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(OTH_GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(ItemList.Electric_Motor_MAX.get(1))
                .setMaxRecipesPerPage(1))
        .build();
    public static final RecipeMap<OTH_RecipeMapBackend> QFTMega = RecipeMapBuilder
        .of("otht.recipe.OTEMegaQFT", OTH_RecipeMapBackend::new)
        .maxIO(8, 8, 8, 8)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(OTH_GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(ItemList.Field_Generator_MAX.get(1))
                .setMaxRecipesPerPage(2))
        .build();

    // 没有使用先注释了 --keriils
    // public static final RecipeMap<OTH_RecipeMapBackend> Cannon = RecipeMapBuilder
    // .of("otht.recipe.EVACannon", OTH_RecipeMapBackend::new)
    // .maxIO(1, 1, 0, 0)
    // .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
    // .frontend(OTH_GeneralFrontend::new)
    // .neiHandlerInfo(
    // builder -> builder.setDisplayStack(OTHItemList.dustIrOsSmM.get(1))
    // .setMaxRecipesPerPage(2))
    // .disableOptimize()
    // .build();

    public static final RecipeMap<OTH_RecipeMapBackend> SINOPEC = RecipeMapBuilder
        .of("otht.recipe.SINOPEC", OTH_RecipeMapBackend::new)
        .maxIO(8, 8, 16, 16)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(OTH_GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(OTHItemList.Zhangww.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static final RecipeMap<OTH_RecipeMapBackend> TSSF = RecipeMapBuilder
        .of("otht.recipe.TSSF", OTH_RecipeMapBackend::new)
        .maxIO(24, 24, 4, 4)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .neiSpecialInfoFormatter(HeatingCoilSpecialValueFormatter.INSTANCE)
        .frontend(OTH_GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(OTHItemList.Dasima.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static final RecipeMap<OTH_RecipeMapBackend> SF1 = RecipeMapBuilder
        .of("otht.recipe.MegaPCB", OTH_RecipeMapBackend::new)
        .maxIO(16, 16, 16, 16)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(OTH_GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(OTHItemList.itemEnqingM.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static final RecipeMap<OTH_RecipeMapBackend> SF2 = RecipeMapBuilder
        .of("otht.recipe.MegaPCB_A", OTH_RecipeMapBackend::new)
        .maxIO(16, 16, 16, 16)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(OTH_GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(OTHItemList.itemEnqingM.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static final RecipeMap<OTH_RecipeMapBackend> SF3 = RecipeMapBuilder
        .of("otht.recipe.MegaPCB_B", OTH_RecipeMapBackend::new)
        .maxIO(16, 16, 16, 16)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(OTH_GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(OTHItemList.itemEnqingM.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static final RecipeMap<OTH_RecipeMapBackend> SF4 = RecipeMapBuilder
        .of("otht.recipe.MegaPCB_C", OTH_RecipeMapBackend::new)
        .maxIO(16, 16, 16, 16)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(OTH_GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(OTHItemList.itemEnqingM.get(1))
                .setMaxRecipesPerPage(1))
        .build();
    public static final RecipeMap<OTH_RecipeMapBackend> COC = RecipeMapBuilder
        .of("otht.recipe.COC", OTH_RecipeMapBackend::new)
        .maxIO(1, 1, 0, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(OTH_GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(OTHItemList.CoccOven.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static final RecipeMap<RecipeMapBackend> WFR = RecipeMapBuilder.of("otht.recipe.WoodenFusionReactor")
        .maxIO(0, 0, 1, 1)
        .useCustomFilterForNEI()
        .frontend(FluidOnlyFrontend::new)
        .build();

    public static final RecipeMap<OTH_RecipeMapBackend> MISA = RecipeMapBuilder
        .of("otht.recipe.MISA", OTH_RecipeMapBackend::new)
        .maxIO(16, 16, 4, 4)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .neiSpecialInfoFormatter(MISASpecialValueFormatter.INSTANCE)
        .frontend(OTH_GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(OTHItemList.MISA.get(1))
                .setMaxRecipesPerPage(1))
        .build();
    public static final RecipeMap<RecipeMapBackend> NQF = RecipeMapBuilder.of("otht.recipe.NQF", RecipeMapBackend::new)
        .maxIO(10, 0, 10, 1)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(OTH_GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(Loaders.FRF.copy())
                .setMaxRecipesPerPage(1))
        .build();
    public static final RecipeMap<FuelBackend> NaquadahFuelFakeRecipes = RecipeMapBuilder
        .of("otht.recipe.fuel.nq", FuelBackend::new)
        .maxIO(0, 0, 1, 1)
        .minInputs(0, 1)
        .neiSpecialInfoFormatter(new SimpleSpecialValueFormatter("value.naquadah_reactor"))
        .neiRecipeComparator(Comparator.comparing(recipe -> recipe.mSpecialValue))
        .dontUseProgressBar()
        .addSpecialTexture(59, 20, 58, 42, GGUITextures.PICTURE_NAQUADAH_REACTOR)
        .build();
    public static final RecipeMap<RecipeMapBackend> MegaCAL = RecipeMapBuilder.of("otht.recipe.MCA")
        .maxIO(6, 1, 1, 0)
        .progressBar(GTUITextures.PROGRESSBAR_CIRCUIT_ASSEMBLER)
        .build();
    public static final RecipeMap<OTH_RecipeMapBackend> AE2 = RecipeMapBuilder
        .of("otht.recipe.AE2", OTH_RecipeMapBackend::new)
        .maxIO(1, 1, 0, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(OTH_GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(OTHItemList.TP.get(1))
                .setMaxRecipesPerPage(1))
        .build();
    public static final RecipeMap<OTH_RecipeMapBackend> BIN = RecipeMapBuilder
        .of("otht.recipe.BIN", OTH_RecipeMapBackend::new)
        .maxIO(1, 1, 0, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(OTH_GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(OTHItemList.TP.get(1))
                .setMaxRecipesPerPage(1))
        .build();
    public static final RecipeMap<OTH_RecipeMapBackend> RCY = RecipeMapBuilder
        .of("otht.recipe.RCY", OTH_RecipeMapBackend::new)
        .maxIO(1, 1, 0, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(OTH_GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(OTHItemList.TP.get(1))
                .setMaxRecipesPerPage(1))
        .build();
    public static final RecipeMap<OTH_RecipeMapBackend> EIO = RecipeMapBuilder
        .of("otht.recipe.EIO", OTH_RecipeMapBackend::new)
        .maxIO(8, 8, 4, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(OTH_GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(OTHItemList.EIO.get(1))
                .setMaxRecipesPerPage(1))
        .build();
    public static final RecipeMap<OTH_RecipeMapBackend> EXH = RecipeMapBuilder
        .of("otht.recipe.EXH", OTH_RecipeMapBackend::new)
        .maxIO(4, 4, 4, 4)
        .dontUseProgressBar()
        .frontend(OTH_GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(OTHItemList.EXH.get(1))
                .setMaxRecipesPerPage(4))
        .build();
    public static final RecipeMap<OTH_RecipeMapBackend> STEAM_PUMP = RecipeMapBuilder
        .of("otht.recipe.steam.pump", OTH_RecipeMapBackend::new)
        .maxIO(1, 0, 0, 1)
        .dontUseProgressBar()
        .frontend(OTH_GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(OTHItemList.SteamNeinInOne.get(1))
                .setMaxRecipesPerPage(4))
        .build();
    public static final RecipeMap<OTH_RecipeMapBackend> NASA = RecipeMapBuilder
        .of("otht.recipe.nasa", OTH_RecipeMapBackend::new)
        .maxIO(70, 1, 7, 3)
        .useSpecialSlot()
        .dontUseProgressBar()
        .frontend(OTH_GeneralFrontendFireRocket::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(OTHItemList.NASA.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static final RecipeMap<RecipeMapBackend> OTEquantumComputerFakeRecipes = RecipeMapBuilder
        .of("ote.computer.recipe")
        .maxIO(1, 0, 0, 0)
        .minInputs(1, 0)
        .dontUseProgressBar()
        .frontend(QuantumComputerFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setMaxRecipesPerPage(4)
                .setDisplayStack(OTHItemList.OTEComputer.get(1)))
        .build();

    // 迪普西科·凝聚态配方（普通形态版）：由 DeepSeikoBECRecipeLoader 在 load complete 阶段
    // 从 TecTech 的 BEC 配方池拷贝而来，纠缠凝聚态流体输入按等量换成普通材料流体，
    // 不带任何 BEC 机制（蜂群/红线/凝聚态存储都没有，就是普通配方）。
    public static final RecipeMap<OTH_RecipeMapBackend> DeepSeikoBEC = RecipeMapBuilder
        .of("otht.recipe.DeepSeikoBEC", OTH_RecipeMapBackend::new)
        // 与 TecTech 凝聚态装配页一致：16 物品输入 / 1 输出 / 4 流体
        .maxIO(16, 1, 4, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        // 复用 TecTech 的 BECAssemblyFrontend（原版装配线排版 + 纳米蜂群行），见 DeepSeikoBECFrontend
        .frontend(DeepSeikoBECFrontend::new)
        // NEI 页签图标 = 迪普西科调试硬币
        .neiHandlerInfo(b -> b.setDisplayStack(OTHItemList.DeepSeikoToken.get(1)))
        .build();

    // 迪普西科·装配线配方（模式 A）：由 DeepSeikoALRecipeLoader 拷贝 GT 装配线可视化配方
    // （去 fake、去数据棒）而来，只收装配线（不收电路装配线 CAL），零维护直跑。
    public static final RecipeMap<OTH_RecipeMapBackend> DeepSeikoAL = RecipeMapBuilder
        .of("otht.recipe.DeepSeikoAL", OTH_RecipeMapBackend::new)
        // 装配线版式：16 物品输入 / 1 输出 / 4 流体（和 GT 装配线一致）
        .maxIO(16, 1, 4, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(AssemblyLineFrontend::new)
        // NEI 页签图标 = **银色**调试币（无附魔光效）—— 和金色的凝聚态/机体升级页一眼区分
        .neiHandlerInfo(b -> b.setDisplayStack(OTHItemList.DeepSeikoTokenSilver.get(1)))
        .build();

    // 迪普西科·机体升级（纯 NEI 展示用）：三条升级消耗清单。
    // 机器本身不跑这张配方图，实际扣除发生在 OTEDeepSeiko.tryUpgradeBec / tryUpgradeNanite /
    // tryUpgradeUltimate 里；这里只为了让玩家在 NEI 里能查到要准备什么材料。
    public static final RecipeMap<OTH_RecipeMapBackend> DeepSeikoUpgrade = RecipeMapBuilder
        .of("otht.recipe.DeepSeikoUpgrade", OTH_RecipeMapBackend::new)
        .maxIO(9, 1, 0, 0)
        .dontUseProgressBar()
        .frontend(OTH_GeneralFrontend::new)
        // 页签图标 = 凝聚态适配舱（和上面两张地图的调试币区分开）
        .neiHandlerInfo(
            b -> b.setDisplayStack(new ItemStack(DeepSeikoBlocks.dsCasing, 1, DeepSeikoCasingMeta.CONDENSATE_ADAPTER)))
        .build();
}
