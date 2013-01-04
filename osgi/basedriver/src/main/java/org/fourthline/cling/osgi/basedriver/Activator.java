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

package org.fourthline.cling.osgi.basedriver;

import java.util.logging.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.osgi.basedriver.discover.UPnPDiscover;
import org.fourthline.cling.osgi.basedriver.present.UPnPPresent;

/**
 * @author Bruce Green
 */
public class Activator implements BundleActivator {

    final private static Logger log = Logger.getLogger(Activator.class.getName());

    private static Activator plugin;
    private BundleContext context;
    private UpnpService upnpService;
    private UPnPPresent present;
    private UPnPDiscover discover;

    public static Activator getPlugin() {
        return plugin;
    }

    public BundleContext getContext() {
        return context;
    }

    public UpnpService getUpnpService() {
        return upnpService;
    }

    @Override
    public void start(BundleContext context) throws Exception {
        Activator.plugin = this;
        this.context = context;

        upnpService = new UpnpServiceImpl(new ApacheUpnpServiceConfiguration());
        discover = new UPnPDiscover(context, upnpService);
        present = new UPnPPresent(context, upnpService);
        upnpService.getControlPoint().search();
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        upnpService.shutdown();
    }
}
