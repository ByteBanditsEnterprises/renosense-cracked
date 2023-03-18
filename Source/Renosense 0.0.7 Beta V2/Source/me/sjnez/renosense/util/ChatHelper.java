//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.util;

import java.util.regex.*;
import java.util.*;
import net.minecraft.client.*;
import net.minecraft.util.text.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.*;
import net.minecraftforge.fml.common.eventhandler.*;

public class ChatHelper
{
    private static final Pattern USELESS_JSON_CONTENT_PATTERN;
    private static final int DISPLAY_DURATION = 5000;
    private final List<ITextComponent> offlineMessages;
    private String[] aboveChatMessage;
    private long aboveChatMessageExpiration;
    
    public ChatHelper() {
        this.offlineMessages = new ArrayList<ITextComponent>();
    }
    
    public void sendMessage(final TextFormatting color, final String text) {
        this.sendMessage(new TextComponentString(text).setStyle(new Style().setColor(color)));
    }
    
    public void sendMessage(final ITextComponent chatComponent) {
        if (Minecraft.getMinecraft().player == null) {
            this.putOfflineMessage(chatComponent);
        }
        else {
            final ClientChatReceivedEvent event = new ClientChatReceivedEvent(ChatType.SYSTEM, chatComponent);
            MinecraftForge.EVENT_BUS.post((Event)event);
            if (!event.isCanceled()) {
                Minecraft.getMinecraft().player.sendMessage(event.getMessage());
            }
        }
    }
    
    private void putOfflineMessage(final ITextComponent chatComponent) {
        if (this.offlineMessages.size() == 0) {
            MinecraftForge.EVENT_BUS.register((Object)this);
        }
        this.offlineMessages.add(chatComponent);
    }
    
    static {
        USELESS_JSON_CONTENT_PATTERN = Pattern.compile("\"[A-Za-z]+\":false,?");
    }
}
