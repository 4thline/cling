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

package org.fourthline.cling.test.ssdp;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.mock.MockUpnpService;
import org.fourthline.cling.model.Constants;
import org.fourthline.cling.model.message.IncomingDatagramMessage;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.model.message.discovery.IncomingNotificationRequest;
import org.fourthline.cling.model.message.header.HostHeader;
import org.fourthline.cling.model.message.header.LocationHeader;
import org.fourthline.cling.model.message.header.MaxAgeHeader;
import org.fourthline.cling.model.message.header.NTSHeader;
import org.fourthline.cling.model.message.header.RootDeviceHeader;
import org.fourthline.cling.model.message.header.UDNHeader;
import org.fourthline.cling.model.message.header.USNRootDeviceHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.types.NotificationSubtype;
import org.fourthline.cling.test.data.SampleData;
import org.fourthline.cling.test.data.SampleDeviceRoot;
import org.testng.annotations.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.testng.Assert.assertEquals;

public class NotifyTest {

    @Test
    public void receivedByeBye() throws Exception {

        UpnpService upnpService = new MockUpnpService();

        RemoteDevice rd = SampleData.createRemoteDevice();
        upnpService.getRegistry().addDevice(rd);
        assertEquals(upnpService.getRegistry().getRemoteDevices().size(), 1);

        IncomingNotificationRequest msg = createRequestMessage();
        msg.getHeaders().add(UpnpHeader.Type.NT, new RootDeviceHeader());
        msg.getHeaders().add(UpnpHeader.Type.NTS, new NTSHeader(NotificationSubtype.BYEBYE));
        msg.getHeaders().add(UpnpHeader.Type.USN, new USNRootDeviceHeader(rd.getIdentity().getUdn()));

        upnpService.getProtocolFactory().createReceivingAsync(msg).run();

        assertEquals(upnpService.getRegistry().getRemoteDevices().size(), 0);
    }

    @Test
    public void receivedNoUDN() throws Exception {

        UpnpService upnpService = new MockUpnpService();

        RemoteDevice rd = SampleData.createRemoteDevice();
        upnpService.getRegistry().addDevice(rd);

        assertEquals(upnpService.getRegistry().getRemoteDevices().size(), 1);

        IncomingNotificationRequest msg = createRequestMessage();
        msg.getHeaders().add(UpnpHeader.Type.NT, new RootDeviceHeader());
        msg.getHeaders().add(UpnpHeader.Type.NTS, new NTSHeader(NotificationSubtype.BYEBYE));
        // This is what we are testing, the missing header!
        // msg.getHeaders().add(UpnpHeader.Type.USN, new USNRootDeviceHeader(rd.getIdentity().getUdn()));

        upnpService.getProtocolFactory().createReceivingAsync(msg).run();

        // This should be unchanged from earlier state
        assertEquals(upnpService.getRegistry().getRemoteDevices().size(), 1);
    }

    @Test
    public void receivedNoLocation() throws Exception {

        MockUpnpService upnpService = new MockUpnpService();

        RemoteDevice rd = SampleData.createRemoteDevice();

        IncomingNotificationRequest msg = createRequestMessage();
        msg.getHeaders().add(UpnpHeader.Type.NTS, new NTSHeader(NotificationSubtype.ALIVE));
        msg.getHeaders().add(UpnpHeader.Type.NT, new RootDeviceHeader());
        msg.getHeaders().add(UpnpHeader.Type.USN, new USNRootDeviceHeader(rd.getIdentity().getUdn()));
        msg.getHeaders().add(UpnpHeader.Type.MAX_AGE, new MaxAgeHeader(rd.getIdentity().getMaxAgeSeconds()));
        // We test the missing header
        //msg.getHeaders().add(UpnpHeader.Type.LOCATION, new LocationHeader(SampleDeviceRoot.getDeviceDescriptorURL()));

        upnpService.getProtocolFactory().createReceivingAsync(msg).run();

        Thread.sleep(100);
        assertEquals(upnpService.getRouter().getSentStreamRequestMessages().size(), 0);
    }

