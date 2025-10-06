package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.common.recipemap.Recipemaps.NASA;
import static gregtech.api.enums.Mods.*;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.newmaa.othtech.common.OTHItemList;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeBuilder;

public class RecipesNASA implements IRecipePool {

    @Override
    public void loadRecipes() {
        GTRecipeBuilder.builder()
            .itemInputs(OTHItemList.SunLighter.get(0))
            .itemOutputs(new ItemStack(Blocks.fire, 123))
            .duration(100)
            .eut(123)
            .addTo(NASA);
        recipes_t1();
    }

    private static void recipes_t1()

    {
        final ItemStack f = new ItemStack(Blocks.fire, 1);
        final ItemStack n = GTModHandler.getModItem(GalacticraftCore.ID, "item.noseCone", 1);
        final ItemStack p = GTModHandler.getModItem(GalacticraftCore.ID, "item.heavyPlating", 1);
        final ItemStack l = GTModHandler.getModItem(GalaxySpace.ID, "item.ModuleLander", 1);
        final ItemStack c = GTModHandler.getModItem(GalaxySpace.ID, "item.RocketControlComputer", 1, 1);
        final ItemStack sf1 = GTModHandler.getModItem(GalaxySpace.ID, "item.ModuleSmallFuelCanister");
        final ItemStack f1 = GTModHandler.getModItem(GalacticraftCore.ID, "item.rocketFins", 1);
        final ItemStack e = GTModHandler.getModItem(GalacticraftCore.ID, "item.engine", 1);
        final ItemStack s = ItemList.Sensor_LV.get(1);
        final ItemStack a = ItemList.Robot_Arm_LV.get(1);
        final ItemStack g = ItemList.Field_Generator_LV.get(1);
        final ItemStack d = GTOreDictUnificator.get(OrePrefixes.toolHeadDrill, Materials.Titanium, 1);
        final Fluid i140 = FluidRegistry.getFluid("molten.indalloy140");
        // rocket
        GTRecipeBuilder.builder()
            .itemInputs(
                f,
                f,
                f,
                f,
                f,
                f,
                f,
                f,
                f,
                f,
                f,
                f,
                f,
                f,
                f,
                f,
                f,
                f,
                f,
                f,
                f,
                f,
                f,
                f,
                f,
                f,
                f,
                f,
                f,
                f,
                f,
                n,
                f,
                f,
                f,
                f,
                f,
                p,
                l,
                p,
                f,
                f,
                f,
                f,
                p,
                c,
                p,
                f,
                f,
                f,
                f,
                p,
                sf1,
                p,
                f,
                f,
                f,
                f1,
                p,
                sf1,
                p,
                f1,
                f,
                f,
                f1,
                f,
                e,
                f,
                f1,
                f)
            .itemOutputs(GTModHandler.getModItem(GalacticraftCore.ID, "item.spaceship", 1, 0))
            .eut(100)
            .duration(100)
            .special(GTModHandler.getModItem(NewHorizonsCoreMod.ID, "item.SchematicsTier1", 0, 0))
            .addTo(NASA);
        // drone
        GTRecipeBuilder.builder()
            .itemInputs(
                f,
                f,
                f,
                f,
                f,
                f,
                f,
                f,
                f,
                f,
                f,
                f,
                f,
                f,
                f,
                f,
                s,
                d,
                a,
                f,
                f,
                f,
                f,
                p,
                p,
                p,
                f,
                f,
                f,
                p,
                f,
                f,
                f,
                p,
                f,
                f,
                p,
                f,
                c,
                f,
                p,
                f,
                f,
                p,
                f,
                g,
                f,
                p,
                f,
                f,
                p,
                f,
                f,
                f,
                p,
                f,
                f,
                f,
                p,
                p,
                p,
                f,
                f,
                f,
                f,
                e,
                f,
                e,
                f,
                f)
            .itemOutputs(GTModHandler.getModItem(GTNHIntergalactic.ID, "item.MiningDrone", 1, 0))
            .eut(100)
            .duration(100)
            .special(GTModHandler.getModItem(NewHorizonsCoreMod.ID, "item.SchematicsTier1", 0, 0))
            .addTo(NASA);
        // plating
        GTRecipeBuilder.builder()
            .itemInputsUnsafe(
                Materials.Bronze.getPlates(48 * 3),
                Materials.Aluminium.getPlates(48 * 3),
                Materials.Steel.getPlates(48 * 3))
            .itemOutputs(new ItemStack(p.getItem(), 64))
            .fluidInputs(new FluidStack(i140, 144))
            .special(GTModHandler.getModItem(NewHorizonsCoreMod.ID, "item.SchematicsTier1", 0, 0))
            .duration(240)
            .eut(1920)
            .addTo(NASA);
    }
}
