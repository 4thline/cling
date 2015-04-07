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
import org.fourthline.cling.model.Constants;
import org.fourthline.cling.model.DiscoveryOptions;
import org.fourthline.cling.model.Namespace;
import org.fourthline.cling.model.message.IncomingDatagramMessage;
import org.fourthline.cling.model.message.OutgoingDatagramMessage;
import org.fourthline.cling.model.message.UpnpMessage;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.model.message.discovery.IncomingSearchRequest;
import org.fourthline.cling.model.message.header.DeviceTypeHeader;
import org.fourthline.cling.model.message.header.DeviceUSNHeader;
import org.fourthline.cling.model.message.header.EXTHeader;
import org.fourthline.cling.model.message.header.HostHeader;
import org.fourthline.cling.model.message.header.MANHeader;
import org.fourthline.cling.model.message.header.MXHeader;
import org.fourthline.cling.model.message.header.MaxAgeHeader;
import org.fourthline.cling.model.message.header.RootDeviceHeader;
import org.fourthline.cling.model.message.header.STAllHeader;
import org.fourthline.cling.model.message.header.ServiceTypeHeader;
import org.fourthline.cling.model.message.header.ServiceUSNHeader;
import org.fourthline.cling.model.message.header.UDNHeader;
import org.fourthline.cling.model.message.header.USNRootDeviceHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.NotificationSubtype;
import org.fourthline.cling.protocol.async.ReceivingSearch;
import org.fourthline.cling.mock.MockUpnpService;
import org.fourthline.cling.test.data.SampleData;
import org.fourthline.cling.test.data.SampleUSNHeaders;
import org.seamless.util.URIUtil;
import org.testng.annotations.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.testng.Assert.*;

public class SearchReceivedTest {

    @Test
    public void receivedSearchAll() throws Exception {

        MockUpnpService upnpService = new MockUpnpService();

        LocalDevice localDevice = SampleData.createLocalDevice();
        LocalDevice embeddedDevice = localDevice.getEmbeddedDevices()[0];
        upnpService.getRegistry().addDevice(localDevice);

        IncomingSearchRequest searchMsg = createRequestMessage();
        searchMsg.getHeaders().add(UpnpHeader.Type.MAN, new MANHeader(NotificationSubtype.DISCOVER.getHeaderString()));
        searchMsg.getHeaders().add(UpnpHeader.Type.MX, new MXHeader(1));
        searchMsg.getHeaders().add(UpnpHeader.Type.ST, new STAllHeader());
        searchMsg.getHeaders().add(UpnpHeader.Type.HOST, new HostHeader());

        ReceivingSearch prot = createProtocol(upnpService, searchMsg);
        prot.run();

        assertEquals(upnpService.getRouter().getOutgoingDatagramMessages().size(), 10);

        for (OutgoingDatagramMessage msg : upnpService.getRouter().getOutgoingDatagramMessages()) {
            //SampleData.debugMsg(msg);
            assertSearchResponseBasics(upnpService.getConfiguration().getNamespace(), msg, localDevice);
        }

        SampleUSNHeaders.assertUSNHeaders(upnpService.getRouter().getOutgoingDatagramMessages(), localDevice, embeddedDevice, UpnpHeader.Type.ST);
    }

    @Test
    public void receivedSearchRoot() throws Exception {

        MockUpnpService upnpService = new MockUpnpService();

        LocalDevice localDevice = SampleData.createLocalDevice();
        upnpService.getRegistry().addDevice(localDevice);

        IncomingSearchRequest searchMsg = createRequestMessage();
        searchMsg.getHeaders().add(UpnpHeader.Type.MAN, new MANHeader(NotificationSubtype.DISCOVER.getHeaderString()));
        searchMsg.getHeaders().add(UpnpHeader.Type.MX, new MXHeader(1));
        searchMsg.getHeaders().add(UpnpHeader.Type.ST, new RootDeviceHeader());
        searchMsg.getHeaders().add(UpnpHeader.Type.HOST, new HostHeader());

        ReceivingSearch prot = createProtocol(upnpService, searchMsg);
        prot.run();

        assertEquals(upnpService.getRouter().getOutgoingDatagramMessages().size(), 1);

        assertSearchResponseBasics(
            upnpService.getConfiguration().getNamespace(),
            upnpService.getRouter().getOutgoingDatagramMessages().get(0),
            localDevice
        );
        assertEquals(
                upnpService.getRouter().getOutgoingDatagramMessages().get(0).getHeaders().getFirstHeader(UpnpHeader.Type.ST).getString(),
                new RootDeviceHeader().getString()
        );
        assertEquals(
                upnpService.getRouter().getOutgoingDatagramMessages().get(0).getHeaders().getFirstHeader(UpnpHeader.Type.USN).getString(),
                localDevice.getIdentity().getUdn().toString() + USNRootDeviceHeader.ROOT_DEVICE_SUFFIX
        );
    }

