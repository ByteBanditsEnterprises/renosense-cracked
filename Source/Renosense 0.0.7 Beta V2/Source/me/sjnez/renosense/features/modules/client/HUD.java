//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.client;

import me.sjnez.renosense.features.modules.*;
import net.minecraft.util.*;
import me.sjnez.renosense.features.setting.*;
import java.awt.*;
import net.minecraftforge.fml.common.gameevent.*;
import org.lwjgl.opengl.*;
import net.minecraftforge.fml.common.eventhandler.*;
import me.sjnez.renosense.*;
import net.minecraft.client.gui.*;
import com.mojang.realmsclient.gui.*;
import net.minecraft.client.*;
import net.minecraft.potion.*;
import java.text.*;
import java.util.*;
import me.sjnez.renosense.features.command.*;
import java.util.function.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.client.renderer.*;
import me.sjnez.renosense.util.*;
import net.minecraftforge.event.entity.player.*;
import me.sjnez.renosense.event.events.*;
import net.minecraft.network.play.server.*;
import net.minecraft.util.text.*;

public class HUD extends Module
{
    private static final ResourceLocation box;
    private static final ItemStack totem;
    private static final ItemStack crystal;
    private static final ItemStack obsidian;
    private static final ItemStack exp;
    private static HUD INSTANCE;
    public Setting<HudMode> hudMode;
    public Setting<String> gameTitle;
    public Setting<Boolean> gameTVer;
    private final Setting<Boolean> waterMark;
    private final Setting<String> waterMarkName;
    public Setting<Integer> waterMarkY;
    private final Setting<Boolean> arrayList;
    public Setting<Integer> animationHorizontalTime;
    public Setting<Integer> animationVerticalTime;
    public Setting<RenderingMode> renderingMode;
    private final Setting<Boolean> renderingUp;
    private final Setting<Boolean> grayNess;
    private final Setting<Boolean> coords;
    private final Setting<Boolean> direction;
    private final Setting<Boolean> armor;
    private final Setting<Boolean> combatItems;
    private final Setting<CombatItemMode> combatItemModeSetting;
    private final Setting<Boolean> totems;
    private final Setting<Boolean> greeter;
    public Setting<Boolean> speed;
    public Setting<SpeedMode> speedMode;
    public Setting<Boolean> potions;
    public Setting<Boolean> potionSync;
    private final Setting<Boolean> ping;
    private final Setting<Boolean> ms;
    private final Setting<Boolean> tps;
    private final Setting<Boolean> fps;
    private final Setting<Boolean> server;
    public Setting<Boolean> time;
    private final Setting<Boolean> lag;
    public Setting<Integer> lagTime;
    public Setting<String> command;
    public Setting<Boolean> timestamp;
    public Setting<Boolean> commandPrefix;
    public Setting<Boolean> rainbowPrefix;
    public Setting<Boolean> notifyToggles;
    public Setting<Boolean> selfTextRadar;
    public Setting<Integer> textRadarUpdates;
    public Setting<Boolean> textRadar;
    public Setting<Boolean> textRadarSync;
    public Setting<Integer> textRadarHue;
    public Setting<Integer> textRadarSat;
    public Setting<Integer> textRadarBright;
    public Setting<Integer> debugDelay;
    public static Timer debugTimer;
    private final Timer timer;
    private Map<String, Integer> players;
    public Map<Integer, Integer> colorHeightMap;
    private int color;
    private int textRadarColor;
    private boolean shouldIncrement;
    private int hitMarkerTimer;
    public float hue;
    
    public int getTextRadarRed() {
        return new Color(this.getColor()).getRed();
    }
    
    public int getTextRadarGreen() {
        return new Color(this.getColor()).getGreen();
    }
    
    public int getTextRadarBlue() {
        return new Color(this.getColor()).getBlue();
    }
    
    public int getColor() {
        return Color.HSBtoRGB(this.textRadarHue.getValue() / 360.0f, this.textRadarSat.getValue() / 100.0f, this.textRadarBright.getValue() / 100.0f);
    }
    
