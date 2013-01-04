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

package org.fourthline.cling.bridge;

import org.fourthline.cling.DefaultUpnpServiceConfiguration;
import org.fourthline.cling.bridge.auth.AuthManager;
import org.fourthline.cling.bridge.auth.SecureHashAuthManager;
import org.fourthline.cling.bridge.gateway.FormActionProcessor;
import org.fourthline.cling.bridge.link.proxy.CombinedDescriptorBinder;
import org.fourthline.cling.transport.Router;
import org.fourthline.cling.transport.spi.InitializationException;
import org.fourthline.cling.transport.spi.NetworkAddressFactory;
import org.fourthline.cling.transport.spi.StreamServer;
import org.fourthline.cling.transport.spi.StreamServerConfiguration;
import org.seamless.util.URIUtil;

import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.util.logging.Logger;

/**
 * @author Christian Bauer
 */
public class BridgeUpnpServiceConfiguration extends DefaultUpnpServiceConfiguration {

    final private static Logger log = Logger.getLogger(BridgeUpnpServiceConfiguration.class.getName());

    final private URL localBaseURL;
    final private String contextPath;
    final private CombinedDescriptorBinder combinedDescriptorBinder;
    final private FormActionProcessor actionProcessor;
    final private AuthManager authManager;

    public BridgeUpnpServiceConfiguration(URL localBaseURL) {
        this(localBaseURL, "");
    }

    public BridgeUpnpServiceConfiguration(URL localBaseURL, String contextPath) {
        super(localBaseURL.getPort(), false);
        this.localBaseURL = localBaseURL;
        this.contextPath = contextPath;
        this.actionProcessor = createFormActionProcessor();
        this.combinedDescriptorBinder = createCombinedDescriptorBinder();
        this.authManager = createAuthManager();

        log.info("Bridge configured with local URL: " + getLocalEndpointURLWithCredentials());
    }

    public URL getLocalBaseURL() {
        return localBaseURL;
    }

    public String getContextPath() {
        return contextPath;
    }

    public CombinedDescriptorBinder getCombinedDescriptorBinder() {
        return combinedDescriptorBinder;
    }

    public FormActionProcessor getActionProcessor() {
        return actionProcessor;
    }

    public AuthManager getAuthManager() {
        return authManager;
    }

    protected CombinedDescriptorBinder createCombinedDescriptorBinder() {
        return new CombinedDescriptorBinder(this);
    }

    protected FormActionProcessor createFormActionProcessor() {
        return new FormActionProcessor();
    }

    protected AuthManager createAuthManager() {
        return new SecureHashAuthManager();
    }

    public URL getLocalEndpointURL() {
        try {
            return new URL(
                    getLocalBaseURL().getProtocol(),
                    getLocalBaseURL().getHost(),
                    getLocalBaseURL().getPort(),
                    getNamespace().getBasePath().toString()
            );
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public URL getLocalEndpointURLWithCredentials() {
        StringBuilder url = new StringBuilder();
        url.append(getLocalEndpointURL().toString()).append("/");
        url.append("?").append(SecureHashAuthManager.QUERY_PARAM_AUTH);
        url.append("=").append(getAuthManager().getLocalCredentials());
        return URIUtil.toURL(URI.create(url.toString()));
    }

    // TODO: Make the network interfaces/IPs for binding configurable with servlet context params

    @Override
    public BridgeNamespace getNamespace() {
        return new BridgeNamespace(getContextPath());
    }

    // The job of the StreamServer is now taken care of by the GatewayFilter

    @Override
    public StreamServer createStreamServer(NetworkAddressFactory networkAddressFactory) {
        return new StreamServer() {
            public void init(InetAddress bindAddress, Router router) throws InitializationException {
            }

            public int getPort() {
                return getLocalBaseURL().getPort();
            }

            public void stop() {
            }

            public StreamServerConfiguration getConfiguration() {
                return null;
            }

            public void run() {
            }
        };
    }
}
