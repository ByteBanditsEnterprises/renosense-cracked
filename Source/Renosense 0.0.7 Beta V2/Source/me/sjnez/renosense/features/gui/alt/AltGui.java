//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.gui.alt;

import net.minecraft.client.gui.*;
import com.mojang.realmsclient.gui.*;
import me.sjnez.renosense.*;
import me.sjnez.renosense.util.*;
import java.util.*;
import org.lwjgl.input.*;
import java.awt.*;
import java.awt.datatransfer.*;
import net.minecraft.util.*;
import java.io.*;

public class AltGui implements Util
{
    public static ArrayList<AltButton> altButtons;
    private static String email;
    private static String password;
    private static boolean e;
    private static boolean p;
    private static boolean microsoft;
    public static boolean isActive;
    public static int y;
    private static final Timer typingIconTimer;
    
    public static void drawScreen(final int mouseX, final int mouseY) {
        if (!AltGui.isActive) {
            return;
        }
        final ScaledResolution scaledResolution = new ScaledResolution(AltGui.mc);
        final float width = (float)scaledResolution.getScaledWidth();
        final float height = (float)scaledResolution.getScaledHeight();
        final float x = width - 300.0f;
        final float center = x + 150.0f;
        final String text = ChatFormatting.BOLD + "Currently logged in as " + AltGui.mc.session.getUsername() + ".";
        RenoSense.textManager.drawStringWithShadow(text, width / 2.0f - RenoSense.textManager.getStringWidth(text) / 2.0f, (float)AltGui.y, Color.WHITE.getRGB());
        RenderUtil.drawRect(x, 0.0f, width, height, new Color(14, 14, 14).getRGB());
        RenderUtil.drawRect(x, 0.0f, width, 20.0f, new Color(12, 12, 12).getRGB());
        RenderUtil.drawRect(x, 19.0f, width, 20.0f, new Color(20, 20, 20).getRGB());
        RenoSense.textManager.drawStringWithShadow("Alt Manager", center - RenoSense.textManager.getStringWidth("Alt Manager") / 2.0f, 10.0f - RenoSense.textManager.getFontHeight() / 2.0f, Color.WHITE.getRGB());
        RenderUtil.drawRect(x, height - 85.0f, width, height, new Color(12, 12, 12).getRGB());
        RenderUtil.drawRect(x, height - 85.0f, width, height - 84.0f, new Color(20, 20, 20).getRGB());
        final boolean canAdd = !AltGui.password.equals("") && !AltGui.email.equals("");
        RenderUtil.drawRect(x + 5.0f, height - 30.0f, x + 72.5f, height - 10.0f, new Color(20, 20, 20).getRGB());
        RenoSense.textManager.drawStringWithShadow("Add Alt", x + 36.25f - RenoSense.textManager.getStringWidth("Add Alt") / 2.0f, height - 20.0f - RenoSense.textManager.getFontHeight() / 2.0f, canAdd ? Color.WHITE.getRGB() : Color.GRAY.getRGB());
        if (mouseX > x + 5.0f && mouseX < x + 72.5f && mouseY > height - 30.0f && mouseY < height - 10.0f) {
            RenderUtil.drawRect(x + 5.0f, height - 30.0f, x + 72.5f, height - 10.0f, new Color(0, 0, 0, 50).getRGB());
        }
        RenderUtil.drawRect(x + 77.5f, height - 30.0f, x + 145.0f, height - 10.0f, AltGui.microsoft ? Color.CYAN.getRGB() : Color.RED.getRGB());
        if (mouseX > x + 77.5f && mouseX < x + 145.0f && mouseY > height - 30.0f && mouseY < height - 10.0f) {
            RenderUtil.drawRect(x + 77.5f, height - 30.0f, x + 145.0f, height - 10.0f, new Color(0, 0, 0, 150).getRGB());
        }
        RenoSense.textManager.drawStringWithShadow(AltGui.microsoft ? "Microsoft" : "Cracked", x + 112.5f - RenoSense.textManager.getStringWidth(AltGui.microsoft ? "Microsoft" : "Cracked") / 2.0f, height - 20.0f - RenoSense.textManager.getFontHeight() / 2.0f, Color.WHITE.getRGB());
        RenderUtil.drawRect(x + 5.0f, height - 55.0f, width - 5.0f, height - 35.0f, new Color(20, 20, 20).getRGB());
        if (AltGui.password.equals("")) {
            RenoSense.textManager.drawStringWithShadow("Password" + (AltGui.p ? typingIcon() : ""), x + 7.5f, height - 45.0f - RenoSense.textManager.getFontHeight() / 2.0f, new Color(100, 100, 100, 50).getRGB());
        }
        else {
            final StringBuilder pass = new StringBuilder();
            for (final char ignored : AltGui.password.toCharArray()) {
                pass.append("*");
            }
            RenoSense.textManager.drawStringWithShadow(pass.toString() + (AltGui.p ? typingIcon() : ""), x + 7.5f, height - 45.0f - RenoSense.textManager.getFontHeight() / 2.0f, Color.WHITE.getRGB());
        }
        RenderUtil.drawRect(x + 5.0f, height - 80.0f, width - 5.0f, height - 60.0f, new Color(20, 20, 20).getRGB());
        if (AltGui.email.equals("")) {
            RenoSense.textManager.drawStringWithShadow((AltGui.microsoft ? "Email" : "Username") + (AltGui.e ? typingIcon() : ""), x + 7.5f, height - 70.0f - RenoSense.textManager.getFontHeight() / 2.0f, new Color(100, 100, 100, 50).getRGB());
        }
        else {
            RenoSense.textManager.drawStringWithShadow(AltGui.email + (AltGui.e ? typingIcon() : ""), x + 7.5f, height - 70.0f - RenoSense.textManager.getFontHeight() / 2.0f, Color.WHITE.getRGB());
        }
        AltGui.altButtons.forEach(altButton -> {
            altButton.x = x + 5.0f;
            altButton.drawScreen(mouseX, mouseY);
        });
    }
    
