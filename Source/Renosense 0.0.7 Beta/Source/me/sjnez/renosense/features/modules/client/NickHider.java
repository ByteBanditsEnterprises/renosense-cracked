//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.client;

import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.features.setting.*;
import me.sjnez.renosense.util.*;
import com.mojang.realmsclient.gui.*;
import me.sjnez.renosense.features.command.*;

public class NickHider extends Module
{
    public final Setting<String> NameString;
    public final Setting<Boolean> skinChanger;
    public final Setting<String> skinFile;
    public Setting<Integer> debugDelay;
    public static Timer debugTimer;
    private static NickHider instance;
    
    public NickHider() {
        super("Media", "Change your name and skin with this module. For the skin file, you must add the directory assets/minecraft/skins/skin.png in your resource pack.", Category.CLIENT, true, false, false);
        this.NameString = (Setting<String>)this.register(new Setting("Name", (T)"Name Here", "Changes your name client-sided."));
        this.skinChanger = (Setting<Boolean>)this.register(new Setting("SkinChanger", (T)false, "Changes skin. Read description of module for tutorial."));
        this.skinFile = (Setting<String>)this.register(new Setting("SkinFile", (T)"skin", "The name of the png file you want your skin to be named as."));
        this.debugDelay = (Setting<Integer>)this.register(new Setting("DebugDelay", (T)200, (T)0, (T)10000, v -> this.debug.getValue(), "Delay between messages sent."));
        NickHider.instance = this;
    }
    
    @Override
    public void onEnable() {
        Command.sendMessage(ChatFormatting.GRAY + "Success! Name succesfully changed to " + ChatFormatting.GREEN + this.NameString.getValue());
    }
    
    @Override
    public void onUpdate() {
        if (NickHider.mc.player == null) {
            this.disable();
        }
    }
    
    public static NickHider getInstance() {
        if (NickHider.instance == null) {
            NickHider.instance = new NickHider();
        }
        return NickHider.instance;
    }
    
    static {
        NickHider.debugTimer = new Timer();
    }
}
