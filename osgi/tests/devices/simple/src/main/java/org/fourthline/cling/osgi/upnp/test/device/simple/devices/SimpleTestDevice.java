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

package org.fourthline.cling.osgi.upnp.test.device.simple.devices;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.osgi.service.upnp.UPnPDevice;
import org.osgi.service.upnp.UPnPIcon;
import org.osgi.service.upnp.UPnPService;
import org.fourthline.cling.osgi.upnp.test.device.simple.icons.SimpleTestDeviceIcon;
import org.fourthline.cling.osgi.upnp.test.device.simple.model.Simple;
import org.fourthline.cling.osgi.upnp.test.device.simple.services.SimpleTestService;


public class SimpleTestDevice extends BaseUPnPDevice {
	static private final String SIMPLE_TEST_DEVICE_ID = String.format("SIMPLE-TEST-DEVICE-%s", UUID.randomUUID());
	static private final String SIMPLE_TEST_DEVICE_TYPE = "urn:schemas-4thline-com:device:simple-test:1";
	
	private UPnPService[] services;
	private Map<String, UPnPService> index = new HashMap<String, UPnPService>();
	private UPnPIcon[] icons = { 
			new SimpleTestDeviceIcon("image/png",  50,  50, 32,  6243, "images/upnp-icon-small.png"),
			new SimpleTestDeviceIcon("image/png", 100, 100, 32, 14812, "images/upnp-icon-large.png"),
	};

	public SimpleTestDevice(Simple simple) {
		Dictionary<String, Object> descriptions = getDescriptions(null);
		descriptions.put(UPnPDevice.UPNP_EXPORT, "");
		descriptions.put(
		        org.osgi.service.device.Constants.DEVICE_CATEGORY,
	        	new String[]{ UPnPDevice.DEVICE_CATEGORY }
	        );
		descriptions.put(UPnPDevice.FRIENDLY_NAME, "OSGi-UPnP Simple Test Device"); 
		descriptions.put(UPnPDevice.MANUFACTURER, "4th Line");
		descriptions.put(UPnPDevice.MANUFACTURER_URL, "http://4thline.org/projects/cling");
		descriptions.put(UPnPDevice.MODEL_DESCRIPTION,"A test OSGi UPnP device.");
		descriptions.put(UPnPDevice.MODEL_NAME, "Simple Test"); 
		descriptions.put(UPnPDevice.MODEL_NUMBER, "1.0"); 
		descriptions.put(UPnPDevice.MODEL_URL, "http://4thline.org/projects/cling");
		descriptions.put(UPnPDevice.SERIAL_NUMBER, "1000001");
		descriptions.put(UPnPDevice.TYPE, SIMPLE_TEST_DEVICE_TYPE);
		descriptions.put(UPnPDevice.UDN , SIMPLE_TEST_DEVICE_ID); 
		descriptions.put(UPnPDevice.UPC , "012345678905");

		UPnPService service;
		
		List<UPnPService> list = new ArrayList<UPnPService>();
		
		service = new SimpleTestService(this, simple);
		list.add(service);
		index.put(service.getId(), service);
		
		services = list.toArray(new UPnPService[list.size()]);
	}
	
	public SimpleTestDevice() {
		this(new Simple());
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
}
