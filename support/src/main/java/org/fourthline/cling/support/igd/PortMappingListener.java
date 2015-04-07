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

package org.fourthline.cling.support.igd;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.support.igd.callback.PortMappingAdd;
import org.fourthline.cling.support.igd.callback.PortMappingDelete;
import org.fourthline.cling.support.model.PortMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Maintains UPnP port mappings on an InternetGatewayDevice automatically.
 * <p>
 * This listener will wait for discovered devices which support either
 * {@code WANIPConnection} or the {@code WANPPPConnection} service. As soon as any such
 * service is discovered, the desired port mapping will be created. When the UPnP service
 * is shutting down, all previously established port mappings with all services will
 * be deleted.
 * </p>
 * <p>
 * The following listener maps external WAN TCP port 8123 to internal host 10.0.0.2:
 * </p>
 * <pre>{@code
 * upnpService.getRegistry().addListener(
 *newPortMappingListener(newPortMapping(8123, "10.0.0.2",PortMapping.Protocol.TCP))
 * );}</pre>
 * <p>
 * If all you need from the Cling UPnP stack is NAT port mapping, use the following idiom:
 * </p>
 * <pre>{@code
 * UpnpService upnpService = new UpnpServiceImpl(
 *     new PortMappingListener(new PortMapping(8123, "10.0.0.2", PortMapping.Protocol.TCP))
 * );
 * <p/>
 * upnpService.getControlPoint().search(new STAllHeader()); // Search for all devices
 * <p/>
 * upnpService.shutdown(); // When you no longer need the port mapping
 * }</pre>
 *
 * @author Christian Bauer
 */
public class PortMappingListener extends DefaultRegistryListener {

    private static final Logger log = Logger.getLogger(PortMappingListener.class.getName());

    public static final DeviceType IGD_DEVICE_TYPE = new UDADeviceType("InternetGatewayDevice", 1);
    public static final DeviceType CONNECTION_DEVICE_TYPE = new UDADeviceType("WANConnectionDevice", 1);

    public static final ServiceType IP_SERVICE_TYPE = new UDAServiceType("WANIPConnection", 1);
    public static final ServiceType PPP_SERVICE_TYPE = new UDAServiceType("WANPPPConnection", 1);

    protected PortMapping[] portMappings;

    // The key of the map is Service and equality is object identity, this is by-design
    protected Map<Service, List<PortMapping>> activePortMappings = new HashMap<>();

    public PortMappingListener(PortMapping portMapping) {
        this(new PortMapping[]{portMapping});
    }

    public PortMappingListener(PortMapping[] portMappings) {
        this.portMappings = portMappings;
    }

    @Override
    synchronized public void deviceAdded(Registry registry, Device device) {

        Service connectionService;
        if ((connectionService = discoverConnectionService(device)) == null) return;

        log.fine("Activating port mappings on: " + connectionService);

        final List<PortMapping> activeForService = new ArrayList<>();
        for (final PortMapping pm : portMappings) {
            new PortMappingAdd(connectionService, registry.getUpnpService().getControlPoint(), pm) {

                @Override
                public void success(ActionInvocation invocation) {
                    log.fine("Port mapping added: " + pm);
                    activeForService.add(pm);
                }

                @Override
                public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                    handleFailureMessage("Failed to add port mapping: " + pm);
                    handleFailureMessage("Reason: " + defaultMsg);
                }
            }.run(); // Synchronous!
        }

        activePortMappings.put(connectionService, activeForService);
    }

    @Override
    synchronized public void deviceRemoved(Registry registry, Device device) {
        for (Service service : device.findServices()) {
            Iterator<Map.Entry<Service, List<PortMapping>>> it = activePortMappings.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Service, List<PortMapping>> activeEntry = it.next();
                if (!activeEntry.getKey().equals(service)) continue;

                if (activeEntry.getValue().size() > 0)
                    handleFailureMessage("Device disappeared, couldn't delete port mappings: " + activeEntry.getValue().size());

                it.remove();
            }
        }
    }

    @Override
    synchronized public void beforeShutdown(Registry registry) {
        for (Map.Entry<Service, List<PortMapping>> activeEntry : activePortMappings.entrySet()) {

            final Iterator<PortMapping> it = activeEntry.getValue().iterator();
            while (it.hasNext()) {
                final PortMapping pm = it.next();
                log.fine("Trying to delete port mapping on IGD: " + pm);
                new PortMappingDelete(activeEntry.getKey(), registry.getUpnpService().getControlPoint(), pm) {

                    @Override
                    public void success(ActionInvocation invocation) {
                        log.fine("Port mapping deleted: " + pm);
                        it.remove();
                    }

                    @Override
                    public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                        handleFailureMessage("Failed to delete port mapping: " + pm);
                        handleFailureMessage("Reason: " + defaultMsg);
                    }

                }.run(); // Synchronous!
            }
        }
    }

    protected Service discoverConnectionService(Device device) {
        if (!device.getType().equals(IGD_DEVICE_TYPE)) {
            return null;
        }

        Device[] connectionDevices = device.findDevices(CONNECTION_DEVICE_TYPE);
        if (connectionDevices.length == 0) {
            log.fine("IGD doesn't support '" + CONNECTION_DEVICE_TYPE + "': " + device);
            return null;
        }

        Device connectionDevice = connectionDevices[0];
        log.fine("Using first discovered WAN connection device: " + connectionDevice);

        Service ipConnectionService = connectionDevice.findService(IP_SERVICE_TYPE);
        Service pppConnectionService = connectionDevice.findService(PPP_SERVICE_TYPE);

        if (ipConnectionService == null && pppConnectionService == null) {
            log.fine("IGD doesn't support IP or PPP WAN connection service: " + device);
        }

        return ipConnectionService != null ? ipConnectionService : pppConnectionService;
    }

    protected void handleFailureMessage(String s) {
        log.warning(s);
    }

}

