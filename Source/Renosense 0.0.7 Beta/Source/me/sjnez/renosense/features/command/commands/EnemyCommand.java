//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.command.commands;

import me.sjnez.renosense.features.command.*;
import me.sjnez.renosense.*;
import me.sjnez.renosense.manager.*;
import com.mojang.realmsclient.gui.*;
import java.util.*;

public class EnemyCommand extends Command
{
    public EnemyCommand() {
        super("enemy", "Lets you add and delete enemies, gets enemy list. Put the scale of how much of an enemy they are after their name.", new String[] { "<add/del/name/clear/edit>", "<name>", "<priority>" });
    }
    
    public void execute(final String[] commands) {
        if (commands.length == 1) {
            if (RenoSense.enemyManager.getEnemies().isEmpty()) {
                sendMessage("Enemy list is empty");
            }
            else {
                sendMessage("Enemies:");
                for (final EnemyManager.Enemy enemy : RenoSense.enemyManager.getEnemies()) {
                    try {
                        final String f = enemy.getUsername() + ", Enemy scale " + enemy.getPriority() + "/2";
                        sendMessage(f);
                    }
                    catch (Exception ex) {}
                }
            }
            return;
        }
        if (commands.length != 2) {
            if (commands.length >= 2) {
                final String s = commands[0];
                switch (s) {
                    case "edit": {
                        final int t = (commands[2] == null) ? 1 : Integer.parseInt(commands[2]);
                        if (RenoSense.friendManager.isFriend(commands[1])) {
                            sendMessage(ChatFormatting.RED + commands[1] + " is a FRIEND, they can't be an enemy unless you unfriend them!");
                            return;
                        }
                        sendMessage(ChatFormatting.GREEN + commands[1] + " has been edited to be priority " + t + "/2");
                        RenoSense.enemyManager.removeEnemy(commands[1]);
                        RenoSense.enemyManager.addEnemy(commands[1], t);
                    }
                    case "add": {
                        final int t = (commands[2] == null) ? 1 : Integer.parseInt(commands[2]);
                        RenoSense.enemyManager.addEnemy(commands[1], t);
                    }
                    case "del": {
                        RenoSense.enemyManager.removeEnemy(commands[1]);
                    }
                    default: {
                        sendMessage("Unknown Command, try enemy add/del (name)");
                        break;
                    }
                }
            }
            return;
        }
        final String s2 = commands[0];
        switch (s2) {
            case "reset":
            case "clear": {
                RenoSense.enemyManager.onLoad();
                sendMessage("Enemies got reset.");
            }
            default: {
                sendMessage(commands[0] + (RenoSense.enemyManager.isEnemy(commands[0]) ? " is an enemy." : " isn't an enemy."));
            }
        }
    }
}
