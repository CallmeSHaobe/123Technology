package com.newmaa.othtech.recipe;

import com.newmaa.othtech.common.recipemap.Recipemaps;
import gregtech.api.enums.*;
import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_OreDictUnificator;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class MegaISA implements IRecipePool {
    @Override
    public void loadRecipes(){
        Fluid laoda = FluidRegistry.getFluid("molten.helicopter");
        Fluid quantum = FluidRegistry.getFluid("molten.quantum");
        Fluid Sun = FluidRegistry.getFluid("molten.sunnarium");
        Fluid white = FluidRegistry.getFluid("molten.whitedwarfmatter");
        Fluid cuihuaji5 = FluidRegistry.getFluid("exciteddtsc");
        Fluid black = FluidRegistry.getFluid("molten.blackdwarfmatter");
        Fluid space = FluidRegistry.getFluid("molten.spacetime");
        Fluid inf = FluidRegistry.getFluid("molten.infinity");
        Fluid Hy = FluidRegistry.getFluid("molten.hypogen");
        Fluid WNt = FluidRegistry.getFluid("molten.neutronium");
        Fluid BNt = FluidRegistry.getFluid("molten.cosmicneutronium");
        Fluid lava = FluidRegistry.getFluid("rawstarmatter");

        final IRecipeMap ISA = Recipemaps.Mega_ISA_Forge;
        GT_Values.RA.stdBuilder()
            .fluidInputs(new FluidStack(laoda, 123123))
            .itemInputs(GT_ModHandler.getModItem("123Technology", "LookNEIdust", 1))
            .itemOutputs(                GT_ModHandler.getModItem("miscutils", "itemDustRhenium", 64),
                GT_ModHandler.getModItem("miscutils", "itemDustThallium", 64),
                GT_ModHandler.getModItem("miscutils", "itemDustSelenium", 64),
                GT_ModHandler.getModItem("miscutils", "itemDustGermanium", 64),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 2044),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 2073),
                GT_ModHandler.getModItem("dreamcraft", "item.LapotronDust", 64 ))
            .fluidOutputs(
                new FluidStack(quantum, 177120)
            )
            .noOptimize()
            .eut(TierEU.RECIPE_MAX)
            .duration(20)
            .addTo(ISA);
        GT_Values.RA.stdBuilder()
            .itemInputs(ItemList.Robot_Arm_UXV.get(0),
            GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 1))
            .fluidOutputs(new FluidStack(Sun, 144))
            .noOptimize()
            .duration(100)
            .eut(123123123)
            .addTo(ISA);
        GT_Values.RA.stdBuilder()
            .itemInputs(ItemList.Robot_Arm_UXV.get(0),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Glowstone, 1))
            .fluidOutputs(new FluidStack(Sun, 288))
            .noOptimize()
            .duration(50)
            .eut(123123123)
            .addTo(ISA);
        GT_Values.RA.stdBuilder()
            .itemInputs(GT_ModHandler.getModItem("tectech", "gt.spacetime_compression_field_generator", 0, 2),
            ItemList.Field_Generator_UMV.get(1))
            .fluidInputs(new FluidStack(WNt, 5760))
            .fluidOutputs(new FluidStack(white, 36))
            .noOptimize()
            .duration(4000)
            .eut(2000000000)
            .addTo(ISA);
        GT_Values.RA.stdBuilder()
            .itemInputs(GT_ModHandler.getModItem("tectech", "gt.spacetime_compression_field_generator", 0, 5),
                ItemList.Field_Generator_UMV.get(2))
            .fluidInputs(new FluidStack(BNt,5760))
            .fluidOutputs(new FluidStack(black, 36))
            .noOptimize()
            .duration(8000)
            .eut(2000000000)
            .addTo(ISA);
        GT_Values.RA.stdBuilder()
            .itemInputs(GT_ModHandler.getModItem("tectech", "gt.spacetime_compression_field_generator", 0, 8),
                ItemList.Field_Generator_UXV.get(0))
            .fluidInputs(new FluidStack(space,16 * 144), new FluidStack(lava, 1000))
            .fluidOutputs(new FluidStack(inf, 576), new FluidStack(Hy, 288))
            .noOptimize()
            .duration(16000)
            .eut(2000000000)
            .addTo(ISA);
        GT_Values.RA.stdBuilder()
            .itemInputs(GT_ModHandler.getModItem("tectech", "gt.spacetime_compression_field_generator", 0, 2),
                ItemList.Field_Generator_UXV.get(1))
            .fluidInputs(new FluidStack(WNt, 5760))
            .fluidOutputs(new FluidStack(white, 576))
            .noOptimize()
            .duration(40000)
            .eut(2000000000)
            .addTo(ISA);
        GT_Values.RA.stdBuilder()
            .itemInputs(GT_ModHandler.getModItem("tectech", "gt.spacetime_compression_field_generator", 0, 5),
                ItemList.Field_Generator_UXV.get(2))
            .fluidInputs(new FluidStack(BNt,5760))
            .fluidOutputs(new FluidStack(black, 576))
            .noOptimize()
            .duration(80000)
            .eut(2000000000)
            .addTo(ISA);


    }
}
