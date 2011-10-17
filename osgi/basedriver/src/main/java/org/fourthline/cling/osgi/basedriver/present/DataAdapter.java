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

package org.fourthline.cling.osgi.basedriver.present;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.upnp.UPnPDevice;
import org.osgi.service.upnp.UPnPEventListener;
import org.osgi.service.upnp.UPnPService;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.osgi.basedriver.Activator;

import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Bruce Green
 */
public class DataAdapter implements UPnPEventListener {

    final private static Logger log = Logger.getLogger(DataAdapter.class.getName());

    private PropertyChangeSupport propertyChangeSupport;

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    public DataAdapter(LocalService<LocalDevice> service) {
        propertyChangeSupport = new PropertyChangeSupport(this);
        LocalDevice device = service.getDevice();
        String string = String.format(
                "(&(%s=%s)(%s=%s))",
                UPnPDevice.UDN, device.getIdentity().getUdn().getIdentifierString(),
                UPnPService.ID, service.getServiceId()
        );
        log.finer(String.format("filter: %s", string));

        try {
            BundleContext context = Activator.getPlugin().getContext();
            Filter filter = context.createFilter(string);

            Dictionary<String, Object> properties = new Hashtable<String, Object>();
            properties.put(UPnPEventListener.UPNP_FILTER, filter);
            context.registerService(UPnPEventListener.class.getName(), this, properties);
        } catch (InvalidSyntaxException e) {
            log.severe(String.format("Cannot create DataAdapter (%s).", service.getServiceId()));
            log.severe(e.getMessage());
        }
    }

    @Override
    public void notifyUPnPEvent(String deviceId, String serviceId, Dictionary events) {
        log.entering(this.getClass().getName(), "notifyUPnPEvent", new Object[]{deviceId, serviceId, events});

        for (String key : (List<String>) Collections.list(events.keys())) {
            Object value = events.get(key);
            propertyChangeSupport.firePropertyChange(key, null, value);
        }
    }
}
