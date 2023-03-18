//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.command.commands;

import me.sjnez.renosense.features.command.*;
import me.sjnez.renosense.*;

public class ResetCommand extends Command
{
    public ResetCommand() {
        super("reset", "Resets the config", new String[0]);
    }
    
    public void execute(final String[] commands) {
        RenoSense.configManager.resetConfig(false, "");
    }
}
