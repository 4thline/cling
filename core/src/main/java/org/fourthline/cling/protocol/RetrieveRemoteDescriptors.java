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

package org.fourthline.cling.protocol;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.binding.xml.DescriptorBindingException;
import org.fourthline.cling.binding.xml.DeviceDescriptorBinder;
import org.fourthline.cling.binding.xml.ServiceDescriptorBinder;
import org.fourthline.cling.model.ValidationError;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.registry.RegistrationException;
import org.seamless.util.Exceptions;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;

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

        // Exit if it has been discovered already, could be have been waiting in the executor queue too long
        if (getUpnpService().getRegistry().getRemoteDevice(rd.getIdentity().getUdn(), true) != null) {
            log.finer("Exiting early, already discovered: " + deviceURL);
            return;
        }

        try {
            activeRetrievals.add(deviceURL);
            describe();
        } finally {
            activeRetrievals.remove(deviceURL);
        }
    }

    protected void describe() {

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

    		log.fine("Sending device descriptor retrieval message: " + deviceDescRetrievalMsg);
            deviceDescMsg = getUpnpService().getRouter().send(deviceDescRetrievalMsg);
            
    	} catch(IllegalArgumentException e) {
    		// UpnpRequest constructor can throw IllegalArgumentException on invalid URI
    		// IllegalArgumentException can also be thrown by Appache HttpClient on blank URI in send()
            log.warning("Device descriptor retrieval failed: " + e.getMessage());
            return ;
    	}


        if (deviceDescMsg == null) {
            log.warning("Device descriptor retrieval failed, no response: " + rd.getIdentity().getDescriptorURL());
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
            log.fine("Received device descriptor without or with invalid Content-Type: " + rd.getIdentity().getDescriptorURL());
            // We continue despite the invalid UPnP message because we can still hope to convert the content
        }

        log.fine("Received root device descriptor: " + deviceDescMsg);
        describe(deviceDescMsg.getBodyString());
    }

    protected void describe(String descriptorXML) {

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
                log.warning("Device service description failed: " + rd);
                if (notifiedStart)
                    getUpnpService().getRegistry().notifyDiscoveryFailure(
                            describedDevice,
                            new DescriptorBindingException("Device service description failed: " + rd)
                    );
                return;
            }

            log.fine("Adding fully hydrated remote device to registry: " + hydratedDevice);
            // The registry will do the right thing: A new root device is going to be added, if it's
            // already present or we just received the descriptor again (because we got an embedded
            // devices' notification), it will simply update the expiration timestamp of the root
            // device.
            getUpnpService().getRegistry().addDevice(hydratedDevice);

        } catch (ValidationException ex) {
            log.warning("Could not validate device model: " + rd);
            for (ValidationError validationError : ex.getErrors()) {
                log.warning(validationError.toString());
            }
            if (describedDevice != null && notifiedStart)
                getUpnpService().getRegistry().notifyDiscoveryFailure(describedDevice, ex);

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
            throws DescriptorBindingException, ValidationException {

        List<RemoteService> describedServices = new ArrayList();
        if (currentDevice.hasServices()) {
            List<RemoteService> filteredServices = filterExclusiveServices(currentDevice.getServices());
            for (RemoteService service : filteredServices) {
                RemoteService svc = describeService(service);
                if (svc == null) { // Something went wrong, bail out
                    return null;
                }
                describedServices.add(svc);
            }
        }

        List<RemoteDevice> describedEmbeddedDevices = new ArrayList();
        if (currentDevice.hasEmbeddedDevices()) {
            for (RemoteDevice embeddedDevice : currentDevice.getEmbeddedDevices()) {
                if (embeddedDevice == null) continue;
                RemoteDevice describedEmbeddedDevice = describeServices(embeddedDevice);
                if (describedEmbeddedDevice == null) { // Something was wrong, recursively
                    return null;
                }
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
            throws DescriptorBindingException, ValidationException {

        URL descriptorURL = service.getDevice().normalizeURI(service.getDescriptorURI());
        StreamRequestMessage serviceDescRetrievalMsg = new StreamRequestMessage(UpnpRequest.Method.GET, descriptorURL);

        log.fine("Sending service descriptor retrieval message: " + serviceDescRetrievalMsg);
        StreamResponseMessage serviceDescMsg = getUpnpService().getRouter().send(serviceDescRetrievalMsg);

        if (serviceDescMsg == null) {
            log.warning("Could not retrieve service descriptor: " + service);
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
            log.warning("Received empty descriptor:" + descriptorURL);
            return null;
        }

        log.fine("Received service descriptor, hydrating service model: " + serviceDescMsg);
        ServiceDescriptorBinder serviceDescriptorBinder =
                getUpnpService().getConfiguration().getServiceDescriptorBinderUDA10();

        return serviceDescriptorBinder.describe(service, serviceDescMsg.getBodyString());
    }

    protected List<RemoteService> filterExclusiveServices(RemoteService[] services) {
        ServiceType[] exclusiveTypes = getUpnpService().getConfiguration().getExclusiveServiceTypes();

        if (exclusiveTypes == null || exclusiveTypes.length == 0)
            return Arrays.asList(services);

        List<RemoteService> exclusiveServices = new ArrayList();
        for (RemoteService discoveredService : services) {
            for (ServiceType exclusiveType : exclusiveTypes) {
                if (discoveredService.getServiceType().implementsVersion(exclusiveType)) {
                    log.fine("Including exlusive service: " + discoveredService);
                    exclusiveServices.add(discoveredService);
                } else {
                    log.fine("Excluding unwanted service: " + exclusiveType);
                }
            }
        }
        return exclusiveServices;
    }

}
