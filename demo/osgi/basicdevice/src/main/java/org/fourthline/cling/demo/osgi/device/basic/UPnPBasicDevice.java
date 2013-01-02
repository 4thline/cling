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

package org.fourthline.cling.demo.osgi.device.basic;

import org.osgi.service.upnp.UPnPDevice;
import org.osgi.service.upnp.UPnPIcon;
import org.osgi.service.upnp.UPnPService;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.UUID;

/**
 * @author Bruce Green
 */
public class UPnPBasicDevice implements UPnPDevice {

    final private String DEVICE_ID = UUID.randomUUID().toString();

    private Dictionary<String, Object> descriptions;
    private UPnPIcon icon = new UPnPBasicDeviceIcon();
    private UPnPIcon[] icons = new UPnPIcon[]{icon};

    public UPnPBasicDevice() {
        descriptions = new Hashtable<String, Object>();
        descriptions.put(UPnPDevice.UPNP_EXPORT, "");
        descriptions.put(
                org.osgi.service.device.Constants.DEVICE_CATEGORY,
                new String[]{UPnPDevice.DEVICE_CATEGORY}
        );
        descriptions.put(UPnPDevice.FRIENDLY_NAME, "Basic Device");
        descriptions.put(UPnPDevice.MANUFACTURER, "4thline.org");
        descriptions.put(
                UPnPDevice.MANUFACTURER_URL,
                "http://4thline.org/projects/cling4osgi"
        );
        descriptions.put(
                UPnPDevice.MODEL_DESCRIPTION,
                "Basic Device provides a mechanism for products that " +
                        "wish to use UPnP, but for which there is not" +
                        " yet an appropriate standard base device type."
        );
        descriptions.put(UPnPDevice.MODEL_NAME, "Basic");
        descriptions.put(UPnPDevice.MODEL_NUMBER, "1");
        descriptions.put(UPnPDevice.MODEL_URL, "http://4thline.org/projects/cling4osgi");
        descriptions.put(UPnPDevice.SERIAL_NUMBER, "1000001");
        descriptions.put(UPnPDevice.TYPE, "urn:schemas-upnp-org:device:Basic:1.0");
        descriptions.put(UPnPDevice.UDN, DEVICE_ID);
        descriptions.put(UPnPDevice.UPC, "012345678905");
    }

    @Override
    public UPnPService getService(String serviceId) {
        return null;
    }

    @Override
    public UPnPService[] getServices() {
        return null;
    }

    @Override
    public UPnPIcon[] getIcons(String locale) {
        return icons;
    }

    @Override
    public Dictionary getDescriptions(String locale) {
        return descriptions;
    }

}
