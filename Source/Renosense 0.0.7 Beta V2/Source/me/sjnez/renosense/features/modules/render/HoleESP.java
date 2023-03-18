//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.render;

import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.features.setting.*;
import java.awt.*;
import me.sjnez.renosense.event.events.*;
import net.minecraft.util.math.*;
import net.minecraft.init.*;
import me.sjnez.renosense.features.modules.client.*;
import me.sjnez.renosense.util.*;

public class HoleESP extends Module
{
    public Setting<Boolean> renderOwn;
    public Setting<Boolean> fov;
    public Setting<Boolean> rainbow;
    private final Setting<Integer> range;
    private final Setting<Integer> rangeY;
    public Setting<Boolean> box;
    public Setting<Boolean> gradientBox;
    public Setting<Boolean> invertGradientBox;
    public Setting<Boolean> outline;
    public Setting<Boolean> gradientOutline;
    public Setting<Boolean> invertGradientOutline;
    public Setting<Double> height;
    private Setting<Integer> hue;
    private Setting<Integer> sat;
    private Setting<Integer> bright;
    private Setting<Integer> alpha;
    private Setting<Float> lineWidth;
    private Setting<Integer> boxAlpha;
    public Setting<Boolean> safeColor;
    private Setting<Integer> safeHue;
    private Setting<Integer> safeSat;
    private Setting<Integer> safeBright;
    private Setting<Integer> safeAlpha;
    public Setting<Boolean> customOutline;
    private Setting<Integer> cHue;
    private Setting<Integer> cSat;
    private Setting<Integer> cBright;
    private Setting<Integer> cAlpha;
    private Setting<Integer> safecHue;
    private Setting<Integer> safecSat;
    private Setting<Integer> safecBrightness;
    private Setting<Integer> safecAlpha;
    private static HoleESP INSTANCE;
    private int currentAlpha;
    
