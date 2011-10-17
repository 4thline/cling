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
import org.fourthline.cling.protocol.ProtocolCreationException;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.protocol.ReceivingAsync;
import org.fourthline.cling.transport.spi.DatagramIO;
import org.fourthline.cling.transport.spi.InitializationException;
import org.fourthline.cling.transport.spi.MulticastReceiver;
import org.fourthline.cling.transport.spi.NetworkAddressFactory;
import org.fourthline.cling.transport.spi.StreamClient;
import org.fourthline.cling.transport.spi.StreamServer;
import org.fourthline.cling.transport.spi.UpnpStream;
import org.seamless.util.Exceptions;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default implementation of network message router.
 * <p>
 * Initializes and starts listenting for data on the network immediately on construction.
 * </p>
 *
 * @author Christian Bauer
 */
public class RouterImpl implements Router {

    private static Logger log = Logger.getLogger(Router.class.getName());

    protected final UpnpServiceConfiguration configuration;
    protected final ProtocolFactory protocolFactory;

    protected final StreamClient streamClient;
    protected final NetworkAddressFactory networkAddressFactory;

    protected final Map<NetworkInterface, MulticastReceiver> multicastReceivers = new HashMap();
    protected final Map<InetAddress, DatagramIO> datagramIOs = new HashMap();
    protected final Map<InetAddress, StreamServer> streamServers = new HashMap();

    /**
     * Creates a {@link org.fourthline.cling.transport.spi.NetworkAddressFactory} from the given
     * {@link org.fourthline.cling.UpnpServiceConfiguration} and initializes listening services. First an instance
     * of {@link org.fourthline.cling.transport.spi.MulticastReceiver} is bound to eatch network interface. Then
     * an instance of {@link org.fourthline.cling.transport.spi.DatagramIO}
     * and {@link org.fourthline.cling.transport.spi.StreamServer} is bound to each bind address
     * returned by the network address factory, respectively. There is only one instance of
     * {@link org.fourthline.cling.transport.spi.StreamClient} created and managed by this router.
     *
     * @param configuration   The configuration used by this router.
     * @param protocolFactory The protocol factory used by this router.
     * @throws InitializationException When initialization of any listening network service fails.
     */
    public RouterImpl(UpnpServiceConfiguration configuration, ProtocolFactory protocolFactory)
            throws InitializationException {

        log.info("Creating Router: " + getClass().getName());

        this.configuration = configuration;
        this.protocolFactory = protocolFactory;

        log.fine("Starting networking services...");
        networkAddressFactory = getConfiguration().createNetworkAddressFactory();

        streamClient = getConfiguration().createStreamClient();

        for (NetworkInterface networkInterface : networkAddressFactory.getNetworkInterfaces()) {
            MulticastReceiver multicastReceiver = getConfiguration().createMulticastReceiver(networkAddressFactory);
            if (multicastReceiver != null) {
                multicastReceivers.put(networkInterface, multicastReceiver);
            }
        }

        for (InetAddress inetAddress : networkAddressFactory.getBindAddresses()) {

            DatagramIO datagramIO = getConfiguration().createDatagramIO(networkAddressFactory);
            if (datagramIO != null) {
                datagramIOs.put(inetAddress, datagramIO);
            }
            StreamServer streamServer = getConfiguration().createStreamServer(networkAddressFactory);
            if (streamServer != null) {
                streamServers.put(inetAddress, streamServer);
            }
        }

        // Start this first so we get a BindException if it's already started on this machine
        for (Map.Entry<InetAddress, StreamServer> entry : streamServers.entrySet()) {
            log.fine("Starting stream server on address: " + entry.getKey());
            entry.getValue().init(entry.getKey(), this);
            getConfiguration().getStreamServerExecutor().execute(entry.getValue());
        }

        for (Map.Entry<NetworkInterface, MulticastReceiver> entry : multicastReceivers.entrySet()) {
            log.fine("Starting multicast receiver on interface: " + entry.getKey().getDisplayName());
            entry.getValue().init(entry.getKey(), this, getConfiguration().getDatagramProcessor());
            getConfiguration().getMulticastReceiverExecutor().execute(entry.getValue());
        }

        for (Map.Entry<InetAddress, DatagramIO> entry : datagramIOs.entrySet()) {
            log.fine("Starting datagram I/O on address: " + entry.getKey());
            entry.getValue().init(entry.getKey(), this, getConfiguration().getDatagramProcessor());
            getConfiguration().getDatagramIOExecutor().execute(entry.getValue());
        }

    }

    public UpnpServiceConfiguration getConfiguration() {
        return configuration;
    }

    public ProtocolFactory getProtocolFactory() {
        return protocolFactory;
    }

    public NetworkAddressFactory getNetworkAddressFactory() {
        return networkAddressFactory;
    }

    protected Map<NetworkInterface, MulticastReceiver> getMulticastReceivers() {
        return multicastReceivers;
    }

    protected Map<InetAddress, DatagramIO> getDatagramIOs() {
        return datagramIOs;
    }

    protected StreamClient getStreamClient() {
        return streamClient;
    }

    protected Map<InetAddress, StreamServer> getStreamServers() {
        return streamServers;
    }

