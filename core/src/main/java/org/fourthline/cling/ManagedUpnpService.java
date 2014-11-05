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

package org.fourthline.cling;

import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryListener;
import org.fourthline.cling.registry.event.After;
import org.fourthline.cling.registry.event.Before;
import org.fourthline.cling.registry.event.FailedRemoteDeviceDiscovery;
import org.fourthline.cling.registry.event.LocalDeviceDiscovery;
import org.fourthline.cling.registry.event.Phase;
import org.fourthline.cling.registry.event.RegistryShutdown;
import org.fourthline.cling.registry.event.RemoteDeviceDiscovery;
import org.fourthline.cling.transport.DisableRouter;
import org.fourthline.cling.transport.EnableRouter;
import org.fourthline.cling.transport.Router;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * Adapter for CDI environments.
 * <p>
 * The CDI container provides injectable instances of Cling UPnP interfaces, e.g.
 * you can <code>@Inject Registry</code> or <code>@Inject ControlPoint</code>.
 * </p>
 * <p>
 * Furthermore, this adapter also binds Cling into the CDI eventing system. You
 * can <code>@Observe RemoteDeviceDiscoveryStart</code> etc. events of the
 * registry.
 * </p>
 * <p>
 * Even better, in the future you might be able to listen to GENA UPnP events with
 * the same API - although this will require some magic for subscription...
 * </p>
 * <p>
 * TODO: This is a work in progress.
 * </p>
 *
 * @author Christian Bauer
 */
@ApplicationScoped
public class ManagedUpnpService implements UpnpService {

    final private static Logger log = Logger.getLogger(ManagedUpnpService.class.getName());

    @Inject
    RegistryListenerAdapter registryListenerAdapter;

    @Inject
    Instance<UpnpServiceConfiguration> configuration;

    @Inject
    Instance<Registry> registryInstance;

    @Inject
    Instance<Router> routerInstance;

    @Inject
    Instance<ProtocolFactory> protocolFactoryInstance;

    @Inject
    Instance<ControlPoint> controlPointInstance;

    @Inject
    Event<EnableRouter> enableRouterEvent;

    @Inject
    Event<DisableRouter> disableRouterEvent;

    @Override
    public UpnpServiceConfiguration getConfiguration() {
        return configuration.get();
    }

    @Override
    public ControlPoint getControlPoint() {
        return controlPointInstance.get();
    }

    @Override
    public ProtocolFactory getProtocolFactory() {
        return protocolFactoryInstance.get();
    }

    @Override
    public Registry getRegistry() {
        return registryInstance.get();
    }

    @Override
    public Router getRouter() {
        return routerInstance.get();
    }

    public void start(@Observes Start start) {
        log.info(">>> Starting managed UPnP service...");

        // First start the registry before we can receive messages through the transport

        getRegistry().addListener(registryListenerAdapter);

        enableRouterEvent.fire(new EnableRouter());

        log.info("<<< Managed UPnP service started successfully");
    }

    @Override
    public void shutdown() {
        shutdown(null);
    }

    public void shutdown(@Observes Shutdown shutdown) {

        // Well, since java.util.logging has its own shutdown hook, this
        // might actually make it into the log or not...
        log.info(">>> Shutting down managed UPnP service...");

        // First stop the registry and announce BYEBYE on the transport
        getRegistry().shutdown();

        disableRouterEvent.fire(new DisableRouter());

        getConfiguration().shutdown();

        log.info("<<< Managed UPnP service shutdown completed");
    }

    @ApplicationScoped
    static class RegistryListenerAdapter implements RegistryListener {

        @Inject
        @Any
        Event<RemoteDeviceDiscovery> remoteDeviceDiscoveryEvent;

        @Inject
        @Any
        Event<FailedRemoteDeviceDiscovery> failedRemoteDeviceDiscoveryEvent;

        @Inject
        @Any
        Event<LocalDeviceDiscovery> localDeviceDiscoveryEvent;

        @Inject
        @Any
        Event<RegistryShutdown> registryShutdownEvent;

        @Override
        public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
            remoteDeviceDiscoveryEvent.select(Phase.ALIVE).fire(
                    new RemoteDeviceDiscovery(device)
            );
        }

        @Override
        public void remoteDeviceDiscoveryFailed(Registry registry, RemoteDevice device, Exception ex) {
            failedRemoteDeviceDiscoveryEvent.fire(
                    new FailedRemoteDeviceDiscovery(device, ex)
            );
        }

        @Override
        public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
            remoteDeviceDiscoveryEvent.select(Phase.COMPLETE).fire(
                    new RemoteDeviceDiscovery(device)
            );
        }

        @Override
        public void remoteDeviceUpdated(Registry registry, RemoteDevice device) {
            remoteDeviceDiscoveryEvent.select(Phase.UPDATED).fire(
                    new RemoteDeviceDiscovery(device)
            );
        }

        @Override
        public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
            remoteDeviceDiscoveryEvent.select(Phase.BYEBYE).fire(
                    new RemoteDeviceDiscovery(device)
            );
        }

        @Override
        public void localDeviceAdded(Registry registry, LocalDevice device) {
            localDeviceDiscoveryEvent.select(Phase.COMPLETE).fire(
                    new LocalDeviceDiscovery(device)
            );
        }

        @Override
        public void localDeviceRemoved(Registry registry, LocalDevice device) {
            localDeviceDiscoveryEvent.select(Phase.BYEBYE).fire(
                    new LocalDeviceDiscovery(device)
            );
        }

        @Override
        public void beforeShutdown(Registry registry) {
            registryShutdownEvent.select(new AnnotationLiteral<Before>() {
            }).fire(
                    new RegistryShutdown()
            );
        }

        @Override
        public void afterShutdown() {
            registryShutdownEvent.select(new AnnotationLiteral<After>() {
            }).fire(
                    new RegistryShutdown()
            );
        }
    }

}
