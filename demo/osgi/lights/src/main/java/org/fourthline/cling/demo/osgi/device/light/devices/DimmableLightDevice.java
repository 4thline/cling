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

package org.fourthline.cling.demo.osgi.device.light.devices;

import org.fourthline.cling.demo.osgi.device.light.services.DimmingService;
import org.osgi.service.upnp.UPnPDevice;
import org.osgi.service.upnp.UPnPIcon;
import org.osgi.service.upnp.UPnPService;
import org.fourthline.cling.demo.osgi.device.light.icons.LightDeviceIcon;
import org.fourthline.cling.demo.osgi.device.light.model.DimmableLight;
import org.fourthline.cling.demo.osgi.device.light.services.SwitchPowerService;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DimmableLightDevice implements UPnPDevice {
    final private String DEVICE_ID = UUID.randomUUID().toString();

    private Dictionary<String, Object> descriptions;
    private UPnPService[] services;
    private Map<String, UPnPService> index = new HashMap<String, UPnPService>();
    private UPnPIcon icon = new LightDeviceIcon();
    private UPnPIcon[] icons = {icon};

    public DimmableLightDevice(DimmableLight light, String presentationURL) {
        descriptions = new Hashtable<String, Object>();
        descriptions.put(UPnPDevice.UPNP_EXPORT, "");
        descriptions.put(
                org.osgi.service.device.Constants.DEVICE_CATEGORY,
                new String[]{UPnPDevice.DEVICE_CATEGORY}
        );
        descriptions.put(UPnPDevice.FRIENDLY_NAME, "cling4osgi Dimmable Light");
        descriptions.put(UPnPDevice.MANUFACTURER, "4thline.org");
        descriptions.put(UPnPDevice.MANUFACTURER_URL, "http://4thline.org/projects/cling4osgi");
        descriptions.put(
                UPnPDevice.MODEL_DESCRIPTION,
                "Dimmable light provides the following functionality: Switching " +
                        "the light source on or off; Changing the intensity of " +
                        "the light source in intermediate steps; Configuring and " +
                        "running ramping operations (optional); Providing definition " +
                        "of interaction between SwitchPower and Dimming service"
        );
        descriptions.put(UPnPDevice.MODEL_NAME, "Dimmer");
        descriptions.put(UPnPDevice.MODEL_NUMBER, "1.0");
        descriptions.put(UPnPDevice.MODEL_URL, "http://4thline.org/projects/cling4osgi");
        descriptions.put(UPnPDevice.SERIAL_NUMBER, "1000001");
        descriptions.put(UPnPDevice.TYPE, "urn:schemas-upnp-org:device:DimmableLight:1");
        descriptions.put(UPnPDevice.UDN, DEVICE_ID);
        descriptions.put(UPnPDevice.UPC, "012345678905");
        if (presentationURL != null) {
            descriptions.put(UPnPDevice.PRESENTATION_URL, presentationURL);
        }

        UPnPService service;

        List<UPnPService> list = new ArrayList<UPnPService>();

        service = new SwitchPowerService(this, light.getLightSwitch());
        list.add(service);
        index.put(service.getId(), service);

        service = new DimmingService(this, light.getDimmer());
        list.add(service);
        index.put(service.getId(), service);

        services = list.toArray(new UPnPService[list.size()]);
    }

    public DimmableLightDevice(DimmableLight light) {
        this(light, null);
    }

    public DimmableLightDevice() {
        this(new DimmableLight());
    }

    @Override
    public UPnPService getService(String id) {
        return index.get(id);
    }

    @Override
    public UPnPService[] getServices() {
        return services;
    }

    @Override
    public UPnPIcon[] getIcons(String locale) {
        return icons;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Dictionary getDescriptions(String locale) {
        return descriptions;
    }

}
