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


public class NotifyAliveConcurrentTest {

/* TODO: Fixme

    protected final UpnpServiceImpl upnpService = new UpnpServiceImpl(new TestModel.NetworkDisabledRouterConfiguration()) {

        @Override
        public StreamResponseMessage send(StreamRequestMessage msg) {

            // GET request for the device descriptor must arrive here
            UpnpRequest request = msg.getOperation();
            Assert.assertEquals(request.getMethod(), UpnpRequest.Method.GET);
            if (request.getURI().toString().equals(TestModel.getDeviceDescriptorURL().toString())) {

                // Now, to simulate concurrency, we send another notify before the descriptor retrieval completes
                NotifyAliveConcurrentTest.this.upnpService.received(createMessage());

                // Send the mock response back
                return new StreamResponseMessage(TestModel.getSampleDeviceDescriptorUDA10(), new ContentTypeHeader());

            } else if (request.getURI().toString().equals(TestModel.getServiceOneDescriptorURL().toString())) {

                // Send the mock response back
                return new StreamResponseMessage(TestModel.getSampleServiceOneDescriptorUDA10(), new ContentTypeHeader());

            } else if (request.getURI().toString().equals(TestModel.getServiceTwoDescriptorURL().toString())) {

                // Send the mock response back
                return new StreamResponseMessage(TestModel.getSampleServiceTwoDescriptorUDA10(), new ContentTypeHeader());

            } else if (request.getURI().toString().equals(TestModel.getServiceThreeDescriptorURL().toString())) {

                // Send the mock response back
                return new StreamResponseMessage(TestModel.getSampleServiceThreeDescriptorUDA10(), new ContentTypeHeader());

            } else {
                throw new RuntimeException("Unknown (non-mocked) stream request: " + msg);
            }
        }
    };

    @Test
    public void notifyAliveConcurrent() throws Exception {

        TestListener listener = new TestListener();

        MockUpnpService upnpService = new Upnp
        upnpService.start(false);

        upnpService.addListener(listener);

        upnpService.received(createMessage());

        Thread.sleep(1000); // TODO: This is pretty random but I don't see how we can simulate concurrency otherwise
        assert listener.valid;
    }

    protected IncomingDatagramMessage createMessage() {
        try {

            RemoteDevice device = TestModel.getRemoteDevice();

            IncomingNotificationRequest msg =
                    new IncomingNotificationRequest(
                            new IncomingDatagramMessage<UpnpRequest>(
                                    new UpnpRequest(UpnpRequest.Method.NOTIFY),
                                    InetAddress.getByName("127.0.0.1"),
                                    Constants.UPNP_MULTICAST_PORT,
                                    InetAddress.getByName("127.0.0.1")
                            )
                    );

            msg.getHeaders().add(UpnpHeader.Type.HOST, new HostHeader(Constants.IPV4_UPNP_MULTICAST_GROUP, Constants.UPNP_MULTICAST_PORT));
            msg.getHeaders().add(UpnpHeader.Type.MAX_AGE, new MaxAgeHeader(2000));
            msg.getHeaders().add(UpnpHeader.Type.LOCATION, new LocationHeader(device.getDescriptorURL()));
            msg.getHeaders().add(UpnpHeader.Type.NT, new RootDeviceHeader());
            msg.getHeaders().add(UpnpHeader.Type.NTS, new NTSHeader(NotificationSubtype.ALIVE));
            msg.getHeaders().add(UpnpHeader.Type.SERVER, new ServerHeader());
            msg.getHeaders().add(UpnpHeader.Type.USN, new USNRootDeviceHeader(device.getUdn()));

            return msg;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    protected class TestListener implements RegistryListener {

        public boolean valid = false;

        public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
            assert !valid;
            assert !device.isLocal();
            assert device.isDescribed();
            TestModel.assertTestDataMatch(device);
            TestModel.assertTestDataMatchServiceOne(device.getDeviceServices().get(0).getService());
            TestModel.assertTestDataMatchServiceTwo(device.getEmbeddedDevices().get(0).getDeviceServices().get(0).getService());
            TestModel.assertTestDataMatchServiceThree(device.getEmbeddedDevices().get(0).getEmbeddedDevices().get(0).getDeviceServices().get(0).getService());
            Assert.assertEquals(device.getMaxAge(), new Integer(2000));
            valid = true;
        }

        public void remoteDeviceUpdated(Registry registry, RemoteDevice device) {

        }

        public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {

        }

        public void localDeviceAdded(Registry registry, LocalDevice device) {

        }

        public void localDeviceRemoved(Registry registry, LocalDevice device) {

        }
    }
*/

}
