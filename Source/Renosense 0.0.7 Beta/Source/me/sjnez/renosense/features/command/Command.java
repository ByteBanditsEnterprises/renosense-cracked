//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.command;

import me.sjnez.renosense.features.*;
import me.sjnez.renosense.features.modules.client.*;
import com.mojang.realmsclient.gui.*;
import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.*;
import net.minecraft.util.text.*;
import java.util.regex.*;

public abstract class Command extends Feature
{
    protected String name;
    protected String description;
    protected String[] commands;
    
    public Command(final String name, final String description) {
        super(name);
        this.name = name;
        this.description = description;
        this.commands = new String[] { "" };
    }
    
    public Command(final String name, final String description, final String[] commands) {
        super(name);
        this.name = name;
        this.description = description;
        this.commands = commands;
    }
    
    public static void sendMessage(final String message) {
        sendSilentMessage(HUD.getInstance().getCommandMessage() + ChatFormatting.GRAY + message);
    }
    
    public static void sendDebugMessage(final String message, final Module module) {
        if (module.debug.getValue()) {
            sendSilentMessage(HUD.getInstance().getCommandMessage() + ChatFormatting.GOLD + "[" + ChatFormatting.YELLOW + "Debug" + ChatFormatting.GOLD + "] " + ChatFormatting.YELLOW + message);
        }
    }
    
    public static void sendSilentMessage(final String message) {
        if (nullCheck()) {
            return;
        }
        Command.mc.player.sendMessage((ITextComponent)new ChatMessage(message));
    }
    
    public static String getCommandPrefix() {
        return RenoSense.commandManager.getPrefix();
    }
    
    public abstract void execute(final String[] p0);
    
    @Override
    public String getName() {
        return this.name;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public String[] getCommands() {
        return this.commands;
    }
    
    public static class ChatMessage extends TextComponentBase
    {
        private final String text;
        
        public ChatMessage(final String text) {
            final Pattern pattern = Pattern.compile("&[0123456789abcdefrlosmk]");
            final Matcher matcher = pattern.matcher(text);
            final StringBuffer stringBuffer = new StringBuffer();
            while (matcher.find()) {
                final String replacement = matcher.group().substring(1);
                matcher.appendReplacement(stringBuffer, replacement);
            }
            matcher.appendTail(stringBuffer);
            this.text = stringBuffer.toString();
        }
        
        public String getUnformattedComponentText() {
            return this.text;
        }
        
        public ITextComponent createCopy() {
            return null;
        }
        
        public ITextComponent shallowCopy() {
            return (ITextComponent)new ChatMessage(this.text);
        }
    }
}