    public HUD() {
        super("HUD", "The HUD Module puts elemets on your screen that show you info about the server, players, etc.", Category.CLIENT, true, false, false);
        this.hudMode = (Setting<HudMode>)this.register(new Setting("HudPage", (T)HudMode.HUD, "Page of HUD."));
        this.gameTitle = (Setting<String>)this.register(new Setting("AppTitle", (T)"RenoSense", v -> this.hudMode.getValue() == HudMode.WATERMARK, "Sets the display title of the application"));
        this.gameTVer = (Setting<Boolean>)this.register(new Setting("GameTitleVersion", (T)true, v -> this.hudMode.getValue() == HudMode.WATERMARK, "Toggles whether the version of RenoSense is appended to the display title."));
        this.waterMark = (Setting<Boolean>)this.register(new Setting("Watermark", (T)true, v -> this.hudMode.getValue() == HudMode.WATERMARK, "displays watermark"));
        this.waterMarkName = (Setting<String>)this.register(new Setting("WaterMarkName", (T)"RenoSense", v -> this.waterMark.getValue() && this.hudMode.getValue() == HudMode.WATERMARK, "Changes the name of the watermark."));
        this.waterMarkY = (Setting<Integer>)this.register(new Setting("WatermarkPosY", (T)2, (T)0, (T)20, v -> this.waterMark.getValue() && this.hudMode.getValue() == HudMode.WATERMARK, "Changes the position of the watermark."));
        this.arrayList = (Setting<Boolean>)this.register(new Setting("ActiveModules", (T)true, v -> this.hudMode.getValue() == HudMode.HUD, "Lists the active modules."));
        this.animationHorizontalTime = (Setting<Integer>)this.register(new Setting("AnimationHTime", (T)500, (T)1, (T)1000, v -> this.arrayList.getValue() && this.hudMode.getValue() == HudMode.HUD, "Changes speed of array list module animation."));
        this.animationVerticalTime = (Setting<Integer>)this.register(new Setting("AnimationVTime", (T)50, (T)1, (T)500, v -> this.arrayList.getValue() && this.hudMode.getValue() == HudMode.HUD, "Changes speed of array list module animation."));
        this.renderingMode = (Setting<RenderingMode>)this.register(new Setting("Ordering", (T)RenderingMode.ABC, v -> this.hudMode.getValue() == HudMode.HUD, "Modes: ABC, Length. Choose how to sort the array list."));
        this.renderingUp = (Setting<Boolean>)this.register(new Setting("RenderingUp", (T)true, v -> this.hudMode.getValue() == HudMode.HUD, "Orientation of the HUD-Elements."));
        this.grayNess = (Setting<Boolean>)this.register(new Setting("Gray", (T)true, v -> this.hudMode.getValue() == HudMode.HUD, "Toggles whether some elements are gray or sync with the client."));
        this.coords = (Setting<Boolean>)this.register(new Setting("Coords", (T)true, v -> this.hudMode.getValue() == HudMode.HUD, "Your current coordinates"));
        this.direction = (Setting<Boolean>)this.register(new Setting("Direction", (T)true, v -> this.hudMode.getValue() == HudMode.HUD, "The Direction you are facing."));
        this.armor = (Setting<Boolean>)this.register(new Setting("Armor", (T)true, v -> this.hudMode.getValue() == HudMode.HUD, "ArmorHUD"));
        this.combatItems = (Setting<Boolean>)this.register(new Setting("CombatItems", (T)true, v -> this.hudMode.getValue() == HudMode.HUD, "CombatItemsHUD"));
        this.combatItemModeSetting = (Setting<CombatItemMode>)this.register(new Setting("CombatItemMode", (T)CombatItemMode.Square, v -> this.hudMode.getValue() == HudMode.HUD));
        this.totems = (Setting<Boolean>)this.register(new Setting("Totems", (T)true, v -> !this.combatItems.getValue() && this.hudMode.getValue() == HudMode.HUD, "TotemHUD"));
        this.greeter = (Setting<Boolean>)this.register(new Setting("Welcomer", (T)true, v -> this.hudMode.getValue() == HudMode.HUD, "The time"));
        this.speed = (Setting<Boolean>)this.register(new Setting("Speed", (T)true, v -> this.hudMode.getValue() == HudMode.HUD, "Speed"));
        this.speedMode = (Setting<SpeedMode>)this.register(new Setting("SpeedUnits", (T)SpeedMode.BLOCKS, v -> this.speed.getValue() && this.hudMode.getValue() == HudMode.HUD, "Your Speed"));
        this.potions = (Setting<Boolean>)this.register(new Setting("Potions", (T)true, v -> this.hudMode.getValue() == HudMode.HUD, "Active potion effects"));
        this.potionSync = (Setting<Boolean>)this.register(new Setting("PotionSync", (T)true, v -> this.potions.getValue() && this.hudMode.getValue() == HudMode.HUD, "Syncs the potion colors with the client."));
        this.ping = (Setting<Boolean>)this.register(new Setting("Ping", (T)true, v -> this.hudMode.getValue() == HudMode.HUD, "Your response time to the server."));
        this.ms = (Setting<Boolean>)this.register(new Setting("ms", (T)true, v -> this.ping.getValue() && this.hudMode.getValue() == HudMode.HUD, "Appends ms to the end of the ping number."));
        this.tps = (Setting<Boolean>)this.register(new Setting("TPS", (T)true, v -> this.hudMode.getValue() == HudMode.HUD, "Ticks per second of the server."));
        this.fps = (Setting<Boolean>)this.register(new Setting("FPS", (T)true, v -> this.hudMode.getValue() == HudMode.HUD, "Your frames per second."));
        this.server = (Setting<Boolean>)this.register(new Setting("Server", (T)true, v -> this.hudMode.getValue() == HudMode.HUD, "Shows the server"));
        this.time = (Setting<Boolean>)this.register(new Setting("Time", (T)true, v -> this.hudMode.getValue() == HudMode.HUD, "The time"));
        this.lag = (Setting<Boolean>)this.register(new Setting("LagNotifier", (T)true, v -> this.hudMode.getValue() == HudMode.HUD, "Tells you if the server is lagging"));
        this.lagTime = (Setting<Integer>)this.register(new Setting("LagTime", (T)1000, (T)0, (T)2000, v -> this.lag.getValue() && this.hudMode.getValue() == HudMode.HUD, "The amount of time the server is unresponsive before it tells you its lagging."));
        this.command = (Setting<String>)this.register(new Setting("Command", (T)"RenoSense", v -> this.hudMode.getValue() == HudMode.PREFIX, "For use in chat."));
        this.timestamp = (Setting<Boolean>)this.register(new Setting("TimeStamps", (T)true, v -> this.hudMode.getValue() == HudMode.PREFIX, "Toggles timestamps at the beginning of each chat message."));
        this.commandPrefix = (Setting<Boolean>)this.register(new Setting("CommandPrefix", (T)true, v -> this.hudMode.getValue() == HudMode.PREFIX, "Puts the string above this setting behind chat messages."));
        this.rainbowPrefix = (Setting<Boolean>)this.register(new Setting("RainbowPrefix", (T)true, v -> this.hudMode.getValue() == HudMode.PREFIX, "Makes the string above this setting rainbow."));
        this.notifyToggles = (Setting<Boolean>)this.register(new Setting("ChatNotify", (T)true, v -> this.hudMode.getValue() == HudMode.PREFIX, "notifys in chat"));
        this.selfTextRadar = (Setting<Boolean>)this.register(new Setting("SelfOnTextRadar", (T)Boolean.TRUE, v -> this.textRadar.getValue() && this.hudMode.getValue() == HudMode.TEXTRADAR, "Toggles whether yourself shows up on the text radar."));
        this.textRadarUpdates = (Setting<Integer>)this.register(new Setting("TRUpdates", (T)500, (T)0, (T)1000, v -> this.textRadar.getValue() && this.hudMode.getValue() == HudMode.TEXTRADAR, "The amount of ms until the text radar updates."));
        this.textRadar = (Setting<Boolean>)this.register(new Setting("TextRadar", (T)Boolean.FALSE, v -> this.hudMode.getValue() == HudMode.TEXTRADAR, "Displays list of players in your render distance."));
        this.textRadarSync = (Setting<Boolean>)this.register(new Setting("TextRadarColorSync", (T)false, v -> this.textRadar.getValue() && this.hudMode.getValue() == HudMode.TEXTRADAR, "Toggles whether the text radar color syncs with the client."));
        this.textRadarHue = (Setting<Integer>)this.register(new Setting("TextRadarHue", (T)300, (T)0, (T)360, v -> this.textRadar.getValue() && !this.textRadarSync.getValue() && this.hudMode.getValue() == HudMode.TEXTRADAR, "The hue of the text radar."));
        this.textRadarSat = (Setting<Integer>)this.register(new Setting("TextRadarSaturation", (T)100, (T)0, (T)100, v -> this.textRadar.getValue() && !this.textRadarSync.getValue() && this.hudMode.getValue() == HudMode.TEXTRADAR, "The saturation of the text radar."));
        this.textRadarBright = (Setting<Integer>)this.register(new Setting("TextRadarBrightness", (T)33, (T)0, (T)100, v -> this.textRadar.getValue() && !this.textRadarSync.getValue() && this.hudMode.getValue() == HudMode.TEXTRADAR, "The brightness of the text radar."));
        this.debugDelay = (Setting<Integer>)this.register(new Setting("DebugDelay", (T)1000, (T)0, (T)10000, v -> this.debug.getValue(), "Delay between messages sent."));
        this.timer = new Timer();
        this.players = new HashMap<String, Integer>();
        this.colorHeightMap = new HashMap<Integer, Integer>();
        this.setInstance();
    }
    
