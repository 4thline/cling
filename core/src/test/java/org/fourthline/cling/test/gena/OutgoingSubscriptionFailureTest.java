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

package org.fourthline.cling.test.gena;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.mock.MockRouter;
import org.fourthline.cling.mock.MockUpnpService;
import org.fourthline.cling.model.NetworkAddress;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.gena.RemoteGENASubscription;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.gena.IncomingEventRequestMessage;
import org.fourthline.cling.model.message.gena.OutgoingEventRequestMessage;
import org.fourthline.cling.model.message.header.SubscriptionIdHeader;
import org.fourthline.cling.model.message.header.TimeoutHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.protocol.ReceivingSync;
import org.fourthline.cling.test.data.SampleData;
import org.fourthline.cling.transport.Router;
import org.seamless.util.URIUtil;
import org.testng.annotations.Test;

import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.assertEquals;


public class OutgoingSubscriptionFailureTest {


    @Test
    public void subscriptionLifecycleNetworkOff() throws Exception {

        MockUpnpService upnpService = new MockUpnpService() {
            @Override
            protected MockRouter createRouter() {
                return new MockRouter(getConfiguration(), getProtocolFactory()) {
                    @Override
                    public List<NetworkAddress> getActiveStreamServers(InetAddress preferredAddress) {
                        // Simulate network switched off
                        return Collections.EMPTY_LIST;
                    }
                    @Override
                    public StreamResponseMessage[] getStreamResponseMessages() {

                        return new StreamResponseMessage[]{
                                createSubscribeResponseMessage()

                        };
                    }
                };
            }
        };

        final List<Boolean> testAssertions = new ArrayList<>();

        // Register remote device and its service
        RemoteDevice device = SampleData.createRemoteDevice();
        upnpService.getRegistry().addDevice(device);

        RemoteService service = SampleData.getFirstService(device);

        SubscriptionCallback callback = new SubscriptionCallback(service) {

            @Override
            protected void failed(GENASubscription subscription,
                                  UpnpResponse responseStatus,
                                  Exception exception,
                                  String defaultMsg) {
                // Should fail without response and exception (only FINE log message)
                assert responseStatus == null;
                assert exception == null;
                testAssertions.add(true);
            }

            @Override
            public void established(GENASubscription subscription) {
                testAssertions.add(false);
            }

            @Override
            public void ended(GENASubscription subscription, CancelReason reason, UpnpResponse responseStatus) {
                testAssertions.add(false);
            }

            public void eventReceived(GENASubscription subscription) {
                testAssertions.add(false);
            }

            public void eventsMissed(GENASubscription subscription, int numberOfMissedEvents) {
                testAssertions.add(false);
            }

        };

        upnpService.getControlPoint().execute(callback);
        for (Boolean testAssertion : testAssertions) {
            assert testAssertion;
        }
    }

