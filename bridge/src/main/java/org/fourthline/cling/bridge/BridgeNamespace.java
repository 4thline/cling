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
