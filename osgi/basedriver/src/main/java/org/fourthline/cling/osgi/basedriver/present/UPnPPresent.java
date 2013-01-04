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

package org.fourthline.cling.osgi.basedriver.present;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.upnp.UPnPDevice;
import org.fourthline.cling.UpnpService;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.logging.Logger;

/**
 * 111.2.1 UPnP Base Driver
 * <p>
 * The functionality of the UPnP service is implemented in a UPnP base driver.
 * This is a bundle that implements the UPnP protocols and handles the interaction
 * with bundles that use the UPnP devices. A UPnP base driver bundle
 * must provide the following functions:
 * </p>
 * <ul>
 * <li>Discover UPnP devices on the network and map each discovered device
 * into an OSGi registered UPnP Device service.</li>
 * <li>Present UPnP marked services that are registered with the OSGi
 * Framework on one or more networks to be used by other computers.
 * </li>
 * </ul>
 * <p>
 * UPnPPresent tracks UPnPDevice services registered for export. When a service
 * is registered/unregistered  UPnPPresent will add/remove it with Cling.
 * </p>
 * <p>
 * When a service changes a state variable that sends events UPnPPresent will
 * send that change to external listeners.
 * </p>
 *
 * @author Bruce Green
 */
public class UPnPPresent {

    final private static Logger log = Logger.getLogger(UPnPPresent.class.getName());

    private static final String UPNP_EVENT_TOPIC = "org/osgi/service/upnp/UPnPEvent";
    private UPnPDeviceTracker deviceTracker;

    public UPnPPresent(BundleContext context, UpnpService upnpService) {
        /*
           * Track all UPnPDevices registered for export.
           */
        String string = String.format("(&(%s=%s)(%s=%s))",
                                      Constants.OBJECTCLASS, UPnPDevice.class.getName(),
                                      UPnPDevice.UPNP_EXPORT, "*");
        try {
            Filter filter = context.createFilter(string);

            deviceTracker = new UPnPDeviceTracker(context, upnpService, filter);
            deviceTracker.open();
        } catch (InvalidSyntaxException e) {
            log.severe("Cannot create UPnPDevice tracker.");
            log.severe("Cannot export UPnPDevices.");
            log.severe(e.getMessage());
        }

        /*
           * Track OSGi UPnP events. Local devices fire a UPnP event when
           * a state variable that sends an event when changed.
           */
        Dictionary<String, String> properties = new Hashtable<String, String>();
        properties.put(EventConstants.EVENT_TOPIC, UPNP_EVENT_TOPIC);
        context.registerService(
                EventHandler.class.getName(),
                new UPnPEventHandler(context),
                properties
        );
    }
}
