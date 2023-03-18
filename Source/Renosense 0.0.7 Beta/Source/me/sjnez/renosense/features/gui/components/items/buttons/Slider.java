//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.gui.components.items.buttons;

import me.sjnez.renosense.features.setting.*;
import me.sjnez.renosense.util.*;
import me.sjnez.renosense.features.modules.client.*;
import java.awt.*;
import me.sjnez.renosense.*;
import com.mojang.realmsclient.gui.*;
import me.sjnez.renosense.features.gui.*;
import me.sjnez.renosense.features.gui.components.items.*;
import me.sjnez.renosense.mixin.mixins.*;
import me.sjnez.renosense.features.gui.components.*;
import java.util.*;
import org.lwjgl.input.*;

public class Slider extends Button
{
    private final Number min;
    private final Number max;
    private final int difference;
    public Setting setting;
    
    public Slider(final Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.min = setting.getMin();
        this.max = setting.getMax();
        this.difference = this.max.intValue() - this.min.intValue();
        this.width = 15;
    }
    
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.dragSetting(mouseX, mouseY);
        RenderUtil.drawRect(this.x, this.y, this.x + this.width + 7.4f, this.y + this.height - 0.5f, this.isHovering(mouseX, mouseY) ? -2007673515 : 290805077);
        RenderUtil.drawRect(this.x, this.y, (this.setting.getValue().floatValue() <= this.min.floatValue()) ? this.x : (this.x + (this.width + 7.4f) * this.partialMultiplier()), this.y + this.height - 0.5f, this.isHovering(mouseX, mouseY) ? new Color(ClickGui.getInstance().getRed(), ClickGui.getInstance().getGreen(), ClickGui.getInstance().getBlue(), ClickGui.getInstance().hoverAlpha1.getValue()).getRGB() : new Color(ClickGui.getInstance().getRed(), ClickGui.getInstance().getGreen(), ClickGui.getInstance().getBlue(), ClickGui.getInstance().alpha.getValue()).getRGB());
        RenoSense.textManager.drawStringWithShadow(this.getName() + " " + ChatFormatting.RED + ((this.setting.getValue() instanceof Float) ? this.setting.getValue() : this.setting.getValue().doubleValue()), this.x + 2.3f, this.y - 1.7f - RenoSenseGui.getClickGui().getTextOffset(), -1);
        if (DescBox.open && this.isHovering(mouseX, mouseY)) {
            final String s = this.setting.getDescription();
            final int color = ClickGui.getInstance().getTColor();
            ((IFontRenderer)Slider.mc.fontRenderer).invokeRenderSplitString(s, (int)DescBox.descX + 3, (int)DescBox.descY + 2, 250, true);
            Slider.mc.fontRenderer.drawSplitString(s, (int)DescBox.descX + 3, (int)DescBox.descY + 2, 250, color);
        }
    }
    
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            this.setSettingFromX(mouseX);
        }
    }
    
    public boolean isHovering(final int mouseX, final int mouseY) {
        for (final Component component : RenoSenseGui.getClickGui().getComponents()) {
            if (!component.drag) {
                continue;
            }
            return false;
        }
        return mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() + 8.0f && mouseY >= this.getY() && mouseY <= this.getY() + this.height;
    }
    
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }
    
    private void dragSetting(final int mouseX, final int mouseY) {
        if (this.isHovering(mouseX, mouseY) && Mouse.isButtonDown(0)) {
            this.setSettingFromX(mouseX);
        }
    }
    
    public int getHeight() {
        return 14;
    }
    
    private void setSettingFromX(final int mouseX) {
        final float percent = (mouseX - this.x) / (this.width + 7.4f);
        if (this.setting.getValue() instanceof Double) {
            final double result = this.setting.getMin() + this.difference * percent;
            this.setting.setValue(Math.round(10.0 * result) / 10.0);
        }
        else if (this.setting.getValue() instanceof Float) {
            final float result2 = this.setting.getMin() + this.difference * percent;
            this.setting.setValue(Math.round(10.0f * result2) / 10.0f);
        }
        else if (this.setting.getValue() instanceof Integer) {
            this.setting.setValue(this.setting.getMin() + (int)(this.difference * percent));
        }
    }
    
    private float middle() {
        return this.max.floatValue() - this.min.floatValue();
    }
    
    private float part() {
        return this.setting.getValue().floatValue() - this.min.floatValue();
    }
    
    private float partialMultiplier() {
        return this.part() / this.middle();
    }
}
