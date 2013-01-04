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

package org.fourthline.cling.transport.impl;

import org.fourthline.cling.transport.spi.MulticastReceiverConfiguration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Settings for the default implementation.
 * 
 * @author Christian Bauer
 */
public class MulticastReceiverConfigurationImpl implements MulticastReceiverConfiguration {

    private InetAddress group;
    private int port;
    private int maxDatagramBytes;

    public MulticastReceiverConfigurationImpl(InetAddress group, int port, int maxDatagramBytes) {
        this.group = group;
        this.port = port;
        this.maxDatagramBytes = maxDatagramBytes;
    }

    /**
     * Defaults to maximum datagram size of 640 bytes (512 per UDA 1.0, 128 byte header).
     */
    public MulticastReceiverConfigurationImpl(InetAddress group, int port) {
        this(group, port, 640);
    }

    public MulticastReceiverConfigurationImpl(String group, int port, int maxDatagramBytes) throws UnknownHostException {
        this(InetAddress.getByName(group), port, maxDatagramBytes);
    }

    /**
     * Defaults to maximum datagram size of 640 bytes (512 per UDA 1.0, 128 byte header).
     */
    public MulticastReceiverConfigurationImpl(String group, int port) throws UnknownHostException {
        this(InetAddress.getByName(group), port, 640);
    }

    public InetAddress getGroup() {
        return group;
    }

    public void setGroup(InetAddress group) {
        this.group = group;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMaxDatagramBytes() {
        return maxDatagramBytes;
    }

    public void setMaxDatagramBytes(int maxDatagramBytes) {
        this.maxDatagramBytes = maxDatagramBytes;
    }

}
