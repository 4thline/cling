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
