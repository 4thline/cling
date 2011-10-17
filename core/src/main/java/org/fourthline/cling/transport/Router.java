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

package org.fourthline.cling.transport;

import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.model.NetworkAddress;
import org.fourthline.cling.model.message.IncomingDatagramMessage;
import org.fourthline.cling.model.message.OutgoingDatagramMessage;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.transport.spi.NetworkAddressFactory;
import org.fourthline.cling.transport.spi.UpnpStream;

import java.net.InetAddress;
import java.util.List;

/**
 * Interface of the network transport layer.
 * <p>
 * Encapsulates the transport layer and provides methods to the upper layers for
 * sending UPnP stream (HTTP) {@link org.fourthline.cling.model.message.StreamRequestMessage}s,
 * sending (UDP) datagram {@link org.fourthline.cling.model.message.OutgoingDatagramMessage}s,
 * as well as broadcasting bytes to all LAN participants.
 * </p>
 * <p>
 * A router also maintains listening sockets and services, for incoming UDP unicast/multicast
 * {@link org.fourthline.cling.model.message.IncomingDatagramMessage} and TCP
 * {@link org.fourthline.cling.transport.spi.UpnpStream}s. An implementation of this interface
 * handles these messages, e.g. by selecting and executing the right protocol.
 * </p>
 *
 * @see org.fourthline.cling.protocol.ProtocolFactory
 *
 * @author Christian Bauer
 */
public interface Router {

    /**
     * @return The configuration used by this router.
     */
    public UpnpServiceConfiguration getConfiguration();

    /**
     * @return The protocol factory used by this router.
     */
    public ProtocolFactory getProtocolFactory();

    /**
     * @return The network interface and address binding configuration of this router.
     */
    public NetworkAddressFactory getNetworkAddressFactory();

    /**
     * @param preferredAddress A preferred stream server bound address or <code>null</code>.
     * @return An empty list if no stream server is currently active, otherwise a single network
     *         address if the preferred address is active, or a list of all active bound
     *         stream servers.
     */
    public List<NetworkAddress> getActiveStreamServers(InetAddress preferredAddress);

    /**
     * Unbinds all sockets and stops all listening threads for datagrams and streams.
     */
    public void shutdown();

    /**
     * <p>
     * This method is called internally by the transport layer when a datagram, either unicast or
     * multicast, has been received. An implementation of this interface has to handle the received
     * message, e.g. selecting and executing a UPnP protocol. This method should not block until
     * the execution completes, the calling thread should be free to handle the next reception as
     * soon as possible.
     * </p>
     * @param msg The received datagram message.
     */
    public void received(IncomingDatagramMessage msg);

    /**
     * <p>
     * This method is called internally by the transport layer when a TCP stream connection has
     * been made and a response has to be returned to the sender. An implementation of this interface
     * has to handle the received stream connection and return a response, e.g. selecting and executing
     * a UPnP protocol. This method should not block until the execution completes, the calling thread
     * should be free to process the next reception as soon as possible. Typically this means starting
     * a new thread of execution in this method.
     * </p>
     * @param stream
     */
    public void received(UpnpStream stream);

    /**
     * <p>
     * Call this method to send a UDP datagram message.
     * </p>
     * @param msg The UDP datagram message to send.
     */
    public void send(OutgoingDatagramMessage msg);

    /**
     * <p>
     * Call this method to send a TCP (HTTP) stream message.
     * </p>
     * @param msg The TCP (HTTP) stream message to send.
     * @return The response received from the server.
     */
    public StreamResponseMessage send(StreamRequestMessage msg);

    /**
     * <p>
     * Call this method to broadcast a UDP message to all hosts on the network.
     * </p>
     * @param bytes The byte payload of the UDP datagram.
     */
    public void broadcast(byte[] bytes);

}
