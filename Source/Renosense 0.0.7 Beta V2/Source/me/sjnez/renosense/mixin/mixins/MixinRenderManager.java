//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.mixin.mixins;

import me.sjnez.renosense.util.*;
import org.spongepowered.asm.mixin.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.entity.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import net.minecraft.entity.passive.*;
import me.sjnez.renosense.features.modules.render.*;
import me.sjnez.renosense.event.events.*;
import net.minecraftforge.common.*;
import net.minecraftforge.fml.common.eventhandler.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ RenderManager.class })
public class MixinRenderManager implements Util
{
    private Entity entityIn;
    
    @Inject(method = { "renderEntityStatic" }, at = { @At("HEAD") }, cancellable = true)
    private void renderEntityStatic(final Entity entityIn, final float partialTicks, final boolean p_188388_3_, final CallbackInfo ci) {
        this.entityIn = entityIn;
        if (entityIn instanceof EntityParrot && (boolean)NoRender.getInstance().parrot.getValue() && NoRender.getInstance().isOn()) {
            ci.cancel();
        }
    }
    
    @ModifyArg(method = { "renderEntityStatic" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RenderManager;renderEntity(Lnet/minecraft/entity/Entity;DDDFFZ)V"), index = 1)
    private double renderEntityStaticX(final double x) {
        if (this.entityIn == null || MixinRenderManager.mc.world == null) {
            return x;
        }
        final InterpolateEvent event = new InterpolateEvent();
        MinecraftForge.EVENT_BUS.post((Event)event);
        if (event.isCanceled()) {
            return this.entityIn.posX - MixinRenderManager.mc.getRenderManager().viewerPosX;
        }
        return x;
    }
    
    @ModifyArg(method = { "renderEntityStatic" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RenderManager;renderEntity(Lnet/minecraft/entity/Entity;DDDFFZ)V"), index = 2)
    private double renderEntityStaticY(final double y) {
        if (this.entityIn == null || MixinRenderManager.mc.world == null) {
            return y;
        }
        final InterpolateEvent event = new InterpolateEvent();
        MinecraftForge.EVENT_BUS.post((Event)event);
        if (event.isCanceled()) {
            return this.entityIn.posY - MixinRenderManager.mc.getRenderManager().viewerPosY;
        }
        return y;
    }
    
    @ModifyArg(method = { "renderEntityStatic" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RenderManager;renderEntity(Lnet/minecraft/entity/Entity;DDDFFZ)V"), index = 3)
    private double renderEntityStaticZ(final double z) {
        if (this.entityIn == null || MixinRenderManager.mc.world == null) {
            return z;
        }
        final InterpolateEvent event = new InterpolateEvent();
        MinecraftForge.EVENT_BUS.post((Event)event);
        if (event.isCanceled()) {
            return this.entityIn.posZ - MixinRenderManager.mc.getRenderManager().viewerPosZ;
        }
        return z;
    }
}
