//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.command.commands;

import me.sjnez.renosense.features.command.*;
import java.awt.*;
import java.io.*;

public class FolderCommand extends Command
{
    public FolderCommand() {
        super("folder", "Opens the renosense folder.");
    }
    
    public void execute(final String[] commands) {
        try {
            Desktop.getDesktop().open(new File("renosense/"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
