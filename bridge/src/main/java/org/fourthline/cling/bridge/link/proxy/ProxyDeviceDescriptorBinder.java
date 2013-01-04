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

import org.fourthline.cling.binding.staging.MutableDevice;
import org.fourthline.cling.binding.staging.MutableService;
import org.fourthline.cling.binding.xml.UDA10DeviceDescriptorBinderImpl;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.types.ServiceId;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Christian Bauer
 */
public class ProxyDeviceDescriptorBinder extends UDA10DeviceDescriptorBinderImpl {

    final Map<ServiceId, ProxyServiceCoordinates> serviceCoordinates = new HashMap();

    public Map<ServiceId, ProxyServiceCoordinates> getServiceCoordinates() {
        return serviceCoordinates;
    }

    @Override
    public <D extends Device> D buildInstance(D undescribedDevice, MutableDevice descriptor) throws ValidationException {
        storeServiceCoordinates(descriptor);
        return super.buildInstance(undescribedDevice, descriptor);
    }
    
    protected void storeServiceCoordinates(MutableDevice descriptor) {

        // Keep these for later, we can't store them in the LocalDevice graph
        // TODO: One of the reasons why it should be redesigned, again...

        for (MutableService service : descriptor.services) {
            getServiceCoordinates().put(
                    service.serviceId,
                    new ProxyServiceCoordinates(
                            service.descriptorURI,
                            service.controlURI,
                            service.eventSubscriptionURI
                    )
            );
        }

        for (MutableDevice embeddedDevice : descriptor.embeddedDevices) {
            storeServiceCoordinates(embeddedDevice);
        }
    }
}
