//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.event.events;

import me.sjnez.renosense.event.*;

public class PerspectiveEvent extends EventStage
{
    private float aspect;
    
    public PerspectiveEvent(final float aspect) {
        this.aspect = aspect;
    }
    
    public float getAspect() {
        return this.aspect;
    }
    
    public void setAspect(final float aspect) {
        this.aspect = aspect;
    }
}
