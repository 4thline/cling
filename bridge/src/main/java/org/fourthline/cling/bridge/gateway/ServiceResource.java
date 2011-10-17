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

package org.fourthline.cling.bridge.gateway;

import org.jboss.resteasy.client.ClientRequest;
import org.fourthline.cling.bridge.BridgeWebApplicationException;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.meta.Service;
import org.seamless.util.Exceptions;
import org.seamless.xhtml.Body;
import org.seamless.xhtml.XHTML;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Logger;

import static org.seamless.xhtml.XHTML.ELEMENT;

/**
 * @author Christian Bauer
 */
@Path("/dev/{UDN}/svc")
public class ServiceResource extends GatewayServerResource {

    final private static Logger log = Logger.getLogger(ServiceResource.class.getName());

    @GET
    public XHTML browseAll() {
        Device device = getRequestedDevice();

        XHTML result = getParserXHTML().createDocument();
        Body body = createBodyTemplate(result, getParserXHTML().createXPath(), "Services");

        body.createChild(ELEMENT.h1).setContent("Services");
        if (device.hasServices()) {
            representServices(body, Arrays.asList(device.getServices()));
        }

        return result;
    }

    @GET
    @Path("/{ServiceIdNamespace}")
    public XHTML browseNamespace(@PathParam("ServiceIdNamespace") String serviceIdNamespace) {
        Device device = getRequestedDevice();

        XHTML result = getParserXHTML().createDocument();
        Body body = createBodyTemplate(result, getParserXHTML().createXPath(), "Services");

        body.createChild(ELEMENT.h1).setContent("Services");
        if (device.hasServices()) {
            Collection<Service> services = new ArrayList();
            for (Service service : device.getServices()) {
                if (service.getServiceId().getNamespace().equals(serviceIdNamespace))
                    services.add(service);
            }
            if (services.size() == 0) {
                throw new BridgeWebApplicationException(Response.Status.NOT_FOUND);
            }
            representServices(body, services);
        }

        return result;
    }

    @GET
    @Path("/{ServiceIdNamespace}/{ServiceId}")
    public XHTML browse() {
        Service service = getRequestedService();

        XHTML result = getParserXHTML().createDocument();
        Body body = createBodyTemplate(result, getParserXHTML().createXPath(), "Service");

        body.createChild(ELEMENT.h1).setContent("Service");
        representService(body, service);

        return result;

    }

    @GET
    @Path("/{ServiceIdNamespace}/{ServiceId}/desc.xml")
    public Response retrieveMappedDescriptor() {
        RemoteDevice rd = getRequestedRemoteDevice();
        RemoteService service = getRequestedRemoteService();

        URL url = rd.normalizeURI(service.getDescriptorURI());

        log.fine("Retrieving service descriptor from remote URL: " + url);
        try {
            ClientRequest request = new ClientRequest(url.toString());
            request.accept(MediaType.TEXT_XML);
            return request.get(String.class);
        } catch (Exception ex) {
            throw new BridgeWebApplicationException(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    "Retrieving mapped descriptor failed: " + Exceptions.unwrap(ex)
            );
        }
    }

}
