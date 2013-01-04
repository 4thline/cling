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

package org.fourthline.cling.demo.osgi.device.basic;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.upnp.UPnPDevice;

/**
 * @author Bruce Green
 */
public class Activator implements BundleActivator {

    private static BundleContext context;
    private ServiceReference reference;

    static BundleContext getContext() {
        return context;
    }


    @Override
    public void start(BundleContext bundleContext) throws Exception {
        Activator.context = bundleContext;
        UPnPDevice device = new UPnPBasicDevice();
        ServiceRegistration registration =
                context.registerService(UPnPDevice.class.getName(), device, device.getDescriptions(null));
        reference = registration.getReference();
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        context.ungetService(reference);
        Activator.context = null;
    }

}
