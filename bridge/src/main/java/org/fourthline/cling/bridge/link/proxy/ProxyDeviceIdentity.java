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

import org.fourthline.cling.bridge.link.Endpoint;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.types.UDN;

/**
 * @author Christian Bauer
 */
public class ProxyDeviceIdentity extends DeviceIdentity {

    private Endpoint endpoint;
    
    public ProxyDeviceIdentity(UDN udn, Integer maxAgeSeconds, Endpoint endpoint) {
        super(udn, maxAgeSeconds);
        this.endpoint = endpoint;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ") UDN: " + getUdn().toString() + ", Endpoint: " + endpoint.toString();
    }

}
