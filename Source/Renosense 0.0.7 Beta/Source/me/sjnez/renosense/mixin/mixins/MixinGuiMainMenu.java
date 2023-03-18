//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.mixin.mixins;

import org.spongepowered.asm.mixin.*;
import net.minecraft.client.gui.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import java.awt.*;
import java.net.*;
import me.sjnez.renosense.features.gui.alt.*;
import net.minecraft.client.*;
import me.sjnez.renosense.features.gui.management.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ GuiMainMenu.class })
public abstract class MixinGuiMainMenu extends GuiScreen
{
    @Inject(method = { "actionPerformed" }, at = { @At("HEAD") })
    public void actionPerformed(final GuiButton button, final CallbackInfo ci) {
        if (button.id == 1000) {
            try {
                Desktop.getDesktop().browse(URI.create("https://discord.gg/kncfFHmfc4"));
            }
            catch (Exception ex) {}
        }
        if (button.id == 6969) {
            AltGui.isActive = !AltGui.isActive;
        }
        if (button.id == 613) {
            Minecraft.getMinecraft().displayGuiScreen((GuiScreen)new RSManagerGui((GuiScreen)new GuiMainMenu(), Minecraft.getMinecraft()));
        }
    }
    
    @Inject(method = { "drawScreen" }, at = { @At("RETURN") })
    private void drawScreen(final int mouseX, final int mouseY, final float partialTicks, final CallbackInfo ci) {
        AltGui.drawScreen(mouseX, mouseY);
    }
    
    @Inject(method = { "keyTyped" }, at = { @At("HEAD") })
    private void keyTyped(final char typedChar, final int keyCode, final CallbackInfo ci) {
        AltGui.keyTyped(typedChar, keyCode);
    }
    
    @Inject(method = { "mouseClicked" }, at = { @At("HEAD") })
    private void mouseClicked(final int mouseX, final int mouseY, final int mouseButton, final CallbackInfo ci) {
        AltGui.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
