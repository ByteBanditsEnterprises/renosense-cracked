//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.gui.alt;

import java.awt.*;
import me.sjnez.renosense.util.*;
import me.sjnez.renosense.*;

public class AltButton implements Util
{
    public final String email;
    public final String password;
    public final Alt.AltType altType;
    public float x;
    public float y;
    public float width;
    public float height;
    
    public AltButton(final String email, final String password, final Alt.AltType altType, final float x, final float y, final float width, final float height) {
        this.altType = altType;
        this.email = email;
        this.password = password;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public void drawScreen(final int mouseX, final int mouseY) {
        RenderUtil.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, this.altType.equals((Object)Alt.AltType.MICROSOFT) ? Color.CYAN.getRGB() : Color.RED.getRGB());
        RenderUtil.drawRect(this.x + 0.5f, this.y + 0.5f, this.x + this.width - 0.5f, this.y + this.height - 0.5f, new Color(18, 18, 18).getRGB());
        RenoSense.textManager.drawStringWithShadow(this.email, this.x + 5.0f, this.y + 5.0f, Color.WHITE.getRGB());
        final StringBuilder string = new StringBuilder();
        for (int i = 0; i < this.password.length(); ++i) {
            string.append("*");
        }
        RenoSense.textManager.drawStringWithShadow(string.toString(), this.x + 5.0f, this.y + 17.5f, Color.WHITE.getRGB());
        RenderUtil.drawRect(this.x + 5.0f, this.y + this.height - 17.5f, this.x + this.width / 2.0f - 5.0f, this.y + this.height - 2.5f, new Color(15, 15, 15).getRGB());
        RenoSense.textManager.drawStringWithShadow("Login", this.x + 2.5f + (this.width / 2.0f - 5.0f) / 2.0f - RenoSense.textManager.getStringWidth("Login") / 2.0f, this.y + this.height - 7.5f - RenoSense.textManager.getFontHeight() / 2.0f, Color.WHITE.getRGB());
        if (mouseX > this.x + 5.0f && mouseX < this.x + this.width / 2.0f - 5.0f && mouseY > this.y + this.height - 17.5f && mouseY < this.y + this.height - 2.5f) {
            RenderUtil.drawRect(this.x + 5.0f, this.y + this.height - 17.5f, this.x + this.width / 2.0f - 5.0f, this.y + this.height - 2.5f, new Color(0, 0, 0, 50).getRGB());
        }
        RenderUtil.drawRect(this.x + this.width / 2.0f + 2.5f, this.y + this.height - 17.5f, this.x + this.width - 5.0f, this.y + this.height - 2.5f, new Color(15, 15, 15).getRGB());
        if (mouseX > this.x + this.width / 2.0f + 2.5f && mouseX < this.x + this.width - 5.0f && mouseY > this.y + this.height - 17.5f && mouseY < this.y + this.height - 2.5f) {
            RenderUtil.drawRect(this.x + this.width / 2.0f + 2.5f, this.y + this.height - 17.5f, this.x + this.width - 5.0f, this.y + this.height - 2.5f, new Color(0, 0, 0, 50).getRGB());
        }
        RenoSense.textManager.drawStringWithShadow("Delete", this.x + this.width / 2.0f + 2.5f + (this.width / 2.0f - 5.0f) / 2.0f - RenoSense.textManager.getStringWidth("Delete") / 2.0f, this.y + this.height - 7.5f - RenoSense.textManager.getFontHeight() / 2.0f, Color.RED.getRGB());
    }
    
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        if (mouseButton != 0) {
            return;
        }
        if (mouseX > this.x + 5.0f && mouseX < this.x + this.width / 2.0f - 5.0f && mouseY > this.y + this.height - 17.5f && mouseY < this.y + this.height - 2.5f) {
            try {
                new Alt(this.email, this.password, this.altType).login();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mouseX > this.x + this.width / 2.0f + 2.5f && mouseX < this.x + this.width - 5.0f && mouseY > this.y + this.height - 17.5f && mouseY < this.y + this.height - 2.5f) {
            AltGui.altButtons.remove(this);
            AltGui.updateButtons();
        }
    }
}
