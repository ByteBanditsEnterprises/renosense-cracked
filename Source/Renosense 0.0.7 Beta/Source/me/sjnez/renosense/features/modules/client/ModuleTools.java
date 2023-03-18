//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.client;

import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.features.setting.*;

public class ModuleTools extends Module
{
    private static ModuleTools INSTANCE;
    public Setting<Notifier> notifier;
    
    public ModuleTools() {
        super("ModuleTools", "Change settings for Pop Notifier and Module Notifier.", Category.CLIENT, true, false, false);
        this.notifier = (Setting<Notifier>)this.register(new Setting("ModuleNotifier", (T)Notifier.FUTURE, "Modes: Future, Dotgod, Phobos, Trollgod. Changes the style of chat notify for modules toggling on and off."));
        ModuleTools.INSTANCE = this;
    }
    
    public static ModuleTools getInstance() {
        if (ModuleTools.INSTANCE == null) {
            ModuleTools.INSTANCE = new ModuleTools();
        }
        return ModuleTools.INSTANCE;
    }
    
    public enum Notifier
    {
        TROLLGOD, 
        PHOBOS, 
        FUTURE, 
        DOTGOD;
    }
}
