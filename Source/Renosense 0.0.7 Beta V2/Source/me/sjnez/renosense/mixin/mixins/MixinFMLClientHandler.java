//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.mixin.mixins;

import org.spongepowered.asm.mixin.*;
import net.minecraftforge.fml.client.*;
import net.minecraft.client.*;
import java.util.*;
import net.minecraft.client.resources.*;
import net.minecraft.client.resources.data.*;
import me.sjnez.renosense.util.*;
import java.io.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ FMLClientHandler.class })
public class MixinFMLClientHandler
{
    @Redirect(method = { "beginMinecraftLoading" }, at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/client/SplashProgress;start()V"), remap = false)
    public void startScreen(final Minecraft minecraft, final List<IResourcePack> resourcePackList, final IReloadableResourceManager resourceManager, final MetadataSerializer metaSerializer) throws IOException {
        CustomSplashProgress.start();
    }
    
    @Redirect(method = { "haltGame" }, at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/client/SplashProgress;finish()V"), remap = false)
    public void closeScreen(final String message, final Throwable t) {
        CustomSplashProgress.finish();
    }
    
    @Redirect(method = { "finishMinecraftLoading" }, at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/client/SplashProgress;finish()V"), remap = false)
    public void closeScreenI() {
        CustomSplashProgress.finish();
    }
}
