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

package org.fourthline.cling.osgi.basedriver.discover;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.upnp.UPnPDevice;
import org.osgi.service.upnp.UPnPEventListener;
import org.osgi.util.tracker.ServiceTracker;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.osgi.basedriver.impl.UPnPDeviceImpl;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;

import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Monitors and handles the addition and removal of remote devices.
 * <p>
 * When a device is added:
 * </p>
 * <ul>
 * <li>Wrap the device inside of a UPnPDevice implementation.</li>
 * <li>Create and open a UPnPEventListener tracker for that device.</li>
 * <li>Register the new UPnPDevice with the OSGi Framework.</li>
 * </ul>
 * <p>
 * When a device is removed:
 * </p>
 * <ul>
 * <li>Unregister the UPnPDevice with the OSGi Framework.</li>
 * <li>Close the UPnPEventListener tracker for that device.</li>
 * </ul>
 *
 * @author Bruce Green
 */
class ClingRegistryListener extends DefaultRegistryListener {

    private static final Logger log = Logger.getLogger(ClingRegistryListener.class.getName());

    private Map<Device, UPnPDeviceBinding> deviceBindings = new Hashtable<Device, UPnPDeviceBinding>();
    private BundleContext context;
    private UpnpService upnpService;

    class UPnPDeviceBinding {
        private ServiceRegistration reference;
        private ServiceTracker tracker;

        UPnPDeviceBinding(ServiceRegistration reference, ServiceTracker tracker) {
            this.reference = reference;
            this.tracker = tracker;
        }

        public ServiceRegistration getServiceRegistration() {
            return reference;
        }

        public ServiceTracker getServiceTracker() {
            return tracker;
        }
    }

    public ClingRegistryListener(BundleContext context, UpnpService upnpService) {
        this.context = context;
        this.upnpService = upnpService;
    }

    /*
      * When an external device is discovered wrap it with UPnPDeviceImpl,
      * create a tracker for any listener to this device or its services,
      * and register the UPnPDevice.
      */
    @Override
    public void deviceAdded(Registry registry, @SuppressWarnings("rawtypes") Device device) {
        log.entering(this.getClass().getName(), "deviceAdded", new Object[]{registry, device});

        UPnPDeviceImpl upnpDevice = new UPnPDeviceImpl(device);
        if (device instanceof RemoteDevice) {
            String string = String.format("(%s=%s)",
                                          Constants.OBJECTCLASS, UPnPEventListener.class.getName()
            );
            try {
                Filter filter = context.createFilter(string);
                UPnPEventListenerTracker tracker = new UPnPEventListenerTracker(context, filter, upnpService, upnpDevice);
                tracker.open();

                ServiceRegistration registration = context.registerService(UPnPDevice.class.getName(), upnpDevice, upnpDevice.getDescriptions(null));
                deviceBindings.put(device, new UPnPDeviceBinding(registration, tracker));
            } catch (InvalidSyntaxException e) {
                log.severe(String.format("Cannot add remote device (%s).", device.getIdentity().getUdn().toString()));
                log.severe(e.getMessage());
            }
        }
    }

    @Override
    public void deviceRemoved(Registry registry, @SuppressWarnings("rawtypes") Device device) {
        log.entering(this.getClass().getName(), "deviceRemoved", new Object[]{registry, device});

        if (device instanceof RemoteDevice) {
            UPnPDeviceBinding data = deviceBindings.get(device);
            if (data == null) {
                log.warning(String.format("Unknown device %s removed.", device.getIdentity().getUdn().toString()));
            } else {
                data.getServiceRegistration().unregister();
                data.getServiceTracker().close();
                deviceBindings.remove(device);
            }
        }
    }
}
