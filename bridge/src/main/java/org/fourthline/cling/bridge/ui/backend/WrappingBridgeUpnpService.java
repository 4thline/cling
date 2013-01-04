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

package org.fourthline.cling.bridge.ui.backend;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.bridge.BridgeProtocolFactory;
import org.fourthline.cling.bridge.BridgeUpnpService;
import org.fourthline.cling.bridge.BridgeUpnpServiceConfiguration;
import org.fourthline.cling.bridge.link.LinkManager;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.transport.Router;

/**
 * @author Christian Bauer
 */
public class WrappingBridgeUpnpService implements BridgeUpnpService {

    final private UpnpService wrapped;
    final private BridgeUpnpServiceConfiguration configuration;
    final private LinkManager linkManager;

    public WrappingBridgeUpnpService(UpnpService wrapped, BridgeUpnpServiceConfiguration configuration) {
        this.wrapped = wrapped;
        this.configuration = configuration;
        this.linkManager = createLinkManager();
        getRegistry().addListener(linkManager.getDeviceDiscovery());
    }

    protected LinkManager createLinkManager() {
        return new LinkManager(this);
    }

    public BridgeUpnpServiceConfiguration getConfiguration() {
        return configuration;
    }

    public LinkManager getLinkManager() {
        return linkManager;
    }

    public ControlPoint getControlPoint() {
        return wrapped.getControlPoint();
    }

    public ProtocolFactory getProtocolFactory() {
        return new BridgeProtocolFactory(this);
    }

    public Registry getRegistry() {
        return wrapped.getRegistry();
    }

    public Router getRouter() {
        return wrapped.getRouter();
    }

    public void shutdown() {
        getLinkManager().shutdown();
    }
}
