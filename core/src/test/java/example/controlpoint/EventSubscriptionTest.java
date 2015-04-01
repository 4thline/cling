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
package example.controlpoint;

import example.binarylight.BinaryLightSampleData;
import example.binarylight.SwitchPower;
import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.mock.MockRouter;
import org.fourthline.cling.mock.MockUpnpService;
import org.fourthline.cling.model.UnsupportedDataException;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.gena.RemoteGENASubscription;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.header.SubscriptionIdHeader;
import org.fourthline.cling.model.message.header.TimeoutHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.model.types.BooleanDatatype;
import org.fourthline.cling.model.types.Datatype;
import org.seamless.util.Reflections;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

/**
 * Receiving events from services
 * <p>
 * The UPnP specification defines a general event notification architecture (GENA) which is based
 * on a publish/subscribe paradigm. Your control point subscribes with a service in order to receive
 * events. When the service state changes, an event message will be delivered to the callback
 * of your control point. Subscriptions are periodically refreshed until you unsubscribe from
 * the service. If you do not unsubscribe and if a refresh of the subscription fails, maybe
 * because the control point was turned off without proper shutdown, the subscription will
 * timeout on the publishing service's side.
 * </p>
 * <p>
 * This is an example subscription on a service that sends events for a state variable named
 * <code>Status</code> (e.g. the previously shown <a href="#section.SwitchPower">SwitchPower</a>
 * service). The subscription's refresh and timeout period is 600 seconds:
 * </p>
 * <a class="citation" href="javacode://this#subscriptionLifecycle" style="include: SUBSCRIBE; exclude: EXC1, EXC2, EXC3, EXC4, EXC5;"/>
 * <p>
 * The <code>SubscriptionCallback</code> offers the methods <code>failed()</code>,
 * <code>established()</code>, and <code>ended()</code> which are called during a subscription's lifecycle.
 * When a subscription ends you will be notified with a <code>CancelReason</code> whenever the termination
 * of the subscription was irregular. See the Javadoc of these methods for more details.
 * </p>
 * <p>
 * Every event message from the service will be passed to the <code>eventReceived()</code> method,
 * and every message will carry a sequence number. You can access the changed state variable values
 * in this method, note that only state variables which changed are included in the event messages.
 * A special event message called the "initial event" will be send by the service once, when you
 * subscribe. This message contains values for <em>all</em> evented state variables of the service;
 * you'll receive an initial snapshot of the state of the service at subscription time.
 * </p>
 * <p>
 * Whenever the receiving UPnP stack detects an event message that is out of sequence, e.g. because
 * some messages were lost during transport, the <code>eventsMissed()</code> method will be called
 * before you receive the event. You then decide if missing events is important for the correct
 * behavior of your application, or if you can silently ignore it and continue processing events
 * with non-consecutive sequence numbers.
 * </p>
 * <p>
 * You can optionally override the <code>invalidMessage()</code> method and react to message parsing
 * errors, if your subscription is with a remote service. Most of the time all you can do here is
 * log or report an error to developers, so they can work around the broken remote service (UPnP
 * interoperability is frequently very poor).
 * </p>
 * <p>
 * You end a subscription regularly by calling <code>callback.end()</code>, which will unsubscribe
 * your control point from the service.
 * </p>
 */
public class EventSubscriptionTest {

    @Test
    public void subscriptionLifecycle() throws Exception {

        MockUpnpService upnpService = createMockUpnpService();

        final List<Boolean> testAssertions = new ArrayList<>();

        // Register local device and its service
        LocalDevice device = BinaryLightSampleData.createDevice(SwitchPower.class);
        upnpService.getRegistry().addDevice(device);

        LocalService service = device.getServices()[0];

        SubscriptionCallback callback = new SubscriptionCallback(service, 600) {            // DOC: SUBSCRIBE

            @Override
            public void established(GENASubscription sub) {
                System.out.println("Established: " + sub.getSubscriptionId());
                testAssertions.add(true); // DOC: EXC2
            }

            @Override
            protected void failed(GENASubscription subscription,
                                  UpnpResponse responseStatus,
                                  Exception exception,
                                  String defaultMsg) {
                System.err.println(defaultMsg);
                testAssertions.add(false); // DOC: EXC1
            }

            @Override
            public void ended(GENASubscription sub,
                              CancelReason reason,
                              UpnpResponse response) {
                assertNull(reason);
                assertNotNull(sub); // DOC: EXC3
                assertNull(response);
                testAssertions.add(true);     // DOC: EXC3
            }

            @Override
            public void eventReceived(GENASubscription sub) {

                System.out.println("Event: " + sub.getCurrentSequence().getValue());

                Map<String, StateVariableValue> values = sub.getCurrentValues();
                StateVariableValue status = values.get("Status");

                assertEquals(status.getDatatype().getClass(), BooleanDatatype.class);
                assertEquals(status.getDatatype().getBuiltin(), Datatype.Builtin.BOOLEAN);

                System.out.println("Status is: " + status.toString());

                if (sub.getCurrentSequence().getValue() == 0) {                             // DOC: EXC4
                    assertEquals(sub.getCurrentValues().get("Status").toString(), "0");
                    testAssertions.add(true);
                } else if (sub.getCurrentSequence().getValue() == 1) {
                    assertEquals(sub.getCurrentValues().get("Status").toString(), "1");
                    testAssertions.add(true);
                } else {
                    testAssertions.add(false);
                }                                                                           // DOC: EXC4
            }

            @Override
            public void eventsMissed(GENASubscription sub, int numberOfMissedEvents) {
                System.out.println("Missed events: " + numberOfMissedEvents);
                testAssertions.add(false);                                                  // DOC: EXC5
            }

            @Override
            protected void invalidMessage(RemoteGENASubscription sub,
                                          UnsupportedDataException ex) {
                // Log/send an error report?
            }
        };

        upnpService.getControlPoint().execute(callback);                                    // DOC: SUBSCRIBE

        // Modify the state of the service and trigger event
        Object serviceImpl = service.getManager().getImplementation();
        Reflections.set(Reflections.getField(serviceImpl.getClass(), "status"), serviceImpl, true);
        service.getManager().getPropertyChangeSupport().firePropertyChange("Status", false, true);

        assertEquals(callback.getSubscription().getCurrentSequence().getValue(), Long.valueOf(2)); // It's the NEXT sequence!
        assert callback.getSubscription().getSubscriptionId().startsWith("uuid:");

        // Actually, the local subscription we are testing here has an "unlimited" duration
        assertEquals(callback.getSubscription().getActualDurationSeconds(), Integer.MAX_VALUE);

        callback.end();

        assertEquals(testAssertions.size(), 4);
        for (Boolean testAssertion : testAssertions) {
            assert testAssertion;
        }

        assertEquals(upnpService.getRouter().getSentStreamRequestMessages().size(), 0);
    }

    protected MockUpnpService createMockUpnpService() {
        return new MockUpnpService() {
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


}