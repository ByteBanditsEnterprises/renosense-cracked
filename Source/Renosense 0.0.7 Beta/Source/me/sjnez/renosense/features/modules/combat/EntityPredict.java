//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.combat;

import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.features.setting.*;
import me.sjnez.renosense.util.*;
import net.minecraft.util.math.*;
import net.minecraft.entity.*;
import net.minecraft.init.*;
import net.minecraft.entity.item.*;
import java.util.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import me.sjnez.renosense.event.events.*;
import me.sjnez.renosense.features.command.*;

public class EntityPredict extends Module
{
    public Setting<Integer> startFrom;
    public Setting<Integer> attackAmount;
    public Setting<Boolean> holdingCrystalOnly;
    public Setting<Integer> debugDelay;
    public static Timer debugTimer;
    private final Timer timer;
    int currentId;
    BlockPos currentPos;
    Entity entity;
    
    public EntityPredict() {
        super("EntityPredict", "IronMans best friend.", Category.COMBAT, true, false, false);
        this.startFrom = (Setting<Integer>)this.register(new Setting("StartFrom", (T)5, (T)0, (T)10, "Change this setting according to your ping to make the entity predict work faster."));
        this.attackAmount = (Setting<Integer>)this.register(new Setting("EndFrom", (T)1, (T)1, (T)15, "How many times the entity is being attacked."));
        this.holdingCrystalOnly = (Setting<Boolean>)this.register(new Setting("Holding Crystal Only", (T)false));
        this.debugDelay = (Setting<Integer>)this.register(new Setting("DebugDelay", (T)200, (T)0, (T)10000, v -> this.debug.getValue(), "Delay between messages sent."));
        this.timer = new Timer();
    }
    
    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (fullNullCheck()) {
            return;
        }
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && this.timer.passedMs(2000L)) {
            int entityId = 0;
            final CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock)event.getPacket();
            if (!EntityPredict.mc.world.getBlockState(packet.position).getBlock().equals(Blocks.OBSIDIAN) && !EntityPredict.mc.world.getBlockState(packet.position).getBlock().equals(Blocks.BEDROCK)) {
                return;
            }
            if (EntityPredict.mc.player.getHeldItemMainhand().getItem().equals(Items.EXPERIENCE_BOTTLE)) {
                return;
            }
            if (this.holdingCrystalOnly.getValue() && !EntityPredict.mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL) && !EntityPredict.mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL)) {
                return;
            }
            for (final Entity entity : new ArrayList<Entity>(EntityPredict.mc.world.loadedEntityList)) {
                if (entity instanceof EntityEnderCrystal) {
                    if (entity.entityId > entityId) {
                        entityId = entity.entityId;
                    }
                    this.entity = entity;
                }
            }
            this.currentPos = packet.getPos();
            if (this.entity != null) {
                for (int i = 1 - this.startFrom.getValue(); i <= this.attackAmount.getValue(); ++i) {
                    this.attackEntity(entityId + i);
                }
            }
        }
    }
    
    void attackEntity(final int entityId) {
        final CPacketUseEntity packet = new CPacketUseEntity();
        packet.entityId = entityId;
        packet.action = CPacketUseEntity.Action.ATTACK;
        EntityPredict.mc.player.connection.sendPacket((Packet)packet);
        this.currentId = entityId;
    }
    
    @SubscribeEvent
    public void onPlayerDeath(final DeathEvent event) {
        this.timer.reset();
    }
    
    @Override
    public void onRender2D(final Render2DEvent event) {
        if (this.debug.getValue() && EntityPredict.debugTimer.passedMs(this.debugDelay.getValue())) {
            Command.sendDebugMessage("Entity Id: " + this.currentId + "", (Module)this);
            EntityPredict.debugTimer.reset();
        }
    }
    
    static {
        EntityPredict.debugTimer = new Timer();
    }
}
