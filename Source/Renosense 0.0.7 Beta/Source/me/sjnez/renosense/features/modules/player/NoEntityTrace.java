//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.player;

import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.features.setting.*;

public class NoEntityTrace extends Module
{
    public static NoEntityTrace INSTANCE;
    public Setting<Boolean> gapple;
    public Setting<Boolean> pickaxe;
    
    public NoEntityTrace() {
        super("NoEntityTrace", "Place and break blocks through entities.", Module.Category.PLAYER, true, false, false);
        this.gapple = (Setting<Boolean>)this.register(new Setting("Gapple", (T)true));
        this.pickaxe = (Setting<Boolean>)this.register(new Setting("Pickaxe", (T)true));
        this.setInstance();
    }
    
    private void setInstance() {
        NoEntityTrace.INSTANCE = this;
    }
    
    public static NoEntityTrace getInstance() {
        if (NoEntityTrace.INSTANCE == null) {
            NoEntityTrace.INSTANCE = new NoEntityTrace();
        }
        return NoEntityTrace.INSTANCE;
    }
}
