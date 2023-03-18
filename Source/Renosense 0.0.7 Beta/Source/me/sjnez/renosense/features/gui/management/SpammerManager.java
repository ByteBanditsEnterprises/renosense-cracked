//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.gui.management;

import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import me.sjnez.renosense.features.gui.*;
import me.sjnez.renosense.features.modules.misc.*;
import me.sjnez.renosense.*;
import java.io.*;

public class SpammerManager extends GuiScreen
{
    private final GuiScreen previousGuiScreen;
    
    public SpammerManager(final GuiScreen parent, final Minecraft mcIn) {
        this.mc = mcIn;
        this.previousGuiScreen = parent;
    }
    
    protected void actionPerformed(final GuiButton button) {
        if (button.id == 999) {
            this.mc.displayGuiScreen((GuiScreen)new RenoSenseGui());
            RenoSense.moduleManager.getModuleByClass(FileSpammer.class).act.setValue(false);
        }
    }
    
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(999, this.width / 2 + 150, this.height - 60, 200, 20, "Back to Game"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 2, 200, 20, "Coming Soon."));
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
