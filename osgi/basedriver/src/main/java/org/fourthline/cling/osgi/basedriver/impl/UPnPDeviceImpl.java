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

package org.fourthline.cling.osgi.basedriver.impl;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.osgi.service.upnp.UPnPDevice;
import org.osgi.service.upnp.UPnPIcon;
import org.osgi.service.upnp.UPnPService;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.ManufacturerDetails;
import org.fourthline.cling.model.meta.ModelDetails;
import org.fourthline.cling.model.meta.Service;

public class UPnPDeviceImpl implements UPnPDevice {
	private Device<?, ?, ?> device;
	private UPnPServiceImpl[] services;
	private Hashtable<String, UPnPService> servicesIndex;
	private UPnPIconImpl[] icons;
	private Dictionary<String, Object> descriptions = new Hashtable<String, Object>();
	
	public UPnPDeviceImpl(Device<?, ?, ?> device) {
		this.device = device;
		DeviceDetails deviceDetails = device.getDetails();
		ManufacturerDetails manufacturerDetails = deviceDetails.getManufacturerDetails();
		ModelDetails modelDetails = deviceDetails.getModelDetails();
		
		/* DEVICE CATEGORY */
		descriptions.put(
			org.osgi.service.device.Constants.DEVICE_CATEGORY,
			new String[]{ UPnPDevice.DEVICE_CATEGORY }
		);
		
		// mandatory properties
		if (!device.isRoot()) {
			Device<?, ?, ?> parent = device.getParentDevice();
			descriptions.put(UPnPDevice.PARENT_UDN, parent.getIdentity().getUdn().toString());
		}
		
		if (device.getEmbeddedDevices() != null) {
			List<String> list = new ArrayList<String>();
			
			for (Device<?, ?, ?> embedded : device.getEmbeddedDevices()) {
				list.add(embedded.getIdentity().getUdn().toString());
			}
			
			descriptions.put(UPnPDevice.CHILDREN_UDN, list.toArray(new String[list.size()]));
		}
		
		descriptions.put(UPnPDevice.FRIENDLY_NAME, deviceDetails.getFriendlyName());
		descriptions.put(UPnPDevice.MANUFACTURER, manufacturerDetails.getManufacturer());
		descriptions.put(UPnPDevice.TYPE, device.getType().toString());
		descriptions.put(UPnPDevice.UDN, device.getIdentity().getUdn().toString());
		
		// optional properties (but recommended)
		if (modelDetails.getModelDescription() != null) {
			descriptions.put(UPnPDevice.MODEL_DESCRIPTION, modelDetails.getModelDescription());
		}
		if (modelDetails.getModelNumber() != null) {
			descriptions.put(UPnPDevice.MODEL_NUMBER, modelDetails.getModelNumber());
		}
		if (deviceDetails.getPresentationURI() != null) {
			descriptions.put(UPnPDevice.PRESENTATION_URL, deviceDetails.getPresentationURI().toString());
		}
		if (deviceDetails.getSerialNumber() != null) {
			descriptions.put(UPnPDevice.SERIAL_NUMBER, deviceDetails.getSerialNumber());
		}
		
		// optional properties
		if (manufacturerDetails.getManufacturerURI() != null) {
			descriptions.put(UPnPDevice.MANUFACTURER_URL, manufacturerDetails.getManufacturerURI().toString());
		}
		if (modelDetails.getModelName() != null) {
			descriptions.put(UPnPDevice.MODEL_NAME, modelDetails.getModelName());
		}
		if (modelDetails.getModelURI() != null) {
			descriptions.put(UPnPDevice.MODEL_URL, modelDetails.getModelURI().toString());
		}
		if (deviceDetails.getUpc() != null) {
			descriptions.put(UPnPDevice.UPC, deviceDetails.getUpc());
		}
		
		if (device.getServices() != null && device.getServices().length != 0) {
			List<UPnPServiceImpl> list = new ArrayList<UPnPServiceImpl>();
			servicesIndex = new Hashtable<String, UPnPService>();
	
			for (Service<?, ?> service : device.getServices()) {
				UPnPServiceImpl item = new UPnPServiceImpl(service);
				list.add(item);
				servicesIndex.put(item.getId(), item);
			}
			
			services = list.toArray(new UPnPServiceImpl[list.size()]);
		}
		
		if (device.getIcons() != null && device.getIcons().length != 0) {
			List<UPnPIconImpl> list = new ArrayList<UPnPIconImpl>();
	
			for (Icon icon : device.getIcons()) {
				UPnPIconImpl item = new UPnPIconImpl(icon);
				list.add(item);
			}
			
			icons = list.toArray(new UPnPIconImpl[list.size()]);
		}
	}

	@Override
	public UPnPService getService(String serviceId) {
		return (servicesIndex != null) ? servicesIndex.get(serviceId) : null;
	}

	@Override
	public UPnPService[] getServices() {
		return services;
	}

	@Override
	public UPnPIcon[] getIcons(String locale) {
		return icons;
	}

	@Override
	public Dictionary<String, Object> getDescriptions(String locale) {
		return descriptions;
	}

	public Device<?, ?, ?> getDevice() {
		return device;
	}

}
