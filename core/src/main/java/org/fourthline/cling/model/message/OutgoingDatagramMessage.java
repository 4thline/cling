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
 * A UDP datagram request or response message for sending, with destination address and port.
 *
 * @author Christian Bauer
 */
public abstract class OutgoingDatagramMessage<O extends UpnpOperation> extends UpnpMessage<O> {

    private InetAddress destinationAddress;
    private int destinationPort;
    // For performance reasons, headers of this message are not normalized
    private UpnpHeaders headers = new UpnpHeaders(false);

    protected OutgoingDatagramMessage(O operation, InetAddress destinationAddress, int destinationPort) {
        super(operation);
        this.destinationAddress = destinationAddress;
        this.destinationPort = destinationPort;
    }

    protected OutgoingDatagramMessage(O operation, BodyType bodyType, Object body, InetAddress destinationAddress, int destinationPort) {
        super(operation, bodyType, body);
        this.destinationAddress = destinationAddress;
        this.destinationPort = destinationPort;
    }

    public InetAddress getDestinationAddress() {
        return destinationAddress;
    }

    public int getDestinationPort() {
        return destinationPort;
    }
    
    @Override
    public UpnpHeaders getHeaders() {
        return this.headers;
    }
}