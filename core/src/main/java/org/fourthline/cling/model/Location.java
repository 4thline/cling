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

package org.fourthline.cling.model;

import java.net.InetAddress;
import java.net.URL;

/**
 * The IP address/port, MAC address, and URI path of a (network) location.
 * <p>
 * Used when sending messages about local devices and services to
 * other UPnP participants on the network, such as where our device/service
 * descriptors can be found or what callback address to use for event message
 * delivery. We also let them know our MAC hardware address so they
 * can wake us up from sleep with Wake-On-LAN if necessary.
 * </p>
 *
 * @author Christian Bauer
 */
public class Location {

    protected final NetworkAddress networkAddress;
    protected final String path;
    protected final URL url;

    public Location(NetworkAddress networkAddress, String path) {
        this.networkAddress = networkAddress;
        this.path = path;
        this.url = createAbsoluteURL(networkAddress.getAddress(), networkAddress.getPort(), path);
    }

    public NetworkAddress getNetworkAddress() {
        return networkAddress;
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (!networkAddress.equals(location.networkAddress)) return false;
        if (!path.equals(location.path)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = networkAddress.hashCode();
        result = 31 * result + path.hashCode();
        return result;
    }

    /**
     * @return An HTTP URL with the address, port, and path of this location.
     */
    public URL getURL() {
        return url;
    }

    // Performance optimization on Android
    private static URL createAbsoluteURL(InetAddress address, int localStreamPort, String path) {
        try {
            return new URL("http", address.getHostAddress(), localStreamPort, path);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Address, port, and URI can not be converted to URL", ex);
        }
    }
}
