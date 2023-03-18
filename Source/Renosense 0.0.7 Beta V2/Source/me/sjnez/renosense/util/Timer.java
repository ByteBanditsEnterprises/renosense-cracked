//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.util;

public class Timer
{
    private long time;
    private long current;
    long startTime;
    long delay;
    boolean paused;
    
    public Timer() {
        this.startTime = System.currentTimeMillis();
        this.delay = 0L;
        this.paused = false;
        this.time = -1L;
        this.current = System.currentTimeMillis();
    }
    
    public boolean passedS(final double s) {
        return this.passedMs((long)s * 1000L);
    }
    
    public boolean passedDms(final double dms) {
        return this.passedMs((long)dms * 10L);
    }
    
    public boolean passedDs(final double ds) {
        return this.passedMs((long)ds * 100L);
    }
    
    public boolean passedMs(final long ms) {
        return this.passedNS(this.convertToNS(ms));
    }
    
    public void setMs(final long ms) {
        this.time = System.nanoTime() - this.convertToNS(ms);
    }
    
    public boolean passedNS(final long ns) {
        return System.nanoTime() - this.time >= ns;
    }
    
    public long getPassedTimeMs() {
        return this.getMs(System.nanoTime() - this.time);
    }
    
    public Timer reset() {
        this.time = System.nanoTime();
        return this;
    }
    
    public void reset2() {
        this.current = System.currentTimeMillis();
    }
    
    public long getMs(final long time) {
        return time / 1000000L;
    }
    
    public long convertToNS(final long time) {
        return time * 1000000L;
    }
    
    public boolean passed(final long delay) {
        return System.currentTimeMillis() - this.current >= delay;
    }
    
    public boolean isPassed() {
        return !this.paused && System.currentTimeMillis() - this.startTime >= this.delay;
    }
    
    public void resetDelay() {
        this.startTime = System.currentTimeMillis();
    }
    
    public void setDelay(final long delay) {
        this.delay = delay;
    }
    
    public void setPaused(final boolean paused) {
        this.paused = paused;
    }
    
    public boolean isPaused() {
        return this.paused;
    }
    
    public long getStartTime() {
        return this.startTime;
    }
}
