//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.gui.management;

import net.minecraft.client.gui.*;
import net.minecraft.client.*;
import com.google.common.collect.*;
import me.sjnez.renosense.*;
import me.sjnez.renosense.manager.*;
import org.lwjgl.input.*;
import com.mojang.realmsclient.gui.*;
import java.awt.*;
import java.io.*;
import scala.actors.threadpool.*;
import java.util.*;

public class PlayerManagerGui extends GuiScreen
{
    int i;
    int ei;
    int s;
    public List<GuiButton> playerList;
    public List<GuiButton> playerList2;
    static List<String> friendList;
    static List<String> enemyList;
    private final GuiScreen previousGuiScreen;
    private List<GuiButton> addSuper;
    private GuiTextField addFriend;
    public float y1;
    public int i1;
    public int i2;
    public int i3;
    
    public PlayerManagerGui(final GuiScreen parent, final Minecraft mcIn) {
        this.i = 0;
        this.ei = 2000;
        this.s = 0;
        this.playerList = (List<GuiButton>)Lists.newArrayList();
        this.playerList2 = (List<GuiButton>)Lists.newArrayList();
        this.addSuper = (List<GuiButton>)Lists.newArrayList();
        this.mc = mcIn;
        this.previousGuiScreen = parent;
    }
    
    public void getFriends() {
        for (final FriendManager.Friend friend : RenoSense.friendManager.getFriends()) {
            PlayerManagerGui.friendList.add(friend.getUsername());
        }
    }
    
    public void getEnemies() {
        for (final EnemyManager.Enemy enemy : RenoSense.enemyManager.getEnemies()) {
            PlayerManagerGui.enemyList.add(enemy.getUsername());
        }
    }
    
    public void checkMouseWheel() {
        final int dWheel = Mouse.getDWheel();
        if (dWheel < 0) {
            for (final GuiButton guiButton : this.playerList) {
                final GuiButton component = guiButton;
                guiButton.y -= 10;
            }
            for (final GuiButton guiButton2 : this.playerList2) {
                final GuiButton component = guiButton2;
                guiButton2.y -= 10;
            }
            for (final GuiButton guiButton3 : this.addSuper) {
                final GuiButton component = guiButton3;
                guiButton3.y -= 10;
            }
        }
        else if (dWheel > 0) {
            for (final GuiButton guiButton4 : this.playerList) {
                final GuiButton component = guiButton4;
                guiButton4.y += 10;
            }
            for (final GuiButton guiButton5 : this.playerList2) {
                final GuiButton component = guiButton5;
                guiButton5.y += 10;
            }
            for (final GuiButton guiButton6 : this.addSuper) {
                final GuiButton component = guiButton6;
                guiButton6.y += 10;
            }
        }
    }
    
