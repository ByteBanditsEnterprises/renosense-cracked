//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.asm.mixin.throwables;

public class MixinPrepareError extends Error
{
    private static final long serialVersionUID = 1L;
    
    public MixinPrepareError(final String message) {
        super(message);
    }
    
    public MixinPrepareError(final Throwable cause) {
        super(cause);
    }
    
    public MixinPrepareError(final String message, final Throwable cause) {
        super(message, cause);
    }
}