    public HoleESP() {
        super("HoleESP", "Shows safe spots to not die when getting crystaled.", Module.Category.RENDER, true, false, false);
        this.renderOwn = (Setting<Boolean>)this.register(new Setting("RenderOwn", (T)true));
        this.fov = (Setting<Boolean>)this.register(new Setting("InFov", (T)true));
        this.rainbow = (Setting<Boolean>)this.register(new Setting("Rainbow", (T)false));
        this.range = (Setting<Integer>)this.register(new Setting("RangeX", (T)0, (T)0, (T)10));
        this.rangeY = (Setting<Integer>)this.register(new Setting("RangeY", (T)0, (T)0, (T)10));
        this.box = (Setting<Boolean>)this.register(new Setting("Box", (T)true));
        this.gradientBox = (Setting<Boolean>)this.register(new Setting("Gradient", (T)false, v -> this.box.getValue()));
        this.invertGradientBox = (Setting<Boolean>)this.register(new Setting("ReverseGradient", (T)false, v -> this.gradientBox.getValue()));
        this.outline = (Setting<Boolean>)this.register(new Setting("Outline", (T)true));
        this.gradientOutline = (Setting<Boolean>)this.register(new Setting("GradientOutline", (T)false, v -> this.outline.getValue()));
        this.invertGradientOutline = (Setting<Boolean>)this.register(new Setting("ReverseOutline", (T)false, v -> this.gradientOutline.getValue()));
        this.height = (Setting<Double>)this.register(new Setting("Height", (T)0.0, (T)(-2.0), (T)2.0));
        this.hue = (Setting<Integer>)this.register(new Setting("Hue", (T)0, (T)0, (T)360));
        this.sat = (Setting<Integer>)this.register(new Setting("Saturation", (T)100, (T)0, (T)100));
        this.bright = (Setting<Integer>)this.register(new Setting("Brightness", (T)33, (T)0, (T)100));
        this.alpha = (Setting<Integer>)this.register(new Setting("Alpha", (T)255, (T)0, (T)255));
        this.lineWidth = (Setting<Float>)this.register(new Setting("LineWidth", (T)1.0f, (T)0.1f, (T)5.0f, v -> this.outline.getValue()));
        this.boxAlpha = (Setting<Integer>)this.register(new Setting("BoxAlpha", (T)125, (T)0, (T)255, v -> this.box.getValue()));
        this.safeColor = (Setting<Boolean>)this.register(new Setting("BedrockColor", (T)false));
        this.safeHue = (Setting<Integer>)this.register(new Setting("BedrockHue", (T)120, (T)0, (T)360, v -> this.safeColor.getValue()));
        this.safeSat = (Setting<Integer>)this.register(new Setting("BedrockSaturation", (T)100, (T)0, (T)100, v -> this.safeColor.getValue()));
        this.safeBright = (Setting<Integer>)this.register(new Setting("BedrockBrightness", (T)33, (T)0, (T)100, v -> this.safeColor.getValue()));
        this.safeAlpha = (Setting<Integer>)this.register(new Setting("BedrockAlpha", (T)255, (T)0, (T)255, v -> this.safeColor.getValue()));
        this.customOutline = (Setting<Boolean>)this.register(new Setting("CustomLine", (T)Boolean.FALSE, v -> this.outline.getValue()));
        this.cHue = (Setting<Integer>)this.register(new Setting("OL-Hue", (T)0, (T)0, (T)360, v -> this.customOutline.getValue() && this.outline.getValue()));
        this.cSat = (Setting<Integer>)this.register(new Setting("OL-Saturation", (T)0, (T)0, (T)100, v -> this.customOutline.getValue() && this.outline.getValue()));
        this.cBright = (Setting<Integer>)this.register(new Setting("OL-Brightness", (T)0, (T)0, (T)100, v -> this.customOutline.getValue() && this.outline.getValue()));
        this.cAlpha = (Setting<Integer>)this.register(new Setting("OL-Alpha", (T)255, (T)0, (T)255, v -> this.customOutline.getValue() && this.outline.getValue()));
        this.safecHue = (Setting<Integer>)this.register(new Setting("OL-SafeHue", (T)0, (T)0, (T)360, v -> this.customOutline.getValue() && this.outline.getValue() && this.safeColor.getValue()));
        this.safecSat = (Setting<Integer>)this.register(new Setting("OL-SafeSaturation", (T)0, (T)0, (T)100, v -> this.customOutline.getValue() && this.outline.getValue() && this.safeColor.getValue()));
        this.safecBrightness = (Setting<Integer>)this.register(new Setting("OL-SafeBrightness", (T)0, (T)0, (T)100, v -> this.customOutline.getValue() && this.outline.getValue() && this.safeColor.getValue()));
        this.safecAlpha = (Setting<Integer>)this.register(new Setting("OL-SafeAlpha", (T)255, (T)0, (T)255, v -> this.customOutline.getValue() && this.outline.getValue() && this.safeColor.getValue()));
        this.currentAlpha = 0;
        this.setInstance();
    }
    
    private void setInstance() {
        HoleESP.INSTANCE = this;
    }
    
