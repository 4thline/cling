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

package org.fourthline.cling.protocol;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.binding.xml.DescriptorBindingException;
import org.fourthline.cling.binding.xml.DeviceDescriptorBinder;
import org.fourthline.cling.binding.xml.ServiceDescriptorBinder;
import org.fourthline.cling.model.ValidationError;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.UpnpHeaders;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.registry.RegistrationException;
import org.fourthline.cling.transport.RouterException;
import org.seamless.util.Exceptions;

/**
 * Retrieves all remote device XML descriptors, parses them, creates an immutable device and service metadata graph.
 * <p>
 * This implementation encapsulates all steps which are necessary to create a fully usable and populated
 * device metadata graph of a particular UPnP device. It starts with an unhydrated and typically just
 * discovered {@link org.fourthline.cling.model.meta.RemoteDevice}, the only property that has to be available is
 * its {@link org.fourthline.cling.model.meta.RemoteDeviceIdentity}.
 * </p>
 * <p>
 * This protocol implementation will then retrieve the device's XML descriptor, parse it, and retrieve and
 * parse all service descriptors until all device and service metadata has been retrieved. The fully
 * hydrated device is then added to the {@link org.fourthline.cling.registry.Registry}.
 * </p>
 * <p>
 * Any descriptor retrieval, parsing, or validation error of the metadata will abort this protocol
 * with a warning message in the log.
 * </p>
 *
 * @author Christian Bauer
 */
public class RetrieveRemoteDescriptors implements Runnable {

    final private static Logger log = Logger.getLogger(RetrieveRemoteDescriptors.class.getName());

    private final UpnpService upnpService;
    private RemoteDevice rd;

    private static final Set<URL> activeRetrievals = new CopyOnWriteArraySet();
    protected List<UDN> errorsAlreadyLogged = new ArrayList<>();

    public RetrieveRemoteDescriptors(UpnpService upnpService, RemoteDevice rd) {
        this.upnpService = upnpService;
        this.rd = rd;
    }

    public UpnpService getUpnpService() {
        return upnpService;
    }

    public void run() {

        URL deviceURL = rd.getIdentity().getDescriptorURL();

        // Performance optimization, try to avoid concurrent GET requests for device descriptor,
        // if we retrieve it once, we have the hydrated device. There is no different outcome
        // processing this several times concurrently.

        if (activeRetrievals.contains(deviceURL)) {
            log.finer("Exiting early, active retrieval for URL already in progress: " + deviceURL);
            return;
        }

        // Exit if it has been discovered already, could be we have been waiting in the executor queue too long
        if (getUpnpService().getRegistry().getRemoteDevice(rd.getIdentity().getUdn(), true) != null) {
            log.finer("Exiting early, already discovered: " + deviceURL);
            return;
        }

        try {
            activeRetrievals.add(deviceURL);
            describe();
        } catch (RouterException ex) {
            log.log(Level.WARNING,
                "Descriptor retrieval failed: " + deviceURL,
                ex
            );
        } finally {
            activeRetrievals.remove(deviceURL);
        }
    }

    protected void describe() throws RouterException {

        // All of the following is a very expensive and time consuming procedure, thanks to the
        // braindead design of UPnP. Several GET requests, several descriptors, several XML parsing
        // steps - all of this could be done with one and it wouldn't make a difference. So every
        // call of this method has to be really necessary and rare.

    	if(getUpnpService().getRouter() == null) {
    		log.warning("Router not yet initialized");
    		return ;
    	}

    	StreamRequestMessage deviceDescRetrievalMsg;
    	StreamResponseMessage deviceDescMsg;

    	try {

    		deviceDescRetrievalMsg =
                new StreamRequestMessage(UpnpRequest.Method.GET, rd.getIdentity().getDescriptorURL());

            // Extra headers
            UpnpHeaders headers =
                getUpnpService().getConfiguration().getDescriptorRetrievalHeaders(rd.getIdentity());
            if (headers != null)
                deviceDescRetrievalMsg.getHeaders().putAll(headers);

    		log.fine("Sending device descriptor retrieval message: " + deviceDescRetrievalMsg);
            deviceDescMsg = getUpnpService().getRouter().send(deviceDescRetrievalMsg);

    	} catch(IllegalArgumentException ex) {
    		// UpnpRequest constructor can throw IllegalArgumentException on invalid URI
    		// IllegalArgumentException can also be thrown by Apache HttpClient on blank URI in send()
            log.warning(
                "Device descriptor retrieval failed: "
                + rd.getIdentity().getDescriptorURL()
                + ", possibly invalid URL: " + ex);
            return ;
        }

        if (deviceDescMsg == null) {
            log.warning(
                "Device descriptor retrieval failed, no response: " + rd.getIdentity().getDescriptorURL()
            );
            return;
        }

        if (deviceDescMsg.getOperation().isFailed()) {
            log.warning(
                    "Device descriptor retrieval failed: "
                            + rd.getIdentity().getDescriptorURL() +
                            ", "
                            + deviceDescMsg.getOperation().getResponseDetails()
            );
            return;
        }

        if (!deviceDescMsg.isContentTypeTextUDA()) {
            log.fine(
                "Received device descriptor without or with invalid Content-Type: "
                    + rd.getIdentity().getDescriptorURL());
            // We continue despite the invalid UPnP message because we can still hope to convert the content
        }

        String descriptorContent = deviceDescMsg.getBodyString();
        if (descriptorContent == null || descriptorContent.length() == 0) {
            log.warning("Received empty device descriptor:" + rd.getIdentity().getDescriptorURL());
            return;
        }

        log.fine("Received root device descriptor: " + deviceDescMsg);
        describe(descriptorContent);
    }

