//Raddon On Top!

package org.apache.commons.io.output;

import java.util.*;
import java.io.*;

public class TeeWriter extends ProxyCollectionWriter
{
    public TeeWriter(final Collection<Writer> writers) {
        super((Collection)writers);
    }
    
    public TeeWriter(final Writer... writers) {
        super(writers);
    }
}
