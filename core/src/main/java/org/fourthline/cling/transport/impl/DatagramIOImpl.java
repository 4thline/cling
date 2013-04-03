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

import org.fourthline.cling.model.message.OutgoingDatagramMessage;
import org.fourthline.cling.transport.Router;
import org.fourthline.cling.transport.spi.DatagramIO;
import org.fourthline.cling.transport.spi.DatagramProcessor;
import org.fourthline.cling.transport.spi.InitializationException;
import org.fourthline.cling.model.UnsupportedDataException;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default implementation based on a single shared (receive/send) UDP <code>MulticastSocket</code>.
 * <p>
 * Although we do not receive multicast datagrams with this service, sending multicast
 * datagrams with a configuration time-to-live requires a <code>MulticastSocket</code>.
 * </p>
 * <p>
 * Thread-safety is guaranteed through synchronization of methods of this service and
 * by the thread-safe underlying socket.
 * </p>
 * @author Christian Bauer
 */
public class DatagramIOImpl implements DatagramIO<DatagramIOConfigurationImpl> {

    private static Logger log = Logger.getLogger(DatagramIO.class.getName());

    /* Implementation notes for unicast/multicast UDP:

    http://forums.sun.com/thread.jspa?threadID=771852
    http://mail.openjdk.java.net/pipermail/net-dev/2008-December/000497.html
    https://jira.jboss.org/jira/browse/JGRP-978
    http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4701650

     */

    final protected DatagramIOConfigurationImpl configuration;

    protected Router router;
    protected DatagramProcessor datagramProcessor;

    protected InetSocketAddress localAddress;
    protected MulticastSocket socket; // For sending unicast & multicast, and reveiving unicast

    public DatagramIOImpl(DatagramIOConfigurationImpl configuration) {
        this.configuration = configuration;
    }

    public DatagramIOConfigurationImpl getConfiguration() {
        return configuration;
    }

    synchronized public void init(InetAddress bindAddress, Router router, DatagramProcessor datagramProcessor) throws InitializationException {

        this.router = router;
        this.datagramProcessor = datagramProcessor;

        try {

            // TODO: UPNP VIOLATION: The spec does not prohibit using the 1900 port here again, however, the
            // Netgear ReadyNAS miniDLNA implementation will no longer answer if it has to send search response
            // back via UDP unicast to port 1900... so we use an ephemeral port
            log.info("Creating bound socket (for datagram input/output) on: " + bindAddress);
            localAddress = new InetSocketAddress(bindAddress, 0);
            socket = new MulticastSocket(localAddress);
            socket.setTimeToLive(configuration.getTimeToLive());
            socket.setReceiveBufferSize(262144); // Keep a backlog of incoming datagrams if we are not fast enough
        } catch (Exception ex) {
            throw new InitializationException("Could not initialize " + getClass().getSimpleName() + ": " + ex);
        }
    }

    synchronized public void stop() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    public void run() {
        log.fine("Entering blocking receiving loop, listening for UDP datagrams on: " + socket.getLocalAddress());

        while (true) {

            try {
                byte[] buf = new byte[getConfiguration().getMaxDatagramBytes()];
                DatagramPacket datagram = new DatagramPacket(buf, buf.length);

                socket.receive(datagram);

                log.fine(
                        "UDP datagram received from: "
                                + datagram.getAddress().getHostAddress()
                                + ":" + datagram.getPort()
                                + " on: " + localAddress
                );


                router.received(datagramProcessor.read(localAddress.getAddress(), datagram));

            } catch (SocketException ex) {
                log.fine("Socket closed");
                break;
            } catch (UnsupportedDataException ex) {
                log.info("Could not read datagram: " + ex.getMessage());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        try {
            if (!socket.isClosed()) {
                log.fine("Closing unicast socket");
                socket.close();
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    synchronized public void send(OutgoingDatagramMessage message) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("Sending message from address: " + localAddress);
        }
        DatagramPacket packet = datagramProcessor.write(message);

        if (log.isLoggable(Level.FINE)) {
            log.fine("Sending UDP datagram packet to: " + message.getDestinationAddress() + ":" + message.getDestinationPort());
        }
        
        send(packet);
    }

    synchronized public void send(DatagramPacket datagram) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("Sending message from address: " + localAddress);
        }
            
        try {
            socket.send(datagram);
        } catch (SocketException ex) {
            log.fine("Socket closed, aborting datagram send to: " + datagram.getAddress());
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Exception sending datagram to: " + datagram.getAddress() + ": " + ex, ex);
        }
    }
}
