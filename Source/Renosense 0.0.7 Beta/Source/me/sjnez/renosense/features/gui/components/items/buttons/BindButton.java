//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.gui.components.items.buttons;

import me.sjnez.renosense.features.modules.client.*;
import java.awt.*;
import me.sjnez.renosense.util.*;
import me.sjnez.renosense.*;
import me.sjnez.renosense.features.gui.*;
import com.mojang.realmsclient.gui.*;
import net.minecraft.init.*;
import net.minecraft.client.audio.*;
import me.sjnez.renosense.features.setting.*;

public class BindButton extends Button
{
    private final Setting setting;
    public boolean isListening;
    
    public BindButton(final Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        RenderUtil.drawRect(this.x, this.y, this.x + this.width + 7.4f, this.y + this.height - 0.5f, this.getState() ? (this.isHovering(mouseX, mouseY) ? -2007673515 : 290805077) : (this.isHovering(mouseX, mouseY) ? new Color(ClickGui.getInstance().getRed(), ClickGui.getInstance().getGreen(), ClickGui.getInstance().getBlue(), ClickGui.getInstance().hoverAlpha1.getValue()).getRGB() : new Color(ClickGui.getInstance().getRed(), ClickGui.getInstance().getGreen(), ClickGui.getInstance().getBlue(), ClickGui.getInstance().alpha.getValue()).getRGB()));
        if (this.isListening) {
            RenoSense.textManager.drawStringWithShadow("Press a Key...", this.x + 2.3f, this.y - 1.7f - RenoSenseGui.getClickGui().getTextOffset(), -1);
        }
        else {
            RenoSense.textManager.drawStringWithShadow(this.setting.getName() + " " + ChatFormatting.RED + this.setting.getValue().toString().toUpperCase(), this.x + 2.3f, this.y - 1.7f - RenoSenseGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
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
            BindButton.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }
    
    @Override
    public void onKeyTyped(final char typedChar, final int keyCode) {
        if (this.isListening) {
            Bind bind = new Bind(keyCode);
            if (bind.toString().equalsIgnoreCase("Escape")) {
                return;
            }
            if (bind.toString().equalsIgnoreCase("Delete")) {
                bind = new Bind(-1);
            }
            this.setting.setValue(bind);
            this.onMouseClick();
        }
    }
    
    @Override
    public int getHeight() {
        return 14;
    }
    
    @Override
    public void toggle() {
        this.isListening = !this.isListening;
    }
    
    @Override
    public boolean getState() {
        return !this.isListening;
    }
}