    synchronized public List<NetworkAddress> getActiveStreamServers(InetAddress preferredAddress) {
        if (getStreamServers().size() == 0) return Collections.EMPTY_LIST;
        List<NetworkAddress> streamServerAddresses = new ArrayList();

        StreamServer preferredServer;
        if (preferredAddress != null &&
                (preferredServer = getStreamServers().get(preferredAddress)) != null) {
            streamServerAddresses.add(
                    new NetworkAddress(
                            preferredAddress,
                            preferredServer.getPort(),
                            getNetworkAddressFactory().getHardwareAddress(preferredAddress)

                   )
            );
            return streamServerAddresses;
        }

        for (Map.Entry<InetAddress, StreamServer> entry : getStreamServers().entrySet()) {
            byte[] hardwareAddress = getNetworkAddressFactory().getHardwareAddress(entry.getKey());
            streamServerAddresses.add(
                    new NetworkAddress(entry.getKey(), entry.getValue().getPort(), hardwareAddress)
            );
        }
        return streamServerAddresses;
    }

    synchronized public void shutdown() {
        log.fine("Shutting down network services");

        if (streamClient != null) {
            log.fine("Stopping stream client connection management/pool");
            streamClient.stop();
        }

        for (Map.Entry<InetAddress, StreamServer> entry : streamServers.entrySet()) {
            log.fine("Stopping stream server on address: " + entry.getKey());
            entry.getValue().stop();
        }
        streamServers.clear();

        for (Map.Entry<NetworkInterface, MulticastReceiver> entry : multicastReceivers.entrySet()) {
            log.fine("Stopping multicast receiver on interface: " + entry.getKey().getDisplayName());
            entry.getValue().stop();
        }
        multicastReceivers.clear();

        for (Map.Entry<InetAddress, DatagramIO> entry : datagramIOs.entrySet()) {
            log.fine("Stopping datagram I/O on address: " + entry.getKey());
            entry.getValue().stop();
        }
        datagramIOs.clear();
    }

    /**
     * Obtains the asynchronous protocol {@code Executor} and runs the protocol created
     * by the {@link org.fourthline.cling.protocol.ProtocolFactory} for the given message.
     * <p>
     * If the factory doesn't create a protocol, the message is dropped immediately without
     * creating another thread or consuming further resoures. This means we can filter the
     * datagrams in the protocol factory and e.g. completely disable discovery or only
     * allow notification message from some known services we'd like to work with.
     * </p>
     *
     * @param msg The received datagram message.
     */
    public void received(IncomingDatagramMessage msg) {
        try {
            ReceivingAsync protocol = getProtocolFactory().createReceivingAsync(msg);
            if (protocol == null) {
                if (log.isLoggable(Level.FINEST))
                    log.finest("No protocol, ignoring received message: " + msg);
                return;
            }
            if (log.isLoggable(Level.FINE))
                log.fine("Received asynchronous message: " + msg);
            getConfiguration().getAsyncProtocolExecutor().execute(protocol);
        } catch (ProtocolCreationException ex) {
            log.warning("Handling received datagram failed - " + Exceptions.unwrap(ex).toString());
        }
    }

    /**
     * Obtains the synchronous protocol {@code Executor} and runs the
     * {@link org.fourthline.cling.transport.spi.UpnpStream} directly.
     *
     * @param stream The received {@link org.fourthline.cling.transport.spi.UpnpStream}.
     */
    public void received(UpnpStream stream) {
        log.fine("Received synchronous stream: " + stream);
        getConfiguration().getSyncProtocolExecutor().execute(stream);
    }

    /**
     * Sends the UDP datagram on all bound {@link org.fourthline.cling.transport.spi.DatagramIO}s.
     *
     * @param msg The UDP datagram message to send.
     */
    public void send(OutgoingDatagramMessage msg) {
        for (DatagramIO datagramIO : getDatagramIOs().values()) {
            datagramIO.send(msg);
        }
    }

    /**
     * Sends the TCP stream request with the {@link org.fourthline.cling.transport.spi.StreamClient}.
     *
     * @param msg The TCP (HTTP) stream message to send.
     * @return The return value of the {@link org.fourthline.cling.transport.spi.StreamClient#sendRequest(StreamRequestMessage)}
     *         method or <code>null</code> if no <code>StreamClient</code> is available.
     */
    public StreamResponseMessage send(StreamRequestMessage msg) {
        if (getStreamClient() == null) {
            log.fine("No StreamClient available, ignoring: " + msg);
            return null;
        }
        log.fine("Sending via TCP unicast stream: " + msg);
        return getStreamClient().sendRequest(msg);
    }

    /**
     * Sends the given bytes as a broadcast on all bound {@link org.fourthline.cling.transport.spi.DatagramIO}s,
     * using source port 9.
     * <p>
     * TODO: Support source port parameter
     * </p>
     *
     * @param bytes The byte payload of the UDP datagram.
     */
    public void broadcast(byte[] bytes) {
        for (Map.Entry<InetAddress, DatagramIO> entry : getDatagramIOs().entrySet()) {
            InetAddress broadcast = getNetworkAddressFactory().getBroadcastAddress(entry.getKey());
            if (broadcast != null) {
                log.fine("Sending UDP datagram to broadcast address: " + broadcast.getHostAddress());
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length, broadcast, 9);
                entry.getValue().send(packet);
            }
        }
    }
}
