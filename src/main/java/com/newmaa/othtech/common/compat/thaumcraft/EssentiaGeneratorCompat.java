package com.newmaa.othtech.common.compat.thaumcraft;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;
import com.newmaa.othtech.machine.hatch.OTEEssentiaInputHatch;
import com.newmaa.othtech.machine.machineclass.EssentiaGeneratorFuel;

import cpw.mods.fml.common.registry.GameRegistry;
import goodgenerator.loader.Loaders;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TCAspects;
import gregtech.api.util.GTOreDictUnificator;
import ic2.core.Ic2Items;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.common.config.ConfigBlocks;

public final class EssentiaGeneratorCompat {

    private static final String RESEARCH = "OTH_ESSENTIA_GENERATOR";
    public static final OTEEssentiaInputHatch.IntakeBlock INTAKE = new OTEEssentiaInputHatch.IntakeBlock();
    private static final String[] ICONS = { "null", "air", "thermal", "unstable", "victus", "tainted", "mechanics",
        "spirit", "radiation", "electric" };

    private static final Item[] UPGRADES = new Item[ICONS.length];

    private EssentiaGeneratorCompat() {}

    public static void registerParts() {
        INTAKE.setCreativeTab(CreativeTabsLoader.tabothtech);
        GameRegistry.registerBlock(INTAKE, "essentia_intake");
        GameRegistry.registerTileEntity(OTEEssentiaInputHatch.class, "othtech.essentia_intake");
        for (int i = 0; i < ICONS.length; i++) {
            String id = "essentia_upgrade_" + ICONS[i];
            UPGRADES[i] = new Item().setUnlocalizedName("oth." + id)
                .setTextureName("goodgenerator:upgradeEssentia/" + ICONS[i])
                .setCreativeTab(CreativeTabsLoader.tabothtech);
            GameRegistry.registerItem(UPGRADES[i], id);
        }
    }

    public static int upgradeBit(ItemStack stack) {
        if (stack != null && stack.stackSize > 0) {
            for (int i = 1; i < UPGRADES.length; i++) if (UPGRADES[i] == stack.getItem()) return 1 << i;
        }
        return 0;
    }

    private static ItemStack part(OrePrefixes prefix, Materials material) {
        return GTOreDictUnificator.get(prefix, material, 1L);
    }

    public static void registerResearch() {
        EssentiaGeneratorFuel.load();
        Aspect electricity = TCAspects.ELECTRUM.getAspect();
        ItemStack controller = OTHItemList.LargeEssentiaGenerator.get(1);
        AspectList cost = new AspectList().add(Aspect.ENERGY, 64)
            .add(electricity, 64)
            .add(Aspect.MAGIC, 32)
            .add(Aspect.EXCHANGE, 32);
        InfusionRecipe machine = ThaumcraftApi.addInfusionCraftingRecipe(
            RESEARCH,
            controller,
            6,
            cost,
            ItemList.Hull_HV.get(1),
            new ItemStack[] { part(OrePrefixes.circuit, Materials.HV), new ItemStack(ConfigBlocks.blockJar),
                part(OrePrefixes.gearGt, Materials.Thaumium), part(OrePrefixes.gem, Materials.Emerald),
                new ItemStack(ConfigBlocks.blockWoodenDevice), part(OrePrefixes.spring, Materials.Manyullyn),
                Ic2Items.teslaCoil.copy(), ItemList.Sensor_MV.get(1) });

        List<ResearchPage> pages = new ArrayList<>();
        pages.add(new ResearchPage("oth.essentia.research"));
        pages.add(new ResearchPage("oth.essentia.reagents"));
        pages.add(new ResearchPage("oth.essentia.reagents_extra"));
        pages.add(new ResearchPage(machine));
        pages.add(
            new ResearchPage(
                ThaumcraftApi.addInfusionCraftingRecipe(
                    RESEARCH,
                    new ItemStack(INTAKE),
                    2,
                    new AspectList().add(Aspect.VOID, 16)
                        .add(Aspect.WATER, 16),
                    new ItemStack(Loaders.magicCasing),
                    new ItemStack[] { new ItemStack(ConfigBlocks.blockJar), ItemList.Electric_Pump_HV.get(1) })));
        pages.add(
            new ResearchPage(
                ThaumcraftApi.addCrucibleRecipe(
                    RESEARCH,
                    new ItemStack(UPGRADES[0]),
                    part(OrePrefixes.plate, Materials.Thaumium),
                    new AspectList().add(Aspect.ORDER, 16)
                        .add(Aspect.MAGIC, 16))));
        Aspect[] catalysts = { null, Aspect.AIR, Aspect.FIRE, Aspect.ENTROPY, Aspect.LIFE, Aspect.TAINT,
            Aspect.MECHANISM, Aspect.SOUL, TCAspects.RADIO.getAspect(), electricity };
        for (int i = 1; i < UPGRADES.length; i++) {
            pages.add(
                new ResearchPage(
                    ThaumcraftApi.addCrucibleRecipe(
                        RESEARCH,
                        new ItemStack(UPGRADES[i]),
                        new ItemStack(UPGRADES[0]),
                        new AspectList().add(catalysts[i], 32))));
        }
        new ResearchItem(
            RESEARCH,
            "123technology",
            new AspectList().add(Aspect.ENERGY, 8)
                .add(Aspect.MAGIC, 8),
            -4,
            0,
            2,
            controller).setParents("INFUSION")
                .setPages(pages.toArray(new ResearchPage[0]))
                .registerResearchItem();
    }
}
