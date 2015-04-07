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
import org.fourthline.cling.model.UnsupportedDataException;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.gena.RemoteGENASubscription;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.gena.IncomingEventRequestMessage;
import org.fourthline.cling.model.message.gena.OutgoingEventRequestMessage;
import org.fourthline.cling.model.message.header.CallbackHeader;
import org.fourthline.cling.model.message.header.SubscriptionIdHeader;
import org.fourthline.cling.model.message.header.TimeoutHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.protocol.ProtocolCreationException;
import org.fourthline.cling.test.data.SampleData;
import org.fourthline.cling.transport.RouterException;
import org.seamless.util.URIUtil;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import static org.testng.Assert.assertEquals;

public class OutgoingSubscriptionLifecycleTest {

    @Test
    public void subscriptionLifecycle() throws Exception {

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
                testAssertions.add(false);
            }

        };

        upnpService.getControlPoint().execute(callback);

        // Subscription process OK?
        for (Boolean testAssertion : testAssertions) {
            assert testAssertion;
        }

        // Simulate received event
        upnpService.getProtocolFactory().createReceivingSync(
                createEventRequestMessage(upnpService, callback)
        ).run();

        assertEquals(callback.getSubscription().getCurrentSequence().getValue(), Long.valueOf(0));
        assertEquals(callback.getSubscription().getSubscriptionId(), "uuid:1234");
        assertEquals(callback.getSubscription().getActualDurationSeconds(), 180);

        List<URL> callbackURLs = ((RemoteGENASubscription) callback.getSubscription())
                .getEventCallbackURLs(upnpService.getRouter().getActiveStreamServers(null), upnpService.getConfiguration().getNamespace());

        callback.end();

        assert callback.getSubscription() == null;

        assertEquals(testAssertions.size(), 3);
        for (Boolean testAssertion : testAssertions) {
            assert testAssertion;
        }

        List<StreamRequestMessage> sentMessages = upnpService.getRouter().getSentStreamRequestMessages();
        assertEquals(sentMessages.size(), 2);
        assertEquals(
                (sentMessages.get(0).getOperation()).getMethod(),
                UpnpRequest.Method.SUBSCRIBE
        );
        assertEquals(
            sentMessages.get(0).getHeaders().getFirstHeader(UpnpHeader.Type.TIMEOUT, TimeoutHeader.class).getValue(),
                Integer.valueOf(1800)
        );

        assertEquals(callbackURLs.size(), 1);
        assertEquals(
            sentMessages.get(0).getHeaders().getFirstHeader(UpnpHeader.Type.CALLBACK, CallbackHeader.class).getValue().get(0),
                callbackURLs.get(0)
        );

        assertEquals(
                (sentMessages.get(1).getOperation()).getMethod(),
                UpnpRequest.Method.UNSUBSCRIBE
        );
        assertEquals(
            sentMessages.get(1).getHeaders().getFirstHeader(UpnpHeader.Type.SID, SubscriptionIdHeader.class).getValue(),
                "uuid:1234"
        );
    }

    @Test
    public void subscriptionLifecycleNotifyBeforeResponse() throws Exception {

        final RemoteDevice device = SampleData.createRemoteDevice();
        final RemoteService service = SampleData.getFirstService(device);

        final StreamResponseMessage subscribeResponseMessage = createSubscribeResponseMessage();
        final Semaphore subscribeResponseSemaphore = new Semaphore(0);

        final MockUpnpService upnpService = new MockUpnpService() {
            @Override
            protected MockRouter createRouter() {
                return new MockRouter(getConfiguration(), getProtocolFactory()) {
                    @Override
                    public StreamResponseMessage getStreamResponseMessage(StreamRequestMessage request) {
                        try {
                            // bloc subscription response until the first notification
                            subscribeResponseSemaphore.acquire();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        return subscribeResponseMessage;
                    }
                };
            }
        };

        final List<Boolean> testAssertions = new ArrayList<>();
        final List<Boolean> notificationCalled = new ArrayList<>();

        // Register remote device and its service
        upnpService.getRegistry().addDevice(device);

        // send subscription request
        final SubscriptionCallback callback = new SubscriptionCallback(service) {

            @Override
            protected void failed(GENASubscription subscription,
                                  UpnpResponse responseStatus,
                                  Exception exception,
                                  String defaultMsg) {
                testAssertions.add(false);
            }

            @Override
            public void established(GENASubscription subscription) {
                testAssertions.add(true);
            }

            @Override
            public void ended(GENASubscription subscription, CancelReason reason, UpnpResponse responseStatus) {
            }

            public void eventReceived(GENASubscription subscription) {
                assertEquals(subscription.getCurrentValues().get("Status").toString(), "0");
                assertEquals(subscription.getCurrentValues().get("Target").toString(), "1");
                testAssertions.add(true);
                notificationCalled.add(true);
            }

            public void eventsMissed(GENASubscription subscription, int numberOfMissedEvents) {
                testAssertions.add(false);
            }

        };

        // send subscription request is a separate thread
        Thread subscribeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                upnpService.getControlPoint().execute(callback);
            }
        });
        subscribeThread.start();

        // generate notification in a separate thread
        // use a dummy GENASubscription for that to have a valid subscriptionId
       final GENASubscription subscription = new RemoteGENASubscription(service, 180) {
            @Override
            public void established() {
            }
            @Override
            public void eventReceived() {
            }
            @Override
            public void invalidMessage(UnsupportedDataException ex) {
            }
            @Override
            public void failed(UpnpResponse responseStatus) {
            }
            @Override
            public void ended(CancelReason reason, UpnpResponse responseStatus) {

            }
            @Override
            public void eventsMissed(int numberOfMissedEvents) {
            }
        };
        subscription.setSubscriptionId("uuid:1234");

        Thread notifyThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Simulate received event before the subscription response
                try {
                    upnpService.getProtocolFactory().createReceivingSync(
                            createEventRequestMessage(upnpService, service, subscription)
                    ).run();
                } catch (ProtocolCreationException e) {
                    testAssertions.add(false);
                }

            }
        });
        notifyThread.start();

        // give time to process notification
        Thread.sleep(1000);

        // notification should not have been received before the subscribe response
        assertEquals(0, notificationCalled.size());

        // unlock subscription response
        subscribeResponseSemaphore.release();

        subscribeThread.join();
        notifyThread.join();

        // notification should have been received after the subscribe response
        assertEquals(1, notificationCalled.size());

        for (Boolean testAssertion : testAssertions) {
            assert testAssertion;
        }
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

    protected IncomingEventRequestMessage createEventRequestMessage(final UpnpService upnpService, final SubscriptionCallback callback) {

        List<StateVariableValue> values = new ArrayList<>();
        values.add(
                new StateVariableValue(callback.getService().getStateVariable("Status"), false)
        );
        values.add(
                new StateVariableValue(callback.getService().getStateVariable("Target"), true)
        );

        OutgoingEventRequestMessage outgoing = new OutgoingEventRequestMessage(
                callback.getSubscription(),
                URIUtil.toURL(URI.create("http://10.0.0.123/this/is/ignored/anyway")),
                new UnsignedIntegerFourBytes(0),
                values
        );
        outgoing.getOperation().setUri(
                upnpService.getConfiguration().getNamespace().getEventCallbackPath(callback.getService())
        );

        upnpService.getConfiguration().getGenaEventProcessor().writeBody(outgoing);

        return new IncomingEventRequestMessage(outgoing, ((RemoteGENASubscription)callback.getSubscription()).getService());
    }


    protected IncomingEventRequestMessage createEventRequestMessage(final UpnpService upnpService, final RemoteService service, final GENASubscription subscription) {

        List<StateVariableValue> values = new ArrayList<>();
        values.add(
                new StateVariableValue(service.getStateVariable("Status"), false)
        );
        values.add(
                new StateVariableValue(service.getStateVariable("Target"), true)
        );

        OutgoingEventRequestMessage outgoing = new OutgoingEventRequestMessage(
                subscription,
                URIUtil.toURL(URI.create("http://10.0.0.123/this/is/ignored/anyway")),
                new UnsignedIntegerFourBytes(0),
                values
        );
        outgoing.getOperation().setUri(
                upnpService.getConfiguration().getNamespace().getEventCallbackPath(service)
        );

        upnpService.getConfiguration().getGenaEventProcessor().writeBody(outgoing);

        return new IncomingEventRequestMessage(outgoing, service);
    }

 }
