//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package fr.litarvan.openauth.microsoft.model.response;

public class MinecraftLoginResponse
{
    private final String username;
    private final String access_token;
    private final String token_type;
    private final long expires_in;
    
    public MinecraftLoginResponse(final String username, final String access_token, final String token_type, final long expires_in) {
        this.username = username;
        this.access_token = access_token;
        this.token_type = token_type;
        this.expires_in = expires_in;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public String getAccessToken() {
        return this.access_token;
    }
    
    public String getTokenType() {
        return this.token_type;
    }
    
    public long getExpiresIn() {
        return this.expires_in;
    }
}
