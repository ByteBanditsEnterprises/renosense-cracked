//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.gui.management;

import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import java.io.*;

public class RSManagerGui extends GuiScreen
{
    private final GuiScreen previousGuiScreen;
    
    public RSManagerGui(final GuiScreen parent, final Minecraft mcIn) {
        this.mc = mcIn;
        this.previousGuiScreen = parent;
    }
    
    protected void actionPerformed(final GuiButton button) {
        if (button.id == 999) {
            this.mc.displayGuiScreen(this.previousGuiScreen);
        }
        if (button.id == 1) {
            this.mc.displayGuiScreen((GuiScreen)new PlayerManagerGui((GuiScreen)this, Minecraft.getMinecraft()));
        }
        if (button.id == 2) {
            this.mc.displayGuiScreen((GuiScreen)new ServerManagerGui(this, Minecraft.getMinecraft()));
        }
        if (button.id == 3) {
            this.mc.displayGuiScreen((GuiScreen)new ConfigManagerGui((GuiScreen)this, Minecraft.getMinecraft()));
        }
        if (button.id == 4) {
            this.mc.displayGuiScreen((GuiScreen)new ClipManager((GuiScreen)this, Minecraft.getMinecraft()));
        }
        if (button.id == 5) {
            this.mc.displayGuiScreen((GuiScreen)new RemoveAds((GuiScreen)this, Minecraft.getMinecraft()));
        }
    }
    
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(999, this.width / 2 - 100, this.height - 60, 200, 20, "Back to Menu"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height - 85, 200, 20, "PlayerManager"));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, this.height - 110, 200, 20, "ServerManager"));
        this.buttonList.add(new GuiButton(3, this.width / 2 - 100, this.height - 135, 200, 20, "ConfigManager"));
        this.buttonList.add(new GuiButton(4, this.width / 2 - 100, this.height - 160, 200, 20, "ClipManager"));
        this.buttonList.add(new GuiButton(5, this.width / 2 - 100, this.height - 185, 200, 20, "Remove Ads"));
        this.buttonList.add(new GuiButton(6, this.width / 2 - 100, this.height - 210, 200, 20, "SpammerManager"));
        this.buttonList.get(4).enabled = false;
    }
    
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    public void updateScreen() {
        super.updateScreen();
    }
    
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
