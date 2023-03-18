//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package fr.litarvan.openauth.microsoft.model.request;

public class MinecraftLoginRequest
{
    private final String identityToken;
    
    public MinecraftLoginRequest(final String identityToken) {
        this.identityToken = identityToken;
    }
    
    public String getIdentityToken() {
        return this.identityToken;
    }
}
