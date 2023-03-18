//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.mixin.mixins;

import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.network.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import org.spongepowered.asm.mixin.injection.*;
import net.minecraft.util.text.*;
import java.io.*;

@Mixin({ GuiConnecting.class })
public class MixinGuiConnecting extends GuiScreen
{
    @Shadow
    private boolean cancel;
    @Shadow
    private NetworkManager networkManager;
    @Shadow
    @Final
    private GuiScreen previousGuiScreen;
    
    @Inject(method = { "drawScreen" }, at = { @At("HEAD") }, cancellable = true)
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks, final CallbackInfo ci) {
        this.drawDefaultBackground();
        ci.cancel();
    }
    
    @Inject(method = { "keyTyped" }, at = { @At("HEAD") })
    protected void keyTyped(final char typedChar, final int keyCode, final CallbackInfo ci) throws IOException {
        if (keyCode == 1) {
            this.cancel = true;
            if (this.networkManager != null) {
                this.networkManager.closeChannel((ITextComponent)new TextComponentString("Aborted"));
            }
            this.mc.displayGuiScreen(this.previousGuiScreen);
        }
    }
}
