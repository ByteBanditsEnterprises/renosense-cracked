//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package fr.litarvan.openauth.model;

public class AuthProfile
{
    private String name;
    private String id;
    
    public AuthProfile() {
        this.name = "";
        this.id = "";
    }
    
    public AuthProfile(final String name, final String id) {
        this.name = name;
        this.id = id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getId() {
        return this.id;
    }
}
