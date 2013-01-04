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

import org.fourthline.cling.mock.MockUpnpService;
import org.fourthline.cling.mock.MockUpnpServiceConfiguration;
import org.fourthline.cling.model.ExpirationDetails;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.resource.Resource;
import org.fourthline.cling.test.data.SampleData;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class RegistryExpirationTest {

    @Test
    public void addAndExpire() throws Exception {

        MockUpnpService upnpService = new MockUpnpService(false, true);

        RemoteDevice rd = SampleData.createRemoteDevice(
                SampleData.createRemoteDeviceIdentity(1)
        );
        upnpService.getRegistry().addDevice(rd);
        
        assertEquals(upnpService.getRegistry().getRemoteDevices().size(), 1);

        Thread.sleep(3000);

        assertEquals(upnpService.getRegistry().getRemoteDevices().size(), 0);

        upnpService.shutdown();
    }

    @Test
    public void overrideAgeThenAddAndExpire() throws Exception {

        MockUpnpService upnpService = new MockUpnpService(
            new MockUpnpServiceConfiguration(true) {

                @Override
                public Integer getRemoteDeviceMaxAgeSeconds() {
                    return 0;
                }
            }
        );

        RemoteDevice rd = SampleData.createRemoteDevice(
                SampleData.createRemoteDeviceIdentity(1)
        );
        upnpService.getRegistry().addDevice(rd);

        assertEquals(upnpService.getRegistry().getRemoteDevices().size(), 1);

        Thread.sleep(3000);

        // Still registered!
        assertEquals(upnpService.getRegistry().getRemoteDevices().size(), 1);

        // Update should not change the expiration time
        upnpService.getRegistry().update(rd.getIdentity());

        Thread.sleep(3000);

        // Still registered!
        assertEquals(upnpService.getRegistry().getRemoteDevices().size(), 1);

        upnpService.shutdown();
    }

    @Test
    public void addAndUpdateAndExpire() throws Exception {

        MockUpnpService upnpService = new MockUpnpService(false, true);

        RemoteDevice rd = SampleData.createRemoteDevice(
                SampleData.createRemoteDeviceIdentity(2)
        );

        // Add it to registry
        upnpService.getRegistry().addDevice(rd);
        Thread.sleep(1000);
        assertEquals(upnpService.getRegistry().getRemoteDevices().size(), 1);

        // Update it in registry
        upnpService.getRegistry().addDevice(rd);
        Thread.sleep(1000);
        assertEquals(upnpService.getRegistry().getRemoteDevices().size(), 1);

        // Update again
        upnpService.getRegistry().update(rd.getIdentity());
        Thread.sleep(1000);
        assertEquals(upnpService.getRegistry().getRemoteDevices().size(), 1);

        // Wait for expiration
        Thread.sleep(3000);
        assertEquals(upnpService.getRegistry().getRemoteDevices().size(), 0);


        upnpService.shutdown();
    }

    @Test
    public void addResourceAndExpire() throws Exception {

        MockUpnpService upnpService = new MockUpnpService(false, true);

        Resource resource = new Resource(URI.create("/this/is/a/test"), "foo");
        upnpService.getRegistry().addResource(resource, 2);

        assertEquals(upnpService.getRegistry().getResources().size(), 1);

        Thread.sleep(4000);

        assertEquals(upnpService.getRegistry().getResources().size(), 0);

        upnpService.shutdown();
    }

    @Test
    public void addResourceAndMaintain() throws Exception {

        MockUpnpService upnpService = new MockUpnpService(false, true);

        final TestRunnable testRunnable = new TestRunnable();

        Resource resource = new Resource<String>(URI.create("/this/is/a/test"), "foo") {
            @Override
            public void maintain(List<Runnable> pendingExecutions, ExpirationDetails expirationDetails) {
                if (expirationDetails.getSecondsUntilExpiration() == 1) {
                    pendingExecutions.add(testRunnable);
                }
            }
        };
        upnpService.getRegistry().addResource(resource, 2);

        assertEquals(upnpService.getRegistry().getResources().size(), 1);

        Thread.sleep(2000);

        assertEquals(testRunnable.wasExecuted, true);

        upnpService.shutdown();
    }

    protected class TestRunnable implements Runnable {
        boolean wasExecuted = false;

        public void run() {
            wasExecuted = true;
        }
    }

}
