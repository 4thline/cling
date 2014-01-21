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

package org.fourthline.cling.test.local;

import org.fourthline.cling.binding.LocalServiceBinder;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.binding.annotations.UpnpOutputArgument;
import org.fourthline.cling.binding.annotations.UpnpService;
import org.fourthline.cling.binding.annotations.UpnpServiceId;
import org.fourthline.cling.binding.annotations.UpnpServiceType;
import org.fourthline.cling.binding.annotations.UpnpStateVariable;
import org.fourthline.cling.binding.xml.DeviceDescriptorBinder;
import org.fourthline.cling.binding.xml.ServiceDescriptorBinder;
import org.fourthline.cling.mock.MockUpnpService;
import org.fourthline.cling.mock.MockUpnpServiceConfiguration;
import org.fourthline.cling.model.DiscoveryOptions;
import org.fourthline.cling.model.Namespace;
import org.fourthline.cling.model.ServerClientTokens;
import org.fourthline.cling.model.message.UpnpMessage;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.profile.RemoteClientInfo;
import org.fourthline.cling.model.types.NotificationSubtype;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.test.data.SampleData;
import org.seamless.util.URIUtil;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * TODO: These timing-sensitive tests fail sometimes... should use latches instead to coordinate threads
 */
public class LocalDeviceBindingAdvertisementTest {

    @Test
    public void registerLocalDevice() throws Exception {

        MockUpnpService upnpService = new MockUpnpService(true, true);

        LocalDevice binaryLight = DemoBinaryLight.createTestDevice();

        upnpService.getRegistry().addDevice(binaryLight);

        Thread.sleep(2000);

        assertEquals(upnpService.getRouter().getOutgoingDatagramMessages().size(), 12);
        for (UpnpMessage msg : upnpService.getRouter().getOutgoingDatagramMessages()) {
            assertAliveMsgBasics(upnpService.getConfiguration().getNamespace(), msg, binaryLight, 1800);
        }

        upnpService.shutdown();

        DeviceDescriptorBinder dvcBinder = upnpService.getConfiguration().getDeviceDescriptorBinderUDA10();
        String descriptorXml = dvcBinder.generate(
            binaryLight,
            new RemoteClientInfo(),
            upnpService.getConfiguration().getNamespace()
        );

        RemoteDevice testDevice = new RemoteDevice(SampleData.createRemoteDeviceIdentity());

        testDevice = dvcBinder.describe(testDevice, descriptorXml);
        assertEquals(testDevice.getDetails().getFriendlyName(), "Example Binary Light");

        // TODO: more tests

        ServiceDescriptorBinder svcBinder = upnpService.getConfiguration().getServiceDescriptorBinderUDA10();
        String serviceXml = svcBinder.generate(binaryLight.getServices()[0]);

        // TODO: more tests
    }

    @Test
    public void waitForRefresh() throws Exception {

        MockUpnpService upnpService = new MockUpnpService(true, true);

        LocalDevice ld =
            SampleData.createLocalDevice(
                SampleData.createLocalDeviceIdentity(1)
            );

        upnpService.getRegistry().addDevice(ld);
        assertEquals(upnpService.getRegistry().getLocalDevices().size(), 1);

        Thread.sleep(2000);

        assertEquals(upnpService.getRegistry().getLocalDevices().size(), 1);

        // 30 from addDevice()
        // 30 from regular refresh
        assertTrue(upnpService.getRouter().getOutgoingDatagramMessages().size() >= 60);
        for (UpnpMessage msg : upnpService.getRouter().getOutgoingDatagramMessages()) {
            assertAliveMsgBasics(upnpService.getConfiguration().getNamespace(), msg, ld, 1);
        }

        upnpService.getRouter().getOutgoingDatagramMessages().clear();

        upnpService.shutdown();

        // Check correct byebye
        assertTrue(upnpService.getRouter().getOutgoingDatagramMessages().size() >= 30);
        for (UpnpMessage msg : upnpService.getRouter().getOutgoingDatagramMessages()) {
            assertByeByeMsgBasics(upnpService.getConfiguration().getNamespace(), msg, ld, 1);
        }
    }

    @Test
    public void waitForAliveFlood() throws Exception {

        MockUpnpService upnpService = new MockUpnpService(true,
            new MockUpnpServiceConfiguration(true) {
                @Override
                public int getAliveIntervalMillis() {
                    return 2000;
                }
            });

        LocalDevice ld =
            SampleData.createLocalDevice(
                SampleData.createLocalDeviceIdentity(1000) // Max age ignored
            );

        upnpService.getRegistry().addDevice(ld);
        assertEquals(upnpService.getRegistry().getLocalDevices().size(), 1);

        Thread.sleep(5000);

        assertEquals(upnpService.getRegistry().getLocalDevices().size(), 1);

        // 30 from addDevice()
        // 30 from first flood
        // 30 from second flood
        assertTrue(upnpService.getRouter().getOutgoingDatagramMessages().size() >= 90);
        for (UpnpMessage msg : upnpService.getRouter().getOutgoingDatagramMessages()) {
            assertAliveMsgBasics(upnpService.getConfiguration().getNamespace(), msg, ld, 1000);
        }

        upnpService.shutdown();
    }

