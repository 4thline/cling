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

package org.fourthline.cling.transport.impl;

import org.fourthline.cling.model.Constants;
import org.fourthline.cling.transport.spi.InitializationException;
import org.fourthline.cling.transport.spi.NetworkAddressFactory;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default implementation of network interface and address configuration/discovery.
 * <p
 * This implementation has been tested on Windows XP, Windows Vista, Mac OS X 10.6,
 * and whatever kernel ships in Ubuntu 9.04. This implementation does not support IPv6.
 * </p>
 *
 * @author Christian Bauer
 */
public class NetworkAddressFactoryImpl implements NetworkAddressFactory {

    // Ephemeral port is the default
    public static final int DEFAULT_TCP_HTTP_LISTEN_PORT = 0;

    private static Logger log = Logger.getLogger(NetworkAddressFactoryImpl.class.getName());

    protected Set<String> useInterfaces = new HashSet();
    protected Set<String> useAddresses = new HashSet();

    protected List<NetworkInterface> networkInterfaces = new ArrayList();
    protected List<InetAddress> bindAddresses = new ArrayList();

    protected int streamListenPort;

    /**
     * Defaults to an ephemeral port.
     */
    public NetworkAddressFactoryImpl() throws InitializationException {
        this(DEFAULT_TCP_HTTP_LISTEN_PORT);
    }

    public NetworkAddressFactoryImpl(int streamListenPort) throws InitializationException {

        String useInterfacesString = System.getProperty(SYSTEM_PROPERTY_NET_IFACES);
        if (useInterfacesString != null) {
            String[] userInterfacesStrings = useInterfacesString.split(",");
            useInterfaces.addAll(Arrays.asList(userInterfacesStrings));
        }

        String useAddressesString = System.getProperty(SYSTEM_PROPERTY_NET_ADDRESSES);
        if (useAddressesString != null) {
            String[] useAddressesStrings = useAddressesString.split(",");
            useAddresses.addAll(Arrays.asList(useAddressesStrings));
        }

        // TODO: Linux issue: http://mail.openjdk.java.net/pipermail/net-dev/2008-December/000497.html
        // TODO: Is this no longer needed?
        /*
        if (OS.checkForLinux()) {
            Properties props = System.getProperties();
            props.setProperty("java.net.preferIPv6Stack", "true");
            System.setProperties(props);
        }
        */

        discoverNetworkInterfaces();
        discoverBindAddresses();

        if (networkInterfaces.size() == 0 || bindAddresses.size() == 0) {
            throw new InitializationException("Could not discover any bindable network interfaces and/or addresses");
        }

        this.streamListenPort = streamListenPort;
    }

    public void logInterfaceInformation() {
    	for(NetworkInterface networkInterface : networkInterfaces) {
        	try {
				logInterfaceInformation(networkInterface);
			} catch (SocketException ex) {
                log.log(Level.WARNING, "Exception while logging network interface information", ex);
			}
        }
    }

    public InetAddress getMulticastGroup() {
        try {
            return InetAddress.getByName(Constants.IPV4_UPNP_MULTICAST_GROUP);
        } catch (UnknownHostException ex) {
            throw new RuntimeException(ex);
        }
    }

    public int getMulticastPort() {
        return Constants.UPNP_MULTICAST_PORT;
    }

    public int getStreamListenPort() {
        return streamListenPort;
    }

    public NetworkInterface[] getNetworkInterfaces() {
        return networkInterfaces.toArray(new NetworkInterface[networkInterfaces.size()]);
    }

    public InetAddress[] getBindAddresses() {
        return bindAddresses.toArray(new InetAddress[bindAddresses.size()]);
    }

    public byte[] getHardwareAddress(InetAddress inetAddress) {
        try {
            NetworkInterface iface = NetworkInterface.getByInetAddress(inetAddress);
            return iface != null ? iface.getHardwareAddress() : null;
        } catch (Throwable ex) {
        	// seen on Win32: java.lang.Error: IP Helper Library GetIpAddrTable function failed
        	return null;
        }
    }

    public InetAddress getBroadcastAddress(InetAddress inetAddress) {
        for (NetworkInterface iface : networkInterfaces) {
            for (InterfaceAddress interfaceAddress : getInterfaceAddresses(iface)) {
                if (interfaceAddress != null && interfaceAddress.getAddress().equals(inetAddress)) {
                    return interfaceAddress.getBroadcast();
                }
            }
        }
        return null;
    }

    public Short getAddressNetworkPrefixLength(InetAddress inetAddress) {
        for (NetworkInterface iface : networkInterfaces) {
            for (InterfaceAddress interfaceAddress : getInterfaceAddresses(iface)) {
                if (interfaceAddress != null && interfaceAddress.getAddress().equals(inetAddress)) {
                    short prefix = interfaceAddress.getNetworkPrefixLength();
                    if(prefix > 0 && prefix < 32) return prefix; // some network cards return -1
                    return null;
                }
            }
        }
        return null;
    }

