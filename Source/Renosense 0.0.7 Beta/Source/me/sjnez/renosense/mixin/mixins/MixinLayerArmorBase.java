//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.mixin.mixins;

import org.spongepowered.asm.mixin.*;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.entity.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import me.sjnez.renosense.features.modules.render.*;
import org.spongepowered.asm.mixin.injection.*;
import net.minecraft.inventory.*;

@Mixin({ LayerArmorBase.class })
public class MixinLayerArmorBase
{
    @Inject(method = { "doRenderLayer" }, at = { @At("HEAD") }, cancellable = true)
    public void doRenderLayer(final EntityLivingBase entitylivingbaseIn, final float limbSwing, final float limbSwingAmount, final float partialTicks, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale, final CallbackInfo ci) {
        if (NoRender.getInstance().isEnabled() && NoRender.getInstance().noArmor.getValue() == NoRender.NoArmor.ALL) {
            ci.cancel();
        }
    }
    
    @Inject(method = { "renderArmorLayer" }, at = { @At("HEAD") }, cancellable = true)
    public void renderArmorLayer(final EntityLivingBase entityLivingBaseIn, final float limbSwing, final float limbSwingAmount, final float partialTicks, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale, final EntityEquipmentSlot slotIn, final CallbackInfo ci) {
        if (NoRender.getInstance().isEnabled() && NoRender.getInstance().noArmor.getValue() == NoRender.NoArmor.HELMET && slotIn == EntityEquipmentSlot.HEAD) {
            ci.cancel();
        }
    }
}
