//Raddon On Top!

package org.apache.commons.lang3.text;

import java.text.*;

@Deprecated
public class CompositeFormat extends Format
{
    private static final long serialVersionUID = -4329119827877627683L;
    private final Format parser;
    private final Format formatter;
    
    public CompositeFormat(final Format parser, final Format formatter) {
        this.parser = parser;
        this.formatter = formatter;
    }
    
    @Override
    public StringBuffer format(final Object obj, final StringBuffer toAppendTo, final FieldPosition pos) {
        return this.formatter.format(obj, toAppendTo, pos);
    }
    
    @Override
    public Object parseObject(final String source, final ParsePosition pos) {
        return this.parser.parseObject(source, pos);
    }
    
    public Format getParser() {
        return this.parser;
    }
    
    public Format getFormatter() {
        return this.formatter;
    }
    
    public String reformat(final String input) throws ParseException {
        return this.format(this.parseObject(input));
    }
}
