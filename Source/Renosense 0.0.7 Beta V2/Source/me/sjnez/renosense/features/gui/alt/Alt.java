//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.gui.alt;

import net.minecraft.util.*;
import java.util.*;
import me.sjnez.renosense.mixin.mixins.*;
import net.minecraft.client.*;
import fr.litarvan.openauth.microsoft.*;

public class Alt
{
    private final String login;
    private final String password;
    private final AltType altType;
    private Session altSession;
    
    public Alt(final String altLogin, final String altPassword, final AltType altType) {
        this.login = altLogin;
        this.password = altPassword;
        this.altType = altType;
    }
    
    public void login() {
        try {
            if (this.altSession == null) {
                switch (this.getAltType()) {
                    case MICROSOFT: {
                        final MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
                        try {
                            final MicrosoftAuthResult result = authenticator.loginWithCredentials(this.login, this.password);
                            this.altSession = new Session(result.getProfile().getName(), result.getProfile().getId(), result.getAccessToken(), "legacy");
                        }
                        catch (MicrosoftAuthenticationException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case CRACKED: {
                        this.altSession = new Session(this.getLogin(), UUID.randomUUID().toString(), "", "legacy");
                        break;
                    }
                }
            }
            if (this.altSession != null) {
                ((IMinecraft)Minecraft.getMinecraft()).setSession(this.altSession);
            }
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
    }
    
    public AltType getAltType() {
        return this.altType;
    }
    
    public String getLogin() {
        return this.login;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public Session getAltSession() {
        return this.altSession;
    }
    
    public enum AltType
    {
        MICROSOFT, 
        CRACKED;
    }
}
