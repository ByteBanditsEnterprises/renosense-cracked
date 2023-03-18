//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.player;

import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.features.setting.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import me.sjnez.renosense.util.*;
import net.minecraft.init.*;
import net.minecraft.util.math.*;

public class AntiAnvil extends Module
{
    private static AntiAnvil instance;
    public final Setting<Integer> range;
    public final Setting<Integer> time;
    public static boolean hasTakenOff;
    public static Timer timer;
    
    public AntiAnvil() {
        super("AntiAnvil", "Takes off helmet when an anvil is placed above you.", Module.Category.PLAYER, true, false, false);
        this.range = (Setting<Integer>)this.register(new Setting("Range", (T)20, (T)0, (T)100, "Range for the module to search for an anvil above you."));
        this.time = (Setting<Integer>)this.register(new Setting("Delay", (T)5, (T)0, (T)10, "Delay until the helmet is put back on."));
        AntiAnvil.instance = this;
    }
    
    public static AntiAnvil getInstance() {
        if (AntiAnvil.instance == null) {
            AntiAnvil.instance = new AntiAnvil();
        }
        return AntiAnvil.instance;
    }
    
    public void onUpdate() {
        final float anvil = this.anvil();
        if (anvil != -1.0f) {
            if (!AntiAnvil.mc.player.inventory.getStackInSlot(39).isEmpty()) {
                final int slot = InventoryUtil.findItemInventorySlot(Items.AIR, false);
                if (slot != -1) {
                    AntiAnvil.timer.reset();
                    AntiAnvil.mc.playerController.windowClick(AntiAnvil.mc.player.inventoryContainer.windowId, 5, 0, ClickType.QUICK_MOVE, (EntityPlayer)AntiAnvil.mc.player);
                    AntiAnvil.hasTakenOff = true;
                }
            }
        }
        else if (AntiAnvil.timer.passedMs(this.time.getValue() * 1000) && AntiAnvil.hasTakenOff) {
            if (AntiAnvil.mc.player.inventory.getStackInSlot(39).isEmpty()) {
                final int slot = InventoryUtil.findItemInventorySlot((Item)Items.DIAMOND_HELMET, false);
                if (slot != -1) {
                    AntiAnvil.mc.playerController.windowClick(AntiAnvil.mc.player.inventoryContainer.windowId, slot, 0, ClickType.QUICK_MOVE, (EntityPlayer)AntiAnvil.mc.player);
                }
            }
            AntiAnvil.hasTakenOff = false;
        }
    }
    
    public float anvil() {
        for (int bound = this.range.getValue(), i = 2; i < bound; ++i) {
            final BlockPos pos = BlockUtil.getPlayerPos().add(0, i, 0);
            if (AntiAnvil.mc.world.getBlockState(pos).getBlock().equals(Blocks.ANVIL)) {
                return (float)i;
            }
        }
        return -1.0f;
    }
    
    static {
        AntiAnvil.timer = new Timer();
    }
}
