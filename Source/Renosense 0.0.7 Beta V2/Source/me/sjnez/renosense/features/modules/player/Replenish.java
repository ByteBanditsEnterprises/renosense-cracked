//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.player;

import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.features.setting.*;
import me.sjnez.renosense.util.*;
import java.util.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;

public class Replenish extends Module
{
    private final Setting<Integer> delay;
    private final Setting<Integer> gapStack;
    private final Setting<Integer> xpStackAt;
    private final Setting<Integer> crystalStack;
    private final Timer timer;
    private final ArrayList<Item> Hotbar;
    
    public Replenish() {
        super("Replenish", "Replenishes your hotbar.", Module.Category.PLAYER, true, false, false);
        this.delay = (Setting<Integer>)this.register(new Setting("Delay", (T)0, (T)0, (T)10));
        this.gapStack = (Setting<Integer>)this.register(new Setting("GapStack", (T)1, (T)50, (T)64));
        this.xpStackAt = (Setting<Integer>)this.register(new Setting("XPStack", (T)1, (T)50, (T)64));
        this.crystalStack = (Setting<Integer>)this.register(new Setting("CrystalStack", (T)1, (T)50, (T)64));
        this.timer = new Timer();
        this.Hotbar = new ArrayList<Item>();
    }
    
    public void onEnable() {
        if (fullNullCheck()) {
            return;
        }
        this.Hotbar.clear();
        for (int l_I = 0; l_I < 9; ++l_I) {
            final ItemStack l_Stack = Replenish.mc.player.inventory.getStackInSlot(l_I);
            if (!l_Stack.isEmpty() && !this.Hotbar.contains(l_Stack.getItem())) {
                this.Hotbar.add(l_Stack.getItem());
            }
            else {
                this.Hotbar.add(Items.AIR);
            }
        }
    }
    
    public void onUpdate() {
        if (Replenish.mc.currentScreen != null) {
            return;
        }
        if (!this.timer.passedMs(this.delay.getValue() * 1000)) {
            return;
        }
        for (int l_I = 0; l_I < 9; ++l_I) {
            if (this.RefillSlotIfNeed(l_I)) {
                this.timer.reset();
                return;
            }
        }
    }
    
    private boolean RefillSlotIfNeed(final int p_Slot) {
        final ItemStack l_Stack = Replenish.mc.player.inventory.getStackInSlot(p_Slot);
        if (l_Stack.isEmpty() || l_Stack.getItem() == Items.AIR) {
            return false;
        }
        if (!l_Stack.isStackable()) {
            return false;
        }
        if (l_Stack.getCount() >= l_Stack.getMaxStackSize()) {
            return false;
        }
        if (l_Stack.getItem().equals(Items.GOLDEN_APPLE) && l_Stack.getCount() >= this.gapStack.getValue()) {
            return false;
        }
        if (l_Stack.getItem().equals(Items.EXPERIENCE_BOTTLE) && l_Stack.getCount() > this.xpStackAt.getValue()) {
            return false;
        }
        if (l_Stack.getItem().equals(Items.END_CRYSTAL) && l_Stack.getCount() > this.crystalStack.getValue()) {
            return false;
        }
        for (int l_I = 9; l_I < 36; ++l_I) {
            final ItemStack l_Item = Replenish.mc.player.inventory.getStackInSlot(l_I);
            if (!l_Item.isEmpty() && this.CanItemBeMergedWith(l_Stack, l_Item)) {
                Replenish.mc.playerController.windowClick(Replenish.mc.player.inventoryContainer.windowId, l_I, 0, ClickType.QUICK_MOVE, (EntityPlayer)Replenish.mc.player);
                Replenish.mc.playerController.updateController();
                return true;
            }
        }
        return false;
    }
    
    private boolean CanItemBeMergedWith(final ItemStack p_Source, final ItemStack p_Target) {
        return p_Source.getItem() == p_Target.getItem() && p_Source.getDisplayName().equals(p_Target.getDisplayName());
    }
}
