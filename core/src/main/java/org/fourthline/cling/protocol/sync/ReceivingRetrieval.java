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

package org.fourthline.cling.protocol.sync;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.binding.xml.DescriptorBindingException;
import org.fourthline.cling.binding.xml.DeviceDescriptorBinder;
import org.fourthline.cling.binding.xml.ServiceDescriptorBinder;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.header.ContentTypeHeader;
import org.fourthline.cling.model.message.header.ServerHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.profile.ControlPointInfo;
import org.fourthline.cling.model.resource.DeviceDescriptorResource;
import org.fourthline.cling.model.resource.IconResource;
import org.fourthline.cling.model.resource.Resource;
import org.fourthline.cling.model.resource.ServiceDescriptorResource;
import org.fourthline.cling.protocol.ReceivingSync;
import org.seamless.util.Exceptions;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles reception of device/service descriptor and icon retrieval messages.
 *
 * <p>
 * Requested device and service XML descriptors are generated on-the-fly for every request.
 * </p>
 * <p>
 * Descriptor XML is dynamically generated depending on the control point - some control
 * points require different metadata than others for the same device and services.
 * </p>
 *
 * @author Christian Bauer
 */
public class ReceivingRetrieval extends ReceivingSync<StreamRequestMessage, StreamResponseMessage> {

    final private static Logger log = Logger.getLogger(ReceivingRetrieval.class.getName());

    public ReceivingRetrieval(UpnpService upnpService, StreamRequestMessage inputMessage) {
        super(upnpService, inputMessage);
    }

    protected StreamResponseMessage executeSync() {

        if (!getInputMessage().hasHostHeader()) {
            log.fine("Ignoring message, missing HOST header: " + getInputMessage());
            return new StreamResponseMessage(new UpnpResponse(UpnpResponse.Status.PRECONDITION_FAILED));
        }

        URI requestedURI = getInputMessage().getOperation().getURI();

        Resource foundResource = getUpnpService().getRegistry().getResource(requestedURI);

        if (foundResource == null) {
            log.fine("No local resource found: " + getInputMessage());
            return null;
        }

        return createResponse(requestedURI, foundResource);
    }

    protected StreamResponseMessage createResponse(URI requestedURI, Resource resource) {

        StreamResponseMessage response;

        try {

            if (DeviceDescriptorResource.class.isAssignableFrom(resource.getClass())) {

                log.fine("Found local device matching relative request URI: " + requestedURI);
                LocalDevice device = (LocalDevice) resource.getModel();

                DeviceDescriptorBinder deviceDescriptorBinder =
                        getUpnpService().getConfiguration().getDeviceDescriptorBinderUDA10();
                String deviceDescriptor = deviceDescriptorBinder.generate(
                        device,
                        createControlPointInfo(),
                        getUpnpService().getConfiguration().getNamespace()
                );
                response = new StreamResponseMessage(
                        deviceDescriptor,
                        new ContentTypeHeader(ContentTypeHeader.DEFAULT_CONTENT_TYPE)
                );
            } else if (ServiceDescriptorResource.class.isAssignableFrom(resource.getClass())) {


                log.fine("Found local service matching relative request URI: " + requestedURI);
                LocalService service = (LocalService) resource.getModel();

                ServiceDescriptorBinder serviceDescriptorBinder =
                        getUpnpService().getConfiguration().getServiceDescriptorBinderUDA10();
                String serviceDescriptor = serviceDescriptorBinder.generate(service);
                response = new StreamResponseMessage(
                        serviceDescriptor,
                        new ContentTypeHeader(ContentTypeHeader.DEFAULT_CONTENT_TYPE)
                );

            } else if (IconResource.class.isAssignableFrom(resource.getClass())) {

                log.fine("Found local icon matching relative request URI: " + requestedURI);
                Icon icon = (Icon) resource.getModel();
                response = new StreamResponseMessage(icon.getData(), icon.getMimeType());

            } else {

                log.fine("Ignoring GET for found local resource: " + resource);
                return null;
            }

        } catch (DescriptorBindingException ex) {
            log.warning("Error generating requested device/service descriptor: " + ex.toString());
            log.log(Level.WARNING, "Exception root cause: ", Exceptions.unwrap(ex));
            response = new StreamResponseMessage(UpnpResponse.Status.INTERNAL_SERVER_ERROR);
        }
        
        response.getHeaders().add(UpnpHeader.Type.SERVER, new ServerHeader());

        return response;
    }

    protected ControlPointInfo createControlPointInfo() {
        return new ControlPointInfo(getInputMessage().getHeaders());
    }

}
