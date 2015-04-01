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
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.discovery.IncomingSearchResponse;
import org.fourthline.cling.model.message.header.EXTHeader;
import org.fourthline.cling.model.message.header.HostHeader;
import org.fourthline.cling.model.message.header.LocationHeader;
import org.fourthline.cling.model.message.header.MaxAgeHeader;
import org.fourthline.cling.model.message.header.STAllHeader;
import org.fourthline.cling.model.message.header.UDNHeader;
import org.fourthline.cling.model.message.header.USNRootDeviceHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.test.data.SampleData;
import org.fourthline.cling.test.data.SampleDeviceRoot;
import org.testng.annotations.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.testng.Assert.assertEquals;

/**
 * @author Christian Bauer
 */
public class SearchResponseTest {

    @Test
    public void receivedValidResponse() throws Exception {

        MockUpnpService upnpService = new MockUpnpService();

        RemoteDevice rd = SampleData.createRemoteDevice();

        IncomingSearchResponse msg = createResponseMessage(new STAllHeader());
        msg.getHeaders().add(UpnpHeader.Type.USN, new USNRootDeviceHeader(rd.getIdentity().getUdn()));
        msg.getHeaders().add(UpnpHeader.Type.LOCATION, new LocationHeader(SampleDeviceRoot.getDeviceDescriptorURL()));
        msg.getHeaders().add(UpnpHeader.Type.MAX_AGE, new MaxAgeHeader(rd.getIdentity().getMaxAgeSeconds()));

        upnpService.getProtocolFactory().createReceivingAsync(msg).run();
        Thread.sleep(100);
        assertEquals(upnpService.getRouter().getSentStreamRequestMessages().size(), 1);
    }

    @Test
    public void receivedInvalidSearchResponses() throws Exception {

        MockUpnpService upnpService = new MockUpnpService();

        RemoteDevice rd = SampleData.createRemoteDevice();

        // Missing USN header
        IncomingSearchResponse msg = createResponseMessage(new STAllHeader());
        upnpService.getProtocolFactory().createReceivingAsync(msg).run();
        Thread.sleep(100);
        assertEquals(upnpService.getRouter().getSentStreamRequestMessages().size(), 0);

        // Missing location header
        msg = createResponseMessage(new STAllHeader());
        msg.getHeaders().add(UpnpHeader.Type.USN, new USNRootDeviceHeader(rd.getIdentity().getUdn()));
        upnpService.getProtocolFactory().createReceivingAsync(msg).run();
        Thread.sleep(100);
        assertEquals(upnpService.getRouter().getSentStreamRequestMessages().size(), 0);

        // Missing max age header
        msg = createResponseMessage(new STAllHeader());
        msg.getHeaders().add(UpnpHeader.Type.USN, new USNRootDeviceHeader(rd.getIdentity().getUdn()));
        msg.getHeaders().add(UpnpHeader.Type.LOCATION, new LocationHeader(SampleDeviceRoot.getDeviceDescriptorURL()));
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

        IncomingSearchResponse msg = createResponseMessage(new STAllHeader());
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

        RemoteDevice rd = SampleData.createRemoteDevice();
        RemoteDevice embedded = rd.getEmbeddedDevices()[0];

        upnpService.getRegistry().addDevice(rd);

        assertEquals(upnpService.getRegistry().getRemoteDevices().size(), 1);

        IncomingSearchResponse msg = createResponseMessage(new STAllHeader());
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

    protected IncomingSearchResponse createResponseMessage(UpnpHeader stHeader) throws UnknownHostException {
        IncomingSearchResponse msg = new IncomingSearchResponse(
                new IncomingDatagramMessage<>(
                        new UpnpResponse(UpnpResponse.Status.OK),
                        InetAddress.getByName("127.0.0.1"),
                        Constants.UPNP_MULTICAST_PORT,
                        InetAddress.getByName("127.0.0.1")
                )
        );

        msg.getHeaders().add(UpnpHeader.Type.ST, stHeader);
        msg.getHeaders().add(UpnpHeader.Type.EXT, new EXTHeader());
        msg.getHeaders().add(UpnpHeader.Type.HOST, new HostHeader());
        return msg;

    }
    
}
