//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.command.commands;

import me.sjnez.renosense.features.command.*;
import me.sjnez.renosense.*;

public class ToggleCommand extends Command
{
    public ToggleCommand() {
        super("Toggle", "Toggles modules", new String[] { "<module>" });
    }
    
    public void execute(final String[] commands) {
        if (commands.length == 1) {
            Command.sendMessage("Please enter the module you would like to toggle!");
        }
        if (commands.length == 2) {
            RenoSense.moduleManager.getModuleByName(commands[0]).toggle();
        }
    }
}