    public void initGui() {
        PlayerManagerGui.friendList.clear();
        this.playerList.clear();
        PlayerManagerGui.enemyList.clear();
        this.buttonList.clear();
        this.addSuper.clear();
        this.getFriends();
        this.getEnemies();
        int y = -140;
        for (final String friendlist : PlayerManagerGui.friendList) {
            this.playerList.add(new GuiButton(this.i, this.width / 2 - 420, this.height - 200 - y, 20, 20, ChatFormatting.RED + "-"));
            this.playerList.add(new GuiButton(300 + this.i, this.width / 2 - 400, this.height - 200 - y, 200, 20, ChatFormatting.GREEN + friendlist));
            ++this.i;
            y += 23;
        }
        if (!PlayerManagerGui.friendList.isEmpty()) {
            this.buttonList.addAll(this.playerList);
        }
        this.buttonList.add(new GuiButton(999, this.width / 2 + 150, this.height - 60, 200, 20, "Back to RenoSense Manager"));
        this.buttonList.add(new GuiButton(1967, this.width / 2 + 329, this.height - 139, 20, 20, ChatFormatting.GREEN + "+"));
        this.addFriend = new GuiTextField(954, this.fontRenderer, this.width / 2 + 152, this.height - 115, 196, 20);
        int y2 = -140;
        for (final String enemyList : PlayerManagerGui.enemyList) {
            this.playerList2.add(new GuiButton(this.ei, this.width / 2 - 170, this.height - 200 - y2, 20, 20, ChatFormatting.RED + "-"));
            this.playerList2.add(new GuiButton(3000 + this.ei, this.width / 2 - 150, this.height - 200 - y2, 200, 20, (RenoSense.enemyManager.isSuperEnemy(enemyList) ? (ChatFormatting.DARK_RED + "" + ChatFormatting.BOLD) : ChatFormatting.RED) + enemyList));
            this.addSuper.add(new GuiButton(this.ei + 4000, this.width / 2 + 50, this.height - 200 - y2, 20, 20, ChatFormatting.DARK_RED + "+"));
            if (RenoSense.enemyManager.isSuperEnemy(enemyList)) {
                this.addSuper.get(this.s).enabled = false;
            }
            ++this.s;
            ++this.ei;
            y2 += 23;
        }
        if (!PlayerManagerGui.enemyList.isEmpty()) {
            this.buttonList.addAll(this.addSuper);
            this.buttonList.addAll(this.playerList2);
        }
        this.buttonList.add(new GuiButton(1968, this.width / 2 + 309, this.height - 139, 20, 20, ChatFormatting.RED + "+"));
        this.buttonList.add(new GuiButton(1969, this.width / 2 + 289, this.height - 139, 20, 20, ChatFormatting.DARK_RED + "+"));
        this.buttonList.add(new GuiButton(1234, this.width / 2 + 150, this.height - 85, 200, 20, "Save Config"));
    }
    
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.checkMouseWheel();
        this.drawDefaultBackground();
        this.addFriend.drawTextBox();
        this.drawString(this.fontRenderer, "Add players", this.width / 2 + 150, this.height - 135, 10526880);
        this.fontRenderer.drawSplitString("Player" + ChatFormatting.LIGHT_PURPLE + "Manager", 10, 10, 200, Color.GREEN.getRGB());
        this.fontRenderer.drawSplitString("Use comma seperated values if you want to add more than one player at once!                                          ex: \"KingHolecamp,Sjnez,Rwah,277\" (Without the quotes of course.)", this.width / 2 + 150, this.height - 260, 200, Color.PINK.getRGB());
        this.fontRenderer.drawSplitString("If you don't see all of your friends and enemies, make sure to use the scroll wheel to find them!", this.width / 2 + 150, this.height - 170, 200, Color.YELLOW.getRGB());
        this.fontRenderer.drawSplitString("The green + is for adding friends, the light red + is for adding enemies, and the dark red + is for adding super enemies!", this.width / 2 + 150, this.height - 210, 200, Color.ORANGE.getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    public void updateScreen() {
        super.updateScreen();
        this.i2 = 0;
        this.i1 = 0;
        this.i = 0;
        this.ei = 2000;
        this.s = 0;
        this.addFriend.updateCursorCounter();
    }
    
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.addFriend.mouseClicked(mouseX, mouseY, mouseButton);
    }
    
    protected void keyTyped(final char typedChar, final int keyCode) {
        this.addFriend.textboxKeyTyped(typedChar, keyCode);
    }
    
