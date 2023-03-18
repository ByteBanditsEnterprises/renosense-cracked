//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.asm.bridge;

import org.spongepowered.asm.mixin.extensibility.*;
import org.spongepowered.asm.util.*;
import org.objectweb.asm.commons.*;
import org.apache.logging.log4j.*;

public abstract class RemapperAdapter implements IRemapper, ObfuscationUtil.IClassRemapper
{
    protected final Logger logger;
    protected final Remapper remapper;
    
    public RemapperAdapter(final Remapper remapper) {
        this.logger = LogManager.getLogger("mixin");
        this.remapper = remapper;
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
    
    @Override
    public String mapMethodName(final String owner, final String name, final String desc) {
        this.logger.debug("{} is remapping method {}{} for {}", new Object[] { this, name, desc, owner });
        final String newName = this.remapper.mapMethodName(owner, name, desc);
        if (!newName.equals(name)) {
            return newName;
        }
        final String obfOwner = this.unmap(owner);
        final String obfDesc = this.unmapDesc(desc);
        this.logger.debug("{} is remapping obfuscated method {}{} for {}", new Object[] { this, name, obfDesc, obfOwner });
        return this.remapper.mapMethodName(obfOwner, name, obfDesc);
    }
    
    @Override
    public String mapFieldName(final String owner, final String name, final String desc) {
        this.logger.debug("{} is remapping field {}{} for {}", new Object[] { this, name, desc, owner });
        final String newName = this.remapper.mapFieldName(owner, name, desc);
        if (!newName.equals(name)) {
            return newName;
        }
        final String obfOwner = this.unmap(owner);
        final String obfDesc = this.unmapDesc(desc);
        this.logger.debug("{} is remapping obfuscated field {}{} for {}", new Object[] { this, name, obfDesc, obfOwner });
        return this.remapper.mapFieldName(obfOwner, name, obfDesc);
    }
    
    @Override
    public String map(final String typeName) {
        this.logger.debug("{} is remapping class {}", new Object[] { this, typeName });
        return this.remapper.map(typeName);
    }
    
    @Override
    public String unmap(final String typeName) {
        return typeName;
    }
    
    @Override
    public String mapDesc(final String desc) {
        return this.remapper.mapDesc(desc);
    }
    
    @Override
    public String unmapDesc(final String desc) {
        return ObfuscationUtil.unmapDescriptor(desc, this);
    }
}
