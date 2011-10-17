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

import org.jboss.resteasy.client.ClientRequest;
import org.fourthline.cling.bridge.BridgeUpnpService;
import org.fourthline.cling.bridge.Constants;
import org.fourthline.cling.bridge.link.proxy.ProxyDiscovery;
import org.fourthline.cling.bridge.link.proxy.ProxyLocalDevice;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.resource.Resource;
import org.seamless.util.Exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author Christian Bauer
 */
public class LinkManager {

    final private static Logger log = Logger.getLogger(LinkManager.class.getName());

    public static final String FORM_CALLBACK = "callback";
    public static final String FORM_TIMEOUT = "timeout-seconds";
    public static final String FORM_AUTH_HASH = "auth-key";

    final private BridgeUpnpService upnpService;
    final private ProxyDiscovery deviceDiscovery;
    final private Set<LinkManagementListener> listeners = new HashSet();

    public LinkManager(BridgeUpnpService upnpService) {
        this(upnpService, new ProxyDiscovery(upnpService));
    }

    public LinkManager(BridgeUpnpService upnpService, ProxyDiscovery deviceDiscovery) {
        this.upnpService = upnpService;
        this.deviceDiscovery = deviceDiscovery;
    }

    public BridgeUpnpService getUpnpService() {
        return upnpService;
    }

    public ProxyDiscovery getDeviceDiscovery() {
        return deviceDiscovery;
    }

    synchronized public void addListener(LinkManagementListener listener) {
        listeners.add(listener);
    }

    synchronized public void removeListener(LinkManagementListener listener) {
        listeners.remove(listener);
    }

    synchronized public void shutdown() {
        for (EndpointResource resource : getUpnpService().getRegistry().getResources(EndpointResource.class)) {
            log.fine("Deregistering and deleting on shutdown: " + resource.getModel());
            deregisterAndDelete(resource);
            log.info("Removed link: " + resource.getModel());
        }
    }

    synchronized boolean register(final EndpointResource resource) {
        return register(resource, Constants.LINK_DEFAULT_TIMEOUT_SECONDS);
    }

    synchronized boolean register(final EndpointResource resource, int timeoutSeconds) {
        Resource<Endpoint> existingResource = getUpnpService().getRegistry().getResource(resource.getPathQuery());

        log.info("New link created: " + resource.getModel());
        getUpnpService().getRegistry().addResource(resource, timeoutSeconds);

        if (existingResource == null) {

            for (final LinkManagementListener listener : listeners) {
                getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(
                        new Runnable() {
                            public void run() {
                                listener.endpointRegistered(resource.getModel());
                            }
                        }
                );
            }

            getUpnpService().getConfiguration().getAsyncProtocolExecutor().execute(
                    new Runnable() {
                        public void run() {
                            log.fine("Asynchronously sending current devices to new remote: " + resource.getModel());
                            getDeviceDiscovery().putCurrentDevices(resource.getModel());
                        }
                    }
            );
            return true;
        }

        return false;
    }

    synchronized public boolean registerAndPut(EndpointResource resource) {
        return registerAndPut(resource, Constants.LINK_DEFAULT_TIMEOUT_SECONDS);
    }

    synchronized public boolean registerAndPut(final EndpointResource resource, int timeoutSeconds) {

        log.fine("Storing in registry: " + resource.getModel());
        getUpnpService().getRegistry().addResource(resource, timeoutSeconds);

        boolean created = false;
        boolean failed = false;
        try {
            String requestURL = resource.getRemoteEndpointURL().toString();
            log.fine("Sending PUT to remote: " + requestURL);
            ClientRequest request = new ClientRequest(requestURL);

            StringBuilder body = new StringBuilder();
            body.append(FORM_CALLBACK)
                    .append("=")
                    .append(URLEncoder.encode(resource.getLocalEndpointURL().toString(), "UTF-8"));
            body.append("&");
            body.append(FORM_TIMEOUT)
                    .append("=")
                    .append(Integer.toString(timeoutSeconds));
            body.append("&");
            body.append(FORM_AUTH_HASH)
                    .append("=")
                    .append(getUpnpService().getConfiguration().getAuthManager().getLocalCredentials());
            request.body(MediaType.APPLICATION_FORM_URLENCODED, body.toString());

            getUpnpService().getConfiguration().getAuthManager().write(resource.getModel().getCredentials(), request);
            Response response = request.put();

            log.fine("Received response: " + response.getStatus());

            if (response.getStatus() != Response.Status.OK.getStatusCode() &&
                    response.getStatus() != Response.Status.CREATED.getStatusCode()) {

                log.info("Remote '" + resource.getModel() + "' notification failed: " + response.getStatus());
                failed = true;

            } else if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                created = true;
            }

        } catch (Exception ex) {
            log.info("Remote '" + resource.getModel() + "' notification failed: " + Exceptions.unwrap(ex));
            failed = true;
        }

        if (created) {
            log.info("New link created with local origin: " + resource.getModel());

            for (final LinkManagementListener listener : listeners) {
                getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(
                        new Runnable() {
                            public void run() {
                                listener.endpointRegistered(resource.getModel());
                            }
                        }
                );
            }

            getUpnpService().getConfiguration().getAsyncProtocolExecutor().execute(
                    new Runnable() {
                        public void run() {
                            log.fine("Asynchronously sending current devices to new remote: " + resource.getModel());
                            getDeviceDiscovery().putCurrentDevices(resource.getModel());
                        }
                    }
            );
        }

        if (failed) {
            deregister(resource);
        }

        return !failed;
    }

    synchronized protected boolean deregister(final EndpointResource resource) {
        boolean removed = getUpnpService().getRegistry().removeResource(resource);
        if (removed) {
            log.info("Link removed: " + resource.getModel());

            for (final LinkManagementListener listener : listeners) {
                getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(
                        new Runnable() {
                            public void run() {
                                listener.endpointDeregistered(resource.getModel());
                            }
                        }
                );
            }

            for (LocalDevice localDevice : getUpnpService().getRegistry().getLocalDevices()) {
                if (localDevice instanceof ProxyLocalDevice) {
                    ProxyLocalDevice proxyLocalDevice = (ProxyLocalDevice) localDevice;
                    if (proxyLocalDevice.getIdentity().getEndpoint().equals(resource.getModel())) {
                        log.info("Removing endpoint's proxy device from registry: " + proxyLocalDevice);
                        getUpnpService().getRegistry().removeDevice(proxyLocalDevice);
                    }
                }
            }
        }
        return removed;
    }

    synchronized public void deregisterAndDelete(EndpointResource resource) {
        deregister(resource);
        try {
            ClientRequest request = new ClientRequest(resource.getRemoteEndpointURL().toString());
            getUpnpService().getConfiguration().getAuthManager().write(resource.getModel().getCredentials(), request);
            Response response = request.delete();

            if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                log.info("Remote '" + resource.getModel() + "' deletion failed: " + response.getStatus());
            }

        } catch (Exception ex) {
            log.info("Remote '" + resource.getModel() + "' deletion failed: " + Exceptions.unwrap(ex));
        }
    }

}
