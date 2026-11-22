package com.newmaa.othtech.machine.hatch;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.newmaa.othtech.machine.EssentiaGeneratorFuel;

import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.aspects.IEssentiaTransport;

public final class OTEEssentiaInputHatch extends TileEntity implements IEssentiaTransport, IAspectContainer {

    private static final int CAPACITY = 1000;
    private final AspectList stock = new AspectList();
    private Aspect filter;
    private int enabledFamilies = 1;

    public void configure(int families) {
        if (enabledFamilies != families) {
            enabledFamilies = families;
            markDirty();
        }
    }

    @Override
    public void updateEntity() {
        if (worldObj.isRemote) return;
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            if (stock.visSize() >= CAPACITY) break;
            TileEntity neighbor = ThaumcraftApiHelper.getConnectableTile(worldObj, xCoord, yCoord, zCoord, side);
            if (!(neighbor instanceof IEssentiaTransport)) continue;
            IEssentiaTransport source = (IEssentiaTransport) neighbor;
            ForgeDirection face = side.getOpposite();
            Aspect offered = source.getEssentiaType(face);
            if (doesContainerAccept(offered) && source.canOutputTo(face)
                && source.getSuctionAmount(face) < 256
                && source.getMinimumSuction() <= 256
                && source.getEssentiaAmount(face) > 0) {
                addEssentia(offered, source.takeEssentia(offered, 1, face), side);
            }
        }
    }

    public void burnEssentia(Aspect aspect, int amount) {
        stock.remove(aspect, amount);
        markDirty();
    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        EssentiaGeneratorFuel fuel = EssentiaGeneratorFuel.forAspect(aspect);
        return fuel != null && fuel.enabled(enabledFamilies)
            && (filter == null || filter == aspect)
            && stock.visSize() < CAPACITY;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, ForgeDirection face) {
        if (amount <= 0 || !doesContainerAccept(aspect)) return 0;
        int accepted = Math.min(amount, CAPACITY - stock.visSize());
        stock.add(aspect, accepted);
        markDirty();
        return accepted;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        stock.writeToNBT(tag);
        tag.setInteger("families", enabledFamilies);
        if (filter != null) tag.setString("filter", filter.getTag());
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        stock.readFromNBT(tag);
        enabledFamilies = tag.getInteger("families") | 1;
        filter = Aspect.getAspect(tag.getString("filter"));
    }

    @Override
    public AspectList getAspects() {
        return stock.copy();
    }

    @Override
    public void setAspects(AspectList incoming) {
        if (incoming != null)
            for (Aspect aspect : incoming.getAspects()) addToContainer(aspect, incoming.getAmount(aspect));
    }

    @Override
    public int addToContainer(Aspect aspect, int amount) {
        return amount - addEssentia(aspect, amount, null);
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int amount) {
        return false;
    }

    @Override
    public boolean takeFromContainer(AspectList request) {
        return false;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect aspect, int amount) {
        return stock.getAmount(aspect) >= amount;
    }

    @Override
    public boolean doesContainerContain(AspectList request) {
        for (Aspect aspect : request.getAspects())
            if (stock.getAmount(aspect) < request.getAmount(aspect)) return false;
        return true;
    }

    @Override
    public int containerContains(Aspect aspect) {
        return stock.getAmount(aspect);
    }

    @Override
    public boolean isConnectable(ForgeDirection side) {
        return true;
    }

    @Override
    public boolean canInputFrom(ForgeDirection side) {
        return true;
    }

    @Override
    public boolean canOutputTo(ForgeDirection side) {
        return false;
    }

    @Override
    public void setSuction(Aspect aspect, int strength) {}

    @Override
    public Aspect getSuctionType(ForgeDirection side) {
        return filter;
    }

    @Override
    public int getSuctionAmount(ForgeDirection side) {
        return 256;
    }

    @Override
    public int getMinimumSuction() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, ForgeDirection side) {
        return 0;
    }

    @Override
    public Aspect getEssentiaType(ForgeDirection side) {
        return stock.size() == 0 ? null : stock.getAspects()[0];
    }

    @Override
    public int getEssentiaAmount(ForgeDirection side) {
        return stock.visSize();
    }

    @Override
    public boolean renderExtendedTube() {
        return true;
    }

    public static final class IntakeBlock extends BlockContainer {

        public IntakeBlock() {
            super(Material.iron);
            setBlockName("oth.essentia_intake");
            setBlockTextureName("goodgenerator:essentiaHatch");
            setHardness(3.0F);
        }

        @Override
        public TileEntity createNewTileEntity(World world, int metadata) {
            return new OTEEssentiaInputHatch();
        }

        @Override
        public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
            float hitY, float hitZ) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (!(tile instanceof OTEEssentiaInputHatch)) return false;
            ItemStack held = player.getHeldItem();
            Aspect selection = null;
            if (held != null && held.getItem() instanceof IEssentiaContainerItem) {
                AspectList contents = ((IEssentiaContainerItem) held.getItem()).getAspects(held);
                if (contents == null || contents.size() != 1) return false;
                selection = contents.getAspects()[0];
            } else if (held != null || !player.isSneaking()) return false;
            if (!world.isRemote) {
                OTEEssentiaInputHatch intake = (OTEEssentiaInputHatch) tile;
                intake.filter = selection;
                intake.markDirty();
                player.addChatMessage(
                    new ChatComponentTranslation("oth.essentia.filter", selection == null ? "*" : selection.getName()));
            }
            return true;
        }
    }
}
