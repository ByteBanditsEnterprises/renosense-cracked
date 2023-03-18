//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules;

import me.sjnez.renosense.features.*;
import me.sjnez.renosense.features.setting.*;
import com.mojang.realmsclient.gui.*;
import me.sjnez.renosense.features.modules.client.*;
import net.minecraft.util.text.*;
import net.minecraftforge.common.*;
import me.sjnez.renosense.event.events.*;
import net.minecraftforge.fml.common.eventhandler.*;
import me.sjnez.renosense.*;
import me.sjnez.renosense.features.command.*;

public class Module extends Feature
{
    private final String description;
    private final Category category;
    public Setting<Boolean> enabled;
    public Setting<Boolean> drawn;
    public Setting<Boolean> debug;
    public Setting<Bind> bind;
    public Setting<String> displayName;
    public boolean hasListener;
    public boolean alwaysListening;
    public boolean hidden;
    public float arrayListOffset;
    public float arrayListVOffset;
    public float offset;
    public float vOffset;
    public boolean sliding;
    
    public Module(final String name, final String description, final Category category, final boolean hasListener, final boolean hidden, final boolean alwaysListening) {
        super(name);
        this.enabled = (Setting<Boolean>)this.register(new Setting("Enabled", (T)false));
        this.drawn = (Setting<Boolean>)this.register(new Setting("Drawn", (T)true));
        this.debug = (Setting<Boolean>)this.register(new Setting("Debug", (T)false));
        this.bind = (Setting<Bind>)this.register(new Setting("Keybind", (T)new Bind(-1)));
        this.arrayListOffset = 0.0f;
        this.arrayListVOffset = 0.0f;
        this.displayName = (Setting<String>)this.register(new Setting("DisplayName", (T)name));
        this.description = description;
        this.category = category;
        this.hasListener = hasListener;
        this.hidden = hidden;
        this.alwaysListening = alwaysListening;
    }
    
    public boolean isSliding() {
        return this.sliding;
    }
    
    public void onEnable() {
    }
    
    public void onDisable() {
    }
    
    public void onToggle() {
    }
    
    public void onLoad() {
    }
    
    public void onTick() {
    }
    
    public void onLogin() {
    }
    
    public void onLogout() {
    }
    
    public void onUpdate() {
    }
    
    public void onRender2D(final Render2DEvent event) {
    }
    
    public void onRender3D(final Render3DEvent event) {
    }
    
    public void onUnload() {
    }
    
    public String getDisplayInfo() {
        return null;
    }
    
    public boolean isOn() {
        return this.enabled.getValue();
    }
    
    public boolean isOff() {
        return !this.enabled.getValue();
    }
    
    public void setEnabled(final boolean enabled) {
        if (enabled) {
            this.enable();
        }
        else {
            this.disable();
        }
    }
    
    public TextComponentString getNotifierOn() {
        if (ModuleTools.getInstance().isEnabled()) {
            switch (ModuleTools.getInstance().notifier.getValue()) {
                case FUTURE: {
                    final TextComponentString text = new TextComponentString(ChatFormatting.RED + "[Future] " + ChatFormatting.GRAY + this.getDisplayName() + " toggled " + ChatFormatting.GREEN + "on" + ChatFormatting.GRAY + ".");
                    return text;
                }
                case DOTGOD: {
                    final TextComponentString text = new TextComponentString(ChatFormatting.DARK_PURPLE + "[" + ChatFormatting.LIGHT_PURPLE + "DotGod.CC" + ChatFormatting.DARK_PURPLE + "] " + ChatFormatting.DARK_AQUA + this.getDisplayName() + ChatFormatting.LIGHT_PURPLE + " was " + ChatFormatting.GREEN + "enabled.");
                    return text;
                }
                case PHOBOS: {
                    final TextComponentString text = new TextComponentString(HUD.getInstance().getCommandMessage() + ChatFormatting.BOLD + this.getDisplayName() + ChatFormatting.RESET + ChatFormatting.GREEN + " enabled.");
                    return text;
                }
                case TROLLGOD: {
                    final TextComponentString text = new TextComponentString(HUD.getInstance().getCommandMessage() + ChatFormatting.DARK_PURPLE + this.getDisplayName() + ChatFormatting.LIGHT_PURPLE + " was " + ChatFormatting.GREEN + "enabled.");
                    return text;
                }
            }
        }
        final TextComponentString text = new TextComponentString(HUD.getInstance().getCommandMessage() + ChatFormatting.GREEN + this.getDisplayName() + " toggled on.");
        return text;
    }
    