    @Test
    public void receivedNoMaxAge() throws Exception {

        MockUpnpService upnpService = new MockUpnpService();

        RemoteDevice rd = SampleData.createRemoteDevice();

        IncomingNotificationRequest msg = createRequestMessage();
        msg.getHeaders().add(UpnpHeader.Type.NTS, new NTSHeader(NotificationSubtype.ALIVE));
        msg.getHeaders().add(UpnpHeader.Type.NT, new RootDeviceHeader());
        msg.getHeaders().add(UpnpHeader.Type.USN, new USNRootDeviceHeader(rd.getIdentity().getUdn()));
        msg.getHeaders().add(UpnpHeader.Type.LOCATION, new LocationHeader(SampleDeviceRoot.getDeviceDescriptorURL()));
        // We test the missing header
        //msg.getHeaders().add(UpnpHeader.Type.MAX_AGE, new MaxAgeHeader(rd.getIdentity().getMaxAgeSeconds()));

        upnpService.getProtocolFactory().createReceivingAsync(msg).run();

        Thread.sleep(100);
        assertEquals(upnpService.getRouter().getSentStreamRequestMessages().size(), 0);
    }

    @Test
    public void receivedAlreadyKnownLocalUDN() throws Exception {

        MockUpnpService upnpService = new MockUpnpService();

        LocalDevice localDevice = SampleData.createLocalDevice();
        upnpService.getRegistry().addDevice(localDevice);

        RemoteDevice rd = SampleData.createRemoteDevice();

        IncomingNotificationRequest msg = createRequestMessage();
        msg.getHeaders().add(UpnpHeader.Type.NTS, new NTSHeader(NotificationSubtype.ALIVE));
        msg.getHeaders().add(UpnpHeader.Type.NT, new RootDeviceHeader());
        msg.getHeaders().add(UpnpHeader.Type.USN, new USNRootDeviceHeader(rd.getIdentity().getUdn()));
        msg.getHeaders().add(UpnpHeader.Type.LOCATION, new LocationHeader(SampleDeviceRoot.getDeviceDescriptorURL()));
        msg.getHeaders().add(UpnpHeader.Type.MAX_AGE, new MaxAgeHeader(rd.getIdentity().getMaxAgeSeconds()));

        upnpService.getProtocolFactory().createReceivingAsync(msg).run();

        Thread.sleep(100);
        assertEquals(upnpService.getRouter().getSentStreamRequestMessages().size(), 0);
    }

    @Test
    public void receiveEmbeddedTriggersUpdate() throws Exception {

        UpnpService upnpService = new MockUpnpService(false, true);

        RemoteDevice rd = SampleData.createRemoteDevice(
                SampleData.createRemoteDeviceIdentity(2)
        );
        RemoteDevice embedded = rd.getEmbeddedDevices()[0];

        upnpService.getRegistry().addDevice(rd);

        assertEquals(upnpService.getRegistry().getRemoteDevices().size(), 1);

        IncomingNotificationRequest msg = createRequestMessage();
        msg.getHeaders().add(UpnpHeader.Type.NTS, new NTSHeader(NotificationSubtype.ALIVE));
        msg.getHeaders().add(UpnpHeader.Type.NT, new UDNHeader(embedded.getIdentity().getUdn()));
        msg.getHeaders().add(UpnpHeader.Type.USN, new UDNHeader(embedded.getIdentity().getUdn()));
        msg.getHeaders().add(UpnpHeader.Type.LOCATION, new LocationHeader(SampleDeviceRoot.getDeviceDescriptorURL()));
        msg.getHeaders().add(UpnpHeader.Type.MAX_AGE, new MaxAgeHeader(rd.getIdentity().getMaxAgeSeconds()));

        Thread.sleep(1000);
        upnpService.getProtocolFactory().createReceivingAsync(msg).run();

        Thread.sleep(1000);
        upnpService.getProtocolFactory().createReceivingAsync(msg).run();

        Thread.sleep(1000);
        assertEquals(upnpService.getRegistry().getRemoteDevices().size(), 1);

        upnpService.shutdown();
    }

    protected IncomingNotificationRequest createRequestMessage() throws UnknownHostException {
        IncomingNotificationRequest msg = new IncomingNotificationRequest(
                new IncomingDatagramMessage<>(
                        new UpnpRequest(UpnpRequest.Method.NOTIFY),
                        InetAddress.getByName("127.0.0.1"),
                        Constants.UPNP_MULTICAST_PORT,
                        InetAddress.getByName("127.0.0.1")
                )
        );

        msg.getHeaders().add(UpnpHeader.Type.HOST, new HostHeader());
        return msg;

    }

}