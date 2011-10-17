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

package org.fourthline.cling.model.meta;

import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.model.message.discovery.IncomingNotificationRequest;
import org.fourthline.cling.model.message.discovery.IncomingSearchResponse;
import org.fourthline.cling.model.types.UDN;

import java.net.InetAddress;
import java.net.URL;

/**
 * Additional identifying information only relevant for discovered remote devices.
 * <p>
 * This information always includes the URL of the device's descriptor, and the
 * local network interface address we should use in the future, because it is
 * guaranteed to be reachable by this remote device (e.g. when we build a local
 * callback URL).
 * </p>
 * <p>
 * Optional is the remote hosts interface MAC hardware address. If we have it, we
 * can use it to send Wake-On-LAN broadcasts if we think the remote host is not
 * reachable and might be sleeping. (Useful for "stateless" reconnecting control
 * points.)
 * </p>
 *
 * @author Christian Bauer
 */
public class RemoteDeviceIdentity extends DeviceIdentity {

    final private URL descriptorURL;
    final private byte[] interfaceMacAddress;
    final private InetAddress discoveredOnLocalAddress;

    public RemoteDeviceIdentity(UDN udn, RemoteDeviceIdentity template) {
        this(udn, template.getMaxAgeSeconds(), template.getDescriptorURL(), template.getInterfaceMacAddress(), template.getDiscoveredOnLocalAddress());
    }

    public RemoteDeviceIdentity(UDN udn, Integer maxAgeSeconds, URL descriptorURL, byte[] interfaceMacAddress, InetAddress discoveredOnLocalAddress) {
        super(udn, maxAgeSeconds);
        this.descriptorURL = descriptorURL;
        this.interfaceMacAddress = interfaceMacAddress;
        this.discoveredOnLocalAddress = discoveredOnLocalAddress;
    }

    public RemoteDeviceIdentity(IncomingNotificationRequest notificationRequest) {
        this(notificationRequest.getUDN(),
             notificationRequest.getMaxAge(),
             notificationRequest.getLocationURL(),
             notificationRequest.getInterfaceMacHeader(),
             notificationRequest.getLocalAddress()
        );
    }

    public RemoteDeviceIdentity(IncomingSearchResponse searchResponse) {
        this(searchResponse.getRootDeviceUDN(),
             searchResponse.getMaxAge(),
             searchResponse.getLocationURL(),
             searchResponse.getInterfaceMacHeader(),
             searchResponse.getLocalAddress()
        );
    }

    public URL getDescriptorURL() {
        return descriptorURL;
    }

    public byte[] getInterfaceMacAddress() {
        return interfaceMacAddress;
    }

    public InetAddress getDiscoveredOnLocalAddress() {
        return discoveredOnLocalAddress;
    }

    public byte[] getWakeOnLANBytes() {
        if (getInterfaceMacAddress() == null) return null;
        byte[] bytes = new byte[6 + 16 * getInterfaceMacAddress().length];
        for (int i = 0; i < 6; i++) {
            bytes[i] = (byte) 0xff;
        }
        for (int i = 6; i < bytes.length; i += getInterfaceMacAddress().length) {
            System.arraycopy(getInterfaceMacAddress(), 0, bytes, i, getInterfaceMacAddress().length);
        }
        return bytes;
    }

    @Override
    public String toString() {
        // Performance optimization, so we don't have to wrap all log("foo " + device) calls with isLoggable
		if(ModelUtil.ANDROID_RUNTIME) {
            return "(RemoteDeviceIdentity) UDN: " + getUdn() + ", Descriptor: " + getDescriptorURL();
        }
        return "(" + getClass().getSimpleName() + ") UDN: " + getUdn() + ", Descriptor: " + getDescriptorURL();
    }
}