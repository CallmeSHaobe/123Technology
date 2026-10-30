package com.newmaa.othtech.common.item.itemMisc;

import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 米饭奇点：1234 个白米饭在亚稳态黑洞遏制场（COMPRESSION_TIER = 2）里被压成一个点。
 *
 * <p>
 * 配方在 {@code RecipeMaps.neutroniumCompressorRecipes}，NEI 里会显示「需要稳定的黑洞」——
 * 也就是必须用 BHC（亚稳态黑洞遏制场）在黑洞稳定时跑。
 * </p>
 */
public class ItemRiceSingularity extends Item {

    public ItemRiceSingularity() {
        super();
        this.setUnlocalizedName("RiceSingularity");
        this.setTextureName("123technology:RiceSingularity");
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
        this.setMaxStackSize(64);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        tooltip.add(EnumChatFormatting.LIGHT_PURPLE + translateToLocal("tt.ds.singularity.0"));
        tooltip.add(EnumChatFormatting.GRAY + translateToLocal("tt.ds.singularity.1"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack, int pass) {
        return true; // 附魔光效，一眼认出是特殊物品
    }
}
