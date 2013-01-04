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

package org.fourthline.cling.test.data;

import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.message.OutgoingDatagramMessage;
import org.fourthline.cling.model.message.UpnpMessage;
import org.fourthline.cling.model.message.UpnpOperation;
import org.fourthline.cling.model.message.header.DeviceTypeHeader;
import org.fourthline.cling.model.message.header.DeviceUSNHeader;
import org.fourthline.cling.model.message.header.RootDeviceHeader;
import org.fourthline.cling.model.message.header.ServiceTypeHeader;
import org.fourthline.cling.model.message.header.ServiceUSNHeader;
import org.fourthline.cling.model.message.header.UDNHeader;
import org.fourthline.cling.model.message.header.USNRootDeviceHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Christian Bauer
 */
public class SampleUSNHeaders {

    public static void assertUSNHeaders(List<OutgoingDatagramMessage> msgs, LocalDevice rootDevice, LocalDevice embeddedDevice, UpnpHeader.Type ntstHeaderType) {

        // See the tables in UDA 1.0 section 1.1.2

        boolean gotRootDeviceFirstMsg = false;
        boolean gotRootDeviceSecondMsg = false;
        boolean gotRootDeviceThirdMsg = false;

        boolean gotEmbeddedDeviceFirstMsg = false;
        boolean gotEmbeddedDeviceSecondMsg = false;

        boolean gotFirstServiceMsg = false;
        boolean gotSecondServiceMsg = false;

        for (UpnpMessage<UpnpOperation> msg : msgs) {

            if (msg.getHeaders().getFirstHeader(ntstHeaderType, RootDeviceHeader.class) != null) {
                assertEquals(
                        msg.getHeaders().getFirstHeader(UpnpHeader.Type.USN, USNRootDeviceHeader.class).getString(),
                        new USNRootDeviceHeader(rootDevice.getIdentity().getUdn()).getString()
                );
                gotRootDeviceFirstMsg = true;
            }

            UDNHeader foundUDN = msg.getHeaders().getFirstHeader(ntstHeaderType, UDNHeader.class);
            if (foundUDN != null && foundUDN.getString().equals(new UDNHeader(rootDevice.getIdentity().getUdn()).getString())) {
                assertEquals(
                        msg.getHeaders().getFirstHeader(ntstHeaderType).getString(),
                        msg.getHeaders().getFirstHeader(UpnpHeader.Type.USN).getString()
                );
                gotRootDeviceSecondMsg = true;
            }

            if (foundUDN != null && foundUDN.getString().equals(new UDNHeader(embeddedDevice.getIdentity().getUdn()).getString())) {
                assertEquals(
                        msg.getHeaders().getFirstHeader(ntstHeaderType).getString(),
                        msg.getHeaders().getFirstHeader(UpnpHeader.Type.USN).getString()
                );
                gotEmbeddedDeviceFirstMsg = true;

            }

            DeviceTypeHeader foundDeviceNTST = msg.getHeaders().getFirstHeader(ntstHeaderType, DeviceTypeHeader.class);
            if (foundDeviceNTST != null && foundDeviceNTST.getString().equals(new DeviceTypeHeader(rootDevice.getType()).getString())) {
                assertEquals(
                        msg.getHeaders().getFirstHeader(UpnpHeader.Type.USN, DeviceUSNHeader.class).getString(),
                        new DeviceUSNHeader(rootDevice.getIdentity().getUdn(), rootDevice.getType()).getString()
                );
                gotRootDeviceThirdMsg = true;
            }

            if (foundDeviceNTST != null && foundDeviceNTST.getString().equals(new DeviceTypeHeader(embeddedDevice.getType()).getString())) {
                assertEquals(
                        msg.getHeaders().getFirstHeader(UpnpHeader.Type.USN, DeviceUSNHeader.class).getString(),
                        new DeviceUSNHeader(embeddedDevice.getIdentity().getUdn(), embeddedDevice.getType()).getString()
                );
                gotEmbeddedDeviceSecondMsg = true;
            }

            ServiceTypeHeader foundServiceNTST = msg.getHeaders().getFirstHeader(ntstHeaderType, ServiceTypeHeader.class);
            if (foundServiceNTST != null && foundServiceNTST.getString().equals(new ServiceTypeHeader(SampleServiceOne.getThisServiceType()).getString())) {
                assertEquals(
                        msg.getHeaders().getFirstHeader(UpnpHeader.Type.USN, ServiceUSNHeader.class).getString(),
                        new ServiceUSNHeader(rootDevice.getIdentity().getUdn(), SampleServiceOne.getThisServiceType()).getString()
                );
                gotFirstServiceMsg = true;
            }

            if (foundServiceNTST != null && foundServiceNTST.getString().equals(new ServiceTypeHeader(SampleServiceTwo.getThisServiceType()).getString())) {
                assertEquals(
                        msg.getHeaders().getFirstHeader(UpnpHeader.Type.USN, ServiceUSNHeader.class).getString(),
                        new ServiceUSNHeader(rootDevice.getIdentity().getUdn(), SampleServiceTwo.getThisServiceType()).getString()
                );
                gotSecondServiceMsg = true;
            }
        }

        assertTrue(gotRootDeviceFirstMsg);
        assertTrue(gotRootDeviceSecondMsg);
        assertTrue(gotRootDeviceThirdMsg);

        assertTrue(gotEmbeddedDeviceFirstMsg);
        assertTrue(gotEmbeddedDeviceSecondMsg);

        assertTrue(gotFirstServiceMsg);
        assertTrue(gotSecondServiceMsg);

    }
}
