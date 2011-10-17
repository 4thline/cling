/*
 * Copyright (C) 2011 4th Line GmbH, Switzerland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.fourthline.cling.model;

import org.seamless.util.URIUtil;

import java.net.URI;
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

    protected NetworkAddress networkAddress;
    protected URI path;

    public Location(NetworkAddress networkAddress, URI path) {
        this.networkAddress = networkAddress;
        this.path = path;
    }

    public NetworkAddress getNetworkAddress() {
        return networkAddress;
    }

    public URI getPath() {
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
        return URIUtil.createAbsoluteURL(networkAddress.getAddress(), networkAddress.getPort(), path);
    }

}
