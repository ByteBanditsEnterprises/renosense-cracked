//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.event.events;

import me.sjnez.renosense.event.*;

public class RenderItemEvent extends EventStage
{
    double mainX;
    double mainY;
    double mainZ;
    double offX;
    double offY;
    double offZ;
    double mainRAngel;
    double mainRx;
    double mainRy;
    double mainRz;
    double offRAngel;
    double offRx;
    double offRy;
    double offRz;
    double mainHandScaleX;
    double mainHandScaleY;
    double mainHandScaleZ;
    double offHandScaleX;
    double offHandScaleY;
    double offHandScaleZ;
    
    public RenderItemEvent(final double mainX, final double mainY, final double mainZ, final double offX, final double offY, final double offZ, final double mainRAngel, final double mainRx, final double mainRy, final double mainRz, final double offRAngel, final double offRx, final double offRy, final double offRz, final double mainHandScaleX, final double mainHandScaleY, final double mainHandScaleZ, final double offHandScaleX, final double offHandScaleY, final double offHandScaleZ) {
        this.mainX = mainX;
        this.mainY = mainY;
        this.mainZ = mainZ;
        this.offX = offX;
        this.offY = offY;
        this.offZ = offZ;
        this.mainRAngel = mainRAngel;
        this.mainRx = mainRx;
        this.mainRy = mainRy;
        this.mainRz = mainRz;
        this.offRAngel = offRAngel;
        this.offRx = offRx;
        this.offRy = offRy;
        this.offRz = offRz;
        this.mainHandScaleX = mainHandScaleX;
        this.mainHandScaleY = mainHandScaleY;
        this.mainHandScaleZ = mainHandScaleZ;
        this.offHandScaleX = offHandScaleX;
        this.offHandScaleY = offHandScaleY;
        this.offHandScaleZ = offHandScaleZ;
    }
    
    public void setMainX(final double v) {
        this.mainX = v;
    }
    
    public void setMainY(final double v) {
        this.mainY = v;
    }
    
    public void setMainZ(final double v) {
        this.mainZ = v;
    }
    
    public void setOffX(final double v) {
        this.offX = v;
    }
    
    public void setOffY(final double v) {
        this.offY = v;
    }
    
    public void setOffZ(final double v) {
        this.offZ = v;
    }
    
    public void setOffRAngel(final double v) {
        this.offRAngel = v;
    }
    
    public void setOffRx(final double v) {
        this.offRx = v;
    }
    
    public void setOffRy(final double v) {
        this.offRy = v;
    }
    
    public void setOffRz(final double v) {
        this.offRz = v;
    }
    
    public void setMainRAngel(final double v) {
        this.mainRAngel = v;
    }
    
    public void setMainRx(final double v) {
        this.mainRx = v;
    }
    
    public void setMainRy(final double v) {
        this.mainRy = v;
    }
    
    public void setMainRz(final double v) {
        this.mainRz = v;
    }
    
    public void setMainHandScaleX(final double v) {
        this.mainHandScaleX = v;
    }
    
    public void setMainHandScaleY(final double v) {
        this.mainHandScaleY = v;
    }
    
    public void setMainHandScaleZ(final double v) {
        this.mainHandScaleZ = v;
    }
    
    public void setOffHandScaleX(final double v) {
        this.offHandScaleX = v;
    }
    
    public void setOffHandScaleY(final double v) {
        this.offHandScaleY = v;
    }
    
    public void setOffHandScaleZ(final double v) {
        this.offHandScaleZ = v;
    }
    
    public double getMainX() {
        return this.mainX;
    }
    
    public double getMainY() {
        return this.mainY;
    }
    
    public double getMainZ() {
        return this.mainZ;
    }
    
    public double getOffX() {
        return this.offX;
    }
    
    public double getOffY() {
        return this.offY;
    }
    
    public double getOffZ() {
        return this.offZ;
    }
    
    public double getMainRAngel() {
        return this.mainRAngel;
    }
    
    public double getMainRx() {
        return this.mainRx;
    }
    
    public double getMainRy() {
        return this.mainRy;
    }
    
    public double getMainRz() {
        return this.mainRz;
    }
    
    public double getOffRAngel() {
        return this.offRAngel;
    }
    
    public double getOffRx() {
        return this.offRx;
    }
    
    public double getOffRy() {
        return this.offRy;
    }
    
    public double getOffRz() {
        return this.offRz;
    }
    
    public double getMainHandScaleX() {
        return this.mainHandScaleX;
    }
    
    public double getMainHandScaleY() {
        return this.mainHandScaleY;
    }
    
    public double getMainHandScaleZ() {
        return this.mainHandScaleZ;
    }
    
    public double getOffHandScaleX() {
        return this.offHandScaleX;
    }
    
    public double getOffHandScaleY() {
        return this.offHandScaleY;
    }
    
    public double getOffHandScaleZ() {
        return this.offHandScaleZ;
    }
}
