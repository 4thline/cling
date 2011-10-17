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

package org.fourthline.cling.osgi.device.light.devices;

import org.osgi.service.upnp.UPnPDevice;
import org.osgi.service.upnp.UPnPIcon;
import org.osgi.service.upnp.UPnPService;
import org.fourthline.cling.osgi.device.light.icons.LightDeviceIcon;
import org.fourthline.cling.osgi.device.light.model.BinaryLight;
import org.fourthline.cling.osgi.device.light.services.SwitchPowerService;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Bruce Green
 */
public class BinaryLightDevice implements UPnPDevice {

    final private String DEVICE_ID = UUID.randomUUID().toString();

    private Dictionary<String, Object> descriptions;
    private UPnPService[] services;
    private Map<String, UPnPService> index = new HashMap<String, UPnPService>();
    private UPnPIcon icon = new LightDeviceIcon();
    private UPnPIcon[] icons = {icon};

    public BinaryLightDevice(BinaryLight light, String presentationURL) {
        descriptions = new Hashtable<String, Object>();
        descriptions.put(UPnPDevice.UPNP_EXPORT, "");
        descriptions.put(
                org.osgi.service.device.Constants.DEVICE_CATEGORY,
                new String[]{UPnPDevice.DEVICE_CATEGORY}
        );
        descriptions.put(UPnPDevice.FRIENDLY_NAME, "cling4osgi Binary Light");
        descriptions.put(UPnPDevice.MANUFACTURER, "4thline.org");
        descriptions.put(UPnPDevice.MANUFACTURER_URL, "http://4thline.org/projects/cling4osgi");
        descriptions.put(
                UPnPDevice.MODEL_DESCRIPTION,
                "A simple representation of a bulb or any " +
                        "other kind of light emitting device " +
                        "that can be switched completely on or off."
        );
        descriptions.put(UPnPDevice.MODEL_NAME, "Edison");
        descriptions.put(UPnPDevice.MODEL_NUMBER, "1.0");
        descriptions.put(UPnPDevice.MODEL_URL, "http://4thline.org/projects/cling4osgi");
        descriptions.put(UPnPDevice.SERIAL_NUMBER, "1000001");
        descriptions.put(UPnPDevice.TYPE, "urn:schemas-upnp-org:device:BinaryLight:1");
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

        services = list.toArray(new UPnPService[list.size()]);
    }

    public BinaryLightDevice(BinaryLight light) {
        this(light, null);
    }

    public BinaryLightDevice() {
        this(new BinaryLight());
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
