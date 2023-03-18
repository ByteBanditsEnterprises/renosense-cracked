//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.client;

import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.features.setting.*;
import java.awt.*;

public class Colors extends Module
{
    private static Colors INSTANCE;
    public Setting<Integer> hue;
    public Setting<Integer> sat;
    public Setting<Integer> bright;
    
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
    
    public Color getTrueColor() {
        return new Color(this.getRed(), this.getGreen(), this.getBlue());
    }
    
    public Colors() {
        super("Colors", "When color sync is on in modules, the colors will be set to these colors.", Category.CLIENT, true, false, false);
        this.hue = (Setting<Integer>)this.register(new Setting("Hue", (T)1, (T)0, (T)360, "Sets the hue of color."));
        this.sat = (Setting<Integer>)this.register(new Setting("Saturation", (T)1, (T)0, (T)100, "Sets the saturation of the color."));
        this.bright = (Setting<Integer>)this.register(new Setting("Brightness", (T)1, (T)0, (T)100, "Sets the brightness of the color."));
        this.setInstance();
    }
    
    public static Colors getInstance() {
        if (Colors.INSTANCE == null) {
            Colors.INSTANCE = new Colors();
        }
        return Colors.INSTANCE;
    }
    
    private void setInstance() {
        Colors.INSTANCE = this;
    }
    
    static {
        Colors.INSTANCE = new Colors();
    }
}
