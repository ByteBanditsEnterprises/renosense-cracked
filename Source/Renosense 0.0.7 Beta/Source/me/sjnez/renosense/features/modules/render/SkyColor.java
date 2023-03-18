//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.render;

import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.features.setting.*;
import java.awt.*;
import net.minecraftforge.client.event.*;
import me.sjnez.renosense.features.modules.client.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.common.*;

public class SkyColor extends Module
{
    private Setting<Integer> hue;
    private Setting<Integer> sat;
    private Setting<Integer> bright;
    private Setting<Boolean> fog;
    public Setting<Boolean> colorSync;
    private static SkyColor INSTANCE;
    
    public SkyColor() {
        super("SkyColor", "Changes the color of the sky.", Module.Category.RENDER, true, false, false);
        this.hue = (Setting<Integer>)this.register(new Setting("Hue", (T)300, (T)0, (T)360));
        this.sat = (Setting<Integer>)this.register(new Setting("Saturation", (T)100, (T)0, (T)100));
        this.bright = (Setting<Integer>)this.register(new Setting("Brightness", (T)33, (T)0, (T)100));
        this.fog = (Setting<Boolean>)this.register(new Setting("Fog", (T)true));
        this.colorSync = (Setting<Boolean>)this.register(new Setting("Color Sync", (T)false));
    }
    
    private void setInstance() {
        SkyColor.INSTANCE = this;
    }
    
    public static SkyColor getInstance() {
        if (SkyColor.INSTANCE == null) {
            SkyColor.INSTANCE = new SkyColor();
        }
        return SkyColor.INSTANCE;
    }
    
    public int getRed() {
        return new Color(this.getColor()).getRed();
    }
    
    public int getGreen() {
        return new Color(this.getColor()).getGreen();
    }
    
    public int getBlue() {
        return new Color(this.getColor()).getBlue();
    }
    
    public int getColor() {
        return Color.HSBtoRGB(this.hue.getValue() / 360.0f, this.sat.getValue() / 100.0f, this.bright.getValue() / 100.0f);
    }
    
    @SubscribeEvent
    public void fogColors(final EntityViewRenderEvent.FogColors event) {
        if (this.colorSync.getValue()) {
            event.setRed(Colors.getInstance().getRed() / 255.0f);
        }
        else {
            event.setRed(this.getRed() / 255.0f);
        }
        if (this.colorSync.getValue()) {
            event.setGreen(Colors.getInstance().getGreen() / 255.0f);
        }
        else {
            event.setGreen(this.getGreen() / 255.0f);
        }
        if (this.colorSync.getValue()) {
            event.setBlue(Colors.getInstance().getBlue() / 255.0f);
        }
        else {
            event.setBlue(this.getBlue() / 255.0f);
        }
    }
    
    @SubscribeEvent
    public void fog_density(final EntityViewRenderEvent.FogDensity event) {
        if (this.fog.getValue()) {
            event.setDensity(0.0f);
            event.setCanceled(true);
        }
    }
    
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register((Object)this);
    }
    
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister((Object)this);
    }
    
    static {
        SkyColor.INSTANCE = new SkyColor();
    }
}