    public static HUD getInstance() {
        if (HUD.INSTANCE == null) {
            HUD.INSTANCE = new HUD();
        }
        return HUD.INSTANCE;
    }
    
    private void setInstance() {
        HUD.INSTANCE = this;
    }
    
    @Override
    public void onUpdate() {
        if (this.shouldIncrement) {
            ++this.hitMarkerTimer;
        }
        if (this.hitMarkerTimer == 10) {
            this.hitMarkerTimer = 0;
            this.shouldIncrement = false;
        }
        if (this.timer.passedMs(getInstance().textRadarUpdates.getValue())) {
            this.players = this.getTextRadarPlayers();
            this.timer.reset();
        }
    }
    
    @SubscribeEvent
    public void onTick(final TickEvent.ClientTickEvent event) {
        Display.setTitle(this.gameTitle.getValue() + (this.gameTVer.getValue() ? " 0.0.7" : ""));
    }
    
    @Override
    public void onRender2D(final Render2DEvent event) {
        if (RenoSense.moduleManager.isModuleEnabled("JarvisCamera")) {
            return;
        }
        if (fullNullCheck()) {
            return;
        }
        final int width = this.renderer.scaledWidth;
        final int height = this.renderer.scaledHeight;
        if (this.textRadar.getValue()) {
            this.drawTextRadar(0);
        }
        if (this.textRadarSync.getValue()) {
            this.textRadarColor = Colors.getInstance().getColor();
        }
        else {
            this.textRadarColor = ColorUtil.toRGBA(this.getTextRadarRed(), this.getTextRadarGreen(), this.getTextRadarBlue());
        }
        this.color = Colors.getInstance().getColor();
        if (this.waterMark.getValue()) {
            final String string = this.waterMarkName.getPlannedValue();
            if (ClickGui.getInstance().rainbow.getValue()) {
                if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                    this.renderer.drawString(string, 2.0f, this.waterMarkY.getValue(), ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                }
                else {
                    final int[] arrayOfInt = { 1 };
                    final char[] stringToCharArray = string.toCharArray();
                    float f = 0.0f;
                    for (final char c : stringToCharArray) {
                        this.renderer.drawString(String.valueOf(c), 2.0f + f, this.waterMarkY.getValue(), ColorUtil.rainbow(arrayOfInt[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                        f += this.renderer.getStringWidth(String.valueOf(c));
                        ++arrayOfInt[0];
                    }
                }
            }
            else {
                this.renderer.drawString(string, 2.0f, this.waterMarkY.getValue(), this.color, true);
            }
        }
        final int[] counter1 = { 1 };
        int j = (HUD.mc.currentScreen instanceof GuiChat && !this.renderingUp.getValue()) ? 14 : 0;
        if (this.arrayList.getValue()) {
            if (this.renderingUp.getValue()) {
                if (this.renderingMode.getValue() == RenderingMode.ABC) {
                    for (int k = 0; k < RenoSense.moduleManager.sortedModulesABC.size(); ++k) {
                        final String str = RenoSense.moduleManager.sortedModulesABC.get(k);
                        this.renderer.drawString(str, (float)(width - 2 - this.renderer.getStringWidth(str)), (float)(2 + j * 10), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                        ++j;
                        ++counter1[0];
                    }
                }
                else {
                    for (int k = 0; k < RenoSense.moduleManager.sortedModules.size(); ++k) {
                        final Module module = RenoSense.moduleManager.sortedModules.get(k);
                        final String str2 = module.getDisplayName() + ChatFormatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]") : "");
                        this.renderer.drawString(str2, (float)(width - 2 - this.renderer.getStringWidth(str2)), (float)(2 + j * 10), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                        ++j;
                        ++counter1[0];
                    }
                }
            }
            else if (this.renderingMode.getValue() == RenderingMode.ABC) {
                for (int k = 0; k < RenoSense.moduleManager.sortedModulesABC.size(); ++k) {
                    final String str = RenoSense.moduleManager.sortedModulesABC.get(k);
                    j += 10;
                    this.renderer.drawString(str, (float)(width - 2 - this.renderer.getStringWidth(str)), (float)(height - j), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    ++counter1[0];
                }
            }
            else {
                for (int k = 0; k < RenoSense.moduleManager.sortedModules.size(); ++k) {
                    final Module module = RenoSense.moduleManager.sortedModules.get(k);
                    final String str2 = module.getDisplayName() + ChatFormatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]") : "");
                    j += 10;
                    this.renderer.drawString(str2, (float)(width - 2 - this.renderer.getStringWidth(str2)), (float)(height - j), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    ++counter1[0];
                }
            }
        }
        final String grayString = this.grayNess.getValue() ? String.valueOf(ChatFormatting.GRAY) : "";
        int i = (HUD.mc.currentScreen instanceof GuiChat && this.renderingUp.getValue()) ? 13 : (this.renderingUp.getValue() ? -2 : 0);
        if (this.renderingUp.getValue()) {
            if (this.potions.getValue()) {
                final List<PotionEffect> effects = new ArrayList<PotionEffect>(Minecraft.getMinecraft().player.getActivePotionEffects());
                for (final PotionEffect potionEffect : effects) {
                    final String str3 = RenoSense.potionManager.getColoredPotionString(potionEffect);
                    i += 10;
                    this.renderer.drawString(str3, (float)(width - this.renderer.getStringWidth(str3) - 2), (float)(height - 2 - i), ((boolean)this.potionSync.getValue()) ? (ClickGui.getInstance().rainbow.getValue() ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : (this.potionSync.getValue() ? this.color : potionEffect.getPotion().getLiquidColor())) : potionEffect.getPotion().getLiquidColor(), true);
                }
            }
            if (this.server.getValue()) {
                final String sText = grayString + "Server " + ChatFormatting.WHITE + (HUD.mc.isSingleplayer() ? "SinglePlayer" : HUD.mc.getCurrentServerData().serverIP);
                i += 10;
                this.renderer.drawString(sText, (float)(width - this.renderer.getStringWidth(sText) - 2), (float)(height - 2 - i), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                ++counter1[0];
            }
            if (this.speed.getValue()) {
                final String str2 = grayString + "Speed " + ChatFormatting.WHITE + (this.speedMode.getValue().equals(SpeedMode.KMH) ? (RenoSense.speedManager.getSpeedKpH() + " km/h") : (this.speedMode.getValue().equals(SpeedMode.BLOCKS) ? (RenoSense.speedManager.getSpeedMpS() + " bps") : ""));
                i += 10;
                this.renderer.drawString(str2, (float)(width - this.renderer.getStringWidth(str2) - 2), (float)(height - 2 - i), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                ++counter1[0];
            }
            if (this.time.getValue()) {
                final String str2 = grayString + "Time " + ChatFormatting.WHITE + new SimpleDateFormat("h:mm a").format(new Date());
                i += 10;
                this.renderer.drawString(str2, (float)(width - this.renderer.getStringWidth(str2) - 2), (float)(height - 2 - i), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                ++counter1[0];
            }
            if (this.tps.getValue()) {
                final String str2 = grayString + "TPS " + ChatFormatting.WHITE + RenoSense.serverManager.getTPS();
                i += 10;
                this.renderer.drawString(str2, (float)(width - this.renderer.getStringWidth(str2) - 2), (float)(height - 2 - i), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                ++counter1[0];
            }
            final String fpsText = grayString + "FPS " + ChatFormatting.WHITE + Minecraft.debugFPS;
            final String sText2 = grayString + "Server " + ChatFormatting.WHITE + (HUD.mc.isSingleplayer() ? "SinglePlayer" : HUD.mc.getCurrentServerData().serverIP);
            final String str4 = grayString + "Ping " + ChatFormatting.WHITE + RenoSense.serverManager.getPing() + (this.ms.getValue() ? "ms" : "");
            if (this.renderer.getStringWidth(str4) > this.renderer.getStringWidth(fpsText)) {
                if (this.ping.getValue()) {
                    i += 10;
                    this.renderer.drawString(str4, (float)(width - this.renderer.getStringWidth(str4) - 2), (float)(height - 2 - i), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    ++counter1[0];
                }
                if (this.fps.getValue()) {
                    i += 10;
                    this.renderer.drawString(fpsText, (float)(width - this.renderer.getStringWidth(fpsText) - 2), (float)(height - 2 - i), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    ++counter1[0];
                }
            }
            else {
                if (this.fps.getValue()) {
                    i += 10;
                    this.renderer.drawString(fpsText, (float)(width - this.renderer.getStringWidth(fpsText) - 2), (float)(height - 2 - i), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    ++counter1[0];
                }
                if (this.ping.getValue()) {
                    i += 10;
                    this.renderer.drawString(str4, (float)(width - this.renderer.getStringWidth(str4) - 2), (float)(height - 2 - i), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    ++counter1[0];
                }
            }
        }
        else {
            if (this.potions.getValue()) {
                final List<PotionEffect> effects = new ArrayList<PotionEffect>(Minecraft.getMinecraft().player.getActivePotionEffects());
                for (final PotionEffect potionEffect : effects) {
                    final String str3 = RenoSense.potionManager.getColoredPotionString(potionEffect);
                    this.renderer.drawString(str3, (float)(width - this.renderer.getStringWidth(str3) - 2), (float)(2 + i++ * 10), ((boolean)this.potionSync.getValue()) ? (ClickGui.getInstance().rainbow.getValue() ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : (this.potionSync.getValue() ? this.color : potionEffect.getPotion().getLiquidColor())) : potionEffect.getPotion().getLiquidColor(), true);
                }
            }
            if (this.server.getValue()) {
                final String sText = grayString + "Server " + ChatFormatting.WHITE + (HUD.mc.isSingleplayer() ? "SinglePlayer" : HUD.mc.getCurrentServerData().serverIP);
                this.renderer.drawString(sText, (float)(width - this.renderer.getStringWidth(sText) - 2), (float)(2 + i++ * 10), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                ++counter1[0];
            }
            if (this.speed.getValue()) {
                final String str2 = grayString + "Speed " + ChatFormatting.WHITE + (this.speedMode.getValue().equals(SpeedMode.KMH) ? (RenoSense.speedManager.getSpeedKpH() + " km/h") : (this.speedMode.getValue().equals(SpeedMode.BLOCKS) ? (RenoSense.speedManager.getSpeedMpS() + " bps") : ""));
                this.renderer.drawString(str2, (float)(width - this.renderer.getStringWidth(str2) - 2), (float)(2 + i++ * 10), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                ++counter1[0];
            }
            if (this.time.getValue()) {
                final String str2 = grayString + " Time " + ChatFormatting.WHITE + new SimpleDateFormat("h:mm a").format(new Date());
                this.renderer.drawString(str2, (float)(width - this.renderer.getStringWidth(str2) - 2), (float)(2 + i++ * 10), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                ++counter1[0];
            }
            if (this.tps.getValue()) {
                final String str2 = grayString + "TPS " + ChatFormatting.WHITE + RenoSense.serverManager.getTPS();
                this.renderer.drawString(str2, (float)(width - this.renderer.getStringWidth(str2) - 2), (float)(2 + i++ * 10), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                ++counter1[0];
            }
            final String fpsText = grayString + "FPS " + ChatFormatting.WHITE + Minecraft.debugFPS;
            final String str5 = grayString + "Ping " + ChatFormatting.WHITE + RenoSense.serverManager.getPing() + (this.ms.getValue() ? "ms" : "");
            if (this.renderer.getStringWidth(str5) > this.renderer.getStringWidth(fpsText)) {
                if (this.ping.getValue()) {
                    this.renderer.drawString(str5, (float)(width - this.renderer.getStringWidth(str5) - 2), (float)(2 + i++ * 10), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    ++counter1[0];
                }
                if (this.fps.getValue()) {
                    this.renderer.drawString(fpsText, (float)(width - this.renderer.getStringWidth(fpsText) - 2), (float)(2 + i++ * 10), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    ++counter1[0];
                }
            }
            else {
                if (this.fps.getValue()) {
                    this.renderer.drawString(fpsText, (float)(width - this.renderer.getStringWidth(fpsText) - 2), (float)(2 + i++ * 10), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    ++counter1[0];
                }
                if (this.ping.getValue()) {
                    this.renderer.drawString(str5, (float)(width - this.renderer.getStringWidth(str5) - 2), (float)(2 + i++ * 10), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    ++counter1[0];
                }
            }
        }
        final boolean inHell = HUD.mc.world.getBiome(HUD.mc.player.getPosition()).getBiomeName().equals("Hell");
        final int posX = (int)HUD.mc.player.posX;
        final int posY = (int)HUD.mc.player.posY;
        final int posZ = (int)HUD.mc.player.posZ;
        final float nether = inHell ? 8.0f : 0.125f;
        final int hposX = (int)(HUD.mc.player.posX * nether);
        final int hposZ = (int)(HUD.mc.player.posZ * nether);
        i = ((HUD.mc.currentScreen instanceof GuiChat) ? 14 : 0);
        final String coordinates = ChatFormatting.RESET + (inHell ? (String.valueOf(ChatFormatting.WHITE) + posX + ChatFormatting.GRAY + " [" + ChatFormatting.WHITE + hposX + ChatFormatting.GRAY + "], " + ChatFormatting.WHITE + posY + ChatFormatting.GRAY + ", " + ChatFormatting.WHITE + posZ + ChatFormatting.GRAY + " [" + ChatFormatting.WHITE + hposZ + ChatFormatting.GRAY + "]") : (String.valueOf(ChatFormatting.WHITE) + posX + ChatFormatting.GRAY + " [" + ChatFormatting.WHITE + hposX + ChatFormatting.GRAY + "], " + ChatFormatting.WHITE + posY + ChatFormatting.GRAY + ", " + ChatFormatting.WHITE + posZ + ChatFormatting.GRAY + " [" + ChatFormatting.WHITE + hposZ + ChatFormatting.GRAY + "]"));
        final String direction = this.direction.getValue() ? RenoSense.rotationManager.getDirection4D(false) : "";
        final String coords = this.coords.getValue() ? coordinates : "";
        i += 10;
        if (ClickGui.getInstance().rainbow.getValue()) {
            final String rainbowCoords = this.coords.getValue() ? (inHell ? (posX + " [" + hposX + "], " + posY + ", " + posZ + " [" + hposZ + "]") : (posX + " [" + hposX + "], " + posY + ", " + posZ + " [" + hposZ + "]")) : "";
            if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                this.renderer.drawString(direction, 2.0f, (float)(height - i - 11 - (this.coords.getValue() ? 0 : -11)), ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                this.renderer.drawString(rainbowCoords, 2.0f, (float)(height - i), ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
            }
            else {
                final int[] counter2 = { 1 };
                final char[] stringToCharArray2 = direction.toCharArray();
                float s = 0.0f;
                for (final char c2 : stringToCharArray2) {
                    this.renderer.drawString(String.valueOf(c2), 2.0f + s, (float)(height - i - 11), ColorUtil.rainbow(counter2[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                    s += this.renderer.getStringWidth(String.valueOf(c2));
                    ++counter2[0];
                }
                final int[] counter3 = { 1 };
                final char[] stringToCharArray3 = rainbowCoords.toCharArray();
                float u = 0.0f;
                for (final char c3 : stringToCharArray3) {
                    this.renderer.drawString(String.valueOf(c3), 2.0f + u, (float)(height - i), ColorUtil.rainbow(counter3[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                    u += this.renderer.getStringWidth(String.valueOf(c3));
                    ++counter3[0];
                }
            }
        }
        else {
            this.renderer.drawString(direction, 2.0f, (float)(height - i - 11 - (this.coords.getValue() ? 0 : -11)), this.color, true);
            this.renderer.drawString(coords, 2.0f, (float)(height - i), this.color, true);
        }
        if (this.armor.getValue()) {
            this.renderArmorHUD(true);
        }
        if (this.totems.getValue()) {
            this.renderTotemHUD();
        }
        if (this.greeter.getValue()) {
            this.renderGreeter();
        }
        if (this.combatItems.getValue()) {
            this.renderCombatItemsHUD();
        }
        if (this.lag.getValue()) {
            this.renderLag();
        }
    }
    
    public Map<String, Integer> getTextRadarPlayers() {
        return EntityUtil.getTextRadarPlayers();
    }
    
    public void renderGreeter() {
        final int width = this.renderer.scaledWidth;
        String text = "";
        if (this.greeter.getValue()) {
            text = text + MathUtil.getTimeOfDay() + HUD.mc.player.getDisplayNameString();
        }
        if (ClickGui.getInstance().rainbow.getValue()) {
            if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                this.renderer.drawString(text, width / 2.0f - this.renderer.getStringWidth(text) / 2.0f + 2.0f, 2.0f, ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
            }
            else {
                final int[] counter1 = { 1 };
                final char[] stringToCharArray = text.toCharArray();
                float i = 0.0f;
                for (final char c : stringToCharArray) {
                    this.renderer.drawString(String.valueOf(c), width / 2.0f - this.renderer.getStringWidth(text) / 2.0f + 2.0f + i, 2.0f, ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                    i += this.renderer.getStringWidth(String.valueOf(c));
                    ++counter1[0];
                }
            }
        }
        else {
            this.renderer.drawString(text, width / 2.0f - this.renderer.getStringWidth(text) / 2.0f + 2.0f, 2.0f, this.color, true);
        }
    }
    
    public void renderLag() {
        final int width = this.renderer.scaledWidth;
        if (RenoSense.serverManager.isServerNotResponding()) {
            final String text = "Server not responding " + MathUtil.round(RenoSense.serverManager.serverRespondingTime() / 1000.0f, 1) + "s.";
            if (this.debug.getValue() && HUD.debugTimer.passedMs(this.debugDelay.getValue())) {
                Command.sendDebugMessage(text, (Module)this);
                HUD.debugTimer.reset();
            }
            this.renderer.drawString(text, width / 2.0f - this.renderer.getStringWidth(text) / 2.0f + 2.0f, 20.0f, this.color, true);
        }
    }
    
    public void renderCombatItemsHUD() {
        final int width = this.renderer.scaledWidth;
        final int height = this.renderer.scaledHeight;
        int totems = HUD.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        int obsidians = HUD.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Item.getItemFromBlock(Blocks.OBSIDIAN)).mapToInt(ItemStack::getCount).sum();
        int experience = HUD.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.EXPERIENCE_BOTTLE).mapToInt(ItemStack::getCount).sum();
        int crystals = HUD.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.END_CRYSTAL).mapToInt(ItemStack::getCount).sum();
        if (HUD.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            totems += HUD.mc.player.getHeldItemOffhand().getCount();
        }
        if (HUD.mc.player.getHeldItemOffhand().getItem() == Items.EXPERIENCE_BOTTLE) {
            experience += HUD.mc.player.getHeldItemOffhand().getCount();
        }
        if (HUD.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            crystals += HUD.mc.player.getHeldItemOffhand().getCount();
        }
        if (HUD.mc.player.getHeldItemOffhand().getItem() == Item.getItemFromBlock(Blocks.OBSIDIAN)) {
            obsidians += HUD.mc.player.getHeldItemOffhand().getCount();
        }
        GlStateManager.enableTexture2D();
        final int i = width / 2;
        final int y = height - 55 - ((HUD.mc.player.isInWater() && HUD.mc.playerController.gameIsSurvivalOrAdventure()) ? 10 : 0);
        final int x = i - 189 + 180 + 2;
        GlStateManager.enableDepth();
        RenderUtil.itemRender.zLevel = 200.0f;
        RenderUtil.itemRender.renderItemAndEffectIntoGUI(HUD.totem, x + ((this.combatItemModeSetting.getValue() == CombatItemMode.Horizontal) ? 157 : 100), y + ((this.combatItemModeSetting.getValue() == CombatItemMode.Horizontal) ? 0 : 19));
        RenderUtil.itemRender.renderItemOverlayIntoGUI(HUD.mc.fontRenderer, HUD.totem, x + 157, y, "");
        RenderUtil.itemRender.renderItemAndEffectIntoGUI(HUD.exp, x + ((this.combatItemModeSetting.getValue() == CombatItemMode.Horizontal) ? 140 : 120), y + ((this.combatItemModeSetting.getValue() == CombatItemMode.Horizontal) ? 0 : 19));
        RenderUtil.itemRender.renderItemOverlayIntoGUI(HUD.mc.fontRenderer, HUD.exp, x + 140, y, "");
        RenderUtil.itemRender.renderItemAndEffectIntoGUI(HUD.crystal, x + 120, y);
        RenderUtil.itemRender.renderItemOverlayIntoGUI(HUD.mc.fontRenderer, HUD.crystal, x + 120, y, "");
        RenderUtil.itemRender.renderItemAndEffectIntoGUI(HUD.obsidian, x + 100, y);
        RenderUtil.itemRender.renderItemOverlayIntoGUI(HUD.mc.fontRenderer, HUD.obsidian, x + 100, y, "");
        RenderUtil.itemRender.zLevel = 0.0f;
        GlStateManager.enableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        this.renderer.drawStringWithShadow(totems + "", (float)(x + ((this.combatItemModeSetting.getValue() == CombatItemMode.Horizontal) ? 157 : 100) + 19 - 2 - this.renderer.getStringWidth(totems + "")), (float)(y + 9 + ((this.combatItemModeSetting.getValue() == CombatItemMode.Horizontal) ? 0 : 19)), 16777215);
        this.renderer.drawStringWithShadow(experience + "", (float)(x + ((this.combatItemModeSetting.getValue() == CombatItemMode.Horizontal) ? 140 : 120) + 19 - 2 - this.renderer.getStringWidth(experience + "")), (float)(y + 9 + ((this.combatItemModeSetting.getValue() == CombatItemMode.Horizontal) ? 0 : 19)), 16777215);
        this.renderer.drawStringWithShadow(crystals + "", (float)(x + 120 + 19 - 2 - this.renderer.getStringWidth(crystals + "")), (float)(y + 9), 16777215);
        this.renderer.drawStringWithShadow(obsidians + "", (float)(x + 100 + 19 - 2 - this.renderer.getStringWidth(obsidians + "")), (float)(y + 9), 16777215);
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
    }
    
    public void renderTotemHUD() {
        final int width = this.renderer.scaledWidth;
        final int height = this.renderer.scaledHeight;
        int totems = HUD.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        if (HUD.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            totems += HUD.mc.player.getHeldItemOffhand().getCount();
        }
        if (totems > 0) {
            GlStateManager.enableTexture2D();
            final int i = width / 2;
            final int iteration = 0;
            final int y = height - 55 - ((HUD.mc.player.isInWater() && HUD.mc.playerController.gameIsSurvivalOrAdventure()) ? 10 : 0);
            final int x = i - 189 + 180 + 2;
            GlStateManager.enableDepth();
            RenderUtil.itemRender.zLevel = 200.0f;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(HUD.totem, x, y);
            RenderUtil.itemRender.renderItemOverlayIntoGUI(HUD.mc.fontRenderer, HUD.totem, x, y, "");
            RenderUtil.itemRender.zLevel = 0.0f;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            this.renderer.drawStringWithShadow(totems + "", (float)(x + 19 - 2 - this.renderer.getStringWidth(totems + "")), (float)(y + 9), 16777215);
            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
        }
    }
    
    public void renderArmorHUD(final boolean percent) {
        final int width = this.renderer.scaledWidth;
        final int height = this.renderer.scaledHeight;
        GlStateManager.enableTexture2D();
        final int i = width / 2;
        int iteration = 0;
        final int y = height - 55 - ((HUD.mc.player.isInWater() && HUD.mc.playerController.gameIsSurvivalOrAdventure()) ? 10 : 0);
        for (final ItemStack is : HUD.mc.player.inventory.armorInventory) {
            ++iteration;
            if (is.isEmpty()) {
                continue;
            }
            final int x = i - 90 + (9 - iteration) * 20 + 2;
            GlStateManager.enableDepth();
            RenderUtil.itemRender.zLevel = 200.0f;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(is, x, y);
            RenderUtil.itemRender.renderItemOverlayIntoGUI(HUD.mc.fontRenderer, is, x, y, "");
            RenderUtil.itemRender.zLevel = 0.0f;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            final String s = (is.getCount() > 1) ? (is.getCount() + "") : "";
            this.renderer.drawStringWithShadow(s, (float)(x + 19 - 2 - this.renderer.getStringWidth(s)), (float)(y + 9), 16777215);
            if (!percent) {
                continue;
            }
            int dmg = 0;
            final int itemDurability = is.getMaxDamage() - is.getItemDamage();
            final float green = (is.getMaxDamage() - (float)is.getItemDamage()) / is.getMaxDamage();
            final float red = 1.0f - green;
            dmg = (percent ? (100 - (int)(red * 100.0f)) : itemDurability);
            this.renderer.drawStringWithShadow(dmg + "", (float)(x + 8 - this.renderer.getStringWidth(dmg + "") / 2), (float)(y - 11), ColorUtil.toRGBA((int)(red * 255.0f), (int)(green * 255.0f), 0));
        }
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
    }
    
    @SubscribeEvent
    public void onUpdateWalkingPlayer(final AttackEntityEvent event) {
        this.shouldIncrement = true;
    }
    
    @Override
    public void onLoad() {
        RenoSense.commandManager.setClientMessage(this.getCommandMessage());
    }
    
    @SubscribeEvent
    public void onSettingChange(final ClientEvent event) {
        if (event.getSetting() != null && this.equals(event.getSetting().getFeature())) {
            RenoSense.commandManager.setClientMessage(this.getCommandMessage());
        }
    }
    
    @SubscribeEvent
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (event.getStage() == 0 && event.getPacket() instanceof SPacketChat && this.timestamp.getValue()) {
            final String originalMessage = ((SPacketChat)event.getPacket()).chatComponent.getFormattedText();
            final String message = this.getTimeString(originalMessage) + originalMessage;
            ((SPacketChat)event.getPacket()).chatComponent = (ITextComponent)new TextComponentString(message);
        }
    }
    
    public String getTimeString(final String message) {
        final String date = new SimpleDateFormat("h:mm").format(new Date());
        final String timeString = "<" + date + "> ";
        final StringBuilder builder = new StringBuilder(timeString);
        builder.insert(0, ((boolean)this.rainbowPrefix.getValue()) ? "§+" : ChatFormatting.LIGHT_PURPLE);
        if (!message.contains(getInstance().getRainbowCommandMessage())) {
            builder.append("§r");
        }
        return builder.toString();
    }
    
    public String getTimeString2() {
        final String date = new SimpleDateFormat("h:mm").format(new Date());
        final String timeString = "<" + date + ">";
        final StringBuilder builder = new StringBuilder(timeString);
        return builder.toString();
    }
    
    public String getCommandMessage() {
        if (this.commandPrefix.getValue() || this.timestamp.getValue()) {
            final StringBuilder stringBuilder = new StringBuilder((this.timestamp.getValue() ? this.getTimeString2() : "") + (this.commandPrefix.getValue() ? ("<" + this.getRawCommandMessage() + ">") : ""));
            stringBuilder.insert(0, ((this.timestamp.getValue() || this.commandPrefix.getValue()) && this.rainbowPrefix.getValue()) ? "§+" : ChatFormatting.LIGHT_PURPLE);
            stringBuilder.append("§r ");
            return stringBuilder.toString();
        }
        return "";
    }
    
    public String getRainbowCommandMessage() {
        final StringBuilder stringBuilder = new StringBuilder(this.getRawCommandMessage());
        stringBuilder.insert(0, ((boolean)this.rainbowPrefix.getValue()) ? "§+" : ChatFormatting.LIGHT_PURPLE);
        stringBuilder.append("§r");
        return stringBuilder.toString();
    }
    
    public String getRawCommandMessage() {
        return this.command.getValue();
    }
    
    public void drawTextRadar(final int yOffset) {
        if (!this.players.isEmpty()) {
            int y = this.renderer.getFontHeight() + 7 + yOffset;
            for (final Map.Entry<String, Integer> player : this.players.entrySet()) {
                final String text = player.getKey() + " ";
                final int textheight = this.renderer.getFontHeight() + 1;
                this.renderer.drawString(text, 2.0f, (float)y, this.textRadarColor, true);
                y += textheight;
            }
        }
    }
    
    static {
        box = new ResourceLocation("textures/gui/container/shulker_box.png");
        totem = new ItemStack(Items.TOTEM_OF_UNDYING);
        crystal = new ItemStack(Items.END_CRYSTAL);
        obsidian = new ItemStack(Item.getItemFromBlock(Blocks.OBSIDIAN));
        exp = new ItemStack(Items.EXPERIENCE_BOTTLE);
        HUD.INSTANCE = new HUD();
        HUD.debugTimer = new Timer();
    }
    
    public enum RenderingMode
    {
        Length, 
        ABC;
    }
    
    public enum SpeedMode
    {
        BLOCKS, 
        KMH;
    }
    
    public enum CombatItemMode
    {
        Horizontal, 
        Square;
    }
    
    public enum HudMode
    {
        HUD, 
        WATERMARK, 
        PREFIX, 
        TEXTRADAR;
    }
}
