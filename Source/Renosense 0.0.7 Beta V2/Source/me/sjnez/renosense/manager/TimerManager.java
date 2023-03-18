//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.manager;

import me.sjnez.renosense.features.*;

public class TimerManager extends Feature
{
    private float timer;
    
    public TimerManager() {
        this.timer = 1.0f;
    }
    
    public void init() {
    }
    
    public void unload() {
        this.timer = 1.0f;
    }
    
    public void update() {
    }
    
    public void setTimer(final float timer) {
        if (timer > 0.0f) {
            this.timer = timer;
        }
    }
    
    public float getTimer() {
        return this.timer;
    }
    
    public void reset() {
        this.timer = 1.0f;
    }
}
