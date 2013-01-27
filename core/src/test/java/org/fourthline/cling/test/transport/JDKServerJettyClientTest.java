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
package org.fourthline.cling.test.transport;

import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.transport.impl.StreamServerConfigurationImpl;
import org.fourthline.cling.transport.impl.StreamServerImpl;
import org.fourthline.cling.transport.impl.jetty.StreamClientConfigurationImpl;
import org.fourthline.cling.transport.impl.jetty.StreamClientImpl;
import org.fourthline.cling.transport.spi.StreamClient;
import org.fourthline.cling.transport.spi.StreamServer;

/**
 * @author Christian Bauer
 */
public class JDKServerJettyClientTest extends StreamServerClientTest {

    @Override
    public StreamServer createStreamServer(int port) {
        return new StreamServerImpl(
            new StreamServerConfigurationImpl(port)
        );
    }

    @Override
    public StreamClient createStreamClient(UpnpServiceConfiguration configuration) {
        return new StreamClientImpl(
            new StreamClientConfigurationImpl(
                configuration.getSyncProtocolExecutorService(),
                3
            )
        );
    }

    // DISABLED, NOT SUPPORTED
    @Override
    public void checkAliveExpired() throws Exception {
    }

    @Override
    public void checkAliveCancelled() throws Exception {
    }

}
