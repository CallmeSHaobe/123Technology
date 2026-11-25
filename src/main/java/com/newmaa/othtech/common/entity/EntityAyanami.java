package com.newmaa.othtech.common.entity;

import java.util.Arrays;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.EntityGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.newmaa.othtech.common.blocks.AyanamiAction;
import com.newmaa.othtech.common.blocks.AyanamiBlink;
import com.newmaa.othtech.common.dialogue.AyanamiChatGui;
import com.newmaa.othtech.common.dialogue.AyanamiDialogue;
import com.newmaa.othtech.common.dialogue.AyanamiDialogueNetwork.LocalLine;
import com.newmaa.othtech.common.dialogue.AyanamiEventSpeech;
import com.newmaa.othtech.common.item.itemWeapons.ItemLonginus;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** Persistent, passive NPC using vanilla creature movement. */
public class EntityAyanami extends EntityCreature implements IGuiHolder<EntityGuiData> {

    private static final int ACTION_WATCHER = 20;
    // Forge 1.7.10 DataWatcher has no long type; sync the full world tick as two ints.
    private static final int ACTION_TICK_LOW_WATCHER = 21;
    private static final int ACTION_TICK_HIGH_WATCHER = 22;
    private static final int FOCUSED_PLAYER_WATCHER = 23;
    private static final int DIALOGUE_PLAYER_WATCHER = 24;
    private static final int BLOCK_LOOK_WATCHER = 26;
    private static final int DOWNED_WATCHER = 27;
    private static final String DOWNED_TAG = "AyanamiDownedTicks";
    public static final float STANDING_HEIGHT = 1.70F;
    public static final float BODY_SCALE = STANDING_HEIGHT / 1.60F; // Standing OBJ height above its foot origin.
    public static final int DOWNED_TICKS = 100;
    public static final int FALL_TICKS = 10;
    public static final int RISE_TICKS = 16;
    private int revivalProtection;
    private static final int NO_FOCUSED_PLAYER = -1;
    private static final int AWAKENING_TICKS = 84;
    private static final int STRIKE_TICKS = 10;
    private static final int THRUST_TICKS = 14;
    public static final int PERSONAL_SLOTS = 8;
    private static final double GAZE_RANGE_SQUARED = 8.0D * 8.0D;
    private static final String ACTION_TAG = "AyanamiAction";
    private static final String ACTION_START_TICK_TAG = "AyanamiActionStartTick";

    private final AyanamiBlink blink = new AyanamiBlink();
    private final AyanamiEventSpeech eventSpeech = new AyanamiEventSpeech(this);
    private long dialogueGestureUntil;
    private long awakeningUntil;
    private long awakeningLineAt;
    private EntityPlayerMP awakeningRecipient;
    private EntityPlayerMP cloneWakeRecipient;
    private long cloneWakeLineAt;
    private long nextAmbientCheck;
    private long nextPickupCheck;
    private long blockLookUntil;
    private int lookBlockX;
    private int lookBlockY;
    private int lookBlockZ;
    private boolean clonePodAwakened;
    private boolean longinusAwakened;
    private UUID itemCustodian;
    private final ItemStack[] pockets = new ItemStack[PERSONAL_SLOTS];
    private final UUID[] pocketCustodians = new UUID[PERSONAL_SLOTS];
    private long personalInventoryRevision;
    private long nextPersonalAction;
    private boolean applyingPersonalAction;
    private AyanamiItemClick.Hold pendingItemHold;
    private BooleanSupplier pendingItemValidity;
    private Consumer<String> pendingItemCompletion;
    private UUID pendingItemGiver;
    private long dialogueHoldUntil;
    private EntityPlayerMP dialoguePlayer;
    private Object dialogueSession;
    private EntityLiving assignedAttacker;
    private long nextAttackPath;
    private long strikeUntil;
    private long strikeAt;
    private boolean thrusting;

