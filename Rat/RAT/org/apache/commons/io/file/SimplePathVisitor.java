//Raddon On Top!

package org.apache.commons.io.file;

import java.nio.file.*;

public abstract class SimplePathVisitor extends SimpleFileVisitor<Path> implements PathVisitor
{
    protected SimplePathVisitor() {
    }
}
