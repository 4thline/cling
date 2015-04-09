/*
 * Copyright (C) 2013 4th Line GmbH, Switzerland
 *
 * The contents of this file are subject to the terms of either the GNU
 * Lesser General Public License Version 2 or later ("LGPL") or the
 * Common Development and Distribution License Version 1 or later
 * ("CDDL") (collectively, the "License"). You may not use this file
 * except in compliance with the License. See LICENSE.txt for more
 * information.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.fourthline.cling.android;

import android.net.ConnectivityManager;
import org.fourthline.cling.transport.impl.NetworkAddressFactoryImpl;

import java.lang.reflect.Field;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This factory tries to work around and patch some Android bugs.
 *
 * @author Michael Pujos
 * @author Christian Bauer
 */
public class AndroidNetworkAddressFactory extends NetworkAddressFactoryImpl {

    final private static Logger log = Logger.getLogger(AndroidUpnpServiceConfiguration.class.getName());

    private int[] allowedNetworkTypes = new int[]{};

    public AndroidNetworkAddressFactory(int streamListenPort, int[] allowedNetworkTypes) {
        super(streamListenPort);

        this.allowedNetworkTypes = allowedNetworkTypes;
        Arrays.sort(this.allowedNetworkTypes);
    }

    // Android does not offer the network interface information in its NetworkInfo class, nor does it offer the
    // IP Address in there. We are using the standard NetworkInterface classes from Java, and as such need to map.
    // https://code.google.com/p/android/issues/detail?id=14548
    private static final Map<String, Integer> networkInterfaceToTypeMap = new HashMap<>();

    static {
        networkInterfaceToTypeMap.put("wlan0", ConnectivityManager.TYPE_WIFI);
        networkInterfaceToTypeMap.put("eth0", ConnectivityManager.TYPE_ETHERNET);
        networkInterfaceToTypeMap.put("p2p0", AndroidUpnpServiceConfiguration.CONNECTIVITY_TYPE_WIFI_P2P);

        // Even better, the wlan-p2p interface has a different name for every connection, but hopefully the stays equal.
        networkInterfaceToTypeMap.put("p2p-wlan", AndroidUpnpServiceConfiguration.CONNECTIVITY_TYPE_WIFI_P2P);
    }

    @Override
    protected boolean requiresNetworkInterface() {
        return false;
    }

    @Override
    protected boolean isUsableAddress(NetworkInterface networkInterface, InetAddress address) {
        boolean result = isInterfaceAllowed(networkInterface);

        if (result) {
            result = super.isUsableAddress(networkInterface, address);
            if (result) {
                // TODO: Workaround Android DNS reverse lookup issue, still a problem on ICS+?
                // http://4thline.org/projects/mailinglists.html#nabble-td3011461
                String hostName = address.getHostAddress();
                try {
                    Field field = InetAddress.class.getDeclaredField("hostName");
                    field.setAccessible(true);
                    field.set(address, hostName);
                } catch (Exception ex) {
                    log.log(Level.SEVERE,
                            "Failed injecting hostName to work around Android InetAddress DNS bug: " + address,
                            ex
                    );
                    return false;
                }
            }
        }
        return result;
    }

    private boolean isInterfaceAllowed(final NetworkInterface networkInterface) {
        if (allowedNetworkTypes == null) {
            // TODO: This is due to the constructor logic.
            return false;
        }

        if (networkInterface.getName().startsWith("p2p-wlan")) {
            return Arrays.binarySearch(allowedNetworkTypes, AndroidUpnpServiceConfiguration.CONNECTIVITY_TYPE_WIFI_P2P) >= 0;
        }

        final Integer type = networkInterfaceToTypeMap.get(networkInterface.getName());

        if (type != null) {
            return Arrays.binarySearch(allowedNetworkTypes, type) >= 0;
        } else {
            log.warning("Can not map interface '" + networkInterface.getName() + "' to Android connectivity type.");
            return false;
        }
    }

    @Override
    public InetAddress getLocalAddress(NetworkInterface networkInterface, boolean isIPv6, InetAddress remoteAddress) {
        // TODO: This is totally random because we can't access low level InterfaceAddress on Android!
        for (InetAddress localAddress : getInetAddresses(networkInterface)) {
            if (isIPv6 && localAddress instanceof Inet6Address)
                return localAddress;
            if (!isIPv6 && localAddress instanceof Inet4Address)
                return localAddress;
        }
        throw new IllegalStateException("Can't find any IPv4 or IPv6 address on interface: " + networkInterface.getDisplayName());
    }
}