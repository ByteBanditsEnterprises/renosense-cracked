//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.render;

import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.features.setting.*;
import net.minecraft.util.math.*;
import java.awt.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import java.util.*;
import me.sjnez.renosense.event.events.*;
import me.sjnez.renosense.util.*;
import me.sjnez.renosense.features.modules.client.*;

public class BurrowESP extends Module
{
    private static BurrowESP INSTANCE;
    public Setting<Integer> range;
    public Setting<Boolean> self;
    public Setting<Boolean> text;
    public Setting<String> textString;
    public Setting<Boolean> rainbow;
    public Setting<Integer> hue;
    public Setting<Integer> sat;
    public Setting<Integer> bright;
    public Setting<Integer> alpha;
    public Setting<Integer> outlineAlpha;
    public Setting<Boolean> colorSync;
    private final List<BlockPos> posList;
    private final RenderUtil renderUtil;
    
    public BurrowESP() {
        super("BurrowESP", "See who is in a burrow.", Module.Category.RENDER, true, false, false);
        this.range = (Setting<Integer>)this.register(new Setting("Range", (T)20, (T)5, (T)50));
        this.self = (Setting<Boolean>)this.register(new Setting("Self", (T)true));
        this.text = (Setting<Boolean>)this.register(new Setting("Text", (T)true));
        this.textString = (Setting<String>)this.register(new Setting("TextString", (T)"BURROW", v -> this.text.getValue()));
        this.rainbow = (Setting<Boolean>)this.register(new Setting("Rainbow", (T)false));
        this.hue = (Setting<Integer>)this.register(new Setting("Hue", (T)300, (T)0, (T)360, v -> !this.rainbow.getValue()));
        this.sat = (Setting<Integer>)this.register(new Setting("Saturation", (T)100, (T)0, (T)100, v -> !this.rainbow.getValue()));
        this.bright = (Setting<Integer>)this.register(new Setting("Brightness", (T)33, (T)0, (T)100, v -> !this.rainbow.getValue()));
        this.alpha = (Setting<Integer>)this.register(new Setting("Alpha", (T)0, (T)0, (T)255));
        this.outlineAlpha = (Setting<Integer>)this.register(new Setting("OL-Alpha", (T)0, (T)0, (T)255));
        this.colorSync = (Setting<Boolean>)this.register(new Setting("Color Sync", (T)false));
        this.posList = new ArrayList<BlockPos>();
        this.renderUtil = new RenderUtil();
        this.setInstance();
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
    
    public static BurrowESP getInstance() {
        if (BurrowESP.INSTANCE == null) {
            BurrowESP.INSTANCE = new BurrowESP();
        }
        return BurrowESP.INSTANCE;
    }
    
    private void setInstance() {
        BurrowESP.INSTANCE = this;
    }
    
    public void onTick() {
        this.posList.clear();
        for (final EntityPlayer player : BurrowESP.mc.world.playerEntities) {
            final BlockPos blockPos = new BlockPos(Math.floor(player.posX), Math.floor(player.posY + 0.2), Math.floor(player.posZ));
            if ((BurrowESP.mc.world.getBlockState(blockPos).getBlock() == Blocks.ENDER_CHEST || BurrowESP.mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN) && blockPos.distanceSq(BurrowESP.mc.player.posX, BurrowESP.mc.player.posY, BurrowESP.mc.player.posZ) <= this.range.getValue() && (blockPos.distanceSq(BurrowESP.mc.player.posX, BurrowESP.mc.player.posY, BurrowESP.mc.player.posZ) > 1.5 || this.self.getValue())) {
                this.posList.add(blockPos);
            }
        }
    }
    
    public void onRender3D(final Render3DEvent event) {
        for (final BlockPos blockPos : this.posList) {
            final String s = this.textString.getValue().toUpperCase();
            if (this.text.getValue()) {
                this.renderUtil.drawText(blockPos, s, ((boolean)this.rainbow.getValue()) ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()) : new Color(this.getRed(), this.getGreen(), this.getBlue(), this.outlineAlpha.getValue()));
            }
            RenderUtil.drawBoxESP(blockPos, ((boolean)this.colorSync.getValue()) ? new Color(Colors.getInstance().getRed(), Colors.getInstance().getGreen(), Colors.getInstance().getBlue(), ClickGui.getInstance().alpha.getValue()) : (this.rainbow.getValue() ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()) : new Color(this.getRed(), this.getGreen(), this.getBlue(), this.outlineAlpha.getValue())), 1.5f, true, true, this.alpha.getValue());
        }
    }
    
    static {
        BurrowESP.INSTANCE = new BurrowESP();
    }
}
