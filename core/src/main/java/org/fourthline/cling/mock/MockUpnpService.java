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

package org.fourthline.cling.mock;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.controlpoint.ControlPointImpl;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.protocol.ProtocolFactoryImpl;
import org.fourthline.cling.protocol.async.SendingNotificationAlive;
import org.fourthline.cling.protocol.async.SendingSearch;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryImpl;
import org.fourthline.cling.registry.RegistryMaintainer;
import org.fourthline.cling.transport.RouterException;
import org.fourthline.cling.transport.spi.NetworkAddressFactory;

import javax.enterprise.inject.Alternative;

/**
 * Simplifies testing of core and non-core modules.
 * <p>
 * It uses the {@link org.fourthline.cling.mock.MockUpnpService.MockProtocolFactory}.
 * </p>
 *
 * @author Christian Bauer
 */
@Alternative
public class MockUpnpService implements UpnpService {

    protected final UpnpServiceConfiguration configuration;
    protected final ControlPoint controlPoint;
    protected final ProtocolFactory protocolFactory;
    protected final Registry registry;
    protected final MockRouter router;

    protected final NetworkAddressFactory networkAddressFactory;

    /**
     * Single-thread of execution for the whole UPnP stack, no ALIVE messages or registry maintenance.
     */
    public MockUpnpService() {
        this(false, new MockUpnpServiceConfiguration(false, false));
    }

    /**
     * No ALIVE messages.
     */
    public MockUpnpService(MockUpnpServiceConfiguration configuration) {
        this(false, configuration);
    }

    /**
     * Single-thread of execution for the whole UPnP stack, except one background registry maintenance thread.
     */
    public MockUpnpService(final boolean sendsAlive, final boolean maintainsRegistry) {
        this(sendsAlive, new MockUpnpServiceConfiguration(maintainsRegistry, false));
    }

    public MockUpnpService(final boolean sendsAlive, final boolean maintainsRegistry, final boolean multiThreaded) {
        this(sendsAlive, new MockUpnpServiceConfiguration(maintainsRegistry, multiThreaded));
    }

    public MockUpnpService(final boolean sendsAlive, final MockUpnpServiceConfiguration configuration) {

        this.configuration = configuration;

        this.protocolFactory = createProtocolFactory(this, sendsAlive);

        this.registry = new RegistryImpl(this) {
            @Override
            protected RegistryMaintainer createRegistryMaintainer() {
                return configuration.isMaintainsRegistry() ? super.createRegistryMaintainer() : null;
            }
        };

        this.networkAddressFactory = this.configuration.createNetworkAddressFactory();

        this.router = createRouter();

        this.controlPoint = new ControlPointImpl(configuration, protocolFactory, registry);
    }

    protected ProtocolFactory createProtocolFactory(UpnpService service, boolean sendsAlive) {
        return new MockProtocolFactory(service, sendsAlive);
    }

    protected MockRouter createRouter() {
        return new MockRouter(getConfiguration(), getProtocolFactory());
    }

    /**
     * This factory customizes several protocols.
     * <p>
     * The {@link org.fourthline.cling.protocol.async.SendingNotificationAlive} protocol
     * only sends messages if this feature is enabled when instantiating the factory.
     * </p>
     * <p>
     * The {@link org.fourthline.cling.protocol.async.SendingSearch} protocol doesn't wait between
     * sending search message bulks, this speeds up testing.
     * </p>
     */
    public static class MockProtocolFactory extends ProtocolFactoryImpl {

        private boolean sendsAlive;

        public MockProtocolFactory(UpnpService upnpService, boolean sendsAlive) {
            super(upnpService);
            this.sendsAlive = sendsAlive;
        }

        @Override
        public SendingNotificationAlive createSendingNotificationAlive(LocalDevice localDevice) {
            return new SendingNotificationAlive(getUpnpService(), localDevice) {
                @Override
                protected void execute() throws RouterException {
                    if (sendsAlive) super.execute();
                }
            };
        }

        @Override
        public SendingSearch createSendingSearch(UpnpHeader searchTarget, int mxSeconds) {
            return new SendingSearch(getUpnpService(), searchTarget, mxSeconds) {
                @Override
                public int getBulkIntervalMilliseconds() {
                    return 0; // Don't wait
                }
            };
        }
    }

    public UpnpServiceConfiguration getConfiguration() {
        return configuration;
    }

    public ControlPoint getControlPoint() {
        return controlPoint;
    }

    public ProtocolFactory getProtocolFactory() {
        return protocolFactory;
    }

    public Registry getRegistry() {
        return registry;
    }

    public MockRouter getRouter() {
        return router;
    }

    public void shutdown() {
        getRegistry().shutdown();
        getConfiguration().shutdown();
    }
}
