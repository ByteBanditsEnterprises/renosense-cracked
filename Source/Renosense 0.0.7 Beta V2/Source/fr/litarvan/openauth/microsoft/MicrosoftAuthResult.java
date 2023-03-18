//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package fr.litarvan.openauth.microsoft;

import fr.litarvan.openauth.microsoft.model.response.*;

public class MicrosoftAuthResult
{
    private final MinecraftProfile profile;
    private final String accessToken;
    private final String refreshToken;
    
    public MicrosoftAuthResult(final MinecraftProfile profile, final String accessToken, final String refreshToken) {
        this.profile = profile;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
    
    public MinecraftProfile getProfile() {
        return this.profile;
    }
    
    public String getAccessToken() {
        return this.accessToken;
    }
    
    public String getRefreshToken() {
        return this.refreshToken;
    }
}
