//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.mixin.mixins;

import org.spongepowered.asm.mixin.*;
import net.minecraft.entity.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import me.sjnez.renosense.*;
import me.sjnez.renosense.features.modules.render.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ EntityLivingBase.class })
public class MixinEntityLivingBase
{
    @Inject(method = { "getArmSwingAnimationEnd" }, at = { @At("HEAD") }, cancellable = true)
    private void getArmSwingAnimationEnd(final CallbackInfoReturnable<Integer> info) {
        if (RenoSense.moduleManager.isModuleEnabled("Swing") && (boolean)Swing.getINSTANCE().slowSwing.getValue()) {
            info.setReturnValue((Object)15);
        }
    }
}
