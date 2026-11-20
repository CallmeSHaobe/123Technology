package com.newmaa.othtech.common.blocks.nailong;

import static com.newmaa.othtech.common.creativetab.CreativeTabsLoader.tabothtech;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IHasIndexedTexture;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;

public class BlockNailongCasing extends Block implements IHasIndexedTexture {

    public static final int METACOUNT = NailongCasingMeta.VALUES.length;

    /** GT casing 贴图页（0-127） */
    public static final byte TEXTURE_PAGE = 113;

    private static final Logger LOG = LogManager.getLogger("123Technology/NailongCasing");
    private static final int WHITE = 0xFFFFFF;

    static {
        if (!GTUtility.addTexturePage(TEXTURE_PAGE)) {
            LOG.warn(
                "GT casing texture page {} is already taken by another mod; hatch textures of Nailong casings may clash.",
                TEXTURE_PAGE);
        }
    }

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public BlockNailongCasing() {
        super(Material.iron);
        this.setBlockName("nailongCasing");
        this.setHardness(9.0F);
        this.setResistance(20.0F);
        this.setStepSound(Block.soundTypeMetal);
        this.setHarvestLevel("wrench", 2);
        this.setHarvestLevel("pickaxe", 2);
        GregTechAPI.registerMachineBlock(this, -1);
        // GTNH's boron-silicate glass family renders in the translucent pass with
        // glass-like face culling. Metadata cannot change these block-level flags,
        // so the shared Nailong casing block uses the same rendering rules.
        this.setLightOpacity(0);
        for (int i = 0; i < METACOUNT; i++) {
            Textures.BlockIcons.setCasingTextureForId(
                getTextureIndex(i),
                TextureFactory.of(this, i, net.minecraftforge.common.util.ForgeDirection.SOUTH, getCasingTint(i)));
        }
    }

    public static int getCasingColor(int meta) {
        return WHITE;
    }

    public static short[] getCasingTint(int meta) {
        int color = getCasingColor(meta);
        return new short[] { (short) ((color >> 16) & 0xFF), (short) ((color >> 8) & 0xFF), (short) (color & 0xFF), 0 };
    }

    @Override
    public int getTextureIndex(int aMeta) {
        if (aMeta < 0 || aMeta >= METACOUNT) return Textures.BlockIcons.ERROR_TEXTURE_INDEX;
        return (TEXTURE_PAGE << 7) + aMeta;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        icons = new IIcon[METACOUNT];
        for (int i = 0; i < METACOUNT; i++) {
            icons[i] = register.registerIcon("123technology:nailong/casing_" + i);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        meta = Math.max(0, Math.min(METACOUNT - 1, meta));
        return icons[meta];
    }

    @Override
    public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
        return getCasingColor(world.getBlockMetadata(x, y, z));
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        if (world.getBlock(x, y, z) == this) return false;
        return super.shouldSideBeRendered(world, x, y, z, side);
    }

    @Override
    protected boolean canSilkHarvest() {
        return true;
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        GregTechAPI.causeMachineUpdate(world, x, y, z);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        GregTechAPI.causeMachineUpdate(world, x, y, z);
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < METACOUNT; i++) {
            if (i == NailongCasingMeta.VOID_NEXUS_CONTROLLER_FRONT) continue;
            list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTabToDisplayOn() {
        return tabothtech;
    }
}
