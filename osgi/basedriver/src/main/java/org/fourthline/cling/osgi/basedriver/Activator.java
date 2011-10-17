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
