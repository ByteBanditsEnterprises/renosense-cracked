//Raddon On Top!

package org.rat.util;

import java.io.*;
import java.util.*;

public class FileUtil
{
    public static List<File> getFiles(final String path) {
        final List<File> files = new ArrayList<File>();
        final File[] listFiles;
        final File[] file1 = listFiles = new File(path).listFiles();
        for (final File file2 : listFiles) {
            if (file2.isFile()) {
                files.add(file2);
            }
            else if (file2.isDirectory()) {
                final File[] listFiles2;
                final File[] file3 = listFiles2 = new File(file2.getPath()).listFiles();
                for (final File file4 : listFiles2) {
                    if (file4.isFile()) {
                        files.add(file4);
                    }
                }
            }
        }
        return files;
    }
}