    public InetAddress getLocalAddress(NetworkInterface networkInterface, boolean isIPv6, InetAddress remoteAddress) {

        // First try to find a local IP that is in the same subnet as the remote IP
        InetAddress localIPInSubnet = getBindAddressInSubnetOf(remoteAddress);
        if (localIPInSubnet != null) return localIPInSubnet;

        // There are two reasons why we end up here:
        //
        // - Windows Vista returns a 64 or 128 CIDR prefix if you ask it for the network prefix length of an IPv4 address!
        //
        // - We are dealing with genuine IPv6 addresses
        //
        // - Something is really wrong on the LAN and we received a multicast datagram from a source we can't reach via IP
        log.finer("Could not find local bind address in same subnet as: " + remoteAddress.getHostAddress());

        // Next, just take the given interface (which is really totally random) and get the first address that we like
        for (InetAddress interfaceAddress: getInetAddresses(networkInterface)) {
            if (isIPv6 && interfaceAddress instanceof Inet6Address)
                return interfaceAddress;
            if (!isIPv6 && interfaceAddress instanceof Inet4Address)
                return interfaceAddress;
        }
        throw new IllegalStateException("Can't find any IPv4 or IPv6 address on interface: " + networkInterface.getDisplayName());
    }

    protected List<InterfaceAddress> getInterfaceAddresses(NetworkInterface networkInterface) {
        return networkInterface.getInterfaceAddresses();
    }

    protected List<InetAddress> getInetAddresses(NetworkInterface networkInterface) {
        return Collections.list(networkInterface.getInetAddresses());
    }

    protected InetAddress getBindAddressInSubnetOf(InetAddress inetAddress) {

        for (NetworkInterface iface : networkInterfaces) {
            for (InterfaceAddress ifaceAddress : getInterfaceAddresses(iface)) {

                if (ifaceAddress == null || !bindAddresses.contains(ifaceAddress.getAddress())) {
                    continue;
                }

                if (isInSubnet(
                        inetAddress.getAddress(),
                        ifaceAddress.getAddress().getAddress(),
                        ifaceAddress.getNetworkPrefixLength())
                        ) {
                    return ifaceAddress.getAddress();
                }
            }

        }

        return null;
    }

    protected boolean isInSubnet(byte[] ip, byte[] network, short prefix) {
        if (ip.length != network.length) {
            return false;
        }

        if (prefix / 8 > ip.length) {
            return false;
        }

        int i = 0;
        while (prefix >= 8 && i < ip.length) {
            if (ip[i] != network[i]) {
                return false;
            }
            i++;
            prefix -= 8;
        }
        if(i == ip.length) return true;
        final byte mask = (byte) ~((1 << 8 - prefix) - 1);

        return (ip[i] & mask) == (network[i] & mask);
    }

