package com.newmaa.othtech.common.blocks.deepseiko;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * 米饭全息投影的「影子方块」—— 不可见、无碰撞、不可破坏、不掉落，只负责承载
 * {@link TEDeepSeikoHolo}（TESR 必须挂在自有方块上，GT 的 {@code BaseMetaTileEntity} 的 TESR 槽已被 GTClient 占用）。
 *
 * <p>
 * 由 {@code OTEDeepSeiko} 在结构成型后自动放到控制器正前方，拆机/结构破坏时自动清掉。
 * </p>
 */
public class BlockDeepSeikoHolo extends Block {

    public BlockDeepSeikoHolo() {
        super(Material.iron);
        setBlockName("dsHoloRice");
        setHardness(-1.0F);
        setResistance(114514.0F);
        setCreativeTab(null); // 纯技术方块，不出现在创造栏（NEI 里另外 hideItem）
    }

    // region 不可见 / 无碰撞

    @Override
    public int getRenderType() {
        return -1;
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
    public boolean canRenderInPass(int pass) {
        return true;
    }

    /** 零体积 ⇒ 没碰撞、没选择框、玩家点不到 */
    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        setBlockBounds(0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }

    // endregion

    // region 拿不到 / 拆不掉

    @Override
    public boolean canHarvestBlock(EntityPlayer player, int meta) {
        return false;
    }

    @Override
    public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
        return false;
    }

    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int fortune) {}

    @Override
    public void dropBlockAsItem(World world, int x, int y, int z, ItemStack stack) {}

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
        return new ArrayList<>();
    }

    // endregion

    // region TE

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TEDeepSeikoHolo();
    }

    // endregion
}