    @Test
    public void byeByeBeforeAlive() throws Exception {

        MockUpnpService upnpService = new MockUpnpService(true, true);

        LocalDevice ld =
            SampleData.createLocalDevice(
                SampleData.createLocalDeviceIdentity(60)
            );

        upnpService.getRegistry().addDevice(ld, new DiscoveryOptions(true, true));

        Thread.sleep(2000);

        assertTrue(upnpService.getRouter().getOutgoingDatagramMessages().size() >= 60);
        // 30 BYEBYE
        // 30 ALIVE
        int i = 0;
        for (; i < 30; i++) {
            UpnpMessage msg = upnpService.getRouter().getOutgoingDatagramMessages().get(i);
            assertByeByeMsgBasics(upnpService.getConfiguration().getNamespace(), msg, ld, 60);
        }
        for (; i < 60; i++) {
            UpnpMessage msg = upnpService.getRouter().getOutgoingDatagramMessages().get(i);
            assertAliveMsgBasics(upnpService.getConfiguration().getNamespace(), msg, ld, 60);
        }

        upnpService.shutdown();
    }


    @Test
    public void registerNonAdvertisedLocalDevice() throws Exception {
        MockUpnpService upnpService = new MockUpnpService(true, true);

        LocalDevice binaryLight = DemoBinaryLight.createTestDevice();

        upnpService.getRegistry().addDevice(binaryLight, new DiscoveryOptions(false)); // Not advertised

        Thread.sleep(2000);

        assertEquals(upnpService.getRouter().getOutgoingDatagramMessages().size(), 0);

        upnpService.shutdown();
    }

    protected void assertAliveMsgBasics(Namespace namespace, UpnpMessage msg, LocalDevice device, Integer maxAge) {
        assertEquals(msg.getHeaders().getFirstHeader(UpnpHeader.Type.NTS).getValue(), NotificationSubtype.ALIVE);
        assertEquals(
            msg.getHeaders().getFirstHeader(UpnpHeader.Type.LOCATION).getValue().toString(),
            URIUtil.createAbsoluteURL(SampleData.getLocalBaseURL(), namespace.getDescriptorPath(device)).toString()
        );
        assertEquals(msg.getHeaders().getFirstHeader(UpnpHeader.Type.MAX_AGE).getValue(), maxAge);
        assertEquals(msg.getHeaders().getFirstHeader(UpnpHeader.Type.SERVER).getValue(), new ServerClientTokens());
    }

    protected void assertByeByeMsgBasics(Namespace namespace, UpnpMessage msg, LocalDevice device, Integer maxAge) {
        assertEquals(msg.getHeaders().getFirstHeader(UpnpHeader.Type.NTS).getValue(), NotificationSubtype.BYEBYE);
        assertEquals(
            msg.getHeaders().getFirstHeader(UpnpHeader.Type.LOCATION).getValue().toString(),
            URIUtil.createAbsoluteURL(SampleData.getLocalBaseURL(), namespace.getDescriptorPath(device)).toString()
        );
        assertEquals(msg.getHeaders().getFirstHeader(UpnpHeader.Type.MAX_AGE).getValue(), maxAge);
        assertEquals(msg.getHeaders().getFirstHeader(UpnpHeader.Type.SERVER).getValue(), new ServerClientTokens());
    }

    @UpnpService(
        serviceId = @UpnpServiceId("SwitchPower"),
        serviceType = @UpnpServiceType(value = "SwitchPower", version = 1)
    )
    public static class DemoBinaryLight {

        private static LocalDevice createTestDevice() throws Exception {
            LocalServiceBinder binder = new AnnotationLocalServiceBinder();
            return new LocalDevice(
                SampleData.createLocalDeviceIdentity(),
                new UDADeviceType("BinaryLight", 1),
                new DeviceDetails("Example Binary Light"),
                binder.read(DemoBinaryLight.class)
            );
        }

        @UpnpStateVariable(defaultValue = "0", sendEvents = false)
        private boolean target = false;

        @UpnpStateVariable(defaultValue = "0")
        private boolean status = false;

        @UpnpAction
        public void setTarget(@UpnpInputArgument(name = "NewTargetValue") boolean newTargetValue) {
            target = newTargetValue;
            status = newTargetValue;
        }

        @UpnpAction(out = @UpnpOutputArgument(name = "RetTargetValue"))
        public boolean getTarget() {
            return target;
        }

        @UpnpAction(out = {@UpnpOutputArgument(name = "ResultStatus")})
        public boolean getStatus() {
            return status;
        }

    }

}

