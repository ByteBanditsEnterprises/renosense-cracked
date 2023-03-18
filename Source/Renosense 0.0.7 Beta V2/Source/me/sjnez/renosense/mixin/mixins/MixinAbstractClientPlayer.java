//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.mixin.mixins;

import net.minecraft.client.entity.*;
import org.spongepowered.asm.mixin.*;
import javax.annotation.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import net.minecraft.util.*;
import net.minecraft.client.network.*;
import net.minecraft.client.*;
import java.util.*;
import me.sjnez.renosense.features.command.*;
import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.features.modules.client.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ AbstractClientPlayer.class })
public abstract class MixinAbstractClientPlayer
{
    @Shadow
    @Nullable
    protected abstract NetworkPlayerInfo getPlayerInfo();
    
    @Inject(method = { "getLocationSkin()Lnet/minecraft/util/ResourceLocation;" }, at = { @At("HEAD") }, cancellable = true)
    public void getLocationSkin(final CallbackInfoReturnable<ResourceLocation> cir) {
        final ResourceLocation old = (ResourceLocation)cir.getReturnValue();
        final NetworkPlayerInfo info = Objects.requireNonNull(Minecraft.getMinecraft().getConnection()).getPlayerInfo(Minecraft.getMinecraft().getSession().getUsername());
        if (this.getPlayerInfo() != null && info != null && info.equals(this.getPlayerInfo())) {
            final ResourceLocation mark = new ResourceLocation("minecraft", "skins/" + NickHider.getInstance().skinFile.getValueAsString() + ".png");
            if (NickHider.getInstance().isOn() && (boolean)NickHider.getInstance().skinChanger.getValue()) {
                if ((boolean)NickHider.getInstance().debug.getValue() && NickHider.debugTimer.passedMs((int)NickHider.getInstance().debugDelay.getValue())) {
                    Command.sendDebugMessage("Path: " + mark.toString(), (Module)NickHider.getInstance());
                    NickHider.debugTimer.reset();
                }
                cir.setReturnValue((Object)mark);
            }
            else if (old != null) {
                cir.setReturnValue((Object)old);
            }
        }
        final NetworkPlayerInfo networkplayerinfo = Objects.requireNonNull(Minecraft.getMinecraft().getConnection()).getPlayerInfo((String)MontageEnder.getInstance().nameString.getValue());
        if (networkplayerinfo != null && this.getPlayerInfo() != null && networkplayerinfo.equals(this.getPlayerInfo())) {
            final String s = MontageEnder.getInstance().skinFile.getValueAsString();
            final ResourceLocation mark2 = new ResourceLocation("minecraft", "skins/" + s + ".png");
            if (MontageEnder.getInstance().isOn()) {
                cir.setReturnValue((Object)mark2);
            }
            else if (old != null) {
                cir.setReturnValue((Object)old);
            }
        }
    }
    
    @Inject(method = { "getLocationCape" }, at = { @At("HEAD") }, cancellable = true)
    public void getLocationCape(final CallbackInfoReturnable<ResourceLocation> cir) {
        final ResourceLocation old = (ResourceLocation)cir.getReturnValue();
        final NetworkPlayerInfo networkplayerinfo = Objects.requireNonNull(Minecraft.getMinecraft().getConnection()).getPlayerInfo((String)MontageEnder.getInstance().nameString.getValue());
        if (networkplayerinfo != null && this.getPlayerInfo() != null && networkplayerinfo.equals(this.getPlayerInfo())) {
            final String s = ((MontageEnder.Cape)MontageEnder.getInstance().cape.getValue()).equals((Object)MontageEnder.Cape.MINECON) ? "minecon" : (((MontageEnder.Cape)MontageEnder.getInstance().cape.getValue()).equals((Object)MontageEnder.Cape.NONE) ? "none" : MontageEnder.getInstance().capeFile.getValueAsString());
            final ResourceLocation mark = new ResourceLocation("minecraft", "skins/" + s + ".png");
            if (MontageEnder.getInstance().isOn()) {
                cir.setReturnValue((Object)mark);
            }
            else if (old != null) {
                cir.setReturnValue((Object)old);
            }
        }
    }
}
