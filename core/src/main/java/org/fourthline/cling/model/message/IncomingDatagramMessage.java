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

package org.fourthline.cling.model.message;

import java.net.InetAddress;

/**
 * A received UDP datagram request or response message, with source address and port.
 * <p>
 * Additionally, holds a local address that is reachable from the source
 * address (in the same subnet):
 * </p>
 * <ul>
 * <li>When an M-SEARCH is received, we send a LOCATION header back with a
 *     reachable (by the remote control point) local address.</li>
 * <li>When a NOTIFY discovery message (can be a search response) is received we
       need to memorize on which local address it was received, so that the we can
       later give the remote device a reachable (from its point of view) local
       GENA callback address.</li>
 * </ul>
 *
 * @author Christian Bauer
 */
public class IncomingDatagramMessage<O extends UpnpOperation> extends UpnpMessage<O> {

    private InetAddress sourceAddress;
    private int sourcePort;
    private InetAddress localAddress;

    public IncomingDatagramMessage(O operation, InetAddress sourceAddress, int sourcePort, InetAddress localAddress) {
        super(operation);
        this.sourceAddress = sourceAddress;
        this.sourcePort = sourcePort;
        this.localAddress = localAddress;
    }

    protected IncomingDatagramMessage(IncomingDatagramMessage<O> source) {
        super(source);
        this.sourceAddress = source.getSourceAddress();
        this.sourcePort = source.getSourcePort();
        this.localAddress = source.getLocalAddress();
    }

    public InetAddress getSourceAddress() {
        return sourceAddress;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public InetAddress getLocalAddress() {
        return localAddress;
    }

}
