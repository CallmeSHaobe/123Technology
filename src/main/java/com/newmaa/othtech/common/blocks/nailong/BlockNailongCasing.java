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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IHasIndexedTexture;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;

/**
 * 奈隆系机壳：一个 meta Block 塞下「奈隆机械方块」与「黄桃罐头机械方块」。
 * <p>
 * meta 0 = 奈隆：六面同材质，纯黄色里掺一点淡黄。
 * meta 1 = 黄桃罐头：在 meta 0 的底色上加玻璃质感（半透明高光 + 玻璃罐边）。
 * <p>
 * 实现 {@link IHasIndexedTexture} 并占用 GT 贴图页 113（GT 官方用 0/1/2/8/16/17/20/42，
 * 本模组 DeepSeiko 占 114，TST 占 115），这样它作为多方块机壳时仓口贴图能跟着换色。
 */
public class BlockNailongCasing extends Block implements IHasIndexedTexture {

    public static final int METACOUNT = NailongCasingMeta.VALUES.length;

    /** GT casing 贴图页（0-127） */
    public static final byte TEXTURE_PAGE = 113;

    private static final Logger LOG = LogManager.getLogger("123Technology/NailongCasing");

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
        for (int i = 0; i < METACOUNT; i++) {
            Textures.BlockIcons.setCasingTextureForId(getTextureIndex(i), TextureFactory.of(this, i));
        }
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
        // 六面同材质
        return icons[meta];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < METACOUNT; i++) {
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
