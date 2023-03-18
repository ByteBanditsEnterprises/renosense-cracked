//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package fr.litarvan.openauth.microsoft;

import java.io.*;

public class MicrosoftAuthenticationException extends Exception
{
    public MicrosoftAuthenticationException(final String message) {
        super(message);
    }
    
    public MicrosoftAuthenticationException(final IOException cause) {
        super("I/O exception thrown during Microsoft HTTP requests", cause);
    }
    
    public MicrosoftAuthenticationException(final Throwable cause) {
        super(cause);
    }
}
