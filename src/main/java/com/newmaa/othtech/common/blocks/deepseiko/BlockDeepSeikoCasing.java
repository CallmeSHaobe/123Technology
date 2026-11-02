package com.newmaa.othtech.common.blocks.deepseiko;

import static com.newmaa.othtech.common.creativetab.CreativeTabsLoader.tabothtech;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IHasIndexedTexture;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;

/**
 * 迪普西科（DeepSeiko）的全部结构方块：一个 meta Block 塞下鲸体机壳 / 鸿蒙件 / 机房件 / 适配舱。
 * meta 列表见 {@link DeepSeikoCasingMeta}。
 * <p>
 * 实现 {@link IHasIndexedTexture} 并占用 GT 贴图页 114，让 {@code buildHatchAdder().casingIndex(...)}
 * 拿到有效索引——舱室（总线/仓/能源）被机器吸收时会自动把贴图换成对应机壳色，与鲸体浑然一体。
 */
public class BlockDeepSeikoCasing extends Block implements IHasIndexedTexture {

    public static final int METACOUNT = DeepSeikoCasingMeta.VALUES.length;

    /** GT casing 贴图页（0-127）：TST 占 115，我们用 114；GT 官方占用低段（0/1/2/8/16/17/20/42）。 */
    public static final byte TEXTURE_PAGE = 114;

    static {
        GTUtility.addTexturePage(TEXTURE_PAGE);
    }

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public BlockDeepSeikoCasing() {
        super(Material.iron);
        this.setBlockName("dsCasing");
        this.setHardness(9.0F);
        this.setResistance(20.0F);
        this.setStepSound(Block.soundTypeMetal);
        this.setHarvestLevel("wrench", 2);
        this.setHarvestLevel("pickaxe", 2);
        // 结构方块自带亮度（约 light 9），鲸体内部不至于黑漆漆
        this.setLightLevel(0.625F);
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
            // 9~11 是三个「鳍」：外观和鲸脂外壳（casing_0）完全一样，只是名字不同
            int tex = i < DeepSeikoCasingMeta.TEXTURE_COUNT ? i : 0;
            icons[i] = register.registerIcon("123technology:deepseiko/casing_" + tex);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        meta = Math.max(0, Math.min(METACOUNT - 1, meta));
        return icons[meta];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < METACOUNT; i++) {
            // meta 5 = 已停用的装配腔外壳（UXV）：保留编号（不能挪 meta），但不在创造栏/NEI 里出现
            if (i == DeepSeikoCasingMeta.ASSEMBLY_CASING_UXV_RETIRED) continue;
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
