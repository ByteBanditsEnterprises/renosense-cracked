//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.render;

import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.features.setting.*;
import net.minecraft.util.*;
import net.minecraft.item.*;
import me.sjnez.renosense.event.events.*;
import net.minecraft.network.play.client.*;
import net.minecraftforge.fml.common.eventhandler.*;

public class Swing extends Module
{
    public Setting<Hand> hand;
    public Setting<Boolean> slowSwing;
    private static Swing INSTANCE;
    
    public Swing() {
        super("Swing", "Changes the hand you swing with.", Module.Category.RENDER, true, false, false);
        this.hand = (Setting<Hand>)this.register(new Setting("Mode", (T)Hand.Offhand));
        this.slowSwing = (Setting<Boolean>)this.register(new Setting("SlowSwing", (T)false));
        this.setInstance();
    }
    
    public static Swing getINSTANCE() {
        if (Swing.INSTANCE == null) {
            Swing.INSTANCE = new Swing();
        }
        return Swing.INSTANCE;
    }
    
    private void setInstance() {
        Swing.INSTANCE = this;
    }
    
    public String getDisplayInfo() {
        final String ModeInfo = String.valueOf(this.hand.getValue());
        return ModeInfo;
    }
    
    public void onUpdate() {
        if (Swing.mc.world == null) {
            return;
        }
        if (this.hand.getValue().equals(Hand.Offhand)) {
            Swing.mc.player.swingingHand = EnumHand.OFF_HAND;
        }
        else if (this.hand.getValue().equals(Hand.Mainhand)) {
            Swing.mc.player.swingingHand = EnumHand.MAIN_HAND;
        }
        else if (this.hand.getValue().equals(Hand.Cancel) && Swing.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && Swing.mc.entityRenderer.itemRenderer.prevEquippedProgressMainHand >= 0.9) {
            Swing.mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1.0f;
            Swing.mc.entityRenderer.itemRenderer.itemStackMainHand = Swing.mc.player.getHeldItemMainhand();
        }
    }
    
    @SubscribeEvent
    public void onPacket(final Packet event) {
        if (Module.nullCheck() || event.getPacket() == Packet.Type.INCOMING) {
            return;
        }
        if (event.getPacket() instanceof CPacketAnimation) {
            event.setCanceled(true);
        }
    }
    
    static {
        Swing.INSTANCE = new Swing();
    }
    
    public enum Hand
    {
        Offhand, 
        Mainhand, 
        Cancel;
    }
}
