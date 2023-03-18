//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.asm.mixin.transformer;

import org.spongepowered.asm.mixin.extensibility.*;
import org.spongepowered.asm.mixin.*;

public class Config
{
    private final String name;
    private final MixinConfig config;
    
    public Config(final MixinConfig config) {
        this.name = config.getName();
        this.config = config;
    }
    
    public String getName() {
        return this.name;
    }
    
    MixinConfig get() {
        return this.config;
    }
    
    public boolean isVisited() {
        return this.config.isVisited();
    }
    
    public IMixinConfig getConfig() {
        return (IMixinConfig)this.config;
    }
    
    public MixinEnvironment getEnvironment() {
        return this.config.getEnvironment();
    }
    
    @Override
    public String toString() {
        return this.config.toString();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof Config && this.name.equals(((Config)obj).name);
    }
    
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
    
    @Deprecated
    public static Config create(final String configFile, final MixinEnvironment outer) {
        return MixinConfig.create(configFile, outer);
    }
    
    public static Config create(final String configFile) {
        return MixinConfig.create(configFile, MixinEnvironment.getDefaultEnvironment());
    }
}
