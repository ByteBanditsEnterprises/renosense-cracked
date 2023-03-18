//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package fr.litarvan.openauth;

import fr.litarvan.openauth.model.*;

public class AuthenticationException extends Exception
{
    private AuthError model;
    
    public AuthenticationException(final AuthError model) {
        super(model.getErrorMessage());
        this.model = model;
    }
    
    public AuthError getErrorModel() {
        return this.model;
    }
}