    @Test
    public void receivedSearchUDN() throws Exception {

        MockUpnpService upnpService = new MockUpnpService();

        LocalDevice localDevice = SampleData.createLocalDevice();
        upnpService.getRegistry().addDevice(localDevice);

        IncomingSearchRequest searchMsg = createRequestMessage();
        searchMsg.getHeaders().add(UpnpHeader.Type.MAN, new MANHeader(NotificationSubtype.DISCOVER.getHeaderString()));
        searchMsg.getHeaders().add(UpnpHeader.Type.MX, new MXHeader(1));
        searchMsg.getHeaders().add(UpnpHeader.Type.ST, new UDNHeader(localDevice.getIdentity().getUdn()));
        searchMsg.getHeaders().add(UpnpHeader.Type.HOST, new HostHeader());

        ReceivingSearch prot = createProtocol(upnpService, searchMsg);
        prot.run();

        assertEquals(upnpService.getRouter().getOutgoingDatagramMessages().size(), 1);

        assertSearchResponseBasics(
                upnpService.getConfiguration().getNamespace(),
                upnpService.getRouter().getOutgoingDatagramMessages().get(0),
                localDevice
        );
        assertEquals(
                upnpService.getRouter().getOutgoingDatagramMessages().get(0).getHeaders().getFirstHeader(UpnpHeader.Type.ST).getString(),
                new UDNHeader(localDevice.getIdentity().getUdn()).getString()
        );
        assertEquals(
                upnpService.getRouter().getOutgoingDatagramMessages().get(0).getHeaders().getFirstHeader(UpnpHeader.Type.USN).getString(),
                new UDNHeader(localDevice.getIdentity().getUdn()).getString()
        );
    }

    @Test
    public void receivedSearchDeviceType() throws Exception {

        MockUpnpService upnpService = new MockUpnpService();

        LocalDevice localDevice = SampleData.createLocalDevice();
        upnpService.getRegistry().addDevice(localDevice);

        IncomingSearchRequest searchMsg = createRequestMessage();
        searchMsg.getHeaders().add(UpnpHeader.Type.MAN, new MANHeader(NotificationSubtype.DISCOVER.getHeaderString()));
        searchMsg.getHeaders().add(UpnpHeader.Type.MX, new MXHeader(1));
        searchMsg.getHeaders().add(UpnpHeader.Type.ST, new DeviceTypeHeader(localDevice.getType()));
        searchMsg.getHeaders().add(UpnpHeader.Type.HOST, new HostHeader());

        ReceivingSearch prot = createProtocol(upnpService, searchMsg);
        prot.run();

        assertEquals(upnpService.getRouter().getOutgoingDatagramMessages().size(), 1);

        assertSearchResponseBasics(
                upnpService.getConfiguration().getNamespace(),
                upnpService.getRouter().getOutgoingDatagramMessages().get(0),
                localDevice
        );
        assertEquals(
                upnpService.getRouter().getOutgoingDatagramMessages().get(0).getHeaders().getFirstHeader(UpnpHeader.Type.ST).getString(),
                new DeviceTypeHeader(localDevice.getType()).getString()
        );
        assertEquals(
                upnpService.getRouter().getOutgoingDatagramMessages().get(0).getHeaders().getFirstHeader(UpnpHeader.Type.USN).getString(),
                new DeviceUSNHeader(localDevice.getIdentity().getUdn(), localDevice.getType()).getString()
        );
    }

    @Test
    public void receivedSearchServiceType() throws Exception {

        MockUpnpService upnpService = new MockUpnpService();

        LocalDevice localDevice = SampleData.createLocalDevice();
        Service service = localDevice.getServices()[0];
        upnpService.getRegistry().addDevice(localDevice);

        IncomingSearchRequest searchMsg = createRequestMessage();
        searchMsg.getHeaders().add(UpnpHeader.Type.MAN, new MANHeader(NotificationSubtype.DISCOVER.getHeaderString()));
        searchMsg.getHeaders().add(UpnpHeader.Type.MX, new MXHeader(1));
        searchMsg.getHeaders().add(UpnpHeader.Type.ST, new ServiceTypeHeader(service.getServiceType()));
        searchMsg.getHeaders().add(UpnpHeader.Type.HOST, new HostHeader());

        ReceivingSearch prot = createProtocol(upnpService, searchMsg);
        prot.run();

        assertEquals(upnpService.getRouter().getOutgoingDatagramMessages().size(), 1);

        assertSearchResponseBasics(
                upnpService.getConfiguration().getNamespace(),
                upnpService.getRouter().getOutgoingDatagramMessages().get(0),
                localDevice
        );
        assertEquals(
                upnpService.getRouter().getOutgoingDatagramMessages().get(0).getHeaders().getFirstHeader(UpnpHeader.Type.ST).getString(),
                new ServiceTypeHeader(service.getServiceType()).getString()
        );
        assertEquals(
                upnpService.getRouter().getOutgoingDatagramMessages().get(0).getHeaders().getFirstHeader(UpnpHeader.Type.USN).getString(),
                new ServiceUSNHeader(localDevice.getIdentity().getUdn(), service.getServiceType()).getString()
        );
    }

