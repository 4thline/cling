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

package org.fourthline.cling.osgi.upnp.test.device.simple;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.upnp.UPnPDevice;
import org.osgi.util.tracker.ServiceTracker;
import org.fourthline.cling.osgi.upnp.test.device.simple.devices.BaseUPnPDevice;
import org.fourthline.cling.osgi.upnp.test.device.simple.devices.SimpleTestDevice;
import org.fourthline.cling.osgi.upnp.test.device.simple.model.Simple;

public class Activator implements BundleActivator {
	private static Activator plugin;
	private static BundleContext context;
	private ServiceTracker tracker;
	private List<ServiceReference> references = new ArrayList<ServiceReference>();

	public static Activator getPlugin() {
		return plugin;
	}

	static BundleContext getContext() {
		return context;
	}

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		Activator.plugin = this;
		Activator.context = bundleContext;
		String string = String.format("(%s=%s)",
				Constants.OBJECTCLASS , EventAdmin.class.getName()
				);
		try {
			Filter filter = context.createFilter(string);
		
			tracker = new ServiceTracker(context, filter, null);
			tracker.open();
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
		
		BaseUPnPDevice simpleTestDevice = new SimpleTestDevice(new Simple());

		ServiceReference reference;
		ServiceRegistration registration;
		
		registration = context.registerService(UPnPDevice.class.getName(), simpleTestDevice, simpleTestDevice.getDescriptions(null));
		reference = registration.getReference();
		references.add(reference);
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		for (ServiceReference reference : references) {
			context.ungetService(reference);
		}
		Activator.context = null;
	}

	public EventAdmin getEventAdmin() {
		return (EventAdmin) tracker.getService();
	}
}