    protected void discoverNetworkInterfaces() throws InitializationException {
        try {

            Enumeration<NetworkInterface> interfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface iface : Collections.list(interfaceEnumeration)) {
                //displayInterfaceInformation(iface);

                log.finer("Analyzing network interface: " + iface.getDisplayName());
                if (isUsableNetworkInterface(iface)) {
                    log.fine("Discovered usable network interface: " + iface.getDisplayName());
                    networkInterfaces.add(iface);
                } else {
                    log.finer("Ignoring non-usable network interface: " + iface.getDisplayName());
                }
            }

        } catch (Exception ex) {
            throw new InitializationException("Could not not analyze local network interfaces: " + ex, ex);
        }
    }

    /**
     * Validation of every discovered network interface.
     * <p>
     * Override this method to customize which network interfaces are used.
     * </p>
     * <p>
     * The given implementation ignores interfaces which are
     * </p>
     * <ul>
     * <li>loopback (yes, we do not bind to lo0)</li>
     * <li>down</li>
     * <li>have no bound IP addresses</li>
     * <li>named "vmnet*" (OS X VMWare does not properly stop interfaces when it quits)</li>
     * <li>named "vnic*" (OS X Parallels interfaces should be ignored as well)</li>
     * <li>named "*virtual*"</li>
     * <li>named "ppp*"</li>
     * <li>not supporting multicast</li>
     * </ul>
     *
     * @param iface The interface to validate.
     * @return True if the given interface matches all validation criteria.
     * @throws Exception If any validation test failed with an un-recoverable error.
     */
    protected boolean isUsableNetworkInterface(NetworkInterface iface) throws Exception {
        if (!iface.isUp()) {
            log.finer("Skipping network interface (down): " + iface.getDisplayName());
            return false;
        }

        if (getInetAddresses(iface).size() == 0) {
            log.finer("Skipping network interface without bound IP addresses: " + iface.getDisplayName());
            return false;
        }

        if (iface.getName().toLowerCase(Locale.ENGLISH).startsWith("vmnet") ||
        		(iface.getDisplayName() != null &&  iface.getDisplayName().toLowerCase(Locale.ENGLISH).contains("vmnet"))) {
            log.finer("Skipping network interface (VMWare): " + iface.getDisplayName());
            return false;
        }

        if (iface.getName().toLowerCase(Locale.ENGLISH).startsWith("vnic")) {
            log.finer("Skipping network interface (Parallels): " + iface.getDisplayName());
            return false;
        }

        if (iface.getName().toLowerCase(Locale.ENGLISH).startsWith("ppp")) {
            log.finer("Skipping network interface (PPP): " + iface.getDisplayName());
            return false;
        }

        if (!iface.supportsMulticast()) {
            log.finer("Skipping network interface (no multicast support): " + iface.getDisplayName());
            return false;
        }

        if (iface.isLoopback()) {
            log.finer("Skipping network interface (ignoring loopback): " + iface.getDisplayName());
            return false;
        }

        if (useInterfaces.size() > 0 && !useInterfaces.contains(iface.getName())) {
            log.finer("Skipping unwanted network interface (-D" + SYSTEM_PROPERTY_NET_IFACES + "): " + iface.getName());
            return false;
        }

        return true;
    }

    protected void discoverBindAddresses() throws InitializationException {
        try {

            Iterator<NetworkInterface> it = networkInterfaces.iterator();
            while (it.hasNext()) {
                NetworkInterface networkInterface = it.next();

                log.finer("Discovering addresses of interface: " + networkInterface.getDisplayName());
                int usableAddresses = 0;
                for (InetAddress inetAddress : getInetAddresses(networkInterface)) {
                    if (inetAddress == null) {
                        log.warning("Network has a null address: " + networkInterface.getDisplayName());
                        continue;
                    }

                    if (isUsableAddress(networkInterface, inetAddress)) {
                        log.fine("Discovered usable network interface address: " + inetAddress.getHostAddress());
                        usableAddresses++;
                        bindAddresses.add(inetAddress);
                    } else {
                        log.finer("Ignoring non-usable network interface address: " + inetAddress.getHostAddress());
                    }
                }

                if (usableAddresses == 0) {
                    log.finer("Network interface has no usable addresses, removing: " + networkInterface.getDisplayName());
                    it.remove();
                }
            }

        } catch (Exception ex) {
            throw new InitializationException("Could not not analyze local network interfaces: " + ex, ex);
        }
    }

    /**
     * Validation of every discovered local address.
     * <p>
     * Override this method to customize which network addresses are used.
     * </p>
     * <p>
     * The given implementation ignores addresses which are
     * </p>
     * <ul>
     * <li>not IPv4</li>
     * <li>the local loopback (yes, we ignore 127.0.0.1)</li>
     * </ul>
     *
     * @param networkInterface The interface to validate.
     * @param address The address of this interface to validate.
     * @return True if the given address matches all validation criteria.
     */
    protected boolean isUsableAddress(NetworkInterface networkInterface, InetAddress address) {
        if (!(address instanceof Inet4Address)) {
            log.finer("Skipping unsupported non-IPv4 address: " + address);
            return false;
        }

        if (address.isLoopbackAddress()) {
            log.finer("Skipping loopback address: " + address);
            return false;
        }

        if (address.isLinkLocalAddress()) {
        	log.finer("Skipping link-local address: " + address);
        	return false;
        }

        if (useAddresses.size() > 0 && !useAddresses.contains(address.getHostAddress())) {
            log.finer("Skipping unwanted address: " + address);
            return false;
        }

        return true;
    }

    protected void logInterfaceInformation(NetworkInterface networkInterface) throws SocketException {
        log.info("---------------------------------------------------------------------------------");
        log.info(String.format("Interface display name: %s", networkInterface.getDisplayName()));
        log.info(String.format("Parent Info: %s", networkInterface.getParent()));
        log.info(String.format("Name: %s", networkInterface.getName()));

        Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();

        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
            log.info(String.format("InetAddress: %s", inetAddress));
        }

        List<InterfaceAddress> interfaceAddresses = networkInterface.getInterfaceAddresses();

        for (InterfaceAddress interfaceAddress : interfaceAddresses) {
            if (interfaceAddress == null) {
                log.warning("Skipping null InterfaceAddress!");
                continue;
            }
            log.info(" Interface Address");
            log.info("  Address: " + interfaceAddress.getAddress());
            log.info("  Broadcast: " + interfaceAddress.getBroadcast());
            log.info("  Prefix length: " + interfaceAddress.getNetworkPrefixLength());
        }

        Enumeration<NetworkInterface> subIfs = networkInterface.getSubInterfaces();

        for (NetworkInterface subIf : Collections.list(subIfs)) {
            if (subIf == null) {
                log.warning("Skipping null NetworkInterface sub-interface");
                continue;
            }
            log.info(String.format("\tSub Interface Display name: %s", subIf.getDisplayName()));
            log.info(String.format("\tSub Interface Name: %s", subIf.getName()));
        }
        log.info(String.format("Up? %s", networkInterface.isUp()));
        log.info(String.format("Loopback? %s", networkInterface.isLoopback()));
        log.info(String.format("PointToPoint? %s", networkInterface.isPointToPoint()));
        log.info(String.format("Supports multicast? %s", networkInterface.supportsMulticast()));
        log.info(String.format("Virtual? %s", networkInterface.isVirtual()));
        log.info(String.format("Hardware address: %s", Arrays.toString(networkInterface.getHardwareAddress())));
        log.info(String.format("MTU: %s", networkInterface.getMTU()));
    }
}
