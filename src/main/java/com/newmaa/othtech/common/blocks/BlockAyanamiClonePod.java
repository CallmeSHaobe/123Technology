package com.newmaa.othtech.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.ForgeDirection;

import com.newmaa.othtech.OTHTechnology;
import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;
import com.newmaa.othtech.common.entity.EntityAyanami;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** A quest-granted, manually opened single-use clone pod. Metadata is also the carried item's damage value. */
public final class BlockAyanamiClonePod extends Block {

    public static final String REGISTRY_NAME = "ayanami_clone_pod";
    static final int READY = 0;
    static final int SPENT = 1;
    static final int UPPER = 2;
    private static final int FACING_MASK = 12;
    private static final ForgeDirection[] FRONTS = { ForgeDirection.NORTH, ForgeDirection.EAST, ForgeDirection.SOUTH,
        ForgeDirection.WEST };
    private static final ForgeDirection[] EXITS = { ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST,
        ForgeDirection.EAST };
    private boolean opening;

    @SideOnly(Side.CLIENT)
    private IIcon doorLower;
    @SideOnly(Side.CLIENT)
    private IIcon doorUpper;
    @SideOnly(Side.CLIENT)
    private IIcon openedLower;
    @SideOnly(Side.CLIENT)
    private IIcon openedUpper;
    @SideOnly(Side.CLIENT)
    private IIcon endCap;

    private BlockAyanamiClonePod() {
        super(Material.iron);
        setBlockName(REGISTRY_NAME);
        setBlockTextureName(OTHTechnology.MODID + ":clone_pod/wall");
        setHardness(3.5F);
        setResistance(12.0F);
        setStepSound(soundTypeGlass);
        setHarvestLevel("pickaxe", 0);
        setCreativeTab(CreativeTabsLoader.tabothtech);
    }

    public static void register() {
        GameRegistry.registerBlock(new BlockAyanamiClonePod(), ItemAyanamiClonePod.class, REGISTRY_NAME);
    }

    static int variant(int meta) {
        return meta == READY ? READY : SPENT;
    }

    @Override
    public int damageDropped(int meta) {
        return meta & SPENT;
    }

    static int placementMetadata(EntityPlayer player, int itemVariant) {
        int direction = player == null ? 0 : MathHelper.floor_double(player.rotationYaw * 4F / 360F + 0.5D) & 3;
        return (direction << 2) | variant(itemVariant);
    }

    boolean hasMatchingHalf(World world, int x, int y, int z, int metadata) {
        int otherY = y + ((metadata & UPPER) == 0 ? 1 : -1);
        return otherY >= 0 && otherY < world.getActualHeight()
            && world.getBlock(x, otherY, z) == this
            && ((world.getBlockMetadata(x, otherY, z) ^ metadata) & (UPPER | FACING_MASK)) == UPPER;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block previous, int metadata) {
        if (!world.isRemote && hasMatchingHalf(world, x, y, z, metadata)) {
            // Destroy only the other half, without a second drop. The harvested half supplies the canonical item.
            world.setBlockToAir(x, y + ((metadata & UPPER) == 0 ? 1 : -1), z);
        }
        super.breakBlock(world, x, y, z, previous, metadata);
    }

