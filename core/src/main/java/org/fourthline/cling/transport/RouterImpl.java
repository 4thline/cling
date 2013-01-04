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
import org.fourthline.cling.transport.spi.NoNetworkException;
import org.fourthline.cling.transport.spi.StreamClient;
import org.fourthline.cling.transport.spi.StreamServer;
import org.fourthline.cling.transport.spi.UpnpStream;
import org.seamless.util.Exceptions;

import java.net.BindException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default implementation of network message router.
 * <p>
 * Initializes and starts listening for data on the network immediately on construction.
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

        startInterfaceBasedTransports(networkAddressFactory.getNetworkInterfaces());
        startAddressBasedTransports(networkAddressFactory.getBindAddresses());

        // The transports possibly removed some unusable network interfaces/addresses
        if (!networkAddressFactory.hasUsableNetwork()) {
            throw new NoNetworkException(
                "No usable network interface and/or addresses available, check the log for errors."
            );
        }

        // Start the HTTP client last, we don't even have to try if there is no network
        streamClient = getConfiguration().createStreamClient();
    }

    protected void startInterfaceBasedTransports(Iterator<NetworkInterface> interfaces) throws InitializationException {
        while (interfaces.hasNext()) {
            NetworkInterface networkInterface = interfaces.next();

            // We only have the MulticastReceiver as an interface-based transport
            MulticastReceiver multicastReceiver = getConfiguration().createMulticastReceiver(networkAddressFactory);
            if (multicastReceiver == null) {
                log.info("Configuration did not create a MulticastReceiver for: " + networkInterface);
            } else {
                try {
                    if (log.isLoggable(Level.FINE))
                        log.fine("Init multicast receiver on interface: " + networkInterface.getDisplayName());
                    multicastReceiver.init(networkInterface, this, getConfiguration().getDatagramProcessor());
                    multicastReceivers.put(networkInterface, multicastReceiver);
                } catch (InitializationException ex) {
                    /* TODO: What are some recoverable exceptions for this?
                    log.warning(
                        "Ignoring network interface '"
                            + networkInterface.getDisplayName()
                            + "' init failure of MulticastReceiver: " + ex.toString());
                    if (log.isLoggable(Level.FINE))
                        log.log(Level.FINE, "Initialization exception root cause", Exceptions.unwrap(ex));
                    log.warning("Removing unusable interface " + interface);
                    it.remove();
                    continue; // Don't need to try anything else on this interface
                    */
                    throw ex;
                }
            }
        }

        for (Map.Entry<NetworkInterface, MulticastReceiver> entry : multicastReceivers.entrySet()) {
            if (log.isLoggable(Level.FINE))
                log.fine("Starting multicast receiver on interface: " + entry.getKey().getDisplayName());
            getConfiguration().getMulticastReceiverExecutor().execute(entry.getValue());
        }
    }

    protected void startAddressBasedTransports(Iterator<InetAddress> addresses) throws InitializationException {
        while (addresses.hasNext()) {
            InetAddress address = addresses.next();

            // HTTP servers
            StreamServer streamServer = getConfiguration().createStreamServer(networkAddressFactory);
            if (streamServer == null) {
                log.info("Configuration did not create a StreamServer for: " + address);
            } else {
                try {
                    if (log.isLoggable(Level.FINE))
                        log.fine("Init stream server on address: " + address);
                    streamServer.init(address, this);
                    streamServers.put(address, streamServer);
                } catch (InitializationException ex) {
                    // Try to recover
                    Throwable cause = Exceptions.unwrap(ex);
                    if (cause instanceof BindException) {
                        log.warning("Failed to init StreamServer: " + cause);
                        if (log.isLoggable(Level.FINE))
                            log.log(Level.FINE, "Initialization exception root cause", cause);
                        log.warning("Removing unusable address: " + address);
                        addresses.remove();
                        continue; // Don't try anything else with this address
                    }
                    throw ex;
                }
            }

            // Datagram I/O
            DatagramIO datagramIO = getConfiguration().createDatagramIO(networkAddressFactory);
            if (datagramIO == null) {
                log.info("Configuration did not create a StreamServer for: " + address);
            } else {
                try {
                    if (log.isLoggable(Level.FINE))
                        log.fine("Init datagram I/O on address: " + address);
                    datagramIO.init(address, this, getConfiguration().getDatagramProcessor());
                    datagramIOs.put(address, datagramIO);
                } catch (InitializationException ex) {
                    /* TODO: What are some recoverable exceptions for this?
                    Throwable cause = Exceptions.unwrap(ex);
                    if (cause instanceof BindException) {
                        log.warning("Failed to init datagram I/O: " + cause);
                        if (log.isLoggable(Level.FINE))
                            log.log(Level.FINE, "Initialization exception root cause", cause);
                        log.warning("Removing unusable address: " + address);
                        addresses.remove();
                        continue; // Don't try anything else with this address
                    }
                    */
                    throw ex;
                }
            }
        }

        for (Map.Entry<InetAddress, StreamServer> entry : streamServers.entrySet()) {
            if (log.isLoggable(Level.FINE))
                log.fine("Starting stream server on address: " + entry.getKey());
            getConfiguration().getStreamServerExecutor().execute(entry.getValue());
        }

        for (Map.Entry<InetAddress, DatagramIO> entry : datagramIOs.entrySet()) {
            if (log.isLoggable(Level.FINE))
                log.fine("Starting datagram I/O on address: " + entry.getKey());
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
