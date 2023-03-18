//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.gui.components.items.buttons;

import me.sjnez.renosense.features.modules.client.*;
import java.awt.*;
import me.sjnez.renosense.util.*;
import me.sjnez.renosense.*;
import me.sjnez.renosense.features.gui.*;
import me.sjnez.renosense.features.gui.components.items.*;
import me.sjnez.renosense.mixin.mixins.*;
import me.sjnez.renosense.features.modules.*;
import java.util.*;
import net.minecraft.init.*;
import net.minecraft.client.audio.*;
import me.sjnez.renosense.features.gui.components.*;

public class Button extends Item
{
    private boolean state;
    
    public Button(final String name) {
        super(name);
        this.height = 15;
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        RenderUtil.drawRect(this.x, this.y, this.x + this.width, this.y + this.height - 0.5f, this.getState() ? (this.isHovering(mouseX, mouseY) ? new Color(ClickGui.getInstance().getRed(), ClickGui.getInstance().getGreen(), ClickGui.getInstance().getBlue(), ClickGui.getInstance().hoverAlpha1.getValue()).getRGB() : new Color(ClickGui.getInstance().getRed(), ClickGui.getInstance().getGreen(), ClickGui.getInstance().getBlue(), ClickGui.getInstance().alpha.getValue()).getRGB()) : (this.isHovering(mouseX, mouseY) ? -2007673515 : 290805077));
        RenoSense.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 2.0f - RenoSenseGui.getClickGui().getTextOffset(), this.getState() ? (ClickGui.getInstance().modTColor.getValue() ? ClickGui.getInstance().getTColor() : -1) : -5592406);
        if (this.getMod(mouseX, mouseY) != null && DescBox.open) {
            final String m = this.getDesc(this.getMod(mouseX, mouseY));
            final int color = ClickGui.getInstance().getTColor();
            ((IFontRenderer)Button.mc.fontRenderer).invokeRenderSplitString(m, (int)DescBox.descX + 3, (int)DescBox.descY + 2, DescBox.width, true);
            Button.mc.fontRenderer.drawSplitString(m, (int)DescBox.descX + 3, (int)DescBox.descY + 2, DescBox.width, this.getMod(mouseX, mouseY).isOn() ? color : -5592406);
        }
    }
    
    public Module getMod(final int mouseX, final int mouseY) {
        for (final Module module : RenoSense.moduleManager.modules) {
            if (this.isHovering(mouseX, mouseY)) {
                System.out.println("returning mod");
                return module;
            }
        }
        return null;
    }
    
    public String getDesc(final Module module) {
        return module.getDescription();
    }
    
    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            this.onMouseClick();
        }
        if (mouseButton == 1 && this.isHovering(mouseX, mouseY)) {
            this.onMouseClickR();
        }
    }
    
    public void onMouseClick() {
        this.state = !this.state;
        this.toggle();
        Button.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }
    
    public void onMouseClickR() {
        this.state = !this.state;
        this.toggleR();
        Button.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }
    
    public void toggle() {
    }
    
    public void toggleR() {
    }
    
    public boolean getState() {
        return this.state;
    }
    
    @Override
    public int getHeight() {
        return 14;
    }
    
    public boolean isHovering(final int mouseX, final int mouseY) {
        for (final Component component : RenoSenseGui.getClickGui().getComponents()) {
            if (!component.drag) {
                continue;
            }
            return false;
        }
        return mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY <= this.getY() + this.height;
    }
}
