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
import org.fourthline.cling.transport.impl.AsyncServletStreamServerConfigurationImpl;
import org.fourthline.cling.transport.impl.AsyncServletStreamServerImpl;
import org.fourthline.cling.transport.impl.jetty.JettyServletContainer;
import org.fourthline.cling.transport.impl.jetty.StreamClientConfigurationImpl;
import org.fourthline.cling.transport.impl.jetty.StreamClientImpl;
import org.fourthline.cling.transport.spi.StreamClient;
import org.fourthline.cling.transport.spi.StreamServer;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Christian Bauer
 */
public class JettyServerJettyClientTest extends StreamServerClientTest {

    @Override
    public StreamServer createStreamServer(int port) {
        AsyncServletStreamServerConfigurationImpl configuration =
            new AsyncServletStreamServerConfigurationImpl(
                JettyServletContainer.INSTANCE,
                port
            );

        return new AsyncServletStreamServerImpl(
            configuration
        ) {
            @Override
            protected boolean isConnectionOpen(HttpServletRequest request) {
                return JettyServletContainer.isConnectionOpen(request);
            }
        };
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
}
