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
}