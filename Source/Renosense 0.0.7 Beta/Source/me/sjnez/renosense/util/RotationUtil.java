//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.util;

import net.minecraft.entity.player.*;
import net.minecraft.entity.*;
import me.sjnez.renosense.features.modules.client.*;
import net.minecraft.util.math.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import com.mojang.realmsclient.gui.*;

public class RotationUtil implements Util
{
    public static Vec3d getEyesPos() {
        return new Vec3d(RotationUtil.mc.player.posX, RotationUtil.mc.player.posY + RotationUtil.mc.player.getEyeHeight(), RotationUtil.mc.player.posZ);
    }
    
    public static double[] calculateLookAt(final double px, final double py, final double pz, final EntityPlayer me) {
        double dirx = me.posX - px;
        double diry = me.posY - py;
        double dirz = me.posZ - pz;
        final double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);
        double pitch = Math.asin(diry /= len);
        dirz /= len;
        double yaw = Math.atan2(dirz, dirx /= len);
        pitch = pitch * 180.0 / 3.141592653589793;
        yaw = yaw * 180.0 / 3.141592653589793;
        final double[] array = new double[2];
        yaw = (array[0] = yaw + 90.0);
        array[1] = pitch;
        return array;
    }
    
    public static float[] getLegitRotations(final Vec3d vec) {
        final Vec3d eyesPos = getEyesPos();
        final double diffX = vec.x - eyesPos.x;
        final double diffY = vec.y - eyesPos.y;
        final double diffZ = vec.z - eyesPos.z;
        final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        final float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        final float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[] { RotationUtil.mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - RotationUtil.mc.player.rotationYaw), RotationUtil.mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - RotationUtil.mc.player.rotationPitch) };
    }
    
    public static boolean isInFov(final BlockPos pos) {
        return pos != null && (RotationUtil.mc.player.getDistanceSq(pos) < 4.0 || yawDist(pos) < getHalvedfov() + 2.0f);
    }
    
    public static boolean isInFov(final Entity entity) {
        return entity != null && (RotationUtil.mc.player.getDistanceSq(entity) < 4.0 || yawDist(entity) < getHalvedfov() + 2.0f);
    }
    
    public static float getFov() {
        return (float)(ClickGui.getInstance().customFov.getValue() ? ClickGui.getInstance().fov.getValue() : RotationUtil.mc.gameSettings.fovSetting);
    }
    
    public static float getHalvedfov() {
        return getFov() / 2.0f;
    }
    
    public static double yawDist(final BlockPos pos) {
        if (pos != null) {
            final Vec3d difference = new Vec3d((Vec3i)pos).subtract(RotationUtil.mc.player.getPositionEyes(RotationUtil.mc.getRenderPartialTicks()));
            final double d = Math.abs(RotationUtil.mc.player.rotationYaw - (Math.toDegrees(Math.atan2(difference.z, difference.x)) - 90.0)) % 360.0;
            return (d > 180.0) ? (360.0 - d) : d;
        }
        return 0.0;
    }
    
    public static double yawDist(final Entity e) {
        if (e != null) {
            final Vec3d difference = e.getPositionVector().add(0.0, (double)(e.getEyeHeight() / 2.0f), 0.0).subtract(RotationUtil.mc.player.getPositionEyes(RotationUtil.mc.getRenderPartialTicks()));
            final double d = Math.abs(RotationUtil.mc.player.rotationYaw - (Math.toDegrees(Math.atan2(difference.z, difference.x)) - 90.0)) % 360.0;
            return (d > 180.0) ? (360.0 - d) : d;
        }
        return 0.0;
    }
    
    public static float transformYaw() {
        float yaw = RotationUtil.mc.player.rotationYaw % 360.0f;
        if (RotationUtil.mc.player.rotationYaw > 0.0f) {
            if (yaw > 180.0f) {
                yaw = -180.0f + (yaw - 180.0f);
            }
        }
        else if (yaw < -180.0f) {
            yaw = 180.0f + (yaw + 180.0f);
        }
        if (yaw < 0.0f) {
            return 180.0f + yaw;
        }
        return -180.0f + yaw;
    }
    
    public static boolean isInFov(final Vec3d vec3d, final Vec3d other) {
        Label_0069: {
            if (RotationUtil.mc.player.rotationPitch > 30.0f) {
                if (other.y <= RotationUtil.mc.player.posY) {
                    break Label_0069;
                }
            }
            else if (RotationUtil.mc.player.rotationPitch >= -30.0f || other.y >= RotationUtil.mc.player.posY) {
                break Label_0069;
            }
            return true;
        }
        final float angle = MathUtil.calcAngleNoY(vec3d, other)[0] - transformYaw();
        if (angle < -270.0f) {
            return true;
        }
        final float fov = (float)((ClickGui.getInstance().customFov.getValue() ? ClickGui.getInstance().fov.getValue() : RotationUtil.mc.gameSettings.fovSetting) / 2.0f);
        return angle < fov + 10.0f && angle > -fov - 10.0f;
    }
    
    public static void faceYawAndPitch(final float yaw, final float pitch) {
        RotationUtil.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(yaw, pitch, RotationUtil.mc.player.onGround));
    }
    
    public static void faceVector(final Vec3d vec, final boolean normalizeAngle) {
        final float[] rotations = getLegitRotations(vec);
        RotationUtil.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(rotations[0], normalizeAngle ? ((float)MathHelper.normalizeAngle((int)rotations[1], 360)) : rotations[1], RotationUtil.mc.player.onGround));
    }
    
    public static void faceEntity(final Entity entity) {
        final float[] angle = MathUtil.calcAngle(RotationUtil.mc.player.getPositionEyes(RotationUtil.mc.getRenderPartialTicks()), entity.getPositionEyes(RotationUtil.mc.getRenderPartialTicks()));
        faceYawAndPitch(angle[0], angle[1]);
    }
    
    public static float[] getAngle(final Entity entity) {
        return MathUtil.calcAngle(RotationUtil.mc.player.getPositionEyes(RotationUtil.mc.getRenderPartialTicks()), entity.getPositionEyes(RotationUtil.mc.getRenderPartialTicks()));
    }
    
    public static int getDirection4D() {
        return MathHelper.floor(RotationUtil.mc.player.rotationYaw * 4.0f / 360.0f + 0.5) & 0x3;
    }
    
    public static String getDirection4D(final boolean northRed) {
        final int dirnumber = getDirection4D();
        if (dirnumber == 0) {
            if (!(boolean)ClickGui.getInstance().rainbow.getValue()) {
                return "South " + ChatFormatting.GRAY + "[" + ChatFormatting.WHITE + "+Z" + ChatFormatting.GRAY + "]";
            }
            return "South [+Z]";
        }
        else if (dirnumber == 1) {
            if (!(boolean)ClickGui.getInstance().rainbow.getValue()) {
                return "West " + ChatFormatting.GRAY + "[" + ChatFormatting.WHITE + "-X" + ChatFormatting.GRAY + "]";
            }
            return "West [-X]";
        }
        else if (dirnumber == 2) {
            if (!(boolean)ClickGui.getInstance().rainbow.getValue()) {
                return (northRed ? "\u00c2§c" : "") + "North " + ChatFormatting.GRAY + "[" + ChatFormatting.WHITE + "-Z" + ChatFormatting.GRAY + "]";
            }
            return "North [-Z]";
        }
        else {
            if (dirnumber != 3) {
                return "Loading...";
            }
            if (!(boolean)ClickGui.getInstance().rainbow.getValue()) {
                return "East " + ChatFormatting.GRAY + "[" + ChatFormatting.WHITE + "+X" + ChatFormatting.GRAY + "]";
            }
            return "East [+X]";
        }
    }
}
