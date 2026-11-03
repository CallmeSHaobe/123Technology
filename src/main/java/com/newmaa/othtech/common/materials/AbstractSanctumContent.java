package com.newmaa.othtech.common.materials;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.newmaa.othtech.OTHTechnology;
import com.newmaa.othtech.utils.modsEnum;

import bartworks.system.material.WerkstoffLoader;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Fluid bridge used by AbstractSanctum.
 *
 * <p>
 * TST owns {@code liquidmana} when present. Without TST this mod registers its own
 * {@code liquidmana123} fluid and supplies it exclusively through the 123Technology
 * Mana Hatch.
 */
public final class AbstractSanctumContent {

    public static final String LOCAL_MANA_FLUID = "liquidmana123";
    public static final String TST_MANA_FLUID = "liquidmana";
    public static final String BLOOD_FLUID = "lifeessence";

    private static Fluid localManaFluid;
    private static BlockFluidClassic localManaBlock;

    private AbstractSanctumContent() {}

    public static void initialize() {
        if (modsEnum.TwistSpaceTechnology.isModLoaded()) return;
        if (FluidRegistry.getFluid(LOCAL_MANA_FLUID) != null) return;

        localManaFluid = new Fluid(LOCAL_MANA_FLUID).setLuminosity(10)
            .setDensity(-1000)
            .setViscosity(1000);
        FluidRegistry.registerFluid(localManaFluid);
        localManaBlock = new LocalManaBlock(localManaFluid);
        localManaFluid.setBlock(localManaBlock);
        GameRegistry.registerBlock(localManaBlock, LOCAL_MANA_FLUID);
    }

    public static Fluid getManaFluid() {
        if (modsEnum.TwistSpaceTechnology.isModLoaded()) {
            return FluidRegistry.getFluid(TST_MANA_FLUID);
        }
        if (localManaFluid == null) {
            localManaFluid = FluidRegistry.getFluid(LOCAL_MANA_FLUID);
        }
        return localManaFluid;
    }

    public static FluidStack getManaStack(int amount) {
        Fluid fluid = getManaFluid();
        return fluid == null || amount <= 0 ? null : new FluidStack(fluid, amount);
    }

    public static FluidStack getBloodStack(int amount) {
        Fluid fluid = FluidRegistry.getFluid(BLOOD_FLUID);
        return fluid == null || amount <= 0 ? null : new FluidStack(fluid, amount);
    }

    public static FluidStack getOganessonStack(int amount) {
        return amount <= 0 ? null : WerkstoffLoader.Oganesson.getFluidOrGas(amount);
    }

    @SideOnly(Side.CLIENT)
    public static void bindClientFluidIcons(TextureMap textureMap) {
        Fluid mana = getManaFluid();
        if (mana == null) return;
        Fluid distilledWater = FluidRegistry.getFluid("ic2distilledwater");
        IIcon still = distilledWater == null ? null : distilledWater.getStillIcon();
        if (still == null) {
            Fluid blood = FluidRegistry.getFluid(BLOOD_FLUID);
            still = blood == null ? null : blood.getStillIcon();
        }
        if (still == null || "missingno".equals(still.getIconName())) return;
        IIcon flowing = still;
        mana.setIcons(still, flowing);
        OTHTechnology.LOG.info(
            "Bound fluid icons for {}: still={}, flowing={}",
            mana.getName(),
            still.getIconName(),
            flowing.getIconName());
        if (localManaBlock != null) {
            ((LocalManaBlock) localManaBlock).setClientIcons(still, flowing);
        }
    }

    /**
     * A real Fluid Block is required so Forge initializes still/flowing icons. Without these
     * icons WAILA throws while rendering a fluid tooltip and can leave Tessellator active.
     */
    private static final class LocalManaBlock extends BlockFluidClassic {

        @SideOnly(Side.CLIENT)
        private IIcon stillIcon;
        @SideOnly(Side.CLIENT)
        private IIcon flowingIcon;

        private LocalManaBlock(Fluid fluid) {
            super(fluid, Material.water);
            setBlockName(LOCAL_MANA_FLUID);
            setLightLevel(0.5F);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void registerBlockIcons(IIconRegister register) {
            stillIcon = register.registerIcon("ic2:blocks/fluids/distilledwater_still");
            flowingIcon = stillIcon;
            definedFluid.setStillIcon(stillIcon);
            definedFluid.setFlowingIcon(flowingIcon);
            blockIcon = stillIcon;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public IIcon getIcon(int side, int meta) {
            return side <= 1 ? stillIcon : flowingIcon;
        }

        @SideOnly(Side.CLIENT)
        private void setClientIcons(IIcon still, IIcon flowing) {
            stillIcon = still;
            flowingIcon = flowing;
            blockIcon = still;
        }
    }
}
