//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.mixin.mixins;

import org.spongepowered.asm.mixin.*;
import net.minecraft.network.*;
import net.minecraft.network.play.client.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ NetHandlerPlayServer.class })
public class MixinNetHandlerPlayServer
{
    @Inject(method = { "processUseEntity" }, at = { @At(value = "INVOKE", target = "Lnet/minecraft/network/NetHandlerPlayServer;disconnect(Lnet/minecraft/util/text/ITextComponent;)V") }, cancellable = true)
    private void processUseEntity(final CPacketUseEntity packetIn, final CallbackInfo ci) {
        ci.cancel();
    }
}