    protected void actionPerformed(final GuiButton button) {
        if (button.id == 999) {
            this.mc.displayGuiScreen(this.previousGuiScreen);
        }
        if (button.id == 1967) {
            if (RenoSense.enemyManager.isEnemy(this.addFriend.getText())) {
                return;
            }
            if (this.addFriend.getText().contains(",")) {
                final String c = this.addFriend.getText();
                final String[] combo = c.split(",");
                final List<String> fixedLengthList = (List<String>)Arrays.asList((Object[])combo);
                final ArrayList<String> listOfString = new ArrayList<String>(fixedLengthList);
                for (int i = 0; i < listOfString.size(); ++i) {
                    RenoSense.friendManager.addFriend(combo[i]);
                }
            }
            else {
                RenoSense.friendManager.addFriend(this.addFriend.getText());
            }
        }
        if (button.id == 1969) {
            if (RenoSense.friendManager.isFriend(this.addFriend.getText())) {
                return;
            }
            if (this.addFriend.getText().isEmpty() || this.addFriend.getText().equalsIgnoreCase(" ")) {
                return;
            }
            if (this.addFriend.getText().contains(",")) {
                final String c = this.addFriend.getText();
                final String[] combo = c.split(",");
                final List<String> fixedLengthList = (List<String>)Arrays.asList((Object[])combo);
                final ArrayList<String> listOfString = new ArrayList<String>(fixedLengthList);
                for (int i = 0; i < listOfString.size(); ++i) {
                    RenoSense.enemyManager.addEnemy(combo[i], 2);
                }
            }
            else {
                RenoSense.enemyManager.addEnemy(this.addFriend.getText(), 2);
            }
        }
        if (button.id == 1968) {
            if (this.addFriend.getText().isEmpty() || this.addFriend.getText().equalsIgnoreCase(" ")) {
                return;
            }
            if (RenoSense.friendManager.isFriend(this.addFriend.getText())) {
                return;
            }
            if (this.addFriend.getText().contains(",")) {
                final String c = this.addFriend.getText();
                final String[] combo = c.split(",");
                final List<String> fixedLengthList = (List<String>)Arrays.asList((Object[])combo);
                final ArrayList<String> listOfString = new ArrayList<String>(fixedLengthList);
                for (int i = 0; i < listOfString.size(); ++i) {
                    RenoSense.enemyManager.addEnemy(combo[i], 1);
                }
            }
            else {
                RenoSense.enemyManager.addEnemy(this.addFriend.getText(), 1);
            }
        }
        if (button.id >= 6000 && button.id < 7000) {
            this.i3 = 6000;
            while (this.i3 < this.playerList2.size() + 6000) {
                if (button.id == this.i3) {
                    RenoSense.enemyManager.editEnemy(this.i3 - 6000, PlayerManagerGui.enemyList.get(this.i3 - 6000), 2);
                }
                ++this.i3;
            }
        }
        if (button.id >= 2000 && button.id < 3000) {
            this.i2 = 2000;
            while (this.i2 < this.playerList2.size() + 2000) {
                if (button.id == this.i2) {
                    if (RenoSense.enemyManager.isSuperEnemy(PlayerManagerGui.enemyList.get(this.i2 - 2000))) {
                        RenoSense.enemyManager.editEnemy(this.i2 - 2000, PlayerManagerGui.enemyList.get(this.i2 - 2000), 1);
                    }
                    else {
                        this.buttonList.remove(this.i1);
                        RenoSense.enemyManager.removeEnemy(PlayerManagerGui.enemyList.get(this.i2 - 2000));
                    }
                }
                ++this.i2;
            }
        }
        if (button.id >= 0 && button.id < 299) {
            this.i1 = 0;
            while (this.i1 < this.playerList.size()) {
                if (button.id == this.i1) {
                    this.buttonList.remove(this.i1);
                    RenoSense.friendManager.removeFriend(PlayerManagerGui.friendList.get(this.i1));
                }
                ++this.i1;
            }
        }
        if (button.id == 1234) {
            System.out.println("Saving " + RenoSense.configManager.loadCurrentConfig() + " Config!");
            RenoSense.configManager.saveConfig(RenoSense.configManager.loadCurrentConfig());
        }
        this.buttonList.clear();
        this.playerList.clear();
        this.playerList2.clear();
        this.initGui();
        this.updateScreen();
    }
    
    static {
        PlayerManagerGui.friendList = new ArrayList<String>();
        PlayerManagerGui.enemyList = new ArrayList<String>();
    }
}
