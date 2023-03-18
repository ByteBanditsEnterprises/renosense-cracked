//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.misc;

import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.features.setting.*;
import me.sjnez.renosense.util.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import me.sjnez.renosense.features.command.*;
import me.sjnez.renosense.features.gui.*;
import net.minecraft.client.*;
import me.sjnez.renosense.features.gui.management.*;
import net.minecraft.client.gui.*;
import java.util.*;
import java.nio.file.*;
import java.io.*;

public class FileSpammer extends Module
{
    public Setting<Boolean> act;
    public Setting<String> fileName;
    public Setting<Integer> delay;
    public Setting<Boolean> greenT;
    public Setting<Boolean> loop;
    public static List<String> msgs;
    private final Timer timer;
    
    public FileSpammer() {
        super("FileSpammer", "FileSpams", Category.MISC, true, false, false);
        this.act = (Setting<Boolean>)this.register(new Setting("SpammerManager", (T)false));
        this.fileName = (Setting<String>)this.register(new Setting("File", (T)"File", "name of file"));
        this.delay = (Setting<Integer>)this.register(new Setting("Delay", (T)5, (T)1, (T)60));
        this.greenT = (Setting<Boolean>)this.register(new Setting("GreenText", (T)false));
        this.loop = (Setting<Boolean>)this.register(new Setting("Loop", (T)true));
        this.timer = new Timer();
    }
    
    public void doSend() throws IOException {
        if (FileSpammer.mc.player != null && FileSpammer.mc.world != null && FileSpammer.mc.player.connection != null) {
            final String mout = FileSpammer.msgs.get(0);
            final String g = "> ";
            FileSpammer.mc.player.connection.sendPacket((Packet)new CPacketChatMessage(((boolean)this.greenT.getValue()) ? (g + mout) : mout));
            FileSpammer.msgs.remove(0);
            this.timer.reset();
        }
    }
    
    @Override
    public void onLoad() {
        try {
            this.getText();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void onEnable() {
        final String filet = "renosense/spammers/";
        final File path = new File(filet);
        if (!path.exists()) {
            Command.sendDebugMessage("There was no folder! Creating one now.", (Module)this);
            path.mkdir();
        }
        try {
            this.getText();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        Command.sendDebugMessage("msgs size " + FileSpammer.msgs.size(), (Module)this);
        this.timer.setDelay(this.delay.getValue() - 1);
    }
    
    @Override
    public void onUpdate() {
        final File file = new File("renosense/spammers/" + this.fileName.getValue() + ".txt");
        if (!file.exists()) {
            Command.sendDebugMessage("There is no file with the name " + this.fileName.getValue() + ".", (Module)this);
            if (fullNullCheck()) {
                this.disable();
            }
            return;
        }
        if (this.act.getValue()) {
            FileSpammer.mc.displayGuiScreen((GuiScreen)new SpammerManager((GuiScreen)new RenoSenseGui(), Minecraft.getMinecraft()));
        }
        if (FileSpammer.msgs.size() == 0 && this.loop.getValue()) {
            try {
                this.getText();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (FileSpammer.msgs.size() == 0 && !this.loop.getValue()) {
            Command.sendDebugMessage("Spam File ended, disabling.", (Module)this);
            this.disable();
        }
        if (!this.timer.passedMs(this.delay.getValue() * 1000)) {
            return;
        }
        try {
            this.doSend();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.timer.reset();
    }
    
    @Override
    public void onDisable() {
        FileSpammer.msgs.clear();
        this.timer.reset();
    }
    
    public void getText() throws IOException {
        FileSpammer.msgs.clear();
        final List<String> texts = new ArrayList<String>();
        final File file = new File("renosense/spammers/" + this.fileName.getValue() + ".txt");
        if (!file.exists()) {
            Command.sendDebugMessage("There is no file with the name " + this.fileName.getValue() + ".", (Module)this);
            if (!fullNullCheck()) {
                this.disable();
            }
            return;
        }
        final BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath(), new OpenOption[0])));
        String line;
        while ((line = reader.readLine()) != null) {
            if (!texts.contains(line)) {
                texts.add(line);
                FileSpammer.msgs.add(line);
            }
        }
    }
    
    static {
        FileSpammer.msgs = new ArrayList<String>();
    }
    
    public enum SpamFiles
    {
    }
}
