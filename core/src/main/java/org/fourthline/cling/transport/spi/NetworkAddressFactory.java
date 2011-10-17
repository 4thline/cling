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

package org.fourthline.cling.transport.spi;

import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * Configuration utility for network interfaces and addresses.
 * <p>
 * An implementation has to be thread-safe.
 * </p>
 *
 * @author Christian Bauer
 */
public interface NetworkAddressFactory {

    // An implementation can honor these if it wants (the default does)
    public static final String SYSTEM_PROPERTY_NET_IFACES = "org.fourthline.cling.network.useInterfaces";
    public static final String SYSTEM_PROPERTY_NET_ADDRESSES = "org.fourthline.cling.network.useAddresses";

    /**
     * @return The UDP multicast group to join.
     */
    public InetAddress getMulticastGroup();

    /**
     * @return The UDP multicast port to listen on.
     */
    public int getMulticastPort();

    /**
     * @return The TCP (HTTP) stream request port to listen on.
     */
    public int getStreamListenPort();

    /**
     * @return The local network interfaces on which multicast groups will be joined.
     */
    public NetworkInterface[] getNetworkInterfaces();

    /**
     * @return The local addresses of the network interfaces bound to
     *         sockets listening for unicast datagrams and TCP requests.
     */
    public InetAddress[] getBindAddresses();

    /**
     * @param inetAddress An address of a local network interface.
     * @return The MAC hardware address of the network interface or <code>null</code> if no
     *         hardware address could be obtained.
     */
    public byte[] getHardwareAddress(InetAddress inetAddress);

    /**
     * @param inetAddress An address of a local network interface.
     * @return The broadcast address of the network (interface) or <code>null</code> if no
     *         broadcast address could be obtained.
     */
    public InetAddress getBroadcastAddress(InetAddress inetAddress);

    /**
     * Best-effort attempt finding a reachable local address for a given remote host.
     * <p>
     * This method is called whenever a multicast datagram has been received. We need to be
     * able to communicate with the sender using UDP unicast and we need to tell the sender
     * how we are reachable with TCP requests. We need a local address that is in the same
     * subnet as the senders address, that is reachable from the senders point of view.
     * </p>
     *
     * @param networkInterface The network interface to examine.
     * @param isIPv6 True if the given remote address is an IPv6 address.
     * @param remoteAddress The remote address for which to find a local address in the same subnet.
     * @return A local address that is reachable from the given remote address.
     * @throws IllegalStateException If no local address reachable by the remote address has been found.
     */
    public InetAddress getLocalAddress(NetworkInterface networkInterface,
                                       boolean isIPv6,
                                       InetAddress remoteAddress) throws IllegalStateException;
}
