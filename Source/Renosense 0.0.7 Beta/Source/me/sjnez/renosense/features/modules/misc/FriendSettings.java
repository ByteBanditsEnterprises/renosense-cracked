//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.misc;

import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.features.setting.*;

public class FriendSettings extends Module
{
    private static FriendSettings INSTANCE;
    public Setting<Boolean> notify;
    public Setting<Boolean> inTab;
    
    public FriendSettings() {
        super("FriendSettings", "When Notify is on, it sends a message the person you add as a friend on the client.", Category.MISC, true, false, false);
        this.notify = (Setting<Boolean>)this.register(new Setting("Notify", (T)false));
        this.inTab = (Setting<Boolean>)this.register(new Setting("InTab", (T)true));
        FriendSettings.INSTANCE = this;
    }
    
    public static FriendSettings getInstance() {
        if (FriendSettings.INSTANCE == null) {
            FriendSettings.INSTANCE = new FriendSettings();
        }
        return FriendSettings.INSTANCE;
    }
}
