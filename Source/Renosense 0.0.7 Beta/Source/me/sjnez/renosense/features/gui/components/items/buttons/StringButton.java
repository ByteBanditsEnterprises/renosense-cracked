//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.gui.components.items.buttons;

import me.sjnez.renosense.features.setting.*;
import me.sjnez.renosense.features.modules.client.*;
import me.sjnez.renosense.util.*;
import me.sjnez.renosense.*;
import me.sjnez.renosense.features.gui.*;
import com.mojang.realmsclient.gui.*;
import me.sjnez.renosense.features.gui.components.items.*;
import me.sjnez.renosense.mixin.mixins.*;
import net.minecraft.init.*;
import net.minecraft.client.audio.*;
import org.lwjgl.input.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.*;
import net.minecraft.util.*;

public class StringButton extends Button
{
    private final Setting setting;
    public boolean isListening;
    private CurrentString currentString;
    
    public StringButton(final Setting setting) {
        super(setting.getName());
        this.currentString = new CurrentString("");
        this.setting = setting;
        this.width = 15;
    }
    
    public static String removeLastChar(final String str) {
        String output = "";
        if (str != null && str.length() > 0) {
            output = str.substring(0, str.length() - 1);
        }
        return output;
    }
    
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        RenderUtil.drawRect(this.x, this.y, this.x + this.width + 7.4f, this.y + this.height - 0.5f, this.getState() ? (this.isHovering(mouseX, mouseY) ? new Color(ClickGui.getInstance().getRed(), ClickGui.getInstance().getGreen(), ClickGui.getInstance().getBlue(), ClickGui.getInstance().hoverAlpha1.getValue()).getRGB() : new Color(ClickGui.getInstance().getRed(), ClickGui.getInstance().getGreen(), ClickGui.getInstance().getBlue(), ClickGui.getInstance().alpha.getValue()).getRGB()) : (this.isHovering(mouseX, mouseY) ? -2007673515 : 290805077));
        if (this.isListening) {
            RenoSense.textManager.drawStringWithShadow(this.currentString.getString() + RenoSense.textManager.getIdleSign(), this.x + 2.3f, this.y - 1.7f - RenoSenseGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
        }
        else {
            RenoSense.textManager.drawStringWithShadow((this.setting.getName().equals("Buttons") ? "Buttons " : (this.setting.getName().equals("Prefix") ? ("Prefix  " + ChatFormatting.RED) : "")) + this.setting.getValue(), this.x + 2.3f, this.y - 1.7f - RenoSenseGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
        }
        if (DescBox.open && this.isHovering(mouseX, mouseY)) {
            final String s = this.setting.getDescription();
            final int color = ClickGui.getInstance().getTColor();
            ((IFontRenderer)StringButton.mc.fontRenderer).invokeRenderSplitString(s, (int)DescBox.descX + 3, (int)DescBox.descY + 2, 250, true);
            StringButton.mc.fontRenderer.drawSplitString(s, (int)DescBox.descX + 3, (int)DescBox.descY + 2, 250, color);
        }
    }
    
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            StringButton.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }
    
    public void onKeyTyped(final char typedChar, final int keyCode) {
        if (this.isListening) {
            switch (keyCode) {
                case 47: {
                    if (Keyboard.isKeyDown(29)) {
                        try {
                            this.setString(this.currentString.getString() + Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).toString());
                        }
                        catch (UnsupportedFlavorException | IOException ex2) {
                            final Exception ex;
                            final Exception unsupportedFlavorException = ex;
                            unsupportedFlavorException.printStackTrace();
                        }
                        break;
                    }
                    break;
                }
                case 1: {
                    return;
                }
                case 28: {
                    this.enterString();
                }
                case 14: {
                    if (Keyboard.isKeyDown(29)) {
                        this.setString("");
                    }
                    this.setString(removeLastChar(this.currentString.getString()));
                    break;
                }
            }
            if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                this.setString(this.currentString.getString() + typedChar);
            }
        }
    }
    
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }
    
    private void enterString() {
        if (this.currentString.getString().isEmpty()) {
            this.setting.setValue(this.setting.getDefaultValue());
        }
        else {
            this.setting.setValue(this.currentString.getString());
        }
        this.setString("");
        this.onMouseClick();
    }
    
    public int getHeight() {
        return 14;
    }
    
    public void toggle() {
        this.isListening = !this.isListening;
    }
    
    public boolean getState() {
        return !this.isListening;
    }
    
    public void setString(final String newString) {
        this.currentString = new CurrentString(newString);
    }
    
    public static class CurrentString
    {
        private final String string;
        
        public CurrentString(final String string) {
            this.string = string;
        }
        
        public String getString() {
            return this.string;
        }
    }
}
