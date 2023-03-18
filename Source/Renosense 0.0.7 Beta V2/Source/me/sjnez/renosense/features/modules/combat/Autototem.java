//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.combat;

import me.sjnez.renosense.features.modules.*;
import java.util.*;
import me.sjnez.renosense.features.setting.*;
import java.util.concurrent.*;
import net.minecraft.util.*;
import net.minecraft.init.*;
import net.minecraft.entity.player.*;
import net.minecraft.world.*;
import net.minecraftforge.fml.common.eventhandler.*;
import org.lwjgl.input.*;
import me.sjnez.renosense.event.events.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import java.util.function.*;
import me.sjnez.renosense.util.*;
import net.minecraft.entity.*;
import net.minecraft.inventory.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.item.*;
import net.minecraft.block.*;

public class Autototem extends Module
{
    private static Autototem instance;
    private final Queue<InventoryUtil.Task> taskList;
    private final Timer timer;
    private final Timer secondTimer;
    public Setting<Boolean> crystal;
    public Setting<Float> crystalHealth;
    public Setting<Float> crystalHoleHealth;
    public Setting<Boolean> gapple;
    public Setting<Boolean> antiGappleFail;
    public Setting<Boolean> armorCheck;
    public Setting<Integer> actions;
    public Setting<Boolean> fallDistance;
    public Setting<Float> Height;
    public Mode2 currentMode;
    public int totems;
    public int crystals;
    public int gapples;
    public int lastTotemSlot;
    public int lastGappleSlot;
    public int lastCrystalSlot;
    public int lastObbySlot;
    public int lastWebSlot;
    public boolean holdingCrystal;
    public boolean holdingTotem;
    public boolean holdingGapple;
    public boolean didSwitchThisTick;
    private boolean second;
    private boolean switchedForHealthReason;
    
    public Autototem() {
        super("AutoTotem", "Switches between Totems, Gapples, and Crystals.", Category.COMBAT, true, false, false);
        this.taskList = new ConcurrentLinkedQueue<InventoryUtil.Task>();
        this.timer = new Timer();
        this.secondTimer = new Timer();
        this.crystal = (Setting<Boolean>)this.register(new Setting("Crystal", (T)true, "Holds crystal in hand."));
        this.crystalHealth = (Setting<Float>)this.register(new Setting("CrystalHP", (T)13.0f, (T)0.1f, (T)36.0f, "HP to switch to totem."));
        this.crystalHoleHealth = (Setting<Float>)this.register(new Setting("CrystalHoleHP", (T)3.5f, (T)0.1f, (T)36.0f, "HP to switch to totem when in a hole."));
        this.gapple = (Setting<Boolean>)this.register(new Setting("Gapple", (T)true, "Holds gapple in hand."));
        this.antiGappleFail = (Setting<Boolean>)this.register(new Setting("AntiGapFail", (T)false, "Doesnt totem fail with gaps."));
        this.armorCheck = (Setting<Boolean>)this.register(new Setting("ArmorCheck", (T)true, "Armor factors in switching to totem."));
        this.actions = (Setting<Integer>)this.register(new Setting("Packets", (T)4, (T)1, (T)4, "Switches with packets."));
        this.fallDistance = (Setting<Boolean>)this.register(new Setting("FallDistance", (T)false, "Toggles whether falling will switch to totem."));
        this.Height = (Setting<Float>)this.register(new Setting("Height", (T)0.0f, (T)0.0f, (T)30.0f, v -> this.fallDistance.getValue(), "Fall distance to switch to totem."));
        this.currentMode = Mode2.TOTEMS;
        this.totems = 0;
        this.crystals = 0;
        this.gapples = 0;
        this.lastTotemSlot = -1;
        this.lastGappleSlot = -1;
        this.lastCrystalSlot = -1;
        this.lastObbySlot = -1;
        this.lastWebSlot = -1;
        this.holdingCrystal = false;
        this.holdingTotem = false;
        this.holdingGapple = false;
        this.didSwitchThisTick = false;
        this.second = false;
        this.switchedForHealthReason = false;
        Autototem.instance = this;
    }
    
    public static Autototem getInstance() {
        if (Autototem.instance == null) {
            Autototem.instance = new Autototem();
        }
        return Autototem.instance;
    }
    
    @SubscribeEvent
    public void onUpdateWalkingPlayer(final ProcessRightClickBlockEvent event) {
        if (event.hand == EnumHand.MAIN_HAND && event.stack.getItem() == Items.END_CRYSTAL && Autototem.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && Autototem.mc.objectMouseOver != null && event.pos == Autototem.mc.objectMouseOver.getBlockPos()) {
            event.setCanceled(true);
            Autototem.mc.player.setActiveHand(EnumHand.OFF_HAND);
            Autototem.mc.playerController.processRightClick((EntityPlayer)Autototem.mc.player, (World)Autototem.mc.world, EnumHand.OFF_HAND);
        }
    }
    
