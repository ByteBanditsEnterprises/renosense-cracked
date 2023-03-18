//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.command.commands;

import me.sjnez.renosense.features.command.*;
import me.sjnez.renosense.*;
import com.mojang.realmsclient.gui.*;
import me.sjnez.renosense.util.*;
import net.minecraft.util.text.*;
import java.util.*;

public class HelpCommand extends Command
{
    public HelpCommand() {
        super("help", "Sends a list of commands");
    }
    
    public void execute(final String[] commands) {
        sendMessage("Commands: ");
        for (final Command command : RenoSense.commandManager.getCommands()) {
            final ChatHelper ch = new ChatHelper();
            final String t = ChatFormatting.GRAY + RenoSense.commandManager.getPrefix() + command.getName();
            ch.sendMessage((ITextComponent)new ChatComponent(t).setUrl(RenoSense.commandManager.getPrefix() + command.getName(), (ITextComponent)new ChatComponent(command.getDescription()).green()));
        }
    }
}
