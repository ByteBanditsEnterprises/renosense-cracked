//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.mixin.mixins;

import org.spongepowered.asm.mixin.*;
import net.minecraft.client.resources.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import net.minecraft.client.*;
import java.util.*;
import me.sjnez.renosense.features.modules.client.*;
import me.sjnez.renosense.util.*;
import net.minecraft.client.network.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ DefaultPlayerSkin.class })
public class MixinDefaultPlayerSkin
{
    @Inject(method = { "getSkinType" }, at = { @At("HEAD") }, cancellable = true)
    private static void getSkinType(final UUID playerUUID, final CallbackInfoReturnable<String> cir) {
        final NetworkPlayerInfo networkplayerinfo = Objects.requireNonNull(Minecraft.getMinecraft().getConnection()).getPlayerInfo((String)MontageEnder.getInstance().nameString.getValue());
        if (networkplayerinfo != null && playerUUID.equals(PlayerUtil.getUUIDFromName((String)MontageEnder.getInstance().nameString.getValue()))) {
            cir.setReturnValue((Object)(((MontageEnder.skinType)MontageEnder.getInstance().skinTypeSetting.getValue()).equals((Object)MontageEnder.skinType.NORMAL) ? "default" : "slim"));
        }
    }
}
