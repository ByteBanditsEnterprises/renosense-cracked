//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.render;

import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.features.setting.*;
import me.sjnez.renosense.event.events.*;
import me.sjnez.renosense.features.modules.client.*;
import java.awt.*;
import me.sjnez.renosense.util.shaders.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.*;
import java.util.*;

public class ShaderChams extends Module
{
    public Setting<mode> shaderMode;
    public Setting<Integer> speed;
    public Setting<Integer> opacity;
    private static ShaderChams INSTANCE;
    
    public ShaderChams() {
        super("ShaderChams", "Shades the chams man.", Module.Category.RENDER, true, false, false);
        this.shaderMode = (Setting<mode>)this.register(new Setting("ShaderMode", (T)mode.BLUE_ABSTRACT, "Page of HUD."));
        this.speed = (Setting<Integer>)this.register(new Setting("Speed", (T)5, (T)1, (T)10, "Speed"));
        this.opacity = (Setting<Integer>)this.register(new Setting("Opacity", (T)10, (T)1, (T)10, "Opacity"));
        this.setInstance();
    }
    
    public static ShaderChams getInstance() {
        if (ShaderChams.INSTANCE == null) {
            ShaderChams.INSTANCE = new ShaderChams();
        }
        return ShaderChams.INSTANCE;
    }
    
    private void setInstance() {
        ShaderChams.INSTANCE = this;
    }
    
    public void onRender3D(final Render3DEvent event) {
        if (fullNullCheck()) {
            this.disable();
            return;
        }
        switch (this.shaderMode.getValue()) {
            case RIZZ: {
                PhantomStarShader.setup(this.speed.getValue() / 100.0f);
                break;
            }
            case VORTEX: {
                VortexShader.setup(this.speed.getValue() / 100.0f);
                break;
            }
            case DARKMATTER: {
                DarkMatterShader.setup(this.speed.getValue() / 190.0f);
                break;
            }
            case HYPNOTIZE: {
                HypnoShader.setup(this.speed.getValue() / 100.0f);
                break;
            }
            case BLUE_ABSTRACT: {
                ScottShader.setup(this.speed.getValue() / 150.0f);
                break;
            }
            case GRADIENT_SHADER: {
                GradientShader.setup(0.2f, this.speed.getValue() / 10.0f, Colors.getInstance().getTrueColor(), Color.MAGENTA);
                break;
            }
            case PINK_LEMONADE: {
                PinkLemonadeShader.setup(this.speed.getValue() / 50.0f);
                break;
            }
            case STAR_SHADER: {
                StarShader.setup(this.speed.getValue() / 140.0f);
                break;
            }
        }
        for (final EntityPlayer entityPlayer : ShaderChams.mc.world.playerEntities) {
            if (entityPlayer == null) {
                continue;
            }
            if (entityPlayer.equals((Object)ShaderChams.mc.player)) {
                continue;
            }
            if (entityPlayer.equals((Object)JarvisCamera.renderer)) {
                continue;
            }
            ShaderChams.mc.getRenderManager().renderEntityStatic((Entity)entityPlayer, event.getPartialTicks(), true);
        }
        switch (this.shaderMode.getValue()) {
            case RIZZ: {
                PhantomStarShader.finish();
                break;
            }
            case VORTEX: {
                VortexShader.finish();
                break;
            }
            case DARKMATTER: {
                DarkMatterShader.finish();
                break;
            }
            case HYPNOTIZE: {
                HypnoShader.finish();
                break;
            }
            case BLUE_ABSTRACT: {
                ScottShader.finish();
                break;
            }
            case GRADIENT_SHADER: {
                GradientShader.finish();
                break;
            }
            case PINK_LEMONADE: {
                PinkLemonadeShader.finish();
                break;
            }
            case STAR_SHADER: {
                StarShader.finish();
                break;
            }
        }
    }
    
    static {
        ShaderChams.INSTANCE = new ShaderChams();
        ShaderChams.INSTANCE = new ShaderChams();
    }
    
    public enum mode
    {
        DARKMATTER, 
        HYPNOTIZE, 
        BLUE_ABSTRACT, 
        GRADIENT_SHADER, 
        PINK_LEMONADE, 
        STAR_SHADER, 
        VORTEX, 
        RIZZ;
    }
}
