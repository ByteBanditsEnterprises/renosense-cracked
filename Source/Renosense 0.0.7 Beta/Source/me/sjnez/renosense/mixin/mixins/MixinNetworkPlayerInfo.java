//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.mixin.mixins;

import com.mojang.authlib.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import net.minecraft.util.*;
import net.minecraft.client.network.*;
import net.minecraft.client.*;
import java.util.*;
import me.sjnez.renosense.features.modules.client.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ NetworkPlayerInfo.class })
public abstract class MixinNetworkPlayerInfo
{
    @Shadow
    public abstract GameProfile getGameProfile();
    
    @Inject(method = { "getLocationSkin" }, at = { @At("HEAD") }, cancellable = true)
    public void getLocationSkin(final CallbackInfoReturnable<ResourceLocation> cir) {
        final ResourceLocation old = (ResourceLocation)cir.getReturnValue();
        final NetworkPlayerInfo info = Objects.requireNonNull(Minecraft.getMinecraft().getConnection()).getPlayerInfo(Minecraft.getMinecraft().getSession().getUsername());
        if (info != null && this.getGameProfile().equals((Object)info.getGameProfile())) {
            final String s = ((String)NickHider.getInstance().skinFile.getValue()).toLowerCase();
            final ResourceLocation mark = new ResourceLocation("minecraft", "skins/" + s + ".png");
            if (NickHider.getInstance().isOn() && (boolean)NickHider.getInstance().skinChanger.getValue()) {
                cir.setReturnValue((Object)mark);
            }
            else if (old != null) {
                cir.setReturnValue((Object)old);
            }
        }
        final NetworkPlayerInfo networkplayerinfo = Objects.requireNonNull(Minecraft.getMinecraft().getConnection()).getPlayerInfo((String)MontageEnder.getInstance().nameString.getValue());
        if (networkplayerinfo != null && this.getGameProfile().equals((Object)networkplayerinfo.getGameProfile())) {
            final String s2 = ((String)MontageEnder.getInstance().skinFile.getValue()).toLowerCase();
            final ResourceLocation mark2 = new ResourceLocation("minecraft", "skins/" + s2 + ".png");
            if (MontageEnder.getInstance().isOn()) {
                cir.setReturnValue((Object)mark2);
            }
            else if (old != null) {
                cir.setReturnValue((Object)old);
            }
        }
    }
}
