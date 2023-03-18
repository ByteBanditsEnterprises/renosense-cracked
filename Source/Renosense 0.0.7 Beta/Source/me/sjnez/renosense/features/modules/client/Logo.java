//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.client;

import me.sjnez.renosense.features.modules.*;
import net.minecraft.util.*;
import me.sjnez.renosense.features.setting.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.gui.*;
import me.sjnez.renosense.event.events.*;

public class Logo extends Module
{
    public static final ResourceLocation mark;
    public Setting<Integer> imageX;
    public Setting<Integer> imageY;
    public Setting<Integer> imageWidth;
    public Setting<Integer> imageHeight;
    
    public Logo() {
        super("Logo", "Adds a RenoSense logo to your screen.", Category.CLIENT, true, false, false);
        this.imageX = (Setting<Integer>)this.register(new Setting("WatermarkX", (T)0, (T)0, (T)300, "X position of the logo."));
        this.imageY = (Setting<Integer>)this.register(new Setting("WatermarkY", (T)0, (T)0, (T)300, "Y position of the logo."));
        this.imageWidth = (Setting<Integer>)this.register(new Setting("WatermarkWidth", (T)97, (T)0, (T)1000, "Width of the logo."));
        this.imageHeight = (Setting<Integer>)this.register(new Setting("WatermarkHeight", (T)97, (T)0, (T)1000, "Height of the logo."));
    }
    
    public void renderLogo() {
        final int width = this.imageWidth.getValue();
        final int height = this.imageHeight.getValue();
        final int x = this.imageX.getValue();
        final int y = this.imageY.getValue();
        Logo.mc.renderEngine.bindTexture(Logo.mark);
        GlStateManager.color(255.0f, 255.0f, 255.0f);
        Gui.drawScaledCustomSizeModalRect(x - 2, y - 36, 7.0f, 7.0f, width - 7, height - 7, width, height, (float)width, (float)height);
    }
    
    @Override
    public void onRender2D(final Render2DEvent event) {
        if (!fullNullCheck()) {
            final int width = this.renderer.scaledWidth;
            final int height = this.renderer.scaledHeight;
            if (this.enabled.getValue()) {
                this.renderLogo();
            }
        }
    }
    
    static {
        mark = new ResourceLocation("textures/renosense.png");
    }
}
