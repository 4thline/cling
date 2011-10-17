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
