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

package org.fourthline.cling.bridge.link.proxy;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.fourthline.cling.bridge.BridgeNamespace;
import org.fourthline.cling.bridge.BridgeUpnpService;
import org.fourthline.cling.bridge.auth.AuthCredentials;
import org.fourthline.cling.bridge.auth.AuthManager;
import org.fourthline.cling.bridge.link.Endpoint;
import org.fourthline.cling.bridge.link.EndpointResource;
import org.fourthline.cling.model.ValidationError;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.meta.StateVariable;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.seamless.util.Exceptions;
import org.seamless.util.MimeType;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Christian Bauer
 */
public class ProxyDiscovery extends DefaultRegistryListener {

    final private static Logger log = Logger.getLogger(ProxyDiscovery.class.getName());

    final private BridgeUpnpService upnpService;

    public ProxyDiscovery(BridgeUpnpService upnpService) {
        this.upnpService = upnpService;
    }

    public BridgeUpnpService getUpnpService() {
        return upnpService;
    }

    public AuthManager getAuthManager() {
        return getUpnpService().getConfiguration().getAuthManager();
    }

    @Override
    public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
        for (EndpointResource resource : registry.getResources(EndpointResource.class)) {
            log.fine("Remote device added, sending to endpoint: " + resource.getModel());
            putRemoteDevice(resource.getModel(), device);
        }
    }

    @Override
    public void localDeviceAdded(Registry registry, LocalDevice device) {
        if (device instanceof ProxyLocalDevice) {
            log.fine("Proxy added, not announcing to any endpoints: " + device);
            return;
        }
        for (EndpointResource resource : registry.getResources(EndpointResource.class)) {
            log.fine("Local device added, sending to endpoint: " + resource.getModel());
            putLocalDevice(resource.getModel(), device);
        }
    }

    @Override
    public void deviceRemoved(Registry registry, Device device) {
        if (device instanceof ProxyLocalDevice) {
            log.fine("Proxy removed, not announcing to any endpoints: " + device);
            return;
        }
        for (EndpointResource resource : registry.getResources(EndpointResource.class)) {
            log.fine("Device removed, removing from endpoint: " + resource.getModel());
            deleteDevice(resource.getModel(), device);
        }
    }

    public void putCurrentDevices(Endpoint endpoint) {
        log.fine("Sending current devices to: " + endpoint);
        
        boolean success = true;

        for (RemoteDevice remoteDevice : getUpnpService().getRegistry().getRemoteDevices()) {
            if (!putRemoteDevice(endpoint, remoteDevice)) {
                success = false;
                break;
            }
        }

        if (success) {
            for (LocalDevice localDevice : getUpnpService().getRegistry().getLocalDevices()) {
                if (localDevice instanceof ProxyLocalDevice) {
                    log.fine("Skipping proxy, not announcing to any endpoints: " + localDevice);
                    continue;
                }
                if (!putLocalDevice(endpoint, localDevice)) {
                    success = false;
                    break;
                }
            }
        }
        if (!success) {
            log.warning("Sending notification of current devices to remote '" + endpoint + "' failed");
        }
    }

    public boolean putRemoteDevice(Endpoint endpoint, RemoteDevice device) {

        RemoteDevice preparedDevice;
        try {
            // Rewrite the URIs of all services to URIs reachable through the HTTP gateway, etc.
            log.fine("Preparing remote device for proxying with a modified copy of the device metamodel graph");
            preparedDevice = prepareRemoteDevice(device);
        } catch (ValidationException ex) {
            // This should never happen, the graph was already OK and our transformation is bug-free
            log.warning("Could not validate transformed device model: " + device);
            for (ValidationError validationError : ex.getErrors()) {
                log.warning(validationError.toString());
            }
            return false;
        }

        return putProxy(
                getRemoteProxyURL(endpoint, device),
                endpoint.getCredentials(),
                preparedDevice
        );
    }

    public boolean putLocalDevice(final Endpoint endpoint, LocalDevice device) {
        return putProxy(
                getRemoteProxyURL(endpoint, device),
                endpoint.getCredentials(),
                device
        );
    }

    protected boolean putProxy(String remoteURL, AuthCredentials credentials, Device device) {
        String descriptor;
        try {
            descriptor = getUpnpService().getConfiguration().getCombinedDescriptorBinder().write(device);
        } catch (IOException ex) {
            log.warning("Could not create combined descriptor: " + Exceptions.unwrap(ex));
            return false;
        }

        boolean failed = false;
        try {
            log.info("Sending device proxy to: " + remoteURL);
            ClientRequest request = new ClientRequest(remoteURL);
            request.body(MediaType.TEXT_XML, descriptor);
            getAuthManager().write(credentials, request);
            Response response = request.put();

            if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                failed = true;
                log.warning("Sending notification of device addition to '" + remoteURL + "' failed: " + response.getStatus());
            }

        } catch (Exception ex) {
            log.warning("Sending notification of device addition to remote '" + remoteURL + "' failed: " + Exceptions.unwrap(ex));
            failed = true;
        }
        return !failed;
    }

    public boolean deleteDevice(Endpoint endpoint, Device device) {
        return deleteProxy(
                getRemoteProxyURL(endpoint, device),
                endpoint.getCredentials(),
                device
        );
    }

    protected boolean deleteProxy(String remoteURL, AuthCredentials credentials, Device device) {
        boolean failed = false;
        try {
            log.info("Sending deletion of device proxy: " + remoteURL);
            ClientRequest request = new ClientRequest(remoteURL);
            getAuthManager().write(credentials, request);
            Response response = request.delete();

            if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                failed = true;
                log.warning("Deleting remote proxy '" + remoteURL + "' failed: " + response.getStatus());
            } else {
                log.fine("Deleted remote proxy: " + remoteURL);
            }

        } catch (Exception ex) {
            log.warning("Deleting remote proxy '" + remoteURL + "' failed: " + Exceptions.unwrap(ex));
            failed = true;
        }
        return !failed;
    }

    protected RemoteDevice prepareRemoteDevice(RemoteDevice currentDevice) throws ValidationException {

        List<RemoteService> services = new ArrayList();
        if (currentDevice.hasServices()) {
            for (RemoteService service : currentDevice.getServices()) {
                services.add(prepareRemoteService(service));
            }
        }

        List<RemoteDevice> embeddedDevices = new ArrayList();
        if (currentDevice.hasEmbeddedDevices()) {
            for (RemoteDevice embeddedDevice : currentDevice.getEmbeddedDevices()) {
                embeddedDevices.add(prepareRemoteDevice(embeddedDevice));
            }
        }

        // We have to retrieve all icon data, so we can later put it into the combined descriptor base64 encoded
        List<Icon> icons = new ArrayList();
        if (currentDevice.hasIcons()) {
            for (int i = 0; i < currentDevice.getIcons().length; i++) {

                Icon icon = currentDevice.getIcons()[i];
                byte[] iconData = retrieveIconData(icon);
                if (iconData == null || iconData.length == 0) continue;

                // The URI is really not important, it just has to be unique when we later proxy this remote device
                // and of course, it has to match the rules for a local device icon URI (see LocalDevice.java#validate)
                icons.add(
                        new Icon(
                                icon.getMimeType(),
                                icon.getWidth(),
                                icon.getHeight(),
                                icon.getDepth(),
                                URI.create(BridgeNamespace.getIconId(currentDevice, i)),
                                iconData
                        )
                );
            }
        }

        return currentDevice.newInstance(
                currentDevice.getIdentity().getUdn(),
                currentDevice.getVersion(),
                currentDevice.getType(),
                currentDevice.getDetails(),
                icons.toArray(new Icon[icons.size()]),
                currentDevice.toServiceArray(services),
                embeddedDevices
        );
    }

    protected RemoteService prepareRemoteService(RemoteService service) throws ValidationException {
        BridgeNamespace namespace = getUpnpService().getConfiguration().getNamespace();

        Action[] actionDupes = new Action[service.getActions().length];
        for (int i = 0; i < service.getActions().length; i++) {
            Action<RemoteService> action = service.getActions()[i];
            actionDupes[i] = action.deepCopy();
        }

        StateVariable[] stateVariableDupes = new StateVariable[service.getStateVariables().length];
        for (int i = 0; i < service.getStateVariables().length; i++) {
            StateVariable stateVariable = service.getStateVariables()[i];
            stateVariableDupes[i] = stateVariable.deepCopy();
        }

        return service.getDevice().newInstance(
                service.getServiceType(),
                service.getServiceId(),
                namespace.getDescriptorPath(service),
                namespace.getControlPath(service),
                namespace.getEventSubscriptionPath(service),
                actionDupes,
                stateVariableDupes
        );
    }

    protected byte[] retrieveIconData(Icon icon) {
        if (icon.getData() != null) return icon.getData(); // This should cover LocalDevice

        if (!(icon.getDevice() instanceof RemoteDevice)) {
            log.warning("Can't retrieve icon data of: " + icon.getDevice());
            return new byte[0];
        }

        RemoteDevice remoteDevice = (RemoteDevice) icon.getDevice();
        String remoteURL = remoteDevice.normalizeURI(icon.getUri()).toString();
        try {
            ClientRequest request = new ClientRequest(remoteURL);
            log.fine("Retrieving icon data: " + remoteURL);
            ClientResponse<byte[]> response = request.get(byte[].class);

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                String contentType = response.getHeaders().getFirst("Content-Type");
                if (contentType == null || !MimeType.valueOf(contentType).getType().equals("image")) {
                    log.warning("Retrieving icon data of '" + remoteURL + "' failed, no image content type: " + contentType);
                    return new byte[0];
                }
                return response.getEntity();
            }
            log.warning("Retrieving icon data of '" + remoteURL + "' failed: " + response.getStatus());
        } catch (Exception ex) {
            log.warning("Retrieving icon data of '" + remoteURL + "' failed: " + Exceptions.unwrap(ex));
        }
        return new byte[0];
    }


    protected String getRemoteProxyURL(Endpoint endpoint, Device device) {
        return endpoint.getCallbackString() + new BridgeNamespace().getProxyPath(endpoint.getId(), device);
    }

}