    public static void keyTyped(final char typedChar, final int keyCode) {
        if (!AltGui.isActive) {
            return;
        }
        if (AltGui.e) {
            AltGui.email = type(typedChar, keyCode, AltGui.email);
        }
        else if (AltGui.p) {
            AltGui.password = type(typedChar, keyCode, AltGui.password);
        }
    }
    
    public static void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        if (!AltGui.isActive) {
            return;
        }
        final ScaledResolution scaledResolution = new ScaledResolution(AltGui.mc);
        final float width = (float)scaledResolution.getScaledWidth();
        final float height = (float)scaledResolution.getScaledHeight();
        final float x = width - 300.0f;
        if (mouseButton == 0) {
            if (!AltGui.password.equals("") && !AltGui.email.equals("") && mouseX > x + 5.0f && mouseX < x + 72.5f && mouseY > height - 30.0f && mouseY < height - 10.0f) {
                AltGui.altButtons.add(new AltButton(AltGui.email, AltGui.password, AltGui.microsoft ? Alt.AltType.MICROSOFT : Alt.AltType.CRACKED, x + 5.0f, 25.0f + AltGui.altButtons.size() * 50.0f, 290.0f, 45.0f));
                AltGui.email = "";
                AltGui.password = "";
            }
            if (mouseX > x + 77.5f && mouseX < x + 145.0f && mouseY > height - 30.0f && mouseY < height - 10.0f) {
                AltGui.microsoft = !AltGui.microsoft;
                AltGui.email = "";
                AltGui.password = "";
            }
            if (mouseX > x + 5.0f && mouseX < width - 5.0f) {
                AltGui.e = (mouseY > height - 80.0f && mouseY < height - 60.0f);
                AltGui.p = (mouseY > height - 55.0f && mouseY < height - 35.0f);
            }
        }
        new ArrayList(AltGui.altButtons).forEach(altButton -> altButton.mouseClicked(mouseX, mouseY, mouseButton));
    }
    
    public static void updateButtons() {
        final ScaledResolution scaledResolution = new ScaledResolution(AltGui.mc);
        final float width = (float)scaledResolution.getScaledWidth();
        final float x = width - 150.0f;
        final ArrayList<AltButton> altButtons1 = new ArrayList<AltButton>();
        for (final AltButton altButton : AltGui.altButtons) {
            altButtons1.add(new AltButton(altButton.email, altButton.password, altButton.altType, x + 5.0f, 25.0f + altButtons1.size() * 50.0f, 290.0f, 45.0f));
        }
        AltGui.altButtons = altButtons1;
    }
    
    private static String typingIcon() {
        if (AltGui.typingIconTimer.passedMs(1000L)) {
            AltGui.typingIconTimer.reset();
        }
        if (AltGui.typingIconTimer.passedMs(500L)) {
            return "";
        }
        return "_";
    }
    
    private static String type(final char typedChar, final int keyCode, final String string) {
        String newString = string;
        switch (keyCode) {
            case 15: {
                if (AltGui.e) {
                    AltGui.e = false;
                    AltGui.p = true;
                    break;
                }
                break;
            }
            case 29: {
                if (Keyboard.isKeyDown(47)) {
                    try {
                        newString += Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).toString();
                    }
                    catch (UnsupportedFlavorException unsupportedFlavorException) {
                        unsupportedFlavorException.printStackTrace();
                    }
                    catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    break;
                }
                break;
            }
            case 14: {
                if (Keyboard.isKeyDown(29)) {
                    newString = "";
                }
                if (newString.length() > 0) {
                    newString = newString.substring(0, newString.length() - 1);
                    break;
                }
                break;
            }
            case 27:
            case 28: {
                AltGui.e = false;
                AltGui.p = false;
                break;
            }
            default: {
                if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                    newString += typedChar;
                    break;
                }
                break;
            }
        }
        return newString;
    }
    
    public static void saveAlts() {
        final File file = new File("renosense/alts.txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            final FileWriter writer = new FileWriter(file);
            for (final AltButton altButton : AltGui.altButtons) {
                writer.write(altButton.email + ":" + altButton.password + ":" + altButton.altType.equals((Object)Alt.AltType.MICROSOFT) + "\n");
            }
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void loadAlts() {
        final File file = new File("renosense/alts.txt");
        try {
            if (file.exists()) {
                final ScaledResolution scaledResolution = new ScaledResolution(AltGui.mc);
                final float width = (float)scaledResolution.getScaledWidth();
                final float x = width - 150.0f;
                final FileReader reader = new FileReader(file);
                final BufferedReader bufferedReader = new BufferedReader(reader);
                final String[] split;
                final ArrayList<AltButton> altButtons;
                final AltButton altButton;
                final float n;
                bufferedReader.lines().forEach(line -> {
                    split = line.split(":");
                    altButtons = AltGui.altButtons;
                    new AltButton(split[0], split[1], Boolean.parseBoolean(split[2]) ? Alt.AltType.MICROSOFT : Alt.AltType.CRACKED, n + 5.0f, 25.0f + AltGui.altButtons.size() * 50.0f, 290.0f, 45.0f);
                    altButtons.add(altButton);
                });
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    static {
        AltGui.altButtons = new ArrayList<AltButton>();
        AltGui.email = "";
        AltGui.password = "";
        AltGui.e = false;
        AltGui.p = false;
        AltGui.microsoft = false;
        typingIconTimer = new Timer();
    }
}
