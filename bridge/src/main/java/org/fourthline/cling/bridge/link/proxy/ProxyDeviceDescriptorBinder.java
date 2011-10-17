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
