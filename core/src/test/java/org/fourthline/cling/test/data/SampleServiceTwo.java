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

package org.fourthline.cling.test.data;

import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.StateVariable;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDAServiceId;
import org.fourthline.cling.model.types.UDAServiceType;
import org.seamless.util.URIUtil;

import java.net.URI;
import java.net.URL;

/**
 * @author Christian Bauer
 */
public class SampleServiceTwo extends SampleService {

    public static URI getThisDescriptorURI() {
        return URI.create("service/upnp-org/MY-SERVICE-456/desc");
    }

    public static URL getThisDescriptorURL() {
        return URIUtil.createAbsoluteURL(SampleDeviceRoot.getDeviceDescriptorURL(), getThisDescriptorURI());
    }

    public static ServiceId getThisServiceId() {
        return new UDAServiceId("MY-SERVICE-456");
    }

    public static ServiceType getThisServiceType() {
        return new UDAServiceType("MY-SERVICE-TYPE-TWO", 2);
    }

    @Override
    public ServiceType getServiceType() {
        return getThisServiceType();
    }

    @Override
    public ServiceId getServiceId() {
        return getThisServiceId();
    }

    @Override
    public URI getDescriptorURI() {
        return getThisDescriptorURI();
    }

    @Override
    public URI getControlURI() {
        return URI.create("service/upnp-org/MY-SERVICE-456/control");
    }

    @Override
    public URI getEventSubscriptionURI() {
        return URI.create("service/upnp-org/MY-SERVICE-456/events");
    }

    @Override
    public Action[] getActions() {
        return new Action[0];
    }

    @Override
    public StateVariable[] getStateVariables() {
        return new StateVariable[0];
    }

}
