//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package fr.litarvan.openauth.microsoft.model.request;

public class XboxLoginRequest<T>
{
    private final T Properties;
    private final String RelyingParty;
    private final String TokenType;
    
    public XboxLoginRequest(final T Properties, final String RelyingParty, final String TokenType) {
        this.Properties = Properties;
        this.RelyingParty = RelyingParty;
        this.TokenType = TokenType;
    }
    
    public T getProperties() {
        return this.Properties;
    }
    
    public String getSiteName() {
        return this.RelyingParty;
    }
    
    public String getTokenType() {
        return this.TokenType;
    }
}
