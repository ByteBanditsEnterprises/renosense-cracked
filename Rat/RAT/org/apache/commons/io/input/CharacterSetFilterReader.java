//Raddon On Top!

package org.apache.commons.io.input;

import java.util.function.*;
import java.io.*;
import java.util.*;

public class CharacterSetFilterReader extends AbstractCharacterFilterReader
{
    private static IntPredicate toIntPredicate(final Set<Integer> skip) {
        if (skip == null) {
            return CharacterSetFilterReader.SKIP_NONE;
        }
        final Set<Integer> unmodifiableSet = Collections.unmodifiableSet((Set<? extends Integer>)skip);
        return c -> unmodifiableSet.contains(c);
    }
    
    public CharacterSetFilterReader(final Reader reader, final Integer... skip) {
        this(reader, new HashSet<Integer>(Arrays.asList(skip)));
    }
    
    public CharacterSetFilterReader(final Reader reader, final Set<Integer> skip) {
        super(reader, toIntPredicate(skip));
    }
}
