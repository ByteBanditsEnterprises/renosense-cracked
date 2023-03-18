//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.client;

import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.features.setting.*;
import me.sjnez.renosense.*;

public class RPC extends Module
{
    public static RPC INSTANCE;
    public Setting<Mode> mode;
    public Setting<String> uberText;
    public Setting<Boolean> driving;
    public Setting<Integer> passengers;
    public Setting<Boolean> showIP;
    public Setting<String> state;
    public Setting<String> largeImageText;
    public Setting<String> smallImageText;
    public Setting<LargeImage> largeImage;
    public Setting<SmallImage> smallImage;
    public Mode lastMode;
    public LargeImage lastLargeImage;
    public SmallImage lastSmallImage;
    
    public RPC() {
        super("RPC", "Discord rich presence.", Category.CLIENT, true, false, false);
        this.mode = (Setting<Mode>)this.register(new Setting("Mode", (T)Mode.RENOSENSE, "Choose mode for rpc"));
        this.uberText = (Setting<String>)this.register(new Setting("UberText", (T)"Driving people to where they need to be.", v -> this.mode.getValue() == Mode.UBER, "Uber string"));
        this.driving = (Setting<Boolean>)this.register(new Setting("DrivingInto", (T)false, v -> this.mode.getValue() == Mode.UBER, "Driving into 'closest player'"));
        this.passengers = (Setting<Integer>)this.register(new Setting("Passengers", (T)1, (T)1, (T)5, v -> this.mode.getValue() == Mode.UBER, "How many passengers you're driving."));
        this.showIP = (Setting<Boolean>)this.register(new Setting("ShowIP", (T)true, v -> this.mode.getValue() == Mode.RENOSENSE, "Shows the server IP in your discord presence."));
        this.state = (Setting<String>)this.register(new Setting("State", (T)"RenoSense 2", v -> this.mode.getValue() == Mode.RENOSENSE, "Sets the state of the DiscordRPC."));
        this.largeImageText = (Setting<String>)this.register(new Setting("LargeImageText", (T)"discord.gg/YuQ66dGSUV", v -> this.mode.getValue() == Mode.RENOSENSE, "Sets the large image text of the DiscordRPC."));
        this.smallImageText = (Setting<String>)this.register(new Setting("SmallImageText", (T)"discord.gg/YuQ66dGSUV", v -> this.mode.getValue() == Mode.RENOSENSE, "Sets the small image text of the DiscordRPC."));
        this.largeImage = (Setting<LargeImage>)this.register(new Setting("LargeImage", (T)LargeImage.renosense, v -> this.mode.getValue() == Mode.RENOSENSE));
        this.smallImage = (Setting<SmallImage>)this.register(new Setting("SmallImage", (T)SmallImage.transparent, v -> this.mode.getValue() == Mode.RENOSENSE));
        RPC.INSTANCE = this;
    }
    
    @Override
    public String getDisplayInfo() {
        return this.mode.currentEnumName();
    }
    
    @Override
    public void onEnable() {
        DiscordPresence.start();
    }
    
    @Override
    public void onUpdate() {
        if (this.lastLargeImage != this.largeImage.getValue() || this.lastSmallImage != this.smallImage.getValue() || this.lastMode != this.mode.getValue()) {
            DiscordPresence.stop();
            DiscordPresence.start();
        }
        this.lastMode = this.mode.getValue();
        this.lastLargeImage = this.largeImage.getValue();
        this.lastSmallImage = this.smallImage.getValue();
    }
    
    @Override
    public void onDisable() {
        DiscordPresence.stop();
    }
    
    public enum LargeImage
    {
        transparent, 
        renosense;
    }
    
    public enum SmallImage
    {
        transparent, 
        renosense, 
        none;
    }
    
    public enum Mode
    {
        RENOSENSE, 
        SKYLANDERS, 
        PLAGUEINC, 
        UBER;
    }
}
