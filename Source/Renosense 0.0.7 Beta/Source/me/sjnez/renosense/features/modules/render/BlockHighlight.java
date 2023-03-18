//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.render;

import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.features.setting.*;
import java.awt.*;
import me.sjnez.renosense.event.events.*;
import me.sjnez.renosense.features.modules.client.*;
import me.sjnez.renosense.util.*;
import net.minecraft.util.math.*;

public class BlockHighlight extends Module
{
    private final Setting<Float> lineWidth;
    private Setting<Integer> hue;
    private Setting<Integer> sat;
    private Setting<Integer> bright;
    private final Setting<Integer> cAlpha;
    public Setting<Boolean> colorSync;
    
    public BlockHighlight() {
        super("BlockHighlight", "Highlights the block you look at.", Module.Category.RENDER, true, false, false);
        this.lineWidth = (Setting<Float>)this.register(new Setting("LineWidth", (T)1.0f, (T)0.1f, (T)5.0f));
        this.hue = (Setting<Integer>)this.register(new Setting("Hue", (T)300, (T)0, (T)360));
        this.sat = (Setting<Integer>)this.register(new Setting("Saturation", (T)100, (T)0, (T)100));
        this.bright = (Setting<Integer>)this.register(new Setting("Brightness", (T)33, (T)0, (T)100));
        this.cAlpha = (Setting<Integer>)this.register(new Setting("Alpha", (T)255, (T)0, (T)255));
        this.colorSync = (Setting<Boolean>)this.register(new Setting("Color Sync", (T)false));
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
    
    public void onRender3D(final Render3DEvent event) {
        final RayTraceResult ray = BlockHighlight.mc.objectMouseOver;
        if (ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK) {
            final BlockPos blockpos = ray.getBlockPos();
            RenderUtil.drawBlockOutline(blockpos, ((boolean)this.colorSync.getValue()) ? new Color(Colors.getInstance().getRed(), Colors.getInstance().getGreen(), Colors.getInstance().getBlue(), this.cAlpha.getValue()) : (ClickGui.getInstance().rainbow.getValue() ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()) : new Color(this.getRed(), this.getGreen(), this.getBlue(), this.cAlpha.getValue())), this.lineWidth.getValue(), false);
        }
    }
}
