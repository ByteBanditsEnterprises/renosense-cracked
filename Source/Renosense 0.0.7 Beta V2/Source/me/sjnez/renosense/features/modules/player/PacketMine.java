//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.player;

import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.features.setting.*;
import net.minecraft.block.state.*;
import me.sjnez.renosense.features.*;
import net.minecraft.network.*;
import java.awt.*;
import net.minecraft.network.play.client.*;
import net.minecraft.util.math.*;
import net.minecraft.entity.*;
import net.minecraft.entity.item.*;
import java.util.*;
import net.minecraftforge.fml.common.eventhandler.*;
import me.sjnez.renosense.event.events.*;
import me.sjnez.renosense.util.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.util.*;

public class PacketMine extends Module
{
    public Setting<Boolean> tweaks;
    public Setting<Boolean> reset;
    public Setting<Float> range;
    public Setting<Boolean> silent;
    public Setting<Boolean> noBreakAnim;
    public Setting<Boolean> noDelay;
    public Setting<Boolean> noSwing;
    public Setting<Boolean> allow;
    public Setting<Boolean> doubleBreak;
    public Setting<Boolean> render;
    public Setting<Integer> red;
    public Setting<Integer> green;
    public Setting<Integer> blue;
    public Setting<Boolean> box;
    public Setting<Boolean> outline;
    public final Setting<Float> lineWidth;
    public final Setting<Integer> boxAlpha;
    private static PacketMine INSTANCE;
    public BlockPos currentPos;
    public IBlockState currentBlockState;
    public float breakTime;
    public final Timer timer;
    private boolean isMining;
    private BlockPos lastPos;
    private EnumFacing lastFacing;
    private boolean shouldSwitch;
    
    public PacketMine() {
        super("PacketMine", "Speeds up mining.", Module.Category.PLAYER, true, false, false);
        this.tweaks = (Setting<Boolean>)this.register(new Setting("Tweaks", (T)true));
        this.reset = (Setting<Boolean>)this.register(new Setting("Reset", (T)true));
        this.range = (Setting<Float>)this.register(new Setting("Range", (T)10.0f, (T)0.0f, (T)50.0f));
        this.silent = (Setting<Boolean>)this.register(new Setting("Silent", (T)true));
        this.noBreakAnim = (Setting<Boolean>)this.register(new Setting("NoBreakAnim", (T)false));
        this.noDelay = (Setting<Boolean>)this.register(new Setting("NoDelay", (T)false));
        this.noSwing = (Setting<Boolean>)this.register(new Setting("NoSwing", (T)false));
        this.allow = (Setting<Boolean>)this.register(new Setting("AllowMultiTask", (T)false));
        this.doubleBreak = (Setting<Boolean>)this.register(new Setting("DoubleBreak", (T)false));
        this.render = (Setting<Boolean>)this.register(new Setting("Render", (T)false));
        this.red = (Setting<Integer>)this.register(new Setting("Red", (T)125, (T)0, (T)255, v -> this.render.getValue()));
        this.green = (Setting<Integer>)this.register(new Setting("Green", (T)105, (T)0, (T)255, v -> this.render.getValue()));
        this.blue = (Setting<Integer>)this.register(new Setting("Blue", (T)255, (T)0, (T)255, v -> this.render.getValue()));
        this.box = (Setting<Boolean>)this.register(new Setting("Box", (T)false, v -> this.render.getValue()));
        this.outline = (Setting<Boolean>)this.register(new Setting("Outline", (T)true, v -> this.render.getValue()));
        this.lineWidth = (Setting<Float>)this.register(new Setting("LineWidth", (T)1.0f, (T)0.1f, (T)5.0f, v -> this.outline.getValue() && this.render.getValue()));
        this.boxAlpha = (Setting<Integer>)this.register(new Setting("BoxAlpha", (T)85, (T)0, (T)255, v -> this.box.getValue() && this.render.getValue()));
        this.breakTime = -1.0f;
        this.timer = new Timer();
        this.isMining = false;
        this.lastPos = null;
        this.lastFacing = null;
        this.shouldSwitch = false;
        this.setInstance();
    }
    
    private void setInstance() {
        PacketMine.INSTANCE = this;
    }
    
    public static PacketMine getInstance() {
        if (PacketMine.INSTANCE == null) {
            PacketMine.INSTANCE = new PacketMine();
        }
        return PacketMine.INSTANCE;
    }
    
    public void onEnable() {
        this.shouldSwitch = false;
    }
    
