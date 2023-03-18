//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.mixin.mixins;

import net.minecraft.client.*;
import org.spongepowered.asm.mixin.*;
import net.minecraft.client.entity.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import org.spongepowered.asm.mixin.injection.*;
import net.minecraft.entity.*;
import net.minecraft.client.renderer.block.model.*;
import me.sjnez.renosense.features.modules.render.*;
import net.minecraft.client.renderer.*;

@Mixin({ ItemRenderer.class })
public abstract class MixinItemRenderer
{
    @Shadow
    @Final
    public Minecraft mc;
    private boolean injection;
    
    public MixinItemRenderer() {
        this.injection = true;
    }
    
    @Shadow
    public abstract void renderItemInFirstPerson(final AbstractClientPlayer p0, final float p1, final float p2, final EnumHand p3, final float p4, final ItemStack p5, final float p6);
    
    @Shadow
    protected abstract void renderArmFirstPerson(final float p0, final float p1, final EnumHandSide p2);
    
    @Inject(method = { "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V" }, at = { @At("HEAD") }, cancellable = true)
    public void renderItemInFirstPersonHook(final AbstractClientPlayer player, final float p_1874572, final float p_1874573, final EnumHand hand, final float p_1874575, final ItemStack stack, final float p_1874577, final CallbackInfo info) {
    }
    
    @Inject(method = { "renderFireInFirstPerson" }, at = { @At("HEAD") }, cancellable = true)
    public void renderFireInFirstPersonHook(final CallbackInfo info) {
        if (NoRender.getInstance().isOn() && (boolean)NoRender.getInstance().fire.getValue()) {
            info.cancel();
        }
    }
    
    @Inject(method = { "renderSuffocationOverlay" }, at = { @At("HEAD") }, cancellable = true)
    public void renderSuffocationOverlay(final CallbackInfo ci) {
        if (NoRender.getInstance().isOn() && (boolean)NoRender.getInstance().blocks.getValue()) {
            ci.cancel();
        }
    }
    
    @Inject(method = { "renderItemSide" }, at = { @At("HEAD") })
    public void renderItemSide(final EntityLivingBase entitylivingbaseIn, final ItemStack heldStack, final ItemCameraTransforms.TransformType transform, final boolean leftHanded, final CallbackInfo ci) {
        if (ViewModel.INSTANCE.isEnabled()) {
            GlStateManager.scale((int)ViewModel.INSTANCE.scaleX.getValue() / 100.0f, (int)ViewModel.INSTANCE.scaleY.getValue() / 100.0f, (int)ViewModel.INSTANCE.scaleZ.getValue() / 100.0f);
            if (transform == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND) {
                GlStateManager.translate((int)ViewModel.INSTANCE.translateX.getValue() / 200.0f, (int)ViewModel.INSTANCE.translateY.getValue() / 200.0f, (int)ViewModel.INSTANCE.translateZ.getValue() / 200.0f);
                GlStateManager.rotate((float)(int)ViewModel.INSTANCE.rotateX.getValue(), 1.0f, 0.0f, 0.0f);
                GlStateManager.rotate((float)(int)ViewModel.INSTANCE.rotateY.getValue(), 0.0f, 1.0f, 0.0f);
                GlStateManager.rotate((float)(int)ViewModel.INSTANCE.rotateZ.getValue(), 0.0f, 0.0f, 1.0f);
            }
            else if (transform == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND) {
                GlStateManager.translate(-(int)ViewModel.INSTANCE.translateX.getValue() / 200.0f, (int)ViewModel.INSTANCE.translateY.getValue() / 200.0f, (int)ViewModel.INSTANCE.translateZ.getValue() / 200.0f);
                GlStateManager.rotate((float)(-(int)ViewModel.INSTANCE.rotateX.getValue()), 1.0f, 0.0f, 0.0f);
                GlStateManager.rotate((float)(int)ViewModel.INSTANCE.rotateY.getValue(), 0.0f, 1.0f, 0.0f);
                GlStateManager.rotate((float)(int)ViewModel.INSTANCE.rotateZ.getValue(), 0.0f, 0.0f, 1.0f);
            }
        }
    }
}
