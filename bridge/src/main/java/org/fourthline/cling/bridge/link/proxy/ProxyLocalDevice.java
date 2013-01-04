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

import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.UDAVersion;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.UDN;

import java.util.List;

/**
 * @author Christian Bauer
 */
public class ProxyLocalDevice extends LocalDevice {

    public ProxyLocalDevice(ProxyDeviceIdentity identity) throws ValidationException {
        super(identity);
    }

    public ProxyLocalDevice(ProxyDeviceIdentity identity, UDAVersion version, DeviceType type, DeviceDetails details,
                            Icon[] icons,
                            LocalService[] services,
                            LocalDevice[] embeddedDevices) throws ValidationException {
        super(identity, version, type, details, icons, services, embeddedDevices);
    }

    @Override
    public ProxyDeviceIdentity getIdentity() {
        return (ProxyDeviceIdentity)super.getIdentity();
    }

    @Override
    public ProxyLocalDevice newInstance(UDN udn, UDAVersion version, DeviceType type, DeviceDetails details,
                                   Icon[] icons, LocalService[] services, List<LocalDevice> embeddedDevices) throws ValidationException {
        return new ProxyLocalDevice(
                new ProxyDeviceIdentity(udn, getIdentity().getMaxAgeSeconds(), getIdentity().getEndpoint()),
                version, type, details, icons,
                services,
                embeddedDevices.size() > 0 ? embeddedDevices.toArray(new LocalDevice[embeddedDevices.size()]) : null
        );
    }

}
