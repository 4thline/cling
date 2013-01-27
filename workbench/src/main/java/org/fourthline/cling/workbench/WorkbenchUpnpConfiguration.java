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

package org.fourthline.cling.workbench;

import org.fourthline.cling.ManagedUpnpServiceConfiguration;
import org.fourthline.cling.model.Namespace;
import org.fourthline.cling.transport.impl.RecoveringSOAPActionProcessorImpl;
import org.fourthline.cling.transport.spi.NetworkAddressFactory;
import org.fourthline.cling.transport.spi.SOAPActionProcessor;
import org.fourthline.cling.transport.spi.StreamClient;
import org.fourthline.cling.transport.spi.StreamServer;

import javax.enterprise.inject.Alternative;

/**
 * @author Christian Bauer
 */
@Alternative
public class WorkbenchUpnpConfiguration extends ManagedUpnpServiceConfiguration {

    @Override
    public SOAPActionProcessor getSoapActionProcessor() {
        return new RecoveringSOAPActionProcessorImpl();
    }

    @Override
    protected Namespace createNamespace() {
        return new Namespace("/upnp");
    }

    @Override
    public StreamClient createStreamClient() {
        return new org.fourthline.cling.transport.impl.jetty.StreamClientImpl(
            new org.fourthline.cling.transport.impl.jetty.StreamClientConfigurationImpl(
                getSyncProtocolExecutorService()
            )
        );
    }

    @Override
    public StreamServer createStreamServer(NetworkAddressFactory networkAddressFactory) {
        return new org.fourthline.cling.transport.impl.AsyncServletStreamServerImpl(
            new org.fourthline.cling.transport.impl.AsyncServletStreamServerConfigurationImpl(
                org.fourthline.cling.transport.impl.jetty.JettyServletContainer.INSTANCE,
                networkAddressFactory.getStreamListenPort()
            )
        );
    }
}