    public void onTick() {
        if (Feature.fullNullCheck()) {
            return;
        }
        if (this.currentPos != null && PacketMine.mc.player != null && PacketMine.mc.player.getDistanceSq(this.currentPos) > MathUtil.square(this.range.getValue())) {
            this.currentPos = null;
            this.currentBlockState = null;
            PacketMine.mc.playerController.isHittingBlock = false;
            return;
        }
        this.onMine();
    }
    
    public void onMine() {
        if (this.currentPos != null && (!PacketMine.mc.world.getBlockState(this.currentPos).equals(this.currentBlockState) || PacketMine.mc.world.getBlockState(this.currentPos).getBlock() == Blocks.AIR)) {
            this.currentPos = null;
            this.currentBlockState = null;
            this.shouldSwitch = true;
        }
    }
    
    public void onUpdate() {
        if (Feature.fullNullCheck()) {
            return;
        }
        if (this.noDelay.getValue()) {
            PacketMine.mc.playerController.blockHitDelay = 0;
        }
        if (this.isMining && this.lastPos != null && this.lastFacing != null && this.noBreakAnim.getValue()) {
            PacketMine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.lastPos, this.lastFacing));
        }
        if (this.reset.getValue() && PacketMine.mc.gameSettings.keyBindUseItem.isKeyDown() && !this.allow.getValue()) {
            PacketMine.mc.playerController.isHittingBlock = false;
        }
    }
    
    public void onRender3D(final Render3DEvent event) {
        if (this.render.getValue() && this.currentPos != null) {
            final Color color = new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.boxAlpha.getValue());
            RenderUtil.gradientBox(this.currentPos, color, this.lineWidth.getValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), true);
        }
    }
    
    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (Feature.fullNullCheck()) {
            return;
        }
        if (event.getStage() == 0) {
            if (this.noSwing.getValue() && event.getPacket() instanceof CPacketAnimation) {
                event.setCanceled(true);
            }
            if (this.noBreakAnim.getValue() && event.getPacket() instanceof CPacketPlayerDigging) {
                final CPacketPlayerDigging packet = (CPacketPlayerDigging)event.getPacket();
                if (packet != null && packet.getPosition() != null) {
                    try {
                        for (final Entity entity : PacketMine.mc.world.getEntitiesWithinAABBExcludingEntity((Entity)null, new AxisAlignedBB(packet.getPosition()))) {
                            if (entity instanceof EntityEnderCrystal) {
                                this.showAnimation();
                                return;
                            }
                        }
                    }
                    catch (Exception ex) {}
                    if (packet.getAction().equals((Object)CPacketPlayerDigging.Action.START_DESTROY_BLOCK)) {
                        this.showAnimation(true, packet.getPosition(), packet.getFacing());
                    }
                    if (packet.getAction().equals((Object)CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK)) {
                        this.showAnimation();
                    }
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onBlockEvent(final BlockEvent event) {
        if (Feature.fullNullCheck()) {
            return;
        }
        if (event.getStage() == 3 && this.reset.getValue() && PacketMine.mc.playerController.curBlockDamageMP > 0.1f) {
            PacketMine.mc.playerController.isHittingBlock = true;
        }
        if (event.getStage() == 4 && this.tweaks.getValue()) {
            if (BlockUtil.canBreak(event.pos)) {
                if (this.currentPos == null) {
                    this.currentPos = event.pos;
                    this.currentBlockState = PacketMine.mc.world.getBlockState(this.currentPos);
                    final ItemStack pick = new ItemStack(Items.DIAMOND_PICKAXE);
                    this.breakTime = pick.getDestroySpeed(this.currentBlockState) / 3.71f;
                    this.timer.reset();
                }
                PacketMine.mc.player.swingArm(EnumHand.MAIN_HAND);
                PacketMine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.pos, event.facing));
                PacketMine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
                event.setCanceled(true);
            }
            if (this.doubleBreak.getValue()) {
                final BlockPos above = event.pos.add(0, 1, 0);
                if (BlockUtil.canBreak(above) && PacketMine.mc.player.getDistance((double)above.getX(), (double)above.getY(), (double)above.getZ()) <= 5.0) {
                    PacketMine.mc.player.swingArm(EnumHand.MAIN_HAND);
                    PacketMine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, above, event.facing));
                    PacketMine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, above, event.facing));
                    PacketMine.mc.playerController.onPlayerDestroyBlock(above);
                    PacketMine.mc.world.setBlockToAir(above);
                }
            }
        }
    }
    
    private void showAnimation(final boolean isMining, final BlockPos lastPos, final EnumFacing lastFacing) {
        this.isMining = isMining;
        this.lastPos = lastPos;
        this.lastFacing = lastFacing;
    }
    
    public void showAnimation() {
        this.showAnimation(false, null, null);
    }
    
    static {
        PacketMine.INSTANCE = new PacketMine();
    }
}