    public static HoleESP getInstance() {
        if (HoleESP.INSTANCE == null) {
            HoleESP.INSTANCE = new HoleESP();
        }
        return HoleESP.INSTANCE;
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
    
    public int getsRed() {
        return new Color(this.getsColor()).getRed();
    }
    
    public int getsGreen() {
        return new Color(this.getsColor()).getGreen();
    }
    
    public int getsBlue() {
        return new Color(this.getsColor()).getBlue();
    }
    
    public int getsColor() {
        return Color.HSBtoRGB(this.safeHue.getValue() / 360.0f, this.safeSat.getValue() / 100.0f, this.safeBright.getValue() / 100.0f);
    }
    
    public int getcRed() {
        return new Color(this.getcColor()).getRed();
    }
    
    public int getcGreen() {
        return new Color(this.getcColor()).getGreen();
    }
    
    public int getcBlue() {
        return new Color(this.getcColor()).getBlue();
    }
    
    public int getcColor() {
        return Color.HSBtoRGB(this.cHue.getValue() / 360.0f, this.cSat.getValue() / 100.0f, this.cBright.getValue() / 100.0f);
    }
    
    public int getcsRed() {
        return new Color(this.getcsColor()).getRed();
    }
    
    public int getcsGreen() {
        return new Color(this.getcsColor()).getGreen();
    }
    
    public int getcsBlue() {
        return new Color(this.getcsColor()).getBlue();
    }
    
    public int getcsColor() {
        return Color.HSBtoRGB(this.safecHue.getValue() / 360.0f, this.safecSat.getValue() / 100.0f, this.safecBrightness.getValue() / 100.0f);
    }
    
    public void onRender3D(final Render3DEvent event) {
        assert HoleESP.mc.renderViewEntity != null;
        final Vec3i playerPos = new Vec3i(HoleESP.mc.renderViewEntity.posX, HoleESP.mc.renderViewEntity.posY, HoleESP.mc.renderViewEntity.posZ);
        for (int x = playerPos.getX() - this.range.getValue(); x < playerPos.getX() + this.range.getValue(); ++x) {
            for (int z = playerPos.getZ() - this.range.getValue(); z < playerPos.getZ() + this.range.getValue(); ++z) {
                for (int y = playerPos.getY() + this.rangeY.getValue(); y > playerPos.getY() - this.rangeY.getValue(); --y) {
                    final BlockPos pos = new BlockPos(x, y, z);
                    if (HoleESP.mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) && HoleESP.mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) && HoleESP.mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR) && (!pos.equals((Object)new BlockPos(HoleESP.mc.player.posX, HoleESP.mc.player.posY, HoleESP.mc.player.posZ)) || this.renderOwn.getValue())) {
                        if (BlockUtil.isPosInFov(pos) || !this.fov.getValue()) {
                            if (HoleESP.mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK) {
                                RenderUtil.drawBoxESP(pos, ((boolean)this.rainbow.getValue()) ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()) : new Color(this.getsRed(), this.getsGreen(), this.getsBlue(), this.safeAlpha.getValue()), this.customOutline.getValue(), new Color(this.getcsRed(), this.getcsGreen(), this.getcsBlue(), this.safecAlpha.getValue()), this.lineWidth.getValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), true, this.height.getValue(), this.gradientBox.getValue(), this.gradientOutline.getValue(), this.invertGradientBox.getValue(), this.invertGradientOutline.getValue(), this.currentAlpha);
                            }
                            else if (BlockUtil.isBlockUnSafe(HoleESP.mc.world.getBlockState(pos.down()).getBlock()) && BlockUtil.isBlockUnSafe(HoleESP.mc.world.getBlockState(pos.east()).getBlock()) && BlockUtil.isBlockUnSafe(HoleESP.mc.world.getBlockState(pos.west()).getBlock()) && BlockUtil.isBlockUnSafe(HoleESP.mc.world.getBlockState(pos.south()).getBlock())) {
                                if (BlockUtil.isBlockUnSafe(HoleESP.mc.world.getBlockState(pos.north()).getBlock())) {
                                    RenderUtil.drawBoxESP(pos, ((boolean)this.rainbow.getValue()) ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()) : new Color(this.getRed(), this.getGreen(), this.getBlue(), this.alpha.getValue()), this.customOutline.getValue(), new Color(this.getcRed(), this.getcGreen(), this.getcBlue(), this.cAlpha.getValue()), this.lineWidth.getValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), true, this.height.getValue(), this.gradientBox.getValue(), this.gradientOutline.getValue(), this.invertGradientBox.getValue(), this.invertGradientOutline.getValue(), this.currentAlpha);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    static {
        HoleESP.INSTANCE = new HoleESP();
    }
}
