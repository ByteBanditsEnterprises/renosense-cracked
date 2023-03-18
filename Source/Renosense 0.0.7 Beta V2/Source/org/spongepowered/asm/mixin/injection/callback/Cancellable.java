//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.asm.mixin.injection.callback;

public interface Cancellable
{
    boolean isCancellable();
    
    boolean isCancelled();
    
    void cancel() throws CancellationException;
}
