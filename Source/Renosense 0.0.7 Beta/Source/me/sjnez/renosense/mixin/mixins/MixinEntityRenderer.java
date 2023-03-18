//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.mixin.mixins;

import org.spongepowered.asm.mixin.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.entity.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import me.sjnez.renosense.features.modules.render.*;
import me.sjnez.renosense.event.events.*;
import net.minecraftforge.common.*;
import net.minecraftforge.fml.common.eventhandler.*;
import org.lwjgl.util.glu.*;
import org.spongepowered.asm.mixin.injection.*;
import me.sjnez.renosense.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.entity.*;
import net.minecraft.util.math.*;
import com.google.common.base.*;
import me.sjnez.renosense.features.modules.player.*;
import net.minecraft.item.*;
import java.util.*;

@Mixin(value = { EntityRenderer.class }, priority = 1001)
public abstract class MixinEntityRenderer
{
    Minecraft mc;
    
    protected MixinEntityRenderer(final RenderManager renderManager) {
        this.mc = Minecraft.getMinecraft();
    }
    
    @Inject(method = { "updateLightmap" }, at = { @At("HEAD") }, cancellable = true)
    private void updateLightmap(final float partialTicks, final CallbackInfo info) {
        if (NoRender.getInstance().isOn() && (NoRender.getInstance().skylight.getValue() == NoRender.Skylight.ENTITY || NoRender.getInstance().skylight.getValue() == NoRender.Skylight.ALL)) {
            info.cancel();
        }
    }
    
    @Inject(method = { "hurtCameraEffect" }, at = { @At("HEAD") }, cancellable = true)
    public void hurtCameraEffect(final float ticks, final CallbackInfo info) {
        if ((boolean)NoRender.getInstance().hurtCam.getValue() && NoRender.getInstance().isOn()) {
            info.cancel();
        }
    }
    
    @Redirect(method = { "setupCameraTransform" }, at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onSetupCameraTransform(final float fovy, final float aspect, final float zNear, final float zFar) {
        final PerspectiveEvent event = new PerspectiveEvent(this.mc.displayWidth / (float)this.mc.displayHeight);
        MinecraftForge.EVENT_BUS.post((Event)event);
        Project.gluPerspective(fovy, event.getAspect(), zNear, zFar);
    }
    
    @Inject(method = { "renderHand" }, at = { @At("HEAD") }, cancellable = true)
    private void renderHand(final float partialTicks, final int pass, final CallbackInfo ci) {
        if (RenoSense.moduleManager.isModuleEnabled("JarvisCamera")) {
            ci.cancel();
        }
    }
    
    @Redirect(method = { "getMouseOver" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"))
    public List<Entity> getEntitiesInAABBexcludingHook(final WorldClient worldClient, final Entity entityIn, final AxisAlignedBB boundingBox, final Predicate<? super Entity> predicate) {
        if ((NoEntityTrace.getInstance().isOn() && (boolean)NoEntityTrace.getInstance().gapple.getValue() && this.mc.player.getHeldItemMainhand().getItem() instanceof ItemAppleGold) || ((boolean)NoEntityTrace.getInstance().pickaxe.getValue() && this.mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe)) {
            return new ArrayList<Entity>();
        }
        return (List<Entity>)worldClient.getEntitiesInAABBexcluding(entityIn, boundingBox, (Predicate)predicate);
    }
    
    @Redirect(method = { "renderWorldPass" }, at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onRenderWorldPass(final float fovy, final float aspect, final float zNear, final float zFar) {
        final PerspectiveEvent event = new PerspectiveEvent(this.mc.displayWidth / (float)this.mc.displayHeight);
        MinecraftForge.EVENT_BUS.post((Event)event);
        Project.gluPerspective(fovy, event.getAspect(), zNear, zFar);
    }
    
    @Redirect(method = { "renderCloudsCheck" }, at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onRenderCloudsCheck(final float fovy, final float aspect, final float zNear, final float zFar) {
        final PerspectiveEvent event = new PerspectiveEvent(this.mc.displayWidth / (float)this.mc.displayHeight);
        MinecraftForge.EVENT_BUS.post((Event)event);
        Project.gluPerspective(fovy, event.getAspect(), zNear, zFar);
    }
}
