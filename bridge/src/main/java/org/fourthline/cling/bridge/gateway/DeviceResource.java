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

package org.fourthline.cling.bridge.gateway;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.fourthline.cling.bridge.BridgeWebApplicationException;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.seamless.util.Exceptions;
import org.seamless.xhtml.Body;
import org.seamless.xhtml.XHTML;
import org.seamless.xhtml.XHTMLElement;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.seamless.xhtml.XHTML.ELEMENT;

/**
 * @author Christian Bauer
 */
@Path("/dev")
public class DeviceResource extends GatewayServerResource {

    final private static Logger log = Logger.getLogger(DeviceResource.class.getName());

    @GET
    public XHTML browseAll() {

        XHTML result = getParserXHTML().createDocument();
        Body body = createBodyTemplate(result, getParserXHTML().createXPath(), "Devices");

        body.createChild(ELEMENT.h1).setContent("Devices");

        XHTMLElement registryContainer = body.createChild(ELEMENT.div).setId("registry");

        List<Device> devices = new ArrayList(getRegistry().getDevices());

        representDeviceList(registryContainer, devices);

        return result;
    }

    @GET
    @Path("/{UDN}")
    public XHTML browse() {
        Device device = getRequestedDevice();

        XHTML result = getParserXHTML().createDocument();
        Body body = createBodyTemplate(result, getParserXHTML().createXPath(), device.getClass().getSimpleName());

        body.createChild(ELEMENT.h1).setContent(device.getClass().getSimpleName());

        representDevice(body, device);

        return result;
    }

    @GET
    @Path("/{UDN}/desc.xml")
    public Response retrieveMappedDescriptor() {
        RemoteDevice rd = getRequestedRemoteDevice();
        String url = rd.getIdentity().getDescriptorURL().toString();
        log.fine("Retrieving device descriptor from remote URL: " + url);
        try {
            ClientRequest request = new ClientRequest(url);
            request.accept(MediaType.TEXT_XML);
            return request.get(String.class);
        } catch (Exception ex) {
            throw new BridgeWebApplicationException(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    "Retrieving mapped descriptor failed: " + Exceptions.unwrap(ex)
            );
        }
    }

    @GET
    @Path("/{UDN}/MappedIcon/{Index}")
    public Response retrieveMappedIcon(@PathParam("Index") int index) {
        RemoteDevice rd = getRequestedRemoteDevice();

        if (!rd.hasIcons() || rd.getIcons().length - 1 < index) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        URL iconURL = rd.normalizeURI(rd.getIcons()[index].getUri());
        log.fine("Retrieving icon from remote URL: " + iconURL);
        try {
            ClientRequest request = new ClientRequest(iconURL.toString());
            request.accept("image/*");
            // If I return the Response directly, Resteasy puts some broken additional headers on it - but of
            // course only if I serve the icon from my own Cling library code. I have no idea why, all seems valid.
            // return request.get(byte[].class);
            ClientResponse res = request.get(byte[].class);
            return Response.ok(res.getEntity(), res.getHeaders().getFirst("Content-Type").toString()).build();
        } catch (Exception ex) {
            throw new BridgeWebApplicationException(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    "Retrieving mapped icon failed: " + Exceptions.unwrap(ex)
            );
        }
    }

}
