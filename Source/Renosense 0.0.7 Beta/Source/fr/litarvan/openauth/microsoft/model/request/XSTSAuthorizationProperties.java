//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package fr.litarvan.openauth.microsoft.model.request;

public class XSTSAuthorizationProperties
{
    private final String SandboxId;
    private final String[] UserTokens;
    
    public XSTSAuthorizationProperties(final String SandboxId, final String[] UserTokens) {
        this.SandboxId = SandboxId;
        this.UserTokens = UserTokens;
    }
    
    public String getSandboxId() {
        return this.SandboxId;
    }
    
    public String[] getUserTokens() {
        return this.UserTokens;
    }
}