    @Test
    public void subscriptionLifecycleMissedEvent() throws Exception {

        MockUpnpService upnpService = new MockUpnpService() {
            @Override
            protected MockRouter createRouter() {
                return new MockRouter(getConfiguration(), getProtocolFactory()) {
                    @Override
                    public StreamResponseMessage[] getStreamResponseMessages() {

                        return new StreamResponseMessage[]{
                            createSubscribeResponseMessage(),
                            createUnsubscribeResponseMessage()

                        };
                    }
                };
            }
        };

        final List<Boolean> testAssertions = new ArrayList<>();

        // Register remote device and its service
        RemoteDevice device = SampleData.createRemoteDevice();
        upnpService.getRegistry().addDevice(device);

        RemoteService service = SampleData.getFirstService(device);

        SubscriptionCallback callback = new SubscriptionCallback(service) {

            @Override
            protected void failed(GENASubscription subscription,
                                  UpnpResponse responseStatus,
                                  Exception exception,
                                  String defaultMsg) {
                testAssertions.add(false);
            }

            @Override
            public void established(GENASubscription subscription) {
                assertEquals(subscription.getSubscriptionId(), "uuid:1234");
                assertEquals(subscription.getActualDurationSeconds(), 180);
                testAssertions.add(true);
            }

            @Override
            public void ended(GENASubscription subscription, CancelReason reason, UpnpResponse responseStatus) {
                assert reason == null;
                assertEquals(responseStatus.getStatusCode(), UpnpResponse.Status.OK.getStatusCode());
                testAssertions.add(true);
            }

            public void eventReceived(GENASubscription subscription) {
                assertEquals(subscription.getCurrentValues().get("Status").toString(), "0");
                assertEquals(subscription.getCurrentValues().get("Target").toString(), "1");
                testAssertions.add(true);
            }

            public void eventsMissed(GENASubscription subscription, int numberOfMissedEvents) {
                assertEquals(numberOfMissedEvents, 2);
                testAssertions.add(true);
            }

        };

        upnpService.getControlPoint().execute(callback);

        ReceivingSync prot = upnpService.getProtocolFactory().createReceivingSync(
                createEventRequestMessage(upnpService, callback, 0)
        );
        prot.run();

        prot = upnpService.getProtocolFactory().createReceivingSync(
                createEventRequestMessage(upnpService, callback, 3) // Note the missing event messages
        );
        prot.run();

        callback.end();

        assertEquals(testAssertions.size(), 5);
        for (Boolean testAssertion : testAssertions) {
            assert testAssertion;
        }

        List<StreamRequestMessage> sentMessages = upnpService.getRouter().getSentStreamRequestMessages();

        assertEquals(sentMessages.size(), 2);
        assertEquals(
                sentMessages.get(0).getOperation().getMethod(),
                UpnpRequest.Method.SUBSCRIBE
        );
        assertEquals(
                sentMessages.get(0).getHeaders().getFirstHeader(UpnpHeader.Type.TIMEOUT, TimeoutHeader.class).getValue(),
                Integer.valueOf(1800)
        );

        assertEquals(
                sentMessages.get(1).getOperation().getMethod(),
                UpnpRequest.Method.UNSUBSCRIBE
        );
        assertEquals(
                sentMessages.get(1).getHeaders().getFirstHeader(UpnpHeader.Type.SID, SubscriptionIdHeader.class).getValue(),
                "uuid:1234"
        );

    }

    protected StreamResponseMessage createSubscribeResponseMessage() {
        StreamResponseMessage msg = new StreamResponseMessage(new UpnpResponse(UpnpResponse.Status.OK));
        msg.getHeaders().add(
                UpnpHeader.Type.SID, new SubscriptionIdHeader("uuid:1234")
        );
        msg.getHeaders().add(
                UpnpHeader.Type.TIMEOUT, new TimeoutHeader(180)
        );
        return msg;
    }

    protected StreamResponseMessage createUnsubscribeResponseMessage() {
        return new StreamResponseMessage(new UpnpResponse(UpnpResponse.Status.OK));
    }

    protected IncomingEventRequestMessage createEventRequestMessage(UpnpService upnpService, SubscriptionCallback callback, int sequence) {

        List<StateVariableValue> values = new ArrayList<>();
        values.add(
                new StateVariableValue(callback.getService().getStateVariable("Status"), false)
        );
        values.add(
                new StateVariableValue(callback.getService().getStateVariable("Target"), true)
        );

        OutgoingEventRequestMessage outgoing = new OutgoingEventRequestMessage(
                callback.getSubscription(),
                URIUtil.toURL(URI.create("http://10.0.0.123/some/callback")),
                new UnsignedIntegerFourBytes(sequence),
                values
        );
        outgoing.getOperation().setUri(
                upnpService.getConfiguration().getNamespace().getEventCallbackPath(callback.getService())
        );

        upnpService.getConfiguration().getGenaEventProcessor().writeBody(outgoing);

        return new IncomingEventRequestMessage(outgoing, ((RemoteGENASubscription) callback.getSubscription()).getService());
    }

}