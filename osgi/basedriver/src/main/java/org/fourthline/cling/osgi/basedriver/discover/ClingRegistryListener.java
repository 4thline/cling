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
 * <li>Create and open a UPnPEventListner tracker for that device.</li>
 * <li>Register the new UPnPDevice with the OSGi Framework.</li>
 * </ul>
 * <p>
 * When a device is removed:
 * </p>
 * <ul>
 * <li>Unregister the UPnPDevice with the OSGi Framework.</li>
 * <li>Close the UPnPEventListner tracker for that device.</li>
 * </ul>
 *
 * @author Bruce Green
 */
class ClingRegistryListener extends DefaultRegistryListener {

    private static final Logger log = Logger.getLogger(ClingRegistryListener.class.getName());

    private Map<Device, UPnPDeviceBinding> deviceBindings = new Hashtable();
    private BundleContext context;
    private UpnpService upnpService;

    class UPnPDeviceBinding {
        private ServiceReference reference;
        private ServiceTracker tracker;

        UPnPDeviceBinding(ServiceReference reference, ServiceTracker tracker) {
            this.reference = reference;
            this.tracker = tracker;
        }

        public ServiceReference getServiceReference() {
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
                deviceBindings.put(device, new UPnPDeviceBinding(registration.getReference(), tracker));
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
                context.ungetService(data.getServiceReference());
                data.getServiceTracker().close();
                deviceBindings.remove(device);
            }
        }
    }
}
