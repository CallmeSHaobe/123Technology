package com.newmaa.othtech.common.recipemap;

import java.util.Comparator;

import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.recipemap.formatter.MISASpecialValueFormatter;
import com.newmaa.othtech.common.recipemap.recipeMapFrontends.OTH_GeneralFrontend;

import goodgenerator.client.GUI.GGUITextures;
import goodgenerator.loader.Loaders;
import gregtech.api.enums.ItemList;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMapBackend;
import gregtech.api.recipe.RecipeMapBuilder;
import gregtech.api.recipe.maps.FluidOnlyFrontend;
import gregtech.api.recipe.maps.FuelBackend;
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
        .disableOptimize()
        .build();
    public static final RecipeMap<OTH_RecipeMapBackend> QFTMega = RecipeMapBuilder
        .of("otht.recipe.OTEMegaQFT", OTH_RecipeMapBackend::new)
        .maxIO(8, 8, 8, 8)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(OTH_GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(ItemList.Field_Generator_MAX.get(1))
                .setMaxRecipesPerPage(2))
        .disableOptimize()
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
        .disableOptimize()
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
        .disableOptimize()
        .build();

    public static final RecipeMap<OTH_RecipeMapBackend> SF1 = RecipeMapBuilder
        .of("otht.recipe.MegaPCB", OTH_RecipeMapBackend::new)
        .maxIO(16, 16, 16, 16)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(OTH_GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(OTHItemList.itemEnqingM.get(1))
                .setMaxRecipesPerPage(1))
        .disableOptimize()
        .build();

    public static final RecipeMap<OTH_RecipeMapBackend> SF2 = RecipeMapBuilder
        .of("otht.recipe.MegaPCB_A", OTH_RecipeMapBackend::new)
        .maxIO(16, 16, 16, 16)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(OTH_GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(OTHItemList.itemEnqingM.get(1))
                .setMaxRecipesPerPage(1))
        .disableOptimize()
        .build();

    public static final RecipeMap<OTH_RecipeMapBackend> SF3 = RecipeMapBuilder
        .of("otht.recipe.MegaPCB_B", OTH_RecipeMapBackend::new)
        .maxIO(16, 16, 16, 16)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(OTH_GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(OTHItemList.itemEnqingM.get(1))
                .setMaxRecipesPerPage(1))
        .disableOptimize()
        .build();

    public static final RecipeMap<OTH_RecipeMapBackend> SF4 = RecipeMapBuilder
        .of("otht.recipe.MegaPCB_C", OTH_RecipeMapBackend::new)
        .maxIO(16, 16, 16, 16)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(OTH_GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(OTHItemList.itemEnqingM.get(1))
                .setMaxRecipesPerPage(1))
        .disableOptimize()
        .build();
    public static final RecipeMap<OTH_RecipeMapBackend> COC = RecipeMapBuilder
        .of("otht.recipe.COC", OTH_RecipeMapBackend::new)
        .maxIO(1, 1, 0, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(OTH_GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(OTHItemList.CoccOven.get(1))
                .setMaxRecipesPerPage(1))
        .disableOptimize()
        .build();

    public static final RecipeMap<RecipeMapBackend> WFR = RecipeMapBuilder.of("otht.recipe.WoodenFusionReactor")
        .maxIO(0, 0, 1, 1)
        .disableOptimize()
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
        .disableOptimize()
        .build();
    public static final RecipeMap<RecipeMapBackend> NQF = RecipeMapBuilder.of("otht.recipe.NQF", RecipeMapBackend::new)
        .maxIO(10, 0, 10, 1)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(OTH_GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(Loaders.FRF.copy())
                .setMaxRecipesPerPage(1))
        .neiSpecialInfoFormatter(new SimpleSpecialValueFormatter("value.naquadah_fuel_refine_factory"))
        .disableOptimize()
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
}
