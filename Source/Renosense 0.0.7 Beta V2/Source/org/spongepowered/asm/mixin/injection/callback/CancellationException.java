//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.asm.mixin.injection.callback;

public class CancellationException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    
    public CancellationException() {
    }
    
    public CancellationException(final String message) {
        super(message);
    }
    
    public CancellationException(final Throwable cause) {
        super(cause);
    }
    
    public CancellationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
