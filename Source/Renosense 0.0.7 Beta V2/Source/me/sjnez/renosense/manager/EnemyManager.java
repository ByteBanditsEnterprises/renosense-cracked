//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.manager;

import me.sjnez.renosense.features.*;
import net.minecraft.entity.player.*;
import me.sjnez.renosense.*;
import com.mojang.realmsclient.gui.*;
import me.sjnez.renosense.features.command.*;
import me.sjnez.renosense.features.setting.*;
import java.util.*;
import java.util.function.*;
import net.minecraft.client.network.*;

public class EnemyManager extends Feature
{
    private List<Enemy> enemies;
    
    public EnemyManager() {
        super("Enemies");
        this.enemies = new ArrayList<Enemy>();
    }
    
    public boolean isSuperEnemy(final String name) {
        this.cleanEnemies();
        for (final Enemy enemy : this.enemies) {
            if (enemy.getUsername().equalsIgnoreCase(name) && enemy.priority == 2) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isSuperEnemy(final EntityPlayer player) {
        return this.isSuperEnemy(player.getName());
    }
    
    public boolean isEnemy(final String name) {
        this.cleanEnemies();
        for (final Enemy enemy : this.enemies) {
            if (enemy.username.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
    
    public void editEnemy(final int index, final String name, final int priority) {
        final Enemy enemy = this.getEnemyByName(name, priority);
        this.enemies.set(index, enemy);
    }
    
    public void editEnemy(final String name, final int priority) {
        final Enemy enemy = this.getEnemyByName(name, priority);
        this.removeEnemySilently(enemy.getUsername());
        this.addEnemy(enemy.getUsername(), priority);
    }
    
    public boolean isEnemy(final EntityPlayer player) {
        return this.isEnemy(player.getName());
    }
    
    public void addEnemy(final String name, final int priority) {
        final Enemy enemy = this.getEnemyByName(name, priority);
        if (RenoSense.friendManager.isFriend(enemy.getUsername())) {
            Command.sendMessage(ChatFormatting.RED + enemy.getUsername() + " is already a FRIEND, they can't be an enemy unless you unfriend them!");
            return;
        }
        if (this.isEnemy(enemy.getUsername())) {
            Command.sendMessage(ChatFormatting.RED + enemy.getUsername() + " is already an enemy.");
            return;
        }
        Command.sendMessage(ChatFormatting.GREEN + enemy.getUsername() + " has been added as an enemy with priority of " + priority + "/2");
        this.enemies.add(enemy);
        this.cleanEnemies();
    }
    
    private void isCracked(final String username) {
    }
    
    public void removeEnemySilently(final String name) {
        this.cleanEnemies();
        for (final Enemy enemy : this.enemies) {
            if (!enemy.getUsername().equalsIgnoreCase(name)) {
                continue;
            }
            this.enemies.remove(enemy);
            break;
        }
    }
    
    public void removeEnemy(final String name) {
        this.cleanEnemies();
        for (final Enemy enemy : this.enemies) {
            if (!RenoSense.enemyManager.isEnemy(enemy.getUsername())) {
                Command.sendMessage(ChatFormatting.RED + enemy.getUsername() + " is not an enemy.");
                return;
            }
            if (!enemy.getUsername().equalsIgnoreCase(name)) {
                continue;
            }
            Command.sendMessage(ChatFormatting.RED + enemy.getUsername() + " has been un-enemies");
            this.enemies.remove(enemy);
            break;
        }
    }
    
    public void onLoad() {
        this.enemies = new ArrayList<Enemy>();
        this.clearSettings();
    }
    
    public void saveEnemies() {
        this.clearSettings();
        this.cleanEnemies();
        for (final Enemy enemy : this.enemies) {
            this.register(new Setting(enemy.getUsername(), (Object)enemy.getPriority()));
        }
    }
    
    public void cleanEnemies() {
        this.enemies.stream().filter(Objects::nonNull).filter(enemy -> enemy.getUsername() != null);
    }
    
    public List<Enemy> getEnemies() {
        this.cleanEnemies();
        return this.enemies;
    }
    
    public boolean areEnemiesOnline() {
        for (final NetworkPlayerInfo networkPlayerInfo : Objects.requireNonNull(EnemyManager.mc.getConnection()).getPlayerInfoMap()) {
            if (this.isEnemy(Objects.requireNonNull(networkPlayerInfo.getGameProfile().getName()))) {
                return true;
            }
        }
        return false;
    }
    
    public Enemy getEnemyByName(final String input, final int priority) {
        final Enemy enemy = new Enemy(priority, input);
        return enemy;
    }
    
    public void addEnemy(final Enemy enemy) {
        this.enemies.add(enemy);
    }
    
    public static class Enemy
    {
        private final String username;
        private final int priority;
        
        public Enemy(final int priority, final String username) {
            this.username = username;
            this.priority = priority;
        }
        
        public int getPriority() {
            return this.priority;
        }
        
        public String getUsername() {
            return this.username;
        }
    }
}
