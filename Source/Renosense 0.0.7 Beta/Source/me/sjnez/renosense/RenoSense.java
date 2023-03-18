//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense;

import net.minecraftforge.fml.common.*;
import me.sjnez.renosense.manager.*;
import me.sjnez.renosense.features.gui.alt.*;
import net.minecraft.util.*;
import net.minecraft.client.*;
import java.nio.*;
import me.sjnez.renosense.util.*;
import org.lwjgl.opengl.*;
import java.io.*;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.*;

@Mod(modid = "renosense", name = "RenoSense", version = "0.0.7")
public class RenoSense
{
    public static final String MODVER = "0.0.7";
    public static final String MODNAME = "RenoSense";
    public static final Logger LOGGER;
    public static final String MODID = "renosense";
    public static TotemPopManager totemPopManager;
    public static TimerManager timerManager;
    public static CommandManager commandManager;
    public static FriendManager friendManager;
    public static ModuleManager moduleManager;
    public static PacketManager packetManager;
    public static ColorManager colorManager;
    public static HoleManager holeManager;
    public static InventoryManager inventoryManager;
    public static PotionManager potionManager;
    public static RotationManager rotationManager;
    public static PositionManager positionManager;
    public static SpeedManager speedManager;
    public static ReloadManager reloadManager;
    public static FileManager fileManager;
    public static ConfigManager configManager;
    public static ServerManager serverManager;
    public static EventManager eventManager;
    public static TextManager textManager;
    public static EnemyManager enemyManager;
    public static Thread thread;
    @Mod.Instance
    public static RenoSense INSTANCE;
    private static boolean unloaded;
    
    public static void load() {
        RenoSense.LOGGER.info("\n\nLoading RenoSense 2 by Sjnez");
        RenoSense.unloaded = false;
        if (RenoSense.reloadManager != null) {
            RenoSense.reloadManager.unload();
            RenoSense.reloadManager = null;
        }
        RenoSense.totemPopManager = new TotemPopManager();
        RenoSense.timerManager = new TimerManager();
        RenoSense.textManager = new TextManager();
        RenoSense.commandManager = new CommandManager();
        RenoSense.friendManager = new FriendManager();
        RenoSense.enemyManager = new EnemyManager();
        RenoSense.moduleManager = new ModuleManager();
        RenoSense.rotationManager = new RotationManager();
        RenoSense.packetManager = new PacketManager();
        RenoSense.eventManager = new EventManager();
        RenoSense.speedManager = new SpeedManager();
        RenoSense.potionManager = new PotionManager();
        RenoSense.inventoryManager = new InventoryManager();
        RenoSense.serverManager = new ServerManager();
        RenoSense.fileManager = new FileManager();
        RenoSense.colorManager = new ColorManager();
        RenoSense.positionManager = new PositionManager();
        RenoSense.configManager = new ConfigManager();
        RenoSense.holeManager = new HoleManager();
        RenoSense.thread = new Thread(RenoSense::onUnload);
        Runtime.getRuntime().addShutdownHook(RenoSense.thread);
        RenoSense.LOGGER.info("Managers loaded.");
        RenoSense.moduleManager.init();
        RenoSense.LOGGER.info("Modules loaded.");
        RenoSense.configManager.init();
        RenoSense.eventManager.init();
        RenoSense.LOGGER.info("EventManager loaded.");
        RenoSense.textManager.init(true);
        RenoSense.moduleManager.onLoad();
        RenoSense.LOGGER.info("RenoSense 2 successfully loaded!\n");
    }
    
    public static void unload(final boolean unload) {
        RenoSense.LOGGER.info("\n\nUnloading RenoSense 2 by Sjnez");
        if (unload) {
            (RenoSense.reloadManager = new ReloadManager()).init((RenoSense.commandManager != null) ? RenoSense.commandManager.getPrefix() : ".");
        }
        onUnload();
        RenoSense.timerManager = null;
        RenoSense.eventManager = null;
        RenoSense.friendManager = null;
        RenoSense.speedManager = null;
        RenoSense.holeManager = null;
        RenoSense.positionManager = null;
        RenoSense.rotationManager = null;
        RenoSense.configManager = null;
        RenoSense.enemyManager = null;
        RenoSense.commandManager = null;
        RenoSense.colorManager = null;
        RenoSense.serverManager = null;
        RenoSense.fileManager = null;
        RenoSense.potionManager = null;
        RenoSense.inventoryManager = null;
        RenoSense.moduleManager = null;
        RenoSense.textManager = null;
        RenoSense.LOGGER.info("RenoSense 2 unloaded!\n");
    }
    
    public static void reload() {
        unload(false);
        load();
    }
    
    public static void onUnload() {
        if (!RenoSense.unloaded) {
            RenoSense.eventManager.onUnload();
            RenoSense.moduleManager.onUnload();
            AltGui.saveAlts();
            RenoSense.configManager.saveConfig(RenoSense.configManager.config.replaceFirst("renosense/", ""));
            RenoSense.moduleManager.onUnloadPost();
            RenoSense.unloaded = true;
        }
    }
    
    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        RenoSense.LOGGER.info(System.getProperty("javax.net.ssl.trustStorePassword"));
        RenoSense.LOGGER.info(System.getProperty("javax.net.ssl.trustStore"));
        RenoSense.LOGGER.info("RENOSENSE!!!");
    }
    
    public static void setWindowIcon() {
        if (Util.getOSType() != Util.EnumOS.OSX) {
            try (final InputStream inputStream16x = Minecraft.class.getResourceAsStream("/assets/renosense/icons/icon-16x.png");
                 final InputStream inputStream32x = Minecraft.class.getResourceAsStream("/assets/renosense/icons/icon-32x.png")) {
                final ByteBuffer[] icons = { IconUtil.INSTANCE.readImageToBuffer(inputStream16x), IconUtil.INSTANCE.readImageToBuffer(inputStream32x) };
                Display.setIcon(icons);
            }
            catch (Exception e) {
                RenoSense.LOGGER.error("Couldn't set Windows Icon", (Throwable)e);
            }
        }
    }
    
    private void setWindowsIcon() {
        setWindowIcon();
    }
    
    @Mod.EventHandler
    public void init(final FMLInitializationEvent event) {
        Display.setTitle("RenoSense 2 0.0.7");
        AltGui.loadAlts();
        this.setWindowsIcon();
        load();
    }
    
    static {
        LOGGER = LogManager.getLogger("RenoSense 2");
        RenoSense.unloaded = false;
    }
}
