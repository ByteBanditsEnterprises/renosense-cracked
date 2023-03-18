//Raddon On Top!

package org.apache.commons.codec.language.bm;

public enum RuleType
{
    APPROX("approx"), 
    EXACT("exact"), 
    RULES("rules");
    
    private final String name;
    
    private RuleType(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
}
