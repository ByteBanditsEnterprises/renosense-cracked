//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.combat;

import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.features.setting.*;
import net.minecraft.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.util.text.*;
import me.sjnez.renosense.features.command.*;

public class FakeKick extends Module
{
    private final Setting<Boolean> healthDisplay;
    
    public FakeKick() {
        super("FakeKick", "Log with the press of a button.", Category.COMBAT, true, false, false);
        this.healthDisplay = (Setting<Boolean>)this.register(new Setting("HealthDisplay", (T)false, "Displays health on disconnet gui."));
    }
    
    @Override
    public void onEnable() {
        if (!FakeKick.mc.isSingleplayer()) {
            if (this.healthDisplay.getValue()) {
                final float health = FakeKick.mc.player.getAbsorptionAmount() + FakeKick.mc.player.getHealth();
                Minecraft.getMinecraft().getConnection().handleDisconnect(new SPacketDisconnect((ITextComponent)new TextComponentString("Logged out with " + health + " health remaining.")));
                this.disable();
            }
            Minecraft.getMinecraft().getConnection().handleDisconnect(new SPacketDisconnect((ITextComponent)new TextComponentString("Internal Exception: java.lang.NullPointerException")));
            this.disable();
        }
        Command.sendMessage("Cannot fake kick in single player lil bro.");
        this.disable();
    }
}
