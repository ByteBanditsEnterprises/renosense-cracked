//Raddon On Top!

package org.apache.commons.codec.language.bm;

import org.apache.commons.codec.*;

public class BeiderMorseEncoder implements StringEncoder
{
    private PhoneticEngine engine;
    
    public BeiderMorseEncoder() {
        this.engine = new PhoneticEngine(NameType.GENERIC, RuleType.APPROX, true);
    }
    
    public Object encode(final Object source) throws EncoderException {
        if (!(source instanceof String)) {
            throw new EncoderException("BeiderMorseEncoder encode parameter is not of type String");
        }
        return this.encode((String)source);
    }
    
    @Override
    public String encode(final String source) throws EncoderException {
        if (source == null) {
            return null;
        }
        return this.engine.encode(source);
    }
    
    public NameType getNameType() {
        return this.engine.getNameType();
    }
    
    public RuleType getRuleType() {
        return this.engine.getRuleType();
    }
    
    public boolean isConcat() {
        return this.engine.isConcat();
    }
    
    public void setConcat(final boolean concat) {
        this.engine = new PhoneticEngine(this.engine.getNameType(), this.engine.getRuleType(), concat, this.engine.getMaxPhonemes());
    }
    
    public void setNameType(final NameType nameType) {
        this.engine = new PhoneticEngine(nameType, this.engine.getRuleType(), this.engine.isConcat(), this.engine.getMaxPhonemes());
    }
    
    public void setRuleType(final RuleType ruleType) {
        this.engine = new PhoneticEngine(this.engine.getNameType(), ruleType, this.engine.isConcat(), this.engine.getMaxPhonemes());
    }
    
    public void setMaxPhonemes(final int maxPhonemes) {
        this.engine = new PhoneticEngine(this.engine.getNameType(), this.engine.getRuleType(), this.engine.isConcat(), maxPhonemes);
    }
}
