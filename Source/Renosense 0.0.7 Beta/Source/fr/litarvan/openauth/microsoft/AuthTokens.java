//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package fr.litarvan.openauth.microsoft;

public class AuthTokens
{
    private final String accessToken;
    private final String refreshToken;
    
    public AuthTokens(final String accessToken, final String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
    
    public String getAccessToken() {
        return this.accessToken;
    }
    
    public String getRefreshToken() {
        return this.refreshToken;
    }
}