    public TextComponentString getNotifierOff() {
        if (ModuleTools.getInstance().isEnabled()) {
            switch (ModuleTools.getInstance().notifier.getValue()) {
                case FUTURE: {
                    final TextComponentString text = new TextComponentString(ChatFormatting.RED + "[Future] " + ChatFormatting.GRAY + this.getDisplayName() + " toggled " + ChatFormatting.RED + "off" + ChatFormatting.GRAY + ".");
                    return text;
                }
                case DOTGOD: {
                    final TextComponentString text = new TextComponentString(ChatFormatting.DARK_PURPLE + "[" + ChatFormatting.LIGHT_PURPLE + "DotGod.CC" + ChatFormatting.DARK_PURPLE + "] " + ChatFormatting.DARK_AQUA + this.getDisplayName() + ChatFormatting.LIGHT_PURPLE + " was " + ChatFormatting.RED + "disabled.");
                    return text;
                }
                case PHOBOS: {
                    final TextComponentString text = new TextComponentString(HUD.getInstance().getCommandMessage() + ChatFormatting.BOLD + this.getDisplayName() + ChatFormatting.RESET + ChatFormatting.RED + " disabled.");
                    return text;
                }
                case TROLLGOD: {
                    final TextComponentString text = new TextComponentString(HUD.getInstance().getCommandMessage() + ChatFormatting.DARK_PURPLE + this.getDisplayName() + ChatFormatting.LIGHT_PURPLE + " was " + ChatFormatting.RED + "disabled.");
                    return text;
                }
            }
        }
        final TextComponentString text = new TextComponentString(HUD.getInstance().getCommandMessage() + ChatFormatting.RED + this.getDisplayName() + " toggled off.");
        return text;
    }
    
    public void enable() {
        this.enabled.setValue(Boolean.TRUE);
        this.onToggle();
        this.onEnable();
        if (HUD.getInstance().notifyToggles.getValue()) {
            Module.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion((ITextComponent)this.getNotifierOn(), 1);
            if (this.isOn() && this.hasListener && !this.alwaysListening) {
                MinecraftForge.EVENT_BUS.register((Object)this);
            }
        }
    }
    
    public void disable() {
        if (this.hasListener && !this.alwaysListening) {
            MinecraftForge.EVENT_BUS.unregister((Object)this);
        }
        this.enabled.setValue(false);
        if (HUD.getInstance().notifyToggles.getValue()) {
            Module.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion((ITextComponent)this.getNotifierOff(), 1);
        }
        this.onToggle();
        this.onDisable();
    }
    
    public void toggle() {
        final ClientEvent event = new ClientEvent((int)(this.isEnabled() ? 0 : 1), (Feature)this);
        MinecraftForge.EVENT_BUS.post((Event)event);
        if (!event.isCanceled()) {
            this.setEnabled(!this.isEnabled());
        }
    }
    
    public String getDisplayName() {
        return this.displayName.getValue();
    }
    
    public void setDisplayName(final String name) {
        final Module module = RenoSense.moduleManager.getModuleByDisplayName(name);
        final Module originalModule = RenoSense.moduleManager.getModuleByName(name);
        if (module == null && originalModule == null) {
            Command.sendMessage(this.getDisplayName() + ", name: " + this.getName() + ", has been renamed to: " + name);
            this.displayName.setValue(name);
            return;
        }
        Command.sendMessage(ChatFormatting.RED + "A module of this name already exists.");
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public boolean isDebug() {
        return this.debug.getValue();
    }
    
    public void setDebug(final boolean debug) {
        this.debug.setValue(debug);
    }
    
    public boolean isDrawn() {
        return this.drawn.getValue();
    }
    
    public void setDrawn(final boolean drawn) {
        this.drawn.setValue(drawn);
    }
    
    public Category getCategory() {
        return this.category;
    }
    
    public String getInfo() {
        return null;
    }
    
    public Bind getBind() {
        return this.bind.getValue();
    }
    
    public void setBind(final int key) {
        this.bind.setValue(new Bind(key));
    }
    
    public boolean listening() {
        return (this.hasListener && this.isOn()) || this.alwaysListening;
    }
    
    public String getFullArrayString() {
        return this.getDisplayName() + ChatFormatting.GRAY + ((this.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + this.getDisplayInfo() + ChatFormatting.GRAY + "]") : "");
    }
    
    public enum Category
    {
        COMBAT("Combat"), 
        MISC("Misc"), 
        MOVEMENT("Movement"), 
        RENDER("Render"), 
        PLAYER("Player"), 
        CLIENT("Client");
        
        private final String name;
        
        private Category(final String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }
    }
}
