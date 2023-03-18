//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.mixin.mixins;

import org.spongepowered.asm.mixin.*;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import me.sjnez.renosense.features.modules.render.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ ItemStack.class })
public class MixinItemStack
{
    @Inject(method = { "hasEffect" }, at = { @At("HEAD") }, cancellable = true)
    public void hasEffect(final CallbackInfoReturnable<Boolean> cir) {
        if ((boolean)NoRender.getInstance().glint.getValue() && NoRender.getInstance().isOn()) {
            cir.setReturnValue((Object)false);
        }
    }
}
