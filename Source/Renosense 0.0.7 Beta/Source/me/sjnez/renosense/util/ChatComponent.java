//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.util;

import net.minecraft.util.text.*;
import net.minecraft.util.text.event.*;
import java.io.*;

public class ChatComponent extends TextComponentString
{
    public ChatComponent(final String msg) {
        super(msg);
    }
    
    public ChatComponent black() {
        this.setStyle(this.getStyle().setColor(TextFormatting.BLACK));
        return this;
    }
    
    public ChatComponent darkBlue() {
        this.setStyle(this.getStyle().setColor(TextFormatting.DARK_BLUE));
        return this;
    }
    
    public ChatComponent darkGreen() {
        this.setStyle(this.getStyle().setColor(TextFormatting.DARK_GREEN));
        return this;
    }
    
    public ChatComponent darkAqua() {
        this.setStyle(this.getStyle().setColor(TextFormatting.DARK_AQUA));
        return this;
    }
    
    public ChatComponent darkRed() {
        this.setStyle(this.getStyle().setColor(TextFormatting.DARK_RED));
        return this;
    }
    
    public ChatComponent darkPurple() {
        this.setStyle(this.getStyle().setColor(TextFormatting.DARK_PURPLE));
        return this;
    }
    
    public ChatComponent gold() {
        this.setStyle(this.getStyle().setColor(TextFormatting.GOLD));
        return this;
    }
    
    public ChatComponent gray() {
        this.setStyle(this.getStyle().setColor(TextFormatting.GRAY));
        return this;
    }
    
    public ChatComponent darkGray() {
        this.setStyle(this.getStyle().setColor(TextFormatting.DARK_GRAY));
        return this;
    }
    
    public ChatComponent blue() {
        this.setStyle(this.getStyle().setColor(TextFormatting.BLUE));
        return this;
    }
    
    public ChatComponent green() {
        this.setStyle(this.getStyle().setColor(TextFormatting.GREEN));
        return this;
    }
    
    public ChatComponent aqua() {
        this.setStyle(this.getStyle().setColor(TextFormatting.AQUA));
        return this;
    }
    
    public ChatComponent red() {
        this.setStyle(this.getStyle().setColor(TextFormatting.RED));
        return this;
    }
    
    public ChatComponent lightPurple() {
        this.setStyle(this.getStyle().setColor(TextFormatting.LIGHT_PURPLE));
        return this;
    }
    
    public ChatComponent yellow() {
        this.setStyle(this.getStyle().setColor(TextFormatting.YELLOW));
        return this;
    }
    
    public ChatComponent white() {
        this.setStyle(this.getStyle().setColor(TextFormatting.WHITE));
        return this;
    }
    
    public ChatComponent obfuscated() {
        this.setStyle(this.getStyle().setObfuscated(Boolean.valueOf(true)));
        return this;
    }
    
    public ChatComponent bold() {
        this.setStyle(this.getStyle().setBold(Boolean.valueOf(true)));
        return this;
    }
    
    public ChatComponent strikethrough() {
        this.setStyle(this.getStyle().setStrikethrough(Boolean.valueOf(true)));
        return this;
    }
    
    public ChatComponent underline() {
        this.setStyle(this.getStyle().setUnderlined(Boolean.valueOf(true)));
        return this;
    }
    
    public ChatComponent italic() {
        this.setStyle(this.getStyle().setItalic(Boolean.valueOf(true)));
        return this;
    }
    
    public ChatComponent reset() {
        this.setStyle(this.getStyle().setParentStyle((Style)null).setBold(Boolean.valueOf(false)).setItalic(Boolean.valueOf(false)).setObfuscated(Boolean.valueOf(false)).setUnderlined(Boolean.valueOf(false)).setStrikethrough(Boolean.valueOf(false)));
        return this;
    }
    
    public ChatComponent setHover(final ITextComponent hover) {
        this.setStyle(this.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover)));
        return this;
    }
    
    public ChatComponent setUrl(final String url) {
        return this.setUrl(url, (ITextComponent)new KeyValueTooltipComponent("Click to visit", url));
    }
    
    public ChatComponent setUrl(final String url, final String hover) {
        return this.setUrl(url, (ITextComponent)new ChatComponent(hover).yellow());
    }
    
    public ChatComponent setUrl(final String url, final ITextComponent hover) {
        this.setStyle(this.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, url)));
        this.setHover(hover);
        return this;
    }
    
    public ChatComponent setOpenFile(final File filePath) {
        this.setStyle(this.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, filePath.getAbsolutePath())));
        this.setHover((ITextComponent)new ChatComponent(filePath.isFile() ? ("Open " + filePath.getName()) : ("Open folder: " + filePath.toString())).yellow());
        return this;
    }
    
    public ChatComponent setSuggestCommand(final String command) {
        this.setSuggestCommand(command, true);
        return this;
    }
    
    public ChatComponent setSuggestCommand(final String command, final boolean addTooltip) {
        this.setStyle(this.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)));
        if (addTooltip) {
            this.setHover((ITextComponent)new KeyValueChatComponent("Run", command, " "));
        }
        return this;
    }
    
    public ChatComponent appendSibling(final ITextComponent component) {
        super.appendSibling(component);
        return this;
    }
    
    public ChatComponent appendFreshSibling(final ITextComponent sibling) {
        this.siblings.add(new TextComponentString("\n").appendSibling(sibling));
        return this;
    }
    
    public static class KeyValueChatComponent extends ChatComponent
    {
        public KeyValueChatComponent(final String key, final String value) {
            this(key, value, ": ");
        }
        
        public KeyValueChatComponent(final String key, final String value, final String separator) {
            super(key);
            this.appendText(separator);
            this.gold().appendSibling((ITextComponent)new ChatComponent(value).yellow());
        }
    }
    
    public static class KeyValueTooltipComponent extends ChatComponent
    {
        public KeyValueTooltipComponent(final String key, final String value) {
            super(key);
            this.appendText(": ");
            this.gray().appendSibling((ITextComponent)new ChatComponent(value).yellow());
        }
    }
}
