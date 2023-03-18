//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.command.commands;

import me.sjnez.renosense.features.command.*;
import me.sjnez.renosense.*;
import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.util.*;
import net.minecraft.util.text.*;
import me.sjnez.renosense.features.setting.*;
import com.google.gson.*;
import com.mojang.realmsclient.gui.*;
import me.sjnez.renosense.manager.*;
import me.sjnez.renosense.features.*;
import java.util.*;

public class ModuleCommand extends Command
{
    public ModuleCommand() {
        super("module", "Sends a list of modules, lets you change values of module settings.", new String[] { "<module>", "<set/reset/toggle>", "<setting>", "<value>" });
    }
    
    public void execute(final String[] commands) {
        if (commands.length == 1) {
            sendMessage("Modules: ");
            for (final Module.Category category : RenoSense.moduleManager.getCategories()) {
                final String modules = category.getName() + ": ";
                for (final Module module1 : RenoSense.moduleManager.getModulesByCategory(category)) {
                    final ChatHelper ch = new ChatHelper();
                    ch.sendMessage((ITextComponent)new ChatComponent(module1.getName()).gray().setUrl(RenoSense.commandManager.getPrefix() + "module " + module1.getName() + " toggle", "Toggle this"));
                }
            }
            return;
        }
        Module module2 = RenoSense.moduleManager.getModuleByDisplayName(commands[0]);
        if (module2 == null) {
            module2 = RenoSense.moduleManager.getModuleByName(commands[0]);
            if (module2 == null) {
                sendMessage("This module doesnt exist.");
                return;
            }
            sendMessage("This is the original name of the module. Its current name is: " + module2.getDisplayName());
        }
        else {
            if (commands.length == 2) {
                sendMessage(module2.getDisplayName() + " : " + module2.getDescription());
                for (final Setting setting2 : module2.getSettings()) {
                    final ChatHelper ch2 = new ChatHelper();
                    ch2.sendMessage((ITextComponent)new ChatComponent(setting2.getName()).gray().setHover((ITextComponent)new ChatComponent(setting2.getValue().toString()).lightPurple()));
                }
                return;
            }
            if (commands.length == 3) {
                if (commands[1].equalsIgnoreCase("toggle")) {
                    module2.toggle();
                }
                if (commands[1].equalsIgnoreCase("set")) {
                    sendMessage("Please specify a setting.");
                }
                else if (commands[1].equalsIgnoreCase("reset")) {
                    for (final Setting setting3 : module2.getSettings()) {
                        setting3.setValue(setting3.getDefaultValue());
                    }
                }
                else {
                    sendMessage("This command doesn't exist.");
                }
                return;
            }
            if (commands.length == 4) {
                sendMessage("Please specify a value.");
                return;
            }
            final Setting setting4;
            if (commands.length == 5 && (setting4 = module2.getSettingByName(commands[2])) != null) {
                final JsonParser jp = new JsonParser();
                if (setting4.getType().equalsIgnoreCase("String")) {
                    setting4.setValue(commands[3]);
                    sendMessage(ChatFormatting.DARK_GRAY + module2.getName() + " " + setting4.getName() + " has been set to " + commands[3] + ".");
                    return;
                }
                try {
                    if (setting4.getName().equalsIgnoreCase("Enabled")) {
                        if (commands[3].equalsIgnoreCase("true")) {
                            module2.enable();
                        }
                        if (commands[3].equalsIgnoreCase("false")) {
                            module2.disable();
                        }
                    }
                    ConfigManager.setValueFromJson(module2, setting4, jp.parse(commands[3]));
                }
                catch (Exception e) {
                    sendMessage("Bad Value! This setting requires a: " + setting4.getType() + " value.");
                    return;
                }
                if (!setting4.getName().equalsIgnoreCase("Enabled")) {
                    sendMessage(ChatFormatting.GRAY + module2.getName() + " " + setting4.getName() + " has been set to " + commands[3] + ".");
                }
            }
        }
    }
}
