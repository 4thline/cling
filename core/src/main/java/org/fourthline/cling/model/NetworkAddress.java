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
import java.util.Arrays;

/**
 * IP address, port, and optional interface hardware address (MAC) of a network service.
 *
 * @author Christian Bauer
 */
public class NetworkAddress {

    protected InetAddress address;
    protected int port;
    protected byte[] hardwareAddress;

    public NetworkAddress(InetAddress address, int port) {
        this(address, port, null);
    }

    public NetworkAddress(InetAddress address, int port, byte[] hardwareAddress) {
        this.address = address;
        this.port = port;
        this.hardwareAddress = hardwareAddress;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public byte[] getHardwareAddress() {
        return hardwareAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NetworkAddress that = (NetworkAddress) o;

        if (port != that.port) return false;
        if (!address.equals(that.address)) return false;
        if (!Arrays.equals(hardwareAddress, that.hardwareAddress)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = address.hashCode();
        result = 31 * result + port;
        result = 31 * result + (hardwareAddress != null ? Arrays.hashCode(hardwareAddress) : 0);
        return result;
    }
}
