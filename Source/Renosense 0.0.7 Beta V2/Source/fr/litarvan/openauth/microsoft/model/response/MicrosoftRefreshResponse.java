//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package fr.litarvan.openauth.microsoft.model.response;

public class MicrosoftRefreshResponse
{
    private final String token_type;
    private final long expires_in;
    private final String scope;
    private final String access_token;
    private final String refresh_token;
    private final String user_id;
    
    public MicrosoftRefreshResponse(final String token_type, final long expires_in, final String scope, final String access_token, final String refresh_token, final String user_id) {
        this.token_type = token_type;
        this.expires_in = expires_in;
        this.scope = scope;
        this.access_token = access_token;
        this.refresh_token = refresh_token;
        this.user_id = user_id;
    }
    
    public String getTokenType() {
        return this.token_type;
    }
    
    public long getExpiresIn() {
        return this.expires_in;
    }
    
    public String getScope() {
        return this.scope;
    }
    
    public String getAccessToken() {
        return this.access_token;
    }
    
    public String getRefreshToken() {
        return this.refresh_token;
    }
    
    public String getUserId() {
        return this.user_id;
    }
}
