//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.mixin.mixins;

import org.spongepowered.asm.mixin.*;
import net.minecraft.client.gui.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import java.io.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ GuiDownloadTerrain.class })
public abstract class MixinGuiDownloadTerrain extends GuiScreen
{
    @Inject(method = { "drawScreen" }, at = { @At("HEAD") }, cancellable = true)
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks, final CallbackInfo ci) throws IOException {
        this.drawBackground(0);
        ci.cancel();
    }
}
