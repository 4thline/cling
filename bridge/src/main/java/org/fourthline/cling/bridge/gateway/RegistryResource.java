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


import org.fourthline.cling.model.resource.Resource;
import org.fourthline.cling.model.meta.Device;
import org.seamless.xhtml.Body;
import org.seamless.xhtml.XHTML;
import org.seamless.xhtml.XHTMLElement;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.seamless.xhtml.XHTML.ELEMENT;

/**
 * @author Christian Bauer
 */
@Path("/")
public class RegistryResource extends GatewayServerResource {

    @GET
    public XHTML represent() {
        XHTML result = getParserXHTML().createDocument();
        Body body = createBodyTemplate(result, getParserXHTML().createXPath(), "Registry");

        body.createChild(ELEMENT.h1).setContent("Registry");

        XHTMLElement registryContainer = body.createChild(ELEMENT.div).setId("registry");

        registryContainer.createChild(ELEMENT.h2).setContent("Devices");

        List<Device> devices = new ArrayList(getRegistry().getDevices());

        representDeviceList(registryContainer, devices);

        registryContainer.createChild(ELEMENT.h2).setContent("Internal (Local) Resources");

        List<Resource> resources = new ArrayList(getRegistry().getResources());
        if (resources.size() > 0) {
            Collections.sort(resources, new Comparator<Resource>() {
                public int compare(Resource a, Resource b) {
                    return a.getPathQuery().compareTo(b.getPathQuery());
                }
            });

            XHTMLElement registryResourcesList =
                    registryContainer.createChild(ELEMENT.ul)
                            .setId("resources");

            for (Resource resource : resources) {
                registryResourcesList.createChild(ELEMENT.li)
                        .setContent(
                                resource.getModel().getClass().getSimpleName() + " - " +
                                        appendLocalCredentials(resource.getPathQuery().toString())

                        );
            }
        }

        return result;
    }
}
