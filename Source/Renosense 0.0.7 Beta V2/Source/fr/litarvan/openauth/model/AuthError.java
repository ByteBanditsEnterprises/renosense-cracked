//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package fr.litarvan.openauth.model;

public class AuthError
{
    private String error;
    private String errorMessage;
    private String cause;
    
    public AuthError(final String error, final String errorMessage, final String cause) {
        this.error = error;
        this.errorMessage = errorMessage;
        this.cause = cause;
    }
    
    public String getError() {
        return this.error;
    }
    
    public String getErrorMessage() {
        return this.errorMessage;
    }
    
    public String getCause() {
        return this.cause;
    }
}