    protected void describe(String descriptorXML) throws RouterException {

        boolean notifiedStart = false;
        RemoteDevice describedDevice = null;
        try {

            DeviceDescriptorBinder deviceDescriptorBinder =
                    getUpnpService().getConfiguration().getDeviceDescriptorBinderUDA10();

            describedDevice = deviceDescriptorBinder.describe(
                    rd,
                    descriptorXML
            );

            log.fine("Remote device described (without services) notifying listeners: " + describedDevice);
            notifiedStart = getUpnpService().getRegistry().notifyDiscoveryStart(describedDevice);

            log.fine("Hydrating described device's services: " + describedDevice);
            RemoteDevice hydratedDevice = describeServices(describedDevice);
            if (hydratedDevice == null) {
            	if(!errorsAlreadyLogged.contains(rd.getIdentity().getUdn())) {
            		errorsAlreadyLogged.add(rd.getIdentity().getUdn());
            		log.warning("Device service description failed: " + rd);
            	}
                if (notifiedStart)
                    getUpnpService().getRegistry().notifyDiscoveryFailure(
                            describedDevice,
                            new DescriptorBindingException("Device service description failed: " + rd)
                    );
                return;
            } else {
                log.fine("Adding fully hydrated remote device to registry: " + hydratedDevice);
                // The registry will do the right thing: A new root device is going to be added, if it's
                // already present or we just received the descriptor again (because we got an embedded
                // devices' notification), it will simply update the expiration timestamp of the root
                // device.
                getUpnpService().getRegistry().addDevice(hydratedDevice);
            }

        } catch (ValidationException ex) {
    		// Avoid error log spam each time device is discovered, errors are logged once per device.
        	if(!errorsAlreadyLogged.contains(rd.getIdentity().getUdn())) {
        		errorsAlreadyLogged.add(rd.getIdentity().getUdn());
        		log.warning("Could not validate device model: " + rd);
        		for (ValidationError validationError : ex.getErrors()) {
        			log.warning(validationError.toString());
        		}
                if (describedDevice != null && notifiedStart)
                    getUpnpService().getRegistry().notifyDiscoveryFailure(describedDevice, ex);
        	}

        } catch (DescriptorBindingException ex) {
            log.warning("Could not hydrate device or its services from descriptor: " + rd);
            log.warning("Cause was: " + Exceptions.unwrap(ex));
            if (describedDevice != null && notifiedStart)
                getUpnpService().getRegistry().notifyDiscoveryFailure(describedDevice, ex);

        } catch (RegistrationException ex) {
            log.warning("Adding hydrated device to registry failed: " + rd);
            log.warning("Cause was: " + ex.toString());
            if (describedDevice != null && notifiedStart)
                getUpnpService().getRegistry().notifyDiscoveryFailure(describedDevice, ex);
        }
    }

