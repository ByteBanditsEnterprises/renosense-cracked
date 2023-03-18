//Raddon On Top!

package org.apache.commons.io.input;

import java.io.*;
import java.util.function.*;

public class CharacterFilterReader extends AbstractCharacterFilterReader
{
    public CharacterFilterReader(final Reader reader, final int skip) {
        super(reader, c -> c == skip);
    }
    
    public CharacterFilterReader(final Reader reader, final IntPredicate skip) {
        super(reader, skip);
    }
}
