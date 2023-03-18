//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.gui.management;

import net.minecraft.client.*;
import net.minecraft.client.multiplayer.*;
import java.io.*;
import net.minecraftforge.fml.client.*;
import net.minecraft.client.gui.*;

public class ServerManagerGui extends GuiScreen
{
    private final GuiScreen previousGuiScreen;
    
    public ServerManagerGui(final GuiScreen parent, final Minecraft mcIn) {
        this.mc = mcIn;
        this.previousGuiScreen = parent;
    }
    
    protected void actionPerformed(final GuiButton button) {
        if (button.id == 999) {
            this.mc.displayGuiScreen(this.previousGuiScreen);
        }
        if (button.id == 1) {
            this.connectToServer(new ServerData("2b2t", "2b2t.org", false));
        }
        if (button.id == 2) {
            this.connectToServer(new ServerData("9b9t", "9b9t.com", false));
        }
        if (button.id == 3) {
            this.connectToServer(new ServerData("5b5t", "5b5t.org", false));
        }
        if (button.id == 4) {
            this.connectToServer(new ServerData("2bpvp", "2b2tpvp.net", false));
        }
        if (button.id == 5) {
            this.connectToServer(new ServerData("2bpvp Strict", "strict.2b2tpvp.net", false));
        }
        if (button.id == 6) {
            this.connectToServer(new ServerData("Crystalpvp.cc", "crystalpvp.cc", false));
        }
    }
    
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(999, this.width / 2 - 100, this.height - 60, 200, 20, "Back to RenoSense Manager"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 2 - 60, 200, 20, "2b2t"));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, this.height / 2 - 85, 200, 20, "9b9t"));
        this.buttonList.add(new GuiButton(3, this.width / 2 - 100, this.height / 2 - 110, 200, 20, "5b5t"));
        this.buttonList.add(new GuiButton(4, this.width / 2 - 100, this.height / 2 - 35, 200, 20, "2bpvp"));
        this.buttonList.add(new GuiButton(5, this.width / 2 - 100, this.height / 2 - 10, 200, 20, "2bpvp Strict"));
        this.buttonList.add(new GuiButton(6, this.width / 2 - 100, this.height / 2 + 15, 200, 20, "Crystalpvp.cc"));
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
    
    private void connectToServer(final ServerData server) {
        FMLClientHandler.instance().connectToServer((GuiScreen)new GuiMultiplayer(this.previousGuiScreen), server);
    }
}