    protected RemoteDevice describeServices(RemoteDevice currentDevice)
            throws RouterException, DescriptorBindingException, ValidationException {

        List<RemoteService> describedServices = new ArrayList<>();
        if (currentDevice.hasServices()) {
            List<RemoteService> filteredServices = filterExclusiveServices(currentDevice.getServices());
            for (RemoteService service : filteredServices) {
                RemoteService svc = describeService(service);
                 // Skip invalid services (yes, we can continue with only some services available)
                if (svc != null)
                    describedServices.add(svc);
                else
                    log.warning("Skipping invalid service '" + service + "' of: " + currentDevice);
            }
        }

        List<RemoteDevice> describedEmbeddedDevices = new ArrayList<>();
        if (currentDevice.hasEmbeddedDevices()) {
            for (RemoteDevice embeddedDevice : currentDevice.getEmbeddedDevices()) {
                 // Skip invalid embedded device
                if (embeddedDevice == null)
                    continue;
                RemoteDevice describedEmbeddedDevice = describeServices(embeddedDevice);
                 // Skip invalid embedded services
                if (describedEmbeddedDevice != null)
                    describedEmbeddedDevices.add(describedEmbeddedDevice);
            }
        }

        Icon[] iconDupes = new Icon[currentDevice.getIcons().length];
        for (int i = 0; i < currentDevice.getIcons().length; i++) {
            Icon icon = currentDevice.getIcons()[i];
            iconDupes[i] = icon.deepCopy();
        }

        // Yes, we create a completely new immutable graph here
        return currentDevice.newInstance(
                currentDevice.getIdentity().getUdn(),
                currentDevice.getVersion(),
                currentDevice.getType(),
                currentDevice.getDetails(),
                iconDupes,
                currentDevice.toServiceArray(describedServices),
                describedEmbeddedDevices
        );
    }

    protected RemoteService describeService(RemoteService service)
            throws RouterException, DescriptorBindingException, ValidationException {

    	URL descriptorURL;
    	try {
    		descriptorURL = service.getDevice().normalizeURI(service.getDescriptorURI());
    	}  catch(IllegalArgumentException e) {
    		log.warning("Could not normalize service descriptor URL: " + service.getDescriptorURI());
    		return null;
    	}

        StreamRequestMessage serviceDescRetrievalMsg = new StreamRequestMessage(UpnpRequest.Method.GET, descriptorURL);

        // Extra headers
        UpnpHeaders headers =
            getUpnpService().getConfiguration().getDescriptorRetrievalHeaders(service.getDevice().getIdentity());
        if (headers != null)
            serviceDescRetrievalMsg.getHeaders().putAll(headers);

        log.fine("Sending service descriptor retrieval message: " + serviceDescRetrievalMsg);
        StreamResponseMessage serviceDescMsg = getUpnpService().getRouter().send(serviceDescRetrievalMsg);

        if (serviceDescMsg == null) {
            log.warning("Could not retrieve service descriptor, no response: " + service);
            return null;
        }

        if (serviceDescMsg.getOperation().isFailed()) {
            log.warning("Service descriptor retrieval failed: "
                                + descriptorURL
                                + ", "
                                + serviceDescMsg.getOperation().getResponseDetails());
            return null;
        }

        if (!serviceDescMsg.isContentTypeTextUDA()) {
            log.fine("Received service descriptor without or with invalid Content-Type: " + descriptorURL);
            // We continue despite the invalid UPnP message because we can still hope to convert the content
        }

        String descriptorContent = serviceDescMsg.getBodyString();
        if (descriptorContent == null || descriptorContent.length() == 0) {
            log.warning("Received empty service descriptor:" + descriptorURL);
            return null;
        }

        log.fine("Received service descriptor, hydrating service model: " + serviceDescMsg);
        ServiceDescriptorBinder serviceDescriptorBinder =
                getUpnpService().getConfiguration().getServiceDescriptorBinderUDA10();

        return serviceDescriptorBinder.describe(service, descriptorContent);
    }

    protected List<RemoteService> filterExclusiveServices(RemoteService[] services) {
        ServiceType[] exclusiveTypes = getUpnpService().getConfiguration().getExclusiveServiceTypes();

        if (exclusiveTypes == null || exclusiveTypes.length == 0)
            return Arrays.asList(services);

        List<RemoteService> exclusiveServices = new ArrayList<>();
        for (RemoteService discoveredService : services) {
            for (ServiceType exclusiveType : exclusiveTypes) {
                if (discoveredService.getServiceType().implementsVersion(exclusiveType)) {
                    log.fine("Including exclusive service: " + discoveredService);
                    exclusiveServices.add(discoveredService);
                } else {
                    log.fine("Excluding unwanted service: " + exclusiveType);
                }
            }
        }
        return exclusiveServices;
    }

}
