//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.render;

import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.features.setting.*;
import me.sjnez.renosense.event.events.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.client.event.*;

public class Chams extends Module
{
    private static Chams INSTANCE;
    private final Setting<Page> page;
    public Setting<Boolean> players;
    public Setting<RenderMode> mode;
    public Setting<Boolean> playerModel;
    public Setting<Boolean> lol;
    public Setting<Boolean> sneak;
    public Setting<Boolean> lagChams;
    public Setting<Integer> red;
    public Setting<Integer> green;
    public Setting<Integer> blue;
    public Setting<Integer> red1;
    public Setting<Integer> green1;
    public Setting<Integer> blue1;
    public final Setting<Float> alpha;
    public final Setting<Float> lineWidth;
    public Setting<Boolean> rainbow;
    public Setting<Integer> rainbowHue;
    public Setting<Boolean> colorSync;
    
    public Chams() {
        super("Chams", "Draws a pretty ESP around other players.", Module.Category.RENDER, true, false, false);
        this.colorSync = (Setting<Boolean>)this.register(new Setting("Color Sync", (T)false));
        this.page = (Setting<Page>)this.register(new Setting("Settings", (T)Page.GLOBAL));
        this.players = (Setting<Boolean>)this.register(new Setting("Render", (T)Boolean.TRUE, v -> this.page.getValue() == Page.GLOBAL));
        this.mode = (Setting<RenderMode>)this.register(new Setting("Mode", (T)RenderMode.Wireframe, v -> this.players.getValue() && this.page.getValue() == Page.GLOBAL));
        this.playerModel = (Setting<Boolean>)this.register(new Setting("PlayerModel", (T)true, v -> this.page.getValue() == Page.GLOBAL));
        this.lol = (Setting<Boolean>)this.register(new Setting("Freeze", (T)false, v -> this.page.getValue() == Page.GLOBAL));
        this.sneak = (Setting<Boolean>)this.register(new Setting("Sneak", (T)false, v -> this.page.getValue() == Page.GLOBAL));
        this.lagChams = (Setting<Boolean>)this.register(new Setting("LagChams", (T)true, v -> this.page.getValue() == Page.GLOBAL));
        this.red = (Setting<Integer>)this.register(new Setting("Red", (T)255, (T)0, (T)255, v -> this.players.getValue() && this.page.getValue() == Page.COLORS));
        this.green = (Setting<Integer>)this.register(new Setting("Green", (T)255, (T)0, (T)255, v -> this.players.getValue() && this.page.getValue() == Page.COLORS));
        this.blue = (Setting<Integer>)this.register(new Setting("Blue", (T)255, (T)0, (T)255, v -> this.players.getValue() && this.page.getValue() == Page.COLORS));
        this.red1 = (Setting<Integer>)this.register(new Setting("WireframeRed", (T)255, (T)0, (T)255, v -> this.players.getValue() && this.page.getValue() == Page.COLORS));
        this.green1 = (Setting<Integer>)this.register(new Setting("WireframeGreen", (T)255, (T)0, (T)255, v -> this.players.getValue() && this.page.getValue() == Page.COLORS));
        this.blue1 = (Setting<Integer>)this.register(new Setting("WireframeBlue", (T)255, (T)0, (T)255, v -> this.players.getValue() && this.page.getValue() == Page.COLORS));
        this.alpha = (Setting<Float>)this.register(new Setting("Alpha", (T)80.0f, (T)0.1f, (T)255.0f, v -> this.players.getValue() && this.page.getValue() == Page.COLORS));
        this.lineWidth = (Setting<Float>)this.register(new Setting("LineWidth", (T)1.0f, (T)0.1f, (T)3.0f, v -> this.players.getValue() && this.page.getValue() == Page.COLORS));
        this.rainbow = (Setting<Boolean>)this.register(new Setting("Rainbow", (T)Boolean.FALSE, v -> this.page.getValue() == Page.COLORS));
        this.rainbowHue = (Setting<Integer>)this.register(new Setting("Brightness", (T)100, (T)0, (T)600, v -> this.rainbow.getValue() && this.page.getValue() == Page.COLORS));
        this.setInstance();
    }
    
    public static Chams getInstance() {
        if (Chams.INSTANCE == null) {
            Chams.INSTANCE = new Chams();
        }
        return Chams.INSTANCE;
    }
    
    private void setInstance() {
        Chams.INSTANCE = this;
    }
    
    public String getDisplayInfo() {
        final String ModeInfo = String.valueOf(this.mode.getValue());
        return ModeInfo;
    }
    
    @SubscribeEvent
    public void onInterpolate(final InterpolateEvent event) {
        if (this.lagChams.getValue()) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public void onRenderPlayerEvent(final RenderPlayerEvent.Pre event) {
        event.getEntityPlayer().hurtTime = 0;
    }
    
    static {
        Chams.INSTANCE = new Chams();
    }
    
    public enum RenderMode
    {
        Solid, 
        Wireframe, 
        Both;
    }
    
    public enum Page
    {
        COLORS, 
        GLOBAL;
    }
}
