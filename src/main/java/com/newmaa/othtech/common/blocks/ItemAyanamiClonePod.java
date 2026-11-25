package com.newmaa.othtech.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

/** Preserve the spent metadata across harvesting, pick-block, item storage and placement. */
public final class ItemAyanamiClonePod extends ItemBlock {

    public ItemAyanamiClonePod(Block block) {
        super(block);
        setHasSubtypes(true);
        setMaxDamage(0);
        setMaxStackSize(1);
    }

    @Override
    public int getMetadata(int damage) {
        return BlockAyanamiClonePod.variant(damage);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ, int metadata) {
        if (y + 1 >= world.getActualHeight() || !world.isAirBlock(x, y + 1, z)
            || !world.checkNoEntityCollision(AxisAlignedBB.getBoundingBox(x, y + 1, z, x + 1, y + 2, z + 1))
            || (player != null
                && (!player.canPlayerEdit(x, y + 1, z, side, stack) || !world.canMineBlock(player, x, y + 1, z))))
            return false;

        int lower = BlockAyanamiClonePod.placementMetadata(player, getMetadata(stack.getItemDamage()));
        Block pod = field_150939_a;
        if (!world.setBlock(x, y, z, pod, lower, 2)) return false;
        if (world.setBlock(x, y + 1, z, pod, lower | BlockAyanamiClonePod.UPPER, 2)) return true;
        if (world.getBlock(x, y, z) == pod && world.getBlockMetadata(x, y, z) == lower) {
            world.setBlockToAir(x, y, z);
        }
        return false;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        String name = super.getUnlocalizedName(stack);
        return getMetadata(stack.getItemDamage()) == BlockAyanamiClonePod.READY ? name : name + ".spent";
    }
}