    public EntityAyanami(World world) {
        super(world);
        setSize(0.6F * BODY_SCALE, STANDING_HEIGHT);
        setEquipmentDropChance(0, 2F); // Preserve the exact loaned stack when the NPC drops equipment.
        func_110163_bv(); // Enable persistence and keep vanilla's wandering age counter from expiring.
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIWander(this, 0.8D) {

            @Override
            public boolean shouldExecute() {
                return activeDialoguePlayer() == null && awakeningUntil == 0 && !retaliating() && super.shouldExecute();
            }

            @Override
            public boolean continueExecuting() {
                return activeDialoguePlayer() == null && awakeningUntil == 0
                    && !retaliating()
                    && super.continueExecuting();
            }
        });
        tasks.addTask(2, new LookAtNearestPlayer());
        tasks.addTask(3, new EntityAILookIdle(this));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataWatcher.addObject(ACTION_WATCHER, AyanamiAction.STANDING.getId());
        dataWatcher.addObject(ACTION_TICK_LOW_WATCHER, 0);
        dataWatcher.addObject(ACTION_TICK_HIGH_WATCHER, 0);
        dataWatcher.addObject(FOCUSED_PLAYER_WATCHER, NO_FOCUSED_PLAYER);
        dataWatcher.addObject(DIALOGUE_PLAYER_WATCHER, NO_FOCUSED_PLAYER);
        dataWatcher.addObject(BLOCK_LOOK_WATCHER, (byte) 0);
        dataWatcher.addObject(DOWNED_WATCHER, -1);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D);
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
        getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage)
            .setBaseValue(2D);
    }

    @Override
    protected boolean isAIEnabled() {
        return true;
    }

    @Override
    public float getEyeHeight() {
        return 1.225F * BODY_SCALE; // Match the rendered eye center for vanilla look and sight.
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public void onUpdate() {
        if (!worldObj.isRemote) {
            if (revivalProtection > 0) revivalProtection--;
            if (getDownedTicksElapsed() >= 0 && getHealth() > 0F) setHealth(0F);
        }
        super.onUpdate();
        if (!isEntityAlive()) return;
        if (worldObj.isRemote) {
            blink.tick(rand);
            return;
        }
        long now = worldObj.getTotalWorldTime();
        tickPersonalItemHold();
        if (cloneWakeLineAt != 0 && now >= cloneWakeLineAt) {
            if (nearby(cloneWakeRecipient)) eventSpeech.privateLine(cloneWakeRecipient, LocalLine.CLONE_WAKE);
            cloneWakeLineAt = 0;
            cloneWakeRecipient = null;
        }
        if (awakeningLineAt != 0 && now >= awakeningLineAt) {
            if (nearby(awakeningRecipient)) eventSpeech.privateLine(awakeningRecipient, LocalLine.AWAKENING);
            awakeningLineAt = 0;
            awakeningRecipient = null;
        }
        if (awakeningUntil != 0 && now >= awakeningUntil) {
            awakeningUntil = 0;
            setAction(AyanamiAction.STANDING, now);
        }
        if (dialogueGestureUntil != 0 && now >= dialogueGestureUntil) {
            dialogueGestureUntil = 0;
            setAction(AyanamiAction.STANDING, now);
        }
        if (blockLookUntil != 0 && (now >= blockLookUntil || activeDialoguePlayer() == null)) clearBlockLook();
        if (strikeUntil != 0 && now >= strikeUntil) {
            strikeUntil = 0;
            setAction(AyanamiAction.STANDING, now);
        }
        if (activeDialoguePlayer() != null || awakeningUntil != 0 || retaliating()) eventSpeech.cancelAmbient();
        eventSpeech.tick(now);
        updateWanderEvents(now);
    }

    public float getEyeClosure(float partialTick) {
        return isEntityAlive() ? blink.closure(partialTick) : 0;
    }

    public AyanamiAction getAction() {
        String id = dataWatcher.getWatchableObjectString(ACTION_WATCHER);
        if (AyanamiAction.AWAKENING.getId()
            .equals(id)) return AyanamiAction.AWAKENING;
        if (AyanamiAction.STRIKE.getId()
            .equals(id)) return AyanamiAction.STRIKE;
        if (AyanamiAction.THRUST.getId()
            .equals(id)) return AyanamiAction.THRUST;
        return AyanamiAction.fromId(id);
    }

    /** Total world time when the server selected the current animation preset. */
    public long getActionStartTick() {
        long high = dataWatcher.getWatchableObjectInt(ACTION_TICK_HIGH_WATCHER);
        long low = dataWatcher.getWatchableObjectInt(ACTION_TICK_LOW_WATCHER) & 0xFFFFFFFFL;
        return (high << 32) | low;
    }

    /** Resolves the server-selected gaze target on either side, when present in this world. */
    public EntityPlayer getFocusedPlayer() {
        if (dataWatcher.getWatchableObjectByte(BLOCK_LOOK_WATCHER) != 0 || getAction() == AyanamiAction.AWAKENING)
            return null;
        int id = dataWatcher.getWatchableObjectInt(DIALOGUE_PLAYER_WATCHER);
        if (id == NO_FOCUSED_PLAYER) id = dataWatcher.getWatchableObjectInt(FOCUSED_PLAYER_WATCHER);
        if (worldObj == null || id == NO_FOCUSED_PLAYER || !isEntityAlive()) return null;

        Entity entity = worldObj.getEntityByID(id);
        if (entity instanceof EntityPlayer player && player.isEntityAlive() && !player.isInvisible()) return player;
        return null;
    }

    private boolean nearby(EntityPlayer player) {
        return player != null && player.isEntityAlive()
            && player.worldObj == worldObj
            && getDistanceSqToEntity(player) <= GAZE_RANGE_SQUARED;
    }

    @Override
    protected boolean interact(EntityPlayer player) {
        if (!isEntityAlive()) return super.interact(player);
        ItemStack offered = player.getHeldItem();
        if (worldObj.isRemote) return true;
        if (!(player instanceof EntityPlayerMP giver) || giver instanceof FakePlayer || !nearby(giver)) return false;
        EntityPlayerMP partner = activeDialoguePlayer();
        if (awakeningUntil != 0 || retaliating()
            || applyingPersonalAction
            || pendingItemHold != null
            || (partner != null && partner != giver)) {
            tell(giver, "handoff_busy");
            return true;
        }
        if (offered == null) {
            returnHeldItem(giver);
        } else if (offered.stackSize > 0) {
            acceptItem(giver, offered);
        }
        return true;
    }

    private void acceptItem(EntityPlayerMP giver, ItemStack offered) {
        boolean spear = offered.getItem() instanceof ItemLonginus;
        int slot = getHeldItem() == null ? 0 : firstEmptyPocket();
        if (slot < 0 || (spear && !longinusAwakened && slot != 0 && getHeldItem().stackSize != 1)) {
            tell(giver, "inventory_full");
            return;
        }
        ItemStack accepted = offered.splitStack(1);
        if (offered.stackSize == 0) giver.inventory.setInventorySlotContents(giver.inventory.currentItem, null);
        giver.inventory.markDirty();
        if (spear && !longinusAwakened && slot != 0) {
            setPersonalItem(slot, getHeldItem(), itemCustodian);
            slot = 0;
        }
        setPersonalItem(slot, accepted, giver.getUniqueID());
        if (slot == 0 && getAction() == AyanamiAction.WAVE) {
            dialogueGestureUntil = 0;
            setAction(AyanamiAction.STANDING, worldObj.getTotalWorldTime());
        }
        if (accepted.getItem() instanceof ItemLonginus) {
            if (!longinusAwakened) {
                longinusAwakened = true;
                long now = worldObj.getTotalWorldTime();
                awakeningUntil = now + AWAKENING_TICKS;
                awakeningLineAt = now + 47;
                awakeningRecipient = giver;
                dialogueGestureUntil = 0;
                getNavigator().clearPathEntity();
                clearBlockLook();
                eventSpeech.cancelAmbient();
                setAction(AyanamiAction.AWAKENING, now);
            } else {
                eventSpeech.privateLine(giver, LocalLine.LONGINUS_AGAIN);
            }
        } else {
            eventSpeech.privateLine(giver, LocalLine.RECEIVED);
        }
    }

    private void returnHeldItem(EntityPlayerMP player) {
        UUID owner = player.getUniqueID();
        int slot = getHeldItem() != null && (itemCustodian == null || owner.equals(itemCustodian)) ? 0 : -1;
        // The original giver can still take back a gift even after Rei moves it into a pocket.
        for (int i = 1; slot < 0 && i <= PERSONAL_SLOTS; i++) {
            if (personalItem(i) != null && owner.equals(personalCustodian(i))) slot = i;
        }
        if (slot < 0) {
            if (getHeldItem() != null) tell(player, "not_custodian");
            return;
        }
        ItemStack held = personalItem(slot);
        int empty = player.inventory.getFirstEmptyStack();
        int target = -1;
        for (int i = 0; i < player.inventory.mainInventory.length; i++) {
            ItemStack stack = player.inventory.mainInventory[i];
            if (stack != null && stack.isStackable()
                && stack.isItemEqual(held)
                && ItemStack.areItemStackTagsEqual(stack, held)
                && stack.stackSize + held.stackSize
                    <= Math.min(stack.getMaxStackSize(), player.inventory.getInventoryStackLimit())) {
                target = i;
                break;
            }
        }
        if (target < 0 && empty < 0) {
            tell(player, "no_inventory_room");
            return;
        }
        // Vanilla inventory insertion discards a full-inventory remainder in creative mode.
        if (target < 0) player.inventory.setInventorySlotContents(empty, held.copy());
        else player.inventory.mainInventory[target].stackSize += held.stackSize;
        player.inventory.markDirty();
        setPersonalItem(slot, null, null);
        eventSpeech.privateLine(player, LocalLine.RETURNED);
    }

    public ItemStack getPersonalItem(int slot) {
        ItemStack stack = personalItem(slot);
        return stack == null || stack.stackSize <= 0 ? null : stack.copy();
    }

    public long getPersonalInventoryRevision() {
        return personalInventoryRevision;
    }

    public boolean hasDialogueItemLease(Object session, EntityPlayerMP player) {
        return !worldObj.isRemote && isEntityAlive()
            && session != null
            && player != null
            && dialogueSession == session
            && activeDialoguePlayer() == player
            && awakeningUntil == 0
            && !retaliating();
    }

    private ItemStack personalItem(int slot) {
        return slot == 0 ? getHeldItem() : slot > 0 && slot <= PERSONAL_SLOTS ? pockets[slot - 1] : null;
    }

    private UUID personalCustodian(int slot) {
        return slot == 0 ? itemCustodian : pocketCustodians[slot - 1];
    }

    private int firstEmptyPocket() {
        for (int i = 1; i <= PERSONAL_SLOTS; i++) if (personalItem(i) == null) return i;
        return -1;
    }

    @Override
    public void setCurrentItemOrArmor(int slot, ItemStack stack) {
        super.setCurrentItemOrArmor(slot, stack);
        if (slot == 0) {
            itemCustodian = null;
            personalInventoryRevision++;
        }
    }

    private void setPersonalItem(int slot, ItemStack stack, UUID giver) {
        if (slot == 0) {
            setCurrentItemOrArmor(0, stack);
            itemCustodian = stack == null ? null : giver;
        } else {
            pockets[slot - 1] = stack;
            pocketCustodians[slot - 1] = stack == null ? null : giver;
            personalInventoryRevision++;
        }
    }

    /** Only a live dialogue result may request one exact operation on Rei's own unchanged slot. */
    public String applyPersonalItemAction(String verb, int slot, ItemStack expected, long revision,
        BooleanSupplier stillValid, Consumer<String> completion) {
        if (worldObj.isRemote || !isEntityAlive() || activeDialoguePlayer() == null) return "item_session";
        long now = worldObj.getTotalWorldTime();
        if (awakeningUntil != 0 || retaliating()
            || applyingPersonalAction
            || pendingItemHold != null
            || now < nextPersonalAction) return "item_busy";
        ItemStack stack = personalItem(slot);
        if (verb == null || revision != personalInventoryRevision
            || expected == null
            || stack == null
            || stack.stackSize <= 0
            || !ItemStack.areItemStacksEqual(stack, expected)) return "item_changed";
        UUID giver = personalCustodian(slot);
        applyingPersonalAction = true;
        try {
            switch (verb) {
                case "hold":
                    if (slot == 0 || (getHeldItem() != null && getHeldItem().stackSize != 1)) return "item_unsupported";
                    setPersonalItem(slot, getHeldItem(), itemCustodian);
                    setPersonalItem(0, stack, giver);
                    break;
                case "drop":
                    if (!dropPersonalItem(slot, stack, giver)) return "item_unsupported";
                    break;
                case "left_click":
                    if (slot != 0) return "item_unsupported";
                    dialogueGestureUntil = 0;
                    strikeUntil = now + STRIKE_TICKS;
                    setAction(AyanamiAction.STRIKE, now);
                    // The rig handles the visible swing; trusted items can also receive Forge's swing hook.
                    if (AyanamiDialogue.allowsNativeItemClick(Item.itemRegistry.getNameForObject(stack.getItem()))) {
                        nextPersonalAction = now + 20;
                        personalInventoryRevision++;
                        swingItem();
                        if (stack == getHeldItem() && stack.stackSize <= 0) setPersonalItem(0, null, null);
                    }
                    break;
                case "right_click":
                case "hold_right":
                    if (slot != 0 || stack.stackSize != 1) return "item_unsupported";
                    if (AyanamiItemUse.canConsume(stack, "eat")) consumePersonalItem(slot, stack, giver, "eat");
                    else if (AyanamiItemUse.canConsume(stack, "use")) consumePersonalItem(slot, stack, giver, "use");
                    else {
                        String id = Item.itemRegistry.getNameForObject(stack.getItem());
                        if (!AyanamiDialogue.allowsNativeItemClick(id)) return "item_not_allowed";
                        nextPersonalAction = now + 20;
                        // Remove the unit before a mod hook can re-enter entity events (e.g. death/drop callbacks).
                        setPersonalItem(slot, null, null);
                        AyanamiItemClick.Result result = AyanamiItemClick
                            .rightClick(this, stack.copy(), "hold_right".equals(verb));
                        if (result.hold != null) {
                            pendingItemHold = result.hold;
                            pendingItemValidity = stillValid;
                            pendingItemCompletion = completion;
                            pendingItemGiver = giver;
                            return null;
                        }
                        if (!isDead && personalItem(slot) == null) {
                            setPersonalItem(slot, result.replacement, giver);
                        }
                        if (!result.success) return result.reason;
                    }
                    break;
                case "eat":
                case "use":
                    if (stack.stackSize != 1 || !AyanamiItemUse.canConsume(stack, verb)) return "item_unsupported";
                    consumePersonalItem(slot, stack, giver, verb);
                    break;
                default:
                    return "item_unsupported";
            }
            nextPersonalAction = now + 20;
            return "";
        } finally {
            applyingPersonalAction = false;
        }
    }

    private void tickPersonalItemHold() {
        if (pendingItemHold == null) return;
        if (awakeningUntil != 0 || retaliating() || getHeldItem() != null || !pendingItemValidity.getAsBoolean()) {
            settlePersonalItemHold(pendingItemHold.cancel(), "item_interrupted");
            return;
        }
        applyingPersonalAction = true;
        try {
            AyanamiItemClick.Result result = pendingItemHold.tick(this);
            if (result != null) settlePersonalItemHold(
                result,
                isEntityAlive() ? (result.success ? "" : result.reason) : "item_interrupted",
                !isDead);
        } finally {
            applyingPersonalAction = false;
        }
    }

    private void settlePersonalItemHold(AyanamiItemClick.Result result, String status) {
        settlePersonalItemHold(result, status, true);
    }

    private void settlePersonalItemHold(AyanamiItemClick.Result result, String status, boolean restore) {
        UUID giver = pendingItemGiver;
        Consumer<String> completion = pendingItemCompletion;
        pendingItemHold = null;
        pendingItemGiver = null;
        pendingItemValidity = null;
        pendingItemCompletion = null;
        if (restore && getHeldItem() == null) setPersonalItem(0, result.replacement, giver);
        else if (restore && result.replacement != null) {
            int pocket = firstEmptyPocket();
            if (pocket >= 0) setPersonalItem(pocket, result.replacement, giver);
        }
        if (completion != null) completion.accept(status);
    }

    private void consumePersonalItem(int slot, ItemStack stack, UUID giver, String verb) {
        setPersonalItem(slot, null, null);
        ItemStack container = AyanamiItemUse.consume(this, stack, verb);
        if (container != null && isEntityAlive() && personalItem(slot) == null) {
            setPersonalItem(slot, container, giver);
        }
        worldObj.playSoundAtEntity(this, "eat".equals(verb) ? "random.eat" : "random.drink", 0.6F, 1F);
    }

    private boolean dropPersonalItem(int slot, ItemStack stack, UUID giver) {
        if (stack.getItem() == null || !usesVanillaDropEntity(stack.getItem())) return false;
        ItemStack unit = stack.copy();
        unit.stackSize = 1;
        EntityItem drop = new EntityItem(worldObj, posX, posY + 0.2D, posZ, unit);
        drop.motionX = 0D;
        drop.motionY = 0.08D;
        drop.motionZ = 0D;
        drop.delayBeforeCanPickup = 30;
        drop.func_145799_b("Ayanami"); // Do not re-collect this as a naturally abandoned item.
        // Keep the slot untouched if another mod vetoes spawning an item entity.
        if (!worldObj.spawnEntityInWorld(drop)) return false;
        if (!isEntityAlive() || personalItem(slot) != stack) {
            drop.setDead();
            return false;
        }
        if (stack.stackSize == 1) setPersonalItem(slot, null, null);
        else {
            ItemStack remainder = stack.copy();
            remainder.stackSize--;
            setPersonalItem(slot, remainder, giver);
        }
        return true;
    }

    private static boolean usesVanillaDropEntity(Item item) {
        try {
            return item.getClass()
                .getMethod("hasCustomEntity", ItemStack.class)
                .getDeclaringClass() == Item.class;
        } catch (ReflectiveOperationException | SecurityException e) {
            return false;
        }
    }

    private static void tell(EntityPlayerMP player, String key) {
        player.addChatMessage(new ChatComponentTranslation("oth.ayanami.interact." + key));
    }

    public void markCloneAwakened() {
        if (!worldObj.isRemote) clonePodAwakened = true;
    }

    public boolean isClonePodAwakened() {
        return clonePodAwakened;
    }

    public void notifyCloneAwakening(EntityPlayerMP player) {
        if (!worldObj.isRemote && clonePodAwakened && nearby(player)) {
            cloneWakeRecipient = player;
            cloneWakeLineAt = worldObj.getTotalWorldTime() + 4;
        }
    }

    public void lookAtDialogueBlock(int x, int y, int z) {
        if (worldObj.isRemote || activeDialoguePlayer() == null
            || awakeningUntil != 0
            || retaliating()
            || getDistanceSq(x + 0.5D, y + 0.5D, z + 0.5D) > GAZE_RANGE_SQUARED
            || !worldObj.blockExists(x, y, z)) return;
        lookBlockX = x;
        lookBlockY = y;
        lookBlockZ = z;
        blockLookUntil = worldObj.getTotalWorldTime() + 30;
        dataWatcher.updateObject(BLOCK_LOOK_WATCHER, (byte) 1);
    }

    private void clearBlockLook() {
        blockLookUntil = 0;
        dataWatcher.updateObject(BLOCK_LOOK_WATCHER, (byte) 0);
    }

    private void updateWanderEvents(long now) {
        if (activeDialoguePlayer() != null || awakeningUntil != 0
            || retaliating()
            || getNavigator().noPath()
            || !onGround) return;
        if (now >= nextAmbientCheck && worldObj.getClosestPlayerToEntity(this, 12D) != null) {
            nextAmbientCheck = now + 900 + rand.nextInt(600);
            if (rand.nextInt(3) == 0) {
                int x = MathHelper.floor_double(posX);
                int y = MathHelper.floor_double(posY);
                int z = MathHelper.floor_double(posZ);
                byte line = (byte) (worldObj.isRaining() && worldObj.canBlockSeeTheSky(x, y, z) ? 1
                    : !worldObj.isDaytime() ? 2 : 3 + rand.nextInt(2));
                eventSpeech.selfTalk(line);
            }
        }
        if (getHeldItem() == null && now >= nextPickupCheck) {
            nextPickupCheck = now + 100;
            if (rand.nextInt(200) == 0) tryPickUpOwnerlessDrop();
        }
    }

    private void tryPickUpOwnerlessDrop() {
        // No general Forge 1.7.10 claim/pickup permission API for NPCs. Fail closed on public servers.
        MinecraftServer server = MinecraftServer.getServer();
        if (server == null || !server.isSinglePlayer() || server.getConfigurationManager().playerEntityList.size() != 1)
            return;
        EntityPlayer player = worldObj.getClosestPlayerToEntity(this, 12D);
        if (player == null || player != server.getConfigurationManager().playerEntityList.get(0)) return;
        for (EntityItem item : worldObj
            .getEntitiesWithinAABB(EntityItem.class, boundingBox.expand(0.55D, 0.3D, 0.55D))) {
            if (item.isDead || item.ticksExisted < 200
                || item.delayBeforeCanPickup > 0
                || item.func_145798_i() != null
                || item.func_145800_j() != null) continue;
            ItemStack stack = item.getEntityItem();
            if (stack.stackSize < 1 || stack.getItem() == null || !getEntitySenses().canSee(item)) continue;
            int x = MathHelper.floor_double(item.posX);
            int y = MathHelper.floor_double(item.posY);
            int z = MathHelper.floor_double(item.posZ);
            if (!worldObj.blockExists(x, y, z) || !worldObj.canMineBlock(player, x, y, z)) continue;
            ItemStack taken = stack.splitStack(1);
            if (stack.stackSize == 0) item.setDead();
            else item.setEntityItemStack(stack);
            setPersonalItem(0, taken, player.getUniqueID());
            return;
        }
    }

    @Override
    public ModularPanel buildUI(EntityGuiData data, PanelSyncManager syncManager, UISettings settings) {
        return AyanamiChatGui.build(this, data, syncManager, settings);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModularScreen createScreen(EntityGuiData data, ModularPanel panel) {
        return new ModularScreen("123technology", panel).pausesGame(false)
            .drawDarkBackground(false);
    }

    public boolean isInDialogue() {
        return dataWatcher.getWatchableObjectInt(DIALOGUE_PLAYER_WATCHER) != NO_FOCUSED_PLAYER;
    }

    /** Server-tick lease: whichever open conversation claims this NPC first owns its attention. */
    public boolean holdForConversation(Object session, EntityPlayerMP player) {
        if (worldObj.isRemote || !isEntityAlive()
            || retaliating()
            || player.worldObj != worldObj
            || !player.isEntityAlive()
            || player.getDistanceSqToEntity(this) > GAZE_RANGE_SQUARED) return false;
        EntityPlayerMP current = activeDialoguePlayer();
        if (current != null && dialogueSession != session && current != player) return false;
        if (current == null && !getNavigator().noPath() && onGround) {
            motionX = 0;
            motionZ = 0;
        }
        if (dialogueSession != session) clearBlockLook();
        eventSpeech.cancelAmbient();
        dialogueSession = session;
        dialoguePlayer = player;
        dialogueHoldUntil = worldObj.getTotalWorldTime() + 5;
        dataWatcher.updateObject(DIALOGUE_PLAYER_WATCHER, player.getEntityId());
        getNavigator().clearPathEntity();
        return true;
    }

    private EntityPlayerMP activeDialoguePlayer() {
        EntityPlayerMP player = dialoguePlayer;
        return player != null && worldObj.getTotalWorldTime() < dialogueHoldUntil
            && player.worldObj == worldObj
            && player.isEntityAlive()
            && player.getDistanceSqToEntity(this) <= GAZE_RANGE_SQUARED ? player : null;
    }

    @Override
    protected void updateAITick() {
        super.updateAITick();
        if (retaliating()) {
            updateRetaliation();
            return;
        }
        EntityPlayerMP player = activeDialoguePlayer();
        if (awakeningUntil != 0) {
            getNavigator().clearPathEntity();
            setAIMoveSpeed(0F);
            return;
        }
        if (player == null) {
            if (dialoguePlayer != null) {
                dialoguePlayer = null;
                dialogueSession = null;
                dataWatcher.updateObject(DIALOGUE_PLAYER_WATCHER, NO_FOCUSED_PLAYER);
            }
            return;
        }
        getNavigator().clearPathEntity();
        setAIMoveSpeed(0F);
        double dx = (blockLookUntil != 0 ? lookBlockX + 0.5D : player.posX) - posX;
        double dz = (blockLookUntil != 0 ? lookBlockZ + 0.5D : player.posZ) - posZ;
        if (dx * dx + dz * dz > 0.0001) {
            float desiredYaw = (float) (Math.atan2(dz, dx) * 180.0 / Math.PI) - 90F;
            rotationYaw += MathHelper.clamp_float(MathHelper.wrapAngleTo180_float(desiredYaw - rotationYaw), -18F, 18F);
        }
        if (blockLookUntil != 0) getLookHelper()
            .setLookPosition(lookBlockX + 0.5D, lookBlockY + 0.5D, lookBlockZ + 0.5D, 30F, getVerticalFaceSpeed());
        else getLookHelper().setLookPositionWithEntity(player, 24F, getVerticalFaceSpeed());
    }

    /** Server-side only, supports presentation presets, never commands or world actions. */
    public void playDialogueGesture(AyanamiAction action) {
        if (worldObj.isRemote || !isEntityAlive()
            || retaliating()
            || dialogueGestureUntil != 0
            || getAction() != AyanamiAction.STANDING) {
            return;
        }
        if (action == AyanamiAction.LOOK_AROUND && isInDialogue()) return;
        if (action == AyanamiAction.WAVE && getHeldItem() != null) return;
        int duration;
        switch (action) {
            case NOD:
                duration = 37;
                break;
            case LOOK_AROUND:
                duration = 100;
                break;
            case WAVE:
                duration = 62;
                break;
            default:
                return;
        }
        long now = worldObj.getTotalWorldTime();
        dialogueGestureUntil = now + duration;
        setAction(action, now);
    }

    public int getDownedTicksElapsed() {
        return dataWatcher.getWatchableObjectInt(DOWNED_WATCHER);
    }

    @Override
    public void onDeath(DamageSource source) {
        // This is a temporary knockdown, not a vanilla kill: keep the entity, its name, and all equipment.
        if (!worldObj.isRemote && getHealth() <= 0F) startKnockdown();
    }

    @Override
    protected void onDeathUpdate() {
        if (worldObj.isRemote) return;
        if (getDownedTicksElapsed() < 0) startKnockdown(); // Also handles mods that set health directly.
        if (pendingItemHold != null && !applyingPersonalAction) {
            settlePersonalItemHold(pendingItemHold.cancel(), "item_interrupted");
        }
        int elapsed = getDownedTicksElapsed() + 1;
        motionX = motionZ = 0D;
        if (elapsed < DOWNED_TICKS) {
            dataWatcher.updateObject(DOWNED_WATCHER, elapsed);
            return;
        }
        deathTime = 0;
        setHealth(getMaxHealth());
        extinguish();
        setAir(300);
        revivalProtection = 20;
        dataWatcher.updateObject(DOWNED_WATCHER, -1);
    }

    private void startKnockdown() {
        if (getDownedTicksElapsed() >= 0) return;
        dataWatcher.updateObject(DOWNED_WATCHER, 0);
        deathTime = 0;
        getNavigator().clearPathEntity();
        setAttackTarget(null);
        setRevengeTarget(null);
        assignedAttacker = null;
        strikeUntil = dialogueGestureUntil = awakeningUntil = 0;
        awakeningLineAt = cloneWakeLineAt = 0;
        awakeningRecipient = cloneWakeRecipient = null;
        dialoguePlayer = null;
        dialogueSession = null;
        dialogueHoldUntil = 0;
        dataWatcher.updateObject(DIALOGUE_PLAYER_WATCHER, NO_FOCUSED_PLAYER);
        dataWatcher.updateObject(FOCUSED_PLAYER_WATCHER, NO_FOCUSED_PLAYER);
        clearBlockLook();
        eventSpeech.cancelAll();
        motionX = motionZ = 0D;
        setAction(AyanamiAction.STANDING, worldObj.getTotalWorldTime());
        if (pendingItemHold != null && !applyingPersonalAction) {
            settlePersonalItemHold(pendingItemHold.cancel(), "item_interrupted");
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (getDownedTicksElapsed() >= 0 || (revivalProtection > 0 && source != DamageSource.outOfWorld)) return false;
        boolean hurt = super.attackEntityFrom(source, amount);
        if (hurt && amount > 0F
            && !worldObj.isRemote
            && isEntityAlive()
            && source.getEntity() instanceof EntityLiving attacker
            && validCombatTarget(attacker)
            && AyanamiHostileTargeting.isNaturalAttacker(attacker)
            && !validCombatTarget(assignedAttacker)) {
            startRetaliation(attacker);
        }
        return hurt;
    }

    private boolean validCombatTarget(EntityLiving target) {
        return target != null && target.worldObj == worldObj
            && target.isEntityAlive()
            && worldObj.difficultySetting != EnumDifficulty.PEACEFUL
            && worldObj.getEntityByID(target.getEntityId()) == target;
    }

    private boolean retaliating() {
        return assignedAttacker != null || strikeUntil != 0;
    }

    private void startRetaliation(EntityLiving attacker) {
        long now = worldObj.getTotalWorldTime();
        assignedAttacker = attacker;
        strikeAt = 0;
        nextAttackPath = now;
        // Finish a first-time awakening even if an enemy interrupts the conversation.
        dialoguePlayer = null;
        dialogueSession = null;
        dialogueHoldUntil = 0;
        dataWatcher.updateObject(DIALOGUE_PLAYER_WATCHER, NO_FOCUSED_PLAYER);
        dataWatcher.updateObject(FOCUSED_PLAYER_WATCHER, NO_FOCUSED_PLAYER);
        dialogueGestureUntil = 0;
        clearBlockLook();
        eventSpeech.cancelAmbient();
        getNavigator().clearPathEntity();
        if (awakeningUntil == 0 && strikeUntil == 0) setAction(AyanamiAction.STANDING, now);
    }

    private void updateRetaliation() {
        EntityLiving target = assignedAttacker;
        if (!validCombatTarget(target)) {
            assignedAttacker = null;
            strikeAt = 0;
            getNavigator().clearPathEntity();
            return;
        }
        if (awakeningUntil != 0) return;
        long now = worldObj.getTotalWorldTime();
        getLookHelper().setLookPositionWithEntity(target, 30F, getVerticalFaceSpeed());
        if (strikeUntil != 0) {
            if (strikeAt != 0 && now >= strikeAt) {
                strikeAt = 0;
                if (withinMeleeReach(target, thrusting)) strike(target, thrusting);
            }
            return;
        }
        boolean spear = hasLonginus();
        if (withinMeleeReach(target, spear)) {
            getNavigator().clearPathEntity();
            if (onGround) {
                motionX = 0D;
                motionZ = 0D;
            }
            rotationYaw = (float) Math.toDegrees(Math.atan2(target.posZ - posZ, target.posX - posX)) - 90F;
            renderYawOffset = rotationYaw;
            thrusting = spear;
            strikeAt = now + (spear ? 3 : 4);
            strikeUntil = now + (spear ? THRUST_TICKS : STRIKE_TICKS);
            setAction(spear ? AyanamiAction.THRUST : AyanamiAction.STRIKE, now);
            swingItem();
        } else if (now >= nextAttackPath) {
            nextAttackPath = now + (spear ? 5 : 10);
            // Navigator respects blocks; never teleport, cross unloaded chunks or damage through walls.
            getNavigator().tryMoveToEntityLiving(target, spear ? 1.85D : 1.15D);
        }
    }

    private boolean hasLonginus() {
        ItemStack held = getHeldItem();
        return held != null && held.getItem() instanceof ItemLonginus;
    }

    private boolean withinMeleeReach(EntityLiving target, boolean spear) {
        double reach = (spear ? 2.2D : width * 2D) + target.width * 0.5D;
        return getDistanceSqToEntity(target) <= reach * reach && getEntitySenses().canSee(target);
    }

    private void strike(EntityLiving target, boolean spear) {
        ItemStack held = getHeldItem();
        DamageSource source = DamageSource.causeMobDamage(this);
        // A Longinus thrust deals lethal damage to ordinary mobs, but still goes through Forge's damage hooks,
        // enemy immunities and the normal death/loot path. Protected targets are never force-removed.
        double damage = spear && hasLonginus() ? Math.max(40D, (target.getHealth() + target.getAbsorptionAmount()) * 4D)
            : getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue()
                + EnchantmentHelper.getEnchantmentModifierLiving(this, target);
        if (!Double.isFinite(damage) || damage <= 0D) return;
        if (spear && hasLonginus()) source.setDamageBypassesArmor();
        if (target.attackEntityFrom(source, (float) Math.min(damage, spear ? Float.MAX_VALUE : 40D)) && held != null
            && held == getHeldItem()
            && held.isItemStackDamageable()) {
            held.damageItem(1, this);
            personalInventoryRevision++;
            if (held.stackSize <= 0) setPersonalItem(0, null, null);
        }
    }

    @Override
    protected void dropEquipment(boolean recentlyHit, int looting) {
        if (pendingItemHold != null && !applyingPersonalAction) {
            settlePersonalItemHold(pendingItemHold.cancel(), "item_interrupted");
        }
        super.dropEquipment(recentlyHit, looting);
        // Vanilla has queued the drop but leaves equipment on a dying entity until it is removed.
        setPersonalItem(0, null, null);
        // Forge captures these together with vanilla equipment before firing LivingDropsEvent.
        for (int i = 1; i <= PERSONAL_SLOTS; i++) {
            ItemStack stack = personalItem(i);
            if (stack == null) continue;
            setPersonalItem(i, null, null);
            entityDropItem(stack, 0.5F);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        if (pendingItemHold != null && !applyingPersonalAction) {
            settlePersonalItemHold(pendingItemHold.cancel(), "item_interrupted");
        }
        super.writeEntityToNBT(nbt);
        nbt.setInteger(DOWNED_TAG, getDownedTicksElapsed());
        boolean temporary = dialogueGestureUntil != 0 || awakeningUntil != 0 || strikeUntil != 0;
        nbt.setString(ACTION_TAG, (temporary ? AyanamiAction.STANDING : getAction()).getId());
        nbt.setLong(ACTION_START_TICK_TAG, temporary ? 0 : getActionStartTick());
        nbt.setBoolean("AyanamiClonePodAwakened", clonePodAwakened);
        nbt.setBoolean("AyanamiLonginusAwakened", longinusAwakened);
        if (itemCustodian != null && getHeldItem() != null) {
            nbt.setLong("AyanamiItemCustodianMost", itemCustodian.getMostSignificantBits());
            nbt.setLong("AyanamiItemCustodianLeast", itemCustodian.getLeastSignificantBits());
        }
        NBTTagList savedPockets = new NBTTagList();
        for (int i = 1; i <= PERSONAL_SLOTS; i++) {
            ItemStack stack = personalItem(i);
            if (stack == null) continue;
            NBTTagCompound entry = new NBTTagCompound();
            entry.setByte("Slot", (byte) i);
            NBTTagCompound content = new NBTTagCompound();
            stack.writeToNBT(content);
            entry.setTag("Stack", content);
            UUID giver = personalCustodian(i);
            if (giver != null) {
                entry.setLong("GiverMost", giver.getMostSignificantBits());
                entry.setLong("GiverLeast", giver.getLeastSignificantBits());
            }
            savedPockets.appendTag(entry);
        }
        nbt.setTag("AyanamiPockets", savedPockets);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        deathTime = 0;
        int downed = nbt.hasKey(DOWNED_TAG) ? nbt.getInteger(DOWNED_TAG) : -1;
        dataWatcher.updateObject(DOWNED_WATCHER, Math.max(-1, Math.min(DOWNED_TICKS - 1, downed)));
        if (downed >= 0) setHealth(0F);
        setEquipmentDropChance(0, 2F);
        clonePodAwakened = nbt.getBoolean("AyanamiClonePodAwakened");
        longinusAwakened = nbt.getBoolean("AyanamiLonginusAwakened");
        itemCustodian = getHeldItem() != null && nbt.hasKey("AyanamiItemCustodianMost", 4)
            && nbt.hasKey("AyanamiItemCustodianLeast", 4)
                ? new UUID(nbt.getLong("AyanamiItemCustodianMost"), nbt.getLong("AyanamiItemCustodianLeast"))
                : null;
        Arrays.fill(pockets, null);
        Arrays.fill(pocketCustodians, null);
        NBTTagList savedPockets = nbt.getTagList("AyanamiPockets", 10);
        for (int i = 0; i < Math.min(savedPockets.tagCount(), PERSONAL_SLOTS); i++) {
            NBTTagCompound entry = savedPockets.getCompoundTagAt(i);
            int slot = entry.getByte("Slot");
            if (slot < 1 || slot > PERSONAL_SLOTS || personalItem(slot) != null || !entry.hasKey("Stack", 10)) continue;
            ItemStack stack = ItemStack.loadItemStackFromNBT(entry.getCompoundTag("Stack"));
            if (stack == null || stack.getItem() == null || stack.stackSize != 1) continue;
            UUID giver = entry.hasKey("GiverMost", 4) && entry.hasKey("GiverLeast", 4)
                ? new UUID(entry.getLong("GiverMost"), entry.getLong("GiverLeast"))
                : null;
            setPersonalItem(slot, stack, giver);
        }
        personalInventoryRevision++;
        func_110163_bv(); // Also persist when loaded from command NBT without a PersistenceRequired tag.
        setAction(AyanamiAction.fromId(nbt.getString(ACTION_TAG)), Math.max(0L, nbt.getLong(ACTION_START_TICK_TAG)));
    }

    private void setAction(AyanamiAction action, long startTick) {
        dataWatcher.updateObject(ACTION_TICK_LOW_WATCHER, (int) startTick);
        dataWatcher.updateObject(ACTION_TICK_HIGH_WATCHER, (int) (startTick >>> 32));
        dataWatcher.updateObject(ACTION_WATCHER, action.getId());
    }

    /** The normal creature AI scheduler runs this task server-side only. */
    private final class LookAtNearestPlayer extends EntityAIBase {

        private EntityPlayer target;

        private LookAtNearestPlayer() {
            setMutexBits(2); // Own the look control, without blocking the wander task's movement control.
        }

        @Override
        public boolean shouldExecute() {
            return selectTarget();
        }

        @Override
        public boolean continueExecuting() {
            return selectTarget();
        }

        private boolean selectTarget() {
            if (retaliating()) return false;
            target = activeDialoguePlayer();
            if (target != null) return true;
            double bestDistance = GAZE_RANGE_SQUARED;
            for (EntityPlayer player : worldObj.playerEntities) {
                if (!player.isEntityAlive() || player.isInvisible() || player.isPlayerSleeping()) continue;
                double distance = getDistanceSqToEntity(player);
                if (distance > bestDistance
                    || (distance == bestDistance && target != null && player.getEntityId() >= target.getEntityId()))
                    continue;
                if (!getEntitySenses().canSee(player)) continue;
                target = player;
                bestDistance = distance;
            }
            return target != null;
        }

        @Override
        public void updateTask() {
            getLookHelper().setLookPositionWithEntity(target, 10.0F, getVerticalFaceSpeed());
            dataWatcher.updateObject(FOCUSED_PLAYER_WATCHER, target.getEntityId());
        }

        @Override
        public void resetTask() {
            target = null;
            dataWatcher.updateObject(FOCUSED_PLAYER_WATCHER, NO_FOCUSED_PLAYER);
        }
    }
}
