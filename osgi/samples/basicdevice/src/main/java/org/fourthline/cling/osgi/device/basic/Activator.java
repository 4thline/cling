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

package org.fourthline.cling.osgi.device.basic;

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
