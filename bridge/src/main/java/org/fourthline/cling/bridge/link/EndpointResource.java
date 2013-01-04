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

package org.fourthline.cling.bridge.link;

import org.fourthline.cling.bridge.BridgeNamespace;
import org.fourthline.cling.model.ExpirationDetails;
import org.fourthline.cling.model.resource.Resource;
import org.seamless.util.URIUtil;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Christian Bauer
 */
public abstract class EndpointResource extends Resource<Endpoint> {

    final private static Logger log = Logger.getLogger(EndpointResource.class.getName());

    final protected URL localEndpointURL;

    public EndpointResource(URI resourcePath, URL localEndpointURL, Endpoint endpoint) {
        super(resourcePath, endpoint);
        this.localEndpointURL = localEndpointURL;
    }

    public URL getLocalEndpointURL() {
        return localEndpointURL;
    }

    public URL getRemoteEndpointURL() {
        String callbackURL = getModel().getCallbackString();
        return URIUtil.toURL(URI.create(
                (callbackURL.endsWith("/") ? callbackURL.substring(0, callbackURL.length() - 1) : callbackURL) +
                        new BridgeNamespace().getEndpointPath(getModel().getId())
        ));
    }

    @Override
    synchronized public void maintain(List<Runnable> pendingExecutions, final ExpirationDetails expirationDetails) {
        if (expirationDetails.hasExpired(true)) {

            // Link was initiated on this side, maintain it
            if (getModel().isLocalOrigin()) {
                pendingExecutions.add(new Runnable() {
                    public void run() {
                        log.fine("Endpoint with this origin is almost expired, updating link: " + getModel());
                        getLinkManager().registerAndPut(EndpointResource.this, expirationDetails.getMaxAgeSeconds());
                    }
                });
            }

            // Link wasn't initiated on this side, remove it
        }
    }

    @Override
    synchronized public void shutdown() {
        getLinkManager().deregisterAndDelete(this);
    }

    public abstract LinkManager getLinkManager();

}
