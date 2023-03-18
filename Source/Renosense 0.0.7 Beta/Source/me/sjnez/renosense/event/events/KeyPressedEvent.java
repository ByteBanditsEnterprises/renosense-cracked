//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.event.events;

import me.sjnez.renosense.event.*;

public class KeyPressedEvent extends EventStage
{
    public boolean info;
    public boolean pressed;
    
    public KeyPressedEvent(final boolean info, final boolean pressed) {
        this.info = info;
        this.pressed = pressed;
    }
}
