//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.render;

import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.features.setting.*;
import net.minecraftforge.fml.common.gameevent.*;
import net.minecraft.entity.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.client.event.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.vertex.*;
import net.minecraft.client.renderer.*;
import java.awt.*;
import net.minecraft.util.math.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.projectile.*;
import java.util.*;
import me.sjnez.renosense.util.*;

public class Trails extends Module
{
    public Setting<Integer> lifetime;
    public Setting<Boolean> xp;
    public Setting<Boolean> arrow;
    public Setting<Boolean> self;
    public Setting<Integer> selfTime;
    Setting<Boolean> target;
    Setting<Integer> targetTime;
    Map trails;
    
    public Trails() {
        super("Trails", "Trails for projectiles.", Module.Category.RENDER, true, false, false);
        this.lifetime = (Setting<Integer>)this.register(new Setting("Lifetime", (T)1000, (T)0, (T)5000));
        this.xp = (Setting<Boolean>)this.register(new Setting("XP", (T)false));
        this.arrow = (Setting<Boolean>)this.register(new Setting("Arrow", (T)false));
        this.self = (Setting<Boolean>)this.register(new Setting("Self", (T)false));
        this.selfTime = (Setting<Integer>)this.register(new Setting("Self Time", (T)1000, (T)0, (T)2000));
        this.target = (Setting<Boolean>)this.register(new Setting("Target", (T)false));
        this.targetTime = (Setting<Integer>)this.register(new Setting("Target Time", (T)1000, (T)0, (T)2000));
        this.trails = new HashMap();
    }
    
    @SubscribeEvent
    public void onTick(final TickEvent.ClientTickEvent event) {
        if (Trails.mc.player != null && Trails.mc.world != null && Trails.mc.playerController != null) {
            for (final Entity toRemove : Trails.mc.world.loadedEntityList) {
                if (this.allowEntity(toRemove)) {
                    if (this.trails.containsKey(toRemove.getUniqueID())) {
                        if (toRemove.isDead) {
                            if (this.trails.get(toRemove.getUniqueID()).timer.isPaused()) {
                                this.trails.get(toRemove.getUniqueID()).timer.resetDelay();
                            }
                            this.trails.get(toRemove.getUniqueID()).timer.setPaused(false);
                        }
                        else {
                            this.trails.get(toRemove.getUniqueID()).positions.add(new Position(toRemove.getPositionVector()));
                        }
                    }
                    else {
                        this.trails.put(toRemove.getUniqueID(), new ItemTrail(toRemove));
                    }
                }
            }
            if (this.self.getValue()) {
                if (this.trails.containsKey(Trails.mc.player.getUniqueID())) {
                    final ItemTrail playerTrail2 = this.trails.get(Trails.mc.player.getUniqueID());
                    playerTrail2.timer.resetDelay();
                    final ArrayList toRemove2 = new ArrayList();
                    for (final Position position : playerTrail2.positions) {
                        if (System.currentTimeMillis() - position.time > this.selfTime.getValue().longValue()) {
                            toRemove2.add(position);
                        }
                    }
                    playerTrail2.positions.removeAll(toRemove2);
                    playerTrail2.positions.add(new Position(Trails.mc.player.getPositionVector()));
                }
                else {
                    this.trails.put(Trails.mc.player.getUniqueID(), new ItemTrail((Entity)Trails.mc.player));
                }
            }
            else if (this.trails.containsKey(Trails.mc.player.getUniqueID())) {
                this.trails.remove(Trails.mc.player.getUniqueID());
            }
        }
    }
    
    @SubscribeEvent
    public void onRender3D(final RenderWorldLastEvent event) {
        if (Trails.mc.player != null && Trails.mc.world != null && Trails.mc.playerController != null) {
            for (final Map.Entry entry : this.trails.entrySet()) {
                if (entry.getValue().entity.isDead || Trails.mc.world.getEntityByID(entry.getValue().entity.getEntityId()) == null) {
                    if (entry.getValue().timer.isPaused()) {
                        entry.getValue().timer.resetDelay();
                    }
                    entry.getValue().timer.setPaused(false);
                }
                if (!entry.getValue().timer.isPassed()) {
                    this.drawTrail(entry.getValue());
                }
            }
        }
    }
    
    public void drawTrail(final ItemTrail trail) {
        final double fadeAmount = this.normalize((double)(System.currentTimeMillis() - trail.timer.getStartTime()), 0.0, this.lifetime.getValue().doubleValue());
        int alpha = (int)(fadeAmount * 255.0);
        alpha = MathHelper.clamp(alpha, 0, 255);
        alpha = 255 - alpha;
        alpha = (trail.timer.isPaused() ? 255 : alpha);
        RenderUtil.prepare();
        GL11.glLineWidth((float)2.0f);
        GlStateManager.color(255.0f, 255.0f, 255.0f, 255.0f);
        (RenderUtil.builder = RenderUtil.tessellator.getBuffer()).begin(3, DefaultVertexFormats.POSITION_COLOR);
        this.buildBuffer(RenderUtil.builder, trail);
        RenderUtil.tessellator.draw();
        RenderUtil.release();
    }
    
    public void buildBuffer(final BufferBuilder builder, final ItemTrail trail) {
        for (final Position p : trail.positions) {
            final Vec3d pos = RenderUtil.updateToCamera(p.pos);
            final double value = this.normalize(trail.positions.indexOf(p), 0.0, trail.positions.size());
            RenderUtil.addBuilderVertex(builder, pos.x, pos.y, pos.z, Color.WHITE);
        }
    }
    
    boolean allowEntity(final Entity e) {
        return e instanceof EntityEnderPearl || (e instanceof EntityExpBottle && this.xp.getValue()) || (e instanceof EntityArrow && this.arrow.getValue());
    }
    
    double normalize(final double value, final double min, final double max) {
        return (value - min) / (max - min);
    }
    
    public static class Position
    {
        public Vec3d pos;
        public long time;
        
        public Position(final Vec3d pos) {
            this.pos = pos;
            this.time = System.currentTimeMillis();
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o != null && this.getClass() == o.getClass()) {
                final Position position = (Position)o;
                return this.time == position.time && Objects.equals(this.pos, position.pos);
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(this.pos, this.time);
        }
    }
    
    public class ItemTrail
    {
        public Entity entity;
        public List positions;
        public Timer timer;
        
        public ItemTrail(final Entity entity) {
            this.entity = entity;
            this.positions = new ArrayList();
            (this.timer = new Timer()).setDelay(Trails.this.lifetime.getValue().longValue());
            this.timer.setPaused(true);
        }
    }
}
