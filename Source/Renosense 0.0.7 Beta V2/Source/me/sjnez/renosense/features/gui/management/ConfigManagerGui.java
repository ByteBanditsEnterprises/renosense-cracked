//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.gui.management;

import net.minecraft.client.gui.*;
import net.minecraft.client.*;
import com.google.common.collect.*;
import java.util.stream.*;
import me.sjnez.renosense.*;
import java.awt.*;
import java.io.*;
import com.mojang.realmsclient.gui.*;
import java.util.*;

public class ConfigManagerGui extends GuiScreen
{
    int i;
    private final GuiScreen previousGuiScreen;
    public List<GuiButton> configF;
    static List<String> configList;
    public int i1;
    public int i2;
    public int i3;
    
    public ConfigManagerGui(final GuiScreen parent, final Minecraft mcIn) {
        this.i = 0;
        this.configF = (List<GuiButton>)Lists.newArrayList();
        this.mc = mcIn;
        this.previousGuiScreen = parent;
    }
    
    public void getConfigs() {
        final File file = new File("renosense/");
        final List<File> directories = Arrays.stream(file.listFiles()).filter(File::isDirectory).filter(f -> !f.getName().equals("util")).collect((Collector<? super File, ?, List<File>>)Collectors.toList());
        for (final File file2 : directories) {
            ConfigManagerGui.configList.add(file2.getName());
        }
    }
    
    public boolean isCurrent(final String config) {
        return RenoSense.configManager.loadCurrentConfig().equals(config);
    }
    
    protected void actionPerformed(final GuiButton button) {
        if (button.id == 999) {
            this.mc.displayGuiScreen(this.previousGuiScreen);
        }
        if (button.id == 1000) {
            try {
                Desktop.getDesktop().open(new File("renosense/"));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (button.id >= 0 && button.id < 299) {
            this.i1 = 0;
            while (this.i1 < this.configF.size()) {
                if (button.id == this.i1) {}
                ++this.i1;
            }
        }
        if (button.id >= 300 && button.id < 500) {
            this.i2 = 300;
            while (this.i2 < this.configF.size() + 300) {
                if (button.id == this.i2 && !this.isCurrent(ConfigManagerGui.configList.get(this.i2 - 300))) {
                    RenoSense.configManager.loadConfig(ConfigManagerGui.configList.get(this.i2 - 300));
                    System.out.println("Loading " + ConfigManagerGui.configList.get(this.i2 - 300));
                }
                ++this.i2;
            }
        }
        this.buttonList.clear();
        this.configF.clear();
        ConfigManagerGui.configList.clear();
        this.initGui();
        this.updateScreen();
    }
    
    public void initGui() {
        this.configF.clear();
        ConfigManagerGui.configList.clear();
        this.buttonList.clear();
        this.getConfigs();
        int y = -140;
        for (final String config : ConfigManagerGui.configList) {
            this.configF.add(new GuiButton(300 + this.i, this.width / 2 - 400, this.height - 200 - y, 200, 20, this.isCurrent(config) ? (ChatFormatting.GREEN + "" + ChatFormatting.BOLD + config) : config));
            ++this.i;
            y += 23;
        }
        this.buttonList.addAll(this.configF);
        this.buttonList.add(new GuiButton(999, this.width / 2 - 100, this.height - 60, 200, 20, "Back to RenoSense Manager"));
        this.buttonList.add(new GuiButton(1000, this.width / 2 - 100, this.height - 85, 200, 20, "Open RenoSense Folder"));
    }
    
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    public void updateScreen() {
        super.updateScreen();
        this.i2 = 0;
        this.i1 = 0;
        this.i = 0;
    }
    
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
    
    static {
        ConfigManagerGui.configList = new ArrayList<String>();
    }
}
