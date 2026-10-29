package com.newmaa.othtech.common.item.itemSpawnEgg;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.newmaa.othtech.OTHTechnology;
import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;
import com.newmaa.othtech.common.entity.EntityDogBase;
import com.newmaa.othtech.common.entity.EntityDogBlue;
import com.newmaa.othtech.common.entity.EntityDogRed;
import com.newmaa.othtech.common.entity.EntityDogYellow;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 三色狗的刷怪蛋。
 * <p>
 * 为什么不直接注册原版刷怪蛋：1.7.10 的 {@link ItemMonsterPlacer} 靠「全局实体 id」工作
 * （{@code EntityList.entityEggs} + {@code EntityList.createEntityByID}），而三色狗是用
 * {@code EntityRegistry.registerModEntity} 注册的 mod 实体，没有全局 id，也没有 egg 信息。
 * 所以这里继承它、只接管「刷哪只狗」，外观（蛋底图 + 斑点叠层）继续复用原版贴图，不需要任何新素材。
 * <p>
 * 一个物品 + metadata 区分三种颜色（0 黄 / 1 红 / 2 蓝），行为和原版刷怪蛋一致：
 * 右键方块召唤、右键液面召唤、命名牌改过名的蛋召唤出来的狗保留名字、创造模式不消耗。
 */
public class ItemDogSpawnEgg extends ItemMonsterPlacer {

    public static final int META_YELLOW = 0;
    public static final int META_RED = 1;
    public static final int META_BLUE = 2;
    public static final int META_COUNT = 3;

    /** 三种狗的实体名（不含 modid），顺序必须与 metadata 一致。 */
    private static final String[] DOG_NAMES = { "DogYellow", "DogRed", "DogBlue" };

    /** 蛋壳主色 / 斑点色，原版刷怪蛋的灰度底图会被这两个颜色乘算。 */
    private static final int[] PRIMARY_COLORS = { 0xEFD45B, 0xE0615F, 0x5F7FE0 };
    private static final int[] SPOT_COLORS = { 0x8A6C12, 0x7A1C1C, 0x1C2A7A };

    public ItemDogSpawnEgg() {
        super();
        this.setUnlocalizedName("dogSpawnEgg");
        // 复用原版刷怪蛋贴图：textures/items/spawn_egg.png + spawn_egg_overlay.png
        this.setTextureName("spawn_egg");
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String base = ("" + StatCollector.translateToLocal(this.getUnlocalizedName() + ".name")).trim();
        String dogName = "entity." + OTHTechnology.MODID
            + "."
            + DOG_NAMES[normalizeMeta(stack.getItemDamage())]
            + ".name";
        return base + " " + StatCollector.translateToLocal(dogName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        int meta = normalizeMeta(stack.getItemDamage());
        return renderPass == 0 ? PRIMARY_COLORS[meta] : SPOT_COLORS[meta];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List subItems) {
        for (int meta = 0; meta < META_COUNT; ++meta) {
            subItems.add(new ItemStack(item, 1, meta));
        }
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        Block clicked = world.getBlock(x, y, z);
        int spawnX = x + Facing.offsetsXForSide[side];
        int spawnY = y + Facing.offsetsYForSide[side];
        int spawnZ = z + Facing.offsetsZForSide[side];
        // 点在上半砖 / 台阶顶面时原版会抬高半个方块，免得实体卡进方块
        double offsetY = side == 1 && clicked.getRenderType() == 11 ? 0.5D : 0.0D;

        Entity dog = spawnDog(
            world,
            stack.getItemDamage(),
            spawnX + 0.5D,
            spawnY + offsetY,
            spawnZ + 0.5D,
            stack.hasDisplayName() ? stack.getDisplayName() : null);
        if (dog != null && !player.capabilities.isCreativeMode) {
            --stack.stackSize;
        }
        return true;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote) {
            return stack;
        }

        MovingObjectPosition hit = this.getMovingObjectPositionFromPlayer(world, player, true);
        if (hit == null || hit.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
            return stack;
        }

        int x = hit.blockX;
        int y = hit.blockY;
        int z = hit.blockZ;
        if (!world.canMineBlock(player, x, y, z) || !player.canPlayerEdit(x, y, z, hit.sideHit, stack)) {
            return stack;
        }
        // 和原版刷怪蛋一样，只往液面（水 / 王水之类的流体）里放
        if (!(world.getBlock(x, y, z) instanceof BlockLiquid)) {
            return stack;
        }

        Entity dog = spawnDog(
            world,
            stack.getItemDamage(),
            x,
            y,
            z,
            stack.hasDisplayName() ? stack.getDisplayName() : null);
        if (dog != null && !player.capabilities.isCreativeMode) {
            --stack.stackSize;
        }
        return stack;
    }

    /**
     * 按 metadata 造一只狗（不放进世界）。
     *
     * @param meta 0 黄 / 1 红 / 2 蓝，越界时按模 3 折叠
     */
    public static EntityDogBase createDog(World world, int meta) {
        switch (normalizeMeta(meta)) {
            case META_RED:
                return new EntityDogRed(world);
            case META_BLUE:
                return new EntityDogBlue(world);
            case META_YELLOW:
            default:
                return new EntityDogYellow(world);
        }
    }

    /**
     * 按 metadata 在世界里生成一只狗，流程与原版 {@code ItemMonsterPlacer#spawnCreature} 一致。
     *
     * @param customName 非空时给这只狗挂上名字（命过名的刷怪蛋）
     * @return 生成出来的狗；客户端或空世界返回 null
     */
    public static EntityDogBase spawnDog(World world, int meta, double x, double y, double z, String customName) {
        if (world == null || world.isRemote) {
            return null;
        }

        EntityDogBase dog = createDog(world, meta);
        if (dog == null) {
            return null;
        }

        dog.setLocationAndAngles(x, y, z, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0F), 0.0F);
        dog.rotationYawHead = dog.rotationYaw;
        dog.renderYawOffset = dog.rotationYaw;
        dog.onSpawnWithEgg((IEntityLivingData) null);
        if (customName != null && !customName.isEmpty()) {
            dog.setCustomNameTag(customName);
        }
        world.spawnEntityInWorld(dog);
        dog.playLivingSound();
        return dog;
    }

    /** 越界的 metadata 折叠回 0..META_COUNT-1，免得指令 / NEI 传怪值时数组越界。 */
    private static int normalizeMeta(int meta) {
        int folded = meta % META_COUNT;
        return folded < 0 ? folded + META_COUNT : folded;
    }
}
