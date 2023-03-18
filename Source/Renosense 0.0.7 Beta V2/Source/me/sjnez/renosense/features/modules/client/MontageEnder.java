//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.client;

import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.features.setting.*;

public class MontageEnder extends Module
{
    public final Setting<Page> page;
    public final Setting<Cape> cape;
    public final Setting<String> capeFile;
    public final Setting<String> nameString;
    public final Setting<String> repName;
    public final Setting<Integer> ping;
    public final Setting<String> skinFile;
    public final Setting<skinType> skinTypeSetting;
    private static MontageEnder instance;
    
    public MontageEnder() {
        super("ModuleName", "Plays a sound when pressed", Category.CLIENT, true, true, false);
        this.page = (Setting<Page>)this.register(new Setting("Page", (T)Page.NAME, "Changes page of montage ender."));
        this.cape = (Setting<Cape>)this.register(new Setting("CapeMode", (T)Cape.MINECON, v -> this.page.getValue().equals(Page.CAPE), "Changes cape"));
        this.capeFile = (Setting<String>)this.register(new Setting("CapeFile", (T)"cape", v -> this.page.getValue().equals(Page.CAPE) && this.cape.getValue().equals(Cape.CUSTOM), "Custom Cape File"));
        this.nameString = (Setting<String>)this.register(new Setting("FirstName", (T)"EnemiesName", v -> this.page.getValue().equals(Page.NAME), "Changes peoples name client-sided."));
        this.repName = (Setting<String>)this.register(new Setting("ReplaceAsName", (T)"ReplacedName", v -> this.page.getValue().equals(Page.NAME), "Changes peoples name client-sided."));
        this.ping = (Setting<Integer>)this.register(new Setting("Ping", (T)20, (T)0, (T)400, v -> this.page.getValue().equals(Page.NAME), "Changes peoples ping client-sided."));
        this.skinFile = (Setting<String>)this.register(new Setting("SkinFile", (T)"skin", v -> this.page.getValue().equals(Page.SKIN), "The name of the png file you want their skin to be."));
        this.skinTypeSetting = (Setting<skinType>)this.register(new Setting("SkinType", (T)skinType.NORMAL, v -> this.page.getValue().equals(Page.SKIN), "Changes skin type."));
        MontageEnder.instance = this;
    }
    
    public static MontageEnder getInstance() {
        if (MontageEnder.instance == null) {
            MontageEnder.instance = new MontageEnder();
        }
        return MontageEnder.instance;
    }
    
    public enum Page
    {
        SKIN, 
        NAME, 
        CAPE;
    }
    
    public enum Cape
    {
        NONE, 
        MINECON, 
        CUSTOM;
    }
    
    public enum skinType
    {
        SLIM, 
        NORMAL;
    }
}
