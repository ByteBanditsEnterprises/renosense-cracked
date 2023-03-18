//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.render;

import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.features.setting.*;
import java.awt.*;
import me.sjnez.renosense.event.events.*;
import net.minecraft.entity.player.*;
import me.sjnez.renosense.*;
import me.sjnez.renosense.features.modules.client.*;
import me.sjnez.renosense.util.*;
import java.util.*;
import net.minecraft.util.math.*;

public class AnvilESP extends Module
{
    public Setting<Boolean> rainbow;
    public Setting<Integer> hue;
    public Setting<Integer> sat;
    public Setting<Integer> bright;
    public Setting<Integer> alpha;
    public Setting<Float> lineWidth;
    public Setting<Boolean> colorSync;
    
    public AnvilESP() {
        super("AnvilESP", "Shows players when you are high up", Module.Category.RENDER, true, false, false);
        this.rainbow = (Setting<Boolean>)this.register(new Setting("Rainbow", (T)false, v -> !this.colorSync.getValue()));
        this.hue = (Setting<Integer>)this.register(new Setting("Hue", (T)300, (T)0, (T)360, v -> !this.rainbow.getValue() && !this.colorSync.getValue()));
        this.sat = (Setting<Integer>)this.register(new Setting("Saturation", (T)100, (T)0, (T)100, v -> !this.rainbow.getValue() && !this.colorSync.getValue()));
        this.bright = (Setting<Integer>)this.register(new Setting("Brightness", (T)33, (T)0, (T)100, v -> !this.rainbow.getValue() && !this.colorSync.getValue()));
        this.alpha = (Setting<Integer>)this.register(new Setting("Alpha", (T)0, (T)0, (T)255));
        this.lineWidth = (Setting<Float>)this.register(new Setting("Line Width", (T)1.0f, (T)0.5f, (T)3.0f));
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
        for (final EntityPlayer entityPlayer : AnvilESP.mc.world.playerEntities) {
            if (!entityPlayer.equals((Object)AnvilESP.mc.player)) {
                if (RenoSense.friendManager.isFriend(entityPlayer)) {
                    continue;
                }
                AxisAlignedBB bb = entityPlayer.getEntityBoundingBox();
                bb = bb.setMaxY(AnvilESP.mc.player.posY);
                RenderUtil.drawHESPBlockOutline(bb, ((boolean)this.colorSync.getValue()) ? new Color(Colors.getInstance().getRed(), Colors.getInstance().getGreen(), Colors.getInstance().getBlue(), this.alpha.getValue()) : (this.rainbow.getValue() ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()) : new Color(this.getRed(), this.getGreen(), this.getBlue())), this.lineWidth.getValue());
            }
        }
    }
}
