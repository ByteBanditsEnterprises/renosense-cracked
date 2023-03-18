//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.zero.alpine.event.type;

public class Cancellable implements ICancellable
{
    private boolean cancelled;
    
    @Override
    public void cancel() {
        this.cancelled = true;
    }
    
    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
}
