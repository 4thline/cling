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

package org.fourthline.cling.bridge;

import org.seamless.util.URIUtil;
import org.fourthline.cling.model.Namespace;
import org.fourthline.cling.model.meta.Device;

import java.net.URI;

/**
 * @author Christian Bauer
 */
public class BridgeNamespace extends Namespace {

    public static final String PATH_LINK = "/link";
    public static final String PROXY_SEGMENT = "/proxy";

    public BridgeNamespace() {
        this("");
    }

    public BridgeNamespace(String contextPath) {
        // This is the base path of the internal UPnP stack's registry and its resources, which
        // we'll access with a filter when a request is processed by the gateway
        super(contextPath);
    }

    public URI getEndpointPath(String endpointId) {
        return URI.create(getBasePath() + PATH_LINK + "/" + endpointId);
    }

    public URI getProxyPath(String endpointId, Device device) {
        return URI.create(
                getEndpointPath(endpointId)
                        + PROXY_SEGMENT
                        + "/" + URIUtil.encodePathSegment(device.getIdentity().getUdn().getIdentifierString())
        );
    }

    public static String getIconId(Device device, int iconIndex) {
        return URIUtil.percentEncode(device.getIdentity().getUdn().getIdentifierString()) + "/" + iconIndex;
    }

}
