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

package org.fourthline.cling.transport.spi;

import org.fourthline.cling.transport.Router;

import java.net.NetworkInterface;

/**
 * Service for receiving multicast UDP datagrams, one per bound network interface.
 * <p>
 * This services typically listens on a socket for UDP datagrams, the socket has joined
 * the configured multicast group.
 * </p>
 * <p>
 * This listening loop is started with the <code>run()</code> method,
 * this service is <code>Runnable</code>. Any received datagram is then converted into an
 * {@link org.fourthline.cling.model.message.IncomingDatagramMessage} and
 * handled by the
 * {@link org.fourthline.cling.transport.Router#received(org.fourthline.cling.model.message.IncomingDatagramMessage)}
 * method. This conversion is the job of the {@link org.fourthline.cling.transport.spi.DatagramProcessor}.
 * </p>
 * <p>
 * An implementation has to be thread-safe.
 * </p>
 * 
 * @param <C> The type of the service's configuration.
 *
 * @author Christian Bauer
 */
public interface MulticastReceiver<C extends MulticastReceiverConfiguration> extends Runnable {

    /**
     * Configures the service and starts any listening sockets.
     *
     * @param networkInterface The network interface on which to join the multicast group on.
     * @param router The router which handles received {@link org.fourthline.cling.model.message.IncomingDatagramMessage}s.
     * @param networkAddressFactory The network address factory to use for local address lookup given a local interface and a remote address.
     * @param datagramProcessor Reads and writes datagrams.
     * @throws InitializationException If the service could not be initialized or started.
     */
    public void init(NetworkInterface networkInterface,
                     Router router,
                     NetworkAddressFactory networkAddressFactory,
                     DatagramProcessor datagramProcessor) throws InitializationException;

    /**
     * Stops the service, closes any listening sockets.
     */
    public void stop();

    /**
     * @return This service's configuration.
     */
    public C getConfiguration();

}
