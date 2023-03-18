//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.client;

import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.features.setting.*;
import me.sjnez.renosense.features.gui.*;
import java.awt.*;
import me.sjnez.renosense.*;
import net.minecraft.client.settings.*;
import net.minecraft.client.gui.*;

public class ClickGui extends Module
{
    private static ClickGui INSTANCE;
    public Setting<Boolean> customFov;
    public Setting<Float> fov;
    public Setting<Boolean> snowing;
    public Setting<Integer> tint;
    public Setting<Integer> hue;
    public Setting<Integer> sat;
    public Setting<Integer> bright;
    public Setting<Integer> alpha;
    public Setting<Boolean> modTColor;
    public Setting<Integer> thue;
    public Setting<Integer> tsat;
    public Setting<Integer> tbright;
    public Setting<Integer> hoverAlpha1;
    public Setting<Boolean> rainbow;
    public Setting<rainbowMode> rainbowModeHud;
    public Setting<rainbowModeArray> rainbowModeA;
    public Setting<Integer> rainbowHue;
    public Setting<Float> rainbowBrightness;
    public Setting<Float> rainbowSaturation;
    private RenoSenseGui click;
    
    public ClickGui() {
        super("ClickGui", "This is the ClickGui Module. This has settings that help customize the Gui.", Category.CLIENT, true, false, false);
        this.customFov = (Setting<Boolean>)this.register(new Setting("CustomFov", (T)false, "Allows you to change the fov of your screen."));
        this.fov = (Setting<Float>)this.register(new Setting("Fov", (T)150.0f, (T)(-180.0f), (T)180.0f, v -> this.customFov.getValue(), "Changes the fov of your screen."));
        this.snowing = (Setting<Boolean>)this.register(new Setting("Snowing", (T)true, "Adds snow to the gui."));
        this.tint = (Setting<Integer>)this.register(new Setting("BackgroundTint", (T)100, (T)0, (T)100, "Changes the tint of the background when the gui is open."));
        this.hue = (Setting<Integer>)this.register(new Setting("Hue", (T)120, (T)0, (T)360, "Changes the hue of some elements of the gui."));
        this.sat = (Setting<Integer>)this.register(new Setting("Saturation", (T)100, (T)0, (T)100, "Changes the saturation of some elements of the gui."));
        this.bright = (Setting<Integer>)this.register(new Setting("Brightness", (T)66, (T)0, (T)100, "Changes the brightness of some elements of the gui."));
        this.alpha = (Setting<Integer>)this.register(new Setting("Alpha", (T)10, (T)0, (T)255, "Changes the alpha of some elements of the gui."));
        this.modTColor = (Setting<Boolean>)this.register(new Setting("ModuleTextColor", (T)true, "When modules are enabled, this will set the color of those modules in the gui to the color of TopHue, TopSaturation, and TopBrightness."));
        this.thue = (Setting<Integer>)this.register(new Setting("TopHue", (T)300, (T)0, (T)360, "Changes the hue of other elements of the gui."));
        this.tsat = (Setting<Integer>)this.register(new Setting("TopSaturation", (T)100, (T)0, (T)100, "Changes the saturation of other elements of the gui."));
        this.tbright = (Setting<Integer>)this.register(new Setting("TopBrightness", (T)33, (T)0, (T)100, "Changes the brightness of other elements of the gui."));
        this.hoverAlpha1 = (Setting<Integer>)this.register(new Setting("HoverAlpha", (T)180, (T)0, (T)255, "Changes the alpha of items that are being hovered over in the gui."));
        this.rainbow = (Setting<Boolean>)this.register(new Setting("Rainbow", (T)false, "Makes things rainbow."));
        this.rainbowModeHud = (Setting<rainbowMode>)this.register(new Setting("HRainbowMode", (T)rainbowMode.Static, v -> this.rainbow.getValue(), "Sets the mode of the rainbow."));
        this.rainbowModeA = (Setting<rainbowModeArray>)this.register(new Setting("ARainbowMode", (T)rainbowModeArray.Static, v -> this.rainbow.getValue(), "Sets the mode of the rainbow."));
        this.rainbowHue = (Setting<Integer>)this.register(new Setting("Delay", (T)240, (T)0, (T)600, v -> this.rainbow.getValue(), "Sets the delay of the rainbow."));
        this.rainbowBrightness = (Setting<Float>)this.register(new Setting("Brightness ", (T)150.0f, (T)1.0f, (T)255.0f, v -> this.rainbow.getValue(), "Sets the brightness of the rainbow"));
        this.rainbowSaturation = (Setting<Float>)this.register(new Setting("Saturation", (T)150.0f, (T)1.0f, (T)255.0f, v -> this.rainbow.getValue(), "Sets the saturation of the rainbow."));
        this.setInstance();
    }
    
    public static ClickGui getInstance() {
        if (ClickGui.INSTANCE == null) {
            ClickGui.INSTANCE = new ClickGui();
        }
        return ClickGui.INSTANCE;
    }
    
    private void setInstance() {
        ClickGui.INSTANCE = this;
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
    
    public int getTRed() {
        return new Color(this.getTColor()).getRed();
    }
    
    public int getTGreen() {
        return new Color(this.getTColor()).getGreen();
    }
    
    public int getTBlue() {
        return new Color(this.getTColor()).getBlue();
    }
    
    public int getTColor() {
        return Color.HSBtoRGB(this.thue.getValue() / 360.0f, this.tsat.getValue() / 100.0f, this.tbright.getValue() / 100.0f);
    }
    
    @Override
    public void onUpdate() {
        if (this.customFov.getValue() && !RenoSense.moduleManager.isModuleEnabled("JarvisCamera")) {
            ClickGui.mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV, (float)this.fov.getValue());
        }
    }
    
    @Override
    public void onEnable() {
        ClickGui.mc.displayGuiScreen((GuiScreen)RenoSenseGui.getClickGui());
    }
    
    @Override
    public void onTick() {
        if (!(ClickGui.mc.currentScreen instanceof RenoSenseGui)) {
            this.disable();
        }
    }
    
    static {
        ClickGui.INSTANCE = new ClickGui();
    }
    
    public enum rainbowModeArray
    {
        Static, 
        Up;
    }
    
    public enum rainbowMode
    {
        Static, 
        Sideway;
    }
}
