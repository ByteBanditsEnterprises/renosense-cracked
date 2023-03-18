//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.render;

import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.features.setting.*;
import me.sjnez.renosense.event.events.*;

public class World extends Module
{
    public Setting<Integer> time;
    public Setting<Boolean> rain;
    
    public World() {
        super("World", "Changes time, weather", Module.Category.RENDER, true, false, false);
        this.time = (Setting<Integer>)this.register(new Setting("Time", (T)5, (T)1, (T)18000, "Changes time of day"));
        this.rain = (Setting<Boolean>)this.register(new Setting("Rain", (T)false, "Raining"));
    }
    
    public void onRender3D(final Render3DEvent event) {
        World.mc.world.setWorldTime((long)this.time.getValue());
        if (this.rain.getValue()) {
            World.mc.world.getWorldInfo().setRaining(true);
            World.mc.world.setRainStrength(1.0f);
        }
        if (!this.rain.getValue()) {
            World.mc.world.getWorldInfo().setRaining(false);
            World.mc.world.setRainStrength(0.0f);
        }
    }
}
