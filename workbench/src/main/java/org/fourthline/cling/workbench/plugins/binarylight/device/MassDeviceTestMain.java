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

package org.fourthline.cling.workbench.plugins.binarylight.device;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.types.UDN;

/**
 * Starts a UPnP service, creates and registers many BinaryLight devices.
 *
 * @author Christian Bauer
 */
public class MassDeviceTestMain {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Cling...");
        UpnpService upnpService = new UpnpServiceImpl();

        long noOfDevices = args.length == 1 ? Long.valueOf(args[0]) : 10;
        System.out.println("Registering BinaryLight devices: " + noOfDevices);

        for (long i = 0; i <= noOfDevices; i++) {
            LocalService service = new AnnotationLocalServiceBinder().read(DemoBinaryLight.class);
            service.setManager(new DefaultServiceManager(service, DemoBinaryLight.class));
            upnpService.getRegistry().addDevice(
                    DemoBinaryLight.createDefaultDevice(UDN.uniqueSystemIdentifier(Long.toString(i)), "Device: " + i, service)
            );
        }
        System.out.println("Completed device registration");
    }
}
