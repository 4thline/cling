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

package org.fourthline.cling.transport.spi;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Iterator;

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
     * The caller might <code>remove()</code> an interface if initialization fails.
     *
     * @return The local network interfaces on which multicast groups will be joined.
     */
    public Iterator<NetworkInterface> getNetworkInterfaces();

    /**
     * The caller might <code>remove()</code> an address if initialization fails.
     *
     * @return The local addresses of the network interfaces bound to
     *         sockets listening for unicast datagrams and TCP requests.
     */
    public Iterator<InetAddress> getBindAddresses();

    /**
     * @return <code>true</code> if there is at least one usable network interface and bind address.
     */
    public boolean hasUsableNetwork();

    /**
     * @return The network prefix length of this address or <code>null</code>.
     */
    public Short getAddressNetworkPrefixLength(InetAddress inetAddress);

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

    /**
     * For debugging, logs all "usable" network interface(s) details with INFO level.
     */
    public void logInterfaceInformation();
}
