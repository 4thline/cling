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

package org.fourthline.cling;

import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.controlpoint.ControlPointImpl;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.protocol.ProtocolFactoryImpl;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryImpl;
import org.fourthline.cling.registry.RegistryListener;
import org.fourthline.cling.transport.Router;
import org.fourthline.cling.transport.RouterImpl;

import javax.enterprise.inject.Alternative;
import java.util.logging.Logger;

/**
 * Default implementation of {@link UpnpService}, starts immediately on construction.
 * <p>
 * If no {@link UpnpServiceConfiguration} is provided it will automatically
 * instantiate {@link DefaultUpnpServiceConfiguration}. This configuration <strong>does not
 * work</strong> on Android! Use the {@link org.fourthline.cling.android.AndroidUpnpService}
 * application component instead
 * </p>
 * <p>
 * Override the various <tt>create...()</tt> methods to customize instantiation of protocol factory,
 * router, etc.
 * </p>
 *
 * @author Christian Bauer
 */
@Alternative
public class UpnpServiceImpl implements UpnpService {

    private static Logger log = Logger.getLogger(UpnpServiceImpl.class.getName());

    protected final UpnpServiceConfiguration configuration;
    protected final ControlPoint controlPoint;
    protected final ProtocolFactory protocolFactory;
    protected final Registry registry;
    protected final Router router;

    public UpnpServiceImpl() {
        this(new DefaultUpnpServiceConfiguration());
    }

    public UpnpServiceImpl(RegistryListener... registryListeners) {
        this(new DefaultUpnpServiceConfiguration(), registryListeners);
    }

    public UpnpServiceImpl(UpnpServiceConfiguration configuration, RegistryListener... registryListeners) {
        this.configuration = configuration;

        log.info(">>> Starting UPnP service...");

        log.info("Using configuration: " + getConfiguration().getClass().getName());

        // Instantiation order is important: Router needs to start its network services after registry is ready

        this.protocolFactory = createProtocolFactory();

        this.registry = createRegistry(protocolFactory);
        for (RegistryListener registryListener : registryListeners) {
            this.registry.addListener(registryListener);
        }

        this.router = createRouter(protocolFactory, registry);

        this.controlPoint = createControlPoint(protocolFactory, registry);

        log.info("<<< UPnP service started successfully");
    }

    protected ProtocolFactory createProtocolFactory() {
        return new ProtocolFactoryImpl(this);
    }

    protected Registry createRegistry(ProtocolFactory protocolFactory) {
        return new RegistryImpl(this);
    }

    protected Router createRouter(ProtocolFactory protocolFactory, Registry registry) {
        return new RouterImpl(getConfiguration(), protocolFactory);
    }

    protected ControlPoint createControlPoint(ProtocolFactory protocolFactory, Registry registry) {
        return new ControlPointImpl(getConfiguration(), protocolFactory, registry);
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

    public Router getRouter() {
        return router;
    }

    synchronized public void shutdown() {
        // Well, since java.util.logging has its own shutdown hook, this
        // might actually make it into the log or not...
        log.info(">>> Shutting down UPnP service...");

        getRegistry().shutdown();
        getRouter().shutdown();
        getConfiguration().shutdown();

        log.info("<<< UPnP service shutdown completed");
    }

}
