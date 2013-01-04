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
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.service.upnp.UPnPEventListener;
import org.osgi.service.upnp.UPnPService;
import org.osgi.util.tracker.ServiceTracker;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.osgi.basedriver.impl.UPnPDeviceImpl;
import org.fourthline.cling.osgi.basedriver.impl.UPnPServiceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Monitors and handles addition/removal of UPnPEventListeners for a device.
 * <p>
 * A UPnPEventListener can listen to all devices, some devices,
 * some device and some services, etc.
 * </p>
 * <p>
 * When a UPnPEventListener is registered this tracker will
 * determine if the device it represents applies to this
 * listener. If it does this tracker will establish then
 * necessary subscription with the device and relay its
 * events back to the listener via a callback. A listener
 * may have one or more callbacks associated with it.
 * </p>
 *
 * @author Bruce Green
 */
class UPnPEventListenerTracker extends ServiceTracker {

    final private static Logger log = Logger.getLogger(UPnPEventListenerTracker.class.getName());

    private UpnpService upnpService;
    private UPnPDeviceImpl device;
    private Map<ServiceReference, List<SubscriptionCallback>> listenerCallbacks = new Hashtable();

    public UPnPEventListenerTracker(BundleContext context, Filter filter, UpnpService upnpService, UPnPDeviceImpl device) {
        super(context, filter, null);
        log.entering(this.getClass().getName(), "<init>", new Object[]{context, filter, upnpService, device});
        this.upnpService = upnpService;
        this.device = device;
    }

    @Override
    public Object addingService(ServiceReference reference) {
        log.entering(this.getClass().getName(), "addingService", new Object[]{reference});
        final UPnPEventListener listener = (UPnPEventListener) super.addingService(reference);

        Filter filter = (Filter) reference.getProperty(UPnPEventListener.UPNP_FILTER);
        if (filter != null) {
            List<SubscriptionCallback> callbacks = new ArrayList<SubscriptionCallback>();
            UPnPServiceImpl[] services = (UPnPServiceImpl[]) device.getServices();
            if (services != null) {
                Dictionary descriptions = device.getDescriptions(null);
                boolean all = filter.match(descriptions);

                if (all) {
                    log.finer(String.format(
                            "Matched UPnPEvent listener for device %s service: ALL.",
                            device.getDevice().getIdentity().getUdn().toString()
                    ));
                }

                for (UPnPServiceImpl service : services) {
                    boolean match = all;

                    if (!match) {
                        Dictionary dictionary = new Hashtable();
                        for (Object key : Collections.list(descriptions.keys())) {
                            dictionary.put(key, descriptions.get(key));
                        }
                        dictionary.put(UPnPService.ID, service.getId());
                        dictionary.put(UPnPService.TYPE, service.getType());
                        match = filter.match(dictionary);
                        if (match) {
                            log.finer(String.format(
                                    "Matched UPnPEvent listener for device %s service: %s.",
                                    device.getDevice().getIdentity().getUdn().toString(), service.getId()
                            ));
                        }
                    }

                    if (match) {
                        log.finer(String.format(
                                "Creating subscription callback for device %s service: %s.",
                                device.getDevice().getIdentity().getUdn().toString(), service.getId()
                        ));
                        SubscriptionCallback callback = new UPnPEventListenerSubscriptionCallback(device, service, listener);
                        upnpService.getControlPoint().execute(callback);
                        callbacks.add(callback);
                    }
                }
            }

            listenerCallbacks.put(reference, callbacks);
        }

        return listener;
    }

    @Override
    public void removedService(ServiceReference reference, Object service) {
        log.entering(this.getClass().getName(), "removedService", new Object[]{reference, service});

        List<SubscriptionCallback> callbacks = listenerCallbacks.get(reference);
        if (callbacks != null) {
            for (SubscriptionCallback callback : callbacks) {
                // TODO: callbacks are executed ... don't know how to remove them
            }
        }

        listenerCallbacks.remove(reference);
        super.removedService(reference, service);
    }
}