    @Override
    public void onUpdate() {
        if (this.timer.passedMs(50L)) {
            if (Autototem.mc.player != null && Autototem.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && Autototem.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL && Mouse.isButtonDown(1)) {
                Autototem.mc.player.setActiveHand(EnumHand.OFF_HAND);
                Autototem.mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown(1);
            }
        }
        else if (Autototem.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && Autototem.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
            Autototem.mc.gameSettings.keyBindUseItem.pressed = false;
        }
        if (nullCheck()) {
            return;
        }
        this.doOffhand();
        if (this.secondTimer.passedMs(50L) && this.second) {
            this.second = false;
            this.timer.reset();
        }
    }
    
    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (!fullNullCheck() && Autototem.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && Autototem.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL && Autototem.mc.gameSettings.keyBindUseItem.isKeyDown()) {
            if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
                final CPacketPlayerTryUseItemOnBlock packet2 = (CPacketPlayerTryUseItemOnBlock)event.getPacket();
                if (packet2.getHand() == EnumHand.MAIN_HAND) {
                    if (this.timer.passedMs(50L)) {
                        Autototem.mc.player.setActiveHand(EnumHand.OFF_HAND);
                        Autototem.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(EnumHand.OFF_HAND));
                    }
                    event.setCanceled(true);
                }
            }
            else {
                final CPacketPlayerTryUseItem packet3;
                if (event.getPacket() instanceof CPacketPlayerTryUseItem && (packet3 = (CPacketPlayerTryUseItem)event.getPacket()).getHand() == EnumHand.OFF_HAND && !this.timer.passedMs(50L)) {
                    event.setCanceled(true);
                }
            }
        }
    }
    
    @Override
    public String getDisplayInfo() {
        if (Autototem.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            return "Crystals";
        }
        if (Autototem.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            return "Totems";
        }
        if (Autototem.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE) {
            return "Gapples";
        }
        return null;
    }
    
    public void doOffhand() {
        this.didSwitchThisTick = false;
        this.holdingCrystal = (Autototem.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL);
        this.holdingTotem = (Autototem.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING);
        this.holdingGapple = (Autototem.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE);
        this.totems = Autototem.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        if (this.holdingTotem) {
            this.totems += Autototem.mc.player.inventory.offHandInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        }
        this.crystals = Autototem.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.END_CRYSTAL).mapToInt(ItemStack::getCount).sum();
        if (this.holdingCrystal) {
            this.crystals += Autototem.mc.player.inventory.offHandInventory.stream().filter(itemStack -> itemStack.getItem() == Items.END_CRYSTAL).mapToInt(ItemStack::getCount).sum();
        }
        this.gapples = Autototem.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.GOLDEN_APPLE).mapToInt(ItemStack::getCount).sum();
        if (this.holdingGapple) {
            this.gapples += Autototem.mc.player.inventory.offHandInventory.stream().filter(itemStack -> itemStack.getItem() == Items.GOLDEN_APPLE).mapToInt(ItemStack::getCount).sum();
        }
        this.doSwitch();
    }
    
    public void doSwitch() {
        this.currentMode = Mode2.TOTEMS;
        if (this.gapple.getValue() && Autototem.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && Autototem.mc.gameSettings.keyBindUseItem.isKeyDown()) {
            this.currentMode = Mode2.GAPPLES;
        }
        else if (this.currentMode != Mode2.CRYSTALS && this.crystal.getValue() && ((EntityUtil.isSafe((Entity)Autototem.mc.player) && EntityUtil.getHealth((Entity)Autototem.mc.player, true) > this.crystalHoleHealth.getValue()) || EntityUtil.getHealth((Entity)Autototem.mc.player, true) > this.crystalHealth.getValue())) {
            this.currentMode = Mode2.CRYSTALS;
        }
        if (this.antiGappleFail.getValue() && this.currentMode == Mode2.GAPPLES && ((!EntityUtil.isSafe((Entity)Autototem.mc.player) && EntityUtil.getHealth((Entity)Autototem.mc.player, true) <= this.crystalHealth.getValue()) || EntityUtil.getHealth((Entity)Autototem.mc.player, true) <= this.crystalHoleHealth.getValue())) {
            this.switchedForHealthReason = true;
            this.setMode(Mode2.TOTEMS);
        }
        if (this.currentMode == Mode2.CRYSTALS && this.crystals == 0) {
            this.setMode(Mode2.TOTEMS);
        }
        if (this.currentMode == Mode2.CRYSTALS && ((!EntityUtil.isSafe((Entity)Autototem.mc.player) && EntityUtil.getHealth((Entity)Autototem.mc.player, true) <= this.crystalHealth.getValue()) || EntityUtil.getHealth((Entity)Autototem.mc.player, true) <= this.crystalHoleHealth.getValue())) {
            if (this.currentMode == Mode2.CRYSTALS) {
                this.switchedForHealthReason = true;
            }
            this.setMode(Mode2.TOTEMS);
        }
        if (this.switchedForHealthReason && ((EntityUtil.isSafe((Entity)Autototem.mc.player) && EntityUtil.getHealth((Entity)Autototem.mc.player, true) > this.crystalHoleHealth.getValue()) || EntityUtil.getHealth((Entity)Autototem.mc.player, true) > this.crystalHealth.getValue())) {
            this.setMode(Mode2.CRYSTALS);
            this.switchedForHealthReason = false;
        }
        if (this.currentMode == Mode2.CRYSTALS && this.armorCheck.getValue() && (Autototem.mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.AIR || Autototem.mc.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == Items.AIR || Autototem.mc.player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() == Items.AIR || Autototem.mc.player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() == Items.AIR)) {
            this.setMode(Mode2.TOTEMS);
        }
        if ((this.currentMode == Mode2.CRYSTALS || this.currentMode == Mode2.GAPPLES) && Autototem.mc.player.fallDistance > this.Height.getValue() && this.fallDistance.getValue()) {
            this.setMode(Mode2.TOTEMS);
        }
        if (Autototem.mc.currentScreen instanceof GuiContainer && !(Autototem.mc.currentScreen instanceof GuiInventory)) {
            return;
        }
        final Item currentOffhandItem = Autototem.mc.player.getHeldItemOffhand().getItem();
        switch (this.currentMode) {
            case TOTEMS: {
                if (this.totems <= 0) {
                    break;
                }
                if (this.holdingTotem) {
                    break;
                }
                this.lastTotemSlot = InventoryUtil.findItemInventorySlot(Items.TOTEM_OF_UNDYING, false);
                final int lastSlot = this.getLastSlot(currentOffhandItem, this.lastTotemSlot);
                this.putItemInOffhand(this.lastTotemSlot, lastSlot);
                break;
            }
            case GAPPLES: {
                if (this.gapples <= 0) {
                    break;
                }
                if (this.holdingGapple) {
                    break;
                }
                this.lastGappleSlot = InventoryUtil.findItemInventorySlot(Items.GOLDEN_APPLE, false);
                final int lastSlot = this.getLastSlot(currentOffhandItem, this.lastGappleSlot);
                this.putItemInOffhand(this.lastGappleSlot, lastSlot);
                break;
            }
            default: {
                if (this.crystals <= 0) {
                    break;
                }
                if (this.holdingCrystal) {
                    break;
                }
                this.lastCrystalSlot = InventoryUtil.findItemInventorySlot(Items.END_CRYSTAL, false);
                final int lastSlot = this.getLastSlot(currentOffhandItem, this.lastCrystalSlot);
                this.putItemInOffhand(this.lastCrystalSlot, lastSlot);
                break;
            }
        }
        for (int i = 0; i < this.actions.getValue(); ++i) {
            final InventoryUtil.Task task = this.taskList.poll();
            if (task != null) {
                task.run();
                if (task.isSwitching()) {
                    this.didSwitchThisTick = true;
                }
            }
        }
    }
    
    private int getLastSlot(final Item item, final int slotIn) {
        if (item == Items.END_CRYSTAL) {
            return this.lastCrystalSlot;
        }
        if (item == Items.GOLDEN_APPLE) {
            return this.lastGappleSlot;
        }
        if (item == Items.TOTEM_OF_UNDYING) {
            return this.lastTotemSlot;
        }
        if (InventoryUtil.isBlock(item, BlockObsidian.class)) {
            return this.lastObbySlot;
        }
        if (InventoryUtil.isBlock(item, BlockWeb.class)) {
            return this.lastWebSlot;
        }
        if (item == Items.AIR) {
            return -1;
        }
        return slotIn;
    }
    
    private void putItemInOffhand(final int slotIn, final int slotOut) {
        if (slotIn != -1 && this.taskList.isEmpty()) {
            this.taskList.add(new InventoryUtil.Task(slotIn));
            this.taskList.add(new InventoryUtil.Task(45));
            this.taskList.add(new InventoryUtil.Task(slotOut));
            this.taskList.add(new InventoryUtil.Task());
        }
    }
    
    public void setMode(final Mode2 mode) {
        this.currentMode = ((this.currentMode == mode) ? Mode2.TOTEMS : mode);
    }
    
    public enum Mode2
    {
        TOTEMS, 
        GAPPLES, 
        CRYSTALS;
    }
    
    public enum HandMode
    {
        OFFHAND, 
        MAINHAND;
    }
}