    @Override
    public int getMobilityFlag() {
        return 2; // Refuse piston moves so the capsule is never reconstructed without its state.
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        super.registerBlockIcons(register);
        doorLower = register.registerIcon(OTHTechnology.MODID + ":clone_pod/door_lower");
        doorUpper = register.registerIcon(OTHTechnology.MODID + ":clone_pod/door_upper");
        openedLower = register.registerIcon(OTHTechnology.MODID + ":clone_pod/opened_lower");
        openedUpper = register.registerIcon(OTHTechnology.MODID + ":clone_pod/opened_upper");
        endCap = register.registerIcon(OTHTechnology.MODID + ":clone_pod/plain_cap");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata) {
        if (side == 0 || side == 1) return endCap;
        if (side != FRONTS[metadata >> 2 & 3].ordinal()) return blockIcon;
        boolean upper = (metadata & UPPER) != 0;
        if ((metadata & SPENT) != 0) return upper ? openedUpper : openedLower;
        return upper ? doorUpper : doorLower;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (world.isRemote) return true;
        if (!(player instanceof EntityPlayerMP activator) || activator instanceof FakePlayer
            || !activator.isEntityAlive()
            || opening
            || !world.canMineBlock(activator, x, y, z)
            || !activator.canPlayerEdit(x, y, z, side, activator.getHeldItem())) return true;
        int clickedMeta = world.getBlockMetadata(x, y, z);
        if ((clickedMeta & UPPER) != 0 && !hasMatchingHalf(world, x, y, z, clickedMeta)) return true;
        if ((clickedMeta & UPPER) != 0) y--;
        int lowerMeta = world.getBlockMetadata(x, y, z);
        boolean paired = hasMatchingHalf(world, x, y, z, lowerMeta);
        if (!world.canMineBlock(activator, x, y, z) || !activator.canPlayerEdit(x, y, z, side, activator.getHeldItem()))
            return true;
        if ((lowerMeta & SPENT) != 0) {
            feedback(activator, "spent");
            return true;
        }

        // Server interactions are serial. Also refuse re-entry by Forge callbacks during the spawn transaction.
        opening = true;
        try {
            EntityAyanami npc = new EntityAyanami(world);
            ForgeDirection front = FRONTS[lowerMeta >> 2 & 3];
            boolean safeExit = paired
                ? y > 0 && y + 1 < world.getActualHeight()
                    && tryExit(world, x + front.offsetX, y, z + front.offsetZ, activator, npc)
                : findExit(world, x, y, z, ForgeDirection.getOrientation(side), activator, npc);
            if (!safeExit) {
                feedback(activator, "blocked");
                return true;
            }
            npc.markCloneAwakened();
            if (!world.spawnEntityInWorld(npc)) {
                feedback(activator, "failed");
                return true;
            }
            // A mod may have replaced the block during EntityJoinWorldEvent. Roll back our spawn in that case.
            if (!npc.isEntityAlive() || world.getBlock(x, y, z) != this
                || world.getBlockMetadata(x, y, z) != lowerMeta
                || (paired && !hasMatchingHalf(world, x, y, z, lowerMeta))
                || !world.setBlockMetadataWithNotify(x, y, z, lowerMeta | SPENT, 2)) {
                world.removeEntity(npc);
                feedback(activator, "failed");
                return true;
            }
            if (paired) world.setBlockMetadataWithNotify(x, y + 1, z, lowerMeta | UPPER | SPENT, 2);
            world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.door_open", 0.8F, 0.7F);
            npc.notifyCloneAwakening(activator);
        } finally {
            opening = false;
        }
        return true;
    }

    private static boolean findExit(World world, int x, int y, int z, ForgeDirection preferred,
        EntityPlayerMP activator, EntityAyanami npc) {
        if (y < 1 || y + 1 >= world.getActualHeight()) return false;
        if (preferred.offsetY == 0 && preferred != ForgeDirection.UNKNOWN
            && tryExit(world, x + preferred.offsetX, y, z + preferred.offsetZ, activator, npc)) return true;
        for (ForgeDirection exit : EXITS) {
            if (exit != preferred && tryExit(world, x + exit.offsetX, y, z + exit.offsetZ, activator, npc)) return true;
        }
        return false;
    }

    private static boolean tryExit(World world, int x, int y, int z, EntityPlayerMP activator, EntityAyanami npc) {
        if (!world.blockExists(x, y, z) || !world.canMineBlock(activator, x, y, z)
            || !world.isAirBlock(x, y, z)
            || !world.isAirBlock(x, y + 1, z)
            || !World.doesBlockHaveSolidTopSurface(world, x, y - 1, z)) return false;
        double centerX = x + 0.5;
        double centerZ = z + 0.5;
        float yaw = (float) Math.toDegrees(Math.atan2(activator.posZ - centerZ, activator.posX - centerX)) - 90F;
        npc.setLocationAndAngles(centerX, y, centerZ, yaw, 0F);
        return world.checkNoEntityCollision(npc.boundingBox, npc)
            && world.getCollidingBoundingBoxes(npc, npc.boundingBox)
                .isEmpty()
            && !world.isAnyLiquid(npc.boundingBox);
    }

    private static void feedback(EntityPlayerMP player, String status) {
        player.addChatMessage(new ChatComponentTranslation("oth.ayanami.clone_pod." + status));
    }
}
