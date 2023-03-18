//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.gui.components.items.buttons;

import me.sjnez.renosense.features.setting.*;
import me.sjnez.renosense.features.modules.client.*;
import java.awt.*;
import me.sjnez.renosense.util.*;
import me.sjnez.renosense.*;
import me.sjnez.renosense.features.gui.*;
import me.sjnez.renosense.features.gui.components.items.*;
import me.sjnez.renosense.mixin.mixins.*;
import net.minecraft.init.*;
import net.minecraft.client.audio.*;

public class BooleanButton extends Button
{
    private final Setting setting;
    
    public BooleanButton(final Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        RenderUtil.drawRect(this.x, this.y, this.x + this.width + 7.4f, this.y + this.height - 0.5f, this.getState() ? (this.isHovering(mouseX, mouseY) ? new Color(ClickGui.getInstance().getRed(), ClickGui.getInstance().getGreen(), ClickGui.getInstance().getBlue(), ClickGui.getInstance().hoverAlpha1.getValue()).getRGB() : new Color(ClickGui.getInstance().getRed(), ClickGui.getInstance().getGreen(), ClickGui.getInstance().getBlue(), ClickGui.getInstance().alpha.getValue()).getRGB()) : (this.isHovering(mouseX, mouseY) ? -2007673515 : 290805077));
        RenoSense.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 1.7f - RenoSenseGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
        if (DescBox.open && this.isHovering(mouseX, mouseY)) {
            final String s = this.setting.getDescription();
            final int color = ClickGui.getInstance().getTColor();
            ((IFontRenderer)BooleanButton.mc.fontRenderer).invokeRenderSplitString(s, (int)DescBox.descX + 3, (int)DescBox.descY + 2, 250, true);
            BooleanButton.mc.fontRenderer.drawSplitString(s, (int)DescBox.descX + 3, (int)DescBox.descY + 2, 250, ((boolean)this.setting.getValue()) ? color : -5592406);
        }
    }
    
    @Override
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }
    
    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            BooleanButton.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }
    
    @Override
    public int getHeight() {
        return 14;
    }
    
    @Override
    public void toggle() {
        this.setting.setValue(!this.setting.getValue());
    }
    
    @Override
    public boolean getState() {
        return this.setting.getValue();
    }
}
