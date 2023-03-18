//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.command.commands;

import me.sjnez.renosense.features.command.*;
import net.minecraft.client.audio.*;
import net.minecraftforge.fml.common.*;
import com.mojang.realmsclient.gui.*;

public class ReloadSoundCommand extends Command
{
    public ReloadSoundCommand() {
        super("sound", "Reload sound.", new String[0]);
    }
    
    public void execute(final String[] commands) {
        try {
            final SoundManager sndManager = (SoundManager)ObfuscationReflectionHelper.getPrivateValue((Class)SoundHandler.class, (Object)ReloadSoundCommand.mc.getSoundHandler(), new String[] { "sndManager", "sndManager" });
            sndManager.reloadSoundSystem();
            Command.sendMessage(ChatFormatting.GREEN + "Reloaded Sound System.");
        }
        catch (Exception e) {
            System.out.println(ChatFormatting.RED + "Could not restart sound manager: " + e.toString());
            e.printStackTrace();
            Command.sendMessage(ChatFormatting.RED + "Couldnt Reload Sound System!");
        }
    }
}
