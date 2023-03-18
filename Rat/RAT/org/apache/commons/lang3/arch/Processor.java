//Raddon On Top!

package org.apache.commons.lang3.arch;

public class Processor
{
    private final Arch arch;
    private final Type type;
    
    public Processor(final Arch arch, final Type type) {
        this.arch = arch;
        this.type = type;
    }
    
    public Arch getArch() {
        return this.arch;
    }
    
    public Type getType() {
        return this.type;
    }
    
    public boolean is32Bit() {
        return Arch.BIT_32 == this.arch;
    }
    
    public boolean is64Bit() {
        return Arch.BIT_64 == this.arch;
    }
    
    public boolean isX86() {
        return Type.X86 == this.type;
    }
    
    public boolean isIA64() {
        return Type.IA_64 == this.type;
    }
    
    public boolean isPPC() {
        return Type.PPC == this.type;
    }
    
    public enum Arch
    {
        BIT_32("32-bit"), 
        BIT_64("64-bit"), 
        UNKNOWN("Unknown");
        
        private final String label;
        
        private Arch(final String label) {
            this.label = label;
        }
        
        public String getLabel() {
            return this.label;
        }
    }
    
    public enum Type
    {
        X86, 
        IA_64, 
        PPC, 
        UNKNOWN;
    }
}
