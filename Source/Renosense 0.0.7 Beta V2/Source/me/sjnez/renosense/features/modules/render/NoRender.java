//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.render;

import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.features.setting.*;
import me.sjnez.renosense.event.events.*;
import net.minecraft.network.play.server.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.client.event.*;

public class NoRender extends Module
{
    private static NoRender INSTANCE;
    public Setting<Boolean> parrot;
    public Setting<Boolean> glint;
    public Setting<Boolean> entityFire;
    public Setting<Boolean> blocks;
    public Setting<NoArmor> noArmor;
    public Setting<Skylight> skylight;
    public Setting<Boolean> advancements;
    public Setting<Boolean> hurtCam;
    public Setting<Boolean> fire;
    public Setting<Boolean> explosion;
    public Setting<Boolean> noFog;
    
    public NoRender() {
        super("NoRender", "Allows you to stop rendering stuff.", Module.Category.RENDER, true, false, false);
        this.parrot = (Setting<Boolean>)this.register(new Setting("NoParrot", (T)false, "hate parrots"));
        this.glint = (Setting<Boolean>)this.register(new Setting("NoGlint", (T)false, "Turns off glint"));
        this.entityFire = (Setting<Boolean>)this.register(new Setting("Entity Fire", (T)Boolean.FALSE, "Don't render fire on entities"));
        this.blocks = (Setting<Boolean>)this.register(new Setting("Blocks", (T)Boolean.FALSE, "Blocks"));
        this.noArmor = (Setting<NoArmor>)this.register(new Setting("NoArmor", (T)NoArmor.NONE, "Doesnt Render Armor on players."));
        this.skylight = (Setting<Skylight>)this.register(new Setting("Skylight", (T)Skylight.NONE));
        this.advancements = (Setting<Boolean>)this.register(new Setting("Advancements", (T)false));
        this.hurtCam = (Setting<Boolean>)this.register(new Setting("NoHurtCam", (T)false));
        this.fire = (Setting<Boolean>)this.register(new Setting("Fire", (T)false, "Removes the portal overlay."));
        this.explosion = (Setting<Boolean>)this.register(new Setting("Explosions", (T)false, "Removes explosions"));
        this.noFog = (Setting<Boolean>)this.register(new Setting("NoFog", (T)false, "Removes Fog"));
        this.setInstance();
    }
    
    public static NoRender getInstance() {
        if (NoRender.INSTANCE == null) {
            NoRender.INSTANCE = new NoRender();
        }
        return NoRender.INSTANCE;
    }
    
    private void setInstance() {
        NoRender.INSTANCE = this;
    }
    
    @SubscribeEvent
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketExplosion && this.explosion.getValue()) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public void fog_density(final EntityViewRenderEvent.FogDensity event) {
        if (this.noFog.getValue()) {
            event.setDensity(0.0f);
            event.setCanceled(true);
        }
    }
    
    static {
        NoRender.INSTANCE = new NoRender();
        NoRender.INSTANCE = new NoRender();
    }
    
    public enum Skylight
    {
        NONE, 
        WORLD, 
        ENTITY, 
        ALL;
    }
    
    public enum NoArmor
    {
        NONE, 
        ALL, 
        HELMET;
    }
}