    @Test
    public void receivedInvalidST() throws Exception {

        MockUpnpService upnpService = new MockUpnpService();

        IncomingSearchRequest searchMsg = createRequestMessage();
        searchMsg.getHeaders().add(UpnpHeader.Type.MAN, new MANHeader(NotificationSubtype.DISCOVER.getHeaderString()));
        searchMsg.getHeaders().add(UpnpHeader.Type.MX, new MXHeader(1));
        searchMsg.getHeaders().add(UpnpHeader.Type.HOST, new HostHeader());

        ReceivingSearch prot = createProtocol(upnpService, searchMsg);
        prot.run();

        assertEquals(upnpService.getRouter().getOutgoingDatagramMessages().size(), 0);
    }

    @Test
    public void receivedInvalidMX() throws Exception {

        MockUpnpService upnpService = new MockUpnpService();

        IncomingSearchRequest searchMsg = createRequestMessage();
        searchMsg.getHeaders().add(UpnpHeader.Type.MAN, new MANHeader(NotificationSubtype.DISCOVER.getHeaderString()));
        searchMsg.getHeaders().add(UpnpHeader.Type.ST, new STAllHeader());
        searchMsg.getHeaders().add(UpnpHeader.Type.HOST, new HostHeader());

        ReceivingSearch prot = createProtocol(upnpService, searchMsg);
        prot.run();

        assertEquals(upnpService.getRouter().getOutgoingDatagramMessages().size(), 0);
    }

    @Test
    public void receivedNonAdvertised() throws Exception {

        MockUpnpService upnpService = new MockUpnpService();

        LocalDevice localDevice = SampleData.createLocalDevice();

        // Disable advertising
        upnpService.getRegistry().addDevice(localDevice, new DiscoveryOptions(false));

        IncomingSearchRequest searchMsg = createRequestMessage();
        searchMsg.getHeaders().add(UpnpHeader.Type.MAN, new MANHeader(NotificationSubtype.DISCOVER.getHeaderString()));
        searchMsg.getHeaders().add(UpnpHeader.Type.MX, new MXHeader(1));
        searchMsg.getHeaders().add(UpnpHeader.Type.ST, new STAllHeader());
        searchMsg.getHeaders().add(UpnpHeader.Type.HOST, new HostHeader());

        ReceivingSearch prot = createProtocol(upnpService, searchMsg);
        prot.run();

        assertEquals(upnpService.getRouter().getOutgoingDatagramMessages().size(), 0);

        // Enable advertising
        upnpService.getRegistry().setDiscoveryOptions(
            localDevice.getIdentity().getUdn(),
            new DiscoveryOptions(true)
        );

        prot = createProtocol(upnpService, searchMsg);
        prot.run();

        assertEquals(upnpService.getRouter().getOutgoingDatagramMessages().size(), 10);
    }

    protected ReceivingSearch createProtocol(UpnpService upnpService, IncomingSearchRequest searchMsg) throws Exception {
        return new ReceivingSearch(upnpService, searchMsg);
    }

    protected void assertSearchResponseBasics(Namespace namespace, UpnpMessage msg, LocalDevice rootDevice) {
        assertEquals(
                msg.getHeaders().getFirstHeader(UpnpHeader.Type.MAX_AGE).getString(),
                new MaxAgeHeader(rootDevice.getIdentity().getMaxAgeSeconds()).getString()
        );
        assertEquals(msg.getHeaders().getFirstHeader(UpnpHeader.Type.EXT).getString(), new EXTHeader().getString());
        assertEquals(
                msg.getHeaders().getFirstHeader(UpnpHeader.Type.LOCATION).getString(),
                URIUtil.createAbsoluteURL(SampleData.getLocalBaseURL(), namespace.getDescriptorPath(rootDevice)).toString()
        );
        assertNotNull(msg.getHeaders().getFirstHeader(UpnpHeader.Type.SERVER).getString());
    }

    protected IncomingSearchRequest createRequestMessage() throws UnknownHostException {
        return new IncomingSearchRequest(
                new IncomingDatagramMessage<>(
                        new UpnpRequest(UpnpRequest.Method.MSEARCH),
                        InetAddress.getByName("127.0.0.1"),
                        Constants.UPNP_MULTICAST_PORT,
                        InetAddress.getByName("127.0.0.1")
                )
        );

    }

}