package com.newmaa.othtech.common.item.itemMisc;

import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 算力中心（D99 / D199 / D999）。
 *
 * <p>
 * 放进控制器算力槽里当"算力卡"用，参与 BEC 的点数系数：`1 + ³√(张数) × 卡系数`。
 * 系数：D99 = 0.08、D199 = 0.16、D999 = 0.25。
 * </p>
 */
public class ItemComputeCenter extends Item {

    private static final String[] NAMES = { "ComputeCenterD99", "ComputeCenterD199", "ComputeCenterD999" };
    private static final String[] TIPS = { "tt.ds.compute.d99", "tt.ds.compute.d199", "tt.ds.compute.d999" };
    private final IIcon[] icons = new IIcon[NAMES.length];

    public ItemComputeCenter() {
        super();
        this.setUnlocalizedName("ComputeCenter");
        this.setHasSubtypes(true);
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
        this.setMaxStackSize(64);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
        for (int i = 0; i < NAMES.length; i++) {
            icons[i] = reg.registerIcon("123technology:" + NAMES[i]);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return icons[Math.max(0, Math.min(NAMES.length - 1, meta))];
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int meta = Math.max(0, Math.min(NAMES.length - 1, stack == null ? 0 : stack.getItemDamage()));
        return "item." + NAMES[meta];
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < NAMES.length; i++) list.add(new ItemStack(item, 1, i));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        int meta = Math.max(0, Math.min(TIPS.length - 1, stack.getItemDamage()));
        tooltip.add(EnumChatFormatting.GRAY + translateToLocal(TIPS[meta]));
    }
}
